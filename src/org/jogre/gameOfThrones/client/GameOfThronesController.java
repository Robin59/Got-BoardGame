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

import graphisme.BoardComponent;
import graphisme.PlayersChoices;

import java.awt.event.MouseEvent;
import java.lang.ref.PhantomReference;
import java.util.List;
import java.util.ListIterator;

import javax.swing.JLabel;

import org.jogre.gameOfThrones.common.Bidding;
import org.jogre.gameOfThrones.common.BiddingAgainstWild;
import org.jogre.gameOfThrones.common.Deck;
import org.jogre.gameOfThrones.common.Family;
import org.jogre.gameOfThrones.common.GameOfThronesModel;
import org.jogre.gameOfThrones.common.combat.Battle;
import org.jogre.gameOfThrones.common.combat.BattlePvP;
import org.jogre.gameOfThrones.common.orders.Order;
import org.jogre.gameOfThrones.common.orders.OrderType;
import org.jogre.gameOfThrones.common.territory.BoardModel;
import org.jogre.gameOfThrones.common.territory.Land;
import org.jogre.gameOfThrones.common.territory.Territory;
import org.jogre.gameOfThrones.common.territory.Water;
import org.jogre.client.JogreController;
import org.jogre.client.awt.PlayerComponent;
import org.jogre.common.PlayerList;
import org.jogre.common.comm.CommGameOver;

import state.*;
import wildlingsResolution.WildlingsResolution;


/**
 * Controller for the gameOfThrones game.
 * The game is working more or less like an finished automate, with a lot of state from which some transition are available
 * The state here are combination of the state of the model and the state of the player(PlayerChoice classe) 
 * @author  Robin Giraudon
 * @version Beta 0.3
 */
public class GameOfThronesController extends JogreController {

	
	// utiliser pour sauver les coordonées de la souris quand necessaire
	private int xMouse;
	private int yMouse;
	
    // links to game data and the board component
    protected GameOfThronesModel     model;
    protected BoardComponent gameOfThronesComponent;
    protected JLabel infoLabel;
    protected PlayersChoices playerChoices;
    private String temp;
    
    /**
     * Default constructor for the gameOfThrones controller which takes a
     * model and a view.
     *
     * @param gameOfThronesModel      GameOfThrones model.
     * @param gameOfThronesComponent  GameOfThrones view.
     */
    public GameOfThronesController (GameOfThronesModel gameOfThronesModel, BoardComponent gameOfThronesComponent, JLabel infoLabel,PlayersChoices playerChoices)
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
        this.gameOfThronesComponent.setPlayer(getSeatNum());
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
    
    /**
     * Use for the translation of the view on the board */
    public void mouseDragged (MouseEvent e) {
    	if(e.getComponent()==gameOfThronesComponent){	
    		if(e.getY()<yMouse){
    			gameOfThronesComponent.down();
			}else if(e.getY()>yMouse){
				gameOfThronesComponent.up();
			}
			yMouse=e.getY();
    	}/*else if(e.getComponent()==playerChoices){
    		// on translate
    	}*/
    		
    }
    
