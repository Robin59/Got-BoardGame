package org.jogre.gameOfThrones.common.combat;

import java.util.LinkedList;
import java.util.List;

import org.jogre.gameOfThrones.common.CombatantCard;
import org.jogre.gameOfThrones.common.Family;
import org.jogre.gameOfThrones.common.orders.OrderType;
import org.jogre.gameOfThrones.common.territory.Land;
import org.jogre.gameOfThrones.common.territory.Territory;
import org.jogre.gameOfThrones.common.territory.Water;

public class Battle {

	private Territory attTerritory;
	private Territory defTerritory;
	private int[] attTroops;
	private List<Territory> attSupport;
	private List<Territory> defSupport;
	//private int[] attSupport;
	//private int[] defSupport;
	CombatResolution resolution;
	private int groundType; // 2 water, 0 land, 1 castle
	private int def;
	private int att;
	private Family attFamily;
	private int state; // 0 for the choosing troops, 1 for choosing cards, 2 for sword, 3 for troops destruction and 4 for the retrait
	CombatantCard attCard;
	CombatantCard defCard;
	
	public Battle(Territory attTerritory, Territory defTerritory){
		System.out.println("a new battle begin");
		this.attTerritory=attTerritory;
		this.defTerritory=defTerritory;
		attTroops= new int[4];
		attSupport= new LinkedList<Territory>();
		defSupport= new LinkedList<Territory>();
		/*attSupport= new int[4];
		defSupport= new int[4];*/
		if(defTerritory instanceof Water){
			groundType=2;
		}else if(defTerritory.getCastle()>0){
			groundType=1;
		}else{groundType=0;}
		def=0;
		att=0;
		attFamily = attTerritory.getFamily();
		state=0;
		attCard=null;
		defCard=null;
	}


	public void addTroop(int boat, int foot, int knight, int siege) {
		attTerritory.mouveTroops(new Water("trash"),boat,foot,knight, siege);
		attTroops[0]+=boat;
		attTroops[1]+=foot;
		attTroops[2]+=knight;
		attTroops[3]+=siege;
	}

	/** brute force without support and Family cards*/
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
	
	public int defPower(){
		int res=defTerritory.getOrder().getDefBonus();
		if(groundType==2){
			res+= defTerritory.getTroup().getEffectif();
		}else{
			res+=defTerritory.getTroup().getTroops()[1]+defTerritory.getTroup().getTroops()[2]*2;
		}
		for(Territory territory : defSupport){
			int[] troops =territory.getTroup().getTroops();
			res+=territory.getOrder().getOthBonus()+troops[0]+troops[1]+troops[2]*2+troops[3]*4*groundType;
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
	
	public void cont(){
		//choix des cartes
		//épée
		if(this.battleResolution()){
			//on detruit les troups du defenceur, il choisit une retraite
			//on retire l'ordre
			defTerritory.rmOrder();
			// on met les nouvelles troupes sur le territoire
			if(groundType==2){
				defTerritory.setTroup(new NavalTroup(attFamily,defTerritory, attTroops[0]));
			}else{
				defTerritory.setTroup(new GroundForce(attFamily,defTerritory, attTroops[1],attTroops[2],attTroops[3]));
			}
			System.out.println("victoire");
		}else{
			//on detruit les troups de l'attaquant
			System.out.println("defailt");
			attTroops[3]=0;//destruction of siege tower
			if(attTroops[0]+attTroops[1]+attTroops[2]!=0){
				if(attTerritory.getTroup()!=null){
					attTerritory.getTroup().addTroop(attTroops);
				}else if(groundType==2){
					attTerritory.setTroup(new NavalTroup(attFamily, attTerritory, attTroops[0]));
				}else{
					attTerritory.setTroup(new GroundForce(attFamily, attTerritory, attTroops[1],attTroops[2],0));
				}
			}
		}
	}

/**return true if the attaquant win, false if it's the defencer*/
	private boolean battleResolution() {
		return att>def; // changer pour prendre en compte la piste des fiefs
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
 * @return true if the player(setNum) is participating 
 */
	public boolean playerPartisipate(int seatNum) {
		return (attFamily.getPlayer()==seatNum || defTerritory.getFamily().getPlayer()==seatNum);
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
				this.state=2;
				System.out.println("Cartes jouée :");
				System.out.println("carte att "+attCard.getName());
				System.out.println("carte def "+defCard.getName());
				//on applique les effets de carte
					// la reine des epines 
					//les autres (vict,
				//on effectue le calcul la puissance final avant épée
				this.powerWithoutSword();
			}
		}
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
	private void powerWithoutSword() {
		att=this.attPower()+attCard.getPower();
		def=this.defPower()+defCard.getPower();
		System.out.println("attack power "+att);
		System.out.println("def power "+def);
	}


	public Family getDefFamily() {
		return defTerritory.getFamily();
	}
	
}
