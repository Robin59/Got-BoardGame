package graphisme;

import java.awt.Graphics;
import java.awt.Image;
import java.util.List;

import javax.swing.JLabel;

import org.jogre.client.awt.GameConnectionPanel;
import org.jogre.client.awt.GameImages;
import org.jogre.client.awt.JogreComponent;
import org.jogre.client.awt.JogrePanel;
import org.jogre.gameOfThrones.common.Bidding;
import org.jogre.gameOfThrones.common.CombatantCard;
import org.jogre.gameOfThrones.common.Family;
import org.jogre.gameOfThrones.common.GameOfThronesModel;
import org.jogre.gameOfThrones.common.combat.Battle;
import org.jogre.gameOfThrones.common.combat.BattlePvP;
import org.jogre.gameOfThrones.common.orders.*;
import org.jogre.gameOfThrones.common.territory.Territory;
import org.jogre.gameOfThrones.common.territory.Water;

import state.ModelState;
import wildlingsResolution.WildlingsResolution;


public class PlayersChoices extends JogreComponent {

	
	private Image endTurnImage;
	private Image dontUseImage;
	private Image attackerImage;
	private Image defencerImage;
	private Image noOneImage;
	private Image validateImage;
	private ImageSelector images;
	//le label qui affiche les info
	private JLabel label;
	// indique dans quel etat est le PlayersChoices, 0 pour rien d'affiché, 1 pour ordres, 2 pour fin de tour Prog
	// 3 quand un ordre est selectionné (phase de resolution), 4 quand on va faire un mouvement naval et que les troupes sont affichées
	//5 pour terrestre. 6 et 7 pour les combats, 8 pour le support, 9 pour les cartes
	private int panel;
	/*the territory related to the current choice, null if none*/
	Territory relatedTerr;
	/*the cible territory when the player choice need 2 territory*/ 
	Territory cibleTerr;
	Battle battle;
	/*The index of the selected house card. -1 if none*/
	private int selectedHouseCard;
	/*Said the index of the first house card show on the left during the battle*/
	private int indexHouseCard;
	private Family family;
	private Image swordImage;
	private Image swordFlipImage;
	private Image recruitImage;
	private Image consoImage;
	private Image leftArrowImage;
	private Image rigthArrowImage;
	private String westerosCard;
	private String wilidingsCard;
	private Image[] letters;
	private Image selectionBid;
	/*use for the equality case, value = -1 if no bid selected otherwise it's the position of the bid who is selected*/
	private int selectedBid;
	private int powerBid;
	private Bidding bidding;
	//pointeur to the model
	GameOfThronesModel model;
	// True if a ship is selected during the mustering phase, false else
	private Boolean shipRecrutement;
	private WildlingsResolution wildlingsResolution;
	
	//Constant use for the message return by the main method (RigthClick) of this object 
	public static final int END_PROGRAMATION_PHASE=1;
	public static final int CANCEL=2;
	public static final int SEND_SHIP=3;
	public static final int SEND_FOOT=4;
	public static final int SEND_KNIGHT=5;
	public static final int SEND_SEIGE=6;
	public static final int ATT_PREPARATION_ENDED=7;
	public static final int SEND_SHIP_FOR_ATT=8;
	public static final int SEND_FOOT_FOR_ATT=9;
	public static final int SEND_KNIGHT_FOR_ATT=10;
	public static final int SEND_SEIGE_FOR_ATT=11;
	public static final int SUPPORT_ATT=12;
	public static final int SUPPORT_DEF=13;
	public static final int SUPPORT_NONE=14;
	public static final int HOUSE_CARD_CHOSEN=15;
	public static final int	VALYRIAN_SWORD_USE=16;
	public static final int	VALYRIAN_SWORD_NOT_USE=17;
	public static final int CONSOLID=18;
	public static final int RECRUIT_SHIP=19;
	public static final int RECRUIT_FOOT=20;
	public static final int RECRUIT_KNIGHT=21;
	public static final int RECRUIT_SEIGE=22;
	public static final int WESTEROS_CARD_SAW=23;
	public static final int BID_CHOSED=24;
	public static final int LETTER_A_CHOSE=25;
	public static final int LETTER_B_CHOSE=26;
	public static final int LETTER_C_CHOSE=27;
	public static final int TRACK_SORTED=28;
	public static final int WILDINGS_CARD_SAW=29; 
	public static final int REMOVE_SHIP=30;
	public static final int REMOVE_FOOT=31;
	public static final int REMOVE_KNIGHT=32;
	public static final int REMOVE_SIEGE=33;
	public static final int USE_INF_TOKEN=34;
	public static final int DONT_USE_INF_TOKEN=35;
	public static final int ORDER_CHANGED=36;
	public static final int PUT_CARD_TOP=39;
	public static final int PUT_CARD_BOTTOM=38;
	
