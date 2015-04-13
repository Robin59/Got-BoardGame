package org.jogre.gameOfThrones.common;
import java.util.LinkedList;
import java.util.List;

import org.jogre.gameOfThrones.common.orders.Order;
import org.jogre.gameOfThrones.common.orders.OrderType;
import org.jogre.gameOfThrones.common.territory.HomeLand;
import org.jogre.gameOfThrones.common.territory.Territory;


/**
 * @author robin
 *Instance of this class represent one of the six great family playable in the game
 */
public class Family {
	//names
	private final static String[] NAMES = {"Baratheon","Lannister","Stark","Greyjoy","Tyrell","Martell"};
	// the player of the family (0 baratheon, 1 lannister, etc)
	private int player;
	private boolean ordersGiven;
	// current value of the supply 
	private int supply; // influence the max troop
	private boolean infoCheck;
	/* The number of influence point is always between 0 and 20 */ 
	private int inflPoint;
	private int bid;
	
	//combatant card  (2 listes cartes utilisées et non utilisées)
	protected List<CombatantCard> combatantsAvailable;
	private List<CombatantCard> combatantsUse;
	//territory under control (quelle structure de donnée)
	private List<Territory> territories;
	
	//orders 
	private List<Order> ordersAvailable;
	private List<Order> ordersUse;
	//position sur les pistes A RETIRER!!!! 
	protected int fiefdomsTrack;
	//the model of the game
	private GameOfThronesModel model;
	private boolean swordUsed;
	
	
	/**
	*/
	public Family (int player, GameOfThronesModel model){
		this.model=model;
		combatantsAvailable=new LinkedList<CombatantCard>();
		territories=new LinkedList<Territory>();
		combatantsUse=new LinkedList<CombatantCard>();
		this.player=player;
		ordersGiven=false;
		inflPoint=5;
		swordUsed=false;
		//orders list creation
		ordersUse= new LinkedList<Order>(); // CETTE LISTE EST ELLE UTILE ???
		ordersAvailable= new LinkedList<Order>();
		// we add the defence orders
		ordersAvailable.add(new Order(false,1,0,OrderType.DEF));
		ordersAvailable.add(new Order(false,1,0,OrderType.DEF));
		ordersAvailable.add(new Order(true,2,0,OrderType.DEF));
		//support orders
		ordersAvailable.add(new Order(false,0,0,OrderType.SUP));
		ordersAvailable.add(new Order(false,0,0,OrderType.SUP));
		ordersAvailable.add(new Order(true,0,1,OrderType.SUP));
		//attaque orders
		ordersAvailable.add(new Order(false,0,0,OrderType.ATT));
		ordersAvailable.add(new Order(false,0,-1,OrderType.ATT));
		ordersAvailable.add(new Order(true,0,1,OrderType.ATT));
		//raid et consolidation
		ordersAvailable.add(new Order(false,0,0,OrderType.RAI));
		ordersAvailable.add(new Order(false,0,0,OrderType.RAI));
		ordersAvailable.add(new Order(true,0,0,OrderType.RAI));
		ordersAvailable.add(new Order(false,0,0,OrderType.CON));
		ordersAvailable.add(new Order(false,0,0,OrderType.CON));
		ordersAvailable.add(new Order(true,0,0,OrderType.CON));
	}
	
	
	//
	public void regainCard(CombatantCard card){
		combatantsUse.remove(card);
		combatantsAvailable.add(card);
	}
	public void removeCard(CombatantCard card){
		combatantsUse.add(card);
		combatantsAvailable.remove(card);
	}
	public void removeCard(int indexCard){
		removeCard(combatantsAvailable.get(indexCard));
	}
	public void addCard(CombatantCard card){
		combatantsAvailable.add(card);
	}
	public void setFiefdomsTrack(int fiefdomsTrack){
		this.fiefdomsTrack=fiefdomsTrack;
	}
	public int getFiefdomsTrack(){
		return fiefdomsTrack;
	}
	public void addTerritory(Territory territory){
		territories.add(territory);
	}
	public void removeTerritory(Territory territory){
		territories.remove(territory);
	}
	public List<Territory> getTerritories(){
		return territories;
	}
	public String getName(){
		return NAMES[player];
	}

