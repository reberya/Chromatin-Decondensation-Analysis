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
 * Program pools ROIs from all images within specified folder. 
 * It then eliminates outliers based on Raw Integrated Density (RID). 
 * Any ROIs with a RID greater than a predetermined (value*SD of all ROIs) 
 * from the mean ROI's RID are excluded. Relative DNA decondensation 
 * and %NETosis are calculated by averaging the area of the 5 smallest, 
 * non-excluded ROI's and dividing the area of the ROI of interest by that value. 
 *  
 * The value of DNA decondensation which defines a NET is based upon
 * a user defined cutoff. 4.72x the avg area for Human PMNs
 * and 5.00 for Murine PMNs are the default settings.
 * 
 * @author RyanRebernick
 *
 */

public class Multi_NET_Analysis {
//
	//user determined parameters
	static Double CDcutoff1, CDcutoff2, CDcutoff3, CDcutoff4;	//cutoff level to determine whether NET
	static String outputDirectory, inputDirectory;		//directory files will be saved to
	static Double upperCutoff;			//upper cutoff SD for excluding cells
	static Double lowerCutoff;			//lower cutoff SD for excluding cells
	static Double minRID;				//lowest allowed lowercutoff value for excluding cells
	static Double NETcutoff;			//cutoff for whether NET
	static String treatment;			//Optional separation point

	//program parameters
	static int numCells;				//number of data-containing cells in current .csv file
	static String fileName;				//name of the file being edited
	static String[] oldLabels;			//array holding labels in top column of .csv file
	static ArrayList<Matrix> allFiles;	//holds matrix from 5 files
	static ArrayList<Double> allRID;	//all Raw integrated density values

	/**
	 * The main functional class of DANA. Takes in user params from GUI
	 * and begins reading in .csv files from specified folders.  Calls
	 * respective methods/classes to carry out analysis. 
	 * 
	 * 
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public Multi_NET_Analysis(String output, String input, Double upper, Double lower, Double min, Double CD1,
			Double CD2, Double CD3, Double CD4, Double Net, String oParam) throws FileNotFoundException {
		
		inputDirectory = input;
		outputDirectory = output;
		upperCutoff = upper;
		lowerCutoff = lower;
		minRID = min;
		CDcutoff1 = CD1;
		CDcutoff2 = CD2;
		CDcutoff3 = CD3;
		CDcutoff4 = CD4;
		NETcutoff = Net;

		allFiles = new ArrayList<Matrix>();	//initializes array to hold all matricies
		allRID = new ArrayList<Double>();	//array to hold all RIDs for taking average
		treatment = oParam;

		//folder from which .csv files taken out of; INPUT DIRECTORY
		File folder = new File(inputDirectory);
		File[] listOfFiles = folder.listFiles();
		Boolean isTreatment = false;
		
		//loops through directory taking only .csv files and seperating
		//based on optional user-specified treatment.
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
	 * Finds average of 5 smallest non-outlier cells.
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
	 * Iterates through the raw integrated density of all cells
	 * within file folder to determine average and create cutoffs
	 * based on user specified values. 
	 * 
	 */
	public static void findCutoffs() {
		//generates list of all RID values from all files
		for (Matrix m: allFiles) {
			allRID.addAll(m.getRID()); }

		Double average, variance, SD;
		average = variance = SD = 0.0;
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
		if (lowerCutoff < 20000.0){
			lowerCutoff = minRID;
		}
		
	}
	
	
	
