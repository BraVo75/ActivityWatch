package org.bravo.activitywatch;

import javax.swing.UIManager;

public class WindowUtils {
	public static void setNativeLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e) {
			StatusBar.setMessage("Could not set native look and feel", 3000);
			System.err.println("Error setting Look and Feel: "+e);
		}
	}

	public static void setJavaLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		}
		catch (Exception e) {
			StatusBar.setMessage("Could not set java look and feel", 3000);
			System.err.println("Error setting Look and Feel: "+e);
		}
	}

	public static void setMotifLookAndFeel() {
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
		}
		catch (Exception e) {
			StatusBar.setMessage("Could not set motif look and feel", 3000);
			System.err.println("Error setting Look and Feel: "+e);
		}
	}
}
