package org.jogre.gameOfThrones.common.combat;

import org.jogre.gameOfThrones.common.Family;
import org.jogre.gameOfThrones.common.GameOfThronesModel;
import org.jogre.gameOfThrones.common.orders.OrderType;
import org.jogre.gameOfThrones.common.territory.Territory;
import org.jogre.gameOfThrones.common.territory.Water;

public class BattlePvE {

	private Territory attTerritory;
	private Territory defTerritory;
	private int[] attTroops;
	private int groundType;
	public BattlePvE(Territory attTerritory, Territory defTerritory) {
		this.attTerritory=attTerritory;
		this.defTerritory=defTerritory;
		attTroops=new int[4];
		if(defTerritory.getCastle()>0){
			groundType=1;
		}else{
			groundType=1;
		}
		
	}

	
	public void addTroop(int boat, int foot, int knight, int siege) {
		attTerritory.getTroup().rmToop(boat, foot, knight, siege);
		attTroops[1]+=foot;
		attTroops[2]+=knight;
		attTroops[3]+=siege;
	}



	public void resolution(GameOfThronesModel model) {
		if(victory()){
			defTerritory.setNeutralForce(0);
			defTerritory.setTroup(new GroundForce(attTerritory.getFamily(),attTroops[1],attTroops[2],attTroops[3]));
			if(attTerritory.getTroup()== null || attTerritory.getTroup().getEffectif()==0){//if there is no more troops the territory does not belong to the player any more
				attTerritory.rmOrder();
				attTerritory.removeOwner();
				model.nextPlayer();
			}
		}else{
			if(attTerritory.getTroup()!=null){
				attTerritory.getTroup().addTroop(attTroops);
			}else{
				attTerritory.setTroup(new GroundForce(attTerritory.getFamily(),attTroops[1],attTroops[2],attTroops[3]));
			}
		}
	}
	
	private boolean victory(){
		int res=attTerritory.getOrder().getOthBonus();
		res+=(attTroops[2])*2+attTroops[1]+attTroops[3]*4*groundType;
		//support
		for(Territory territory : defTerritory.getNeighbors()){
			if(territory.getOrder()!=null && territory.getOrder().getType()==OrderType.SUP && territory.getFamily()==attTerritory.getFamily() ){
				int[] troops =territory.getTroup().getTroops();
				res+=territory.getOrder().getOthBonus()+troops[0]+troops[1]+troops[2]*2+troops[3]*4*groundType;
			}
		}
		return res>=defTerritory.getNeutralForce();
	}
	
}
