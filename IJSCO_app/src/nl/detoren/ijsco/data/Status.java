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
	 * Geselecteerde schema
	 */
	public Schema schema;

	/**
	 * Ingedeelde groepen
	 */
	public Groepen groepen;
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
