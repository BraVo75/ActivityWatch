package org.bravo.activitywatch;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.Timer;

@Deprecated
public class StatusBar extends Container {
	private static final long serialVersionUID = 1L;
	private static Timer t;
	private static JLabel status = new JLabel("", JLabel.LEFT);

	public StatusBar() {
		this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
//		this.add(new JSeparator(SwingConstants.HORIZONTAL));
		this.add(status);
		setPreferredSize(new Dimension(150,16));
		t = new Timer(5000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clear();
			}
		});
	}
	
	public static void clear() {
		setMessage("");
	}
	
	public static void setMessage(String message) {
		t.stop();
		status.setText(message);
	}
	
	public static void setMessage(String message, int delay) {
		setMessage(message);
		t.setInitialDelay(delay);
		t.start();
	}
}
