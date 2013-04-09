package no.domeneparser;

import javax.swing.JDialog;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;
import java.awt.Insets;
import javax.swing.JSpinner;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.SpinnerNumberModel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Dialog.ModalityType;

public class InputDialog extends JDialog {
	private JTextField textField;
	private JSpinner spinner;
	private boolean canceled = false;
	
	public InputDialog() {
		this.setLocationRelativeTo(null);
		setModalityType(ModalityType.APPLICATION_MODAL);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);
		
		JLabel lblDomene = new JLabel("Domene");
		GridBagConstraints gbc_lblDomene = new GridBagConstraints();
		gbc_lblDomene.insets = new Insets(0, 0, 5, 5);
		gbc_lblDomene.anchor = GridBagConstraints.EAST;
		gbc_lblDomene.gridx = 0;
		gbc_lblDomene.gridy = 0;
		getContentPane().add(lblDomene, gbc_lblDomene);
		
		textField = new JTextField(30);
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.weightx = 1.0;
		gbc_textField.insets = new Insets(0, 0, 5, 0);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 1;
		gbc_textField.gridy = 0;
		getContentPane().add(textField, gbc_textField);
		textField.setColumns(10);
		
		JLabel lblSleepTime = new JLabel("Intervall");
		GridBagConstraints gbc_lblSleepTime = new GridBagConstraints();
		gbc_lblSleepTime.anchor = GridBagConstraints.EAST;
		gbc_lblSleepTime.insets = new Insets(0, 0, 5, 5);
		gbc_lblSleepTime.gridx = 0;
		gbc_lblSleepTime.gridy = 1;
		getContentPane().add(lblSleepTime, gbc_lblSleepTime);
		
		spinner = new JSpinner(new SpinnerNumberModel(0.5, 0, 10.0, 0.5));//DoubleSpinnerModel());
		GridBagConstraints gbc_spinner = new GridBagConstraints();
		gbc_spinner.weightx = 1.0;
		gbc_spinner.insets = new Insets(0, 0, 5, 0);
		gbc_spinner.anchor = GridBagConstraints.WEST;
		gbc_spinner.gridx = 1;
		gbc_spinner.gridy = 1;
		getContentPane().add(spinner, gbc_spinner);
		
		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.gridwidth = 2;
		gbc_panel.insets = new Insets(0, 0, 0, 5);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 2;
		getContentPane().add(panel, gbc_panel);
		
		JButton btnOk = new JButton("Start");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				InputDialog.this.setVisible(false);
			}
		});
		panel.add(btnOk);
		
		JButton btnAvbryt = new JButton("Avbryt");
		btnAvbryt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				canceled = true;
				InputDialog.this.setVisible(false);
			}
		});
		panel.add(btnAvbryt);
	
		pack();
	}

	public String getDomene() {
		return textField.getText();
	}

	public double getSleepTime() {
		return (Double)spinner.getValue();
	}

	public boolean isCanceled() {
		return canceled;
	}

}
