package nl.detoren.ijsco.data;

import java.util.Comparator;
import java.util.logging.Logger;
import nl.detoren.ijsco.ui.control.IJSCOController;

public class SpelerIndeling {

	public SpelerIndeling(Speler s, String n, String cr1, String cr2, String cr3) {
		speler = s;
		groep = n;
		celreferentie1 = cr1;
		celreferentie2 = cr2;
		celreferentie3 = cr3;
	}
		// region Static
		private final static Logger logger = Logger.getLogger(IJSCOController.class.getName());
		// endregion

		// region Properties
		private Speler speler;
		private String sheetname;
		private String groep;
		private String celreferentie1;
		private String celreferentie2;
		private String celreferentie3;
		private String celreferentie4;
		// endregion Properties

		/**
		 * Geef speler van spelerindeling
		 * @return
		 */
		public Speler getSpeler() {
			return speler;
		}

		/**
		 * Geef sheetnaam van spelerindeling
		 * @return
		 */
		public String getSheetname() {
			return sheetname;
		}

		/**
		 * Geef groepnaam van spelerindeling
		 * @return
		 */
		public String getGroep() {
			return groep;
		}

		/**
		 * Geef celreferentie 1 van spelerindeling
		 * @return
		 */
		public String getcr1() {
			return celreferentie1;
		}

		/**
		 * Geef celreferentie 2 van spelerindeling
		 * @return
		 */
		public String getcr2() {
			return celreferentie2;
		}

		/**
		 * Geef celreferentie 3 van spelerindeling
		 * @return
		 */
		public String getcr3() {
			return celreferentie3;
		}

		/*Comparator for sorting the list by Speler Name*/
	    public static Comparator<SpelerIndeling> NaamComparator = new Comparator<SpelerIndeling>() {

		public int compare(SpelerIndeling s1, SpelerIndeling s2) {
		   String Naam1 = s1.speler.getNaam().toUpperCase();
		   String Naam2 = s2.speler.getNaam().toUpperCase();

		   //ascending order
		   return Naam1.compareTo(Naam2);

		   //descending order
		   //return Naam2.compareTo(Naam1);
	    }};

		/*Comparator for sorting the list by Speler Rating*/
	    public static Comparator<SpelerIndeling> RatingComparator = new Comparator<SpelerIndeling>() {

		public int compare(SpelerIndeling s1, SpelerIndeling s2) {
		   Integer Rating1 = s1.speler.getRating();
		   Integer Rating2 = s2.speler.getRating();

		   //ascending order
		   //return Rating1.compareTo(Rating2);

		   //descending order
		   return Rating2.compareTo(Rating1);
	    }};

}
