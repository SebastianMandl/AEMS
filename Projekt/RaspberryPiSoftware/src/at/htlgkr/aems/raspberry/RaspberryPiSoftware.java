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
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.nio.BufferOverflowException;
import java.nio.FloatBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import at.htlgkr.aems.util.crypto.Encrypter;
import at.htlgkr.aems.util.key.DiffieHellmanProcedure;

/**
 * This class represents the software which is running on the 
 * raspberry pi. It is responsible for gathering information which
 * is sent over a serial connection by a meter (electricity, gas or water).
 * @author Sebastian
 * @since 04-08-2017
 */
public class RaspberryPiSoftware {

	/**
	 * This enumeration represents the possible configuration
	 * modes for the serial connection. The meters can either be configured
	 * to transmit 300 bits per minute or 9600 bits per minute.
	 * @author Sebastian
	 */
	public enum PortConfigurationMode {
		BAUD_UNDEFINED, BAUD_300, BAUD_9600;
	}

	/**
	 * in case of the port being not properly configured this is the amount of chars which
	 * are considered junk hence able to be discarded.
	 */
	private static final int JUNK_CHARS = 25;
	
	/** 
	 * this field is used to spare this application from finding
	 * the proper configuration mode repeatedly.
	 * this field is considered to be a performance tweak.
	 */
	private static PortConfigurationMode portConfigMode;
		
	/**
	 * this buffer is used to store values which could not be transmitted to the server
	 * due to communication issues. Unless the connection is restored all the values
	 * that are supposed to be sent to the server will be stored in this buffer preliminarily.
	 * After the connection has been recovered this buffer will be cleared and all the values
	 * that it holds will be transmitted to the server.
	 * 
	 * INFO:
	 * no value which is aquired through reading the serial connection will become lost.
	 */
	private static FloatBuffer valueBuffer = FloatBuffer.allocate(25);
	