    /*This method is call whenever the player click on the board*/
    private void mouseClickOnBoard (MouseEvent e){
    	//we first look if the player click on a territory
    	if (gameOfThronesComponent.getTerritory(e.getX(),e.getY())!=null){
    		switch(model.getPhase()){
    		case SUPPLY_TO_LOW:
    			if(gameOfThronesComponent.getTerritory(e.getX(),e.getY()).getFamily()==model.getFamily(getSeatNum())){
    				playerChoices.destroyTroop(gameOfThronesComponent.getTerritory(e.getX(),e.getY()));
    			}
    			break;
    		case MUSTERING: 
    			if(model.canRecruit(gameOfThronesComponent.getTerritory(e.getX(),e.getY()), getSeatNum())){
    				playerChoices.recruit(gameOfThronesComponent.getTerritory(e.getX(),e.getY()));
    			}else if(playerChoices.getShipSelected()){
    				musterShip(e);
    			}
    			break;
    		case PHASE_PROGRAMATION :
    			// on selectionne un territoire et on le passe en parametre de canGiveOrder(teritoir, numJoueur)
    			if(model.canGiveOrder(gameOfThronesComponent.getTerritory(e.getX(),e.getY()), getSeatNum())){
    				//on affiche en bas l'ecran d'ordres 
    				playerChoices.showOrders(model.getFamily(getSeatNum()),gameOfThronesComponent.getTerritory(e.getX(),e.getY()));
    			}else{
    				playerChoices.blank();
    			}
    			break;
    		case BATTLE :
    			//case when a player want to withdraw
    			if(model.getBattle().getState()==Battle.BATTLE_WITHDRAWAL && 
    			model.canWithdraw(gameOfThronesComponent.getTerritory(e.getX(),e.getY()),getSeatNum())){
    				model.getBattle().withdraw(gameOfThronesComponent.getTerritory(e.getX(),e.getY()));
    				model.battleEnd();
    				gameOfThronesComponent.repaint();
    				sendProperty("withdraw", gameOfThronesComponent.getTerritory(e.getX(),e.getY()).getName());
    			}
    			//case when a player can support and click on his territory
    			else if(model.getBattle().getState()==Battle.BATTLE_SUPPORT_PHASE &&
    					model.canSupport(gameOfThronesComponent.getTerritory(e.getX(),e.getY()),getSeatNum())){
    				playerChoices.support(gameOfThronesComponent.getTerritory(e.getX(),e.getY()));
    			}
    			break;
    		case PHASE_EXECUTION:
    			// on regarde si un ordre est deja selectioné, qu'il peut etre utilisé dans le territoir voulu et qu'on peut l'utiliser
    			if(playerChoices.getRelatedTerr()!=null && playerChoices.getRelatedTerr().canUseOrderOn(gameOfThronesComponent.getTerritory(e.getX(),e.getY()))){
    				//On execute l'ordre
    				int orderEx =playerChoices.getRelatedTerr().useOrderOn(gameOfThronesComponent.getTerritory(e.getX(),e.getY()));
    				switch(orderEx){
    				case 0: //raid the territory
    					sendProperty(playerChoices.getRelatedTerr().getName(), gameOfThronesComponent.getTerritory(e.getX(),e.getY()).getName());
    					playerChoices.blank();
    					model.nextPlayer();
    					break;
    				case 1:// movement
    					playerChoices.moveTo(gameOfThronesComponent.getTerritory(e.getX(),e.getY()));
    					model.mvInitiated(playerChoices.getRelatedTerr(),gameOfThronesComponent.getTerritory(e.getX(),e.getY()));// on indique au model qu'un mouvement est commencé on ne peut plus changer d'ordre
    					//ICI IL faut indiquer les info aux autres joueurs
    					sendProperty("mvInitiated", playerChoices.getRelatedTerr().getName());
    					sendProperty("mvInitiated2", gameOfThronesComponent.getTerritory(e.getX(),e.getY()).getName());
    					break;
    				case 2://click on an opponent territory for beginning a battle
    					playerChoices.attackTo(gameOfThronesComponent.getTerritory(e.getX(),e.getY()));
    					model.battle(playerChoices.getRelatedTerr(),gameOfThronesComponent.getTerritory(e.getX(),e.getY()));
    					sendProperty("mvInitiated", playerChoices.getRelatedTerr().getName());
    					sendProperty("battleInitiated", gameOfThronesComponent.getTerritory(e.getX(),e.getY()).getName());
    					break;
    				case 3:// Click back on the territory (for canceling order for example)
    					playerChoices.orderSelected(playerChoices.getRelatedTerr());
    					break;
    				}
    				gameOfThronesComponent.repaint();

    			}else if(playerChoices.getShipSelected()){
        				musterShip(e);
        		}else if(model.canPlayThisOrder(gameOfThronesComponent.getTerritory(e.getX(),e.getY()), getSeatNum())){
    				playerChoices.orderSelected(gameOfThronesComponent.getTerritory(e.getX(),e.getY()));
    			}
    			break;
			case CROW_CHOICE:
				if(model.canChangeOrder(gameOfThronesComponent.getTerritory(e.getX(),e.getY()), getSeatNum())){
    				playerChoices.changeOrder(gameOfThronesComponent.getTerritory(e.getX(),e.getY()));
    			}
				break;
			case WILDLINGSRESOLUTION:
				wildlingsAction(gameOfThronesComponent.getTerritory(e.getX(),e.getY()), getSeatNum());
				sendProperty(gameOfThronesComponent.getTerritory(e.getX(),e.getY()).getName(), getSeatNum());
				if(model.getWildingsResolution().getEnded()){
					playerChoices.blank();
					nextWesterosPhase();
				}
				break;
			default:
				System.out.println(model.getPhase());
				break;
    		}
    	}
    	if(model.checkNewTurn() && model.getWesterosPhase()==0){
    		nextWesterosPhase();
    	}
    }
    
