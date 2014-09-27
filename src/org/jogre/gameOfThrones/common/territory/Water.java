package org.jogre.gameOfThrones.common.territory;

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

	@Override
	public void recruit(int troopIndex) {
		if(troop==null){
			troop= new NavalTroup(getFamily(), this, 1);
		}else{
			this.troop.addToop(1, 0,0,0);
		}
	}
}
