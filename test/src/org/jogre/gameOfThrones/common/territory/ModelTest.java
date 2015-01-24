package org.jogre.gameOfThrones.common.territory;

import javax.swing.JLabel;

import org.jogre.gameOfThrones.common.GameOfThronesModel;
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
}
