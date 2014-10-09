package org.bravo.activitywatch;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import org.apache.commons.lang3.SystemUtils;
import org.bravo.activitywatch.entity.AWVersion;
import org.bravo.activitywatch.entity.AWVersion.Platform;

public class ActivityWatch extends Application {

	/* Application Properties
	 * Should be more comfortable for platform dependent builds
	 */
	public static final String PRG_NAME = "ActivityWatch";
	public static final String PRG_VERSION = "0.11";
	public static final String AWSTORE_XML = "AWStore.xml";
	public static final int AWSTORE_VERSION = 3;
	public static final int AWVERSION_ID = 3;
	public static AWVersion.Platform PLATFORM;// = AWVersion.Platform.MAC;

	private TrayMenu trayMenu = TrayMenu.getInstance();
	
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
	        
	        // Show icon in Window Headline
//	        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("images/aw-logo-64.png"),64,64,false,false));
	        
	        primaryStage.setMinHeight(200L);
	        primaryStage.setTitle(PRG_NAME);
	        primaryStage.setScene(scene);
	        scene.getStylesheets().add(ActivityWatch.class.getResource("ActivityWatch.css").toExternalForm());
	        primaryStage.show();
	        primaryStage.setMinWidth(primaryStage.getWidth());
	        primaryStage.setMaxWidth(primaryStage.getWidth());
	        
	        javafx.application.Platform.setImplicitExit(false);
	        
	        // add MAC style. Experimental
//	        if(PLATFORM.equals(Platform.MAC)) {
//	        	AquaFx.style();
//	        }
			trayMenu.createTrayIcon(primaryStage);
	}
	
	@Override
	public void stop() throws Exception {
		ActivityManager.getInstance().saveActivities();
		super.stop(); // for JavaFX Shutdown
		System.exit(0); // for AWT Shutdown
	}

	public static void main(String[] args) {
		if (SystemUtils.IS_OS_MAC) {
			PLATFORM = Platform.MAC;
		} else 
			if (SystemUtils.IS_OS_UNIX) {
			PLATFORM = Platform.UNIX;
		} else {
			PLATFORM = Platform.WIN;
		}
		launch(args);
	}
	
}
