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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import nl.detoren.ijsco.io.OSBOLoader;

public class Spelers implements Collection<Speler> {


	private final static Logger logger = Logger.getLogger(Spelers.class.getName());

	private ArrayList<Speler> deelnemers;

	public Spelers() {
		deelnemers = new ArrayList<>();
	}

	@Override
	public boolean add(Speler nieuw) {
		if (nieuw != null) {
			// Bestaande speler verwijderen
			for (int i = 0; i < deelnemers.size(); i++) {
				if (deelnemers.get(i).getKnsbnummer() == nieuw.getKnsbnummer() && nieuw.getKnsbnummer() != -1) {
					deelnemers.remove(i);
					break;
				}
			}
			deelnemers.add(nieuw);
			sort();
			return true;
		}
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends Speler> arg0) {
		if (arg0 != null) {
			for (Speler speler : arg0) {
				add(speler);
			}
			sort();
			return true;
		}
		return false;
	}
	
	public boolean addJSON(JSONArray json) {
			for (Object l : json){
				if (l instanceof JSONObject) {
					Object o;
					JSONObject s = null;
					Speler speler;
					try {
						logger.log(Level.INFO, "JSONObject" + l.toString());
						 s = (JSONObject) l; // Cast
						 speler= new Speler();
					} catch (Exception e) {
						logger.log(Level.WARNING, "Problem with JSONObject while parsing Speler data. Skipping data for this Speler.");
						continue;
					}
					try {
						o = s.get("knsbnummer");
						long k = (long) o;
						speler.setKnsbnummer((int) k);
						logger.log(Level.INFO, String.format("Speler heeft KNSBnummer: %s",speler.getKnsbnummer()));
					} catch (Exception e) {
						logger.log(Level.WARNING, "Problem with knsbnummer while parsing Speler data. Skipping data for this Speler.");
						continue;
					}						
					String samengestelde_naam = "";
					try{
						String voornaam = (String) s.get("voornaam");
						String tussenvoegsel = (String) s.get("tussenvoegsel");
						String achternaam = (String) s.get("achternaam");
						samengestelde_naam = voornaam;
						if (tussenvoegsel != null && !tussenvoegsel.trim().isEmpty()) samengestelde_naam += " " + tussenvoegsel;
						if (achternaam != null && !achternaam.trim().isEmpty()) samengestelde_naam += " " + achternaam;
						if (samengestelde_naam!=null) {
							speler.setNaamHandmatig(samengestelde_naam);
							logger.log(Level.INFO, String.format("Speler heeft samengestelde naam: %s",samengestelde_naam));
						} else {
							logger.log(Level.INFO, String.format("samengestelde naam is NULL"));
						}
					} catch (Exception e) {
						logger.log(Level.INFO, "Problem with voornaam/tussenvoegsel/achternaam while parsing Speler data. Continuing without samengestelde naam.");
					}
					try {
						o = s.get("naamKNSB");
						String k = (String) o;
						speler.setNaamKNSB((String) o);
						logger.log(Level.INFO, String.format("Speler heeft naamKNSB: %s", (String) o));
					} catch (NullPointerException ne) {
						logger.log(Level.WARNING, "`No naamKNSB found while parsing Speler data.");
						continue;
					} catch (Exception e) {
						logger.log(Level.WARNING, "Problem with naamKNSB while parsing Speler data. Continuing without naamKNSB.");
						continue;
					}
					try {
						o = s.get("naamHandmatig");
						String k = (String) o;
						speler.setNaamHandmatig((String) o);
						logger.log(Level.INFO, String.format("Speler heeft naamHandmatig : %s", (String) o));
					} catch (NullPointerException ne) {
						logger.log(Level.WARNING, "`No naamHandmatig found while parsing Speler data.");
						continue;
					} catch (Exception e) {
						logger.log(Level.WARNING, "Problem with naamHandmatig while parsing Speler data. Continuing without naamHandmatig.");
						continue;
					}						
					String bs;
					try {						
						o = s.get("aanwezig");
						boolean g = (boolean) o;
						speler.setAanwezig(g);
						if (g) bs = "true"; else bs = "false";
						logger.log(Level.INFO, "Aanwezig(heid) van " + speler.getNaam() + " is " + bs);
					} catch (Exception e) {
						logger.log(Level.INFO, "Problem with Aanwezigheid parsing Speler data. Continuing without Aanwezigheid .");												
					}
					try {						
						o = s.get("overruleNaam");
						boolean g = (boolean) o;
						speler.setOverruleNaam(g);
						if (g) bs = "true"; else bs = "false";
						logger.log(Level.INFO, "OverruleNaam van " + speler.getNaam() + " is " + bs);
					} catch (Exception e) {
						logger.log(Level.INFO, "Problem with OverruleNaamparsing Speler data. Continuing without OverruleNaam.");												
					}
					try {						
						o = s.get("overruleRating");
						boolean g = (boolean) o;
						speler.setOverruleRating(g);
						if (g) bs = "true"; else bs = "false";
						logger.log(Level.INFO, "OverruleRating van " + speler.getNaam() + " is " + bs);
					} catch (Exception e) {
						logger.log(Level.INFO, "Problem with OverruleRating Speler data. Continuing without OverruleRating.");												
					}
					try {
						o = s.get("vereniging");
						String k = (String) o;
						speler.setVereniging(k);
						logger.log(Level.INFO, String.format("Speler heeft Verenging(en): %s", (String) k));
					} catch (NullPointerException ne) {
						logger.log(Level.WARNING, "`No vereniging(en) found while parsing Speler data.");
						continue;
					} catch (Exception e) {
						logger.log(Level.WARNING, "Problem with vereniging(en) while parsing Speler data. Continuing without vereniging(en).");
						continue;
					}						
					try {						
						o = s.get("geboortejaar");
						long g = (long) o;
						speler.setGeboortejaar((int) g);
						logger.log(Level.INFO, "Geboortejaar van " + speler.getNaam() + " is " + speler.getGeboortejaar());
					} catch (Exception e) {
						logger.log(Level.INFO, "Problem with Geboortejaar while parsing Speler data. Continuing without Geboortejaar.");												
					}
					try {
						String geslacht = (String) s.get("geslacht");
						speler.setGeslacht(geslacht);
						logger.log(Level.INFO, "Geslacht van " + speler.getNaam() + " is " + geslacht);
					} catch (Exception e) {						
						logger.log(Level.INFO, "Problem with Geslacht while parsing Speler data. Continuing without Geslacht.");						
					}
					try {
						o = s.get("rating");
						long r = (long) o;
						speler.setRatingHandmatig((int) r);
						logger.log(Level.INFO, "Rating van " + speler.getNaam() + " is " + speler.getRating());
					} catch (Exception e) {
						logger.log(Level.INFO, "Problem with Rating while parsing Speler data. Continuing without Rating.");
					}
					try {
						speler.bepaalCategorie();
						logger.log(Level.INFO, "Categorie van " + speler.getNaam() + " is " + speler.getCategorie());
					} catch (Exception e) {
						logger.log(Level.INFO, "Problem with Categorie while parsing Speler data. Continuing without Categorie.");						
					}
					try {
						deelnemers.add(speler);
					} catch (Exception e) {
						logger.log(Level.INFO, "Problem with adding speler to deelnemers. Skipping data for this Speler.");						
					}
				}
			}
		return true;
	}

