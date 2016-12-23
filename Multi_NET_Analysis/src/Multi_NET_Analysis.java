
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * TEST BRANCH (12.23.2016)
 * 		Added features from master:
 * 			-multiple NET cutoffs
 * 			-number of NETs per image by cutoff
 * 
 * 
 * Directs analysis of Fiji NET_analysis output. 
 * Inputs .csv files from a specified folder and outputs 
 * modified .csv files to specified folder. Program eliminates 
 * outlier cell values by 
 * 
 * calculating average of 5 smallest cells
 * from all images w/in folder. That average is used to normalize 
 * all other cells to determine if NET. 
 * averages for all fields.
 * 
 * @author RyanRebernick
 *
 */

public class Multi_NET_Analysis {
//
	//user determined parameters
	static Double NETcutoff1, NETcutoff2, NETcutoff3, NETcutoff4;	//cutoff level to determine whether NET
	static String outputDirectory, inputDirectory;		//directory files will be saved to
	static Double upperCutoff;			//upper cutoff SD
	static Double lowerCutoff;			//lower cutoff SD
	static String treatment;

	//program parameters
	static int numCells;		//number of data-containing cells in current .csv file
	static String fileName;		//name of the file being edited
	static String[] oldLabels;	//array holding labels in top column of .csv file
	static ArrayList<Matrix> allFiles;	//holds matrix from 5 files
	static ArrayList<Double> allRID;

	/**
	 * @param args
	 */
	public static void main(String[] args) throws FileNotFoundException {
		//OUTPUT DIRECTORY
		outputDirectory = "G:\\Team\\Shelef Lab\\NETosis Analysis Program\\JAVA_output\\";
		//INPUT DIRECTORY
		inputDirectory = "G:\\Team\\Shelef Lab\\NETosis Analysis Program\\FIJI_output\\";
		//UPPER STANDARD CUTOFF VALUE
		upperCutoff = 1.5;
		//LOWER STANDARD CUTOFF VALUE
		lowerCutoff = 1.5;
		
		//NET CUTOFF VALUE 1
		NETcutoff1 = 2.0;
		//NET CUTOFF VALUE 2
		NETcutoff2 = 3.0;
		//NET CUTOFF VALUE 3
		NETcutoff3 = 4.0;
		//NET CUTOFF VALUE 4
		NETcutoff4 = 5.0;
		
		//TREATMENT COMPARING W/IN SUBJECT
		treatment = "IO";

		allFiles = new ArrayList<Matrix>();	//initializes array to hold all matricies
		allRID = new ArrayList<Double>();	//array to hold all RIDs for taking average


		//folder from which .csv files taken out of; INPUT DIRECTORY
		File folder = new File(inputDirectory);
		File[] listOfFiles = folder.listFiles();
		int i = 0;	//count for allFiles array of matricies
		Boolean isTreatment = false;

		for (File cFile : listOfFiles){
			if(cFile.isFile()) {
				isTreatment = false;
				String cFileString = cFile.toString();
				String check = cFileString.substring(cFileString.lastIndexOf('.') +1);
				fileName = cFileString.substring(cFileString.lastIndexOf('\\') +1);
				if (fileName.contains(treatment)) {
					isTreatment = true;
				}
				System.out.println(fileName);
				System.out.println(isTreatment);
				if (check.equals("csv")){	
					Matrix newMatrix = new Matrix(cFile, fileName, isTreatment);
					allFiles.add(newMatrix);
					i++;		
				}
			}
		}

		//Find cutoff values from RID of all matricies in allFiles
		findCutoffs();

		//Find outliers for each file based on cutoffs generated from all RID values
		for (Matrix next: allFiles){
			next.findOutliers(lowerCutoff, upperCutoff);
		}

		//computes average of 5 smallest cells
		double average = normalize(allFiles);

		//computes new parameters and updates matrix with new param values
		for(Matrix m: allFiles){
			m.update(average, NETcutoff1, NETcutoff2, NETcutoff3, NETcutoff4);
			m.createCSV(outputDirectory);
		}
		
		//creates CSV file containing summary of data
		//between user specified treatments.
		totalCSV();	
	}



	/**
	 * Finds 5 smallest non outlier cells
	 * 
	 * @param list - the arraylist containing all matricies
	 * @return the average of the 5 smallest cells
	 */
	public static double normalize(ArrayList<Matrix> list) {
		ArrayList<Double> values = new ArrayList<Double>();
		Double average = 0.0;

		//collects all values within files in folder
		for (Matrix x: list){
			values.addAll(x.getNonOutliers());}

		//sorts values from low to high
		values.sort(null);

		//takes average of smallest 5 cells
		for (int i=0; i<5; i++) {
			average = average + values.get(i); } 
		average = average/5;
		return average;
	}


