package org.jogre.gameOfThrones.common.territory;

import org.jogre.gameOfThrones.common.Family;
import org.jogre.gameOfThrones.common.combat.GroundForce;
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
		 assertFalse(terr1.canRecruitShip());
		 assertFalse(terr2.canRecruitShip());
		 terr1.setTroup(new GroundForce(family,1,0,0));
		 assertFalse(terr1.canRecruitShip());
		 terr2.setTroup(new GroundForce(family,1,0,0));
		 assertTrue(terr1.canRecruitShip());
		 assertTrue(terr2.canRecruitShip());
	}
	
}
