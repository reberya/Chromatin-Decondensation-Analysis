import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Window extends JFrame {

	public Window() {
		super("DNA Area and NETosis Analysis (DANA)");
		setSize(650,400);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		//CENTER - Panel A
		JPanel pA = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(2,2,5,5);
		
		//Inputdir
		JLabel inputdir = new JLabel("Input Directory: ");
		JTextField inDirTF = new JTextField("Insert Location Here", 40); 
		
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		pA.add(inputdir, gbc);
		gbc.gridx = 1;
		gbc.gridy = 0;
		pA.add(inDirTF, gbc);
		
		//Output dir
		JLabel outputDir = new JLabel("Output Directory: ");
		JTextField outDirTF = new JTextField("Insert Location Here", 40); 
		gbc.gridx = 0;
		gbc.gridy = 1;
		pA.add(outputDir, gbc);
		gbc.gridx = 1;
		gbc.gridy = 1;
		pA.add(outDirTF, gbc);
		
		//Upper Elimination Cutoff
		JLabel uCut = new JLabel("Upper Elimination Cutoff: ");
		JTextField uCutTF = new JTextField("1.4", 5); 
		gbc.gridx = 0;
		gbc.gridy = 2;
		pA.add(uCut, gbc);
		gbc.gridx = 1;
		gbc.gridy = 2;
		pA.add(uCutTF, gbc);

		//Lower Elimination Cutoff
		JLabel LCut = new JLabel("Lower Elimination Cutoff: ");
		JTextField LCutTF = new JTextField("1.3", 5); 
		gbc.gridx = 0;
		gbc.gridy = 3;
		pA.add(LCut, gbc);
		gbc.gridx = 1;
		gbc.gridy = 3;
		pA.add(LCutTF, gbc);
		
		add(pA, BorderLayout.NORTH);
		
		//DNA Decondensation Cutoffs
		JPanel pB = new JPanel(new GridBagLayout());
		GridBagConstraints gbc2 = new GridBagConstraints();
		gbc2.insets = new Insets(1,1,5,5);
		
		JLabel DNACut1 = new JLabel("DNA Decondensation Cutoff 1: ");
		JLabel DNACut2 = new JLabel("DNA Decondensation Cutoff 2: ");
		JLabel DNACut3 = new JLabel("DNA Decondensation Cutoff 3: ");
		JLabel DNACut4 = new JLabel("DNA Decondensation Cutoff 4: ");
		
		JTextField DNACutTF1 = new JTextField("3.00", 5); 
		JTextField DNACutTF2 = new JTextField("4.00", 5); 
		JTextField DNACutTF3 = new JTextField("5.00", 5); 
		JTextField DNACutTF4 = new JTextField("6.00", 5); 
		
		gbc2.gridx = 0;
		gbc2.gridy = 0;
		pB.add(DNACut1, gbc2);
		gbc2.gridx = 1;
		gbc2.gridy = 0;
		pB.add(DNACutTF1, gbc2);
		
		gbc2.gridx = 0;
		gbc2.gridy = 1;
		pB.add(DNACut2, gbc2);
		gbc2.gridx = 1;
		gbc2.gridy = 1;
		pB.add(DNACutTF2, gbc2);
		
		gbc2.gridx = 0;
		gbc2.gridy = 2;
		pB.add(DNACut3, gbc2);
		gbc2.gridx = 1;
		gbc2.gridy = 2;
		pB.add(DNACutTF3, gbc2);
		
		gbc2.gridx = 0;
		gbc2.gridy = 3;
		pB.add(DNACut4, gbc2);
		gbc2.gridx = 1;
		gbc2.gridy = 3;
		pB.add(DNACutTF4, gbc2);
		
		//NET Cutoff
		JLabel NETcut = new JLabel("NET Cutoff:");
		JTextField NETcutTF = new JTextField("4.72", 5); 
		gbc2.gridx = 0;
		gbc2.gridy = 4;
		pB.add(NETcut, gbc2);
		gbc2.gridx = 1;
		gbc2.gridy = 4;
		pB.add(NETcutTF, gbc2);
		
		add(pB, BorderLayout.CENTER);

		//DNA Decondensation Cutoffs
		JPanel pC = new JPanel(new GridBagLayout());
		GridBagConstraints gbc3 = new GridBagConstraints();
		gbc3.insets = new Insets(1,1,5,5);
		
		//SAVE SETTINGS
		JButton save = new JButton("Save Current Settings");
		//Save button action
		save.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//TODO: SAVE SETTINGS FUNCTIONALITY
				JOptionPane.showMessageDialog(null, "WORKING ON IT");
			}
		});
		gbc3.gridx = 0;
		gbc3.gridy = 0;
		pC.add(save, gbc3);
		
		//Default settings
		JButton defSet = new JButton("Default Settings");
		gbc3.gridx = 1;
		gbc3.gridy = 0;
		pC.add(defSet, gbc3);
		
		//RUN DANA
		JButton run = new JButton("Run DANA");
		run.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO SETUP 
				 
			}
		});
		
		gbc3.gridx = 2;
		gbc3.gridy = 0;
		pC.add(run, gbc3);
		
		add(pC, BorderLayout.SOUTH);

		
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
