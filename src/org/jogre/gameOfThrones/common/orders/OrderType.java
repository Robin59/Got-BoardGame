package org.jogre.gameOfThrones.common.orders;

public enum OrderType {
	RAI("raid"),ATT("attaque"),CON("consolidation"),DEF("defence"),SUP("support");
	
	private String name;
	private OrderType(String name){
		this.name=name;
	}
	public String toString(){
		return name;
	}
}
