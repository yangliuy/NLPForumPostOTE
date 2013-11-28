clear
clc
%
tic
load TestPMFAUU.mat
% FilInt: target user for evaluation
% AUUmatrix: aspect user1 user2 user1-aspect-score user1-user2-score
                
%% Grib search
% epsionArray = [0.01 0.005 0.0005];
% lrcArray = [1 0.1];
% luvaArray = [0.01 0.001];
epsionArray = 0.005;
lrcArray = 0.1;
luvaArray = 0.01;
maxIter = 5;
res = [];
finalRes = [];

for i = 1:length(epsionArray)
    for j = 1:length(lrcArray)
        for k = 1:length(luvaArray)
            for iter = 1:maxIter
                inputParas.epsilon = epsionArray(i);
                inputParas.lambda_rc = lrcArray(j);
                inputParas.lambda_uva = luvaArray(k);
                inputParas.batchSize = 50;
                inputParas.momentum = 0.9;
                inputParas.maxepoch = 400;
                inputParas.featSize = 10; % Rank 10 decomposition

                DATA.train_vec = AUUmatrix;
                DATA.test_vec = AUUmatrix;
                DATA.userSize = length(names);
                DATA.itemSize = length(aspects);

                Result = pmfAUU(DATA, inputParas);

                clearvars -except Result labels users aspects iter test_vec ...
                    resout DATA uaDataName uuDataName AUUmatrix names res ...
                    epsionArray lrcArray luvaArray i j k finalRes filterlevel ...
                    FilInt maxIter

                % cosine sqeuclidean
                opts = statset('Display','final');
                [idx,ctrs] = kmeans(Result.vecU,2,...
                      'Distance','sqeuclidean',...
                      'Replicates',5,...
                      'Options',opts);
%                 [results resM] = cluEval(idx, labels, 2);            
                if ~isnan(Result.err_train(length(Result.err_train)))
                    [isvalid results resM] = cluEval(idx(FilInt), labels(FilInt), 2);      
                else
                    resM = [0 1; 0 0; 0 0];
                end
                if ~isnan(Result.err_train(length(Result.err_train)))
                    opts = statset('Display','final');
                    [idx,ctrs] = kmeans(Result.vecU(FilInt,:),2,...
                          'Distance','cosine',...
                          'Replicates',5,...
                          'Options',opts); 
                    [isvalid results resMP] = cluEval(idx, labels(FilInt), 2);
                    if(resMP(1,2) < resM(1,2))
                        resM = resMP;
                    end
                else
                    resM = [0 1; 0 0; 0 0];
                end
            %%     visulize
            %     vecU = Result.vecU;
            %     res = labels;
            %     [U S V] = svd(vecU);
            %     if flag ~= 1
            %         subplot(1,2,2); plot3(U(res==1,1),U(res==1,2),U(res==1,3),'r.','MarkerSize',12)
            %         hold on, plot3(U(res==2,1),U(res==2,2),U(res==2,3),'b.','MarkerSize',12)
            %         plot(U(res==1,1),U(res==1,2),'r.','MarkerSize',12)
            %         hold on, plot(U(res==2,1),U(res==2,2),'b.','MarkerSize',12)
            %     end
                resout(:,:, iter) = resM;
            end
            resM2 = mean(resout,3);
            finalRes = [finalRes; resM2];
            res = [res; [length(finalRes) i j k resM2(1,1) resM2(1,2) resM2(1,1)/resM2(1,2)]];
        end
    end
save tmp.mat
end

lastPos = size(res,2);
index = find(res(:,lastPos) == max(res(:,lastPos)));
if(~isempty(index))
    index = index(1);
    i = res(index,2);
    j = res(index,3);
    k = res(index,4);
    fprintf('\nbest results:\neps %f lrc %f luva %f\n', epsionArray(i), lrcArray(j), luvaArray(k));

    % purity entropy
    % Precision1 Precision2
    % Acc RandIndex
    % mean-F1 NaN
    resM2 = finalRes((res(index,1)-3):res(index,1),:);
    for i = 1:size(resM2,1)
        disp(sprintf([num2str(resM2(i,1)) '\t' num2str(resM2(i,2))]));
    end
    fprintf('total users: %d\n', sum(sum(results.M)));
    plot(1:length(Result.err_train), Result.err_train, 'b*');
    toc
else
    disp('No valid solutions found! Try other paramter settings...');
end

