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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Groepen implements Iterable<Groep> {
	private List<Groep> groepen;
	private int aantal;

	@SuppressWarnings("unused")
	public Groepen() {
		groepen = new ArrayList<Groep>();
		aantal = 0;
	}

	public Groepen(int aantalGroepen, int[] groottes) {
		if (aantalGroepen > 0) {
			this.aantal = aantalGroepen;
			groepen = new ArrayList<Groep>();
			// 9 vervangen door 0. Geen idee of dit goed is.
			for (int i = 0; i < aantal; i++) {
				groepen.add(new Groep(groottes[i], new String("" + 'A' + i)));
			}
		}
	}

	public Groep getGroep(int i) {
		if (i < aantal && i > 0) {
			return groepen.get(i);
		}
		return null;
	}

	public void setGroep(int i, Groep groep) {
		if (i < aantal && i >= 0) {
			groepen.set(i,groep);
		}
	}

	public String toString() {
		String result = String.format("Groepen:n=%2d,spr=%4d,std=%3.0f,diff=%3d%n", aantal, getSpreidingTotaal(),
				getSomStdDev(), getSomGroepVerschil());
		for (Groep groep : groepen) {
			result += " " + groep.wedstrijdentoString() + "\n";
		}
		return result;
	}

	public String getDescription() {
		String result = toString(	) + "\n";
		for (Groep groep : groepen) {
			result += groep.getDescription() + "\n";
		}
		return result;
	}

	public int getSpreidingTotaal() {
		int result = 0;
		for (Groep g : groepen) {
			result += g.getSpreiding();
		}
		return result;
	}

	/**
	 * Retourneer de som van de verschillen tussen de laagste speler in groep n
	 * en de hoogste speler in groep n + 1
	 *
	 * @return
	 */
	public int getSomGroepVerschil() {
		int result = 0;
		for (int i = 0; i < aantal - 1; i++) {
			result += groepen.get(i).getMinRating() - groepen.get(i+1).getMaxRating();
		}
		return result;
	}

	public double getSomStdDev() {
		int result = 0;
		for (Groep g : groepen) {
			result += g.getStandDev();
		}
		return result;
	}

	public int getKleinsteGroep() {
		int result = 99;
		for (Groep g : groepen) {
			result = Math.min(g.getAantal(), result);
		}
		return result;
	}

	public int getGrootsteGroep () {
		int result = 0;
		for (Groep g : groepen) {
			result = Math.max(g.getAantal(), result);
		}
		return result;
	}

	@Override
	public Iterator<Groep> iterator() {
		return (groepen).iterator();
	}

	public void Add(Groep groep) {
		this.aantal++;
		groepen.add(groep);
		
	}
}
