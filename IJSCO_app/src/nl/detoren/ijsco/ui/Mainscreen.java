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
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
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
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.commons.io.FilenameUtils;

import com.google.gson.Gson;

import nl.detoren.ijsco.Configuratie;
import nl.detoren.ijsco.data.GroepsUitslag;
import nl.detoren.ijsco.data.GroepsUitslagen;
import nl.detoren.ijsco.data.Speler;
import nl.detoren.ijsco.data.Spelers;
import nl.detoren.ijsco.data.Status;
import nl.detoren.ijsco.data.WedstrijdUitslag;
import nl.detoren.ijsco.io.DeelnemersLader;
import nl.detoren.ijsco.io.ExcelExport;
import nl.detoren.ijsco.io.ExcelImport;
import nl.detoren.ijsco.io.OSBOLoader;
import nl.detoren.ijsco.io.OutputUitslagen;
import nl.detoren.ijsco.io.StatusIO;
import nl.detoren.ijsco.ui.control.IJSCOController;
import nl.detoren.ijsco.ui.control.IJSCOIndeler;
import nl.detoren.ijsco.ui.control.Suggesties;
import nl.detoren.ijsco.ui.control.Uitslagverwerker;
import nl.detoren.ijsco.ui.model.DeelnemersModel;
import nl.detoren.ijsco.ui.model.SchemaModel;
import nl.detoren.ijsco.ui.util.SendAttachmentInEmail;
import nl.detoren.ijsco.ui.util.Utils;
import nl.detoren.ijsco.view.ConfigurationDialog;
import nl.detoren.ijsco.view.ToernooiDialog;

@SuppressWarnings("serial")
public class Mainscreen extends JFrame {

	private SchemaModel schemaModel;
	private DeelnemersModel deelnemersModel;
	private JTable schemaTabel;
	private JPanel hoofdPanel;
	private JLabel tfAanwezig;
	private JLabel lbAanwezig;
	private IJSCOController controller;
	private Suggesties suggesties;

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
/*		status = new StatusIO().read("status.json");
		if (status == null) {
			status = new Status();
		}
*/
		if (status.deelnemers==null) {
			status.deelnemers = new Spelers();
		}
		if (status.config==null) {
			status.config = new Configuratie();
		}
		//leesOSBOlijst();
		addMenubar();
		// Frame
		setBounds(25, 25, 1300, 700);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle(IJSCOController.c().appTitle + " - versie " + IJSCOController.getAppVersion());
		getContentPane().setLayout(new GridLayout(1, 0, 0, 0));

		// Frame - Hoofdpanel
		hoofdPanel = new JPanel();
		getContentPane().add(hoofdPanel);
		//hoofdPanel.setLayout(new GridLayout(1, 4, 0, 0));
		hoofdPanel.setLayout(new GridLayout(1, 3, 0, 0));
		//hoofdPanel.setLayout(new GridBagLayout());
		

		// LINKS: Deelnemers
		hoofdPanel.add(createDeelnemersPanel(), new ExtendedWeightConstraints(0, 0, 500.0, 650.0));
		// LINKSMIDDEN: INSTELLINGEN EN CONTROL
		//hoofdPanel.add(createInstellingenPanel(), new ExtendedWeightConstraints(1, 0, 300.0, 650.0));

		// RECHSTMIDDEN: SCENARIOS
		hoofdPanel.add(createPanelScenariosHolder(), new ExtendedWeightConstraints(1, 0, 250.0, 650.0));

		// RECHTS: GROEPEN
		hoofdPanel.add(createPanelGroepen(), new ExtendedWeightConstraints(2, 0, 450.0, 650.0));
		
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				new StatusIO().write(status);
			}
		});
	}

	private void addMenubar() {
		// Menu bar met 1 niveau
		Mainscreen ms = this;
		JMenuBar menubar = new JMenuBar();
		JMenu filemenu = new JMenu("Bestand");
		// File menu
		JMenuItem item;
/*		item = new JMenuItem("Openen...");
		item.setAccelerator(KeyStroke.getKeyStroke('O', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));

		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// Create a file chooser
				final JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
				// In response to a button click:
				int returnVal = fc.showOpenDialog(ms);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					logger.log(Level.INFO, "Opening: " + file.getAbsolutePath() + ".");
					//controller.leesBestand(file.getAbsolutePath());
					ms.repaint();
				}
			}
		});
		filemenu.add(item);
*/
/*		item = new JMenuItem("Opslaan");
		item.setAccelerator(KeyStroke.getKeyStroke('S', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//controller.saveState(true, "save");
			}
		});
		filemenu.add(item);
*/
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
		
		/**
		 *  Toernooi menu 
    	 */
		
		JMenu toernooimenu = new JMenu("Toernooi");
		item = new JMenuItem("Toernooiinformatie");
		item.setAccelerator(KeyStroke.getKeyStroke('T', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//actieNieuweSpeler(null, null);
				bewerkToernooi();
				hoofdPanel.repaint();
			}
		});
		toernooimenu.add(item);
		menubar.add(toernooimenu);		
		
		/**
		 *  Spelersdatabase menu 
    	 */
		
		JMenu spelermenu = new JMenu("Spelersdatabase");

		item = new JMenuItem("OSBO JSON lijst ophalen (Online)");
		item.setAccelerator(KeyStroke.getKeyStroke('J', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//actieNieuweSpeler(null, null);
				leeslijstOnline("www.osbo.nl", "/jeugd/currentratings.json");
				suggesties.setDictionary(setSuggesties());
				hoofdPanel.repaint();
				
			}
		});
		spelermenu.add(item);

		item = new JMenuItem("OSBO htmllijst ophalen !verouderd! (Online)");
		item.setAccelerator(KeyStroke.getKeyStroke('O', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//actieNieuweSpeler(null, null);
				leeslijstOnline("www.osbo.nl", "/jeugd/jrating.htm");
				suggesties.setDictionary(setSuggesties());
				hoofdPanel.repaint();
			}
		});
		spelermenu.add(item);

		item = new JMenuItem("OSBO/IJSCO compatible lijst inlezen (Bestand)");
		item.setAccelerator(KeyStroke.getKeyStroke('L', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Create a file chooser
				final JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
				// In response to a button click:
				int returnVal = fc.showOpenDialog(ms);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					logger.log(Level.INFO, "Opening: " + file.getAbsolutePath() + ".");
					leesOSBOlijstBestand(file.getAbsolutePath());
				}
				suggesties.setDictionary(setSuggesties());
				hoofdPanel.repaint();
			}
		});
		spelermenu.add(item);

		item = new JMenuItem("Spelerslijst wissen");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				status.OSBOSpelers = new HashMap<>();
				suggesties.setDictionary(setSuggesties());
				hoofdPanel.repaint();
			}
		});
		spelermenu.add(item);
		
