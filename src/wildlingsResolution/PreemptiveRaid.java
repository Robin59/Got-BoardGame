package wildlingsResolution;

import java.awt.Choice;

import graphisme.PlayersChoices;

import org.jogre.gameOfThrones.common.Family;
import org.jogre.gameOfThrones.common.GameOfThronesModel;
import org.jogre.gameOfThrones.common.territory.Territory;

public class PreemptiveRaid extends WildlingsResolution {
	/*Tell the choice made by the looser player*/
	private boolean chooseA;
	private int unitsToRemove;
	private Territory territory;
	
	public PreemptiveRaid(boolean victory, Family family,GameOfThronesModel model,PlayersChoices playerChoices,Family[]  track) {
		super(victory, family, model,playerChoices,track);
			//in case the players loose
			chooseA=false;
			if(playerChoices.getFamily()==family){
				playerChoices.setPanel(PlayersChoices.DISPLAY_LETTERS_AB, this);
			}
	}

	@Override
	public String Description() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int actionOnBoard(Territory territory, int player) {
		if(chooseA && territory.getTroup()!=null && territory.getFamily()==family){
			this.territory=territory;
			return PlayersChoices.DISPLAY_TROOP_DESTRUCTION;
		}
		return 0;
	}

	@Override
	public void actionOnPChoice(int choice, int player) {
		switch(choice){
		case PlayersChoices.LETTER_A_CHOSE :
			chooseA=true;
			unitsToRemove=2;
			plChoice.blank();
			break;
		case PlayersChoices.LETTER_B_CHOSE :
			reduceHighestTrack();
			plChoice.blank();
			end();
			break;
		case PlayersChoices.REMOVE_SHIP :
			territory.getTroup().rmToop(1, 0, 0, 0);
			unitsToRemove--;
			break;
		case PlayersChoices.REMOVE_FOOT :
			territory.getTroup().rmToop(0, 1, 0, 0);
			unitsToRemove--;
			break;
		case PlayersChoices.REMOVE_KNIGHT :
			territory.getTroup().rmToop(0, 0, 1, 0);
			unitsToRemove--;
			break;
		case PlayersChoices.REMOVE_SIEGE :
			territory.getTroup().rmToop(0, 0, 0, 1);
			unitsToRemove--;
			break;
		}
		checkFinish();
	}
	
	/*Check if the players still have to do some actions for the resolution*/
	private void checkFinish(){
		if(chooseA && unitsToRemove<1){
			end();
		}
	}
	
	@Override
	public Territory getTerritory(Family family){
		return territory;
	}
	
	/*reduce the loosing player from two positions on is highest track*/
	private void reduceHighestTrack(){
		int[] highTrack=null;
		int position=0;
		//calcul the highest track
		while(highTrack==null){
			if(model.getThrone()[position]==family.getPlayer()){
				highTrack=model.getThrone();
			}else if (model.getCourt()[position]==family.getPlayer()){
				highTrack=model.getCourt();
			}else if (model.getFiefdoms()[position]==family.getPlayer()){
				highTrack=model.getFiefdoms();
			}
		}
		//move the player two position below 
		if(position<highTrack.length-1){
			highTrack[position]=highTrack[position+1];
			if(position<highTrack.length-2){
				highTrack[position+1]=highTrack[position+2];
				highTrack[position+2]=family.getPlayer();
			}else highTrack[position+1]=family.getPlayer();
		}
	}

}
