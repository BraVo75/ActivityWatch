/**
 * 
 */
package org.bravo.activitywatch.entity;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.bravo.activitywatch.Settings;

/**
 * @author Volker Braun
 *
 */
@XmlRootElement(name="AWStore")
public class AWStore {

	private int version;
	private Settings settings = new Settings();
	@XmlElementWrapper(name="activities")
	@XmlElement(name="activity")
	private List<Activity> activities;
	
	/**
	 * @return the activities
	 */
	public List<Activity> getActivitiyList() {
		return activities;
	}

	/**
	 * @param activities the activities to set
	 */
	public void setActivities(List<Activity> activities) {
		this.activities = activities;
	}

	/**
	 * @return the version
	 */
	@XmlElement(name="version")
	public int getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(int version) {
		this.version = version;
	}

	/**
	 * @return the settings
	 */
	@XmlElement(name="settings")
	public Settings getSettings() {
		return settings;
	}

	/**
	 * @param settings the settings to set
	 */
	public void setSettings(Settings settings) {
		this.settings = settings;
	}
}
