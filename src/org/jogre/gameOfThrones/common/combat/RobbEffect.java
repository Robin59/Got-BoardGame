package org.jogre.gameOfThrones.common.combat;

import java.util.List;

import org.jogre.gameOfThrones.common.territory.Land;
import org.jogre.gameOfThrones.common.territory.Territory;
import org.jogre.gameOfThrones.common.territory.Water;

public class RobbEffect extends HouseCardEffect {
	
	public RobbEffect(BattlePvP battle, boolean defender) {
		super(battle, defender);
		if(defender){
			//suppress the siege machines of the battle looser
			battle.getAttTroops()[3]=0;
			// if there is no more troops, don't do anything
			if(battle.getAttTroops()[0]>0 || battle.getAttTroops()[1]>0 || battle.getAttTroops()[2]>0){
				battle.setState(Battle.BATTLE_CARD_EFFECT_END_BATTLE);
			}else finish=true;
		}else{
			//suppress the siege machines of the battle looser
			battle.getDefTerritory().getTroup().getTroops()[3]=0;
			// if there is no place to withdraw or no more troops, don't do anything
			if(battle.getDefTerritory().canWithdraw() || battle.getDefTerritory().getTroup().getEffectif()>0){
				battle.setState(Battle.BATTLE_CARD_EFFECT_END_BATTLE);
			}else finish=true;
		}
		
	}

	@Override
	public void execute(Territory territory) {
		if(defender){
			if(attCanWithdraw(territory)){
				if(territory.getTroup()!=null){
					territory.getTroup().addTroop(battle.attTroops);
				}else{
					if(territory instanceof Water){
						territory.setTroup(new NavalTroup(oppFamily, battle.getAttTroops()[0]));
					}else{
						territory.setTroup(new GroundForce(oppFamily, battle.getAttTroops()[1],battle.getAttTroops()[2],0));
					}
				}
				for(int i=0;i<3;i++) battle.getAttTroops()[i]=0;
				finish=true;
			}
		}else{
			if(battle.defTerritory.canWithdraw(territory)){
				battle.getDefTerritory().mouveTroops(territory);
				battle.getDefTerritory().setTroup(new NavalTroup(oppFamily, 0));
				finish=true;
			}
		}
	}

	@Override
	public void execute(int value) {}

	@Override
	public void autoExecute() {}

	@Override
	public int display(int player) {
		return 0;
	}

	private boolean attCanWithdraw(Territory territory){
		if(battle.defTerritory.getNeighbors().contains(territory)){
			if(battle.defTerritory instanceof Water){
				return (territory instanceof Water) && (territory.getFamily()==null || territory.getFamily()==oppFamily);
			}else{
				return (territory instanceof Land) && (territory.getFamily()==null || territory.getFamily()==oppFamily);
			}
		}else{
			return false;//or naval withdraw
		}
	}
	
	
	
	
}
