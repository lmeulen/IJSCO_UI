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
 * Problemen in deze code:
 */
package nl.detoren.ijsco.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.JButton;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import nl.detoren.ijsco.Configuratie;
import nl.detoren.ijsco.ui.ExtendedGridConstraints;
import nl.detoren.ijsco.ui.control.IJSCOController;
import nl.detoren.ijsco.ui.util.Utils;
import nl.detoren.ijsco.ui.view.ExtendedGridLayout;

/**
*
* @author Lars Dam
*/

/**
 * Panel met editor voor Configuratie object.
 * 
 */
public class ConfigurationDialog extends JDialog {

	private IJSCOController controller;
	private Configuratie config;

	private final static Logger logger = Logger.getLogger(ConfigurationDialog.class.getName());	
	
	private JTextField tfConfigfile;
	private JTextField tfStatusfile;
	private JTextField tfMinGroepen;
	private JTextField tfMaxGroepen;
	private JTextField tfMinSpelers;
	private JTextField tfMaxSpelers;
	private JTextField tfMinDelta;
	private JTextField tfMaxDelta;
	private JTextField tfMinDeltaGroepen;
	private JTextField tfMaxDeltaGroepen;
	private JTextField tfMinByes;
	private JTextField tfMaxByes;
	private JTextField tfNobyesmask;
	
	
	public ConfigurationDialog(Frame frame, String title) {
		super(frame, title);
		logger.log(Level.INFO, "Bewerk configuratie");
		controller = IJSCOController.getInstance();
		config = IJSCOController.c();
		setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		getContentPane().add(createPanel());
		setSize(600, 420);
		setLocationRelativeTo(frame);
	}
	
