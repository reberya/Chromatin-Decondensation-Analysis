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

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Window extends JFrame {

	//user determined parameters
	static String CDcutoff1, CDcutoff2, CDcutoff3, CDcutoff4;	//cutoff level to determine whether NET
	static String outputDirectory, inputDirectory;		//directory files will be saved to
	static String upperCutoff;			//upper cutoff SD
	static String lowerCutoff;			//lower cutoff SD
	static String NETcutoff;
	static String treatment;
	static String settingsName;



	public Window() {
		super("DNA Area and NETosis Analysis (DANA)");
		setSize(650,400);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		inputDirectory = "G:\\Team\\Shelef Lab\\NETosis Analysis Program\\FIJI_output\\";
		outputDirectory = "G:\\Team\\Shelef Lab\\NETosis Analysis Program\\JAVA_output\\";
		upperCutoff = "1.4";
		lowerCutoff = "1.3";
		CDcutoff1 = "3.0";
		CDcutoff2 = "4.0";
		CDcutoff3 = "5.0";
		CDcutoff4 = "6.0";
		NETcutoff = "4.72";
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
		JLabel uCut = new JLabel("Upper Elimination Cutoff: ");
		JTextField uCutTF = new JTextField(upperCutoff, 5); 
		gbc2.gridx = 0;
		gbc2.gridy = 0;
		pB.add(uCut, gbc2);
		gbc2.gridx = 1;
		gbc2.gridy = 0;
		pB.add(uCutTF, gbc2);

		//Lower Elimination Cutoff
		JLabel LCut = new JLabel("Lower Elimination Cutoff: ");
		JTextField LCutTF = new JTextField(lowerCutoff, 5); 
		gbc2.gridx = 0;
		gbc2.gridy = 1;
		pB.add(LCut, gbc2);
		gbc2.gridx = 1;
		gbc2.gridy = 1;
		pB.add(LCutTF, gbc2);

		JLabel DNACut1 = new JLabel("DNA Decondensation Cutoff 1: ");
		JLabel DNACut2 = new JLabel("DNA Decondensation Cutoff 2: ");
		JLabel DNACut3 = new JLabel("DNA Decondensation Cutoff 3: ");
		JLabel DNACut4 = new JLabel("DNA Decondensation Cutoff 4: ");

		JTextField DNACutTF1 = new JTextField(CDcutoff1, 5); 
		JTextField DNACutTF2 = new JTextField(CDcutoff2, 5); 
		JTextField DNACutTF3 = new JTextField(CDcutoff3, 5); 
		JTextField DNACutTF4 = new JTextField(CDcutoff4, 5); 

		gbc2.gridx = 0;
		gbc2.gridy = 2;
		pB.add(DNACut1, gbc2);
		gbc2.gridx = 1;
		gbc2.gridy = 2;
		pB.add(DNACutTF1, gbc2);

		gbc2.gridx = 0;
		gbc2.gridy = 3;
		pB.add(DNACut2, gbc2);
		gbc2.gridx = 1;
		gbc2.gridy = 3;
		pB.add(DNACutTF2, gbc2);

		gbc2.gridx = 0;
		gbc2.gridy = 4;
		pB.add(DNACut3, gbc2);
		gbc2.gridx = 1;
		gbc2.gridy = 4;
		pB.add(DNACutTF3, gbc2);

		gbc2.gridx = 0;
		gbc2.gridy = 5;
		pB.add(DNACut4, gbc2);
		gbc2.gridx = 1;
		gbc2.gridy = 5;
		pB.add(DNACutTF4, gbc2);

		//NET Cutoff
		JLabel NETcut = new JLabel("NET Cutoff:");
		JTextField NETcutTF = new JTextField("4.72", 5); 
		gbc2.gridx = 0;
		gbc2.gridy = 6;
		pB.add(NETcut, gbc2);
		gbc2.gridx = 1;
		gbc2.gridy = 6;
		pB.add(NETcutTF, gbc2);

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
						if (!temp.contains(".txt")){
							temp = temp + ".txt";
						}

						File newFile = new File(temp);
						PrintWriter writer = new PrintWriter(newFile, "UTF-8");
						writer.println(inDirTF.getText());
						writer.println(outDirTF.getText());
						writer.println(uCutTF.getText());
						writer.println(LCutTF.getText());
						writer.println(DNACutTF1.getText());
						writer.println(DNACutTF2.getText());
						writer.println(DNACutTF3.getText());
						writer.println(DNACutTF4.getText());
						writer.println(NETcutTF.getText());
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

				if(returnVal == JFileChooser.APPROVE_OPTION){
					//TODO Figure out why Directories aren't named appropriately
					//Name current settings field
					try {
						BufferedReader in = new BufferedReader(new FileReader(fc.getSelectedFile()));
						String element = null;
						for (int j=0; j<9; j++){
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
								LCutTF.setText(element);
							}
							else if (j == 4){
								DNACutTF1.setText(element);
							}
							else if (j == 5){
								DNACutTF1.setText(element);
							}
							else if (j == 4){
								DNACutTF2.setText(element);
							}
							else if (j == 6){
								DNACutTF3.setText(element);
							}
							else if (j == 7){
								DNACutTF4.setText(element);
							}
							else if (j == 8){
								NETcutTF.setText(element);
							}
						}


					} catch (IOException e1) {
						e1.printStackTrace();

					}
				}

				inDirTF.setText("Test");
				outDirTF.setText("Test");
				uCutTF.setText("Test");

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
				//Get directory
				String indir = inDirTF.getText();
				String outdir = outDirTF.getText();
				Double UCut = Double.parseDouble(uCutTF.getText());
				Double LCut = Double.parseDouble(LCutTF.getText());
				Double DNAcut1 = Double.parseDouble(DNACutTF1.getText());
				Double DNAcut2 = Double.parseDouble(DNACutTF2.getText());
				Double DNAcut3 = Double.parseDouble(DNACutTF3.getText());
				Double DNAcut4 = Double.parseDouble(DNACutTF4.getText());
				Double NETcut = Double.parseDouble(NETcutTF.getText());

				Multi_NET_Analysis m;
				try {
					m = new Multi_NET_Analysis(outdir, indir, UCut, LCut, DNAcut1, DNAcut2,
							DNAcut3, DNAcut4, NETcut);
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}


			}
		});

		gbc3.gridx = 4;
		gbc3.gridy = 1;
		pC.add(run, gbc3);

		add(pC, BorderLayout.SOUTH);


	}







	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
