package wildlingsResolution;

import graphisme.PlayersChoices;

import org.jogre.gameOfThrones.common.Family;
import org.jogre.gameOfThrones.common.GameOfThronesModel;
import org.jogre.gameOfThrones.common.territory.Territory;

public class KingBeyondWall extends WildlingsResolution {
	private int i;
	
	public KingBeyondWall(boolean victory, Family family,
			GameOfThronesModel model, PlayersChoices plChoice, Family[] track) {
		super(victory, family, model, plChoice,track);
		if(victory){
			if(plChoice.getFamily()==family){
				plChoice.setPanel(PlayersChoices.DISPLAY_TRACKS, this);
			}
		}else{
			i=0;
			if(plChoice.getFamily()==track[i]){
				plChoice.setPanel(PlayersChoices.DISPLAY_TRACKS, this);
			}
		}
	}

	@Override
	public String Description() {
		if(victory){
			return "can put his token on the highest place of one track";
		}else return "put his tokens in last place on all 3 track, other players have to choose on which track they're gonna be on last track";
	}

	@Override
	public int actionOnBoard(Territory territory, int player) {
		//do nothing
		return 0;
	}

	@Override
	public void actionOnPChoice(int choice, int player) {
		if(victory){
			switch(choice){
			case PlayersChoices.CHOOSE_THRONE :
				putInFirstPlace(model.getThrone(), player);
				end();
				break;
			case PlayersChoices.CHOOSE_BLADE :
				putInFirstPlace(model.getFiefdoms(), player);
				updateFiefdom();
				end();
				break;
			case PlayersChoices.CHOOSE_RAVEN:
				putInFirstPlace(model.getCourt(), player);
				end();
				break;
			}
		}else{
			switch(choice){
			case PlayersChoices.CHOOSE_THRONE :
				putInLastPlace(model.getThrone(), player);
				i++;
				break;
			case PlayersChoices.CHOOSE_BLADE :
				putInLastPlace(model.getFiefdoms(), player);
				updateFiefdom();
				i++;
				break;
			case PlayersChoices.CHOOSE_RAVEN :
				putInLastPlace(model.getCourt(), player);
				i++;
				break;
			}
			checkFinish();
		}
	
	}

	//put a player in last place of an influence track
	private void putInFirstPlace(int[] infTrack, int player){
		//search the actual position of the player on the track
		int position =0;
		while(infTrack[position]!=player) position++;
		//we transfer the positions
			while(position>0){
					infTrack[position]=infTrack[position-1];
					position--;
			}
			infTrack[position]=player;
	}
	
	//put a player in last place of an influence track
	private void putInLastPlace(int[] infTrack, int player){
		//search the actual position of the player on the track
		int position =0;
		while(infTrack[position]!=player) position++;
		//we transfer the positions
		while(position<infTrack.length-1){
			infTrack[position]=infTrack[position+1];
			position++;
		}
		infTrack[position]=player;
	}
	
	//this method update the fiefdom place in the family objects 
	private void updateFiefdom(){
		for(int i=0;i<model.getNumberPlayers(); i++){
			model.getFamily(model.getFiefdoms()[i]).setFiefdomsTrack(i+1);
		}
	}
	
	private void checkFinish(){
		if(i>=track.length) end();
		else if(plChoice.getFamily()==track[i]){
			plChoice.setPanel(PlayersChoices.DISPLAY_TRACKS, this);
		}
	}
}
