clear all
clc

%%
data = S;
opts = statset('Display','final');
[idx,ctrs] = kmeans(data,4,...
      'Distance','sqeuclidean',...
      'Replicates',5,...
      'Options',opts);
figure;
plot(X(idx==1,1),X(idx==1,2),'r.','MarkerSize',12)
hold on
plot(X(idx==2,1),X(idx==2,2),'b.','MarkerSize',12)
%%  plot two clusters




