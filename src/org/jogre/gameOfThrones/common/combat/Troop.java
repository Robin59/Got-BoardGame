package org.jogre.gameOfThrones.common.combat;


import org.jogre.gameOfThrones.common.Family;
import org.jogre.gameOfThrones.common.territory.Territory;

public abstract class Troop {
	private Family family;
	
	
	public Troop (Family family ){
		this.family=family;
	}
	/**
	 * remove some soldier to this troop
	 * @param casualties the number of soldier that are gonna be remove 
	 */
	public abstract void destruction (int casualties);
	/**
	 * @return
	 */
	public abstract int getEffectif();
	
	/**add to this troop some new force*/
	public abstract void addTroop(int[] troups);
	/**get the  troop*/
	public abstract int[] getTroops();
	
	public Family getFamily(){
		return family;
	}
	
	public void moveTroup(){
		// changer les proprietaires de territoire
		//verifier la ration, fusionner les armées ...
	}
	
	public abstract void addToop(int ship,int foot,int knight,int siege);
	
	/**
	 * Remove the number of troops given in parameters (be careful don't use negative numbers)  
	 * @param ship the number of ship that will be removed
	 * @param foot the number of footman that will be removed
	 * @param knight the number of knight that will be removed
	 * @param siege the number of siege machine that will be removed
	 */
	public abstract void rmToop(int ship,int foot,int knight,int siege);
	
	public abstract boolean canMoveTo(Territory territory);
	
}
