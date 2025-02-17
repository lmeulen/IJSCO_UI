/**
;< * Copyright (C) 2018 Lars Dam
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation version 3.0
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * See: http://www.gnu.org/licenses/gpl-3.0.html
 *
 * Known issues in this code
 * - 
 */
package nl.detoren.ijsco.io;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.microsoft.schemas.office.visio.x2012.main.SheetType;

import nl.detoren.ijsco.data.GroepsUitslag;
import nl.detoren.ijsco.data.GroepsUitslagen;
import nl.detoren.ijsco.data.Speler;
import nl.detoren.ijsco.data.Spelers;
import nl.detoren.ijsco.data.Status;
import nl.detoren.ijsco.data.UitslagSpeler;
import nl.detoren.ijsco.data.Wedstrijd;
import nl.detoren.ijsco.data.WedstrijdUitslag;
import nl.detoren.ijsco.ui.control.IJSCOController;
import nl.detoren.ijsco.ui.util.Utils;

public class ExcelImport implements ImportInterface {
	
	private final static Logger logger = Logger.getLogger(ExcelImport.class.getName());
	
	public GroepsUitslagen importeerUitslagen(File file) {
		
        GroepsUitslagen groepen = new GroepsUitslagen();
		try {
			
            FileInputStream excelFile = new FileInputStream(file);
            Workbook workbook = new XSSFWorkbook(excelFile);
            String versie = "onbekend";
            Sheet sheet = workbook.getSheet("Configuratie");
            try {
            	versie = sheet.getRow(0).getCell(1).getStringCellValue();
            }
            catch (Exception ex) {
            	logger.log(Level.INFO, "Indelings Excel versie ophalen mislukt");
            }
            logger.log(Level.INFO, "Indelings Excel is versie " + versie);
        	for(int i=0;i < workbook.getNumberOfSheets();i++){
        		sheet = workbook.getSheetAt(i);
        		if (sheet.getSheetName().startsWith("Groep ")) {
        			logger.log(Level.INFO, "Importeer uitslag van groep : " + sheet.getSheetName());
        			Row row = sheet.getRow(0);
        			if (row != null) {
        				Cell cell = row.getCell(0);
        			    if (cell != null) {
                			int groepsgrootte = (int) cell.getNumericCellValue();
                			logger.log(Level.INFO, "Groepsgrootte is " + groepsgrootte);
                			switch (groepsgrootte) {
                				case 4:
                					groepen.Add(importeerGroep4(sheet));
                					break;
                				case 6: 
                					groepen.Add(importeerGroep6(sheet));
                					break;
                				case 8:
                					groepen.Add(importeerGroep8(sheet));
                					break;
                				case 10:
                					groepen.Add(importeerGroep10(sheet));
                					break;
                				case 12:
                					groepen.Add(importeerGroep12(sheet));
                					break;
                				case 14:
                					groepen.Add(importeerGroep14(sheet));
                					break;
                				default:
                        			logger.log(Level.WARNING, "Uitslagen verwerken voor groepsgrootte " + groepsgrootte + " niet ondersteund!");
                        			break;
        			    	}
        			  	}
        			}
        			logger.log(Level.INFO, "next");
        		}
            }
        	workbook.close();
		}
		catch (Exception ex){
			logger.log(Level.SEVERE, "Exception! Cause: " + ex.getCause() + ". Internal error is " + ex.getMessage() + ". Stracktrace: \r\n" + Utils.stackTraceToString(ex));
		}
		// Print resultaat;
		logger.log(Level.INFO, groepen.toString());
		return groepen;
	}

