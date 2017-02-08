
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * 
 * Directs analysis of Fiji NET_analysis output. 
 * Inputs .csv files from a specified folder and outputs 
 * modified .csv files to specified folder. 
 * 
 * Images from a single subject/sample are analyzed together to
 * improve sample size and provide accurate measurements.
 * 
 * Program pools ROIs from all images within folder. 
 * It then eliminates outliers based on Raw Integrated Density (RID). 
 * Any ROIs with a RID greater than a predetermined (value*SD of all ROIs) 
 * from the mean ROI's RID are excluded. Relative chromatin decondensation 
 * and %NETosis are calculated by averaging the area of the 5 smallest, 
 * non-excluded ROI's and dividing the area of the ROI of interest by that value. 
 *  
 * The value of chromatin decondensation which defines a NET is based upon
 * a user defined cutoff. Currently we use 4.72x avg area for Human PMNs
 * and 5.00 for Murine PMNs.
 * 
 * @author RyanRebernick
 *
 */

public class Multi_NET_Analysis {
//
	//user determined parameters
	static Double CDcutoff1, CDcutoff2, CDcutoff3, CDcutoff4;	//cutoff level to determine whether NET
	static String outputDirectory, inputDirectory;		//directory files will be saved to
	static Double upperCutoff;			//upper cutoff SD
	static Double lowerCutoff;			//lower cutoff SD
	static Double NETcutoff;
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
		//UPPER STANDARD CUTOFF VALUE		1.4(Human) 1.7(Mouse)
		upperCutoff = 1.7;
		//LOWER STANDARD CUTOFF VALUE
		lowerCutoff = 1.3;
		
		//CHROMATIN DECONDENSATION CUTOFF VALUE 1
		CDcutoff1 = 3.0;
		//CHROMATIN DECONDENSATION CUTOFF VALUE 2
		CDcutoff2 = 4.0;
		//CHROMATIN DECONDENSATION CUTOFF VALUE 3
		CDcutoff3 = 5.0;
		//CHROMATIN DECONDENSATION CUTOFF VALUE 4
		CDcutoff4 = 6.0;
		
		//NET CUTOFF		4.72(human) 5.00(mouse)
		NETcutoff = 5.0;
		
		//TREATMENT COMPARING W/IN SUBJECT
		treatment = "IO";

		allFiles = new ArrayList<Matrix>();	//initializes array to hold all matricies
		allRID = new ArrayList<Double>();	//array to hold all RIDs for taking average


		//folder from which .csv files taken out of; INPUT DIRECTORY
		File folder = new File(inputDirectory);
		File[] listOfFiles = folder.listFiles();
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

				if (check.equals("csv")){	
					Matrix newMatrix = new Matrix(cFile, fileName, isTreatment);
					allFiles.add(newMatrix);		
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
			m.update(average, CDcutoff1, CDcutoff2, CDcutoff3, CDcutoff4, NETcutoff);
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
			values.addAll(x.getNonOutlierAreas());}

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
		
