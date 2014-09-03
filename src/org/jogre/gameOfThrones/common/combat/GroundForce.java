package org.jogre.gameOfThrones.common.combat;

import org.jogre.gameOfThrones.common.Family;
import org.jogre.gameOfThrones.common.territory.Land;
import org.jogre.gameOfThrones.common.territory.Territory;

public class GroundForce extends Troop {
	private int infentrie;
	private int knight;
	private int machine;
	
	public GroundForce(Family family, Territory territory,int infenterie, int knight, int machine) {
		super(family, territory);
		this.infentrie=infenterie;
		this.knight=knight;
		this.machine=machine;
	}

	@Override
	public void destruction(int causualties) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getEffectif() {
		return infentrie+knight+machine;
	}

	@Override
	public void addTroup(int[] troups) {
		troups[1]+=infentrie;
		troups[2]+=knight;
		troups[3]+=machine;
	}

	@Override
	public boolean canMoveTo(Territory territory) {
		return (territory instanceof Land);// ajouter les terres relier par la mer 
	}

	@Override
	public int[] getTroops() {
		int[]res={0,infentrie,knight,machine};
		return res;
	}

	@Override
	public void addToop(int ship, int foot, int knigth, int siege) {
		infentrie+=foot;
		this.knight+=knigth;
		this.machine+=siege;
	}
	@Override
	public void rmToop(int ship, int foot, int knigth, int siege) {
		infentrie-=foot;
		this.knight-=knigth;
		this.machine-=siege;
	}
}
