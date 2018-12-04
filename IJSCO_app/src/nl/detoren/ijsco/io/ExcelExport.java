/**
 * Copyright (C) 2016-2018 Leo van der Meulen & Lars Dam
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
 * - Pagina eindes zijn niet goed ingesteld in de gegenereerde Excel
 */
package nl.detoren.ijsco.io;

import java.awt.Color;
import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFPrintSetup;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import nl.detoren.ijsco.data.Groep;
import nl.detoren.ijsco.data.Groepen;
import nl.detoren.ijsco.data.Speler;

public class ExcelExport implements ExportInterface {

	private final static Logger logger = Logger.getLogger(ExcelExport.class.getName());

	public void exportGroepen(Groepen groepen) {
	    String password= "abcd";
		try {
			if (groepen == null) return;
			// sheetindx geeft index in Excel template op basis van groepsgrootte. -1: geen sheet voor groepsgrootte 
			int[] sheetindx = new int[] { -1, -1, -1, -1,  5, -1,  4, -1,  3, -1,  2, -1, 1, -1, 0, -1, -1, -1 };
			// columnsize geeft lengte in Excel template op basis van groepsgrootte. -1: geen sheet voor groepsgrootte
			int[] columnsize = new int[] { -1, -1, -1, -1, 20, -1, 35, -1, 54, -1, 77, -1, 100, -1, 127, -1, -1, -1 };
			// pagelngth geeft lengte in Excel template op basis van groepsgrootte. -1: geen sheet voor groepsgrootte
			int[] pagelngth = new int[] { -1, -1, -1, -1, 20, -1, 35, -1, 54, -1, 77, -1, 100, -1, 127, -1, -1, -1 };
			int sheet2row = 2;
			int sheet3row = 2;
			FileInputStream file = new FileInputStream("Indeling.xlsm");
			XSSFWorkbook workbook = new XSSFWorkbook(file);
		    XSSFCellStyle style1 = workbook.createCellStyle();
            style1.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		    style1.setFillForegroundColor(new XSSFColor(new java.awt.Color(180, 180, 180)));
            XSSFCellStyle my_style = workbook.createCellStyle();
            XSSFColor my_foreground=new XSSFColor(Color.ORANGE);
            XSSFColor my_background=new XSSFColor(Color.RED);
            my_style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            my_style.setFillForegroundColor(my_foreground);
            my_style.setFillBackgroundColor(my_background);
			XSSFSheet sheet2 = workbook.getSheet("Groepsindeling");
			XSSFSheet sheet3 = workbook.getSheet("Deelnemerslijst");
			updateCell(sheet3, sheet3row, 0, "Naam", style1);
			updateCell(sheet3, sheet3row, 1, "KNSB nr", style1);
			updateCell(sheet3, sheet3row, 2, "rating", style1);
			updateCell(sheet3, sheet3row, 3, "groep", style1);
			sheet3row++;
			for (Groep groep : groepen) {
				logger.log(Level.INFO, "Exporteer groep : " + groep.getNaam());
				XSSFSheet sheet = workbook.cloneSheet(sheetindx[groep.getGrootte()], groep.getNaam());
				updateCell(sheet, 0, 6, groep.getNaam());
				updateCell(sheet2, sheet2row, 1, groep.getNaam());
				sheet2row++;
				updateCell(sheet2, sheet2row, 0, "nr", style1);
				updateCell(sheet2, sheet2row, 1, "Naam", style1);
				updateCell(sheet2, sheet2row, 2, "KNSB nr", style1);
				updateCell(sheet2, sheet2row, 3, "rating", style1);
				sheet2row++;
				for (int i = 0; i < groep.getGrootte(); i++) {
					updateCell(sheet, 3 + i, 2, groep.getSpeler(i).getNaam());
					updateCell(sheet, 3 + i, 3, groep.getSpeler(i).getKnsbnummer());
					updateCell(sheet, 3 + i, 5, groep.getSpeler(i).getRating());
					updateCell(sheet2, sheet2row, 0, i+1);
					updateCell(sheet2, sheet2row, 1, "'" + sheet.getSheetName() + "'!" + org.apache.poi.ss.util.CellReference.convertNumToColString(2) + (4+i), true);
					updateCell(sheet2, sheet2row, 2,  "'" + sheet.getSheetName() + "'!" + org.apache.poi.ss.util.CellReference.convertNumToColString(3) + (4+i), true);
					updateCell(sheet2, sheet2row, 3,  "'" + sheet.getSheetName() + "'!" + org.apache.poi.ss.util.CellReference.convertNumToColString(5) + (4+i), true);
					if (groep.getSpeler(i).getNaam() != "Bye") {
						updateCell(sheet3, sheet3row, 0, "'" + sheet.getSheetName() + "'!" + org.apache.poi.ss.util.CellReference.convertNumToColString(2) + (4+i), true);
						updateCell(sheet3, sheet3row, 1, "'" + sheet.getSheetName() + "'!" + org.apache.poi.ss.util.CellReference.convertNumToColString(3) + (4+i), true);
						updateCell(sheet3, sheet3row, 2, "'" + sheet.getSheetName() + "'!" + org.apache.poi.ss.util.CellReference.convertNumToColString(5) + (4+i), true);
						updateCell(sheet3, sheet3row, 3, groep.getNaam());
					}
					sheet2row++;
					sheet3row++;
				}
				sheet2row++;
				sheet.setForceFormulaRecalculation(true);
				// Set print margins
				XSSFPrintSetup ps = sheet.getPrintSetup();
				ps.setLandscape(true);
				ps.setFitWidth((short)1);
				sheet.setFitToPage(true);
				sheet.setAutobreaks(false);
				workbook.setPrintArea(workbook.getSheetIndex(sheet), 1, 26, 0, pagelngth[groep.getGrootte()]);
				sheet.setColumnBreak(18);
			    sheet.protectSheet(password);
				sheet.enableLocking();
			}
			XSSFSheet sheet4 = workbook.cloneSheet(workbook.getSheetIndex(sheet3), "Deelnemerslijst (naam)");
			sortSheet(sheet4, 1,3, 62);
			//XSSFSheet sheet5 = workbook.cloneSheet(workbook.getSheetIndex(sheet3), "Deelnemerslijst (rating)");
			//sortSheet(sheet5, 1,4);
			sheet2.protectSheet(password);
			sheet3.protectSheet(password);
			sheet4.protectSheet(password);
			//sheet5.protectSheet(password);
			// Remove template sheets
			for (int i = 0; i < 6; i++) {
				workbook.removeSheetAt(0);
			}
			
			// Close input file
			file.close();
			// Store Excel to new file
			String filename = "Indeling resultaat.xlsm";
			File outputFile = new File(filename);
			FileOutputStream outFile = new FileOutputStream(outputFile);
			workbook.write(outFile);
			// Close output file
			workbook.close();
			outFile.close();
			// And open it in the system editor
			Desktop.getDesktop().open(outputFile);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Fout bij maken indeling excel : " + e.getMessage());
			
		}
	}