	public int getPlayer() {
		return player;
	}
	
	/**This method add influence to the current influence points (you can use negative point for loosing influence)*/
	public void addInflu(int influ){
		inflPoint+=influ;
		if(inflPoint<0){
			inflPoint=0;
		}else if (inflPoint+influencePointOnBoard()>20){
			inflPoint=20-influencePointOnBoard();
		}
	}
	//return the nomber of influence point the family have put and board
	private int influencePointOnBoard(){
		int res=0;
		for(Territory territory : territories){
			if(territory.getInfluenceToken() && (!(territory instanceof HomeLand)|| ((HomeLand) territory).originalOwner(this))){
				res++;
			}
		}
		return res;
	}
	
	public void setBid(int bid){
		this.bid=bid;
	}
	public int getBid(){
		return bid;
	}
	public void resetBid(){
		inflPoint-=bid;
		bid=-1;
	}
	/**
	 * Give the number of influence point available for the family (always between 0 and 20) 
	 * @return the number of influence point in the family reserve (always between 0 and 20)
	 */
	public int getInflu(){
		return inflPoint;
	}
	public List<Order> getOrders(){
		return ordersAvailable;
	}
	/**
	 * during the order phase, the player give order to one of this territory (with troops)
	 * To use this method you have to be sure that the order is available and that the troops are from the right family
	 * @return true if the operation succeed, false if the order is not given  
	 */
	public boolean giveOrders(Territory territory,Order order){
		if(canPlayThisOrder(order)){
			//on retire l'ancien ordre avant
			if (territory.getOrder()!=null){
				ordersAvailable.add(territory.getOrder());
				ordersUse.remove(territory.getOrder());
			}
			territory.setOrder(order);
			ordersUse.add(order);
			ordersAvailable.remove(order);
			return true;
		}else return false;
	}
	/**
	 * tell if this family can play the given order (for now just check that it dosn't exceed the stars limitation
	 * @param order the order we want to use
	 * @return true if it can be played
	 */
	private boolean canPlayThisOrder(Order order){
		if(order.getStar()){
			int starAvailable=model.getStars(player);
			for(Order orderGived : ordersUse){
				if(orderGived.getStar()){
					starAvailable--;
				}
			}
			return starAvailable>0;
		}else{
			return true;
		}
		
	}
	
	/**
	 * This method is use to know  if a family have put order on all its territory with troops
	 * or if the family have no more orders available
	 * @return true if the family have put order on all its territory with troops or have no more orders 
	 */
	public boolean allOrdersGived(){
		boolean res=true;
		if(ordersAvailable.isEmpty()) return true;
		for(Territory territory: territories){
			if(territory.getOrder()==null && territory.getTroup()!=null){
				res=false;
			}
		}
		return res;
	}

	public void regainOrder(Order order) {
		ordersAvailable.add(order);
		ordersUse.remove(order);
		
	}
	
	/**
	 * return all cards in the combatantsAvailable list except the one gived in parameter
	 * @param card
	 */
	public void regainCombatantCards(CombatantCard card){
		this.regainCombatantCards();
		this.removeCard(card);
	}
	
	/** return all cards in the combatantsAvailable list*/
	public void regainCombatantCards(){
		for(CombatantCard card :combatantsUse){
			combatantsAvailable.add(card);
		}
		combatantsUse=new LinkedList<CombatantCard>();
	}
	/**
	 * Return the combatant cards(house card) available  
	 * @return the combatant cards available
	 */
	public List<CombatantCard> getCombatantCards() {
		return combatantsAvailable;
	}

	/**
	 * Return the discard combatant cards 
	 * @return the discard combatant cards 
	 */
	public List<CombatantCard> getDiscardCombatantCards() {
		return combatantsUse;
	}
	
	public boolean canUseSword() {
		return fiefdomsTrack==1 && !swordUsed;
	}
	public void swordUse(){
		swordUsed=true;
	}

