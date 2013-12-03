clear
clc

load methodRes.mat

res(1,:,:) = Freq_1;
res(2,:,:) = Freq_2;
res(3,:,:) = Disc_1;
res(4,:,:) = Disc_2;

[dim1 dim2 dim3] = size(res);

length = 10;
k = 1;
N = dim1;
for i = 1:N
    for j = i+1:N
        disp([num2str(i) ' ' num2str(j) ', ']);
        tut1 = map3d22d(res(i,:,1:length), 1, 1);
        tut2 = map3d22d(res(j,:,1:length), 1, 1);
        for m = 1:size(tut1,1)
            a = max(size(intersect(tut1(m,:), tut2(m,:))));
            b = max(size(union(tut1(m,:), tut2(m,:))));
            sim(m,k) = a/b;
        end
        k = k + 1;
    end
end
