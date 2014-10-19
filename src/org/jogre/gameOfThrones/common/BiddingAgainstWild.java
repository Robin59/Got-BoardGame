package org.jogre.gameOfThrones.common;

public class BiddingAgainstWild extends Bidding {

	private int wildingForce;
	
	public BiddingAgainstWild(Family[] families, int wildingForce) {
		super(families);
		this.wildingForce=wildingForce;
	}
	
	/**
	 * Said if the wilding force is beat off
	 * @return false if the wildings win 
	 */
	public boolean victory(){
		int totalBids=0;
		for (Family family : families){
			totalBids+=family.getBid();
		}
		return totalBids>=wildingForce;
	}

}
