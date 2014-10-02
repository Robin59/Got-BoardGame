package org.jogre.gameOfThrones.common.territory;

import java.util.HashMap;// UTILISER UNE TABLE HACHAGE OU LISTE ???
import java.util.Map;


public class BoardModel {

	public Map<String,Territory> board;//rendre privé
	//public static BoardModel BAORD = new BoardModel();// Inutile maintenant
	
	public BoardModel(){
		board = new HashMap<String,Territory>();
		
		//le nord creation (terre)
		Territory Winterfell = new HomeLand("Winterfell",1,1);
		Territory CastleBlack = new Land("Castle Black",1,0,0);
		Territory Karhold = new Land("Karhold",1,0,0);
		Territory StonyShore = new Land("The Stony Shore",0,1,0);
		Territory WhiteHarbor = new Land("White Harbor",0,0,1);
		Territory WidowWatch = new Land("Widow's Watch",0,1,0);
		//le nord creation (mer)
		Territory BayOfIce = new Water("Bay Of Ice");
		Territory ShiveringSea = new Water("Shivering Sea");
		Territory NarrowSea = new Water("Narrow Sea");
		//
		ShiveringSea.addTerritory(Karhold);
		ShiveringSea.addTerritory(CastleBlack);
		ShiveringSea.addTerritory(Winterfell);
		ShiveringSea.addTerritory(WhiteHarbor);
		ShiveringSea.addTerritory(WidowWatch);
		ShiveringSea.addTerritory(NarrowSea);
		CastleBlack.addTerritory(Winterfell);
		CastleBlack.addTerritory(Karhold);
		CastleBlack.addTerritory(BayOfIce);
		CastleBlack.addTerritory(ShiveringSea);
		Karhold.addTerritory(ShiveringSea);
		Karhold.addTerritory(CastleBlack);
		Karhold.addTerritory(Winterfell);
		Winterfell.addTerritory(Karhold);
		Winterfell.addTerritory(CastleBlack);
		Winterfell.addTerritory(StonyShore);
		Winterfell.addTerritory(ShiveringSea);
		Winterfell.addTerritory(BayOfIce);
		Winterfell.addTerritory(WhiteHarbor);
		NarrowSea.addTerritory(WhiteHarbor);
		NarrowSea.addTerritory(WidowWatch);
		NarrowSea.addTerritory(ShiveringSea);
		BayOfIce.addTerritory(CastleBlack);
		BayOfIce.addTerritory(Winterfell);
		BayOfIce.addTerritory(StonyShore);
		StonyShore.addTerritory(Winterfell);
		StonyShore.addTerritory(BayOfIce);
		WhiteHarbor.addTerritory(Winterfell);
		WhiteHarbor.addTerritory(WidowWatch);
		WhiteHarbor.addTerritory(ShiveringSea);
		WhiteHarbor.addTerritory(NarrowSea);
		WidowWatch.addTerritory(WhiteHarbor);
		WidowWatch.addTerritory(NarrowSea);
		WidowWatch.addTerritory(ShiveringSea);
		
		
		//le nord ajout
		board.put("Winterfell", Winterfell);
		board.put("Castle Black", CastleBlack);
		board.put("Karhold", Karhold);
		board.put("Shivering Sea", ShiveringSea);
		board.put("Narrow Sea", NarrowSea);
		board.put("Bay Of Ice", BayOfIce);
		board.put("The Stony Shore", StonyShore);
		board.put("White Harbor", WhiteHarbor);
		board.put("Widow's Watch", WidowWatch);
	}


	public Territory getTerritory(String name){
		return board.get(name);
	}
}