    private void mouseClickOnPlayerChoices (MouseEvent e){
    	if(model.getPhase()==ModelState.WILDLINGSRESOLUTION){
    		int choice = playerChoices.RigthClick(e.getX(),e.getY(),model.getFamily(getSeatNum()));
    		sendProperty("wildlings_PChoice", choice, getSeatNum());
    		wildingsActionPChoice(choice,getSeatNum());
    		if(model.getWildingsResolution().getEnded()) {
    			playerChoices.blank();
    			nextWesterosPhase();
    		}
    	}else{
    		// quand on click sur le playerChoice on reccupère un message de ce qui s'est passe 
    		switch (playerChoices.RigthClick(e.getX(),e.getY(),model.getFamily(getSeatNum()))){
    		case PlayersChoices.END_PROGRAMATION_PHASE :
    			//Quand un joueur a donnée tous ses ordres (durant la phase1) on les envois et on l'indique au autres
    			Family family=model.getFamily(getSeatNum());
    			for(Territory territory : family.getTerritories() )
    			{	
    				if(territory.getOrder()!=null){ // condition in case territory don't have any orders, like territory without troops but with influence token
    					int[] order =territory.getOrder().getOrderInt();
    					sendProperty(territory.getName(),order[0],order[1]);
    				}
    			}
    			family.ordersGiven();
    			sendProperty ("endProg",getSeatNum());
    			model.endProg();
    			if(model.getPhase()==ModelState.CROW_CHOICE) playerChoices.crowChoice();
    			break;
    		case PlayersChoices.CANCEL : 
    			if(model.getPhase()==ModelState.MUSTERING){
    				playerChoices.getRelatedTerr().recruitmentDone();
    				sendProperty("recruitmentDone", playerChoices.getRelatedTerr().getName());
    				if(model.allRecruitementDone()){//on teste si tous les recrutement sont bon
    					sendProperty("allRecruitementDone", 0);
    					nextWesterosPhase();
    				}
    			}else if(model.getPhase()==ModelState.SUPPLY_TO_LOW){
    				playerChoices.blank2();
    			}else if(model.getPhase()==ModelState.CROW_CHOICE){
    				model.nextPhase();
    				sendProperty("nextPhase", 0);
    			}else{
    				sendProperty("cancelOrder",playerChoices.getRelatedTerr().getName());
    				playerChoices.getRelatedTerr().rmOrder();
    				model.nextPlayer();
    				playerChoices.blank();
    			}
    			break;
    		case PlayersChoices.ORDER_CHANGED:
    			int[] order =playerChoices.getRelatedTerr().getOrder().getOrderInt();
    			sendProperty(playerChoices.getRelatedTerr().getName(),order[0],order[1]);
    			model.nextPhase();
    			sendProperty("nextPhase", 0);
    			break;
    		case PlayersChoices.SEND_SHIP :
    			model.troopSend(1,0,0,0);
    			sendProperty("troopSend", 0);playerChoices.checkPlayerChoices();
    			break;
    		case PlayersChoices.SEND_FOOT :
    			model.troopSend(0,1,0,0);
    			sendProperty("troopSend", 1);playerChoices.checkPlayerChoices();
    			break;
    		case PlayersChoices.SEND_KNIGHT:
    			model.troopSend(0,0,1,0);
    			sendProperty("troopSend", 2);playerChoices.checkPlayerChoices();
    			break;
    		case PlayersChoices.SEND_SEIGE:
    			model.troopSend(0,0,0,1);
    			sendProperty("troopSend", 3);playerChoices.checkPlayerChoices();
    			break;
    		case PlayersChoices.ATT_PREPARATION_ENDED:
    			model.attPrepEnd();
    			sendProperty("attPreparationEnded", 0);
    			playerChoices.checkPlayerChoices();
    			break;
    		case PlayersChoices.SEND_SHIP_FOR_ATT :
    			model.troopSend(1,0,0,0);
    			sendProperty("troopSend", 0);
    			break;
    		case PlayersChoices.SEND_FOOT_FOR_ATT :
    			model.troopSend(0,1,0,0);
    			sendProperty("troopSend", 1);
    			break;
    		case PlayersChoices.SEND_KNIGHT_FOR_ATT:
    			model.troopSend(0,0,1,0);
    			sendProperty("troopSend", 2);
    			break;
    		case PlayersChoices.SEND_SEIGE_FOR_ATT:
    			model.troopSend(0,0,0,1);
    			sendProperty("troopSend", 3);
    			break;
    		case PlayersChoices.SUPPORT_ATT :
    			model.getBattle().addAttSupport(playerChoices.getRelatedTerr());
    			sendProperty("attSupport",playerChoices.getRelatedTerr().getName());
    			break;
    		case PlayersChoices.SUPPORT_DEF :
    			model.getBattle().addDefSupport(playerChoices.getRelatedTerr());
    			sendProperty("defSupport",playerChoices.getRelatedTerr().getName());
    			break;
    		case PlayersChoices.SUPPORT_NONE:
    			playerChoices.getRelatedTerr().getOrder().setUse(true);
    			sendProperty("noSupport",playerChoices.getRelatedTerr().getName());
    			break;
    		case PlayersChoices.HOUSE_CARD_CHOSEN:
    			if(model.getBattle().getAttFamily().getPlayer()==this.getSeatNum() && playerChoices.getIndexCard()!=-1){
    				sendProperty("attCardPlayed",playerChoices.getIndexCard());
    			}else if (playerChoices.getIndexCard()!=-1){
    				sendProperty("defCardPlayed",playerChoices.getIndexCard());
    			}
    			break;
    		case PlayersChoices.INFORMATION_CHECK:
    			BattlePvP battle=((BattlePvP)model.getBattle());
    			sendProperty("InformationCheck",getSeatNum());
    			if(battle.getAttFamily().isInfoCheck() && battle.getDefFamily().isInfoCheck() && battle.playerPartisipate(getSeatNum())){
    				battle.nextPhase();
    			}
    			break;
    		case PlayersChoices.VALYRIAN_SWORD_USE: 
    			model.getBattle().useSword();
    			sendProperty("useSword", 0);
    			break;
    		case PlayersChoices.VALYRIAN_SWORD_NOT_USE :
    			model.getBattle().dontUseSword();
    			sendProperty("dontUseSword", 0);
    			break;
    		case PlayersChoices.CONSOLID :
    			sendProperty("consolidation", playerChoices.getRelatedTerr().getName());
    			model.getFamily(getSeatNum()).addInflu(playerChoices.getRelatedTerr().consolidation());
    			model.updateLabel();
    			playerChoices.getRelatedTerr().rmOrder();
    			model.nextPlayer();
    			break;
    		case PlayersChoices.RECRUIT_FOOT :
    			if(model.checkSupplyLimits(getSeatNum(), playerChoices.getRelatedTerr())){
    				playerChoices.getRelatedTerr().recruit(1);
    				sendProperty("recruitFoot", playerChoices.getRelatedTerr().getName());
    				if(model.getPhase()==ModelState.MUSTERING && model.allRecruitementDone()){
    					sendProperty("allRecruitementDone", 0);
    					nextWesterosPhase();
    				}else if(model.getPhase()!=ModelState.MUSTERING && playerChoices.getRelatedTerr().getOrder()==null ){
    					model.nextPlayer();
    					sendProperty("nextPlayer", 0);
    				}
    			}
    			break;
    		case PlayersChoices.RECRUIT_KNIGHT :
    			if(model.checkRecrutment(getSeatNum(), playerChoices.getRelatedTerr())){
    				playerChoices.getRelatedTerr().recruit(2);
    				sendProperty("recruitKnight", playerChoices.getRelatedTerr().getName());
    				if(model.getPhase()==ModelState.MUSTERING && model.allRecruitementDone()){
    					sendProperty("allRecruitementDone", 0);
    					nextWesterosPhase();
    				}else if(model.getPhase()!=ModelState.MUSTERING && playerChoices.getRelatedTerr().getOrder()==null){
    					model.nextPlayer();
    					sendProperty("nextPlayer", 0);
    				}}
    			break;
    		case PlayersChoices.RECRUIT_SEIGE :
    			if(model.checkRecrutment(getSeatNum(), playerChoices.getRelatedTerr())){
    				playerChoices.getRelatedTerr().recruit(3);
    				sendProperty("recruitTower", playerChoices.getRelatedTerr().getName());
    				if(model.getPhase()==ModelState.MUSTERING && model.allRecruitementDone()){
    					sendProperty("allRecruitementDone", 0);
    					nextWesterosPhase();
    				}else if(model.getPhase()!=ModelState.MUSTERING && playerChoices.getRelatedTerr().getOrder()==null){
    					model.nextPlayer();
    					sendProperty("nextPlayer", 0);
    				}}
    			break;
    		case PlayersChoices.WESTEROS_CARD_SAW: 
    			this.westerosCardSaw();
    			break;
    		case PlayersChoices.BID_CHOSED :
    			model.getFamily(getSeatNum()).setBid(playerChoices.getBid());
    			sendProperty("bidSet", getSeatNum(),playerChoices.getBid());
    			playerChoices.blank();
    			break;
    		case PlayersChoices.LETTER_A_CHOSE :
    			model.westerosCardChoice(true);
    			sendProperty("westerosCardChoiceSelected", 1);
    			if(model.getWesterosPhase()==2){
    				playerChoices.bidding();
    				sendProperty("ClashOfKings", 0);
    			}else if (model.getPhase()!=ModelState.SUPPLY_TO_LOW){
    				nextWesterosPhase();
    			}
    			break;
    		case PlayersChoices.LETTER_B_CHOSE :
    			sendProperty("westerosCardChoiceSelected", 0);
    			model.westerosCardChoice(false);
    			if(model.getWesterosPhase()!=1){
    				nextWesterosPhase();
    			}
    			break;
    		case PlayersChoices.LETTER_C_CHOSE :
    			if(model.getWesterosPhase()!=3){
    				model.wildingsGrow();
    				sendProperty("wildingsGrow", 0);
    			}
    			nextWesterosPhase();
    			break;
    		case PlayersChoices.TRACK_SORTED:
    			//on envoi l'ordre des joueurs
    			for(Family fam : model.getBidding().getTrack()){
    				sendProperty("bid track", fam.getPlayer());
    			}
    			sendProperty("bid resolution", 0);
    			if(model.getBidding() instanceof BiddingAgainstWild){
    				wilidingBattleResolution();
    			}else{
    				if( model.biddingResolution()<3){
    					playerChoices.bidding();
    				}else{
    					nextWesterosPhase();
    				}
    			}
    			break;
    		case PlayersChoices.WILDINGS_CARD_SAW:
    			model.getFamily(getSeatNum()).infoCheck(); 
    			sendProperty("cardSaw", getSeatNum()); 
    			playerChoices.blank2();
    			//verifie si tout le monde a executé
    			if(model.westerosCardcheck()){
    				sendProperty("wilidingBattle", 0);
    				if(model.biddingSort() ){
    					wilidingBattleResolution();
    				}else if( model.haveThrone(getSeatNum())){
    					playerChoices.biddingEgality(model.getBidding());
    				}
    			}
    			break;
    		case PlayersChoices.REMOVE_SHIP:
    			playerChoices.getRelatedTerr().removeTroop(0);
    			sendProperty("remove_ship", playerChoices.getRelatedTerr().getName());
    			break;
    		case PlayersChoices.REMOVE_FOOT:
    			playerChoices.getRelatedTerr().removeTroop(1);
    			sendProperty("remove_foot", playerChoices.getRelatedTerr().getName());
    			break;
    		case PlayersChoices.REMOVE_KNIGHT:
    			playerChoices.getRelatedTerr().removeTroop(2);
    			sendProperty("remove_knight", playerChoices.getRelatedTerr().getName());
    			break;
    		case PlayersChoices.REMOVE_SIEGE:
    			playerChoices.getRelatedTerr().removeTroop(3);
    			sendProperty("remove_siege", playerChoices.getRelatedTerr().getName());
    			break;
    		case PlayersChoices.USE_INF_TOKEN:
    			this.useInfluToken(playerChoices.getRelatedTerr());
    			sendProperty("use_inf_token", playerChoices.getRelatedTerr().getName());
    			playerChoices.blank();
    			break;
    		case PlayersChoices.DONT_USE_INF_TOKEN:
    			this.dontUseInfluToken(playerChoices.getRelatedTerr());
    			sendProperty("dont_use_inf_token", playerChoices.getRelatedTerr().getName());
    			playerChoices.blank();
    			break;
    		case PlayersChoices.PUT_CARD_BOTTOM:
    			sendProperty("SendWildingsDeck",0);
    			for (String card : model.putCardOnBottom(playerChoices.getWildingsCard())){
    				sendProperty("AddWildingsCard", card);
    			}
    			model.goToExecutionPhase();
    			sendProperty("goToExecutionPhase",0);
    			break;
    		case PlayersChoices.PUT_CARD_TOP:
    			sendProperty("SendWildingsDeck",0);
    			for (String card : model.putCardOnTop(playerChoices.getWildingsCard())){
    				sendProperty("AddWildingsCard", card);
    			}
    			model.goToExecutionPhase();
    			sendProperty("goToExecutionPhase",0);
    			break;
    		}
    		if(model.getBattle()!=null){
    			switch(model.informations(getSeatNum())){
    			case Battle.BATTLE_SHOW_CARDS:
    				playerChoices.ShowBothBattleCards(model.getBattle());
    				sendProperty("BattleShowBothCards",0);
    				break;
    			case Battle.BATTLE_SHOW_RESOLUTION:
    				playerChoices.setPanel(PlayersChoices.DISPLAY_BATTLE_RESOLUTION);
    				sendProperty("BattleShowResolution",0);
    				break;
    			case Battle.BATTLE_CHOOSE_CARD:
    				if(model.getBattle().canPlayCard(model.getFamily(getSeatNum()))){playerChoices.showHouseCards(model.getBattle());}
    				sendProperty("FamilyCards", 0);
    				break;
    			case Battle.BATTLE_PLAY_SWORD:
    				playerChoices.swordPlay(model.getFamily(getSeatNum()));
    				sendProperty("Sword", 0);
    				break;
    			case Battle.BATTLE_WITHDRAWAL:
    				playerChoices.withdrawal(model.getFamily(getSeatNum()), model.getBattle());
    				sendProperty("withdraw", 0);
    				break;
    			case Battle.BATTLE_END:
    				model.battleEnd();
    				sendProperty("BattleEnd", 0);// VRAIMENT Necessaire ?!!
    				break;
    			}
    		}
    		if(model.getPhase()==ModelState.MUSTERING){
    			//we do nothing
    		}else if (model.getPhase()==ModelState.SUPPLY_TO_LOW){
    			this.checkSupplyLimit();
    		}else if(model.checkNewTurn() && model.getWesterosPhase()==0){
    			nextWesterosPhase();
    		}else if(model.getBidding()!=null && playerChoices.getPanel()!=16 && model.getBidding().allBidsDone() && playerChoices.getWildingsCard()==null){
    			if(model.getBidding() instanceof BiddingAgainstWild ){
    				String card = model.choseWildingCard();
    				playerChoices.wilidingsCard(card);
    				sendProperty("WildingsCard",card);
    			}else{
    				sendProperty("bid done",0);	
    				if(model.biddingSort() ){//egality and player can chose
    					if( model.biddingResolution()<3){
    						playerChoices.bidding();
    					}else {
    						nextWesterosPhase();
    					}
    				}else if( model.haveThrone(getSeatNum())){
    					playerChoices.biddingEgality(model.getBidding());
    				}
    			}
    		}
    	}
    }
    
   

