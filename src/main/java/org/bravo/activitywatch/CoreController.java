package org.bravo.activitywatch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.bravo.activitywatch.entity.AWStore;
import org.bravo.activitywatch.entity.Activity;

/**
 * Singleton
 * 
 * @author Volker Braun
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
					.println("First run. Initializing...");
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
	}
	
	private void migrateStore() {
		if( store.getVersion() == 0 ) {
			System.out.println("Migrating from version 0 to 1");
			store.getSettings().setCountersVisible(true);
			store.getSettings().setStatusBarVisible(true);
			store.getSettings().setAlwaysOnTop(false);
		}
		
		if( store.getVersion() == 1) {
			System.out.println("Migrating from version 1 to 2");
			long id = 1;
			for (Activity a : store.getActivitiyList()) {
				a.setId(id);
				id++;
			}
		}
	}
	
	public List<Activity> getActivities() {
		return store.getActivitiyList();
	}

	public void setActivities(List<Activity> activities) {
		store.setActivities(activities);
	}
	
	public void saveActivities()
	{
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(AWStore.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			Writer w = null;
			try {
				w = new FileWriter(storePath);
				m.marshal(store, w);
			} catch (IOException e) {
				System.err.println("Error while saving activities!");
				e.printStackTrace();
			}
			finally {
				if( w != null ) {
					try {
						w.close();
						System.out.println("Saved "+store.getActivitiyList().size()+" activities.");
					}
					catch (Exception e){
						e.printStackTrace();
						System.err.println("Error while saving activities!");
					}
				}
			}
		} catch (JAXBException e1) {
			e1.printStackTrace();
			System.err.println("Error while saving activities!");
		}
	}
}
