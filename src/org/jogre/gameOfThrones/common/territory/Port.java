package org.jogre.gameOfThrones.common.territory;

import org.jogre.gameOfThrones.common.Family;



public class Port extends Water {
	public Port(String name) {
		super(name);
	}


	private Territory land;
	
	// Un port donne acces à une mer et est rataché à une terre 
	
	//peut-etre surchager la methode qui ajoute les teritoires pour affecter le proprietaire d'un port au propriétaire 
	public Family getFamily() {
		return land.getFamily();
	}
	
	
	// methode pour savoir si un port et sous blocus
	public boolean commerce(){
		// troup =! null
		// territoire voisin n'appartient pas à ennemy
		return false;
	}
}
