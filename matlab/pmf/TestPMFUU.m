clear all
clc

load 'testData.mat'

% inputParas = 
%     batchSize: 50     % update a batch at a time
%       epsilon: 0.0200 % prior
%        lambda: 0.0500 % lambda and momentum determine the slope of
%                       % gradient descent
%      momentum: 0.9000
%      maxepoch: 400    % iteration
%      featSize: 10     %

% DATA = 
%     train_vec: [145x3 double] % train data: each line: u1 u2 score
%      test_vec: [145x3 double] % test data: each line: u1 u2 score
%      userSize: 112            % total user size

Result = pmfUU_M(DATA, inputParas);
vecU = Result.vecU;

plot(1:length(Result.err_train), Result.err_train, 'b*');
hold on; plot(1:length(Result.err_valid), Result.err_valid, 'r*');
legend('Train Error','Test Error')

