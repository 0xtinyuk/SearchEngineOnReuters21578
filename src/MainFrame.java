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
		frame.setBounds(100, 100, 800, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel);
		
		JLabel lblle = new JLabel("Ging Search:");
		panel.add(lblle);
		
		textField_Query = new JTextField();
		panel.add(textField_Query);
		textField_Query.setColumns(25);
		
		
		
		JPanel panel_Configuration = new JPanel();
		frame.getContentPane().add(panel_Configuration);
		
		JPanel panel_Source = new JPanel();
		panel_Configuration.add(panel_Source);
		panel_Source.setLayout(new GridLayout(2, 1, 0, 0));
		
		JPanel panel_FileAmount = new JPanel();
		panel_Configuration.add(panel_FileAmount);
		panel_FileAmount.setLayout(new GridLayout(3, 1, 0, 0));
		
		JLabel lblFileAmount = new JLabel("Reuters File Amount:");
		panel_FileAmount.add(lblFileAmount);
		
		JTextField textField_FileAmount = new JTextField();
		panel_FileAmount.add(textField_FileAmount);
		textField_FileAmount.setColumns(3);
		textField_FileAmount.setText(String.valueOf(Do.reutersFileAmount));
		
		JButton btnFileAmount = new JButton("Input Reuters");
		
		panel_FileAmount.add(btnFileAmount);
		
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
		JCheckBox chckbx_TopicAssign = new JCheckBox("TopicAssign");
		panel_Options.add(chckbx_TopicAssign);
		
		JPanel panel_topic_choosing = new JPanel();
		panel_topic_choosing.setLayout(new GridLayout(0, 1, 0, 0));
		panel_Configuration.add(panel_topic_choosing);
		JCheckBox chckbx_TopicRestriction = new JCheckBox("TopicRestriction");
		panel_topic_choosing.add(chckbx_TopicRestriction);
		JTextField textField_TopicRestrction = new JTextField();
		panel_topic_choosing.add(textField_TopicRestrction);
		textField_TopicRestrction.setColumns(3);
		
		JPanel panel_Result = new JPanel();
		frame.getContentPane().add(panel_Result);
		
		JTextArea textArea_Result = new JTextArea();
		textArea_Result.setEditable(false);
		panel_Result.add(textArea_Result);
		textArea_Result.setColumns(60);
		textArea_Result.setRows(12);
		textArea_Result.setLineWrap(true);
		JScrollPane pane = new JScrollPane(textArea_Result,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		panel_Result.add(pane);
		
		btnFileAmount.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int fileAmount = Integer.parseInt(textField_FileAmount.getText());
				boolean reuters = rdbtn_Reuters.isSelected();
				boolean stemming = chckbx_stemming.isSelected();
				boolean normalization = chckbx_normalization.isSelected();
				boolean stopword = chckbx_Stopword.isSelected();
				boolean topicAssign = chckbx_TopicAssign.isSelected();
				Do.inputReuters(fileAmount,stemming,stopword, normalization, reuters,topicAssign);
				JOptionPane.showMessageDialog(null, "Success","Load Files",JOptionPane.PLAIN_MESSAGE);
			}
		});
		
		JButton btnSearch = new JButton("Search");
		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String query = textField_Query.getText();
				boolean reuters = rdbtn_Reuters.isSelected();
				boolean stemming = chckbx_stemming.isSelected();
				boolean normalization = chckbx_normalization.isSelected();
				boolean stopword = chckbx_Stopword.isSelected();
				boolean bm = rdbtn_BooleanModel.isSelected();
				boolean topicAssign = chckbx_TopicAssign.isSelected();
				boolean topicRestrction = chckbx_TopicRestriction.isSelected();
				String topics = textField_TopicRestrction.getText();
				String result = Do.search(query,stemming,stopword,normalization,bm,reuters,topicAssign,topicRestrction,topics);
				textArea_Result.setText(result);
			}
		});
		panel.add(btnSearch);
		
		JButton btnCompletion = new JButton("Completion");
		btnCompletion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String query = textField_Query.getText();
				boolean reuters = rdbtn_Reuters.isSelected();
				boolean stemming = chckbx_stemming.isSelected();
				boolean normalization = chckbx_normalization.isSelected();
				boolean stopword = chckbx_Stopword.isSelected();
				boolean topicAssign = chckbx_TopicAssign.isSelected();
				String suggestion = Do.queryCompletion(query,stemming,stopword,normalization,reuters,topicAssign);
				JOptionPane.showMessageDialog(null, suggestion,"Query Completion",JOptionPane.PLAIN_MESSAGE);
			}
		});
		panel.add(btnCompletion);
		
		JButton btnExpension = new JButton("Expension");
		btnExpension.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String query = textField_Query.getText();
				boolean reuters = rdbtn_Reuters.isSelected();
				boolean stemming = chckbx_stemming.isSelected();
				boolean normalization = chckbx_normalization.isSelected();
				boolean stopword = chckbx_Stopword.isSelected();
				boolean topicAssign = chckbx_TopicAssign.isSelected();
				String expensionStr = Do.queryExpension(query,stemming,stopword,normalization,reuters,topicAssign);
				JOptionPane.showMessageDialog(null, expensionStr,"Query Expension",JOptionPane.PLAIN_MESSAGE);
			}
		});
		panel.add(btnExpension);
	}

}
