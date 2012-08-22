package org.bravo.activitywatch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.swing.JOptionPane;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class ActivityWatch extends Application {

	private static final String PRG_NAME = "ActivityWatch";
	private static final String PRG_VERSION = "0.9";
	private static final String AWSTORE_XML = "AWStore.xml";
	private static final int AWSTORE_VERSION = 1;

	private String storePath;
	
	private Group mainGroup;

	private Stage myStage;
	
	private Settings settings;
	private AWStore store;
	private WindowClosingAdapter windowClosingAdapter;

	private GregorianCalendar displayedDate = new GregorianCalendar();
	private Timer activeTimer;

	// UI Controls
	
//	private TextField txt_newActivity;
//	private VBox mainLayout;
//	private JXDatePicker datePicker = new JXDatePicker();
	private List<Timer> activityTimers;
	private VBox activitiesLayout;

	private TextField txt_newActivity;
	private VBox mainLayout;

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		if( storePath == null)
		{
			storePath = System.getProperty("user.home")+File.separator+AWSTORE_XML;
		}

		mainGroup = new Group();
	    Scene scene = new Scene(mainGroup);

		loadSettings();
	    store = new AWStore();
		settings = new Settings();
		store.setSettings(settings);

		store.setActivities(new ArrayList<Activity>());
		loadActivities();

		addUIControls();

		windowClosingAdapter = new WindowClosingAdapter(true, store, storePath);

		if(store != null && store.getActivitiyList() != null) {
			showActivities();
		}

		myStage = primaryStage;
		myStage.setTitle(PRG_NAME);
		
	    myStage.setScene(scene);
	    myStage.sizeToScene();
	    myStage.show();

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		String storePath = null;
//		for(String arg : args)
//		{
//			if(arg.startsWith("--settings-file="))
//			{
//				storePath = arg.replaceFirst("--settings-file=", "");;
//			}
//		}
//		if( storePath == null)
//		{
//			storePath = System.getProperty("user.home")+File.separator+AWSTORE_XML;
//		}
//		
		launch(args);
	}

	private void addUIControls() {
		mainLayout = new VBox();
		txt_newActivity = new TextField();
		txt_newActivity.setPromptText("add new activity");
		txt_newActivity.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				if(txt_newActivity.getText().isEmpty()) {
					return;
				}
				addActivity(txt_newActivity.getText());
				txt_newActivity.clear();
				txt_newActivity.requestFocus();
			}
		});
		
		mainLayout.getChildren().add(txt_newActivity);
		
