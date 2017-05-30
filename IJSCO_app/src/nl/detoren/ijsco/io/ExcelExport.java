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
 * - Pagina eindes zijn niet goed ingesteld in de gegenereerde Excel
 */
package nl.detoren.ijsco.io;

import java.awt.Color;
import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFPrintSetup;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import nl.detoren.ijsco.data.Groep;
import nl.detoren.ijsco.data.Groepen;

public class ExcelExport implements ExportInterface {

	private final static Logger logger = Logger.getLogger(ExcelExport.class.getName());

	public void exportGroepen(Groepen groepen) {
		try {
			if (groepen == null) return;
			int[] sheetindx = new int[] { -1, -1, -1, -1,  3, -1,  2, -1,  1, -1,  0, -1, -1, -1, -1, -1, -1, -1 };
			int[] pagelngth = new int[] { -1, -1, -1, -1, 20, -1, 35, -1, 54, -1, 77, -1, -1, -1, -1, -1, -1, -1 };
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
				XSSFSheet sheet = workbook.cloneSheet(sheetindx[groep.getGrootte()]);
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
					updateCell(sheet2, sheet2row, 1, groep.getSpeler(i).getNaam());
					updateCell(sheet2, sheet2row, 2, groep.getSpeler(i).getKnsbnummer());
					updateCell(sheet2, sheet2row, 3, groep.getSpeler(i).getRating());
					if (groep.getSpeler(i).getNaam() != "Bye") {
						updateCell(sheet3, sheet3row, 0, groep.getSpeler(i).getNaam());
						updateCell(sheet3, sheet3row, 1, groep.getSpeler(i).getKnsbnummer());
						updateCell(sheet3, sheet3row, 2, groep.getSpeler(i).getRating());
						updateCell(sheet3, sheet3row, 3, groep.getNaam());
					}
					sheet2row++;
					sheet3row++;
				}
				sheet2row++;
				sheet.setForceFormulaRecalculation(true);
				// Set print margins
				workbook.setSheetName(workbook.getSheetIndex(sheet), groep.getNaam());
				XSSFPrintSetup ps = sheet.getPrintSetup();
				ps.setFitWidth((short)1);
				sheet.setFitToPage(true);
				sheet.setAutobreaks(false);
				workbook.setPrintArea(workbook.getSheetIndex(sheet), 1, 26, 0, pagelngth[groep.getGrootte()]);
				sheet.setColumnBreak(18);
			    String password= "abcd";
			    sheet.protectSheet(password);
				sheet.enableLocking();
			}
			// Remove template sheets
			for (int i = 0; i < 4; i++) {
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
		} catch (Exception e) {
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

}
