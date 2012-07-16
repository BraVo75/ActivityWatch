/**
 * 
 */
package org.bravo.activitywatch;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * @author Volker
 *
 */
public class Timer extends Group {

	private Button label;
	private Activity activity;
	private Label lbl_time;

	private HBox layout;
	
	private Date startTime = new Date();
//	private Timer t;

	private SimpleDateFormat sdf = new SimpleDateFormat(TIMER_FORMAT);
	private static final String TIMER_FORMAT = "HH:mm:ss";

	private boolean running = false;

	public Timer(Activity activity) {
		super();
		this.activity = activity;
		
		if( activity.getElapsedMillis() == null ) {
			activity.setElapsedMillis(0L);
		}

		setupUIControls();
	}

	private void setupUIControls() {
		layout = new HBox();
		getChildren().add(layout);
		
		label = new Button(activity.getName());
		layout.getChildren().add(label);
		
		lbl_time = new Label();
		layout.getChildren().add(lbl_time);
		updateTimerDisplay();
	}

	private void updateTimerDisplay() {
		GregorianCalendar elapsedTime = new GregorianCalendar();
		elapsedTime.set(Calendar.HOUR_OF_DAY, 0);
		elapsedTime.set(Calendar.MINUTE, 0);
		elapsedTime.set(Calendar.SECOND, 0);
		long t = elapsedTime.getTimeInMillis() + activity.getElapsedMillis();
		elapsedTime.setTimeInMillis(t);
		lbl_time.setText(sdf.format(elapsedTime.getTime()));
	}

	public void start() {
		running = true;
//		btn_activity.setBackground(Color.CYAN);
//		lbl_play.setIcon(playIcon);
		startTime = new Date();
		startTime.setTime(startTime.getTime() - activity.getElapsedMillis());
//		t.start();
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
