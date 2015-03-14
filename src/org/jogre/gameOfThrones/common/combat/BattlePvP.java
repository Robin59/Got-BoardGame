package org.jogre.gameOfThrones.common.combat;

import org.jogre.gameOfThrones.common.CombatantCard;
import org.jogre.gameOfThrones.common.Family;
import org.jogre.gameOfThrones.common.GameOfThronesModel;
import org.jogre.gameOfThrones.common.orders.Order;
import org.jogre.gameOfThrones.common.orders.OrderType;
import org.jogre.gameOfThrones.common.territory.Territory;


public class BattlePvP extends Battle{

	
	private int def;
	private int att;
	private int attSwords;
	private int attTowers;
	private int defSwords;
	private int defTowers;
	private Family defFamily;
	CombatantCard attCard;
	CombatantCard defCard;
	private int attCardPower;
	private int defCardPower;
	//these objects are for solving house card's effects
	private HouseCardEffect defCardEffect;
	private HouseCardEffect attCardEffect;
	//
	private boolean attUseSword;
	private boolean defUseSword;
	
	public BattlePvP(Territory attTerritory, Territory defTerritory,GameOfThronesModel model,Order order){
		super(attTerritory, defTerritory,model,order);
		def=0;
		att=0;
		defFamily=defTerritory.getFamily();
		attCard=null;
		defCard=null;
		attCardEffect=null;
		defCardEffect=null;
		attUseSword=false;
		defUseSword=false;
		attSwords=0;
		attTowers=0;
		defSwords=0;
		defTowers=0;
		attCardPower=0;
		defCardPower=0;
	}



	@Override
	public void nextPhase() {
		switch(state){
		case BATTLE_SHOW_CARDS :
			afterCardsSaw();
			break;
		case BATTLE_SHOW_RESOLUTION :
			cardEffectEndBattle();
			break;
		case BATTLE_CARD_EFFECT_END_BATTLE:
			battleResolution();
			break;
		}
	}
	
	
	@Override
	public int defPower(){
		int res =0;
		if (defTerritory.getOrder()!=null) res=defTerritory.getOrder().getDefBonus();
		if (defTerritory.haveGarrison()) res+=2;
		if(groundType==2){
			res+= defTerritory.getTroup().getEffectif();
		}else{
			res+=defTerritory.getTroup().getTroops()[1]+defTerritory.getTroup().getTroops()[2]*2;
		}
		for(Territory territory : defSupport){
			int[] troops =territory.getTroup().getTroops();
			res+=territory.getOrder().getOthBonus()+troops[0]+troops[1]+troops[2]*2;
		}
		return res;
	}
	
	public void startBattle() {
		state=BATTLE_CHOOSE_CARD;
		model.updateLabel();
	}
	
	/**
	 * Remove troops to the army (be careful this method automatically remove the siege engines)
	 * @param territory the territory who loose some troops
	 * @param casualties the number of troops that are remove
	 * @return false if all the troops are remove and the army doesn't exist anymore 
	 */
	private boolean destructTroops(Territory territory, int casualties){
		territory.getTroup().getTroops()[3]=0;
		if(casualties>=territory.getTroup().getEffectif()){
			territory.setTroup(null);
			return false;
		}else if (casualties<1){
			return true;
		}else{
			if(groundType==2){
				territory.getTroup().destruction(casualties);
			}else{
				territory.getTroup().destruction(casualties);
			}
			return true;
		}
	}
	
	/*Check if there is some card effect to apply before */
	private void cardEffectEndBattle(){
		state=BATTLE_END;// this is changed if there is still actions to do for solving the battle
		//creation of the card's effect
		afterResolutionCardEffect(battleWinner());
		retournCards();
		//resolution of the automatics card's effect
		if(attCardEffect!=null)attCardEffect.autoExecute();
		if(defCardEffect!=null)defCardEffect.autoExecute();
		// if there is no effect (so state still equals BATTLE_END) we continue to the resolution 
		if(state==BATTLE_END) battleResolution();
		model.updateLabel();
	}
	
