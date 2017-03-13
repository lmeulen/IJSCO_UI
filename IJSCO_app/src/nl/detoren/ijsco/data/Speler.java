package nl.detoren.ijsco.data;

public class Speler {

	private int knsbnummer;
	private String naamKNSB;
	private String naamHandmatig;
	private int ratingIJSCO;
	private int ratingKNSB;
	private int ratingHandmatig;

	public Speler() {
		knsbnummer = 0;
		naamKNSB = null;
		naamHandmatig = null;
		ratingIJSCO = -1;
		ratingKNSB = -1;
		ratingHandmatig = -1;
	}

	public Speler(int knsbnummer, String naamKNSB, int ratingIJSCO, int ratingKNSB) {
		this.knsbnummer = knsbnummer;
		this.naamKNSB = naamKNSB;
		this.naamHandmatig = null;
		this.ratingIJSCO = ratingIJSCO;
		this.ratingKNSB = ratingKNSB;
		this.ratingHandmatig = -1;
	}

	/**
	 * Geef rating van de speler. Prioriteit in rating: 1. IJSCO rating 2.
	 * Handmatig ingestelde rating 3. KNSB rating 4. 100
	 *
	 * @return Rating van de speler
	 */
	public int getRating() {
		if (ratingIJSCO > 0) {
			return ratingIJSCO;
		} else if (ratingHandmatig > 0) {
			return ratingHandmatig;
		} else if (ratingKNSB > 0) {
			return ratingKNSB;
		} else {
			return 100;
		}
	}

	/**
	 * Geef naam van de speler. Prioriteit: 1. Naam KNSB 2. Handmatig ingestelde
	 * naam 3. "???"
	 *
	 * @return
	 */
	public String getNaam() {
		if (naamKNSB != null && naamKNSB.length() > 0) {
			return naamKNSB;
		} else if (naamHandmatig != null && naamHandmatig.length() > 0) {
			return naamHandmatig;
		} else {
			return "???";
		}
	}

	public int getKnsbnummer() {
		return knsbnummer;
	}

	public void setKnsbnummer(int knsbnummer) {
		this.knsbnummer = knsbnummer;
	}

	public String getNaamKNSB() {
		return naamKNSB;
	}

	public void setNaamKNSB(String naam) {
		this.naamKNSB = naam;
	}

	public String getNaamHandmatig() {
		return naamHandmatig;
	}

	public void setNaamHandmatig(String naamHandmatig) {
		this.naamHandmatig = naamHandmatig;
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

	public String toString() {
		String result = "";
		result += knsbnummer + " - ";
		result += getNaam() + " [" + getNaamHandmatig() + " / " + getNaamKNSB() + " ] - ";
		result += getRating() + " [HM:" + getRatingHandmatig() + ", OSBO:" + getRatingIJSCO() + ", KNSB:"
				+ getRatingKNSB() + "]";
		return result;
	}
}
