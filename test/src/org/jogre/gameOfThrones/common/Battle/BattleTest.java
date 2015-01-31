package org.jogre.gameOfThrones.common.Battle;
import org.jogre.gameOfThrones.common.Family;
import org.jogre.gameOfThrones.common.combat.*;
import org.jogre.gameOfThrones.common.territory.Land;
import org.jogre.gameOfThrones.common.territory.Territory;
import org.junit.Test;
import static org.junit.Assert.*;


public class BattleTest {
	@Test
	 public void constructor() {
		//family construction
		Family attFamily = new Family(1, null);
		Family defFamily = new Family(2, null);
		//contruction territory 
		Territory attTerritory= new Land("test",0,0,0);
		Territory defTerritory= new Land("test",0,0,0);
		Territory neutralTerritory= new Land("test",0,0,0);
		attTerritory.setTroup(new GroundForce(attFamily,1,0,0));
		defTerritory.setTroup(new GroundForce(defFamily,1,0,0));
		neutralTerritory.setNeutralForce(1);
		//
		Battle battlePvP=new BattlePvP(attTerritory,defTerritory);
		Battle battlePvE=new BattlePvE(attTerritory,neutralTerritory);
		assertNotNull(battlePvP);
		assertNotNull(battlePvE);
		assertEquals(battlePvP.getAttFamily(),attFamily);
		assertEquals(battlePvE.getState(),Battle.BATTLE_SUPPORT_PHASE);
	}
}
