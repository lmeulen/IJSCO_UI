package nl.detoren.ijsco.ui;

import java.awt.GridBagConstraints;
import java.awt.Insets;

@SuppressWarnings("serial")
public class ExtendedWeightConstraints extends GridBagConstraints {

	private static final Insets insets5555 = new Insets(5, 5, 5, 5);
	@SuppressWarnings("unused")
	private static final Insets insets5500 = new Insets(5, 5, 0, 0);

	public ExtendedWeightConstraints(int x, int y, double wx, double wy) {
		gridx = x;
		gridy = y;
		weightx = wx;
		weighty = wy;
		insets = insets5555;
	}
}
