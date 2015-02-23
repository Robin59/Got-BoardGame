package wildlingsResolution;

import org.jogre.gameOfThrones.common.Family;
import org.jogre.gameOfThrones.common.GameOfThronesModel;
import org.jogre.gameOfThrones.common.territory.Territory;


public class CrowKillers extends WildlingsResolution {
	/*The number of knight players have to transform on the board, ex: numberKnight[0]=1 means that the baratheon have to transform 1 knight*/
	private int[] numberKnight;
	private Family[] track;
	public CrowKillers(boolean victory, Family family,GameOfThronesModel model,Family[] track) {
		super(victory, family, model);
		this.track=track;
		numberKnight= new int[track.length];
		if(victory){
			if(family.knightAvailable())  numberKnight[0]=2;
		}else{ 
			for(int i=0;  i< track.length-1; i++){
				if(haveKnightOnBoard(track[i])) numberKnight[i]=2;
			}
			this.changeAllKnight();
		}
		// now we check if there is something to do for the resolution
		checkFinish();
	}

	@Override
	public int actionOnBoard(Territory territory, int player){
		if(victory){
			if(territory.getFamily()==model.getFamily(player) && territory.getTroup()!=null && territory.getTroup().getTroops()[1]>0){
				territory.getTroup().addToop(0, 0, 1, 0);
				territory.getTroup().rmToop(0, 1, 0, 0);
				numberKnight[0]--;
				if(!family.knightAvailable())  numberKnight[0]=0;
			}
		}else{
			if(territory.getFamily()==model.getFamily(player) && territory.getTroup()!=null && territory.getTroup().getTroops()[2]>0){
				territory.getTroup().addToop(0, 1, 0, 0);
				territory.getTroup().rmToop(0, 0, 1, 0);
				numberKnight[getPlaceOnTrack(player)]--;
				if(!haveKnightOnBoard(model.getFamily(player))) numberKnight[getPlaceOnTrack(player)]=0;
			}
		}
		checkFinish();
		return 0;
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
	
	@Override
	public String Description() {
		if(victory){
			return " can change "+numberKnight[0]+" footman in knight";
		}else{
			return " have all this knight change in footman";
		}
	}
	
	/*return true if the family have still some knight on the board*/
	private boolean haveKnightOnBoard(Family family){
		for(Territory territory : family.getTerritories()){
			if(territory.getTroup()!=null && territory.getTroup().getTroops()[2]>0){
				return true;
			}
		}
		return false;
	}
	
	/*transform all the knight of the looser player in footman*/
	private void changeAllKnight(){
		for(Territory territory : family.getTerritories()){
			if(territory.getTroup()!=null){
				int knights =territory.getTroup().getTroops()[2];
				territory.getTroup().getTroops()[2]-=knights;
				territory.getTroup().getTroops()[1]+=knights;
			}
		}
	}
	
	/*Check if the players still have to do some actions for the resolution*/
	private void checkFinish(){
		boolean finish=true;
		for(int i :numberKnight){
			if (i>0) finish=false;
		}
		if(finish) this.end();
	}

	@Override
	public void actionOnPChoice(int choice, int player) {
		// There is no use of this method in this class 
	}
}
