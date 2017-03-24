package nl.detoren.ijsco.data;

import java.util.ArrayList;
import java.util.HashMap;

public class Status {
	/**
	 * Spelers zoals bekend bij de OSBO
	 */
	public HashMap<Integer, Speler> OSBOSpelers;
	/**
	 * Deelnemers aan het toernooi
	 */
	public Deelnemers deelnemers;
	/**
	 * Mogelijke speelschemas
	 * Schema is aantal groepen, spelers per groep (variabel)
	 * en aantal byes
	 */
	public Schemas schemas;
	/**
	 * Mogelijke groepsindelingen. Alle mogelijkheden
	 * hebben gelijk aantal groepen en aantal spelers per
	 * groep, maar verdeling byes over groepen verschilt
	 */
	public ArrayList<Groepen> mogelijkheden;

	public int minGroepen = 1;
	public int maxGroepen = 24;
	public int minSpelers = 4;
	public int maxSpelers = 10;
	public int minDeltaSpelers = 0;
	public int maxDeltaSpelers = 2;
	public int minAfwijkendeGroepen = 0;
	public int maxAfwijkendeGroepen = 2;
	public int minToegestaneByes = 0;
	public int maxToegestaneByes = 3;

}
