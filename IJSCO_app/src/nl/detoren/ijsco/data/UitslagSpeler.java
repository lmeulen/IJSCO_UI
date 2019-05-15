package nl.detoren.ijsco.data;

import java.util.ArrayList;
import java.util.Arrays;

public class UitslagSpeler implements Comparable{

	private int _rang;
	private int _uitslagspelerid;
	private String _naam;
	private String _voornaam;
	private String _voorletters;
	private String _tussenvoegsel;
	private String _achternaam;
	private int _geboortejaar;
	private String _categorie;
	private String[] _vereniging;
	private int _punten; // punten is wedstrijdpunten * 10 
	private int _wp; // WP is WP *10
	private int _sb; // SB is SB * 100
	private int _knsbnummer;
	private int _startrating;
	private int _deltarating;
	
	
	public UitslagSpeler(Speler speler) {
		_rang=0;
		_punten = 0;
		_wp = 0;
		_sb = 0 ;
		_startrating = 0;
		_deltarating = 0;
		_naam = speler.getNaam();
		_geboortejaar = 0;
		_vereniging[0] = speler.getVereniging();
	}

	public UitslagSpeler() {
		// TODO Auto-generated constructor stub
	}

	public void setRang (int rang) {
		this._rang = rang;
	}
	
	public int getRang() {
		return _rang;
	}

	public void setId (int id) {
		this._uitslagspelerid = id;
	}
	
	public int getId() {
		return _uitslagspelerid;
	}

	public void setStartrating (int startrating) {
		this._startrating = startrating;
	}
	
	public int getStartrating() {
		return _startrating;
	}

	public void setDeltarating (int deltarating) {
		this._deltarating = deltarating;
	}

	public int getDeltarating() {
		return _deltarating;
	}

	public int getPunten() {
		return _punten;
	}

	public void setPunten(int punten) {
		this._punten = punten;
	}

	public int getWP() {
		return _wp;
	}

	public void setWP(int wp) {
		this._wp = wp;
	}

	public int getSB() {
		return _sb;
	}

	public void setSB(int sb) {
		this._sb = sb;
	}
	public String getNaam() {
		return _naam;
	}

	public String getVoornaam() {
		return _voornaam;
	}

	public String setVoorletters() {
		return _voorletters;
	}

	public String getTussenvoegsel() {
		return _tussenvoegsel;
	}

	public String getAchternaam() {
		return _achternaam;
	}

	public void setKNSBnummer (int knsbnummer) {
		this._knsbnummer = knsbnummer;
	}
	
	public int getKNSBnummer() {
		return _knsbnummer;
	}

	public void setNaam(String naam) {
		_naam = naam;
	}

	public void setVoornaam(String voornaam) {
		_voornaam = voornaam;
	}

	public void setVoorletters(String voorletters) {
		_voorletters = voorletters;
	}

	public void setTussenvoegsel(String tussenvoegsel) {
		_tussenvoegsel = tussenvoegsel;
	}

	public void setAchternaam(String achternaam) {
		_achternaam = achternaam;
	}

	public String getVereniging() {
		if (_vereniging == null) {
			_vereniging = new String[] { "" };
		}
		return _vereniging[0];
	}

	public void setVereniging(String naamvereniging) {
		if (naamvereniging == null) {
			return;
		}
		ArrayList<String> aL = new ArrayList<String>(Arrays.asList(_vereniging));
		aL.add(naamvereniging);
		_vereniging = (String[]) aL.toArray();
	}

	public int getGeboortejaar() {
		return _geboortejaar;
	}

	public void setGeboortejaar(int jaar) {
		_geboortejaar = jaar;
	}

	public String getCategorie() {
		return _categorie;
	}

	public void setCategorie(String categorie) {
		this._categorie = categorie;
	}

	public String toFormattedString() {
			String result = "";
			result += String.format("%1$10d | %2$-34s", _knsbnummer, this.getNaam());
//			result += String.format("%1$10d | %2$34s | %3$6d | %4$6d | %5$6d | %6$6d | %7$6d", knsbnummer, this.getNaam(), this.getVereniging(), this.getGeboortejaar(), this.getPunten()/10, this.getSB()/100, this.getStartrating());
//			result += String.format("%1 10d | %2$20s | %3 6d", knsbnummer, this.getNaam(), this.getStartrating());
			return result;
		}
		    
	    /**
	     * Wordt dezelfde speler gerepresenteerd door het andere object?
	     * @param speler
	     * @return
	     */
	public boolean gelijkAan(UitslagSpeler speler) {
	        return (this.getNaam().equals(speler.getNaam())
//	                && this.getInitialen().equals(speler.getInitialen())
//	                && this.getGroep() == speler.getGroep()
	        		&& this.getNaam() == speler.getNaam()
	                && this.getKNSBnummer() == speler.getKNSBnummer());
	    }

//		//@Override
//		public int compareTo(UitslagSpeler that) {
//			    int result = 0;
//			    result = getRang().compareTo(that.getRang());
//			    return result;
//			  }

		@Override
		public int compareTo(Object o) {
		    if (getRang() < ((UitslagSpeler) o).getRang()){
		    	return -1;
		    }
		    if (getRang() > ((UitslagSpeler) o).getRang()){
		    	return 1;
		    }
		    return 0;
		   		  }
}