	public PlayersChoices (JLabel label, GameOfThronesModel model){
		this.label=label;
		this.model=model;
		panel=0;
		relatedTerr =null;
		cibleTerr = null;
		battle = null;
		selectedBid=-1;
		shipRecrutement=false;
		//on ajoute les images qui seront utitles
		selectionBid= GameImages.getImage(127);
		endTurnImage = GameImages.getImage(202);
		dontUseImage = GameImages.getImage(7);
		validateImage = GameImages.getImage(202);
		attackerImage= GameImages.getImage(4);
		defencerImage= GameImages.getImage(5);
		leftArrowImage=GameImages.getImage(134);
		rigthArrowImage=GameImages.getImage(135);		
		noOneImage= GameImages.getImage(7);
		images= ImageSelector.IMAGESELECTOR;
		swordImage =GameImages.getImage(108);
		swordFlipImage =GameImages.getImage(109);
		recruitImage= GameImages.getImage(110);
		consoImage= GameImages.getImage(3);
		letters=new Image[3];
		letters[0]=GameImages.getImage(136);
		letters[1]=GameImages.getImage(137);
		letters[2]=GameImages.getImage(138);
		wildlingsResolution=null;
	}
	
//c'est la methode appelé quand on click gauche dans le playerChoice
	//renvoit un int code qui dit au controlleur ce qui s'est passé
	// 1 : le joueur a fini sa programmation
	// 2 : le joueur a retirer un ordre
	// 3 à 6 envoi de troups
	public int RigthClick(int x, int y, Family family) {
		switch (panel) {
		case DISPLAY_ORDERS :
			if(family.giveOrders(relatedTerr,choseOrder(x,y,family))){
				this.blank();
				// on regarde si tout ses teritoires on des ordres!!!
				if(family.allOrdersGived()){
					endProgramation();
				}
			}
		break;
		case DISPLAY_CHANGE_ORDER:
			if(family.giveOrders(relatedTerr,choseOrder(x,y,family))){
				this.blank2();
				return ORDER_CHANGED;
			}
			break;
		case DISPLAY_END_PROGRAMATION :
			if (x>200 && x<275 && y>100 && y<200){
				blank2();
				return END_PROGRAMATION_PHASE;
			}
			break;
		case DISPLAY_CANCEL :
			if (x>150 && x<200 && y>50 && y<100){
				return CANCEL;
			}
			break;
		//les mouvements
		case DISPLAY_WATER_MOV :
			if (x>150 && x<200 && y>50 && y<100 ){ 
				return SEND_SHIP;
			}
			break;
		case DISPLAY_LAND_MOV :
			if (y<170 && x>150 && x<200 && relatedTerr.getTroup().getTroops()[1]!=0 ){
				return SEND_FOOT;
			}else if (y<170 && x>250 && x<300 && relatedTerr.getTroup().getTroops()[2]!=0){
				return SEND_KNIGHT;
			}else if (y<170 && x>350 && x<400 && relatedTerr.getTroup().getTroops()[3]!=0){
				return SEND_SEIGE;
			}
			break;
		//les combats
		case DISPLAY_WATER_ATT :
			if (x>150 && x<200 && y>150 && y<200){ 
				return ATT_PREPARATION_ENDED;
			}
			break;
		case DISPLAY_LAND_ATT :
			if (x>150 && x<200 && y>150 && y<200){ 
				return ATT_PREPARATION_ENDED;
			}else if (x>50 && x<100 && y>50 && y<100 && relatedTerr.getTroup().getTroops()[1]!=0){
				return SEND_FOOT_FOR_ATT;
			}else if(x>150 && x<200 && y>50 && y<100 && relatedTerr.getTroup().getTroops()[2]!=0){
				return SEND_KNIGHT_FOR_ATT;
			}else if(x>250 && x<300 && y>50 && y<100 && relatedTerr.getTroup().getTroops()[3]!=0){
				return SEND_SEIGE_FOR_ATT;
			}
			break;
		case DISPLAY_SUPPORT_CHOICE:
			if (x>100 && x<150 && y>50 && y<100){ 
				blank2();
				return SUPPORT_ATT;
			}else if(x>250 && x<300 && y>50 && y<100){
				blank2();
				return SUPPORT_DEF;
			}else if(x>250 && x<300 && y>175 && y<225){
				return SUPPORT_NONE;
			}
			break;
		case DISPLAY_HOUSE_CARDS :
			if(x<60 && indexHouseCard>0){
				indexHouseCard--;
				repaint();
			}else if(x>510 && indexHouseCard<family.getCombatantCards().size()-1){
				indexHouseCard++;
				repaint();
			}else if (x>60 && x<510){
				choseCard(x, family);
				return HOUSE_CARD_CHOSEN;
			}
			break;
		case DISPLAY_VALYRIAN_SWORD :
			if (x>50 && x<140){
				blank2();
				this.repaint();
				return VALYRIAN_SWORD_NOT_USE;
			}else if(x>200 && x<300){
				blank2();
				this.repaint();
				return VALYRIAN_SWORD_NOT_USE;
			}
			break;
		case DISPLAY_RECRUIT_OR_CONSOLID : 
			if(x>50 && x<130){
				relatedTerr.resetRecruit();
				this.recruit(relatedTerr);
			}else if(x>350 && x<430){
				blank2();
				this.repaint();
				return CONSOLID;
			}
			break;
		case DISPLAY_RECRUITEMENT :
			if(y<170 && x>40&&x<130 && relatedTerr.canRecruitShip() && family.shipAvailable()){ 
				shipRecrutement=true;
				break;
			}else if (y<170 && x>150 && x<200 && family.footmanAvailable()){
				return RECRUIT_FOOT;
			}else if (y<170 && x>250 && x<300 && family.knightAvailable()){
				return RECRUIT_KNIGHT;
			}else if (y<170 && x>350 && x<400 && family.siegeAvailable()){
				return RECRUIT_SEIGE;
			}else if(y>190 && x>200 && x<270){
				return CANCEL;
			}
			break;
		case DISPLAY_WESTEROS_CARD:
			return WESTEROS_CARD_SAW;
		case DISPLAY_BID :
			if(x>100&&x<150 && powerBid>0){
				powerBid--;
				label.setText("Power Bid : "+powerBid);
			}else if(x>400&&x<450 && powerBid<family.getInflu()){
				powerBid++;
				label.setText("Power Bid : "+powerBid);
			}else if(x>240 && x<300){
				return BID_CHOSED; // end of the biddings
			}break;
		case DISPLAY_LETTERS:
			if(x>100 && x<170){
				blank();
				return LETTER_A_CHOSE;
			}else if(x>190 && x<260){
				blank();
				return LETTER_B_CHOSE;
			}else if(x>280 && x<350){
				blank();
				return LETTER_C_CHOSE;
			}
			break;
		case DISPLAY_BID_SORT:
			if(x>170 && x<230 && y>180){
				selectedBid=-1;
				return TRACK_SORTED;
			}else if (y<180){
				changeTrack(x);
			}
			break;
		case DISPLAY_WILDINGS_CARD:
			return WILDINGS_CARD_SAW;
		case DISPLAY_TROOP_DESTRUCTION:
			int troops[]=relatedTerr.getTroup().getTroops();
			if(x>50&&x<100 && troops[0]>0){
				return REMOVE_SHIP;
			}else if (y<170 && x>150 && x<200 && troops[1]>0){
				return REMOVE_FOOT;
			}else if (y<170 && x>250 && x<300 && troops[2]>0){
				return REMOVE_KNIGHT;
			}else if (y<170 && x>350 && x<400 && troops[3]>0){
				return REMOVE_SIEGE;
			}else if(y>190 && x>200 && x<270){
				return CANCEL;
			}
			break;
		case DISPLAY_USE_INF_TOKEN:
			if(x>120 && x<200){
				return USE_INF_TOKEN;
			}else if (x>320 && x<400){
				return DONT_USE_INF_TOKEN;
			}
			break;
		case DISPLAY_CROW_CHOICE:
			if(x>400){
				blank2();
				return CANCEL;
			}else if (x>20 && x<180){
				ravenSeeWildings();
			}
			break;
		case DISPLAY_RAVEN_SEE_WILDINGS:
			if(x>50 && x<100){
				blank2();
				return PUT_CARD_TOP;
			}else if(x>450 && x<500){
				blank2();
				return PUT_CARD_BOTTOM;
			}
			break;
		case DISPLAY_DISCARD_HOUSE_CARDS:
			if(x<60 && indexHouseCard>0){
				indexHouseCard--;
				repaint();
			}else if(x>510 && indexHouseCard<family.getCombatantCards().size()-1){
				indexHouseCard++;
				repaint();
			}else if (x>60 && x<510){
				choseDisplayCard(x, family);
				return HOUSE_CARD_CHOSEN;
			}
			break;
		}
		return 0;
	}
	
