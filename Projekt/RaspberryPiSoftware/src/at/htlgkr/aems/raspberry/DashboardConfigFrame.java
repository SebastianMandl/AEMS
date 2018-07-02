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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.json.JSONArray;
import org.json.JSONObject;

import at.htlgkr.aems.logger.Logger;
import at.htlgkr.aems.logger.Logger.LogType;
import at.htlgkr.aems.plugins.PlugIn;
import at.htlgkr.aems.raspberry.plugins.PlugInManager;
import at.htlgkr.aems.raspberry.upload.Authentication;
import at.htlgkr.aems.raspberry.upload.Authentication.Encryption;
import at.htlgkr.aems.settings.MeterTypes;

public class DashboardConfigFrame {

	public static final String BASE_TITLE = "AMES Konfigurationstool";
	
	private static final Color SEPARATOR_COLOR = new Color(0, 180, 50);
	private static final Font FONT = new Font("Arial", Font.PLAIN, 16);
	
	public static final HashMap<String, JTextField> PORT_LABELS = new HashMap<>();
	public static final HashMap<String, JComboBox<PortOption>> COMBOBOXES = new HashMap<>();
	
	private static volatile boolean shouldBlockExecution = true;
	private static BufferedImage image;
	
	private static JTextField username, password;
	
	//private static final int INDEX_AUTOMATIC = 0;
	private static final int INDEX_GAS_METER = 0;
	private static final int INDEX_SENSOR = 1;
	private static final int INDEX_ELECTRICITY_METER = 2;
	private static final int INDEX_WATER_METER = 3;
	
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
				g.fillRect(0, 30, getWidth(), getHeight());
				g.drawImage(image, 10, 35, image.getWidth() / 2, image.getHeight() / 2, null);
				
				g.setColor(DetailMeterConfigFrame.SEPARATOR_COLOR);
				g.setFont(FONT.deriveFont(Font.BOLD, 30));
				
				g.drawString("       Konfigurationstool", image.getWidth() / 2 + 50, image.getHeight() / 2 + 35);
				
