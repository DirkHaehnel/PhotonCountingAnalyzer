function [t, tcspc, head] = ReadAndBin(name, binwidth, time, tcspcwind)

% t = ReadAndBin(name, binwidth, flag)
% name : file name
% binwidth : width of time bin in ms (default 1 ms)
% time : time window to read in s (default [0 inf])
% tcspcwind : tcspc windows (laser pulses) (default [0 inf -1 -1])
% (c) Christoph Pieper (2010)

head = ht3v2read_head(name);

if ((isempty(head)) && not(isempty(strfind(name, '.ht3'))))
    % no header => old ht3 File
    fprintf(1,'\n\n      Warning: No File Header. Assuming old ht3 File\n');
    head.Ident = 'HydraHarp';
    head.Resolution = 2e-3;   % in nanoseconds
    head.SyncRate = 80000000;
    head.DataStart = 0;
    tmp = dir(name);
    head.Records = tmp.bytes/4;
end;

photons = 1e6; % number of photons processed one at a time

if ((nargin < 4) || (length(tcspcwind) < 4))
    tcspcwind = [0 inf -1 -1];
end
if ((nargin < 3) || (length(time) < 2))
    time = [0 inf];
end
if nargin < 2
    binwidth = 1;
end

cnt = 1;
num = photons;
timeup = 0;
isht3 = 0;
if strcmp(name(end-2:end),'pt3')
    head.Sync = 1E9/head.CntRate0;
    synctime_ms = head.Sync*1e-6;
elseif strcmp(name(end-2:end),'ht3')
    synctime_ms = 1000 / head.SyncRate;
    isht3 = 1;
end

syncperbin = binwidth / synctime_ms;

startbin = ceil(time(1) * 1000 / binwidth);
endbin = ceil(time(2) * 1000 / binwidth)- startbin;

if (isht3)
    [sync, tcspc, chan, special] = ht3v2read_old(name, [1 1e5]);
    [dind, m] = unique(sort(chan(special==0)));
    occurrence = diff(vertcat(0, m));
    dind(occurrence < 10) = [];
    tcspc2 = zeros(2^15,length(dind));
else
    tcspc2 = zeros(2^12,2);
    dind = [1 3];
end

% channel LUT
chanLUT = zeros(8, 1);
for i = 1:length(dind)
    chanLUT(dind(i)+1) = i;
end

virtualchans = length(dind) * 2;
if ((tcspcwind(3) == -1) && (tcspcwind(3) == -1))
    virtualchans = length(dind);
end
    
if (time(2) == inf)
    t = zeros(10000, virtualchans);
    fieldlength = 10000;
else
    bc = (time(2) - time(1)) * 1000 / binwidth + 1;
    t = zeros(bc, virtualchans);
    fieldlength = bc;
end
bincounter = 0;
ov2 = 0;

% auto timegate 
%tmp = mean(tcspcdata,2);
%bin = 0:length(tcspcdata);
%[ind, num] = mCluster(tmp-min(tmp)>0.3*mean(tmp-min(tmp)));
%t1 = max([1, min(bin(ind==length(num)))-50]);
%t2 = min(bin(ind==length(num)-1))-50;
%if t1>t2
%    tmp = t1; t1 = t2; t2 = tmp;
%end
%len = min([t2-t1,length(bin)-t2-20]);

while ((num == photons) && (timeup ~= 1))
    [sync, tcspc, chan, special, num, overcount, head2] = ht3v2read_old(name, [cnt photons]);
    cnt = cnt + num;
    if (binwidth > 0)
        for i = 1 : length(sync)
            bin = ceil((sync(i)+ov2)/syncperbin) - startbin;
            if (bin > endbin)
                timeup = 1;
                break;
            end
            if (bin > fieldlength)
                t = [t; zeros(10000, virtualchans)];
                fieldlength = fieldlength + 10000;
            end
            if ((bin > 0) && (special(i) == 0))
                thisChan = chanLUT(chan(i)+1);
                if (thisChan ~= 0)
                    if ((tcspc(i) >= tcspcwind(1)) && (tcspc(i) <= tcspcwind(2)))
                        t(bin, thisChan) = t(bin, thisChan) + 1;
                    end
                    thisChan = thisChan + length(dind);
                    if ((tcspc(i) >= tcspcwind(3)) && (tcspc(i) <= tcspcwind(4)))
                        t(bin, thisChan) = t(bin, thisChan) + 1;
                    end
                    bincounter = bin;
                end
            end
        end
    end
    ov2 = ov2 + overcount;
    tcspc(special == 1) = [];
    chan(special == 1) = [];
    if (isht3)
        if (isempty(tcspc2))
            for j=1:length(dind)
                idx = dind(j);
                tcspc2(:,j) = mHist(tcspc(chan==idx),0:2^15-1);
            end
        else
            for j=1:length(dind)
                idx = dind(j);
                tcspc2(:,j) = tcspc2(:,j) + mHist(tcspc(chan==idx),0:2^15-1);
            end
        end
    else
        if (isempty(tcspc2))
            tcspc2(:,1) = mHist(tcspc(chan==1),0:2^12-1);
            tcspc2(:,2) = mHist(tcspc(chan==3),0:2^12-1);
        else
            tcspc2(:,1) = tcspc2(:,1) + mHist(tcspc(chan==1),0:2^12-1);
            tcspc2(:,2) = tcspc2(:,2) + mHist(tcspc(chan==3),0:2^12-1);
        end
    end
end
tcspc = tcspc2(:,:);
last = find(tcspc(:,1)>0,1,'last');
tcspc = tcspc(1:last,:);
t = t(1:bincounter, :);
