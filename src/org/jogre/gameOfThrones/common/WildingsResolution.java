package org.jogre.gameOfThrones.common;
/**
 * This class is use for the resolution of wildings battle, it's call after the bidding and the sort by throne's owner 
 * it's use just for the effects that needs actions of players 
 * @author robin
 *
 */
public abstract class WildingsResolution {
	
	/**
	 * 
	 * @param victory true if the players wins against the wildings, false in the other case
	 * @param family if victory is true, family is the biggest bid family, if it's false it's the family with the smallest bid
	 */
	public WildingsResolution (boolean victory,Family family ){
		
	}
}
