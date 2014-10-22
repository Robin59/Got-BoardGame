package org.jogre.gameOfThrones.common.territory;

import org.jogre.gameOfThrones.common.Family;
import org.jogre.gameOfThrones.common.combat.NavalTroup;


/**
 * A port is controlled by a land, so the owner of a port and is boat are always the same as the land owner
 * @author robin
 *
 */
public class Port extends Water {

	private Territory land;
	private Territory water;

	
	public Port(String name) {
		super(name);
	}

	
	public void addTerritory(Territory ter){
		super.addTerritory(ter);
		if(ter instanceof Water){
			water=ter;
		}else{
			land=ter;
		}
	}
	
	/** when the owner of land where the port is change, he gain the boats that are inside the port */
	public void changeOwner(Family family){
		if(troop!=null){
			int effectif =troop.getEffectif()-1;
			this.setTroup(new NavalTroup(family, 1));
			while(effectif>0 && family.checkSupplyLimits(this)){
				this.getTroup().addToop(1, 0, 0, 0);
				effectif--;
			}
		}
	}
	
	public Family getFamily() {
		return land.getFamily();
	}
	
	@Override
	public int consolidation() {
		if(commerce())return 1;
		else return 0;
	}
	
	@Override
	public int westerosCardGameOfThrones() {
		return consolidation();
	}
	// with this method we can know if the port can make commerce
	private boolean commerce(){
		return this.troop!=null && (water.getTroup()==null ||water.getTroup().getFamily()==this.getFamily());
	}
}
