function [res] = map3d22d(BUT, dim, value)

% sample useage
% a = map3d22d(BUT, dim, value);
% dim: 1:3, value: depends on dim size
% e.g: BUT: 10*20*30, a = map22d(BUT, 1, 2); 
% a = BUT(2,:,:);
%%
if(dim == 1)
    tmp = BUT(value, :, :);
end
if(dim == 2)
    tmp = BUT(:, value, :);
end
if(dim == 3)
    tmp = BUT(:, :, value);
end

dims = 1:3;
dims(dim) = [];

res = reshape(tmp, [size(BUT, dims(1)) size(BUT, dims(2))]);
