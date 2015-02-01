package org.jogre.gameOfThrones.common.combat;

import java.util.LinkedList;
import java.util.List;

import org.jogre.gameOfThrones.common.CombatantCard;
import org.jogre.gameOfThrones.common.Family;
import org.jogre.gameOfThrones.common.orders.Order;
import org.jogre.gameOfThrones.common.orders.OrderType;
import org.jogre.gameOfThrones.common.territory.Territory;
import org.jogre.gameOfThrones.common.territory.Water;

public class BattlePvP extends Battle{

	
	private int def;
	private int att;
	private int attSwords;
	private int attTowers;
	private int defSwords;
	private int defTowers;
	private Family defFamily;
	CombatantCard attCard;
	CombatantCard defCard;
	
	public BattlePvP(Territory attTerritory, Territory defTerritory){
		super(attTerritory, defTerritory);
		def=0;
		att=0;
		defFamily=defTerritory.getFamily();
		attCard=null;
		defCard=null;
		attSwords=0;
		attTowers=0;
		defSwords=0;
		defTowers=0;
	}


	
	@Override
	public int defPower(){
		int res =0;
		if (defTerritory.getOrder()!=null) res=defTerritory.getOrder().getDefBonus();
		if (defTerritory.haveGarrison()) res+=2;
		if(groundType==2){
			res+= defTerritory.getTroup().getEffectif();
		}else{
			res+=defTerritory.getTroup().getTroops()[1]+defTerritory.getTroup().getTroops()[2]*2;
		}
		for(Territory territory : defSupport){
			int[] troops =territory.getTroup().getTroops();
			res+=territory.getOrder().getOthBonus()+troops[0]+troops[1]+troops[2]*2;
		}
		return res;
	}
	
	public void startBattle() {
		attTerritory.getOrder().setUse(true);
		state=BATTLE_CHOOSE_CARD;
	}
	
	/**
	 * 
	 * @param territory
	 * @param casualties
	 * @return false if all the troop is destructed
	 */
	private boolean destructTroops(Territory territory, int casualties){
		territory.getTroup().getTroops()[3]=0;
		if(casualties>=territory.getTroup().getEffectif()){
			territory.setTroup(null);
			return false;
		}else if (casualties<1){
			return true;
		}else{
			if(groundType==2){
				territory.mouveTroops(new Water("trash"),casualties,0,0,0);
			}else{
				territory.getTroup().destruction(casualties);
			}
			return true;
		}
	}
	
	public void battleResolution(){
		if(this.battleWinner()){
			//on applique les effets des cartes
			afterResolutionCardEffect(true);
			//destruction of the garrison if there's one
			defTerritory.destructGarrison();
			//on detruit les troups du defenceur 
			if (destructTroops(defTerritory, (attSwords-defTowers)) && defTerritory.canWithdraw()){
				//il choisit une retraite car il reste des troupes
				System.out.println("retraite");
				state=3;
			}else{
				state=4;//all defenciv force have been killed, no withdraw 
				// on met les nouvelles troupes sur le territoire
				if(groundType==2){
					defTerritory.setTroup(new NavalTroup(attFamily, attTroops[0]));
				}else{
					defTerritory.setTroup(new GroundForce(attFamily, attTroops[1],attTroops[2],attTroops[3]));
				}
				
			}
			//on retire l'ordre
			defTerritory.rmOrder();
			if(attCard.getName().equals("Loras")){
				defTerritory.setOrder(new Order(attTerritory.getOrder().getStar(),0,attTerritory.getOrder().getOthBonus(), OrderType.ATT));
				attTerritory.rmOrder();
			}
			System.out.println("victoire");
		}else{
			//on applique les effets des cartes
			afterResolutionCardEffect(false);
			//on detruit les troups de l'attaquant
			System.out.println("defailt");
			// faire les destructions des troupes ici!!
			attTroops[3]=0;//destruction of siege tower
			if(defSwords>attTowers){
				if(groundType==2){
					attTroops[0]-=(defSwords-attTowers);
				}else{
					if(attTroops[1]<(defSwords-attTowers)){
						attTroops[2]-=(defSwords-attTowers-attTroops[1]);
						attTroops[1]=0;
					}else{
						attTroops[1]-=(defSwords-attTowers);
					}
				}
			}
			//retraites des troupes
			if(attTroops[0]+attTroops[1]+attTroops[2]>=0){
				if(attTerritory.getTroup()!=null){
					attTerritory.getTroup().addTroop(attTroops);
				}else if(groundType==2){
					attTerritory.setTroup(new NavalTroup(attFamily, attTroops[0]));
				}else{
					attTerritory.setTroup(new GroundForce(attFamily, attTroops[1],attTroops[2],0));
				}
			}
			System.out.println("befor state 4");
			state=4;	
		}
	}

/**return true if the attaquant win, false if it's the defencer*/
	private boolean battleWinner() {
		if(att==def){
			return attFamily.getFiefdomsTrack()<defFamily.getFiefdomsTrack();
		}else{
			return att>def; 
		}
	}
	
	


	
	@Override
	public boolean playerPartisipate(int seatNum) {
		return (attFamily.getPlayer()==seatNum || defTerritory.getFamily().getPlayer()==seatNum);
	}
	
	
	/**
	 * Tell if the attacker and the defender have choose theirs cards
	 * @return true if the attacker and the defender have choose theirs cards
	 */
	public boolean cardsPlayed(){
		return attCard!=null && defCard!=null;
	}
	