	private void battleResolution(){
		state=BATTLE_END;// this is changed if there is still actions to do for solving the battle
		//
		if(battleWinner()){
			//destruction of the garrison if there's one
			defTerritory.destructGarrison();
			//on detruit les troups du defenceur 
			if (destructTroops(defTerritory, (attSwords-defTowers)) && defTerritory.canWithdraw()){
				//il choisit une retraite car il reste des troupes
				System.out.println("retraite");
				state=BATTLE_WITHDRAWAL;
			}else{
				// on met les nouvelles troupes sur le territoire
				if(groundType==2){
					defTerritory.setTroup(new NavalTroup(attFamily, attTroops[0]));
				}else{
					defTerritory.setTroup(new GroundForce(attFamily, attTroops[1],attTroops[2],attTroops[3]));
				}
				
			}
			//on retire l'ordre
			defTerritory.rmOrder();
			defTerritory.setInfluenceToken(false);
			if(attCard.getName().equals("Loras")){
				defTerritory.setOrder(new Order(attOrder.getStar(),0,attOrder.getOthBonus(), OrderType.ATT));
			}
			System.out.println("victoire");
		}else{
			//on detruit les troups de l'attaquant
			System.out.println("defailt");
			// faire les destructions des troupes ici!!
			attTroops[3]=0;//destruction of siege tower
			if(defSwords>attTowers){
				if(groundType==2){
					attTroops[0]-=(defSwords-attTowers);
				}else{
					if(attTroops[1]<(defSwords-attTowers)){
						attTroops[2]-=(defSwords-attTowers-attTroops[1]);
						attTroops[1]=0;
					}else{
						attTroops[1]-=(defSwords-attTowers);
					}
				}
			}
			//Attacker's troops withdraw (if there is still some) 
			if(attTroops[0]+attTroops[1]+attTroops[2]>=0){
				if(attTerritory.getTroup()!=null){
					attTerritory.getTroup().addTroop(attTroops);
				}else if(groundType==2){
					attTerritory.setTroup(new NavalTroup(attFamily, attTroops[0]));
				}else{
					attTerritory.setTroup(new GroundForce(attFamily, attTroops[1],attTroops[2],0));
				}
			}
		}
		System.out.println("sate "+state);
	}

	/**
	 * This method is call when a battle end, it remove the card players played and check if they still have some,
	 * if not they regain their cards except the one they just played 
	 */
	public void retournCards(){
		attFamily.removeCard(attCard);
		defFamily.removeCard(defCard);
		//if a player have no more cards in his hand, he regain them  
		if (attFamily.getCombatantCards().isEmpty()){
			attFamily.regainCombatantCards(attCard);
		}
		if (defFamily.getCombatantCards().isEmpty()){
			defFamily.regainCombatantCards(defCard);
		}
	}
	
	/**return true if the attacker win, false if it's the defender*/
	private boolean battleWinner() {
		if(att==def){
			return attFamily.getFiefdomsTrack()<defFamily.getFiefdomsTrack();
		}else{
			return att>def; 
		}
	}
	
	


	
	@Override
	public boolean playerPartisipate(int seatNum) {
		return (attFamily.getPlayer()==seatNum || defTerritory.getFamily().getPlayer()==seatNum);
	}
	
	
	/**
	 * Tell if the attacker and the defender have choose theirs cards
	 * @return true if the attacker and the defender have choose theirs cards
	 */
	public boolean cardsPlayed(){
		return attCard!=null && defCard!=null;
	}
	
	/**
	 * 
	 * @param card
	 * @param family
	 */
	public void playCard(CombatantCard card, Family family) {
		if(family==attFamily){
			attCard=card;
		}else{
			defCard=card;
		}
		if(cardsPlayed()){
			state=BATTLE_SHOW_CARDS;
			attFamily.infoNotCheck();
			defFamily.infoNotCheck();
		}
	}
	
	
	private void afterCardsSaw(){
		//on test Tyrion 
		if(attCard.getName().equals("Tyrion")&& attCardEffect==null){
			attCardEffect=new TyrionEffect(this, false);
		}else if(defCard.getName().equals("Tyrion")&& defCardEffect==null) {
			defCardEffect=new TyrionEffect(this, true);
		}else if ((attCardEffect==null || attCardEffect.getFinish())&&(defCardEffect==null || defCardEffect.getFinish())){
			attSwords=attCard.getSword();
			attTowers=attCard.getTower();
			defSwords=defCard.getSword();
			defTowers=defCard.getTower();
			attCardPower=attCard.getPower();
			defCardPower=defCard.getPower();
			att=this.attPower();
			def=this.defPower();
			//la reines des epines, Faire un etat particulié qui permet de clicker sur la carte pour les cartes 
			//les autres (vict,etc)
			befforSwordCardEffect();
			att+=attCardPower;
			def+=defCardPower;
			//on regarde si un des deux joueurs a l'épée
			if(attFamily.canUseSword()||defFamily.canUseSword()){
				this.state=BATTLE_PLAY_SWORD;
			}else{
				state=BATTLE_SHOW_RESOLUTION;
				attFamily.infoNotCheck();
				defFamily.infoNotCheck();
			}
			model.updateLabel();
		}
	}
	
