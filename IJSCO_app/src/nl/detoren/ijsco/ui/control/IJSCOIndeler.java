/**
 * Copyright (C) 2016-2018 Leo van der Meulen & Lars Dam
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.detoren.ijsco.data.Spelers;
import nl.detoren.ijsco.Configuratie;
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
	public Spelers controleerSpelers(Spelers deelnemers, HashMap<Integer, Speler> osbolijst) {
		Spelers update = new Spelers();
		if (deelnemers != null) { 
			for (Speler s : deelnemers) {
				if (osbolijst == null) {
					logger.log(Level.WARNING, "Osbolijst is null");
				} else {
					Speler osbogegevens = osbolijst.get(s.getKnsbnummer());
					if (osbogegevens != null) {
						s.setNaamKNSB(osbogegevens.getNaam());
						s.setRatingIJSCO(osbogegevens.getRatingIJSCO());
						s.setRatingKNSB(osbogegevens.getRatingKNSB());
						logger.log(Level.INFO, "Bond gegevens Geboortejaar :" + osbogegevens.getGeboortejaar());
						s.setGeboortejaar(osbogegevens.getGeboortejaar());
						s.setGeslacht(osbogegevens.getGeslacht());
						s.setCategorie(osbogegevens.getCategorie());
						// not available at the moment
						// s.setVereniging(osbogegevens.getVereniging());
					}
					update.add(s);
				}
			}	
		}
		return update;
	}
	/**
	 * Bepaal beste groepsindeling op basis van schema en deelnemers
	 * @param schema Schema om te vullen
	 * @param deelnemers Deelnemers aan toernooi
	 * @return
	 */
	public Groepen bepaalGroep(Schema schema, Spelers deelnemers, int nobyesmask) {
		ArrayList<Groepen> mogelijkheden = mogelijkeGroepen(deelnemers.getAanwezigen(), schema.getGroepen(),
				schema.getGroepsgroottes(), schema.getByes(), nobyesmask);
		logger.log(Level.INFO, mogelijkheden.toString());
		Groepen groep = bepaalOptimaleGroep(mogelijkheden);
		return groep;
	}

	/**
	 * Van alle mogelijke groepen, bepaal de variant die het beste voldoet aan
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
			//if (groepen.getKleinsteGroep() >= 4) {
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
			//}
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
	 * @var max 2^groepen om binair mask te berekenen
	 * @var mask binair mask = maximaal mask - nobyesmask
	 * @var aantalbyes aantal byes in current mask i 
	 * @return lijst met mogelijke groepsindelingen
	 */
	private ArrayList<Groepen> mogelijkeGroepen(Spelers spelers, int groepen, int[] grootte, int byes, int nobyesmask) {

		ArrayList<Groepen> result = new ArrayList<>();
		int max = (int) Math.pow(2, groepen);
		int mask = max - 1 - nobyesmask;
		for (int i = 0; i < max; i++) {
			int j = i & mask; 
			int aantalbyes = Integer.bitCount(reversebits(j));
			if (aantalbyes == byes) {
				Groepen maakGroepen = maakGroepen(spelers, groepen, grootte, j);
				result.add(maakGroepen);				
			}
		}
		return result;
	}

	/**
	 * Maak groepen, rekening houdende met de vastgestelde bye
	 
	 * @param deelnemers Deelnemers te verdelen over de groepen
	 * @param nGroepen Het aantal te maken groepen
	 * @param grootte Het aantal spelers in een groep
	 * @param byemask Een bytemask dat aangeeft welke groepen een speler minder hebben
	 * @return
	 */
	private Groepen maakGroepen(Spelers deelnemers, int nGroepen, int[] grootte, int byemask) {
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
		Configuratie c = status.config;
		int nSpelers = status.deelnemers.aantalAanwezig();
		Schemas mogelijkheden = new Schemas();
		if (c.minSpelers == 0) return mogelijkheden;
		for (int n_m = c.minSpelers; n_m <= c.maxSpelers; n_m += 2) { // itereer of standaard groepsgrootte
			for (int d_h = c.minDeltaSpelers; d_h <= c.maxDeltaSpelers; d_h += 2) { // itereer of delta (-) groepsgrootte bovenste groepen
				for (int d_l = c.minDeltaSpelers; d_l <= c.maxDeltaSpelers; d_l += 2) { // itereer of delta (+) groepsgrootte onderste groepen
					int n_hoog = n_m - d_h; // aantal spelers in bovenste groepen
					// int n_hoog = n_m; // aantal spelers in bovenste groepen (VERKEERD)
					int n_laag = n_m + d_l; // aantal spelers in onderste groepen
					for (int i = c.minAfwijkendeGroepen; i <= c.maxAfwijkendeGroepen; i++) { // itereer over aantal (1..2) aan te passen hoogste groepen
						for (int j = c.minAfwijkendeGroepen; j <= c.maxAfwijkendeGroepen; j++) { // itereer over aantal (1..3) aan te passen onderste groepen
							int size_midden = nSpelers - (n_hoog * i) - (n_laag * j); // aantal spelers in standaard groepen
							if (size_midden > 0) {
								int gr_midden = (size_midden / n_m) + (((size_midden % n_m) == 0) ? 0 : 1);
								int[] groepen = creeerGroottes(i, n_hoog, gr_midden, n_m, j, n_laag);
								int byes = bepaalByes(groepen, nSpelers);
								logger.log(Level.INFO, "Er zijn " + i + " top groepen met grootte " + n_hoog);
								logger.log(Level.INFO, "Er zijn " + size_midden + " spelers in de middelste " +  gr_midden + " groepen met grootte " + n_m);
								logger.log(Level.INFO, "Er zijn " + j + " onderste groepen met grootte " + n_laag);
								if (n_hoog==6) {
									logger.log(Level.INFO, "Size midden is " + size_midden);
									logger.log(Level.INFO, "i is " + i);
									logger.log(Level.INFO, "n_hoog is " + n_hoog);
									logger.log(Level.INFO, "gr_midden is " + gr_midden);
									logger.log(Level.INFO, "n_m is " + n_m);
									logger.log(Level.INFO, "j is " + j);
									logger.log(Level.INFO, "n_laag is " + n_laag);
									logger.log(Level.INFO, "groepen :" + Arrays.toString(groepen));
									logger.log(Level.INFO, "Mogelijk schema : " + new Schema(groepen.length, byes, groepen).toString());
								}
								if ((n_hoog >= c.minSpelers) && (n_laag <= c.maxSpelers)
										&& (groepen.length >= c.minGroepen) && (groepen.length <= c.maxGroepen)
										&& (byes >= c.minToegestaneByes) && (byes <= c.maxToegestaneByes)) {
									if (byes <= groepen.length) {
										logger.log(Level.INFO, "Nieuwe mogelijkheid");
										mogelijkheden.add(new Schema(groepen.length, byes, groepen));
										logger.log(Level.INFO, "Nieuw schema : " + new Schema(groepen.length, byes, groepen).toString());
										logger.log(Level.INFO, "Aantal mogelijkheden tot nu toe : " + mogelijkheden.size());
									}
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
	
	private int reversebits(int x) {
	int b=0;
		while (x!=0){
		  b<<=1;
		  b|=( x &1);
		  x>>=1;
		}
		return b;
	}
}
