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
		//ouest
		Territory moatCailin = new Land("Moat Cailin",0,0,1);
		Territory GreyWaterWatch = new Land("GreyWater Watch",0,1,0);
		Territory FlintFinger = new Land("Flint's Finger",0,0,1);
		Territory sunsetSea = new Water("Sunset Sea");
		//reste à ajouter les territoires voisins
		Territory IronmanBay = new Water("Ironman's Bay");
		Territory GoldenSound = new Water("The Golden Sound");
		Territory Pike = new HomeLand("Pike",1,1);
		Territory Seaguard = new Land("Seaguard",1,1,2);
		Territory Riverrun = new Land("Riverrun",1,1,2);
		Territory Twins = new Land("The Twins",1,0,0);
		Territory Fingers = new Land("The Fingers",0,1,0);
		Territory MountainsMoon = new Land("The Moutains of the Moon",1,0,0);
		Territory Eyrie = new Land("The Eyrie",1,1,1);
		Territory Lannisport = new HomeLand("Lannisport",0,2);
		
		//Ajout des voisins 
		//(tout faire en double, ex : Karhold.addTerritory(Winterfell); Winterfell.addTerritory(Karhold); )
		sunsetSea.addTerritory(FlintFinger);
		sunsetSea.addTerritory(BayOfIce);
		sunsetSea.addTerritory(IronmanBay);
		FlintFinger.addTerritory(GreyWaterWatch);
		FlintFinger.addTerritory(sunsetSea);
		FlintFinger.addTerritory(IronmanBay);
		FlintFinger.addTerritory(BayOfIce);
		GreyWaterWatch.addTerritory(moatCailin);
		GreyWaterWatch.addTerritory(FlintFinger);
		GreyWaterWatch.addTerritory(IronmanBay);
		GreyWaterWatch.addTerritory(BayOfIce);
		moatCailin.addTerritory(WhiteHarbor);
		moatCailin.addTerritory(Winterfell);
		moatCailin.addTerritory(GreyWaterWatch);
		moatCailin.addTerritory(NarrowSea);
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
		Winterfell.addTerritory(moatCailin);
		NarrowSea.addTerritory(WhiteHarbor);
		NarrowSea.addTerritory(WidowWatch);
		NarrowSea.addTerritory(ShiveringSea);
		NarrowSea.addTerritory(moatCailin);
		BayOfIce.addTerritory(CastleBlack);
		BayOfIce.addTerritory(Winterfell);
		BayOfIce.addTerritory(StonyShore);
		BayOfIce.addTerritory(GreyWaterWatch);
		BayOfIce.addTerritory(sunsetSea);
		BayOfIce.addTerritory(FlintFinger);
		StonyShore.addTerritory(Winterfell);
		StonyShore.addTerritory(BayOfIce);
		WhiteHarbor.addTerritory(Winterfell);
		WhiteHarbor.addTerritory(WidowWatch);
		WhiteHarbor.addTerritory(ShiveringSea);
		WhiteHarbor.addTerritory(NarrowSea);
		WhiteHarbor.addTerritory(moatCailin);
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
		board.put("Moat Cailin", moatCailin);
		board.put("GreyWater Watch", GreyWaterWatch);
		board.put("Flint's Finger", FlintFinger);
		board.put("Sunset Sea", sunsetSea);
		board.put("Ironman's Bay", IronmanBay);
		board.put("The Golden Sound", GoldenSound);
		board.put("Pike", Pike);
		board.put("Seaguard", Seaguard);
		board.put("Riverrun", Riverrun);
		board.put("The Twins", Twins);
		board.put("The Fingers", Fingers);
		board.put("The Mountains of the Moon", MountainsMoon);
		board.put("The Eyrie", Eyrie);
		board.put("Lannisport", Lannisport);
	}


	public Territory getTerritory(String name){
		return board.get(name);
	}
}