	private GroepsUitslag importeerGroep14(Sheet sheet) {
/*
 * 		versie < 0.3
 */ 		
/*		int groepsgrootte = ?;
		int rowidxbase = ?;
		int columnuitslag = ?;
		int columntotaal = ?;
		int columnwedstrijdidwit = ?;	
		int columnwedstrijdidzwart = ?;			
*/
/*
 * 		versie 0.3
 */
		int groepsgrootte = 14;
		int rowidxbase = 20; // Rij waar Ronde 1 start
		int columnuitslag = 23; // Uitslag kolom
		int columntotaal = 21; // Totaalpunten kolom
		int columnwedstrijdidwit = 50; // Witwedstrijd kolom (berekening)
		int columnwedstrijdidzwart = 51; // Zwartwedstrijd kolom (berekening)
		GroepsUitslag groep = importeergroep(groepsgrootte, sheet, rowidxbase, columnuitslag, columntotaal, columnwedstrijdidwit, columnwedstrijdidzwart);
		logger.log(Level.INFO, "Import groep 14 klaar");
		return groep;		
	}

	private GroepsUitslag importeerGroep12(Sheet sheet) {
/*
 * 		versie < 0.3
 */ 		
/*		int groepsgrootte = 12;
		int rowidxbase = ?;
		int columnuitslag = ?;
		int columntotaal = ?;
		int columnwedstrijdidwit = ?;	
		int columnwedstrijdidzwart = ?;			
*/
/*
 * 		versie 0.3
 */
		int groepsgrootte = 12;
		int rowidxbase = 18; // Rij waar Ronde 1 start
		int columnuitslag = 21; // Uitslag kolom
		int columntotaal = 19; // Totaalpunten kolom
		int columnwedstrijdidwit = 46; // Witwedstrijd kolom (berekening)
		int columnwedstrijdidzwart = 47; // Zwartwedstrijd kolom (berekening)
		GroepsUitslag groep = importeergroep(groepsgrootte, sheet, rowidxbase, columnuitslag, columntotaal, columnwedstrijdidwit, columnwedstrijdidzwart);
		logger.log(Level.INFO, "Import groep 12 klaar");
		return groep;		
	}


	private GroepsUitslag importeerGroep10(Sheet sheet) {
/*
 * 		versie < 0.3
 */ 		
/*		int groepsgrootte = 10;
		int rowidxbase = 17;
		int columnuitslag = 19;
		int columntotaal = 17;
		int columnwedstrijdidwit = 47;	
		int columnwedstrijdidzwart = 48;			
*/
/*
 * 		versie 0.3
 */
		int groepsgrootte = 10;
		int rowidxbase = 17;
		int columnuitslag = 19;
		int columntotaal = 17;
		int columnwedstrijdidwit = 46;	
		int columnwedstrijdidzwart = 47;			
		GroepsUitslag groep = importeergroep(groepsgrootte, sheet, rowidxbase, columnuitslag, columntotaal, columnwedstrijdidwit, columnwedstrijdidzwart);
		logger.log(Level.INFO, "Import groep 10 klaar");
		return groep;		
	}

	private GroepsUitslag importeerGroep8(Sheet sheet) {
/*
 * 		versie < 0.3
 */ 		
/*
  		int groepsgrootte = 8;
		int rowidxbase = 15;
		int columnuitslag = 19;
		int columntotaal = 17;
		int columnwedstrijdidwit = 50;	
		int columnwedstrijdidzwart = 51;
 */			
/*
 * 		versie 0.3
 */

		int groepsgrootte = 8;
		int rowidxbase = 15;
		int columnuitslag = 19;
		int columntotaal = 17;
		int columnwedstrijdidwit = 43;	
		int columnwedstrijdidzwart = 44;
		GroepsUitslag groep = importeergroep(groepsgrootte, sheet, rowidxbase, columnuitslag, columntotaal, columnwedstrijdidwit, columnwedstrijdidzwart);
		logger.log(Level.INFO, "Import groep 8 klaar");
		return groep;
	}

