function spaMatrix = loadSparseMatrix(file)

% %%
% clear all
% clc
% 
% file = 'D:\Shared_documents\Matlab\recFees\input\testUserFees.txt';
%% sequential read the file and store it in a sparse matrix
if exist(file, 'file')
    % It exists.
%     storedVariablesStructure = load(fullFileName);
    fid = fopen(file);

    spaMatrix = sparse(0);
    tline = fgets(fid);
    count = 1;
    while ischar(tline)
        tmp(1,:) = strread(tline, '%d');
        if(count == 0)
            spaMatrix = sparse(tmp);
        else
            spaMatrix(count, 1:length(tmp)) = tmp;
        end
        count = count + 1;
        tline = fgets(fid);
        tmp = [];
    end

    fclose(fid);
else
    % It doesn't exist.
    warningMessage = sprintf(...
        'Error reading mat file\n%s.\n\nFile not found', file);
    uiwait(warndlg(warningMessage));
end

% ouputSparseMatrix(spaMatrix, ...
%     'D:\Shared_documents\Matlab\recFees\output\test.txt')