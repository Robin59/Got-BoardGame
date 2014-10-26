package org.jogre.gameOfThrones.common.combat;

import java.util.LinkedList;
import java.util.List;

import org.jogre.gameOfThrones.common.CombatantCard;
import org.jogre.gameOfThrones.common.Family;
import org.jogre.gameOfThrones.common.orders.OrderType;
import org.jogre.gameOfThrones.common.territory.Territory;
import org.jogre.gameOfThrones.common.territory.Water;

public class Battle {

	private Territory attTerritory;
	private Territory defTerritory;
	private int[] attTroops;
	private List<Territory> attSupport;
	private List<Territory> defSupport;
	private int groundType; // 2 water, 0 land, 1 castle
	private int def;
	private int att;
	private int attSwords;
	private int attTowers;
	private int defSwords;
	private int defTowers;
	private Family attFamily;
	private Family defFamily;
	private int state; // 0 for the choosing troops, 1 for choosing cards, 2 for sword, 3 for the withdrawal, 4 when it's ended
	CombatantCard attCard;
	CombatantCard defCard;
	
	public Battle(Territory attTerritory, Territory defTerritory){
		System.out.println("a new battle begin");
		this.attTerritory=attTerritory;
		this.defTerritory=defTerritory;
		attTroops= new int[4];
		attSupport= new LinkedList<Territory>();
		defSupport= new LinkedList<Territory>();
		if(defTerritory instanceof Water){
			groundType=2;
		}else if(defTerritory.getCastle()>0){
			groundType=1;
		}else{groundType=0;}
		def=0;
		att=0;
		attFamily=attTerritory.getFamily();
		defFamily=defTerritory.getFamily();
		state=0;
		attCard=null;
		defCard=null;
		attSwords=0;
		attTowers=0;
		defSwords=0;
		defTowers=0;
	}


	public void addTroop(int boat, int foot, int knight, int siege) {
		attTerritory.mouveTroops(new Water("trash"),boat,foot,knight, siege);
		attTroops[0]+=boat;
		attTroops[1]+=foot;
		attTroops[2]+=knight;
		attTroops[3]+=siege;
	}

	/** brute force without Family cards*/
	public int attPower(){
		int res=attTerritory.getOrder().getOthBonus();
		if(groundType==2){
			res+= attTroops[0];
		}else{
			res+=(attTroops[2])*2+attTroops[1]+attTroops[3]*4*groundType;
		}
		for(Territory territory : attSupport){
			int[] troops =territory.getTroup().getTroops();
			res+=territory.getOrder().getOthBonus()+troops[0]+troops[1]+troops[2]*2+troops[3]*4*groundType;
		}
	return res;
	}
	
	/**
	 * calculate the defensive initial force (without Family cards but with support, garrison, order's bonus)
	 * @return the defensive force
	 */
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
		att=this.attPower();
		def=this.defPower();
		System.out.println("attack power "+att);
		System.out.println("def power "+def);
		state=1;
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
			System.out.println("victoire");
		}else{
			//on applique les effets des cartes
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
	
	public void addAttSupport(Territory territory){
		System.out.println("AttSupport"+territory.getName());
		attSupport.add(territory);
		territory.getOrder().used(); 
		//on verifie si on peut commencer la bataille
		if(checkSupport()){
			startBattle();			
		}
	}


	public void addDefSupport(Territory territory) {
		System.out.println("defSupport");
		defSupport.add(territory);
		territory.getOrder().used();
		//on verifie si on peut commencer la bataille
		if(checkSupport()){
			startBattle();
		}
	}
	
	/**
	 * Check if all territory with support and the attaquant territory have done their orders
	 * @return true if all supports and the attaquant troups are sended
	 */
	public boolean checkSupport(){	
		System.out.println("Inside checkSupport");
		for (Territory territory : defTerritory.getNeighbors()){
			if(territory.getOrder()!=null && territory.getOrder().getType()==OrderType.SUP && !territory.getOrder().getUse()){
				return false;
			}
		}
		return attTerritory.getOrder().getUse();
	}

/**
 * Return a boolean saying if a player is participating or not to this battle (support dosen't count)
 * @param seatNum
 * @return true if the player is participating 
 */
	public boolean playerPartisipate(int seatNum) {
		return (attFamily.getPlayer()==seatNum || defTerritory.getFamily().getPlayer()==seatNum);
	}
/**
 * Return a boolean saying if a player is participating or not to this battle (support dosen't count)
 * @param family
 * @return true if the player is participating 
 */
	public boolean playerPartisipate(Family family) { // NOT USE !!
		return (attFamily==family || defTerritory.getFamily()==family);
	}
	
	
/**
 * return the state of the battle	
 * @return 0 for the support, 1 for cards 
 */
	public int getState(){
		return state;
	}

	public boolean cardsPlayed(){
		return attCard!=null && defCard!=null;
	}
	
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
			if(attCard.getName().equals("Tyrion")){
				System.out.println("Tyrion has been played");
				defCard=null;
			}else if(defCard.getName().equals("Tyrion")) {
				System.out.println("Tyrion has been played");
				attCard=null;
			}else{
				System.out.println("Cartes jouée :");
				System.out.println("carte att "+attCard.getName());
				System.out.println("carte def "+defCard.getName());
				//on effectue le calcul la puissance final avant épée
				this.powerWithoutSword();
				//on applique les effets de carte
				attSwords=attCard.getSword();
				defSwords=defCard.getSword();
				attTowers=attCard.getTower();
				defTowers=defCard.getTower();
					//la reines des epines, Faire un etat particulié qui permet de clicker sur la carte pour les cartes 
					//les autres (vict,etc)
				befforSwordCardEffect();
				//on regarde si un des deux joueurs a l'épée
				if(attFamily.canUseSword()||defFamily.canUseSword()){
					this.state=2;
				}else{
					//on passe directement à la resolution
					battleResolution();
				}
			}
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
		System.out.println("sword use");
		System.out.println("attack power "+att);
		System.out.println("def power "+def);
		battleResolution();
		
	}
	public void dontUseSword(){
		System.out.println("sword don't use");
		System.out.println("attack power "+att);
		System.out.println("def power "+def);
		battleResolution();
	}
	/*public CombatantCard getAttCard(){
		return attCard;
	}

	public CombatantCard getDefCard(){
		return defCard;
	}*/
	public Family getAttFamily (){
		return attFamily;
	}
	public Family getDefFamily() {
		return defTerritory.getFamily();
	}
	
	private void powerWithoutSword() {
		att=this.attPower()+attCard.getPower();
		def=this.defPower()+defCard.getPower();
		System.out.println("attack power "+att);
		System.out.println("def power "+def);
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
		attTerritory.getOrder().used();
		// on regarde si un joueur n'a plus de cartes, au quel cas on lui rend
		if (attFamily.getCombatantCards().isEmpty()){
			attFamily.regainCombatantCards(attCard);
		}
		if (defFamily.getCombatantCards().isEmpty()){
			defFamily.regainCombatantCards(defCard);
		}
	}
	
	// on bellow is the different card effects 
	
	private void befforSwordCardEffect(){
		if(attCard.getName().equals("Kevan")){
			kevanEffect();
		}else if(attCard.getName().equals("Victarion")){
			victarionEffect();
		}else if(attCard.getName().equals("Salladhor")|| defCard.getName().equals("Salladhor")){
			salladhorEffect();
		}else if(attCard.getName().equals("Davos")|| defCard.getName().equals("Davos")){
			davosEffect();
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