	/**
	 * Iterates through the RID of all cells
	 * w/in file folder to determine average and create cutoffs
	 * based on user specified SD. 
	 * 
	 */
	public static void findCutoffs() {
		//generates list of all RID values from all files
		for (Matrix m: allFiles) {
			allRID.addAll(m.getRID()); }

		Double average = 0.0;
		Double variance = 0.0;
		Double SD = 0.0;
		int numCells = 0;

		for (Double d: allRID) {
			average = average + d;
			numCells++; }
		average = average/numCells;

		//gets SD of RID
		for (Double d: allRID){
			if (d!=null){
				variance += ((d-average)*(d-average));	
			}
		} 
		variance = variance/(numCells-1);
		SD = Math.sqrt(variance);

		//sets RID CUTOFF VALUES
		upperCutoff = average + (upperCutoff*SD);
		lowerCutoff = average - (lowerCutoff*SD);
	}
	
	
	
	/**
	 * Exports the averages of all files by treatment into 
	 * separate CSV file called "Total"
	 * @throws FileNotFoundException
	 */
	private static void totalCSV() throws FileNotFoundException {
		// TODO Auto-generated method stub
		//compute average NETosis and output to CSV
				ArrayList<Double> nonTreatment = new ArrayList<Double>();
				ArrayList<Double> treatments = new ArrayList<Double>();
				int treatmentNETs = 0;
				int nonTreatmentNETs = 0;
				Double treatmentNETosis = 0.0;
				Double nonTreatmentNETosis = 0.0;
				Double avgTreatment = 0.0;
				Double avgNonTreatment = 0.0;
				
				//adds all non-outlier RID values from csv files into appropriate
				//list to be used for average %NETosis and average NET relative area
				for (Matrix m: allFiles){
					if (m.isTreatment()){
						treatments.addAll(m.getNormalizedAreas());
					}
					else {
						nonTreatment.addAll(m.getNormalizedAreas());
					}
				}
				
				//computes averages
				for (Double q: treatments) {
					if (q > NETcutoff1) {
						treatmentNETs++;
						avgTreatment = avgTreatment + q;
					} else {
						avgTreatment = avgTreatment + q;
					}
				}
				for (Double w: nonTreatment) {
					if (w > NETcutoff1) {
						nonTreatmentNETs++;
						avgNonTreatment = avgNonTreatment + w;
					} else {
						avgNonTreatment = avgNonTreatment + w;
					}
				}

				avgTreatment = (avgTreatment/treatments.size());
				avgNonTreatment = (avgNonTreatment/nonTreatment.size());
				treatmentNETosis = ((double) treatmentNETs/treatments.size())*100;
				nonTreatmentNETosis = ((double) nonTreatmentNETs/nonTreatment.size())*100;
				Double treatmentSD = 0.0;
				Double nonTreatmentSD = 0.0;
				
				for (Double qq: treatments){
					treatmentSD += ((qq-avgTreatment)*(qq-avgTreatment));
				}
				for (Double ww: nonTreatment){
					nonTreatmentSD += ((ww-avgNonTreatment)*(ww-avgNonTreatment));
				}
				treatmentSD = (treatmentSD/(treatments.size()-1));
				nonTreatmentSD = (nonTreatmentSD/(nonTreatment.size()-1));
				Double treatmentSEM = (treatmentSD/(Math.sqrt(treatments.size())));
				Double nonTreatmentSEM = (nonTreatmentSD/(Math.sqrt(nonTreatment.size())));
				
				PrintWriter pw = new PrintWriter(new File(outputDirectory + "Total.csv" + "\\"));
				StringBuilder sb = new StringBuilder();
				
				sb.append(" " + ',' + treatment + ',' + "Control" + ',');
				sb.append('\n');
				sb.append("% NETosis" + ',' + treatmentNETosis + ',' + nonTreatmentNETosis + ',');
				sb.append('\n');
				sb.append("Avg. Normalized Area" + ',' + avgTreatment + ',' + avgNonTreatment + ',');
				sb.append('\n');
				sb.append("ANA SD" + ',' + treatmentSD + ',' + nonTreatmentSD + ',');
				sb.append('\n');
				sb.append("ANA SEM" + ',' + treatmentSEM + ',' + nonTreatmentSEM + ',');
				
				
				pw.write(sb.toString());
				pw.close();
	}
}