	/**
	 * Set the supply track of this family, the value of supply will always be between 0 and 6, 
	 * if it's under 0 the value will automatically be raise to 0,
	 * and if it's above 6 the value will be brink back to 6  
	 * @param supply the new value of the supply (a value between 0 and 6)
	 */
	public void setSupply(int supply) {
		if(supply>=0 && supply<7) this.supply=supply;
		else if(supply<0)this.supply=0;
		else this.supply=6;
	}
	public int getSupply(){
		return supply;
	}

	public void infoNotCheck(){
		infoCheck=false;
	}
	public void infoCheck() {
		infoCheck=true;
	}
	
	public boolean isInfoCheck(){
		return infoCheck;
	}
	
	/**said if this family can recruit a new troop in this territory*/
	public boolean checkSupplyLimits(Territory territory){
		int[][] supplyLimites= {{2,2,0,0,0},{3,2,0,0,0},{3,2,2,0,0},{3,2,2,2,0},{3,3,2,2,0},{4,3,2,2,0},{4,3,2,2,2}};
		if(territory.getTroup()==null){
			return true;
		}
		int[] supply = new int[5];
		for(int i=0;i<5;i++){
			supply[i]=supplyLimites[this.getSupply()][i];
		}
		for(Territory otherTerritory : this.getTerritories()){
			if(otherTerritory!=territory && otherTerritory.getTroup()!=null && otherTerritory.getTroup().getEffectif()>1){
				int i=4;
				while(i>=0 && otherTerritory.getTroup().getEffectif()>supply[i]){
					i--;
				}
				if(i==-1){ //Normalement inutile si tout le reste est bien programmé
					return false;
				}
				supply[i]=0;
			}
		}
		for(int sup: supply){
			if(sup>territory.getTroup().getEffectif()){
				return true;
			}
		}
		return false;
		}
	
	/**
	 * This method is used to say that all orders have been given and validate by the family
	 */ 
	public void ordersGiven(){
		this.ordersGiven=true;
	}
	/**
	 * This method return the value of ordersGiven which tell us if all orders have been given and validate by the family
	 * @return True if all orders have been given and validate by the family, false if there is still orders to give or if the family havn't validate them already
	 */
	public boolean getOrdersGiven(){
		return ordersGiven;
	}
	/**Return true if the family still have orders on the board
	 * @return true if the family still have orders on the board
	 */
	public boolean haveOrderOnBoard(){
		for(Territory territory : territories){
			if(territory.getOrder()!=null) return true;
		}
		return false;
	}
	
	/**
	 * Tell if this family can add new ship on the board
	 * @return true if the family can add new ship on the board
	 */
	public boolean shipAvailable(){
		return this.getNumberShip()<6;
	}
	/**
	 * Tell if this family can add new footman on the board
	 * @return true if the family can add new footman on the board
	 */
	public boolean footmanAvailable(){
		return this.getNumberFootman()<10;
	}
	/**
	 * Tell if this family can add new knight on the board
	 * @return true if the family can add new knights on the board
	 */
	public boolean knightAvailable(){
		return this.getNumberKnight()<5;
	}
	/**
	 * Tell if this family can add new siege on the board
	 * @return true if the family can add new siege on the board
	 */
	public boolean siegeAvailable(){
		return this.getNumberSiege()<2;
	}
	/**
	 * Return the number of ship (own by this family) already on the board 
	 * @return  Return the number of ship (own by this family) already on the board
	 */
	public int getNumberShip(){
		int result=0;
		for(Territory territory : territories){
			if(territory.getTroup()!=null){
				result+=territory.getTroup().getTroops()[0];
			}
		}
		return result;
	}
	/**
	 * Return the number of footman(own by this family) already on the board 
	 * @return  Return the number of footman (own by this family) already on the board
	 */
	public int getNumberFootman(){
		int result=0;
		for(Territory territory : territories){
			if(territory.getTroup()!=null){
				result+=territory.getTroup().getTroops()[1];
			}
		}
		return result;
	}
	/**
	 * Return the number of knight (own by this family) already on the board 
	 * @return  Return the number of knight (own by this family) already on the board
	 */
	public int getNumberKnight(){
		int result=0;
		for(Territory territory : territories){
			if(territory.getTroup()!=null){
				result+=territory.getTroup().getTroops()[2];
			}
		}
		return result;
	}
	/**
	 * Return the number of siege machine (own by this family) already on the board 
	 * @return  Return the number of siege machine (own by this family) already on the board
	 */
	public int getNumberSiege(){
		int result=0;
		for(Territory territory : territories){
			if(territory.getTroup()!=null){
				result+=territory.getTroup().getTroops()[3];
			}
		}
		return result;
	}
	
