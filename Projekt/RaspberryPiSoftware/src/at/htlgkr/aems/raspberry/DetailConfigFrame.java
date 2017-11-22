package at.htlgkr.aems.raspberry;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import at.htlgkr.aems.plugins.PlugIn;
import at.htlgkr.aems.raspberry.plugins.PlugInManager;

public class DetailConfigFrame {

	public static final Color SEPARATOR_COLOR = new Color(0, 180, 50);
	private static final Font FONT = new Font("Arial", Font.PLAIN, 16);
	private static final int WINDOW_WIDTH = 600;
	
	private static JComponent meterId = null;
	
	public static final HashMap<String, PortOption> CONFIGURATIONS = new HashMap<>();
	
	public static void doInterface(PortOption option) {
		doInterface(option, false);
	}
	
	public static void doInterface(PortOption option, boolean closeImmediately) {
		
		PortOption config = CONFIGURATIONS.get(option.getPort());
		
		JFrame frame = new JFrame(DashboardConfigFrame.BASE_TITLE + " - " + option.getTitle() + " (" + option.getPort() + ")");
		frame.setSize(WINDOW_WIDTH, 170);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setAlwaysOnTop(true);
		
		JPanel header = new JPanel();
		header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
		header.setBackground(Color.WHITE);
		JLabel label = new JLabel(option.getTitle() + " (" + option.getPort() + ")");
		label.setForeground(Color.BLACK);
		label.setFont(FONT.deriveFont(Font.BOLD));
		label.setAlignmentX(Component.CENTER_ALIGNMENT);
		label.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		JSeparator separator = new JSeparator();
		separator.setPreferredSize(new Dimension(400, 2));
		separator.setForeground(SEPARATOR_COLOR);
		
		header.add(label);
		header.add(separator);
		
		frame.add(header, BorderLayout.NORTH);
		
		JPanel body = new JPanel();
		body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
		body.setBackground(Color.WHITE);
		
		body.add((meterId = GUIUtils.createInput("Zählernummer:", FONT, false, new Dimension(WINDOW_WIDTH, 35), new Dimension(400, 25))));
		
		if(config != null && config.getTitle() != null) {
			((JTextField) meterId.getComponents()[1]).setText(config.getTitle());
		}
		
		JPanel container = new JPanel();
		container.setBackground(Color.WHITE);
		container.setLayout(new FlowLayout());
		
		JLabel pluginListLabel = new JLabel("Plugin:");
		pluginListLabel.setForeground(Color.BLACK);
		pluginListLabel.setFont(FONT.deriveFont(Font.BOLD));
		pluginListLabel.setPreferredSize(new Dimension(120, 25));
		container.add(pluginListLabel);
		
		JComboBox<PlugIn> pluginList = new JComboBox<>();
		pluginList.setPreferredSize(new Dimension(400, 25));
		
		int selectedIndex = 0;
		for(PlugIn plugin : PlugInManager.getPluginsForType(option.getMeterType())) {
			pluginList.addItem(plugin);
			
			if(config != null && config.getPlugIn() != null && config.getPlugIn().getName().equals(plugin.getName())) {
				pluginList.setSelectedIndex(selectedIndex);
			}
			
			selectedIndex++;
			
		}
		pluginList.setFont(FONT);
		container.add(pluginList);
		
		
		separator = new JSeparator();
		separator.setPreferredSize(new Dimension(400, 2));
		separator.setForeground(SEPARATOR_COLOR.darker());
		
		body.add(container);
		body.add(separator);
				
		frame.add(body, BorderLayout.CENTER);
		
		JPanel footer = new JPanel();
		footer.setBackground(Color.WHITE);
		footer.setLayout(new FlowLayout());
		
		JButton dismiss = new JButton("Abbrechen");
		dismiss.setFont(FONT);
		dismiss.setPreferredSize(new Dimension(150, 25));
		dismiss.addActionListener(x -> {
			frame.dispose();
		});
		
		JButton resetConfiguration = new JButton("dekonfigurieren");
		resetConfiguration.setFont(FONT);
		resetConfiguration.setPreferredSize(new Dimension(150, 25));
		resetConfiguration.addActionListener(x -> {
			JTextField field = ((JTextField) meterId.getComponents()[1]);
			CONFIGURATIONS.remove(option.getPort());
			PlugInManager.unsetPluginForMeter(field.getText());
			field.setText("");
			pluginList.setSelectedIndex(0);
			
			if(DashboardConfigFrame.PORT_LABELS.containsKey(option.getPort())) {
				DashboardConfigFrame.PORT_LABELS.get(option.getPort()).setForeground(Color.BLACK);
			}
			
			if(DashboardConfigFrame.COMBOBOXES.containsKey(option.getPort())) {
				DashboardConfigFrame.COMBOBOXES.get(option.getPort()).setEnabled(true);
			}
			
			frame.dispose();
		});
		
		JButton confirm = new JButton("Bestätigen");
		confirm.setFont(FONT.deriveFont(Font.BOLD));
		confirm.setPreferredSize(new Dimension(150, 25));
		confirm.addActionListener(x -> {
			String mID = ((JTextField) meterId.getComponents()[1]).getText();
			
			if(mID == null || mID.isEmpty()) {
				JOptionPane.showMessageDialog(frame, "Zählernummer wurde nicht angegeben!", "Fehler", JOptionPane.OK_OPTION);
				return;
			}
			
			
			PortOption _option = new PortOption((PlugIn) pluginList.getSelectedItem(), mID, option.getPort());
			_option.setPlugin(PlugInManager.setPluginForMeter(mID, option.getPort(), (PlugIn) pluginList.getSelectedItem()));
			CONFIGURATIONS.put(option.getPort(), _option);
			
			if(DashboardConfigFrame.PORT_LABELS.containsKey(option.getPort())) {
				DashboardConfigFrame.PORT_LABELS.get(option.getPort()).setForeground(SEPARATOR_COLOR);
			}
			
			if(DashboardConfigFrame.COMBOBOXES.containsKey(option.getPort())) {
				DashboardConfigFrame.COMBOBOXES.get(option.getPort()).setEnabled(false);
			}
			
			frame.dispose();
		});
		
		footer.add(dismiss);
		footer.add(resetConfiguration);
		footer.add(confirm);
		
		frame.add(footer, BorderLayout.SOUTH);
		
		if(!closeImmediately)
			frame.setVisible(true);
		else {
			confirm.doClick();
			frame.dispose();
		}
		
	}
	
}
