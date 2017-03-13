package nl.detoren.ijsco.data;

import java.io.File;
import java.util.HashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class OSBOLoader {

	public HashMap<Integer, Speler> load() {
		HashMap<Integer, Speler> spelers = new HashMap<>();
		try {
			File input = new File("OSBO Jeugd-rating-lijst.htm");
			Document doc = Jsoup.parse(input, "ISO-8859-9", "http://osbo.nl/jeugd/");

			//Document doc = Jsoup.connect("http://osbo.nl/jeugd/jrating.htm").get();

			Element table = doc.select("table").first();
			Elements rows = table.select("tr");
			for (Element row : rows) {
				Elements cells = row.select("td");
				if (cells.size() > 7) {
					String naam = cells.get(1).text();
					int knsbnummer = Integer.parseInt(cells.get(8).text());
					int osborating = Integer.parseInt(cells.get(3).text());
					int knsbrating = Integer.parseInt(cells.get(4).text());
					Speler s = new Speler(knsbnummer, naam, osborating, knsbrating);
					spelers.put(knsbnummer, s);
					//System.out.println(knsbnummer + ";" +  naam + ";" + osborating);
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		return spelers;
	}

	public static void main(String[] a) {
		OSBOLoader l = new OSBOLoader();
		l.load();
		DeelnemersLader lader = new DeelnemersLader();
		lader.importeerSpelers("deelnemers.csv");
	}
}
