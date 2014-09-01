package org.jogre.gameOfThrones.common;

import org.jogre.gameOfThrones.common.combat.CombatResolution;

/**
 * This class represent the combatant card that give bonus during the fights 
 * @author robin
 *
 */
public class CombatantCard {
	//family (vraiment necessaire de mettre un pointeur ?)
	
	/**The name of the personage that is represented by this card */
	private String name;
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
	 */
	protected int priority;
	
	
	/**
	 * 
	 * @param name
	 * @param power
	 * @param sword
	 * @param tower
	 */
	
	public CombatantCard(String name, int power, int sword,int tower){
		this.name = name;
		this.power=power;
		this.sword=sword;
		this.tower=tower;
		priority=5; // you have to change this value for card with special effect
	}
	
	//special effect (brise toutes les regles du jeu, doivent donc etre en dessous
	//creer une classe special pour ça (ou une classe regle))
	//BRISE TOUTE LES REGLES OU SEULEMENT CELLES DES COMBATS ?
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
	public int getPriority(){
		return priority;
	}
	
	public void effect(CombatResolution combat){
		// nothing here, have to be surcharge 
	}
	
	
	
}
