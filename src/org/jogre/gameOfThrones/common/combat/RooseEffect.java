package org.jogre.gameOfThrones.common.combat;

import org.jogre.gameOfThrones.common.CombatantCard;

public class RooseEffect extends HouseCardEffect {

	public RooseEffect(BattlePvP battle, boolean defender) {
		super(battle, defender);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void execute(int value) {
		// TODO Auto-generated method stub

	}

	@Override
	public int display(int player) {
		// TODO Auto-generated method stub
		return 0;
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

}
