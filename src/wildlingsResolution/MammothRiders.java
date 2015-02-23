package wildlingsResolution;

import graphisme.PlayersChoices;

import org.jogre.client.awt.PlayerComponent;
import org.jogre.gameOfThrones.common.Family;
import org.jogre.gameOfThrones.common.GameOfThronesModel;
import org.jogre.gameOfThrones.common.territory.Territory;

public class MammothRiders extends WildlingsResolution {

	/*The number of units players have to remove on the board,
	 *ex: numberKnight[0]=1 means that the baratheon have to remove 1 unit*/
	private int[] units;
	private Territory[] territories;
	private boolean regainCard;
	private PlayersChoices plChoice;
	private Family[] track;
	public MammothRiders(boolean victory, Family family,
			GameOfThronesModel model,Family[] track, PlayersChoices playerChoices) {
		super(victory, family, model);
		territories= new Territory[track.length];
		units= new int[track.length];
		plChoice=playerChoices;
		if(victory && !family.getDiscardCombatantCards().isEmpty()){
			if(playerChoices.getFamily()==family){
				regainCard=false;
				playerChoices.setPanel(PlayersChoices.DISPLAY_DISCARD_HOUSE_CARDS, this);
			}
		}else if(!victory){
			regainCard=true;
			for(int i=0;  i< track.length; i++){
				units[i]=2;
			}
			units[track.length-1]++;
		}else end();
	}

	@Override
	public String Description() {
		if(victory){
			return family.getName()+" can take back a fammily card";
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
		if(!victory && territory.getTroup()!=null){
				territories[getPlaceOnTrack(player)]=territory;
				return PlayersChoices.DISPLAY_TROOP_DESTRUCTION;
		}
		return 0;
	}

	@Override
	public void actionOnPChoice(int choice, int player) {
		switch(choice){
		case PlayersChoices.REMOVE_SHIP :
			territories[getPlaceOnTrack(player)].getTroup().rmToop(1, 0, 0, 0);
			units[getPlaceOnTrack(player)]--;
			break;
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
		case PlayersChoices.HOUSE_CARD_CHOSEN :
			family.regainCard(family.getDiscardCombatantCards().get(plChoice.getIndexCard()));
			regainCard=true;
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
		if(finish && regainCard) this.end();
	}
	
	@Override
	public Territory getTerritory(Family family){
		return territories[getPlaceOnTrack(family.getPlayer())];
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
