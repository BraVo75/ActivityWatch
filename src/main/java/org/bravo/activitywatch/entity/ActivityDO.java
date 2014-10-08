package org.bravo.activitywatch.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;

public class ActivityDO {
	
	private Activity activity;
	private SimpleLongProperty timeProperty = new SimpleLongProperty(0L);
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

	public SimpleLongProperty getTimeProperty() {
		return timeProperty;
	}

	public void setTimeProperty(SimpleLongProperty time) {
		this.timeProperty = time;
	}
	
	public void updateTimeProperty() {
		updateTimeProperty(activity.getElapsedMillis());
	}
	
	public void updateTimeProperty(final Long time) {
    	timeProperty.setValue(time);
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
	
	public LocalDate getStartDate() {
		return activity.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}
}
