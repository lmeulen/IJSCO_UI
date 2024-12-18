/**
 * Copyright (C) 2024 Lars Dam
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

package nl.detoren.ijsco.view;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;

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
* @author Lars Dam
*/
public class RequestPrivateKeyDialog extends JDialog {

	private static final long serialVersionUID = -5297394315846599943L;

    public RequestPrivateKeyDialog(Frame frame, String title) {
        super(frame, title);
        setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().add(createPanel());
        setSize(300, 11 * 24);
        setLocationRelativeTo(frame);
    }

    private JPanel createPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 2));
        //Email address
        panel.add(new JLabel("E-mail adres"));
        final JTextField tfEmail = new JTextField();
        panel.add(tfEmail);
        //Button
        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
//				TODO Call RESTAPI
            	
//            	Handle response (possibly retry?)
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
        }
        );
        panel.add(okButton);
        panel.add(cancelButton);
        //}
		return panel;
    }

}
