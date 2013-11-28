function Result = pmfU_M(DATA, inputParas)
    % save tmp.mat
    % clearvars -except train_vec test_vec inputParas itemSize userSize

    batchSize = inputParas.batchSize;
    epsilon = inputParas.epsilon;
    lambda  = inputParas.lambda;
    momentum = inputParas.momentum;
    maxepoch= inputParas.maxepoch;
    featSize = inputParas.featSize;
    train_vec = DATA.train_vec;
    test_vec = DATA.test_vec;
    userSize = DATA.userSize;
    %% movie data
    % load moviedata
    % % Map to [0 1]
    % train_vec(:, 3) = 1./(1 + exp(train_vec(:,3)));
    % probe_vec(:, 3) = 1./(1 + exp(probe_vec(:,3)));

    %% train_vec validation data: user_id movie_id ratings 10-cross validation
    rate_mean = mean(train_vec(:,3));
    rate_mean = 0.5;

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

    vecU = randn(userSize, featSize)./featSize; % User feature vecators

    %%
    for epoch = 1:maxepoch  
        vecU1_inc = zeros(batchSize, featSize);
        vecU2_inc = zeros(batchSize, featSize);
    %       fprintf(1, 'epoch %d \r', epoch);
        for batch = 1:numbatches
            %         fprintf(1,'epoch %d batch %d \r',epoch,batch);
            minNum = min(batch*batchSize, pairs_tr);
            range = ((batch-1)*batchSize + 1):minNum;
            users = train_vec(range,1);
            items = train_vec(range,2);
            rating = train_vec(range,3) - rate_mean;
            % handle the last batch
            if length(range) < batchSize
                vecU1_inc = zeros(length(range), featSize);
                vecU2_inc = zeros(length(range), featSize);
            end

            %% ui = ui - epsilon*(2 (ui.*vj - Rij) vj + 2 * lambda * ui)
            %% vj = vj - epsilon*(2 (ui.*vj - Rij) ui + 2 * lambda * vj)
            predDiff = repmat( sum(vecU(users,:).*vecU(items,:),2) - rating, ...
                        1, featSize );
            VecUold = vecU;
            vecU1_inc = momentum*vecU1_inc + ...
                2*predDiff.*VecUold(items,:) + 2*lambda*VecUold(users,:);    
            vecU2_inc = momentum*vecU2_inc + ...
                2*predDiff.*VecUold(users,:) + 2*lambda*VecUold(items,:);

            %% Using the old VecUold to update vecU
            for i = 1:length(range)
                vecU(users(i),:) = VecUold(users(i),:) - epsilon*vecU1_inc(i,:); 
                vecU(items(i),:) = VecUold(items(i),:) - epsilon*vecU2_inc(i,:);
            end
        end

        %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        %% training error
        users = train_vec(:,1);
        items = train_vec(:,2);
        rating = train_vec(:,3) - rate_mean;
        pred_out = sum(vecU(items,:).*vecU(users,:),2);
        f_s = sum( (pred_out - rating).^2 + ...
            0.5*lambda*( sum( (vecU(items,:).^2 + vecU(users,:).^2),2)));
        err_train(epoch) = sqrt(f_s/pairs_tr);
        corrTR = corr(pred_out, rating);

        %% Compute predictions on the validation set %%%%%%%%%%%%%%%%%%%%%% 
        NN=pairs_te;

        users = test_vec(:,1);
        items = test_vec(:,2);
        rating = test_vec(:,3);

        pred_out = sum(vecU(items,:).*vecU(users,:),2) + rate_mean;
        % Clip predictions 
        ff=find(pred_out>max(train_vec(:,3)));
        pred_out(ff)=max(train_vec(:,3));
        ff2=find(pred_out<min(train_vec(:,3)));
        pred_out(ff2)=min(train_vec(:,3));
        %       fprintf(1, '> maximum: %d, < minmum: %d, counts: %d\n', ...
        %                   length(ff), length(ff2), length(pred_out));

        err_valid(epoch) = sqrt(sum((pred_out- rating).^2)/NN);
        
        if mod(epoch, 40) == 0
            fprintf(1,'ep%4i RMSE TR %6.4f TE %6.4f Corr TR %1.4f TE %1.4f\n',...
                      epoch, err_train(epoch), err_valid(epoch), ...
                      corrTR, corr(pred_out, rating));
        end
        %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

        Result.err_train = err_train;
        Result.err_valid = err_valid;
        Result.vecU = vecU;
        Result.pred_out = pred_out;
        Result.items = items;
        Result.users = users;
    end