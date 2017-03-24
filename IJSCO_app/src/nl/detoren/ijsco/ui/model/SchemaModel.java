package nl.detoren.ijsco.ui.model;

import javax.swing.JComponent;
import javax.swing.table.AbstractTableModel;

import nl.detoren.ijsco.data.Schema;
import nl.detoren.ijsco.data.Schemas;

@SuppressWarnings("serial")
public class SchemaModel extends AbstractTableModel {

	private JComponent component;
	private Schemas schemas;
	private int selected;

	private String[] columnNames = {"Schema"};

	public SchemaModel(JComponent component, Schemas schemas) {
		super();
		this.component = component;
		this.schemas = schemas;
		this.selected = -1;
		init();
	}

	private void init() {

	}
	public void setSchemas(Schemas schemas) {
		this.schemas = schemas;
	}

	@Override
	public int getColumnCount() {
		System.out.println("Get column count = " + columnNames.length);
		return columnNames.length;
	}

	@Override
    public String getColumnName(int col) {
        return columnNames[col].toString();
    }

	@Override
	public int getRowCount() {
		System.out.println("Get row count = " + (schemas!= null ? schemas.size() : 0));
		return schemas!= null ? schemas.size() : 0;
	}

	@Override
	public Object getValueAt(int row, int col) {
		System.out.println("Get value at " + row + "," + col);
		if (row < schemas.size()) {
			Schema schema = schemas.get(row);
			switch (col) {
			case 0:
				return schema.toString();
			case 2:
				return "";
			}
		}
		return null;
	}

    @Override
    public boolean isCellEditable(int row, int col) {
        return col == 0;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
    	selected = (selected == row) ? -1 : row;
    	component.repaint();
    }

    @Override
    public Class<?> getColumnClass(int col) {
        switch (col) {
        	case 0:
                return String.class;
            default:
                return String.class;
        }
    }
}
