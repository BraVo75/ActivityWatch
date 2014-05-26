package org.bravo.activitywatch.entity;

import java.time.LocalTime;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

public class ActivityDO {
	
	private Activity activity;
	private SimpleStringProperty timeProperty = new SimpleStringProperty("");
	private BooleanProperty running = new SimpleBooleanProperty(false);

	public ActivityDO(Activity activity) {
		this.activity = activity;
		updateTimeProperty();
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public SimpleStringProperty getTimeProperty() {
		return timeProperty;
	}

	public void setTimeProperty(SimpleStringProperty time) {
		this.timeProperty = time;
	}
	
	public void updateTimeProperty() {
		updateTimeProperty(activity.getElapsedMillis().toString());
	}
	
	public void updateTimeProperty(final String formattedTime) {
    	timeProperty.setValue(formattedTime);
	}
	
	public BooleanProperty getRunningProperty() {
		return running;
	}

	public void setRunningProperty(BooleanProperty running) {
		this.running = running;
	}
	
	public boolean isRunning() {
		return this.running.getValue();
	}

	public void addMinutes(long l) {
		activity.setElapsedMillis(activity.getElapsedMillis()+(l*60000));
	}
	
	public void addSeconds(long l) {
		activity.setElapsedMillis(activity.getElapsedMillis()+(l*1000));
	}
	
	public void subtractMinutes(long l) {
		activity.setElapsedMillis(activity.getElapsedMillis()-(l*60000));
		if (activity.getElapsedMillis() < 0L) {
			activity.setElapsedMillis(0L);
		}
	}
	
	public void setTime(LocalTime time) {
		addMinutes(time.getHour()*60);
		addMinutes(time.getMinute());
		addSeconds(time.getSecond());
		updateTimeProperty();
	}
}
