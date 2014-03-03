function [t] = MultiRead(name, nums, binwidth, time, tcspcwind)

% t = ReadAndBin(name, binwidth, flag)
% name : file name
% binwidth : width of time bin in ms (default 1 ms)
% time : time window to read in s (default [0 inf])
% tcspcwind : tcspc windows (laser pulses) (default [0 inf -1 -1])
% (c) Christoph Pieper (2010)

if ((nargin < 5) || (length(tcspcwind) < 4))
    tcspcwind = [0 inf -1 -1];
end
if ((nargin < 4) || (length(time) < 2))
    time = [0 inf];
end
if nargin < 3
    binwidth = 1;
end

t = [];

h = waitbar(0,'Please wait...');
if ((nargin >= 2) || (length(nums) == 2))
    j = 0;
    for i=nums(1):nums(2)
        j = j + 1;
        waitbar(j/(nums(2)-nums(1)),h);
        [s, errmsg] = sprintf('%s_%d.ht3', name, i);
        [trace, tcspc, head] = ReadAndBin(s, binwidth, time, tcspcwind);
        t(:,j) = trace(:,1);
    end
end
close(h);
