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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.poi.ss.usermodel.Cell;
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
			FileInputStream file = new FileInputStream("Indeling.xlsm");
			XSSFWorkbook workbook = new XSSFWorkbook(file);
			for (Groep groep : groepen) {
				logger.log(Level.INFO, "Exporteer groep : " + groep.getNaam());
				XSSFSheet sheet = workbook.cloneSheet(sheetindx[groep.getGrootte()]);
				updateCell(sheet, 0, 6, groep.getNaam());
				for (int i = 0; i < groep.getGrootte(); i++) {
					updateCell(sheet, 3 + i, 2, groep.getSpeler(i).getNaam());
					updateCell(sheet, 3 + i, 3, groep.getSpeler(i).getKnsbnummer());
					updateCell(sheet, 3 + i, 5, groep.getSpeler(i).getRating());
				}
				sheet.setForceFormulaRecalculation(true);
				// Set print margins
				workbook.setSheetName(workbook.getSheetIndex(sheet), groep.getNaam());
				XSSFPrintSetup ps = sheet.getPrintSetup();
				ps.setFitWidth((short)1);
				sheet.setFitToPage(true);
				sheet.setAutobreaks(false);
				workbook.setPrintArea(workbook.getSheetIndex(sheet), 1, 26, 0, pagelngth[groep.getGrootte()]);
				sheet.setColumnBreak(18);
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
