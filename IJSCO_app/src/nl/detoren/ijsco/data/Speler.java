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
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.detoren.ijsco.ui.control.IJSCOController;

public class Speler {

	//region Static
    private final static Logger logger = Logger.getLogger(IJSCOController.class.getName());
	//endregion 

    //region Properties
	private int knsbnummer;
	private String naamKNSB;
	private String voornaamKNSB;
	private String achternaamKNSB;
	private String tussenvoegselKNSB;
	private String voorlettersKNSB;
	private String naamHandmatig;
	private String voornaamHandmatig;
	private String achternaamHandmatig;
	private String tussenvoegselHandmatig;
	private String voorlettersHandmatig;
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
	//endregion Properties

	public Speler() {
		this.knsbnummer = 0;
		this.naamKNSB = null;
		this.voornaamKNSB = null;
		this.achternaamKNSB = null;
		this.tussenvoegselKNSB = null;
		this.voorlettersKNSB = null;
		this.naamHandmatig = null;
		this.voornaamHandmatig = null;
		this.achternaamHandmatig = null;
		this.tussenvoegselHandmatig = null;
		this.voorlettersHandmatig = null;
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
		this.voornaamKNSB = null;
		this.achternaamKNSB = null;
		this.tussenvoegselKNSB = null;
		this.voorlettersKNSB = null;
		this.naamHandmatig = naam;
		this.voornaamHandmatig = null;
		this.achternaamHandmatig = null;
		this.tussenvoegselHandmatig = null;
		this.voorlettersHandmatig = null;
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

	public Speler(String voornaam, String voorletters, String tussenvoegsel, String achternaam) {
		this.knsbnummer = 0;
		this.naamKNSB = null;
		this.voornaamKNSB = null;
		this.achternaamKNSB = null;
		this.tussenvoegselKNSB = null;
		this.voorlettersKNSB = null;
		this.naamHandmatig = null;
		this.voornaamHandmatig = voornaam;
		this.achternaamHandmatig = achternaam;
		this.tussenvoegselHandmatig = tussenvoegsel;
		this.voorlettersHandmatig = voorletters;
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
		this.voornaamKNSB = null;
		this.achternaamKNSB = null;
		this.tussenvoegselKNSB = null;
		this.voorlettersKNSB = null;
		this.naamHandmatig = null;
		this.voornaamHandmatig = null;
		this.achternaamHandmatig = null;
		this.tussenvoegselHandmatig = null;
		this.voorlettersHandmatig = null;
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
		this.voornaamKNSB = null;
		this.achternaamKNSB = null;
		this.tussenvoegselKNSB = null;
		this.voorlettersKNSB = null;
		this.naamHandmatig = null;
		this.voornaamHandmatig = null;
		this.achternaamHandmatig = null;
		this.tussenvoegselHandmatig = null;
		this.voorlettersHandmatig = null;
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

	public Speler(int knsbnummer, String naamKNSB, String vereniging, int geboortejaar, String categorie, int ratingIJSCO,
			int ratingKNSB) {
		this.knsbnummer = knsbnummer;
		this.naamKNSB = naamKNSB;
		this.voornaamKNSB = null;
		this.achternaamKNSB = null;
		this.tussenvoegselKNSB = null;
		this.voorlettersKNSB = null;
		this.naamHandmatig = null;
		this.voornaamHandmatig = null;
		this.achternaamHandmatig = null;
		this.tussenvoegselHandmatig = null;
		this.voorlettersHandmatig = null;
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

	public Speler(int knsbnummer, String naamKNSB, String vereniging, int geboortejaar, String geslacht, String categorie, int ratingIJSCO,
			int ratingKNSB) {
		this.knsbnummer = knsbnummer;
		this.naamKNSB = naamKNSB;
		this.voornaamKNSB = null;
		this.achternaamKNSB = null;
		this.tussenvoegselKNSB = null;
		this.voorlettersKNSB = null;
		this.naamHandmatig = null;
		this.voornaamHandmatig = null;
		this.achternaamHandmatig = null;
		this.tussenvoegselHandmatig = null;
		this.voorlettersHandmatig = null;
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

	public String getAchternaamHandmatig() {
		return achternaamHandmatig;
	}

	public String getVoornaamHandmatig() {
		return voornaamHandmatig;
	}

	public String getVoorlettersHandmatig() {
		return voorlettersHandmatig;
	}

	public String getTussenvoegselHandmatig() {
		return tussenvoegselHandmatig;
	}

	/**
	 * Geef naam van de speler. Prioriteit: 1. Naam KNSB 2. Handmatig ingestelde
	 * naam 3. "???"
	 *
	 * @return
	 */
	public String getNaam() {
		if (overruleNaam) {
			return (getNaamHandmatig() != null ? getNaamHandmatig() : "");
		} else if (getNaamKNSB() != null && getNaamKNSB().length() > 0) {
			return getNaamKNSB();
		} else if (getNaamHandmatig() != null && getNaamHandmatig().length() > 0) {
			return getNaamHandmatig();
		} else {
			return "???";
		}
	}

	public String getAchternaam() {
		if (overruleNaam) {
			return (getAchternaamHandmatig() != null ? getAchternaamHandmatig() : "");
		} else if (getAchternaamKNSB() != null && getAchternaamKNSB().length() > 0) {
			return getAchternaamKNSB();
		} else if (getAchternaamHandmatig() != null && getAchternaamHandmatig().length() > 0) {
			return getAchternaamHandmatig();
		} else {
			return "???";
		}
	}

	public String getVoornaam() {
		if (overruleNaam) {
			return (getVoornaamHandmatig() != null ? getVoornaamHandmatig() : "");
		} else if (getVoornaamKNSB() != null && getVoornaamKNSB().length() > 0) {
			return getVoornaamKNSB();
		} else if (getVoornaamHandmatig() != null && getVoornaamHandmatig().length() > 0) {
			return getVoornaamHandmatig();
		} else {
			return "???";
		}
	}

	public String getVoorletters() {
		if (overruleNaam) {
			return (getVoorlettersHandmatig() != null ? getVoorlettersHandmatig() : "");
		} else if (getVoorlettersKNSB() != null && getVoorlettersKNSB().length() > 0) {
			return getVoorlettersKNSB();
		} else if (getVoorlettersHandmatig() != null && getVoorlettersHandmatig().length() > 0) {
			return getVoorlettersHandmatig();
		} else {
			return "";
		}
	}

	public String getTussenvoegsel() {
		if (overruleNaam) {
			return (getTussenvoegselHandmatig() != null ? getTussenvoegselHandmatig() : "");
		} else if (getTussenvoegselKNSB() != null && getTussenvoegselKNSB().length() > 0) {
			return getTussenvoegselKNSB();
		} else if (getTussenvoegselHandmatig() != null && getTussenvoegselHandmatig().length() > 0) {
			return getTussenvoegselHandmatig();
		} else {
			return "";
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

	public String getAchternaamKNSB() {
		return achternaamKNSB;
	}

	public String getVoornaamKNSB() {
		return voornaamKNSB;
	}

	public String getVoorlettersKNSB() {
		return voorlettersKNSB;
	}

	public String getTussenvoegselKNSB() {
		return tussenvoegselKNSB;
	}

	public String getNaamKNSB() {
		String samengestelde_naam = voornaamKNSB;
		if (tussenvoegselKNSB != null && !tussenvoegselKNSB.trim().isEmpty()) samengestelde_naam += " " + tussenvoegselKNSB;
		if (achternaamKNSB != null && !achternaamKNSB.trim().isEmpty()) samengestelde_naam += " " + achternaamKNSB;
		return samengestelde_naam;
	}

	public void setNaamKNSB(String naam) {
		this.naamKNSB = naam;
	}

	public void setNaamKNSB(String voornaam, String tussenvoegsel, String achternaam) {
		this.voornaamKNSB = voornaam;
		this.tussenvoegselKNSB = tussenvoegsel;
		this.achternaamKNSB = achternaam;
	}

	public void setNaamKNSB(String voornaam, String voorletters, String tussenvoegsel, String achternaam) {
		this.voornaamKNSB = voornaam;
		this.voorlettersKNSB = voorletters;
		this.tussenvoegselKNSB = tussenvoegsel;
		this.achternaamKNSB = achternaam;
	}

	public String getNaamHandmatig() {
		String samengestelde_naam = voornaamHandmatig;
		if (tussenvoegselHandmatig != null && !tussenvoegselHandmatig.trim().isEmpty()) samengestelde_naam += " " + tussenvoegselHandmatig;
		if (achternaamHandmatig != null && !achternaamHandmatig.trim().isEmpty()) samengestelde_naam += " " + achternaamHandmatig;
		return samengestelde_naam;
	}

	public void setNaamHandmatig(String naamHandmatig) {
		this.naamHandmatig = naamHandmatig;
	}

	public void setNaamHandmatig(String voornaam, String voorletters, String tussenvoegsel, String achternaam) {
		//this.naamHandmatig = naamHandmatig;
		this.voornaamHandmatig = voornaam;
		this.voorlettersHandmatig = voorletters;
		this.tussenvoegselHandmatig = tussenvoegsel;
		this.achternaamHandmatig = achternaam;
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

    /**
     * Wordt dezelfde speler gerepresenteerd door het andere object?
     * @param speler
     * @return
     */
    public boolean gelijkAan(Speler speler) {
        return (this.getNaam().equals(speler.getNaam())
//                && this.getInitialen().equals(speler.getInitialen())
//                && this.getGroep() == speler.getGroep()
        		&& this.getNaamKNSB() == speler.getNaamKNSB()
                && this.getKnsbnummer() == speler.getKnsbnummer());
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
		}
		catch (Exception ex) {
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

	public String getGeslacht() {
		return geslacht;
	}

	public void setGeslacht(String geslacht) {
		this.geslacht = geslacht;
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
