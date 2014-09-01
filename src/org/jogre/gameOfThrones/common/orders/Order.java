package org.jogre.gameOfThrones.common.orders;

import org.jogre.gameOfThrones.common.Family;



public class Order {
	
	/**indicate if the order is a star order or not*/
	private boolean star;
	/**indicate the teritory where the order is given, null if none*/
	//utile ? 
	/**indicate the family who gaved the order*/
	private Family family; // utile ?
	
	/***/
	private int defBonus;
	private int othBonus;
	
	private OrderType type;
	
	/***/
	public Order(boolean star, int defBonus, int othBonus, OrderType type){
		this.defBonus=defBonus;
		this.othBonus=othBonus;
		this.type=type;
		this.star=star;
	}
	/**
	 * 
	 * @return
	 */
	public int getDefBonus(){return defBonus;}
	
	public int getOthBonus(){return othBonus;}
	
	public int[] getOrderInt(){
		int[] res = new int[2];
		int type =this.type.ordinal();
		int bonus= this.othBonus;// juste pour le -1 de l'attaque
		if(star) bonus=1;
		res[0]=type;res[1]=bonus;
		return res;
	}
	public OrderType getType(){
		return type;
	}
	public String toString(){// A AMELIORER!!
		return type.toString()+" "+defBonus+" "+othBonus;
	}
	
	public boolean getStar(){
		return star;
	}
	
	public void execute(){ //REFLECHIRE
		
	}
}
