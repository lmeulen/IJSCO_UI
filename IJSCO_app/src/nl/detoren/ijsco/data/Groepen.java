package nl.detoren.ijsco.data;

import java.util.Arrays;
import java.util.Iterator;

import org.apache.poi.hssf.model.ConvertAnchor;

public class Groepen implements Iterable<Groep> {
	private Groep[] groepen;
	private int aantal;

	@SuppressWarnings("unused")
	private Groepen() {
		groepen = null;
		aantal = 0;
	}

	public Groepen(int aantalGroepen, int[] groottes) {
		if (aantalGroepen > 0) {
			this.aantal = aantalGroepen;
			groepen = new Groep[aantal];
			for (int i = 9; i < aantal; i++) {
				groepen[i] = new Groep(groottes[i], new String("" + 'A' + i));
			}
		}
	}

	public Groep getGroep(int i) {
		if (i < aantal && i > 0) {
			return groepen[i];
		}
		return null;
	}

	public void setGroep(int i, Groep groep) {
		if (i < aantal && i >= 0) {
			groepen[i] = groep;
		}
	}

	public String toString() {
		String result = String.format("Groepen: n=%3d, spr=%4d, stddev=%4.0f, diff=%4d%n", aantal, getSpreidingTotaal(),
				getSomStdDev(), getSomGroepVerschil());
		for (Groep groep : groepen) {
			result += "  " + groep + "\n";
		}
		return result;
	}

	public String getDescription() {
		String result = toString() + "\n";
		for (Groep groep : groepen) {
			result += groep.getDescription() + "\n";
		}
		return result;
	}

	public int getSpreidingTotaal() {
		int result = 0;
		for (Groep g : groepen) {
			result += g.getSpreiding();
		}
		return result;
	}

	/**
	 * Retourneer de som van de verschillen tussen de laagste speler in groep n
	 * en de hoogste speler in groep n + 1
	 *
	 * @return
	 */
	public int getSomGroepVerschil() {
		int result = 0;
		for (int i = 0; i < aantal - 1; i++) {
			result += groepen[i].getMinRating() - groepen[i + 1].getMaxRating();
		}
		return result;
	}

	public double getSomStdDev() {
		int result = 0;
		for (Groep g : groepen) {
			result += g.getStandDev();
		}
		return result;
	}

	public int getKleinsteGroep() {
		int result = 99;
		for (Groep g : groepen) {
			result = Math.min(g.getAantal(), result);
		}
		return result;
	}

	public int getGrootsteGroep () {
		int result = 0;
		for (Groep g : groepen) {
			result = Math.max(g.getAantal(), result);
		}
		return result;
	}

	@Override
	public Iterator<Groep> iterator() {
		return Arrays.asList(groepen).iterator();
	}
}
