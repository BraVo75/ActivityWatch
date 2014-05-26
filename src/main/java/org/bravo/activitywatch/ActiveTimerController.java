/**
 * 
 */
package org.bravo.activitywatch;

import java.io.IOException;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import org.controlsfx.dialog.Dialogs;

/**
 * @author Volker
 *
 */
public class ActiveTimerController extends VBox {

	@FXML private Label lbl_activityName;
	@FXML private Label lbl_activityTime;
	@FXML private Button btn_toggleTimer;
	@FXML private Button btn_delete;
	@FXML private Button btn_rename;
	@FXML private VBox activeTimerBox;
	
	private Image imageStart;
	private Image imageStop;
	private ChangeListener<Boolean> runningListener;
	private ChangeListener<String> timeListener;
	private InvalidationListener selectedListener;
	private Long selectedActivity;
//	private FadeTransition ft;
	
	private static final ActiveTimerController instance = new ActiveTimerController();
	
	private ActivityManager activityManager = ActivityManager.getInstance();
	
	private ActiveTimerController() {
	
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ActiveTimer.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);
		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
		
		Image imageDelete = new Image(getClass().getResourceAsStream("images/delete_icon&24.png"),12,12,false,false);
		btn_delete.setGraphic(new ImageView(imageDelete));
		
		Image imageRename = new Image(getClass().getResourceAsStream("images/pencil_icon&24.png"),12,12,false,false);
		btn_rename.setGraphic(new ImageView(imageRename));

		imageStop = new Image(getClass().getResourceAsStream("images/playback_stop_icon&16.png"),16,16,false,false);
		imageStart = new Image(getClass().getResourceAsStream("images/playback_play_icon&16.png"),16,16,false,false);
		btn_toggleTimer.setGraphic(new ImageView(imageStart));
//		initButtonAnimation();
	}

	private void setupListeners() {
        
//		System.out.println("init Listener for "+selectedActivity);
//		activityManager.getActivity(9999L).getActivity();
		
		selectedListener = new InvalidationListener() {
			
			@Override
			public void invalidated(Observable arg0) {
				changeSelectedActivity();
			}
		};	
		
		runningListener = new ChangeListener<Boolean>() {
			
			@Override
			public void changed(ObservableValue<? extends Boolean> observable,
					Boolean oldValue, Boolean newValue) {
				setButtonStatus(newValue);
			}
			
		};
		
	    timeListener = new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
//				System.out.println(TimeConverter.autoconvert(Long.valueOf(oldValue))+"->"+TimeConverter.autoconvert(Long.valueOf(newValue)));
				lbl_activityTime.setText(TimeConverter.autoconvert(Long.valueOf(newValue)));
			}
		};

		activityManager.getSelectedActivityProperty().addListener(selectedListener);
		activityManager.getSelectedActivity().getTimeProperty().addListener(timeListener);
		activityManager.getSelectedActivity().getRunningProperty().addListener(runningListener);
	}

	public static final ActiveTimerController getInstance() {
		return instance;
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
		removeListeners();
		activityManager.removeActivity(activityManager.getSelectedActivityProperty().getValue());
		selectedActivity=null;
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
		}
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
	
	public void changeSelectedActivity() {
		
		// nothing to remove at first run
		if (selectedActivity != null) {
			removeListeners();
		}
		
		selectedActivity = activityManager.getSelectedActivityProperty().getValue();
		if (activityManager.getSelectedActivity() != null) {
			lbl_activityName.setText(activityManager.getSelectedActivity().getActivity().getName());
			if (activityManager.getSelectedActivity().getRunningProperty().getValue()) {
				start();
			}

			if (activityManager.getSelectedActivity().getTimeProperty().getValue() != "") {
				lbl_activityTime.setText(TimeConverter.convertToTime(Long.valueOf(activityManager.getSelectedActivity().getTimeProperty().getValue())));
			}
			setupListeners();
		}
	}

	private void removeListeners() {
		activityManager.getSelectedActivityProperty().removeListener(selectedListener);
		if (null != selectedActivity) {
			activityManager.getActivity(selectedActivity).getRunningProperty().removeListener(runningListener);
			activityManager.getActivity(selectedActivity).getTimeProperty().removeListener(timeListener);
		}
	}
	
	public void start() {
		setButtonStatus(true);
		activityManager.startActivity(activityManager.getSelectedActivity().getActivity().getId());
	}
	
	private void setButtonStatus(boolean running) {
		if (running) {
			btn_toggleTimer.setGraphic(new ImageView(imageStop));
//			ft.play();
		}
		else {
			btn_toggleTimer.setGraphic(new ImageView(imageStart));
//			ft.stop();
		}
	}

	// Deactivated because doesn't work smoothly. Flickering while updating the time.
//	private void initButtonAnimation() {
//		ft = new FadeTransition(Duration.millis(3000), btn_toggleTimer);
//		ft.setFromValue(1.0);
//		ft.setToValue(0.3);
//		ft.setCycleCount(Timeline.INDEFINITE);
//		ft.setAutoReverse(true);
//	}
}
