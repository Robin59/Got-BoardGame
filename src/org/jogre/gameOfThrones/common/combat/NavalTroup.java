package org.jogre.gameOfThrones.common.combat;

import org.jogre.gameOfThrones.common.Family;
import org.jogre.gameOfThrones.common.territory.Land;
import org.jogre.gameOfThrones.common.territory.Territory;


public class NavalTroup extends Troop {
	
	private int effectif;
	private int routedTroops;
	
	public NavalTroup(Family family, int effectif) {
		super(family/*, territory*/);
		this.effectif=effectif;
		routedTroops=0;
	}

	public int getEffectif(){
		return effectif;
	}

	@Override
	public void destruction(int causualties) {
			effectif-=causualties;
	}

	@Override
	public void addTroop(int[] troups) {
		effectif+=troups[0];
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

	@Override
	public void addToop(int ship, int foot, int knigth, int siege) {
		effectif+=ship;
		
	}
	public void rmToop(int ship,int foot,int knigth,int siege){
		effectif-=ship;
	}
	
	@Override
	public int getDefPower(){
		return effectif-routedTroops;
	}
	@Override
	public void regroupRoutedTroops() {
		routedTroops=0;
	}

	@Override
	public void addRoutedTroops(int[] routedTroops) {
		this.routedTroops=routedTroops[0];
	}

	@Override
	public void destroyRoutedTroops() {
		effectif-=routedTroops;
		routedTroops=0;
	}
}
