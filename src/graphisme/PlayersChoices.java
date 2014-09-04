package graphisme;

import java.awt.Graphics;
import java.awt.Image;
import java.util.List;

import javax.swing.JLabel;

import org.jogre.client.awt.GameImages;
import org.jogre.client.awt.JogreComponent;
import org.jogre.client.awt.JogrePanel;
import org.jogre.gameOfThrones.common.Family;
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
	//5 pour terrestre. 6 et 7 pour les combats, 8 pour le support
	private int panel;
	/*the territory related to the current choice, null if none*/
	Territory relatedTerr;
	/*the cible territory when the player choice need 2 territory*/ 
	Territory cibleTerr;
	
	public PlayersChoices (JLabel label){
		this.label=label;
		panel=0;
		relatedTerr =null;
		cibleTerr = null;
		//on ajoute les images qui seront utitles
		endTurnImage = GameImages.getImage(6);
		dontUseImage = GameImages.getImage(7);
		attackerImage= GameImages.getImage(4);
		defencerImage= GameImages.getImage(5);
		noOneImage= GameImages.getImage(3);
		images= ImageSelector.IMAGESELECTOR;
	}
	
//c'est la methode appelé quand on click gauche dans le playerChoice
	//renvoit un int code qui dit au controlleur ce qui s'est passé
	// 1 : le joueur a fini sa programmation
	// 2 : le joueur a retirer un ordre
	// 3 à 6 envoi de troups
	public int RigthClick(int x, int y, Family family) {
		switch (panel) {
		case 1 :
			family.giveOrders(relatedTerr,choseOrder(x,y,family));
			this.blank();
			// on regarde si tout ses teritoires on des ordres!!!
			if(family.allOrdersGived()){
				endProgramation();
			}
		return 0;
		case 2 :// fin de phase de prog
			if (x>150 && x<200 && y>50 && y<100){
				family.ordersGived=true; 
				blank();
				return 1;
			}
			break;
		case 3 :
			if (x>150 && x<200 && y>50 && y<100){
				return 2;// on informe le server et on met à jour le board
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
				return 12;
			}else if(x>250 && x<300 && y>50 && y<100){
				return 13;
			}else if(x>250 && x<300 && y>175 && y<225){
				return 14;
			}
		}
		return 0;
	}
	
	
	/* panel =1 c'est à dire phase d'ordre*/
	
	//Quand on click dans une zone renvoit l'objet en relation
	public Order choseOrder(int x, int y,Family family){
		int i=(x-10)/80+(y/80)*6;
		return family.getOrders().get(i);
	}
	
	public void showOrders(Family family, Territory terr) {//necessite de connaitre les ordres dispo
		relatedTerr=terr;
		label.setText("Give order in "+terr.getName());
		panel=1;
		int x =0;
		int y =0;
		List<Order> orders =family.getOrders();
		for (Order order : orders){
			getGraphics().drawImage(images.getOrderImage(order), (10+x*80), (10+y*80), null);
			x++;
			if(x==6){
				y++;
				x=0;
			}
		}
	}
	
	
	public void paintComponent (Graphics g) {
		super.paintComponent (g);
		
	}
	public void blank() {
		getGraphics().clearRect(0, 0, 600, 250);
		relatedTerr=null;//vraiment utile ?
		panel=0;
		label.setText("");
	}

	/**
	 * Affiche une fenetre qui demande la confimation de fin de phase de programmation
	 */
	public void endProgramation(){
		label.setText("You have gived all your orders, do you want to end your turn ?");
		panel=2;
		getGraphics().drawImage(endTurnImage,150,50, null);
	}
	/*On indique l'ordre qu'on veut utiliser  */
	public void orderSelected(Territory territory) {
		relatedTerr=territory;
		panel=3;
		label.setText(territory.getName()+" gonna "+territory.getOrder().getType());
		//On affiche une image qui permet d'annuler l'ordre
		getGraphics().clearRect(0, 0, 600, 250);
		getGraphics().drawImage(dontUseImage,150,50, null);
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
		Image[] troopsImages=images.getTroopImages(relatedTerr.getFamily());
		if(territory instanceof Water){
			panel=4;
			getGraphics().drawImage(troopsImages[0],150,50, null);
		}else{
			panel=5;
			getGraphics().drawImage(troopsImages[1],50,50, null);
			getGraphics().drawImage(troopsImages[2],150,50, null);
			getGraphics().drawImage(troopsImages[3],250,50, null);
			}
	}

	public void checkPlayerChoices() {
		if(relatedTerr.getOrder()==null){
			this.blank();
		}
		
	}

	public void attackTo(Territory territory) {
		getGraphics().clearRect(0, 0, 600, 250);
		cibleTerr = territory;
		label.setText("wich troops do want to send from "+relatedTerr.getName()+" to attack "+cibleTerr.getName());
		Image[] troopsImages=images.getTroopImages(relatedTerr.getFamily());
		if(territory instanceof Water){
			panel=6;
			getGraphics().drawImage(troopsImages[0],150,50, null);
		}else{
			panel=7;
			getGraphics().drawImage(troopsImages[1],50,50, null);
			getGraphics().drawImage(troopsImages[2],150,50, null);
			getGraphics().drawImage(troopsImages[3],250,50, null);}
			getGraphics().drawImage(endTurnImage,150,150, null);
	}

	public void support(Territory territory) {
		label.setText("who do you want to support ?");
		panel=8;
		cibleTerr = territory;
		getGraphics().clearRect(0, 0, 600, 250);
		getGraphics().drawImage(attackerImage, 100,50, null);
		getGraphics().drawImage(defencerImage, 250,50, null);
		getGraphics().drawImage(noOneImage, 175,150, null);
		
	}
	
}


