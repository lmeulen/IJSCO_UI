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
 * - Enter op naamveld linksboven gelijk aan toevoeg knop
 * - Kunnen bewerken/verwijderen van een speler
 * - Overzicht aantal aanwezige deelnemers
 * - Verwijderen/bewerken speler in contextmenu
 * - Inlezen OSBO lijst
 * - Inlezen deelnemers (toevoegen)
 * - Wissen deelnemerslijst
 */
package nl.detoren.ijsco.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.gson.Gson;

import nl.detoren.ijsco.data.Groep;
import nl.detoren.ijsco.data.Speler;
import nl.detoren.ijsco.data.Status;
import nl.detoren.ijsco.ui.control.IJSCOIndeler;
import nl.detoren.ijsco.ui.control.Suggesties;
import nl.detoren.ijsco.ui.model.DeelnemersModel;
import nl.detoren.ijsco.ui.model.SchemaModel;

@SuppressWarnings("serial")
public class Mainscreen extends JFrame {

	private SchemaModel schemaModel;
	private DeelnemersModel deelnemersModel;
	private JTable schemaTabel;
	private JPanel hoofdPanel;

	private JTextArea groepenText;

	IJSCOIndeler indeler;
	private Status status;
	private final static Logger logger = Logger.getLogger(Mainscreen.class.getName());

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Mainscreen window = new Mainscreen();
					window.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Mainscreen() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		indeler = new IJSCOIndeler();
		if (!leesStatus("status.json")) {
			status = new Status();
			status.deelnemers = indeler.bepaalDeelnemers();
		}

//		Deelnemers tmp = (new OSBOLoader()).laadBestand("OSBO Jeugd-rating-lijst.htm");
//		status.OSBOSpelers = new HashMap<>();
//		for (Speler d : tmp) {
//			status.OSBOSpelers.put(d.getKnsbnummer(), d);
//		}

		// Frame
		setBounds(25, 25, 1300, 700);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("IJSCO Groepenindeler");
		getContentPane().setLayout(new GridLayout(1, 0, 0, 0));

		// Frame - Hoofdpanel
		hoofdPanel = new JPanel();
		getContentPane().add(hoofdPanel);
		hoofdPanel.setLayout(new GridLayout(1, 4, 0, 0));

		// LINKS: Deelnemers
		hoofdPanel.add(createDeelnemersPanel());

		// LINKSMIDDEN: INSTELLINGEN
		hoofdPanel.add(createInstellingenPanel());

		// RECHSTMIDDEN: SCENARIOS
		hoofdPanel.add(createPanelScenarios());