	/**
	 * This method is the literally the heart of this program.
	 * It keeps the process going until the meter is no longer proned 
	 * to transmit data.
	 * 
	 * INFO:
	 * Data will be read cyclical in a period of 10 minutes time.
	 * 
	 * Once all the data has been read in it will seek for the position 1.8.1 which
	 * is considered to hold the total consumption value of the meter (as far as an electricity meter is concerned)
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		final int CYCLE_MINUTES = 10;
		final int CYCLE_INTERVAL = 1_000 * 60 * CYCLE_MINUTES;
		
		final Calendar CALENDAR = Calendar.getInstance();
		
		openConfigurationInterface();
		
		while(true) {
			
			long startTime = System.currentTimeMillis();
			
			// read incoming data from stream and store it for further processing
			String data = readFromStream();
			
			if(data == null) {
				System.err.println("no data was received from the meter! Connection probably broke!");
				return;
			}
			
			// initialize a pattern to search the data read from the stream for a specific value: total consumption of electricity
			// value is to be found at postition 1.8.1
			Pattern pattern = Pattern.compile("1\\.8\\.1\\((.*)\\*kWh\\)*");
			Matcher matcher = pattern.matcher(data);
			if(matcher.find() && matcher.groupCount() > 0) {
				float totalConsumption = Float.valueOf(matcher.group(1));
				System.out.printf("total consumption is %f%n", totalConsumption);
				transmitDataToServer(totalConsumption);
			} else
				System.err.println("regex didn't find a match!");
			
			long endTime = System.currentTimeMillis();
			long timeDiff = endTime - startTime;
			
			int deltaTime = (int) (CYCLE_INTERVAL - timeDiff);
			deltaTime = (deltaTime < 0) ? 0 : deltaTime;
			
			CALENDAR.setTime(new Date(endTime + deltaTime));
			int hours = CALENDAR.get(Calendar.HOUR_OF_DAY);
			int minutes = CALENDAR.get(Calendar.MINUTE);
			int seconds = CALENDAR.get(Calendar.SECOND);
			
			System.out.println("next read impending at " + (hours < 10 ? "0" + hours : hours) + ":" + 
					(minutes < 10 ? "0" + minutes : minutes) + ":" + (seconds < 10 ? "0" + seconds : seconds));
			
			delay(deltaTime);
		}
	}
	
	/**
	 * This function transmits the total consumption value to the server via REST-COMMUNICATION.
	 * @param totalConsumption - the total consumption value aquired through reading the serial connection.
	 */
	private static void transmitDataToServer(float totalConsumption) {		
		final String HTTP_METHOD = "POST";
		final String PROPERTY_TOTAL_CONSUMPTON = "total_consumption";
		
		final int TIME_OUT = 10 * 1_000; // 10 seconds
		final long LAST_TIME = System.currentTimeMillis();
		boolean shouldTransmitData = true;
		final byte[] KEY = new byte[DiffieHellmanProcedure.KEY_LENGTH];
		Thread keyExchangeThread = new Thread(() -> {
									try {
										DiffieHellmanProcedure.sendKeyInfos(new Socket(InetAddress.getByName("aems.net"), 9950));
										System.arraycopy(DiffieHellmanProcedure.confirmKey(), 0, KEY, 0, DiffieHellmanProcedure.KEY_LENGTH);
									} catch (IOException e) {
										e.printStackTrace();
									}
								});
		
		try {			
			HttpURLConnection connection = (HttpURLConnection) new URL("").openConnection();
			
			keyExchangeThread.start();
			
			while(keyExchangeThread.isAlive()) {
				if(System.currentTimeMillis() - LAST_TIME >= TIME_OUT) {
					System.err.println("key could not be exhanged! Aborting...! Connection would be insecure otherwise!");
					shouldTransmitData = false;
				}
			}
			
			if(!shouldTransmitData)
				return;
			
			// if the connection above could be established then first
			// check if there are any values which could not be transmitted priorly.
			// this is necessary otherwise these values will be lost forever.
			if(valueBuffer.position() > 0) {
				valueBuffer.rewind();
				while(valueBuffer.hasRemaining()) {
					try {
						transmitDataToServer(Float.valueOf(new String(Encrypter.requestEncryption(KEY, String.valueOf(valueBuffer.get()).getBytes()))));
					} catch (NumberFormatException | InvalidKeyException | IllegalBlockSizeException
							| BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException e) {
						e.printStackTrace();
					}
				}
				valueBuffer.clear();
			}
			
			connection.setRequestMethod(HTTP_METHOD);
			connection.setDoInput(true);
			connection.addRequestProperty(PROPERTY_TOTAL_CONSUMPTON, String.valueOf(totalConsumption));
			
			if(connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
				connection.getInputStream();
			} else {
				System.err.printf("transmission was unsuccessful! Response code is %d!%n", connection.getResponseCode());
			}
			
		} catch (IOException e) {
			System.err.println("connection could not be established! -> " + e.getMessage());
			try {
				valueBuffer.put(totalConsumption); // store the value in the buffer to not loose it.
			} catch(BufferOverflowException ex) {
				// when the buffer exceeds its capacity it will notify the user via a message that many values
				// could not be transmitted.
				System.err.println("buffer is has exceeded its limit! Please check your internet connection!");
				float[] bufferBackup = valueBuffer.array();
				valueBuffer = FloatBuffer.allocate(valueBuffer.capacity() * 2);
				valueBuffer.put(bufferBackup);
				valueBuffer.put(totalConsumption); // since the exception has been thrown this value wasn't 
												   // put into the buffer, prior to the backup buffer beeing created.
			}
		}
	}
	
	/**
	 * This function takes two steps:
	 * 
	 * First step:
	 * It tries to find the proper configuration for the serial connection.
	 * If no configuration was fitting the operation will abort and it will result in an error message
	 * which will be outputted.
	 * 
	 * Second step:
	 * Once the communication port was properly configured it is ready to be read from.
	 * This part is handled by the {@link RaspberryPiSoftware#getStreamData(String, PortConfigurationMode[])} function.
	 * 
	 * @return all the characters which were read from the stream of the serial connection.
	 * @see RaspberryPiSoftware#getStreamData(String, PortConfigurationMode[])
	 */
	private static String readFromStream() {	
		String command = readCommand("readFromStream.bash");
		
		PortConfigurationMode[] modes = new PortConfigurationMode[] { PortConfigurationMode.BAUD_300, PortConfigurationMode.BAUD_9600 };
		
		String streamData = getStreamData(command, modes);
		if(streamData == null) {
			System.err.println("port could not be properly configured (bit rate did not match)!");
		}
		return streamData;
	}