	public void paintComponent (Graphics g) { 
		switch(panel){
		case DISPLAY_BLANK:
			label.setText("");
			break;
		case DISPLAY_ORDERS:
			showOrders(g);
			break;
		case DISPLAY_CHANGE_ORDER :
			showOrders(g);
			break;
		case DISPLAY_END_PROGRAMATION:
			g.drawImage(endTurnImage,200,100, null);
			break;
		case DISPLAY_CANCEL:
			g.drawImage(dontUseImage,150,50, null);
			break;
		case DISPLAY_WATER_MOV :
			g.drawImage(images.getTroopImages(family)[0],150,50, null);
		break;
		case DISPLAY_LAND_MOV :
			drawTroops(g);
			break;
		case DISPLAY_WATER_ATT:
			g.drawImage(images.getTroopImages(family)[0],150,50, null);
			g.drawImage(endTurnImage,150,150, null);
			break;
		case  DISPLAY_LAND_ATT:
			g.drawImage(images.getTroopImages(family)[1],50,50, null);
			g.drawImage(images.getTroopImages(family)[2],150,50, null);
			g.drawImage(images.getTroopImages(family)[3],250,50, null);
			g.drawImage(endTurnImage,150,150, null);
			break;
		case DISPLAY_SUPPORT_CHOICE:
			g.clearRect(0, 0, 600, 250);
			g.drawImage(attackerImage, 100,50, null);
			g.drawImage(defencerImage, 250,50, null);
			g.drawImage(noOneImage, 175,150, null);
			break;
		case DISPLAY_HOUSE_CARDS:
			g.clearRect(0, 0, 600, 250);
			drawHouseCards(g);
			break;
		case DISPLAY_VALYRIAN_SWORD:
			g.drawImage(swordImage, 50, 5, null);
			g.drawImage(swordFlipImage, 200, 5, null);
			break;
		case DISPLAY_RECRUIT_OR_CONSOLID:
			g.drawImage(recruitImage, 50, 100, null);
			g.drawImage(consoImage, 350, 100, null);
			break;
		case DISPLAY_RECRUITEMENT:
			drawRecruit(g);
			g.drawImage(dontUseImage, 200,190,null);
			break;
		case DISPLAY_WESTEROS_CARD:
			g.drawImage(images.getWestCardImage(westerosCard), 50,0, null);
			break;
		case DISPLAY_BID : 
			g.drawImage(leftArrowImage,100,100,null);
			g.drawImage(rigthArrowImage,400,100,null);
			g.drawImage(images.getPowerImage(family),240,100, null);
			break;
		case DISPLAY_LETTERS:
			g.drawImage(letters[0],100,100,null);
			g.drawImage(letters[1],190,100,null);
			g.drawImage(letters[2],280,100,null);
			break;
		case DISPLAY_BID_SORT:
			drawBiddingTable(g);
			break;
		case DISPLAY_WILDINGS_CARD:
			g.drawImage(images.getWildingCardImage(wilidingsCard),100,0,null);
			break;
		case DISPLAY_TROOP_DESTRUCTION:
			drawTroops(g);
			g.drawImage(dontUseImage, 200,190,null);
			break;
		case DISPLAY_USE_INF_TOKEN:
			g.drawImage(images.getPowerImage(family),200,50,null);
			g.drawImage(validateImage, 120, 140, null);
			g.drawImage(dontUseImage, 320,140,null);
			break;
		case DISPLAY_CROW_CHOICE:
			label.setText("You can change an order, see the wildings card on top or do nothing");
			g.drawImage(images.getWildingCardImage("WildingsBack"), 25, 25,null);
			g.drawImage(dontUseImage, 400,100,null);
			break;
		case DISPLAY_RAVEN_SEE_WILDINGS:
			label.setText("You can put the card on top or bottom of the deck");
			g.drawImage(images.getWildingCardImage(wilidingsCard),180,5,null);
			g.drawImage(images.getTopImage(), 50, 125,null);
			g.drawImage(images.getBottomImage(), 450, 125,null);
			break;
		case DISPLAY_DISCARD_HOUSE_CARDS :
			label.setText("");
			drawDiscardHouseCards(g);
			break;
		}
	}

