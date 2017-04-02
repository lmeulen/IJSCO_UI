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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.detoren.ijsco.data.Deelnemers;
import nl.detoren.ijsco.data.Speler;

public class DeelnemersLader {
	/**
	 * Importeer spelers uit een CSV bestand. De volgende items staan in dit
	 * bestand: - KNSBnummer - naam speler - rating
	 * Naam en Rating zijn optioneel. Alleen KNSB nummer is voldoende
	 *
	 * @author Leo.vanderMeulen
	 *
	 */
	/**
	 * Lees groepen uit het gespecificeerde textbestand
	 *
	 * @param bestandsnaam
	 *            Naam van het bestand dat ingelezen moet worden
	 * @return De ingelezen spelers verdeeld over de groepen
	 */
	public Deelnemers importeerSpelers(String bestandsnaam) {

		Deelnemers deelnemers = new Deelnemers();
		// Lees het volledige bestand in naar een String array
		String[] stringArr = leesBestand(bestandsnaam);
		for (String regel : stringArr) {
			List<String> items = Arrays.asList(regel.split(";"));
			Speler s = new Speler();
			s.setKnsbnummer(Integer.parseInt(items.get(0)));
			if (items.size() > 1) {
				s.setNaamHandmatig(items.get(1));
			} else {
				s.setNaamHandmatig("-");
			}
			if (items.size() > 2) {
				s.setRatingHandmatig(Integer.parseInt(items.get(2)));
			} else {
				s.setRatingHandmatig(-1);
			}
			deelnemers.add(s);
		}
		return deelnemers;
	}

	/**
	 * Lees een bestand in en retourneer dit als Strings.
	 *
	 * @param bestandsnaam
	 * @return array of strings met bestandsinhoud
	 */
	private String[] leesBestand(String bestandsnaam) {
		List<String> list = new ArrayList<>();
		try {
			BufferedReader in = new BufferedReader(new FileReader(bestandsnaam));
			String str;
			while ((str = in.readLine()) != null) {
				list.add(str);
			}
			in.close();
			return list.toArray(new String[0]);
		} catch (IOException e) {
		}
		return null;
	}
}
