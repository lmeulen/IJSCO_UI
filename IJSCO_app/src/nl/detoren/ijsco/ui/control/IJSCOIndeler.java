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
package nl.detoren.ijsco.ui.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.detoren.ijsco.data.Deelnemers;
import nl.detoren.ijsco.data.Groep;
import nl.detoren.ijsco.data.Groepen;
import nl.detoren.ijsco.data.Schema;
import nl.detoren.ijsco.data.Schemas;
import nl.detoren.ijsco.data.Speler;
import nl.detoren.ijsco.data.Status;
import nl.detoren.ijsco.ui.Mainscreen;

public class IJSCOIndeler {

	private final static Logger logger = Logger.getLogger(Mainscreen.class.getName());

	/**
	 * Controleer deelnemers tegen de OSBO lijst. Obv KNSB nummer is de OSBO
	 * lijst leidend voor naam en rating.
	 *
	 * @param deelnemers Deelnemers
	 * @param osbolijst Osbo lijst
	 * @return bijgewerkte spelerslijst
	 */
	public Deelnemers controleerSpelers(Deelnemers deelnemers, HashMap<Integer, Speler> osbolijst) {
		Deelnemers update = new Deelnemers();
		for (Speler s : deelnemers) {
			Speler osbogegevens = osbolijst.get(s.getKnsbnummer());
			if (osbogegevens != null) {
				s.setNaamKNSB(osbogegevens.getNaam());
				s.setRatingIJSCO(osbogegevens.getRatingIJSCO());
			}
			update.add(s);
		}
		return update;
	}
	/**
	 * Bepaal beste groepsindeling op basis van schema en deelnemers
	 * @param schema Schema om te vullen
	 * @param deelnemers Deelnemers aan toernooi
	 * @return
	 */
	public Groepen bepaalGroep(Schema schema, Deelnemers deelnemers) {
		ArrayList<Groepen> mogelijkheden = mogelijkeGroepen(deelnemers.getAanwezigen(), schema.getGroepen(),
				schema.getGroepsgroottes(), schema.getByes());
		logger.log(Level.INFO, mogelijkheden.toString());
		Groepen groep = bepaalOptimaleGroep(mogelijkheden);
		return groep;
	}

	/**
	 * Van alle mogelijke groepen, bepaal de variant mdie het beste voldoet aan
	 * de voorwaarden:
	 * 1. kleinste spreiding in de groepen,
	 * 2. grootste verschil tussen de groepsovergangen
	 * 3. minimale standaard deviatie in de groepen
	 *
	 * @param mogelijkegroepen
	 * @return
	 */
	private Groepen bepaalOptimaleGroep(ArrayList<Groepen> mogelijkegroepen) {
		Groepen beste = null;
		int beste_spreiding = Integer.MAX_VALUE;
		int beste_groepverschil = 0;
		double beste_stddev = Double.MAX_VALUE;
		for (Groepen groepen : mogelijkegroepen) {
			if (groepen.getKleinsteGroep() >= 4) {
				if (groepen.getSpreidingTotaal() < beste_spreiding) {
					beste = groepen;
					beste_spreiding = groepen.getSpreidingTotaal();
					beste_groepverschil = groepen.getSomGroepVerschil();
					beste_stddev = groepen.getSomStdDev();
				} else if (groepen.getSpreidingTotaal() == beste_spreiding) {
					if (groepen.getSomGroepVerschil() > beste_groepverschil) {
						beste = groepen;
						beste_spreiding = groepen.getSpreidingTotaal();
						beste_groepverschil = groepen.getSomGroepVerschil();
						beste_stddev = groepen.getSomStdDev();
					} else if (groepen.getSomStdDev() < beste_stddev) {
						beste = groepen;
						beste_spreiding = groepen.getSpreidingTotaal();
						beste_groepverschil = groepen.getSomGroepVerschil();
						beste_stddev = groepen.getSomStdDev();
					}
				}
			}
		}
		return beste;
	}

	/**
	 * Bepaal alle mogelijke groepsindelingen, rekening houdende met de parameters
	 *
	 * @param spelers Deelnemers aan het toernooi
	 * @param groepen aantal groepen
	 * @param grootte int[] met groepsgrootte per groep
	 * @param byes aantal byes
	 * @return lijst met mogelijke groepsindelingen
	 */
	private ArrayList<Groepen> mogelijkeGroepen(Deelnemers spelers, int groepen, int[] grootte, int byes) {

		ArrayList<Groepen> result = new ArrayList<>();

		int max = (int) Math.pow(2, groepen);
		for (int i = 0; i < max; i++) {
			if (Integer.bitCount(i) == byes) {
				result.add(maakGroepen(spelers, groepen, grootte, i));
			}
		}
		return result;
	}