	/**
	 * Exports the averages of all files by treatment into 
	 * separate CSV file called "Summary"
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
				Double treatmentNETosis, nonTreatmentNETosis, avgTreatmentNormalized, 
				avgNonTreatmentNormalized, avgCombinedNormalized, avgTreatment, 
				avgNonTreatment, treatmentSD, nonTreatmentSD, treatmentNormalizedSD, 
				nonTreatmentNormalizedSD, ttest, combinedNormalizedSD, avgCombined, 
				combinedNETosis, combinedSD;
				
				treatmentNETosis = nonTreatmentNETosis = avgTreatmentNormalized = avgNonTreatmentNormalized = combinedNormalizedSD =
				avgTreatment = avgNonTreatment = treatmentNormalizedSD = nonTreatmentNormalizedSD = treatmentSD = nonTreatmentSD 
				= ttest = avgCombinedNormalized = avgCombined = combinedNETosis = combinedSD = 0.0;
				

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
				
				//Number of cells
				int normTreatmentSize = treatmentsNormalized.size();
				int treatmentsSize = treatments.size();
				int normNonTreatmentsSize = nonTreatmentNormalized.size();
				int nonTreatmentSize = nonTreatment.size();
				int combinedSize = treatmentsSize + normNonTreatmentsSize;	
				
				//computes normalized averages and calculates %NETosis based on NETcutoff2
				for (Double q: treatmentsNormalized) {
					if (q > NETcutoff) {
						treatmentNETs++;
					} 
					avgTreatmentNormalized = avgTreatmentNormalized + q;
				}
				for (Double w: nonTreatmentNormalized) {
					if (w > NETcutoff) {
						nonTreatmentNETs++;
					}
					avgNonTreatmentNormalized = avgNonTreatmentNormalized + w;
				}
				
				//computes actual averages
				for (Double q: treatments) {
						avgTreatment = avgTreatment + q;
				}
				for (Double w: nonTreatment) {
						avgNonTreatment = avgNonTreatment + w;
				}
				
				
				//normalized average
				avgCombinedNormalized = ((avgTreatmentNormalized+avgNonTreatmentNormalized)/(combinedSize));
				avgTreatmentNormalized = (avgTreatmentNormalized/treatmentsSize);
				avgNonTreatmentNormalized = (avgNonTreatmentNormalized/nonTreatmentSize);
				
				
				//actual average
				avgCombined = (avgTreatment + avgNonTreatment)/(treatmentsSize+nonTreatmentSize);
				avgTreatment = (avgTreatment/treatmentsSize);
				avgNonTreatment = (avgNonTreatment/nonTreatmentSize);
				
				//Percent NETosis
				treatmentNETosis = ((double) treatmentNETs/treatmentsSize)*100;
				nonTreatmentNETosis = ((double) nonTreatmentNETs/nonTreatmentSize)*100;
				combinedNETosis = ((double) (treatmentNETs+nonTreatmentNETs)/(combinedSize))*100;
				
				//computes SD and SEM for normalized areas
				for (Double qq: treatmentsNormalized){
					treatmentNormalizedSD += ((qq-avgTreatmentNormalized)*(qq-avgTreatmentNormalized));
					combinedNormalizedSD += ((qq-avgCombinedNormalized)*(qq-avgCombinedNormalized));
				}

				for (Double ww: nonTreatmentNormalized){
					nonTreatmentNormalizedSD += ((ww-avgNonTreatmentNormalized)*(ww-avgNonTreatmentNormalized));
					combinedNormalizedSD += ((ww-avgCombinedNormalized)*(ww-avgCombinedNormalized));
				}
				
				treatmentNormalizedSD = Math.sqrt(treatmentNormalizedSD/(treatmentsSize-1));
				nonTreatmentNormalizedSD = Math.sqrt(nonTreatmentNormalizedSD/(nonTreatmentSize-1));
				combinedNormalizedSD = Math.sqrt(combinedNormalizedSD/(combinedSize-1));
				
				//SEM
				Double treatmentNormalizedSEM = (treatmentNormalizedSD/(Math.sqrt(treatmentsSize)));
				Double nonTreatmentNormalizedSEM = (nonTreatmentNormalizedSD/(Math.sqrt(nonTreatmentSize)));
				Double combinedNormalizedSEM = (combinedNormalizedSD/(Math.sqrt(combinedSize)));
				
				
				//computes SD and SEM for actual areas
				for (Double qq: treatments){
					treatmentSD += ((qq-avgTreatment)*(qq-avgTreatment));
					combinedSD += ((qq-avgCombined)*(qq-avgCombined));
				}
				for (Double ww: nonTreatment){
					nonTreatmentSD += ((ww-avgNonTreatment)*(ww-avgNonTreatment));
					combinedSD += ((ww-avgCombined)*(ww-avgCombined));
				}
				treatmentSD = Math.sqrt(treatmentSD/(treatmentsSize-1));
				nonTreatmentSD = Math.sqrt(nonTreatmentSD/(nonTreatmentSize-1));
				combinedSD = Math.sqrt(combinedSD/(combinedSize-1));
				
				//SEM
				Double treatmentSEM = (treatmentSD/(Math.sqrt(treatmentsSize)));
				Double nonTreatmentSEM = (nonTreatmentSD/(Math.sqrt(nonTreatmentSize)));
				Double combinedSEM = (combinedSD/(Math.sqrt(combinedSize)));
				
				//ttest
				ttest = (avgTreatment - avgNonTreatment);
				ttest = ttest/(  Math.sqrt( ((treatmentSEM*treatmentSEM)/treatmentsSize) +  ((nonTreatmentSEM*nonTreatmentSEM)/nonTreatmentSize) ) );
				
				//Writes Files
				PrintWriter pw = new PrintWriter(new File(outputDirectory + "Summary.csv" + "\\"));
				StringBuilder sb = new StringBuilder();
				//relative areas
				sb.append(" " + ',' + "Optional Parameter (" + treatment + ")" + ',' + " " + ',' + "Combined" + ',');
				sb.append('\n');
				sb.append("% NETosis" + ',' + treatmentNETosis + ',' + nonTreatmentNETosis + ',' + combinedNETosis + ',');
				sb.append('\n');
				sb.append("Avg. Normalized Area" + ',' + avgTreatmentNormalized + ',' + avgNonTreatmentNormalized + ',' + avgCombinedNormalized + ',');
				sb.append('\n');
				sb.append("ANA SD" + ',' + treatmentNormalizedSD + ',' + nonTreatmentNormalizedSD + ',' + combinedNormalizedSD + ',');
				sb.append('\n');
				sb.append("ANA SEM" + ',' + treatmentNormalizedSEM + ',' + nonTreatmentNormalizedSEM + ',' + combinedNormalizedSEM + ',');
				sb.append('\n');
				sb.append("Total Cells" + ',' + treatmentsSize + ',' + nonTreatmentSize + ',' + combinedSize + ',');
				
				//actual areas
				sb.append('\n');
				sb.append('\n');
				sb.append("Avg. Area" + ',' + avgTreatment + ',' + avgNonTreatment + ',' + avgCombined + ',');
				sb.append('\n');
				sb.append("AA SD" + ',' + treatmentSD + ',' + nonTreatmentSD + ',' + combinedSD + ',');
				sb.append('\n');
				sb.append("AA SEM" + ',' + treatmentSEM + ',' + nonTreatmentSEM + ',' + combinedSEM + ',');
				
				//ttest
				sb.append('\n');
				sb.append('\n');
				sb.append("tscore:" + ',' + ttest + ',');
				
				pw.write(sb.toString());
				pw.close();
	}
}
