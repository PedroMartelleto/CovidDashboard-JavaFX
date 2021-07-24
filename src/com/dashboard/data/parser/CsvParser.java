package com.dashboard.data.parser;

import java.io.StringReader;
import java.util.List;

import com.opencsv.CSVReader;

public class CsvParser {
	public static List<String[]> getAllContent(String csvContent) {
		try {
			CSVReader reader = new CSVReader(new StringReader(csvContent));
			List<String[]> csv = reader.readAll();
			reader.close();
			return csv;
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
	}
}