function [isValid results resM] = cluEval(res, label, maxCluSize)
% save tmp.mat
% % clear all
% % clc
% % load tmp.mat
% clear all
% clc
% load test.mat
% maxCluSize = Ksize;
% label(label == 5) = 1;
% M = [2 2; 9 11];
% % M = [3 0; 9 12];
% [n d] = size(M);

%% M: cluster * truth classes
% index = find(label > maxCluSize);
% res(index) = [];
% label(index) = [];

uniqueRes = unique(res);
uniqueLabels = unique(label);

% res
n = length(uniqueRes);
d = length(uniqueLabels);
maxCluSize = max(n,d);

% if n ~= d
%     isValid = 0;
%     PRF = ones(maxCluSize,3)*inf;
%     results.PRF = PRF;
%     results.purity = inf;
%     results.entropy = inf;
%     results.ACC = inf;
%     M = ones(length(uniqueRes), length(uniqueLabels))*inf;
%     results.M = M;
%     resM = ones(4,2)*inf;
%     % purity entropy
%     % Precision1 Precision 2
%     % Acc RandIndex
%     resM = ones(4,2)*inf;
%     return;
% end

M = zeros(length(uniqueRes), length(uniqueLabels));
for i = 1:maxCluSize
    for j = 1:maxCluSize
        if(i <= length(uniqueRes) && j <= length(uniqueLabels))
            M(i,j) = sum(res == uniqueRes(i) & label == uniqueLabels(j));
        else
            M(i,j) = 0;
        end
    end
end
M

%% precision recall and acc
isValid = 1;
PRF = ones(maxCluSize,3)*inf;
if maxCluSize == 2
    if M(1,1) + M(2,2) < M(1,2) + M(2,1)
        M = [M(2,:); M(1,:)];
    end
    purity = sum(max(M,[],2))/sum(sum(M));

    % entropy
    sumM = sum(M,2);
    sumM(sumM == 0) = 1;
%     prob = M./repmat(sum(M,2), 1,maxCluSize);
    prob = M./repmat(sumM, 1,maxCluSize);
    logProb = prob;
    logProb(logProb == 0) = 1;
    logProb = log(logProb)/log(2);
    ent = prob.*logProb;
    entropy = sum(ent,2).*sum(M,2);
    entropy = -sum(entropy)/sum(sum(M));

    disp('confussion matrix:')
    disp(M)
    disp(sprintf(['purity  is:\t' num2str(purity)]));
    disp(sprintf(['entropy is:\t' num2str(entropy)]));

%     save cluEvalResult.mat M purity entropy

    PRF(1,1) = M(1,1)/sum(M(1,:)); % precision
    PRF(1,2) = M(1,1)/sum(M(:,1));
    PRF(1,3) = 2*PRF(1,1)*PRF(1,2)/(PRF(1,1) + PRF(1,2));
    PRF(2,1) = M(2,2)/sum(M(2,:)); % precision
    PRF(2,2) = M(2,2)/sum(M(:,2));
    PRF(2,3) = 2*PRF(2,1)*PRF(2,2)/(PRF(2,1) + PRF(2,2));
    disp(sprintf(['class 1 P R F1:\t', num2str(PRF(1,:))]));
    disp(sprintf(['class 2 P R F1:\t', num2str(PRF(2,:))]));
    ACC = (M(1,1)+M(2,2))/sum(sum(M));
    disp(sprintf(['ACC is:\t', num2str(ACC)]));
    tmp = [];
    tmp(1,:) = res;
    tmp(2,:) = label;
    res_ri = adjRI(tmp(1,:), tmp(2,:));
    disp(sprintf(['RI is:\t' num2str(res_ri.RI) '\t' num2str(res_ri.adjRI)]));
else
    for i = 1:(maxCluSize - 1)
        tmpM = M(i:maxCluSize, i:maxCluSize);
        [a b] = find(tmpM == max(max(tmpM)));
        a = a(1);
        b = b(1);
        a = a + i - 1;
        b = b + i - 1;
        % swap line i and line a
        M([i a],:) = M([a i],:);
        % swap col i and line b
        M(:, [i b]) = M(:, [b i]);
    end
    purity = sum(max(M,[],2))/sum(sum(M));

    % entropy
    sumM = sum(M,2);
    sumM(sumM == 0) = 1;
%     prob = M./repmat(sum(M,2), 1,maxCluSize);
    prob = M./repmat(sumM, 1,maxCluSize);
%     prob = M./repmat(sum(M,2), 1,maxCluSize);
    logProb = prob;
    logProb(logProb == 0) = 1;
    logProb = log(logProb)/log(2);
    ent = prob.*logProb;
    entropy = sum(ent,2).*sum(M,2);
    entropy = -sum(entropy)/sum(sum(M));

    disp('confussion matrix:')
    disp(M)
    disp(sprintf(['purity  is:\t' num2str(purity)]));
    disp(sprintf(['entropy is:\t' num2str(entropy)]));

%     save cluEvalResult.mat M purity entropy

    for cS = 1:maxCluSize
        PRF(cS,1) = M(cS,cS)/sum(M(cS,:)); % precision
        PRF(cS,2) = M(cS,cS)/sum(M(:, cS)); % precision
        PRF(cS,3) = 2*PRF(cS,1)*PRF(cS,2)/(PRF(cS,1) + PRF(cS,2));
        disp(sprintf(['class ' num2str(cS) ' P R F1:\t', num2str(PRF(cS,:))]));
    end
    ACC = sum(sum(M.*eye(maxCluSize)))/sum(sum(M));
    disp(sprintf(['ACC is:\t', num2str(ACC)]));
    tmp = [];
    tmp(1,:) = res;
    tmp(2,:) = label;
    res_ri = adjRI(tmp(1,:), tmp(2,:));
    disp(sprintf(['RI is:\t' num2str(res_ri.RI) '\t' num2str(res_ri.adjRI)]));
end

results.PRF = PRF;
results.purity = purity;
results.entropy = entropy;
results.ACC = ACC;
results.M = M;
resM = ones(4,2)*inf;
% purity entropy
% Precision1 Precision 2
% Acc RandIndex
resM(1,:) = [purity entropy];
resM(2,:) = [PRF(1,3) PRF(2,3)];
resM(3,:) = [ACC res_ri.RI];
resM(4,:) = [mean(PRF(:,3)) NaN];
% results.RI = res_ri;

disp(resM)

if(~numel(purity) || ~numel(entropy))
    resM
    isValid = 0;
end
