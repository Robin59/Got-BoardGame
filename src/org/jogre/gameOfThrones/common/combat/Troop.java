package org.jogre.gameOfThrones.common.combat;


import org.jogre.gameOfThrones.common.Family;
import org.jogre.gameOfThrones.common.territory.Territory;

public abstract class Troop {
	private Family family;
	private Territory territory;
	private boolean naval;
	
	public Troop (Family family, Territory territory){
		this.family=family;
		this.territory=territory;
		//we add the territory to the family
		family.addTerritory(territory);
		//we add the troup to the territory
		territory.setTroup(this);
	}
	//detruit un nb de troup egal aux pertes, on est sur que les pertes sont inferieur aux effectifs
	public abstract void destruction (int causualties);
	// effectif pour le ravitaillement
	public abstract int getEffectif();
	
	/**add the current army to the total army (for battles)*/
	public abstract void addTroup(int[] troups);
	/**get the  troopq*/
	public abstract int[] getTroops();
	
	public Family getFamily(){
		return family;
	}
	
	public void moveTroup(){
		// changer les proprietaires de territoire
		//verifier la ration, fusionner les armées ...
	}
	
	public abstract boolean canMoveTo(Territory territory);
	
}
