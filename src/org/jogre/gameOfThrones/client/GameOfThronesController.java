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
import org.jogre.gameOfThrones.common.territory.Land;
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
    	}else if(e.getComponent()==playerChoices){
    		// on translate
    	}
    		
    }
    
    public void mouseClicked(MouseEvent e){
    	
    	if (e.getButton()==MouseEvent.BUTTON1 && isGamePlaying()){
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
    					if(model.getBattle()!=null){//On verifie si il y a combat
    					
    						//en cas de retraite !!
    						if(model.getBattle().getState()==3 && model.canWithdraw(gameOfThronesComponent.getTerritory(e.getX(),e.getY()),getSeatNum())){
    							//System.out.println("can withdraw here");
    							model.getBattle().withdraw(gameOfThronesComponent.getTerritory(e.getX(),e.getY()));
    							model.battleEnd();
    							gameOfThronesComponent.repaint();
    							sendProperty("withdraw", gameOfThronesComponent.getTerritory(e.getX(),e.getY()).getName());
    						}// on peut clicker pour supporter
    						else if(model.canSupport(gameOfThronesComponent.getTerritory(e.getX(),e.getY()),getSeatNum())){
    							playerChoices.support(gameOfThronesComponent.getTerritory(e.getX(),e.getY()));
    						}else if(gameOfThronesComponent.getTerritory(e.getX(),e.getY())==model.getTerritory1()){
    							playerChoices.attackTo(gameOfThronesComponent.getTerritory(e.getX(),e.getY()));
    						}
    						
    					// on regarde si un ordre est deja selectioné, qu'il peut etre utilisé dans le territoir voulu et qu'on peut l'utiliser
    					}else if(playerChoices.getRelatedTerr()!=null && playerChoices.getRelatedTerr().canUseOrderOn(gameOfThronesComponent.getTerritory(e.getX(),e.getY()))){
    						//On execute l'ordre
    						int orderEx =playerChoices.getRelatedTerr().useOrderOn(gameOfThronesComponent.getTerritory(e.getX(),e.getY()));
    						switch(orderEx){
    						case 0:
    							// on envoi le territoir qui donne l'ordre et celui qui execute
        						sendProperty(playerChoices.getRelatedTerr().getName(), gameOfThronesComponent.getTerritory(e.getX(),e.getY()).getName());
    							playerChoices.blank();
    							model.nextPlayer(); //model.checkRaid(); // peut-etre tout mettre en 1
    							break;
    						case 1:
    							playerChoices.moveTo(gameOfThronesComponent.getTerritory(e.getX(),e.getY()));
    							model.mvInitiated(playerChoices.getRelatedTerr(),gameOfThronesComponent.getTerritory(e.getX(),e.getY()));// on indique au model qu'un mouvement est commencé on ne peut plus changer d'ordre
    							//ICI IL faut indiquer les info aux autres joueurs
    							sendProperty("mvInitiated", playerChoices.getRelatedTerr().getName());
    							sendProperty("mvInitiated2", gameOfThronesComponent.getTerritory(e.getX(),e.getY()).getName());
    							break;
    						case 2:
    							playerChoices.attackTo(gameOfThronesComponent.getTerritory(e.getX(),e.getY()));
    							model.battle(playerChoices.getRelatedTerr(),gameOfThronesComponent.getTerritory(e.getX(),e.getY()));
    							//ICI IL faut indiquer les info aux autres joueurs
    							sendProperty("mvInitiated", playerChoices.getRelatedTerr().getName());
    							sendProperty("battleInitiated", gameOfThronesComponent.getTerritory(e.getX(),e.getY()).getName());
    							break;
    						}
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
    			switch (choice){
    			case 1 :
    					Family family =model.getFamily(getSeatNum());
    					for(Territory territory : family.getTerritories() )
    					{	
    						int[] order =territory.getOrder().getOrderInt();
    						sendProperty(territory.getName(),order[0],order[1]);
    					}
    				sendProperty ("endProg",getSeatNum());
    				model.endProg();//encore utile ?
    				break;
    			case 2 :
    				sendProperty("cancelOrder",playerChoices.getRelatedTerr().getName());//on envoi le message
    				playerChoices.getRelatedTerr().rmOrder();// on supprime l'ordre
    				model.nextPlayer();
    				playerChoices.blank();
    				break;
    			case 3 :
    				model.troopSend(1,0,0,0);
    				sendProperty("troopSend", 0);playerChoices.checkPlayerChoices();
    				break;
    			case 4 :
    				model.troopSend(0,1,0,0);
    				sendProperty("troopSend", 1);playerChoices.checkPlayerChoices();
    				break;
    			case 5:
    				model.troopSend(0,0,1,0);
    				sendProperty("troopSend", 2);playerChoices.checkPlayerChoices();
    				break;
    			case 6:
    				model.troopSend(0,0,0,1);
    				sendProperty("troopSend", 3);playerChoices.checkPlayerChoices();
    				break;
    			case 7:
    				model.attPrepEnd();
    				sendProperty("attPreparationEnded", 0);
    				//playerChoices.blank();
    				break;
    			case 8 :
    				model.troopSend(1,0,0,0);
    				sendProperty("troopSend", 0);
    				break;
    			case 9 :
    				model.troopSend(0,1,0,0);
    				sendProperty("troopSend", 1);
    				break;
    			case 10:
    				model.troopSend(0,0,1,0);
    				sendProperty("troopSend", 2);
    				break;
    			case 11:
    				model.troopSend(0,0,0,1);
    				sendProperty("troopSend", 3);
    				break;
    			case 12 :
    				model.getBattle().addAttSupport(playerChoices.getRelatedTerr());
    				sendProperty("attSupport",playerChoices.getRelatedTerr().getName());
    				//playerChoices.blank();
    				break;
    			case 13 :
    				model.getBattle().addDefSupport(playerChoices.getRelatedTerr());
    				sendProperty("defSupport",playerChoices.getRelatedTerr().getName());
    				//playerChoices.blank();
    				break;
    			case 14 :
    				playerChoices.getRelatedTerr().getOrder().used();// ne verifie pas si il y a des ordres
    				sendProperty("noSupport",playerChoices.getRelatedTerr().getName());
    				//playerChoices.blank();
    				break;
    			case 15 :
    				if(model.getBattle().getAttFamily().getPlayer()==this.getSeatNum()){
    					sendProperty("attCardPlayed",playerChoices.getIndexCard());
    				}else {
    					sendProperty("defCardPlayed",playerChoices.getIndexCard());
    				}
    				break;
    			case 16 : 
    				model.getBattle().useSword();
    				sendProperty("useSword", 0);
    				break;
    			case 17 :
    				model.getBattle().dontUseSword();
    				sendProperty("dontUseSword", 0);
    				break;
    			case 18 :
    				sendProperty("consolidation", playerChoices.getRelatedTerr().getName());
    				model.getFamily(getSeatNum()).gainInflu(playerChoices.getRelatedTerr().consolidation());
    				playerChoices.getRelatedTerr().rmOrder();
    				nextPlayer();
    				break;
    			case 19 :
    				System.out.println("recruit ship");
    				break;
    			case 20 :
    				playerChoices.getRelatedTerr().recruit(1);
    				((Land)playerChoices.getRelatedTerr()).haveRecruit(1);
    				sendProperty("recruitFoot", playerChoices.getRelatedTerr().getName());
    				if(playerChoices.getRelatedTerr().getOrder()==null){
    					nextPlayer();
    					sendProperty("nextPlayer", 0);
    				}
    				break;
    			case 21 :
    				playerChoices.getRelatedTerr().recruit(2);
    				((Land)playerChoices.getRelatedTerr()).haveRecruit(2);
    				sendProperty("recruitKnight", playerChoices.getRelatedTerr().getName());
    				if(playerChoices.getRelatedTerr().getOrder()==null){
    					nextPlayer();
    					sendProperty("nextPlayer", 0);
    				}
    				break;
    			case 22 :
    				playerChoices.getRelatedTerr().recruit(3);
    				((Land)playerChoices.getRelatedTerr()).haveRecruit(2);
    				sendProperty("recruitTower", playerChoices.getRelatedTerr().getName());
    				if(playerChoices.getRelatedTerr().getOrder()==null){
    					nextPlayer();
    					sendProperty("nextPlayer", 0);
    				}
    				break;
    			}
    			//le playerChoice verifie si il doit afficher quelque chose de nouveau
    			switch(playerChoices.check(model.informations(getSeatNum()), model.getFamily(getSeatNum()), model.getBattle())){
    			case 1:
    				sendProperty("FamilyCards", 0);
    				break;
    			case 2:
    				sendProperty("Sword", 0);
    				break;
    			case 3:
    				System.out.println("controller : cheked state 3");
    				break;
    			case 4:
    				model.battleEnd();
    				sendProperty("BattleEnd", 0);// VRAIMENT Necessaire ?!!
    				break;
    			}
    			gameOfThronesComponent.repaint();//encore utile ? 
    			playerChoices.repaint(); // au cas ou (quand on affiche autre chose devant le jeux)
    			
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
    		model.nextPlayer();//model.checkRaid();
    	}else if(key.equals("mvInitiated")){
    		model.mvInitiated(model.getBoardModel().getTerritory(territory));
    	}else if(key.equals("mvInitiated2")){
    		model.mvInitiated2(model.getBoardModel().getTerritory(territory));
    	}else if(key.equals("battleInitiated")){
    		model.battleInitiated(model.getBoardModel().getTerritory(territory));
    	}else if(key.equals("attSupport")){
    	model.getBattle().addAttSupport(model.getBoardModel().getTerritory(territory));
    	}else if(key.equals("defSupport")){
        	model.getBattle().addDefSupport(model.getBoardModel().getTerritory(territory));
    	}else if(key.equals("noSupport")){
    		model.getBoardModel().getTerritory(territory).getOrder().used();
    	}else if(key.equals("withdraw")){
    		model.getBattle().withdraw(model.getBoardModel().getTerritory(territory));
			model.battleEnd();
			gameOfThronesComponent.repaint();
    	}else if(key.equals("consolidation")){
    		Territory terr =model.getBoardModel().getTerritory(territory);
    		terr.getFamily().gainInflu(terr.consolidation());
    		terr.rmOrder();playerChoices.blank();
    		nextPlayer();
    	}else if (key.equals("recruitFoot")){
    		model.getBoardModel().getTerritory(territory).recruit(1);
    	}else if (key.equals("recruitKnight")){
    		model.getBoardModel().getTerritory(territory).recruit(2);
    	}else if (key.equals("recruitTower")){
    		model.getBoardModel().getTerritory(territory).recruit(3);
    	}else{
    		model.getBoardModel().getTerritory(key).useOrderOn(model.getBoardModel().getTerritory(territory));
    		model.nextPlayer();//model.checkRaid(); // fusionner dans check Raid?
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
    
    
     public void receiveProperty (String key, int value) { 
    	 if (key.equals("nextPlayer")){
    		 model.nextPlayer();
    	 }else if(key.equals("troopSend")){
     		int[]troops= new int[4];
     		troops[value]=1;
     		model.troopSend(troops[0],troops[1],troops[2],troops[3]);
    	 }else if(key.equals("FamilyCards")){
    		 playerChoices.check(model.informations(getSeatNum()), model.getFamily(getSeatNum()), model.getBattle());
    	 }else if(key.equals("attPreparationEnded")){
     		System.out.println("Inside controller send  attPrepEnd");
     		model.attPrepEnd();
     	}else if(key.equals("attCardPlayed")){
    		model.getBattle().playCard(model.getBattle().getAttFamily().getCombatantCards().get(value), model.getBattle().getAttFamily());
    	}else if(key.equals("defCardPlayed")){
    		model.getBattle().playCard(model.getBattle().getDefFamily().getCombatantCards().get(value), model.getBattle().getDefFamily());
    	}else if(key.equals("Sword")){
    		playerChoices.swordPlay(model.getFamily(getSeatNum()));
    	}else if(key.equals("BattleEnd") && model.getBattle()!=null){ // Seconde condition importante ?
			model.battleEnd();
    	}else if (key.equals("useSword")){
    		model.getBattle().useSword();
    	}else if (key.equals("dontUseSword")){
    		model.getBattle().dontUseSword();
    	}else{
    		//on indique que le joueur a fini de donner ses ordres
    		model.getFamily(value).ordersGived=true;
    		// on verifie que si c'etait le dernier
    		model.endProg();
    		//gameOfThronesComponent.repaint();
    		//playerChoices.repaint();
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