	/**
	 * This method is call when a player use the sword to boost his strength 
	 */
	public void useSword(){
		if(attFamily.canUseSword()){
			att++;
			attUseSword=true;
			attFamily.swordUse();
		}else{
			def++;
			defUseSword=false;
			defFamily.swordUse();
		}
		state=BATTLE_SHOW_RESOLUTION;
		attFamily.infoNotCheck();
		defFamily.infoNotCheck();
		
	}
	public void dontUseSword(){
		state=BATTLE_SHOW_RESOLUTION;
		attFamily.infoNotCheck();
		defFamily.infoNotCheck();
	}
	public boolean getDefUseSword(){
		return defUseSword;}
	public boolean getAttUseSword(){
		return attUseSword;}
	
	public void setAttCard(CombatantCard card){
		attCard=card;}
	public void setDefCard(CombatantCard card){
		defCard=card;}
	public CombatantCard getAttCard(){
		return attCard;}
	public CombatantCard getDefCard(){
		return defCard;}
	
	public Family getDefFamily() {
		return defTerritory.getFamily();
	}


	

	
	public boolean canPlayCard(Family family) {
		return ((family==attFamily && attCard==null)||(family==defTerritory.getFamily() && defCard==null));
	}
	
	/**
	 * This method is call when the defencer lose and must withdraw
	 * @param territory
	 */
	public void withdraw (Territory territory){
		defTerritory.mouveTroops(territory);
		defFamily.removeTerritory(defTerritory);
		state=BATTLE_END;
		// put the victorious troops on the territory
		if(groundType==2){
			defTerritory.setTroup(new NavalTroup(attFamily, attTroops[0]));
		}else{
			defTerritory.setTroup(new GroundForce(attFamily, attTroops[1],attTroops[2],attTroops[3]));
		}
	}
	
	@Override
	public int mustDisplay(int player){
		if(state==BATTLE_CARD_EFFECT_END_BATTLE){
			if(defCardEffect!=null){
				return defCardEffect.display(player);
			}else{
				return attCardEffect.display(player);
			}
		}else{
			return super.mustDisplay(player);
		}
			
	}
	
	
	// on bellow is the different card effects 
	
	/**
	 * This method is call when a player click on his playerChoice for solving a card effect  
	 * @param value the value send by the player choice
	 */
	public void afterEffectBattle(int value){
		if(attCardEffect!=null && !attCardEffect.getFinish()){
			attCardEffect.execute(value);
		}else if(defCardEffect!=null && !defCardEffect.getFinish()){
			defCardEffect.execute(value);
		}
		if((attCardEffect==null || attCardEffect.getFinish())&&(defCardEffect==null || defCardEffect.getFinish())){
			nextPhase();
		}
	}
	/**
	 * This method is call when a player click on the board for solving a card effect  
	 * @param the territory on which the player click 
	 */
	public void afterEffectBattle(Territory territory){
		if(attCardEffect!=null && !attCardEffect.getFinish()){
			attCardEffect.execute(territory);
		}else if(defCardEffect!=null && !defCardEffect.getFinish()){
			defCardEffect.execute(territory);
		}
		if((attCardEffect==null || attCardEffect.getFinish())&&(defCardEffect==null || defCardEffect.getFinish())){
			nextPhase();
		}
	}
	/**
	 * This method is call when a player click on the board for solving a card effect,
	 * this method do the same thing as afterEffectBattle(Territory territory) but check if the player who click was authorized to do so
	 * @param territory the territory on which the player click
	 * @param  player the player who click
	 * @return true, if the player was authorized to did it
	 */
	public boolean afterEffectBattle(Territory territory, int player){
		boolean res=false;
		if(attCardEffect!=null && !attCardEffect.getFinish()){
			res=attCardEffect.execute(territory,player);
		}else if(defCardEffect!=null && !defCardEffect.getFinish()){
			res=defCardEffect.execute(territory,player);
		}
		if((attCardEffect==null || attCardEffect.getFinish())&&(defCardEffect==null || defCardEffect.getFinish())){
			nextPhase();
		}
		return res;
	}
	/*
	 * card's effect that are use after the battle resolutions 
	 * @param attackerWin true if the attacker win  
	 */
	private void afterResolutionCardEffect(boolean attackerWin){// THE BEST WOULD BE TO ATTACH DIRECTLY EFFECTS TO CARD AND SUPPRESS THESES METHODS
		if(attackerWin){
			if(attCard.getName().equals("Tywin")){
				attFamily.addInflu(2);
			}else if(attCard.getName().equals("Cersei")){
				attCardEffect= new CerseiEffect(this, false);
			}else if(attCard.getName().equals("Renly")){
				attCardEffect= new RenlyEffect(this, false);
			}else if(attCard.getName().equals("Robb")){
				attCardEffect= new RobbEffect(this, false);
			}
			if(defCard.getName().equals("BlackFish")){
				attSwords=0;
			}else if(defCard.getName().equals("Roose")){
				defCardEffect= new RooseEffect(this,true);
			}
		}else{
			if(defCard.getName().equals("Tywin")){
				defFamily.addInflu(2);
			}else if(defCard.getName().equals("Cersei")){
				defCardEffect= new CerseiEffect(this, true);
			}else if(defCard.getName().equals("Renly")){
				defCardEffect= new RenlyEffect(this, true);
			}else if(defCard.getName().equals("Robb")){
				defCardEffect= new RobbEffect(this, true);
			}
			if(attCard.getName().equals("BlackFish")){
				defSwords=0;
			}else if(attCard.getName().equals("Roose")){
				attCardEffect= new RooseEffect(this,false);
			}
		}
		if(attCard.getName().equals("PatchFace")){
			attCardEffect=new PatchfaceEffect(this, false);
		}else if(defCard.getName().equals("PatchFace")){
			defCardEffect=new PatchfaceEffect(this, true);
		}
	}
	