				g.setFont(FONT.deriveFont(Font.BOLD, 20));
				g.setColor(Color.BLACK);
				g.drawString("Anschlüsse:", 10, image.getHeight() - 10);
			}
		};
		
		JMenuBar menuBar = new JMenuBar();
		menuBar.setPreferredSize(new Dimension(800, 25));
		
		JMenu file = new JMenu("Datei");
		menuBar.add(file);
		
		JMenuItem _import = new JMenuItem("Konfiguration importieren...");
		_import.addActionListener(x -> {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			chooser.setFileFilter(new FileNameExtensionFilter("Konfigurationsdatei", "conf"));
			chooser.setDialogTitle("Konfiguration importieren");
			int result = chooser.showOpenDialog(frame);
			if(result == JFileChooser.APPROVE_OPTION) {
				File configFile = chooser.getSelectedFile();
				try {
					BufferedReader reader = new BufferedReader(new FileReader(configFile));
					StringBuilder builder = new StringBuilder();
					for(String line = reader.readLine(); line != null; line = reader.readLine()) {
						builder.append(line);
					}		
					reader.close();
					
					String definitiveString = builder.toString();
					Logger.log(LogType.INFO, "Read %d bytes from \"%s\"", definitiveString.getBytes().length, configFile.getAbsolutePath());
					
					JSONObject root = new JSONObject(definitiveString);
					JSONArray meters = root.getJSONArray("meters");
					int totalImports = 0;
					int i = 0;
					for(; i < meters.length(); i++) {
						JSONObject obj = meters.getJSONObject(i);
						String meterId = obj.getString("meterId");
						String port = obj.getString("port");
						String pluginName = obj.getString("plugin");
						
						PlugIn _plugin = PlugInManager.getPluginByName(pluginName);
						PortOption _option = new PortOption(_plugin, meterId, port, false);
						_option.setMeterType(_plugin.getSetting().getMeterType().getType());
						DetailMeterConfigFrame.CONFIGURATIONS.put(port, _option);
						DetailMeterConfigFrame.doInterface(_option, true);
						
						int index = -1;
						if(_plugin.getSetting().getMeterType().getType().equals(MeterTypes.ELECTRICITY)) {
							index = INDEX_ELECTRICITY_METER;
						} else if (_plugin.getSetting().getMeterType().getType().equals(MeterTypes.WATER)) {
							index = INDEX_WATER_METER;
						} else if (_plugin.getSetting().getMeterType().getType().equals(MeterTypes.GAS)) {
							index = INDEX_GAS_METER;
						}
						
						COMBOBOXES.get(port).setSelectedIndex(index);
						
						PlugInManager.setPluginForMeter(meterId, port, _plugin);
					}
					
					totalImports += i;
					
					JSONArray sensors = root.getJSONArray("sensors");
					i = 0;
					for(; i < sensors.length(); i++) {
						JSONObject obj = sensors.getJSONObject(i);
						String sensorName = obj.getString("sensorName");
						String port = obj.getString("port");
						String pluginName = obj.getString("plugin");
						
						PlugIn _plugin = PlugInManager.getPluginByName(pluginName);
						PortOption _option = new PortOption(_plugin, sensorName, port, true);
						DetailSensorConfigFrame.CONFIGURATIONS.put(port, _option);
						DetailSensorConfigFrame.doInterface(_option, true);
						COMBOBOXES.get(port).setSelectedIndex(INDEX_SENSOR);
						PlugInManager.setPluginForSensor(sensorName, port, _plugin);
					}
					
					totalImports += i;
					
					Logger.log(LogType.INFO, "Imported %d port configurations", totalImports);
				} catch (IOException e) {
					JOptionPane.showMessageDialog(frame, "Konfigurationsdatei konnte nicht gelesen werden!", "Fehler", JOptionPane.OK_OPTION);
					Logger.log(LogType.ERROR, "Configuration could not be imported! %s", e.getMessage());
				}
			}
		});
		file.add(_import);
		
		JMenuItem _export = new JMenuItem("Konfiguration exportieren...");
		_export.addActionListener(x -> {
			JFileChooser chooser = new JFileChooser();
			chooser.setDialogTitle("Konfiguration exportieren");
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int result = chooser.showSaveDialog(frame);
			if(result == JFileChooser.APPROVE_OPTION) {
				File dir = chooser.getSelectedFile();
				SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH-mm-ss");
				String fileName = "aems_config_" + dateFormat.format(new Date()) + ".conf";
				File configFile = new File(dir.getAbsolutePath() + File.separator + fileName);
				try {
					configFile.createNewFile();
					StringBuilder builder = new StringBuilder();
					builder.append("{\n");
					builder.append("\tmeters : [\n");
					BufferedWriter writer = new BufferedWriter(new FileWriter(configFile));
					for(String key : DetailMeterConfigFrame.CONFIGURATIONS.keySet()) {
						PortOption option = DetailMeterConfigFrame.CONFIGURATIONS.get(key);
						String plugInName = option.getPlugIn().getName();
						String port = option.getPort();
						builder.append("\t\t{ ").append("meterId : \"").append(option.getPlugIn().getSetting().getMeterId()).append("\", ");
						builder.append("plugin : \"").append(plugInName).append("\", ");
						builder.append("port : \"").append(port).append("\" },\n");
					}
					if(DetailMeterConfigFrame.CONFIGURATIONS.size() != 0)
						builder.setLength(builder.length() - 2);
					builder.append("\n\t],\n");
					builder.append("\tsensors : [\n");
					for(String key : DetailSensorConfigFrame.CONFIGURATIONS.keySet()) {
						PortOption option = DetailSensorConfigFrame.CONFIGURATIONS.get(key);
						String plugInName = option.getPlugIn().getName();
						String port = option.getPort();
						builder.append("\t\t{ ").append("sensorName : \"").append(option.getTitle()).append("\", ");
						builder.append("plugin : \"").append(plugInName).append("\", ");
						builder.append("port : \"").append(port).append("\" },\n");
					}
					if(DetailSensorConfigFrame.CONFIGURATIONS.size() != 0)
						builder.setLength(builder.length() - 2);
					builder.append("\n\t]\n}");
					
					String definitiveString = builder.toString();
					Logger.log(LogType.INFO, "Exported %d bytes to \"%s%s%s\"", definitiveString.getBytes().length, dir.getAbsolutePath(), File.separator, fileName);
					
					writer.write(definitiveString);
					writer.flush();
					writer.close();
				} catch (IOException e) {
					JOptionPane.showMessageDialog(frame, "Konfigurationsdatei konnte nicht erstellt werden!", "Fehler", JOptionPane.OK_OPTION);
					Logger.log(LogType.ERROR, "Configuration could not be exported! %s", e.getMessage());
				}
			}
		});
		file.add(_export);
		
		JMenuItem _exit = new JMenuItem("Beenden");
		_exit.addActionListener(x -> {
			int response = JOptionPane.showConfirmDialog(frame, "Sind Sie sicher, dass Sie das Programm beenden wollen?", "Beenden?", JOptionPane.YES_NO_OPTION);
			if(response == JOptionPane.YES_OPTION) {
				System.exit(0);
			}			
		});
		file.add(_exit);
		
		header.add(menuBar);
		
		header.setPreferredSize(new Dimension(800, 180));
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
		
		StringBuilder builder = new StringBuilder();
		try {
			Process process = Runtime.getRuntime().exec(new String[] {"bash", "listDevsCommand.bash"});
		    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		    Logger.log(LogType.INFO, "Reading in ports...");
		    for(String line = reader.readLine(); line != null; line = reader.readLine()) {
		    	builder.append(line).append("\n");
		    }
		} catch(Exception e) {e.printStackTrace();}
		
		Logger.log(LogType.INFO, builder.toString());
		
		String commandResult = /*builder.toString();*/"ttyUSB0"; //*/
		final Pattern TTY_USB_PATTERN = Pattern.compile("(ttyUSB[0-9]+|ttyACM[0-9]+)+");
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
				Logger.log(LogType.INFO, "detected port \"%s\"", ttyUSBPort);
				
				JTextField field = new JTextField(ttyUSBPort.substring(3));
				field.setBorder(null);
				field.setForeground(Color.BLACK);
				PORT_LABELS.put(ttyUSBPort, field);
				field.setBackground(color);
				field.setFont(FONT.deriveFont(Font.BOLD));
				field.setEditable(false);
				container.add(field);
				
				JComboBox<PortOption> comboBox = new JComboBox<>();
				//comboBox.addItem(new PortOption("Automatisch", ttyUSBPort));
				comboBox.addItem(new PortOption("Gaszähler", ttyUSBPort));
				comboBox.addItem(new PortOption("Sensor", ttyUSBPort));
				comboBox.addItem(new PortOption("Stromzähler", ttyUSBPort));
				comboBox.addItem(new PortOption("Wasserzähler", ttyUSBPort));
				comboBox.setFont(FONT);
				comboBox.setSelectedIndex(2);
				container.add(comboBox);
				
				COMBOBOXES.put(ttyUSBPort, comboBox);
				
				JButton config = new JButton(new ImageIcon("gear.png"));
				config.setPreferredSize(new Dimension(35, 35));
				config.addActionListener(x -> {
					int item = comboBox.getSelectedIndex();
					switch(item) {
						case INDEX_ELECTRICITY_METER:
							PortOption option = (PortOption)comboBox.getSelectedItem();
							option.setMeterType(MeterTypes.ELECTRICITY);
							DetailMeterConfigFrame.doInterface(option);
							break;
						case INDEX_GAS_METER:
							option = (PortOption)comboBox.getSelectedItem();
							option.setMeterType(MeterTypes.GAS);
							DetailMeterConfigFrame.doInterface(option);
							break;
						case INDEX_WATER_METER:
							option = (PortOption)comboBox.getSelectedItem();
							option.setMeterType(MeterTypes.WATER);
							DetailMeterConfigFrame.doInterface(option);
							break;
							
						case INDEX_SENSOR:
							option = (PortOption)comboBox.getSelectedItem();
							DetailSensorConfigFrame.doInterface(option);
							break;
							
//						case INDEX_AUTOMATIC:
//							break;
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

		JComponent username = GUIUtils.createInput("Benutzername:", FONT, false);
		DashboardConfigFrame.username = ((JTextField) username.getComponents()[1]);
		credentials.add(username);
		JComponent password = GUIUtils.createInput("Passwort:", FONT, true);
		DashboardConfigFrame.password = ((JTextField) password.getComponents()[1]);
		credentials.add(password);
		
		JPanel configurePanel = new JPanel();
		configurePanel.setBackground(Color.WHITE);
		
		JButton configure = new JButton("konfigurieren");
		configure.setFont(FONT.deriveFont(Font.BOLD));
		configure.setPreferredSize(new Dimension(200, 25));
	
		configure.addActionListener(x -> {
			frame.dispose(); // exchange with loading screen introduction ?
			PlugInManager.setAuthentication(new Authentication(DashboardConfigFrame.username.getText(), DashboardConfigFrame.password.getText(), Encryption.AES));
			PlugInManager.runAllPlugins();//			frame.setContentPane(consoleView);
//			ConsoleView.readFromConsole();
//			System.out.println("test");
//			frame.repaint();
		});
		
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
	
	//private static JPanel consoleView = ConsoleView.generateView();
	
}
