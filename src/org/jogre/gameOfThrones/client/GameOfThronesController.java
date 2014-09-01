/*
 * JOGRE (Java Online Gaming Real-time Engine) - GameOfThrones
 * Copyright (C) 2003 - 2014  Robin Giraudon (giraudon.robin@gmail.com)
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

import nanoxml.XMLElement;

import graphisme.PlayersChoices;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;

import org.jogre.gameOfThrones.common.Family;
import org.jogre.gameOfThrones.common.GameOfThronesModel;
import org.jogre.gameOfThrones.common.orders.Order;
import org.jogre.gameOfThrones.common.orders.OrderType;
import org.jogre.gameOfThrones.common.territory.BoardModel;
import org.jogre.gameOfThrones.common.territory.Territory;
import org.jogre.client.JogreController;
import org.jogre.common.PlayerList;
import org.jogre.common.comm.CommNextPlayer;

/**
 * Controller for the gameOfThrones game.
 *
 * @author  Robin Giraudon
 * @version Beta 0.3
 */
public class GameOfThronesController extends JogreController {

	// Indique un click droit et une volonté de translation
	//private boolean translate;
	// utiliser pour sauver les coordonées de la souris quand necessaire
	private int xMouse;
	private int yMouse;
	
    // links to game data and the board component
    protected GameOfThronesModel     model;
    protected GameOfThronesComponent gameOfThronesComponent;
    protected JLabel infoLabel;
    protected PlayersChoices playerChoices;
    /**
     * Default constructor for the gameOfThrones controller which takes a
     * model and a view.
     *
     * @param gameOfThronesModel      GameOfThrones model.
     * @param gameOfThronesComponent  GameOfThrones view.
     */
    public GameOfThronesController (GameOfThronesModel gameOfThronesModel, GameOfThronesComponent gameOfThronesComponent, JLabel infoLabel,PlayersChoices playerChoices)
    {
        super (gameOfThronesModel, gameOfThronesComponent);
        this.model     = gameOfThronesModel;
        this.gameOfThronesComponent = gameOfThronesComponent;
        
        this.playerChoices=playerChoices;
        this.infoLabel=infoLabel;
        
    }

    /**
     * Start method which restarts the model.
     *
     * @see org.jogre.common.JogreModel#start()
     */
    public void start () {
        model.reset ();
    }
    
//Controle avec la SOURIS
    //affiche les infos a l'ecran
    public void mouseMoved(MouseEvent e) {
    	yMouse=e.getY();
    	if(e.getComponent()==gameOfThronesComponent){
    		// Si on passe sur un territoire, renvoie les infos le concernant
    		infoLabel.setText(gameOfThronesComponent.getInfo(e.getX(),e.getY()));
    	}
    }
    
    /**translate le plateau */
    public void mouseDragged (MouseEvent e) {
    	if(e.getComponent()==gameOfThronesComponent){	
    		if(e.getY()<yMouse){
    			gameOfThronesComponent.down();
			}else if(e.getY()>yMouse){
				gameOfThronesComponent.up();
			}
			yMouse=e.getY();
    	}
    		
    }
    
