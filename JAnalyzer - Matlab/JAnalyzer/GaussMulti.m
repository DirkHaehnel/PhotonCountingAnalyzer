function [err, c, z, A] = GaussMulti(p,t,y,polyorder,bck,pic,weight)

if nargin<4 || isempty(polyorder)
    polyorder = 1;
end

if isempty(t)
	t = 1:length(y);
end
t = t(:);
y = y(:);
if nargin>6 && ~isempty(weight)
    weight = weight(:);
end
nn = length(p)-1;

A = zeros(length(t),nn + polyorder);
for j = 1:nn
   A(:,j) = exp(-(t-(p(1)*j)).^2/p(1+j)^2/2);
end
for j=1:polyorder
    A(:,nn+j) = t.^(j-1);
end
if nargin>4 && ~isempty(bck)
    A(:,nn+1) = bck(:);
end
ind = y>0 & isfinite(y);
c = lsqnonneg(A(ind,:),y(ind));
%c = A(ind,:)\y(ind);
z = A*c;
if nargin>5 && ~isempty(pic)
    if (pic == 2)
        plot(t,y,'o');
        hold on;
        plot(t,z,'k');
        for j = 1:nn
            plot(t,A(:,j)*c(j,:), 'b');
        end
        hold off; drawnow
    else
        plot(t,y,'o',t,z); drawnow
    end
end
if nargin<7 || isempty(weight)
    err = sum((z(ind)-y(ind)).^2./abs(z(ind)));
else
    err = sum(weight(ind).*(z(ind)-y(ind)).^2);
end

