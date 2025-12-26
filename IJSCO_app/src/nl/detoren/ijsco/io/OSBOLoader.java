/**
 * Copyright (C) 2016 Leo van der Meulen
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
package nl.detoren.ijsco.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.math3.util.MultidimensionalCounter.Iterator;
import org.apache.poi.util.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities.EscapeMode;
import org.jsoup.select.Elements;

import nl.detoren.ijsco.data.Spelers;
import nl.detoren.ijsco.ui.Mainscreen;
import nl.detoren.ijsco.data.Speler;
import nl.detoren.ijsco.ui.util.Utils;

/**
 * Laad bekende OSBO spelers in, dit kan automatisch vanaf de website van de
 * OSBO door het jeugdrating bestand in te lezen en door een lokale kopie van
 * dit bestand in te lezen.
 * @author Leo.vanderMeulen
 *
 */
public class OSBOLoader {

	private final static Logger logger = Logger.getLogger(OSBOLoader.class.getName());
	
	public Spelers laadBestand(String bestandsnaam) {
		try {
			//File input = new File("c:/lijst.html");
			File input = new File(bestandsnaam);
			//Document doc = Jsoup.parse(input, "UTF-8");
			Document doc = Jsoup.parse(input, "ISO-8859-1");
            //((org.jsoup.nodes.Document) doc).outputSettings().charset().forName("UTF-8");
            ((org.jsoup.nodes.Document) doc).outputSettings().escapeMode(EscapeMode.xhtml);
			return load(doc);
		} catch (Exception e) {
			//System.out.println("Error loading OSBO spelers " + e.getMessage());
		}
		return null;
	}
	
