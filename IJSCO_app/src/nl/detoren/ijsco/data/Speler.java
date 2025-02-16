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

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import nl.detoren.ijsco.ui.control.IJSCOController;

public class Speler {

	// region Static
	private final static Logger logger = Logger.getLogger(IJSCOController.class.getName());
	// endregion

	// region Properties
	private int knsbnummer;
	private String naamKNSB;
	private String naamHandmatig;
	private String vereniging;
	private int geboortejaar;
	private String geslacht;
	private String categorie;
	private int ratingIJSCO;
	private int ratingKNSB;
	private int ratingHandmatig;
	private boolean aanwezig;
	private boolean overruleNaam;
	private boolean overruleRating;
	// endregion Properties

	public static String makeStringnumeric(String str) {
		String str2 = str.replaceAll("\\P{Digit}", "");
//		System.out.println(str2);
		return str2;
	}

	public Speler() {
		this.knsbnummer = 0;
		this.naamKNSB = null;
		this.naamHandmatig = null;
		this.vereniging = null;
		this.geboortejaar = -1;
		this.geslacht = "";
		this.ratingIJSCO = -1;
		this.ratingKNSB = -1;
		this.ratingHandmatig = -1;
		this.aanwezig = true;
		this.overruleNaam = false;
		this.overruleRating = false;
	}

	public Speler(String naam) {
		this.knsbnummer = 0;
		this.naamKNSB = null;
		this.naamHandmatig = naam;
		this.vereniging = null;
		this.geboortejaar = -1;
		this.geslacht = "";
		this.ratingIJSCO = -1;
		this.ratingKNSB = -1;
		this.ratingHandmatig = -1;
		this.aanwezig = true;
		this.overruleNaam = false;
		this.overruleRating = false;
	}

	public Speler(int knsbnummer) {
		this.knsbnummer = knsbnummer;
		this.naamKNSB = null;
		this.naamHandmatig = null;
		this.vereniging = null;
		this.geboortejaar = 1;
		this.geslacht = "";
		this.ratingIJSCO = -1;
		this.ratingKNSB = -1;
		this.ratingHandmatig = -1;
		this.aanwezig = true;
		this.overruleNaam = false;
		this.overruleRating = false;
	}

	public Speler(int knsbnummer, String naamKNSB, String vereniging, int ratingIJSCO, int ratingKNSB) {
		this.knsbnummer = knsbnummer;
		this.naamKNSB = naamKNSB;
		this.naamHandmatig = null;
		this.vereniging = vereniging;
		this.geboortejaar = -1;
		this.geslacht = "";
		this.ratingIJSCO = ratingIJSCO;
		this.ratingKNSB = ratingKNSB;
		this.ratingHandmatig = -1;
		this.aanwezig = true;
		this.overruleNaam = false;
		this.overruleRating = false;
	}

	public Speler(Speler speler) {
	}

	public Speler(int knsbnummer, String naamKNSB, String vereniging, int geboortejaar, String categorie,
			int ratingIJSCO, int ratingKNSB) {
		this.knsbnummer = knsbnummer;
		this.naamKNSB = naamKNSB;
		this.naamHandmatig = null;
		this.vereniging = vereniging;
		this.setGeboortejaar(geboortejaar);
		this.geslacht = "";
		this.setCategorie(categorie);
		this.ratingIJSCO = ratingIJSCO;
		this.ratingKNSB = ratingKNSB;
		this.ratingHandmatig = -1;
		this.aanwezig = true;
		this.overruleNaam = false;
		this.overruleRating = false;
	}

	public Speler(int knsbnummer, String naamKNSB, String vereniging, int geboortejaar, String geslacht,
			String categorie, int ratingIJSCO, int ratingKNSB) {
		this.knsbnummer = knsbnummer;
		this.naamKNSB = naamKNSB;
		this.naamHandmatig = null;
		this.vereniging = vereniging;
		this.setGeboortejaar(geboortejaar);
		this.geslacht = geslacht;
		this.setCategorie(categorie);
		this.ratingIJSCO = ratingIJSCO;
		this.ratingKNSB = ratingKNSB;
		this.ratingHandmatig = -1;
		this.aanwezig = true;
		this.overruleNaam = false;
		this.overruleRating = false;
	}