		// RECHTS: GROEPEN
		hoofdPanel.add(createPanelGroepen());

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				bewaarStatus();
			}
		});
	}

	public JPanel createPanelGroepen() {
		JPanel panel = new JPanel();
		panel.setBackground(Color.YELLOW);
		panel.setLayout(new GridLayout(1, 0));
		groepenText = new JTextArea(40, 40);
		groepenText.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		groepenText.setFont(new Font("courier new", Font.PLAIN, 12));
		groepenText.setLineWrap(false);
		if (status.groepen != null) {
			groepenText.setText(status.groepen.getDescription());
			groepenText.setCaretPosition(0);
		}
		JScrollPane scrollpane = new JScrollPane(groepenText);
		scrollpane.setAutoscrolls(true);
		scrollpane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		panel.add(scrollpane);
		return panel;
	}

	public JPanel createPanelScenarios() {
		JPanel panel = new JPanel();
		panel.setBackground(Color.RED);
		panel.setLayout(new GridLayout(1, 0));
		schemaModel = new SchemaModel(panel, status.schemas);
		schemaTabel = new JTable(schemaModel) {
			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component c = super.prepareRenderer(renderer, row, column);
				// Alternate row color
				if (!isRowSelected(row)) {
					c.setBackground(row % 2 == 0 ? Color.WHITE : Color.LIGHT_GRAY);
				}
				return c;
			}
		};
		schemaTabel.getModel().addTableModelListener(new TableModelListener() {

			@Override
			public void tableChanged(TableModelEvent arg0) {
				panel.getParent().getParent().repaint();
			}

		});
		JScrollPane scrollpane = new JScrollPane();
		scrollpane.setViewportView(schemaTabel);
		panel.add(scrollpane);
		return panel;
	}

	public JPanel createInstellingenPanel() {
		JPanel panel = new JPanel();
		panel.setBackground(Color.ORANGE);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 128, 32, 0 };
		gbl_panel.rowHeights = new int[] { 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 0 };
		gbl_panel.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		// Aantal groepen
		JLabel label_4 = new JLabel("Aantal groepen");
		panel.add(label_4, new ExtendedConstraints(0, 0, 2, 1));
		panel.add(new JLabel("Minimum:"), new ExtendedConstraints(0, 1));
		JTextField tfMinGroepen = new JTextField(Integer.toString(status.minGroepen), 10);
		tfMinGroepen.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				status.minGroepen = newIntegerValue(tfMinGroepen, status.minGroepen);
			}
		});
		panel.add(tfMinGroepen, new ExtendedConstraints(1, 1));
		panel.add(new JLabel("Maximum:"), new ExtendedConstraints(0, 2));
		JTextField tfMaxGroepen = new JTextField(Integer.toString(status.maxGroepen), 10);
		tfMaxGroepen.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				status.maxGroepen = newIntegerValue(tfMaxGroepen, status.maxGroepen);
			}
		});
		panel.add(tfMaxGroepen, new ExtendedConstraints(1, 2));

		// Aantal spelers
		panel.add(new JLabel("Aantal spelers per groep"), new ExtendedConstraints(0, 3, 2, 1));
		panel.add(new JLabel("Minimum:"), new ExtendedConstraints(0, 4));
		JTextField tfMinSpelers = new JTextField(Integer.toString(status.minSpelers), 10);
		tfMinSpelers.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				status.minSpelers = newIntegerValue(tfMinSpelers, status.minSpelers);
			}
		});
		panel.add(tfMinSpelers, new ExtendedConstraints(1, 4));
		panel.add(new JLabel("Maximum:"), new ExtendedConstraints(0, 5));
		JTextField tfMaxSpelers = new JTextField(Integer.toString(status.maxSpelers), 10);
		tfMaxSpelers.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				status.maxSpelers = newIntegerValue(tfMaxSpelers, status.maxSpelers);
			}
		});
		panel.add(tfMaxSpelers, new ExtendedConstraints(1, 5));

		// Delta aantal spelers
		panel.add(new JLabel("Delta spelers voor hoogste en laagste groepen"), new ExtendedConstraints(0, 6, 2, 1));
		panel.add(new JLabel("Minimum:"), new ExtendedConstraints(0, 7));
		JTextField tfMinDelta = new JTextField(Integer.toString(status.minDeltaSpelers), 10);
		tfMinDelta.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				status.minDeltaSpelers = newIntegerValue(tfMinDelta, status.minDeltaSpelers);
			}
		});
		panel.add(tfMinDelta, new ExtendedConstraints(1, 7));
		panel.add(new JLabel("Maximum:"), new ExtendedConstraints(0, 8));
		JTextField tfMaxDelta = new JTextField(Integer.toString(status.maxDeltaSpelers), 10);
		tfMaxDelta.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				status.maxDeltaSpelers = newIntegerValue(tfMaxDelta, status.maxDeltaSpelers);
			}
		});
		panel.add(tfMaxDelta, new ExtendedConstraints(1, 8));

		// Delta aantal groepen met afwijkend aantal spelers
		panel.add(new JLabel("Aantal afwijkende groepen aan boven- en onderzijde"),
				new ExtendedConstraints(0, 9, 2, 1));
		panel.add(new JLabel("Minimum:"), new ExtendedConstraints(0, 10));
		JTextField tfMinDeltaGroepen = new JTextField(Integer.toString(status.minAfwijkendeGroepen), 10);
		tfMinDeltaGroepen.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				status.minAfwijkendeGroepen = newIntegerValue(tfMinDeltaGroepen, status.minAfwijkendeGroepen);
			}
		});
		panel.add(tfMinDeltaGroepen, new ExtendedConstraints(1, 10));
		panel.add(new JLabel("Maximum:"), new ExtendedConstraints(0, 11));
		JTextField tfMaxDeltaGroepen = new JTextField(Integer.toString(status.maxAfwijkendeGroepen), 10);
		tfMaxDeltaGroepen.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				status.maxAfwijkendeGroepen = newIntegerValue(tfMaxDeltaGroepen, status.maxAfwijkendeGroepen);
			}
		});
		panel.add(tfMaxDeltaGroepen, new ExtendedConstraints(1, 11));

		// Byes
		panel.add(new JLabel("Aantal toegestane byes"), new ExtendedConstraints(0, 12, 2, 1));
		panel.add(new JLabel("Minimum:"), new ExtendedConstraints(0, 13));
		JTextField tfMinByes = new JTextField(Integer.toString(status.minToegestaneByes), 10);
		tfMinByes.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				status.minToegestaneByes = newIntegerValue(tfMinByes, status.minToegestaneByes);
			}
		});
		panel.add(tfMinByes, new ExtendedConstraints(1, 13));
		panel.add(new JLabel("Maximum:"), new ExtendedConstraints(0, 14));
		JTextField tfMaxByes = new JTextField(Integer.toString(status.maxToegestaneByes), 10);
		tfMaxByes.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				status.maxToegestaneByes = newIntegerValue(tfMaxByes, status.maxToegestaneByes);
			}
		});
		panel.add(tfMaxByes, new ExtendedConstraints(1, 14));
		JButton bSchemas = new JButton("Bepaal mogelijkheden");
		bSchemas.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				status.groepen = null;
				bepaalSchemas();
				schemaTabel.getSelectionModel().clearSelection();
				groepenText.setText("");
				panel.getParent().repaint();
			}

		});
		panel.add(bSchemas, new ExtendedConstraints(0, 16));
		JButton bGroepen = new JButton("Bepaal groepen");
		bGroepen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (schemaTabel != null) {
					int row = schemaTabel.getSelectedRow();
					bepaalGroepen(row);
					maakExcelSchema();
				}
				panel.getParent().repaint();
			}
		});
		panel.add(bGroepen, new ExtendedConstraints(1, 16));
		panel.add(new JLabel(" "), new ExtendedConstraints(0, 17, 2, 1));
		fixedComponentSize(panel, 300, 400);
		return panel;
	}

	public JPanel createDeelnemersPanel() {
		JPanel panel = new JPanel(false);
		panel.setBackground(Color.BLACK);
		//panel.setLayout(new GridLayout(1, 0));
		panel.setLayout(new BorderLayout());
		{
			JPanel innerPanel = new JPanel();
			//innerPanel.setLayout(new GridLayout(1, 0));
			innerPanel.add(new JLabel("Naam:"), BorderLayout.NORTH);
			JTextField deelnemer = new JTextField(15);
			ArrayList<String> words = new ArrayList<>();
			for (Speler s : status.OSBOSpelers.values()) {
				words.add(s.getNaam());
				words.add(Integer.toString(s.getKnsbnummer()));
			}
			@SuppressWarnings("unused")
			Suggesties suggesties = new Suggesties(deelnemer, this, words, 2);
			innerPanel.add(deelnemer, BorderLayout.NORTH);
			JButton btVoegToe = new JButton("Voeg toe");
			btVoegToe.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					actieVoegSpelerToe(deelnemer.getText().trim());
					deelnemer.setText("");
				}
			});
			innerPanel.add(btVoegToe);
			panel.add(innerPanel);
		}
		// panel_deelnemers.add(new JLabel("Deelnemers IJSCO toernooi"));
		deelnemersModel = new DeelnemersModel(panel, status.deelnemers);
		JTable deelnemersTabel = new JTable(deelnemersModel) {
			private static final long serialVersionUID = -8293073016982337108L;

			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component c = super.prepareRenderer(renderer, row, column);
				// Tooltip
				if (c instanceof JComponent) {
					DeelnemersModel model = (DeelnemersModel) getModel();
					((JComponent) c).setToolTipText(model.getToolTip(row, column).toString());
				}

				// Alternate row color
				if (!isRowSelected(row)) {
					c.setBackground(row % 2 == 0 ? Color.WHITE : Color.LIGHT_GRAY);
				}
				return c;
			}
		};

		deelnemersTabel.getModel().addTableModelListener(new TableModelListener() {

			@Override
			public void tableChanged(TableModelEvent arg0) {
				status.groepen = null;
				status.schemas = null;
				status.schema = null;
				groepenText.setText("");
				schemaModel.setSchemas(null);
				schemaModel.fireTableDataChanged();
				panel.repaint();
			}

		});
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(deelnemersTabel);
		panel.add(scrollPane, BorderLayout.SOUTH);

		fixedColumSize(deelnemersTabel.getColumnModel().getColumn(0), 30);
		fixedColumSize(deelnemersTabel.getColumnModel().getColumn(1), 55);
		fixedColumSize(deelnemersTabel.getColumnModel().getColumn(2), 150);
		fixedColumSize(deelnemersTabel.getColumnModel().getColumn(3), 40);
		fixedComponentSize(scrollPane, 300, 630);
		return panel;
	}

	protected void actieVoegSpelerToe(String trim) {
		int knsbnr = 0;
		try {
			knsbnr = Integer.parseInt(trim );
		} catch (NumberFormatException e) {
			knsbnr = -1;
		}
		Speler nieuw = null;
		if (knsbnr > 0) {
			nieuw = status.OSBOSpelers.get(knsbnr);
			nieuw = (nieuw != null) ? nieuw : new Speler(knsbnr, "", -1, -1);
		} else {
			for (Speler s : status.OSBOSpelers.values()) {
				if (s.getNaam().equals(trim)) {
					nieuw = s;
					break;
				}
			}
			nieuw = (nieuw != null) ? nieuw : new Speler(trim);
		}

		BewerkSpelerDialoog rd = new BewerkSpelerDialoog(new JFrame(), "Bewerk Speler", nieuw, deelnemersModel);
		rd.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				System.out.println("closing...");
			}

		});
		rd.setVisible(true);

	}


	/**
	 * Bepaal de nieuwe integer waarde voor een textfield Als het tekstveld een
	 * geldig getal bevat, wordt deze waarde geretourneerd anders de oude waarde
	 *
	 * @param text
	 * @param oldValue
	 * @return
	 */
	private int newIntegerValue(JTextField text, int oldValue) {
		int value;
		try {
			value = Integer.parseInt(text.getText());
		} catch (Exception e) {
			value = oldValue;
		}
		text.setText(Integer.toString(value));
		return value;
	}

	public boolean leesStatus(String bestandsnaam) {
		try {
			logger.log(Level.INFO, "Lees status uit bestand " + bestandsnaam);
			Gson gson = new Gson();
			BufferedReader br = new BufferedReader(new FileReader(bestandsnaam));
			Status nieuw = gson.fromJson(br, Status.class);
			status = nieuw; // assure exception is thrown when things go wrong
			return true;
		} catch (Exception e) {
			// Could not read status
			System.out.println("Failed to read status : " + e.getMessage());
			return false;
		}
	}

	private void bewaarStatus() {
		try {
			String bestandsnaam = "status.json";
			logger.log(Level.INFO, "Sla status op in bestand " + bestandsnaam);
			Gson gson = new Gson();
			String jsonString = gson.toJson(status);
			// write converted json data to a file
			FileWriter writer = new FileWriter(bestandsnaam);
			writer.write(jsonString);
			writer.close();
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error saving status : " + e.getMessage());
		}
	}

	private void fixedComponentSize(Component c, int width, int height) {
		c.setMinimumSize(new Dimension(width, height));
		c.setMaximumSize(new Dimension(width, height));
		c.setPreferredSize(new Dimension(width, height));
		c.setSize(new Dimension(width, height));
	}

	private void fixedColumSize(TableColumn c, int width) {
		c.setMinWidth(width);
		c.setMaxWidth(width);
	}

	/**
	 * Bepaal de mogelijke schemas op basis van het aantals spelers en de
	 * gemaakte instellingen
	 */
	public void bepaalSchemas() {
		int ndeelnemers = status.deelnemers.aantalAanwezig();
		logger.log(Level.INFO, "Bepaal mogelijkheden voor n=" + ndeelnemers);
		status.schemas = indeler.mogelijkeSchemas(status);
		status.schema = null;
		schemaModel.setSchemas(status.schemas);
	}

	/**
	 * Bepaal de groepsindeling op basis van het geselecteerde schema
	 *
	 * @param row
	 *            Gesleecteerde schema
	 */
	public void bepaalGroepen(int row) {
		status.schema = schemaModel.getSchema(row);
		logger.log(Level.INFO, "Bepaal groepen voor schema " + status.schema);
		if (status.schema != null) {
			status.groepen = indeler.bepaalGroep(status.schema, status.deelnemers);
			System.out.println(status.groepen.getDescription());
			groepenText.setText(status.groepen.getDescription());
			groepenText.setCaretPosition(0);
		}
	}

	public void maakExcelSchema() {
		try {
			if (status.groepen == null) return;
			int[] sheetindx = new int[] { -1, -1, -1, -1, 3, -1, 2, -1, 1, -1, 0, -1, -1, -1, -1, -1, -1, -1 };
			FileInputStream file = new FileInputStream("Indeling.xlsm");
			XSSFWorkbook workbook = new XSSFWorkbook(file);
			for (Groep groep : status.groepen) {
				logger.log(Level.INFO, "Exporteer groep : " + groep.getNaam());
				XSSFSheet sheet = workbook.cloneSheet(sheetindx[groep.getGrootte()]);
				updateCell(sheet, 0, 6, groep.getNaam());
				for (int i = 0; i < groep.getGrootte(); i++) {
					updateCell(sheet, 3 + i, 2, groep.getSpeler(i).getNaam());
					updateCell(sheet, 3 + i, 3, groep.getSpeler(i).getKnsbnummer());
					updateCell(sheet, 3 + i, 5, groep.getSpeler(i).getRating());
				}
				sheet.setForceFormulaRecalculation(true);
				workbook.setSheetName(workbook.getSheetIndex(sheet), groep.getNaam());
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