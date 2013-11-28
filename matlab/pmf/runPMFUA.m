clear all
clc

% user opinion data
load uaData_6.3.mat

addpath('../common/');
%% get train_vec
train_vec = [];
% default posRate = 0.5
for i = 1:size(posRate,1)
    for j = 1:size(posRate,2)
        if posRate(i,j) ~= 0.5
            train_vec = [train_vec; [i j posRate(i,j)]];
        end
    end
end
probe_vec = train_vec;

maxIter = 2;
epsionArray = [0.02 0.01];
luvaArray = [0.05 0.01];
res = [];
finalRes = [];
for i = 1:length(epsionArray)
    for j = 1:length(luvaArray)
        for iter = 1:maxIter
            %% PMF by Minghui
            clearvars -except res iter resout posRate train_vec probe_vec ...
                label  i j epsionArray luvaArray finalRes res FilInt maxIter
            %%
            inputParas.batchSize = 50;
            inputParas.epsilon = epsionArray(i);
            inputParas.lambda  = luvaArray(j); % Regularization parameter
            inputParas.momentum = 0.9;
            inputParas.maxepoch = 500;
            inputParas.featSize = 10; % Rank 10 decomposition

            test_vec = train_vec;
            DATA.train_vec = train_vec;
            DATA.test_vec = test_vec;
            DATA.userSize = size(posRate,1);  % Number of users
            DATA.itemSize = size(posRate,2);  % Number of movies 

            Result = pmfUA_M(DATA, inputParas);

            % figure, plot(1:length(err_train), err_train, 'b*');
            % hold on; plot(1:length(err_valid), err_valid, 'r.');
%             [results resM] = evaluation(label, Result.vecU, posRate, 1);
            if ~isnan(Result.err_train(length(Result.err_train)))
                [results resM] = evaluation(label(FilInt), ...
                    Result.vecU(FilInt,:), posRate, 1);
            else
                resM = [0 1; 0 0; 0 0; 0 NaN];
            end
            resout(:,:, iter) = resM;
        end
        resM2 = mean(resout,3);
        finalRes = [finalRes; resM2];
        res = [res; [length(finalRes) i j resM2(1,1) resM2(1,2) resM2(1,1)/resM2(1,2)]];
    end
end

lastPos = size(res,2);
index = find(res(:,lastPos) == max(res(:,lastPos)));
index = index(1);
if(~isempty(index))
    i = res(index,2);
    j = res(index,3);
    k = res(index,4);
    fprintf('\nbest results:\neps %f luva %f\n', epsionArray(i), luvaArray(j));

    resM2 = finalRes((res(index,1)-3):res(index,1),:);
    for i = 1:size(resM2,1)
        disp(sprintf([num2str(resM2(i,1)) '\t' num2str(resM2(i,2))]));
    end
    fprintf('total users: %d\n', sum(sum(results.M)));
else
    disp('No valid solutions found! Try other paramter settings...');
end

