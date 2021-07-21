package com.dashboard.components.graphs;

import java.util.HashMap;
import java.util.Map;

import com.sothawo.mapjfx.Coordinate;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class TestGraphics extends Application{
	public static void main(String[] args) {
		launch(args);
	}
	
	@Override
	public void start(Stage window) throws Exception {
		Map<Coordinate, Integer> mapa = new HashMap<Coordinate, Integer>();
		mapa.put(new Coordinate(-21.98761654615617, -48.783849369628065), 500000);
		MapChart m = new MapChart(mapa);
		
		
		BarChart<String, Number> l = CovidBarCharts.getNewCasesPerDate("Birigui");
		//LineChart<String, Number> l2 = CovidLineCharts.getLineChartNumberString("Teste 2", "X", "Y");
		
		StackPane root = new StackPane();
		root.getChildren().add(m.getMap());
		Scene scene = new Scene(root, 500, 500);
		
		window.setScene(scene);
		
		window.show();
	}

}
