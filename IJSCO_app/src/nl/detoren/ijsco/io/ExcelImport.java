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

public class ExcelImport implements ImportInterface {
	
	private final static Logger logger = Logger.getLogger(ExcelImport.class.getName());
	
	public void importeerUitslagen(File file) {
		
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
                					importeerGroep4();
                					break;
                				case 6: 
                					importeerGroep6();
                					break;
                				case 8:
                					importeerGroep8();
                					break;
                				case 10:
                					importeerGroep10();
                					break;
                				default:
                        			logger.log(Level.WARNING, "Uitslagen verwerken voor groepsgrootte " + groepsgrootte + " niet ondersteund!");
                        			break;
        			    	}
        			  	}
        			}	
        		}
            }
		}
		catch (Exception ex){
		}
	}

	private void importeerGroep10() {
		// TODO Auto-generated method stub
		
	}

	private void importeerGroep8() {
		// TODO Auto-generated method stub
		
	}

	private void importeerGroep6() {
		// TODO Auto-generated method stub
		
	}

	private void importeerGroep4() {
		// TODO Auto-generated method stub
		
	}
}