	/**
	 * Maak groepen, rekening houdende met de vastgestelde bye
	 *
	 * @param deelnemers Deelnemers te verdelen over de groepen
	 * @param nGroepen Het aantal te maken groepen
	 * @param grootte Het aantal spelers in een groep
	 * @param byemask Een bytemask dat aangeeft welke groepen een speler minder hebben
	 * @return
	 */
	private Groepen maakGroepen(Deelnemers deelnemers, int nGroepen, int[] grootte, int byemask) {
		logger.log(Level.INFO, "Deelnemers : "  + deelnemers + ",Groepen    : "  + nGroepen +
				",Grootte    : "  + grootte + ",Byemask    : "  + Integer.toBinaryString(byemask));
		Groepen groepen = new Groepen(nGroepen, grootte);
		Iterator<Speler> it = deelnemers.iterator();
		for (int i = 0; i < nGroepen; i++) {
			Groep groep = new Groep(grootte[i], String.format("Groep %1s", (char)((int)('A') + i)));
			int n = (getBit(byemask, i) == 1) ? grootte[i] - 1 : grootte[i];
			for (int j = 0; j < n; j++) {
				groep.addSpeler(it.next());
			}
			groepen.setGroep(i, groep);
		}
		return groepen;
	}

	/**
	 * Return bit k van waarde value
	 * @param value waarde
	 * @param k k-th bit
	 * @return 1 or 0, waarde van k-th bit van getal value
	 */
	private int getBit(int value, int k) {
		return (value >> k) & 1;
	}

	public Schemas mogelijkeSchemas(Status status) {
		int nSpelers = status.deelnemers.aantalAanwezig();
		Schemas mogelijkheden = new Schemas();
		for (int n_m = status.minSpelers; n_m <= status.maxSpelers; n_m += 2) { // itereer of standaard groepsgrootte
			for (int d_h = status.minDeltaSpelers; d_h <= status.maxDeltaSpelers; d_h += 2) { // itereer of delta (-) groepsgrootte bovenste groepen
				for (int d_l = status.minDeltaSpelers; d_l <= status.maxDeltaSpelers; d_l += 2) { // itereer of delta (+) groepsgrootte onderste groepen
					int n_hoog = n_m - d_h; // aantal spelers in bovenste groepen
					int n_laag = n_m + d_l; // aantal spelers in onderste groepen
					for (int i = status.minAfwijkendeGroepen; i <= status.maxAfwijkendeGroepen; i++) { // itereer over aantal (1..2) aan te passen hoogste groepen
						for (int j = status.minAfwijkendeGroepen; j <= status.maxAfwijkendeGroepen; j++) { // itereer over aantal (1..3) aan te passen onderste groepen
							int size_midden = nSpelers - (n_hoog * i) - (n_laag * j); // aantal spelers in standaard groepen
							if (size_midden > 0) {
								int gr_midden = (size_midden / n_m) + (((size_midden % n_m) == 0) ? 0 : 1);
								int[] groepen = creeerGroottes(i, n_hoog, gr_midden, n_m, j, n_laag);
								int byes = bepaalByes(groepen, nSpelers);
								if ((n_hoog >= status.minSpelers) && (n_laag <= status.maxSpelers)
										&& (groepen.length >= status.minGroepen) && (groepen.length <= status.maxGroepen)
										&& (byes >= status.minToegestaneByes) && (byes <= status.maxToegestaneByes)) {
									mogelijkheden.add(new Schema(groepen.length, byes, groepen));
								}
							}
						}
					}
				}
			}
		}
		return mogelijkheden;
	}


	/**
	 * Creeer int array met groepsgroottes
	 * @param n_hoog Aantal hoogste groepen met v_hoog spelers
	 * @param v_hoog Aantal spelers in de hoogste groepen
	 * @param n_midden Aantal groepen met v_midden spelers
	 * @param v_midden Aantal spelers in de middengroepen (basis)
	 * @param n_laag Aantal groepen met v_laag spelers
	 * @param v_laag Aantal spelers in de laggste groepen
	 * @return int[] met per groep de groepsgrootte
	 */
	private int[] creeerGroottes(int n_hoog, int v_hoog, int n_midden, int v_midden, int n_laag, int v_laag) {
		ArrayList<Integer> values = new ArrayList<>();
		for (int i = 0; i < n_hoog; ++i)
			values.add(v_hoog);
		for (int i = 0; i < n_midden; ++i)
			values.add(v_midden);
		for (int i = 0; i < n_laag; ++i)
			values.add(v_laag);
		int[] result = new int[values.size()];
		for (int i = 0; i < values.size(); i++)
			result[i] = values.get(i);
		return result;
	}

	/**
	 * Bepaal hoeveel byes er nodig zijn
	 * @param arr int array met groepsgroottes
	 * @param n aantal deelnemers
	 * @return teveel aan deelnemersplaatsen in de groepen
	 */
	private int bepaalByes(int[] arr, int n) {
		int tot = 0;
		for (int v : arr)
			tot += v;
		return (tot - n);
	}
}
