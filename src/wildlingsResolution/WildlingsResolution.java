package wildlingsResolution;

import org.jogre.gameOfThrones.common.Family;
import org.jogre.gameOfThrones.common.GameOfThronesModel;
import org.jogre.gameOfThrones.common.territory.Territory;


/**
 * This class is use for the resolution of wildlings battle, it's call after the bidding and the sort by throne's owner 
 * it's use just for the effects that needs actions of players 
 * @author robin
 *
 */
public abstract class WildlingsResolution {
	
	private boolean ended;
	protected boolean victory;
	protected Family family;
	protected GameOfThronesModel model;
	/**
	 * @param victory true if the players wins against the wildlings, false in the other case
	 * @param family if victory is true, family is the biggest bid family, if it's false it's the family with the smallest bid
	 */
	public WildlingsResolution (boolean victory,Family family, GameOfThronesModel model ){
		this.victory=victory;
		this.family=family;
		this.model= model;
		this.ended=false;
	}
	

	
	/**
	 * This method is call when the players have finish to resolve the wildlings card effect 
	 */
	public void end(){
		ended=true;
	}
	/**
	 * Use to known if the wildlings resolutions is done
	 * @return true if the wildlings resolutions is done
	 */
	public boolean getEnded(){
		return ended;
	}
	public String toString(){
		if(victory){
			return "The nigth watch win, the "+family.getName()+Description();
		}else return "The nigth watch loose, the "+family.getName()+Description();
	}
	/**
	 * This method is the description of the wildlings card and what players have to do
	 * @return The description of the wildlings card and what players have to do
	 */
	public abstract String Description();


	/**
	 * This method is call when a player click on the board to resolve the wildlings attack effects
	 * @param territory the territory that was clicked 
	 * @param player the player who clicked
	 */
	public abstract void action(Territory territory, int player);
}