	/**
	 * Change back the status of all the player's routed troops to normal
	 */
	public void rallyingTroops(){
		for(Territory territory :territories){
			if (territory.getTroup()!=null)territory.getTroup().rallyingTroops();
		}
	}
	
	/**remove the consolidation's orders from the ordersAivalable list*/
	public void removeConsOrder(){
		ordersAvailable= new LinkedList<Order>();
		// we add the defence orders
		ordersAvailable.add(new Order(false,1,0,OrderType.DEF));
		ordersAvailable.add(new Order(false,1,0,OrderType.DEF));
		ordersAvailable.add(new Order(true,2,0,OrderType.DEF));
		//support orders
		ordersAvailable.add(new Order(false,0,0,OrderType.SUP));
		ordersAvailable.add(new Order(false,0,0,OrderType.SUP));
		ordersAvailable.add(new Order(true,0,1,OrderType.SUP));
		//attaque orders
		ordersAvailable.add(new Order(false,0,0,OrderType.ATT));
		ordersAvailable.add(new Order(false,0,-1,OrderType.ATT));
		ordersAvailable.add(new Order(true,0,1,OrderType.ATT));
		//raid et consolidation
		ordersAvailable.add(new Order(false,0,0,OrderType.RAI));
		ordersAvailable.add(new Order(false,0,0,OrderType.RAI));
		ordersAvailable.add(new Order(true,0,0,OrderType.RAI));
	}
	/**this method remove the march +1 order*/
	public void removeMarchPOrder(){
		ordersAvailable= new LinkedList<Order>();
		// we add the defence orders
		ordersAvailable.add(new Order(false,1,0,OrderType.DEF));
		ordersAvailable.add(new Order(false,1,0,OrderType.DEF));
		ordersAvailable.add(new Order(true,2,0,OrderType.DEF));
		//support orders
		ordersAvailable.add(new Order(false,0,0,OrderType.SUP));
		ordersAvailable.add(new Order(false,0,0,OrderType.SUP));
		ordersAvailable.add(new Order(true,0,1,OrderType.SUP));
		//attaque orders
		ordersAvailable.add(new Order(false,0,0,OrderType.ATT));
		ordersAvailable.add(new Order(false,0,-1,OrderType.ATT));
		//raid et consolidation
		ordersAvailable.add(new Order(false,0,0,OrderType.RAI));
		ordersAvailable.add(new Order(false,0,0,OrderType.RAI));
		ordersAvailable.add(new Order(true,0,0,OrderType.RAI));
		ordersAvailable.add(new Order(false,0,0,OrderType.CON));
		ordersAvailable.add(new Order(false,0,0,OrderType.CON));
		ordersAvailable.add(new Order(true,0,0,OrderType.CON));
	}
	/**this method remove all orders from the territories and give them back to the player */
	public void ordersBack(){
		for(Territory territory : territories){
			territory.rmOrder();
		}
		ordersGiven=false;
		ordersAvailable= new LinkedList<Order>();
		// we add the defence orders
		ordersAvailable.add(new Order(false,1,0,OrderType.DEF));
		ordersAvailable.add(new Order(false,1,0,OrderType.DEF));
		ordersAvailable.add(new Order(true,2,0,OrderType.DEF));
		//support orders
		ordersAvailable.add(new Order(false,0,0,OrderType.SUP));
		ordersAvailable.add(new Order(false,0,0,OrderType.SUP));
		ordersAvailable.add(new Order(true,0,1,OrderType.SUP));
		//attaque orders
		ordersAvailable.add(new Order(false,0,0,OrderType.ATT));
		ordersAvailable.add(new Order(false,0,-1,OrderType.ATT));
		ordersAvailable.add(new Order(true,0,1,OrderType.ATT));
		//raid et consolidation
		ordersAvailable.add(new Order(false,0,0,OrderType.RAI));
		ordersAvailable.add(new Order(false,0,0,OrderType.RAI));
		ordersAvailable.add(new Order(true,0,0,OrderType.RAI));
		ordersAvailable.add(new Order(false,0,0,OrderType.CON));
		ordersAvailable.add(new Order(false,0,0,OrderType.CON));
		ordersAvailable.add(new Order(true,0,0,OrderType.CON));
	}

