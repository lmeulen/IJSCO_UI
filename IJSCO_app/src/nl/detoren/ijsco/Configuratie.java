/**
 * Copyright (C) 2018 Lars Dam
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
package nl.detoren.ijsco;

public class Configuratie {

	public Configuratie() {
		
	}
		/**
		 * No Byes Mask
		 * Mask for groups that may not have a bye
		 */
		public int minGroepen = 0;
		public int maxGroepen = 10;
		public int minSpelers = 4;
		public int maxSpelers = 10;
		public int minDeltaSpelers = 2;
		public int maxDeltaSpelers = 2;
		public int minAfwijkendeGroepen = 1;
		public int maxAfwijkendeGroepen = 4;
		public int minToegestaneByes = 1;
		public int maxToegestaneByes = 2;
		public int nobyesmask = 1 + 2;
		
		/**
		 * Bestandsnaam voor configuratie bestand prefix .json wordt automatisch
		 * toegevoegd
		 */
		public String configuratieBestand = "configuratie";

		/**
		 * Bestandsnaam voor status bestand prefix .json )en evt datum postfix)
		 * wordt automatisch toegevoegd
		 */
		public String statusBestand = "status";

}