	@Override
	public void clear() {
		deelnemers = new ArrayList<>();
	}

	@Override
	public boolean contains(Object arg0) {
		if (arg0 instanceof Speler && deelnemers != null) {
			for (Speler s : deelnemers) {
				if (s.equals(arg0)) {
					return true;
				}
			}
			return false;
		}
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> arg0) {
		try {
			@SuppressWarnings("unchecked")
			Collection<Speler> spelers = (Collection<Speler>) arg0;
			for (Speler s : spelers) {
				if (!contains(s))
					return false;
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean isEmpty() {
		if (deelnemers != null && deelnemers.size() != 0)
			return true;
		else
			return false;
	}

	@Override
	public Iterator<Speler> iterator() {
		if (deelnemers != null)
			return deelnemers.iterator();
		else
			return null;
	}

	@Override
	public boolean remove(Object arg0) {
		// TODO Auto-generated method stub
		if (arg0 instanceof Speler) {
			deelnemers.remove(arg0);
			return true;
		}
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> arg0) {
		try {
			boolean result = true;
			@SuppressWarnings("unchecked")
			Collection<Speler> spelers = (Collection<Speler>) arg0;
			for (Speler s : spelers) {
				result = result & remove(s);
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean retainAll(Collection<?> arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object[] toArray() {
		if (deelnemers != null)
			return deelnemers.toArray();
		else
			return null;
	}

	@Override
	public int size() {
		return deelnemers != null ? deelnemers.size() : 0;
	}

	public Speler get(int i) {
		if (deelnemers != null && i < deelnemers.size())
			return deelnemers.get(i);
		return null;
	}

	public Speler getByKNSB(int knsb) {
		if (deelnemers != null) {
			for (Speler s : deelnemers) {
				if (s.getKnsbnummer() == knsb) {
					return s;
				}
			}
		}
		return null;
	}

	public Speler getByNaam(String naam) {
		System.out.println("Aantal deelnemers is " + deelnemers.size());
		if (deelnemers != null) {
			for (Speler s : deelnemers) {
				if (s.getNaam().equals("Quinte Heijboer")) {
					System.out.println("s.naam = " + s.getNaam() + "is gelijk aan " + naam + "?");
					System.out.println("s.naam lengte = " + s.getNaam().length());
					System.out.println("naam lengte  = " + naam.length());
				}
//				if (s.getNaam() == naam) {
				if (s.getNaam().equals(naam)) {
					return s;
				}
			}
		}
		System.out.println("GetByNaam returns null");
		return null;
	}

	public int aantalAanwezig() {
		if (deelnemers != null) {
			int aanwezig = 0;
			for (Speler s : deelnemers) {
				aanwezig += (s.isAanwezig() ? 1 : 0);
			}
			return aanwezig;
		}
		return 0;
	}

	@Override
	public <T> T[] toArray(T[] arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public void sort() {
		Collections.sort(deelnemers, new Comparator<Speler>() {
			@Override
			public int compare(Speler arg0, Speler arg1) {
				if (arg0.getRating() > arg1.getRating()) {
					return -1;
				} else if (arg0.getRating() < arg1.getRating()) {
					return 1;
				}
				return 0;
			}
		});
	}

	public Spelers getAanwezigen() {
		Spelers result = new Spelers();
		for (Speler s : deelnemers) {
			if (s.isAanwezig())
				result.add(s);
		}
		return result;
	}
}