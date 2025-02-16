package nl.detoren.ijsco.data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroepsUitslag {

	private Map<Integer, UitslagSpeler> spelers = new HashMap<Integer, UitslagSpeler>();
	private int aantalspelers;
	private int aantalspelersmetrating;
	private List<WedstrijdUitslag> wedstrijden = new ArrayList<WedstrijdUitslag>();
	private String groepsnaam;
	
	public GroepsUitslag(int aantal, String naam) {
		if (aantal > 0) {
			this.aantalspelers = aantal;
		} else {
			this.aantalspelers = 0;
		}
		this.aantalspelersmetrating = 0;
		this.groepsnaam = naam;
	}

	public GroepsUitslag() {
		aantalspelers = 0;
		groepsnaam = "";
		aantalspelersmetrating = 0;
	}

	public void addSpeler(UitslagSpeler speler) {
		if (speler.getStartrating()>1) this.setAantalMetRatingPlus1();
		spelers.put(speler.getId(),speler);	
	}

	public void setAantalMetRatingPlus1() {
		this.setAantalspelersmetrating(this.getAantalspelersmetrating() + 1);
	}

	public Map<Integer, UitslagSpeler> getSpelers() {
		//return spelers.sort(Comparator.comparing(UitslagSpeler::getPunten).reversed());;
		//Comparator<UitslagSpeler> s = Comparator.comparing(UitslagSpeler::getPunten).reversed();
		//spelers.sort(s);
		return spelers;
	}

	public UitslagSpeler getSpelerById(int i) {
		return (i <= aantalspelers) ? spelers.get(i) : null;
	}
	
	public String getGroepsnaam() {
		return groepsnaam;
	}

	public void setGroepsnaam(String naam) {
		groepsnaam = naam;
	}

	public void addWedstrijd(WedstrijdUitslag w) {
		wedstrijden.add(w);
	}

	public List<WedstrijdUitslag> getWedstrijden() {
		return this.wedstrijden;
	}

	public String wedstrijdentoString() {
		String result = groepsnaam + " : ";
		try {
			for (WedstrijdUitslag w : this.wedstrijden) {
				result += "\r\n" + w.toString();
			}
		}
		catch (Exception ex) {
			
		}
		return result;
	}

	public int getAantalspelersmetrating() {
		return aantalspelersmetrating;
	}

	public void setAantalspelersmetrating(int aantalspelersmetrating) {
		this.aantalspelersmetrating = aantalspelersmetrating;
	}

	public int getAantal() {
		// TODO Auto-generated method stub
		return aantalspelers;
	}

	public void setAantal(int i) {
		this.aantalspelers--;	
	}
	
}
