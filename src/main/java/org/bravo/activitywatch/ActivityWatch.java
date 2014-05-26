package org.bravo.activitywatch;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import org.bravo.activitywatch.entity.AWVersion;

public class ActivityWatch extends Application {

	public static final String PRG_NAME = "ActivityWatch";
	public static final String PRG_VERSION = "0.9";
	public static final String AWSTORE_XML = "AWStore-dev.xml";
	public static final int AWSTORE_VERSION = 3;
	public static final int AWVERSION_ID = 1;
	public static final AWVersion.Platform PLATFORM = AWVersion.Platform.MAC;
	
	@Override
	public void start(Stage primaryStage) throws Exception {

	        FXMLLoader loader = new FXMLLoader();
	        Parent root = (Parent)loader.load(MainWindowController.class.getResourceAsStream("MainWindow.fxml"));
	        final MainWindowController controller = (MainWindowController)loader.getController();
	        primaryStage.addEventHandler(WindowEvent.WINDOW_SHOWN, new EventHandler<WindowEvent>()
	        {
	            @Override
	            public void handle(WindowEvent window)
	            {
	                controller.handleWindowShownEvent();
	            }
	        });
		
	        Scene scene = new Scene(root);
	        primaryStage.setMinWidth(300L);
	        primaryStage.setWidth(300L);
	        primaryStage.setMaxWidth(300L);
	        primaryStage.setMinHeight(200L);
	        primaryStage.setTitle(PRG_NAME);
	        primaryStage.setScene(scene);
	        scene.getStylesheets().add(ActivityWatch.class.getResource("ActivityWatch.css").toExternalForm());
	        primaryStage.show();
	}
	
	@Override
	public void stop() throws Exception {
		ActivityManager.getInstance().saveActivities();
		super.stop();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
