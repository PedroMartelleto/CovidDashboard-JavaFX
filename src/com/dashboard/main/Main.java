package com.dashboard.main;

import javafx.application.Application;
import javafx.stage.Stage;

// TODO: Vaccines page
	// TODO: Overwrite ChoiceBox css
	// TODO: Enable/disable percentages ChoiceBox

// TODO: Undecorated window

public class Main extends Application {
	
	@Override
    public void start(Stage primaryStage) throws Exception {
		// Scenes.init();
		
		primaryStage.setTitle("COVID-19 Dashboard");
		primaryStage.setScene(Scenes.getVaccinations());
		primaryStage.setWidth(1024);
		primaryStage.setHeight(768);
		primaryStage.show();
    }

    public static void main(String[] args) {

    	
		launch(args);
    }
}
