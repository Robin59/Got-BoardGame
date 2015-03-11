package org.jogre.gameOfThrones.common.combat;

import org.jogre.gameOfThrones.common.CombatantCard;
import org.jogre.gameOfThrones.common.territory.Territory;


public class TyrionEffect extends HouseCardEffect {

	private CombatantCard bannedCard;
	public TyrionEffect(BattlePvP battle, boolean defender) {
		super(battle,defender);
		if(defender){
			bannedCard=battle.getAttCard();
			battle.setAttCard(null);
		}else{
			bannedCard=battle.getDefCard();
			battle.setDefCard(null);
		}
		if(!opponentHaveCard()){
			finish=true;
		}
	}

	
	/*Return true if the opponent still have one card in his hand*/
	private boolean opponentHaveCard(){
		if(defender){
			return battle.getAttFamily().getCombatantCards().size()>1;
		}else{
			return battle.getDefFamily().getCombatantCards().size()>1;
		}
	}


	@Override
	public boolean getFinish(){
		if(defender){
			if(battle.getAttCard()!=bannedCard)return true;
			else{
				battle.setAttCard(null);
				return false;
			}
		}else{
			if(battle.getDefCard()!=bannedCard) return true;
			else{
				battle.setDefCard(null);
				return false;
			}
		}
	}
	
	@Override
	public void execute(int value) {
		// TODO Auto-generated method stub
	}


	@Override
	public int display(int player) {
		// This method must be implented
		return 0;
	}


	@Override
	public void autoExecute() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void execute(Territory territory) {
		// TODO Auto-generated method stub
		
	}
	
}
