package org.jogre.gameOfThrones.common.combat;

import org.jogre.gameOfThrones.common.CombatantCard;

import graphisme.PlayersChoices;

public class PatchfaceEffect extends HouseCardEffect {

	public PatchfaceEffect(BattlePvP battle, boolean defender) {
		super(battle, defender);
		battle.setState(Battle.BATTLE_CARD_EFFECT_END_BATTLE);
	}

	@Override
	public void execute(int indexCard) {
		if(defender){
			battle.getAttFamily().removeCard(indexCard);
		}else{
			battle.getDefFamily().removeCard(indexCard);
		}
		finish=true;
	}

	@Override
	public int display(int player) {
		if((defender && battle.getDefFamily().getPlayer()==player)
				||(!defender && battle.getAttFamily().getPlayer()==player)){
			return PlayersChoices.DISPLAY_OPONANT_CARDS;
		}else
			return 0;
	}

}