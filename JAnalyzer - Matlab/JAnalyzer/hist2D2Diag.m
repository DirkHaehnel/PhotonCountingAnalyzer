function diag = hist2D2Diag(no, width)

if ((nargin < 2) || (isempty(width)))
    width = 2;
end

n = length(no);
diag = zeros(n*2, 1);

for i=1:n
    %odd = sum(mod(0:width,2));
    diag(i*2) = diag(i*2) + no(i,i);
    for j=1:min([width n-i])
        red = 1;
        if (j == width)
            red = 2;
        end
        diag(i*2+j) = diag(i*2+j) + no(i+j,i) /red + no(i,i+j) /red;
    end
end