	private JPanel createPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		//JTabbedPane tabs = new JTabbedPane();
		//tabs.setTabPlacement(JTabbedPane.TOP);
		//tabs.addTab("Indeling", createPanelIndeling());
		//tabs.addTab("Export", createPanelExport());
		//Utils.fixedComponentSize(tabs, 600, 20);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		Utils.fixedComponentSize(buttonPanel, 600, 20);
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				//TODO find storeValues
				storeValues();
				setVisible(false);
				dispose();
			}
		});
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				setVisible(false);
				dispose();
			}
		});
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		panel.add(buttonPanel, BorderLayout.PAGE_START);
		panel.add(createPanelIndeling());
		//panel.add(tabs, BorderLayout.CENTER);

		return panel;
	}

	public JPanel createPanelIndeling() {
		JPanel tabInstellingen = new JPanel(false);
		tabInstellingen.setLayout(new ExtendedGridLayout(20, 2));
		int curRow = 0;

/*		// Header Row
		tabInstellingen.add(new JLabel(" "));
		tabInstellingen.add(new JLabel(" "));*/

		// Aantal groepen
		JLabel label_4 = new JLabel("Aantal groepen");
		tabInstellingen.add(label_4);
		tabInstellingen.add(new JLabel(" "));
		tabInstellingen.add(new JLabel("Minimum:"));
		tfMinGroepen = new JTextField(Integer.toString(config.minGroepen), 10);
		tfMinGroepen.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				config.minGroepen = Utils.newIntegerValue(tfMinGroepen, config.minGroepen);
			}
		});
		tabInstellingen.add(tfMinGroepen);
		tabInstellingen.add(new JLabel("Maximum:"));
		tfMaxGroepen = new JTextField(Integer.toString(config.maxGroepen), 10);
		tfMaxGroepen.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				config.maxGroepen = Utils.newIntegerValue(tfMaxGroepen, config.maxGroepen);
			}
		});
		tabInstellingen.add(tfMaxGroepen);

		// Aantal spelers
		tabInstellingen.add(new JLabel("Aantal spelers per groep"));
		tabInstellingen.add(new JLabel(" "));
		tabInstellingen.add(new JLabel("Minimum:"));
		tfMinSpelers = new JTextField(Integer.toString(config.minSpelers), 10);
		tfMinSpelers.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				config.minSpelers = Utils.newIntegerValue(tfMinSpelers, config.minSpelers);
			}
		});
		tabInstellingen.add(tfMinSpelers);
		tabInstellingen.add(new JLabel("Maximum:"));
		tfMaxSpelers = new JTextField(Integer.toString(config.maxSpelers), 10);
		tfMaxSpelers.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				config.maxSpelers = Utils.newIntegerValue(tfMaxSpelers, config.maxSpelers);
			}
		});
		tabInstellingen.add(tfMaxSpelers);

		// Delta aantal spelers
		tabInstellingen.add(new JLabel("Delta spelers voor uiterste groepen"));
		tabInstellingen.add(new JLabel(" "));
		tabInstellingen.add(new JLabel("Minimum:"));
		tfMinDelta = new JTextField(Integer.toString(config.minDeltaSpelers), 10);
		tfMinDelta.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				config.minDeltaSpelers = Utils.newIntegerValue(tfMinDelta, config.minDeltaSpelers);
			}
		});
		tabInstellingen.add(tfMinDelta);
		tabInstellingen.add(new JLabel("Maximum:"));
		tfMaxDelta = new JTextField(Integer.toString(config.maxDeltaSpelers), 10);
		tfMaxDelta.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				config.maxDeltaSpelers = Utils.newIntegerValue(tfMaxDelta, config.maxDeltaSpelers);
			}
		});
		tabInstellingen.add(tfMaxDelta);

		// Delta aantal groepen met afwijkend aantal spelers
		tabInstellingen.add(new JLabel("Aantal afwijkende groepen"));
		tabInstellingen.add(new JLabel(" "));
		tabInstellingen.add(new JLabel("Minimum:"));
		tfMinDeltaGroepen = new JTextField(Integer.toString(config.minAfwijkendeGroepen), 10);
		tfMinDeltaGroepen.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				config.minAfwijkendeGroepen = Utils.newIntegerValue(tfMinDeltaGroepen, config.minAfwijkendeGroepen);
			}
		});
		tabInstellingen.add(tfMinDeltaGroepen);
		tabInstellingen.add(new JLabel("Maximum:"));
		tfMaxDeltaGroepen = new JTextField(Integer.toString(config.maxAfwijkendeGroepen), 10);
		tfMaxDeltaGroepen.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				config.maxAfwijkendeGroepen = Utils.newIntegerValue(tfMaxDeltaGroepen, config.maxAfwijkendeGroepen);
			}
		});
		tabInstellingen.add(tfMaxDeltaGroepen);

		// Byes
		tabInstellingen.add(new JLabel("Aantal toegestane byes"));
		tabInstellingen.add(new JLabel(" "));
		tabInstellingen.add(new JLabel("Minimum:"));
		tfMinByes = new JTextField(Integer.toString(config.minToegestaneByes), 10);
		tfMinByes.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				config.minToegestaneByes = Utils.newIntegerValue(tfMinByes, config.minToegestaneByes);
			}
		});
		tabInstellingen.add(tfMinByes);

		tabInstellingen.add(new JLabel("Maximum:"));
		tfMaxByes = new JTextField(Integer.toString(config.maxToegestaneByes), 10);
		tfMaxByes.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				config.maxToegestaneByes = Utils.newIntegerValue(tfMaxByes, config.maxToegestaneByes);
			}
		});
		tabInstellingen.add(tfMaxByes);

		
//		for (int i = 0; i < 4; i++) {
//			panel.add(new JLabel("A"), new ExtendedConstraints(0, curRow));
//			panel.add(new JLabel("A"), new ExtendedConstraints(1, curRow++));
//		}

		//Utils.fixedComponentSize(tabInstellingen, 300, 400);
		return tabInstellingen;
	}

	public JPanel createPanelExport() {
		JPanel panel = new JPanel(false);
//		panel.setLayout(new ExtendedGridLayout(20, 2));
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 128, 32, 0 };
		gbl_panel.rowHeights = new int[] { 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 0 };
		gbl_panel.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

