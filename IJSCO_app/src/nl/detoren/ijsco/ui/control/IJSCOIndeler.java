package nl.detoren.ijsco.ui.control;

import java.util.ArrayList;
import java.util.Iterator;

import nl.detoren.ijsco.data.Deelnemers;
import nl.detoren.ijsco.data.Groep;
import nl.detoren.ijsco.data.Groepen;
import nl.detoren.ijsco.data.Schema;
import nl.detoren.ijsco.data.Schemas;
import nl.detoren.ijsco.data.Speler;
import nl.detoren.ijsco.data.Status;
import nl.detoren.ijsco.io.DeelnemersLader;
import nl.detoren.ijsco.io.OSBOLoader;

public class IJSCOIndeler {

	/**
	 * Retourneer een lijst met alle deelnemers aan het toernooi.
	 *
	 * @return
	 */
	public Deelnemers bepaalDeelnemers() {
		// Lees deelnemers bestand in
		Deelnemers deelnemers = new DeelnemersLader().importeerSpelers("deelnemers.csv");
		// Lees OSBO rating lijst in
		Deelnemers osbolijst = new OSBOLoader().laadBestand("OSBO Jeugd-rating-lijst.htm");
		// Werk spelers bij obv OSBO lijst. OSBO lijst is leidend
		deelnemers = controleerSpelers(deelnemers, osbolijst);
		// Sorteer deelnemers, aflopend. op rating
		deelnemers.sort();
		return deelnemers;
	}

	/**
	 * Controleer deelnemers tegen de OSBO lijst. Obv KNSB nummer is de OSBO
	 * lijst leidend voor naam en rating.
	 *
	 * @param deelnemers Deelnemers
	 * @param osbolijst Osbo lijst
	 * @return bijgewerkte spelerslijst
	 */
	private Deelnemers controleerSpelers(Deelnemers deelnemers, Deelnemers osbolijst) {
		Deelnemers update = new Deelnemers();
		for (Speler s : deelnemers) {
			Speler osbogegevens = osbolijst.getByKNSB(s.getKnsbnummer());
			if (osbogegevens != null) {
				s.setNaamKNSB(osbogegevens.getNaam());
				s.setRatingIJSCO(osbogegevens.getRatingIJSCO());
			}
			update.add(s);
		}
		return update;
	}

	/**
	 * Lees spelers in en maak de groepsindeling
	 */
	public void indelen() {
		Deelnemers deelnemers = bepaalDeelnemers();
		System.out.format("Aantal spelers                       : %3d%n", deelnemers.size());

		Schemas opties = mogelijkeSchemas(deelnemers.size());
		System.out.format("Aantal mogelijke variaties           : %3d%n", opties.size());
		int keuze = 12;
		System.out.format("TO DO: Implementeer keuze, nu        : %3d%n", keuze);
		System.out.format("Gekozen indelingsptroon              : %s%n", opties.get(keuze));

		ArrayList<Groepen> mogelijkheden = mogelijkeGroepen(deelnemers, opties.get(keuze).getGroepen(),
				opties.get(keuze).getGroepsgroottes(), opties.get(keuze).getByes());
		System.out.format("Aantal mogelijkhede groepsindelingen : %3d%n", mogelijkheden.size());
		Groepen groep = bepaalOptimaleGroep(mogelijkheden);
		System.out.println("Beste indeling: " + groep);
	}

	public Groepen bepaalGroep(Schema schema, Deelnemers deelnemers) {
		ArrayList<Groepen> mogelijkheden = mogelijkeGroepen(deelnemers, schema.getGroepen(),
				schema.getGroepsgroottes(), schema.getByes());
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
		for (int i = 0; i <= max; i++) {
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
		Groepen groepen = new Groepen(nGroepen, grootte);
		Iterator<Speler> it = deelnemers.iterator();
		for (int i = 0; i < nGroepen; i++) {
			Groep groep = new Groep(grootte[i], String.format("Groep %2d", i));
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
	 * Creeer een overzicht van alle mogelijke speelgroepen
	 * Regels:
	 * 1. Er spelen 4, 6, 8 of 10 spelers in een groep
	 * 2. De hoogste groepen(maximaal 3) spelen met 2 of 4 spelers minder
	 * 3. De laagste groepen(maximaal 3) spelen met 2 of 4 spelers meer
	 * 4. Minimaal 4 spelers in een groep
	 * 5. Maximaal 10 spelers in een groep
	 * 6. Maximaal 16 groepen
	 * 7. Maximaal 4 byes
	 * @param nSpelers Aantal spelers
	 * @return lijst met mogelijke groepen
	 */
	public Schemas mogelijkeSchemas(int size) {
		Schemas mogelijkheden = new Schemas();
		int[] v_midden = { 4, 6, 8, 10 }; // Basis is 4, 6, 8 of 10 spelers in een groep
		int[] v_hoog = { 2, 4 }; // Hoogste groepen 2 of 4 spelers minder
		int[] v_laag = { 2, 4 }; // Laagste groepen 2 of 4 spelers meer
		for (int n_midden : v_midden) { // itereer of standaard groepsgrootte
			for (int d_hoog : v_hoog) { // itereer of delta (-) groepsgrootte bovenste groepen
				for (int d_laag : v_laag) { // itereer of delta (+) groepsgrootte onderste groepen
					int n_hoog = n_midden - d_hoog; // aantal spelers in bovenste groepen
					int n_laag = n_midden + d_laag; // aantal spelers in onderste groepen
					for (int i = 0; i < 3; i++) { // itereer over aantal (1..2) aan te passen hoogste groepen
						for (int j = 0; j < 3; j++) { // itereer over aantal (1..3) aan te passen onderste groepen
							int size_midden = size - (n_hoog * i) - (n_laag * j); // aantal spelers in standaard groepen
							if (size_midden > 0) {
								int gr_midden = (size_midden / n_midden) + (((size_midden % n_midden) == 0) ? 0 : 1);
								int[] groepen = creeerGroottes(i, n_hoog, gr_midden, n_midden, j, n_laag);
								int byes = bepaalByes(groepen, size);
								if ((n_hoog >= 4) && (n_laag <= 10) && (groepen.length <= 16) && (byes <= 4)) {
									// Rule 1. Minimaal 4 spelers in een groep
									// Rule 2. Maximaal 10 spelers in een groep
									// Rule 3. Maximaal 16 groepen
									// Rule 4. Maximaal 4 byes
									// System.out.println(printArray(groepen,
									// size));
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

	/**
	 * Main start
	 * @param args
	 */
	public static void main(String[] args) {
		IJSCOIndeler indeler = new IJSCOIndeler();
		indeler.indelen();

	}

}
