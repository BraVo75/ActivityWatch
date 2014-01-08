package org.bravo.activitywatch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.swing.JOptionPane;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.bravo.activitywatch.entity.AWStore;
import org.bravo.activitywatch.entity.Activity;

/**
 * Singleton
 * 
 * @author volkerb
 * 
 */
public class CoreController {

	private static final CoreController instance = new CoreController();

	private CoreController() {
		loadSettings();
	};

	public static final CoreController getInstance() {
		return instance;
	}

	private Settings settings;
	private AWStore store;
	private String storePath;
	
	private void loadSettings() {

		if( storePath == null)
		{
			storePath = System.getProperty("user.home")+File.separator+ActivityWatch.AWSTORE_XML;
		}
		
		store = new AWStore();
		settings = new Settings();
		store.setSettings(settings);

		store.setActivities(new ArrayList<Activity>());

		// StatusBar.setMessage("Loading Activities...");
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(AWStore.class);
			Unmarshaller um = context.createUnmarshaller();
			store = (AWStore) um.unmarshal(new FileReader(storePath));
		} catch (JAXBException e) {
			JOptionPane
					.showMessageDialog(null, e.getLocalizedMessage(),
							"Error while loading activities",
							JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			System.out
					.println("No AWStore.xml File found. Creating new activities...");
		}
		if (store.getVersion() != ActivityWatch.AWSTORE_VERSION) {
			migrateStore();
			// showWelcomeMessage();
		}
		store.setVersion(ActivityWatch.AWSTORE_VERSION);

		if (store.getSettings() != null) {
			// statusBar.setVisible(store.getSettings().isStatusBarVisible());
			// item_showStatusBar.setSelected(store.getSettings().isStatusBarVisible());
			// this.setAlwaysOnTop(store.getSettings().isAlwaysOnTop());
			// item_alwaysOnTop.setSelected(store.getSettings().isAlwaysOnTop());
		} else {
			store.setSettings(settings);
		}
		System.out.println("Loaded " + store.getActivitiyList().size()
				+ " activities.");
		// StatusBar.setMessage("Loaded "+store.getActivitiyList().size()+" activities.",
		// 3000);
	}
	
	private void migrateStore() {
		if( store.getVersion() == 0 ) {
			store.getSettings().setCountersVisible(true);
			store.getSettings().setStatusBarVisible(true);
			store.getSettings().setAlwaysOnTop(false);
		}
	}
	
//	public void stopAllTimers() {
//		for (TimerController t : activityTimers) {
//			if (t.isTimerActive()) {
//				t.stopTimer();
//			}
//		}
//	}

	
//	for(Activity a : store.getActivitiyList()) {
//	GregorianCalendar tcal = new GregorianCalendar();
//	tcal.setTime(a.getStartDate());
//	if( displayedDate.get(Calendar.DAY_OF_MONTH) == tcal.get(Calendar.DAY_OF_MONTH)
//		&& displayedDate.get(Calendar.MONTH) == tcal.get(Calendar.MONTH)
//		&& displayedDate.get(Calendar.YEAR) == tcal.get(Calendar.YEAR)) {
//			Timer timer = new Timer(a);
//			activityTimers.add(timer);
//			activitiesLayout.getChildren().add(timer);
//		}
//}
//	
//	public void setActivityTimers(List<TimerController> activityTimers) {
//		this.activityTimers = activityTimers;
//	}
//
//	public void addActivityTimer(TimerController timer) {
//		this.activityTimers.add(timer);
//	}
//
//	public void deleteActivityTimer(TimerController timer) {
//		if (timer.isTimerActive()) {
//			timer.stopTimer();
//		}
//		activityTimers.remove(timer);
//	}
	
	public List<Activity> getActivities() {
		return store.getActivitiyList();
	}

	public void setActivities(List<Activity> activities) {
		store.setActivities(activities);
	}
}
