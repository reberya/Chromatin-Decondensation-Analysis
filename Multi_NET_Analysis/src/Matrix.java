import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Stores and modifies the .csv file for each image.
 * 
 * @author Ryan Rebernick
 *
 */
public class Matrix {

	String name;
	Double[][] currMatrix;
	String[] labels;
	int numCells;
	int x;
	int y;
	int cd1, cd2, cd3, cd4;
	int condensed;
	Double upperCutoff;
	Double lowerCutoff;
	Double oldRID;
	ArrayList<Integer> outlierPos;
	ArrayList<Integer> multiplePos;
	ArrayList<Integer> fragmentPos;
	
	ArrayList<Double> nonOutlierAreas;
	String [] NETs;
	String[] newLabels;
	Boolean isTreatment;


	/**
	 * 
	 * Opens the .csv file, stores data and labels separately. 
	 * 
	 * @param cFile - the .csv file
	 * @param fileName - the name of the .csv file
	 * @param isT	- boolean value documenting whether treatment (optional)
	 * @throws FileNotFoundException
	 */
	public Matrix(File cFile, String fileName, Boolean isT) throws FileNotFoundException {
		name = fileName;
		currMatrix = new Double[20][400];
		labels = new String[14];
		x = y = 0;
		outlierPos = null;
		isTreatment = isT;

		//stores csv file in double array matrix
		Scanner scanner = new Scanner(cFile);
		scanner.useDelimiter(",|\\n");

		try {
			//removes labels and store
			for (int k=0; k<11; k++){
				labels[k] = scanner.next().trim();
			} 

			//stores all non-label values in Double[][] "matrix"
			while (scanner.hasNextLine()){
				x=0;
				while (x<11) {
					currMatrix[x][y] = Double.parseDouble(scanner.next());
					x++;
				} 
				y++;
			}

			//catches empty last row of .csv file and allows algorithm to proceed.
		} catch(NoSuchElementException e){}

		//sets the number of data-containing cells for this file
		numCells = y;
		//closes scanner
		scanner.close();
	}


	/**
	 * Finds and marks the outliers in the current matrix
	 * @param upperValue - if above value, then outlier
	 * @param lowerValue - if below value then outlier
	 */
	public int findOutliers(Double lowerValue, Double upperValue) {
		upperCutoff = upperValue;
		lowerCutoff = lowerValue;

		//Marks cells outside of cutoff values (outliers)
		outlierPos = new ArrayList<Integer>();
		fragmentPos = new ArrayList<Integer>();
		multiplePos = new ArrayList<Integer>();
		
		nonOutlierAreas = new ArrayList<Double>();
		int pos = 0;
		for (Double q: currMatrix[7]) {
			// if end of list break
			if (q == null){
				break;
			}
			//if outlier, fill pos w/ null value (col11) and add to list of outlier pos.
			else if ( q>= upperValue){
				currMatrix[11][pos] = null;
				outlierPos.add(pos);
				multiplePos.add(pos);
				
			}
			else if (q <= lowerValue){
				currMatrix[11][pos] = null;
				fragmentPos.add(pos);
				outlierPos.add(pos);
			}
			//if not outlier fill in column 11
			else {
				currMatrix[11][pos] = q;
				nonOutlierAreas.add(currMatrix[1][pos]);
			}
			pos++;
		}
		return fragmentPos.size();
	}




