function [auto, autotime] = MultiFCS(name, nums)

% t = ReadAndBin(name, binwidth, flag)
% name : file name
% binwidth : width of time bin in ms (default 1 ms)
% time : time window to read in s (default [0 inf])
% tcspcwind : tcspc windows (laser pulses) (default [0 inf -1 -1])
% (c) Christoph Pieper (2010)

auto = [];
autotime = [];

h = waitbar(0,'Please wait...','Name','MultiFCS Progress');
setFigureAOT('MultiFCS Progress', true);
if ((nargin >= 2) || (length(nums) == 2))
    j = 0;
    for i=nums(1):nums(2)
        j = j + 1;
        waitbar(j/(nums(2)-nums(1)),h);
        [s, errmsg] = sprintf('%s_%d.ht3', name, i);
        [res, head] = SingleFocus2FCS(s);
        auto(:,:,:,j) = res.auto(:,:,:,1);
        autotime = res.autotime;
    end
end
close(h);
