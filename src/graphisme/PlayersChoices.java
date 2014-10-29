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
import org.jogre.gameOfThrones.common.combat.BattlePvP;
import org.jogre.gameOfThrones.common.orders.*;
import org.jogre.gameOfThrones.common.territory.Territory;
import org.jogre.gameOfThrones.common.territory.Water;


public class PlayersChoices extends JogreComponent {

	
	private Image endTurnImage;
	private Image dontUseImage;
	private Image attackerImage;
	private Image defencerImage;
	private Image noOneImage;
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
	BattlePvP battle;
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
	
	
	
	public PlayersChoices (JLabel label){
		this.label=label;
		panel=0;
		relatedTerr =null;
		cibleTerr = null;
		battle = null;
		selectedBid=-1;
		//on ajoute les images qui seront utitles
		selectionBid= GameImages.getImage(127);
		endTurnImage = GameImages.getImage(6);
		dontUseImage = GameImages.getImage(7);
		attackerImage= GameImages.getImage(4);
		defencerImage= GameImages.getImage(5);
		leftArrowImage=GameImages.getImage(134);
		rigthArrowImage=GameImages.getImage(135);		
		noOneImage= GameImages.getImage(3);
		images= ImageSelector.IMAGESELECTOR;
		swordImage =GameImages.getImage(108);
		swordFlipImage =GameImages.getImage(109);
		recruitImage= GameImages.getImage(110);
		consoImage= GameImages.getImage(3);
		letters=new Image[3];
		letters[0]=GameImages.getImage(136);
		letters[1]=GameImages.getImage(137);
		letters[2]=GameImages.getImage(138);
	}
	
//c'est la methode appelé quand on click gauche dans le playerChoice
	//renvoit un int code qui dit au controlleur ce qui s'est passé
	// 1 : le joueur a fini sa programmation
	// 2 : le joueur a retirer un ordre
	// 3 à 6 envoi de troups
	public int RigthClick(int x, int y, Family family) {
		switch (panel) {
		case 1 :
			if(family.giveOrders(relatedTerr,choseOrder(x,y,family))){
				this.blank();
				// on regarde si tout ses teritoires on des ordres!!!
				if(family.allOrdersGived()){
					endProgramation();
				}
			}
		break;
		case 2 :// fin de phase de prog
			if (x>150 && x<200 && y>50 && y<100){
				family.ordersGived=true; 
				blank();
				return 1;
			}
			break;
		case 3 :
			if (x>150 && x<200 && y>50 && y<100){
				return 2;
			}
			break;
		//les mouvements
		case 4 :
			if (x>150 && x<200 && y>50 && y<100){ // troop navals
				return 3;
			}
			break;
		case 5 :
			if (x>50 && x<100 && y>50 && y<100 && relatedTerr.getTroup().getTroops()[1]!=0){
				return 4;
			}else if(x>150 && x<200 && y>50 && y<100 && relatedTerr.getTroup().getTroops()[2]!=0){
				return 5;
			}else if(x>250 && x<300 && y>50 && y<100 && relatedTerr.getTroup().getTroops()[3]!=0){
				return 6;
			}
			break;
		//les combats
		case 6 :
			if (x>150 && x<200 && y>150 && y<200){ 
				return 7;
			}
			break;
		case 7 :
			if (x>150 && x<200 && y>150 && y<200){ 
				return 7;
			}else if (x>50 && x<100 && y>50 && y<100 && relatedTerr.getTroup().getTroops()[1]!=0){
				return 9;
			}else if(x>150 && x<200 && y>50 && y<100 && relatedTerr.getTroup().getTroops()[2]!=0){
				return 10;
			}else if(x>250 && x<300 && y>50 && y<100 && relatedTerr.getTroup().getTroops()[3]!=0){
				return 11;
			}
			break;
		case 8:
			if (x>100 && x<150 && y>50 && y<100){ 
				blank2();
				return 12;
			}else if(x>250 && x<300 && y>50 && y<100){
				blank2();
				return 13;
			}else if(x>250 && x<300 && y>175 && y<225){
				return 14;
			}
			break;
		case 9 :
			if(x<60 && indexHouseCard>0){
				indexHouseCard--;
				repaint();
			}else if(x>510 && indexHouseCard<family.getCombatantCards().size()-1){
				indexHouseCard++;
				repaint();
			}else if (x>60 && x<510){
				choseCard(x, family);
				return 15;
			}
			break;
		case 10 :
			if (x>50 && x<140){
				blank2();
				this.repaint();
				return 16;
			}else if(x>200 && x<300){
				blank2();
				this.repaint();
				return 17;
			}
			break;
		case 11 : 
			if(x>50 && x<130){
				relatedTerr.resetRecruit();
				this.recruit(relatedTerr);
			}else if(x>350 && x<430){
				blank2();
				this.repaint();
				return 18;
			}
			break;
		case 12 :
			if(x>50&&x<100){
				return 19;
			}else if (y<170 && x>150 && x<200){
				return 20;
			}else if (y<170 && x>250 && x<300){
				return 21;
			}else if (y<170 && x>350 && x<400){
				return 22;
			}else if(y>190 && x>200 && x<270){
				return 2;
			}
			break;
		case 13:
			return 23;
		case 14 :
			if(x>100&&x<150 && powerBid>0){
				powerBid--;
				label.setText("Power Bid : "+powerBid);
			}else if(x>400&&x<450 && powerBid<family.getInflu()){
				powerBid++;
				label.setText("Power Bid : "+powerBid);
			}else if(x>240 && x<300){
				return 24; // end of the biddings
			}break;
		case 15:
			if(x>100 && x<170){
				blank();
				return 25;
			}else if(x>190 && x<260){
				blank();
				return 26;
			}else if(x>280 && x<350){
				blank();
				return 27;
			}
		break;
		case 16:
			if(x>170 && x<230 && y>180){
				selectedBid=-1;
				return 28;
			}else if (y<180){
				changeTrack(x);
			}
		break;
		case 17:
			return 29;
		}
		return 0;
	}
	
	
	
	
	
	
	public void paintComponent (Graphics g) { 
		//super.paintComponent (g);
		switch(panel){
		case 0:
			label.setText("");
			break;
		case 1:
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
		break;
		case 2:
			g.drawImage(endTurnImage,150,50, null);
			break;
		case 3:
			g.drawImage(dontUseImage,150,50, null);
			break;
		case 4 :
			g.drawImage(images.getTroopImages(family)[0],150,50, null);
		break;
		case 5 :
			g.drawImage(images.getTroopImages(family)[1],50,50, null);
			g.drawImage(images.getTroopImages(family)[2],150,50, null);
			g.drawImage(images.getTroopImages(family)[3],250,50, null);
			break;
		case 6:
			g.drawImage(images.getTroopImages(family)[0],150,50, null);
			g.drawImage(endTurnImage,150,150, null);
			break;
		case 7 :
			g.drawImage(images.getTroopImages(family)[1],50,50, null);
			g.drawImage(images.getTroopImages(family)[2],150,50, null);
			g.drawImage(images.getTroopImages(family)[3],250,50, null);
			g.drawImage(endTurnImage,150,150, null);
			break;
		case 8:
			g.clearRect(0, 0, 600, 250);
			g.drawImage(attackerImage, 100,50, null);
			g.drawImage(defencerImage, 250,50, null);
			g.drawImage(noOneImage, 175,150, null);
			break;
		case 9:
			g.clearRect(0, 0, 600, 250);
			drawHouseCards(g);
			break;
		case 10:
			g.drawImage(swordImage, 50, 5, null);
			g.drawImage(swordFlipImage, 200, 5, null);
			break;
		case 11:
			g.drawImage(recruitImage, 50, 100, null);
			g.drawImage(consoImage, 350, 100, null);
			break;
		case 12:
			drawRecruit(g);
			g.drawImage(dontUseImage, 200,190,null);
			break;
		case 13:
			g.drawImage(images.getWestCardImage(westerosCard), 50,0, null);
			break;
		case 14 : 
			g.drawImage(leftArrowImage,100,100,null);
			g.drawImage(rigthArrowImage,400,100,null);
			g.drawImage(images.getPowerImage(family),240,100, null);
			break;
		case 15:
			g.drawImage(letters[0],100,100,null);
			g.drawImage(letters[1],190,100,null);
			g.drawImage(letters[2],280,100,null);
			break;
		case 16:
			drawBiddingTable(g);
			break;
		case 17:
			g.drawImage(images.getWildingCardImage(wilidingsCard),100,0,null);
			break;
		}
	}
	
