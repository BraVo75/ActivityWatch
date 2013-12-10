/**
 * 
 */
package org.bravo.activitywatch;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.TitledBorder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.bravo.activitywatch.entity.AWStore;
import org.bravo.activitywatch.entity.Activity;
import org.jdesktop.swingx.JXDatePicker;

/**
 * @author Volker Braun
 */
public class ActivityWatchSwing extends JFrame implements ActionListener {
	private static final long serialVersionUID = 2930543044324867826L;

	private static final String PRG_NAME = "ActivityWatch";
	private static final String PRG_VERSION = "0.9";
	private static final String AWSTORE_XML = "AWStore.xml";
	private static final int AWSTORE_VERSION = 1;
	
	private static final String PATH_STOPICON = "images/media-playback-stop.png";
	private static final String PATH_ARROWLEFT = "images/go-previous-view.png";
	private static final String PATH_ARROWRIGHT = "images/go-next-view.png";
	private static final String PATH_APPICON = "images/activitywatch-icon_16x16.png";
	
	private static final String ACTION_QUIT = "Quit";
	private static final String ACTION_SAVE = "Save";
	private static final String ACTION_SHOWSTATUSBAR = "Show status bar";
	private static final String ACTION_SHOWCOUNTERS = "Show counters";
	private static final String ACTION_ALWAYSONTOP = "Always on top";

	public static void main(String args[]) throws JAXBException, IOException  {
		String storePath = null;
		for(String arg : args)
		{
			if(arg.startsWith("--settings-file="))
			{
				storePath = arg.replaceFirst("--settings-file=", "");;
			}
		}
		WindowUtils.setNativeLookAndFeel();
		if( storePath == null)
		{
			storePath = System.getProperty("user.home")+File.separator+AWSTORE_XML;
		}

		new ActivityWatchSwing(storePath);
	}

	private String storePath;
	
	private JMenuBar menuBar = new JMenuBar();
	private JMenu menu_help;
	private JMenu menu_options;
//	private JMenu menu_view;
	private JMenu menu_file;
	private JMenuItem item_about;
	private JMenuItem item_showCounters;
	private JMenuItem item_showStatusBar;
	private JMenuItem item_alwaysOnTop;
	private JMenuItem item_quit;
	private JMenuItem item_save;
	
	private JTextField txt_newActivity = new JTextField();
	private JPanel addPanel = new JPanel();
	private Container pane = new Container();
	private List<ActivityTimer> activityTimers;
	private JButton btn_stop = new JButton("Stop timer");
	private JXDatePicker datePicker = new JXDatePicker();
	private JButton btn_nextDate;
	private JButton btn_prevDate;
	private Container activityPane = new Container();
	
	private JButton btn_plus5 = new JButton("+5");
	private JButton btn_plus20 = new JButton("+20");
	private JButton btn_plus60 = new JButton("+60");
	private JButton btn_minus5 = new JButton("-5");
	private JButton btn_minus20 = new JButton("-20");
	private JButton btn_minus60 = new JButton("-60");
	private Container correctionPane = new Container();
	
	private AWStore store = new AWStore();
	private Settings settings;
	private GregorianCalendar displayedDate = new GregorianCalendar();
	
	private ActivityTimer activeTimer;
	
	private WindowClosingAdapter windowClosingAdapter;
	private StatusBar statusBar = new StatusBar();
	
	private final ImageIcon icon_stop;
	private final ImageIcon icon_left;
	private final ImageIcon icon_right;
	private final ImageIcon icon_application;
	
