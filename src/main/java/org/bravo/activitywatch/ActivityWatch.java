package org.bravo.activitywatch;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ActivityWatch extends Application {

	public static final String PRG_NAME = "ActivityWatch";
	public static final String PRG_VERSION = "0.9";
	public static final String AWSTORE_XML = "AWStore-dev.xml";
	public static final int AWSTORE_VERSION = 1;
	
//	private WindowClosingAdapter windowClosingAdapter;

	@Override
	public void start(Stage primaryStage) throws Exception {

	       Parent root = FXMLLoader.load(getClass().getResource("MainWindow.fxml"));
	       
	        Scene scene = new Scene(root, 500, 300);
	    
	        primaryStage.setTitle(PRG_NAME);
	        primaryStage.setScene(scene);
	        scene.getStylesheets().add(ActivityWatch.class.getResource("ActivityWatch.css").toExternalForm());
	        primaryStage.show();

//		windowClosingAdapter = new WindowClosingAdapter(true, store, storePath);

//	    myStage.sizeToScene();
//	    myStage.show();

	}
	
	@Override
	public void stop() throws Exception {
		System.out.println("Closing application. Performing some actions...");
		CoreController.getInstance().stopAllTimers(); 
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