	/**
	 * This function is responsible for configuring and reading the stream data.
	 * 
	 * It tries to find a fitting configuration for the serial connection.
	 * If non of the modes given is suitable the return value of this function is going to be {@code null}.
	 * 
	 * @param command - the command initiating a transmission of data by the meter
	 * @param modes - available configuration modes
	 * @return - the data read from the stream.
	 */
	private static String getStreamData(String command, PortConfigurationMode[] modes) {
		
		// if the port configuration mode is already known
		// the port can be configured directly with that mode.
		if(portConfigMode != null) {
			return readFromStream(command);
		}
		
		// iterate over all modes and check for read success.
		for(PortConfigurationMode mode : modes) {
			configurePort(mode);
			delay(1000); // give the os a pause
			String streamData = readFromStream(command);
			
			if(streamData.trim().length() > JUNK_CHARS) {
				// success: port is properly configured
				portConfigMode = mode;
				return streamData;
			}			
		}
		return null;
	}
	
	/**
	 * Convinience method for {@link RaspberryPiSoftware#readFromStream(String, int, boolean)}
	 */
	private static String readFromStream(String command) {
		return readFromStream(command, 0, true);
	}
	
	/**
	 * Convinience method for {@link RaspberryPiSoftware#readFromStream(String, int, boolean)}
	 */
	private static String readFromStream(String command, boolean init) {
		return readFromStream(command, 0, init);
	}
	
	/**
	 * This variable is utilized for thread terminating purposes.
	 */
	private static boolean isReadThreadInterrupted = false;
	
	/**
	 * This function provides lots of functionality:
	 * 
	 * It executes the command given as a paramter.
	 * It reads the data from the stream with a variable timout technique.
	 * 
	 * At the end it kills the process started to relinquish any occupied resources.
	 * 
	 * @param command - the command to execute
	 * @param delay - an arbitrary delay after which the command will be executed (delay in milliseconds)
	 * @return - the data read from the stream.
	 */
	private static String readFromStream(String command, int delay, boolean init) {
		delay(delay);	
		
		Process process = execCommand(command, delay);
				
		final long TIMEOUT_QUARTER_MINUTES = 1;
		final long TIMEOUT = 1_000 * 15 * TIMEOUT_QUARTER_MINUTES;
		long lastTime = System.currentTimeMillis();
		
		InputStream inputStream = process.getInputStream();
		final StringBuffer BUFFER = new StringBuffer();
		int previousBufferLength = BUFFER.length();
		
		if(init)
			initiateDataExchange();
			
		final Thread READ_THREAD = new Thread(() -> {
			while(!isReadThreadInterrupted) {
				int character = -1;
				
				try {					
					character = inputStream.read();
						
				} catch (IOException e) {
					System.err.println("read failed! -> " + e.getMessage());
				}
				
				if(character == -1) // end of stream
					break; // no more data
				
				BUFFER.append((char) character);
				System.out.print((char) character);
			}
			isReadThreadInterrupted = false;
		});
	
		lastTime = System.currentTimeMillis();
		BUFFER.setLength(0);
		READ_THREAD.start();
		
		while(true) {
			if(System.currentTimeMillis() - lastTime >= TIMEOUT) {
				
				if(BUFFER.length() > previousBufferLength) {
					previousBufferLength = BUFFER.length();
					lastTime = System.currentTimeMillis();
					continue;
				}
				
				isReadThreadInterrupted = true;
				try {
					READ_THREAD.join(1000);
				} catch (InterruptedException e) {
					System.err.println("READ-THREAD could not be terminated! -> " + e.getMessage());
				}
				break; // timeout
			}			
			delay(20);
		}
		
		killProcess(process);
		
		return BUFFER.toString();
	}
	