	/*this method is use to show orders available*/
	private void showOrders(Graphics g){
		int x =0;
		int y =0;
		List<Order> orders =family.getOrders();
		for (Order order : orders){
			g.drawImage(images.getOrderImage(order), (10+x*80), (10+y*80), null);
			x++;
			if(x==6){
				y++;
				x=0;
			}
		}
	}
	
	/**draw the troops actually on this current land*/
	private void drawTroops(Graphics g){
		int[] effectif =relatedTerr.getTroup().getTroops();
		for(int i=0; i<4; i++){
			if(effectif[i]>0){
				g.drawImage(images.getTroopImages(family)[i],i*100+50,100,null);
			}
		}
	}
	/**Draw the correct troop regarding the possibility and the family*/
	private void drawRecruit(Graphics g){
		if(relatedTerr.getRecruit()>0){
			if(relatedTerr.canRecruitShip() && family.shipAvailable()){
				g.drawImage(images.getTroopImages(family)[0],50,100,null);
				if(shipRecrutement) g.drawImage(images.getSelectShipImage(),40,90,null);
			}
			if(family.footmanAvailable()) g.drawImage(images.getTroopImages(family)[1],150,100,null);
			if(relatedTerr.getRecruit()>1 || (relatedTerr.getTroup()!=null && relatedTerr.getTroup().getTroops()[1]>0)){
				if(family.knightAvailable()) g.drawImage(images.getTroopImages(family)[2],250,100,null);
				if(family.siegeAvailable())g.drawImage(images.getTroopImages(family)[3],350,100,null);
			}
		}
	}
	/**
	 * Draw the house cards of the player, plus two arrow to navigate between them
	 * @param g
	 */
	private void drawHouseCards(Graphics g){
		g.drawImage(leftArrowImage, 1,100, null);
		g.drawImage(rigthArrowImage, 510,100, null);
		int i=0;
		List<CombatantCard> cards =family.getCombatantCards();
		for (CombatantCard card : cards){
			if(i>=indexHouseCard && i<=indexHouseCard+2){
				g.drawImage(images.getCardImage(card.getName()), (60+(i-indexHouseCard)*150),0, null);
			}
			i++;
		}
	}
	/**
	 * Draw the discard house cards of the player, plus two arrow to navigate between them
	 * @param g
	 */
	private void drawDiscardHouseCards(Graphics g){
		g.drawImage(leftArrowImage, 1,100, null);
		g.drawImage(rigthArrowImage, 510,100, null);
		int i=0;
		List<CombatantCard> cards =family.getDiscardCombatantCards();
		for (CombatantCard card : cards){
			if(i>=indexHouseCard && i<=indexHouseCard+2){
				g.drawImage(images.getCardImage(card.getName()), (60+(i-indexHouseCard)*150),0, null);
			}
			i++;
		}
	}
	
