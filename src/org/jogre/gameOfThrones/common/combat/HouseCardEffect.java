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
		
		public abstract void execute();
}
