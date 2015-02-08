package org.jogre.gameOfThrones.common.territory;
import javax.swing.JLabel;

import org.jogre.gameOfThrones.common.Family;
import org.jogre.gameOfThrones.common.GameOfThronesModel;
import org.jogre.gameOfThrones.common.combat.GroundForce;
import org.junit.Test;
import static org.junit.Assert.*;

public class FamilyTest {

	@Test
	 public void constructor() {
		GameOfThronesModel model = new GameOfThronesModel(3, new JLabel());
		Family family = new Family(3,model);
		assertNotNull(family);
		assertNotNull(model.getFamily(0));
		assertNotNull(model.getFamily("Stark"));
	}
	
	@Test
	public void getNumberTroopsTest(){
		GameOfThronesModel model = new GameOfThronesModel(3, new JLabel());
		assertEquals(model.getFamily("Stark").getNumberFootman(),2);
		assertEquals(model.getFamily("Stark").getNumberKnight(),1);
		assertEquals(model.getFamily("Stark").getNumberSiege(),0);
		assertEquals(model.getFamily("Stark").getNumberShip(),1);
		assertEquals(model.getFamily("Baratheon").getNumberShip(),2);
	}
	
	@Test
	public void troopsAvailableTest(){
		GameOfThronesModel model = new GameOfThronesModel(3, new JLabel());
		assertTrue(model.getFamily("Stark").shipAvailable());
		assertTrue(model.getFamily("Stark").footmanAvailable());
		assertTrue(model.getFamily("Stark").knightAvailable());
		assertTrue(model.getFamily("Stark").siegeAvailable());
		model.getBoardModel().getTerritory("Karhold").setTroup(new GroundForce(model.getFamily("Stark"), 0, 1, 2));
		assertTrue(model.getFamily("Stark").knightAvailable());
		assertFalse(model.getFamily("Stark").siegeAvailable());
	}
	
}
