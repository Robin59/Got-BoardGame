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
	/**get the total of troops (fresh + routed troops)*/
	public abstract int[] getTroops();
	/**
	 * Return the numbers of routed troops
	 * @return the numbers of routed troops
	 */
	public abstract int[] getRoutedTroops();
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
	
	/**
	 * This method is call at the beginning of a new turn, each routed troop come back to normal state
	 */
	public abstract void regroupRoutedTroops();
	/**
	 * Return the power when defending of this troop 
	 * @return the power when defending of this troop
	 */
	public abstract int getDefPower();
	/**
	 * Return the power when supporting attacker with this troop
	 * @param attCastle true if the territory attacked contain a castle 
	 * @return the power when supporting attacker with this troop
	 */
	public abstract int getAttSuportPower(boolean attCastle);
	/**
	 * Transform some fresh troops to routed troops
	 * @param routedTroops the troops that are now routed (example : routedTroops[2]=1 means that there is one more knight that is now routed) 
	 */
	public abstract void addRoutedTroops(int[]routedTroops);
	/**
	 * destroy the troops that are routed
	 */
	public abstract void destroyRoutedTroops();
}
