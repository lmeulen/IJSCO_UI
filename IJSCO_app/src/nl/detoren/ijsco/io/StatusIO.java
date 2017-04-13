package nl.detoren.ijsco.io;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;

import nl.detoren.ijsco.data.Status;

public class StatusIO {

	private final static Logger logger = Logger.getLogger(StatusIO.class.getName());

	public Status read(String bestandsnaam) {
		try {
			logger.log(Level.INFO, "Lees status uit bestand " + bestandsnaam);
			Gson gson = new Gson();
			BufferedReader br = new BufferedReader(new FileReader(bestandsnaam));
			Status nieuw = gson.fromJson(br, Status.class);
			return nieuw;
		} catch (Exception e) {
			// Could not read status
			System.out.println("Failed to read status : " + e.getMessage());
			return null;
		}
	}

	public void write(Status status) {
		try {
			String bestandsnaam = "status.json";
			logger.log(Level.INFO, "Sla status op in bestand " + bestandsnaam);
			Gson gson = new Gson();
			String jsonString = gson.toJson(status);
			// write converted json data to a file
			FileWriter writer = new FileWriter(bestandsnaam);
			writer.write(jsonString);
			writer.close();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error saving status : " + e.getMessage());
		}
	}
}
