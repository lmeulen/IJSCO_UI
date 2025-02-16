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


import nl.detoren.ijsco.data.UitslagSpeler;

/**
 *
 * @author Lars Dam
 */
public class WedstrijdUitslag {

	int wedstrijduitslagid;
	String poule;
	UitslagSpeler wit;
	int startratingWit;
	UitslagSpeler zwart;
	int startratingZwart;
	int ronde;
	
	/**
	 * Toto / Swiss Master style 1 = wit wint, 2 = zwart wint, 3 = remise, etc
	 */
	int resultaat;
	boolean nietReglementair = true;
	boolean adjourned = false;
	boolean bye = false;

	public static int UNKNOWN = -1; // Unknown
	public static int ZERO_FORF= 0; // Both players didn't show up
	public static int WHITE_WINS = 1; // Whites wins
	public static int BLACK_WINS = 2; // Black wins
	public static int DRAW = 3; // Draw
	public static int WHITE_ADJ = 4; // Adjourned (but the provisional score is 1-0)
	public static int BLACK_ADJ = 5; // Adjourned (but the provisional score is 0-1)
	public static int DRAW_ADJ = 6;	// Adjourned (but the provisional score is a draw)
	public static int WHITE_FORF = 7; // Black did not show up, white receives the point
	public static int BLACK_FORF = 8; // White did not show up, black receives the point
	public static int DRAW_FORF = 9; // The game is not played but the point is shared

	public WedstrijdUitslag() {
		this(0, null, null, 0);
	}

	public WedstrijdUitslag(int id, UitslagSpeler s1, UitslagSpeler s2, int uitslag) {
		this.wedstrijduitslagid = id;
		if ((s1 != null) && (s2 != null)) {
			setSpelers(s1, s2);
		} else {
			this.wit = s1;
			this.zwart = s2;
		}
		this.resultaat = uitslag;
		this.nietReglementair = true;
		this.adjourned = false;
	}

	private void setSpelers(UitslagSpeler s1, UitslagSpeler s2) {
		setWit(s1);
		setZwart(s2);		
	}

	public int getId() {
		return wedstrijduitslagid;
	}

	public void setId(int id) {
		this.wedstrijduitslagid = id;
	}

	public UitslagSpeler getWit() {
		return wit;
	}

	public void setWit(UitslagSpeler wit) {
		this.wit = wit;
	}

	public void setstartratingWit(int startrating) {
		this.startratingWit = startrating;
	}

	public UitslagSpeler getZwart() {
		return zwart;
	}

	public void setZwart(UitslagSpeler zwart) {
		this.zwart = zwart;
	}

	public void setstartratingZwart(int startrating) {
		this.startratingZwart = startrating;
	}
	public final void wisselSpelers() {
		UitslagSpeler tmp = wit;
		wit = zwart;
		zwart = tmp;
	}

	public int getUitslag() {
		return resultaat;
	}

	public void setPoule(String poule) {
		this.poule = poule;
	}

	public String getPoule() {
		return this.poule;
	}

	public void setRonde(int ronde) {
		this.ronde = ronde;
	}

	public int getRonde() {
		return this.ronde;
	}

	/**
	 * Geef uitslag in Toto stijl, waarbij 1=winst wit, 2=winst zwart, 3=remise, etc
	 * @param Uitslag
	 */
	public void setUitslag(int Uitslag) {
		this.resultaat = Uitslag;
	}
	
	public boolean isNietReglementair() {
		return nietReglementair;
	}

	public void setNietReglementair(boolean reglementair) {
		this.nietReglementair = reglementair;
	}

	public boolean isAdjourned() {
		return adjourned;
	}

	public void setAdjourned(boolean adjourned) {
		this.adjourned = adjourned;
	}
	/**
	 * Geef uitslag niet in Toto stijl maar voor snelle invoer in 0/1/2
	 * 0 = 0-1 => 2 (Toto)
	 * 1 = 1-0 => 1 (Toto)
	 * 2 = remise => 3 (Toto)
	 * 3 = 0-1 => 5 (adjourned)
	 * 4 = 1-0 => 4 (adjourned)
	 * 5 = remise => 6 (adjourned)
	 * 6 = 0-0 => 0 (reglementair)
	 * 7 = 0-1 => 2 (reglementair)
	 * 8 = 1-0 => 1 (reglementair)
	 * 9 = remise => 3 (reglementair)
	 * 
	 * Deze variant is handig bij invoeren van veel resultaten doordat
	 * het eerste getal van de uitslag ingevuld kan worden (met 2 voor half).
	 *
	 * @param uitslag
	 */
	public void setUitslag012(int uitslag) {
		nietReglementair = true;
		adjourned = false;
		bye = false;
		if (uitslag > 3 && uitslag <7 ) {
			uitslag -= 3;
			adjourned = true;
		}		
		if (uitslag > 6 ) {
			uitslag -= 7;
			nietReglementair = false;
		}
		if (uitslag > 9 ) {
			uitslag -= 10;
			bye = true;
		}
		this.resultaat = (uitslag == 0 ? 2 : (uitslag == 1 ? 1 : (uitslag == 2 ? 3 : 0)));
	}

	@Override
	public String toString() {
		String result;
		try {
		result = wit.toFormattedString() + zwart.toFormattedString();
//		result = String.format("%1$20s vs %2$20s => ", wit.toString(),zwart.toString());
			} catch (Exception ex)
		{
			result = String.format("%1$36s vs %2$36s => ", "wit", "zwart");
		}
		switch (resultaat) {
		case 0:
			result += "   0  -   0";
			break;
		case 1:
			result += "   1  -   0";
			break;
		case 2:
			result += "   0  -   1";
			break;
		case 3:
			result += "  1/2 - 1/2";
			break;
		default:
			result += "           ";
		}
		if (!nietReglementair) result += " F";
		else if (adjourned) result += " A";
		else result += " ";
		return result;
	}

	public boolean isUitslagBekend() {
		return (resultaat != UNKNOWN);
	}
	
}
