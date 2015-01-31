package org.jogre.gameOfThrones.common.territory;

import org.jogre.gameOfThrones.common.Family;

public class HomeLand extends Land {
	/*This boolean indicate if there is still a garrison on this land*/
	private boolean garrison;
	/*the original owners of this land, they don't need influence token to keep this land*/
	private String originalOwner;
	
	public HomeLand(String name, int influ, int supply, String family) {
		super(name, influ, supply, 2);//tjs une forterresse ?
		garrison=true;
		originalOwner=family;
	}
	
	@Override
	public boolean haveGarrison(){
		return garrison;
	}
	
	/**
	 * Destruct the garrison on this land
	 */
	public void destructGarrison(){
		garrison=false;
	}
	
	/**
	 * Return true if the family give in parameter is the original owner of this land
	 * @param family  
	 * @return true if the family give in parameter is the original owner of this land
	 */
	public boolean originalOwner(Family family){
		return originalOwner.equals(family.getName());
	}
	
}
