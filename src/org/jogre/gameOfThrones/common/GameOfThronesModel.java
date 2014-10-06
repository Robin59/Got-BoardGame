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
import org.jogre.gameOfThrones.common.combat.Battle;
import org.jogre.gameOfThrones.common.combat.GroundForce;
import org.jogre.gameOfThrones.common.combat.NavalTroup;
import org.jogre.gameOfThrones.common.orders.OrderType;
import org.jogre.gameOfThrones.common.territory.BoardModel;
import org.jogre.gameOfThrones.common.territory.Land;
import org.jogre.gameOfThrones.common.territory.Territory;
import org.jogre.gameOfThrones.common.territory.Water;

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
	private boolean combatInitiated;// utile ou faire battle==null ?????
	private boolean musteringPhase;
	//private boolean clashOfKings;
	private Battle battle;
	private Territory territory1;
	private Territory territory2;
	private JLabel jLabel;
	private Deck deck1;
	private Deck deck2;
	private Deck deck3;
	private String currentCard;
	private int[][] supplyLimites= {{2,2,0,0,0},{3,2,0,0,0},{3,2,2,0,0},{3,2,2,2,0},{3,3,2,2,0},{4,3,2,2,0},{4,3,2,2,2}};
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
        // placer le plateau en fonction du jeu
        familiesConstruction(numberPlayers);
        // le jeu commence à la phase programation
        phase=1;
        internPhase=0;
        biddingPhase=3;
        turn=1;
        wildings=2;
        currentPlayer=0;
        mvInitiated=false;
        combatInitiated=false;
        musteringPhase=false;
        battle=null;
        deck1=new Deck(1);
        deck2=new Deck(2);
        deck3=new Deck(3);
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
    					family.gainInflu(territory.consolidation());
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
    	phase= (phase+1)%3;
    	//AJOUTER DES CHOSES EN FONCTION DES PHASES 
    	if(phase==2){
    		//on initialise
    		 internPhase=0;
    		 currentPlayer=0;
    		 // on cherche le premier joueur a avoir un ordre de raid
    		 checkOrder();
    	}else if(phase==0){
    		turn++;//nouveau tour
    		westerosPhase=0;
    		this.westerosCardNotSaw();
    		updateLabel();
    		//ici on test si on arrive au tour 11 et on fini le jeu dans ce cas
    		//sinon, on retire tous les ordres des territoires et on les redonnes aux joueurs
    		for (Family family : families){
    			
    			family.ordersBack();
    		}
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
   
    private boolean checkCurrentPlayerOrder(){
    	for (Territory territory :families[getCurrentPlayer()].getTerritories()){
    		if (territory.getOrder()!=null && territory.getOrder().getType().ordinal()==internPhase){
    			return true;
    		}
    	}
    	return false;
    }
    
    
    /**Check if there is some consolidation's orders, if not go to the next turn*/
    //FAIRE EN 2 FOIS ?
   /* public void checkCons(){
    	boolean flag = false;
    	for(Family family : families){
			for (Territory territory : family.getTerritories()){
				if(territory.getOrder()!=null /*&& !territory.getOrder().getStar()*/ //&& territory.getOrder().getType()==OrderType.CON){
				/*	flag=true;
				}
			}
    	}
    	if(!flag){
    		nextPhase();
    	}
    }*/
    
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
    
    public boolean canGiveOrder(Territory territory, int player){ // a modifier pour verifier les troupes et non juste l'appartenance
    	return phase==1 && (territory.getFamily()!=null) && (territory.getFamily().getPlayer()==player);
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
	

	public int getPhase() {	
		return phase;
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
		System.out.println("mvInitiated");
	}
	public void mvInitiated2(Territory toTerritory){
		territory2=toTerritory;
		System.out.println("mvInitiated2");
	}
	
	public boolean getMvInitiated(){
		return mvInitiated;
	}
	
	/***/
	public void battle(Territory fromTerritory,Territory toTerritory) {
		combatInitiated=true;
		territory1=fromTerritory;
		territory2=toTerritory;
		battle = new Battle(fromTerritory, toTerritory);
		
	}
	public void battleInitiated(Territory territory) {
		territory2=territory;
		battle = new Battle(territory1, territory2);
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
  		// On place les starks (changer pour le joueur 3)
  		families[0]=new Family(0);
  		throne[0]=0;
  		fiefdoms[0]=0;
  		court[0]=0;
  		families[0].setFiefdomsTrack(1);
        boardModel.getTerritory("Winterfell").setTroup(new GroundForce(families[0],1,1,0));
        boardModel.getTerritory("White Harbor").setTroup(new GroundForce(families[0],1,0,0));
        boardModel.getTerritory("Shivering Sea").setTroup(new NavalTroup(families[0],1));
        //ajout des cartes 
        families[0].addCard(new CombatantCard("Mellissandre",1, 1, 0));
        families[0].addCard(new CombatantCard("Salladhor",1, 0, 0));
        families[0].addCard(new CombatantCard("Davos",2, 0, 0));
        families[0].addCard(new CombatantCard("Brienne",2, 1, 1));
        if(playerNumber>1){
        	court[1]=0;
        	families[1]=new Family(1);
          	throne[1]=1;
          	fiefdoms[1]=1;
      		court[0]=1;
          	families[1].setFiefdomsTrack(2);
        	families[1].addCard(new CombatantCard("Tyrion",1, 0, 0));
          	families[1].addCard(new CombatantCard("Kevan",1, 0, 0));
          	families[1].addCard(new CombatantCard("The Hound",2, 0, 2));
          	families[1].addCard(new CombatantCard("Jaime",2, 1, 0));
          	boardModel.getTerritory("Karhold").setTroup(new GroundForce(families[1],1,0,0));
          }
          this.supplyUpdate();
  	}

	public void troopSend(int boat, int foot, int knigth, int siege) {
		if(battle!=null){
			battle.addTroop(boat,foot,knigth,siege);
		}else if(mvInitiated){//on est dans le cas d'un mouvement 
			System.out.println("troopSend");
			territory1.mouveTroops(territory2,boat,foot,knigth, siege );
			if(territory1.getTroup()==null){// dans le cas où il n'y a plus de troupes on supprime l'ordre
				territory1.rmOrder();
				nextPlayer();
			}
		}
		
	}

	public Battle getBattle() {
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
		System.out.println("Inside model.attPrepEnd");
		territory1.getOrder().used();
		if(battle.checkSupport()){
			System.out.println("battle check=true");
			battle.startBattle();
		}
	}

	/**Give some information about the state of the game (for the playerChoice)
	 * 
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
	
	/*Pour le Label ajouter :  
	 * - le nombre de chateau
	 */
	public void updateLabel(){
		String text= new String("<html>Turn: "+turn+"        Wildings: "+wildings+" ");
		switch(phase){
		case 0:
			text+=" Westeros phase";
			break;
		case 1:
			text+=" Programation's phase";
			break;
		case 2:
			text+=" Exection's phase  :  "+families[currentPlayer].getName()+"'s turn";
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
			text+="<br>"+family.getName()+" Infulence : "+family.getInflu()+" Supply : "+family.getSupply();
		}
		
		text+="<html>";
		jLabel.setText(text);
	}


	public boolean checkNewTurn() {
		return phase==0;	
	}


	public String choseCard() {
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
		return currentCard;
	}


	public void removeCard(String card) {
		westerosCardNotSaw();
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
		currentCard=card;
	}


	public String getCurrentCard(){
		return currentCard;
	}

	public int getWesterosPhase(){
		return westerosPhase;
	}

	public void supplyUpdate() {
		for (Family  family : families){
			int supply=0;
			for(Territory territory :family.getTerritories()){
				/*System.out.println(supply);
				System.out.println(territory.getName()+" give "+territory.getSupply());*/
				supply+=territory.getSupply();
			}
			family.setSupply(supply);
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
				family.gainInflu(territory.westerosCardGameOfThrones());
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
		widingsGrow();
	}
	/**this method apply the Westeros card "feast for crows",
	 * remove the consolidate orders, the wildings force grow
	 */
	public void westerosCardRainsOfAutumn(){
		for(Family family: families){
			family.removeMarchPOrder();
		}
		widingsGrow();
	}


	public void westerosCardSeaOfStorms() {
		for(Family family: families){
			family.removeRaidOrder();
		}
		widingsGrow();
	}


	public void westerosCardStormOfSwords() {
		for(Family family: families){
			family.removeDefenceOrder();
		}
		widingsGrow();
		
	}


	public void widingsGrow() {
		wildings+=2;
		updateLabel();
		//tester si on arrive à 12
	}


	public void westerosCardClashOfKings() {
		biddingPhase=0;	
		for(Family family: families){
			family.setBid(-1);
		}
	}
	
	/***/
	public boolean biddingResolution(){
		Family[] track=biddingSort();
		int[] temp;
		switch (biddingPhase){
		case 0:
			temp=throne;
			break;
		case 1:
			temp=fiefdoms;
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
		return biddingPhase<3;
	}
	
	/**sort the families in fonction of there bids*/
	private Family[] biddingSort(){ // refaire
		Family[] track = new Family[numberPlayers];
		for(int i=0; i<numberPlayers;i++){
			track[i]=getFamily(i);
		}
		for (int i=0;i<numberPlayers;i++){
			int z=i;
			for(int y=i;y<numberPlayers;y++){
				if(track[z].getBid()<track[y].getBid()){
					z=y;
				}
			}	
			Family family = track[z];
			track[z]=track[i];
			track[i]=family;
		}
		return track;
	}
	public boolean allBidsDone(){
		if(biddingPhase>2){
			return false;
		}
		for(Family family: families){
			if(family.getBid()==-1){
				return false;
			}
		}
		return true;
	}

	/** initialize the mustering phase*/
	public void mustering() {
		musteringPhase=true;
		//return the recruits available to the initial state
		for(Family family: families){
			for(Territory territory : family.getTerritories()){
				territory.resetRecruit();
			}
		}
	}
	public boolean canRecruit(Territory territory, int player){
		return musteringPhase && (territory.getFamily()!=null) && (territory.getFamily().getPlayer()==player)&& territory.getRecruit()>0;
	}

	public boolean getMusteringPhase(){
		return musteringPhase;
	}
	public boolean allRecruitementDone() {
		for(Family family: families){
			for(Territory territory : family.getTerritories()){
				if(territory.getRecruit()!=0){
					return false;
				}
			}
		}
		musteringPhase=false;
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
}
