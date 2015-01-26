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

import javax.swing.JLabel;

import org.jogre.gameOfThrones.common.Bidding;
import org.jogre.gameOfThrones.common.BiddingAgainstWild;
import org.jogre.gameOfThrones.common.Deck;
import org.jogre.gameOfThrones.common.Family;
import org.jogre.gameOfThrones.common.GameOfThronesModel;
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

import state.ModelState;


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
    /*this boolean is use to say */
    private Boolean shipRecrutement;
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
        shipRecrutement=false;
        
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
			
			if(this.shipRecrutement){
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
					if(model.getMusteringPhase() && model.allRecruitementDone()){
						sendProperty("allRecruitementDone", 0);
						String card = model.choseCard();
						playerChoices.westerosCard(card);
						sendProperty("WesterosCard",card);
					}else if(!model.getMusteringPhase() && playerChoices.getRelatedTerr().getOrder()==null ){
						model.nextPlayer();
						sendProperty("nextPlayer", 0);
					}
				}
				shipRecrutement=false;
			}else{
				switch(model.getPhase().getModelState()){
				case SUPPLY_TO_LOW:
					if(gameOfThronesComponent.getTerritory(e.getX(),e.getY()).getFamily()==model.getFamily(getSeatNum())){
						playerChoices.destroyTroop(gameOfThronesComponent.getTerritory(e.getX(),e.getY()));
					}
					break;
				case PHASE_WESTEROS: 
					if(model.canRecruit(gameOfThronesComponent.getTerritory(e.getX(),e.getY()), getSeatNum())){
						playerChoices.recruit(gameOfThronesComponent.getTerritory(e.getX(),e.getY()));
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
				case PHASE_EXECUTION:
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
								sendProperty("mvInitiated", playerChoices.getRelatedTerr().getName());
								sendProperty("battleInitiated", gameOfThronesComponent.getTerritory(e.getX(),e.getY()).getName());
							break;
						case 3://on a clicke sur le territoire de départ, on revient sur la croix
							playerChoices.orderSelected(playerChoices.getRelatedTerr());
							break;
						}
						gameOfThronesComponent.repaint();
						
					}else if(model.canPlayThisOrder(gameOfThronesComponent.getTerritory(e.getX(),e.getY()), getSeatNum())){
						playerChoices.orderSelected(gameOfThronesComponent.getTerritory(e.getX(),e.getY()));
					}
					break;
				}
			}
			if(model.checkNewTurn() && model.getWesterosPhase()==0){
				String card = model.choseCard();
				playerChoices.westerosCard(card);
				sendProperty("WesterosCard",card);
			}
		}
    }
    
    private void mouseClickOnPlayerChoices (MouseEvent e){

		// quand on click sur le playerChoice on reccupère un message de ce qui s'est passe 
		switch (playerChoices.RigthClick(e.getX(),e.getY(),model.getFamily(getSeatNum()))){
		case PlayersChoices.END_PROGRAMATION_PHASE :
			//Quand un joueur a donnée tous ses ordres (durant la phase1) on les envois et on l'indique au autres
			Family family =model.getFamily(getSeatNum());
			for(Territory territory : family.getTerritories() )
			{	
				int[] order =territory.getOrder().getOrderInt();
				sendProperty(territory.getName(),order[0],order[1]);
				}
				sendProperty ("endProg",getSeatNum());
				model.endProg();
			break;
		case PlayersChoices.CANCEL : 
			if(model.getPhase().getModelState()==ModelState.PHASE_WESTEROS){// on teste si on est dans la phase westeros, donc recrutement
				playerChoices.getRelatedTerr().recruitmentDone();
				sendProperty("recruitmentDone", playerChoices.getRelatedTerr().getName());
				if(model.allRecruitementDone()){//on teste si tous les recrutement sont bon
					sendProperty("allRecruitementDone", 0);
					String card = model.choseCard();
					playerChoices.westerosCard(card);
					sendProperty("WesterosCard",card);
				}
			}else if(model.getPhase().getModelState()==ModelState.SUPPLY_TO_LOW){
				playerChoices.blank2();
			}else{
				sendProperty("cancelOrder",playerChoices.getRelatedTerr().getName());
				playerChoices.getRelatedTerr().rmOrder();
				model.nextPlayer();
				playerChoices.blank();
			}
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
			if(model.getBattle()!=null){
				model.attPrepEnd();
				sendProperty("attPreparationEnded", 0);
			}else{
				model.resolutionPvE();
				sendProperty("resolutionPvE",0);
				playerChoices.blank2();
			}
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
			playerChoices.getRelatedTerr().getOrder().used();
			sendProperty("noSupport",playerChoices.getRelatedTerr().getName());
			break;
		case PlayersChoices.HOUSE_CARD_CHOSEN:
			if(model.getBattle().getAttFamily().getPlayer()==this.getSeatNum() && playerChoices.getIndexCard()!=-1){
				sendProperty("attCardPlayed",playerChoices.getIndexCard());
			}else if (playerChoices.getIndexCard()!=-1){
				sendProperty("defCardPlayed",playerChoices.getIndexCard());
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
			model.getFamily(getSeatNum()).gainInflu(playerChoices.getRelatedTerr().consolidation());
			model.updateLabel();
			playerChoices.getRelatedTerr().rmOrder();
			model.nextPlayer();
			break;
		case PlayersChoices.RECRUIT_SHIP :
			shipRecrutement=true;
			// add a message ?
			break;
		case PlayersChoices.RECRUIT_FOOT :
			if(model.checkSupplyLimits(getSeatNum(), playerChoices.getRelatedTerr())){
				playerChoices.getRelatedTerr().recruit(1);
				sendProperty("recruitFoot", playerChoices.getRelatedTerr().getName());
				if(model.getMusteringPhase() && model.allRecruitementDone()){
					sendProperty("allRecruitementDone", 0);
					String card = model.choseCard();
					playerChoices.westerosCard(card);
					sendProperty("WesterosCard",card);
				}else if(!model.getMusteringPhase() && playerChoices.getRelatedTerr().getOrder()==null ){
					model.nextPlayer();
					sendProperty("nextPlayer", 0);
				}
			}
			break;
		case PlayersChoices.RECRUIT_KNIGHT :
			if(model.checkSupplyLimits(getSeatNum(), playerChoices.getRelatedTerr())){
			playerChoices.getRelatedTerr().recruit(2);
			sendProperty("recruitKnight", playerChoices.getRelatedTerr().getName());
			if(model.getMusteringPhase() && model.allRecruitementDone()){
				sendProperty("allRecruitementDone", 0);
				String card = model.choseCard();
				playerChoices.westerosCard(card);
				sendProperty("WesterosCard",card);
			}else if(!model.getMusteringPhase() && playerChoices.getRelatedTerr().getOrder()==null){
				model.nextPlayer();
				sendProperty("nextPlayer", 0);
			}}
			break;
		case PlayersChoices.RECRUIT_SEIGE :
			if(model.checkSupplyLimits(getSeatNum(), playerChoices.getRelatedTerr())){
			playerChoices.getRelatedTerr().recruit(3);
			sendProperty("recruitTower", playerChoices.getRelatedTerr().getName());
			if(model.getMusteringPhase() && model.allRecruitementDone()){
				sendProperty("allRecruitementDone", 0);
				String card = model.choseCard();
				playerChoices.westerosCard(card);
				sendProperty("WesterosCard",card);
			}else if(!model.getMusteringPhase() && playerChoices.getRelatedTerr().getOrder()==null){
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
				if(model.getWesterosPhase()==3){
					model.nextPhase();
					sendProperty("nextPhase", 0);
				}else if(model.getWesterosPhase()==2){
					playerChoices.bidding();
					sendProperty("ClashOfKings", 0);
				}else if (model.getPhase().getModelState()!=ModelState.SUPPLY_TO_LOW){
					String card = model.choseCard();
					playerChoices.westerosCard(card);
					sendProperty("WesterosCard",card);
				}
				break;
		case PlayersChoices.LETTER_B_CHOSE :
				sendProperty("westerosCardChoiceSelected", 0);
				model.westerosCardChoice(false);
				if(model.getWesterosPhase()==3){
					model.nextPhase();
					sendProperty("nextPhase", 0);
				}else if(model.getWesterosPhase()==2){
					String card = model.choseCard();
					playerChoices.westerosCard(card);
					sendProperty("WesterosCard",card);
				}
			break;
		case PlayersChoices.LETTER_C_CHOSE :
				if(model.getWesterosPhase()==3){
					model.nextPhase();
					sendProperty("nextPhase", 0);
				}else{
					model.widingsGrow();
					String card = model.choseCard();
					playerChoices.westerosCard(card);
					sendProperty("WesterosCard",card);
				}
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
					String card = model.choseCard();
					playerChoices.westerosCard(card);
					sendProperty("WesterosCard",card);
				}
			}
			break;
		case PlayersChoices.WILDINGS_CARD_SAW:
			model.getFamily(getSeatNum()).carteVu(); 
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
		}
		if(model.getMusteringPhase()){
			//we do nothing
		}else if (model.getPhase().getModelState()==ModelState.SUPPLY_TO_LOW){
			this.checkSupplyLimit();
		}else if(model.checkNewTurn() && model.getWesterosPhase()==0){
			String card = model.choseCard();
			playerChoices.westerosCard(card);
			sendProperty("WesterosCard",card);
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
						String card = model.choseCard();
						playerChoices.westerosCard(card);
						sendProperty("WesterosCard",card);
					}
				}else if( model.haveThrone(getSeatNum())){
					playerChoices.biddingEgality(model.getBidding());
				}
			}
		}else{
			//le playerChoice verifie si il doit afficher quelque chose de nouveau
			switch(playerChoices.check(model.informations(getSeatNum()), model.getFamily(getSeatNum()), model.getBattle())){
			case 1:
				sendProperty("FamilyCards", 0);
				break;
			case 2:
				sendProperty("Sword", 0);
				break;
			case 3: // NE SERT A RIEN !!!!!
				//System.out.println("controller : cheked state 3");
				break;
			case 4:
				model.battleEnd();
				sendProperty("BattleEnd", 0);// VRAIMENT Necessaire ?!!
				break;
			}
		}
    	
    }
    
    private void westerosCardSaw(){
    	if(model.getCurrentCard().equals("Summer")){
			model.widingsGrow();
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
		model.getFamily(getSeatNum()).carteVu();
		sendProperty("cardSaw", getSeatNum());
		playerChoices.blank();
		//verifie si tout le monde a executé et qu'on est tjs dans un la phase westeros (cad aucun effet d'autre carte n'a pertubé le cours normal)
		if(model.westerosCardcheck() && model.getPhase().getModelState()==ModelState.PHASE_WESTEROS){
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
			}else if(model.getWesterosPhase()==3){
				model.nextPhase();
				sendProperty("nextPhase", 0);
			}else if(model.getCurrentCard().equals("ClashOfKings")){
				model.westerosCardClashOfKings();
				playerChoices.bidding();
				sendProperty("ClashOfKings", 0);
			}else if (model.getCurrentCard().equals("Mustering")){
				model.westerosCardMustering();
				sendProperty("Mustering", 0);
			}else if (model.getCurrentCard().equals("Winter")){
				model.westerosCardWinter();
				String card = model.choseCard();
				playerChoices.westerosCard(card);
				sendProperty("Winter",card);
			}else{
				String card = model.choseCard();
				playerChoices.westerosCard(card);
				sendProperty("WesterosCard",card);
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
    	if(key.equals("cancelOrder")){
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
    		model.getBoardModel().getTerritory(value).getOrder().used();
    	}else if(key.equals("withdraw")){
    		model.getBattle().withdraw(model.getBoardModel().getTerritory(value));
			model.battleEnd();
			gameOfThronesComponent.repaint();
    	}else if(key.equals("consolidation")){
    		Territory terr =model.getBoardModel().getTerritory(value);
    		terr.getFamily().gainInflu(terr.consolidation()); model.updateLabel();
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
    	}else{
    		model.getBoardModel().getTerritory(key).useOrderOn(model.getBoardModel().getTerritory(value));
    		model.nextPlayer();
    	}
    	
    }
    
    //
    public void receiveProperty(String key, int type, int bonus) {
    	if(key.equals("bidSet")){
    		model.getFamily(type).setBid(bonus);
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
	}
    
    
     public void receiveProperty (String key, int value) { 
    	 if (key.equals("nextPlayer")){
    		 model.nextPlayer();
    	 }else if (key.equals("nextPhase")){
    		 model.nextPhase();
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
    	}else if (key.equals("cardSaw")){
    		model.getFamily(value).carteVu();
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
		}else if (key.equals("WesterosCardChoice") && model.canChose(getSeatNum())){
			playerChoices.westerosCardChoice();
		}else if (key.equals("westerosCardChoiceSelected")){
			model.westerosCardChoice(value==1);
		}else if(key.equals("WildingsAttack")){
			playerChoices.bidding();
			model.wildingsAttack();
		}else if (key.equals("wilidingBattle")){
			if(model.biddingSort() ){
				wilidingBattleResolution();
			}else if( model.haveThrone(getSeatNum())){
				playerChoices.biddingEgality(model.getBidding());
			}
		}else if(key.equals("resolutionPvE")){
			model.resolutionPvE();
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
    
    /** Cette methode applique les effets de la carte sauvage a l'adversaire*/
	public void wilidingBattleResolution() {
		String card =playerChoices.getWildingsCard();
		Family[] families = model.getBidding().getTrack(); 

		if(((BiddingAgainstWild)model.getBidding()).victory()){
			System.out.println("Victoire garde de la nuit");
			if(card.equals("SkinchangerScout")){
				families[0].gainInflu(model.getBidding().getTrack()[0].getBid());
			}else if(card.equals("Massing on the Milkwater")){
				families[0].regainCombatantCards();
			}
			
			
			model.setWildings(0);
		}else{
			System.out.println("Victoire sauvages");
			if(card.equals("SkinchangerScout")){
				int i;
				for(i=0;i<families.length-1;i++){
					families[i].gainInflu(-2);
				}	
				families[i].gainInflu(families[i].getBid()-families[i].getInflu());
			}
			/*if(card.equals("Massing on the Milkwater")){
				
			}*/
			model.setWildings(model.getWildings()-4);
		}
		//on remet les paris à 0
		for(Family family : model.getBidding().getTrack()){
			family.resetBid();
		}
		//model.setCurrentCard(null);
		playerChoices.blank();
		//supprimer le bidding ? 
		model.updateLabel();
		
		if(model.getWesterosPhase()==3){
			model.nextPhase();
			//sendProperty("nextPhase", 0);
		}
	
		
	}		
    private void checkSupplyLimit(){
    	if(model.checkSupplyLimits()){
			playerChoices.blank2();
			if(model.getWesterosPhase()==1){
				//model.setPhase(GameOfThronesModel.PHASE_WESTEROS);
				String card = model.choseCard();
				playerChoices.westerosCard(card);
				sendProperty("WesterosCard",card);
			}
			//AUTRES CAS OU ON MET A JOUR LE ravitaillement ??
		}
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
