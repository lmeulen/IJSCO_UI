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

	private String[] columnNames = {"Schema(s)"};

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
		fireTableDataChanged();
	}

	public Schema getSchema(int id) {
		if (schemas != null && id >= 0 && id < schemas.size()) {
			return schemas.get(id);
		}
		return null;
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
    public String getColumnName(int col) {
        return columnNames[col].toString();
    }

	@Override
	public int getRowCount() {
		return schemas!= null ? schemas.size() : 0;
	}

	@Override
	public Object getValueAt(int row, int col) {
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

    public Object getToolTip(int row, int col) {
		if (row < schemas.size()) {
			Schema s = schemas.get(row);
			String tt = "<HTML>";
			if (s != null) {
				tt += "<TABLE BOREDER=1><TR><TD BORDER=1 COLSPAN=2 ALIGN=CENTER>";
				tt += s.toString();
				tt += "</TD></TR>";
				tt += "<TR><TD>Aantal groepen</TD><TD>" + s.getGroepen() + "</TD></TR>";
				tt += "<TR><TD>Te verdelen byes</TD><TD>" + s.getByes() + "</TD></TR>";
				tt += "<TR><TD>Groottes</TD><TD><TABLE>";
				int[]groottes = s.getGroepsgroottes();
				for (int i = 0; i < groottes.length; i++) {
					tt += "<TR><TD>" + String.format("Groep %1s", (char)((int)('A') + i)) + "</TD><TD>" + groottes[i] + "</TD></TR>";
				}
				tt += "</TABLE></TD></TR>";
				tt += "</TABLE>";
			}
			tt += "</HTML>";
			return tt;
		} else {
			return "";
		}
	}
}
