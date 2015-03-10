package org.jogre.gameOfThrones.common.combat;

public abstract class HouseCardEffect {

		protected boolean finish;//true when the effect off the card has been resolved
		protected boolean defender;//true if this card has been played by the defender
		protected BattlePvP battle;
		
		public HouseCardEffect(BattlePvP battle, boolean defender){
			this.defender=defender;
			this.battle=battle;
			finish=false;
		}
		
		
		public boolean getFinish(){
			return finish;
		}
		
		/**this method is for effects execution that need a player's decision*/
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