	/**Draw for the player with the throne, the different bid if there is an equality*/
	private void drawBiddingTable(Graphics g){
		int i = 10;
		for(Family family : bidding.getTrack()){
			g.drawImage(images.getPowerImage(family),i,100,null);
			g.drawImage(images.getNumber(family.getBid()),i+5,110,null);
			i+=70;
		}
		if (selectedBid!=-1){
			g.drawImage(selectionBid,selectedBid*70+10,100,null);
		}
		g.drawImage(dontUseImage, 175,180, null);
		repaint();
	}
	
	
	
	
	public Order choseOrder(int x, int y,Family family){//attraper les execptions
		int i=(x-10)/80+(y/80)*6;
		return family.getOrders().get(i);
	}
	
	/**
	 * This method is call when a player chose a House card for a battle
	 * @param x the x coordinate of the mouse 
	 * @param family the family that have chose a House card
	 */
	private void choseCard(int x, Family family){
		if (((x-60)/150)+indexHouseCard<family.getCombatantCards().size()){
			selectedHouseCard =((x-60)/150)+indexHouseCard;
			CombatantCard card =family.getCombatantCards().get(selectedHouseCard);
			battle.playCard(card, family);
			panel=DISPLAY_BLANK;
			this.repaint();
		}
	}
	/**
	 * This method is call when a player chose a display House card
	 * @param x the x coordinate of the mouse 
	 * @param family the family that have chose a House card
	 */
	private void choseDisplayCard(int x, Family family){
		if (((x-60)/150)+indexHouseCard<family.getDiscardCombatantCards().size()){
			selectedHouseCard =((x-60)/150)+indexHouseCard;
			panel=DISPLAY_BLANK;
			this.repaint();
		}
	}
	/**
	 * This method is call when a player want to give orders to a territory during the programations phase
	 * It display the orders available on this component
	 * @param family the family that want to give order
	 * @param terr The territory on which we want to give orders
	 */
	public void showOrders(Family family, Territory terr) {
		relatedTerr=terr;
		this.family=family;
		label.setText("Give order in "+terr.getName());
		panel=DISPLAY_ORDERS;
		this.repaint();
	}
	/**
	 * This method is call when a player want to change an order to a territory with is raven token
	 * It display the orders available on this component
	 * @param terr The territory on which you want to change the order
	 */
	public void changeOrder(Territory terr){
		relatedTerr=terr;
		label.setText("Give order in "+terr.getName());
		panel=DISPLAY_CHANGE_ORDER;
		this.repaint();
	}
	
