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


public class PlayersChoices extends JogreComponent {

	private Image[] orders;
	private Image endTurnImage;
	private Image dontUseImage;
	//le label qui affiche les info
	private JLabel label;
	// indique dans quel etat est le PlayersChoices, 0 pour rien d'affiché, 1 pour ordres, 2 pour fin de tour Prog
	// 3 quand un ordre est selectionné (phase de resolution)
	private int panel;
	/*the territory related to the current choice, null if none*/
	Territory relatedTerr;
	
	
	public PlayersChoices (JLabel label){
		this.label=label;
		panel=0;
		relatedTerr =null;
		//on ajoute les images qui seront utitles
		endTurnImage = GameImages.getImage(6);
		dontUseImage = GameImages.getImage(7);
		//les ordres de 19 à 30
		orders = new Image[11];
		for (int i=0;i<11;i++){
			orders[i]=GameImages.getImage(19+i);
		}
	}
	
//c'est la methode appelé quand on click gauche dans le playerChoice
	//renvoit un int code qui dit au controlleur ce qui s'est passé
	// 1 : le joueur a fini sa programmation
	// 2 : le joueur a retirer un ordre
	public int RigthClick(int x, int y, Family family) {
		switch (panel) {
		case 1 :
			family.giveOrders(relatedTerr,choseOrder(x,y,family));
			this.blank();
			// on regarde si tout ses teritoires on des ordres!!!
			if(family.allOrdersGived()){
				endProgramation();
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
				return 2;// on informe le server et on met à jour le board
			}
			break;
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
			getGraphics().drawImage(orderImage(order), (10+x*80), (10+y*80), null);
			x++;
			if(x==6){
				y++;
				x=0;
			}
		}
	}
	/**
	 * 
	 * @param order
	 * @return
	 */
	private Image orderImage(Order order){
		int res=9;
		if(order.getType()==OrderType.CON){
			res=0;
		}else if (order.getType()==OrderType.DEF){
			res=2;
		}else if (order.getType()==OrderType.ATT){
			if (order.getOthBonus()==0){
				res=4;
			}else{res=5;}
		}else if(order.getType()==OrderType.RAI){
			res=7;
		}
		if(order.getStar()){
			res++;
		}
		return orders[res];
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
		getGraphics().drawImage(dontUseImage,150,50, null);
	}

	public Territory getRelatedTerr() {
		return relatedTerr;
	}
	
}


