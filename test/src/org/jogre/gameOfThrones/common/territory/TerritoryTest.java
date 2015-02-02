package org.jogre.gameOfThrones.common.territory;

import org.jogre.gameOfThrones.common.Family;
import org.jogre.gameOfThrones.common.combat.GroundForce;
import org.jogre.gameOfThrones.common.combat.NavalTroup;
import org.junit.Test;
import static org.junit.Assert.*;

public class TerritoryTest{

	@Test
	 public void constructor() {
		//contruction territory test
		Territory territory= new Land("test",0,0,0);
		Territory territory2= new Water("test");
		assertNotNull(territory);
		assertNotNull(territory2);
		//construction board test
		 BoardModel board = new BoardModel();
		 assertNotNull(board);
		 assertNotNull(board.getTerritory("Winterfell"));
	 }
	
	
	@Test
	public void territoryOwner(){ 
		// test if a territory have the correct owner
		 Territory territory= new Land("test",0,0,0);
		 assertNull(territory.getFamily());
		 Family family = new Family(1, null);
		 territory.setTroup(new GroundForce(family,1,0,0));
		 assertNotNull(territory.getFamily());
		 assertEquals(territory.getFamily(),family);
	}
	
	@Test
	public void setTroopTest(){
		 Territory territory= new Land("test",0,0,0);
		 Family family = new Family(1, null);
		 assertNull(territory.getTroup());
		 territory.setTroup(new GroundForce(family,1,0,0));
		 assertNotNull(territory.getTroup());
		 territory.setTroup(null);
		 assertNull(territory.getTroup());
	}
	
	@Test
	public void port(){
		 BoardModel board = new BoardModel();
		 Family family = new Family(1, null);
		 Territory land= board.getTerritory("Winterfell");
		 Territory port= board.getTerritory("Winterfell's Port");
		 assertNull(port.getFamily());
		 land.setTroup(new GroundForce(family,1,0,0));
		 assertEquals(port.getFamily(),family);
	}
	
	
	
	@Test
	public void portRecrutment(){
		 BoardModel board = new BoardModel();
		 Family family = new Family(1, null);
		 Territory terr1= board.getTerritory("Moat Cailin");
		 Territory terr2= board.getTerritory("White Harbor");
		 Water mer1= (Water) board.getTerritory("Narrow Sea");
		 Water mer2= (Water) board.getTerritory("Shivering Sea");
		// here are the tests to see if a territory can recruit a ship somewere
		 assertFalse(terr1.canRecruitShip());
		 assertFalse(terr2.canRecruitShip());
		 terr1.setTroup(new GroundForce(family,1,0,0));
		 assertFalse(terr1.canRecruitShip());
		 terr2.setTroup(new GroundForce(family,1,0,0));
		 assertTrue(terr1.canRecruitShip());
		 assertTrue(terr2.canRecruitShip());
		// tests to see if the player can recruit on this territory from the given territory
		 assertTrue(mer1.canRecruitShipHere(family, terr1 ));
		 assertTrue(mer1.canRecruitShipHere(family, terr2));
		 assertFalse(mer2.canRecruitShipHere(family, terr2));
		 mer1.setTroup(new NavalTroup(family, 1));
		 assertTrue(mer1.canRecruitShipHere(family, terr2));
		 mer1.setTroup(new NavalTroup(new Family(2, null), 1));
		 assertFalse(mer1.canRecruitShipHere(family, terr2));
	}
	
}
