package org.bravo.activitywatch;

import java.util.Locale;
import java.util.ResourceBundle;

public class Messages {

	public static enum Keys {
		APPLICATION_NAME,
		
		MENU_FILE,
		MENU_SAVE,
		MENU_QUIT,
		MENU_HELP,
		MENU_ABOUT,
		
		ABOUT_TITLE,
		ABOUT_VERSION,
		ABOUT_JAVA,
		ABOUT_ACTIVITIES_IN_DB,
		ABOUT_CREATED,
		
		ERROR_TITLE,
		ERROR_DATABASE_CORRUPTED,
		ERROR_SAVING_DATABASE,
		
		UPDATE_CONNECT_ERROR,
		UPDATE_NEW_VERSION,
		UPDATE_NO_UPDATE,
		
		TRAY_MENU_QUIT,
		TRAY_MENU_SHOW,
		TRAY_MENU_STOP,
		
		MAIN_TOTAL
	}

	private final static Messages instance = new Messages();
	
	private ResourceBundle messages;
	
	private Messages() {
		this.messages = ResourceBundle.getBundle("org/bravo/activitywatch/labels/Labels", Locale.GERMAN);
	}

	public static Messages getInstance() {
		return instance;
	}
	
	public String get(Keys key) {
		return messages.getString(key.toString().toLowerCase());
	}

}
