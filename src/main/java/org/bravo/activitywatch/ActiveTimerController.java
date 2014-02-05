/**
 * 
 */
package org.bravo.activitywatch;

import java.io.IOException;

import org.controlsfx.dialog.Dialogs;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * @author Volker
 *
 */
public class ActiveTimerController extends VBox {

	@FXML private Label lbl_activityName;
	@FXML private Label lbl_activityTime;
	@FXML private Label lbl_shortName;
	@FXML private Label lbl_shortTime;
	@FXML private Button btn_toggleTimer;
	@FXML private VBox activeTimerBox;
	@FXML private VBox simpleTimerBox;
	
	private ActivityManager activityManager = ActivityManager.getInstance();
	
	public ActiveTimerController() {
		
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ActiveTimer.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);
		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
        
		activityManager.getSelectedActivityProperty().addListener(new InvalidationListener() {
			
			@Override
			public void invalidated(Observable arg0) {
				reload();
			}
		});	
		
	}

	@FXML
	protected void toggleTimer(ActionEvent event) {
		if (activityManager.getSelectedActivity().getRunningProperty().getValue()) {
			stopTimer();
		}
		else {
			start();
		}
	}
	
	@FXML
	protected void deleteTimer(ActionEvent event) {
		activityManager.removeActivity(activityManager.getSelectedActivityProperty().getValue());
		this.fireEvent(new RefreshEvent());
	}
	
	@FXML
	protected void renameTimer(ActionEvent event) {
		String response = Dialogs.create()
				.title("Rename")
				.message("Rename Activity")
				.nativeTitleBar()
				.showTextInput(activityManager.getSelectedActivity().getActivity().getName());
		
		if (response != null && !response.isEmpty()) {
			activityManager.getSelectedActivity().getActivity().setName(response);
			lbl_activityName.setText(response);
			lbl_shortName.setText(response);
		}
		this.fireEvent(new RefreshEvent());
	}
	
	@FXML
	protected void minus60(ActionEvent event) {
		activityManager.subtractMinutes(60L);
	}
	
	@FXML
	protected void minus20(ActionEvent event) {
		activityManager.subtractMinutes(20L);
	}
	
	@FXML
	protected void minus5(ActionEvent event) {
		activityManager.subtractMinutes(5L);
	}
	
	@FXML
	protected void plus5(ActionEvent event) {
		activityManager.addMinutes(5L);
	}
	
	@FXML
	protected void plus20(ActionEvent event) {
		activityManager.addMinutes(20L);
	}
	
	@FXML
	protected void plus60(ActionEvent event) {
		activityManager.addMinutes(60L);
	}
	
	public void stopTimer() {
		activityManager.stopActivity();
		setButtonStatus(false);
	}
	
	public void reload() {
		if (activityManager.getSelectedActivity() != null) {
			lbl_activityName.setText(activityManager.getSelectedActivity().getActivity().getName());
			lbl_shortName.setText(activityManager.getSelectedActivity().getActivity().getName());
			if (activityManager.getSelectedActivity().getRunningProperty().getValue()) {
				btn_toggleTimer.setText("Stop");
			}
			activityManager.getSelectedActivity().getRunningProperty().addListener(new ChangeListener<Boolean>() {
				
				@Override
				public void changed(ObservableValue<? extends Boolean> observable,
						Boolean oldValue, Boolean newValue) {
					setButtonStatus(newValue);
				}
				
			});
			
			activityManager.getSelectedActivity().getRunningProperty().addListener(new ChangeListener<Boolean>() {

				@Override
				public void changed(ObservableValue<? extends Boolean> observable,
						Boolean oldValue, Boolean newValue) {
					if (newValue) {
						simpleTimerBox.getStyleClass().add("running");
					}
					else {
						simpleTimerBox.getStyleClass().remove("running");
					}
					
				}
				
			});
			
			activeTimerBox.setVisible(true);
			lbl_activityTime.textProperty().bind(activityManager.getSelectedActivity().getTimeProperty());
			lbl_shortTime.textProperty().bind(activityManager.getSelectedActivity().getTimeProperty());
		}
		else {
			activeTimerBox.setVisible(false);
			lbl_shortTime.textProperty().unbind();
			lbl_shortTime.setText("");
			lbl_shortName.setText("");
		}
		
	}
	
	public void start() {
		setButtonStatus(true);
		activityManager.startActivity(activityManager.getSelectedActivity().getActivity().getId());
	}
	
	private void setButtonStatus(boolean running) {
		if (running) {
			btn_toggleTimer.setText("Stop");
		}
		else {
			btn_toggleTimer.setText("Start");
		}
	}

	public void showDetails() {
		simpleTimerBox.setVisible(false);
	}

	public void hideDetails() {
		simpleTimerBox.setVisible(true);
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
