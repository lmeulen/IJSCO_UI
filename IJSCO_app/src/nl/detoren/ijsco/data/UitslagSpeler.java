package nl.detoren.ijsco.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UitslagSpeler implements Comparable{

	private int startRang;
	private int rang;
	private int uitslagspelerid;
	private String naam;
	private int geboortejaar;
	private String geslacht;
	private String categorie;
	private String[] vereniging;
	private int punten; // punten is wedstrijdpunten * 10 
	private int wp; // WP is WP *10
	private int sb; // SB is SB * 100
	private int knsbnummer;
	private int startrating;
	private int deltarating;
	private List<WedstrijdUitslag> wedstrijden;
	
	
	public UitslagSpeler(Speler speler) {
		rang=0;
		punten = 0;
		wp = 0;
		sb = 0 ;
		startrating = 0;
		deltarating = 0;
		naam = speler.getNaam();
		geslacht = speler.getGeslacht();
		geboortejaar = speler.getGeboortejaar();
		vereniging[0] = speler.getVereniging();
	}

	public UitslagSpeler() {
		// TODO Auto-generated constructor stub
	}

	public void setRang (int rang) {
		this.rang = rang;
	}
	
	public int getRang() {
		return rang;
	}

	public void setStartRang(Integer startRang) {
		this.startRang = startRang;
	}
	
	public int getStartRang() {
		return startRang;
	}

	public void setId (int id) {
		this.uitslagspelerid = id;
	}
	
	public int getId() {
		return uitslagspelerid;
	}

	public void setStartrating (int startrating) {
		this.startrating = startrating;
	}
	
	public int getStartrating() {
		return startrating;
	}

	public void setDeltarating (int deltarating) {
		this.deltarating = deltarating;
	}

	public int getDeltarating() {
		return deltarating;
	}

	public int getPunten() {
		return punten;
	}

	public void setPunten(int punten) {
		this.punten = punten;
	}

	public int getWP() {
		return wp;
	}

	public void setWP(int wp) {
		this.wp = wp;
	}

	public int getSB() {
		return sb;
	}

	public void setSB(int sb) {
		this.sb = sb;
	}
	public String getNaam() {
		return naam;
	}

	public void setKNSBnummer (int knsbnummer) {
		this.knsbnummer = knsbnummer;
	}
	
	public int getKNSBnummer() {
		return knsbnummer;
	}

	public void setNaam(String spelersnaam) {
		naam = spelersnaam;
	}

	public String getVereniging() {
		if (vereniging == null) {
			vereniging = new String[] { "" };
		}
		return vereniging[0];
	}

	public void setVereniging(String naamvereniging) {
		if (naamvereniging == null) {
			return;
		}
		ArrayList<String> aL = new ArrayList<String>(Arrays.asList(vereniging));
		aL.add(naamvereniging);
		vereniging = (String[]) aL.toArray();
	}

	public int getGeboortejaar() {
		return geboortejaar;
	}

	public void setGeboortejaar(int jaar) {
		geboortejaar = jaar;
	}

	public String getGeslacht() {
		return geslacht;
	}

	public void setGeslacht(String geslacht) {
		this.geslacht = geslacht;
	}

	public String getCategorie() {
		return categorie;
	}

	public void setCategorie(String categorie) {
		this.categorie = categorie;
	}

	public List<WedstrijdUitslag> getWedstrijden() {
		return wedstrijden;
	}

	public void setWedstrijden(List<WedstrijdUitslag> spelerWedstrijden) {
		this.wedstrijden = spelerWedstrijden;
	}

	public String toFormattedString() {
			String result = "";
			result += String.format("%1$10d | %2$-34s", knsbnummer, this.getNaam());
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
