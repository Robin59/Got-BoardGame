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

import java.awt.Image;

import javax.swing.JLabel;

import nanoxml.XMLElement;

import org.jogre.client.awt.GameImages;
import org.jogre.common.JogreModel;
import org.jogre.common.comm.Comm;
import org.jogre.gameOfThrones.common.combat.BattlePvE;
import org.jogre.gameOfThrones.common.combat.BattlePvP;
import org.jogre.gameOfThrones.common.combat.GroundForce;
import org.jogre.gameOfThrones.common.combat.NavalTroup;
import org.jogre.gameOfThrones.common.orders.OrderType;
import org.jogre.gameOfThrones.common.territory.BoardModel;
import org.jogre.gameOfThrones.common.territory.HomeLand;
import org.jogre.gameOfThrones.common.territory.Land;
import org.jogre.gameOfThrones.common.territory.Territory;
import org.jogre.gameOfThrones.common.territory.Water;

import state.*;
import state.ModelState;

/**
 * Game model for the gameOfThrones game.
 * The game is working more or less like an finished automate, with a lot of state from which some transition are available,
 * so a lot of constant are use for represent this aspect of the game
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
	//private int[] supply; // tableau de la taille du nombre de joueur, le joueur 1 correspon a supply[1] et la valeur à son ravitaillement
	private int[] victory; // fonctione comme les supply
	// influence pist, ex: thrones(0)=2 means that the third player is first in throne track 
	private int[] throne;
	private int[] fiefdoms;
	private int[] court;
	//determine la phase de jeu (0: phase Westeros, 1= programation, 2: execution)
	private int phase;
	private int internPhase; // determine la phase dans la execution (0: raid, 1: mouvement, 2:consolidation) 
	private int westerosPhase; 
	private int biddingPhase;// O for the throne bidding, 1 for the fiefdoms, 2 for the court 
	private BoardModel boardModel;
	//Creation des fammilles
	private Family[] families;
	// pour les mouvements et combat
	private boolean mvInitiated;
	//private boolean musteringPhase;
	//private boolean clashOfKings;
	// Must make an Interface or abstract class for the 2 objects below
	private BattlePvP battle;
	private BattlePvE battlePvE;
	private Territory territory1;
	private Territory territory2;
	private JLabel jLabel;
	private Deck deck1;
	private Deck deck2;
	private Deck deck3;
	private Deck wildDeck;
	private String currentCard;
	private Bidding bidding;
	/*indicate the number of troops a player can have with is current supply */
	private int[][] supplyLimites= {{2,2,0,0,0},{3,2,0,0,0},{3,2,2,0,0},{3,2,2,2,0},{3,3,2,2,0},{4,3,2,2,0},{4,3,2,2,2}};
	/*indicate how many orders with stars a player can use*/
	private int[] starsLimitation;
	/*indicate the current state of the game(if we consider it as an finished automate)*/
	private ModelState state;
	
    /**
     * Constructor which creates the model.
     */
    public GameOfThronesModel (int numberPlayers, JLabel jLabel) {
        super (GAME_TYPE_TURN_BASED);
        this.jLabel=jLabel;
        // contruit les éléments générale du jeu
        this.numberPlayers=numberPlayers;
        throne= new int[numberPlayers];
        fiefdoms= new int[numberPlayers];
        court= new int[numberPlayers];
        boardModel = new BoardModel();// ajouter numberPlayers en parametre ????
        families=new Family[numberPlayers];
        starsLimitation=new int[numberPlayers];
        // le jeu commence à la phase programation
        phase=1;
        state=ModelState.PHASE_PROGRAMATION;
        internPhase=0;
        biddingPhase=3;
        turn=1;
        wildings=2;
        currentPlayer=0;
        mvInitiated=false;
        // musteringPhase=false;
        battle=null;
        deck1=new Deck(1);
        deck2=new Deck(2);
        deck3=new Deck(3);
        wildDeck=new Deck(0);
     // placer le plateau en fonction du jeu
        familiesConstruction(numberPlayers);
        updateLabel();
    }


    public void nextInternPhase(){
    	internPhase=(internPhase+1)%3;
    	currentPlayer=0;
    	
    	if(internPhase==1){
    		checkOrder();
    	}else if (internPhase==2){
    		System.out.println("Consolidation !");
    		//on retire les ordres consolidations(sans étoiles) pour chaque familles
    		for(Family family : families){
    			for (Territory territory : family.getTerritories()){
    				if(territory.getOrder()!=null && !territory.getOrder().getStar() && territory.getOrder().getType()==OrderType.CON){
    					family.addInflu(territory.consolidation());
    					territory.rmOrder();
    				}
    			}
    		}
    		updateLabel();
    		checkOrder();
    	}else if (internPhase==0){
    		nextPhase();
    	}
    	//System.out.println("nextInternPhase");
    }
    public void nextPhase(){
    	phase=(phase+1)%3;
    	state=ModelState.values()[phase];
    	//AJOUTER DES CHOSES EN FONCTION DES PHASES 
    	if(phase==2){
    		//on initialise
    		 internPhase=0;
    		 currentPlayer=0;
    		 // on cherche le premier joueur a avoir un ordre de raid
    		 checkOrder();
    	}else if(phase==0){
    		//on retire tous les ordres des territoires et on les redonnes aux joueurs
    		for (Family family : families){
    			family.ordersBack();
    		}
    		turn++;//nouveau tour
    		westerosPhase=0;
    		this.westerosCardNotSaw();
    		updateLabel();
    		//ici on test si on arrive au tour 11 et on fini le jeu dans ce cas
    		
    	}
    	updateLabel();
    }
    
  
   


	private void checkOrder(){
    	int i =0;
    	while (!checkCurrentPlayerOrder() && i<=numberPlayers){
    		i++;
			 currentPlayer=(currentPlayer+1)%numberPlayers;
		 }
		 if (i>numberPlayers){
			 nextInternPhase();
		 }
    }
   /**
    * check if the current player can play an order during this part of the execution's phase
    * @return true if the current player can play an order during this part of the execution's phase
    */
    private boolean checkCurrentPlayerOrder(){
    	for (Territory territory :families[getCurrentPlayer()].getTerritories()){
    		if (territory.getOrder()!=null && territory.getOrder().getType().ordinal()==internPhase){
    			return true;
    		}
    	}
    	return false;
    }
    
    
    
    
    public void nextPlayer(){
    	currentPlayer = (currentPlayer+1)%numberPlayers;
    	mvInitiated=false;
    	checkOrder();
    	updateLabel();
    }
    
    /** be careful this method return a playerSeat, not is place on the throne track*/ 
    public int getCurrentPlayer(){
    	return throne[currentPlayer];
    }
    
    /**
     * This method tell if an order can be given by a player to territory
     * @param territory the territory where the player want to put his order
     * @param player the player who want to put an order on a territory 
     * @return true if the player can put an order on the territory 
     */
    public boolean canGiveOrder(Territory territory, int player){
    	return state==ModelState.PHASE_PROGRAMATION && territory.getTroup()!=null &&territory.getFamily().getPlayer()==player;
    }

    public boolean canPlayThisOrder(Territory territory, int seatNum) {
		return ( territory.getFamily()!=null && territory.getFamily().getPlayer()==seatNum && seatNum==getCurrentPlayer() &&  territory.getOrder()!=null &&territory.getOrder().getType().ordinal()==internPhase ) ;
	}
    
    
	public BoardModel getBoardModel() {
		return boardModel;
	}


	public Family getFamily(int seatNum) {
		return families[seatNum];
	}

	public Family getFamily(String familyName){
		for(Family family : families){
			if (family.getName().equals(familyName))
				return family;
		}
		return null;
	}
	
	/**on verifie que tous les joueurs on donné leurs ordres.
	 * Quand c'est le cas on passe à la phase suivante
	 */
	public void endProg(){
		boolean res=true;
		for(Family family : families){
			if(!family.getOrdersGiven()){
				res=false;
			}
		}
		if(res){
			nextPhase();
		}
	}
	
	/** this method indicate to the model that a move as been selected from the territory*/
	// il faut empecher de declancher un mouvement une fois qu'il est commencé
	public void mvInitiated(Territory fromTerritory,Territory toTerritory){
		mvInitiated=true;
		territory1=fromTerritory;
		territory2=toTerritory;
	}
	// same as above but in two time
	public void mvInitiated(Territory fromTerritory){
		mvInitiated=true;
		territory1=fromTerritory;
	}
	public void mvInitiated2(Territory toTerritory){
		territory2=toTerritory;
	}
	
	public boolean getMvInitiated(){
		return mvInitiated;
	}
	
	/***/
	public void battle(Territory fromTerritory,Territory toTerritory) {
		territory1=fromTerritory;
		territory2=toTerritory;
		if(toTerritory.getNeutralForce()>0){
			battlePvE= new BattlePvE(fromTerritory, toTerritory);
		}else{
			battle = new BattlePvP(fromTerritory, toTerritory);
		}
		
	}
	public void battleInitiated(Territory territory) {
		territory2=territory;
		if(territory2.getNeutralForce()>0){
			battlePvE = new BattlePvE(territory1, territory2);
		}else{
			battle = new BattlePvP(territory1, territory2);
		}

		
	}

	
	
	
	

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
    public void reset () {
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

	



  //sert a la construction du plateau en fonction du nb de joueur
  	private void familiesConstruction(int playerNumber){
  		//the stars limitation (change after 4 players)
  		starsLimitation[0]=3;
  		starsLimitation[1]=2;
  		
  		//House Baratheon
  		families[0]=new Family(0,this);
  		throne[0]=0;
  		fiefdoms[1]=0;
  		court[1]=0;
  		families[0].setFiefdomsTrack(2);
  		boardModel.getTerritory("Karhold").setTroup(new GroundForce(families[0],1,1,0));
  		boardModel.getTerritory("Narrow Sea").setTroup(new NavalTroup(families[0],2));
  		//Baratheon's cards
  		families[0].addCard(new CombatantCard("Mellissandre",1, 1, 0));
        families[0].addCard(new CombatantCard("Salladhor",1, 0, 0));
        families[0].addCard(new CombatantCard("Davos",2, 0, 0));
        families[0].addCard(new CombatantCard("Brienne",2, 1, 1));
  		// House Starks 
  		families[1]=new Family(1,this);
  		throne[1]=1;
  		fiefdoms[0]=1;
  		court[1]=1;
  		families[1].setFiefdomsTrack(1);
        boardModel.getTerritory("Winterfell").setTroup(new GroundForce(families[1],1,1,0));
        boardModel.getTerritory("White Harbor").setTroup(new GroundForce(families[1],1,0,0));
        boardModel.getTerritory("Shivering Sea").setTroup(new NavalTroup(families[1],1));
        // Stark's cards
        families[1].addCard(new CombatantCard("Catelyn",0, 0, 0));
        families[1].addCard(new CombatantCard("BlackFish",1, 0, 0));
        families[1].addCard(new CombatantCard("Rodrick",1, 0, 2));
        families[1].addCard(new CombatantCard("Roose",2, 0, 0));
        families[1].addCard(new CombatantCard("GreatJon",2, 1, 0));
        families[1].addCard(new CombatantCard("Robb",3, 0, 0));
        families[1].addCard(new CombatantCard("Eddard",4, 2, 0));
        //
   		//garrison destruction
   			boardModel.getTerritory("Pyke").destructGarrison();
   			boardModel.getTerritory("Highgarden").destructGarrison();
   			boardModel.getTerritory("Sunspear").destructGarrison();
   			// neutral forces construction
   			boardModel.getTerritory("Dornish Marches").setNeutralForce(100);
   	   		boardModel.getTerritory("The Boneway").setNeutralForce(100);
   	   		boardModel.getTerritory("Highgarden").setNeutralForce(100);
   	   		boardModel.getTerritory("Oldtown").setNeutralForce(100);
   	   		boardModel.getTerritory("Prince's Pass").setNeutralForce(100);
   	   		boardModel.getTerritory("Pyke").setNeutralForce(100);
   	   		boardModel.getTerritory("Salt Shore").setNeutralForce(100);
   	   		boardModel.getTerritory("Starfall").setNeutralForce(100);
   	   		boardModel.getTerritory("Storm's End").setNeutralForce(100);
   	   		boardModel.getTerritory("Sunspear").setNeutralForce(100);
   	   		boardModel.getTerritory("Three Towers").setNeutralForce(100);
   	   		boardModel.getTerritory("Yronwood").setNeutralForce(100);
   		
   	   	boardModel.getTerritory("Widow's Watch").setNeutralForce(3);
   		boardModel.getTerritory("The Eyrie").setNeutralForce(6);
   		boardModel.getTerritory("King's Landing").setNeutralForce(5);
        this.supplyUpdate();
  	}

	public void troopSend(int boat, int foot, int knigth, int siege) {
		if(battle!=null){
			battle.addTroop(boat,foot,knigth,siege);
		}else if(battlePvE!=null) {
			battlePvE.addTroop(boat,foot,knigth,siege);
		}else if(mvInitiated){//on est dans le cas d'un mouvement 
			territory1.mouveTroops(territory2,boat,foot,knigth, siege );
			if(territory1.getTroup()==null){// dans le cas où il n'y a plus de troupes on supprime l'ordre
				territory1.rmOrder();
				if (territory1 instanceof Water || territory1.getFamily().getInflu()<1){
					territory1.removeOwner();
					nextPlayer();
				}else if (territory1.getInfluenceToken() || (territory1 instanceof HomeLand && ((HomeLand) territory1).originalOwner(territory1.getFamily()))){ // the territory already have an influence token(or is Homeland), don't need to had one more
					nextPlayer();
				}else{//the player have the possiblity to use a influence token to keep is territory
					state=ModelState.USE_INF_TOKEN;
				}
			}
		}
		
	}

	public BattlePvP getBattle() {
		return battle;
	}
	/**end a battle and remove it
	 */
	public void battleEnd(){
		battle.end();
		battle=null;
		System.out.println("fin battaille");
	}

	/**when a battle is started ask if this territory can support one of the protagoniste*/
	public boolean canSupport(Territory territory, int SeatNum) {  //!territory.getOrder().getUse() semble pas fonctionner
		return (territory2.getNeighbors().contains(territory)&& territory.getOrder()!=null && territory.getFamily().getPlayer()==SeatNum && territory.getOrder().getType()==OrderType.SUP && (!territory.getOrder().getUse())&& (territory instanceof Water || territory2 instanceof Land));
	}

	public Territory getTerritory1() {
		return territory1;
	}

	public void attPrepEnd(){
		territory1.getOrder().used();
		if(battle.checkSupport()){
			System.out.println("battle check=true");
			battle.startBattle();
		}
	}
	
	/**
	 * resolution of a battle against neutral force (after the player get is troops selected)
	 */
	public void resolutionPvE(){
		battlePvE.resolution(this);
		battlePvE=null;
	}

	/**Give some information about the state of the game (for the playerChoice)
	 * 0 if there is no battle or if the player gived in parameter don't participate 
	 * 1- is for the battle card
	 */
	public int informations(int seatNum) {
		if(battle!=null && battle.playerPartisipate(seatNum)){
				return battle.getState();
		}
		return 0;
	}
	/**
	 * When there is a battle and the defencer lose, said if he can withdraw to the given territory
	 * @param territory
	 * @param seatNum
	 * @return
	*/
	public boolean canWithdraw(Territory territory, int seatNum) {
		return territory2.getFamily().getPlayer()==seatNum && territory2.canWithdraw(territory);
	}
	
	/**
	 * 
	 */
	public void updateLabel(){
		String text= new String("<html>Turn: "+turn+"        Wildings: "+wildings+"  ");
		switch(state){
		case PHASE_WESTEROS:
			text+=" Westeros phase";
			break;
		case PHASE_PROGRAMATION:
			text+=" Programation's phase";
			break;
		case PHASE_EXECUTION:
			text+=" Exection's phase  :  "+families[throne[currentPlayer]].getName()+"'s turn";
			break;
		case SUPPLY_TO_LOW :
			text+=" The supply of some players is too low, they have too destruct troops";
			break;
		case MUSTERING :
			text+=" Mustering";// add explenetion on which player can muster 
			break;
		}
		text+="<br>Throne track : ";
		for(int i :throne){
			text+=getFamily(i).getName()+" ";
		}text+="<br>Fiefdoms track : ";
		for(int i :fiefdoms){
			text+=getFamily(i).getName()+" ";
		}
		text+="<br>Court track : ";
		for(int i :court){
			text+=getFamily(i).getName()+" ";
		}
		text+="<br>";
		for(Family family : families){
			text+="<br>"+family.getName()+" Infulence : "+family.getInflu()+", Supply : "+family.getSupply()+", forteress : "+howManyCastle(family);
		}
		
		text+="<html>";
		jLabel.setText(text);
	}


	public boolean checkNewTurn() {
		return phase==0;	
	}

	public String choseWildingCard(){
		westerosCardNotSaw();
		currentCard=wildDeck.nextCard();
		return currentCard;
	}
	
	public String choseCard() {
		state=ModelState.PHASE_WESTEROS;
		westerosCardNotSaw();
		switch (westerosPhase){
		case 0:
			currentCard= deck1.nextCard();
			break;
		case 1:
			currentCard= deck2.nextCard();
			break;
		default :
			System.out.println("chosecard default");
			currentCard= deck3.nextCard();
			break;
		}
		westerosPhase++;
		updateLabel();
		return currentCard;
	}


	public void removeCard(String card) {
		westerosCardNotSaw();
		state=ModelState.PHASE_WESTEROS;
		switch (westerosPhase){
		case 0:
			deck1.cardPlayed(card);
			break;
		case 1:
			deck2.cardPlayed(card);
			break;
		case 2 :
			deck3.cardPlayed(card);
			break;
		}
		westerosPhase++;
		updateLabel();
		currentCard=card;
	}

	public void removeWildingCard(String card) {
		westerosCardNotSaw();
		wildDeck.cardPlayed(card);
		currentCard=card;
	}

	public String getCurrentCard(){
		return currentCard;
	}

	public int getWesterosPhase(){
		return westerosPhase;
	}

	/**
	 * Update the supply limits for each families, depending on which territories they possess, 
	 * if a family don't have enough supply points they must suppress some of their troops
	 */
	public void supplyUpdate() {
		for (Family  family : families){
			int supply=0;
			for(Territory territory :family.getTerritories()){
				supply+=territory.getSupply();
			}
			family.setSupply(supply);
		}
		if(!this.checkSupplyLimits()){
			state=(ModelState.SUPPLY_TO_LOW);
		}
		updateLabel();
	}

	
	
	 private void westerosCardNotSaw() {
		 for(Family family : families){
			family.carteNonVu(); 
		 }
		}

	public boolean westerosCardcheck() {
		for(Family family : families){
			if(!family.carteDejaVu()){
				return false;
			}
		}
		return true;
	}

/**this method apply the Westeros card "winter is comming", 
 * so the westeros phase stay at the same level and the deck is shuffle*/
	public void westerosCardWinter() {
		westerosPhase-=1;
		switch (westerosPhase){
		case 0:
			deck1=new Deck(1);
			break;
		case 1:
			deck2=new Deck(2);
			break;
		}
	}
	/**this method apply the Westeros card "game of thrones",
	 * each players gain influence points for this territories with crown 
	 * */
	public void westerosCardGameOfThrones(){ // commerce from port rules not implented
		for(Family family: families){
			for(Territory territory : family.getTerritories()){
				family.addInflu(territory.westerosCardGameOfThrones());
			}
		}
		updateLabel();
	}
	
	/**this method apply the Westeros card "feast for crows",
	 * remove the consolidate orders, the wildings force grow
	 */
	public void westerosCardFeastForCrows(){
		for(Family family: families){
			family.removeConsOrder();
		}
		wildingsGrow();
	}
	/**this method apply the Westeros card "feast for crows",
	 * remove the consolidate orders, the wildings force grow
	 */
	public void westerosCardRainsOfAutumn(){
		for(Family family: families){
			family.removeMarchPOrder();
		}
		wildingsGrow();
	}


	public void westerosCardSeaOfStorms() {
		for(Family family: families){
			family.removeRaidOrder();
		}
		wildingsGrow();
	}


	public void westerosCardStormOfSwords() {
		for(Family family: families){
			family.removeDefenceOrder();
		}
		wildingsGrow();
		
	}
	public void westerosCardWebOfLies() {
		for(Family family: families){
			family.removeSupportOrder();
		}
		wildingsGrow();
		
	}
	public int getWildings(){
		return wildings;
	}
	/**Set the wildings threat but never under 0*/
	public void setWildings(int wildings){
		if(wildings<0){
			this.wildings=0;
		}else{
			this.wildings=wildings;
		}
	}
	/**
	 * Add 2 to the wildings threat
	 */
	public void wildingsGrow() {
		wildings+=2;
		updateLabel();
		//tester si on arrive à 12
	}
	
	/***/
	public void westerosCardClashOfKings() {
		biddingPhase=0;	
		for(Family family: families){
			family.setBid(-1);
		}
		bidding=new Bidding(families);
	}
	/**This method is called when there is a wildings attack*/
	public void wildingsAttack() {
		for(Family family: families){
			family.setBid(-1);
		}
		bidding=new BiddingAgainstWild(families, wildings);
	}
	
	/**
	 * Be carreful this method have sides effects
	 * @return A CHANGER !!!
	 */
	public int biddingResolution(){
		Family[] track=bidding.getTrack();
		int[] temp;
		switch (biddingPhase){
		case 0:
			temp=throne;
			bidding=new Bidding(families);
			break;
		case 1:
			temp=fiefdoms;
			bidding=new Bidding(families);
			break;
		default :
			temp=court;
			break;
		}
		for(int i=0; i<numberPlayers; i++){
			temp[i]=track[i].getPlayer();
			track[i].resetBid();
		}
		biddingPhase++;
		updateLabel();
		return biddingPhase;
		
	}
	/**
	 * Sort the biddings and said if there is some equality
	 * @return true if the bidding is resolve (no equality)
	 */
	 public boolean biddingSort(){
		 return bidding.biddingResolution();
	 }
	/**
	 * 
	 * @return
	 */
	public Bidding getBidding(){
		return bidding;
	}
	

	/** initialize the mustering phase*/
	public void westerosCardMustering() {
		state=(ModelState.MUSTERING);
		//return the recruits available to the initial state
		for(Family family: families){
			for(Territory territory : family.getTerritories()){
				territory.resetRecruit();
			}
		}
		updateLabel();
	}
	
	/**
	 * Said if the given player can recruit in the given territory during the mestering phase
	 * @param territory the territory where the player want to recruit
	 * @param player the player who want to recruit
	 * @return true if the player can recruit in this territory
	 */
	public boolean canRecruit(Territory territory, int player){
		return /*state==ModelState.MUSTERING &&*/ (territory.getFamily()!=null) && (territory.getFamily().getPlayer()==player)&& territory.getRecruit()>0;
	}
	
	/**
	 * During a Westeros mustering phase, said if all the families have done theirs musterings,
	 * also change this model by setting the boolean musteringPhase to false if all the musterings are done
	 * @return false if there is one (or more) territory who havn't make its mustering
	 */
	public boolean allRecruitementDone() {
		for(Family family: families){
			for(Territory territory : family.getTerritories()){
				if(territory.getRecruit()!=0){
					return false;
				}
			}
		}
		state=ModelState.values()[phase];
		return true;
	}
	
	/**said if a family can recruit a new troop in this territory*/
	public boolean checkSupplyLimits(int player, Territory territory){
		return checkSupplyLimits(getFamily(player), territory);
	}
	/**said if a family can recruit a new troop in this territory*/
	public boolean checkSupplyLimits(Family family, Territory territory){
		if(territory.getTroup()==null){
			return true;
		}
		int[] supply = new int[5];
		for(int i=0;i<5;i++){
			supply[i]=supplyLimites[family.getSupply()][i];
		}
		for(Territory otherTerritory : family.getTerritories()){
			if(otherTerritory!=territory && otherTerritory.getTroup()!=null && otherTerritory.getTroup().getEffectif()>1){
				int i=4;
				while(i>=0 && otherTerritory.getTroup().getEffectif()>supply[i]){
					i--;
				}
				if(i==-1){ //Normalement inutile si tout le reste est bien programmé
					return false;
				}
				supply[i]=0;
			}
		}
		for(int sup: supply){
			if(sup>territory.getTroup().getEffectif()){
				return true;
			}
		}
		return false;
		}
	
	/**check if a family don't execed its supply limit
	 * @return true if the family don't execed the supply limit, else return false
	 * */
	public boolean checkSupplyLimits(Family family){
		int[] supply = new int[5];
		for(int i=0;i<5;i++){
			supply[i]=supplyLimites[family.getSupply()][i];
		}
		for(Territory territory : family.getTerritories()){
			if(territory.getTroup()!=null && territory.getTroup().getEffectif()>1){
				int i=4;
				while(i>=0 && territory.getTroup().getEffectif()>supply[i]){
					i--;
				}
				if(i==-1){
					return false;//territory.getTroup().getEffectif()<=supply[0];
				}
				supply[i]=0;
			}
		}
		return true;
	}
	/**check if no family  execed its supply limit
	 * @return true if all the families dosen't execed the supply limit, else return false
	 * */
	public boolean checkSupplyLimits(){
		for(Family family : families){
			if(!checkSupplyLimits(family)){
				return false;
			}
		}
		return true;
	}
	
	/**Said if a player is abilited to choose for the westeros card*/
	public boolean canChose(int player) {
		switch(westerosPhase){
			case 1 :
				return haveThrone(player);
			case 2 :
				return haveRaven(player);
			default :
				return player==fiefdoms[0];	
		}
	}
	/**Said if a player have the throne*/
	public boolean haveThrone(int player) {
		return throne[0]==player;
	}
	
	/**Said if a player have the raven*/
	private boolean haveRaven(int player) {
		return court[0]==player;
	}

	/**
	 * Do the westeros card effect choose by the appropriate player (for cards like dark Wings dark words, put to the sword or throne of blades)  
	 * @param b true if the player choose the choice A, false if he choose B 
	 */
	public void westerosCardChoice(boolean choice) {
		switch(westerosPhase){
		case 1:
			if(choice){
				supplyUpdate();
			}else{
				westerosCardMustering();
			}
			wildingsGrow();
			break;
		case 2:
			if(choice){
				westerosCardClashOfKings();
			}else{
				westerosCardGameOfThrones();
			}
			wildingsGrow();
			break;
		case 3:
			if(choice){
				westerosCardStormOfSwords();
			}else{
				westerosCardRainsOfAutumn();
			}
			break;
		}
	}

	/**
	 * This method tell how many catle(fort and stronghold) have a family
	 * @param family
	 * @return the number of castle
	 */
	private int howManyCastle(Family family){
		int res=0;
		for(Territory territory : family.getTerritories()){
			if(territory.getCastle()>0){
				res++;
			}
		}
		return res;
	}
	
	/**
	 * Search the court track's position of the given family 
	 * @param family the family wich we want to know the position
	 * @return the court track's position (be carfull it's start from 0 to numberPlayer-1)
	 */
	private int getCourtPosition(int family){
		int res=0;
		while (court[res]!=family){
			res++;
		}
		return res;
	}
	
	/**
	 * Give the maximum number of stars that a player can use 
	 * @param family
	 * @return
	 */
	public int getStars(int family){
		return starsLimitation[getCourtPosition(family)];
	}
	
	
	// return true if the game is won by this player
	public boolean isGameWon (int player) {
		return howManyCastle(getFamily(player))>6;
	}

	/**
	 * This method return the number of players
	 * @return the number of players
	 */
	public int getNumberPlayers(){
		return numberPlayers;
	}
	
	/**
	 * Return the current state of the model
	 * @return the current state of the model
	 */
	public ModelState getPhase(){
		return state;
	}
	/**
	 * 
	 * @param newState
	 */
	public void setPhase(ModelState newState) {
		this.state=newState;
		
	}
	


}
