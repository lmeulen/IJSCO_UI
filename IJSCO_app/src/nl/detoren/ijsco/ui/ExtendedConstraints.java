package nl.detoren.ijsco.ui;

import java.awt.GridBagConstraints;
import java.awt.Insets;

public class ExtendedConstraints extends GridBagConstraints {

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
		this.insets = new Insets(0, 0, 0, 0);
		this.gridx = x;
		this.gridy = y;
		this.gridwidth = width;
		this.gridheight = height;
	}

	public ExtendedConstraints(int x, int y, int width, int height) {
		this.fill = GridBagConstraints.BOTH;
		this.insets = new Insets(0, 0, 0, 0);
		this.gridx = x;
		this.gridy = y;
		this.gridwidth = width;
		this.gridheight = height;
	}

	public ExtendedConstraints(int x, int y) {
		this.fill = GridBagConstraints.BOTH;
		this.insets = new Insets(0, 0, 0, 0);
		this.gridx = x;
		this.gridy = y;
		this.gridwidth = 1;
		this.gridheight = 1;
	}
}
