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
		//check if the territory is own by the good player, if there is a footman and if the footman participated to the battle
		if(territory.getFamily()!=null && territory.getFamily()==playerFamily &&
				territory.getTroup()!=null && territory.getTroup().getTroops()[1]>0 && inBattle(territory)){
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
	
	/*Return if the territory took part of the battle (in the player camp)*/
	private boolean inBattle(Territory territory){
		if(defender){
			if(territory==battle.getDefTerritory()) return true;
			for(Territory t : battle.defSupport){
				if(territory==t) return true;
			}
		}else{
			if(territory==battle.getAttTerritory()) return true;
			for(Territory t : battle.attSupport){
				if(territory==t) return true;
			}
		}
		return false;
	}

	@Override
	public void autoExecute() {}


}
