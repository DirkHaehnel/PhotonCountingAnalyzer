function no = hist2D(x, bins, maxval)

if ((nargin < 2) || (isempty(bins)))
    bins = 20;
end

if ((nargin < 3) || (isempty(maxval)))
    maxval = max(max(x));
end
%minval = min(min(x));
minval = 0;

binwidth = (maxval - minval) / bins;

no = zeros(bins);

for i=1:length(x)
   curx = ceil((x(i,1) - minval) / binwidth);
   cury = ceil((x(i,2) - minval) / binwidth);
   if (curx == 0) 
       curx = 1;
   end
   if (cury == 0) 
       cury = 1;
   end
   if ((curx <= bins) && (cury <= bins))
       no(curx,cury) = no(curx,cury) + 1;
   end
end

