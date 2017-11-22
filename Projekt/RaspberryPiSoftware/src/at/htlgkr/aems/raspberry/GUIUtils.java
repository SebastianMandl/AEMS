package at.htlgkr.aems.raspberry;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class GUIUtils {

	public static JComponent createInput(String title, final Font FONT, boolean isPassword) {
		return createInput(title, FONT, isPassword, new Dimension(800, 35), new Dimension(350, 25));
	}
	
	public static JComponent createInput(String title, final Font FONT, boolean isPassword, Dimension panelSize, Dimension inputFieldSize) {
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setMaximumSize(panelSize);
		panel.setPreferredSize(panelSize);
		JTextField label = new JTextField(title);
		label.setForeground(Color.BLACK);
		label.setPreferredSize(new Dimension(120, inputFieldSize.height));
		label.setAlignmentX(Component.RIGHT_ALIGNMENT);
		label.setFont(FONT.deriveFont(Font.BOLD));
		label.setBackground(Color.WHITE);
		label.setEditable(false);
		label.setBorder(null);
		panel.add(label);
		JTextField input = isPassword ? new JPasswordField() : new JTextField();
		input.setFont(FONT);
		input.setEditable(true);
		input.setBackground(Color.WHITE);
		input.setPreferredSize(inputFieldSize);
		panel.add(input);
		
		return panel;
	}
	
}
