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
	private int player;
	private BoardModel boardModel;
	private GameOfThronesModel gameModel;
	/** la premiere coordoné est pour la famille, la seconde pour le type (terrestre ou naval)*/
	private ImageSelector images;
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
		//image ordres et de troupes
		images=ImageSelector.IMAGESELECTOR;
		
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
			this.showOrders(g);
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
	
	/**Set the player who view this board*/
	public void setPlayer(int player){
		this.player=player;
	}
	
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
		}else if (xMouse>529 && xMouse<613 && yMouse-y>336 && yMouse-y<363){
			return  boardModel.getTerritory("Widow's Watch");	
		}else if(xMouse>127 && xMouse<243 && yMouse-y>284 && yMouse-y<319){
			return  boardModel.getTerritory("The Stony Shore");
		}else if(xMouse>645 && xMouse<777 && yMouse-y>430 && yMouse-y<479){
			return  boardModel.getTerritory("Narrow Sea");
		}else{return null;}
	}
	
	/**
	 * Affiche les troupes d'un territoir,
	 * on a verifier qu'il y avait bien des troupes sur le territoir
	 * @param territory
	 * @param g
	 */
	private void showTroops(Territory territory , Graphics g){
		// recupere les coordonée dans territoryCoord
		int[] coordinate=territoryCoord.get(territory.getName());
		g.drawImage(images.getTroopImage(territory.getFamily(), territory),coordinate[0]+x,coordinate[1]+y, null);
	}
	/*on affiche tous les ordres pour les troupes*/
	public void showOrders(Graphics g){
		for (Territory territory : boardModel.board.values()){
			if (territory.getOrder()!=null ){
				if(territory.getFamily().getPlayer()==player || gameModel.getPhase()==2){
					// on choisit la bonne image
					Image orderImage= images.getSmallOrderImage(territory.getOrder());
					// 	recupere les coordonée dans territoryCoord
					int[] coordinate=territoryCoord.get(territory.getName());
					g.drawImage(orderImage,coordinate[0]+x-30,coordinate[1]+y, null);
				}
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
		int[] widowsWatch ={554,369};
		int[] stonyShore ={222,343};
		int[] narrowSea ={692,485};
		int[] castelBlack={460,45};
		//bay of ice
		
		territoryCoord.put("Winterfell", winterfell);
		territoryCoord.put("White Harbor", whiteHarbor);
		territoryCoord.put("Shivering Sea", shiveringSea);
		territoryCoord.put("Karhold", karhold);
		territoryCoord.put("Widow's Watch",widowsWatch);
		territoryCoord.put("The Stony Shore",stonyShore);
		territoryCoord.put("Narrow Sea", narrowSea);
		territoryCoord.put("Castle Black", castelBlack);
	}
	
}
