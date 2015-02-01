package org.jogre.gameOfThrones.common.Battle;
import org.jogre.gameOfThrones.common.Family;
import org.jogre.gameOfThrones.common.combat.*;
import org.jogre.gameOfThrones.common.orders.Order;
import org.jogre.gameOfThrones.common.orders.OrderType;
import org.jogre.gameOfThrones.common.territory.HomeLand;
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
	
	@Test
	 public void initialForceCalulTestPvP() {
		Family attFamily = new Family(1, null);
		Family defFamily = new Family(2, null);
		//contruction territory 
		Territory attTerritory= new Land("test",0,0,0);
		Territory defTerritory= new HomeLand("test",0,0,"");
		attTerritory.setTroup(new GroundForce(attFamily,1,0,1));
		attTerritory.setOrder(new Order(false,0,0,OrderType.ATT));
		defTerritory.setTroup(new GroundForce(defFamily,1,0,1));
		Battle battle=new BattlePvP(attTerritory,defTerritory);
		//test defender force
		assertEquals(battle.defPower(),3);
		defTerritory.setOrder(new Order(false,0,0,OrderType.CON));
		assertEquals(battle.defPower(),3);
		defTerritory.setOrder(new Order(true,2,0,OrderType.DEF));
		assertEquals(battle.defPower(),5);
		defTerritory.setTroup(new GroundForce(defFamily,1,1,1));
		assertEquals(battle.defPower(),7);
		//without garisson
		defTerritory.destructGarrison();
		assertEquals(battle.defPower(),5);
		//att force
		battle.addTroop(0, 1, 0, 0);
		assertEquals(battle.attPower(),1);
		battle.addTroop(0, 0, 0, 1);
		assertEquals(battle.attPower(),5);
	};
	
	@Test
	public void initialForceCalulTestPvE() {
		Family attFamily = new Family(1, null);
		Territory attTerritory= new Land("test",0,0,0);
		Territory neutralTerritory= new Land("test",0,0,0);
		attTerritory.setTroup(new GroundForce(attFamily,1,0,0));
		attTerritory.setOrder(new Order(true, 0, 1, OrderType.ATT));
		neutralTerritory.setNeutralForce(3);
		Battle battlePvE=new BattlePvE(attTerritory,neutralTerritory);
		//support 
		Territory supTerritory= new Land("test",0,0,0);
		supTerritory.setTroup(new GroundForce(attFamily,1,0,0));
		supTerritory.setOrder(new Order(true, 0, 1, OrderType.SUP));
		//start of the tests, attacker
		battlePvE.addTroop(0, 1, 0, 0);
		assertEquals(battlePvE.attPower(),2);
		battlePvE.addTroop(0, 0, 1, 0);
		assertEquals(battlePvE.attPower(),4);
		battlePvE.addTroop(0, 0, 0, 1);
		assertEquals(battlePvE.attPower(),4);
		// with support
		battlePvE.addAttSupport(supTerritory);
		assertEquals(battlePvE.attPower(),6);
		//defender test
		assertEquals(battlePvE.defPower(),3);
		battlePvE.addDefSupport(supTerritory);
		assertEquals(battlePvE.defPower(),5);
	};
	
	@Test
	public void PveTest(){
		Family attFamily = new Family(1, null);
		Territory attTerritory= new Land("test",0,0,0);
		Territory neutralTerritory= new Land("test",0,0,0);
		attTerritory.setTroup(new GroundForce(attFamily,1,1,1));
		attTerritory.setOrder(new Order(true, 0, 1, OrderType.ATT));
		neutralTerritory.setNeutralForce(3);
		Battle battlePvE=new BattlePvE(attTerritory,neutralTerritory);
		battlePvE.addTroop(0, 1, 0, 1);
		// defeat test
		battlePvE.startBattle();
		assertEquals(battlePvE.getState(),Battle.BATTLE_END);
		assertEquals(attTerritory.getTroup().getTroops()[1],1);
		assertEquals(attTerritory.getTroup().getTroops()[2],1);
		assertEquals(attTerritory.getTroup().getTroops()[3],0);
		assertEquals(neutralTerritory.getNeutralForce(),3);
		//victory test
		Battle battle2=new BattlePvE(attTerritory,neutralTerritory);
		battle2.addTroop(0, 0, 1, 0);
		battle2.startBattle();
		assertTrue(attTerritory.getOrder().getUse());
		assertEquals(battlePvE.getState(),Battle.BATTLE_END);
		assertEquals(attTerritory.getTroup().getTroops()[1],1);
		assertEquals(attTerritory.getTroup().getTroops()[2],0);
		assertEquals(attTerritory.getTroup().getTroops()[3],0);
		assertEquals(neutralTerritory.getNeutralForce(),0);
		assertEquals(neutralTerritory.getTroup().getTroops()[2],1);
		assertNotNull(neutralTerritory.getFamily());
	}
	
	 
	@Test
	public void supportTest(){
		Family attFamily = new Family(1, null);
		Territory attTerritory= new Land("test",0,0,0);
		Territory neutralTerritory= new Land("test",0,0,0);
		attTerritory.setTroup(new GroundForce(attFamily,1,0,0));
		attTerritory.setOrder(new Order(true, 0, 1, OrderType.ATT));
		neutralTerritory.setNeutralForce(4);
		Battle battlePvE=new BattlePvE(attTerritory,neutralTerritory);
		battlePvE.addTroop(0, 1, 0, 0);
		//support 
		Territory supTerritory= new Land("test",0,0,0);
		neutralTerritory.addTerritory(supTerritory);
		supTerritory.setTroup(new GroundForce(attFamily,1,0,0));
		supTerritory.setOrder(new Order(false, 0, 0, OrderType.SUP));
		battlePvE.addAttSupport(supTerritory);
		assertTrue(supTerritory.getOrder().getUse());
		battlePvE.startBattle();
		assertFalse(supTerritory.getOrder().getUse());
	
	}
	
	
}
