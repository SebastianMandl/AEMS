package at.htlgkr.aems.raspberry;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class ConsoleView {
	
	private static final Font FONT = new Font("Arial", Font.PLAIN, 16);

	private static JTextArea area;
	private static Thread thread;
	private static volatile boolean shouldTerminate = false;
	
	public static JPanel generateView() {
		area = new JTextArea();
		area.setFont(FONT);
		area.setEditable(false);
		area.setBackground(Color.WHITE);
		
		JScrollPane pane = new JScrollPane(area);
		pane.getHorizontalScrollBar().setUnitIncrement(10);
		pane.getVerticalScrollBar().setUnitIncrement(10);
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(pane, BorderLayout.CENTER);
		return panel;
	}
	
	public static void readFromConsole() {
		if(thread == null) {
			thread = new Thread(() -> {
				while(!shouldTerminate) {
					try(BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
						area.append(reader.readLine());
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			});
			thread.start();
		} // if thread is not null then the task is already running
	}
	
	public static void stopReadingFromConsole() {
		shouldTerminate = true;
		thread = null;
	}
	
}
