package org.jogre.gameOfThrones.common.combat;

import graphisme.PlayersChoices;

import org.jogre.gameOfThrones.common.territory.Territory;

public class RenlyEffect extends HouseCardEffect {

	public RenlyEffect(BattlePvP battle, boolean defender) {
		super(battle, defender);
		battle.setState(Battle.BATTLE_CARD_EFFECT_END_BATTLE);
	}

	@Override 
	public void execute(Territory territory) {
		//check if the territory is own by the good player, if there is a footman 
		if(territory.getFamily()!=null && territory.getFamily()==playerFamily &&
				territory.getTroup()!=null && territory.getTroup().getTroops()[1]>0){
			//then change a footman in knight
			territory.getTroup().addToop(0, 0, 1, 0);
			territory.getTroup().rmToop(0, 1, 0, 0);
			finish=true;
		}
		
		
	}

	@Override
	public void execute(int value) {
		// the player choose to cancel
		if(value==PlayersChoices.CANCEL)
			finish=true;
	}

	@Override
	public int display(int player) {
		if(playerFamily.getPlayer()==player){
			return PlayersChoices.DISPLAY_CANCEL;
		}else return 0;
	}
	

	@Override
	public void autoExecute() {}


}
