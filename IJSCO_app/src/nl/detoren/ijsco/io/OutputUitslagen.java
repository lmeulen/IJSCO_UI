package nl.detoren.ijsco.io;

import java.io.FileWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.detoren.ijsco.ui.control.IJSCOController;
import nl.detoren.ijsco.data.Groep;
import nl.detoren.ijsco.data.Groepen;
import nl.detoren.ijsco.data.GroepsUitslag;
import nl.detoren.ijsco.data.GroepsUitslagen;


public class OutputUitslagen implements GroepenExportInterface{

    private final static Logger logger = Logger.getLogger(IJSCOController.class.getName());
    private final static String ls = System.lineSeparator();

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
			writer.write(ls + "Aangemaakt met " + IJSCOController.c().appTitle);
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
			result += ls + "-----------------------------" + ls + ls;
			for (GroepsUitslag g : groepenuitslagen) { 
				result += g.wedstrijdentoString() + ls;
			}
			FileWriter writer = new FileWriter(bestandsnaam);
			writer.write(result);
			writer.write(ls + "Aangemaakt met " + IJSCOController.c().appTitle);
			writer.close();
		} catch (Exception e) {
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
			writer.write(ls + "Aangemaakt met " + IJSCOController.c().appTitle);
			writer.close();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "An " + e.getMessage() + " occured ");
		}
	}

}
