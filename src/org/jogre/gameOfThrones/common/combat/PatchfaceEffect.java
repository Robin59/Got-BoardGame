package org.jogre.gameOfThrones.common.combat;

import org.jogre.gameOfThrones.common.territory.Territory;

import graphisme.PlayersChoices;

public class PatchfaceEffect extends HouseCardEffect {

	public PatchfaceEffect(BattlePvP battle, boolean defender) {
		super(battle, defender);
		battle.setState(Battle.BATTLE_CARD_EFFECT_END_BATTLE);
	}

	@Override
	public void execute(int indexCard) {
		if (indexCard>=PlayersChoices.CHOOSE_CARD0){
			oppFamily.removeCard(indexCard-PlayersChoices.CHOOSE_CARD0);
			finish=true;
		}
	}

	@Override
	public int display(int player) {
		if(playerFamily.getPlayer()==player){
			return PlayersChoices.DISPLAY_OPONANT_CARDS;
		}else
			return 0;
	}

	@Override
	public void autoExecute() {
		// no need
	}

	@Override
	public void execute(Territory territory) {}

}