	public Spelers laadWebsite(String url) {
		try {
			//Document doc = Jsoup.connect("http://osbo.nl/jeugd/jrating.htm").get();
			//String url = "http://osbo.nl/jeugd/jrating.htm";
			//String url = "http://ijsco.schaakverenigingdetoren.nl/ijsco1718/IJSCOrating1718.htm";
			Document doc = Jsoup.connect(url).get();
			doc.head().appendElement("meta").attr("charset","UTF-8");
			doc.head().appendElement("meta").attr("http-equiv","Content-Type").attr("content","text/html"); 
			//Document doc = Jsoup.parse(new URL(url).openStream(), "UTF-8", url);
		    //URI baseURI=new URI(url);
		    //String content=IOUtils.toString(stream,"utf-8");
		    //Document doc=Jsoup.parse(content,baseurl);		
			return load(doc);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Error loading OSBO spelers \" + e.getMessage()");
			System.out.println("Error loading OSBO spelers " + e.getMessage());
		}
		return null;
	}
	
	  private static String readAll(Reader rd) throws IOException {
		    StringBuilder sb = new StringBuilder();
		    int cp;
		    while ((cp = rd.read()) != -1) {
		      sb.append((char) cp);
		    }
		    return sb.toString();
		  }

	  public static JSONArray readJsonFromUrl(String url) throws IOException {
		    InputStream is = new URL(url).openStream();
		    try {
		      BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
		      String jsonText = readAll(rd);
		      //JSONObject json = new JSONObject(jsonText);
		      Object o = JSONValue.parse(jsonText);
		      JSONArray json = (JSONArray) o; 
		      return json;
		    } finally {
		      is.close();
		    }
	  }

	  public static JSONArray readJsonFromFile(String bestandsnaam) throws IOException {
		    try {
		    	FileReader reader = new FileReader(bestandsnaam);
	            BufferedReader rd = new BufferedReader(reader);
		      String jsonText = readAll(rd);
		      //JSONObject json = new JSONObject(jsonText);
		      Object o = JSONValue.parse(jsonText);
		      JSONArray json = (JSONArray) o; 
		      return json;
		    } finally {
		    }
	  }

	public Spelers laadJSON(String url) {
		Spelers spelers = null;
		try {
			JSONArray json = readJsonFromUrl(url);
			spelers = parseJSON(json);
			
		}
		catch (Exception e) {
			logger.log(Level.WARNING, "Error loading OSBO spelers " + e.getMessage());
			System.out.println("Error loading OSBO spelers " + e.getMessage());
		}
		return spelers;
	}
	
	public Spelers laadJSONfromFile(String bestandsnaam) {
		Spelers spelers = null;
		try {
			JSONArray json = readJsonFromFile(bestandsnaam);
			spelers = parseJSON(json);
			
		}
		catch (Exception e) {
			logger.log(Level.WARNING, "Error loading OSBO spelers " + e.getMessage());
			System.out.println("Error loading OSBO spelers " + e.getMessage());
		}
		return spelers;
	}

	public Spelers laadCSV(String csvpath) {
	 File csvData = new File(csvpath);
	 CSVParser parser = null;
	try {
		parser = CSVParser.parse(csvData, java.nio.charset.Charset.defaultCharset(), CSVFormat.RFC4180);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	 for (CSVRecord csvRecord : parser) {
	     //TODO
	 }
	return null;
	}

	public Spelers laadKNSBJeugdOnline_CSVinZIP(String url) {
		String databaseLocation = "database";
		String excelBestand = "JEUGD.CSV";
        try {	
        	System.out.println("Working Directory = " + System.getProperty("user.dir"));
//			Utils.downloadUsingNIO(url, "latestknsbjeugd.zip");
        	Utils.downloadUsingBufferedInputStream(url, "latestknsbjeugd.zip");
        	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Path source = Paths.get("latestknsbjeugd.zip");
		    Path destination = Paths.get(databaseLocation);
		    String password = "password";

		    // Create directory database if not existing
		    if (!Files.exists(destination)) {
	            
	            try {
					Files.createDirectory(destination);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            System.out.println("Directory created");
	        } else {
	            
	            System.out.println("Directory already exists");
	        }
		    
		    // Extract Database
	        try {
	            Utils.unzipFolder(source, destination);
	            System.out.println("Database unpack done");
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        
		 File csvData = new File(databaseLocation + "/" + excelBestand);
		 CSVParser parser = null;
		try {
			parser = CSVParser.parse(csvData, java.nio.charset.Charset.defaultCharset(), CSVFormat.RFC4180.withHeader().withDelimiter(';'));
//			parser = CSVParser.parse(csvData, java.nio.charset.Charset.defaultCharset(), CSVFormat.DEFAULT);
		} catch (FileSystemException ex) {
			 logger.log(Level.SEVERE, "Not able to open " + excelBestand + " in directory " + databaseLocation + "because of " + ex.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Spelers spelers = new Spelers();
		for (CSVRecord csvRecord : parser) {
			try {
			     //TODO Controleren of veld leeg is.
				 Speler speler = new Speler();
				 speler.setKnsbnummer(csvRecord.get(0));
				 speler.setNaamKNSB(csvRecord.get(1));
				 speler.setRatingKNSB(csvRecord.get(4));			 
				 speler.setGeboortejaar(csvRecord.get(6));
				 speler.setGeslacht(csvRecord.get(7));			 
				 logger.log(Level.INFO, "Speler : " + speler.getNaam() + " heeft geboortejaar " + speler.getGeboortejaar() + " en een rating van " + speler.getRating());
				 spelers.add(speler);
			}
		 	catch (Exception e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();			 
				 logger.log(Level.INFO, "Speler " + csvRecord.get(1) + " met geboortejaar " + csvRecord.get(6) + " en een rating van " + csvRecord.get(4) + " kon niet geimporteerd worden.");
		 	}
		}
		return spelers;
	}

	public Spelers laadKNSBRAPIDOnline_CSVinZIP(String url) {
		String databaseLocation = "database";
		String excelBestand = "RAPID.CSV";
        try {	
        	System.out.println("Working Directory = " + System.getProperty("user.dir"));
//			Utils.downloadUsingNIO(url, "latestknsbjeugd.zip");
        	Utils.downloadUsingBufferedInputStream(url, "latestknsbrapid.zip");
        	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Path source = Paths.get("latestknsbrapid.zip");
		    Path destination = Paths.get(databaseLocation);
		    String password = "password";

		    // Create directory database if not existing
		    if (!Files.exists(destination)) {
	            
	            try {
					Files.createDirectory(destination);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            System.out.println("Directory created");
	        } else {
	            
	            System.out.println("Directory already exists");
	        }
		    
		    // Extract Database
	        try {
	            Utils.unzipFolder(source, destination);
	            System.out.println("Database unpack done");
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        
		File csvData = new File(databaseLocation + "/" + excelBestand);
		if (!csvData.exists()) {
			logger.log(Level.SEVERE, "CSV file " + excelBestand + " in directory " + databaseLocation + "does not exists!");
			LocalDate today = LocalDate.now();
			LocalDate juistedatum = today;	
			int month = juistedatum.getMonth().getValue();
			int year = juistedatum.getYear();
			csvData = new File(databaseLocation + "/" + year + "-" + String.format("%02d", month ) + "-" + excelBestand);
		}
		CSVParser parser = null;
		try {
			parser = CSVParser.parse(csvData, java.nio.charset.Charset.defaultCharset(), CSVFormat.RFC4180.withHeader().withDelimiter(';'));
//			parser = CSVParser.parse(csvData, java.nio.charset.Charset.defaultCharset(), CSVFormat.DEFAULT);
		} catch (FileSystemException ex) {
			 logger.log(Level.SEVERE, "Not able to open " + csvData.getName() + " in directory " + databaseLocation + " because of " + ex.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 Spelers spelers = new Spelers();
		 try {
			 for (CSVRecord csvRecord : parser) {
			     //TODO Controleren of veld leeg is.
				 Speler speler = new Speler();
				 speler.setKnsbnummer(csvRecord.get(0));
				 speler.setNaamKNSB(csvRecord.get(1));
				 speler.setRatingKNSB(csvRecord.get(4));			 
				 speler.setGeboortejaar(csvRecord.get(6));
				 speler.setGeslacht(csvRecord.get(7));			 
				 logger.log(Level.INFO, "Speler : " + speler.getNaam() + " heeft geboortejaar " + speler.getGeboortejaar() + " en een rating van " + speler.getRating());
				 spelers.add(speler);
				}
		 } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();			 
		 }
		return spelers;
	}

	public Spelers laadKNSBRAPIDOffline_CSVinZIP(String filepath) {
		String databaseLocation = "database";
		String excelBestand = "RAPID.CSV";
		Path source = Paths.get(filepath);
		    Path destination = Paths.get(databaseLocation);
		    String password = "password";

		    // Create directory database if not existing
		    if (!Files.exists(destination)) {
	            
	            try {
					Files.createDirectory(destination);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            System.out.println("Directory created");
	        } else {
	            
	            System.out.println("Directory already exists");
	        }
		    
		    // Extract Database
	        try {
	            Utils.unzipFolder(source, destination);
	            System.out.println("Database unpack done");
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        
		File csvData = new File(databaseLocation + "/" + excelBestand);
		if (!csvData.exists()) {
			logger.log(Level.SEVERE, "CSV file " + excelBestand + " in directory " + databaseLocation + "does not exists!");
			LocalDate today = LocalDate.now();
			LocalDate juistedatum = today;	
			int month = juistedatum.getMonth().getValue();
			int year = juistedatum.getYear();
			csvData = new File(databaseLocation + "/" + year + "-" + String.format("%02d", month ) + "-" + excelBestand);
		}
		CSVParser parser = null;
		try {
			parser = CSVParser.parse(csvData, java.nio.charset.Charset.defaultCharset(), CSVFormat.RFC4180.withHeader().withDelimiter(';'));
//			parser = CSVParser.parse(csvData, java.nio.charset.Charset.defaultCharset(), CSVFormat.DEFAULT);
		} catch (FileSystemException ex) {
			 logger.log(Level.SEVERE, "Not able to open " + csvData.getName() + " in directory " + databaseLocation + " because of " + ex.getMessage());
		} catch (IOException e) {
			 logger.log(Level.SEVERE, "Not able to open " + csvData.getName() + " in directory " + databaseLocation + " because of " + e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 Spelers spelers = new Spelers();
		 try {
			 for (CSVRecord csvRecord : parser) {
			     //TODO Controleren of veld leeg is.
				 Speler speler = new Speler();
				 speler.setKnsbnummer(csvRecord.get(0));
				 speler.setNaamKNSB(csvRecord.get(1));
				 speler.setRatingKNSB(csvRecord.get(4));			 
				 speler.setGeboortejaar(csvRecord.get(6));
				 speler.setGeslacht(csvRecord.get(7));			 
				 logger.log(Level.INFO, "Speler : " + speler.getNaam() + " heeft geboortejaar " + speler.getGeboortejaar() + " en een rating van " + speler.getRating());
				 spelers.add(speler);
				}
		 } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();			 
		 }
		 
		return spelers;
	}
	
	private Spelers parseJSON(JSONArray json) {
		Spelers spelers = new Spelers();
		for (Object l : json){
			if (l instanceof JSONObject) {
				logger.log(Level.INFO, "JSONObject" + l.toString());
				JSONObject jo = (JSONObject) l;
				JSONObject s = (JSONObject) jo.get("speler");
				Speler speler = new Speler();
				long k = (long) s.get("knsbnummer");
				speler.setKnsbnummer((int) k);
				logger.log(Level.INFO, String.format("%s",speler.getKnsbnummer()));
				String voornaam = (String) s.get("voornaam");
				String tussenvoegsel = (String) s.get("tussenvoegsel");
				String achternaam = (String) s.get("achternaam");
				String samengestelde_naam = voornaam;
				if (tussenvoegsel != null && !tussenvoegsel.trim().isEmpty()) samengestelde_naam += " " + tussenvoegsel;
				if (achternaam != null && !achternaam.trim().isEmpty()) samengestelde_naam += " " + achternaam;
				speler.setNaamKNSB(samengestelde_naam);
				int g = Integer.parseInt((String) s.get("geboortejaar"));
				speler.setGeboortejaar((int) g);
				String geslacht = (String) s.get("geslacht");
				logger.log(Level.INFO, "Geslacht van " + speler.getNaam() + " is " + geslacht);
				speler.setGeslacht(geslacht);
				long r = (long) jo.get("osborating");
				speler.setRatingIJSCO((int) r);
				speler.bepaalCategorie();
				logger.log(Level.INFO, "Speler : " + speler.getNaam() + " heeft geboortejaar " + speler.getGeboortejaar());
				spelers.add(speler);
			}
		}
		return spelers;
	}
	
	private Spelers load(Document doc) {
		Spelers spelers = new Spelers();
		int knsbnummer = 0;
		int knsbrating = 0;
		int osborating = 0;
		String vereniging = "";
		int geboortejaar = 0;
		String categorie = "";
		String naam = "";
		Element table = doc.select("table").first();
		Elements rows = table.select("tr");
		for (Element row : rows) {
			Elements cells = row.select("td");
			if (cells.size() > 7) {
				try {
					naam = cells.get(1).text();
				} catch (Exception e) {
					naam = null;
					System.out.println(e);
				}
				try {
					knsbnummer = Integer.parseInt(cells.get(8).text());
				} catch (Exception e) {
					knsbnummer = 0;
					System.out.println(e);
				}
				try {
					osborating = Integer.parseInt(cells.get(3).text());
				} catch (Exception e) {
					osborating = -1;
					System.out.println(e);
				}
				try {
					knsbrating = Integer.parseInt(cells.get(4).text());
				} catch (Exception e) {
					knsbrating = -1;
					System.out.println(e);
				}
				try {
					vereniging = cells.get(2).text();
				} catch (Exception e) {
					vereniging = "";
					System.out.println(e);
				}
				try {
					geboortejaar = Integer.parseInt(cells.get(6).text());
				} catch (Exception e) {
					geboortejaar = -1;
					System.out.println(e);
				}
				try {
					categorie = cells.get(7).text();
				} catch (Exception e) {
					categorie = "-";
					System.out.println(e);
				}

				Speler s = new Speler(knsbnummer, naam, vereniging, geboortejaar, categorie, osborating, knsbrating);
				spelers.add(s);
			}
		}
		return spelers;
	}
}