/*		item = new JMenuItem("Groslijst CSV inlezen (Bestand) N/A");
		item.setAccelerator(KeyStroke.getKeyStroke('C', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//actieNieuweSpeler(null, null);
				// Create a file chooser
				final JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
				// In response to a button click:
				int returnVal = fc.showOpenDialog(ms);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					logger.log(Level.INFO, "Opening: " + file.getAbsolutePath() + ".");
					leesCSV(file.getAbsolutePath());
				}
				hoofdPanel.repaint();
			}
		});
		spelermenu.add(item);
*/
		menubar.add(spelermenu);

JMenu deelnemersmenu = new JMenu("Deelnemers");

item = new JMenuItem("Wis Deelnemerslijst");
item.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
		// Create a file chooser
		wisDeelnemers();
		hoofdPanel.repaint();
	}
});
deelnemersmenu.add(item);

item = new JMenuItem("Importeren Deelnemerslijst");
item.setAccelerator(KeyStroke.getKeyStroke('I', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
item.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
		// Create a file chooser
		final JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
		// In response to a button click:
		int returnVal = fc.showOpenDialog(ms);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			logger.log(Level.INFO, "Opening: " + file.getAbsolutePath() + ".");
			leesDeelnemers(file.getAbsolutePath());
		}
		hoofdPanel.repaint();
	}
});
deelnemersmenu.add(item);

		item = new JMenuItem("Export Deelnemerslijst (JSON)");
