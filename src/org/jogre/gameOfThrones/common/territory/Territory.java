package org.jogre.gameOfThrones.common.territory;

import java.util.ArrayList;
import java.util.List;

import org.jogre.gameOfThrones.common.Family;
import org.jogre.gameOfThrones.common.combat.GroundForce;
import org.jogre.gameOfThrones.common.combat.NavalTroup;
import org.jogre.gameOfThrones.common.combat.Troop;
import org.jogre.gameOfThrones.common.orders.Order;
import org.jogre.gameOfThrones.common.orders.OrderType;


public abstract class Territory {
	/**Name of the territory*/
	private String name;
	/**the owner of the territory, null if none*/
	private Family owner;  // utile dans le cas ou on a pas de troupes
	protected Troop troop;
	/** the order given in this territory*/
	protected Order order;
	/** to know wich territory you can access from this territory*/
	protected List<Territory> neighbors;
	/** indicate how many troups you can recruit on this territory
	 * 0 is when there is no castle (specialy if it's not a land)*/
	protected int castle;
	
	public Territory(String name){
		this.name=name;
		neighbors=new ArrayList<Territory>();
		castle=0;
	}
	/**This initialisation isn't in the constructor because we need to construct Territory before adding them*/
	public void addTerritory(Territory ter){
		neighbors.add(ter);
	}
	/***/
	public String getName(){
		return name;
	}
	/***/
	public Order getOrder(){
		return order;
	}
	public void setOrder(Order newOrder){ // peut-etre faire des verifications avant d'ajouter un ordre 
		order=newOrder;
	}
	public List<Territory> getNeighbors(){
		return neighbors; // plutôt utiliser un iterateur ?
	}

	public Family getFamily() {
		return owner;
	}
	/*public void setFamily(Family family){//peut-etre supprimer et ajouter à land un la possibiliter d'ajouter un pion d'influence
		owner=family;
	}*/
	public Troop getTroup(){
		return troop;
	}
	
	//AJOUTER automatiquement ce territoire au proprietaire des troupes
	public void setTroup(Troop newTroup){
		//we remove the territory to the old owner if the troups is not its
		if(troop!=null && (newTroup==null || newTroup.getFamily()!=troop.getFamily())){
			troop.getFamily().removeTerritory(this);
		}
		this.troop=newTroup;
		if(newTroup!=null){
			owner=newTroup.getFamily();
			owner.addTerritory(this);
		}else{owner=null;} // verifier que ça ne pose pas de problemes
	}
	
	public int getCastle(){
		return castle;
	}
	
	/**All the information about the territory*/
	public String toString(){
		String res ="This is "+name;
		if(troop!=null){
			int[] troops = troop.getTroops();
			if(troop instanceof GroundForce){
				res+="there is "+troops[1]+" footman, "+troops[2]+" knigth, "+troops[3]+" siege tower";
			}else{
				res+="there is "+troops[0]+" ship";
			}
		}
		return res;
	}
	/*remove an order to this territory  and give it back to is owner*/
	public void rmOrder() {
		owner.regainOrder(order);
		order=null;
	}
	/***/
	public boolean canUseOrderOn(Territory territory){
		boolean res=false;
		if (neighbors.contains(territory)){
			if(order.getType()==OrderType.RAI){
				res=(territory.getOrder()!=null && territory.getOrder().getType()!=OrderType.ATT && (order.getStar() || territory.getOrder().getType()!=OrderType.DEF));
			}else if (!order.getUse()||territory.getTroup()==null || territory.getFamily()==owner){ // attack or move cases
				res=true;
			}
		}
		return res;
	}
	/** 0 for raid, 1 for move, 2 for combats*/
	public int useOrderOn(Territory territory){ // int ou void ???
		if(order.getType()==OrderType.RAI){
			territory.rmOrder();
			this.rmOrder();
			return 0;
		}else{
			if(territory.getTroup()==null || territory.getFamily()==owner){
				return 1;
			}else{
				return 2;	
			}
			
		}
		
	}
	/**
	 * move all troops from this territory to the territory given in parameter
	 * @param toTerritory
	 */
	public void mouveTroops(Territory toTerritory){
		this.mouveTroops(toTerritory,troop.getTroops()[0],troop.getTroops()[1],troop.getTroops()[2],troop.getTroops()[3]);
	}
	/**
	 * Mouve some troops from this territory to the territory given in parameter,
	 * be carfull there is no verification if the number of troops moved is bigger than the troops present
	 * @param toTerritory
	 * @param ship
	 * @param foot
	 * @param knight
	 * @param siege
	 */
	public void mouveTroops(Territory toTerritory, int ship, int foot, int knight, int siege){
		if (toTerritory.getTroup()==null){ //creat a new troop
			if (ship>0){
				toTerritory.setTroup(new NavalTroup(this.owner,ship));
			}else{
				toTerritory.setTroup(new GroundForce(this.owner,foot,knight, siege));
			}
		}else{
			toTerritory.getTroup().addToop(ship, foot, knight, siege);
		}
		this.troop.rmToop(ship, foot, knight, siege);
		if(this.troop.getEffectif()==0){
			this.troop=null;
		}
	}
	
	/**
	 * When there is a battle and the defencer lose, said if there is a territory to withdraw
	 * @return
	 */
	public boolean canWithdraw(){
		for(Territory territory :neighbors){
			if(this.canWithdraw(territory))return true;
		}
		return false;
	}
	
	/**
	 * When there is a battle and the defender lose, said if he can withdraw to the given territory
	 * @param territory
	 * @param seatNum
	 * @return
	 */
	public abstract boolean canWithdraw(Territory territory);
	public abstract int consolidation();
	/**
	 * 
	 * @return how many troops this territory can still recruit
	 */
	public abstract int getRecruit();
	/**return the recruits available to the initial state*/
	public void resetRecruit(){};
	public void recruitmentDone(){};
	/**
	 * Recruit a new troop in this territory, if there was no troops before create a new one, else add the recruit to the old troop
	 * @param troopIndex : 0 represent a boat, 1 a footman, 2 a knight, 3 a siege tower
	 */
	public abstract void recruit(int troopIndex);
	public int getSupply(){
		return 0;
	}
	/**
	 * 
	 * @return the influence point gived by this territory with the westeros card Game of Thrones 
	 */
	public int westerosCardGameOfThrones() {
		return 0;
	}
}
	
