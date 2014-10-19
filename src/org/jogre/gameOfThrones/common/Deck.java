package org.jogre.gameOfThrones.common;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Deck {
	
	
	private List<String> deck;
	
	public Deck (int phase){
		deck= new LinkedList<String>();
		switch(phase){
		case 1 :
			deck.add("Summer");
			deck.add("Winter");
			deck.add("Mustering");
			deck.add("Mustering");
			deck.add("Mustering");
			deck.add("Supply");
			deck.add("Supply");
			deck.add("Supply");
			deck.add("ThroneOfBlades");
			deck.add("ThroneOfBlades");
			break;
		case 2 :
			deck.add("ClashOfKings");
			deck.add("GameOfThrones");
			deck.add("ClashOfKings");
			deck.add("GameOfThrones");
			deck.add("ClashOfKings");
			deck.add("GameOfThrones");
			deck.add("DarkWingsDarkWords");
			deck.add("DarkWingsDarkWords");
			deck.add("Summer");
			deck.add("Winter");
			break;
		case 3 :
			deck.add("Wildings");
			deck.add("Wildings");
			deck.add("Wildings");
			deck.add("FeastForCrows");
			deck.add("RainsOfAutumn");
			deck.add("SeaOfStorms");
			deck.add("StromOfSwords");
			deck.add("WebOfLies");
			deck.add("PutToTheSword");
			deck.add("PutToTheSword");
			break;
		default:
			deck.add("SkinchangerScout");
			deck.add("SilenceAtTheWall");
			//deck.add("RattleshirtRaiders");
			break;
		}
		Collections.shuffle(deck);
	}
	
	
	//a changer pour permettre tous les decks d'avoir les mêmes cartes
	public String nextCard(){
		return deck.remove(0);
	}
	
	public void suffle(){
		Collections.shuffle(deck);
	}
	
	public void cardPlayed(String card){
		deck.remove(card);
	}
}
