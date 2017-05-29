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
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import nl.detoren.ijsco.data.Spelers;
import nl.detoren.ijsco.data.Speler;

/**
 * Laad bekende OSBO spelers in, dit kan automatisch vanaf de website van de
 * OSBO door het jeugdrating bestand in te lezen en door een lokale kopie van
 * dit bestand in te lezen.
 * @author Leo.vanderMeulen
 *
 */
public class OSBOLoader {

	public Spelers laadBestand(String bestandsnaam) {
		try {
			// File input = new File("OSBO Jeugd-rating-lijst.htm");
			File input = new File(bestandsnaam);
			//Document doc = Jsoup.parse(input, "ISO-8859-9", "http://osbo.nl/jeugd/");
			//Document doc = Jsoup.parse(input, "UTF-8", "http://osbo.nl/jeugd/");
			Document doc = Jsoup.parse(input, "ISO-8859-1", "http://osbo.nl/jeugd/");
			return load(doc);
		} catch (Exception e) {
			System.out.println("Error loading OSBO spelers " + e.getMessage());
		}
		return null;
	}

	public Spelers laadWebsite() {
		try {
			//Document doc = Jsoup.connect("http://osbo.nl/jeugd/jrating.htm").get();
			String url = "http://osbo.nl/jeugd/jrating.htm";
			Document doc = Jsoup.parse(new URL(url).openStream(), "ISO-8859-9", url);
			return load(doc);
		} catch (Exception e) {
			System.out.println("Error loading OSBO spelers " + e.getMessage());
		}
		return null;
	}

	private Spelers load(Document doc) {
		Spelers spelers = new Spelers();
		int knsbnummer = 0;
		int knsbrating = 0;
		int osborating = 0;
		String naam = "";
		Element table = doc.select("table").first();
		Elements rows = table.select("tr");
		for (Element row : rows) {
			Elements cells = row.select("td");
			if (cells.size() > 7) {
				try {
					naam = cells.get(1).text();
				} catch (Exception e) {
					naam = null;
					System.out.println(e);
				}
				try {
					knsbnummer = Integer.parseInt(cells.get(8).text());
				} catch (Exception e) {
					knsbnummer = 0;
					System.out.println(e);
				}
				try {
					osborating = Integer.parseInt(cells.get(3).text());
				} catch (Exception e) {
					osborating = -1;
					System.out.println(e);
				}
				try {
					knsbrating = Integer.parseInt(cells.get(4).text());
				} catch (Exception e) {
					knsbrating = -1;
					System.out.println(e);
				}

				Speler s = new Speler(knsbnummer, naam, osborating, knsbrating);
				spelers.add(s);
			}
		}
		return spelers;
	}
}
