package code;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;

// A note: the PCA Analysis is being done through matlab

public class HW3_Code {

	public enum Dataset {
		IRIS		("Iris.csv", "Iris_Initial_Centroids.csv", 4, 12), 
		YEASTGENE	("YeastGene.csv", "YeastGene_Initial_Centroids.csv", 7, 7), 
		UTILITIES	("Utilities.csv", null, 8, 0), 
		EXAMPLE		("Example.csv", null, 5, 0),
		NONE		(null, null, 0, 0);

		private final String filename;
		private final String centroid_filename;
		private final int numCols;
		private final int numKmeansIterations;

		Dataset(String filename, String centroid_filename, int numCols, int numKmeansIterations) {
			this.filename = filename;
			this.centroid_filename = centroid_filename;
			this.numCols = numCols;
			this.numKmeansIterations  = numKmeansIterations;
		}

		private String filename() { return filename;}
		private String centroid_filename() { return centroid_filename;}
		private int numCols() { return numCols;}
		private int numKmeansIterations() { return numKmeansIterations;}
	}

	static Dataset chosen_set;

	public static void main(String[] args) 
	{

		int columns;
		int rows;
		
		// Dynamic array to hold the various points. The arraylist holds multiple arraylists, each inside one represents a point 
		// in n-dimensional space, where n is the number of columns in our input 
		ArrayList<ArrayList<Double>> Data;
		ArrayList<ArrayList<Double>> Centroids = null;

		// If true, then user is looking for K-means:
		boolean Kmeans_Alg = false;
		// If true, user wants hierarchical:
		boolean Hierarchical_Alg = false;
		// REMINDER: we are using MIN to define inter-cluster distance

		chosen_set = Dataset.NONE;
		Scanner user_in = new Scanner(System.in);
		
		// User input to determine which dataset to use
		while (chosen_set == Dataset.NONE) 
		{
			String input;
			System.out.println("Please choose iris, yeast gene, the example, or utilities. \n"
							+ "Type 'iris,' 'example,' 'gene' or 'utilities.'");
			input = user_in.nextLine();
			input = input.replaceAll("[^a-zA-Z ]", "").toLowerCase();
			input = input.replaceAll("\n", "");
			input = input.replaceAll(" ", "");

			switch (input) 
			{
			case "gene": chosen_set = Dataset.YEASTGENE; break;
			case "example": chosen_set = Dataset.EXAMPLE; break;
			case "iris": chosen_set = Dataset.IRIS; break;
			case "utilities": chosen_set = Dataset.UTILITIES; break;
			default: chosen_set = Dataset.NONE; break;
			}
			
			if (chosen_set == Dataset.NONE) { System.out.println("Sorry, that is not one of the three options.");}
		} // End while loop
		
		// User input to decide if they want K-means algorithm or hierarchical
		while(!Kmeans_Alg && !Hierarchical_Alg)
		{
			String input;
			System.out.println("Please choose 'K-means' or 'Hierarchical.' \n");
			input = user_in.nextLine();
			input = input.replaceAll("[^a-zA-Z ]", "").toLowerCase();
			input = input.replaceAll("\n", "");
			
			switch (input) 
			{
			case "kmeans": Kmeans_Alg = true; break;
			case "hierarchical": Hierarchical_Alg = true; break;
			default: break;
			}
		}// End while loop
		
		user_in.close();
		
		// Import our chosen data into our 2D "data" dynamic arraylist:
		Data = import_data(chosen_set);
		if(Kmeans_Alg) Centroids = import_centroids(chosen_set);
		
		// Now we will run K-means or Hierarchical clustering
		if(Kmeans_Alg) 
		{
			int[] cluster_assignment = run_kmeans(Data,Centroids,chosen_set.numKmeansIterations());
			PrintWriter writer = null;
			try {
				writer = new PrintWriter("cluster_assignemt_"+chosen_set.filename, "UTF-8");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			for(int i = 0; i < cluster_assignment.length; i++)
			{
				writer.println(cluster_assignment[i]);
			}
			writer.close();
			// Now we use matlab to run our PCA from assignment 1
//			MatlabControl mc = new MatlabControl();
//			mc.eval("initial_matrix = csvread('"+chosen_set.filename+"')");
//			mc.eval("[coefficient,score,latent] = pca(initial_matrix)");
//			mc.eval("final_matrix = score(:,1:2)");
//			mc.eval("cluster_assignment = csvread('cluster_assignment"+chosen_set.filename+"')");
//			
		}
		if(Hierarchical_Alg) run_hierarch(Data);
	}

	private static void run_hierarch(ArrayList<ArrayList<Double>> data) {
//		System.out.println("Running hierarchical algorithm...");
		ArrayList<ArrayList<Double>> distance_matrix = new ArrayList<ArrayList<Double>>();
		ArrayList<Integer> removed_clusters = new ArrayList<Integer>();
		
		// First, we create a distance matrix
		for(int i = 0; i < data.size(); i++)
		{
			distance_matrix.add(new ArrayList<Double>());
			for(int j = 0; j < data.size(); j++)
			{
				distance_matrix.get(i).add(euclidean_dist(data.get(i),data.get(j)));
			}
		}
		
		// Next, the actual algorithm
		while(removed_clusters.size() < distance_matrix.size()-1)
		{
			// First, we must find the two clusters with the smallest distance
			double min = 90000;
			int row = 0;
			int col = 1;
			for(int i = 0; i < distance_matrix.size(); i++)
			{
				if (!removed_clusters.contains(i))
				{
					for(int j = i + 1; j < distance_matrix.size(); j++)
					{
						if (!removed_clusters.contains(j))
						{
							if(distance_matrix.get(i).get(j) < min)
							{
								min = distance_matrix.get(i).get(j);
								row = i;
								col = j;
							}
						}
					}
				}
			}

			System.out.println((row+1) + "\t" + (col+1) + "\t" + (distance_matrix.size()+1));
			
			removed_clusters.add(row);
			removed_clusters.add(col);
			
			// Add new cluster to distance matrix
			distance_matrix.add(new ArrayList<Double>());
			for(int i = 0; i < distance_matrix.size()-1; i++)
			{
				distance_matrix.get(distance_matrix.size()-1).add(Math.min(distance_matrix.get(row).get(i),distance_matrix.get(col).get(i)));
			}
			for(int i = 0; i < distance_matrix.size(); i++)
			{
				distance_matrix.get(i).add(Math.min(distance_matrix.get(row).get(i),distance_matrix.get(col).get(i)));
			}
			
		} // end while loop
	}

	private static int[] run_kmeans(ArrayList<ArrayList<Double>> Data, ArrayList<ArrayList<Double>> Centroids, int iterations)
	{
		ArrayList<ArrayList<Double>> NewCentroids = Centroids;
		
		
		// Stores, for each point, which cluster it is in
		int[] cluster_assignment = new int[Data.size()];
		
		// Loop for each iteration: assign points, then reassign cluster centroids
		for(int iter = 0; iter < iterations; iter++)
		{
			// Loop for each point
			for(int point = 0; point < Data.size(); point++)
			{
				double closest_dist = euclidean_dist(Data.get(point), NewCentroids.get(0));
				// System.out.println(closest_dist);
				int closest_cen = 0;
				
				// Loop for remaining centroids
				for(int cen = 1; cen < NewCentroids.size(); cen++)
				{
					double dist = euclidean_dist(Data.get(point),NewCentroids.get(cen));
					if (dist < closest_dist) 
					{
						closest_dist = dist;
						closest_cen = cen;
					}
				}
				cluster_assignment[point] = closest_cen;
			}
			//for(int x = 0; x < cluster_assignment.length; x ++){ System.out.println(cluster_assignment[x]);}
			NewCentroids = reassign_centroids(NewCentroids, Data,cluster_assignment);
		}
		
		// write the centroids final location
		DecimalFormat df = new DecimalFormat("#.####");
		for(int i = 0; i < NewCentroids.size(); i++)
		{
			System.out.print("Cluster " + (i+1) + ": ");
			for(int j = 0; j < NewCentroids.get(i).size(); j++)
			{
				System.out.print(df.format(NewCentroids.get(i).get(j)) + "\t");
			}
			System.out.print("\n");
		}
		
		return cluster_assignment;
	}

	private static ArrayList<ArrayList<Double>> reassign_centroids(ArrayList<ArrayList<Double>> Centroids, ArrayList<ArrayList<Double>> data, int[] cluster_assignment) 
	{
		ArrayList<ArrayList<Double>> tempCentroids = new ArrayList<ArrayList<Double>>();
		
		// Iterate for each centroid
		for (int i = 0; i < Centroids.size(); i++)
		{
			tempCentroids.add(new ArrayList<Double>());
			
			// Iterate the mean for each axis
			for (int axis = 0; axis < Centroids.get(0).size(); axis++)
			{
				double dist = 0;
				int count = 0;
				
				// To find mean, we sum up the value at that axis for each point, then divide by number of points
				for (int point = 0; point < data.size(); point++)
				{
					if(cluster_assignment[point] == i)
					{
						count ++;
						dist = dist + data.get(point).get(axis);
					}
				}
				tempCentroids.get(i).add(dist/count);
			}
			
		}
		return tempCentroids;
	}

	private static double euclidean_dist(ArrayList<Double> point1, ArrayList<Double> point2) 
	{
		double sum = 0.0;
		
		for (int i = 0; i < point1.size(); i++)
		{
			sum = sum + Math.pow(point1.get(i)-point2.get(i),2);
			//System.out.println(point1.get(i)+" " + point2.get(i)+ " " + sum);
		}
		sum = Math.sqrt(sum);
		return sum;
	}

	private static ArrayList<ArrayList<Double>> import_data(Dataset chosen_set)
	{
		ArrayList<ArrayList<Double>> imported_data = new ArrayList<ArrayList<Double>>();
		
		Scanner in_from_file = null;
		try
		{
			in_from_file = new Scanner (new File(chosen_set.filename()));
		} catch (FileNotFoundException e) {System.out.print("File not found");}
		in_from_file.useDelimiter(",");
		
		// We will try and dynamically allocate a new 2D dynamic matrix:
		int count = 0;
		while(in_from_file.hasNext())
		{
			imported_data.add(new ArrayList<Double>());
			String nextLine = in_from_file.nextLine();
			String[] charSplit = nextLine.split(",");
			
			for(int i = 0; i < chosen_set.numCols(); i++)
			{
				imported_data.get(count).add(Double.parseDouble(charSplit[i]));
			}
			count ++;
		}
		return imported_data;
	}
	
	private static ArrayList<ArrayList<Double>> import_centroids(Dataset chosen_set)
	{
		ArrayList<ArrayList<Double>> imported_centroids = new ArrayList<ArrayList<Double>>();
		
		Scanner in_from_file = null;
		try
		{
			in_from_file = new Scanner (new File(chosen_set.centroid_filename()));
		} catch (FileNotFoundException e) {System.out.print("File not found");}
		
		// We will try and dynamically allocate a new 2D dynamic matrix:
		int count = 0;
		while(in_from_file.hasNext())
		{
			imported_centroids.add(new ArrayList<Double>());
			String nextLine = in_from_file.nextLine();
			String[] charSplit = nextLine.split(",");
			
			for(int i = 0; i < chosen_set.numCols(); i++)
			{
				imported_centroids.get(count).add(Double.parseDouble(charSplit[i]));
			}
			count ++;
		}
		
		return imported_centroids;
	}
	
	private void SelectData(Dataset chosen_set) 
	{
		this.chosen_set = chosen_set;
	}
	
	
}