	/**
	 * Returns the list of nonOutlierAreas 
	 * 
	 * @return nonOutliers
	 */
	public ArrayList<Double> getNonOutlierAreas() {
		return nonOutlierAreas;
	}


/**
 * Updates the matrix to include outliers, relative sizes, 
 * and new averages excluding outliers.
 * 
 * @param avg - the average RID
 * @param cutoff1 - DNA decondensation cutoff1
 * @param cutoff2 - DNA decondensation cutoff2
 * @param cutoff3 - DNA decondensation cutoff3
 * @param cutoff4 - DNA decondensation cutoff4
 * @param NETcutoff - the NET cutoff
 */
	public void update(Double avg, Double cutoff1, Double cutoff2, 
			Double cutoff3, Double cutoff4, Double NETcutoff) {

		NETs = new String[numCells];
		cd1 = cd2 = cd3 = cd4 = 0;
		condensed = 0;
		Double average,  relArea, 
		areaAvg, meanAvg, minAvg, maxAvg, circAvg, intDenAvg, newRawIntDenAvg,
		ARavg, roundAvg, solidityAvg, RelAreaAvg;
		
		average = relArea = areaAvg = meanAvg = minAvg = maxAvg = circAvg
		= intDenAvg = newRawIntDenAvg = ARavg = roundAvg = solidityAvg 
		= RelAreaAvg = 0.0;

		average = avg;

		//calculates averages excluding outliers
		int count = 0;
		int NETcount = 0;
		
		//calculates averages if not outlier
		for (int i=0; i<numCells; i++){
			if (!outlierPos.contains(i)){
				count++;

				areaAvg += currMatrix[1][i];
				meanAvg += currMatrix[2][i];
				minAvg += currMatrix[3][i];
				maxAvg += currMatrix[4][i];
				circAvg += currMatrix[5][i];
				intDenAvg += currMatrix[6][i];
				newRawIntDenAvg += currMatrix [7][i];
				ARavg += currMatrix[8][i];
				roundAvg += currMatrix[9][i];
				solidityAvg += currMatrix[10][i];

				//adds nonOutliers to column 11
				currMatrix[11][i] = currMatrix[7][i];
				//computes rounded relative area for each cell that's not an outlier
				relArea = (double)((currMatrix[1][i])/average);
				relArea = (double) Math.round(relArea*100);
				relArea = relArea/100;
				currMatrix[12][i] = relArea;
				
				//NET vs non NET
				if (currMatrix[12][i] >= NETcutoff) {
					NETs[i] = "NET (" + NETcutoff + "x)";
					NETcount++;
				}
				else if (currMatrix[12][i] < NETcutoff){
					NETs[i] = "x";
				}
						
				//Chromatin Decondensation
				if (currMatrix[12][i] >= cutoff4) {
					cd4++;
				} 
				if (currMatrix[12][i] >= cutoff3) {
					cd3++;
				} 
				if (currMatrix[12][i] >= cutoff2) {
					cd2++;
				} 
				if (currMatrix[12][i] >= cutoff1) {
					cd1++;
				}
				
				//computes relative average area
				RelAreaAvg = RelAreaAvg += currMatrix[12][i];

			}
			//else outlier values for relative ara and Nonoutlier are null
			else if (fragmentPos.contains(i)){
				currMatrix[11][i] = null;
				currMatrix[12][i] = null;
				NETs[i] = "Fr";
			}
			else if (multiplePos.contains(i)){
				currMatrix[11][i] = null;
				currMatrix[12][i] = null;
				NETs[i] = "Mt";
			}
			
			
		}

		//adds new column labels
		labels[11] = "NonOutliers";
		labels[12] = "RelArea";
		labels[13] = "Classification";

		//stores new average labels in array newLabels	
		newLabels = new String[31];
		newLabels[1] = "Area Average:";
		newLabels[2] = "Mean Average:";
		newLabels[3] = "Min Average:";
		newLabels[4] = "Max Average:";
		newLabels[5] = "Circularity Average:";
		newLabels[6] = "Integrated Density Average:";
		newLabels[7] = "New RID Average::";
		newLabels[8] = "AR Average:";
		newLabels[9] = "Round Average:";
		newLabels[10] = "Solidity Average:";
		newLabels[11] = "Relative Area Average:";
		newLabels[12] = "";	//Space
		newLabels[13] = "Lower cutoff:";
		newLabels[14] = "Fragments Excluded:";
		newLabels[15] = "Upper cutoff:";
		newLabels[16] = "Multiples Excluded:";
		newLabels[17] = "";	//Space
		newLabels[18] = "(" + cutoff1 + "x) %CD";
		newLabels[19] = "(" + cutoff2 + "x) %CD";
		newLabels[20] = "(" + cutoff3 + "x) %CD";
		newLabels[21] = "(" + cutoff4 + "x) %CD";
		newLabels[22] = "(" + cutoff1 + "x) #:";
		newLabels[23] = "(" + cutoff2 + "x) #:";
		newLabels[24] = "(" + cutoff3 + "x) #:";
		newLabels[25] = "(" + cutoff4 + "x) #:";
		newLabels[26] = "";	//Space
		newLabels[27] =  "% NETs " + "(" + NETcutoff + "x):";
		newLabels[28] = "# NETs (" + NETcutoff + "x):";
		newLabels[29] = "# Cells:";			//25 previously
		
		//adds new averages to matrix

		currMatrix[15][1] = areaAvg/count;
		currMatrix[15][2] =meanAvg/count;
		currMatrix[15][3] = minAvg/count;
		currMatrix[15][4] = maxAvg/count;
		currMatrix[15][5] =circAvg/count;
		currMatrix[15][6] =intDenAvg/count;
		currMatrix[15][7] = newRawIntDenAvg/count;
		currMatrix[15][8] = ARavg/count;
		currMatrix[15][9] = roundAvg/count;
		currMatrix[15][10] = solidityAvg/count;		
		currMatrix[15][11] = RelAreaAvg/count;	
		//space
		currMatrix[15][13] = lowerCutoff;
		currMatrix[15][14] = (double)fragmentPos.size();
		currMatrix[15][15] = upperCutoff;
		currMatrix[15][16] = (double)multiplePos.size();
		//space
		currMatrix[15][18] = (double) ((double)cd1*100/(count));	
		currMatrix[15][19] = (double) ((double)cd2*100/(count));
		currMatrix[15][20] = (double) ((double)cd3*100/(count));	
		currMatrix[15][21] = (double) ((double)cd4*100/(count));
		currMatrix[15][22] = (double) cd1;
		currMatrix[15][23] = (double) cd2;
		currMatrix[15][24] = (double) cd3;
		currMatrix[15][25] = (double) cd4;
		//space
		currMatrix[15][27] = (double) ((double) NETcount *100/(count));
		currMatrix[15][28] = (double) NETcount;
		currMatrix[15][29] = (double) count;
		
	}

