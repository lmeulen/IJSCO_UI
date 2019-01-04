	/**
 * Copyright (C) 2018 Lars Dam
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation version 3.0
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * See: http://www.gnu.org/licenses/gpl-3.0.html
 *
 * Problemen in deze code:
 */
package nl.detoren.ijsco.ui.control;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;

import nl.detoren.ijsco.Configuratie;
import nl.detoren.ijsco.data.Status;
import nl.detoren.ijsco.data.Toernooi;

public class IJSCOController {

    private static volatile IJSCOController instance = null;

    private final static Logger logger = Logger.getLogger(IJSCOController.class.getName());

    private static final String defaultInputfile = "uitslag.txt";

	private static String appVersion = "0.3.1.2";
	
    private Status status;

    protected IJSCOController() {
    	status = new Status();
    }    
    
    public static IJSCOController getInstance() {
        if (instance == null) {
            instance = new IJSCOController();
        }
        return instance;
    }

    public static String getAppVersion() {
    	return appVersion;
    }

    public static IJSCOController getI() {
    	return getInstance();
    }

    public static Configuratie c() {
    	return getInstance().status.config;
    }

    public static Toernooi t() {
    	return getInstance().status.toernooi;
    }

    public Status getStatus() {
    	return status;
    }

    public void setStatus(Status _status) {
    	status = _status;
    }

    public void setConfig(Configuratie _config) {
    	status.config = _config;
    }

    public void setToernooi(Toernooi _toernooi) {
    	status.toernooi = _toernooi;
    }

	/**
     * Save state of the application to disk
	 * @param unique if true, a unique file is created with timestamp in filename
	 * @param postfix post fix of filename, before extension. Only used in combination with unique = true
	 */
    public void saveState(boolean unique, String postfix) {
		try {
			String bestandsnaam = c().statusBestand + ".json";
			logger.log(Level.INFO, "Sla status op in bestand " + bestandsnaam);
			Gson gson = new Gson();
			String jsonString = gson.toJson(status);
			// write converted json data to a file
			FileWriter writer = new FileWriter(bestandsnaam);
			writer.write(jsonString);
			writer.close();

/*			if (c.saveAdditionalStates && unique) {
				String s = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
				bestandsnaam = c.statusBestand + s + "-" + postfix + ".json";
				logger.log(Level.INFO, "Sla status op in bestand " + bestandsnaam);
				// write converted json data to a file
				String dirName = "R" + status.wedstrijdgroepen.getPeriode() + "-" + status.wedstrijdgroepen.getRonde();
				new File(dirName).mkdirs();
				writer = new FileWriter(dirName + File.separator + bestandsnaam);
				writer.write(jsonString);
				writer.close();
			}*/
/*			bestandsnaam = c().configuratieBestand + ".json";
			logger.log(Level.INFO, "Sla configuratie op in bestand " + bestandsnaam);
			// write converted json data to a file
			writer = new FileWriter(bestandsnaam);
			jsonString = gson.toJson(c());
			writer.write(jsonString);
			writer.close();
*/
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    public void start() {
        leesStatusBestand();
    }

	public boolean leesStatus() {
		return leesStatus(c().statusBestand + ".json");
	}

	public boolean leesStatus(String bestandsnaam) {
		try {
	    	logger.log(Level.INFO, "Lees status uit bestand " + bestandsnaam);
			Gson gson = new Gson();
			BufferedReader br = new BufferedReader(new FileReader(bestandsnaam));
			Status nieuw = gson.fromJson(br, Status.class);
			status = nieuw;	// assure exception is thrown when things go wrong
			
			return true;
		} catch (Exception e) {
			// Could not read status
			return false;
		}
	}

    
	/**
	 * Lees het status export bestand
	 * @return true, als bestand gevonden en ingelezen
	 */
	public boolean leesStatusBestand() {
		synchronized (this) {
        	logger.log(Level.INFO, "Lees status");
        	leesStatus("status.json");
        	//leesConfiguratie();
			if ((status == null)) {
				status = new Status();
	        	logger.log(Level.INFO, "Status bestand niet ingelezen");
				return false;
			}
			if ((status.config == null)) {
				status.config = new Configuratie();
			}
			if ((status.toernooi == null)) {
				status.toernooi = new Toernooi();
			}
		}
    	logger.log(Level.INFO, "Statusbestand ingelezen");
		return true;
	}

}
