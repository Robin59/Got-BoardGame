package org.jogre.gameOfThrones.common.combat;

import org.jogre.gameOfThrones.common.Family;
import org.jogre.gameOfThrones.common.GameOfThronesModel;
import org.jogre.gameOfThrones.common.orders.OrderType;
import org.jogre.gameOfThrones.common.territory.Territory;
import org.jogre.gameOfThrones.common.territory.Water;

public class BattlePvE extends Battle{
	
	public BattlePvE(Territory attTerritory, Territory defTerritory) {
		super(attTerritory, defTerritory);
	}

	
	@Override
	public int defPower(){
		int res= defTerritory.getNeutralForce();
		for(Territory territory : defSupport){
			int[] troops =territory.getTroup().getTroops();
			res+=territory.getOrder().getOthBonus()+troops[0]+troops[1]+troops[2]*2;
		}
		return res;
	}


	@Override
	public void startBattle() {
		if(attPower()<defPower()){
			if(attTerritory.getTroup()!=null){
				attTroops[3]=0;
				attTerritory.getTroup().addTroop(attTroops);
			}else{
				attTerritory.setTroup(new GroundForce(attFamily, attTroops[1],attTroops[2],0));
			}
		}else{
			defTerritory.setNeutralForce(0);
			defTerritory.setTroup(new GroundForce(attFamily, attTroops[1],attTroops[2],attTroops[3]));
		}
		endBattle();
	}





	@Override
	public Family getDefFamily() {return null;}
	@Override
	public boolean canPlayCard(Family family) {return false;}
	
	
	
	///////old class
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