	/**
	 * This function kills the process passed as a parameter.
	 * @param process - the process to terminate
	 */
	private static void killProcess(Process process) {
		try {
			// INFO: this is reflection.
			// it is required to aquire the process id of the process within the UNIX-system.
			// the proccess id is necessitated to terminate the process definitively.
			Field field = process.getClass().getDeclaredField("pid");
			
			// required otherwise the field won't be accessible (reason: field is private).
			field.setAccessible(true);
			int pid = (Integer) field.get(process);
			execCommand("kill -l " + pid);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Reads in the command from an external location somewhere on the file system (is required to be a .bash file).
	 * @param fileName - the filename required to identify the file to read.
	 * @return the command located within the bash file.
	 */
	private static String readCommand(String fileName) {
		try (BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)))) {
			return reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Sends two messages which eventually allows the meter to send its information.
	 * These two messages don't vary in terms of baud rate.
	 */
	private static void initiateDataExchange() {
		System.out.println("sending messages!");
		
		String commandInit = readCommand("initiateCommand.bash");
		String commandAck = readCommand("acknowledgeCommand.bash");
		
		delay(50);
		
		// execute the init and ack commands several times to
		// ensure the commands are received by the meter
		// INFO: sometimes commands are lost or not well received by the meter
		// hence this is a precaution to prevent this from transpiring.
		for(int i = 0; i < 15; i++) {
			delay(50);
			execCommand(commandInit);
			execCommand(commandAck, 10);
		}
	}
	
	/**
	 * Configures the serial port according to the passed {@link PortConfigurationMode}.
	 * @param mode - the {@link PortConfigurationMode}
	 * @return {@code true} if command execution was successful {@code false} otherwise.
	 * 
	 * WARNING: only because the command execution was successful it does NOT automatically imply that the
	 * 			port configuration was successful as well (it is possible to configure a port 9600 baud even though it is 300 baud).
	 */
	private static boolean configurePort(PortConfigurationMode mode) {
		final String ERROR_CONFIGURATION_FAILED = "configuration %d baud failed!%n";
		
		switch(mode) {
			case BAUD_300:
				// proceed configuring the port with the specified setting until
				// the command is issued successfully.
				while(!configurePort300()) {
					System.err.printf(ERROR_CONFIGURATION_FAILED, 300);
				}
				return true;
			case BAUD_9600:
				// same principles apply as above.
				while(!configurePort9600()) {
					System.err.printf(ERROR_CONFIGURATION_FAILED, 9600);
				}
				return true;
			default:
				return false;
		}
	}
	
	/**
	 * Configures the serial port with 300 baud. (300 chars / min)
	 * @return {@code true} if the command was successful {@code false} otherwise.
	 */
	private static boolean configurePort300() {
		System.out.println("configuring port (300 baud) ...");
		String command = readCommand("config300.bash");
		return checkCommandSuccess(execCommand(command));
	}
	
	/**
	 * Configures the serial port with 9600 baud. (9600 chars / min)
	 * @return {@code true} if the command was successful {@code false} otherwise.
	 */
	private static boolean configurePort9600() {
		System.out.println("configuring port (9600 baud) ...");
		String command = readCommand("config9600.bash");
		return checkCommandSuccess(execCommand(command));
	}
	
	/**
	 * Verifies weather the command executed within the process was successful.
	 * @param process - the process from which the stream will be read
	 * @return - {@code true} if the command was successful {@code false} otherwise.
	 */
	private static boolean checkCommandSuccess(Process process) {
		if(process == null)
			return false;
		
		InputStream inputStream = process.getErrorStream();
		try {
			return inputStream.read() == -1;
		} catch (IOException e) {
			System.err.println("command failed! -> " + e.getMessage());
			return false;
		}
	}
	
	/**
	 * Convinience method for {@link RaspberryPiSoftware#execCommand(String, int)}
	 */
	private static Process execCommand(String command) {
		return execCommand(command, 0);
	}
	
	/**
	 * Executes the specified command in the os environment returning a 
	 * java process representation.
	 * @param command - the command to execute
	 * @param delay - a value constraint to the bounds of the integer size which permits to postpone command execution.
	 * @return the process in which the command is executed.
	 */
	private static Process execCommand(String command, int delay) {
		
		//System.out.println(command);
		
		String[] commands = null;
		if(command.startsWith("echo")) {
			 commands = new String[]{"bash", "-c", command};
		}
		
		delay(delay);
		
		try {
			if(commands != null)
				return Runtime.getRuntime().exec(commands);
			else
				return Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Delays the current process by the specified value.
	 * @param delay - delay time in millis
	 * @return {@code true} if the delay was successful {@code false} otherwise.
	 */
	private static boolean delay(int delay) {
		try {
			Thread.sleep(delay);
			return true;
		} catch (InterruptedException e) {
			return false;
		}
	}
	
	// -------------------------------------------------------------------------------------------------------------------------------------
	// GUI Starting
	
	private static volatile boolean shouldBlockExecution = false;
	private static BufferedImage image;
	
	private static void openConfigurationInterface() {
		JFrame frame = new JFrame("AMES Configuration Interface");
		frame.setSize(800, 600);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		
		try {
			image = ImageIO.read(new File("logo.png")); // load logo into memory
			System.out.println(image);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		final Color COLOR1 = new Color(240, 240, 240);
		final Color COLOR2 = new Color(220, 220, 220);
		Color color = COLOR1;
		final Font FONT = new Font("Arial", Font.PLAIN, 16);
		
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
				g.drawString("Ports:", 10, image.getHeight() - 40);
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
		
		final HashMap<String, JComboBox<String>> CONFIG = new HashMap<>();
		
		String commandResult = readFromStream(readCommand("listDevsCommand.bash"), false);
		final Pattern TTY_USB_PATTERN = Pattern.compile("(?:.*(ttyUSB[0-9]+).*)+");
		Matcher matcher = TTY_USB_PATTERN.matcher(commandResult);
		int count = 0;
		while(matcher.find()) {
			System.out.println(matcher.group(1));
			for(int i = 1; i <= matcher.groupCount(); i++) {
				JPanel container = new JPanel();
				container.setLayout(new FlowLayout());
				container.setPreferredSize(new Dimension(800, 40));
				container.setMaximumSize(new Dimension(800, 40));
				container.setBackground(color);
				
				String ttyUSBPort = matcher.group(i);
				System.out.println("found port " + ttyUSBPort);
				
				JTextField field = new JTextField(ttyUSBPort);
				field.setBorder(null);
				field.setBackground(color);
				field.setFont(FONT.deriveFont(Font.BOLD));
				field.setEditable(false);
				container.add(field);
				
				JComboBox<String> comboBox = new JComboBox<>();
				comboBox.addItem("Electricity Meter");
				comboBox.addItem("Gas Meter");
				comboBox.addItem("Water Meter");
				comboBox.addItem("Other");
				comboBox.setFont(FONT);
				comboBox.setSelectedIndex(3);
				container.add(comboBox);
				
				CONFIG.put(ttyUSBPort, comboBox);
				
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
			
			System.out.println("No device is connected via usb!!");
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
				g.drawString("Credentials:", 10, getX() + metrics.getHeight() + 10);
			};
		};
		credentials.setLayout(new BoxLayout(credentials, BoxLayout.Y_AXIS));
		
		credentials.setPreferredSize(new Dimension(800, 145));
		JSeparator seperator = new JSeparator();
		seperator.setBackground(Color.GREEN);
		seperator.setPreferredSize(new Dimension(800, 20));
		credentials.add(seperator);
		credentials.setBackground(Color.WHITE);

		credentials.add(createInput("Username:", FONT, false));
		credentials.add(createInput("Password:", FONT, true));
		
		JPanel configurePanel = new JPanel();
		configurePanel.setBackground(Color.WHITE);
		
		JButton configure = new JButton("configure");
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
			}
		});
		
		frame.setVisible(true);
	
		while(shouldBlockExecution);
	}
	
	private static JComponent createInput(String title, final Font FONT, boolean isPassword) {
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setMaximumSize(new Dimension(800, 35));
		panel.setPreferredSize(new Dimension(800, 35));
		JTextField label = new JTextField(title);
		label.setFont(FONT.deriveFont(Font.BOLD));
		label.setBackground(Color.WHITE);
		label.setEditable(false);
		label.setBorder(null);
		panel.add(label);
		JTextField input = isPassword ? new JPasswordField() : new JTextField();
		input.setFont(FONT);
		input.setEditable(true);
		input.setBackground(Color.WHITE);
		input.setPreferredSize(new Dimension(350, 25));
		panel.add(input);
		
		return panel;
	}
	
}