	@Deprecated
	public ActivityWatchSwing(String storePath) throws JAXBException, IOException {

		icon_application = new ImageIcon(ActivityWatchSwing.class.getResource(PATH_APPICON));

		this.storePath = storePath;
		settings = new Settings();
		settings.setStatusBarVisible(true);
		settings.setCountersVisible(true);
		store.setSettings(settings);
		
		this.setTitle(PRG_NAME);
		this.setIconImage(icon_application.getImage());
		
		setupSystemTray();
		
		setupMenu();
		
		BoxLayout layout = new BoxLayout(pane, BoxLayout.Y_AXIS);
		pane.setLayout(layout);
		Container dateSelect = new Container();
		dateSelect.setLayout(new FlowLayout());
		datePicker.setDate(displayedDate.getTime());
		icon_left = new ImageIcon(ActivityWatchSwing.class.getResource(PATH_ARROWLEFT));
		icon_right = new ImageIcon(ActivityWatchSwing.class.getResource(PATH_ARROWRIGHT));
		btn_prevDate = new JButton(icon_left);
		btn_nextDate = new JButton(icon_right);
		dateSelect.add(btn_prevDate);
		dateSelect.add(datePicker, Label.CENTER);
		dateSelect.add(btn_nextDate);

		Container stopButton = new Container();
		stopButton.setLayout(new FlowLayout());
		icon_stop = new ImageIcon(ActivityWatchSwing.class.getResource(PATH_STOPICON));
		btn_stop.setIcon(icon_stop);
		btn_stop.setAlignmentX(Component.CENTER_ALIGNMENT);
		btn_stop.setPreferredSize(new Dimension(150,24));
		btn_stop.setToolTipText("Stop running activity");
		btn_stop.setEnabled(false);
		stopButton.add(btn_stop);
		
		addPanel.setLayout(new FlowLayout());
		txt_newActivity.setPreferredSize(new Dimension(340,20));
		TitledBorder title = BorderFactory.createTitledBorder("Add new activity");
		addPanel.setBorder(title);
		addPanel.add(txt_newActivity);
		
		txt_newActivity.addActionListener(this);
		btn_stop.addActionListener(this);
		btn_prevDate.addActionListener(this);
		btn_nextDate.addActionListener(this);

		btn_plus5.setToolTipText("Add 5 minutes to the running activity");
		btn_plus20.setToolTipText("Add 20 minutes to the running activity");
		btn_plus60.setToolTipText("Add 60 minutes to the running activity");
		btn_minus5.setToolTipText("Subtract 5 minutes from the running activity");
		btn_minus20.setToolTipText("Subtract 20 minutes from the running activity");
		btn_minus60.setToolTipText("Subtract 60 minutes from the running activity");
		
		pane.add(dateSelect);
		pane.add(stopButton);
		pane.add(addPanel);
		pane.add(activityPane);

		store.setActivities(new ArrayList<Activity>());
		loadActivities();
		
		if( this.activityTimers == null ) {
			this.activityTimers = new ArrayList<ActivityTimer>();
		}
		
		addCorrectionPane();
		pane.add(statusBar, BorderLayout.SOUTH);

		if(store != null && store.getActivitiyList() != null) {
			showActivities();
		}
		
		this.add(pane);
		this.pack();
		
		datePicker.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopTimers();
				displayedDate.setTime(datePicker.getDate());
				showActivities();
			}
		});

		txt_newActivity.requestFocusInWindow();
		setVisible(true);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		windowClosingAdapter = new WindowClosingAdapter(true, store, storePath);
		this.addWindowListener(windowClosingAdapter);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		if( event.getSource().equals(item_about)) {
			showAboutDialog();
		}
		else if (event.getActionCommand().equals(ACTION_QUIT)) {
			exitApp();
		}
		else if (event.getActionCommand().equals(ACTION_SAVE)) {
			windowClosingAdapter.saveActivities(store);
		}
		else if (event.getActionCommand().equals(ACTION_SHOWCOUNTERS)) {
			showCounters(item_showCounters.isSelected());
		}
		else if (event.getActionCommand().equals(ACTION_ALWAYSONTOP)) {
			store.getSettings().setAlwaysOnTop(item_alwaysOnTop.isSelected());
			this.setAlwaysOnTop(store.getSettings().isAlwaysOnTop());
		}
		else if (event.getActionCommand().equals(ACTION_SHOWSTATUSBAR)) {
			store.getSettings().setStatusBarVisible(item_showStatusBar.isSelected());
			statusBar.setVisible(store.getSettings().isStatusBarVisible());
			this.pack();
		}
		else if( event.getSource().equals(txt_newActivity)) {
			addActivity();
		}
		else if (event.getSource().equals(btn_prevDate)) {
			stopTimers();
			displayedDate.set(Calendar.DAY_OF_MONTH, displayedDate.get(Calendar.DAY_OF_MONTH) -1 );
//			btn_date.setText(sdf.format(displayedDate.getTime()));
			datePicker.setDate(displayedDate.getTime());
			showActivities();
		}
		else if (event.getSource().equals(btn_nextDate)) {
			stopTimers();
			displayedDate.set(Calendar.DAY_OF_MONTH, displayedDate.get(Calendar.DAY_OF_MONTH) +1 );
//			btn_date.setText(sdf.format(displayedDate.getTime()));
			datePicker.setDate(displayedDate.getTime());
			showActivities();
		}
		else if (event.getSource().equals(btn_plus5)) {
			activeTimer.minutesOffset(5);
		}
		else if (event.getSource().equals(btn_plus20)) {
			activeTimer.minutesOffset(20);
		}
		else if (event.getSource().equals(btn_plus60)) {
			activeTimer.minutesOffset(60);
		}
		else if (event.getSource().equals(btn_minus5)) {
			activeTimer.minutesOffset(-5);
		}
		else if (event.getSource().equals(btn_minus20)) {
			activeTimer.minutesOffset(-20);
		}
		else if (event.getSource().equals(btn_minus60)) {
			activeTimer.minutesOffset(-60);
		}
		else if (event.getSource().equals(btn_stop)) {
			stopTimers();
		}
		else {
			for(ActivityTimer t : activityTimers) {
				if(event.getSource().equals(t.btn_remove)) {
					removeActivity(t);
					break;
				}
				else if(event.getSource().equals(t.btn_rename)) {
					renameActivity(t);
				}
				else if(event.getSource().equals(t.btn_activity)) {
					if( !t.equals(activeTimer) ) {
						stopTimers();
						t.start();
						btn_stop.setEnabled(true);
						enableCorrectionPane(true);
						activeTimer = t;
					}
					else {
						stopTimers();
						
					}
				}
			}
		}
	}
	
	private void showAboutDialog() {
		JOptionPane.showMessageDialog(this, PRG_NAME+" v"+PRG_VERSION+"\n\nCopyright 2008-2012 Volker Braun");
	}

	private void loadActivities() {
		StatusBar.setMessage("Loading Activities...");
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(AWStore.class);
			Unmarshaller um = context.createUnmarshaller();
			store = (AWStore) um.unmarshal(new FileReader(storePath));
		} catch (JAXBException e) {
			JOptionPane.showMessageDialog(null, e.getLocalizedMessage(),"Error while loading activities", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			System.out.println("No AWStore.xml File found. Creating new activities...");
		}
		if( store.getVersion() != AWSTORE_VERSION ) {
			migrateStore();
			showWelcomeMessage();
		}
		store.setVersion(AWSTORE_VERSION);
		
		if( store.getSettings() != null ) {
			statusBar.setVisible(store.getSettings().isStatusBarVisible());
			item_showStatusBar.setSelected(store.getSettings().isStatusBarVisible());
			this.setAlwaysOnTop(store.getSettings().isAlwaysOnTop());
			item_alwaysOnTop.setSelected(store.getSettings().isAlwaysOnTop());
		}
		else
		{
			store.setSettings(settings);
		}
		StatusBar.setMessage("Loaded "+store.getActivitiyList().size()+" activities.", 3000);
	}
	
	private void removeActivities() {
		pane.remove(activityPane);
	}
	
	private void removeActivity(ActivityTimer timer) {
		if( timer.equals(activeTimer)) {
			stopTimers();
		}
		store.getActivitiyList().remove(timer.getActivity());
		activityPane.remove(timer);
		this.pack();
		windowClosingAdapter.saveActivities(store);
	}
	
	private void showActivities() {
		removeActivities();
		activityPane = new Container();
		activityPane.setLayout(new BoxLayout(activityPane, BoxLayout.Y_AXIS));
		activityTimers = new ArrayList<ActivityTimer>();
		for(Activity a : store.getActivitiyList()) {
			GregorianCalendar tcal = new GregorianCalendar();
			tcal.setTime(a.getStartDate());
			if( displayedDate.get(Calendar.DAY_OF_MONTH) == tcal.get(Calendar.DAY_OF_MONTH)
				&& displayedDate.get(Calendar.MONTH) == tcal.get(Calendar.MONTH)
				&& displayedDate.get(Calendar.YEAR) == tcal.get(Calendar.YEAR)) {
					ActivityTimer timer = new ActivityTimer(a, this);
					activityTimers.add(timer);
					activityPane.add(timer);
				}
		}
		showCounters(store.getSettings().isCountersVisible());
		pane.add(activityPane, pane.getComponents().length -2);
		this.pack();
	}
	
	private void addCorrectionPane()
	{
		correctionPane.setLayout(new GridLayout(1, 6));
		correctionPane.add(btn_minus60);
		correctionPane.add(btn_minus20);
		correctionPane.add(btn_minus5);
		correctionPane.add(btn_plus5);
		correctionPane.add(btn_plus20);
		correctionPane.add(btn_plus60);

		btn_minus5.addActionListener(this);
		btn_minus20.addActionListener(this);
		btn_minus60.addActionListener(this);
		btn_plus5.addActionListener(this);
		btn_plus20.addActionListener(this);
		btn_plus60.addActionListener(this);

		correctionPane.validate();
		enableCorrectionPane(false);
		pane.add(correctionPane);
	}
	
	private void enableCorrectionPane(boolean enable) {
		btn_minus20.setEnabled(enable);
		btn_minus5.setEnabled(enable);
		btn_minus60.setEnabled(enable);
		btn_plus20.setEnabled(enable);
		btn_plus5.setEnabled(enable);
		btn_plus60.setEnabled(enable);
	}
	
	private void stopTimers() {
		for(ActivityTimer t : activityTimers) {
			t.stop();
		}
		enableCorrectionPane(false);
		btn_stop.setEnabled(false);
		activeTimer = null;
	}
	
	private void renameActivity(ActivityTimer timer) {
		String newName = (String)JOptionPane.showInputDialog(this,"Enter a new name for the activity", timer.getActivity().getName());//, "Rename activity",JOptionPane.QUESTION_MESSAGE);
		if( newName != null && !newName.equals("")) {
			timer.renameActivity(newName);
		}
		this.pack();
		windowClosingAdapter.saveActivities(store);
	}
	
	private void showCounters(boolean visible) {
		item_showCounters.setSelected(visible);
		for( ActivityTimer t : activityTimers ) {
			t.displayCounter(visible);
		}
		store.getSettings().setCountersVisible(visible);
	}
	
	private void setupMenu() {
		menu_help = new JMenu("Help");
		menu_help.setMnemonic(KeyEvent.VK_H);
		menu_options = new JMenu("Options");
		menu_options.setMnemonic(KeyEvent.VK_O);
//		menu_view = new JMenu("View");
//		menu_view.setMnemonic(KeyEvent.VK_V);
		menu_file = new JMenu("File");
		menu_file.setMnemonic(KeyEvent.VK_F);
		
		menuBar.add(menu_file);
		menuBar.add(menu_options);
//		menuBar.add(menu_view);
		menuBar.add(Box.createHorizontalGlue());
		menuBar.add(menu_help);
		
		item_save = new JMenuItem(ACTION_SAVE);
		item_save.setMnemonic(KeyEvent.VK_S);
		item_save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		item_save.addActionListener(this);

		item_quit = new JMenuItem(ACTION_QUIT);
		item_quit.setMnemonic(KeyEvent.VK_Q);
		item_quit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
		item_quit.addActionListener(this);
		
		item_showCounters = new JCheckBoxMenuItem(ACTION_SHOWCOUNTERS);
		item_showCounters.setSelected(true);
		item_showCounters.addActionListener(this);
		item_showStatusBar = new JCheckBoxMenuItem(ACTION_SHOWSTATUSBAR);
		item_showStatusBar.setSelected(true);
		item_showStatusBar.addActionListener(this);
		item_alwaysOnTop = new JCheckBoxMenuItem(ACTION_ALWAYSONTOP);
		item_alwaysOnTop.setSelected(false);
		item_alwaysOnTop.addActionListener(this);
		
		item_about = new JMenuItem("About");
		item_about.setMnemonic(KeyEvent.VK_A);
		item_about.addActionListener(this);

		menu_file.add(item_save);
		menu_file.add(item_quit);
		menu_options.add(item_showCounters);
		menu_options.add(item_showStatusBar);
		menu_options.add(item_alwaysOnTop);
		menu_help.add(item_about);
		this.setJMenuBar(menuBar);
	}
	
	private void migrateStore() {
		if( store.getVersion() == 0 ) {
			store.getSettings().setCountersVisible(true);
			store.getSettings().setStatusBarVisible(true);
			store.getSettings().setAlwaysOnTop(false);
		}
	}
	
	private void showWelcomeMessage() {
		StringBuilder m = new StringBuilder();
		m.append("Welcome to "+PRG_NAME+"!\n\n");
		m.append("New features in Version "+PRG_VERSION+":\n");
		m.append(getActualFeatureList()+"\n");
		
		String hostname = "";
		try {
			hostname = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e1) {
			// just shut up
		}
		if( hostname.startsWith("atbgh")) {
			 m.append("If you need any assistance or if you like this software,\njust come to my office - i like presents! ;-)\n\n");
		}
		
		m.append("Greets,\nVolker Braun");
		JOptionPane.showMessageDialog(this, m);
	}
	
	private String getActualFeatureList() {
		return
			"- added calendar for easy date selection\n" +
			"- minimize to system tray\n"
			;
	}
	
	private void addActivity() {
		if( !isToday(displayedDate) ) {
			int confirm = JOptionPane.showConfirmDialog(this,"Do you really want to add a new activity to an other day than today?\n", "Add new activity", JOptionPane.YES_NO_OPTION);
			if( confirm == JOptionPane.NO_OPTION ) {
				txt_newActivity.setText("");
				return;
			}
		}
		
		String text = txt_newActivity.getText();
		if(text.equals("")) {
			return;
		}
		txt_newActivity.setText("");
		if( activeTimer != null ) {
			stopTimers();
		}
		
		Activity activity = new Activity();
		activity.setName(text);
		activity.setStartDate(displayedDate.getTime());
		store.getActivitiyList().add(activity);
		ActivityTimer timer = new ActivityTimer(activity, this);
		timer.displayCounter(store.getSettings().isCountersVisible());
		activityTimers.add(timer);
		activityPane.add(timer);
		timer.start();
		activeTimer = timer;
		btn_stop.setEnabled(true);
		enableCorrectionPane(true);
		this.pack();
		windowClosingAdapter.saveActivities(store);
		txt_newActivity.requestFocusInWindow();
	}

	private boolean isToday(GregorianCalendar cal) {
		Calendar today = Calendar.getInstance();
		if(cal.get(Calendar.YEAR) == today.get(Calendar.YEAR)
				&& cal.get(Calendar.MONTH) == today.get(Calendar.MONTH)
				&& cal.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)
				) {
			return true;
		}
		return false;
	}

	private void setupSystemTray() {
        if (!SystemTray.isSupported()) {
            return;
        }
        final PopupMenu trayPopup = new PopupMenu();
        final TrayIcon trayIcon = new TrayIcon(icon_application.getImage());
        final SystemTray tray = SystemTray.getSystemTray();
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip(PRG_NAME);
        
        MenuItem aboutItem = new MenuItem("About");
        MenuItem exitItem = new MenuItem("Exit");
        trayPopup.add(aboutItem);
        trayPopup.add(exitItem);
        trayIcon.setPopupMenu(trayPopup);
        
        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tray.remove(trayIcon);
                exitApp();
            }
        });

        aboutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	showAboutDialog();
            }
        });
        
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            // just shut up
        }
        
        trayIcon.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	toggleApp();
            }
        });
	}
	
	private void toggleApp() {
		if( this.isShowing()) {
			this.setVisible(false);
		}
		else {
			this.setVisible(true);
		}
	}
	
	private void exitApp() {
		this.processWindowEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}
}
