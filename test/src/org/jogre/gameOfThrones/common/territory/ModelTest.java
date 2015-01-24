package org.jogre.gameOfThrones.common.territory;

import javax.swing.JLabel;

import org.jogre.gameOfThrones.common.GameOfThronesModel;
import org.jogre.gameOfThrones.common.combat.GroundForce;
import org.junit.Test;
import static org.junit.Assert.*;

public class ModelTest {
	
	
	@Test
	 public void constructor() {
		GameOfThronesModel model = new GameOfThronesModel(3, new JLabel());
		assertNotNull(model);
	}
	
	@Test
	public void getFamilyTest(){
		GameOfThronesModel model = new GameOfThronesModel(3, new JLabel());
		assertNotNull(model.getFamily(0));
		assertTrue(model.getFamily(2).getName()=="Stark");
		//
		assertNotNull(model.getFamily("Stark"));
		assertNull(model.getFamily("Martell"));
	}
	
	@Test
	public void supplyUdateTest(){ // Test if the supply update work correctly
		GameOfThronesModel model = new GameOfThronesModel(3, new JLabel());
		assertTrue(model.getFamily(2).getSupply()==1);
		assertTrue(model.getFamily(1).getSupply()==2);
		model.supplyUpdate();
		assertTrue(model.getFamily(2).getSupply()==1);
		assertTrue(model.getFamily(1).getSupply()==2);
		model.getBoardModel().getTerritory("Widow's Watch").setTroup(new GroundForce(model.getFamily(2),1,0,0));
		assertTrue(model.getFamily(2).getSupply()==1);
		assertTrue(model.getFamily(1).getSupply()==2);
		model.supplyUpdate();
		assertTrue(model.getFamily(2).getSupply()==2);
		assertTrue(model.getFamily(1).getSupply()==2);
	}
	
	@Test
	public void basicMusteringTest(){
		GameOfThronesModel model = new GameOfThronesModel(3, new JLabel());
		assertFalse(model.canRecruit(model.getBoardModel().getTerritory("Lannisport"), 1));
		model.westerosCardMustering();
		assertTrue(model.canRecruit(model.getBoardModel().getTerritory("Lannisport"), 1));
		assertFalse(model.canRecruit(model.getBoardModel().getTerritory("Lannisport"), 2));
	}
	
	@Test
	public void supplyLimitTest(){ // test if the limit supply for troops is working correctly
		GameOfThronesModel model = new GameOfThronesModel(3, new JLabel());
		assertTrue(model.checkSupplyLimits(2, model.getBoardModel().getTerritory("Winterfell")));
		model.getBoardModel().getTerritory("Winterfell").getTroup().addToop(0, 1, 0, 0);
		assertFalse(model.checkSupplyLimits(2, model.getBoardModel().getTerritory("Winterfell")));
		
	}
}
