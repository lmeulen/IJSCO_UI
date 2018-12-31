package nl.detoren.ijsco.io;

import java.io.FileWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;

import nl.detoren.ijsco.ui.control.IJSCOController;
import nl.detoren.ijsco.ui.util.SendAttachmentInEmail;
import nl.detoren.ijsco.ui.util.Utils;
import nl.detoren.ijsco.data.Groep;
import nl.detoren.ijsco.data.Groepen;
import nl.detoren.ijsco.data.GroepsUitslag;
import nl.detoren.ijsco.data.GroepsUitslagen;
import nl.detoren.ijsco.data.Toernooi;
import nl.detoren.ijsco.data.WedstrijdUitslag;


public class OutputUitslagen implements GroepenExportInterface{

    private final static Logger logger = Logger.getLogger(IJSCOController.class.getName());
    private final static String ls = System.lineSeparator();
	private Toernooi toernooi = IJSCOController.t();

	public boolean export(Groepen groepen) {
		try {
			String bestandsnaam = "Uitslagen.txt";
			logger.log(Level.INFO, "Sla uitslag op in bestand " + bestandsnaam);
			String result = "";
			result += ls + "-----------------------------" + ls + ls;
			for (Groep g : groepen) { 
				result += g.wedstrijdentoString() + ls;
			}
			FileWriter writer = new FileWriter(bestandsnaam);
			writer.write(result);
			writer.write(ls + "Aangemaakt met " + IJSCOController.c().appTitle + " " + IJSCOController.getAppVersion());
			writer.close();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "An " + e.getMessage() + " occured ");
			return false;
		}
		return true;
	}

	public void exportuitslagen(GroepsUitslagen groepenuitslagen) {
		try {
			String bestandsnaam = "Uitslagen.txt";
			logger.log(Level.INFO, "Sla uitslag op in bestand " + bestandsnaam);
			String result = "";
			result += toernooi.toString();
			result += ls + "-----------------------------" + ls + ls;
			for (GroepsUitslag g : groepenuitslagen) { 
				result += g.wedstrijdentoString() + ls;
			}
			FileWriter writer = new FileWriter(bestandsnaam);
			writer.write(result);
			writer.write(ls + "Aangemaakt met " + IJSCOController.c().appTitle + " " + IJSCOController.getAppVersion());
			writer.close();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "An " + e.getMessage() + " occured ");
		}
	}

	public void exportJSON(GroepsUitslagen groepenuitslagen) {
		try {
			String bestandsnaam = "Uitslagen.json";
			logger.log(Level.INFO, "Sla uitslag.json op in bestand " + bestandsnaam);
			for (GroepsUitslag g : groepenuitslagen) { 
				for (WedstrijdUitslag u : g.getWedstrijden()) {
				 toernooi.addUitslag(u);
				}
			}
			Gson gson = new Gson();
			String jsonString = gson.toJson(toernooi);
			// write converted json data to a file
			FileWriter writer = new FileWriter(bestandsnaam);
			writer.write(jsonString);
			writer.close();
			SendAttachmentInEmail SAIM = new SendAttachmentInEmail();
			SAIM.sendAttachement("Uitslagen.json");
			}
		catch (Exception e)
		{
			logger.log(Level.SEVERE, "An " + e.getMessage() + " occured ");
		}
	}

	public void exporteindresultaten(GroepsUitslagen groepenuitslagen) {
		try {
			String bestandsnaam = "Eindresultaten.txt";
			logger.log(Level.INFO, "Sla eindresultaat op in bestand " + bestandsnaam);
			String result = "";
			result += ls + "-----------------------------" + ls + ls;
			result += groepenuitslagen.ToString();
			FileWriter writer = new FileWriter(bestandsnaam);
			writer.write(result);
			writer.write(ls + "Aangemaakt met " + IJSCOController.c().appTitle + " " + IJSCOController.getAppVersion());
			writer.close();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "An " + e.getMessage() + " occured ");
		}
	}

}
