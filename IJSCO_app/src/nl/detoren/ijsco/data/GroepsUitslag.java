package nl.detoren.ijsco.data;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroepsUitslag {

	private Map<Integer, UitslagSpeler> spelers = new HashMap<Integer, UitslagSpeler>();
	private int aantal;
	private List<WedstrijdUitslag> wedstrijden = new ArrayList<WedstrijdUitslag>();
	private String groepsnaam;
	
	public GroepsUitslag(int aantal, String naam) {
		if (aantal > 0) {
			this.aantal = aantal;
		} else {
			this.aantal = 0;
		}
		this.groepsnaam = naam;
	}

	public GroepsUitslag() {
		aantal = 0;
		groepsnaam = "";
	}

	public void addSpeler(UitslagSpeler speler) {
		this.setAantal(this.getAantal() + 1);
		spelers.put(speler.getId(),speler);	
	}

	public int getAantal() {
		return aantal;
	}

	public void setAantal(int aantal) {
		this.aantal = aantal;
	}

	public Map<Integer, UitslagSpeler> getSpelers() {
		//return spelers.sort(Comparator.comparing(UitslagSpeler::getPunten).reversed());;
		//Comparator<UitslagSpeler> s = Comparator.comparing(UitslagSpeler::getPunten).reversed();
		//spelers.sort(s);
		return spelers;
	}

	public UitslagSpeler getSpelerById(int i) {
		return (i <= aantal) ? spelers.get(i) : null;
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
	
}
