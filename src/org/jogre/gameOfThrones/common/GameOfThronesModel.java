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
package org.jogre.gameOfThrones.common;

import nanoxml.XMLElement;

import org.jogre.common.JogreModel;
import org.jogre.common.comm.Comm;
import org.jogre.gameOfThrones.common.combat.GroundForce;
import org.jogre.gameOfThrones.common.combat.NavalTroup;
import org.jogre.gameOfThrones.common.orders.OrderType;
import org.jogre.gameOfThrones.common.territory.BoardModel;
import org.jogre.gameOfThrones.common.territory.Territory;

/**
 * Game model for the gameOfThrones game.
 *
 * @author  Robin Giraudon
 * @version Beta 0.3
 */
public class GameOfThronesModel extends JogreModel {

	//
	private int numberPlayers;
	// be careful that indicate the position on the thrones track, not the player number, ex : 2 indicate the third players in the thrones track not the player number 3
	private int currentPlayer; 
	private int turn;
	private int wildings;
	private int[] supply; // tableau de la taille du nombre de joueur, le joueur 1 correspon a supply[1] et la valeur à son ravitaillement
	private int[] victory; // fonctione comme les supply
	// influence pist, ex: thrones(0)=2 means that the third player is first in throne track 
	private int[] throne;
	private int[] fiefdoms;
	private int[] court;
	//determine la phase de jeu (0: phase Westeros, 1= programation, 2: execution)
	private int phase;
	private int internPhase; // determine la phase dans la execution (0: raid, 1: mouvement, 2:consolidation) 
	//
	private BoardModel boardModel;
	//Creation des fammilles
	private Family[] families;
	
	
	
    /**
     * Constructor which creates the model.
     */
    public GameOfThronesModel (int numberPlayers) {
        super (GAME_TYPE_TURN_BASED);
        // contruit les éléments générale du jeu
        this.numberPlayers=numberPlayers;
        throne= new int[numberPlayers];
        fiefdoms= new int[numberPlayers];
        court= new int[numberPlayers];
        boardModel = new BoardModel();// ajouter numberPlayers en parametre ????
        families=new Family[numberPlayers];
        // placer le plateau en fonction du jeu
        familiesConstruction(numberPlayers);
        // le jeu commence à la phase programation
        phase=1;
        internPhase=0;
        turn=1;
        wildings=2;
        currentPlayer=0;
    }
    
    public void nextInternPhase(){
    	internPhase=(internPhase+1)%3;
    }
    public void nextPhase(){
    	phase= (phase+1)%3;
    	//AJOUTER DES CHOSES EN FONCTION DES PHASES 
    	if(phase==2){
    		//on initialise
    		 internPhase=0;
    		 currentPlayer=0;
    		 // on cherche le premier joueur a avoir un ordre de raid
    		 checkRaid();
    	}
    }
    
  //FONCTION QUI VERIFIE  VERIFIE SI IL Y A DES RAIDS PARMI TOUS JOUEURS
    // et on cherche le joueur qui a un ordre de raid suivant. Si il n'y en a pas, on passe à la phase interne suivante
    public void checkRaid(){
    	int i =0;
    	while (!checkCurrentPlayerRaid() && i<=numberPlayers){
    		i++;
			 currentPlayer=(currentPlayer+1)%numberPlayers;
		 }
		 if (i>numberPlayers){
			 nextInternPhase();
		 }
    }
    
    private boolean checkCurrentPlayerRaid(){
    	for (Territory territory :families[getCurrentPlayer()].getTerritories()){
    		if (territory.getOrder().getType()==OrderType.RAI){
    			return true;
    		}
    	}
    	return false;
    }
    
    
    public void nextPlayer(){
    	currentPlayer = (currentPlayer+1)%numberPlayers;
    }
    /** be careful this method return a playerSeat, not is place on the throne track*/ 
    public int getCurrentPlayer(){
    	return throne[currentPlayer];
    }
    
    public boolean canGiveOrder(Territory territory, int player){ // a modifier pour verifier les troupes et non juste l'appartenance
    	return phase==1 && (territory.getFamily()!=null) && (territory.getFamily().getPlayer()==player);
    }

    public boolean canPlayThisOrder(Territory territory, int seatNum) {
		return ( territory.getFamily()!=null && territory.getFamily().getPlayer()==seatNum && seatNum==getCurrentPlayer() && territory.getOrder().getType().ordinal()==internPhase ) ;
	}

    
	public BoardModel getBoardModel() {
		return boardModel;
	}


	public Family getFamily(int seatNum) {
		return families[seatNum];
	}

	/**on verifie que tous les joueurs on donné leurs ordres.
	 * Quand c'est le cas on passe à la phase suivante
	 */
	public void endProg(){
		boolean res=true;
		for(Family family : families){
			if(!family.ordersGived){
				res=false;
			}
		}
		if(res){
			nextPhase();
		}
	}
	/*public boolean endProg(){
		boolean res=true;
		for(Family family : families){
			if(!family.ordersGived){
				res=false;
			}
		}
		return res;
	}*/
	
	

	public int getPhase() {	
		return phase;
	}

	
    
//sert a la construction du plateau en fonction du nb de joueur
	private void familiesConstruction(int playerNumber){
		// On place les starks (changer pour le joueur 3)
		families[0]=new Family(0);
		throne[0]=0;
        boardModel.getTerritory("Winterfell").setTroup(new GroundForce(families[0],boardModel.getTerritory("Winterfell"),1,1,0));
        boardModel.getTerritory("White Harbor").setTroup(new GroundForce(families[0],boardModel.getTerritory("White Harbor"),1,0,0));
        boardModel.getTerritory("Shivering Sea").setTroup(new NavalTroup(families[0],boardModel.getTerritory("Shivering Sea"),1));
        if(playerNumber>1){
        	families[1]=new Family(1);
        	throne[1]=1;
        	boardModel.getTerritory("Karhold").setTroup(new GroundForce(families[1],boardModel.getTerritory("Karhold"),1,0,0));
        }
	}
	
	
//deja en place avant 
    /**
     * Method which reads in XMLElement and sets the state of the models fields.
     * This method is necessary for other users to join a game and have the
     * state of the game set properly.
     *
     * @param message    Data stored in message.
     */
    public void setState (XMLElement message) {
        // Wipe everything
        reset ();

        // TODO - Fill in

        // If everything is read sucessfully then refresh observers
        refreshObservers();
    }

    /**
     * Reset the pieces.
     */
    public void reset () { //depend du nombre de joueur
        // TODO - Fill in
        // inform any graphical observers
        refreshObservers();
    }

    /**
     * Implementation of a gameOfThrones game - This is stored on the server and is used
     * when a player visits a game and a game is in progress.
     *
     * @see org.jogre.common.comm.ITransmittable#flatten()
     */
    public XMLElement flatten () {
        // Create empty model XML to populate
        XMLElement state = new XMLElement (Comm.MODEL);

        // TODO - Fill in

        return state;
    }


	




    
}