	/**
	 * Show the recruit panel when a player click on a territory where he can recruit
	 * @param territory where we can recruit 
	 */
	public void recruit(Territory territory){
		relatedTerr=territory;
		panel=DISPLAY_RECRUITEMENT;
		repaint();
	}
	
	/**
	 * Return most of the attributes in this object to 0 or null and display a blank screen on this component
	 */
	public void blank() {
		relatedTerr=null;//vraiment utile ?
		cibleTerr=null;
		//westerosCard=null;
		wilidingsCard=null;
		indexHouseCard=0;
		panel=DISPLAY_BLANK;
		label.setText("");
		repaint();
	}
	/**
	 * Just display a blank screen on this component without returning the attributes to 0 or null
	 */
	public void blank2() {
		panel=DISPLAY_BLANK;
		label.setText("");
		repaint();
	}

	/**
	 * Affiche une fenetre qui demande la confimation de fin de phase de programmation
	 */
	public void endProgramation(){
		label.setText("You have gived all your orders, do you want to end your turn ?");
		panel=DISPLAY_END_PROGRAMATION;
		repaint();
	}
	/*On indique l'ordre qu'on veut utiliser  */
	public void orderSelected(Territory territory) {
		if(territory.getOrder().getType()==OrderType.CON){
			panel=DISPLAY_RECRUIT_OR_CONSOLID;
			label.setText("Do you want to recruit or to consolidate your power");
		}else{
			panel=DISPLAY_CANCEL;
			label.setText(territory.getName()+" gonna "+territory.getOrder().getType());
		}
		relatedTerr=territory;
		repaint();
	}

