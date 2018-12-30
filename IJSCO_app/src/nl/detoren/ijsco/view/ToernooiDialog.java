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
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.JButton;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.jdatepicker.JDatePicker;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import nl.detoren.ijsco.data.Toernooi;
import nl.detoren.ijsco.ui.ExtendedGridConstraints;
import nl.detoren.ijsco.ui.control.IJSCOController;
import nl.detoren.ijsco.ui.util.DateLabelFormatter;
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
public class ToernooiDialog extends JDialog {

	private IJSCOController controller;
	private Toernooi toernooi;
	private JTextField tfBeschrijving;
	private UtilDateModel model = new UtilDateModel();
	private Properties p = new Properties();
	private JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
	private JDatePickerImpl dtpDatum = new JDatePickerImpl(datePanel,  new DateLabelFormatter());
	private JTextField tfVereniging;
	private JTextField tfLocatie;
	private JTextField tfPlaats;

	private final static Logger logger = Logger.getLogger(ConfigurationDialog.class.getName());	

	public ToernooiDialog(Frame frame, String title) {
		super(frame, title);
		logger.log(Level.INFO, "Bewerk toernooiinformatie");
		controller = IJSCOController.getInstance();
		toernooi = IJSCOController.t();
		setModal(true);
		setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		getContentPane().add(createPanel());
		setSize(600, 160);
		setLocationRelativeTo(frame);
	}

	private JPanel createPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

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
		panel.add(createPanelToernooi());
		//panel.add(tabs, BorderLayout.CENTER);

		return panel;
	}

	public JPanel createPanelToernooi() {
		JPanel tabToernooi = new JPanel(false);
		tabToernooi.setLayout(new ExtendedGridLayout(5, 2));
		int curRow = 0;

		// Aantal groepen
		tabToernooi.add(new JLabel("Toernooi beschrijving"));
		tfBeschrijving = new JTextField(toernooi.getBeschrijving(), 1);
		tfBeschrijving.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				toernooi.setBeschrijving(tfBeschrijving.getText());
			}
		});
		tabToernooi.add(tfBeschrijving);
		
		tabToernooi.add(new JLabel("Datum"));
/*		dtpDatum = new JDatePicker;
		tabToernooi.add(dtpDatum);
*/
		model = new UtilDateModel();
		model.setValue(toernooi.getDatum());
		// Need this...
		p.put("text.today", "Today");
		p.put("text.month", "Month");
		p.put("text.year", "Year");
		datePanel = new JDatePanelImpl(model, p);
		// Don't know about the formatter, but there it is...
		dtpDatum = new JDatePickerImpl(datePanel,  new DateLabelFormatter());
		tabToernooi.add(dtpDatum);

		tabToernooi.add(new JLabel("Vereniging"));
		tfVereniging = new JTextField(toernooi.getVereniging(), 1);
		tfVereniging.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				toernooi.setVereniging(tfVereniging.getText());
			}
		});
		tabToernooi.add(tfVereniging);

		tabToernooi.add(new JLabel("Locatie"));
		tfLocatie = new JTextField(toernooi.getLocatie(), 1);
		tfLocatie.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				toernooi.setLocatie(tfLocatie.getText());
			}
		});
		tabToernooi.add(tfLocatie);

		tabToernooi.add(new JLabel("Plaats"));
		tfPlaats = new JTextField(toernooi.getPlaats(), 1);
		tfPlaats.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				toernooi.setPlaats(tfPlaats.getText());
			}
		});
		tabToernooi.add(tfPlaats);

		//Utils.fixedComponentSize(tabToernooi, 300, 400);
		return tabToernooi;
	}

	private void storeValues() {
		/*		updateIntConfig(config, "minGroepen", tfMinGroepen.getText(), 0, 25);
			updateIntConfig(config, "maxGroepen", tfMaxGroepen.getText(), 0, 25);
			updateIntConfig(config, "minSpelers", tfMinSpelers.getText(), 0, 25);
		 */
		toernooi.setDatum((Date) dtpDatum.getModel().getValue());
		controller.setToernooi(toernooi);
		controller.saveState(false, "");
	}
}
