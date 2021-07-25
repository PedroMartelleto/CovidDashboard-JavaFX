package com.dashboard.components.graphs;

import java.util.ArrayList;
import java.util.List;

import com.dashboard.data.common.BrazilData;
import com.dashboard.data.importer.CSSEGISandOwidImporter;
import com.dashboard.data.model.LineChartDataModel;

import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;

public class TimeSeriesDataProvider {
	private static CSSEGISandOwidImporter importer = new CSSEGISandOwidImporter();
	
	public static int vaccinatedLast7DaysNumber = 0;
	public static int oneDosesNumber = 0;
	public static int twoDosesNumber = 0;
	public static float monthsTo70percent = 0.0f;

	/**
	 * Provides data for the accumulated vaccine doses chart.
	 * 
	 * @param chart Destination chart.
	 * @param stride Number of neighbors from which to average.
	 * @param relativeNumbers Set this to true to display a graph in percentages.
	 */
	public static void vaccinations(XYChart<String, Number> chart, int stride, boolean relativeNumbers) {
		LineChartDataModel[] data = importer.getVaccinationsLineChart();
		calculateVaccinationConstants(data);
		chart.setAnimated(true);
		provideLabels(chart, "Vacinação acumulada por dia", "Data", "Quantidade");
		
		float normFactor = relativeNumbers ? BrazilData.getBrazilPopulation() : 1;
		String chartNameSuffix = relativeNumbers ? "(números relativos)" : "(números absolutos)"; 
		provideSeries(chart, "Pessoas vacinadas " + chartNameSuffix, data[1].getPoints(), stride, normFactor, true);
		provideSeries(chart, "Pessoas totalmente vacinadas " + chartNameSuffix, data[2].getPoints(), stride, normFactor, true);
	}
	
	private static void calculateVaccinationConstants(LineChartDataModel[] data) {
		List<XYChart.Data<String, Number>> partiallyVaccinated = data[1].getPoints();
		List<XYChart.Data<String, Number>> totallyVaccinated = data[2].getPoints();
		
		int lengthTemp = partiallyVaccinated.size();
		Number vaccinatedLastDay = partiallyVaccinated.get(lengthTemp - 1).getYValue();
		Number vaccinated7DaysBefore =  partiallyVaccinated.get(lengthTemp - 8).getYValue();
		vaccinatedLast7DaysNumber = vaccinatedLastDay.intValue() - vaccinated7DaysBefore.intValue();
		
		oneDosesNumber = vaccinatedLastDay.intValue();
		
		lengthTemp = totallyVaccinated.size();
		
		twoDosesNumber = totallyVaccinated.get(lengthTemp - 1).getYValue().intValue();
		
		int speed = twoDosesNumber - totallyVaccinated.get(lengthTemp - 8).getYValue().intValue();
		float nWeeks = ((0.7f*BrazilData.getBrazilPopulation()) -  twoDosesNumber) / speed;
		
		monthsTo70percent = nWeeks / 4.0f;
		
	}
	
	/**
	 * Provides data for the accumulated cases chart.
	 * 
	 * @param chart Destination chart.
	 * @param stride Number of neighbors from which to average.
	 * @param stateName The state from which to get the data.
	 */
	public static void cases(XYChart<String, Number> chart, int stride, String stateName) {
		LineChartDataModel data = importer.getDailyStateCasesLineChart(stateName);
		chart.setAnimated(true);
		provideLabels(chart, "Casos acumulados por dia", "Data", "Quantidade");
		provideSeries(chart, "Casos acumulados por dia", data.getPoints(), stride, 1, true);
	}
	
	/**
	 * Provides data for the accumulated deaths chart.
	 * 
	 * @param chart Destination chart.
	 * @param stride Number of neighbors from which to average.
	 * @param stateName The state from which to get the data.
	 */
	public static void deaths(XYChart<String, Number> chart, int stride, String stateName) {
		LineChartDataModel data = importer.getDailyStateDeathsLineChart(stateName);
		chart.setAnimated(true);
		provideLabels(chart, "Mortes acumuladas por dia", "Data", "Quantidade");
		provideSeries(chart, "Mortes acumuladas por dia", data.getPoints(), stride, 1, true);
	}
	
	/**
	 * Provides labels for dest chart.
	 * 
	 * @param destChart Destination chart.
	 * @param title Title of the graph.
	 * @param xLabel X-axis label.
	 * @param yLabel Y-axis label.
	 */
	private static void provideLabels(XYChart<String, Number> destChart, String title, String xLabel, String yLabel) {
		destChart.getXAxis().setLabel(xLabel);
		destChart.getXAxis().setLabel(yLabel);
		destChart.setTitle(title);
	}
	
	/**
	 * Provides time series data to the destination chart. The resulting data is divided by norm factor and is interpolated using neighbor points.
	 * Stride defines the number of neighbor points to use.
	 * 
	 * @param destChart The destination chart.
	 * @param name The name of the series.
	 * @param data The time series data.
	 * @param stride the number of neighbors from which to take the mean.
	 * @param normFactor the data is divided by this factor.
	 * @param isAscending if true, enforces the time series to be ascending.
	 */
	private static void provideSeries(XYChart<String, Number> destChart, String name,
			ArrayList<Data<String, Number>> data, int stride, float normFactor, boolean isAscending) {		
		XYChart.Series<String, Number> series = new XYChart.Series<>();
		series.setName(name);

		if (data.size() <= 0) return;
		
		for (int i = 0; i < data.size() - 1; i += stride) {
			// Interpolates the values by taking the mean
			float interpolatedValue = 0.0f;
			int total = 0;
			
			for (int j = i; j < i + stride; ++j) {
				if (j < data.size()) {
					int value = (int) data.get(j).getYValue();
					
					// Ensures time series is ascending (when isAscending is true).
					if (j > 0 && isAscending) {
						int previousValue = (int) data.get(j-1).getYValue();
						if (value <= previousValue) {
							value = previousValue;
						}
					}
					
					total += 1;
					interpolatedValue += value;
				}
			}
			
			interpolatedValue /= (total * normFactor);
			
			String label = data.get(Math.min(data.size()-1, i + stride/2)).getXValue();
			series.getData().add(new XYChart.Data<String, Number>(label, interpolatedValue));
		}
		
		destChart.getData().add(series);
	}
}
