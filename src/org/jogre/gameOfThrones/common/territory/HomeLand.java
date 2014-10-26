package org.jogre.gameOfThrones.common.territory;

public class HomeLand extends Land {
	/*This boolean indicate if there is still a garrison on this land*/
	private boolean garrison;
	
	public HomeLand(String name, int influ, int supply) {
		super(name, influ, supply, 2);//tjs une forterresse ?
		garrison=true;
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
	

}
