package org.jogre.gameOfThrones.common.combat;

import org.jogre.gameOfThrones.common.territory.Territory;

public class CerseiEffect extends HouseCardEffect {

	public CerseiEffect(BattlePvP battle, boolean defender) {
		super(battle, defender);
		if(oppFamily.haveOrderOnBoard()){
			battle.setState(Battle.BATTLE_CARD_EFFECT_END_BATTLE);
		}else finish=true;
	}

	@Override
	public void execute(Territory territory) {
		if(territory.getOrder()!=null && territory.getFamily()==oppFamily){
			territory.rmOrder();
			finish=true;
		}
	}

	@Override
	public void execute(int value) {
		// TODO Auto-generated method stub

	}
	@Override
	public void autoExecute() {
		// TODO Auto-generated method stub

	}

	@Override
	public int display(int player) {
		// TODO Auto-generated method stub
		return 0;
	}

}