	/**
	 * creates updated CSV file in location passed to it
	 * 
	 * @param outputDirectory - directory to output to
	 * @throws FileNotFoundException 
	 */
	public void createCSV(String outputDirectory) throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(new File(outputDirectory + name + "/"));
		StringBuilder sb = new StringBuilder();

		//add column labels
		for(int num=0; num<14; num++){
			sb.append(labels[num] + ",");
		}
		sb.append('\n');

		//for each row up to and including row 16 (all calculated)
		for (int row=0; row<29; row++){
			//for each column
			for(int col=0; col<16; col++){
				//adds outlier column
				if (col>14 && row <29){
					sb.append(newLabels[row+1] + ",");
					if (currMatrix[15][row+1] != null){
						sb.append(currMatrix[15][row+1] + ",");
					}
					else {
						sb.append(" ,");
					}
					

				} 
				//Collumn indicating whether NET
				else if (col == 13){
					try {
						sb.append(NETs[row] + ",");
					}catch (ArrayIndexOutOfBoundsException e) {
						sb.append(" " + ",");
					}
				//if no value b/c outlier, leave blank
				} else if (currMatrix[col][row] == null){
					sb.append("" + ",");
				} 
				//else append all other values
				else {
					sb.append(currMatrix[col][row] + ",");
				}
			}
			sb.append('\n');
		}

		//adds remaining rows of data
		if (numCells>13){
			for (int nRow=29; nRow<numCells; nRow++){
				for(int col=0; col<14; col++){
					if (col == 13) {
						sb.append(NETs[nRow] + ",");
					}
					else if (currMatrix[col][nRow] == null){
						sb.append("" + ",");
					}
					else {
						sb.append(currMatrix[col][nRow] + ",");
					}
				}
				sb.append('\n');
			}
		}

		pw.write(sb.toString());
		pw.close();

	}


	/**
	 * @return list of RID for all cells in matrix
	 */
	public Collection<? extends Double> getRID() {
		ArrayList<Double> RID = new ArrayList<Double>();

		for (Double d: currMatrix[7]){
			if (d !=null){
				RID.add(d);
			}
		}
		return RID;
	}


	/**
	 * 
	 * @return true if is treatment
	 */
	public boolean isTreatment() {
		return isTreatment;
	}

	
	/**
	 * Compiles normalized areas and exports ArrayList
	 * @return ArrayList of normalized areas
	 */
	public Collection<? extends Double> getNormalizedAreas() {
		ArrayList<Double> normAreas = new ArrayList<Double>();
		for (Double D: currMatrix[12]){
			if (D != null) {
				normAreas.add(D);
			}

		}
		return normAreas;
	}
}