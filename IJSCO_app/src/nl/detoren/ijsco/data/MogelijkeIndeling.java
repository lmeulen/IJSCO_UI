package nl.detoren.ijsco.data;

public class MogelijkeIndeling {
	private int groepen;
	private int byes;
	private int[] groepsgroottes;

	public MogelijkeIndeling() {
		this(0, 0, null);
	}

	public MogelijkeIndeling(int groepen, int byes, int[] groottes) {
		super();
		this.groepen = groepen;
		this.byes = byes;
		groepsgroottes = new int[groottes.length];
		for (int i = 0; i < groottes.length; ++i) {
			groepsgroottes[i] = groottes[i];
		}
	}

	public String toString() {
		String result = String.format("%3d groepen, %1d byes : [ ", groepen, byes);
		for (int val : groepsgroottes) {
			result += String.format("%2d ", val);
		}
		result += "]";
		return result;
	}

	public int getGroepen() {
		return groepen;
	}

	public void setGroepen(int groepen) {
		this.groepen = groepen;
	}

	public int[] getGroepsgroottes() {
		return groepsgroottes;
	}

	public void setGroepsgroottes(int[] val) {
		groepsgroottes = new int[val.length];
		for (int i = 0; i < val.length; ++i) {
			groepsgroottes[i] = val[i];
		}
	}

	public int getByes() {
		return byes;
	}

	public void setByes(int byes) {
		this.byes = byes;
	}

}
