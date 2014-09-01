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
package org.jogre.gameOfThrones.server;

import nanoxml.XMLElement;

import org.jogre.gameOfThrones.common.GameOfThronesModel;
import org.jogre.server.ServerConnectionThread;
import org.jogre.server.ServerController;

/**
 * Server controller for a game of gameOfThrones.  It stores a GameOfThronesModel
 * on a running Jogre server and receives input from clients playing. It
 * can do further processing on the server side.
 *
 * The main aim of this class is to stop hacked gameOfThrones clients.
 *
 * @author  Robin Giraudon
 * @version Beta 0.3
 */
public class GameOfThronesServerController extends ServerController {

    /**
     * Constructor to create a gameOfThrones controller.
     *
     * @param gameKey  Game key.
     */
    public GameOfThronesServerController (String gameKey) {
        super (gameKey);
    }

    /**
     * Create a new gameOfThrones model when the game starts.
     *
     * @see org.jogre.server.ServerController#startGame(int)
     */
    public void startGame (int tableNum) {
    	int nbPlayers=getTable(tableNum).getNumOfPlayers();
        setModel (tableNum, new GameOfThronesModel (nbPlayers));
    }

    /**
     * This method is called when a client says that the game
     * is over.
     *
     * @see org.jogre.server.ServerController#gameOver(int)
     */
    public void gameOver (ServerConnectionThread conn, int tableNum, int resultType) {
        // TODO - Fill in
        //gameOver (conn, tableNum, conn.getUsername(), resultType);
    }
}
