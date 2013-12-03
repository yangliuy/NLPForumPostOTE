function Res = pdistM(A, B, dist)
% input: A: m*K, B: m*K, output: m
% MetricDescription
% 'euclidean' Euclidean distance (default).
% 'seuclidean' Standardized Euclidean distance. 'seuclidean'
% 'cityblock' City block metric.
% 'minkowski' Minkowski distance.
% 'chebychev' Chebychev distance (maximum coordinate difference).
% 'mahalanobis' Mahalanobis distance
% 'cosine' One minus the cosine of the included angle between points (treated as vectors).
% 'correlation' One minus the sample correlation between points (treated as sequences of values).
% 'spearman' One minus the sample Spearman's rank correlation between observations, treated as sequences of values.
% 'hamming' Hamming distance, the percentage of coordinates that differ.
% 'jaccard' One minus the Jaccard coefficient, the percentage of nonzero coordinates that differ.
% 'KL' KLdivergence(a,b)
% 'JS' JS(a,b)

if size(A) ~= size(B)
    return;
end

%%
[n d] = size(A);
Res = ones(n,1)*inf;

switch dist
    case {'KL'}
        disp('KL');
        for i = 1:n
            Res(i) = KLDiv(A(i,:), B(i,:));
        end
        return;
    case {'JS'}
        disp('JS');
        for i = 1:n
            Res(i) = JSDiv(A(i,:), B(i,:));
        end
        return;
end

disp('other measures');
for i = 1:n
    Res(i) = pdist2(A(i,:), B(i,:), dist);
end
