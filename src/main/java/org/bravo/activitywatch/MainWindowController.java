package org.bravo.activitywatch;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.binding.LongBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import org.bravo.activitywatch.Messages.Keys;
import org.bravo.activitywatch.entity.ActivityDO;
import org.bravo.activitywatch.entity.Settings.TimeFormat;
import org.bravo.activitywatch.events.RefreshEvent;
import org.controlsfx.dialog.Dialogs;

public class MainWindowController {
	
	@FXML private TextField txt_newActivity;
	@FXML private ListView<ActivityListEntryController> lst_activities;
	@FXML private DatePicker datePicker;
	@FXML private VBox controlBox;
	@FXML private VBox leftBox;
	@FXML private HBox topBox;
	@FXML private CheckMenuItem formatTime;
	@FXML private CheckMenuItem formatDecimal;
	@FXML private Button btn_previousDay;
	@FXML private Button btn_nextDay;
	@FXML private Label lbl_total;
	@FXML private Label lbl_totalValue;
	
	private static final String ACTIVITY_INPUT_DEFAULT_TEXT = "What are you doing?";
	
	private ActivityManager activityManager = ActivityManager.getInstance();
	private LocalDate selectedDate;
	private Messages messages = Messages.getInstance();
	private SettingsManager settings = SettingsManager.getInstance();
	private CoreController core = CoreController.getInstance();
	private LongBinding total;
	private ObservableList<ActivityDO> observableActivities;
	

	@FXML
	protected void initialize() {
		selectedDate = LocalDate.now();
		setupUI();
	}

	private void setupUI() {
		Tooltip t = new Tooltip("Enter your activity here");
		txt_newActivity.setTooltip(t);
		txt_newActivity.setPromptText(ACTIVITY_INPUT_DEFAULT_TEXT);
		formatTime.setSelected(settings.getTimeFormat().equals(TimeFormat.TIME));
		formatDecimal.setSelected(settings.getTimeFormat().equals(TimeFormat.DECIMAL));
		Image imagePrevDay = new Image(getClass().getResourceAsStream("images/br_prev_icon&16.png"),16,16,false,false);
		btn_previousDay.setGraphic(new ImageView(imagePrevDay));
		Image imageNextDay = new Image(getClass().getResourceAsStream("images/br_next_icon&16.png"),16,16,false,false);
		btn_nextDay.setGraphic(new ImageView(imageNextDay));
		lbl_total.setText(messages.get(Keys.MAIN_TOTAL));
		if (core.isUpdateAvailable()) {
			showUpdateInfo();
		}
		updateDatePicker();
		initFocusPolicy();
		txt_newActivity.getParent().requestFocus();
	}

	private void updateDatePicker() {
		datePicker.setValue(selectedDate);
		if (selectedDate.equals(LocalDate.now())) {
			datePicker.getEditor().setStyle("-fx-background-color: #e0f0ff;");
		}
		else {
			datePicker.getEditor().setStyle("-fx-background-color: white;");
		}
	}

	private void initFocusPolicy() {
		lst_activities.focusedProperty().addListener(new ChangeListener<Boolean>() {  
			  
            @Override  
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {  
                if (newValue.booleanValue()) {  
                    txt_newActivity.requestFocus();  
                }  
            }  
        });
	}

	private void showUpdateInfo() {
		Image imageUpdate = new Image(getClass().getResourceAsStream("images/download_icon&24.png"),12,12,false,false);
		Label lbl_update = new Label();
		lbl_update.setGraphic(new ImageView(imageUpdate));
		lbl_update.setOpacity(0.6);
		topBox.getChildren().add(lbl_update);
		lbl_update.setVisible(true);
		lbl_update.setTooltip(new Tooltip(core.getNewestVersionInfo()));
	}
	
	@FXML
	protected void timeFormatTime() {
		formatDecimal.setSelected(false);
		formatTime.setSelected(true);
		settings.setTimeFormat(TimeFormat.TIME);
		updateDisplay();
	}
	
	@FXML
	protected void timeFormatDecimal() {
		formatDecimal.setSelected(true);
		formatTime.setSelected(false);
		settings.setTimeFormat(TimeFormat.DECIMAL);
		updateDisplay();
	}

	@FXML
	protected void dateSelected(ActionEvent event) {
		selectedDate = datePicker.getValue();
		updateDisplay();
		datePicker.setValue(selectedDate);
	}
	
	private void populateActivityList(List<ActivityDO> activities) {
		removeActivities();
		if (activities == null) {
			return;
		}
		observableActivities = FXCollections.observableList(new ArrayList<ActivityDO>());
		observableActivities.addAll(activities);
		for (ActivityDO a : activities) {
			lst_activities.getItems().add(createActivityListEntry(a.getActivity().getId()));
		}
		total = new TotalBinding(observableActivities);
		lbl_totalValue.setText(TimeConverter.autoconvert(total.longValue()));
        total.addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            lbl_totalValue.setText(TimeConverter.autoconvert(newValue.longValue()));
        });
	}
	
	@FXML
	protected void aboutDialog(ActionEvent event) {
		Dialogs.create()
			      .title(messages.get(Messages.Keys.ABOUT_TITLE))
			      .masthead(ActivityWatch.PRG_NAME)
			      .message( messages.get(Messages.Keys.ABOUT_VERSION) +": "+ActivityWatch.PRG_VERSION
//			    		  messages.get(Messages.Keys.ABOUT_JAVA) +": " + System.getProperty("java.version")
//			    		  +" on "+System.getProperty("os.name")
//			    		  +" "+System.getProperty("os.version")
//			    		  +" "+System.getProperty("os.arch")
			    		  +"\n\n"
			    		  +messages.get(Messages.Keys.ABOUT_ACTIVITIES_IN_DB) +": "+activityManager.getActivityCount()
			    		  +"\n\n"
			    		  +messages.get(Messages.Keys.ABOUT_CREATED)
			    		  +"\n\n"
			    		  +core.getNewestVersionInfo()
			    		  )
			      .nativeTitleBar()
			      .showInformation();
	}
	
	@FXML
	protected void saveStore() {
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
		populateActivityList(activityManager.getActivities(selectedDate));
		updateDatePicker();
		txt_newActivity.requestFocus();
	}
	
	private void addActivity(String name) {
		Long id = activityManager.getNewActivity().getActivity().getId();
		LocalTime time = NameParser.parseName(name.substring(name.lastIndexOf(" ")+1));
		if (name.indexOf(" ") > 0) {
			if (time.toSecondOfDay() > 0) {
				name = name.substring(0, name.lastIndexOf(" "));
			}
		}
		
		activityManager.getActivity(id).getActivity().setName(name);
		activityManager.getActivity(id).getActivity().setStartDate(localDate2Date(selectedDate));
		activityManager.getActivity(id).setTime(time);
		
		lst_activities.getItems().add(createActivityListEntry(id));
        if (time.toSecondOfDay() == 0) {
        	activityManager.startActivity(id);
        	activityManager.selectActivity(id);
        }
        
        observableActivities.add(activityManager.getActivity(id));
        TrayMenu.getInstance().updateTrayMenu();
        saveStore();
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
		activityManager.cleanupListeners();
		if (total != null) {
			total.dispose();
		}
	}
	
	private Date localDate2Date(LocalDate ld) {
		Instant instant = ld.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
		return Date.from(instant);
	}

	public void handleWindowShownEvent() {
		txt_newActivity.requestFocus();
	}
}
