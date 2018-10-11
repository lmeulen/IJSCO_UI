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
 * - ...
 */
package nl.detoren.ijsco.ui;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import nl.detoren.ijsco.data.Speler;
import nl.detoren.ijsco.ui.model.DeelnemersModel;

/**
 *
 * @author Leo van der Meulen
 */
public class BewerkSpelerDialoog extends JDialog {

	private static final long serialVersionUID = -5297394315846599903L;

	private final static Logger logger = Logger.getLogger(BewerkSpelerDialoog.class.getName());
    private Speler speler;
    private DeelnemersModel model;

    public BewerkSpelerDialoog(Frame frame, String title, Speler sp, DeelnemersModel m) {
        super(frame, title);
        this.speler = sp;
        this.model= m;
    	logger.log(Level.INFO, "Bewerk speler " + sp);
        setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().add(createPanel());
        setSize(300, 9 * 24);
        setLocationRelativeTo(frame);
    }

    private JPanel createPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(9, 2));
        //KNSB nummer
        panel.add(new JLabel("KNSB nummer"));
        final JTextField tfKNSBnr = new JTextField((new Integer(speler.getKnsbnummer())).toString());
        if (speler.getKnsbnummer() > 8000000) tfKNSBnr.setEditable(false);
        panel.add(tfKNSBnr);
        //Naam handmatig
        panel.add(new JLabel("Naam"));
        final JTextField tfNaam = new JTextField(speler.getNaamHandmatig());
        panel.add(tfNaam);
        panel.add(new JLabel("Naam (KNSB)"));
        final JTextField tfNaam2 = new JTextField(speler.getNaamKNSB());
        tfNaam2.setEditable(false);
        panel.add(tfNaam2);

        // Rating
        panel.add(new JLabel("Rating"));
        final JTextField tfRating = new JTextField((new Integer(speler.getRatingHandmatig()).toString()));
        panel.add(tfRating);
        panel.add(new JLabel("Rating (IJSCO)"));
        final JTextField tfRating2 = new JTextField((new Integer(speler.getRatingIJSCO()).toString()));
        tfRating2.setEditable(false);
        panel.add(tfRating2);
        panel.add(new JLabel("Rating (KNSB)"));
        final JTextField tfRating3 = new JTextField((new Integer(speler.getRatingKNSB()).toString()));
        tfRating3.setEditable(false);
        panel.add(tfRating3);

        // Overrule booleans
        panel.add(new JLabel("Overrule naam"));
        final JCheckBox tfOverruleNaam = new JCheckBox("Handmatige naam");
        tfOverruleNaam.setSelected(speler.isOverruleNaam());
        panel.add(tfOverruleNaam);
        panel.add(new JLabel("Overrule rating"));
        final JCheckBox tfOverruleRating = new JCheckBox("Handmatige rating");
        tfOverruleRating.setSelected(speler.isOverruleRating());
        panel.add(tfOverruleRating);

        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                speler.setNaamHandmatig(tfNaam.getText());
                int rating = Integer.parseInt(tfRating.getText());
                speler.setRatingHandmatig(rating);
                int knsb = Integer.parseInt(tfKNSBnr.getText());
                speler.setKnsbnummer(knsb);
                speler.setOverruleNaam(tfOverruleNaam.isSelected());
                speler.setOverruleRating(tfOverruleRating.isSelected());
                setVisible(false);
                // TODO Opslaan
                model.add(speler);
                dispose();
                model.fireTableDataChanged();
            }
        });
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                setVisible(false);
                dispose();
            }
        }
        );
        panel.add(okButton);
        panel.add(cancelButton);
        return panel;
    }
}