	/**this method remove the raid orders*/
	public void removeRaidOrder() {
		ordersAvailable= new LinkedList<Order>();
		// we add the defence orders
		ordersAvailable.add(new Order(false,1,0,OrderType.DEF));
		ordersAvailable.add(new Order(false,1,0,OrderType.DEF));
		ordersAvailable.add(new Order(true,2,0,OrderType.DEF));
		//support orders
		ordersAvailable.add(new Order(false,0,0,OrderType.SUP));
		ordersAvailable.add(new Order(false,0,0,OrderType.SUP));
		ordersAvailable.add(new Order(true,0,1,OrderType.SUP));
		ordersAvailable.add(new Order(false,0,0,OrderType.ATT));
		ordersAvailable.add(new Order(false,0,-1,OrderType.ATT));
		ordersAvailable.add(new Order(true,0,1,OrderType.ATT));
		ordersAvailable.add(new Order(false,0,0,OrderType.CON));
		ordersAvailable.add(new Order(false,0,0,OrderType.CON));
		ordersAvailable.add(new Order(true,0,0,OrderType.CON));
	}

	/**this method remove the defense orders*/
	public void removeDefenceOrder() {
		ordersAvailable= new LinkedList<Order>();
		ordersAvailable.add(new Order(false,0,0,OrderType.SUP));
		ordersAvailable.add(new Order(false,0,0,OrderType.SUP));
		ordersAvailable.add(new Order(true,0,1,OrderType.SUP));
		ordersAvailable.add(new Order(false,0,0,OrderType.ATT));
		ordersAvailable.add(new Order(false,0,-1,OrderType.ATT));
		ordersAvailable.add(new Order(true,0,1,OrderType.ATT));
		ordersAvailable.add(new Order(false,0,0,OrderType.RAI));
		ordersAvailable.add(new Order(false,0,0,OrderType.RAI));
		ordersAvailable.add(new Order(true,0,0,OrderType.RAI));
		ordersAvailable.add(new Order(false,0,0,OrderType.CON));
		ordersAvailable.add(new Order(false,0,0,OrderType.CON));
		ordersAvailable.add(new Order(true,0,0,OrderType.CON));
	}

	/**this method remove the supports orders*/
	public void removeSupportOrder() {
		ordersAvailable= new LinkedList<Order>();
		ordersAvailable.add(new Order(false,1,0,OrderType.DEF));
		ordersAvailable.add(new Order(false,1,0,OrderType.DEF));
		ordersAvailable.add(new Order(true,2,0,OrderType.DEF));
		ordersAvailable.add(new Order(false,0,0,OrderType.ATT));
		ordersAvailable.add(new Order(false,0,-1,OrderType.ATT));
		ordersAvailable.add(new Order(true,0,1,OrderType.ATT));
		ordersAvailable.add(new Order(false,0,0,OrderType.RAI));
		ordersAvailable.add(new Order(false,0,0,OrderType.RAI));
		ordersAvailable.add(new Order(true,0,0,OrderType.RAI));
		ordersAvailable.add(new Order(false,0,0,OrderType.CON));
		ordersAvailable.add(new Order(false,0,0,OrderType.CON));
		ordersAvailable.add(new Order(true,0,0,OrderType.CON));
		
	}
	
}