	private void westerosCardSaw(){
    	if(model.getCurrentCard().equals("Summer")){
			model.wildingsGrow();
		}else if(model.getCurrentCard().equals("Supply")){
			model.supplyUpdate();
		}else if(model.getCurrentCard().equals("GameOfThrones")){
			model.westerosCardGameOfThrones();
		}else if(model.getCurrentCard().equals("FeastForCrows")){
			model.westerosCardFeastForCrows();
		}else if(model.getCurrentCard().equals("RainsOfAutumn")){
			model.westerosCardRainsOfAutumn();
		}else if(model.getCurrentCard().equals("SeaOfStorms")){
			model.westerosCardSeaOfStorms();
		}else if(model.getCurrentCard().equals("StromOfSwords")){
			model.westerosCardStormOfSwords();
		}else if(model.getCurrentCard().equals("WebOfLies")){
			model.westerosCardWebOfLies();
		}
		model.getFamily(getSeatNum()).infoCheck();
		sendProperty("cardSaw", getSeatNum());
		playerChoices.blank();
		//verifie si tout le monde a executé et qu'on est tjs dans un la phase westeros (cad aucun effet d'autre carte n'a pertubé le cours normal)
		if(model.westerosCardcheck() && model.getPhase()==ModelState.PHASE_WESTEROS){
			if(model.getCurrentCard().equals("DarkWingsDarkWords")||model.getCurrentCard().equals("PutToTheSword")||model.getCurrentCard().equals("ThroneOfBlades") ){
				if( model.canChose((getSeatNum()))){
					playerChoices.westerosCardChoice();
				}else{
					sendProperty("WesterosCardChoice", 0);
				}
			}else if(model.getCurrentCard().equals("Wildings")){
				playerChoices.bidding();
				model.wildingsAttack();
				sendProperty("WildingsAttack", 0);
			}else if(model.getCurrentCard().equals("ClashOfKings")){
				model.westerosCardClashOfKings();
				playerChoices.bidding();
				sendProperty("ClashOfKings", 0);
			}else if (model.getCurrentCard().equals("Mustering")){
				model.westerosCardMustering();
				sendProperty("Mustering", 0);
			}else if (model.getCurrentCard().equals("Winter")){
				model.westerosCardWinter();
				nextWesterosPhase();
			}else {
				nextWesterosPhase();
			}
		}
    }
    
