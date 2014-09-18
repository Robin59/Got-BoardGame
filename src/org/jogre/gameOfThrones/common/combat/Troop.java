package org.jogre.gameOfThrones.common.combat;


import org.jogre.gameOfThrones.common.Family;
import org.jogre.gameOfThrones.common.territory.Territory;

public abstract class Troop {
	private Family family;
	//private Territory territory;
	private boolean naval;
	
	public Troop (Family family, Territory territory){
		this.family=family;
		//this.territory=territory;
		family.addTerritory(territory);// NON UTILE DEJA AJOUTE DANS LA METHODE setTroop DE TERRITORY 
		//we add the troup to the territory
		territory.setTroup(this);
	}
	/***/
	public abstract void destruction (int casualties);
	// effectif pour le ravitaillement
	public abstract int getEffectif();
	
	/**add to this troop some new force*/
	public abstract void addTroop(int[] troups);
	/**get the  troop*/
	public abstract int[] getTroops();
	
	public Family getFamily(){
		return family;
	}
	
	public void moveTroup(){
		// changer les proprietaires de territoire
		//verifier la ration, fusionner les armées ...
	}
	
	public abstract void addToop(int ship,int foot,int knight,int siege);
	public abstract void rmToop(int ship,int foot,int knight,int siege);
	
	public abstract boolean canMoveTo(Territory territory);
	
}