	/**Draw the correct troop regarding the possibility and the family*/
	private void drawRecruit(Graphics g){
		if(relatedTerr.getRecruit()>0){
			//g.drawImage(images.getTroopImages(family)[0],50,100,null);
			g.drawImage(images.getTroopImages(family)[1],150,100,null);
			if(relatedTerr.getRecruit()>1 || (relatedTerr.getTroup()!=null && relatedTerr.getTroup().getTroops()[1]>0)){
				g.drawImage(images.getTroopImages(family)[2],250,100,null);
				g.drawImage(images.getTroopImages(family)[3],350,100,null);
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
	
	/**Draw for the player with the throne, the different bid if there is an equality*/
	private void drawBiddingTable(Graphics g){
		int i = 10;
		for(Family family : bidding.getTrack()){
			g.drawImage(images.getPowerImage(family),i,100,null);
			//System.out.println(family.getBid());
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
	 * 
	 * @param x
	 * @param family
	 */
	public void choseCard(int x, Family family){
		if (((x-60)/150)+indexHouseCard<family.getCombatantCards().size()){
			selectedHouseCard =((x-60)/150)+indexHouseCard;
			CombatantCard card =family.getCombatantCards().get(selectedHouseCard);
			battle.playCard(card, family);
			panel=0;
			this.repaint();
		}
	}
		
		public void showOrders(Family family, Territory terr) {
			relatedTerr=terr;
			this.family=family;
			label.setText("Give order in "+terr.getName());
			panel=1;
			this.repaint();
		}
	
	/**
	 * Show the recruit panel when a player click on a territory where he can recruit
	 * @param territory where we can recruit 
	 */
	public void recruit(Territory territory){
		relatedTerr=territory;
		panel=12;
		repaint();
	}
		
	public void blank() {
		relatedTerr=null;//vraiment utile ?
		cibleTerr=null;
		//westerosCard=null;
		wilidingsCard=null;
		indexHouseCard=0;
		panel=0;
		label.setText("");
		repaint();
	}
	
	public void blank2() {
		//getGraphics().clearRect(0, 0, 600, 250);
		//relatedTerr=null;//vraiment utile ?
		panel=0;
		label.setText("");
		repaint();
	}

	/**
	 * Affiche une fenetre qui demande la confimation de fin de phase de programmation
	 */
	public void endProgramation(){
		label.setText("You have gived all your orders, do you want to end your turn ?");
		panel=2;
		repaint();
	}
	/*On indique l'ordre qu'on veut utiliser  */
	public void orderSelected(Territory territory) {
		if(territory.getOrder().getType()==OrderType.CON){
			panel=11;
			label.setText("Do you want to recruit or to consolidate your power");
		}else{
			panel=3;
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
			panel=4;
		}else{
			panel=5;
			}
		this.repaint();
	}

	public void checkPlayerChoices() {
		if(relatedTerr.getOrder()==null){
			this.blank();
		}
		
	}

	public void attackTo(Territory territory) {
		getGraphics().clearRect(0, 0, 600, 250);
		cibleTerr = territory;
		if(territory instanceof Water){
			panel=6;
		}else{
			panel=7;
		}
		this.repaint();
	}

	public void support(Territory territory) {
		label.setText("who do you want to support ?");
		panel=8;
		relatedTerr = territory;
	}
	
	// 1 indique cartes, 2 pour jouer l'épée, 3 retraite, 4 fin de combat
	public int check(int modelState, Family family, BattlePvP battle){
		if(modelState==1 && battle.canPlayCard(family) ){//on verifie si on peut afficher les cartes 
			//getGraphics().clearRect(0, 0, 600, 250);
			showHouseCards(battle);
			return 1;
		}else if(modelState==2){
			swordPlay(family);
			return 2;
		}else if(modelState==3 && family==battle.getDefFamily()){	
			label.setText("Choose a place to withdraw");
			return 3;
		}else if(modelState==4){
			return 4;
		}
		return 0;
	}
	/**
	 * This method set all the parameters to show the house Cards
	 * @param battle
	 */
	public void showHouseCards(BattlePvP battle){
		this.battle=battle;
		selectedHouseCard=-1;
		panel=9;
		repaint();
	}
	
	/**
	 * 
	 * @return
	 */
	public int getIndexCard(){
		return selectedHouseCard;
	}
	
	public void swordPlay(Family family){
		if(family.canUseSword()){
			panel=10;
			repaint();
		}else{
			panel=0;
			repaint();
		}
	}
	
	
	public void wilidingsCard(String card) {
		panel=17;
		wilidingsCard=card;
		repaint();
	}
	
	public void westerosCard(String card) {
		panel=13;
		westerosCard=card;
		repaint();
		
	}

	public void bidding() {
		powerBid=0;
		label.setText("Power Bid : "+powerBid);
		panel=14;	
	}
	
	public int getBid(){
		return powerBid;
	}
	/**Happend when a player have to make a ternair choice because of a westeros card (like dark Wings dark words, put to the sword or throne of blades)*/
	public void westerosCardChoice(){
		panel=15;
		repaint();
	}
	
	public String getWildingsCard(){
		return wilidingsCard;
	}
	
	public int getPanel(){
		return panel;
	}

	/**when there is a bidding equality (the player with the throne can set the players the way he want)
	 * @param bidding the bid that the player can arrange
	 * */
	public void biddingEgality(Bidding bidding){
		panel=16;
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
	
}


