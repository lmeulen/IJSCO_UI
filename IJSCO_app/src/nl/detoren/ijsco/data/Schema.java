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
package nl.detoren.ijsco.data;

public class Schema {
	private int groepen;
	private int byes;
	private int[] groepsgroottes;

	public Schema() {
		this(0, 0, new int[] {0});
	}

	public Schema(int groepen, int byes, int[] groottes) {
		super();
		this.groepen = groepen;
		this.byes = byes;
		groepsgroottes = new int[groottes.length];
		for (int i = 0; i < groottes.length; ++i) {
			groepsgroottes[i] = groottes[i];
		}
	}

	public String toString() {
//		String result = String.format("n=%3d, b=%1d : [ ", groepen, byes);
		String result = String.format("%02d(%1d) [ ", groepen, byes);
		for (int val : groepsgroottes) {
			result += String.format("%02d ", val);
		}
		result += "]";
		return result;
	}

	public int getGroepen() {
		return groepen;
	}

	public void setGroepen(int groepen) {
		this.groepen = groepen;
	}

	public int[] getGroepsgroottes() {
		return groepsgroottes;
	}

	public void setGroepsgroottes(int[] val) {
		groepsgroottes = new int[val.length];
		for (int i = 0; i < val.length; ++i) {
			groepsgroottes[i] = val[i];
		}
	}

	public int getByes() {
		return byes;
	}

	public void setByes(int byes) {
		this.byes = byes;
	}

	@Override
	public boolean equals(Object arg0) {
		if (arg0 instanceof Schema) {
			Schema s2 = (Schema) arg0;
			if ((this.byes != s2.byes) || (this.groepen != s2.groepen) ||
					(this.groepsgroottes.length != s2.groepsgroottes.length) ) {
				return false;
			}
			for (int i = 0; i < groepsgroottes.length; i++) {
				if (this.groepsgroottes[i] != s2.groepsgroottes[i]) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

}
