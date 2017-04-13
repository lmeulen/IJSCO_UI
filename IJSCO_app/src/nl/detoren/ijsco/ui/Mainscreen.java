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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import nl.detoren.ijsco.data.Spelers;
import nl.detoren.ijsco.data.Speler;
import nl.detoren.ijsco.data.Status;
import nl.detoren.ijsco.io.DeelnemersLader;
import nl.detoren.ijsco.io.ExcelExport;
import nl.detoren.ijsco.io.OSBOLoader;
import nl.detoren.ijsco.io.StatusIO;
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
	private JTextField tfAanwezig;

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
		if ((new StatusIO().read("status.json")) == null) {
			status = new Status();
			
		}

		// Frame
		setBounds(25, 25, 1300, 700);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("IJSCO Groepenindeler");
		getContentPane().setLayout(new GridLayout(1, 0, 0, 0));

		// Frame - Hoofdpanel
		hoofdPanel = new JPanel();
		getContentPane().add(hoofdPanel);
		hoofdPanel.setLayout(new GridLayout(1, 4, 0, 0));
		//hoofdPanel.setLayout(new GridBagLayout());

		// LINKS: Deelnemers
		hoofdPanel.add(createDeelnemersPanel(), new ExtendedWeightConstraints(0, 0, 300.0, 650.0));
		// LINKSMIDDEN: INSTELLINGEN EN CONTROL
		hoofdPanel.add(createInstellingenPanel(), new ExtendedWeightConstraints(1, 0, 300.0, 650.0));

		// RECHSTMIDDEN: SCENARIOS
		hoofdPanel.add(createPanelScenarios(), new ExtendedWeightConstraints(2, 0, 300.0, 650.0));

		// RECHTS: GROEPEN
		hoofdPanel.add(createPanelGroepen(), new ExtendedWeightConstraints(3, 0, 300.0, 650.0));

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				new StatusIO().write(status);
			}
		});
	}

	public void leesOSBOlijst() {
		Spelers tmp = (new OSBOLoader()).laadBestand("OSBO Jeugd-rating-lijst.htm");
		logger.log(Level.INFO, "OSBO ingelezen : " + tmp.size() + " spelers in lijst" );
		status.OSBOSpelers = new HashMap<>();
		for (Speler d : tmp) {
			status.OSBOSpelers.put(d.getKnsbnummer(), d);
		}
		indeler.controleerSpelers(status.deelnemers, status.OSBOSpelers);
		JOptionPane.showMessageDialog(null, tmp.size() + " spelers ingelezen uit OSBO jeugdratinglijst");

	}

	public void leesDeelnemers(String file) {
		Spelers tmp = new DeelnemersLader().importeerSpelers(file);
		indeler.controleerSpelers(tmp, status.OSBOSpelers);
		logger.log(Level.INFO, "Deelnemers ingelezen : " + tmp.size() + " spelers in lijst" );
		deelnemersModel.wis();
		for (Speler s : tmp) {
			deelnemersModel.add(s);
		}
		deelnemersModel.fireTableDataChanged();
		JOptionPane.showMessageDialog(null, tmp.size() + " spelers ingelezen uit bestand");

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

	public JPanel createPanelScenarios() {
		JPanel panel = new JPanel();
		panel.setBackground(Color.RED);
		panel.setLayout(new GridLayout(1, 0));
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
		panel.add(scrollpane);
		return panel;
	}

	public JPanel createInstellingenPanel() {
		JPanel panel = new JPanel();
		panel.setBackground(Color.LIGHT_GRAY);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 128, 32, 0 };
		gbl_panel.rowHeights = new int[] { 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 0 };
		gbl_panel.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				Double.MIN_VALUE };
		panel.setLayout(gbl_panel);
		int curRow = 0;

		// Header Row
		panel.add(new JLabel(" "), new ExtendedGridConstraints(0, curRow));
		panel.add(new JLabel(" "), new ExtendedGridConstraints(1, curRow++));


		// Buttons
		JButton bOSBO = new JButton("Import OSBO");
		bOSBO.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				leesOSBOlijst();
			}

		});
		panel.add(bOSBO, new ExtendedGridConstraints(0, curRow));
		Component hs = this;
		JButton bSpelers = new JButton("Lees deelnemers");
		bSpelers.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				// Create a file chooser
				final JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
				// In response to a button click:
				int returnVal = fc.showOpenDialog(hs);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					logger.log(Level.INFO, "Opening: " + file.getAbsolutePath() + ".");
					leesDeelnemers(file.getAbsolutePath());
				}

			}

		});
		panel.add(bSpelers, new ExtendedGridConstraints(1, curRow++));

		JButton bWis = new JButton("Wis lijst");
		bWis.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				wisDeelnemerslijst();
			}

		});
		panel.add(bWis, new ExtendedGridConstraints(0, curRow++));

		// Aantal spelers
		panel.add(new JLabel("Aantal spelers:"), new ExtendedGridConstraints(0, curRow));
		if (status.deelnemers != null) {
			tfAanwezig = new JTextField(Integer.toString(status.deelnemers.aantalAanwezig()), 10);
		} else {
			tfAanwezig = new JTextField(Integer.toString(0), 10);
		}
		tfAanwezig.setEditable(false);
		panel.add(tfAanwezig, new ExtendedGridConstraints(1, curRow++));


		// Aantal groepen
		JLabel label_4 = new JLabel("Aantal groepen");
		panel.add(label_4, new ExtendedGridConstraints(0, curRow++, 2, 1));
		panel.add(new JLabel("Minimum:"), new ExtendedGridConstraints(0, curRow));
		JTextField tfMinGroepen = new JTextField(Integer.toString(status.minGroepen), 10);
		tfMinGroepen.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				status.minGroepen = newIntegerValue(tfMinGroepen, status.minGroepen);
			}
		});
		panel.add(tfMinGroepen, new ExtendedGridConstraints(1, curRow++));
		panel.add(new JLabel("Maximum:"), new ExtendedGridConstraints(0, curRow));
		JTextField tfMaxGroepen = new JTextField(Integer.toString(status.maxGroepen), 10);
		tfMaxGroepen.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				status.maxGroepen = newIntegerValue(tfMaxGroepen, status.maxGroepen);
			}
		});
		panel.add(tfMaxGroepen, new ExtendedGridConstraints(1, curRow++));

		// Aantal spelers
		panel.add(new JLabel("Aantal spelers per groep"), new ExtendedGridConstraints(0, curRow++, 2, 1));
		panel.add(new JLabel("Minimum:"), new ExtendedGridConstraints(0, curRow));
		JTextField tfMinSpelers = new JTextField(Integer.toString(status.minSpelers), 10);
		tfMinSpelers.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				status.minSpelers = newIntegerValue(tfMinSpelers, status.minSpelers);
			}
		});
		panel.add(tfMinSpelers, new ExtendedGridConstraints(1, curRow++));
		panel.add(new JLabel("Maximum:"), new ExtendedGridConstraints(0, curRow));
		JTextField tfMaxSpelers = new JTextField(Integer.toString(status.maxSpelers), 10);
		tfMaxSpelers.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				status.maxSpelers = newIntegerValue(tfMaxSpelers, status.maxSpelers);
			}
		});
		panel.add(tfMaxSpelers, new ExtendedGridConstraints(1, curRow++));

		// Delta aantal spelers
		panel.add(new JLabel("Delta spelers voor uiterste groepen"), new ExtendedGridConstraints(0, curRow++, 2, 1));
		panel.add(new JLabel("Minimum:"), new ExtendedGridConstraints(0, curRow));
		JTextField tfMinDelta = new JTextField(Integer.toString(status.minDeltaSpelers), 10);
		tfMinDelta.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				status.minDeltaSpelers = newIntegerValue(tfMinDelta, status.minDeltaSpelers);
			}
		});
		panel.add(tfMinDelta, new ExtendedGridConstraints(1, curRow++));
		panel.add(new JLabel("Maximum:"), new ExtendedGridConstraints(0, curRow));
		JTextField tfMaxDelta = new JTextField(Integer.toString(status.maxDeltaSpelers), 10);
		tfMaxDelta.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				status.maxDeltaSpelers = newIntegerValue(tfMaxDelta, status.maxDeltaSpelers);
			}
		});
		panel.add(tfMaxDelta, new ExtendedGridConstraints(1, curRow++));

		// Delta aantal groepen met afwijkend aantal spelers
		panel.add(new JLabel("Aantal afwijkende groepen"), new ExtendedGridConstraints(0, curRow++, 2, 1));
		panel.add(new JLabel("Minimum:"), new ExtendedGridConstraints(0, curRow));
		JTextField tfMinDeltaGroepen = new JTextField(Integer.toString(status.minAfwijkendeGroepen), 10);
		tfMinDeltaGroepen.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				status.minAfwijkendeGroepen = newIntegerValue(tfMinDeltaGroepen, status.minAfwijkendeGroepen);
			}
		});
		panel.add(tfMinDeltaGroepen, new ExtendedGridConstraints(1, curRow++));
		panel.add(new JLabel("Maximum:"), new ExtendedGridConstraints(0, curRow));
		JTextField tfMaxDeltaGroepen = new JTextField(Integer.toString(status.maxAfwijkendeGroepen), 10);
		tfMaxDeltaGroepen.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				status.maxAfwijkendeGroepen = newIntegerValue(tfMaxDeltaGroepen, status.maxAfwijkendeGroepen);
			}
		});
		panel.add(tfMaxDeltaGroepen, new ExtendedGridConstraints(1, curRow++));

		// Byes
		panel.add(new JLabel("Aantal toegestane byes"), new ExtendedGridConstraints(0, curRow++, 2, 1));
		panel.add(new JLabel("Minimum:"), new ExtendedGridConstraints(0, curRow));
		JTextField tfMinByes = new JTextField(Integer.toString(status.minToegestaneByes), 10);
		tfMinByes.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				status.minToegestaneByes = newIntegerValue(tfMinByes, status.minToegestaneByes);
			}
		});
		panel.add(tfMinByes, new ExtendedGridConstraints(1, curRow++));

		panel.add(new JLabel("Maximum:"), new ExtendedGridConstraints(0, curRow));
		JTextField tfMaxByes = new JTextField(Integer.toString(status.maxToegestaneByes), 10);
		tfMaxByes.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				status.maxToegestaneByes = newIntegerValue(tfMaxByes, status.maxToegestaneByes);
			}
		});
		panel.add(tfMaxByes, new ExtendedGridConstraints(1, curRow++));

