package wildlingsResolution;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import graphisme.PlayersChoices;

import org.jogre.gameOfThrones.common.CombatantCard;
import org.jogre.gameOfThrones.common.Family;
import org.jogre.gameOfThrones.common.GameOfThronesModel;
import org.jogre.gameOfThrones.common.territory.Territory;

public class MassingMilkwater extends WildlingsResolution {
	
	private boolean[] haveDiscard;

	public MassingMilkwater(boolean victory, Family family,
			GameOfThronesModel model, PlayersChoices plChoice, Family[] track) {
		super(victory, family, model, plChoice, track);
		if(!victory){
			allCardsLost();
			haveDiscard = new boolean[track.length-1];
			for(int i=0;i<track.length-1;i++){
				if(moreThanOneCard(track[i])){
					if(plChoice.getFamily()==track[i]){ 
						plChoice.setPanel(PlayersChoices.DISPLAY_HOUSE_CARDS, this);}
				}else{
					haveDiscard[i]=true;
				}
			}
			checkFinish();
		}
	}

	@Override
	public String Description() {
		if(!victory) return "loose all this card, exepte the lowest, the other players must discard one card";
		else return null;
	}

	@Override
	public int actionOnBoard(Territory territory, int player) {
		// not use
		return 0;
	}

	@Override
	public void actionOnPChoice(int choice, int player) {
		if(choice>=PlayersChoices.CHOOSE_CARD0 && choice<=PlayersChoices.CHOOSE_CARD7){
			int playerOnTrack = getPlaceOnTrack(player);
			track[playerOnTrack].removeCard(choice-PlayersChoices.CHOOSE_CARD0);
			haveDiscard[playerOnTrack]=true;
		}
		checkFinish();
	}

	//return true if the family have more than one house card
	private boolean moreThanOneCard(Family fam){
		return true;
	}
	
	private void checkFinish(){
		boolean finish=true;
		for(boolean bool :haveDiscard){
			if(!bool){
				finish=false;
			}
		}
		if(finish)end();
	}
	
	//discard all cards except the lowest 
	private void allCardsLost(){
		List<CombatantCard> list = new LinkedList<CombatantCard>(); 
		list.addAll(family.getCombatantCards());
		ListIterator<CombatantCard> listIterator= list.listIterator();
		CombatantCard lowestC=listIterator.next();
		while(listIterator.hasNext()){
			CombatantCard card = listIterator.next();
			if(card.getPower()<lowestC.getPower()){
				family.removeCard(lowestC);
				lowestC=card;
			}else{
				family.removeCard(card);
			}
		}
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
