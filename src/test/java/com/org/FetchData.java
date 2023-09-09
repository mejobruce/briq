package com.org;

import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Test;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.org.LibGlobal;

//C. API
public class FetchData extends LibGlobal {
	
	@Test
	public void test1() throws java.text.ParseException {
		
		logManager("FetchData");
		log.info(" Send a GET request to the API URL");
		// Step 1: Send a GET request to the API URL
        Response response = RestAssured.get("https://data.sfgov.org/resource/p4e4-a5a7.json");

        if (response.getStatusCode() == 200) {
        	log.info("Parse the JSON response");
            // Step 2: Parse the JSON response
            JsonPath jsonPath = response.jsonPath();
            List<JSONObject> data = jsonPath.getList("$");

            
            Calendar c = Calendar.getInstance();
            log.info("Format the timestamp fields as dates (mm-dd-yyyy)");
            // Step 3: Format the timestamp fields as dates (mm-dd-yyyy)
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
                     
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("MM-dd-yyyy");

            for (JSONObject item : data) {
                String timestamp = item.get("timestamp").toString();
                String date = inputDateFormat.format(c.getTime());
				item.put("timestamp", outputDateFormat.format(c.getTime()));
				
			log.info("Step 4: Add the is_roof field");

                // Step 4: Add the "is_roof" field
                String description = item.get("description").toString();
                item.put("is_roof", description.toLowerCase().contains("roof"));
            }

            
            log.info("Export data as a JSON file");
            // Step 5: Export data as a JSON file
            SimpleDateFormat timestampFormat = new SimpleDateFormat("MM-dd-yy-HH-mm-ss");
            String timestamp = timestampFormat.format(c.getTime());
            String jsonFileName = "/HOME/briq/sfgov_" + timestamp + ".json";

            try (FileWriter fileWriter = new FileWriter(jsonFileName)) {
                JSONArray jsonArray = new JSONArray();
                jsonArray.addAll(data);
                fileWriter.write(jsonArray.toJSONString());
                System.out.println("JSON file created successfully: " + jsonFileName);
            } catch (IOException e) {
                e.printStackTrace();
            }

              log.info("Export data as a CSV file");
            // BONUS - Step 6: Export data as a CSV file
            String csvFileName = "/HOME/briq/sfgov_" + timestamp + ".csv";
            try (FileWriter csvFileWriter = new FileWriter(csvFileName)) {
                csvFileWriter.append("timestamp,description,is_roof\n");
                for (JSONObject item : data) {
                    String csvRow = item.get("timestamp") + "," +
                            "\"" + item.get("description") + "\"," +
                            item.get("is_roof");
                    csvFileWriter.append(csvRow + "\n");
                }
                System.out.println("CSV file created successfully: " + csvFileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Failed to fetch data. Status code: " + response.getStatusCode());
        }
	}

}