item.setAccelerator(KeyStroke.getKeyStroke('E', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
item.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
		// Create a file chooser
		final JFileChooser fc = new JFileChooser();
	    FileNameExtensionFilter filter = new FileNameExtensionFilter(
	            "JSON", "json");
	    fc.setFileFilter(filter);
		fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
		// In response to a button click:
		int returnVal = fc.showSaveDialog(ms);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			logger.log(Level.INFO, "Opening: " + file.getAbsolutePath() + ".");
			schrijfDeelnemers(file.getAbsolutePath());
		}
		hoofdPanel.repaint();
	}
});
deelnemersmenu.add(item);

	menubar.add(deelnemersmenu);

	
	JMenu uitslagenmenu = new JMenu("Uitslagen");
	Component hs = this;
	item = new JMenuItem("Importeer uitslagenbestand");
	item.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			// Create a file chooser
			final JFileChooser fc = new JFileChooser();
			fc.setCurrentDirectory(new File(System.getProperty("user.dir")));

			// In response to a button click:
			int returnVal = fc.showOpenDialog(hs);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				logger.log(Level.INFO, "Opening: " + file.getAbsolutePath() + ".");
				status.groepenuitslagen = (GroepsUitslagen) new ExcelImport().importeerUitslagen(file);
				OutputUitslagen ou = new OutputUitslagen();
				ou.exportuitslagen(status.groepenuitslagen);
				//IJSCOController.t().wisUitslagen();
				IJSCOController.t().wisPoules();
				ou.exportJSON(status.groepenuitslagen);
				GroepsUitslagen verwerkteUitslag = new Uitslagverwerker().verwerkUitslag(status.groepenuitslagen);
				logger.log(Level.INFO, verwerkteUitslag.ToString());
				new OutputUitslagen().exporteindresultaten(verwerkteUitslag);
				JOptionPane.showMessageDialog(null, "Uitslagen geimporteerd en bestanden aangemaakt.");
			}	
			hoofdPanel.repaint();
		}
	});

	uitslagenmenu.add(item);
	menubar.add(uitslagenmenu);	
	

	JMenu osbomenu = new JMenu("OSBO");

	item = new JMenuItem("Verstuur uitslagen handmatig.");
	item.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			// Create a file chooser
			SendAttachmentInEmail SAIM = new SendAttachmentInEmail();
			SAIM.setSubject("IJSCO Uitslag bestanden van Toernooi " + IJSCOController.t().getBeschrijving() + ".");
			SAIM.setBodyHeader("Beste IJSCO uitslagverwerker,");
			SAIM.setBodyText("Hierbij de uitslagen van het toernooi " + IJSCOController.t().getBeschrijving() + " van " + IJSCOController.t().getDatum() + " te " + IJSCOController.t().getPlaats() + ".\r\n\r\nAangemaakt met " + IJSCOController.c().appTitle + " " + IJSCOController.getAppVersion());
			SAIM.setBodyFooter("Met vriendelijke groet,\r\n\r\nOrganisatie van " + IJSCOController.t().getBeschrijving());
			SAIM.addAttachement("Uitslagen.json");
			SAIM.addAttachement("Uitslagen.txt");
			SAIM.addAttachement("Einduitslagen.txt");
			SAIM.addAttachement("Indeling resultaat.xlsm");
			SAIM.addAttachement("status.json");
			SAIM.send();
			hoofdPanel.repaint();
		}
	});

	osbomenu.add(item);
	menubar.add(osbomenu);	
	
	JMenu helpmenu = new JMenu("Help");

	item = new JMenuItem("Verstuur logging");
	item.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			// Create a file chooser
			SendAttachmentInEmail SAIM = new SendAttachmentInEmail();
			SAIM.setSubject("Logbestand van Toernooi " + IJSCOController.t().getBeschrijving() + ".");
			SAIM.setBodyHeader("Beste IJSCO uitslagverwerker,");
			SAIM.setBodyText("Hierbij het logbestand van het toernooi " + IJSCOController.t().getBeschrijving() + " van " + IJSCOController.t().getDatum() + " te " + IJSCOController.t().getPlaats() + ".\r\n\r\nAangemaakt met " + IJSCOController.c().appTitle + " " + IJSCOController.getAppVersion());
			SAIM.setBodyFooter("Met vriendelijke groet,\r\n\r\nOrganisatie van " + IJSCOController.t().getBeschrijving());
			SAIM.addAttachement("IJSCO_UI.log");
			SAIM.send();
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
	

	
/*		JMenu indelingMenu = new JMenu("Indeling");
		//item = new JMenuItem("Automatisch aan/uit");
		item = new JMenuItem("N/A");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				//actieAutomatisch();
			}
		});

		indelingMenu.add(item);
		//item = new JMenuItem("Maak wedstrijdgroep");
		item = new JMenuItem("N/A");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//actieMaakWedstrijdgroep();
			}
		});

		indelingMenu.add(item);
		//item = new JMenuItem("Maak speelschema");
		item = new JMenuItem("N/A");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evetn) {
				//actieMaakSpeelschema();
			}
		});
		indelingMenu.add(item);
		//item = new JMenuItem("Bewerk speelschema");
		item = new JMenuItem("N/A");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				//updateAutomatisch(false);
				// ResultaatDialoog
				//actieBewerkSchema();
			}
		});

		indelingMenu.add(item);
		indelingMenu.addSeparator();
		//item = new JMenuItem("Export");
		item = new JMenuItem("N/A");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				//actieExport();
			}
		});
		indelingMenu.add(item);
		indelingMenu.addSeparator();
		//item = new JMenuItem("Vul uitslagen in");
		item = new JMenuItem("N/A");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//actieVoerUitslagenIn();
			}
		});
		indelingMenu.add(item);
		//item = new JMenuItem("Externe spelers");
		item = new JMenuItem("N/A");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//actieExterneSpelers();
			}
		});
		indelingMenu.add(item);
		//item = new JMenuItem("Maak nieuwe stand");
		item = new JMenuItem("N/A");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//actieUpdateStand();
			}
		});
		indelingMenu.add(item);
		indelingMenu.addSeparator();
		//item = new JMenuItem("Volgende ronde");
		item = new JMenuItem("N/A");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//actieVolgendeRonde();
			}
		});
		indelingMenu.add(item);
		menubar.add(indelingMenu);
*/		
/*		JMenu overigmenu = new JMenu("Overig");

		//item = new JMenuItem("Reset punten");
		item = new JMenuItem("N/A");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//controller.resetPunten();
				hoofdPanel.repaint();
			}
		});

		overigmenu.add(item);
		menubar.add(overigmenu);
*/
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
	
	
	
	public void leeslijstOnline(String fqdn, String page) {
		Spelers tmp = null;
			InetAddress ip = null;
			try {
				ip = InetAddress.getByName(fqdn);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				logger.log(Level.WARNING, "Unknown host: " + fqdn );
			    ShowWarning("Unknown host: " + fqdn);
			    return;
			}
			try {
				//if ((ip != null) && ip.isReachable(5000)) {	
				if (ip != null) {
				//if (1 == Math.abs(0)) {
					String ext = FilenameUtils.getExtension(page);
					switch (ext) {
					case "html":
					case "htm":
						tmp = (new OSBOLoader()).laadWebsite("https://" + fqdn + page);
						break;
					case "json":
						tmp = (new OSBOLoader()).laadJSON("https://" + fqdn + page);
						logger.log(Level.INFO, "OSBO list loaded from JSON");
						break;
					}
					logger.log(Level.INFO, "Spelers van website http://" + fqdn + page + " opgehaald: " + tmp.size() + " spelers in lijst" );
				} else {
					logger.log(Level.WARNING, "Host " + fqdn +  " not reachable or problem with parsing");
				    ShowWarning("Host " + fqdn + "not reachable or problem with parsing");
				    return;
				}
			//} catch (IOException e) {
			} catch (Exception e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			    ShowWarning("Fout opgetreden bij online inlezen lijst van " + fqdn);
			    return;
			}
		status.OSBOSpelers = new HashMap<>();
		for (Speler d : tmp) {
			try {
			status.OSBOSpelers.put(d.getKnsbnummer(), d);
			}
			catch (Exception ex) {
				logger.log(Level.WARNING, "Problem in getting KNSBnummer");
			}
		}
		indeler.controleerSpelers(status.deelnemers, status.OSBOSpelers);
		JOptionPane.showMessageDialog(null, tmp.size() + " spelers ingelezen uit OSBO jeugdratinglijst");
	}

	public void ShowWarning(String warning) {
/*		JPanel p = new JPanel(new BorderLayout());
		DefaultTableModel tableModel = new DefaultTableModel();
		tableModel.addColumn("Selection", new Object[] { "A", "B", "C" });

		JTable table = new JTable(tableModel);
		ListSelectionModel selectionModel = table.getSelectionModel();
		p.add(table, BorderLayout.CENTER);
*/
		Object[] options = { "OK" };
		int option = JOptionPane.showOptionDialog(null, warning, "Warning",
		JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
		null, options, options[0]);

		//int option = JOptionPane.showConfirmDialog(null, warning, "Warning", , JOptionPane.ERROR_MESSAGE);
	    if (option == 0) {
	    	return;
	    }

	}
	public void leesOSBOlijstBestand(String filepath) {
		Spelers tmp = null;
		try {
			tmp = (new OSBOLoader()).laadBestand(filepath);
			if (tmp != null) {
				logger.log(Level.INFO, "OSBO ingelezen : " + tmp.size() + " spelers in lijst" );
			} else {
				logger.log(Level.WARNING, "OSBO niet ingelezen : Bestand niet gevonden!" );
				JOptionPane.showMessageDialog(null, "Fout met inlezen");
				return;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.log(Level.SEVERE, "Probleem met inlezen OSBO bestand" );
			e.printStackTrace();
		}
		status.OSBOSpelers = new HashMap<>();
		for (Speler d : tmp) {
			status.OSBOSpelers.put(d.getKnsbnummer(), d);
		}
		indeler.controleerSpelers(status.deelnemers, status.OSBOSpelers);
		JOptionPane.showMessageDialog(null, tmp.size() + " spelers ingelezen uit OSBO jeugdratinglijst");
	}

	public void leesCSV(String filepath) {
		Spelers tmp = null;
			try {
				tmp = (new OSBOLoader()).laadCSV(filepath);
				if (tmp != null) {
					logger.log(Level.INFO, "CSV ingelezen : " + tmp.size() + " spelers in lijst" );
				} else {
					logger.log(Level.WARNING, "OSBO niet ingelezen : Bestand niet gevonden!" );
					JOptionPane.showMessageDialog(null, "Fout met inlezen");
					return;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.log(Level.SEVERE, "Probleem met inlezen OSBO bestand" );
				e.printStackTrace();
			}
			status.OSBOSpelers = new HashMap<>();
			for (Speler d : tmp) {
				status.OSBOSpelers.put(d.getKnsbnummer(), d);
			}
			indeler.controleerSpelers(status.deelnemers, status.OSBOSpelers);
			JOptionPane.showMessageDialog(null, tmp.size() + " spelers ingelezen uit OSBO jeugdratinglijst");
		}
	
	
	public void wisDeelnemers() {
		
		deelnemersModel.wis();
		deelnemersModel.fireTableDataChanged();
		JOptionPane.showMessageDialog(null, "Spelers uit deelnemerslijst verwijderd.");
		
	
	}
	
	public void schrijfDeelnemers(String file) {
		try {
			String bestandsnaam = "Deelnemers.json";
			logger.log(Level.INFO, "Sla deelnemers op in bestand " + bestandsnaam);
			Gson gson = new Gson();
			String jsonString = gson.toJson(status.deelnemers);
			// write converted json data to a file
			FileWriter writer = new FileWriter(bestandsnaam);
			writer.write(jsonString);
			writer.close();
		}
		catch (Exception e)
		{
			logger.log(Level.SEVERE, "An " + e.getMessage() + " occured ");
		}
	}
	
	public void leesDeelnemers(String file) {
		Spelers tmp = new DeelnemersLader().importeerSpelers(file);
		if (status.OSBOSpelers != null) {
			indeler.controleerSpelers(tmp, status.OSBOSpelers);
			logger.log(Level.INFO, "Deelnemers ingelezen : " + tmp.size() + " spelers in lijst" );
			try {
				deelnemersModel.wis();
			} 
			catch (Exception ex) {
				logger.log(Level.INFO, "DeelnemersModel kan niet gewist worden : " + ex.getMessage() + "");				
			}
			for (Speler s : tmp) {
				deelnemersModel.add(s);
				status.deelnemers.add(s);
			}
			deelnemersModel.fireTableDataChanged();
			JOptionPane.showMessageDialog(null, tmp.size() + " spelers ingelezen uit bestand");
		}
		else {
			JOptionPane.showMessageDialog(null, "Geen spelers ingelezen uit bestand. OSBO lijst moet eerst gelezen worden.");
		}
		logger.log(Level.INFO, "Deelnemers inlezen uit bestand afgerond." );
	}

	public JPanel createPanelGroepen() {
		JPanel panel = new JPanel();
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

	public JPanel createPanelScenariosHolder() {
		JPanel panel = new JPanel();	
		panel.setBackground(Color.RED);
		panel.setLayout(new BorderLayout(0,	 0));
	    panel.add(createPanelScenariosButtons(), BorderLayout.PAGE_START);
	    panel.add(createPanelScenarios(), BorderLayout.CENTER);
		return panel;
	}

	public JPanel createPanelScenariosButtons() {
		JPanel panel = new JPanel();
		panel.setBackground(Color.RED);
		panel.setLayout(new GridLayout(0,	 2));
		JButton bSchemas = new JButton("1. Bepaal mogelijkheden");
		bSchemas.setPreferredSize(new Dimension(40,40));
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
//		panel.add(bSchemas, new ExtendedGridConstraints(0, 0));
		panel.add(bSchemas);
		JButton bGroepen = new JButton("2. Bepaal groepen");
		bGroepen.setPreferredSize(new Dimension(40,40));
		bGroepen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (schemaTabel != null) {
					List<Integer> nobyeslist;
					int row = schemaTabel.getSelectedRow();
					nobyeslist =  IJSCOController.c().nobyes;
					bepaalGroepen(row, Utils.toMask(nobyeslist));
					new ExcelExport().exportGroepen(status.groepen);
				}
				panel.getParent().repaint();
			}
		});
		//panel.add(bGroepen, new ExtendedGridConstraints(1, 1));
		panel.add(bGroepen);
		return panel;
	}
	
	public JPanel createPanelScenarios() {
		JPanel panel = new JPanel();
		panel.setBackground(Color.RED);
		panel.setLayout(new GridLayout(1,	 0));
		schemaModel = new SchemaModel(panel, status.schemas);
		schemaTabel = new JTable(schemaModel) {
			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component c = super.prepareRenderer(renderer, row, column);
				// Tooltip
				if (c instanceof JComponent) {
					SchemaModel model = (SchemaModel) getModel();
					((JComponent) c).setToolTipText(model.getToolTip(row, column).toString());
				}
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
		//panel.add(scrollpane, new ExtendedGridConstraints(0, 2));
		panel.add(scrollpane);
		return panel;
	}

//	public JPanel createInstellingenPanel() {
//		JPanel panel = new JPanel();
//		panel.setBackground(Color.LIGHT_GRAY);
//		GridBagLayout gbl_panel = new GridBagLayout();
//		gbl_panel.columnWidths = new int[] { 128, 32, 0 };
//		gbl_panel.rowHeights = new int[] { 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 0 };
//		gbl_panel.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
//		gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
//				Double.MIN_VALUE };
//		panel.setLayout(gbl_panel);
//		int curRow = 0;
//
//		// Header Row
//		panel.add(new JLabel(" "), new ExtendedGridConstraints(0, curRow));
//		panel.add(new JLabel(" "), new ExtendedGridConstraints(1, curRow++));
//
//
//		// Buttons
//		JButton bOSBO = new JButton("Alle OSBO deelnemers");
//		bOSBO.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent event) {
//				voegtoeOSBOlijst();
//			}
//
//		});
//		panel.add(bOSBO, new ExtendedGridConstraints(0, curRow));
//		Component hs = this;
//		JButton bSpelers = new JButton("Lees deelnemers");
//		bSpelers.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent event) {
//				// Create a file chooser
//				final JFileChooser fc = new JFileChooser();
//				fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
//				// In response to a button click:
//				int returnVal = fc.showOpenDialog(hs);
//				if (returnVal == JFileChooser.APPROVE_OPTION) {
//					File file = fc.getSelectedFile();
//					logger.log(Level.INFO, "Opening: " + file.getAbsolutePath() + ".");
//					leesDeelnemers(file.getAbsolutePath());
//				}
//
//			}
//
//		});
//		panel.add(bSpelers, new ExtendedGridConstraints(1, curRow++));
//
//		JButton bWis = new JButton("Wis lijst");
//		bWis.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent event) {
//				wisDeelnemerslijst();
//			}
//
//		});
//		panel.add(bWis, new ExtendedGridConstraints(0, curRow++));
//
		// Aantal spelers
/*		panel.add(new JLabel("Aantal spelers:"), new ExtendedGridConstraints(0, curRow));
		if (status.deelnemers != null) {
			tfAanwezig = new JTextField(Integer.toString(status.deelnemers.aantalAanwezig()), 10);
		} else {
			tfAanwezig = new JTextField(Integer.toString(0), 10);
		}
		tfAanwezig.setEditable(false);
		panel.add(tfAanwezig, new ExtendedGridConstraints(1, curRow++));
*///
//
//		// Aantal groepen
//		JLabel label_4 = new JLabel("Aantal groepen");
//		panel.add(label_4, new ExtendedGridConstraints(0, curRow++, 2, 1));
//		panel.add(new JLabel("Minimum:"), new ExtendedGridConstraints(0, curRow));
//		JTextField tfMinGroepen = new JTextField(Integer.toString(status.minGroepen), 10);
//		tfMinGroepen.addFocusListener(new FocusAdapter() {
//			public void focusLost(FocusEvent e) {
//				status.minGroepen = Utils.newIntegerValue(tfMinGroepen, status.minGroepen);
//			}
//		});
//		panel.add(tfMinGroepen, new ExtendedGridConstraints(1, curRow++));
//		panel.add(new JLabel("Maximum:"), new ExtendedGridConstraints(0, curRow));
//		JTextField tfMaxGroepen = new JTextField(Integer.toString(status.maxGroepen), 10);
//		tfMaxGroepen.addFocusListener(new FocusAdapter() {
//			public void focusLost(FocusEvent e) {
//				status.maxGroepen = Utils.newIntegerValue(tfMaxGroepen, status.maxGroepen);
//			}
//		});
//		panel.add(tfMaxGroepen, new ExtendedGridConstraints(1, curRow++));
//
//		// Aantal spelers
//		panel.add(new JLabel("Aantal spelers per groep"), new ExtendedGridConstraints(0, curRow++, 2, 1));
//		panel.add(new JLabel("Minimum:"), new ExtendedGridConstraints(0, curRow));
//		JTextField tfMinSpelers = new JTextField(Integer.toString(status.minSpelers), 10);
//		tfMinSpelers.addFocusListener(new FocusAdapter() {
//			public void focusLost(FocusEvent e) {
//				status.minSpelers = Utils.newIntegerValue(tfMinSpelers, status.minSpelers);
//			}
//		});
//		panel.add(tfMinSpelers, new ExtendedGridConstraints(1, curRow++));
//		panel.add(new JLabel("Maximum:"), new ExtendedGridConstraints(0, curRow));
//		JTextField tfMaxSpelers = new JTextField(Integer.toString(status.maxSpelers), 10);
//		tfMaxSpelers.addFocusListener(new FocusAdapter() {
//			public void focusLost(FocusEvent e) {
//				status.maxSpelers = Utils.newIntegerValue(tfMaxSpelers, status.maxSpelers);
//			}
//		});
//		panel.add(tfMaxSpelers, new ExtendedGridConstraints(1, curRow++));
//
//		// Delta aantal spelers
//		panel.add(new JLabel("Delta spelers voor uiterste groepen"), new ExtendedGridConstraints(0, curRow++, 2, 1));
//		panel.add(new JLabel("Minimum:"), new ExtendedGridConstraints(0, curRow));
//		JTextField tfMinDelta = new JTextField(Integer.toString(status.minDeltaSpelers), 10);
//		tfMinDelta.addFocusListener(new FocusAdapter() {
//			public void focusLost(FocusEvent e) {
//				status.minDeltaSpelers = Utils.newIntegerValue(tfMinDelta, status.minDeltaSpelers);
//			}
//		});
//		panel.add(tfMinDelta, new ExtendedGridConstraints(1, curRow++));
//		panel.add(new JLabel("Maximum:"), new ExtendedGridConstraints(0, curRow));
//		JTextField tfMaxDelta = new JTextField(Integer.toString(status.maxDeltaSpelers), 10);
//		tfMaxDelta.addFocusListener(new FocusAdapter() {
//			public void focusLost(FocusEvent e) {
//				status.maxDeltaSpelers = Utils.newIntegerValue(tfMaxDelta, status.maxDeltaSpelers);
//			}
//		});
//		panel.add(tfMaxDelta, new ExtendedGridConstraints(1, curRow++));
//
//		// Delta aantal groepen met afwijkend aantal spelers
//		panel.add(new JLabel("Aantal afwijkende groepen"), new ExtendedGridConstraints(0, curRow++, 2, 1));
//		panel.add(new JLabel("Minimum:"), new ExtendedGridConstraints(0, curRow));
//		JTextField tfMinDeltaGroepen = new JTextField(Integer.toString(status.minAfwijkendeGroepen), 10);
//		tfMinDeltaGroepen.addFocusListener(new FocusAdapter() {
//			public void focusLost(FocusEvent e) {
//				status.minAfwijkendeGroepen = Utils.newIntegerValue(tfMinDeltaGroepen, status.minAfwijkendeGroepen);
//			}
//		});
//		panel.add(tfMinDeltaGroepen, new ExtendedGridConstraints(1, curRow++));
//		panel.add(new JLabel("Maximum:"), new ExtendedGridConstraints(0, curRow));
//		JTextField tfMaxDeltaGroepen = new JTextField(Integer.toString(status.maxAfwijkendeGroepen), 10);
//		tfMaxDeltaGroepen.addFocusListener(new FocusAdapter() {
//			public void focusLost(FocusEvent e) {
//				status.maxAfwijkendeGroepen = Utils.newIntegerValue(tfMaxDeltaGroepen, status.maxAfwijkendeGroepen);
//			}
//		});
//		panel.add(tfMaxDeltaGroepen, new ExtendedGridConstraints(1, curRow++));
//
//		// Byes
//		panel.add(new JLabel("Aantal toegestane byes"), new ExtendedGridConstraints(0, curRow++, 2, 1));
//		panel.add(new JLabel("Minimum:"), new ExtendedGridConstraints(0, curRow));
//		JTextField tfMinByes = new JTextField(Integer.toString(status.minToegestaneByes), 10);
//		tfMinByes.addFocusListener(new FocusAdapter() {
//			public void focusLost(FocusEvent e) {
//				status.minToegestaneByes = Utils.newIntegerValue(tfMinByes, status.minToegestaneByes);
//			}
//		});
//		panel.add(tfMinByes, new ExtendedGridConstraints(1, curRow++));
//
//		panel.add(new JLabel("Maximum:"), new ExtendedGridConstraints(0, curRow));
//		JTextField tfMaxByes = new JTextField(Integer.toString(status.maxToegestaneByes), 10);
//		tfMaxByes.addFocusListener(new FocusAdapter() {
//			public void focusLost(FocusEvent e) {
//				status.maxToegestaneByes = Utils.newIntegerValue(tfMaxByes, status.maxToegestaneByes);
//			}
//		});
//		panel.add(tfMaxByes, new ExtendedGridConstraints(1, curRow++));
//
//		
////		for (int i = 0; i < 4; i++) {
////			panel.add(new JLabel("A"), new ExtendedConstraints(0, curRow));
////			panel.add(new JLabel("A"), new ExtendedConstraints(1, curRow++));
////		}
//
//		JButton bSchemas = new JButton("1. Bepaal mogelijkheden");
//		bSchemas.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent event) {
//				status.groepen = null;
//				bepaalSchemas();
//				schemaTabel.getSelectionModel().clearSelection();
//				groepenText.setText("");
//				panel.getParent().repaint();
//			}
//
//		});
//		panel.add(bSchemas, new ExtendedGridConstraints(0, curRow));
//		JButton bGroepen = new JButton("2. Bepaal groepen");
//		bGroepen.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent event) {
//				if (schemaTabel != null) {
//					int row = schemaTabel.getSelectedRow();
//					bepaalGroepen(row, IJSCOController.c().nobyesmask);
//					new ExcelExport().exportGroepen(status.groepen);
//				}
//				panel.getParent().repaint();
//			}
//		});
//		panel.add(bGroepen, new ExtendedGridConstraints(1, curRow++));
//		panel.add(new JLabel(" "), new ExtendedGridConstraints(0, 17, 2, 1));
//		Utils.fixedComponentSize(panel, 300, 400);
//		return panel;
//	}

	protected void wisDeelnemerslijst() {
		deelnemersModel.wis();


	}

	public JPanel createDeelnemersPanel() {
		JPanel panel = new JPanel(false);
		panel.setBackground(Color.BLACK);
		//panel.setLayout(new GridLayout(1, 0));
		panel.setLayout(new BorderLayout());
			JPanel innerPanel = new JPanel();
			JLabel lbAanwezig = new JLabel("Deelnemers: ");
			innerPanel.add(lbAanwezig, BorderLayout.NORTH);
			tfAanwezig = new JLabel(Integer.toString(status.deelnemers.aantalAanwezig()), 10);
			innerPanel.add(tfAanwezig, BorderLayout.NORTH);
			//innerPanel.setLayout(new GridLayout(1, 0));
			innerPanel.add(new JLabel("Naam:"), BorderLayout.NORTH);
			JTextField deelnemer = new JTextField(15);
			ArrayList<String> words = setSuggesties();
			//@SuppressWarnings("unused")
			this.suggesties = new Suggesties(deelnemer, this, words, 2);

			innerPanel.add(deelnemer, BorderLayout.NORTH);
			deelnemer.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent event) {
					actieVoegSpelerToe(deelnemer.getText().trim());
					deelnemer.setText("");
				}
			});

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
		// panel_deelnemers.add(new JLabel("Deelnemers IJSCO toernooi"));
		deelnemersModel = new DeelnemersModel(panel, status.deelnemers);
		JTable deelnemersTabel = new JTable(deelnemersModel) {
			private static final long serialVersionUID = -8293073016982337108L;

			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component c = super.prepareRenderer(renderer, row, column);
				DeelnemersModel model = (DeelnemersModel) getModel();
				// Tooltip
				if (c instanceof JComponent) {
					((JComponent) c).setToolTipText(model.getToolTip(convertRowIndexToModel(row), column).toString());
				}

				// Alternate row color
				if (!isRowSelected(row)) {
					c.setBackground(row % 2 == 0 ? Color.WHITE : Color.LIGHT_GRAY);
				}

				// Highlight overruled entries
				if (status.deelnemers.get(convertRowIndexToModel(row)).isOverruleNaam() ||
						status.deelnemers.get(convertRowIndexToModel(row)).isOverruleNaam()) {
					c.setForeground(Color.BLUE);
				} else {
					c.setForeground(Color.BLACK);
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
				if (status.deelnemers != null && tfAanwezig != null) {
					tfAanwezig.setText(Integer.toString(status.deelnemers.aantalAanwezig()));
				}
				panel.repaint();
			}

		});

		deelnemersTabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				logger.log(Level.INFO, "MouseEvent on table fired, type : " + e.toString());
				logger.log(Level.INFO, "Popup trigger? : " + e.isPopupTrigger());
				if (e.isPopupTrigger()) {
					int row = deelnemersTabel.rowAtPoint(e.getPoint());
					JPopupMenu popup = new JPopupMenu();
					JMenuItem menuItem = new JMenuItem("Bewerk speler");
					menuItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							logger.log(Level.INFO, "Bewerk Speler  : " + deelnemersTabel.convertRowIndexToModel(row));
							Speler s = status.deelnemers.get(deelnemersTabel.convertRowIndexToModel(row));
							BewerkSpelerDialoog rd = new BewerkSpelerDialoog(new JFrame(), "Bewerk Speler", s, deelnemersModel);
							rd.addWindowListener(new WindowAdapter() {
								@Override
								public void windowClosed(WindowEvent e) {
									System.out.println("closing...");
								}

							});
							rd.setVisible(true);
						}

					});
					popup.add(menuItem);

					menuItem = new JMenuItem("Verwijder Speler");
					popup.add(menuItem);
					menuItem.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							logger.log(Level.INFO, "Verwijder Speler  : " + deelnemersTabel.convertRowIndexToModel(row));
							Speler s = status.deelnemers.get(deelnemersTabel.convertRowIndexToModel(row));
							status.deelnemers.remove(s);
							deelnemersModel.fireTableDataChanged();
						}
					});
					popup.show(e.getComponent(), e.getX(), e.getY());

				}
			}
		});

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(deelnemersTabel);
		innerPanel.add(scrollPane, BorderLayout.CENTER);

		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(deelnemersModel);
	    deelnemersTabel.setRowSorter(sorter);

		innerPanel.add(new JLabel("Filter op : "));
		JTextField tfFilter = new JTextField(10);
		tfFilter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				String text = tfFilter.getText();
				logger.log(Level.INFO, "Filter tabel op : " + text);
				if (text.length() == 0) {
			          sorter.setRowFilter(null);
			        } else {
			          sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
			        }
			}
		});
		innerPanel.add(tfFilter);
		JButton btPasToe = new JButton("Apply");
		btPasToe.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				String text = tfFilter.getText();
				logger.log(Level.INFO, "Filter tabel op : " + text);
				if (text.length() == 0) {
			          sorter.setRowFilter(null);
			        } else {
			          sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
			        }
			}
		});
		innerPanel.add(btPasToe);
		JButton btWis = new JButton("Wis");
		btWis.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				tfFilter.setText("");
				logger.log(Level.INFO, "Wis filter");
				sorter.setRowFilter(null);
			}
		});
		innerPanel.add(btWis);

		Utils.fixedColumSize(deelnemersTabel.getColumnModel().getColumn(0), 30);
		Utils.fixedColumSize(deelnemersTabel.getColumnModel().getColumn(1), 55);
		Utils.fixedColumSize(deelnemersTabel.getColumnModel().getColumn(2), 115);
		Utils.fixedColumSize(deelnemersTabel.getColumnModel().getColumn(3), 75);
		Utils.fixedColumSize(deelnemersTabel.getColumnModel().getColumn(4), 35);
		Utils.fixedColumSize(deelnemersTabel.getColumnModel().getColumn(5), 35);
		Utils.fixedColumSize(deelnemersTabel.getColumnModel().getColumn(6), 35);
		Utils.fixedColumSize(deelnemersTabel.getColumnModel().getColumn(7), 20);
		Utils.fixedComponentSize(scrollPane, 400, 580);
		return panel;
	}

	protected ArrayList<String> setSuggesties() {
		ArrayList<String> words = new ArrayList<>();
		if (status.OSBOSpelers != null) {
			for (Speler s : status.OSBOSpelers.values()) {
				words.add(s.getNaam().trim());
				words.add(Integer.toString(s.getKnsbnummer()));
			}
		}
		return words;
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
			nieuw = (nieuw != null) ? nieuw : new Speler(knsbnr, "", "", -1, -1);
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


	protected void voegtoeOSBOlijst() {
		//TODO Alle spelers uit de lijst toevoegen als ze er niet al in staan!
		Iterator<Entry<Integer, Speler>> it = status.OSBOSpelers.entrySet().iterator();
		while (it.hasNext()){
			Map.Entry pair = (Map.Entry)it.next();
			Speler s = (Speler) pair.getValue();
			s.setAanwezig(false);
			status.deelnemers.add(s);
		}
		deelnemersModel.fireTableDataChanged();
	}




	/**
	 * Bepaal de mogelijke schemas op basis van het aantals spelers en de
	 * gemaakte instellingen
	 */
	public void bepaalSchemas() {
		int ndeelnemers = status.deelnemers.aantalAanwezig();
		logger.log(Level.INFO, "Bepaal mogelijkheden voor n=" + ndeelnemers);
		status.schemas = indeler.mogelijkeSchemas(status);
		if (status.schemas.size()<1) {
			JOptionPane.showMessageDialog(null, "Geen schema's mogelijk!");
		}
		status.schema = null;
		schemaModel.setSchemas(status.schemas);
	}

	/**
	 * Bepaal de groepsindeling op basis van het geselecteerde schema
	 *
	 * @param row
	 *            Gesleecteerde schema
	 */
	public void bepaalGroepen(int row, int nobyesmask) {
		status.schema = schemaModel.getSchema(row);
		logger.log(Level.INFO, "Bepaal groepen voor schema " + status.schema);
		if (status.schema != null) {
			status.groepen = indeler.bepaalGroep(status.schema, status.deelnemers, nobyesmask);
			System.out.println(status.groepen.getDescription());
			groepenText.setText(status.groepen.getDescription());
			groepenText.setCaretPosition(0);
		} else {
			JOptionPane.showMessageDialog(null, "Geen schema geselecteerd om te gebruiken. \n\rBepaal eerst de mogelijkheden en selecteer er ��n.");
		}
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