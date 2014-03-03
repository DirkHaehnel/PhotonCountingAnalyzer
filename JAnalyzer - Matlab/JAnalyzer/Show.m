function Show(t)

if (size(t, 2) == 1)
   plot(t); 
elseif (size(t, 2) == 2)
   t(:, 2) = -t(:, 2);
   plot(t);
elseif (size(t, 2) == 4)
   offset = -(max(t(:, 2)) + max(t(:, 3)) + 5);
   t(:, 2) = -t(:, 2);
   t(:, 3) = offset+t(:, 3);
   t(:, 4) = offset-t(:, 4);
   plot(t);
end