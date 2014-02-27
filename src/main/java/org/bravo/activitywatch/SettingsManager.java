package org.bravo.activitywatch;

import org.bravo.activitywatch.entity.Settings.TimeFormat;

public class SettingsManager {

	private static final SettingsManager instance = new SettingsManager();

	private TimeFormat timeFormat;
	
	private SettingsManager() {
		timeFormat = TimeFormat.TIME;
	};

	public static final SettingsManager getInstance() {
		return instance;
	}

	public TimeFormat getTimeFormat() {
		return timeFormat;
	}

	public void setTimeFormat(TimeFormat timeFormat) {
		this.timeFormat = timeFormat;
	}

}