	/**
	 * Update a single cell in the Excel Sheet. The cell is specified by its row
	 * and column. Row and column numbers start with 0, so column A equals 0,
	 * column B equals 1, etc.
	 *
	 * @param sheet
	 *            The Excel sheet to update
	 * @param row
	 *            The row number, starting with 0
	 * @param col
	 *            The column number, staring with 0
	 * @param value
	 *            THe value to store in the cell
	 */
	private void updateCell(XSSFSheet sheet, int row, int col, String value) {
		Cell cell = getCell(sheet, row, col);
		cell.setCellValue(value.trim());
	}

	private void updateCell(XSSFSheet sheet, int row, int col, int value) {
		Cell cell = getCell(sheet, row, col);
		cell.setCellValue(value);
	}

	private void updateCell(XSSFSheet sheet, int row, int col, String value, boolean formula) {
		Cell cell = getCell(sheet, row, col);
		cell.setCellFormula(value);
	}
	
	private void updateCell(XSSFSheet sheet, int row, int col, XSSFCellStyle style) {
		Cell cell = getCell(sheet, row, col);
		cell.setCellStyle(style);
	}

	private void updateCell(XSSFSheet sheet, int row, int col, String value, XSSFCellStyle style) {
		Cell cell = getCell(sheet, row, col);
		cell.setCellValue(value.trim());
		cell.setCellStyle(style);
	}
	
	private void updateCell(XSSFSheet sheet, int row, int col, int value, XSSFCellStyle style) {
		Cell cell = getCell(sheet, row, col);
		cell.setCellValue(value);
		cell.setCellStyle(style);
	}

	
	private Cell getCell(XSSFSheet sheet, int row, int col) {
		Cell cell = null;

		// Retrieve the row and create when not valid
		XSSFRow sheetrow = sheet.getRow(row);
		if (sheetrow == null) {
			sheetrow = sheet.createRow(row);
		}
		// Retrieve the correct cell from the column
		cell = sheetrow.getCell(col);
		if (cell == null) {
			cell = sheetrow.createCell(col);
		}
		return cell;
	}
	
	/**
	 * Sorts (A-Z) rows by String column
	 * @param sheet - sheet to sort
	 * @param column - String column to sort by
	 * @param rowStart - sorting from this row down
	 */

	private void sortSheet(XSSFSheet sheet, int column, int rowStart, int rowEnd) {
    	try {
		FormulaEvaluator evaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();
		logger.log(Level.INFO, "sorting sheet: " + sheet.getSheetName());
	    boolean sorting = true;
	    //int lastRow = sheet.getLastRowNum();
	    while (sorting == true) {
	        sorting = false;
	        for (Row row : sheet) {
	            // skip if this row is before first to sort
	            if (row.getRowNum()<rowStart) continue;
	            // end if this is last row
	            if (rowEnd==row.getRowNum()) break;
	            Row row2 = sheet.getRow(row.getRowNum()+1);
	            if (row2 == null) continue;
	            int rownum1 = row.getRowNum();
	            int rownum2 = row2.getRowNum();
	            CellValue firstValue;
	            CellValue secondValue;
	            	firstValue = evaluator.evaluate(row.getCell(column));
	            	secondValue = evaluator.evaluate(row2.getCell(column));
	            //compare cell from current row and next row - and switch if secondValue should be before first
	            if (secondValue.toString().compareToIgnoreCase(firstValue.toString())<0) {            
	        		logger.log(Level.INFO, "Shifting rows" + sheet.getSheetName() + rownum1 + " - " +  rownum2);
	            	sheet.shiftRows(row2.getRowNum(), row2.getRowNum(), -1);
	            	logger.log(Level.INFO, "Shifting rows" + sheet.getSheetName() + rownum1 + " - " +  rownum2);
	                sheet.shiftRows(row.getRowNum(), row.getRowNum(), 1);
	                sorting = true;
	            }
	        }
	    }
    	}
    	catch (Exception ex) {
    		logger.log(Level.WARNING, "Failing Shifting rows" + sheet.getSheetName() + "Error " + ex.getMessage());
    	}
	}
}