	private GroepsUitslag importeerGroep6(Sheet sheet) {
/*
 * 		versie < 0.3
 */ 		
 /* 	int groepsgrootte = 6;
		int rowidxbase = 13;
		int columnuitslag = 19;
		int columntotaal = 17;
		int columnwedstrijdidwit = 46;	
		int columnwedstrijdidzwart = 47;	
 */		
		
/*
 * 		versie 0.3
 */
		int groepsgrootte = 6;
		int rowidxbase = 13;
		int columnuitslag = 19;
		int columntotaal = 17;
		int columnwedstrijdidwit = 40;	
		int columnwedstrijdidzwart = 41;	
		GroepsUitslag groep = importeergroep(groepsgrootte, sheet, rowidxbase, columnuitslag, columntotaal, columnwedstrijdidwit, columnwedstrijdidzwart);
		logger.log(Level.INFO, "Import groep 6 klaar");
		return groep;
	}

	private GroepsUitslag importeerGroep4(Sheet sheet) {
/*
 * 		versie < 0.3
 */ 		
/*
 		int groepsgrootte = 4;
		int rowidxbase = 11;
		int columnuitslag = 18;
		int columtotaal = 16;
		int columnwedstrijdidwit = 42;	
		int columnwedstrijdidzwart = 43;	
 */
/*
 * 		versie 0.3
 */

		int groepsgrootte = 4;
		int rowidxbase = 11;
		int columnuitslag = 18;
		int columtotaal = 16;
		int columnwedstrijdidwit = 36;	
		int columnwedstrijdidzwart = 37;	
		GroepsUitslag groep = importeergroep(groepsgrootte, sheet, rowidxbase, columnuitslag, columtotaal, columnwedstrijdidwit, columnwedstrijdidzwart);
		logger.log(Level.INFO, "Import groep 4 klaar");
		return groep;
	}

