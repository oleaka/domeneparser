package no.domeneparser;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import javax.swing.JTextArea;

public class Display extends JFrame {
	
	private JTextArea textArea;
	public Display() {
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
	
		this.setSize(400, 200);
	}

	public void setText(String text) {
		textArea.append(text+"\n");
		textArea.setCaretPosition(textArea.getText().length());
	}

}
