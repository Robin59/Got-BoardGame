package org.jogre.gameOfThrones.common.combat;

import org.jogre.gameOfThrones.common.Family;
import org.jogre.gameOfThrones.common.territory.Land;
import org.jogre.gameOfThrones.common.territory.Territory;


public class NavalTroup extends Troop {
	
	private int effectif;
	
	public NavalTroup(Family family, Territory territory, int effectif) {
		super(family, territory);
		this.effectif=effectif;
	}

	public int getEffectif(){
		return effectif;
	}

	@Override
	public void destruction(int causualties) {
			effectif-=causualties;
	}

	@Override
	public void addTroup(int[] troups) {
		troups[0]+=effectif;
	}
	
	@Override
	public boolean canMoveTo(Territory territory) {
		return !(territory instanceof Land);
	}

	@Override
	public int[] getTroops() {
		int[]res={effectif,0,0,0};
		return res;
	}

}