	/**
	 * 
	 * @param card
	 * @param family
	 */
	public void playCard(CombatantCard card, Family family) {
		if(family==attFamily){
			attCard=card;
			System.out.println("carte att "+attCard.getName());
		}else{
			defCard=card;
			System.out.println("carte def "+defCard.getName());
		}
		if(cardsPlayed()){
			//on test Tyrion 
			/*if(attCard.getName().equals("Tyrion")){
				System.out.println("Tyrion has been played");
				defCard=null;
			}else if(defCard.getName().equals("Tyrion")) {
				System.out.println("Tyrion has been played");
				attCard=null;
			}else{*/
				//la reines des epines, Faire un etat particulié qui permet de clicker sur la carte pour les cartes 
				//les autres (vict,etc)
				befforSwordCardEffect();
				//on regarde si un des deux joueurs a l'épée
				if(attFamily.canUseSword()||defFamily.canUseSword()){
					this.state=BATTLE_PLAY_SWORD;
				}else{
					//on passe directement à la resolution
					battleResolution();
				}
			//}
		}
	}
	/**
	 */
	public void useSword(){
		if(attFamily.canUseSword()){
			att++;
			attFamily.swordUse();
		}else{
			def++;
			defFamily.swordUse();
		}
		battleResolution();
		
	}
	public void dontUseSword(){
		battleResolution();
	}
	/*public CombatantCard getAttCard(){
		return attCard;
	}

	public CombatantCard getDefCard(){
		return defCard;
	}*/
	public Family getDefFamily() {
		return defTerritory.getFamily();
	}


	

	
	public boolean canPlayCard(Family family) {
		return ((family==attFamily && attCard==null)||(family==defTerritory.getFamily() && defCard==null));
	}
	
	/**
	 * This method is call when the defencer lose and must withdraw
	 * @param territory
	 */
	public void withdraw (Territory territory){
		defTerritory.mouveTroops(territory);
		state=4;
		System.out.println("retraite");
		// on met les nouvelles troupes sur le territoire
		if(groundType==2){
			defTerritory.setTroup(new NavalTroup(attFamily, attTroops[0]));
		}else{
			defTerritory.setTroup(new GroundForce(attFamily, attTroops[1],attTroops[2],attTroops[3]));
		}
	}
	/**
	 * This method is call when a battle end 
	 */
	public void end(){
		// on regarde si un joueur n'a plus de cartes, au quel cas on lui rend
		if (attFamily.getCombatantCards().isEmpty()){
			attFamily.regainCombatantCards(attCard);
		}
		if (defFamily.getCombatantCards().isEmpty()){
			defFamily.regainCombatantCards(defCard);
		}
	}
	
	// on bellow is the different card effects 
	
	
	/*
	 * card's effect that are use after the battle resolutions 
	 * @param attackerWin true if the attacker win  
	 */
	private void afterResolutionCardEffect(boolean attackerWin){
		if(attackerWin){
			if(attCard.getName().equals("Tywin")){
				attFamily.addInflu(2);
			}
			if(defCard.getName().equals("BlackFish")){
				attSwords=0;
			}
		}else{
			if(defCard.getName().equals("Tywin")){
				defFamily.addInflu(2);
			}
			if(attCard.getName().equals("BlackFish")){
				defSwords=0;
			}
		}
	}
	
	/*card's effect that are use before the battle resolutions*/ 
	private void befforSwordCardEffect(){
		if(attCard.getName().equals("Kevan")){
			kevanEffect();
		}
		if(attCard.getName().equals("Victarion")){
			victarionEffect();
		}
		if(attCard.getName().equals("Salladhor")|| defCard.getName().equals("Salladhor")){
			salladhorEffect();
		}
		if(attCard.getName().equals("Davos")|| defCard.getName().equals("Davos")){
			davosEffect();
		}
		if(groundType<2 && (attCard.getName().equals("Mace")|| defCard.getName().equals("Mace")) && (!defCard.getName().equals("BlackFish") || !attCard.getName().equals("BlackFish"))){
			maceEffect();
		}
	}
	
	private void maceEffect(){
		if(attCard.getName().equals("Mace") && defTerritory.getTroup().getTroops()[1]>0){
			defTerritory.getTroup().rmToop(0, 1, 0, 0);
		}else if(defCard.getName().equals("Mace") && attTroops[1]>0){
			attTroops[1]--;
		}
	}
	
	private void kevanEffect(){
		att+=attTroops[1];
		for(Territory territory : attSupport){
			if(territory.getFamily().getName().equals("Lannister")){
				att+=territory.getTroup().getTroops()[1];
			}
		}
	}
	private void victarionEffect(){
		att+=attTroops[0];
		for(Territory territory : attSupport){
			if(territory.getFamily().getName().equals("Greyjoy")){
				att+=territory.getTroup().getTroops()[0];
			}
		}
	}
	private void salladhorEffect(){//
		if((attCard.getName().equals("Salladhor")&& !attSupport.isEmpty())||(defCard.getName().equals("Salladhor")&& !defSupport.isEmpty())){
			for(Territory territory : defSupport){
				if(!territory.getFamily().getName().equals("Baratheon")){
					def-=territory.getTroup().getTroops()[0];
				}
			}
			for(Territory territory : attSupport){
				if(!territory.getFamily().getName().equals("Baratheon")){
					att-=territory.getTroup().getTroops()[0];
				}
			}
		}
	}
	private void davosEffect(){//don't work with BALON EFFECT !!!
		if(attCard.getName().equals("Davos")&&davosAttBoolean()){
			att++;
			attSwords++;
		}else if(davosDefBoolean()){
			def++;
			defSwords++;
		}
	}
	private boolean davosAttBoolean(){
		for (CombatantCard card :attFamily.getCombatantCards()){
			if(card.getName().equals("Stannis")){
				return false;
			}
		}
		return true;
	}
	private boolean davosDefBoolean(){
		for (CombatantCard card :defFamily.getCombatantCards()){
			if(card.getName().equals("Stannis")){
				return false;
			}
		}
		return true;
	}
	
	
}
