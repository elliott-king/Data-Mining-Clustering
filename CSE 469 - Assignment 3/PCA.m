iris = 'Iris.csv';
iris_assignment = 'cluster_assignemt_Iris.csv';

yeast = 'YeastGene.csv';
yeast_assignment = 'cluster_assignemt_YeastGene.csv';

% input either cho, iyer, or iris after csvread
initial_matrix = csvread(yeast);
[coefficient,score,latent] = pca(initial_matrix);
final_matrix = score(:,1:2);
cluster_assignment = csvread(yeast_assignment);

for i = 1:size(cluster_assignment,1)
    % if the point is part of cluster 0, color that point red
    if (cluster_assignment(i,1) == 0)
        p0 = plot(final_matrix(i,1),final_matrix(i,2),'r.');
        hold on;
    end
    % if the point is part of cluster 1, color that point green
    if (cluster_assignment(i,1) == 1)
        p1 = plot(final_matrix(i,1),final_matrix(i,2),'g.');
        hold on;
    end
    % if the point is part of cluster 2, color that point blue
    if (cluster_assignment(i,1) == 2)
        p2 = plot(final_matrix(i,1),final_matrix(i,2),'b.');
        hold on;
    end
    % if the point is part of cluster 3, color that point magenta
    if (cluster_assignment(i,1) == 3)
        p3 = plot(final_matrix(i,1),final_matrix(i,2),'m.');
        hold on;
    end
    % if the point is part of cluster 4, color that point cyan
    if (cluster_assignment(i,1) == 4)
        p4 = plot(final_matrix(i,1),final_matrix(i,2),'c.');
        hold on;
    end
    % if the point is part of cluster 5, color that point yellow
    if (cluster_assignment(i,1) == 5)
        p5 = plot(final_matrix(i,1),final_matrix(i,2),'y.');
        hold on;
    end
end