    public void mouseClicked(MouseEvent e){
    	
    	if (e.getButton()==MouseEvent.BUTTON1 && isGamePlaying()){ // Il vaudrait mieux modifier la classe JogreComponant pour ajouter une methode aClikerSurCeComponant(intx,inty)
    		//on regarde sur quoi on a clicke
    		if(e.getComponent()==gameOfThronesComponent){
    			//on regarde si on click bien sur un territoir
    			if (gameOfThronesComponent.getTerritory(e.getX(),e.getY())!=null){
    				//
    				switch(model.getPhase()){
    				case 1 :
    					// on selectionne un territoire et on le passe en parametre de canGiveOrder(teritoir, numJoueur)
    					if(model.canGiveOrder(gameOfThronesComponent.getTerritory(e.getX(),e.getY()), getSeatNum())){
    						//on affiche en bas l'ecran d'ordres 
    						playerChoices.showOrders(model.getFamily(getSeatNum()),gameOfThronesComponent.getTerritory(e.getX(),e.getY()));
    					}else{
    						playerChoices.blank();
    					}
    					break;
    				case 2:
    					// on regarde si un ordre est deja selectioné, qu'il peut etre utilisé dans le territoir voulu et qu'on peut l'utiliser
    					if(playerChoices.getRelatedTerr()!=null && playerChoices.getRelatedTerr().canUseOrderOn(gameOfThronesComponent.getTerritory(e.getX(),e.getY()))){
    						//On execute l'ordre
    						playerChoices.getRelatedTerr().useOrderOn(gameOfThronesComponent.getTerritory(e.getX(),e.getY()));
    						// on envoi le territoir qui donne l'ordre et celui qui execute
    						sendProperty(playerChoices.getRelatedTerr().getName(), gameOfThronesComponent.getTerritory(e.getX(),e.getY()).getName());
    						//mise a jour graphic
    						playerChoices.blank();
    						gameOfThronesComponent.repaint();
    						
    					}else if(model.canPlayThisOrder(gameOfThronesComponent.getTerritory(e.getX(),e.getY()), getSeatNum())){
    						playerChoices.orderSelected(gameOfThronesComponent.getTerritory(e.getX(),e.getY()));
    					}
    					break;
    				}
    			}
    		}else if (e.getComponent()==playerChoices){
    			// quand on click sur le playerChoice on reccupère un message de ce qui s'est passe 
    			int choice=playerChoices.RigthClick(e.getX(),e.getY(),model.getFamily(getSeatNum()));
    			
    			//Quand un joueur a donnée tous ses ordres (durant la phase1) on les envois et on l'indique au autres
    			if(choice==1){
    				if(model.getFamily(getSeatNum()).ordersGived){
    					Family family =model.getFamily(getSeatNum());
    					for(Territory territory : family.getTerritories() )
    					{	
    						int[] order =territory.getOrder().getOrderInt();
    						sendProperty(territory.getName(),order[0],order[1]);
    					}
    				sendProperty ("endProg",getSeatNum());
    				}
    				model.endProg();//encore utile ? 
    			}else if (choice==2){
    				sendProperty("cancelOrder",playerChoices.getRelatedTerr().getName());//on envoi le message
    				playerChoices.getRelatedTerr().rmOrder();// on supprime l'ordre
    				playerChoices.blank();
    			}
    			gameOfThronesComponent.repaint();//encore utile ? 
    		}
    	}
    }
   
    

	/**
     * Implementation of the mouse pressed interface.
     *
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(MouseEvent e) {
    	//mettre en click
    	System.out.println("joueur num :"+getSeatNum());
        if (/*isGamePlaying() && isThisPlayersTurn () &&*/ e.getButton()==MouseEvent.BUTTON3) {
            // get mouse co-ordinates
            int x = e.getX();
            int y = e.getY();
            
            System.out.println ("Mouse pressed x: " + x + " y:" + y);
          
        }
    }


    // Receive
    public void receiveProperty(String key, String territory){
    	if(key.equals("cancelOrder")){
    		model.getBoardModel().getTerritory(territory).rmOrder();
    	}else{
    		model.getBoardModel().getTerritory(key).useOrderOn(model.getBoardModel().getTerritory(territory));
    	}
    }
    
    //
    public void receiveProperty(String territory, int type, int bonus) {// peut-etre mettre cette methode dans Order
    	boolean star = (bonus==1);
    	switch (type) {
		case 0:
			model.getBoardModel().getTerritory(territory).setOrder(new Order(star, 0, 0, OrderType.RAI));
			break;
		case 4:
			model.getBoardModel().getTerritory(territory).setOrder(new Order(star, 0, bonus, OrderType.SUP));
			break;
		case 1:
			model.getBoardModel().getTerritory(territory).setOrder(new Order(star, 0, bonus, OrderType.ATT));
			break;
		case 2:
			model.getBoardModel().getTerritory(territory).setOrder(new Order(star, 0, 0, OrderType.CON));
			break;
		default:
			model.getBoardModel().getTerritory(territory).setOrder(new Order(star, bonus+1, 0, OrderType.DEF));
			break;
		}
	}
    
    //AJOUTER LA RECEPTION DE NEXT PLAYER!!!
     public void receiveProperty (String key, int family) { 
    	 if (key.equals("nextPlayer")){
    		 model.nextPlayer();
    	 }else{
    		 //on indique que le joueur a fini de donner ses ordres
    		 model.getFamily(family).ordersGived=true;
    		 // on verifie que si c'etait le dernier
    		 model.endProg();
    		 gameOfThronesComponent.repaint();
          }
       }
    
    /**
     * Implementation of the mouse released interface.
     *
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    /*public void mouseReleased (MouseEvent e) {
        if (isGamePlaying() && isThisPlayersTurn ()) {
            // get mouse co-ordinates
            int mouseX = e.getX();
            int mouseY = e.getY();
            
            // if game still in progress then continue on...
           // nextPlayer ();
        }
    }
    */
    
    
    /**
	 * Method which tells everyone  that it is next players turn.
	 */
	public void nextPlayer () { 
		model.nextPlayer();
		sendProperty("nextPlayer", 0);
	}
	/** tell the client if this is the player's turn*/
    public boolean isThisPlayersTurn (){
    	return getSeatNum()==model.getCurrentPlayer();
    }
}
