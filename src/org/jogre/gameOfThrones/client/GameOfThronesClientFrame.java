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

import org.jogre.client.TableConnectionThread;
import org.jogre.client.awt.JogreClientFrame;
import org.jogre.client.awt.JogreTableFrame;

/**
 * Game client frame for a game of gameOfThrones.  This is the
 * entry point to the game as an application.
 *
 * @author  Robin Giraudon
 * @version Beta 0.3
 */
public class GameOfThronesClientFrame extends JogreClientFrame {

    /**
     * Constructor which takes command arguments.
     *
     * @param args     Command arguments from dos.
     */
    public GameOfThronesClientFrame (String [] args) {
        super (args);
    }

    /**
     * Return the correct table frame.
     *
     * @see org.jogre.client.awt.JogreClientFrame#getJogreTableFrame(org.jogre.client.TableConnectionThread)
     */
    public JogreTableFrame getJogreTableFrame (TableConnectionThread conn) {
        return new GameOfThronesTableFrame (conn);
    }

    /**
     * Main method which executes a game of gameOfThrones.
     *
     * @param args
     */
    public static void main (String [] args) {
        GameOfThronesClientFrame client = new GameOfThronesClientFrame (args);
    }
}
