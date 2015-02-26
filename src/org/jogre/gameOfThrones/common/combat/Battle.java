package org.jogre.gameOfThrones.common.combat;

import java.util.LinkedList;
import java.util.List;

import org.jogre.gameOfThrones.common.CombatantCard;
import org.jogre.gameOfThrones.common.Family;
import org.jogre.gameOfThrones.common.GameOfThronesModel;
import org.jogre.gameOfThrones.common.orders.Order;
import org.jogre.gameOfThrones.common.orders.OrderType;
import org.jogre.gameOfThrones.common.territory.Territory;
import org.jogre.gameOfThrones.common.territory.Water;

public abstract class Battle {
	
	protected Territory attTerritory;
	protected Territory defTerritory;
	protected int[] attTroops;
	protected List<Territory> attSupport;
	protected List<Territory> defSupport;
	/*Describe the type of the place were the battle took place, 2 for water, 0 land, 1 castle*/
	protected int groundType;
	protected int state; 
	protected Family attFamily;
	protected GameOfThronesModel model;
	protected Order attOrder;
	
	public Battle(Territory attTerritory, Territory defTerritory,GameOfThronesModel model,Order attOrder) {
		this.model=model;
		this.attTerritory=attTerritory;
		this.defTerritory=defTerritory;
		this.attOrder=attOrder;
		attFamily=attTerritory.getFamily();
		attTroops=new int[4];
		attSupport= new LinkedList<Territory>();
		defSupport= new LinkedList<Territory>();
		state=BATTLE_SUPPORT_PHASE;
		if(defTerritory instanceof Water){
			groundType=2;
		}else if(defTerritory.getCastle()>0){
			groundType=1;
		}else{groundType=0;}
		model.updateLabel();
	}
	
	/**
	 * This method is use to add troops to the attacker force
	 * @param boat the number of boat that are add to the attacker force
	 * @param foot the number of footman that are add to the attacker force
	 * @param knight the number of knight that are add to the attacker force
	 * @param siege the number of siege machine that are add to the attacker force
	 */
	public void addTroop(int boat, int foot, int knight, int siege) {
		attTerritory.getTroup().rmToop(boat, foot, knight, siege);
		attTroops[1]+=foot;
		attTroops[2]+=knight;
		attTroops[3]+=siege;
	}
	
	/**
	 * Add the territory (and the force that constitute it) to the defender force
	 * @param territory the territory that is add to the defender 
	 */
	public void addDefSupport(Territory territory) {
		System.out.println("defSupport");
		defSupport.add(territory);
		territory.getOrder().setUse(true);
		//on verifie si on peut commencer la bataille
		if(checkSupport()){
			startBattle();
		}
	}
	
	/**
	 * Add the territory (and the force that constitute it) to the attacker force
	 * @param territory the territory that is add to the attacker 
	 */
	public void addAttSupport(Territory territory){
		System.out.println("AttSupport"+territory.getName());
		attSupport.add(territory);
		territory.getOrder().setUse(true); 
		//on verifie si on peut commencer la bataille
		if(checkSupport()){
			startBattle();			
		}
	}

	/**
	 * Check if all territory with support have use their orders (or have choose to stay neutral)
	 * @return true if all supports are sent(or choose to stay neutral) 
	 */
	public boolean checkSupport(){	
		System.out.println("Inside checkSupport");
		for (Territory territory : defTerritory.getNeighbors()){
			if(territory.getOrder()!=null && territory.getOrder().getType()==OrderType.SUP && !territory.getOrder().getUse()){
				return false;
			}
		}
		attTerritory.rmOrder();
		return true;
	}

	/**
	 * Return a boolean saying if a player is participating or not to this battle (support dosen't count)
	 * @param seatNum
	 * @return true if the player is participating 
	 */
	public boolean playerPartisipate(int seatNum) {
		return (attFamily.getPlayer()==seatNum);
	}
	
	public Family getAttFamily (){
		return attFamily;
	}
		
	/** 
	 * This method calculate the brute force of the attacker army without Family cards and valaryan sword
	 * @return the force of the attacker army
	 * */
	public int attPower(){
		int res=attOrder.getOthBonus();
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
	public abstract int defPower();
	
	public Territory getAttTerritory(){
		return attTerritory;
	}
	public Territory getDefTerritory(){
		return defTerritory;
	}
	/***/
	public abstract void startBattle();
	public abstract Family getDefFamily();
	public abstract boolean canPlayCard(Family family);
	
	// the method bellow should only be call by a player vs player battle
	public void useSword(){};
	public void dontUseSword(){};
	public void playCard(CombatantCard card, Family family) {}
	
	/**
	 * 
	 * @return
	 */
	public int getState(){
		return state;
	}
	
	/**
	 * 
	 */
	public void endBattle(){ // this method could change and take the winner in parameter to factories more code
		for(Territory territory : defTerritory.getNeighbors()){
			if(territory.getOrder()!=null && territory.getOrder().getType()==OrderType.SUP){
				territory.getOrder().setUse(false);
			}
		}
		state=BATTLE_END;
	}
	
	public static final int BATTLE_SUPPORT_PHASE=0;
	public static final int BATTLE_CHOOSE_CARD=1;
	public static final int BATTLE_PLAY_SWORD=2;
	public static final int BATTLE_WITHDRAWAL=3;
	public static final int BATTLE_END=4;
	public static final int BATTLE_SHOW_CARDS=5;//when players can see both cards played during the battle

	public void withdraw(Territory territory) {
		// TODO Auto-generated method stub
		
	}

	
}
