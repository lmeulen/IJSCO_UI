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

public class Speler {

	private int knsbnummer;
	private String naamKNSB;
	private String naamHandmatig;
	private int ratingIJSCO;
	private int ratingKNSB;
	private int ratingHandmatig;
	private boolean aanwezig;
	private boolean overruleNaam;
	private boolean overruleRating;

	public Speler() {
		knsbnummer = 0;
		naamKNSB = null;
		naamHandmatig = null;
		ratingIJSCO = -1;
		ratingKNSB = -1;
		ratingHandmatig = -1;
		aanwezig = true;
		overruleNaam = false;
		overruleRating = false;
	}

	public Speler(String naam) {
		knsbnummer = 0;
		naamKNSB = null;
		naamHandmatig = naam;
		ratingIJSCO = -1;
		ratingKNSB = -1;
		ratingHandmatig = -1;
		aanwezig = true;
		overruleNaam = false;
		overruleRating = false;
	}

	public Speler(int knsbnummer, String naamKNSB, int ratingIJSCO, int ratingKNSB) {
		this.knsbnummer = knsbnummer;
		this.naamKNSB = naamKNSB;
		this.naamHandmatig = null;
		this.ratingIJSCO = ratingIJSCO;
		this.ratingKNSB = ratingKNSB;
		this.ratingHandmatig = -1;
		this.aanwezig = true;
		overruleNaam = false;
		overruleRating = false;
	}

	/**
	 * Geef rating van de speler. Prioriteit in rating: 1. IJSCO rating 2.
	 * Handmatig ingestelde rating 3. KNSB rating 4. 100
	 *
	 * @return Rating van de speler
	 */
	public int getRating() {
		if (overruleRating) {
			return ratingHandmatig;
		} else if (ratingIJSCO > 0) {
			return ratingIJSCO;
		} else if (ratingHandmatig > 0) {
			return ratingHandmatig;
		} else if (ratingKNSB > 0) {
			return ratingKNSB;
		} else {
			return -1;
		}
	}

	/**
	 * Geef naam van de speler. Prioriteit: 1. Naam KNSB 2. Handmatig ingestelde
	 * naam 3. "???"
	 *
	 * @return
	 */
	public String getNaam() {
		if (overruleNaam) {
			return (naamHandmatig != null ? naamHandmatig : "");
		} else if (naamKNSB != null && naamKNSB.length() > 0) {
			return naamKNSB;
		} else if (naamHandmatig != null && naamHandmatig.length() > 0) {
			return naamHandmatig;
		} else {
			return "???";
		}
	}

	public int getKnsbnummer() {
		return knsbnummer;
	}

	public void setKnsbnummer(int knsbnummer) {
		this.knsbnummer = knsbnummer;
	}

	public void setKnsbnummer(String knsbnummer) {
		try{
			this.knsbnummer = Integer.parseInt(knsbnummer);
		}
		catch (Exception e) {
			this.knsbnummer = -1;
		}
	}

	public String getNaamKNSB() {
		return naamKNSB;
	}

	public void setNaamKNSB(String naam) {
		this.naamKNSB = naam;
	}

	public String getNaamHandmatig() {
		return naamHandmatig;
	}

	public void setNaamHandmatig(String naamHandmatig) {
		this.naamHandmatig = naamHandmatig;
	}

	public int getRatingIJSCO() {
		return ratingIJSCO;
	}

	public void setRatingIJSCO(int ratingIJSCO) {
		this.ratingIJSCO = ratingIJSCO;
	}

	public int getRatingKNSB() {
		return ratingKNSB;
	}

	public void setRatingKNSB(int ratingKNSB) {
		this.ratingKNSB = ratingKNSB;
	}

	public int getRatingHandmatig() {
		return ratingHandmatig;
	}

	public void setRatingHandmatig(int ratingHandmatig) {
		this.ratingHandmatig = ratingHandmatig;
	}

	public void setRatingHandmatig(String ratingHandmatig) {
		try {
			this.ratingHandmatig = Integer.parseInt(ratingHandmatig);
		}
		catch (Exception ex)
		{
			this.ratingHandmatig = -1;
		}
	}

	public boolean isBye() {
		return knsbnummer == 0;
	}

	public void setBye() {
		knsbnummer = 0;
		naamKNSB = null;
		naamHandmatig = "Bye";
		ratingIJSCO = -1;
		ratingKNSB = -1;
		ratingHandmatig = -1;
	}

	public String toStringComplete() {
		String result = "";
		result += knsbnummer + " - ";
		result += getNaam() + " [" + getNaamHandmatig() + " / " + getNaamKNSB() + " ] - ";
		result += getRating() + " [HM:" + getRatingHandmatig() + ", OSBO:" + getRatingIJSCO() + ", KNSB:"
				+ getRatingKNSB() + "]";
		return result;
	}

	public String toString() {
		String result = "";
		result += knsbnummer + " - " + getNaam() + " - " + getRating();
		return result;
	}

	public boolean isAanwezig() {
		return aanwezig;
	}

	public void setAanwezig(boolean aanwezig) {
		this.aanwezig = aanwezig;
	}

	public boolean isOverruleNaam() {
		return overruleNaam;
	}

	public void setOverruleNaam(boolean overruleNaam) {
		this.overruleNaam = overruleNaam;
	}

	public boolean isOverruleRating() {
		return overruleRating;
	}

	public void setOverruleRating(boolean overruleRating) {
		this.overruleRating = overruleRating;
	}




}
