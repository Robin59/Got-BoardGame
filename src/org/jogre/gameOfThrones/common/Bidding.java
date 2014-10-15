package org.jogre.gameOfThrones.common;

public class Bidding {
	private Family[] families;
	private int numberPlayers;
	private Family[] track;
	/*only use for the sending property, indicate which case of the track we need to change*/
	private int index;
	
	public Bidding(Family[] families){
		this.families=families;
		numberPlayers=families.length;
		track= new Family[numberPlayers];
		index=0;
	}
	
	/**
	 * Sort the biddings and said if there is some equality
	 * @return true if the bidding is resolve (no equality)*/
	public boolean biddingResolution(){
		biddingSort();
		return !equality();
	}
	
	/**sort the families in function of there bids*/
	private  void biddingSort(){ 
		for(int i=0; i<numberPlayers;i++){
			track[i]=families[i];
		}
		for (int i=0;i<numberPlayers;i++){
			int z=i;
			for(int y=i;y<numberPlayers;y++){
				if(track[z].getBid()<track[y].getBid()){
					z=y;
				}
			}	
			Family family = track[z];
			track[z]=track[i];
			track[i]=family;
		}
	}
	/**
	 * @return true if some families have made the same bid
	 */
	private boolean equality(){
		for(int i=0; i<track.length-1;i++){
			if(track[i].getBid()==track[i+1].getBid()){
				
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @return true if all players have make their binds
	 */
	public boolean allBidsDone(){
		for(Family family: families){
			if(family.getBid()==-1){
				return false;
			}
		}
		return true;
	}

	public Family[] getTrack(){
		return track;
	}

	public void interchange(int a, int b) {
		System.out.println("Interchange");
		Family temp=track[a];
		track[a]=track[b];
		track[b]=temp;
	}

	public void nextFamily(int value) {
		track[index]=families[value];
		index++;
	}
}
