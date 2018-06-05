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

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

public class Groep {
	private Speler[] spelers;
	private int grootte;
	private int aantal;
	private String naam;

	@SuppressWarnings("unused")
	private Groep() {
		grootte = 0;
		spelers = null;
		aantal = 0;
		naam = "";
	}

	public Groep(int grootte, String naam) {
		if (grootte > 0) {
			this.grootte = grootte;
			this.spelers = new Speler[this.grootte];
			for (int i = 0; i < grootte; ++i) {
				spelers[i] = new Speler();
				spelers[i].setBye();
			}
			this.aantal = 0;
		} else {
			this.grootte = 0;
			this.spelers = null;
			this.aantal = 0;
		}
		this.naam = naam;
	}

	public String getNaam() {
		return naam;
	}

	public void setNaam(String naam) {
		this.naam = naam;
	}

	/**
	 * Voeg een speler toe aan de groep.
	 *
	 * @param s
	 *            Spler
	 * @return true, als gelukt om speler toe te voegen false, als groep reeds
	 *         vol is
	 */
	public boolean addSpeler(Speler s) {
		if (aantal < grootte) {
			spelers[aantal] = s;
			aantal++;
			return true;
		}
		return false;
	}

	public Speler getSpeler(int i) {
		return (i < grootte) ? spelers[i] : null;
	}

	public int getMinRating() {
		int result = 9999;
		for (int i = 0; i < aantal; ++i) {
			result = (!spelers[i].isBye()) ? Math.min(result, spelers[i].getRating()) : result;
		}
		return aantal > 0 ? result : 0;
	}

	public int getMaxRating() {
		int result = 0;
		for (int i = 0; i < aantal; ++i) {
			result = (!spelers[i].isBye()) ? Math.max(result, spelers[i].getRating()) : result;
		}
		return aantal > 0 ? result : 0;
	}

	public int getGemmiddeldeRating() {
		int totaal = 0;
		int n = 0;
		for (int i = 0; i < aantal; ++i) {
			if (!spelers[i].isBye()) {
				totaal += spelers[i].getRating();
				n++;
			}
		}
		return n > 0 ? (totaal / n) : 0;
	}

	public int getAantal() {
		return aantal;
	}

	public int getGrootte() {
		return grootte;
	}

	public double getStandDev() {
		SummaryStatistics stats = new SummaryStatistics();
		for (int i = 0; i < aantal; ++i) {
			if (!spelers[i].isBye()) {
				stats.addValue(spelers[i].getRating());
			}
		}
		return stats.getStandardDeviation();
	}

	public double getSpreiding() {
		return getMaxRating() - getMinRating();
	}

	public String toString() {
		String result = naam + ", ";
		result += String.format("%2d/%2d ", aantal, grootte);
		result += String.format("(%4d-%4d-%4d)", getMinRating(), getGemmiddeldeRating(), getMaxRating());
		result += String.format(",std=%3.0f", getStandDev());
		return result;
	}

	public String getDescription() {
		String result = naam + "\n";
		for (Speler s : spelers) {
			result += String.format("  %7d - %-20.20s - %04d\n", s.getKnsbnummer(), s.getNaam(), s.getRating());
		}
		return result;
	}
}
