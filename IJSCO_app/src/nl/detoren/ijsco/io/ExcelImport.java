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
 * Known issues in this code
 * - 
 */
package nl.detoren.ijsco.io;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import nl.detoren.ijsco.data.Groep;
import nl.detoren.ijsco.data.Groepen;
import nl.detoren.ijsco.data.Speler;
import nl.detoren.ijsco.data.Spelers;
import nl.detoren.ijsco.data.Status;
import nl.detoren.ijsco.data.Wedstrijd;
import nl.detoren.ijsco.ui.control.IJSCOController;

public class ExcelImport implements ImportInterface {
	
	private final static Logger logger = Logger.getLogger(ExcelImport.class.getName());
	
	public void importeerUitslagen(File file) {
		
        Groepen groepen = new Groepen();
		try {
			
            FileInputStream excelFile = new FileInputStream(file);
            Workbook workbook = new XSSFWorkbook(excelFile);

        	for(int i=0;i < workbook.getNumberOfSheets();i++){
        		Sheet sheet = workbook.getSheetAt(i);
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
                				default:
                        			logger.log(Level.WARNING, "Uitslagen verwerken voor groepsgrootte " + groepsgrootte + " niet ondersteund!");
                        			break;
        			    	}
        			  	}
        			}	
        		}
            }
        	workbook.close();
		}
		catch (Exception ex){
			logger.log(Level.SEVERE, "Exception! Internal error is " + ex.getMessage());
		}
		// Print resultaat;
		logger.log(Level.INFO, groepen.toString());
	}

	private Groep importeerGroep10(Sheet sheet) {
		int groepsgrootte = 10;
		int rowidxbase = 17;
		int columnuitslag = 19;
		int columnwedstrijdidwit = 47;	
		int columnwedstrijdidzwart = 48;			
		Groep groep = importeergroep(groepsgrootte, sheet, rowidxbase, columnuitslag, columnwedstrijdidwit, columnwedstrijdidzwart);
		logger.log(Level.INFO, "Import groep 10 klaar");
		return groep;		
	}

	private Groep importeerGroep8(Sheet sheet) {
		int groepsgrootte = 8;
		int rowidxbase = 15;
		int columnuitslag = 19;
		int columnwedstrijdidwit = 50;	
		int columnwedstrijdidzwart = 51;			
		Groep groep = importeergroep(groepsgrootte, sheet, rowidxbase, columnuitslag, columnwedstrijdidwit, columnwedstrijdidzwart);
		logger.log(Level.INFO, "Import groep 8 klaar");
		return groep;
	}

	private Groep importeerGroep6(Sheet sheet) {
		int groepsgrootte = 6;
		int rowidxbase = 13;
		int columnuitslag = 19;
		int columnwedstrijdidwit = 46;	
		int columnwedstrijdidzwart = 47;	
		Groep groep = importeergroep(groepsgrootte, sheet, rowidxbase, columnuitslag, columnwedstrijdidwit, columnwedstrijdidzwart);
		logger.log(Level.INFO, "Import groep 6 klaar");
		return groep;
	}

	private Groep importeerGroep4(Sheet sheet) {
		int groepsgrootte = 4;
		int rowidxbase = 11;
		int columnuitslag = 18;
		int columnwedstrijdidwit = 42;	
		int columnwedstrijdidzwart = 43;	
		Groep groep = importeergroep(groepsgrootte, sheet, rowidxbase, columnuitslag, columnwedstrijdidwit, columnwedstrijdidzwart);
		logger.log(Level.INFO, "Import groep 4 klaar");
		return groep;
	}

	private Groep importeergroep(int groepsgrootte, Sheet sheet, int rIdxbase, int cUitslag, int cWedstrijdIDWit, int cWedstrijdIDZwart) {
		Row row;
		HashMap<Integer, Speler> OSBOSpelers = IJSCOController.getI().getStatus().OSBOSpelers;
		Groep groep = new Groep(groepsgrootte, cellStringValue(sheet.getRow(0).getCell(6)));
		for(int i=0;i <= groepsgrootte-2;i++){
			for(int j=0;j <= (groepsgrootte-2)/2;j++){
				int uitslagcode;
				row = sheet.getRow(rIdxbase+(i*(3+(groepsgrootte-2)/2))+j);
				if (row != null) {
					uitslagcode = cellIntValue(row.getCell(cUitslag));
					if (uitslagcode>=0 && uitslagcode <10) {
						Wedstrijd wedstrijd = new Wedstrijd();
						Integer speleridwit; 
						try {
							speleridwit = cellIntValue(row.getCell(cWedstrijdIDWit));
						} catch (Exception ex) {
							logger.log(Level.WARNING, "Player number for white not found.");
							speleridwit = 0;
						}
						Integer speleridzwart;
						try {
							speleridzwart = cellIntValue(row.getCell(cWedstrijdIDZwart));
						} catch (Exception ex) {
							logger.log(Level.WARNING, "Player number for black not found.");
							speleridzwart = 0;
						}
						Integer knsbwit;
						try {
							knsbwit = cellIntValue(sheet.getRow(2+speleridwit).getCell(3));
						} catch (Exception ex) {
							logger.log(Level.WARNING, "Player KNSB number for white not found.");
							knsbwit = 0;
						}
						String naamwit;
						try {
							naamwit = cellStringValue(sheet.getRow(2+speleridwit).getCell(2));
						} catch (Exception ex) {
							logger.log(Level.WARNING, "Player name for white not found.");
							naamwit = "White";
						}
						Integer knsbzwart;
						try {
							knsbzwart = cellIntValue(sheet.getRow(2+speleridzwart).getCell(3));
						} catch (Exception ex) {
							logger.log(Level.WARNING, "Player number for black not found.");
							knsbzwart = 0;
						}
						String naamzwart;
						try {
							naamzwart = cellStringValue(sheet.getRow(2+speleridzwart).getCell(2));
						} catch (Exception ex) {
							logger.log(Level.WARNING, "Player name for black not found.");
							naamzwart = "Black";
						}
						Speler wit = new Speler();
						Speler zwart = new Speler();
						for (Speler s :OSBOSpelers.values()) {
							int knsbnummer = 0;
							try {
								knsbnummer = s.getKnsbnummer();
							} catch (Exception ex) {
								logger.log(Level.WARNING, "Exception in finding knsbnummer " + knsbnummer + " in OSBO list");
							}
							if (knsbnummer == knsbwit) {
								wit = s;
							}
							if (knsbnummer == knsbzwart) {
								zwart = s;
							}
						}
						if (wit.getNaam() == "???" ) {
							logger.log(Level.WARNING, "Speler wit is niet gevonden. Knsbnummer " + knsbwit);
							wit.setKnsbnummer(knsbwit);
							wit.setNaamHandmatig(naamwit);
						}
						if (zwart.getNaam() == "???" ) {
							logger.log(Level.WARNING, "Speler zwart is niet gevonden. Knsbnummer " + knsbzwart);
							zwart.setKnsbnummer(knsbzwart);
							zwart.setNaamHandmatig(naamzwart);
						}
						boolean bye = false;
						if (wit.getNaamHandmatig()!= null)
							if (wit.getNaamHandmatig().equals("Bye")) bye = true;
						if (zwart.getNaamHandmatig()!= null)
							if (zwart.getNaamHandmatig().equals("Bye")) bye = true;
						if (!bye) {
							wedstrijd.setWit(wit);
							wedstrijd.setZwart(zwart);
							try {
								logger.log(Level.INFO, "Setting SpelerWit : " + wit.getNaam() + " - KNSBWit : " + wit.getKnsbnummer());
								logger.log(Level.INFO, "Setting SpelerZwart : " + zwart.getNaam() + " - KNSBZwart : " + zwart.getKnsbnummer());
							}
							catch (Exception ex) {}
						
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
    		default:
    			logger.log(Level.WARNING, "Waarde is geen Numeric!");
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
    		default:
    			logger.log(Level.WARNING, "Wwaarde is geen String!");
			}
		}
		return value;
	}

}
