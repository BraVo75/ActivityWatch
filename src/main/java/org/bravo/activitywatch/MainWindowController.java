package org.bravo.activitywatch;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import org.bravo.activitywatch.entity.ActivityDO;
import org.controlsfx.dialog.Dialogs;

public class MainWindowController {
	
	@FXML private TextField txt_newActivity;
	@FXML private VBox activeTimerLayout;
	@FXML private ListView<ActivityListEntryController> lst_activities;
	@FXML private DatePicker datePicker;
	@FXML private HBox controlBox;
	
	private ActivityManager activityManager = ActivityManager.getInstance();
	private LocalDate selectedDate;
	private ActiveTimerController activeTimerController = new ActiveTimerController();

	@FXML
	protected void initialize() {
		selectedDate = LocalDate.now();
		datePicker.setValue(selectedDate);
		activeTimerLayout.getChildren().add(activeTimerController);

	}

	@FXML
	protected void dateSelected(ActionEvent event) {
		selectedDate = datePicker.getValue();
		updateDisplay();
	}
	 
	private void populateActivityList(List<ActivityDO> activities) {
		ObservableList<ActivityListEntryController> data = lst_activities.getItems();
		for (ActivityDO a : activities) {
			data.add(createActivityListEntry(a.getActivity().getId()));
		}
	}
	
	@FXML
	protected void aboutDialog(ActionEvent event) {
		 Dialogs.create()
			      .title("About...")
			      .masthead(ActivityWatch.PRG_NAME)
			      .message( "Version: "+ActivityWatch.PRG_VERSION+"\n" +
			    		  "Java: " + System.getProperty("java.version")
			    		  +" on "+System.getProperty("os.name")
			    		  +" "+System.getProperty("os.version")
			    		  +" "+System.getProperty("os.arch")
			    		  +"\n\n"
			    		  +"by Volker Braun")
			      .nativeTitleBar()
			      .showInformation();
	}
	
	@FXML
	protected void saveStore(ActionEvent event) {
		activityManager.saveActivities();
	}
	
	@FXML
	protected void quitApplication(ActionEvent event) {
		Platform.exit();
	}

	@FXML
	protected void createActivity(ActionEvent event) {
		if(txt_newActivity.getText().isEmpty()) {
			return;
		}
		addActivity(txt_newActivity.getText());
		txt_newActivity.clear();
		txt_newActivity.requestFocus();
	}

	@FXML
	protected void previousDay(ActionEvent event) {
		selectedDate = selectedDate.minusDays(1);
		updateDisplay();
	}
	
	@FXML
	protected void nextDay(ActionEvent event) {
		selectedDate = selectedDate.plusDays(1);
		updateDisplay();
	}
	
	private void updateDisplay() {
		datePicker.setValue(selectedDate);
		removeActivities();
		populateActivityList(activityManager.getActivities(selectedDate));
		txt_newActivity.requestFocus();
	}
	
	private void addActivity(String name) {
		Long id = activityManager.getNewActivity().getActivity().getId();
		activityManager.getActivity(id).getActivity().setName(name);
		activityManager.getActivity(id).getActivity().setStartDate(localDate2Date(selectedDate));
		
		ObservableList<ActivityListEntryController> data = lst_activities.getItems();
        data.add(createActivityListEntry(id));
        activityManager.startActivity(id);
        activeTimerController.reload();
	}
	
	private ActivityListEntryController createActivityListEntry(Long id) {
		ActivityListEntryController entryController = new ActivityListEntryController(id);
		entryController.addEventHandler(RefreshEvent.REFRESH_REQUEST, new EventHandler<RefreshEvent>() {

			@Override
			public void handle(RefreshEvent arg0) {
				updateDisplay();
			}
		});
		
		return entryController;
	}
	
	private void removeActivities() {
		lst_activities.getItems().clear();
	}
	
	private Date localDate2Date(LocalDate ld) {
		Instant instant = ld.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
		return Date.from(instant);
	}

	public void handleWindowShownEvent() {
		txt_newActivity.requestFocus();
	}

}
