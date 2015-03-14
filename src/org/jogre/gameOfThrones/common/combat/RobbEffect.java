package org.jogre.gameOfThrones.common.combat;

import org.jogre.gameOfThrones.common.territory.Territory;
import org.jogre.gameOfThrones.common.territory.Water;

public class RobbEffect extends HouseCardEffect {
	
	public RobbEffect(BattlePvP battle, boolean defender) {
		super(battle, defender);
		if(defender){
			//suppress the siege machines of the battle looser
			battle.getAttTroops()[3]=0;
			// if there is no place to withdraw or no more troops, don't do anything
			//...
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
		if(battle.defTerritory.canWithdraw(territory)){
			battle.getDefTerritory().mouveTroops(territory);
			battle.getDefTerritory().setTroup(new NavalTroup(oppFamily, 0));
			finish=true;
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

	
}
