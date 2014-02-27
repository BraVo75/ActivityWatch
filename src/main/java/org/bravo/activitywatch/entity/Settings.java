/**
 * 
 */
package org.bravo.activitywatch.entity;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Volker Braun
 */
@XmlRootElement(name="settings")
public class Settings {

	private boolean statusBar;
	private boolean countersVisible;
	private boolean alwaysOnTop;
	private TimeFormat timeFormat;
	
	public static enum TimeFormat {
		TIME, DECIMAL;
	}

	/**
	 * @return the alwaysOnTop
	 */
	public boolean isAlwaysOnTop() {
		return alwaysOnTop;
	}

	/**
	 * @param alwaysOnTop the alwaysOnTop to set
	 */
	public void setAlwaysOnTop(boolean alwaysOnTop) {
		this.alwaysOnTop = alwaysOnTop;
	}

	/**
	 * @return the countersVisible
	 */
	public boolean isCountersVisible() {
		return countersVisible;
	}

	/**
	 * @param countersVisible the countersVisible to set
	 */
	public void setCountersVisible(boolean countersVisible) {
		this.countersVisible = countersVisible;
	}

	/**
	 * @return the statusBarVisible
	 */
	public boolean isStatusBarVisible() {
		return statusBar;
	}

	/**
	 * @param statusBarVisible the statusBarVisible to set
	 */
	public void setStatusBarVisible(boolean statusBarVisible) {
		this.statusBar = statusBarVisible;
	}

	public TimeFormat getTimeFormat() {
		return timeFormat;
	}

	public void setTimeFormat(TimeFormat timeFormat) {
		this.timeFormat = timeFormat;
	}
	
}
