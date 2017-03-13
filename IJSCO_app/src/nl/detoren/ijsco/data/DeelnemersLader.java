package nl.detoren.ijsco.data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DeelnemersLader {
	/**
	 * Importeer spelers uit een CSV bestand. De volgende items staan in dit
	 * bestand: - KNSBnummer - naam speler - rating
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
	public ArrayList<Speler> importeerSpelers(String bestandsnaam) {

		ArrayList<Speler> deelnemers = new ArrayList<>();
		// Lees het volledige bestand in naar een String array
		String[] stringArr = leesBestand(bestandsnaam);
		for (String regel : stringArr) {
			List<String> items = Arrays.asList(regel.split(";"));
			//List<String> items = Arrays.asList(regel.split("\\s*,\\s*"));
			Speler s = new Speler();
			s.setKnsbnummer(Integer.parseInt(items.get(0)));
			// TODO: Inlezen originele gegevens, zonder aanpassing
			s.setNaamHandmatig(items.get(1) + "(HM)");
			s.setRatingHandmatig(Integer.parseInt(items.get(2)));
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
