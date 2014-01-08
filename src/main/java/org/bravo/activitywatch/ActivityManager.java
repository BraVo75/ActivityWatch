package org.bravo.activitywatch;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimerTask;

import org.bravo.activitywatch.entity.Activity;

public class ActivityManager {
	
	private List<Activity> activities;
	private Activity selectedActivity;
	
	private Date startTime = new Date();
	private java.util.Timer t;
	private boolean running;
	
	private static final ActivityManager instance = new ActivityManager();

	private ActivityManager() {
		if (activities == null) {
			loadActivities();
		}
	};

	public static final ActivityManager getInstance() {
		return instance;
	}
	
	private void loadActivities() {
		activities = CoreController.getInstance().getActivities();
	}
	
	private void saveActivities() {
		CoreController.getInstance().setActivities(activities);
	}
	
	public void startActivity(Long id) {
		this.selectedActivity = getActivity(id);
		running = true;
//		startTime = new Date();
//		startTime.setTime(startTime.getTime() - activity.getElapsedMillis());
//
//		t = new java.util.Timer();
//		t.schedule(new TimerTask(){
//			public void run() {
//				activity.setElapsedMillis(new GregorianCalendar().getTimeInMillis() - startTime.getTime());
//				updateTimerDisplay();
//			}
//		}, 0, 1000);	
	}
	
	public void stopActivity() {
		selectedActivity = null;
		running = false;
//		if (timerActive) {
//			t.cancel();
//			t.purge();
//		}		
	}
	
	public Activity getSelectedActivity() {
		return selectedActivity;
	}
	
	public boolean isRunning() {
		return running;
	}
	
	public Activity getActivity(Long id) {
		for (Activity a : activities) {
			if (a.getId().equals(id)) {
				return a;
			}
		}
		return null;
	}
	
	public Activity getNewActivity() {
		Activity activity = new Activity();
		activity.setId(getMaxId()+1L);
		activities.add(activity);
		return activity;
	}
	
	private Long getMaxId() {
		Long max = 0L;
		for (Activity a : activities) {
			if (max.compareTo(a.getId()) < 0) {
				max = a.getId();
			}
		}
		return max;
	}
	
	public List<Activity> getActivities(GregorianCalendar d) {
		List<Activity> filteredList = new ArrayList<Activity>();
		for (Activity a : activities) {
			GregorianCalendar tcal = new GregorianCalendar();
			tcal.setTime(a.getStartDate());
			if( d.get(Calendar.DAY_OF_MONTH) == tcal.get(Calendar.DAY_OF_MONTH)
				&& d.get(Calendar.MONTH) == tcal.get(Calendar.MONTH)
				&& d.get(Calendar.YEAR) == tcal.get(Calendar.YEAR)) {
					filteredList.add(a);
				}
		}
		return filteredList;
	}
}
