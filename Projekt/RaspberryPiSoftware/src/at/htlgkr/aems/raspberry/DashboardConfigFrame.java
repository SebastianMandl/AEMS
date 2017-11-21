package at.htlgkr.aems.raspberry;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import at.htlgkr.aems.logger.Logger;
import at.htlgkr.aems.logger.Logger.LogType;
import at.htlgkr.aems.settings.MeterTypes;

public class DashboardConfigFrame {

	public static final String BASE_TITLE = "AMES Konfigurationstool";
	
	private static final Color SEPARATOR_COLOR = new Color(0, 180, 50);
	private static final Font FONT = new Font("Arial", Font.PLAIN, 16);
	
	private static volatile boolean shouldBlockExecution = true;
	private static BufferedImage image;
	
	private static final int INDEX_AUTOMATIC = 0;
	private static final int INDEX_GAS_METER = 1;
	private static final int INDEX_SENSOR = 2;
	private static final int INDEX_ELECTRICITY_METER = 3;
	private static final int INDEX_WATER_METER = 4;
	
	public static void doInterface() {
		JFrame frame = new JFrame(BASE_TITLE);
		frame.setSize(800, 600);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		
		try {
			image = ImageIO.read(new File("logo.png")); // load logo into memory
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		final Color COLOR1 = new Color(240, 240, 240);
		final Color COLOR2 = new Color(220, 220, 220);
		Color color = COLOR1;
		
		JPanel header = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				super.paint(g);
				g.setColor(Color.WHITE);
				g.fillRect(0, 0, getWidth(), getHeight());
				g.drawImage(image, 10, 5, image.getWidth() / 2, image.getHeight() / 2, null);
				
				g.setColor(new Color(37, 206, 130));
				g.setFont(FONT.deriveFont(Font.BOLD, 30));
				
				g.drawString("Configuration Interface", image.getWidth() / 2 + 50, image.getHeight() / 2);
				
				g.setFont(FONT.deriveFont(Font.BOLD, 20));
				g.setColor(Color.BLACK);
				g.drawString("Anschlüsse:", 10, image.getHeight() - 40);
			}
		};
		header.setPreferredSize(new Dimension(800, 150));
		frame.add(header, BorderLayout.NORTH);
		JPanel ports = new JPanel();
		ports.setBackground(Color.WHITE);
		ports.setLayout(new BoxLayout(ports, BoxLayout.Y_AXIS));
		
		JScrollPane pane = new JScrollPane(ports);
		JScrollBar bar = new JScrollBar(JScrollBar.VERTICAL);
		bar.setUnitIncrement(20);
		pane.setVerticalScrollBar(bar);
		pane.setHorizontalScrollBar(null);
		frame.add(pane, BorderLayout.CENTER);
		
		String commandResult = "ttyUSB0, ttyUSB1, ttyUSB2, ttyUSB3"; //readFromStream(readCommand("listDevsCommand.bash"), false);
		final Pattern TTY_USB_PATTERN = Pattern.compile("tty(USB[0-9]+)+");
		Matcher matcher = TTY_USB_PATTERN.matcher(commandResult);
		int count = 0;
		while(matcher.find()) {
			for(int i = 1; i <= matcher.groupCount(); i++) {
				JPanel container = new JPanel();
				container.setLayout(new FlowLayout());
				container.setPreferredSize(new Dimension(800, 40));
				container.setMaximumSize(new Dimension(800, 40));
				container.setBackground(color);
				
				String ttyUSBPort = matcher.group(i);
				Logger.log(LogType.INFO, "detected port \"tty%s\"", ttyUSBPort);
				
				JTextField field = new JTextField(ttyUSBPort);
				field.setBorder(null);
				field.setBackground(color);
				field.setFont(FONT.deriveFont(Font.BOLD));
				field.setEditable(false);
				container.add(field);
				
				JComboBox<PortOption> comboBox = new JComboBox<>();
				comboBox.addItem(new PortOption("Automatisch", ttyUSBPort));
				comboBox.addItem(new PortOption("Gaszähler", ttyUSBPort));
				comboBox.addItem(new PortOption("Sensor", ttyUSBPort));
				comboBox.addItem(new PortOption("Stromzähler", ttyUSBPort));
				comboBox.addItem(new PortOption("Wasserzähler", ttyUSBPort));
				comboBox.setFont(FONT);
				comboBox.setSelectedIndex(3);
				container.add(comboBox);
				
				JButton config = new JButton(new ImageIcon("gear.png"));
				config.setPreferredSize(new Dimension(35, 35));
				config.addActionListener(x -> {
					int item = comboBox.getSelectedIndex();
					switch(item) {
						case INDEX_ELECTRICITY_METER:
							PortOption option = (PortOption)comboBox.getSelectedItem();
							option.setMeterType(MeterTypes.ELECTRICITY);
							DetailConfigFrame.doInterface(option);
							break;
						case INDEX_GAS_METER:
							option = (PortOption)comboBox.getSelectedItem();
							option.setMeterType(MeterTypes.GAS);
							DetailConfigFrame.doInterface(option);
							break;
						case INDEX_WATER_METER:
							option = (PortOption)comboBox.getSelectedItem();
							option.setMeterType(MeterTypes.WATER);
							DetailConfigFrame.doInterface(option);
							break;
							
						case INDEX_SENSOR:
							break;
							
						case INDEX_AUTOMATIC:
							break;
					}
				});
				
				container.add(config);
				
				ports.add(container);
				
				
				if(color == COLOR1)
					color = COLOR2;
				else
					color = COLOR1;
				}
			count++;
		} 

		if(count == 0){
			// no interfaces are connected			
			JTextField field = new JTextField("No device is connected via usb!");
			field.setEditable(false);
			field.setFont(FONT);
			field.setForeground(Color.WHITE);	
			field.setHorizontalAlignment(JTextField.CENTER);
			
			field.setBackground(new Color(239, 19, 19));
			ports.add(field);
			
			Logger.log(LogType.WARNING, "No device is connected via usb!!");
		};
		
		JPanel credentials = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				super.paint(g);
				g.setFont(FONT.deriveFont(Font.BOLD, 20));
				g.setColor(Color.BLACK);
				FontMetrics metrics = new FontMetrics(FONT) {
					private static final long serialVersionUID = 1L;
				};
				g.drawString("Benutzerdaten:", 10, getX() + metrics.getHeight() + 10);
			};
		};
		credentials.setLayout(new BoxLayout(credentials, BoxLayout.Y_AXIS));
		
		credentials.setPreferredSize(new Dimension(800, 145));
		JSeparator seperator = new JSeparator();
		seperator.setBackground(SEPARATOR_COLOR);
		seperator.setPreferredSize(new Dimension(800, 20));
		credentials.add(seperator);
		credentials.setBackground(Color.WHITE);

		credentials.add(GUIUtils.createInput("Benutzername:", FONT, false));
		credentials.add(GUIUtils.createInput("Passwort:", FONT, true));
		
		JPanel configurePanel = new JPanel();
		configurePanel.setBackground(Color.WHITE);
		
		JButton configure = new JButton("konfigurieren");
		configure.setFont(FONT.deriveFont(Font.BOLD));
		configure.setPreferredSize(new Dimension(200, 25));
	
		configurePanel.add(configure);
		credentials.add(configurePanel);
		
		frame.add(credentials, BorderLayout.SOUTH);
		
		shouldBlockExecution = true;
		
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				shouldBlockExecution = false;
				frame.dispose();
			}
		});
		
		frame.setVisible(true);
	
		while(shouldBlockExecution);
	}	
	
}