//		datePicker.setDate(displayedDate.getTime());
//		mainLayout.getChildren().add(datePicker);

		activitiesLayout = new VBox();
		mainLayout.getChildren().add(activitiesLayout);
		
		mainGroup.getChildren().add(mainLayout);
	}
	
	private void addActivity(String name) {
		Activity activity = new Activity();
		activity.setName(name);
		activity.setStartDate(displayedDate.getTime());
		store.getActivitiyList().add(activity);
		
		Timer timer = new Timer(activity);
//		timer.displayCounter(store.getSettings().isCountersVisible());
		activityTimers.add(timer);
		activitiesLayout.getChildren().add(new Timer(activity));
//		activityPane.add(timer);
		timer.start();
		activeTimer = timer;
//		btn_stop.setEnabled(true);
//		enableCorrectionPane(true);
//		this.pack();
		windowClosingAdapter.saveActivities(store);

		resizeWindow();
	}
	
	private void resizeWindow() {
		myStage.sizeToScene();
	}
	
	private void loadSettings() {
		
	    store = new AWStore();
		settings = new Settings();
		store.setSettings(settings);

		store.setActivities(new ArrayList<Activity>());

//		StatusBar.setMessage("Loading Activities...");
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(AWStore.class);
			Unmarshaller um = context.createUnmarshaller();
			store = (AWStore) um.unmarshal(new FileReader(storePath));
		} catch (JAXBException e) {
			JOptionPane.showMessageDialog(null, e.getLocalizedMessage(),"Error while loading activities", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} catch (FileNotFoundException e) {
//			System.out.println("No AWStore.xml File found. Creating new activities...");
		}
		if( store.getVersion() != AWSTORE_VERSION ) {
			migrateStore();
//			showWelcomeMessage();
		}
		store.setVersion(AWSTORE_VERSION);
		
		if( store.getSettings() != null ) {
//			statusBar.setVisible(store.getSettings().isStatusBarVisible());
//			item_showStatusBar.setSelected(store.getSettings().isStatusBarVisible());
//			this.setAlwaysOnTop(store.getSettings().isAlwaysOnTop());
//			item_alwaysOnTop.setSelected(store.getSettings().isAlwaysOnTop());
		}
		else
		{
			store.setSettings(settings);
		}
		System.out.println("Loaded "+store.getActivitiyList().size()+" activities.");
//		StatusBar.setMessage("Loaded "+store.getActivitiyList().size()+" activities.", 3000);
		if( this.activityTimers == null ) {
			this.activityTimers = new ArrayList<>();
		}
	}

	private void migrateStore() {
		if( store.getVersion() == 0 ) {
			store.getSettings().setCountersVisible(true);
			store.getSettings().setStatusBarVisible(true);
			store.getSettings().setAlwaysOnTop(false);
		}
	}
	
	private void removeActivities() {
		activitiesLayout.getChildren().clear();
	}

	private void showActivities() {
		removeActivities();
		activityTimers = new ArrayList<>();
		for(Activity a : store.getActivitiyList()) {
			GregorianCalendar tcal = new GregorianCalendar();
			tcal.setTime(a.getStartDate());
			if( displayedDate.get(Calendar.DAY_OF_MONTH) == tcal.get(Calendar.DAY_OF_MONTH)
				&& displayedDate.get(Calendar.MONTH) == tcal.get(Calendar.MONTH)
				&& displayedDate.get(Calendar.YEAR) == tcal.get(Calendar.YEAR)) {
					Timer timer = new Timer(a);
					activityTimers.add(timer);
					activitiesLayout.getChildren().add(timer);
				}
		}
//		showCounters(store.getSettings().isCountersVisible());
//		pane.add(activityPane, pane.getComponents().length -2);
//		this.pack();
	}

//		mainLayout.getChildren().add(new Timer(activity));
//		store.getActivitiyList().add(activity);
//		resizeWindow();
//	}
//	
//	private void resizeWindow() {
//		myStage.sizeToScene();
//	}
	
	private void loadActivities() {
//		StatusBar.setMessage("Loading Activities...");
//		JAXBContext context;
//		try {
//			context = JAXBContext.newInstance(AWStore.class);
//			Unmarshaller um = context.createUnmarshaller();
//			store = (AWStore) um.unmarshal(new FileReader(storePath));
//		} catch (JAXBException e) {
//			JOptionPane.showMessageDialog(null, e.getLocalizedMessage(),"Error while loading activities", JOptionPane.ERROR_MESSAGE);
//			e.printStackTrace();
//		} catch (FileNotFoundException e) {
//			System.out.println("No AWStore.xml File found. Creating new activities...");
//		}
//		if( store.getVersion() != AWSTORE_VERSION ) {
//			migrateStore();
//			showWelcomeMessage();
//		}
//		store.setVersion(AWSTORE_VERSION);
//		
//		if( store.getSettings() != null ) {
//			statusBar.setVisible(store.getSettings().isStatusBarVisible());
//			item_showStatusBar.setSelected(store.getSettings().isStatusBarVisible());
//			this.setAlwaysOnTop(store.getSettings().isAlwaysOnTop());
//			item_alwaysOnTop.setSelected(store.getSettings().isAlwaysOnTop());
//		}
//		else
//		{
//			store.setSettings(settings);
//		}
//		StatusBar.setMessage("Loaded "+store.getActivitiyList().size()+" activities.", 3000);
	}

}
