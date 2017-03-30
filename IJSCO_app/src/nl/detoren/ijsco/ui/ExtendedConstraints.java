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

import java.awt.GridBagConstraints;
import java.awt.Insets;

@SuppressWarnings("serial")
public class ExtendedConstraints extends GridBagConstraints {

	//private static final Insets insets5555 = new Insets(5, 5, 5, 5);
	private static final Insets insets5500 = new Insets(5, 5, 0, 0);

	@SuppressWarnings("unused")
	private ExtendedConstraints() {
	}

	public ExtendedConstraints(int fill, Insets borders, int x, int y, int width, int height) {
		this.fill = fill;
		this.insets = borders;
		this.gridx = x;
		this.gridy = y;
		this.gridwidth = width;
		this.gridheight = height;
	}

	public ExtendedConstraints(int fill, int x, int y, int width, int height) {
		this.fill = fill;
		this.insets = insets5500;
		this.gridx = x;
		this.gridy = y;
		this.gridwidth = width;
		this.gridheight = height;
	}

	public ExtendedConstraints(int x, int y, int width, int height) {
		this.fill = GridBagConstraints.BOTH;
		this.insets = insets5500;
		this.gridx = x;
		this.gridy = y;
		this.gridwidth = width;
		this.gridheight = height;
	}

	public ExtendedConstraints(int x, int y) {
		this.fill = GridBagConstraints.BOTH;
		this.insets = insets5500;
		this.gridx = x;
		this.gridy = y;
		this.gridwidth = 1;
		this.gridheight = 1;
	}
}
