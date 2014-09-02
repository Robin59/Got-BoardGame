package org.jogre.gameOfThrones.common.territory;

import java.util.ArrayList;
import java.util.List;

import org.jogre.gameOfThrones.common.Family;
import org.jogre.gameOfThrones.common.combat.GroundForce;
import org.jogre.gameOfThrones.common.combat.Troop;
import org.jogre.gameOfThrones.common.orders.Order;
import org.jogre.gameOfThrones.common.orders.OrderType;


public abstract class Territory {
	/**Name of the territory*/
	private String name;
	/**the owner of the territory, null if none*/
	private Family owner;  // utile dans le cas ou on a pas de troupes
	private Troop troop;
	/** the order given in this territory*/
	protected Order order;
	/** to know wich territory you can access from this territory*/
	protected List<Territory> neighbors;
	/** indicate how many troups you can recruit on this territory
	 * 0 is when there is no castle (specialy if it's not a land)*/
	protected int castle;
	
	public Territory(String name){
		this.name=name;
		neighbors=new ArrayList();
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
			String sep= System.getProperty("line.separator");// retour à la ligne
			res+= " "+sep+" ";
			int[] troops = troop.getTroops();
			if(troop instanceof GroundForce){
				res+="there is "+troops[1]+" footman, "+troops[2]+" knigth, "+troops[3]+" siege tower";
			}else{
				res+="there is "+troops[0]+" ship";
			}
			if (order!=null){ // empecher tout le monde voir les ordres
				res+=" your order is : "+order;
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
			}else{
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
				territory.getOrder().attUse=true;
				return 2;	
			}
			
		}
		
	}
}
	
