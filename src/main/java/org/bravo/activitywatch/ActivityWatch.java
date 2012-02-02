package org.bravo.activitywatch;

import java.util.List;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ActivityWatch extends Application {

	private TextField txt_newActivity;
	private VBox mainLayout;

	private Group mainGroup;

	private List<Activity> activities;

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		mainGroup = new Group();
	    Scene scene = new Scene(mainGroup);

		addUIControls();

	    primaryStage.setScene(scene);
	    primaryStage.sizeToScene();
		primaryStage.show();

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}


	private void addUIControls() {
		mainLayout = new VBox();
		txt_newActivity = new TextField();
		txt_newActivity.setPromptText("add new activity");
		txt_newActivity.setOnAction(new EventHandler<ActionEvent>() {
			
			@Override
			public void handle(ActionEvent event) {
				Activity activity = new Activity();
				activity.setName(txt_newActivity.getText());
				mainLayout.getChildren().add(new Timer(activity));
				activities.add(activity);
			}
		});
		
		mainLayout.getChildren().add(txt_newActivity);
		
		mainGroup.getChildren().add(mainLayout);
	}
}
