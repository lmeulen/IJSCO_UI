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

public class Spelers implements Collection<Speler> {

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