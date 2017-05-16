import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;


/**
 * Graphic user interface for DANA. 
 * 
 * @author Ryan Rebernick
 *
 */
public class Window extends JFrame {

	//user determined parameters
	static String CDcutoff1, CDcutoff2, CDcutoff3, CDcutoff4;	//cutoff level to determine whether NET
	static String outputDirectory, inputDirectory;		//directory files will be saved to
	static String upperCutoff;			//upper cutoff SD
	static String lowerCutoff;			//lower cutoff SD
	static String absLCutoff;			//lower absolute cutoff (um^2)
	static String NETcutoff;			//cutoff for NETs
	static String minimumRID;			//lowest RID value for fragment elimination
	static String treatment;			//treatment (user defined)
	static String settingsName;			//Name of settings

	
	
	
	public Window() {

		super("DNA Area and NETosis Analysis (DANA)");	
		setSize(650,400);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		//Icon
		try {
			setIconImage(ImageIO.read(new File("Icon/DANA_logo.png")));
		} catch (IOException e3) {}

		inputDirectory = "C:\\Users\\reberya\\Desktop\\DANA_II_output\\";
		outputDirectory = "C:\\Users\\reberya\\Desktop\\DANA_II_output\\";
		upperCutoff = "1.4";
		lowerCutoff = "1.3";
		absLCutoff = "90";
		CDcutoff1 = "3.0";
		CDcutoff2 = "4.0";
		CDcutoff3 = "5.0";
		CDcutoff4 = "6.0";
		NETcutoff = "4.72";
		minimumRID = "20000";
		settingsName = "Default";

		//CENTER - Panel A
		JPanel pA = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(2,2,5,5);

		//Inputdir
		JLabel inputdir = new JLabel("Input Directory: ");
		JTextField inDirTF = new JTextField(inputDirectory, 40); 

		gbc.gridx = 0;
		gbc.gridy = 0;
		pA.add(inputdir, gbc);
		gbc.gridx = 1;
		gbc.gridy = 0;
		pA.add(inDirTF, gbc);

		//Output dir
		JLabel outputDir = new JLabel("Output Directory: ");
		JTextField outDirTF = new JTextField(outputDirectory, 40); 
		gbc.gridx = 0;
		gbc.gridy = 1;
		pA.add(outputDir, gbc);
		gbc.gridx = 1;
		gbc.gridy = 1;
		pA.add(outDirTF, gbc);

		add(pA, BorderLayout.NORTH);

		//DNA Decondensation Cutoffs
		JPanel pB = new JPanel(new GridBagLayout());
		GridBagConstraints gbc2 = new GridBagConstraints();
		gbc2.insets = new Insets(1,1,5,5);

		//Upper Elimination Cutoff
		JLabel uCut = new JLabel("Upper Elimination Cutoff Parameter: ");
		JTextField uCutTF = new JTextField(upperCutoff, 5); 
		gbc2.gridx = 0;
		gbc2.gridy = 0;
		pB.add(uCut, gbc2);
		gbc2.gridx = 1;
		gbc2.gridy = 0;
		pB.add(uCutTF, gbc2);

		//Lower cutoff value (minRID)
		JLabel min = new JLabel("Lower Cutoff Value: ");
		JTextField minTF = new JTextField(minimumRID, 5); 
		gbc2.gridx = 0;
		gbc2.gridy = 1;
		pB.add(min, gbc2);
		gbc2.gridx = 1;
		gbc2.gridy = 1;
		pB.add(minTF, gbc2);

		//Relative area
		JLabel relArea = new JLabel("Relative area normalized to: ");
		JCheckBox Rbox = new JCheckBox();
		JLabel rel5 = new JLabel("Mean area of 5 smallest non-outlier ROIs");
		JCheckBox Abox = new JCheckBox();
		JLabel absLabel = new JLabel("Area (um^2):");
		JTextField areaTF = new JTextField("90", 5);

		gbc2.gridx = 0;
		gbc2.gridy = 2;
		pB.add(relArea, gbc2);

		gbc2.insets.set(0, 0, 0, 0);
		gbc2.gridx = 1;
		gbc2.gridy = 2;
		pB.add(Rbox, gbc2);
		gbc2.gridx = 2;
		gbc2.gridy = 2;
		pB.add(rel5, gbc2);

		gbc2.gridx = 1;
		gbc2.gridy = 3;
		pB.add(Abox, gbc2);
		gbc2.gridx = 2;
		gbc2.gridy = 3;
		pB.add(absLabel, gbc2);
		gbc2.gridx = 3;
		gbc2.gridy = 3;
		pB.add(areaTF, gbc2);

		//DNA decondensation cutoffs
		JLabel DNACut1 = new JLabel("DNA Decondensation Cutoff 1: ");
		JLabel DNACut2 = new JLabel("DNA Decondensation Cutoff 2: ");
		JLabel DNACut3 = new JLabel("DNA Decondensation Cutoff 3: ");
		JLabel DNACut4 = new JLabel("DNA Decondensation Cutoff 4: ");
		JTextField DNACutTF1 = new JTextField(CDcutoff1, 5); 
		JTextField DNACutTF2 = new JTextField(CDcutoff2, 5); 
		JTextField DNACutTF3 = new JTextField(CDcutoff3, 5); 
		JTextField DNACutTF4 = new JTextField(CDcutoff4, 5); 

		gbc2.gridx = 0;
		gbc2.gridy = 5;
		pB.add(DNACut1, gbc2);
		gbc2.gridx = 1;
		gbc2.gridy = 5;
		pB.add(DNACutTF1, gbc2);

		gbc2.gridx = 0;
		gbc2.gridy = 6;
		pB.add(DNACut2, gbc2);
		gbc2.gridx = 1;
		gbc2.gridy = 6;
		pB.add(DNACutTF2, gbc2);

		gbc2.gridx = 0;
		gbc2.gridy = 7;
		pB.add(DNACut3, gbc2);
		gbc2.gridx = 1;
		gbc2.gridy = 7;
		pB.add(DNACutTF3, gbc2);

		gbc2.gridx = 0;
		gbc2.gridy = 8;
		pB.add(DNACut4, gbc2);
		gbc2.gridx = 1;
		gbc2.gridy = 8;
		pB.add(DNACutTF4, gbc2);

		//NET Cutoff
		JLabel NETcut = new JLabel("NET Cutoff:");
		JTextField NETcutTF = new JTextField("4.72", 5); 
		gbc2.gridx = 0;
		gbc2.gridy = 9;
		pB.add(NETcut, gbc2);
		gbc2.gridx = 1;
		gbc2.gridy = 9;
		pB.add(NETcutTF, gbc2);

		//Optional Parameter 
		JLabel oParam = new JLabel("Optional Parameter:");
		JTextField oParamTF = new JTextField("", 5); 
		gbc2.gridx = 0;
		gbc2.gridy = 10;
		pB.add(oParam, gbc2);
		gbc2.gridx = 1;
		gbc2.gridy = 10;
		pB.add(oParamTF, gbc2);
		add(pB, BorderLayout.CENTER);

		//Settings name, Save, load, run buttons
		JPanel pC = new JPanel(new GridBagLayout());
		GridBagConstraints gbc3 = new GridBagConstraints();
		gbc3.insets = new Insets(1,1,5,5);

		//NAME of SETTINGS
		JLabel nameField = new JLabel("Current Settings:");
		gbc3.gridx = 1;
		gbc3.gridy = 0;
		pC.add(nameField, gbc3);
		JTextArea name = new JTextArea(settingsName);
		gbc3.gridx = 2;
		gbc3.gridy = 0;
		pC.add(name, gbc3);

		//SAVE SETTINGS
		JButton save = new JButton("Save Current Settings");
		//Save button action
		save.addActionListener(new ActionListener() {
			@Override
			//Opens up new pane prompting user for filename of saved settings
			//saves settings as new file
			public void actionPerformed(ActionEvent e) {

				JFileChooser saveFile = new JFileChooser();

				int rVal = saveFile.showSaveDialog(null);
				if (rVal == JFileChooser.APPROVE_OPTION){
					
					try {
						String temp = saveFile.getSelectedFile().getAbsolutePath();
						String fName = saveFile.getSelectedFile().getName();
						if (!temp.contains(".txt")){
							name.setText(fName);
							temp = temp + ".txt";
						}
						else{
							name.setText(temp.substring(0, fName.lastIndexOf('.')));
						}

						File newFile = new File(temp);
						PrintWriter writer = new PrintWriter(newFile, "UTF-8");
						writer.println(inDirTF.getText());	//1
						writer.println(outDirTF.getText());	//2
						writer.println(uCutTF.getText());	//3
						writer.println(minTF.getText());	//4
						writer.println(Rbox.isSelected());	//5
						writer.println(Abox.isSelected());	//6
						writer.println(areaTF.getText());	//7
						writer.println(DNACutTF1.getText());//8
						writer.println(DNACutTF2.getText());//9
						writer.println(DNACutTF3.getText());//10
						writer.println(DNACutTF4.getText());//11
						writer.println(NETcutTF.getText());	//12
						writer.println(oParamTF.getText());	//13
						writer.close();
						JOptionPane.showMessageDialog(null, "File has been saved","File Saved",JOptionPane.INFORMATION_MESSAGE);
						// true for rewrite, false for override

					} catch (IOException e2) {
						e2.printStackTrace();
					}
				}
				else if(rVal == JFileChooser.CANCEL_OPTION){
					JOptionPane.showMessageDialog(null, "File save has been canceled");
				}

			}
		});

		gbc3.gridx = 1;
		gbc3.gridy = 1;
		pC.add(save, gbc3);

		//Load settings
		JButton defSet = new JButton("Load Settings");
		defSet.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				final JFileChooser fc = new JFileChooser();
				int returnVal = fc.showOpenDialog(null);

				String fileName = null;
				if(returnVal == JFileChooser.APPROVE_OPTION){
					//Name current settings field
					try {
						BufferedReader in = new BufferedReader(new FileReader(fc.getSelectedFile()));
						fileName = fc.getSelectedFile().getName();
						fileName = fileName.substring(0,fileName.lastIndexOf('.'));
						name.setText(fileName);
						String element = null;
						for (int j=0; j<14; j++){
							element = in.readLine();
							if (j == 0){
								inDirTF.setText(element);
							}
							else if (j == 1){
								outDirTF.setText(element);
							}
							else if (j == 2){
								uCutTF.setText(element);
							}
							else if (j == 3){
								minTF.setText(element);
							}
							else if (j == 4){
								if(element.equals("true")){
									Rbox.setSelected(true);
								} else { Rbox.setSelected(false);}
							}
							else if (j == 5){
								if(element.equals("true")){
									Abox.setSelected(true);
								} else { Abox.setSelected(false);}
							}
							else if (j == 6){
								areaTF.setText(element);
							}
							else if (j == 7){
								DNACutTF1.setText(element);
							}
							else if (j == 8){
								DNACutTF2.setText(element);
							}
							else if (j == 9){
								DNACutTF3.setText(element);
							}
							else if (j == 10){
								DNACutTF4.setText(element);
							}
							else if (j == 11){
								NETcutTF.setText(element);
							}
							else if (j == 12){
								oParamTF.setText(element);
							}
						}
						in.close();

					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}

			}
		});

		gbc3.gridx = 2;
		gbc3.gridy = 1;
		pC.add(defSet, gbc3);


		//RUN DANA
		JButton run = new JButton("Run DANA");
		run.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean tripped = false;

				boolean useRelative = false;
				boolean useAbsolute = false;

				Double UCut, LCut, area, DNACut1, DNACut2,DNACut3,DNACut4, NETcut;
				UCut = LCut = area = DNACut1 = DNACut2 = DNACut3 = DNACut4 = NETcut = null;

				//Get directory
				String indir = inDirTF.getText();
				if (indir.charAt(indir.length()-1) != '\\') {
					indir = indir + "\\";
				}
				String outdir = outDirTF.getText();
				if (outdir.charAt(outdir.length()-1) != '\\') {
					outdir = outdir + "\\";
				}
		
				//Get params
				//Upper cutoff
				try {
					UCut = Double.parseDouble(uCutTF.getText());
				} catch(NumberFormatException n1){
					JOptionPane.showMessageDialog(null, "There was a problem encountered with the Upper "
							+ "Elimination Cutoff Parameter");
					tripped = true;
				}
				//Relative Lower cutoff
				try {
					LCut = Double.parseDouble(minTF.getText());
				} catch(NumberFormatException n1){
					JOptionPane.showMessageDialog(null, "There was a problem encountered with the Lower "
							+ "Cutoff Value");
					tripped = true;
				}

				useRelative = Rbox.isSelected();
				useAbsolute = Abox.isSelected();

				if (useRelative == true && useAbsolute ==true){
					JOptionPane.showMessageDialog(null, "You may only select the mean area of the 5 smallest"
							+ " non-outlier ROIs or the predetermined area to normalize to.");
					tripped = true;
				}
				if (useRelative == false && useAbsolute == false){
					JOptionPane.showMessageDialog(null, "You must select either the mean area of the 5 smallest "
							+ "non-outlier ROIs or the predetermined area to normalize to.");
					tripped = true;
				}

				//Absolute area
				if (useAbsolute == true && useRelative == false){
					try {	
						area = Double.parseDouble(areaTF.getText());
					} catch(NumberFormatException n1){
						JOptionPane.showMessageDialog(null, "There was a problem encountered with the "
								+ "Area (um^2) parameter");
						tripped = true;
					}
				}
				try {	
					DNACut1 = Double.parseDouble(DNACutTF1.getText());
				} catch(NumberFormatException n1){
					JOptionPane.showMessageDialog(null, "There was a problem encountered with DNA "
							+ "Decondensation Cutoff 1");
					tripped = true;
				}
				try {
					DNACut2 = Double.parseDouble(DNACutTF2.getText());
				} catch(NumberFormatException n1){
					JOptionPane.showMessageDialog(null, "There was a problem encountered with DNA "
							+ "Decondensation Cutoff 2");
					tripped = true;
				}
				try {
					DNACut3 = Double.parseDouble(DNACutTF3.getText());
				} catch(NumberFormatException n1){
					JOptionPane.showMessageDialog(null, "There was a problem encountered with DNA "
							+ "Decondensation Cutoff 3");
					tripped = true;
				}
				try {
					DNACut4 = Double.parseDouble(DNACutTF4.getText());
				} catch(NumberFormatException n1){
					JOptionPane.showMessageDialog(null, "There was a problem encountered with DNA "
							+ "Decondensation Cutoff 4");
					tripped = true;
				}
				//NET cutoff
				try {
					NETcut = Double.parseDouble(NETcutTF.getText());
				} catch(NumberFormatException n1){
					JOptionPane.showMessageDialog(null, "There was a problem encountered with the NET "
							+ "Cutoff");
					tripped = true;
				}
				String oParameter = oParamTF.getText();
				if (tripped == false){
					try {
						Multi_NET_Analysis m = new Multi_NET_Analysis(outdir, indir, UCut, LCut, DNACut1, DNACut2,
								DNACut3, DNACut4, NETcut, oParameter, useRelative, area);

						//error messages for file not found warnings
					} catch (FileNotFoundException e1) {
						if (e1.getMessage().equals("oE1")){
							JOptionPane.showMessageDialog(null, "Output directory: " + outdir + " could not be found.");
						}

					}catch (NullPointerException e2){
						JOptionPane.showMessageDialog(null, "There was a problem encountered with the input directory: "
								+ indir);
					}catch(NumberFormatException n1){
						JOptionPane.showMessageDialog(null, "There was a problem encountered with: " + n1.getMessage());
					}
				}

			}
		});

		gbc3.gridx = 4;
		gbc3.gridy = 1;
		pC.add(run, gbc3);
		add(pC, BorderLayout.SOUTH);
	}
	private static final long serialVersionUID = 1L;
}
