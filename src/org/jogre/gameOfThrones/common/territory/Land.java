package org.jogre.gameOfThrones.common.territory;

import org.jogre.gameOfThrones.common.combat.GroundForce;
import org.jogre.gameOfThrones.common.combat.NavalTroup;

public class Land extends Territory {
	/** Influence is the bonus point gived when consolidate order is given*/
	private int influ;
	/***/
	private int supply;
	/***/
	private int recruit;
	
	private boolean inf;//quand on met un pion d'influence (boolean ne permet pas de connaitre le proprio du pion...)
	
	public Land(String name,int influ, int supply, int castle){
		super(name);
		this.influ=influ;
		this.supply=supply;
		this.castle=castle;
		recruit=castle;
	}
	
	public boolean canUseOrderOn(Territory territory){
		return (super.canUseOrderOn(territory) && territory instanceof Land);
	}

	@Override
	public boolean canWithdraw(Territory territory) {
		return territory instanceof Land && territory.getFamily()!=null && territory.getFamily()==this.getFamily();
	}

	@Override
	public int consolidation() {
		return 1+influ;
	}
	
	public int getRecruit(){
		return recruit;
	}
	public void haveRecruit(int recruitUsed){
		recruit-=recruitUsed;
		if(recruit<1){
			this.rmOrder();
		}
	}
	@Override
	public void recruit(int troopIndex) {
		if(troop==null){
			troop= new GroundForce(getFamily(), this,0,0,0);
		}
		switch(troopIndex){
		case 1:
			this.troop.addToop(0,1,0,0);
			break;
		case 2:
			this.troop.addToop(0,0,1,0);
			break;
		case 3:
			this.troop.addToop(0,0,0,1);
			break;
		}
	}
}
