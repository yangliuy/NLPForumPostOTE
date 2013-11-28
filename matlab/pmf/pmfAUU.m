function Result = pmfAUU(DATA, inputParas);   
% clearvars -except DATA inputParas labels users aspects iter test_vec resout
%% AUUmatrix A U V UA UV

batchSize = inputParas.batchSize;
epsilon = inputParas.epsilon;
% lambda  = inputParas.lambda;
lambda_rc = inputParas.lambda_rc;
lambda_uva = inputParas.lambda_uva;
momentum = inputParas.momentum;
maxepoch= inputParas.maxepoch;
featSize = inputParas.featSize;
train_vec = DATA.train_vec;
% ignore train_vec > 0.5
% train_vec(train_vec(:,5) > 0.5,:) = []; 
test_vec = train_vec;
userSize = DATA.userSize;
itemSize = DATA.itemSize;

clear DATA inputParas

% uarate_mean = mean(train_vec(:,4));
uarate_mean = mean(train_vec(:,4));
uurate_mean = mean(train_vec(:,5));

pairs_tr = length(train_vec(:,4)); % training data
pairs_te = length(test_vec(:,4)); % validation data
uurating_raw = train_vec(:,5);
uarating_raw = train_vec(:,4);

numbatches = pairs_tr/batchSize; % Number of batches
if mod(pairs_tr, floor(numbatches)) > 0
    numbatches = numbatches + 1;
end
if floor(numbatches) == 1
    if pairs_tr > batchSize
        numbatches = numbatches + 1;
    end
end
numbatches = floor(numbatches);

vecU = randn(userSize, featSize)./featSize; % User feature vecators
vecM = randn(itemSize, featSize)./featSize; % item feature vecators
checkPoint = -inf;

%%
for epoch = 1:maxepoch  
    vecU1_inc = zeros(batchSize, featSize);
    vecU2_inc = zeros(batchSize, featSize);
    vecM_inc = zeros(batchSize, featSize);
    for batch = 1:numbatches
        minNum = min(batch*batchSize, pairs_tr);
        range = ((batch-1)*batchSize + 1):minNum;
        users1 = train_vec(range,2);
        users2 = train_vec(range,3);
        items = train_vec(range,1);
        uurating = uurating_raw(range) - uurate_mean;
        uarating = uarating_raw(range) - uarate_mean;
        % handle the last batch
        if length(range) < batchSize
            vecU1_inc = zeros(length(range), featSize);
            vecU2_inc = zeros(length(range), featSize);
            vecM_inc = zeros(length(range), featSize);
        end

        %% ui = ui - epsilon*(2 (ui.*vj - Rij) vj + 2 * lambda * ui)
        %% vj = vj - epsilon*(2 (ui.*vj - Rij) ui + 2 * lambda * vj)
        predDiff1 = repmat(sum(vecU(users1,:).*vecU(users2,:),2) - uurating,...
            1,featSize);
        predDiff2 = repmat(sum(vecU(users1,:).*vecM(items,:),2) - uarating,...
            1,featSize);
        VecUold = vecU;
        vecMold = vecM;
        vecU1_inc = momentum*vecU1_inc + 2*predDiff2.*vecMold(items,:) + ...
            2*lambda_rc*predDiff1.*VecUold(users2,:) + 2*lambda_uva*VecUold(users1,:);    
        vecU2_inc = momentum*vecU2_inc + ...
            2*lambda_rc*predDiff1.*VecUold(users1,:) + 2*lambda_uva*VecUold(users2,:);
        vecM_inc = momentum*vecM_inc + ...
            2*predDiff2.*VecUold(users1,:) + 2*lambda_uva*vecMold(items,:);

        %% Using the old VecUold to update vecU
        for i = 1:length(range)
            vecU(users1(i),:)=VecUold(users1(i),:) - epsilon*vecU1_inc(i,:); 
            vecU(users2(i),:)=VecUold(users2(i),:) - epsilon*vecU2_inc(i,:);
            vecM(items(i),:)=vecMold(items(i),:) - epsilon*vecM_inc(i,:); 
        end
    end

    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    %% training error
    users1 = train_vec(:,2);
    users2 = train_vec(:,3);
    items = train_vec(:,1);
    uurating = uurating_raw - uurate_mean;
    uarating = uarating_raw - uarate_mean;
    pred_out1 = sum(vecU(users1,:).*vecU(users2,:),2);
    pred_out2 = sum(vecU(users1,:).*vecM(items,:),2);
    f_s = sum( (pred_out1 - uurating).^2 + (pred_out2 - uarating).^2 + ...
        0.5*lambda_uva*( sum( (vecU(users1,:).^2 + vecU(users2,:).^2 + ...
        vecM(items,:).^2), 2)));
    err_train(epoch) = sqrt(f_s/pairs_tr);
    corrTR1(epoch) = corr(pred_out1, uurating);
    corrTR2(epoch) = corr(pred_out2, uarating);
    
%     %% Compute predictions on the validation set %%%%%%%%%%%%%%%%%%%%%% 
%     NN=pairs_te;
% 
%     users = test_vec(:,1);
%     items = test_vec(:,2);
%     rating = test_vec(:,3);
% 
%     pred_out = sum(vecU(items,:).*vecU(users,:),2) + rate_mean;
%     % Clip predictions 
%     ff=find(pred_out>max(train_vec(:,3)));
%     pred_out(ff)=max(train_vec(:,3));
%     ff2=find(pred_out<min(train_vec(:,3)));
%     pred_out(ff2)=min(train_vec(:,3));
    %       fprintf(1, '> maximum: %d, < minmum: %d, counts: %d\n', ...
    %                   length(ff), length(ff2), length(pred_out));

%     err_valid(epoch) = sqrt(sum((pred_out- rating).^2)/NN);
    err_valid(epoch) = 10;
    corrTE = 10;
    if mod(epoch, 40) == 0
        fprintf(1,'epoch %4i RMSE TR %6.4f Corr TR1 %1.4f TR2 %1.4f\n',...
        	epoch, err_train(epoch), corrTR1(epoch), corrTR2(epoch));
    end
    if isnan(err_train(epoch))
        break;
    end
    
    % update vecUFinal by looking at checkPoint
    newCheckPoint = 1/err_train(epoch);
    if newCheckPoint > checkPoint
        checkPoint = newCheckPoint;
        corrTR1Final = corrTR1(epoch);
        corrTR2Final = corrTR2(epoch);
        vecUFinal = vecU;
        vecMFinal = vecM;
        epochfinal = epoch;
    end
    
    %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    Result.err_train = err_train;
    Result.err_valid = err_valid;
    Result.vecU = vecUFinal;
    Result.vecM = vecMFinal;
    pred_out1 = sum(vecUFinal(users1,:).*vecUFinal(users2,:),2);
    pred_out2 = sum(vecUFinal(users1,:).*vecMFinal(items,:),2);
    Result.pred_out_uu = pred_out1 + uurate_mean;
    Result.pred_out_ua = pred_out2 + uarate_mean;
    Result.corrTR1 = corrTR1Final;
    Result.corrTR2 = corrTR2Final;
end
    fprintf(1,'epoch Final %d Corr TR1 %1.4f TR2 %1.4f\n', ...
              epochfinal, corrTR1Final, corrTR2Final);
          
          