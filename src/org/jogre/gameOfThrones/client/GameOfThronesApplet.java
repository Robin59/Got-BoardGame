/*
 * JOGRE (Java Online Gaming Real-time Engine) - GameOfThrones
 * Copyright (C) 2004 - 2014  Robin Giraudon (giraudon.robin@gmail.com)
 * http://jogre.sourceforge.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.jogre.gameOfThrones.client;

import org.jogre.client.awt.JogreTableFrame;
import org.jogre.client.awt.JogreClientApplet;
import org.jogre.client.TableConnectionThread;

/**
 * Applet class for GameOfThrones.
 *
 * @author  Robin Giraudon
 * @version Beta 0.3
 */
public class GameOfThronesApplet extends JogreClientApplet {

    /**
     * Applet constructor for a game of gameOfThrones.
     *
     * @param conn
     * @param tableNum
     */
    public GameOfThronesApplet () {
        super ();
    }

    /**
     * Return the correct table frame
     *
     * @param conn
     * @param tableNum
     */
    public JogreTableFrame getJogreTableFrame (TableConnectionThread conn) {
        return new GameOfThronesTableFrame (conn);
    }
}