    /**
     * 
     */
    public void mouseClicked(MouseEvent e){
    	
    	if (e.getButton()==MouseEvent.BUTTON1 && isGamePlaying()){
    		//on regarde sur quoi on a clicke
    		if(e.getComponent()==gameOfThronesComponent){
    			this.mouseClickOnBoard(e);
    		}else if (e.getComponent()==playerChoices){
    			mouseClickOnPlayerChoices (e);	
    		}
    		gameOfThronesComponent.repaint(); 
			playerChoices.repaint(); 
    	}
    }
   
    

	/**
     * Implementation of the mouse pressed interface.
     *
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(MouseEvent e) {
    	//mettre en click
    	//System.out.println("joueur num :"+getSeatNum());
        if (/*isGamePlaying() && isThisPlayersTurn () &&*/ e.getButton()==MouseEvent.BUTTON3) {
            // get mouse co-ordinates
            int x = e.getX();
            int y = e.getY();
            
            System.out.println ("Mouse pressed x: " + x + " y:" + y);
          
        }
    }


    // Receive
    public void receiveProperty(String key, String value){ // changer par Value
    	if(key.equals("AddWildingsCard")){
    		model.addWidingsCard(value);
    	}else if(key.equals("cancelOrder")){
    		model.getBoardModel().getTerritory(value).rmOrder();
    		model.nextPlayer();
    	}else if(key.equals("mvInitiated")){
    		model.mvInitiated(model.getBoardModel().getTerritory(value));
    	}else if(key.equals("mvInitiated2")){
    		model.mvInitiated2(model.getBoardModel().getTerritory(value));
    	}else if(key.equals("battleInitiated")){
    		model.battleInitiated(model.getBoardModel().getTerritory(value));
    	}else if(key.equals("attSupport")){
    	model.getBattle().addAttSupport(model.getBoardModel().getTerritory(value));
    	}else if(key.equals("defSupport")){
        	model.getBattle().addDefSupport(model.getBoardModel().getTerritory(value));
    	}else if(key.equals("noSupport")){
    		model.getBoardModel().getTerritory(value).getOrder().setUse(true);
    	}else if(key.equals("withdraw")){
    		model.getBattle().withdraw(model.getBoardModel().getTerritory(value));
			model.battleEnd();
			gameOfThronesComponent.repaint();
    	}else if(key.equals("consolidation")){
    		Territory terr =model.getBoardModel().getTerritory(value);
    		terr.getFamily().addInflu(terr.consolidation()); model.updateLabel();
    		terr.rmOrder();playerChoices.blank();
    		model.nextPlayer();
    	}else if (key.equals("recruitFoot")){
    		model.getBoardModel().getTerritory(value).recruit(1);
    	}else if (key.equals("recruitKnight")){
    		model.getBoardModel().getTerritory(value).recruit(2);
    	}else if (key.equals("recruitTower")){
    		model.getBoardModel().getTerritory(value).recruit(3);
    	}else if (key.equals("WesterosCard")){
    		playerChoices.westerosCard(value);
    		model.removeCard(value);
    	}else if (key.equals("WildingsCard")){
    		playerChoices.wilidingsCard(value);
    		model.removeWildingCard(value);
    	}else if (key.equals("Winter")){
    		model.westerosCardWinter();
    		playerChoices.westerosCard(value);
    		model.removeCard(value);
    	}else if(key.equals("recruitmentDone")){
    		model.getBoardModel().getTerritory(value).recruitmentDone();
    	}else if(key.equals("recruitShipFrom")){
    		model.getBoardModel().getTerritory(value).recruit(0);
    	}else if(key.equals("recruitShipAt")){
    		model.getBoardModel().getTerritory(value).recruit(0);
    	}else if (key.equals("setFamily")){
    		temp=value;
    	}else if (key.equals("setFamilyOn")){
    		model.getBoardModel().getTerritory(value).setFamily(model.getFamily(temp));
    	}else if(key.equals("remove_ship")){
    		model.getBoardModel().getTerritory(value).removeTroop(0);
    	}else if(key.equals("remove_foot")){
    		model.getBoardModel().getTerritory(value).removeTroop(1);
    	}else if(key.equals("remove_knight")){
    		model.getBoardModel().getTerritory(value).removeTroop(2);
    	}else if(key.equals("remove_siege")){
    		model.getBoardModel().getTerritory(value).removeTroop(3);
    	}else if(key.equals("use_inf_token")){
    		this.useInfluToken(model.getBoardModel().getTerritory(value));
    	}else if(key.equals("dont_use_inf_token")){
    		this.dontUseInfluToken(model.getBoardModel().getTerritory(value));
    	}else{
    		model.getBoardModel().getTerritory(key).useOrderOn(model.getBoardModel().getTerritory(value));
    		model.nextPlayer();
    	}
    	gameOfThronesComponent.repaint(); 
		playerChoices.repaint();
    }
    
    /**
     * This method is for the process of using an influence token on a territory
     * @param territory the territory on which the influence token is put
     */
    private void useInfluToken(Territory territory){
    	((Land) territory).setInfluenceToken(true);
		territory.getFamily().addInflu(-1);
		if(model.getBattle()==null){
			model.setPhase(ModelState.PHASE_EXECUTION);
			model.nextPlayer();	
		}else{
			model.setPhase(ModelState.BATTLE);
		}
    }
    
    /**
     * This method call when a player had the choice but refuse to use an influence token on a territory
     * @param territory the territory on which the influence token is not put
     */
    private void dontUseInfluToken(Territory territory){
		territory.removeOwner();
		if(model.getBattle()==null){
			model.setPhase(ModelState.PHASE_EXECUTION);
			model.nextPlayer();
		}else{
			model.setPhase(ModelState.BATTLE);
		}
    }
    //
    public void receiveProperty(String key, int type, int bonus) {
    	if(key.equals("bidSet")){
    		model.getFamily(type).setBid(bonus);
    	}else if (key.equals("wildlings_PChoice")){
    		wildingsActionPChoice(type, bonus);
    	}else{// peut-etre mettre cette methode dans Order
    		boolean star = (bonus==1);
    		switch (type) {
    		case 0:
    			model.getBoardModel().getTerritory(key).setOrder(new Order(star, 0, 0, OrderType.RAI));
    			break;
			case 4:
				model.getBoardModel().getTerritory(key).setOrder(new Order(star, 0, bonus, OrderType.SUP));
				break;
			case 1:
				model.getBoardModel().getTerritory(key).setOrder(new Order(star, 0, bonus, OrderType.ATT));
				break;
			case 2:
				model.getBoardModel().getTerritory(key).setOrder(new Order(star, 0, 0, OrderType.CON));
				break;
			default:
				model.getBoardModel().getTerritory(key).setOrder(new Order(star, bonus+1, 0, OrderType.DEF));
				break;
			}
    	}
    	gameOfThronesComponent.repaint(); 
		playerChoices.repaint(); 
	}
    
    
     public void receiveProperty (String key, int value) { 
    	 if(key.equals("SendWildingsDeck")){
    		 model.reinitializeDeck();
    	 }else if(key.equals("HouseCardsSaw")){
    		 model.getFamily(value).infoCheck();
    		 BattlePvP battle=((BattlePvP)model.getBattle());
    		 if(battle.getAttFamily().isInfoCheck() && battle.getDefFamily().isInfoCheck() && battle.playerPartisipate(getSeatNum())){
    			 battle.nextPhase();
    		 }
    	 }else if (key.equals("BattleShowResolution")){
    		 playerChoices.setPanel(PlayersChoices.DISPLAY_BATTLE_RESOLUTION);
    	 }else if(key.equals("BattleShowBothCards")){
    		 playerChoices.ShowBothBattleCards(model.getBattle());
    	 }else if(key.equals("goToExecutionPhase")){
    		 model.goToExecutionPhase();
    	 }else if (key.equals("nextPlayer")){
    		 model.nextPlayer();
    	 }else if (key.equals("nextPhase")){
    		 model.nextPhase();
    	 }else if(key.equals("troopSend")){
     		int[]troops= new int[4];
     		troops[value]=1;
     		model.troopSend(troops[0],troops[1],troops[2],troops[3]);
    	 }else if(key.equals("FamilyCards")){
    		 if(model.getBattle().canPlayCard(model.getFamily(getSeatNum()))){playerChoices.showHouseCards(model.getBattle());}
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
    	}else if (key.equals("withdraw")){
    		playerChoices.withdrawal(model.getFamily(getSeatNum()), model.getBattle());
    	}else if (key.equals("cardSaw")){
    		model.getFamily(value).infoCheck();
    	}else if(key.equals("ClashOfKings")){
    		model.westerosCardClashOfKings();
			playerChoices.bidding();
    	}else if(key.equals("Mustering")){
    		model.westerosCardMustering();
    	}else if(key.equals("allRecruitementDone")){
    		model.allRecruitementDone();
    	}else if(key.equals("bid done")){
    		if(model.biddingSort() ){//egality and player can chose
				if( model.biddingResolution()<3){
					playerChoices.bidding();
				}
			}else if( model.haveThrone(getSeatNum())){
				playerChoices.biddingEgality(model.getBidding());
			}
		}else if(key.equals("bid track")){
			model.getBidding().nextFamily(value);
		}else if(key.equals("bid resolution")){
			if(model.getBidding() instanceof BiddingAgainstWild){
				wilidingBattleResolution();
			}else if( model.biddingResolution()<3){
				playerChoices.bidding();
			}
		}else if (key.equals("WesterosCardChoice")){
			if( model.canChose(getSeatNum())){
				playerChoices.westerosCardChoice();
			}
		}else if (key.equals("westerosCardChoiceSelected")){
			model.westerosCardChoice(value==1);
		}else if(key.equals("wildingsGrow")){
    		model.wildingsGrow();
    	}else if(key.equals("WildingsAttack")){
			playerChoices.bidding();
			model.wildingsAttack();
		}else if (key.equals("wilidingBattle")){
			if(model.biddingSort() ){
				wilidingBattleResolution();
			}else if( model.haveThrone(getSeatNum())){
				playerChoices.biddingEgality(model.getBidding());
			}
		}else if (key.equals("endProg")){
    		model.getFamily(value).ordersGiven();
    		model.endProg();
    		if(model.getPhase()==ModelState.CROW_CHOICE) playerChoices.crowChoice();
        }else{ // Wildlings card resolution
        	callWildlingsAction(model.getBoardModel().getTerritory(key), value);
        }
    	gameOfThronesComponent.repaint(); 
    	playerChoices.repaint();  
       }
    
    
    
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
    
    /** this method is call for the resolution of the wilding attack, after the bid, it applies the effects of the card and go to next proper phase (picking a new westeros card or going to the execution's phase) */
	private void wilidingBattleResolution() {
		if(playerChoices.getWildingsCard().equals("PreemptiveRaid") && ((BiddingAgainstWild)model.getBidding()).victory()){
			Family winFamily=model.getBidding().getTrack()[0];
			model.preemptiveRaid(winFamily);
			if(getSeatNum()!=winFamily.getPlayer()){
				playerChoices.bidding();
			}
		}else{
			model.wildingsResolution(playerChoices.getWildingsCard(), playerChoices);
			if(model.getPhase()!=ModelState.WILDLINGSRESOLUTION && model.getPhase()!=ModelState.SUPPLY_TO_LOW){//case when the wildings resolution is directly solve
				playerChoices.blank();
				if(model.getWesterosPhase()==3){
					model.nextPhase();
				}else if(getSeatNum()==0){ /*case when the wildings where attacking beacause of the 12 power*/
					String card = model.choseCard();
					playerChoices.westerosCard(card);
					sendProperty("WesterosCard",card);
				}
			}
		}
	}		
	
	/** This method is use to put a new ship on the board 
	 * this method must be call when a player click on the board after selecting a ship to muster. (there is no verification if the player can muster and have selected a ship in this method)
	 *@param e  the coordinate of the territory where the ship is put*/
	private void musterShip(MouseEvent e){
		Territory water = gameOfThronesComponent.getTerritory(e.getX(),e.getY());
		if(water instanceof Water &&((Water) water).canRecruitShipHere(playerChoices.getRelatedTerr()) && model.checkSupplyLimits(getSeatNum(),water)){
			//recruitement
			water.setFamily(playerChoices.getRelatedTerr().getFamily());
			water.recruit(0);
			playerChoices.getRelatedTerr().recruit(0);
			sendProperty("setFamily",playerChoices.getRelatedTerr().getFamily().getName());
			sendProperty("setFamilyOn",water.getName());
			sendProperty("recruitShipAt", water.getName());
			sendProperty("recruitShipFrom", playerChoices.getRelatedTerr().getName());
			if(model.getPhase()==ModelState.MUSTERING && model.allRecruitementDone()){
				sendProperty("allRecruitementDone", 0);
				nextWesterosPhase();
			}else if(model.getPhase()!=ModelState.MUSTERING && playerChoices.getRelatedTerr().getOrder()==null ){
				model.nextPlayer();
				sendProperty("nextPlayer", 0);
			}
		}
		playerChoices.setShipSelected(false);
	}
	/*This method is use to know which card must be the next or if we have to go to the next phase*/
	public void nextWesterosPhase(){
		if(model.getWildings()>=12) wildingsAttack();
		else if (model.getWesterosPhase()==3){
			model.nextPhase();
			sendProperty("nextPhase", 0);
		}
		else nextWesterosCard(); 
	}
	/*this method pick a new westeros card and send it to all the players*/
	private void nextWesterosCard(){
		String card = model.choseCard();
		playerChoices.westerosCard(card);
		sendProperty("WesterosCard",card);
	}
	/*This method is call when there is a wilding attack*/
	private void wildingsAttack(){
		playerChoices.bidding();
		model.wildingsAttack();
		sendProperty("WildingsAttack", 0);
	}
	
    private void checkSupplyLimit(){
    	if(model.checkSupplyLimits()){
			playerChoices.blank2();
			nextWesterosPhase();
		}
    }
    
    /*This method is call when a player click on a territory during a wildlings resolution phase*/
    private void wildlingsAction(Territory territory, int player){
    	int res =model.getWildingsResolution().actionOnBoard(territory, player);
    	playerChoices.setPanel(res,model.getWildingsResolution());
    	model.updateLabel();
    }
    /*This method is call when reciving a property after a player click on a territory during a wildlings resolution phase*/
    private void callWildlingsAction(Territory territory, int player){
    	model.getWildingsResolution().actionOnBoard(territory, player);
    	model.updateLabel();
    }
    
    /*This method is call when a player click on a territory during a wildlings resolution phase*/
    private void wildingsActionPChoice(int choice, int player) {
    	model.getWildingsResolution().actionOnPChoice(choice, player);
		model.updateLabel();
	}
    
 // Check to see if the game is over or not.
    private void checkGameOver () {

        // Status is either -1, DRAW or WIN
        int status = -1;
        if (model.isGameWon (getSeatNum()))
            status = CommGameOver.WIN;

        // Create game over object if a win or draw
        if (status != -1 && conn != null) {
        	System.out.println("Game over!!!");
            CommGameOver gameOver = new CommGameOver (status);
            conn.send (gameOver);
        }
    }
}