		//ensures lowercutoff eliminates fragments
		//Mouse(7500.0) 	Human (20000.0)		
		if (lowerCutoff < 7500.0){
			//TODO Remove if you want...
			lowerCutoff = 7500.0;
		}
		
	}
	
	
	
	/**
	 * Exports the averages of all files by treatment into 
	 * separate CSV file called "Total"
	 * @throws FileNotFoundException
	 */
	private static void totalCSV() throws FileNotFoundException {
		//compute average NETosis and output to CSV
				ArrayList<Double> nonTreatmentNormalized = new ArrayList<Double>();
				ArrayList<Double> treatmentsNormalized = new ArrayList<Double>();
				ArrayList<Double> nonTreatment = new ArrayList<Double>();
				ArrayList<Double> treatments = new ArrayList<Double>();
				int treatmentNETs = 0;
				int nonTreatmentNETs = 0;
				Double treatmentNETosis, nonTreatmentNETosis, avgTreatmentNormalized, avgNonTreatmentNormalized, 
				avgTreatment, avgNonTreatment, treatmentSD, nonTreatmentSD, treatmentNormalizedSD, nonTreatmentNormalizedSD, ttest;
				
				treatmentNETosis = nonTreatmentNETosis = avgTreatmentNormalized = avgNonTreatmentNormalized =
				avgTreatment = avgNonTreatment = treatmentNormalizedSD = nonTreatmentNormalizedSD = treatmentSD = nonTreatmentSD = ttest = 0.0;
				
				//adds all non-outlier RID values from csv files into appropriate
				//list to be used for average %NETosis and average NET relative area
				for (Matrix m: allFiles){
					if (m.isTreatment()){
						treatmentsNormalized.addAll(m.getNormalizedAreas());
						treatments.addAll(m.getNonOutlierAreas());
					}
					else {
						nonTreatmentNormalized.addAll(m.getNormalizedAreas());
						nonTreatment.addAll(m.getNonOutlierAreas());
					}
				}
				
				//computes normalized averages and calculates %NETosis based on NETcutoff2
				for (Double q: treatmentsNormalized) {
					if (q > CDcutoff2) {
						treatmentNETs++;
						avgTreatmentNormalized = avgTreatmentNormalized + q;
						
					} else {
						avgTreatmentNormalized = avgTreatmentNormalized + q;
					}
				}
				for (Double w: nonTreatmentNormalized) {
					if (w > CDcutoff2) {
						nonTreatmentNETs++;
						avgNonTreatmentNormalized = avgNonTreatmentNormalized + w;
					} else {
						avgNonTreatmentNormalized = avgNonTreatmentNormalized + w;
					}
				}
				
				//computes actual averages
				for (Double q: treatments) {
						avgTreatment = avgTreatment + q;
				}
				for (Double w: nonTreatment) {
						avgNonTreatment = avgNonTreatment + w;
				}
				
				//normalized average
				avgTreatmentNormalized = (avgTreatmentNormalized/treatmentsNormalized.size());
				avgNonTreatmentNormalized = (avgNonTreatmentNormalized/nonTreatmentNormalized.size());
				//actual average
				avgTreatment = (avgTreatment/treatments.size());
				avgNonTreatment = (avgNonTreatment/nonTreatment.size());
				
				//Percent NETosis
				treatmentNETosis = ((double) treatmentNETs/treatmentsNormalized.size())*100;
				nonTreatmentNETosis = ((double) nonTreatmentNETs/nonTreatmentNormalized.size())*100;
				
				//computes SD and SEM for normalized areas
				for (Double qq: treatmentsNormalized){
					treatmentNormalizedSD += ((qq-avgTreatmentNormalized)*(qq-avgTreatmentNormalized));
				}
				for (Double ww: nonTreatmentNormalized){
					nonTreatmentNormalizedSD += ((ww-avgNonTreatmentNormalized)*(ww-avgNonTreatmentNormalized));
				}
				treatmentNormalizedSD = Math.sqrt(treatmentNormalizedSD/(treatmentsNormalized.size()-1));
				nonTreatmentNormalizedSD = Math.sqrt(nonTreatmentNormalizedSD/(nonTreatmentNormalized.size()-1));
				
				//SEM
				Double treatmentNormalizedSEM = (treatmentNormalizedSD/(Math.sqrt(treatmentsNormalized.size())));
				Double nonTreatmentNormalizedSEM = (nonTreatmentNormalizedSD/(Math.sqrt(nonTreatmentNormalized.size())));
				
				//computes SD and SEM for actual areas
				for (Double qq: treatments){
					treatmentSD += ((qq-avgTreatment)*(qq-avgTreatment));
				}
				for (Double ww: nonTreatment){
					nonTreatmentSD += ((ww-avgNonTreatment)*(ww-avgNonTreatment));
				}
				treatmentSD = Math.sqrt(treatmentSD/(treatments.size()-1));
				nonTreatmentSD = Math.sqrt(nonTreatmentSD/(nonTreatment.size()-1));
				
				//SEM
				Double treatmentSEM = (treatmentSD/(Math.sqrt(treatments.size())));
				Double nonTreatmentSEM = (nonTreatmentSD/(Math.sqrt(nonTreatment.size())));
				
				//ttest
				ttest = (avgTreatment - avgNonTreatment);
				ttest = ttest/(  Math.sqrt( ((treatmentSEM*treatmentSEM)/treatments.size()) +  ((nonTreatmentSEM*nonTreatmentSEM)/nonTreatment.size()) ) );
				
				PrintWriter pw = new PrintWriter(new File(outputDirectory + "Total.csv" + "\\"));
				StringBuilder sb = new StringBuilder();
				
				//relative areas
				sb.append(" " + ',' + treatment + ',' + "Control" + ',');
				sb.append('\n');
				sb.append("% NETosis" + ',' + treatmentNETosis + ',' + nonTreatmentNETosis + ',');
				sb.append('\n');
				sb.append("Avg. Normalized Area" + ',' + avgTreatmentNormalized + ',' + avgNonTreatmentNormalized + ',');
				sb.append('\n');
				sb.append("ANA SD" + ',' + treatmentNormalizedSD + ',' + nonTreatmentNormalizedSD + ',');
				sb.append('\n');
				sb.append("ANA SEM" + ',' + treatmentNormalizedSEM + ',' + nonTreatmentNormalizedSEM + ',');
				sb.append('\n');
				sb.append("Total Cells" + ',' + treatmentsNormalized.size() + ',' + nonTreatmentNormalized.size() + ',');
				
				//actual areas
				sb.append('\n');
				sb.append('\n');
				sb.append("Avg. Area" + ',' + avgTreatment + ',' + avgNonTreatment + ',');
				sb.append('\n');
				sb.append("ANA SD" + ',' + treatmentSD + ',' + nonTreatmentSD + ',');
				sb.append('\n');
				sb.append("ANA SEM" + ',' + treatmentSEM + ',' + nonTreatmentSEM + ',');
				
				//ttest
				sb.append('\n');
				sb.append('\n');
				sb.append("tscore:" + ',' + ttest + ',');
				
				pw.write(sb.toString());
				pw.close();
	}
}
