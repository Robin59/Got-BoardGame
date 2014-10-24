package org.jogre.gameOfThrones.common.territory;

import java.util.LinkedList;
import java.util.List;

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
	/**
	 * 
	 * @param name
	 * @param influ
	 * @param supply
	 * @param castle
	 */
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
	@Override
	public void resetRecruit(){
		recruit=castle;
	}
	@Override
	public void recruitmentDone(){
		recruit=0;
	}
	@Override
	public int getRecruit(){
		return recruit;
	}
	/*public void haveRecruit(int recruitUsed){
		
	}*/
	@Override
	public void recruit(int troopIndex) {
		if(troop==null){
			troop= new GroundForce(getFamily(),0,0,0);
		}
		switch(troopIndex){
		case 1:
			this.troop.addToop(0,1,0,0);
			recruit-=1;
			break;
		case 2:
			this.troop.addToop(0,0,1,0);
			recruit-=2;
			break;
		case 3:
			this.troop.addToop(0,0,0,1);
			recruit-=2;
			break;
		}
		if(recruit<1 && order!=null){
			this.rmOrder();
		}
	}
	
	public int getSupply(){
		return supply;
	}
	
	public int westerosCardGameOfThrones() {
		return influ;
	}

	@Override
	protected boolean canGoTo(Territory territory) {
		return territory instanceof Land && (navalTransport(territory));
	}
	
	protected Boolean navalTransport(Territory territory){
		List<Territory> alredyCheck = new LinkedList<Territory>();
		return navalTransport(territory, alredyCheck); 
	}
	
}
