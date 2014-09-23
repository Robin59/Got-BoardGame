package org.jogre.gameOfThrones.common;


/**
 * This class represent the combatant card that give bonus during the fights 
 * @author robin
 *
 */
public class CombatantCard {
	
	/**The name of the personage that is represented by this card */
	private String name;
	/** Fighting skill of the card, this is the bonus to the combat force*/
	private int power;
	/** The swords represent the bonus to the destructing force */
	private int sword;
	//tower
	private int tower;
	
	
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
	
	
}
