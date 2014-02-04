/**
 * 
 */
package org.bravo.activitywatch;

import java.io.IOException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import org.bravo.activitywatch.entity.Activity;
import org.controlsfx.dialog.Dialogs;

/**
 * @author Volker
 *
 */
public class ActivityListEntryController extends VBox {

	@FXML private Label lbl_name;
	@FXML private Label lbl_time;
//	@FXML private Button btn_detail;
	@FXML private HBox listbox;
	private Long activityId;
	private ContextMenu contextMenu;
	
	private ActivityManager activityManager = ActivityManager.getInstance();
//	private SimpleStringProperty clock = new SimpleStringProperty("");
//	private GregorianCalendar elapsedTime;
	
	@FXML private HBox timerLayout;

//	private SimpleDateFormat sdf = new SimpleDateFormat(TIMER_FORMAT);
//	private static final String TIMER_FORMAT = "HH:mm:ss";

	public ActivityListEntryController(Long activityId) {
		this.activityId = activityId;
		
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ActivityListEntry.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);
		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
        
		setupUIControls();

		activityManager.getActivity(activityId).getRunningProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable,
					Boolean oldValue, Boolean newValue) {
				if (newValue) {
					listbox.getStyleClass().add("running");
				}
				else {
					listbox.getStyleClass().remove("running");
				}
				
			}
			
		});
		
		lbl_time.setText(activityManager.getActivity(activityId).getTimeProperty().getValue());
		lbl_time.textProperty().bind(activityManager.getActivity(activityId).getTimeProperty());
	}

	private Activity getActivity() {
		return activityManager.getActivity(activityId).getActivity();
	}

	@FXML
	protected void mouseClicked(MouseEvent event) {
		if (event.getButton() == MouseButton.PRIMARY) {
			toggleTimer();
		}
		if (event.getButton() == MouseButton.SECONDARY) {
			contextMenu.show(this, event.getScreenX(), event.getScreenY());
		}
	}
	
	private void toggleTimer() {
		if (activityManager.getActivity(activityId).getRunningProperty().get()) {
			stopTimer();
		}
		else {
			start();
		}
	}
	
	private void deleteTimer() {
		activityManager.removeActivity(activityId);
		this.fireEvent(new RefreshEvent());
	}
	
	@FXML
	protected void timerDetail() {
		Dialogs.create()
			.title("Timer Information")
			.masthead(activityManager.getActivity(activityId).getActivity().getName())
			.nativeTitleBar()
			.message("Starttime: "+activityManager.getActivity(activityId).getActivity().getStartDate() +"\n" +
					"")
			.showInformation();
	}
	
	@FXML
	protected void mouseEntered() {
//		btn_detail.setVisible(true);
	}
	
	@FXML
	protected void mouseExited() {
//		btn_detail.setVisible(false);
	}
	
	public void stopTimer() {
		activityManager.stopActivity();
	}
	
	private void setupUIControls() {
		lbl_name.setText(getActivity().getName());
		lbl_time.setText(activityManager.getActivity(activityId).getTimeProperty().getValue());
//		btn_detail.setAlignment(Pos.CENTER_RIGHT);
		HBox.setHgrow(lbl_time, Priority.ALWAYS);
		
		setupContextMenu();
		if (activityManager.getActivity(activityId).isRunning()) {
			listbox.getStyleClass().add("running");
		}
	}
	
	private void setupContextMenu() {
		contextMenu = new ContextMenu();

		MenuItem cmDelete = new MenuItem("Delete");
		cmDelete.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent e) {
		    	contextMenu.hide();
		    	deleteTimer();
		    }
		});

		MenuItem cmRename = new MenuItem("Rename");
		cmRename.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				contextMenu.hide();
				renameTimer();
			}
		});
		
		contextMenu.getItems().add(cmRename);
		contextMenu.getItems().add(cmDelete);
	}

	private void renameTimer() {
		String response = Dialogs.create()
				.title("Rename")
				.message("Rename Activity")
				.nativeTitleBar()
				.showTextInput(activityManager.getActivity(activityId).getActivity().getName());
		
		if (response != null && !response.isEmpty()) {
			activityManager.getActivity(activityId).getActivity().setName(response);
			lbl_name.setText(response);
		}
	}
	
	public void start() {
		activityManager.startActivity(activityId);
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
