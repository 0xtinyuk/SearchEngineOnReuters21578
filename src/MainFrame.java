import java.awt.Checkbox;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextField;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.FlowLayout;
import javax.swing.JRadioButton;

import javax.swing.JCheckBox;
import java.awt.GridLayout;

import javax.swing.JTextArea;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.awt.event.ActionEvent;

import javax.swing.*;

public class MainFrame {

	private JFrame frame;
	private JTextField textField_Query;
	static Main Do;
	private final ButtonGroup modelButtonGroup = new ButtonGroup();
	private final ButtonGroup sourceButtonGroup = new ButtonGroup();
	
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		Do = new Main();
		Do.preProcessing();
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame window = new MainFrame();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		//VectorSpaceModel vectorSpaceModel = new VectorSpaceModel("");
	}
	


	/**
	 * Create the application.
	 */
	public MainFrame() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel);
		
		JLabel lblle = new JLabel("Ging Search:");
		panel.add(lblle);
		
		textField_Query = new JTextField();
		panel.add(textField_Query);
		textField_Query.setColumns(20);
		
		
		
		JPanel panel_Configuration = new JPanel();
		frame.getContentPane().add(panel_Configuration);
		
		JPanel panel_Source = new JPanel();
		panel_Configuration.add(panel_Source);
		panel_Source.setLayout(new GridLayout(2, 1, 0, 0));
		
		JRadioButton rdbtn_UofO = new JRadioButton("UofO-CSI-Courses");
		panel_Source.add(rdbtn_UofO);
		sourceButtonGroup.add(rdbtn_UofO);
		rdbtn_UofO.setSelected(true);
		
		JRadioButton rdbtn_Reuters = new JRadioButton("Reuters");
		panel_Source.add(rdbtn_Reuters);
		sourceButtonGroup.add(rdbtn_Reuters);
		
		JPanel panel_Model = new JPanel();
		panel_Configuration.add(panel_Model);
		
		JRadioButton rdbtn_BooleanModel = new JRadioButton("Boolean Model");
		modelButtonGroup.add(rdbtn_BooleanModel);
		rdbtn_BooleanModel.setSelected(true);
		
		JRadioButton rdbtn_VSM = new JRadioButton("VectorSpaceModel");
		modelButtonGroup.add(rdbtn_VSM);
		panel_Model.setLayout(new GridLayout(2, 1, 0, 0));
		panel_Model.add(rdbtn_BooleanModel);
		panel_Model.add(rdbtn_VSM);
		
		JPanel panel_Options = new JPanel();
		panel_Configuration.add(panel_Options);
		panel_Options.setLayout(new GridLayout(0, 1, 0, 0));
		
		JCheckBox chckbx_stemming = new JCheckBox("Stemming");
		panel_Options.add(chckbx_stemming);
		
		JCheckBox chckbx_Stopword = new JCheckBox("Stopword");
		panel_Options.add(chckbx_Stopword);
		
		JCheckBox chckbx_normalization = new JCheckBox("normalization");
		panel_Options.add(chckbx_normalization);
		
		JPanel panel_Result = new JPanel();
		frame.getContentPane().add(panel_Result);
		
		JTextArea textArea_Result = new JTextArea();
		textArea_Result.setEditable(false);
		panel_Result.add(textArea_Result);
		textArea_Result.setColumns(32);
		textArea_Result.setRows(7);
		textArea_Result.setLineWrap(true);
		JScrollPane pane = new JScrollPane(textArea_Result,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		panel_Result.add(pane);
		
		JButton btnSearch = new JButton("Search");
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String query = textField_Query.getText();
				boolean reuters = rdbtn_Reuters.isSelected();
				boolean stemming = chckbx_stemming.isSelected();
				boolean normalization = chckbx_normalization.isSelected();
				boolean stopword = chckbx_Stopword.isSelected();
				boolean bm = rdbtn_BooleanModel.isSelected();
				String result = Do.search(query,stemming,stopword,normalization,bm,reuters);
				textArea_Result.setText(result);
			}
		});
		panel.add(btnSearch);
	}

}
