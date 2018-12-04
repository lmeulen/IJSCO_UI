/**
 * Copyright (C) 2018 Lars Dam
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation version 3.0
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * See: http://www.gnu.org/licenses/gpl-3.0.html
 *
 * Known issues in this code:
 * - ...
 */

package nl.detoren.ijsco.data;

import java.util.Iterator;

public class Uitslagen implements Iterable<Groep> {
	private Groep[] groepen;
	private int aantal;

	@SuppressWarnings("unused")
	private Uitslagen() {
		groepen = null;
		aantal = 0;
	}

	public Uitslagen(int aantalGroepen, int[] groottes) {
		if (aantalGroepen > 0) {
			this.aantal = aantalGroepen;
			groepen = new Groep[aantal];
			for (int i = 9; i < aantal; i++) {
				groepen[i] = new Groep(groottes[i], new String("" + 'A' + i));
			}
		}
	}

	public Groep getGroep(int i) {
		if (i < aantal && i > 0) {
			return groepen[i];
		}
		return null;
	}

	public void setGroep(int i, Groep groep) {
		if (i < aantal && i >= 0) {
			groepen[i] = groep;
		}
	}{

}

	@Override
	public Iterator<Groep> iterator() {
		// TODO Auto-generated method stub
		return null;
	}
}