	private GroepsUitslag importeergroep(int groepsgrootte, Sheet sheet, int rIdxbase, int cUitslag, int cTotaal, int cWedstrijdIDWit, int cWedstrijdIDZwart) {
		Row row;
		HashMap<Integer, Speler> OSBOSpelers = IJSCOController.getI().getStatus().OSBOSpelers;
		GroepsUitslag groep = new GroepsUitslag(groepsgrootte, cellStringValue(sheet.getRow(0).getCell(6)));
		for(int i=0;i <= groepsgrootte-1;i++){
			UitslagSpeler s = new UitslagSpeler();
			// Rang
			Integer rang;
			// StartRang
			Integer startRang;
			try {
				startRang = cellIntValue(sheet.getRow(2+i+1).getCell(1));
			} catch (Exception ex) {
				logger.log(Level.WARNING, "StartRang not found.");
				startRang = -1;
			}
			//
			try {
				rang = cellIntValue(sheet.getRow(2+i+1).getCell(cTotaal+2));
			} catch (Exception ex) {
				logger.log(Level.WARNING, "Rang not found.");
				rang = -1;
			}
			// Id
			Integer id;
			try {
				id = cellIntValue(sheet.getRow(2+i+1).getCell(1));
			} catch (Exception ex) {
				logger.log(Level.WARNING, "Rang not found.");
				id = -1;
			}
			// Naam
			String naam;
			try {
				naam = cellStringValue(sheet.getRow(2+i+1).getCell(2));
			} catch (Exception ex) {
				logger.log(Level.WARNING, "Naam not found.");
				naam = "Onbekend";
			}
			// Punten
			Integer punten;
			try {
				punten = (int) (10 *cellDoubleValue(sheet.getRow(2+i+1).getCell(cTotaal)));
			} catch (Exception ex) {
				logger.log(Level.WARNING, "Punten not found.");
				punten = -1;
			}
//			// WP
//			Integer wp;
//			try {
//				wp = (int) (10 * cellDoubleValue(sheet.getRow(2+i+1).getCell(23)));
//			} catch (Exception ex) {
//				logger.log(Level.WARNING, "WP not found.");
//				wp = -1;
//			}
			// SB
			Integer sb;
			try {
				sb = (int) (100 * cellDoubleValue(sheet.getRow(2+i+1).getCell(cTotaal+1)));
			} catch (Exception ex) {
				logger.log(Level.WARNING, "SB not found.");
				sb = -1;
			}
			// Knsbnummer
			Integer knsbnummer;
			try {
				knsbnummer = cellIntValue(sheet.getRow(2+i+1).getCell(3));
			} catch (Exception ex) {
				logger.log(Level.WARNING, "KNSBnummer not found.");
				knsbnummer = -1;
			}
			// Startrating
			Integer startrating;
			try {
				startrating = cellIntValue(sheet.getRow(2+i+1).getCell(5));
			} catch (Exception ex) {
				logger.log(Level.WARNING, "Startrating not found.");
				startrating = -1;
			}
			s.setId(id);
			s.setRang(rang);
			s.setStartRang(startRang);
			s.setNaam(naam);
			s.setPunten(punten);
			//s.setWP(wp);
			s.setSB(sb);
			s.setKNSBnummer(knsbnummer);
			s.setStartrating(startrating);
			//logger.log(Level.INFO, s.toFormattedString());
			for (Speler osbo :OSBOSpelers.values()) {
				int osboknsbnummer = 0;
				try {
					osboknsbnummer = osbo.getKnsbnummer();
				} catch (Exception ex) {
					logger.log(Level.WARNING, "Exception in finding knsbnummer " + knsbnummer + " in OSBO list");
				}
				int geboortejaar = 0;
				try {
					geboortejaar = osbo.getGeboortejaar();
				} catch (Exception ex) {
					logger.log(Level.WARNING, "Exception in finding geboortejaar " + geboortejaar + " in OSBO list");
				}
				if (knsbnummer == osboknsbnummer) {
					s.setVereniging(osbo.getVereniging());
					s.setGeboortejaar(osbo.getGeboortejaar());
					s.setCategorie(osbo.getCategorie());
				}
			}
			groep.addSpeler(s);
		}
		
		for(int i=0;i <= groepsgrootte-2;i++){
			for(int j=0;j <= (groepsgrootte-2)/2;j++){
				int uitslagcode;
				row = sheet.getRow(rIdxbase+(i*(3+(groepsgrootte-2)/2))+j);
				if (row != null) {
					uitslagcode = cellIntValue(row.getCell(cUitslag));
					if (uitslagcode>=0 && uitslagcode <10) {
						WedstrijdUitslag wedstrijd = new WedstrijdUitslag();
						Integer speleridwit; 
						try {
							speleridwit = cellIntValue(row.getCell(cWedstrijdIDWit));
							if (speleridwit == null) throw new NullPointerException()	; 
						} catch (Exception ex) {
							logger.log(Level.WARNING, "Player number for white not found.");
							speleridwit = 0;
						}
						//logger.log(Level.INFO, "Player white id : " + speleridwit);
						Integer speleridzwart;
						try {
							speleridzwart = cellIntValue(row.getCell(cWedstrijdIDZwart));
							if (speleridzwart == null) throw new NullPointerException()	;
						} catch (Exception ex) {
							logger.log(Level.WARNING, "Player number for black not found.");
							speleridzwart = 0;
						}
						//logger.log(Level.INFO, "Player black id : " + speleridzwart);
//						Integer knsbwit;
//						try {
//							knsbwit = cellIntValue(sheet.getRow(2+speleridwit).getCell(3));
//						} catch (Exception ex) {
//							logger.log(Level.WARNING, "Player KNSB number for white not found.");
//							knsbwit = 0;
//						}
//						logger.log(Level.INFO, "Player white knsb number : " + knsbwit);
//						String naamwit;
//						try {
//							naamwit = cellStringValue(sheet.getRow(2+speleridwit).getCell(2));
//						} catch (Exception ex) {
//							logger.log(Level.WARNING, "Player name for white not found.");
//							naamwit = "White";
//						}
//						logger.log(Level.INFO, "Player white name : " + naamwit);
//						Integer startratingwit;
//						try {
//							startratingwit = cellIntValue(sheet.getRow(2+speleridwit).getCell(5));
//						} catch (Exception ex) {
//							logger.log(Level.WARNING, "Start rating for white not found.");
//							startratingwit = -1;
//						}
//						Integer knsbzwart;
//						try {
//							knsbzwart = cellIntValue(sheet.getRow(2+speleridzwart).getCell(3));
//						} catch (Exception ex) {
//							logger.log(Level.WARNING, "Player number for black not found.");
//							knsbzwart = 0;
//						}
//						logger.log(Level.INFO, "Player white knsb number : " + knsbzwart);
//						String naamzwart;
//						try {
//							naamzwart = cellStringValue(sheet.getRow(2+speleridzwart).getCell(2));
//						} catch (Exception ex) {
//							logger.log(Level.WARNING, "Player name for black not found.");
//							naamzwart = "Black";
//						}
//						logger.log(Level.INFO, "Player black name : " + naamzwart);
//						Integer startratingzwart;
//						try {
//							startratingzwart = cellIntValue(sheet.getRow(2+speleridzwart).getCell(5));
//						} catch (Exception ex) {
//							logger.log(Level.WARNING, "Start rating for black not found.");
//							startratingzwart = -1;
//						}
						UitslagSpeler wit = null;
						try {
							wit = groep.getSpelerById(speleridwit);
						}
						catch (Exception e) {
							logger.log(Level.WARNING, "Exception in getSpelerbyId wit : " + speleridwit + "");							
						}
						UitslagSpeler zwart = null;
						try {
							zwart = groep.getSpelerById(speleridzwart);
						}
						catch (Exception e) {
							logger.log(Level.WARNING, "Exception in getSpelerbyId zwart : " + speleridzwart + "");
						}
//						for (Speler s :OSBOSpelers.values()) {
//							int knsbnummer = 0;
//							try {
//								knsbnummer = s.getKnsbnummer();
//							} catch (Exception ex) {
//								logger.log(Level.WARNING, "Exception in finding knsbnummer " + knsbnummer + " in OSBO list");
//							}
//							if (wit != null) {
//								if (knsbnummer == wit.getKNSBnummer()) {
//									wit.setVereniging(s.getVereniging());
//									wit.setGeboortejaar(s.getGeboortejaar());
//									wit.setCategorie(s.getCategorie());
//								}
////								if (wit.getNaam() == "???" ) {
////									logger.log(Level.WARNING, "Speler wit is niet gevonden. Knsbnummer " + knsbwit);
////									wit.setKNSBnummer(knsbwit);
////									wit.setNaam(naamwit);
////								}
//							}
//							if (zwart != null) {
//								if (knsbnummer == zwart.getKNSBnummer()) {
//									zwart.setVereniging(s.getVereniging());
//									zwart.setGeboortejaar(s.getGeboortejaar());
//									zwart.setCategorie(s.getCategorie());
//								}
////								if (zwart.getNaam() == "???" ) {
////									logger.log(Level.WARNING, "Speler zwart is niet gevonden. Knsbnummer " + knsbzwart);
////									zwart.setKNSBnummer(knsbzwart);
////									zwart.setNaam(naamzwart);
////								}
//							}
//						}
						boolean bye = false;
						if (wit.getNaam()!= null)
							if (wit.getNaam().equals("Bye")) bye = true;
						if (zwart.getNaam()!= null)
							if (zwart.getNaam().equals("Bye")) bye = true;
						if (!bye) {
							wedstrijd.setPoule(sheet.getSheetName());
							wedstrijd.setRonde(i+1);
							wedstrijd.setWit(wit);
							wedstrijd.setstartratingWit(wit.getStartrating());
							wedstrijd.setZwart(zwart);
							wedstrijd.setstartratingZwart(zwart.getStartrating());
							try {
								logger.log(Level.INFO, "Setting SpelerWit : " + wit.getNaam() + " - KNSBWit : " + wit.getKNSBnummer());
								logger.log(Level.INFO, "Setting SpelerZwart : " + zwart.getNaam() + " - KNSBZwart : " + zwart.getKNSBnummer());
							}
							catch (Exception ex) {
								logger.log(Level.WARNING, "Exception " + ex.getMessage() + " on logging Spelers");								
							}
						
							try {
								logger.log(Level.INFO, "Setting uitslag");
								wedstrijd.setUitslag012(uitslagcode);
							}
							catch (Exception ex) {
								logger.log(Level.SEVERE, "Exception in setting uitslag: " + ex.getMessage());
							}
							try {
								logger.log(Level.INFO, "Setting Wedstrijd : " + wedstrijd.toString());
							} catch (Exception ex) {
								logger.log(Level.WARNING, "Wedstrijd.tostring failed");
							}
							groep.addWedstrijd(wedstrijd);
						} else {
							logger.log(Level.WARNING, "Match is a bye");
						}
					}
    			}
    		}	
    	}
    return groep;
	}

