package org.jogre.gameOfThrones.common.territory;

public class Land extends Territory {
	/** Influence is the bonus point gived when consolidate order is given*/
	private int influ;
	/***/
	private int supply;
	
	private boolean inf;//quand on met un pion d'influence (boolean ne permet pas de connaitre le proprio du pion...)
	
	public Land(String name,int influ, int supply, int castle){
		super(name);
		this.influ=influ;
		this.supply=supply;
		this.castle=castle;
	}
	
	public boolean canUseOrderOn(Territory territory){
		return (super.canUseOrderOn(territory) && territory instanceof Land);
	}

	@Override
	public boolean canWithdraw(Territory territory) {
		return territory instanceof Land && territory.getFamily()!=null && territory.getFamily()==this.getFamily();
	}
}
