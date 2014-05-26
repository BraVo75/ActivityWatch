package org.bravo.activitywatch;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
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
	
	private List<ActivityDO> activities;
	
	private SimpleLongProperty runningActivity = new SimpleLongProperty();
	private SimpleLongProperty selectedActivity = new SimpleLongProperty();
	private Date startTime;
	private Thread thread;
	
	private static final ActivityManager instance = new ActivityManager();

	private ActivityManager() {
		if (activities == null) {
			loadActivities();
		}
	};
	
	public StringProperty getRunningActivityTimer(Long id) {
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
		if (acts == null) { 
			return; 
		}
		this.activities = new ArrayList<ActivityDO>();
		for (Activity a : acts) {
			ActivityDO activityDO = new ActivityDO(a);
			activityDO.updateTimeProperty();
			activities.add(activityDO);
		}
	}
	
	public void saveActivities() {
		if (activities == null) {
			return;
		}
			List<Activity> l = new ArrayList<Activity>();
			for (ActivityDO a : activities) {
				l.add(a.getActivity());
			}
			CoreController.getInstance().setActivities(l);
			CoreController.getInstance().saveActivities();
	}
	
	public void selectActivity(final Long id) {
		selectedActivity.setValue(id);
	}
	
	public void startActivity(final Long id) {
		stopActivity();
		runningActivity.setValue(id);
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
		runningActivity.setValue(id);
	}
	
	public void updateTimerDisplay(final Long id) {
		getActivity(id).updateTimeProperty();
	}
	
	public void stopActivity() {
		if (runningActivity.getValue() != 0L) {
			getActivity(runningActivity.getValue()).getRunningProperty().setValue(false);
			runningActivity.setValue(0L);
		}
	}
		
	public void addMinutes(long minutes) {
		getRunningActivity().addMinutes(minutes);
		updateTimerDisplay(runningActivity.getValue());
	}
	
	public void subtractMinutes(long minutes) {
		getRunningActivity().subtractMinutes(minutes);
		updateTimerDisplay(runningActivity.getValue());
	}
	
	public ActivityDO getActivity(Long id) {
		if (activities == null) {
			return null;
		}
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
		if (activities == null) {
			return null;
		}
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
	
	public ActivityDO getRunningActivity() {
		return getActivity(runningActivity.getValue());
	}

	public ActivityDO getSelectedActivity() {
		return getActivity(selectedActivity.getValue());
	}

	public SimpleLongProperty getRunningActivityProperty() {
		return runningActivity;
	}
	
	public SimpleLongProperty getSelectedActivityProperty() {
		return selectedActivity;
	}

	public void removeActivity(Long activityId) {
		if (getActivity(activityId).isRunning()) {
			stopActivity();
		}
		
		cleanupListeners();
		activities.remove(getActivity(activityId));
		saveActivities();
	}

	public void cleanupListeners() {
		// kill listeners
		long id = runningActivity.get();
		runningActivity = new SimpleLongProperty(id);
		id = selectedActivity.get();
		selectedActivity = new SimpleLongProperty(id);
	}
	
	public Integer getActivityCount() {
		return activities.size();
	}
}
