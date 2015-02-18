package wildlingsResolution;

import org.jogre.gameOfThrones.common.Family;
import org.jogre.gameOfThrones.common.GameOfThronesModel;
import org.jogre.gameOfThrones.common.territory.Territory;


public class CrowKillers extends WildlingsResolution {
	/*The number of knight players have to transform on the board, ex: numberKnight[0]=1 means that the baratheon have to transform 1 knight*/
	private int[] numberKnight;
	
	public CrowKillers(boolean victory, Family family,GameOfThronesModel model) {
		super(victory, family, model);
		numberKnight= new int[model.getNumberPlayers()];
		if(victory){
			if(family.knightAvailable())  numberKnight[family.getPlayer()]=2;
		}else{ 
			for(int i=0;  i< model.getNumberPlayers(); i++){
				if(haveKnightOnBoard(model.getFamily(i))) numberKnight[i]=2;
			}
			this.changeAllKnight();
			numberKnight[family.getPlayer()]=0;
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
				numberKnight[player]--;
				if(!family.knightAvailable())  numberKnight[player]=0;
			}
		}else{
			if(territory.getFamily()==model.getFamily(player) && territory.getTroup()!=null && territory.getTroup().getTroops()[2]>0){
				territory.getTroup().addToop(0, 1, 0, 0);
				territory.getTroup().rmToop(0, 0, 1, 0);
				numberKnight[player]--;
				if(!haveKnightOnBoard(model.getFamily(player))) numberKnight[player]=0;
			}
		}
		checkFinish();
		return 0;
	}
	

	@Override
	public String Description() {
		if(victory){
			return " can change "+numberKnight[family.getPlayer()]+" footman in knight";
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
