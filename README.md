# Data Mining: Clustering

The purpose of this project is to run either K-means or Hierarchical Clustering on a given dataset. In each dataset, each row corresponds to an object and each column corresponds to an attribute. The attribute values are comma-separated. You don’t need to do normalization for any of the datasets. The original Utilities dataset (Utilities_Unnormalized.xls) is un-normalized, but it is provided just for you to know the meaning of the clusters.

The initial centroids to be used in K-means for Iris and YeastGene datasets are provided. In each file, each row denotes the initial centroid of a cluster. Running the K-means algorithm will iterate as many times as specified, and then output to the appropriate cluster assignment file.


The code is written in Eclipse, so as such, if you use simply the two classes and csv files, and compile from the command line, it will not compile successfully. If you wish to run the java class file after compiling, make sure you run the matlab file first. This will create the requisite .csv files needed for the Java program. Here are the deletions necessary to compile from command line:

In HW3_Code.java:

delete "package src;"

With this change, the file will compile correctly with the standard command:

javac HW3_Code.java


More information about the project synopsis can be found in the doc file.
