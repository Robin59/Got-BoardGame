package org.jogre.gameOfThrones.common.combat;

import org.jogre.gameOfThrones.common.Family;
import org.jogre.gameOfThrones.common.territory.Territory;

public abstract class HouseCardEffect {

		protected boolean finish;//true when the effect off the card has been resolved
		protected boolean defender;//true if this card has been played by the defender
		protected BattlePvP battle;
		protected Family playerFamily;//the family who played the card
		protected Family oppFamily;// his opponent in the battle
		
		public HouseCardEffect(BattlePvP battle, boolean defender){
			this.defender=defender;
			this.battle=battle;
			finish=false;
			if(defender){
				playerFamily=battle.getDefFamily();
				oppFamily=battle.getAttFamily();
			}else{
				playerFamily=battle.getAttFamily();
				oppFamily=battle.getDefFamily();
			}
		}
		
		
		public boolean getFinish(){
			return finish;
		}
		

		/**this method is call when a player on a board for resolving this effect*/
		public abstract void execute(Territory territory);
		/**this method is call when a player on his playerChoice for resolving this effect*/
		public abstract void execute(int value);
		/**this method is for automatic effects execution, that doesn't need the player to do anything*/
		public abstract void autoExecute(); 
		/**
		 * tell if a player must display something new to resolve the card
		 * @param player the number of the player who is checking this method 
		 * @return a PlayerChoice Display state
		 */
		public abstract int display(int player);
}
