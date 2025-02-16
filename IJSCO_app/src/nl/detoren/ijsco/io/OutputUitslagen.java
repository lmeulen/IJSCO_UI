package nl.detoren.ijsco.io;

import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
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
import nl.detoren.ijsco.data.UitslagSpeler;
import nl.detoren.ijsco.data.Wedstrijd;
import nl.detoren.ijsco.data.WedstrijdUitslag;


public class OutputUitslagen implements GroepenExportInterface{

    private final static Logger logger = Logger.getLogger(IJSCOController.class.getName());
    private final static String ls = System.lineSeparator();
	private Toernooi toernooi = IJSCOController.t();

	HashMap<String, String> DIN = createDIN();
	
	private static HashMap<String, String> createDIN() {
	    HashMap<String,String> myMap = new HashMap<String,String>();
	    myMap.put("Player section", "001");
	    myMap.put("Tournament Name", "012");
	    myMap.put("City", "022");
	    myMap.put("Federation", "032");
	    myMap.put("Date of start", "042");	    
	    myMap.put("Date of end", "052");
	    myMap.put("Number of players", "062");
	    myMap.put("Number of rated players", "072");
	    myMap.put("Number of teams", "082");
	    myMap.put("Type of tournament", "092");
	    myMap.put("Chief Arbiter", "102");
	    myMap.put("Deputy Chief Arbiter(s)", "112");
	    myMap.put("Alotted times per moves/game", "122");
	    myMap.put("dates of the rounds (YY/MM/DD)", "132");
	    myMap.put("Team section", "013");
	    return myMap;
	}
	
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
			writer.write(ls + "Aangemaakt met " + IJSCOController.getAppTitle() + " " + IJSCOController.getAppVersion());
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
			result += ls;
			FileWriter writer = new FileWriter(bestandsnaam);
			writer.write(result);
			writer.write(ls + "Aangemaakt met " + IJSCOController.getAppTitle() + " " + IJSCOController.getAppVersion());
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
			result += toernooi.toString();
			result += "De rating berekening is een indicatie op basis van mogelijk beperkte informatie; hieraan kunnen dan ook geen rechten worden ontleend.";
			result += ls + "-----------------------------" + ls + ls;
			result += groepenuitslagen.ToString();
			result += ls;
			FileWriter writer = new FileWriter(bestandsnaam);
			writer.write(result);
			writer.write(ls + "Aangemaakt met " + IJSCOController.getAppTitle() + " " + IJSCOController.getAppVersion());
			writer.close();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "An " + e.getMessage() + " occured ");
		}
	}

	public void exportfide2006(GroepsUitslagen groepenuitslagen) {
		Boolean wit;
		Boolean zwart;
		int startRangTegenstander;
		try {
			
			SimpleDateFormat dateformatter = new SimpleDateFormat("yyyy/MM/dd");
			SimpleDateFormat dateformatter2 = new SimpleDateFormat("yy/MM/dd");
			// Until Rounddates is supplied assume same dates
			ArrayList<Date> roundDates = new ArrayList<Date>();
			//logger.log(Level.INFO, "Grootste groep is " + groepenuitslagen.getGrootsteGroep());
			for (int i=0; i < groepenuitslagen.getGrootsteGroep(); i++) {
				roundDates.add(toernooi.getDatum());
			}
			String bestandsnaam = "Fide2006-Data-Exchange.txt";
			logger.log(Level.INFO, "Aanmaken van Fide2006 DataExchange bestand " + bestandsnaam);
			String result = "";
			// Tournament section
			result += DIN.get("Tournament Name") + " " + toernooi.getBeschrijving() + ls;
			result += DIN.get("City") + " " + toernooi.getPlaats() + ls;
			result += DIN.get("Federation") + " NED" + ls;
			result += DIN.get("Date of start") + " " + dateformatter.format(toernooi.getDatum()) + ls;
			result += DIN.get("Date of end") + " " + dateformatter.format(toernooi.getDatum()) + ls;
			result += DIN.get("Number of players") + " " + groepenuitslagen.getAantalSpelers() + ls;
			result += DIN.get("Number of rated players") + " " + groepenuitslagen.getAantalspelersmetrating() + ls;
			result += DIN.get("Number of teams") + " 0" + ls;
			result += DIN.get("Type of tournament") + " Individual: Round-Robin" + ls;
			result += DIN.get("Chief Arbiter") + " " + ls;
			result += DIN.get("dates of the rounds (YY/MM/DD)") + String.format("%1$86s", "");
			//logger.log(Level.INFO, "Tournament section done. Result is " + ls + result);
			for (int i=0; i < (groepenuitslagen.getGrootsteGroep()-1); i++) {
				result += "  " + String.format("%1$8s", dateformatter2.format(roundDates.get(i)));			
			}
			//logger.log(Level.INFO, "Grootste groep is " + groepenuitslagen.getGrootsteGroep());
			result += ls;
			//
			/////Teams section
			//Not used
			//
			// 
			int totaaleerderegroepen = 0;
			//Players section
			for (GroepsUitslag gu : groepenuitslagen) {
				//logger.log(Level.INFO, "Groep : " + gu.getGroepsnaam());
				for (UitslagSpeler s : gu.getSpelers().values()) {
					// Player info
					result += DIN.get("Player section");
					//logger.log(Level.INFO, "DIN : " + DIN.get("Player section"));
					result += " " + String.format("%1$4s", (totaaleerderegroepen + s.getStartRang()));
//					result += " " + String.format("%1$1s", s.getGeslacht());
// No Sex
					result += " " + " ";
// Assume No titles
					result += "   ";
					//logger.log(Level.INFO, "After Titles");				
//					result += " " + String.format("%-33s", fullname);
					result += " " + String.format("%-33s", s.getNaam());
					if (s.getStartrating() != -1) {
						result += " " + String.format("%1$4s", s.getStartrating());
					} else {
						//logger.log(Level.INFO, "Startrating " + s.getStartrating() + " is niet -1");						
						result += " " + String.format("%1$4s", 0);
					}
					result += " NED";
					result += " " + String.format("%1$11s", s.getKNSBnummer());
					result += " " + String.format("%1$9s", dateformatter.format(s.getGeboortejaar()));
					//logger.log(Level.INFO, "Aantal punten is " + s.getPunten()/10.0);
					String punt = String.format(Locale.US, "%.1f", s.getPunten()/10.0);
					result += " " + String.format("%1$4s", punt);
					result += " " + String.format("%1$4s", (totaaleerderegroepen+s.getRang()));
					wit= false;
					zwart = false;
					startRangTegenstander = 0;
					//logger.log(Level.INFO, "Player section done. Result is " + ls + result);
					logger.log(Level.INFO, "Now getting into matches. Number of matches is " + s.getWedstrijden().size());
					for (WedstrijdUitslag w : s.getWedstrijden()) {
						wit= false;
						zwart = false;
						if (w.getWit().gelijkAan(s)) {
							wit=true;
						}
						if (w.getZwart().gelijkAan(s)) {
							zwart = true;
						}
						if (wit && zwart) logger.log(Level.WARNING, "Speler is zowel wit als zwart?!");
						if (wit) {
							// vind speler in groep
							for (UitslagSpeler s2 : gu.getSpelers().values()) {
								if (w.getZwart().gelijkAan(s2)) {
									startRangTegenstander = s2.getStartRang();
								}
							}
							//logger.log(Level.INFO, "Tegenstander is zwart en met startrang " + startRangTegenstander);
						}
						if (zwart) {
								// vind speler in groep
								for (UitslagSpeler s2 : gu.getSpelers().values()) {
									if (w.getWit().gelijkAan(s2)) {
										startRangTegenstander = s2.getStartRang();
									}
								}
								//logger.log(Level.INFO, "Tegenstander is wit en met startrang (" + totaaleerderegroepen + " + " + startRangTegenstander + ")" + totaaleerderegroepen + startRangTegenstander);
						}
						result += "  " + String.format("%1$4s", (totaaleerderegroepen + startRangTegenstander));
						if (wit && !zwart) {
							// Speler is wit
							result += " w";
							result += " " + String.format("%1$1s", getFIDEUitslag2006(0,w.getUitslag()));
						}
						if (!wit && zwart) {
							// Speler is zwart
							result += " b";
							result += " " + String.format("%1$1s", getFIDEUitslag2006(1,w.getUitslag()));
						}
					}
					result += ls;
					//logger.log(Level.INFO, "Matches finished. Result is " + ls + result);
					logger.log(Level.INFO, "Matches finished.");
				};
				totaaleerderegroepen += gu.getAantal();
			}
			logger.log(Level.INFO, "Now writing file.");
			FileWriter writer = new FileWriter(bestandsnaam);
			writer.write(result);
			writer.close();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "An " + e.getMessage() + " occured ");
		}
	}

		public void exportfide2006pergroep(GroepsUitslagen groepenuitslagen) {
			Boolean wit;
			Boolean zwart;
			int startRangTegenstander;
			//logger.log(Level.INFO, "Starting...");
			try {
				SimpleDateFormat dateformatter = new SimpleDateFormat("yyyy/MM/dd");
				SimpleDateFormat dateformatter2 = new SimpleDateFormat("yy/MM/dd");
				// Until Rounddates is supplied assume same dates
				//logger.log(Level.INFO, "Aantal groepsuitslagen = " + groepenuitslagen.getAantalGroepsuitslagen());
				ArrayList<Date> roundDates = new ArrayList<Date>();
				for (GroepsUitslag gu : groepenuitslagen) {
					String bestandsnaam = "Fide2006-Data-Exchange-Groep-" + gu.getGroepsnaam() + ".txt";
					logger.log(Level.INFO, "Aanmaken van Fide2006 DataExchange bestand " + bestandsnaam);
					String result = "";
					// Tournament section
					result += DIN.get("Tournament Name") + " " + toernooi.getBeschrijving() + "-Groep-" + gu.getGroepsnaam() + ls; // Mandatory
					result += DIN.get("City") + " " + toernooi.getPlaats() + ls; // Mandatory
					result += DIN.get("Federation") + " NED" + ls; // Mandatory
					result += DIN.get("Date of start") + " " + dateformatter.format(toernooi.getDatum()) + ls;
					result += DIN.get("Date of end") + " " + dateformatter.format(toernooi.getDatum()) + ls;
					result += DIN.get("Number of players") + " " + groepenuitslagen.getAantalSpelers() + ls;
					result += DIN.get("Number of rated players") + " " + groepenuitslagen.getAantalspelersmetrating() + ls;
					result += DIN.get("Number of teams") + " 0" + ls;
					result += DIN.get("Type of tournament") + " Individual: Round-Robin" + ls;
					result += DIN.get("Chief Arbiter") + " " + ls;  // Mandatory
					result += DIN.get("dates of the rounds (YY/MM/DD)") + String.format("%1$86s", "");
					//logger.log(Level.INFO, "Tournament section done. Result is " + ls + result);
					logger.log(Level.INFO, "Tournament section done.");
					//logger.log(Level.INFO, "Aantal is " + gu.getAantal());
					for (int i=0; i < (gu.getAantal()-1); i++) {
						result += "  " + String.format("%1$8s", dateformatter2.format(toernooi.getDatum()));			
					}
					result += ls;
					//
					/////Teams section
					//Not used
					//
					//logger.log(Level.INFO, "Starting players section with " + gu.getSpelers().values().size() + " players.");
					logger.log(Level.INFO, "Starting players section.");
					//Players section
					for (UitslagSpeler s : gu.getSpelers().values()) {
						// Player info
						result += DIN.get("Player section");  // Mandatory
						//logger.log(Level.INFO, "DIN : " + DIN.get("Player section"));
						result += " " + String.format("%1$4s", (s.getStartRang()));  // Mandatory
//						result += " " + String.format("%1$1s", s.getGeslacht());
	// No Sex
						result += " " + " ";
	// Assume No titles
						result += "   ";
						//logger.log(Level.INFO, "After Titles");				
//						result += " " + String.format("%-33s", fullname);
						result += " " + String.format("%-33s", s.getNaam());
						if (s.getStartrating() != -1) {
							//logger.log(Level.INFO, "Startrating " + s.getStartrating() + " is niet -1");						
							result += " " + String.format("%1$4s", s.getStartrating());
						} else
							result += " " + String.format("%1$4s", 0);
						{
						result += " NED";
						result += " " + String.format("%1$11s", s.getKNSBnummer()); // Mandatory
						result += " " + String.format("%1$9s", dateformatter.format(s.getGeboortejaar()));
						//logger.log(Level.INFO, "Aantal punten is " + s.getPunten()/10.0);
						String punt = String.format(Locale.US, "%.1f", s.getPunten()/10.0);
						result += " " + String.format("%1$4s", punt);
						result += " " + String.format("%1$4s", s.getRang()); // Mandatory
						wit= false;
						zwart = false;
						startRangTegenstander = 0;
						//logger.log(Level.INFO, "Player section done. Result is " + ls + result);
						logger.log(Level.INFO, "Player section done.");
						logger.log(Level.INFO, "Now getting into matches. Number of matches is " + s.getWedstrijden().size());
						for (WedstrijdUitslag w : s.getWedstrijden()) {
							wit= false;
							zwart = false;
							if (w.getWit().gelijkAan(s)) {
								wit=true;
							}
							if (w.getZwart().gelijkAan(s)) {
								zwart = true;
							}
							if (wit && zwart) logger.log(Level.WARNING, "Speler is zowel wit als zwart?!");
							if (wit) {
								// vind speler in groep
								for (UitslagSpeler s2 : gu.getSpelers().values()) {
									if (w.getZwart().gelijkAan(s2)) {
										startRangTegenstander = s2.getStartRang();
									}
								}
								//logger.log(Level.INFO, "Tegenstander is zwart en met startrang " + startRangTegenstander);
							}
							if (zwart) {
									// vind speler in groep
									for (UitslagSpeler s2 : gu.getSpelers().values()) {
										if (w.getWit().gelijkAan(s2)) {
											startRangTegenstander = s2.getStartRang();
										}
									}
									//logger.log(Level.INFO, "Tegenstander is wit en met startrang " + startRangTegenstander);
							}
							result += "  " + String.format("%1$4s", (startRangTegenstander));  // Mandatory
							if (wit && !zwart) {
								// Speler is wit
								result += " w";  // Mandatory
								result += " " + String.format("%1$1s", getFIDEUitslag2006(0,w.getUitslag())); // Mandatory
							}
							if (!wit && zwart) {
								// Speler is zwart
								result += " b";  // Mandatory
								result += " " + String.format("%1$1s", getFIDEUitslag2006(1,w.getUitslag())); // Mandatory
							}
						}
						result += ls;
						//logger.log(Level.INFO, "Matches finished. Result is " + ls + result);
						logger.log(Level.INFO, "Matches finished.");
					};
				}
				logger.log(Level.INFO, "Now writing file.");
				FileWriter writer = new FileWriter(bestandsnaam);
				writer.write(result);
				writer.close();
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "An " + e.getMessage() + " occured ");
		}
	}

	public String getFIDEUitslag2006(int color, int uitslag) {
		// Color
		// 0 = wit
		// 1 = zwart
		//
		String FIDEUitslag = " ";
//		public static int UNKNOWN = -1; // Unknown
//		public static int ZERO_FORF= 0; // Both players didn't show up
//		public static int WHITE_ADJ = 4; // Adjourned (but the provisional score is 1-0)
//		public static int BLACK_ADJ = 5; // Adjourned (but the provisional score is 0-1)
//		public static int DRAW_ADJ = 6;	// Adjourned (but the provisional score is a draw)
		if (color == 0) {
// 			For white player			
			switch (uitslag) {
				case 1:
//					public static int WHITE_WINS = 1; // Whites wins
					FIDEUitslag = "1";
					break;
				case 2:
//					public static int BLACK_WINS = 2; // Black wins
					FIDEUitslag = "0";
					break;
				case 3:
//					public static int DRAW = 3; // Draw
					FIDEUitslag = "=";
					break;
				case 7:
//					public static int WHITE_FORF = 7; // Black did not show up, white receives the point
					FIDEUitslag = "+";
					break;
				case 8:
//					public static int BLACK_FORF = 8; // White did not show up, black receives the point
					FIDEUitslag = "-";
					break;
				case 9:
//					public static int DRAW_FORF = 9; // The game is not played but the point is shared
					FIDEUitslag = "D";
					break;
			}
		}
		if (color == 1){
// 			For black player			
			switch (uitslag) {
				case 1:
//					public static int WHITE_WINS = 1; // Whites wins
					FIDEUitslag = "0";
					break;
				case 2:
//					public static int BLACK_WINS = 2; // Black wins
					FIDEUitslag = "1";
					break;
				case 3:
//					public static int DRAW = 3; // Draw
					FIDEUitslag = "=";
					break;
				case 7:
//					public static int WHITE_FORF = 7; // Black did not show up, white receives the point
					FIDEUitslag = "-";
					break;
				case 8:
//					public static int BLACK_FORF = 8; // White did not show up, black receives the point
					FIDEUitslag = "+";
					break;
				case 9:
//					public static int DRAW_FORF = 9; // The game is not played but the point is shared
					FIDEUitslag = "D";
					break;
			}
		}			
		return FIDEUitslag;
	}

	public String getFIDEUitslag2016(int color, int uitslag) {
		// Color
		// 0 = wit
		// 1 = zwart
		//
		String FIDEUitslag = " ";
//		public static int UNKNOWN = -1; // Unknown
//		public static int ZERO_FORF= 0; // Both players didn't show up
//		public static int WHITE_ADJ = 4; // Adjourned (but the provisional score is 1-0)
//		public static int BLACK_ADJ = 5; // Adjourned (but the provisional score is 0-1)
//		public static int DRAW_ADJ = 6;	// Adjourned (but the provisional score is a draw)
		if (color == 0) {
// 			For white player			
			switch (uitslag) {
				case 1:
//					public static int WHITE_WINS = 1; // Whites wins
					FIDEUitslag = "1";
					break;
				case 2:
//					public static int BLACK_WINS = 2; // Black wins
					FIDEUitslag = "0";
					break;
				case 3:
//					public static int DRAW = 3; // Draw
					FIDEUitslag = "=";
					break;
				case 7:
//					public static int WHITE_FORF = 7; // Black did not show up, white receives the point
					FIDEUitslag = "+";
					break;
				case 8:
//					public static int BLACK_FORF = 8; // White did not show up, black receives the point
					FIDEUitslag = "-";
					break;
				case 9:
//					public static int DRAW_FORF = 9; // The game is not played but the point is shared
					FIDEUitslag = "D";
					break;
			}
		}
		if (color == 1){
// 			For black player			
			switch (uitslag) {
				case 1:
//					public static int WHITE_WINS = 1; // Whites wins
					FIDEUitslag = "0";
					break;
				case 2:
//					public static int BLACK_WINS = 2; // Black wins
					FIDEUitslag = "1";
					break;
				case 3:
//					public static int DRAW = 3; // Draw
					FIDEUitslag = "=";
					break;
				case 7:
//					public static int WHITE_FORF = 7; // Black did not show up, white receives the point
					FIDEUitslag = "-";
					break;
				case 8:
//					public static int BLACK_FORF = 8; // White did not show up, black receives the point
					FIDEUitslag = "+";
					break;
				case 9:
//					public static int DRAW_FORF = 9; // The game is not played but the point is shared
					FIDEUitslag = "D";
					break;
			}
		}			
		return FIDEUitslag;
	}

}
