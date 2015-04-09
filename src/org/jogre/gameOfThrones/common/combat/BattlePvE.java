package org.jogre.gameOfThrones.common.combat;

import org.jogre.gameOfThrones.common.Family;
import org.jogre.gameOfThrones.common.GameOfThronesModel;
import org.jogre.gameOfThrones.common.orders.Order;
import org.jogre.gameOfThrones.common.territory.Territory;

public class BattlePvE extends Battle{
	
	public BattlePvE(Territory attTerritory, Territory defTerritory,GameOfThronesModel model,Order attOrder) {
		super(attTerritory, defTerritory,model,attOrder);
	}


	@Override
	public void nextPhase() {
		// TODO Auto-generated method stub		
	}

	
	@Override
	public int defPower(){
		int res= defTerritory.getNeutralForce();
		for(Territory territory : defSupport){
			res+=territory.getOrder().getOthBonus()+territory.getTroup().getDefPower();
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
		if(attPower()>=defPower()){
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
	
	





	
}
