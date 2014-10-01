package org.jogre.gameOfThrones.common;
import java.util.LinkedList;
import java.util.List;

import org.jogre.gameOfThrones.common.orders.Order;
import org.jogre.gameOfThrones.common.orders.OrderType;
import org.jogre.gameOfThrones.common.territory.Territory;


/**
 * @author robin
 *Instance of this class represent one of the six great family playable in the game
 */
public class Family {
	//names
	private final static String[] NAMES = {"Baratheon","Lannister","Stark","Greyjoy"};
	// the player of the family (0 baratheon, 1 lannister, etc)
	private int player;
	public boolean ordersGived;
	// current value of the supply 
	private int supply; // influence the max troop
	private boolean cardSaw;
	// influence points 
	private int inflPoint;
	
	//combatant card  (2 listes cartes utilisées et non utilisées)
	protected List<CombatantCard> combatantsAvailable;
	private List<CombatantCard> combatantsUse;
	//territory under control (quelle structure de donnée)
	private List<Territory> territories;
	
	//orders 
	private List<Order> ordersAvailable;
	private List<Order> ordersUse;
	//position sur les pistes 
	protected int fiefdomsTrack;
	private boolean swordUsed;
	
	
	/**
	 *Pour la creation de famille utiliser une classe abstraite et ensuite creer autant
	 *de classes qu'il y a de famille (avec seulement le constructeur qui change) ? 
	*/
	public Family (int player){
		combatantsAvailable=new LinkedList<CombatantCard>();
		territories=new LinkedList<Territory>();
		combatantsUse=new LinkedList<CombatantCard>();
		this.player=player;
		ordersGived=false;
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
	
	public void gainInflu(int influ){
		inflPoint+=influ;
	}
	public int getInflu(){
		return inflPoint;
	}
	public List<Order> getOrders(){
		return ordersAvailable;
	}
	/**
	 * during the order phase, the player give order to one of this territory (with troops)
	 * To use this methode you have to be sure that the order is available and that the troops are from the right familly 
	 */
	public void giveOrders(Territory territory,Order order){
		//on retire l'ancien ordre avant
		if (territory.getOrder()!=null){
			ordersAvailable.add(territory.getOrder());
		}
			territory.setOrder(order);
			ordersUse.add(order);
			ordersAvailable.remove(order);
	}
	
	/** verifie que tous les territoires possedés un ordre*/
	public boolean allOrdersGived(){
		boolean res=true;
		for(Territory territory: territories){
			if(territory.getOrder()==null){
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

	public List<CombatantCard> getCombatantCards() {
		return combatantsAvailable;
	}

	public boolean canUseSword() {
		return fiefdomsTrack==1 && !swordUsed;
	}
	public void swordUse(){
		swordUsed=true;
	}

	public void setSupply(int supply) {
		this.supply=supply;
	}
	public int getSupply(){
		return supply;
	}

	public void carteNonVu(){
		cardSaw=false;
	}
	public void carteVu() {
		cardSaw=true;
	}
	
	public boolean carteDejaVu(){
		return cardSaw;
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
	
}
