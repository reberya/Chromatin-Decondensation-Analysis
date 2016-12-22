import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Matrix {

	String name;
	Double[][] currMatrix;
	String[] labels;
	int numCells;
	int x;
	int y;
	int NET;
	int condensed;
	Double upperCutoff;
	Double lowerCutoff;
	Double oldRID;
	ArrayList<Integer> outlierPos;
	ArrayList<Double> nonOutlierAreas;
	String [] NETs;
	String[] newLabels;
	Boolean isTreatment;


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
				labels[k] = scanner.next();
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
	 * @param upperValue 
	 * @param lowerValue 
	 */
	public void findOutliers(Double lowerValue, Double upperValue) {

		upperCutoff = upperValue;
		lowerCutoff = lowerValue;

		//Marks cells outside of cutoff values (outliers)
		outlierPos = new ArrayList<Integer>();
		nonOutlierAreas = new ArrayList<Double>();
		int pos = 0;
		for (Double q: currMatrix[7]) {
			// if end of list break
			if (q == null){
				break;
			}
			//if outlier, fill pos w/ null value (col11) and add to list of outlier pos.
			else if (q>= upperValue || q<=lowerValue){
				currMatrix[11][pos] = null;
				outlierPos.add(pos);
			}
			//if not outlier fill in column 11
			else {
				currMatrix[11][pos] = q;
				nonOutlierAreas.add(currMatrix[1][pos]);
			}
			pos++;
		}
	}




	/**
	 * Returns the list of nonOutlierAreas 
	 * 
	 * @return nonOutliers
	 */
	public ArrayList<Double> getNonOutliers() {
		return nonOutlierAreas;
	}


	/**
	 * Updates the matrix to include outliers, relative sizes, 
	 * and new avergaes excluding outliers
	 * @param NETcutoff 
	 * 
	 * @param average - the average of the 5 smallest cells
	 * @return the updated matrix 
	 */
	public void update(Double avg, Double NETcutoff) {

		NETs = new String[numCells];
		NET = 0;
		condensed = 0;
		Double sum, average, variance, SD, uCutoff, lCutoff;
		sum = average = variance = SD = uCutoff = lCutoff = 0.0;
		Double areaAvg, meanAvg, minAvg, maxAvg, circAvg, intDenAvg, newRawIntDenAvg,
		ARavg, roundAvg, solidityAvg, RelAreaAvg;

		//initializes all values to be computed
		areaAvg = meanAvg = minAvg = maxAvg = circAvg = intDenAvg = newRawIntDenAvg =
				ARavg = roundAvg = solidityAvg = RelAreaAvg = 0.0;
		average = avg;

		//calculates averages excluding outliers
		int count = 0;

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
				//computes relative area for each cell that's not an outlier
				currMatrix[12][i] = (double)((currMatrix[1][i])/average);
				//identifies if NET based on user defined cutoff
				if (currMatrix[12][i] > NETcutoff) {
					NETs[i] = "NET";
					NET++;
				} else { 
					NETs[i] = "x"; 
				}

				//computes relative average area
				RelAreaAvg = RelAreaAvg += currMatrix[12][i];


			}
			//else outlier values for relative ara and Nonoutlier are null
			else{
				currMatrix[11][i] = null;
				currMatrix[12][i] = null;
				NETs[i] = "";
			}
		}

		//adds new column labels
		labels[11] = "NonOutliers";
		labels[12] = "RelArea";
		labels[13] = "NET?";

		//stores new average labels in array newLabels
		newLabels = new String[17];
		newLabels[1] = "Old RID Average:";
		newLabels[2] = "Upper cutoff:";
		newLabels[3] = "Lower cutoff:";
		newLabels[4] = "Area Average:";
		newLabels[5] = "Mean Average:";
		newLabels[6] = "Min Average:";
		newLabels[7] = "Max Average:";
		newLabels[8] = "Circularity Average:";
		newLabels[9] = "Integrated Density Average:";
		newLabels[10] = "New RID Average::";
		newLabels[11] = "AR Average:";
		newLabels[12] = "Round Average:";
		newLabels[13] = "Solidity Average:";
		newLabels[14] = "Relative Area Average:";

		newLabels[15] = "NET cutoff:";
		newLabels[16] = "%NETosis";

		//adds new averages to matrix
		currMatrix[15][1] = oldRID;
		currMatrix[15][2] = upperCutoff;
		currMatrix[15][3] = lowerCutoff;
		currMatrix[15][4] = areaAvg/count;
		currMatrix[15][5] =meanAvg/count;
		currMatrix[15][6] = minAvg/count;
		currMatrix[15][7] = maxAvg/count;
		currMatrix[15][8] =circAvg/count;
		currMatrix[15][9] =intDenAvg/count;
		currMatrix[15][10] = newRawIntDenAvg/count;
		currMatrix[15][11] = ARavg/count;
		currMatrix[15][12] = roundAvg/count;
		currMatrix[15][13] = solidityAvg/count;		
		currMatrix[15][14] = RelAreaAvg/count;	
		currMatrix[15][15] = NETcutoff;	
		currMatrix[15][16] = (double) ((double)NET*100/(count));
	}

	/**
	 * creates updated CSV file in location passed to it
	 * @param outputDirectory
	 * @throws FileNotFoundException 
	 */
	public void createCSV(String outputDirectory) throws FileNotFoundException {
		PrintWriter pw = new PrintWriter(new File(outputDirectory + name + "/"));
		StringBuilder sb = new StringBuilder();

		//add labels
		for(int num=0; num<11; num++){
			sb.append(labels[num] + ',');
		}
		sb.append('\n');

		//for each row up to and including row 13 (all calculated)
		for (int row=0; row<16; row++){
			//for each column
			for(int col=0; col<16; col++){
				//adds outlier column
				if (col>14 && row <16){
					sb.append(newLabels[row+1] + ",");
					sb.append(currMatrix[15][row+1] + ",");
				} else if (col == 13){
					try {
						sb.append(NETs[row] + ",");
					}catch (ArrayIndexOutOfBoundsException e) {
						sb.append(" " + ",");
					}
				} else if (currMatrix[col][row] == null){
					sb.append("" + ",");
				} else {
					sb.append(currMatrix[col][row] + ",");
				}
			}
			sb.append('\n');
		}

		//adds remaining rows of data
		if (numCells>13){
			for (int nRow=16; nRow<numCells; nRow++){
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
	 * returns list of RID for all cells in matrix
	 * @return
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
	 * @return true if is IO
	 */
	public boolean isTreatment() {
		return isTreatment;
	}

	/**
	 * Compiles normalized areas and exports ArrayList
	 * @return
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