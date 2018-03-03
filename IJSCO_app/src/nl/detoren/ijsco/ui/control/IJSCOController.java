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
package nl.detoren.ijsco.ui.control;

import java.util.logging.Level;
import java.util.logging.Logger;

import nl.de.toren.ijsco.Configuratie;

public class IJSCOController {

    private static volatile IJSCOController instance = null;

    private final static Logger logger = Logger.getLogger(IJSCOController.class.getName());

    private static final String defaultInputfile = "uitslag.txt";

    private Status status;
    private Configuratie c;
    
}