	public int getAtt(){return att;}
	public int getDef(){return def;}
	
	
	/*card's effect that are use before the battle resolutions*/ 
	private void befforSwordCardEffect(){
		if(attCard.getName().equals("Balon")||defCard.getName().equals("Balon")){
			balonEffect();//the effect is just on the printed strength, do not influ on the effect (for stannis and davos) 
		}
		if(attCard.getName().equals("Stannis") || defCard.getName().equals("Stannis")){
			stannisEffect();
		}
		if(defCard.getName().equals("Catelyn")){
			def+=defTerritory.getOrder().getDefBonus();
		}
		if(attCard.getName().equals("Kevan")){
			kevanEffect();
		}
		if(attCard.getName().equals("Victarion")){
			victarionEffect();
		}
		if(attCard.getName().equals("Salladhor")|| defCard.getName().equals("Salladhor")){
			salladhorEffect();
		}
		if(attCard.getName().equals("Davos")|| defCard.getName().equals("Davos")){
			davosEffect();
		}
		if(groundType<2 && (attCard.getName().equals("Mace")|| defCard.getName().equals("Mace")) && (!defCard.getName().equals("BlackFish") || !attCard.getName().equals("BlackFish"))){
			maceEffect();
		}
	}
	
	private void maceEffect(){
		if(attCard.getName().equals("Mace") && defTerritory.getTroup().getTroops()[1]>0){
			defTerritory.getTroup().rmToop(0, 1, 0, 0);
		}else if(defCard.getName().equals("Mace") && attTroops[1]>0){
			attTroops[1]--;
		}
	}
	
	
	private void kevanEffect(){
		att+=attTroops[1];
		for(Territory territory : attSupport){
			if(territory.getFamily().getName().equals("Lannister")){
				att+=territory.getTroup().getTroops()[1];
			}
		}
	}
	private void victarionEffect(){
		att+=attTroops[0];
		for(Territory territory : attSupport){
			if(territory.getFamily().getName().equals("Greyjoy")){
				att+=territory.getTroup().getTroops()[0];
			}
		}
	}
	private void salladhorEffect(){//
		if((attCard.getName().equals("Salladhor")&& !attSupport.isEmpty())||(defCard.getName().equals("Salladhor")&& !defSupport.isEmpty())){
			for(Territory territory : defSupport){
				if(!territory.getFamily().getName().equals("Baratheon")){
					def-=territory.getTroup().getTroops()[0];
				}
			}
			for(Territory territory : attSupport){
				if(!territory.getFamily().getName().equals("Baratheon")){
					att-=territory.getTroup().getTroops()[0];
				}
			}
		}
	}
	
	private void stannisEffect(){
		if(attCard.getName().equals("Stannis")){
			if(!attFirstThrone()) attCardPower++;
		}else{
			if(attFirstThrone()) defCardPower++;
		}
	}
	//tell if the attacker is higher on the throne track
	private boolean attFirstThrone(){
		for(int player : model.getThrone()){
			if(player==attFamily.getPlayer()){
				return true;}
			else if(player==defFamily.getPlayer()){
				return false;}
		}
		return false;
	}
	private void davosEffect(){
		if(attCard.getName().equals("Davos")&&davosAttBoolean()){
			attCardPower++;
			attSwords++;
		}else if(davosDefBoolean()){
			defCardPower++;
			defSwords++;
		}
	}
	private boolean davosAttBoolean(){
		for (CombatantCard card :attFamily.getCombatantCards()){
			if(card.getName().equals("Stannis")){
				return false;
			}
		}
		return true;
	}
	private boolean davosDefBoolean(){
		for (CombatantCard card :defFamily.getCombatantCards()){
			if(card.getName().equals("Stannis")){
				return false;
			}
		}
		return true;
	}
	private void balonEffect(){
		if(attCard.getName().equals("Balon")){
			defCardPower=0;
		}else{
			attCardPower=0;}
	}
	
	
}
