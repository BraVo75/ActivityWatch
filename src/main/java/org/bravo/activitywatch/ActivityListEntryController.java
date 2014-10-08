/**
 * 
 */
package org.bravo.activitywatch;

import java.io.IOException;

import javafx.animation.FadeTransition;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import org.bravo.activitywatch.entity.Activity;
import org.bravo.activitywatch.events.RefreshEvent;
import org.controlsfx.dialog.Dialogs;

/**
 * @author Volker
 *
 */
public class ActivityListEntryController extends VBox {

	@FXML private Label lbl_name;
	@FXML private Label lbl_time;
	@FXML private HBox listbox;
	@FXML private VBox rowLayout;
	@FXML private Button btn_delete;
	@FXML private HBox timerLayout;
	@FXML private Button btn_rename;
	
	private Long activityId;
	private ContextMenu contextMenu;
	private ActiveTimerController activeTimerController;
	
	private ActivityManager activityManager = ActivityManager.getInstance();
	private InvalidationListener selectedListener;

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
		setupListeners();
		
		if (activityManager.getActivity(activityId).getTimeProperty().getValue() != null) {
			lbl_time.setText(TimeConverter.autoconvert(Long.valueOf(activityManager.getActivity(activityId).getTimeProperty().getValue())));
		}
	}

	private void setupListeners() {
		
		// if selection has changed
		selectedListener = new InvalidationListener() {

			@Override
			public void invalidated(Observable arg0) {
				if (!isSelectedActivity()) {
					setupSimpleControls();
				}
				else {
					setupDetailedControls();
				}
			}
		};

		activityManager.getSelectedActivityProperty().addListener(selectedListener);
	}
	
	private Activity getActivity() {
		return activityManager.getActivity(activityId) != null ? activityManager.getActivity(activityId).getActivity() : null;
	}

	@FXML
	protected void mouseClicked(MouseEvent event) {
		if (event.getButton() == MouseButton.PRIMARY) {
			start();
			activityManager.selectActivity(activityId);
		}
		if (event.getButton() == MouseButton.SECONDARY) {
			contextMenu.show(this, event.getScreenX(), event.getScreenY());
		}
	}

	private void removeListeners() {
		activityManager.getSelectedActivityProperty().removeListener(selectedListener);
		this.activeTimerController = null;
	}
	
	@FXML
	protected void deleteTimer() {
		removeListeners();
		activityManager.removeActivity(activityId);
		TrayMenu.getInstance().updateTrayMenu();
		fireRefresh();
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
		TrayMenu.getInstance().updateTrayMenu();
	}
	
	private void setupUIControls() {
		if (isSelectedActivity()) {
			setupDetailedControls();
		}
		else {
			setupSimpleControls();
		}
	}	
	
	private void setupSimpleControls() {
		rowLayout.getChildren().clear();
		rowLayout.getChildren().add(listbox);
		lbl_name.setText(getActivity().getName());
		if (activityManager.getActivity(activityId).getTimeProperty().getValue() != null) {
			lbl_time.setText(TimeConverter.autoconvert(Long.valueOf(activityManager.getActivity(activityId).getTimeProperty().getValue())));
		}
		HBox.setHgrow(lbl_time, Priority.ALWAYS);
		activeTimerController = null;
		
		setupContextMenu();
		if (activityManager.getActivity(activityId).isRunning()) {
			listbox.getStyleClass().add("running");
		}
		
		Tooltip t = new Tooltip(getActivity().getName());
		lbl_name.setTooltip(t);

		Image imageRename = new Image(getClass().getResourceAsStream("images/pencil_icon&24.png"),12,12,false,false);
		btn_rename.setGraphic(new ImageView(imageRename));
		
		Image imageDelete = new Image(getClass().getResourceAsStream("images/delete_icon&24.png"),12,12,false,false);
		btn_delete.setGraphic(new ImageView(imageDelete));
	}
	
	private void setupDetailedControls() {
		activeTimerController = createActiveTimerController();
		rowLayout.getChildren().clear();
		rowLayout.getChildren().add(activeTimerController);
		FadeTransition ft = new FadeTransition(Duration.millis(300), activeTimerController);
		ft.setFromValue(0.0);
		ft.setToValue(1.0);
		ft.setCycleCount(1);// Timeline.INDEFINITE);
		ft.setAutoReverse(false);
		ft.play();
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

	@FXML
	protected void renameTimer() {
		String response = Dialogs.create()
				.title("Rename")
				.message("Rename Activity")
				.nativeTitleBar()
				.showTextInput(activityManager.getActivity(activityId).getActivity().getName());
		
		if (response != null && !response.isEmpty()) {
			activityManager.getActivity(activityId).getActivity().setName(response);
			lbl_name.setText(response);
		}
		TrayMenu.getInstance().updateTrayMenu();
		fireRefresh();
	}
	
	public void start() {
		activityManager.startActivity(activityId);
		TrayMenu.getInstance().updateTrayMenu();
	}
	
	private boolean isSelectedActivity() {
		return activityManager.getSelectedActivity() != null && 
				activityManager.getSelectedActivity().getActivity().getId().equals(activityId);
	}
	
	private ActiveTimerController createActiveTimerController() {
		activeTimerController = ActiveTimerController.getInstance();
		activeTimerController.changeSelectedActivity();
		return activeTimerController;
	}
	
	private void fireRefresh() {
		this.fireEvent(new RefreshEvent());
	}
	
}
