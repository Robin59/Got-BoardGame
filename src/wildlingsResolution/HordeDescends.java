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
	
	public HordeDescends(boolean victory, Family family,GameOfThronesModel model,PlayersChoices plChoice,Family[] track) {
		super(victory, family, model,plChoice,track);
		territories= new Territory[track.length];
		units= new int[track.length];
		if(victory){
			if(haveACastle()) units[0]=32;// a random value >2
			else end();
		}else{
			for(int i=0;  i< track.length; i++){
				units[i]=1;
			}
			units[track.length-1]++;
		}
		
	}

	@Override
	public String Description() {
		if(victory){
			return family.getName()+"can muster in one castle or stronghold";
		}else{
			String string="";
			for(int i=0;  i< track.length; i++){
				string+=", "+track[i].getName()+" have to remove "+units[i]+" troops";
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
						territories[getPlaceOnTrack(player)]=territory;
						return PlayersChoices.DISPLAY_TROOP_DESTRUCTION;
					}
				}else{
					territories[getPlaceOnTrack(player)]=territory;
					return PlayersChoices.DISPLAY_TROOP_DESTRUCTION;
				}
			}else if(territory.getFamily().getPlayer()==player && territory instanceof Land && territory.getTroup()!=null){
				territories[getPlaceOnTrack(player)]=territory;
				return PlayersChoices.DISPLAY_TROOP_DESTRUCTION;
			}
		}else{
			if(player==family.getPlayer() && territory.getCastle()>0 &&
				model.checkSupplyLimits(player, territory)	&& units[0]>2){
				territories[0]=territory;
				territory.resetRecruit();
				units[0]=territory.getRecruit();
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
		return territories[getPlaceOnTrack(family.getPlayer())];
	}

	@Override
	public void actionOnPChoice(int choice, int player) {
		switch(choice){
			case PlayersChoices.REMOVE_FOOT :
				territories[getPlaceOnTrack(player)].getTroup().rmToop(0, 1, 0, 0);
				units[getPlaceOnTrack(player)]--;
				break;
			case PlayersChoices.REMOVE_KNIGHT :
				territories[getPlaceOnTrack(player)].getTroup().rmToop(0, 0, 1, 0);
				units[getPlaceOnTrack(player)]--;
				break;
			case PlayersChoices.REMOVE_SIEGE :
				territories[getPlaceOnTrack(player)].getTroup().rmToop(0, 0, 0, 1);
				units[getPlaceOnTrack(player)]--;
				break;
			case PlayersChoices.RECRUIT_FOOT :
				territories[0].recruit(1);
				units[0]=territories[0].getRecruit();
				break;
			case PlayersChoices.RECRUIT_KNIGHT :
				territories[0].recruit(2);
				units[0]=territories[0].getRecruit();
			case PlayersChoices.RECRUIT_SEIGE :
				territories[0].recruit(2);
				units[0]=territories[0].getRecruit();
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
	
	/**
	 * Convert the player seat number on is place on the bidding track
	 * @param seatPlace the player seat number (also = to Family.getPlayer() )
	 * @return the player position on the bid track (-1 if the player is not in the track) 
	 */
	private int getPlaceOnTrack(int seatPlace){
		for(int res=0; res< track.length; res++ ){
			if(track[res].getPlayer()==seatPlace){
				return res;
			}
		}
		return -1;
	}
}
