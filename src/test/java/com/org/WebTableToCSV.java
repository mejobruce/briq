package com.org;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import lib.org.LibGlobal;

// A. Selenium - Web Table to CSV Generat
public class WebTableToCSV extends LibGlobal {
	@Before
	public void loadDriver() {
		logManager("WebTableToCSV");
		log.info("loading properties file");
		readConfig();
		log.info(" launch the Browser");
		launchBrowser();
		// driver.get("http://the-internet.herokuapp.com/challenging_dom");
		loadUrl("http://the-internet.herokuapp.com/challenging_dom");
		pageLoadTime(10);

	}

	@Test
	public void testCase1() throws IOException {

		log.info("To find the elements from the webtable");
		// Find the table element
		WebElement table = driver.findElement(By.tagName("table"));
		System.out.println(table);
		// Get all the rows in the table
		List<WebElement> rows = table.findElements(By.tagName("tr"));
		System.out.println(rows);
		// Create a list to store the table data
		List<List<String>> tableData = new ArrayList<>();

		// Iterate through each row and get the cell data
		for (WebElement row : rows) {
			List<WebElement> cells = row.findElements(By.tagName("td"));
			List<String> rowData = new ArrayList<>();
			for (WebElement cell : cells) {
				System.out.println(cell.getText());
				rowData.add(cell.getText());
			}
			tableData.add(rowData);
		}

		// Generate the CSV file name

		Calendar c = Calendar.getInstance();
		String timeStamp = new SimpleDateFormat("MM-dd-yy-HH-mm-ss").format(c.getTime());
		System.out.println(timeStamp);
		String fileName = "C:\\\\Users\\\\office\\\\eclipse-workspace-latest\\\\BriqAutomation\\\\briq" + timeStamp
				+ ".csv";

		// Write the table data to the CSV file using FileOutputStream
		try (FileOutputStream fos = new FileOutputStream(fileName)) {
			for (List<String> rowData : tableData) {

				System.out.println(rowData);
				fos.write(String.join(",", rowData).getBytes());
				fos.write("\n".getBytes());
			}
		} catch (FileNotFoundException e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

	@After
	public void teardown() {
		log.info("Tear Down method executed..");
		driver.manage().deleteAllCookies();
		driver.quit();

	}

}
