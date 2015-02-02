package org.jogre.gameOfThrones.common.territory;

import org.jogre.gameOfThrones.common.Family;
import org.jogre.gameOfThrones.common.combat.NavalTroup;
import org.jogre.gameOfThrones.common.orders.OrderType;

/**
 * This territory give no resources and is only use is to travel and give access to ports  
 * @author robin
 *
 */
public class Water extends Territory {

	public Water(String name) {
		super(name);
	}

	public boolean canUseOrderOn(Territory territory){
		return (super.canUseOrderOn(territory) && (territory instanceof Water || order.getType()==OrderType.RAI));
	}

	public boolean canWithdraw(Territory territory){
		return (territory instanceof Water)&& (territory.getFamily()==null || territory.getFamily()==this.getFamily());
	}

	@Override
	public int consolidation() {
		return 0;
	}

	@Override
	public int getRecruit() {
		return 0;
	}


	/**
	 * Recruit a new troop in this territory, if there was no troops before create a new one, else add the recruit to the old troop
	 * @param troopIndex this parameter is not use for instance of Water 
	 */
	public void recruit(int troopIndex) {
		if(troop==null){
			troop= new NavalTroup(getFamily(), 1);
		}else{
			this.troop.addToop(1, 0,0,0);
		}
	}

	@Override
	protected boolean canGoTo(Territory territory) {
		return territory instanceof Water && this.neighbors.contains(territory);
	}

	@Override
	public int getNeutralForce() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Said if a ship can be recruit on this territory by a player 
	 * @param landThatIsRecruting the land that is recruiting new troops 
	 * @return true if a ship can be recruit there 
	 */
	public boolean canRecruitShipHere(Territory landThatIsRecruting ){
		return canRecruitShipHere(landThatIsRecruting.getFamily(), landThatIsRecruting);
	}
	
	/**
	 * Said if a ship can be recruit on this territory by a player 
	 * @param family the family that want to recruit a ship
	 * @return true if a ship can be recruit there 
	 */
	public boolean canRecruitShipHere(Family family, Territory landThatIsRecruting ){ 
		// le territoire n'a pas de toupes adverses
		boolean condition1 = (this.troop==null || this.troop.getFamily()==family);
		// le territoire possede un port ami (ou est se port)
		boolean condition2 = (this instanceof Port && this.getFamily()==family)|| this.frendlyPortNeighbors(family) ;
		// le territoire est voisin du territoire qui effectue le recrutement
		boolean condition3=neighbors.contains(landThatIsRecruting);
		//we test if every conditions are true
		return condition1 && condition2 && condition3;
	}
	
	/**Said if there is a port from the same family as the one in parameter in the neighborhood 
	 * @return  true if there is a port from the same family as the one in parameter in the neighborhood*/
	private boolean frendlyPortNeighbors(Family family){
		for(Territory territory : neighbors){
			if(territory instanceof Port && territory.getFamily()!=null && territory.getFamily()==family){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void setNeutralForce(int neutralForce) {}
	public void setInfluenceToken(Boolean influenceToken){}
}
