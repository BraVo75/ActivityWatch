package org.bravo.activitywatch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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

	// UI Controls
	
	private TextField txt_newActivity;
	private VBox mainLayout;

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		mainGroup = new Group();
	    Scene scene = new Scene(mainGroup);

	    store = new AWStore();
		settings = new Settings();
		store.setSettings(settings);

		store.setActivities(new ArrayList<Activity>());
		loadActivities();

		addUIControls();

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
				addActivity(txt_newActivity.getText());
				txt_newActivity.clear();
			}
		});
		
		mainLayout.getChildren().add(txt_newActivity);
		
		mainGroup.getChildren().add(mainLayout);
	}
	
	private void addActivity(String name) {
		Activity activity = new Activity();
		activity.setName(name);
		mainLayout.getChildren().add(new Timer(activity));
		store.getActivitiyList().add(activity);
		resizeWindow();
	}
	
	private void resizeWindow() {
		myStage.sizeToScene();
	}
	
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
