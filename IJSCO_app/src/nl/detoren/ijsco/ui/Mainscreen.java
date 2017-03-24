package nl.detoren.ijsco.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;

import com.google.gson.Gson;

import nl.detoren.ijsco.data.Schema;
import nl.detoren.ijsco.data.Status;
import nl.detoren.ijsco.ui.control.IJSCOIndeler;
import nl.detoren.ijsco.ui.model.DeelnemersModel;
import nl.detoren.ijsco.ui.model.SchemaModel;

@SuppressWarnings("serial")
public class Mainscreen extends JFrame{

	private JPanel panel_deelnemers;
	private JPanel panel_instellingen;
	private JPanel panel_scenarios;
	private JPanel panel_configuratie;

	private SchemaModel schemaModel;

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

		setBounds(100, 100, 1040, 580);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("IJSCO Groepenindeler");
		getContentPane().setLayout(new GridLayout(1, 0, 0, 0));

		JPanel hoofdPanel = new JPanel();
		getContentPane().add(hoofdPanel);
		hoofdPanel.setLayout(new GridLayout(1, 4, 0, 0));

		// LINKS: Deelnemers
		panel_deelnemers = new JPanel(false);
		panel_deelnemers.setBackground(Color.BLACK);
		panel_deelnemers.setLayout(new GridLayout(1, 0));
		//panel_deelnemers.add(new JLabel("Deelnemers IJSCO toernooi"));
		hoofdPanel.add(panel_deelnemers);
		JTable deelnemersTabel = new JTable(new DeelnemersModel(panel_deelnemers, status.deelnemers)) {
			private static final long serialVersionUID = -8293073016982337108L;

			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component c = super.prepareRenderer(renderer, row, column);
				// Tooltip
				if (c instanceof JComponent) {
					DeelnemersModel model = (DeelnemersModel) getModel();
					((JComponent)c).setToolTipText(model.getToolTip(row, column).toString());
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
				hoofdPanel.repaint();
			}

		});
		JScrollPane scrollPane = new javax.swing.JScrollPane();
		scrollPane.setViewportView(deelnemersTabel);
		panel_deelnemers.add(scrollPane);

		// LINKSMIDDEN: INSTELLINGEN
		panel_configuratie = new JPanel();
		panel_configuratie.setBackground(Color.ORANGE);
		hoofdPanel.add(panel_configuratie);
		GridBagLayout gbl_panel_configuratie = new GridBagLayout();
		gbl_panel_configuratie.columnWidths = new int[]{128, 32, 0};
		gbl_panel_configuratie.rowHeights = new int[]{22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 22, 0};
		gbl_panel_configuratie.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_panel_configuratie.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel_configuratie.setLayout(gbl_panel_configuratie);

		// Aantal groepen
		JLabel label_4 = new JLabel("Aantal groepen");
		panel_configuratie.add(label_4, new ExtendedConstraints(0, 0, 2, 1));
		panel_configuratie.add(new JLabel("Minimum:"), new ExtendedConstraints(0, 1));
		JTextField tfMinGroepen = new JTextField(Integer.toString(status.minGroepen), 10);
		tfMinGroepen.addFocusListener(new FocusAdapter() {
		    public void focusLost(FocusEvent e) {
		    	status.minGroepen = newIntegerValue(tfMinGroepen, status.minGroepen);
		    }
		});
		panel_configuratie.add(tfMinGroepen, new ExtendedConstraints(1, 1));
		panel_configuratie.add(new JLabel("Maximum:"), new ExtendedConstraints(0, 2));
		JTextField tfMaxGroepen = new JTextField(Integer.toString(status.maxGroepen), 10);
		tfMaxGroepen.addFocusListener(new FocusAdapter() {
		    public void focusLost(FocusEvent e) {
		    	status.maxGroepen = newIntegerValue(tfMaxGroepen, status.maxGroepen);
		    }
		});
		panel_configuratie.add(tfMaxGroepen, new ExtendedConstraints(1, 2));

		// Aantal spelers
		panel_configuratie.add(new JLabel("Aantal spelers per groep"), new ExtendedConstraints(0, 3, 2, 1));
		panel_configuratie.add(new JLabel("Minimum:"), new ExtendedConstraints(0, 4));
		JTextField tfMinSpelers = new JTextField(Integer.toString(status.minSpelers), 10);
		tfMinSpelers.addFocusListener(new FocusAdapter() {
		    public void focusLost(FocusEvent e) {
		    	status.minSpelers= newIntegerValue(tfMinSpelers, status.minSpelers);
		    }
		});
		panel_configuratie.add(tfMinSpelers, new ExtendedConstraints(1, 4));
		panel_configuratie.add(new JLabel("Maximum:"), new ExtendedConstraints(0, 5));
		JTextField tfMaxSpelers = new JTextField(Integer.toString(status.maxSpelers), 10);
		tfMaxSpelers.addFocusListener(new FocusAdapter() {
		    public void focusLost(FocusEvent e) {
		    	status.maxSpelers= newIntegerValue(tfMaxSpelers, status.maxSpelers);
		    }
		});
		panel_configuratie.add(tfMaxSpelers, new ExtendedConstraints(1, 5));

		// Delta aantal spelers
		panel_configuratie.add(new JLabel("Delta spelers voor hoogste en laagste groepen"), new ExtendedConstraints(0, 6, 2, 1));
		panel_configuratie.add(new JLabel("Minimum:"), new ExtendedConstraints(0, 7));
		JTextField tfMinDelta = new JTextField(Integer.toString(status.minDeltaSpelers), 10);
		tfMinDelta.addFocusListener(new FocusAdapter() {
		    public void focusLost(FocusEvent e) {
		    	status.minDeltaSpelers= newIntegerValue(tfMinDelta, status.minDeltaSpelers);
		    }
		});
		panel_configuratie.add(tfMinDelta, new ExtendedConstraints(1, 7));
		panel_configuratie.add(new JLabel("Maximum:"), new ExtendedConstraints(0, 8));
		JTextField tfMaxDelta = new JTextField(Integer.toString(status.maxDeltaSpelers), 10);
		tfMaxDelta.addFocusListener(new FocusAdapter() {
		    public void focusLost(FocusEvent e) {
		    	status.maxDeltaSpelers= newIntegerValue(tfMaxDelta, status.maxDeltaSpelers);
		    }
		});
		panel_configuratie.add(tfMaxDelta, new ExtendedConstraints(1, 8));

		// Delta aantal groepen met afwijkend aantal spelers
		panel_configuratie.add(new JLabel("Aantal afwijkende groepen aan boven- en onderzijde"), new ExtendedConstraints(0, 9, 2, 1));
		panel_configuratie.add(new JLabel("Minimum:"), new ExtendedConstraints(0, 10));
		JTextField tfMinDeltaGroepen = new JTextField(Integer.toString(status.minAfwijkendeGroepen), 10);
		tfMinDeltaGroepen.addFocusListener(new FocusAdapter() {
		    public void focusLost(FocusEvent e) {
		    	status.minAfwijkendeGroepen= newIntegerValue(tfMinDeltaGroepen, status.minAfwijkendeGroepen);
		    }
		});
		panel_configuratie.add(tfMinDeltaGroepen, new ExtendedConstraints(1, 10));
		panel_configuratie.add(new JLabel("Maximum:"), new ExtendedConstraints(0, 11));
		JTextField tfMaxDeltaGroepen = new JTextField(Integer.toString(status.maxAfwijkendeGroepen), 10);
		tfMaxDeltaGroepen.addFocusListener(new FocusAdapter() {
		    public void focusLost(FocusEvent e) {
		    	status.maxAfwijkendeGroepen= newIntegerValue(tfMaxDeltaGroepen, status.maxAfwijkendeGroepen);
		    }
		});
		panel_configuratie.add(tfMaxDeltaGroepen, new ExtendedConstraints(1, 11));

		// Byes
		panel_configuratie.add(new JLabel("Aantal toegestane byes"), new ExtendedConstraints(0, 12, 2, 1));
		panel_configuratie.add(new JLabel("Minimum:"), new ExtendedConstraints(0, 13));
		JTextField tfMinByes = new JTextField(Integer.toString(status.minToegestaneByes), 10);
		tfMinByes.addFocusListener(new FocusAdapter() {
		    public void focusLost(FocusEvent e) {
		    	status.minToegestaneByes= newIntegerValue(tfMinByes, status.minToegestaneByes);
		    }
		});
		panel_configuratie.add(tfMinByes, new ExtendedConstraints(1, 13));
		panel_configuratie.add(new JLabel("Maximum:"), new ExtendedConstraints(0, 14));
		JTextField tfMaxByes = new JTextField(Integer.toString(status.maxToegestaneByes), 10);
		tfMaxByes.addFocusListener(new FocusAdapter() {
		    public void focusLost(FocusEvent e) {
		    	status.maxToegestaneByes= newIntegerValue(tfMaxByes, status.maxToegestaneByes);
		    }
		});
		panel_configuratie.add(tfMaxByes, new ExtendedConstraints(1, 14));
		JButton bSchemas = new JButton("Bepaal mogelijkheden");
		bSchemas.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				// TODO Update mogelijkheden model
				int ndeelnemers = status.deelnemers.aantalAanwezig();
				logger.log(Level.INFO, "Bepaal mogelijkheden voor n=" + ndeelnemers );
				status.schemas = indeler.mogelijkeSchemas(status);
				for (Schema s : status.schemas) {
					System.out.println(s);
				}
				schemaModel.setSchemas(status.schemas);
				hoofdPanel.repaint();
			}
		});
		panel_configuratie.add(bSchemas, new ExtendedConstraints(0, 16, 2, 1));

		panel_configuratie.add(new JLabel(" "), new ExtendedConstraints(0, 17, 2, 1));


		// RECHSTMIDDEN: SCENARIOS
		panel_scenarios = new JPanel();
		panel_scenarios.setBackground(Color.RED);
		panel_scenarios.setLayout(new GridLayout(1, 0));
		schemaModel = new SchemaModel(panel_scenarios, status.schemas);
		JTable schemaTabel = new JTable(schemaModel) {
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
				hoofdPanel.repaint();
			}

		});
		JScrollPane scrollPane2 = new javax.swing.JScrollPane();
		scrollPane2.setViewportView(schemaTabel);
		panel_scenarios.add(scrollPane2);
		hoofdPanel.add(panel_scenarios);

		// RECHTS: CONFIGURATIES
		panel_instellingen = new JPanel();
		panel_instellingen.setBackground(Color.YELLOW);
		hoofdPanel.add(panel_instellingen);

		this.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent event){
                    bewaarStatus();
                }
        });
}


	private int newIntegerValue(JTextField text, int oldValue) {
		int value;
		try {
			value = Integer.parseInt(text.getText());
		} catch (Exception e) {
			value= oldValue;
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
			status = nieuw;	// assure exception is thrown when things go wrong
			return true;
		} catch (Exception e) {
			// Could not read status
			System.out.println("Failed to read status : "+ e.getMessage());
			return false;
		}
	}

    public void bewaarStatus() {
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
}
