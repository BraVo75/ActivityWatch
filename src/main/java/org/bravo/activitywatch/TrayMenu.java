package org.bravo.activitywatch;

import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import org.bravo.activitywatch.Messages.Keys;
import org.bravo.activitywatch.entity.ActivityDO;

/**
 * Adds an application icon to the system tray menu.
 * Iconify while hitting the close button is not supported yet because of platform dependent problems.
 * 
 * @author Volker Braun
 *
 */
public class TrayMenu {
	
	private static final TrayMenu instance = new TrayMenu();
	private static final String AW_ICON = "images/aw-logo-64.png";
	private static final String AW_RUNNING_ICON = "images/aw-logo-running-64.png";
	
	private TrayMenu() {
	}
	
	public static final TrayMenu getInstance() {
		return instance;
	}
	
	private TrayIcon trayIcon;
	private Messages messages = Messages.getInstance();
	private ActivityManager activityManager = ActivityManager.getInstance();
	private PopupMenu popup;
	private MenuItem stopItem;
	private MenuItem showItem;
	private MenuItem closeItem;
	
	public enum Icon {
		STOPPED, RUNNING;
	}
	
	public void setTrayIcon(Icon icon) {
		if (SystemTray.isSupported()) {
			if (icon.equals(Icon.STOPPED)) {
				java.awt.Image image = Toolkit.getDefaultToolkit().getImage(getClass().getResource(AW_ICON));
				trayIcon.setImage(image);
			}
			if (icon.equals(Icon.RUNNING)) {
				java.awt.Image image = Toolkit.getDefaultToolkit().getImage(getClass().getResource(AW_RUNNING_ICON));
				trayIcon.setImage(image);
			}
		}
	}
	
	public void createTrayIcon(final Stage stage) {
		if (SystemTray.isSupported()) {
			// get the SystemTray instance
			SystemTray tray = SystemTray.getSystemTray();
			// load an image
			java.awt.Image image = Toolkit.getDefaultToolkit().getImage(getClass().getResource(AW_ICON));
			stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent t) {
					javafx.application.Platform.runLater(new Runnable() {
						@Override
						public void run() {
//							if (SystemTray.isSupported()) {
//								stage.hide();
//								stage.setIconified(true);
//								activityManager.saveActivities();
//								showProgramIsMinimizedMsg(); // does not work on mac
//							} else {
								javafx.application.Platform.exit();
//							}
						}
					});
				}
			});
			// create a action listener to listen for default action executed on the tray icon
			final ActionListener closeListener = new ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					javafx.application.Platform.exit();
				}
			};

			ActionListener showListener = new ActionListener() {
				@Override
				public void actionPerformed(java.awt.event.ActionEvent e) {
					javafx.application.Platform.runLater(new Runnable() {
						@Override
						public void run() {
							stage.setIconified(false);
							stage.show();
							stage.toFront();
						}
					});
				}
			};
			
			ActionListener stopListener = new ActionListener() {
				@Override
				public void actionPerformed(final java.awt.event.ActionEvent e) {
					javafx.application.Platform.runLater(new Runnable() {
						@Override
						public void run() {
							activityManager.stopActivity();
							TrayMenu.getInstance().updateTrayMenu();
						}
					});
				}
			};
			
			popup = new PopupMenu();

			
			stopItem = new MenuItem(messages.get(Keys.TRAY_MENU_STOP));
			stopItem.addActionListener(stopListener);
			
			showItem = new MenuItem(messages.get(Keys.TRAY_MENU_SHOW));
			showItem.addActionListener(showListener);
			
			closeItem = new MenuItem(messages.get(Keys.TRAY_MENU_QUIT));
			closeItem.addActionListener(closeListener);
			
			trayIcon = new TrayIcon(image, "ActivityWatch", popup);
			trayIcon.addActionListener(showListener);
			trayIcon.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(java.awt.event.MouseEvent e) {
//					System.out.println(e.getButton());
					javafx.application.Platform.runLater(new Runnable() {
						@Override
						public void run() {
//							System.out.println(23);
							stage.setIconified(false);
							stage.show();
							stage.toFront();
						}
					});
				}
			});
			
			try {
				tray.add(trayIcon);
			} catch (AWTException e) {
				System.err.println("Could not add application to system tray.");
			}
			updateTrayMenu();
		}
	}

	private void addDefaultMenuItems() {
		if (activityManager.getRunningActivity() != null) {
			stopItem.setEnabled(true);
		}
		else {
			stopItem.setEnabled(false);
		}
		
		popup.addSeparator();
		popup.add(stopItem);
		popup.add(showItem);
		popup.add(closeItem);
	}

	public void updateTrayMenu() {
		popup.removeAll();
		ActionListener toggleActivity = new ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent e) {
				javafx.application.Platform.runLater(new Runnable() {
					@Override
					public void run() {
						activityManager.selectActivity(Long.valueOf(e.getActionCommand()));
						activityManager.startActivity(Long.valueOf(e.getActionCommand()));
						TrayMenu.getInstance().updateTrayMenu();
					}
				});
			}
		};
		
		LocalDate selectedDate = activityManager.getRunningActivity() != null ? activityManager.getRunningActivity().getStartDate() : LocalDate.now();
		MenuItem dateLabel = new MenuItem(selectedDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)));
		dateLabel.setEnabled(false);
		popup.add(dateLabel);
		for (ActivityDO a : activityManager.getActivities(selectedDate)) {
			MenuItem item = new MenuItem(a.getActivity().getName());
			item.setActionCommand(a.getActivity().getId().toString());
			item.addActionListener(toggleActivity);
			if (a.getRunningProperty().getValue()) {
				item.setLabel("> "+item.getLabel());
			}
			popup.add(item);
		}
		addDefaultMenuItems();
		
		if (activityManager.getRunningActivity() != null) {
			setTrayIcon(Icon.RUNNING);
		}
		else {
			setTrayIcon(Icon.STOPPED);
		}
	}
	
	public void showProgramIsMinimizedMsg() {
		trayIcon.displayMessage("Message.", "Application is still running.You can access from here.", TrayIcon.MessageType.INFO);
	}
}