//		// public boolean exportTextShort = true;
//		panel.add(new JLabel("Exporteer uitslag kort formaat"));
//		cbExportShort = new JCheckBox("", config.exportTextShort);
//		panel.add(cbExportShort);
//		// public boolean exportTextLong = true;
//		panel.add(new JLabel("Export uitslag lang formaat"));
//		cbSaveLongformat = new JCheckBox("", config.exportTextLong);
//		panel.add(cbSaveLongformat);
//		// public boolean exportDoorschuivers = true;
//		panel.add(new JLabel("Voeg doorschuivers toe aan uitslag"));
//		cbSaveDoorschuivers = new JCheckBox("", config.exportDoorschuivers);
//		panel.add(cbSaveDoorschuivers);
//		// public String exportDoorschuiversStart
//		panel.add(new JLabel("Header doorschuivers"));
//		tfHeaderDoor = new JTextField(config.exportDoorschuiversStart, 30);
//		panel.add(tfHeaderDoor);
//		// public String exportDoorschuiversStop 
//		panel.add(new JLabel("Footer doorschuivers"));
//		tfFooterDoor = new JTextField(config.exportDoorschuiversStop, 30);
//		panel.add(tfFooterDoor);
//		panel.add(new JLabel(" "));
//		panel.add(new JLabel(" "));
//		// public boolean exportKEIlijst = true;
//		panel.add(new JLabel("Export KEI lijst"));
//		cbSaveKEI = new JCheckBox("", config.exportKEIlijst);
//		panel.add(cbSaveKEI);
//		// public boolean exportIntekenlijst = true;
//		panel.add(new JLabel("Export Intekenlijsten"));
//		cbSaveInteken = new JCheckBox("", config.exportIntekenlijst);
//		panel.add(cbSaveInteken);
//		panel.add(new JLabel(" "));
//		panel.add(new JLabel(" "));
//		// public boolean exportKNSBRating = true;
//		panel.add(new JLabel("Export KNSB rating bestand"));
//		cbSaveKNSB = new JCheckBox("", config.exportKNSBRating);
//		panel.add(cbSaveKNSB);
//		// public boolean exportOSBORating = true;
//		panel.add(new JLabel("Export OSBO rating bestand"));
//		cbSaveOSBO = new JCheckBox("", config.exportOSBORating);
//		panel.add(cbSaveOSBO);
//		panel.add(new JLabel(" "));
//		panel.add(new JLabel(" "));
//		// public boolean saveAdditionalStates = true;
//		panel.add(new JLabel("Sla additionale statusbestanden op"));
//		cbSaveAdditionals = new JCheckBox("", config.saveAdditionalStates);
//		panel.add(cbSaveAdditionals);
//		public String configuratieBestand = "configuratie";
//		panel.add(new JLabel("Prefix configuratiebestanden"));
//		tfConfigfile = new JTextField(config.configuratieBestand, 30);
//		tfConfigfile.setCaretPosition(0);
//		panel.add(tfConfigfile);
//		// public String statusBestand = "status";
//		panel.add(new JLabel("Prefix statusbestanden"));
//		tfStatusfile = new JTextField(config.statusBestand, 30);
//		tfStatusfile.setCaretPosition(0);
//		panel.add(tfStatusfile);
//		for (int i = 0; i < 4; ++i) {
//			panel.add(new JLabel(" "));
//			panel.add(new JLabel(" "));
//		}
		return panel;
	}
	
	/**
	 * Update een string veld in de configuratie
	 * @param c Configuratie object
	 * @param fieldname veldnaam in het configuratie object, moet type string hebben
	 * @param value Waarde om op te slaan
	 * @param minlengte Minimale lengte van de text
	 */
	private static void updateTextConfig(Configuratie c, String fieldname, String value, int minlengte) {
		logger.log(Level.INFO, "Saving value \'" + value + "\' to field " + fieldname);
		if ((value != null) && (value.length() >= minlengte)) {
			try {
				c.getClass().getField(fieldname).set(c, value);
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Update een integer veld in de configuratie. Indien nieuwe waarde niet tussen
	 * min en max, dan wordt de huidige waarde gehandhaafd.
	 * @param c Configuratie object
	 * @param fieldname veldnaam in het configuratie object, moet type int hebben
	 * @param value Waarde om op te slaan
	 * @param min Minimale nieuwe waarde
	 * @param max Maximale nieuwe waarde
	 */
	private static void updateIntConfig(Configuratie c, String fieldname, String value, int min, int max) {
		try {
			logger.log(Level.INFO, "Saving value \'" + value + "\' to field " + fieldname);
			int nieuw = Integer.parseInt(value);
			if ((nieuw >= min) && (nieuw <= max)) {
				c.getClass().getField(fieldname).set(c, nieuw);
			}
		} catch (Exception e) {
			logger.log(Level.WARNING, "Fout bij UpdateInt");
		}
	}


	private void storeValues() {
		updateIntConfig(config, "minGroepen", tfMinGroepen.getText(), 0, 25);
		updateIntConfig(config, "maxGroepen", tfMaxGroepen.getText(), 0, 25);
		updateIntConfig(config, "minSpelers", tfMinSpelers.getText(), 0, 25);
		controller.saveState(false, "");
	}
}