	public Spelers controleerSpelers(Spelers spelers, HashMap<Integer, Speler> osbolijst) {
		Spelers update = new Spelers();
		for (Speler s : spelers) {
			Speler osbogegevens = osbolijst.get(s.getKnsbnummer());
			if (osbogegevens != null) {
				s.setNaamKNSB(osbogegevens.getNaam());
				s.setRatingIJSCO(osbogegevens.getRatingIJSCO());
				s.setRatingKNSB(osbogegevens.getRatingKNSB());
				s.setGeboortejaar(osbogegevens.getGeboortejaar());
				s.setGeslacht(osbogegevens.getGeslacht());
				s.setCategorie(osbogegevens.getCategorie());
				s.setVereniging(osbogegevens.getVereniging());
			}
			update.add(s);
		}
		return update;
	}
	
	private Integer cellIntValue(Cell cell) {
		Integer value = null;
		if (cell != null) {
			switch (cell.getCellType()) {
			case Cell.CELL_TYPE_NUMERIC:
				value = (int) cell.getNumericCellValue();
				// logger.log(Level.INFO, "Waarde is " + value + " !");
				break;
			case Cell.CELL_TYPE_FORMULA:
				switch(cell.getCachedFormulaResultType()) {
					case Cell.CELL_TYPE_NUMERIC:
						value = (int) cell.getNumericCellValue();
						break;
		    		default:
		    			//logger.log(Level.WARNING, "Waarde is geen Numeric!");
					}
				break;
    		default:
    			//logger.log(Level.WARNING, "Waarde is geen Numeric!");
			}
		}
		return value;	
	}

