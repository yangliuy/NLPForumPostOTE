%*************************************************************************
%  Adjusted Rand Index(Rand Index) cal.
%  function : R = adjRI(source,c1,c2);
function  R = adjRI(c1,c2)
% save temp.mat
%*************************************************************************
%  source: n*d  source matrix
%  c1:  1*n calculated membership vector where c(j) in 1..K (K clusters)
%  c2:  the same as c1
%  Note that R.adjRI is the Adjusted Rand Index value
%            R.RI is the Rand Index value
%  J 2008.12.22
%*************************************************************************
% c1 = [ 1 1 1 1 1 1 ]
% c1 = [1 1 1 2 2 2];
% c2 = [1 1 2 2 2 3];
% c1 = ones(1,1000);
% c2(1,1:250)  = 1;
% c2(1,251:500)  = 2;
% c2(1,501:750)  = 3;
% c2(1,751:1000)  = 4;
if size(c1,2)~=size(c2,2) 
    error('c1 and c2 should have the same lenth'); 
end    
% Notation for Comparing Two Partitions generate a max(c2)*max(c1) matrix
noc = zeros( max(c2),max(c1) );
for j = 1:1:max(c1)
    for i = 1:1:max(c2)
        noc(i,j) = sum( (c1 == j)&(c2 == i),2 );
    end
end
noc(i+1,:) = sum( noc(1:i,:),1 );
noc(:,j+1) = sum( noc(:,1:j),2 ); % the (i+1)*(j+1) is not to be used
% i = 3;
% j = 3;
% noc = [ 1 1 0 2;1 2 1 4;0 0 4 4;2 3 5 10];
Rnoc = noc.*(noc-1)/2;
% a b c d :type I~IV propose by Rand(1971) in which 
% a  :objects in the pair are placed in the same class in U and in
%     the same class in V;
% b  : in different classes in U and in different classes in V;
% c  : in different classes in U and in the same class in V;
% d  : in the same class in U and in different classes in V.
n  = sum( sum( noc(1:i,1:j) ) ); 
tm = n*(n-1)/2;
a1 = sum( Rnoc( 1:i,j+1 ) );
a2 = sum( Rnoc( i+1,1:j ) );
a  = sum( sum( Rnoc(1:i,1:j) ) );
b  = a2 -a;
c  = a1 -a;
d  = tm - a - b -c;
% return the result
R.adjRI = (a -(a1*a2/tm))/(0.5*(a1 + a2)-a1*a2/tm);
R.RI = ( a+d )/ tm;

