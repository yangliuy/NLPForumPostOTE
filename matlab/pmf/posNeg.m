%% pos neg
clear all
clc

%% users label pos neg
load posNeg_4.mat

%% pos
data1 = pos(label == 1, :);
data2 = pos(label == 2, :);
sumData1 = sum(data1,1);
sumData2 = sum(data2,1);
POSsumData(:,1) = sumData1;
POSsumData(:,2) = sumData2;

%% neg
data1 = neg(label == 1, :);
data2 = neg(label == 2, :);
sumData1 = sum(data1,1);
sumData2 = sum(data2,1);
NEGsumData(:,1) = sumData1;
NEGsumData(:,2) = sumData2;