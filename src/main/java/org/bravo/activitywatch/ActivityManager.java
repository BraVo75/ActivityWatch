package org.bravo.activitywatch;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.StringProperty;

import org.bravo.activitywatch.entity.Activity;
import org.bravo.activitywatch.entity.ActivityDO;

/**
 * All activities are managed here.
 * Only one timer can be activated at once.
 * 
 * @author Volker Braun
 *
 */
public class ActivityManager {
	
	private SimpleDateFormat sdf = new SimpleDateFormat(TIMER_FORMAT);
	private static final String TIMER_FORMAT = "HH:mm:ss";

	private List<ActivityDO> activities;
	
	private SimpleLongProperty selectedActivity = new SimpleLongProperty();
	private Date startTime;
	private Thread thread;
	
	private static final ActivityManager instance = new ActivityManager();

	private ActivityManager() {
		if (activities == null) {
			loadActivities();
		}
	};
	
	public StringProperty getSelectedActivityTimer(Long id) {
		if (null == id) {
			return null;
		}
		return getActivity(id).getTimeProperty();
	}

	public static final ActivityManager getInstance() {
		return instance;
	}
	
	private void loadActivities() {
		List<Activity> acts = CoreController.getInstance().getActivities();
		this.activities = new ArrayList<ActivityDO>();
		for (Activity a : acts) {
			ActivityDO activityDO = new ActivityDO(a);
			activityDO.updateTimeProperty(millisToTime(a.getElapsedMillis()));
			activities.add(activityDO);
		}
	}
	
	public void saveActivities() {
		List<Activity> l = new ArrayList<Activity>();
		for (ActivityDO a : activities) {
			l.add(a.getActivity());
		}
		CoreController.getInstance().setActivities(l);
		CoreController.getInstance().saveActivities();
	}
	
	public void startActivity(final Long id) {
		stopActivity();
		getActivity(id).getRunningProperty().set(true);
		startTime = new Date();
//		startTime.setTime(startTime.getTime() - getActivity(id).getActivity().getElapsedMillis());

		thread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (getActivity(id) != null && getActivity(id).getRunningProperty().getValue()) {
					Platform.runLater(new Runnable() {
						
						@Override
						public void run() {
							startTime.setTime(startTime.getTime() - getActivity(id).getActivity().getElapsedMillis());
							getActivity(id).getActivity().setElapsedMillis(new GregorianCalendar().getTimeInMillis() - startTime.getTime());
							updateTimerDisplay(id);
							startTime = new Date();
						}
					});
					try {
						Thread.sleep(1000);
					}
					catch (InterruptedException e) {
						break;
					}
				}
			}
		});
		
		thread.setName("Runnable Time Updater");
		thread.setDaemon(true);
		thread.start();
		selectedActivity.setValue(id);
	}
	
	private void updateTimerDisplay(final Long id) {
		getActivity(id).getTimeProperty().setValue(millisToTime(getActivity(id).getActivity().getElapsedMillis()));
	}
	
	private String millisToTime(Long millis) {
		GregorianCalendar elapsedTime = new GregorianCalendar();
		elapsedTime.set(Calendar.HOUR_OF_DAY, 0);
		elapsedTime.set(Calendar.MINUTE, 0);
		elapsedTime.set(Calendar.SECOND, 0);
		long t = elapsedTime.getTimeInMillis() + millis;
		elapsedTime.setTimeInMillis(t);
		return sdf.format(elapsedTime.getTime());
	}
	
	public void stopActivity() {
		if (selectedActivity.getValue() != 0L) {
			getActivity(selectedActivity.getValue()).getRunningProperty().setValue(false);
		}
	}
		
	public void addMinutes(long minutes) {
		getSelectedActivity().addMinutes(minutes);
		updateTimerDisplay(selectedActivity.getValue());
	}
	
	public void subtractMinutes(long minutes) {
		getSelectedActivity().subtractMinutes(minutes);
		updateTimerDisplay(selectedActivity.getValue());
	}
	
	public ActivityDO getActivity(Long id) {
		for (ActivityDO a : activities) {
			if (a.getActivity().getId().equals(id)) {
				return a;
			}
		}
		return null;
	}
	
	public ActivityDO getNewActivity() {
		Activity activity = new Activity();
		activity.setId(getMaxId()+1L);
		activity.setStartDate(new Date());
		activity.setElapsedMillis(0L);
		ActivityDO activityDO = new ActivityDO(activity);
		activities.add(activityDO);
		return activityDO;
	}
	
	private Long getMaxId() {
		Long max = 0L;
		for (ActivityDO a : activities) {
			if (max.compareTo(a.getActivity().getId()) < 0) {
				max = a.getActivity().getId();
			}
		}
		return max;
	}
	
	public List<ActivityDO> getActivities(LocalDate d) {
		List<ActivityDO> filteredList = new ArrayList<ActivityDO>();
		for (ActivityDO a : activities) {
			Instant instant = Instant.ofEpochMilli(a.getActivity().getStartDate().getTime());
			LocalDate tcal = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate();
			if( d.getDayOfMonth() == tcal.getDayOfMonth()
				&& d.getMonth() == tcal.getMonth()
				&& d.getYear() == tcal.getYear()) {
					filteredList.add(a);
				}
		}
		return filteredList;
	}

	public void selectActivity(Long id) {
		this.selectedActivity.setValue(id);
	}
	
	public ActivityDO getSelectedActivity() {
		return getActivity(selectedActivity.getValue());
	}

	public SimpleLongProperty getSelectedActivityProperty() {
		return selectedActivity;
	}

	public void removeActivity(Long activityId) {
		if (getActivity(activityId).isRunning()) {
			stopActivity();
		}
		if (activityId.equals(selectedActivity.getValue())) {
			selectedActivity.setValue(0L);
		}
		activities.remove(getActivity(activityId));
	}
	
}
