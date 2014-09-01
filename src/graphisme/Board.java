package graphisme;

import java.awt.Graphics;
import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

import org.jogre.client.awt.GameImages;
import org.jogre.gameOfThrones.common.GameOfThronesModel;
import org.jogre.gameOfThrones.common.orders.Order;
import org.jogre.gameOfThrones.common.orders.OrderType;
import org.jogre.gameOfThrones.common.territory.BoardModel;
import org.jogre.gameOfThrones.common.territory.Territory;
import org.jogre.gameOfThrones.common.territory.Water;

/**
 * Classe qui gere la gestion du plateau et la partie qu'on doit afficher
 * (donc aussi les troupes etc..)
 * @author robin
 *
 */
public class Board {
	private Image board;
	private int x; // les coordonnée où est affichée la carte 
	private int y;
	private BoardModel boardModel;
	private GameOfThronesModel gameModel;
	/** la premiere coordoné est pour la famille, la seconde pour le type (terrestre ou naval)*/
	private Image[][] troopsImages;
	private Image[] ordersImage;
	//les coordonnée des territoires sont rangée dans une hash table
	private Map<String,int[]> territoryCoord;
	
	public Board (GameOfThronesModel model){
		board = GameImages.getImage(1);
		x=0;
		y=0;
		this.gameModel=model;
		this.boardModel=model.getBoardModel();
		//construit les coordonées des territoires
		coordinate();
		//images des troupes
		troopsImages= new Image[6][2];
		for (int i =0; i<2;i++){
			for(int y=0;y<6;y++){
				troopsImages[y][i]= GameImages.getImage(i+y*2+30);
			}
		}
		//image ordres
		ordersImage = new Image[11];
		for (int i=0;i<11;i++){
			ordersImage[i]=GameImages.getImage(8+i);
		}
		
	}
	
	public void afficher (Graphics g){
		// affiche l'image principale
		g.drawImage(board,x,y, null);
		//affiche les troupes et les symboles influence presentent sur les territoires
		for (Territory territory : boardModel.board.values()){
			if (territory.getTroup()!=null){
				showTroops(territory, g);
			}//maintenant on test si il y a un pion influence
		}
		//on verifie si c'est la phase 2, dans ce cas on affiche les ordres
		if(gameModel.getPhase()==2){
			this.showOrders(g);
		}
	}
	
	public void down(){
		if(y>(-980)){//ou 950
			y-=5;
		}
	}
	public void up(){
		if(y<0){
			y+=5;
		}
	}
	
	//fonction qui renvoit les infos d'une region
	/*public String getTeritoryInfo(int xMouse, int yMouse){
		return getTerritory(xMouse,yMouse).toString();
	}*/
	
	
	// fonction qui renvoit une region en fonction des coordoné de la souri
	public Territory getTeritory(int xMouse, int yMouse){
		if (xMouse<155 && yMouse-y<120){
			return boardModel.getTerritory("Bay Of Ice"); 	
		}else if(xMouse>400 && xMouse<540 && yMouse-y>20 && yMouse-y<50){
			return boardModel.getTerritory("Castle Black");
		}else if(xMouse>255 && xMouse<370 && yMouse-y>245 && yMouse-y<271){
			return boardModel.getTerritory("Winterfell");
		}else if(xMouse>535 && xMouse<623 && yMouse-y>114 && yMouse-y<136){
			return boardModel.getTerritory("Karhold");
		}else if (xMouse>455 && xMouse<520 && yMouse-y>290 && yMouse-y<340){
			return  boardModel.getTerritory("White Harbor");
		}else if (xMouse>619 && xMouse<781 && yMouse-y>269 && yMouse-y<315){
			return  boardModel.getTerritory("Shivering Sea");
		}
		else{return null;}
	}
	
	/**
	 * Affiche les troupes d'un territoir,
	 * on a verifier qu'il y avait bien des troupes sur le territoir
	 * @param territory
	 * @param g
	 */
	private void showTroops(Territory territory , Graphics g){
		int fam=territory.getFamily().getPlayer();
		int typ=0;
		// recupere les coordonée dans territoryCoord
		int[] coordinate=territoryCoord.get(territory.getName());
		if (territory instanceof Water){
			typ++;
		}
		g.drawImage(troopsImages[fam][typ],coordinate[0]+x,coordinate[1]+y, null);
	}
	/*on affiche tous les ordres pour les troupes*/
	public void showOrders(Graphics g){
		for (Territory territory : boardModel.board.values()){
			if (territory.getOrder()!=null){
				// on choisit la bonne image
				Image orderImage= orderImage(territory.getOrder());
				// recupere les coordonée dans territoryCoord
				int[] coordinate=territoryCoord.get(territory.getName());
				g.drawImage(orderImage,coordinate[0]+x-30,coordinate[1]+y, null);
			}
		}
	}
	//appelé dans le constructeur uniquement pour la creation des coordonnées
	private void coordinate(){
		territoryCoord = new HashMap<String,int[]>();
		int[] winterfell ={370,271};
		int[] karhold ={623,136};
		int[] whiteHarbor = {460,340};
		int[] shiveringSea ={674,320};
		
		territoryCoord.put("Winterfell", winterfell);
		territoryCoord.put("White Harbor", whiteHarbor);
		territoryCoord.put("Shivering Sea", shiveringSea);
		territoryCoord.put("Karhold", karhold);
	}
	
	/*Meme methode que dans le player choice, creer une class pour la getion des image */
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
		return ordersImage[res];
	}
}
