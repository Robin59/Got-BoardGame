package org.jogre.gameOfThrones.common.combat;

import org.jogre.gameOfThrones.common.Family;
import org.jogre.gameOfThrones.common.territory.Land;
import org.jogre.gameOfThrones.common.territory.Territory;

public class GroundForce extends Troop {
	private int infentrie;
	private int knight;
	private int machine;
	private int[]routedTroops;
	
	public GroundForce(Family family, int infenterie, int knight, int machine) {
		super(family);
		this.infentrie=infenterie;
		this.knight=knight;
		this.machine=machine;
		routedTroops=new int[3]; 
	}

	@Override
	public void destruction(int casualties) {
		if(casualties>infentrie){
			knight-=(casualties-infentrie);
			infentrie=0;
		}else{
			infentrie-=casualties;
		}
	}

	@Override
	public int getEffectif() {
		return infentrie+knight+machine;
	}

	@Override
	public void addTroop(int[] troups) {
		infentrie+=troups[1];
		knight+=troups[2];
		machine+=troups[3];
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
	public int[] getRoutedTroops(){
		int[] res={0,routedTroops[0],routedTroops[1],routedTroops[2]};
		return res;
	}
	@Override
	public void rallyingTroops(){
		routedTroops= new int[3];
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

	@Override
	public void regroupRoutedTroops() {
		for (int i=0;i<3;i++){
			routedTroops[i]=0;
		}
	}
	@Override
	public int getAttSuportPower(boolean attCastle){
		int res=(infentrie-routedTroops[0])+(knight-routedTroops[1])*2;
		if(attCastle) res+=(machine-routedTroops[2])*4;
		return res;
	}
	@Override
	public int getDefPower() {
		return (infentrie-routedTroops[0])+(knight-routedTroops[1])*2;
	}

	@Override
	public void addRoutedTroops(int[] routedTroops) {
		for(int i=0; i<3; i++){
			this.routedTroops[i]=routedTroops[i+1];
		}
	}

	@Override
	public void destroyRoutedTroops() {
		infentrie-=routedTroops[1];
		knight-=routedTroops[2];
		for(int i=0; i<3; i++){
			this.routedTroops[i]=0;
		}
	}
}
