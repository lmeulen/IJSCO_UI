/**
 * Copyright (C) 2016-2018 Leo van der Meulen, Lars Dam
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
package nl.detoren.ijsco.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.RowFilter;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import nl.detoren.extentions.GridLayout2;
import nl.detoren.ijsco.Configuratie;
import nl.detoren.ijsco.data.Speler;
import nl.detoren.ijsco.data.Spelers;
import nl.detoren.ijsco.data.Status;
import nl.detoren.ijsco.io.ExcelExport;
import nl.detoren.ijsco.ui.control.IJSCOController;
import nl.detoren.ijsco.ui.control.IJSCOIndeler;
import nl.detoren.ijsco.ui.control.Suggesties;
import nl.detoren.ijsco.ui.model.DeelnemersModel;
import nl.detoren.ijsco.ui.model.SchemaModel;
import nl.detoren.ijsco.ui.util.SendAttachmentInEmail;
import nl.detoren.ijsco.ui.util.Utils;
import nl.detoren.ijsco.view.ConfigurationDialog;
import nl.detoren.ijsco.view.ToernooiDialog;


public class Test extends JFrame {

	private JPanel hoofdPanel;
	private JPanel notificatiesPanel;
	private JTextArea notificatiesTextArea;
	private IJSCOController controller;
	IJSCOIndeler indeler;
	
	private Status status;
	
	private final static Logger logger = Logger.getLogger(Test.class.getName());

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Test window = new Test();
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
	public Test() {
		initialize();
		logger.setLevel(Level.INFO);
		if (status.toernooi.getBeschrijving().isEmpty()) {
			JOptionPane.showMessageDialog(null, "U moet eerst toernooi gegeven invoeren!");
			bewerkToernooi();
		}
	}

	/**	
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
    	logger.log(Level.INFO, "IJSCO-UI version " + IJSCOController.getAppVersion());
    	logger.log(Level.INFO, "Opstarten controller");
        IJSCOController.getInstance().start();
		setTitle(IJSCOController.c().appTitle + " - versie " + IJSCOController.getAppVersion());
		indeler = new IJSCOIndeler();
		status = IJSCOController.getI().getStatus();

		// Create new deelnemers when not yet existing
		if (status.deelnemers==null) {
			status.deelnemers = new Spelers();
		}
		// Create new config when not yet existing
		if (status.config==null) {
			status.config = new Configuratie();
		}
		// Update deelnemers with OSBOSpelers information
		indeler.controleerSpelers(status.deelnemers, status.OSBOSpelers);

		addMenubar();
		setBounds(25, 25, 1300, 700);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Test" + " - versie " + "versienummer");

		getContentPane().setLayout(new GridLayout2(2, 0, 0, 0));
		
		// Frame - Hoofdpanel
		hoofdPanel = new JPanel();
		getContentPane().add(hoofdPanel);
		hoofdPanel.setLayout(new GridLayout2(1, 3));

		hoofdPanel.add(createDeelnemersPanel_oud());
		hoofdPanel.add(createPanelScenarios());
		hoofdPanel.add(createPanelGroepen());
		
		// Frame - NotificatiesPanel
		notificatiesPanel = new JPanel();
		notificatiesPanel.setLayout(new GridLayout2(1, 0));
		Border compound = null;
		//Add a title to the red-outlined frame.
		compound = BorderFactory.createTitledBorder(
		                          compound, "title",
		                          TitledBorder.CENTER,
		                          TitledBorder.BELOW_BOTTOM);
		notificatiesPanel.setBorder(compound);
		notificatiesPanel.setPreferredSize(new Dimension(0,5));
		getContentPane().add(notificatiesPanel);
		notificatiesTextArea = new JTextArea();
		notificatiesPanel.add(notificatiesTextArea);
		
	}	
	
	public JPanel createPanelGroepen() {
		JPanel panel1 = new JPanel();
		panel1.setLayout(new GridLayout(3,3));
		panel1.add(new JLabel("Label 3-1"));
		panel1.add(new JLabel("Label 3-2"));
		panel1.add(new JLabel("Label 3-3"));
		panel1.add(new JLabel("Label 3-4"));
		panel1.add(new JLabel("Label 3-5"));
		panel1.add(new JLabel("Label 3-6"));
		panel1.add(new JLabel("Label 3-7"));
		panel1.add(new JLabel("Label 3-8"));
		panel1.add(new JLabel("Label 3-9"));
		return panel1;
	}

	public JPanel createPanelScenarios() {
		JPanel panel2 = new JPanel();
		panel2.setLayout(new GridLayout(5,5));
		panel2.add(new JLabel("Label 2-1"));
		panel2.add(new JLabel("Label 2-2"));
		panel2.add(new JLabel("Label 2-3"));
		panel2.add(new JLabel("Label 2-4"));
		panel2.add(new JLabel("Label 2-5"));
		panel2.add(new JLabel("Label 2-6"));
		panel2.add(new JLabel("Label 2-7"));
		panel2.add(new JLabel("Label 2-8"));
		panel2.add(new JLabel("Label 2-9"));
		panel2.add(new JLabel("Label 2-10"));
		panel2.add(new JLabel("Label 2-11"));
		panel2.add(new JLabel("Label 2-12"));
		panel2.add(new JLabel("Label 2-13"));
		panel2.add(new JLabel("Label 2-14"));
		panel2.add(new JLabel("Label 2-15"));
		panel2.add(new JLabel("Label 2-16"));
		panel2.add(new JLabel("Label 2-17"));
		panel2.add(new JLabel("Label 2-18"));
		panel2.add(new JLabel("Label 2-19"));
		panel2.add(new JLabel("Label 2-20"));
		panel2.add(new JLabel("Label 2-21"));
		panel2.add(new JLabel("Label 2-22"));
		panel2.add(new JLabel("Label 2-23"));
		panel2.add(new JLabel("Label 2-24"));
		panel2.add(new JLabel("Label 2-25"));
		return panel2;
	}

	public JPanel createDeelnemersPanel_oud() {
		JPanel panel1 = new JPanel();
		panel1.setLayout(new GridLayout(2,2));
		panel1.add(new JLabel("Label 1-1"));
		panel1.add(new JLabel("Label 1-2"));
		panel1.add(new JLabel("Label 1-3"));
		panel1.add(new JLabel("Label 1-4"));
		return panel1;
	}
	
	public JPanel createDeelnemersPanel() {
		JPanel panel1 = new JPanel();
		//panel1.setBackground(Color.BLACK);
		panel1.setLayout(new BorderLayout());
		JPanel innerPanel = new JPanel();
		JLabel lbAanwezig = new JLabel("Deelnemers: ");
		innerPanel.add(lbAanwezig, BorderLayout.NORTH);
		//JLabel tfAanwezig = new JLabel(Integer.toString(status.deelnemers.aantalAanwezig()), 10);
		JLabel tfAanwezig = new JLabel(Integer.toString(10), 10);
		innerPanel.add(tfAanwezig, BorderLayout.NORTH);
		//innerPanel.setLayout(new GridLayout(1, 0));
		innerPanel.add(new JLabel("Naam:"), BorderLayout.NORTH);
		JTextField deelnemer = new JTextField(15);
		//ArrayList<String> words = setSuggesties();

		return panel1;
	}
	
	private void addMenubar() {
		// Menu bar met 1 niveau
		Test ms = this;
		JMenuBar menubar = new JMenuBar();
		JMenu filemenu = new JMenu("Bestand");
		// File menu
		JMenuItem item;
		filemenu.addSeparator();
		item = new JMenuItem("Instellingen...");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actieInstellingen();
			}
		});
		item.setAccelerator(KeyStroke.getKeyStroke('I', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
		filemenu.add(item);
		filemenu.addSeparator();
		item = new JMenuItem("Afsluiten");
		item.setAccelerator(KeyStroke.getKeyStroke('Q', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.saveState(false, null);
				System.exit(EXIT_ON_CLOSE);
			}
		});
		filemenu.add(item);
		menubar.add(filemenu);

		JMenu helpmenu = new JMenu("Help");

		item = new JMenuItem("Verstuur logging");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Create a file chooser
				try {
				SendAttachmentInEmail SAIM = new SendAttachmentInEmail();
				SAIM.setSubject("Logbestand van Toernooi " + IJSCOController.t().getBeschrijving() + ".");
				SAIM.setBodyHeader("Beste IJSCO uitslagverwerker,");
				SAIM.setBodyText("Hierbij het logbestand van het toernooi " + IJSCOController.t().getBeschrijving() + " van " + IJSCOController.t().getDatum() + " te " + IJSCOController.t().getPlaats() + ".\r\n\r\nAangemaakt met " + IJSCOController.c().appTitle + " " + IJSCOController.getAppVersion());
				SAIM.setBodyFooter("Met vriendelijke groet,\r\n\r\nOrganisatie van " + IJSCOController.t().getBeschrijving());
				SAIM.addAttachement("IJSCO_UI.log");
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							SAIM.send();
							notificatiesTextArea.append("E-mail was send!");
						} catch (Exception e) {
							notificatiesTextArea.append("Problem with sending the e-mail.");
							e.printStackTrace();
						}
					}
				});
				}
				catch (Exception ex) 
				{
					logger.severe("Problem with sending the e-mail.");
					JOptionPane.showMessageDialog(new JFrame(), "Problem with sending the e-mail.");
				}
				hoofdPanel.repaint();
			}
		});

		helpmenu.add(item);
		item = new JMenuItem("About");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				AboutDialog ad = new AboutDialog(ms);
						ad.setVisible(true);
				hoofdPanel.repaint();
			}
		});

		helpmenu.add(item);
		menubar.add(helpmenu);	

		
		this.setJMenuBar(menubar);


	}

	/*
	 * Bewerk toernooi instellingen 
	 */
	public void bewerkToernooi() {
		hoofdPanel.repaint();
		ToernooiDialog dialoog = new ToernooiDialog(new JFrame(), "Toernooiinstellingen");
		dialoog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				System.out.println("closing...");
				hoofdPanel.repaint();
			}

		});
		dialoog.setVisible(true);		
	}

	
	public void actieInstellingen() {
		hoofdPanel.repaint();
		ConfigurationDialog dialoog = new ConfigurationDialog(new JFrame(), "Configuratie");
		dialoog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				System.out.println("closing...");
				hoofdPanel.repaint();
			}

		});
		dialoog.setVisible(true);
	}

}
