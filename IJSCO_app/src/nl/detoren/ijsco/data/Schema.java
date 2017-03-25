package nl.detoren.ijsco.data;

public class Schema {
	private int groepen;
	private int byes;
	private int[] groepsgroottes;

	public Schema() {
		this(0, 0, new int[] {0});
	}

	public Schema(int groepen, int byes, int[] groottes) {
		super();
		this.groepen = groepen;
		this.byes = byes;
		groepsgroottes = new int[groottes.length];
		for (int i = 0; i < groottes.length; ++i) {
			groepsgroottes[i] = groottes[i];
		}
	}

	public String toString() {
//		String result = String.format("n=%3d, b=%1d : [ ", groepen, byes);
		String result = String.format("%02d(%1d) [ ", groepen, byes);
		for (int val : groepsgroottes) {
			result += String.format("%02d ", val);
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

	@Override
	public boolean equals(Object arg0) {
		if (arg0 instanceof Schema) {
			Schema s2 = (Schema) arg0;
			if ((this.byes != s2.byes) || (this.groepen != s2.groepen) ||
					(this.groepsgroottes.length != s2.groepsgroottes.length) ) {
				return false;
			}
			for (int i = 0; i < groepsgroottes.length; i++) {
				if (this.groepsgroottes[i] != s2.groepsgroottes[i]) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

}
