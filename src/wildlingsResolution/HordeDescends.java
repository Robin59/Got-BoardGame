package wildlingsResolution;

import graphisme.PlayersChoices;
import org.jogre.gameOfThrones.common.Family;
import org.jogre.gameOfThrones.common.GameOfThronesModel;
import org.jogre.gameOfThrones.common.territory.Land;
import org.jogre.gameOfThrones.common.territory.Territory;

public class HordeDescends extends WildlingsResolution {
	
	/*The number of units players have to remove on the board,
	 *ex: numberKnight[0]=1 means that the baratheon have to remove 1 unit*/
	private int[] units;
	private Territory[] territories;
	
	public HordeDescends(boolean victory, Family family,GameOfThronesModel model) {
		super(victory, family, model);
		territories= new Territory[model.getNumberPlayers()];
		units= new int[model.getNumberPlayers()];
		if(victory){
			if(haveACastle()) units[family.getPlayer()]=32;// a random value >2
			else end();
		}else{
			for(int i=0;  i< model.getNumberPlayers(); i++){
				units[i]=1;
			}
			units[family.getPlayer()]++;
		}
		
	}

	@Override
	public String Description() {
		if(victory){
			return family.getName()+"can muster in one castle or stronghold";
		}else{
			String string="";
			for(int i=0;  i< model.getNumberPlayers(); i++){
				string+=", "+model.getFamily(i).getName()+" have to remove "+units[i]+" troops";
			}
			return string;
		}
	}

	@Override
	public int actionOnBoard(Territory territory, int player) {
		if(!victory){
			if(player==family.getPlayer() && territory instanceof Land && territory.getTroup()!=null){
				if(AbleDestroyAtCastle()){
					if(territory.getCastle()>0){
						territories[player]=territory;
						return PlayersChoices.DISPLAY_TROOP_DESTRUCTION;
					}
				}else{
					territories[player]=territory;
					return PlayersChoices.DISPLAY_TROOP_DESTRUCTION;
				}
			}else if(territory.getFamily().getPlayer()==player && territory instanceof Land && territory.getTroup()!=null){
				territories[player]=territory;
				return PlayersChoices.DISPLAY_TROOP_DESTRUCTION;
			}
		}else{
			if(player==family.getPlayer() && territory.getCastle()>0 &&
				model.checkSupplyLimits(player, territory)	&& units[player]>2){
				territories[player]=territory;
				territory.resetRecruit();
				units[player]=territory.getRecruit();
				return PlayersChoices.DISPLAY_RECRUITEMENT;
			}
		}
		return 0;
	}
	
	
	/**
	 * tell if the looser player have a castle or stronghold where he can destroy 2 of his units
	 * @return true if the looser player have a castle or stronghold where he can destroy 2 of his units
	 */
	private boolean AbleDestroyAtCastle(){
		int comp =0;
		for (Territory territory : family.getTerritories()){
			if (territory.getCastle()>0 && territory.getTroup()!=null){
				comp+=territory.getTroup().getEffectif();
			}
		}
		return comp>1;
	}

	/*tell if the winning player have a castel for mustering*/
	private boolean haveACastle(){
		for (Territory territory : family.getTerritories()){
			if(territory.getCastle()>0) return true;
		}
		return false;
	}
	@Override
	public Territory getTerritory(Family family){
		return territories[family.getPlayer()];
	}

	@Override
	public void actionOnPChoice(int choice, int player) {
		switch(choice){
			case PlayersChoices.REMOVE_FOOT :
				territories[player].getTroup().rmToop(0, 1, 0, 0);
				units[player]--;
				break;
			case PlayersChoices.REMOVE_KNIGHT :
				territories[player].getTroup().rmToop(0, 0, 1, 0);
				units[player]--;
				break;
			case PlayersChoices.REMOVE_SIEGE :
				territories[player].getTroup().rmToop(0, 0, 0, 1);
				units[player]--;
				break;
			case PlayersChoices.RECRUIT_FOOT :
				territories[player].recruit(1);
				units[player]=territories[player].getRecruit();
				break;
			case PlayersChoices.RECRUIT_KNIGHT :
				territories[player].recruit(2);
				units[player]=territories[player].getRecruit();
			case PlayersChoices.RECRUIT_SEIGE :
				territories[player].recruit(2);
				units[player]=territories[player].getRecruit();
				break;
		}
		
		checkFinish();
	}
	
	
	/*Check if the players still have to do some actions for the resolution*/
	private void checkFinish(){
		boolean finish=true;
		for(int i :units){
			if (i>0) finish=false;
		}
		if(finish) this.end();
	}
}
