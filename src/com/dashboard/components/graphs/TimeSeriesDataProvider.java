package com.dashboard.components.graphs;

import java.util.ArrayList;

import com.dashboard.data.common.BrazilData;
import com.dashboard.data.importer.ChartsImporter;
import com.dashboard.data.model.LineChartDataModel;

import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;

public class TimeSeriesDataProvider {
	private static LineChartDataModel[] data;
	private static ChartsImporter importer = new ChartsImporter();

	public static void vaccinations(XYChart<String, Number> chart, int stride, boolean relativeNumbers, String stateName) {
		data = importer.getVaccinationsLineChart();
		chart.setAnimated(true);
		provideLabels(chart, "Vacinação acumulada por dia", "Data", "Quantidade");
		
		float normFactor = relativeNumbers ? BrazilData.getPopulation(stateName) : 1;
		String chartNameSuffix = relativeNumbers ? "(números relativos)" : "(números absolutos)"; 
		provideSeries(chart, "Total de vacinas aplicadas " + chartNameSuffix, data[0].getPoints(), stride, normFactor);
		provideSeries(chart, "Pessoas Vacinadas " + chartNameSuffix, data[1].getPoints(), stride, normFactor);
		provideSeries(chart, "Pessoas Totalmente Vacinadas" + chartNameSuffix, data[2].getPoints(), stride, normFactor);
	}
	
	private static void provideLabels(XYChart<String, Number> destChart, String title, String xLabel, String yLabel) {
		destChart.getXAxis().setLabel(xLabel);
		destChart.getXAxis().setLabel(yLabel);
		destChart.setTitle(title);
	}
	
	/**
	 * Provides time series data to the destination chart. The resulting data is divided by norm factor and is interpolated using neighbor points.
	 * Stride defines the number of neighbor points to use.
	 * 
	 * @param destChart
	 * @param name
	 * @param data
	 * @param stride the number of neighbors from which to take the mean.
	 * @param normFactor the data is divided by this factor.
	 * @param isAscending if true, enforces the time series to be ascending.
	 */
	private static void provideSeries(XYChart<String, Number> destChart, String name,
			ArrayList<Data<String, Number>> data, int stride, float normFactor, boolean isAscending) {
		XYChart.Series<String, Number> series = new XYChart.Series<>();
		
		for (int i = 0; i < data.size(); i += stride) {
			// Interpolates the values by taking the mean
			float interpolatedValue = 0.0f;
			
			for (int j = i; j < i + stride; ++j) {
				int value = (int) data.get(j).getYValue();
				
				// Ensures time series is ascending (when isAscending is true).
				if (j > 0 && isAscending) {
					int previousValue = (int) data.get(j-1).getYValue();
					if (value <= previousValue) {
						value = previousValue;
					}
				}
				
				interpolatedValue += value;
			}
			
			interpolatedValue /= (stride * normFactor);
			
			String label = data.get(i + stride/2).getXValue();
			series.getData().add(new XYChart.Data<String, Number>(label, interpolatedValue));
		}
		
		series.setName(name);
		destChart.getData().add(series);
	}

}
