package org.bravo.activitywatch;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ActivityWatch extends Application {

	public static final String PRG_NAME = "ActivityWatch";
	public static final String PRG_VERSION = "0.9 beta";
	public static final String AWSTORE_XML = "AWStore-dev.xml";
	public static final int AWSTORE_VERSION = 2;
	
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
	        primaryStage.setMinWidth(550L);
	        primaryStage.setMinHeight(200L);
	        primaryStage.setTitle(PRG_NAME);
	        primaryStage.setScene(scene);
	        scene.getStylesheets().add(ActivityWatch.class.getResource("ActivityWatch.css").toExternalForm());
	        primaryStage.show();
	}
	
	@Override
	public void stop() throws Exception {
		ActivityManager.getInstance().saveActivities();
		System.out.println("Shutting down.");
		super.stop();
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

////		datePicker.setDate(displayedDate.getTime());
////		mainLayout.getChildren().add(datePicker);

//	private void resizeWindow() {
//		myStage.sizeToScene();
//	}

}
