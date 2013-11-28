function Result = pmfUA_M(DATA, inputParas)
%% test
%     save tmp.mat
%     error('check');
%     clear
%     clc
%     load tmp.mat
%%     load tmp.mat
    batchSize = inputParas.batchSize;
    epsilon = inputParas.epsilon;
    lambda  = inputParas.lambda;
    momentum = inputParas.momentum;
    maxepoch= inputParas.maxepoch;
    featSize = inputParas.featSize;

    train_vec = DATA.train_vec;
    test_vec = DATA.test_vec;
    userSize = DATA.userSize;
    itemSize = DATA.itemSize;

    %% train_vec validation data: user_id movie_id ratings 10-cross validation
    rate_mean = mean(train_vec(:,3)); 

    pairs_tr = length(train_vec); % training data 
    pairs_te = length(test_vec); % validation data 

    numbatches = pairs_tr/batchSize; % Number of batches  

    if mod(pairs_tr, floor(numbatches)) > 0
        numbatches = numbatches + 1;
    end
    if floor(numbatches) == 1
        if pairs_tr > batchSize
            numbatches = numbatches + 1;
        end
    end
    
    vecM = randn(itemSize, featSize)/featSize; % Movie feature vectors
    vecU = randn(userSize, featSize)/featSize; % User feature vecators

    %%
    for epoch = 1:maxepoch
        vecU_inc = zeros(batchSize, featSize);
        vecM_inc = zeros(batchSize, featSize);
        for batch = 1:numbatches
%             fprintf(1,'epoch %d batch %d \r',epoch,batch);
            minNum = min(batch*batchSize, pairs_tr);
            range = ((batch-1)*batchSize + 1):minNum;
            users = train_vec(range,1);
            items = train_vec(range,2);
            rating = train_vec(range,3) - rate_mean;
            
            % handle the last batch
            if length(range) < batchSize
                vecU_inc = zeros(length(range), featSize);
                vecM_inc = zeros(length(range), featSize);
            end
            
            %% ui = ui - epsilon*(2 (ui.*vj - Rij) vj + 2 * lambda * ui)
            %% vj = vj - epsilon*(2 (ui.*vj - Rij) ui + 2 * lambda * vj)
            predDiff = repmat( sum(vecU(users,:).*vecM(items,:),2) - rating, ...
                        1, featSize );
            vecU_inc = momentum*vecU_inc + ...
                2*predDiff.*vecM(items,:) + 2*lambda*vecU(users,:);    
            vecM_inc = momentum*vecM_inc + ...
                2*predDiff.*vecU(users,:) + 2*lambda*vecM(items,:);

            %% update vecU
            for i = 1:length(range)
                vecU(users(i),:) = vecU(users(i),:) - epsilon*vecU_inc(i,:); 
                vecM(items(i),:) = vecM(items(i),:) - epsilon*vecM_inc(i,:); 
            end
        end

        %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        %% training error
        users = train_vec(:,1);
        items = train_vec(:,2);
        rating = train_vec(:,3) - rate_mean;
        pred_out = sum(vecM(items,:).*vecU(users,:),2);
        f_s = sum( (pred_out - rating).^2 + ...
            0.5*lambda*( sum( (vecM(items,:).^2 + vecU(users,:).^2),2)));
        err_train(epoch) = sqrt(f_s/pairs_tr);

        %% Compute predictions on the validation set %%%%%%%%%%%%%%%%%%%%%% 
        NN=pairs_te;

        users = test_vec(:,1);
        items = test_vec(:,2);
        rating = test_vec(:,3);

        pred_out = sum(vecM(items,:).*vecU(users,:),2) + rate_mean;
        % Clip predictions 
%         ff=find(pred_out>max(train_vec(:,3)));
%         ff=find(pred_out>max(train_vec(:,3)));
        pred_out(pred_out>max(train_vec(:,3)))=max(train_vec(:,3));
%         ff2=find(pred_out<min(train_vec(:,3)));
        pred_out(pred_out<min(train_vec(:,3)))=min(train_vec(:,3));
        %       fprintf(1, '> maximum: %d, < minmum: %d, counts: %d\n', ...
        %                   length(ff), length(ff2), length(pred_out));

        err_valid(epoch) = sqrt(sum((pred_out- rating).^2)/NN);
        
        if mod(epoch, 40) == 0
            fprintf(1,'epoch %4i TrRMSE %6.4f TERMSE %6.4f\n',...
                      epoch, err_train(epoch), err_valid(epoch));
        end
        %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

        if epoch > maxepoch - 10
            %     distance = pdistM(vecM(items,:), vecU(users,:), 'cosine');
            %     save result.mat err_train err_valid vecM vecU pred_out items users
            %         save(output, 'err_train', 'err_valid', 'vecM', 'vecU', ...
            %             'pred_out', 'items', 'users', 'label');
            % a high correlation betwen cosine/euclidean and pred_out
            Result.err_train = err_train;
            Result.err_valid = err_valid;
            Result.vecU = vecU;
            Result.vecM = vecM;
            Result.pred_out = pred_out;
            Result.items = items;
            Result.users = users;
        end
    end