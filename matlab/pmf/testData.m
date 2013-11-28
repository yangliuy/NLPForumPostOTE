clear all
clc

%% data pro
userno = 150;
users = (1:userno)';
label = [ones(userno/2,1); ones(userno/2,1)*2];
% label = floor(rand(userno, 1)*2) + 1;

%%
spa = 0.10;

% create data
sel = randperm(size(users,1)^2, size(users,1)^2*spa);
uuRate = sparse(length(users), length(users));
for i = 1:length(sel)
    le = length(users) + 1;
    p1 = floor(sel(i)/le) + 1;
    p2 = mod(sel(i), le-1);
    if p2 == 0
        p2 = le - 1;
    end
    if(label(p1) ~= label(p2))
        uuRate(p1,p2) = 0.1;
    else
        uuRate(p1,p2) = 0.9;
    end
    val = full(uuRate(p1,p2));
    disp([sel(i) p1 p2 val]);
end

save('test.mat', 'label', 'users', 'uuRate');

load test.mat
% plot non-zero score
A = uuRate;
A(A < 0.5) = 0;
S = A * A' + speye(size(A));
pct = 100 / numel(A);

clf; spy(S), title('A Sparse Symmetric Matrix')
nz = nnz(S);
xlabel(sprintf('nonzeros=%d (%.3f%%)',nz,nz*pct));

