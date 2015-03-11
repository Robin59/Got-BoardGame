package org.jogre.gameOfThrones.common.combat;

import org.jogre.gameOfThrones.common.territory.Territory;

public class RooseEffect extends HouseCardEffect {

	public RooseEffect(BattlePvP battle, boolean defender) {
		super(battle, defender);
	}

	@Override
	public void autoExecute() {
		if(defender){
			battle.getDefFamily().regainCombatantCards();
		}else{
			battle.getAttFamily().regainCombatantCards();
		}
		this.finish=true;
	}

	@Override
	public void execute(Territory territory) {
		// unused 
	}
	@Override
	public void execute(int value) {
		// unused 
	}
	@Override
	public int display(int player) {
		//not use
		return 0;
	}



}