	public Territory getRelatedTerr() {
		return relatedTerr;
	}

	/**
	 * Draw the troops that the player can select and initialise everything that can be usefull for */
	public void moveTo(Territory territory) {
		getGraphics().clearRect(0, 0, 600, 250);
		cibleTerr = territory;
		label.setText("wich troops do want to send from "+relatedTerr.getName()+" to "+cibleTerr.getName());
		if(territory instanceof Water){
			panel=DISPLAY_WATER_MOV;
		}else{
			panel=DISPLAY_LAND_MOV;
			}
		this.repaint();
	}

	public void checkPlayerChoices() {
		if(model.getPhase()==ModelState.USE_INF_TOKEN){
			panel=DISPLAY_USE_INF_TOKEN;
			label.setText("Do you want to use an influence token to keep this territory?");
		}else if(relatedTerr.getOrder()==null){
			this.blank();
		}
		
	}
	/**
	 * display the troops that can be send for a battle
	 * @param territory the territory who is attacked
	 */
	public void attackTo(Territory territory) {
		getGraphics().clearRect(0, 0, 600, 250);
		cibleTerr = territory;
		if(territory instanceof Water){
			panel=DISPLAY_WATER_ATT;
		}else{
			panel=DISPLAY_LAND_ATT;
		}
		this.repaint();
	}
	/**
	 * Display a selection screen where a player can chose to support attackant, defencer or no one 
	 * @param territory the territory who can support in this battle
	 */
	public void support(Territory territory) {
		label.setText("who do you want to support ?");
		panel=DISPLAY_SUPPORT_CHOICE;
		relatedTerr = territory;
	}
	
	/**
	 * This method change the text of the label if the player must withdraw
	 * @param family
	 * @param battle
	 */
	public void withdrawal(Family family, Battle battle){
		if(family==battle.getDefFamily()){	
			label.setText("Choose a place to withdraw");
		}
	}
	
	/**
	 * This method set all the parameters to show the house Cards
	 * @param battle
	 */
	public void showHouseCards(Battle battle){
		this.battle=battle;
		label.setText("The attacker force is "+battle.attPower()+" the defender power is "+battle.defPower());
		selectedHouseCard=-1;
		panel=DISPLAY_HOUSE_CARDS;
		repaint();
	}
	
	/**
	 * Return the index of the selected house card, -1 if none
	 * @return the index of the selected house card, -1 if none
	 */
	public int getIndexCard(){
		return selectedHouseCard;
	}
	/**
	 * This method is call when it's time to play the valyrian sword,
	 * if the family have the valyrian sword, it is displayed
	 * @param family
	 */
	public void swordPlay(Family family){
		if(family.canUseSword()){
			panel=DISPLAY_VALYRIAN_SWORD;
			label.setText("The attacker force is "+((BattlePvP)battle).getAtt()+" the defender power is "+((BattlePvP)battle).getDef());
			repaint();
		}else{
			panel=DISPLAY_BLANK;
			repaint();
		}
	}
	
	/**
	 * Display the wildings Card 
	 * @param card the card that is gonna be displayed
	 */
	public void wilidingsCard(String card) {
		panel=DISPLAY_WILDINGS_CARD;
		wilidingsCard=card;
		repaint();
	}
	/**
	 * Display the westeros Card 
	 * @param card the card that is gonna be displayed
	 */
	public void westerosCard(String card) {
		panel=DISPLAY_WESTEROS_CARD;
		westerosCard=card;
		repaint();
		
	}

	public void bidding() {
		powerBid=0;
		label.setText("Power Bid : "+powerBid);
		panel=DISPLAY_BID;	
	}
	
	public int getBid(){
		return powerBid;
	}
	/**Happend when a player have to make a ternair choice because of a westeros card (like dark Wings dark words, put to the sword or throne of blades)*/
	public void westerosCardChoice(){
		panel=DISPLAY_LETTERS;
		repaint();
	}
	
	public String getWildingsCard(){
		return wilidingsCard;
	}
	/**
	 * Return the current state of this component
	 * @return the current state of this component
	 */
	public int getPanel(){
		return panel;
	}

