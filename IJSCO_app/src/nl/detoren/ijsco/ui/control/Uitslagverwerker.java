package nl.detoren.ijsco.ui.control;

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
	 * Problemen in deze code:
	 */
	
	import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
	import java.util.logging.Logger;

import nl.detoren.ijsco.data.Groep;
import nl.detoren.ijsco.data.Groepen;
import nl.detoren.ijsco.data.GroepsUitslag;
import nl.detoren.ijsco.data.GroepsUitslagen;
import nl.detoren.ijsco.data.Speler;
import nl.detoren.ijsco.data.UitslagSpeler;
import nl.detoren.ijsco.data.Wedstrijd;
import nl.detoren.ijsco.data.WedstrijdUitslag;

	/**
	 *
	 * Verwerk uitslagen in een nieuwe stand
	 *
	 */
	public class Uitslagverwerker {

	    private final static Logger logger = Logger.getLogger(IJSCOController.class.getName());

		/**
		 * Verwerk de wedstrijduitslagen en maak groepsuitslag
		 *
		 * @param spelersgroepen
		 *            De spelersgroepen
		 * @param wedstrijden
		 *            De gespeelde wedstrijden
		 * @return Bijgewerkte standen
		 */
		public GroepsUitslagen verwerkUitslag(Groepen groepen) {
			GroepsUitslagen groepsuitslagen = new GroepsUitslagen();
			for (Groep groep : groepen) {
				logger.log(Level.INFO, "Verwerk uitslag voor groep " + groep.getNaam());
				GroepsUitslag uitslag = new GroepsUitslag();
				uitslag.setGroepsnaam(groep.getNaam());
				for (Speler speler : groep.getSpelers()) {
					logger.log(Level.INFO, "Verwerk uitslag voor speler " + speler.getNaam());
					if (speler.getNaamHandmatig() != null) { 
						logger.log(Level.INFO, "NaamHandmatig " + speler.getNaamHandmatig());
						if (!speler.getNaamHandmatig().equals("Bye")) { 
							UitslagSpeler update = updateSpeler(speler, groep);
							uitslag.addSpeler(update);
							uitslag.setAantal(uitslag.getAantal() - 1);
						}
					} else {
						logger.log(Level.INFO, "NaamHandmatig is null");
						UitslagSpeler update = updateSpeler(speler, groep);
						uitslag.addSpeler(update);
					}
				}
				groepsuitslagen.Add(uitslag);
			}
			return groepsuitslagen;
		}

		/**
		 * Return een bijgewerkte versie van een speler. Bijgewerkt zijn aantal
		 * punten en zijn rating
		 *
		 * @param speler
		 *            Speler om bij te werken
		 * @param wedstrijden
		 *            Alle wedstrijden
		 * @return Bijgewerkte speler
		 */
		private UitslagSpeler updateSpeler(Speler speler, Groep groep) {
			ArrayList<Wedstrijd> spelerWedstrijden = getWedstrijdenVoorSpeler(speler, groep);
			UitslagSpeler updateSpeler = new UitslagSpeler(speler);
			int aantalgewonnen = 0;
			int aantalremise = 0;
			int puntenbij = 0; // puntenbij is 10 * bordpunten ivm halve punten
			int ratingbij = 0;
			logger.log(Level.INFO, "UpdateSpeler aangeroepen. Aantal wedstrijden voor speler " + speler.getNaam() + " is " + spelerWedstrijden.size());
			for (Wedstrijd w : spelerWedstrijden) {
				//logger.log(Level.INFO, "    Wedstrijd :" + w.toString());
				int resultaat = -1; // onbekend
				Speler tegenstander = w.getWit().gelijkAan(speler) ? w.getZwart() : w.getWit();
				Boolean spelerIswit = w.getWit().gelijkAan(speler) ? true : false;
				//
				// w.getUitslag 1=wit wint 2=zwart wint 3=remise
				int uitslagWit = w.getUitslag(); // uitslag vanuit perspectief wit
				int uitslagZwart = (w.getUitslag() == 1) ? 2 : ((w.getUitslag() == 2) ? 1 : 3);
				int deltaWit;
				int deltaZwart;

				// PUNTEN
				if (uitslagWit == Wedstrijd.UNKNOWN) {
					// doe niets
				} else if (uitslagWit == Wedstrijd.DRAW) {
					puntenbij += 5;
					ratingbij +=  
					aantalremise++;
					resultaat = 3;
					//logger.log(Level.INFO, "      Remise         : " + puntenbij);
				} else if ((uitslagWit == Wedstrijd.WHITE_WINS) && (w.getWit().gelijkAan(speler))) {
					puntenbij += 10;
					aantalgewonnen++;
					resultaat = 1;
					//logger.log(Level.INFO, "      Winst met wit  : " + puntenbij);
				} else if ((uitslagWit == Wedstrijd.BLACK_WINS) && (w.getZwart().gelijkAan(speler))) {
					puntenbij += 10;
					aantalgewonnen++;
					resultaat = 1;
					//logger.log(Level.INFO, "      Winst met zwart :" + puntenbij);
				} else {
					// verlies
					resultaat = 2;
					//logger.log(Level.INFO, "      Verlies        :" + puntenbij);
				}
				if (w.isNietReglementair()) {
					Speler wit = w.getWit();
					Speler zwart = w.getZwart();
	
					int ratingWit = wit.getRating();
					int ratingZwart = zwart.getRating();

					//logger.log(Level.INFO, "Startrating wit " + wit.getRating());
					//logger.log(Level.INFO, "Startrating zwart " + zwart.getRating());

					deltaWit = deltaRatingOSBO(Math.max(ratingWit, 100), Math.max(ratingZwart, 100), uitslagWit);
					deltaZwart = deltaRatingOSBO(Math.max(ratingZwart,100), Math.max(ratingWit, 100), uitslagZwart);

					ratingbij += spelerIswit ? deltaWit : deltaZwart;

					//wit.setRating(Math.max(nieuwWit, 100));
					//zwart.setRating(Math.max(nieuwZwart, 100));
	
					//logger.log(Level.INFO, w.toString());
					//logger.log(Level.INFO, "Wit: " + wit.getNaam() + " van " + ratingWit + " +/- " + deltaWit + " naar " + (ratingWit + deltaWit));
					//logger.log(Level.INFO, "Zwart: " + zwart.getNaam() + " van " + ratingZwart + " +/- " + deltaZwart + " naar " + (ratingZwart +deltaZwart));
				} else {
					logger.log(Level.INFO, w.toString());
					logger.log(Level.INFO, "Niet reglementair, geen aanpassing rating");
				}
			}
			// check is rating will not drop below 100
			logger.log(Level.INFO, "new rating would be : " + (updateSpeler.getStartrating() + ratingbij));
			logger.log(Level.INFO, "100 - newrating : " + (100 - (updateSpeler.getStartrating() + ratingbij)));
			logger.log(Level.INFO, "max(100 - newrating, 0) : " + Math.max(0, (100 - (updateSpeler.getStartrating() + ratingbij))));
			logger.log(Level.INFO, "ratingbij + max(100 - newrating, 0) : " + (ratingbij + Math.max(0, (100 - (updateSpeler.getStartrating() + ratingbij)))));
			ratingbij = ratingbij + (Math.max(100 - (updateSpeler.getStartrating() + ratingbij),0));
			//logger.log(Level.INFO, "Punten :" + puntenbij/10);
			logger.log(Level.INFO, "DeltaRating :" + ratingbij);
			logger.log(Level.INFO, "Rating van " + updateSpeler.getStartrating() + " +/- " + ratingbij + " naar " + (updateSpeler.getStartrating() + ratingbij));
			String bijofaf = ratingbij>0 ? "+" : "-";
			int startrating = updateSpeler.getStartrating();
			logger.log(Level.INFO, updateSpeler.getNaam() + "|" + puntenbij/10 + "|" + startrating + bijofaf + Math.abs(ratingbij) + " naar " + Math.max(100, (startrating + ratingbij)));
			//
			updateSpeler.setStartrating(speler.getRating());
			updateSpeler.setPunten(puntenbij);
			updateSpeler.setDeltarating(ratingbij);
			return updateSpeler;
		}

		/**
		 * Geef voor een specifieke speler alle wedstrijden die hij gespeeld heeft
		 *
		 * @param speler
		 *            De speler
		 * @param wedstrijden
		 *            Alle wedstrijden van een speelavond
		 * @return wedstrijden gespeeld door speler
		 */
		private ArrayList<Wedstrijd> getWedstrijdenVoorSpeler(Speler speler, Groep groep) {
			logger.log(Level.INFO, "Vind wedstrijden voor speler :" + speler.toString());
			ArrayList<Wedstrijd> result = new ArrayList<>();
			for (Wedstrijd w : groep.getWedstrijden()) {
				if (w !=null) {
					if (w.getWit().gelijkAan(speler) || w.getZwart().gelijkAan(speler)) {
						result.add(w);
					}
				}
			}	
			logger.log(Level.INFO, "" + result.size() + " wedstrijden gevonden voor " + speler.toString());
			return result;
		}

		/**
		 * Geef voor een specifieke speler alle wedstrijden die hij gespeeld heeft
		 *
		 * @param speler
		 *            De speler
		 * @param wedstrijden
		 *            Alle wedstrijden van een speelavond
		 * @return wedstrijden gespeeld door speler
		 */
		private List<WedstrijdUitslag> getWedstrijdenVoorSpeler(UitslagSpeler speler, GroepsUitslag groep) {
			logger.log(Level.INFO, "Vind wedstrijden voor speler :" + speler.toString());
			Iterator<WedstrijdUitslag> iter = groep.getWedstrijden().iterator();
			List<WedstrijdUitslag> result = new ArrayList<>();
			while (iter.hasNext()) {
			//for (WedstrijdUitslag w : result) {
				WedstrijdUitslag w = iter.next();
				if (w !=null) {
					if (w.getWit().gelijkAan(speler) || w.getZwart().gelijkAan(speler)) {
						result.add(w);
					}
				}
			}	
			logger.log(Level.INFO, "" + result.size() + " wedstrijden gevonden voor " + speler.toString());
			return result;
		}

		/**
		 * Bereken delta rating conform de regels van de OSBO en zoals gebruikt bij
		 * de interne competitie
		 *
		 * @param beginRating
		 * @param tegenstanderRating
		 * @param uitslag
		 *            1 = winst, 2 = verlies, 3 = remise
		 * @return
		 */
		public int deltaRatingOSBO(int beginRating, int tegenstanderRating, int uitslag) {

			int[] ratingTabel = { 0, 16, 31, 51, 71, 91, 116, 141, 166, 201, 236, 281, 371, 9999 };
			int ratingVerschil = Math.abs(beginRating - tegenstanderRating);
			boolean ratingHogerDanTegenstander = beginRating > tegenstanderRating;
			int index = 0;
			while (ratingVerschil >= ratingTabel[index]) {
				index++;
			}
			index--; // iterator goes one to far.
			if (index == -1)
				index = 0;
			// Update rating wit
			// Dit gebeurd aan de hand van de volgende OSBO tabel
			// Hierin is: W> = winnaar heeft de hoogste rating
			// W< = winnaar heeft de laagste rating
			// V> = verliezer heeft de hoogste rating
			// V< = verliezer heeft de laagste rating
			// R> = remise met de hoogste rating
 			// R< = remise met de laagste rating
			//
			// In de volgende tabel wordt de aanpassing van de rating weergegeven
			// rating
			// verschil W> V< W< V> R> R<
			// 0- 15 +12 -12 +12 -12 0 0
			// 16- 30 +11 -11 +13 -13 - 1 + 1
			// 31- 50 +10 -10 +14 -14 - 2 + 2
			// 51- 70 + 9 - 9 +15 -15 - 3 + 3
			// 71- 90 + 8 - 8 +16 -16 - 4 + 4
			// 91-115 + 7 - 7 +17 -17 - 5 + 5
			// 116-140 + 6 - 6 +18 -18 - 6 + 6
			// 141-165 + 5 - 5 +19 -19 - 7 + 7
			// 166-200 + 4 - 4 +20 -20 - 8 + 8
			// 201-235 + 3 - 3 +21 -21 - 9 + 9
			// 236-280 + 2 - 2 +22 -22 -10 +10
			// 281-370 + 1 - 1 +23 -23 -11 +11
			// >371 + 0 - 0 +24 -24 -12 +12
			int deltaRating;
			switch (uitslag) {
			case 1: // Winst
				deltaRating = 12 + (ratingHogerDanTegenstander ? (-1 * index) : (+1 * index));
				return deltaRating;
			case 2: // Verlies
				deltaRating = 12 + (ratingHogerDanTegenstander ? (+1 * index) : (-1 * index));
				return -deltaRating;
			case 3: // Remise
				deltaRating = (ratingHogerDanTegenstander ? (-1 * index) : (+1 * index));
				return deltaRating;
			default: // Geen uitstal
				logger.log(Level.SEVERE , "Rating update: Uitslag is geen winst, geen verlies en geen remise.");
				return 0;
			}
		}

		public GroepsUitslagen verwerkUitslag(GroepsUitslagen groepenuitslagen) {
			for (GroepsUitslag groep : groepenuitslagen) {
				logger.log(Level.INFO, "Verwerk uitslag voor groep " + groep.getGroepsnaam());
				for (Map.Entry<Integer, UitslagSpeler> m : groep.getSpelers().entrySet()) {
					UitslagSpeler u = m.getValue();
					if (u.getNaam() != null) { 
						logger.log(Level.INFO, "Verwerk uitslag voor speler " + u.getNaam());
						if (!u.getNaam().equals("Bye")) { 
							u = updateSpeler(u, groep);
							//uitslag.addSpeler(update);
							//uitslag.setAantal(uitslag.getAantal() - 1);
						}
					} else {
						//UitslagSpeler update = updateSpeler(speler, groep);
						//uitslag.addSpeler(update);
					}
				}
				//groepsuitslagen.Add(uitslag);
			}
			return groepenuitslagen;
		}

		private UitslagSpeler updateSpeler(UitslagSpeler u, GroepsUitslag groep) {
			List<WedstrijdUitslag> spelerWedstrijden = getWedstrijdenVoorSpeler(u, groep);
			//UitslagSpeler updateSpeler = new UitslagSpeler(speler);
			int aantalgewonnen = 0;
			int aantalremise = 0;
			int puntenbij = 0; // puntenbij is 10 * bordpunten ivm halve punten
			int ratingbij = 0;
			for (WedstrijdUitslag w : spelerWedstrijden) {
				//logger.log(Level.INFO, "    Wedstrijd :" + w.toString());
				int resultaat = -1; // onbekend
				UitslagSpeler tegenstander = w.getWit().gelijkAan(u) ? w.getZwart() : w.getWit();
				Boolean spelerIswit = w.getWit().gelijkAan(u) ? true : false;
				//
				// w.getUitslag 1=wit wint 2=zwart wint 3=remise
				int uitslagWit = w.getUitslag(); // uitslag vanuit perspectief wit
				int uitslagZwart = (w.getUitslag() == 1) ? 2 : ((w.getUitslag() == 2) ? 1 : 3);
				int deltaWit;
				int deltaZwart;

				// PUNTEN
				if (uitslagWit == Wedstrijd.UNKNOWN) {
					// doe niets
				} else if (uitslagWit == Wedstrijd.DRAW) {
					puntenbij += 5;
					ratingbij +=  
					aantalremise++;
					resultaat = 3;
					//logger.log(Level.INFO, "      Remise         : " + puntenbij);
				} else if ((uitslagWit == Wedstrijd.WHITE_WINS) && (w.getWit().gelijkAan(u))) {
					puntenbij += 10;
					aantalgewonnen++;
					resultaat = 1;
					//logger.log(Level.INFO, "      Winst met wit  : " + puntenbij);
				} else if ((uitslagWit == Wedstrijd.BLACK_WINS) && (w.getZwart().gelijkAan(u))) {
					puntenbij += 10;
					aantalgewonnen++;
					resultaat = 1;
					//logger.log(Level.INFO, "      Winst met zwart :" + puntenbij);
				} else {
					// verlies
					resultaat = 2;
					//logger.log(Level.INFO, "      Verlies        :" + puntenbij);
				}
				if (w.isNietReglementair()) {
					UitslagSpeler wit = w.getWit();
					UitslagSpeler zwart = w.getZwart();
	
					int ratingWit = wit.getStartrating();
					int ratingZwart = zwart.getStartrating();

					//logger.log(Level.INFO, "Startrating wit " + wit.getStartrating());
					//logger.log(Level.INFO, "Startrating zwart " + zwart.getStartrating());

					deltaWit = deltaRatingOSBO(Math.max(ratingWit, 100), Math.max(ratingZwart, 100), uitslagWit);
					deltaZwart = deltaRatingOSBO(Math.max(ratingZwart,100), Math.max(ratingWit, 100), uitslagZwart);

					ratingbij += spelerIswit ? deltaWit : deltaZwart;

					//wit.setRating(Math.max(nieuwWit, 100));
					//zwart.setRating(Math.max(nieuwZwart, 100));
	
					//logger.log(Level.INFO, w.toString());
					//logger.log(Level.INFO, "Wit: " + wit.getNaam() + " van " + ratingWit + " +/- " + deltaWit + " naar " + (ratingWit + deltaWit));
					//logger.log(Level.INFO, "Zwart: " + zwart.getNaam() + " van " + ratingZwart + " +/- " + deltaZwart + " naar " + (ratingZwart +deltaZwart));
				} else {
					logger.log(Level.INFO, w.toString());
					logger.log(Level.INFO, "Niet reglementair, geen aanpassing rating");
				}
			}
			logger.log(Level.INFO, "new rating would be : " + u.getStartrating() + ratingbij);
			logger.log(Level.INFO, "100 - newrating : " + (100 - (u.getStartrating() + ratingbij)));
			logger.log(Level.INFO, "max(100 - newrating, 0) : " + Math.max(0, (100 - (u.getStartrating() + ratingbij))));
			logger.log(Level.INFO, "ratingbij + max(100 - newrating, 0) : " + (ratingbij + Math.max(0, (100 - (u.getStartrating() + ratingbij)))));
			ratingbij = ratingbij + (Math.max(100 - (u.getStartrating() + ratingbij),0));
			String bijofaf = ratingbij>0 ? "+" : "-";
			//int startrating = updateSpeler.getStartrating();
			logger.log(Level.INFO, u.getNaam() + "|" + puntenbij/10 + "|" + u.getStartrating() + bijofaf + Math.abs(ratingbij) + " naar " + (u.getStartrating() + ratingbij));
			//
			//u.setStartrating(speler.getRating());
			//updateSpeler.setPunten(puntenbij);
			logger.log(Level.INFO, "spelerWedstrijden aantal :" + spelerWedstrijden.size());
			u.setWedstrijden(spelerWedstrijden);

			u.setDeltarating(ratingbij);
			return u;
		}
	}
