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
 * - Voorkom bij toevoegen dubbele KNSB nummers
 */
package nl.detoren.ijsco.ui.model;

import javax.swing.JComponent;
import javax.swing.table.AbstractTableModel;

import nl.detoren.ijsco.data.Deelnemers;
import nl.detoren.ijsco.data.Speler;

@SuppressWarnings("serial")
public class DeelnemersModel extends AbstractTableModel {

	private JComponent component;
	private Deelnemers deelnemers;

	private String[] columnNames = { "Aanw", "Nr.", "Naam", "Rating" };

	public DeelnemersModel(JComponent component, Deelnemers deelnemers) {
		super();
		this.component = component;
		this.deelnemers = deelnemers;
		init();
	}

	private void init() {

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
		return deelnemers != null ? deelnemers.size() : 0;
	}

	@Override
	public Object getValueAt(int row, int col) {
		if (row < deelnemers.size()) {
			Speler speler = deelnemers.get(row);
			switch (col) {
			case 0:
				return speler.isAanwezig();
			case 1:
				return new Integer(speler.getKnsbnummer());
			case 2:
				return speler.getNaam();
			case 3:
				return new Integer(speler.getRating());
			default:
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
		if (deelnemers != null && row < deelnemers.size()) {
			Speler speler = deelnemers.get(row);
			speler.setAanwezig((Boolean) value);
			fireTableCellUpdated(row, col);
			component.repaint();
		}
    }

    @Override
    public Class<?> getColumnClass(int col) {
        switch (col) {
        	case 0:
        		return Boolean.class;
            case 1:
                return Integer.class;
            case 2:
                return String.class;
            case 3:
                return Integer.class;
            default:
                return String.class;
        }
    }

    public Object getToolTip(int row, int col) {
		if (row < deelnemers.size()) {
			Speler speler = deelnemers.get(col);
			String tt = "<HTML><TABLE><TR><TD BORDER=1 COLSPAN=2 ALIGN=CENTER>";
			tt += speler.toString();
			tt += "</TD></TR>";
			tt += "<TR><TD>KNSBM</TD><TD>" + speler.getKnsbnummer() + "</TD></TR>";
			tt += "<TR><TD>Naam handmatig</TD><TD>" + speler.getNaamHandmatig() + "</TD></TR>";
			tt += "<TR><TD>Naam OSBK</TD><TD>" + speler.getNaamKNSB() + "</TD></TR>";
			tt += "<TR><TD>Rating handmatig</TD><TD>" + speler.getRatingHandmatig() + "</TD></TR>";
			tt += "<TR><TD>Rating OSBO</TD><TD>" + speler.getRatingIJSCO() + "</TD></TR>";
			tt += "<TR><TD>Rating KNSB</TD><TD>" + speler.getRatingKNSB() + "</TD></TR>";
			tt += "</TD></TR>";
			tt += "</TABLE></HTML>";
			return tt;
		} else {
			return "";
		}
	}

    public void add(Speler s) {
    	if (deelnemers != null) {
    		deelnemers.add(s);
    	}
    }

}