	private Double cellDoubleValue(Cell cell) {
		Double value = null;
		if (cell != null) {
			switch (cell.getCellType()) {
			case Cell.CELL_TYPE_NUMERIC:
				value = (double) cell.getNumericCellValue();
				// logger.log(Level.INFO, "Waarde is " + value + " !");
				break;
			case Cell.CELL_TYPE_FORMULA:
				switch(cell.getCachedFormulaResultType()) {
					case Cell.CELL_TYPE_NUMERIC:
						//System.out.println("Last evaluated as: " + cell.getNumericCellValue());
						value = (double) cell.getNumericCellValue();
						break;
				}
    		default:
    			//logger.log(Level.WARNING, "Waarde is geen Numeric!");
			}
		}
		return value;	
	}

	private String cellStringValue(Cell cell) {
		String value = null;
		if (cell != null) {
			switch (cell.getCellType()) {
			case Cell.CELL_TYPE_STRING:
				value = cell.getStringCellValue();
    			// logger.log(Level.INFO, "Waarde is " + value + " !");
    			break;
			case Cell.CELL_TYPE_FORMULA:
				switch(cell.getCachedFormulaResultType()) {
					case Cell.CELL_TYPE_STRING:
						//System.out.println("Last evaluated as \"" + cell.getRichStringCellValue() + "\"");
						value = cell.getRichStringCellValue().getString();
						break;
		}
    		default:
    			//logger.log(Level.WARNING, "Waarde is geen String!");
			}
		}
		return value;
	}

}