	/**
	 * Geef rating van de speler. Prioriteit in rating: 1. IJSCO rating 2. Handmatig
	 * ingestelde rating 3. KNSB rating 4. 100
	 *
	 * @return Rating van de speler
	 */
	public int getRating() {
		if (overruleRating) {
			return ratingHandmatig;
		} else if (ratingKNSB > 0) {
			return ratingKNSB;
		} else if (ratingIJSCO > 0) {
			return ratingIJSCO;
		} else if (ratingHandmatig > 0) {
			return ratingHandmatig;
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
		try {
			String knsbnummernumeric = makeStringnumeric(knsbnummer);
			this.knsbnummer = Integer.parseInt(knsbnummernumeric);
		} catch (Exception e) {
			logger.log(Level.WARNING, "Could not parse knsbnummer string to integer.");
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

	public String getVereniging() {
		return vereniging;
	}

	public void setVereniging(String vereniging) {
		this.vereniging = vereniging;
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

	public void setRatingKNSB(String ratingKNSB) {
		try {
			this.ratingKNSB = Integer.parseInt(makeStringnumeric(ratingKNSB));
		} catch (Exception e){
			this.ratingKNSB = -1;
		}
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
		} catch (Exception ex) {
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

	/**
	 * Wordt dezelfde speler gerepresenteerd door het andere object?
	 * 
	 * @param speler
	 * @return
	 */
	public boolean gelijkAan(Speler speler) {
		return (this.getNaam().equals(speler.getNaam())
//                && this.getInitialen().equals(speler.getInitialen())
//                && this.getGroep() == speler.getGroep()
				&& this.getNaamKNSB() == speler.getNaamKNSB() && this.getKnsbnummer() == speler.getKnsbnummer());
	}

	/**
	 * Wordt dezelfde speler gerepresenteerd door het andere object?
	 * 
	 * @param Uitslagspeler
	 * @return
	 */
	public boolean gelijkAan(UitslagSpeler s) {
			return (this.getNaam().equals(s.getNaam())
//	                && this.getInitialen().equals(speler.getInitialen())
//	                && this.getGroep() == speler.getGroep()
					&& this.getKnsbnummer() == s.getKNSBnummer());
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
		result += knsbnummer + ", " + getNaam() + ", " + getRating();
		return result;
	}

	public String toFormattedString() {
		String result = "";
		try {
			result += String.format("%1$10s | %2$34s | %3$6s", knsbnummer, this.getNaam(), this.getRating());
		} catch (Exception ex) {
			logger.log(Level.WARNING, "Speler toFormattedString problem");
		}
//		result += String.format("%1 10d | %2$20s | %3 6d", knsbnummer, this.getNaam(), this.getRating());
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

	public int getGeboortejaar() {
		return geboortejaar;
	}

	public void setGeboortejaar(int geboortejaar) {
		this.geboortejaar = geboortejaar;
	}

	public void setGeboortejaar(String geboortejaar) {
		try {
			this.geboortejaar = Integer.parseInt(makeStringnumeric(geboortejaar));		
		}
		catch (Exception e) {
			this.geboortejaar = 0;
		}
		
	}

	public String getGeslacht() {
		return geslacht;
	}

	public void setGeslacht(String geslacht) {
		// Trying to get this straight...\
		switch (geslacht.toUpperCase()) {
		case "W" :
			this.geslacht = "V";
			break;
		case "V" :
			this.geslacht = "V";
			break;
		case "VROUW" :
			this.geslacht = "V";
			break;
		case "MEID" :
			this.geslacht = "V";
			break;			
		case "FEMALE" :
			this.geslacht = "V";
			break;			
		case "MEISJE" :
			this.geslacht = "V";
			break;
		case "M" :
			this.geslacht = "M";
			break;
		case "MAN" :
			this.geslacht = "M";
			break;
		case "J" :
			this.geslacht = "M";
			break;
		case "JONGEN" :
			this.geslacht = "M";
			break;
		case "MALE" :
			this.geslacht = "M";
			break;
		// Mogelijk tricky!
//		case "" :
//			this.geslacht = "M";
//			break;
		default:
			this.geslacht = geslacht;
		}		
	}

	public String getCategorie() {
		return categorie;
	}

	public void setCategorie(String categorie) {
		this.categorie = categorie;
	}

	public void bepaalCategorie() {
		if (this.geboortejaar != -1) {
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(new Date());
			int catnr = calendar.get(Calendar.YEAR) - this.geboortejaar;
			switch (catnr) {
			case 1:
			case 2:
			case 3:
			case 4:
			case 5:
			case 6:
			case 7:
				this.setCategorie("H");
				break;
			case 8:
				this.setCategorie("G");
				break;
			case 9:
				this.setCategorie("F");
				break;
			case 10:
				this.setCategorie("E");
				break;
			case 11:
			case 12:
				this.setCategorie("D");
				break;
			case 13:
			case 14:
				this.setCategorie("C");
				break;
			case 15:
			case 16:
				this.setCategorie("B");
				break;
			case 17:
			case 18:
			case 19:
			case 20:
				this.setCategorie("A");
				break;
			}
		}
	}

}
