/**
 * Copyright (C) 2016 Leo van der Meulen
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation version 3.0
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * See: http://www.gnu.org/licenses/gpl-3.0.html
 *
 * Problemen in deze code:
 */
package nl.detoren.ijsco.io;

import java.io.File;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import nl.detoren.ijsco.data.Deelnemers;
import nl.detoren.ijsco.data.Speler;

/**
 * Laad bekende OSBO spelers in, dit kan automatisch vanaf de website van de
 * OSBO door het jeugdrating bestand in te lezen en door een lokale kopie van
 * dit bestand in te lezen.
 * @author Leo.vanderMeulen
 *
 */
public class OSBOLoader {

	public Deelnemers laadBestand(String bestandsnaam) {
		try {
			// File input = new File("OSBO Jeugd-rating-lijst.htm");
			File input = new File(bestandsnaam);
			Document doc = Jsoup.parse(input, "ISO-8859-9", "http://osbo.nl/jeugd/");
			return load(doc);
		} catch (Exception e) {
			System.out.println("Error loading OSBO spelers " + e.getMessage());
		}
		return null;
	}

	public Deelnemers laadWebsite() {
		try {
			Document doc = Jsoup.connect("http://osbo.nl/jeugd/jrating.htm").get();
			return load(doc);
		} catch (Exception e) {
			System.out.println("Error loading OSBO spelers " + e.getMessage());
		}
		return null;
	}

	private Deelnemers load(Document doc) {
		Deelnemers spelers = new Deelnemers();
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
				spelers.add(s);
			}
		}
		return spelers;
	}
}