	public void setPanel(int panel, WildlingsResolution wildlingsResolution){
		this.wildlingsResolution=wildlingsResolution;
		relatedTerr=wildlingsResolution.getTerritory(family);
		this.panel=panel;
		repaint();
	}
	/**when there is a bidding equality (the player with the throne can set the players the way he want)
	 * @param bidding the bid that the player can arrange
	 * */
	public void biddingEgality(Bidding bidding){
		panel=DISPLAY_BID_SORT;
		this.bidding=bidding;
	}
	/*All the mechanics to change the position on the track */
	private void changeTrack(int x){
		int tempBid=(x-10)/70;
		System.out.println("Dans ChangeTrack : temp ="+tempBid);
		if(selectedBid==tempBid){//cancel the selected bid
			selectedBid=-1;
		}else if(selectedBid==-1 && tempBid<bidding.getTrack().length){ //select a family
			selectedBid=tempBid;
		}else if(tempBid<bidding.getTrack().length && bidding.getTrack()[tempBid].getBid()==bidding.getTrack()[selectedBid].getBid()){//chage the order
			bidding.interchange(selectedBid, tempBid);
			selectedBid=-1;
		}
		repaint();
	}
	
	/**
	 * This method is call when a player have to destroy troops and click on one of this territory
	 * @param territory the territory where the player want to destroy troops
	 */
	public void destroyTroop(Territory territory) {
		relatedTerr=territory;
		label.setText("wich troops do want remove from "+relatedTerr.getName());
		panel=DISPLAY_TROOP_DESTRUCTION;
		repaint();
	}
	/**
	 * this method is call at the begining of the raven phase, if the player have Raven the display change
	 */
	public void crowChoice(){
		if(model.haveRaven(family.getPlayer())){
			panel=DISPLAY_CROW_CHOICE;
		}
		super.repaint();
	}
	
	private void ravenSeeWildings(){
		model.setPhase(ModelState.RAVEN_SEE_WILDINGS);
		wilidingsCard=model.choseWildingCard();
		panel=DISPLAY_RAVEN_SEE_WILDINGS;
	}
	/**
	 * Said if the ship is selected for mustering, during the mustering phase 
	 * @return true a the ship as been selected for mustering
	 */
	public Boolean getShipSelected(){
		return shipRecrutement;
	}
	public void setShipSelected(boolean shipSelected){
		shipRecrutement=shipSelected;
	}
	
	/**
	 * Return the family play by the player who use this playerChoice
	 * @return the family play by the player who use this playerChoice
	 */
	public Family getFamily(){
		return this.family;
	}
	
	// This constant are use to know the state of this component 
	private static final int DISPLAY_BLANK = 0;
	private static final int DISPLAY_ORDERS = 1;
	private static final int DISPLAY_END_PROGRAMATION = 2;
	private static final int DISPLAY_CANCEL=3;
	private static final int DISPLAY_WATER_MOV = 4;
	private static final int DISPLAY_LAND_MOV = 5;
	private static final int DISPLAY_WATER_ATT = 6;
	private static final int DISPLAY_LAND_ATT = 7;
	private static final int DISPLAY_SUPPORT_CHOICE=8;
	private static final int DISPLAY_HOUSE_CARDS=9;
	private static final int DISPLAY_VALYRIAN_SWORD=10;
	private static final int DISPLAY_RECRUIT_OR_CONSOLID = 11;
	public static final int DISPLAY_RECRUITEMENT = 12;
	private static final int DISPLAY_WESTEROS_CARD=13;
	private static final int DISPLAY_BID=14;
	private static final int DISPLAY_LETTERS=15;
	private static final int DISPLAY_BID_SORT=16;
	private static final int DISPLAY_WILDINGS_CARD=17;
	public static final int DISPLAY_TROOP_DESTRUCTION=18;
	private static final int DISPLAY_USE_INF_TOKEN=19;
	private static final int DISPLAY_CROW_CHOICE=20;
	private static final int DISPLAY_CHANGE_ORDER=21;
	private static final int DISPLAY_RAVEN_SEE_WILDINGS=22;
	public static final int DISPLAY_DISCARD_HOUSE_CARDS=23;
}


