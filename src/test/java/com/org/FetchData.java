package com.org;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.org.LibGlobal;

//C. API
public class FetchData extends LibGlobal {

	@Test
	public void test1() throws java.text.ParseException {

		logManager("FetchData");

		// Specify the API endpoint URL
		String apiUrl = "https://data.sfgov.org/resource/p4e4-a5a7.json";

		// Send an HTTP GET request using RestAssured
		Response response = RestAssured.get(apiUrl);
		if (response.getStatusCode() == 200) {
			// Retrieve the JSON response
			List<String> jsonResponse = response.jsonPath().getList("$");
			System.out.println(jsonResponse);
			// Generate a timestamp for filenames
			Calendar c = Calendar.getInstance();
			String timeStamp = new SimpleDateFormat("MM-dd-yy-HH-mm-ss").format(c.getTime());
			System.out.println(timeStamp);
			String path = getProperty() + "\\briq";
			// Create a JSON file
			String jsonFileName = "sfgov_" + timeStamp + ".json";
			File file = new File(path, jsonFileName);
			try {
				// Create the csv file
				boolean fileCreated = file.createNewFile();

				if (fileCreated) {
					System.out.println("File created successfully at: " + file.getAbsolutePath());
				} else {
					System.out.println("File already exists at: " + file.getAbsolutePath());
				}
			} catch (IOException e) {
				System.err.println("An error occurred while creating the file: " + e.getMessage());
			}
			try {
				FileOutputStream jsonFile = new FileOutputStream(file);
				jsonFile.write(response.getBody().asByteArray());
				jsonFile.close();
				System.out.println("JSON file created: " + file);
			} catch (IOException e) {
				e.printStackTrace();
			}
			String asString = response.getBody().asString();
			System.out.println(asString);
			JSONArray jsonArray = new JSONArray(asString);
			// Create an Excel workbook
			String excelFileName = "sfgov_" + timeStamp + ".CSV";

			File loc = new File(path, excelFileName);
			try {
				// Create the text file
				boolean fileCreated = loc.createNewFile();

				if (fileCreated) {
					System.out.println("File created successfully at: " + loc.getAbsolutePath());
				} else {
					System.out.println("File already exists at: " + loc.getAbsolutePath());
				}
			} catch (IOException e) {
				System.err.println("An error occurred while creating the file: " + e.getMessage());
			}

			Workbook workbook = new XSSFWorkbook();
			Sheet sheet = workbook.createSheet("SFGov Data");
			int rowNum = 0;
			Row headerRow = sheet.createRow(rowNum++);
			headerRow.createCell(0).setCellValue("description");
			headerRow.createCell(1).setCellValue("timestamp");
			headerRow.createCell(2).setCellValue("is_roof");
			// Add more headers here
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				Row row = sheet.createRow(i + 1);
				// Format timestamp as a date (bonus)
				Cell timestampCell = row.createCell(1);
				timestampCell.setCellValue(c.getTime());
				CellStyle dateStyle = workbook.createCellStyle();
				dateStyle.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("MM-dd-yyyy"));
				timestampCell.setCellStyle(dateStyle);
				row.createCell(0).setCellValue(jsonObject.getString("description"));
				row.createCell(2).setCellValue(jsonObject.getString("description").contains("roof") ? "Yes" : "No");
			}
			// Save the Excel workbook to a file (bonus)
			try {
				FileOutputStream excelFile = new FileOutputStream(loc);
				workbook.write(excelFile);
				excelFile.close();
				System.out.println("Excel file created: " + loc);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		} else {
			System.err.println("HTTP GET request failed with response code: " + response.getStatusCode());
		}

	}
}
