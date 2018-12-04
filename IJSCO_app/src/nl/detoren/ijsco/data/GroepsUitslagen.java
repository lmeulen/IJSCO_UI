package nl.detoren.ijsco.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

public class GroepsUitslagen implements Iterable<GroepsUitslag>{
	private List<GroepsUitslag> groepsuitslagen;
	private int aantal;

	@SuppressWarnings("unused")
	public GroepsUitslagen() {
		groepsuitslagen = new ArrayList<GroepsUitslag>();
		aantal = 0;
	}

	@Override
	public Iterator<GroepsUitslag> iterator() {
		return (groepsuitslagen).iterator();
	}

	public void Add(GroepsUitslag groepsuitslag) {
		this.aantal++;
		groepsuitslagen.add(groepsuitslag);
		
	}

	public int AantalGroepsuitslagen() {
		return aantal;
	}

	public void addGroepsUitslag(GroepsUitslag uitslag) {
		this.aantal++;
		groepsuitslagen.add(uitslag);
		
	}
	
	public String ToString() {
		String output = "";
		for (GroepsUitslag g : groepsuitslagen) {
			output += "\r\nResultaat Groep " + g.getGroepsnaam();
			//for (UitslagSpeler u : g.getSpelers()) {


			// new
	        Map<Integer, UitslagSpeler> sortedMap = sortByValue(g.getSpelers());
			for (Map.Entry<Integer, UitslagSpeler> m : sortedMap.entrySet()) {
				UitslagSpeler u = m.getValue();
				String bijofaf = u.getDeltarating()>0 ? " + " : " - ";
				output += "\r\n";
				String vereniging = u.getVereniging();
				if (vereniging==null) {
					vereniging = "-";
				}
				String geboortejaar;
				if (u.getGeboortejaar()==0) {
					geboortejaar = "-";
				} else
				{
					geboortejaar = String.format("%1$4s", u.getGeboortejaar());
				}
				
				output += String.format("%10s|%25s|%4s|%25S|%.1f|%5s%3s%3s%3s%5s", u.getRang(), u.getNaam(), geboortejaar, vereniging, ((float)u.getPunten())/10, u.getStartrating(), bijofaf, Math.abs(u.getDeltarating())," = ", (u.getStartrating()+u.getDeltarating())); 
			}
		}
		return output;
				
	}

    private static Map<Integer, UitslagSpeler> sortByValue(Map<Integer, UitslagSpeler> unsortMap) {

        // 1. Convert Map to List of Map
        List<Map.Entry<Integer, UitslagSpeler>> list =
                new LinkedList<Map.Entry<Integer, UitslagSpeler>>(unsortMap.entrySet());

        // 2. Sort list with Collections.sort(), provide a custom Comparator
        //    Try switch the o1 o2 position for a different order
        Collections.sort(list, new Comparator<Map.Entry<Integer, UitslagSpeler>>() {
            public int compare(Map.Entry<Integer, UitslagSpeler> o1,
                               Map.Entry<Integer, UitslagSpeler> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        // 3. Loop the sorted list and put it into a new insertion order Map LinkedHashMap
        Map<Integer, UitslagSpeler> sortedMap = new LinkedHashMap<Integer, UitslagSpeler>();
        for (Entry<Integer, UitslagSpeler> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        /*
        //classic iterator example
        for (Iterator<Map.Entry<String, Integer>> it = list.iterator(); it.hasNext(); ) {
            Map.Entry<String, Integer> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }*/


        return sortedMap;
    }

	public void export() {
		// TODO Auto-generated method stub
		
	}

}

