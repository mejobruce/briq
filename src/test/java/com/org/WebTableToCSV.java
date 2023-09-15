package com.org;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
		Calendar c = Calendar.getInstance();
		String timeStamp = new SimpleDateFormat("MM-dd-yy-HH-mm-ss").format(c.getTime());
		System.out.println(timeStamp);
		String path = getProperty() + "\\briq";
		String fileName = "webtable_"+ timeStamp + ".CSV";
		File file= new File(path,fileName);
		 try {
	            // Create the text file
	            boolean fileCreated = file.createNewFile();

	            if (fileCreated) {
	                System.out.println("File created successfully at: " + file.getAbsolutePath());
	            } else {
	                System.out.println("File already exists at: " + file.getAbsolutePath());
	            }
	        } catch (IOException e) {
	            System.err.println("An error occurred while creating the file: " + e.getMessage());
	        }
		
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("WebTable");
		WebElement table = driver.findElement(By.tagName("table"));
		List<WebElement> allRows = table.findElements(By.tagName("tr"));
		int rowNum = 0;
		// Get the headers
		WebElement headerRow = allRows.get(0);
		List<WebElement> headers = headerRow.findElements(By.tagName("th"));
		Row sheetRow = sheet.createRow(rowNum++);
		int colNum = 0;
		for (WebElement header : headers) {
			if (colNum < headers.size() - 1) { // Ignore the last column
				sheetRow.createCell(colNum).setCellValue(header.getText());
			}
			colNum++;
		}

		// Get the data rows
		for (int i = 1; i < allRows.size(); i++) {
			WebElement row = allRows.get(i);
			List<WebElement> cells = row.findElements(By.tagName("td"));
			sheetRow = sheet.createRow(rowNum++);
			colNum = 0;
			for (WebElement cell : cells) {
				if (colNum < cells.size() - 1) { // Ignore the last column
					sheetRow.createCell(colNum).setCellValue(cell.getText());
				}
				colNum++;
			}
		}

		try {

			FileOutputStream outputStream = new FileOutputStream(file);
			workbook.write(outputStream);
			outputStream.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			// TODO: handle exception
		} catch (IOException e) {
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
