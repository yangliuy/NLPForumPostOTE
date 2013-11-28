clear all
clc

%% test data
load uuData_3.mat
% FilInt: target users
% defV: default score
% uuRate: user's ratings on other users

%% PMF by Minghui
clearvars -except res iter resout uuRate label defV FilInt
clc

epsionArray = [0.02 0.01];
luvaArray = [0.05 0.01];
maxIter = 2;

res = [];
finalRes = [];
for para_i = 1:length(epsionArray)
    for para_j = 1:length(luvaArray)
        for iter = 1:maxIter
            %%
            IMatrix = uuRate;
            inputParas.batchSize = 50;
            inputParas.epsilon = epsionArray(para_i);
            inputParas.lambda  = luvaArray(para_j); % Regularization parameter
            inputParas.momentum = 0.9;
            inputParas.maxepoch = 400;
            inputParas.featSize = 10; % Rank 10 decomposition 

            %% user opinion data
            train_vec = [];
            IMatrix = full(IMatrix);
            for i = 1:size(IMatrix,1)
                for j = 1:size(IMatrix,2)
                    if(IMatrix(i,j) > 0 && IMatrix(i,j) ~= defV && IMatrix(i,j) < 1)
                        train_vec = [train_vec; [i j IMatrix(i,j)]];
                    end
                end
            end
            userSize = size(IMatrix,1);  % Number of users

            % 10-cross validation
        %     randSample = randperm(size(train_vec,1));
        %     pos = floor(size(train_vec,1)/10);
        %     test_vec = train_vec(1:pos,:);
        %     train_vec = train_vec((pos + 1):size(train_vec,1),:);
            test_vec = train_vec;
            DATA.train_vec = train_vec;
            DATA.test_vec = test_vec;
            DATA.userSize = userSize;

            Result = pmfUU_M(DATA, inputParas);
            vecU = Result.vecU;
            
            if ~isnan(Result.err_train(length( Result.err_train)))
                [results resM] = evaluation(label(FilInt), vecU(FilInt,:), ...
                    IMatrix, 1);
            else
                resM = [0 1; 0 0; 0 0; 0 NaN];
            end
            resout(:,:, iter) = resM;
        end
        resM2 = mean(resout,3);
        finalRes = [finalRes; resM2];
        res = [res; [length(finalRes) para_i para_j resM2(1,1) resM2(1,2) resM2(1,1)/resM2(1,2)]];
    end
end

lastPos = size(res,2);
index = find(res(:,lastPos) == max(res(:,lastPos)));
index = index(1);

if(~isempty(index))
    i = res(index,2);
    j = res(index,3);
    k = res(index,4);
    fprintf('\nbest results\n eps %f luva %f\n', epsionArray(i), luvaArray(j));

    resM2 = finalRes((res(index,1)-3):res(index,1),:);
    for i = 1:size(resM2,1)
        disp(sprintf([num2str(resM2(i,1)) '\t' num2str(resM2(i,2))]));
    end
    fprintf('total users: %d\n', sum(sum(results.M)));
    plot(1:length(Result.err_train), Result.err_train, 'b*');
else
    disp('No valid solutions found! Try other paramter settings...');
end

