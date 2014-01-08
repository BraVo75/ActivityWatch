package org.bravo.activitywatch;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.bravo.activitywatch.entity.Activity;

public class MainWindowController implements Initializable {
	
	@FXML private TextField txt_newActivity;
	@FXML private VBox activeTimerLayout;
	@FXML private Label lbl_selectedDate;
	@FXML private ListView<ActivityListEntryController> lst_activities;
	
	private CoreController core = CoreController.getInstance();
	private ActivityManager activityManager = ActivityManager.getInstance();
	private GregorianCalendar selectedDate = new GregorianCalendar(); // TODO: locale
	private SimpleDateFormat sdf = new SimpleDateFormat(TIMER_FORMAT);
	private static final String TIMER_FORMAT = "d. MMMM YYYY"; // TODO: locale
	private SimpleStringProperty dateProperty = new SimpleStringProperty("");
	private ActiveTimerController activeTimerController = new ActiveTimerController();
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		selectedDate.setTime(new Date());
		updateDateDisplay(selectedDate);
		populateActivityList(activityManager.getActivities(selectedDate));
		lbl_selectedDate.textProperty().bind(dateProperty);
		activeTimerLayout.getChildren().add(activeTimerController);
	}

	private void populateActivityList(List<Activity> activities) {
		ObservableList<ActivityListEntryController> data = lst_activities.getItems();
		for (Activity a : activities) {
			data.add(new ActivityListEntryController(a.getId()));
		}
	}
	
	@FXML
	protected void aboutDialog(ActionEvent event) {
		Stage dialog = new Stage();
		dialog.initStyle(StageStyle.UTILITY);
		Scene scene = new Scene(new Group(new Text(25, 25, "Place a nice Dialog here!")));
		dialog.setScene(scene);
		dialog.show();
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
		selectedDate.add(Calendar.DAY_OF_MONTH, -1);
		updateDateDisplay(selectedDate);
		removeActivities();
		populateActivityList(activityManager.getActivities(selectedDate));
	}
	
	@FXML
	protected void nextDay(ActionEvent event) {
		selectedDate.add(Calendar.DAY_OF_MONTH, +1);
		updateDateDisplay(selectedDate);
		removeActivities();
		populateActivityList(activityManager.getActivities(selectedDate));
	}
	
	private void updateDateDisplay(final GregorianCalendar cal) {
		Platform.runLater(new Runnable(){
            @Override
            public void run() {
            	dateProperty.setValue(sdf.format(cal.getTime()));
            }
        });
	}
	
	private void addActivity(String name) {
		Long id = activityManager.getNewActivity().getId();
		activityManager.getActivity(id).setName(name);
		activityManager.getActivity(id).setStartDate(selectedDate.getTime());
		ActivityListEntryController timer = new ActivityListEntryController(id);
		
		ObservableList<ActivityListEntryController> data = lst_activities.getItems();
        data.add(timer);
        activityManager.startActivity(id);
        activeTimerController.reload();
	}
	
	private void removeActivities() {
		lst_activities.getItems().clear();
	}
}
