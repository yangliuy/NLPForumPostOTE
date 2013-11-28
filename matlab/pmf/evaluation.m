function [results resM] = evaluation(label, vecU, posRate, flag)
% clear all
% clc
% 
% %% evaluation on subgroups
% load results_3.mat
% load posNeg_3.mat
% load(output);
% load tmp.mat
% flag = 0;
% clearvars -except posRate label vecU flag
% label = label(1:size(vecU));
filter = find(label == 1| label == 2);
% save tmp.mat
%% kmeans
if flag ~= 1
    X = posRate;
    [U S V] = svd(X);
    res = label;
    % 'sqeuclidean', 'cityblock', 'cosine','correlation', or 'hamming'
    opts = statset('Display','final');
    [idx,ctrs] = kmeans(X,2,...
          'Distance','cosine',... 
          'Replicates',5,...
          'Options',opts);
    % eval
    save tmp.mat
    [isvalid results resM] = cluEval(idx(filter), label(filter));

    figure; subplot(1,2,1);
    plot3(U(res==1,1),U(res==1,2),U(res==1,3),'r.','MarkerSize',12)
    hold on, plot3(U(res==2,1),U(res==2,2),U(res==2,3),'b.','MarkerSize',12)
    plot(U(res==1,1),U(res==1,2),'r.','MarkerSize',12)
    hold on, plot(U(res==2,1),U(res==2,2),'b.','MarkerSize',12)
end

%% check pmf results
label2 = label(filter);
vecU = vecU(filter,:);
res = label2;
if flag ~= 1
    [U S V] = svd(vecU);
    subplot(1,2,2); plot3(U(res==1,1),U(res==1,2),U(res==1,3),'r.','MarkerSize',12)
    hold on, plot3(U(res==2,1),U(res==2,2),U(res==2,3),'b.','MarkerSize',12)
    plot(U(res==1,1),U(res==1,2),'r.','MarkerSize',12)
    hold on, plot(U(res==2,1),U(res==2,2),'b.','MarkerSize',12)
end

opts = statset('Display','final');
[idx,ctrs] = kmeans(vecU,2,...
      'Distance','cosine',...
      'Replicates',5,...
      'Options',opts);
[isvalid results resM] = cluEval(idx, label2,2);

