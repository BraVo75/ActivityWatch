/**
 * 
 */
package org.bravo.activitywatch;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import org.bravo.activitywatch.entity.Activity;

/**
 * @author Volker
 *
 */
public class ActiveTimerController extends VBox {

	@FXML private Label lbl_activityName;
	@FXML private Label lbl_activityTime;
	@FXML private Button btn_toggleTimer;
	@FXML private Activity activity;
	
	private SimpleStringProperty clock = new SimpleStringProperty("");
	private GregorianCalendar elapsedTime;
	private ActivityManager activityManager = ActivityManager.getInstance();
	
	@FXML private HBox timerLayout;
	
	private Date startTime = new Date();
	private java.util.Timer t;

	private SimpleDateFormat sdf = new SimpleDateFormat(TIMER_FORMAT);
	private static final String TIMER_FORMAT = "HH:mm:ss";

	public ActiveTimerController() {
		
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ActiveTimer.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);
		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
        
		reload();
	}

	@FXML
	protected void toggleTimer(ActionEvent event) {
		if (activityManager.isRunning()) {
			stopTimer();
		}
		else {
			start();
		}
	}
	
	@FXML
	protected void deleteTimer(ActionEvent event) {
		System.err.println("delete doesn't work yet");
	}
	
	public void stopTimer() {
		activityManager.stopActivity();
		if (!activityManager.isRunning()) { 
			btn_toggleTimer.setText("Start");
		}
	}
	
	public void reload() { // TODO: observable bei start und stop?
		this.activity = activityManager.getSelectedActivity();
		
		if (activity != null) {
			lbl_activityName.setText(activity.getName());
			lbl_activityTime.textProperty().bind(clock);
			updateTimerDisplay();
			if (activityManager.isRunning()) {
				btn_toggleTimer.setText("Stop");
			}
		}
	}
	
	private void updateTimerDisplay() {
		elapsedTime = new GregorianCalendar();
		elapsedTime.set(Calendar.HOUR_OF_DAY, 0);
		elapsedTime.set(Calendar.MINUTE, 0);
		elapsedTime.set(Calendar.SECOND, 0);
		long t = elapsedTime.getTimeInMillis() + activityManager.getSelectedActivity().getElapsedMillis();
		elapsedTime.setTimeInMillis(t);
		Platform.runLater(new Runnable(){
            @Override
            public void run() {
            	clock.setValue(sdf.format(elapsedTime.getTime()));
            }
        });
	}
	
	public void start() {
		btn_toggleTimer.setText("Stop");
		activityManager.startActivity(activity.getId());
	}
	
}

//	private static final long serialVersionUID = 1L;
//	private static final String DELETE_ICON = "images/edit-delete.png";
//	private static final String RENAME_ICON = "images/edit-rename.png";
//	private static final String PLAY_ICON = "images/media-playback-start.png";
//
//	JButton btn_activity;
//	private Date startTime = new Date();
//	private JLabel lbl_time;
//	JButton btn_rename;
//	JButton btn_remove;
//	private Timer t;
//	private SimpleDateFormat sdf = new SimpleDateFormat(TIMER_FORMAT);
//	private static final String TIMER_FORMAT = "HH:mm:ss";
//	private Activity activity;
//	private boolean running = false;
//	private final ImageIcon deleteIcon;
//	private final ImageIcon renameIcon;
//	private final ImageIcon playIcon;
//	private final JLabel lbl_play;
//	
//	public ActivityTimer(Activity activity, ActionListener listener) {
//		this.activity = activity;
//		setLayout(new FlowLayout());
//		playIcon = new ImageIcon(ActivityWatchSwing.class.getResource(PLAY_ICON));
//		deleteIcon = new ImageIcon(ActivityWatchSwing.class.getResource(DELETE_ICON));
//		btn_remove = new JButton(deleteIcon);
//		btn_remove.setPreferredSize(new Dimension(24,24));
//		renameIcon = new ImageIcon(ActivityWatchSwing.class.getResource(RENAME_ICON));
//		btn_rename = new JButton(renameIcon);
//		btn_rename.setPreferredSize(new Dimension(24,24));
//		btn_activity = new JButton(activity.getName());
//		btn_activity.setToolTipText(activity.getName());
//		btn_activity.setPreferredSize(new Dimension(200, 24));
//		if( running ) {
//			btn_activity.setBackground(Color.CYAN);
//		}
//		else {
//			btn_activity.setBackground(Color.LIGHT_GRAY);
//		}
//		btn_activity.addActionListener(listener);
//		lbl_play = new JLabel();
//		lbl_play.setPreferredSize(new Dimension(24,24));
//		add(lbl_play);
//		add(btn_activity);
//
//		if( activity.getElapsedMillis() == null ) {
//			activity.setElapsedMillis(0L);
//		}
//		lbl_time = new JLabel("", JLabel.CENTER);
//		btn_rename.setToolTipText("Rename");
//		btn_remove.setToolTipText("Remove");
//		add(lbl_time);
//		add(btn_rename);
//		add(btn_remove);
//		btn_remove.addActionListener(listener);
//		btn_rename.addActionListener(listener);
//		updateTimerDisplay();
//		t = new Timer(1000, new DisplayTimer());
//		running = false;
//	}
//	
//	public void start() {
//		running = true;
//		btn_activity.setBackground(Color.CYAN);
//		lbl_play.setIcon(playIcon);
//		startTime = new Date();
//		startTime.setTime(startTime.getTime() - activity.getElapsedMillis());
//		t.start();
//	}
//	
//	public void stop() {
//		running = false;
//		btn_activity.setBackground(Color.LIGHT_GRAY);
//		lbl_play.setIcon(null);
//		t.stop();
//	}
//	
//	private class DisplayTimer implements ActionListener {
//
//		@Override
//		public void actionPerformed(ActionEvent event) {
//			activity.setElapsedMillis(new GregorianCalendar().getTimeInMillis() - startTime.getTime());
//			updateTimerDisplay();
//		}
//	}
//	
//	private void updateTimerDisplay() {
//		GregorianCalendar elapsedTime = new GregorianCalendar();
//		elapsedTime.set(Calendar.HOUR_OF_DAY, 0);
//		elapsedTime.set(Calendar.MINUTE, 0);
//		elapsedTime.set(Calendar.SECOND, 0);
//		long t = elapsedTime.getTimeInMillis() + activity.getElapsedMillis();
//		elapsedTime.setTimeInMillis(t);
//		lbl_time.setText(sdf.format(elapsedTime.getTime()));
//	}
//
//	/**
//	 * @return the running
//	 */
//	public boolean isRunning() {
//		return running;
//	}
//	
//	public void minutesOffset(int minutes) {
//		stop();
//		activity.setElapsedMillis(activity.getElapsedMillis() + (minutes*60000));
//		if( activity.getElapsedMillis() < 0L ) {
//			activity.setElapsedMillis(0L);
//		}
//		start();
//		updateTimerDisplay();
//	}
//
//	/**
//	 * @return the activity
//	 */
//	public Activity getActivity() {
//		return activity;
//	}
//	
//	public void renameActivity(String name) {
//		activity.setName(name);
//		btn_activity.setText(name);
//		btn_activity.setToolTipText(name);
//	}
//	
//	public void displayCounter(boolean visible) {
//		lbl_time.setVisible(visible);
//	}
//}
