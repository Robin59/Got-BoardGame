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

import java.awt.TextArea;

import javax.swing.JLabel;

import graphisme.BoardComponent;
import graphisme.PlayersChoices;
import graphisme.PlayersInfo;
import info.clearthought.layout.TableLayout;

import org.jogre.gameOfThrones.common.GameOfThronesModel;
import org.jogre.client.TableConnectionThread;
import org.jogre.client.awt.JogreLabel;
import org.jogre.client.awt.JogrePanel;
import org.jogre.client.awt.JogreTableFrame;
import org.jogre.client.awt.PlayerComponent;

/**
 * Game table for a GameOfThrones.  This class holds the MVC class for a
 * game of gameOfThrones.  The MVC classes are GameOfThronesModel, GameOfThronesComponent
 * and GameOfThronesController respectively.
 *
 * @author  Robin Giraudon
 * @version Beta 0.3
 */
public class GameOfThronesTableFrame extends JogreTableFrame {

    // Declare MVC classes
    private GameOfThronesModel      gameOfThronesModel;      // model
    private BoardComponent  gameOfThronesComponent;  // view
    private GameOfThronesController gameOfThronesController; // controller
    
    private JLabel infoLabel; // affiches les informations volantes
    private JLabel infoPlayerC; // information en rapport avec le PlayerChoice
    private PlayersChoices playerChoices;
    
    /**
     * Constructor which sets up the MVC classes and takes
     * table connection.
     *
     * @param conn
     * @param tableNum
     */
    public GameOfThronesTableFrame (TableConnectionThread conn)
    {
        super (conn);
        //
        infoLabel = new JLabel("Welcome to Game of Thrones board game!");
        infoPlayerC = new JogreLabel("");  //JogreLabel(text, style, size, color);
        // Initialise MVC classes
        int nbPlayers=table.getNumOfPlayers();
        JLabel textLabel = new JLabel();
        this.gameOfThronesModel = new GameOfThronesModel (nbPlayers, textLabel);
        playerChoices = new PlayersChoices(infoPlayerC, gameOfThronesModel);
        this.gameOfThronesComponent = new BoardComponent (gameOfThronesModel);
        this.gameOfThronesController = new GameOfThronesController (gameOfThronesModel, gameOfThronesComponent, infoLabel, playerChoices);
    
        // The component observes the model
        this.gameOfThronesModel.addObserver(this.gameOfThronesComponent);

        // Set client/server connection on controller FIXME - constructor?
        this.gameOfThronesController.setConnection (conn);

        // Enable main view to recieve user input (e.g. mouse clicks) by setting controller
        this.gameOfThronesComponent.setController (gameOfThronesController);
        this.playerChoices.setController (gameOfThronesController);
        // Set game data and controller (constructor must always call these)
        setupMVC (gameOfThronesModel, gameOfThronesComponent, gameOfThronesController);

      //panel pour la partie droite du jeu
        double [][] leftBoardSizes = {{560}, {1,180,2,10,2,15,290}};
        // les pistes et autres info
        JogrePanel leftPanel = new JogrePanel (leftBoardSizes);
        //leftPanel.add(new PlayersInfo(gameOfThronesModel), "0,0");
        leftPanel.add(textLabel,"0,1");
        leftPanel.add(infoLabel, "0,3");
        leftPanel.add( infoPlayerC ,"0,5");
        leftPanel.add(playerChoices,"0,6");
        // Create game panel and add main view to it
        double pref = TableLayout.PREFERRED;// pas utilisé (mais utile)
        double [][] sizes = {{800,5,560}, {500}};
        JogrePanel mainPanel = new JogrePanel (sizes);
        mainPanel.add (gameOfThronesComponent, "0,0");
        mainPanel.add(leftPanel,"2,0");
        
        
        // Set game panel
        setGamePanel (mainPanel);
        gameOfThronesComponent.paintComponent(this.getGraphics());// ligne ajouté en test  UTILE ?
        pack();
    }

    /**
     * Override to ensure that the player is seated in the correct position.
     *
     * @param tableAction
     */
    public void startGame () {
        super.startGame ();
    }
}
