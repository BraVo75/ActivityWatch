package org.bravo.activitywatch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.bravo.activitywatch.Messages.Keys;
import org.bravo.activitywatch.entity.AWStore;
import org.bravo.activitywatch.entity.AWVersion;
import org.bravo.activitywatch.entity.AWVersions;
import org.bravo.activitywatch.entity.Activity;
import org.bravo.activitywatch.entity.Settings;
import org.bravo.activitywatch.entity.Settings.TimeFormat;
import org.controlsfx.dialog.Dialogs;

/**
 * Singleton
 * 
 * @author Volker Braun
 * 
 */
public class CoreController {

	private static final String UPDATE_URL = "http://bra-vo.de/activitywatch/versions.xml";
	private static final String DEFAULT_DOWNLOAD_URL = "http://bra-vo.de/activitywatch";
	private static final CoreController instance = new CoreController();
	private Messages messages = Messages.getInstance();
	private SettingsManager settingsManager = SettingsManager.getInstance();
	
	private AWVersion awVersion;
	private String updateMessage;

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
		
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(AWStore.class);
			Unmarshaller um = context.createUnmarshaller();
			store = (AWStore) um.unmarshal(new FileReader(storePath));
		} catch (JAXBException e) {
			// error message is shown later
		} catch (FileNotFoundException e) {
			initFirstRun();
		}
		if (store == null) {
			Dialogs.create()
				.title(messages.get(Messages.Keys.ERROR_TITLE))
				.message(messages.get(Messages.Keys.ERROR_DATABASE_CORRUPTED))
				.showError();
			Platform.exit();
			return;
		}
		if (store.getVersion() != ActivityWatch.AWSTORE_VERSION) {
			migrateStore();
//			showWelcomeMessage();
		}
		store.setVersion(ActivityWatch.AWSTORE_VERSION);

		if (store.getSettings() != null) {
			settingsManager.setTimeFormat(store.getSettings().getTimeFormat());
			// statusBar.setVisible(store.getSettings().isStatusBarVisible());
			// item_showStatusBar.setSelected(store.getSettings().isStatusBarVisible());
			// this.setAlwaysOnTop(store.getSettings().isAlwaysOnTop());
			// item_alwaysOnTop.setSelected(store.getSettings().isAlwaysOnTop());
		} else {
			store.setSettings(settings);
		}
	}

	private void initFirstRun() {
		store = new AWStore();
		settings = new Settings();
		settings.setAlwaysOnTop(false);
		settings.setCountersVisible(true);
		settings.setStatusBarVisible(false);
		settings.setTimeFormat(TimeFormat.TIME);
		store.setSettings(settings);
		store.setVersion(ActivityWatch.AWSTORE_VERSION);
		store.setActivities(new ArrayList<Activity>());
	}
	
	private void migrateStore() {
		if( store.getVersion() == 0 ) {
			store.getSettings().setCountersVisible(true);
			store.getSettings().setStatusBarVisible(false);
			store.getSettings().setAlwaysOnTop(false);
			store.setVersion(1);
		}
		
		if( store.getVersion() == 1) {
			long id = 1;
			for (Activity a : store.getActivitiyList()) {
				a.setId(id);
				id++;
			}
			store.setVersion(2);
		}
		
		if( store.getVersion() == 2) {
			store.getSettings().setTimeFormat(TimeFormat.TIME);
			store.setVersion(3);
		}
	}
	
	public List<Activity> getActivities() {
		return store == null ? null : store.getActivitiyList();
	}

	public void setActivities(List<Activity> activities) {
		store.setActivities(activities);
	}
	
	public void saveActivities()
	{
		store.getSettings().setTimeFormat(settingsManager.getTimeFormat());
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
				e.printStackTrace();
				showSaveDatabaseError();
			}
			finally {
				if( w != null ) {
					try {
						w.close();
					}
					catch (Exception e){
						e.printStackTrace();
						showSaveDatabaseError();
					}
				}
			}
		} catch (JAXBException e1) {
			e1.printStackTrace();
			showSaveDatabaseError();
		}
	}

	private void showSaveDatabaseError() {
		Dialogs.create()
			.title(messages.get(Messages.Keys.ERROR_TITLE))
			.message(messages.get(Messages.Keys.ERROR_SAVING_DATABASE))
			.showError();
	}
	
	public String getNewestVersionInfo() {
		if (awVersion == null) {
			checkVersion();
		}
		return updateMessage;
	}
	
	public boolean isUpdateAvailable() {
		if (awVersion == null) {
			checkVersion();
		}
		return awVersion != null;
	}
	
	private void checkVersion() {
		URL url;
		URLConnection connection;
		
		AWVersions versions = new AWVersions();
		JAXBContext context;

		try {
			url = new URL(UPDATE_URL);
			connection = url.openConnection();
			context = JAXBContext.newInstance(AWVersions.class);
			Unmarshaller um = context.createUnmarshaller();
			versions = (AWVersions) um.unmarshal(connection.getInputStream());
		} catch (IOException | JAXBException e) {
			updateMessage = messages.get(Keys.UPDATE_CONNECT_ERROR);
			return;
		}
		for (AWVersion v : versions.getVersionList()) {
			if (v.getPlatform().equals(ActivityWatch.PLATFORM) && v.getId() > ActivityWatch.AWVERSION_ID) {
				this.awVersion = v;
				updateMessage = MessageFormat.format(messages.get(Keys.UPDATE_NEW_VERSION), v.getVersionNumber(), DEFAULT_DOWNLOAD_URL);
				return;
			}
		}
		updateMessage = messages.get(Keys.UPDATE_NO_UPDATE);
		this.awVersion = null;
	}
}