//		for (int i = 0; i < 4; i++) {
//			panel.add(new JLabel("A"), new ExtendedConstraints(0, curRow));
//			panel.add(new JLabel("A"), new ExtendedConstraints(1, curRow++));
//		}

		JButton bSchemas = new JButton("1. Bepaal mogelijkheden");
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
		panel.add(bSchemas, new ExtendedGridConstraints(0, curRow));
		JButton bGroepen = new JButton("2. Bepaal groepen");
		bGroepen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (schemaTabel != null) {
					int row = schemaTabel.getSelectedRow();
					bepaalGroepen(row);
					new ExcelExport().exportGroepen(status.groepen);
				}
				panel.getParent().repaint();
			}
		});
		panel.add(bGroepen, new ExtendedGridConstraints(1, curRow++));
		panel.add(new JLabel(" "), new ExtendedGridConstraints(0, 17, 2, 1));
		fixedComponentSize(panel, 300, 400);
		return panel;
	}

	protected void wisDeelnemerslijst() {
		deelnemersModel.wis();


	}

	public JPanel createDeelnemersPanel() {
		JPanel panel = new JPanel(false);
		panel.setBackground(Color.BLACK);
		//panel.setLayout(new GridLayout(1, 0));
		panel.setLayout(new BorderLayout());
			JPanel innerPanel = new JPanel();
			//innerPanel.setLayout(new GridLayout(1, 0));
			innerPanel.add(new JLabel("Naam:"), BorderLayout.NORTH);
			JTextField deelnemer = new JTextField(15);
			ArrayList<String> words = new ArrayList<>();
			if (status.OSBOSpelers != null) {
				for (Speler s : status.OSBOSpelers.values()) {
					words.add(s.getNaam().trim());
					words.add(Integer.toString(s.getKnsbnummer()));
				}
			}
			@SuppressWarnings("unused")
			Suggesties suggesties = new Suggesties(deelnemer, this, words, 2);
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
				tfAanwezig.setText(Integer.toString(status.deelnemers.aantalAanwezig()));
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

		fixedColumSize(deelnemersTabel.getColumnModel().getColumn(0), 30);
		fixedColumSize(deelnemersTabel.getColumnModel().getColumn(1), 55);
		fixedColumSize(deelnemersTabel.getColumnModel().getColumn(2), 150);
		fixedColumSize(deelnemersTabel.getColumnModel().getColumn(3), 40);
		fixedComponentSize(scrollPane, 300, 580);
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
		} else {
			JOptionPane.showMessageDialog(null, "Geen schema geselecteerd om te gebruiken. \n\rBepaal eerst de mogelijkheden en selecteer er één.");
		}
	}



}