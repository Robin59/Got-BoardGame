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
		}else if(xMouse>337 && xMouse<397 && yMouse-y>488 && yMouse-y<530){
			return  boardModel.getTerritory("Moat Cailin");
		}else if(xMouse>231 && xMouse<307 && yMouse-y>474 && yMouse-y<510){
			return  boardModel.getTerritory("GreyWater Watch");
		}else if(xMouse>152 && xMouse<219 && yMouse-y>488 && yMouse-y<525){
			return  boardModel.getTerritory("Flint's Finger");
		}else if(xMouse>0 && xMouse<100 && yMouse-y>495 && yMouse-y<530){
			return  boardModel.getTerritory("Sunset Sea");
		}else if(xMouse>46 && xMouse<193 && yMouse-y>578 && yMouse-y<598){
			return  boardModel.getTerritory("Ironman's Bay");
		}else if(xMouse>60 && xMouse<133 && yMouse-y>620 && yMouse-y<644){
			return  boardModel.getTerritory("Pike");
		}else if(xMouse>225 && xMouse<301 && yMouse-y>583 && yMouse-y<600){
			return  boardModel.getTerritory("Seaguard");
		}else if(xMouse>348 && xMouse<442 && yMouse-y>607 && yMouse-y<627){
			return  boardModel.getTerritory("The Twins");
		}else if(xMouse>475 && xMouse<595 && yMouse-y>574 && yMouse-y<592){
			return  boardModel.getTerritory("The Fingers");
		}else if(xMouse>432 && xMouse<625 && yMouse-y>646 && yMouse-y<687){
			return  boardModel.getTerritory("The Mountains of the Moon");
		}else if(xMouse>560 && xMouse<661 && yMouse-y>739 && yMouse-y<757){
			return  boardModel.getTerritory("The Eyrie");
		}else if(xMouse>274 && xMouse<371 && yMouse-y>740 && yMouse-y<757){
			return  boardModel.getTerritory("Riverrun");
		}else if(xMouse>152 && xMouse<265 && yMouse-y>765 && yMouse-y<790){
				return  boardModel.getTerritory("Lannisport");
		}else if(xMouse>28 && xMouse<95 && yMouse-y>868 && yMouse-y<914){
			return  boardModel.getTerritory("The Golden Sound");
		}else{return null;}
	}
	
	/**
	 * Affiche les troupes d'un territoir,
	 * on a verifié qu'il y avait bien des troupes sur le territoir
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
		int[] moatCailin={354,536};
		int[] greyWater={268,515};
		int[] flintFiger={182,521};
		int[] sunsetSea={32,524};
		int[] ironManBay={256,679};
		int[] pike={127,681};
		int[] seaguard={300,638};
		int[] twins={398,629};
		int[] fingers={539,592};
		int[] mountainsMoon={462,491};
		int[] eyrie={578,713};
		int[] riverrun={351,726};
		int[] lannisport={209,834};
		int[] goldenSound={78,838};
		
		territoryCoord.put("The Golden Sound", goldenSound);
		territoryCoord.put("Lannisport", lannisport);
		territoryCoord.put("Riverrun", riverrun);
		territoryCoord.put("The Eyrie", eyrie);
		territoryCoord.put("The Moutains of the Moon", mountainsMoon);
		territoryCoord.put("The Fingers", fingers);
		territoryCoord.put("The Twins", twins);
		territoryCoord.put("Pike", pike);
		territoryCoord.put("Seaguard", seaguard);
		territoryCoord.put("Winterfell", winterfell);
		territoryCoord.put("White Harbor", whiteHarbor);
		territoryCoord.put("Shivering Sea", shiveringSea);
		territoryCoord.put("Karhold", karhold);
		territoryCoord.put("Widow's Watch",widowsWatch);
		territoryCoord.put("The Stony Shore",stonyShore);
		territoryCoord.put("Narrow Sea", narrowSea);
		territoryCoord.put("Castle Black", castelBlack);
		territoryCoord.put("Moat Cailin", moatCailin);
		territoryCoord.put("GreyWater Watch", greyWater);
		territoryCoord.put("Flint's Finger", flintFiger);
		territoryCoord.put("Sunset Sea", sunsetSea);
		territoryCoord.put("Ironman's Bay", ironManBay);
		
	
	}
	
}
