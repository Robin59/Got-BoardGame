package org.jogre.gameOfThrones.common.combat;

import org.jogre.gameOfThrones.common.CombatantCard;
import org.jogre.gameOfThrones.common.orders.OrderType;
import org.jogre.gameOfThrones.common.territory.*;



public class CombatResolution {

	private boolean navalBattle;
	private Territory attTer;
	private Territory defTer;
	private int attPower;
	private int defPower;
	//sord and tower for Att and def
	private int attSword;
	private int defSword;
	private int attTower;
	private int defTower;
	//the number of ship, footman, knight, and siege machines
	private int[] attTroups={0,0,0,0};// ne fonctionne pas dans le constructeur
	private int[] defTroups={0,0,0,0};
	//the combatant Cards
	private CombatantCard attCard;
	private CombatantCard defCard;
	
	public CombatResolution(Territory att, Territory def){
		attTer=att;
		defTer=def;
		if (defTer instanceof Water){
			navalBattle=true;
		}else{ navalBattle=false;}
		attPower=0;
		defPower=0;
		attSword=0;
		defSword=0;
		attTower=0;
		defTower=0;
	}
	
	/**
	 * True if the attaquant win
	 * False if the defencer win 
	 */
	public boolean resolution(){
	
		
		//calcul des forces brute
		/** first we add the orders bonus*/
		defPower+=defTer.getOrder().getDefBonus();
		attPower+=attTer.getOrder().getOthBonus();
		/*then we add the troups (there is always, if not there is no combat)*/
		attTer.getTroup().addTroup(attTroups);
		defTer.getTroup().addTroup(defTroups);
		//demande des soutiens (par rapport au teritoir defenceur)
		for (Territory suppTer : defTer.getNeighbors()){
			if(!navalBattle || suppTer instanceof Water){
				if(suppTer.getOrder().getType()==OrderType.SUP){
					if(suppTer.getFamily().supportDef(suppTer, attTer)){
						defPower+=suppTer.getOrder().getOthBonus();
						suppTer.getTroup().addTroup(defTroups);
					}else if(suppTer.getFamily().supportAtt(suppTer, defTer)){
						attPower+=suppTer.getOrder().getOthBonus();
						suppTer.getTroup().addTroup(attTroups);
					}
					
				}
			}
		}
		//each player chose a combatant card
		attCard=attTer.getFamily().playCombatantCard();
		defCard=defTer.getFamily().playCombatantCard();
		//applicate effect with order priority and under 4
		if(attCard.getPriority()<4 && attCard.getPriority()<defCard.getPriority()){
			attCard.effect(this);
			//ask if we have to play the defencer card effect
			if (defCard.getPriority()<4 ){
				defCard.effect(this);
			}else if (defCard.getPriority()<4 ){
				defCard.effect(this);
				//ask if we have to play the attaquant card effect
				if (attCard.getPriority()<4 ){
					attCard.effect(this);
				}
			}
		}
		// applicate the bonus from the cards
		attPower+=attCard.getPower();
		defPower+=defCard.getPower();
		attTower+=attCard.getTower();
		attSword+=attCard.getSword();
		defSword+=defCard.getSword();
		defTower+=defCard.getTower();
		//add the power from the troups
		if(defTer.getCastle()!=0){//for siege machines
			attPower+=attTroups[3]*4;
		}
		attPower+=attTroups[2]*2+attTroups[1]+attTroups[0];
		defPower+=defTroups[2]*2+defTroups[1]+defTroups[0];
		//possibilité de l'epee
		//carte de combat aléatoire
		//testing egality case
		if (attPower==defPower){//on regarde la piste d'influence 
			return attTer.getFamily().getFiedomsTrack()<defTer.getFamily().getFiedomsTrack();
		}else{
			return attPower>defPower;
		}
	}
	
	// on calcule les pertes(et destructions), les retraites le changement de territoire
	
	/**
	 * accesser for the specials effects cards
	 */
	public Territory getAttTer(){
		return attTer;
	}
	public Territory getDefTer(){
		return defTer;
	}
	public int[] getAttTroups(){
		return attTroups;
	}
	public int[] getDefTroups(){
		return defTroups;
	}
	public CombatantCard getAttCard(){
		return attCard;
	}
	public CombatantCard getDefCard(){
		return defCard;
	}
	public void addAttPower(int bonus){
		attPower+=bonus;
	}
	public void addDefPower(int bonus){
		defPower+=bonus;
	}
}
