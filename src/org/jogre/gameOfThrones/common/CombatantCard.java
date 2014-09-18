package org.jogre.gameOfThrones.common;


import org.jogre.gameOfThrones.common.combat.CombatResolution;

/**
 * This class represent the combatant card that give bonus during the fights 
 * @author robin
 *
 */
public class CombatantCard {
	
	/**The name of the personage that is represented by this card */
	private String name;
	/***/
	//private int index;
	/** Fighting skill of the card, this is the bonus to the combat force*/
	private int power;
	/** The swords represent the bonus to the destructing force */
	private int sword;
	//tower
	private int tower;
	
	/**The special effect priority influ on when you have to play the effect on the card
	 * 5= no special effect
	 * 1= play before everything
	 * 3= play after calculate brute force
	 * 4= play after knowing who win
	 
	protected int priority;
	// 1 est tirion, 2 avant le combat
	*/
	
	
	/**
	 * 
	 * @param name
	 * @param power
	 * @param sword
	 * @param tower
	 */
	
	public CombatantCard(String name, /*int index ,*/int power, int sword,int tower){
		this.name = name;
		this.power=power;
		this.sword=sword;
		this.tower=tower;
		//priority=5; // you have to change this value for card with special effect
	}
	
	
	public String getName(){
		return name;
	}
	public int getPower(){
		return power;
	}
	public int getSword(){
		return sword;
	}
	public int getTower(){
		return tower;
	}
	/*public int getPriority(){
		return priority;
	}*/
	
	/*public void effect(CombatResolution combat){
		// nothing here, have to be surcharge 
	}*/
	
	/*public int getIndex(){
		return index;
	}*/
	
}
