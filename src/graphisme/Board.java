package graphisme;

import java.awt.Graphics;
import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

import org.jogre.client.awt.GameImages;
import org.jogre.gameOfThrones.common.GameOfThronesModel;
import org.jogre.gameOfThrones.common.combat.Troop;
import org.jogre.gameOfThrones.common.orders.Order;
import org.jogre.gameOfThrones.common.orders.OrderType;
import org.jogre.gameOfThrones.common.territory.BoardModel;
import org.jogre.gameOfThrones.common.territory.Territory;
import org.jogre.gameOfThrones.common.territory.Water;

import state.ModelState;

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
	/*coordinate for the troops on the board*/
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
		
		for (Territory territory : boardModel.board.values()){
			//display the garrisons
			if(territory.haveGarrison()){
				displayGarrison(territory, g);
			}
			//display the neutral forces
			if(territory.getNeutralForce()>0){
				displayNeutralForce(territory,g);
			}
			//affiche les troupes et les symboles influence presentent sur les territoires
			if (territory.getTroup()!=null){
				showTroops(territory, g);
			}//maintenant on test si il y a un pion influence
		}
			this.showOrders(g);
	}
	
	public void down(){
		if(y>(-980)){//ou 950
			y-=15;
		}
	}
	public void up(){
		if(y<0){
			y+=15;
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
		}else if(xMouse>699 && xMouse<795 && yMouse-y>800 && yMouse-y<821){
			return  boardModel.getTerritory("Dragonstone");
		}else if(xMouse>381 && xMouse<468 && yMouse-y>803 && yMouse-y<824){
			return  boardModel.getTerritory("Harrenhal");
		}else if(xMouse>269 && xMouse<379 && yMouse-y>834 && yMouse-y<857){
			return  boardModel.getTerritory("Stoney Sept");
		}else if(xMouse>479 && xMouse<557 && yMouse-y>849 && yMouse-y<883){
			return  boardModel.getTerritory("Crackclaw Point");
		}else if(xMouse>163 && xMouse<233 && yMouse-y>921 && yMouse-y<954){
			return  boardModel.getTerritory("Searoad Marches");
		}else if(xMouse>296 && xMouse<414 && yMouse-y>942 && yMouse-y<966){
			return  boardModel.getTerritory("Blackwater");
		}else if(xMouse>568 && xMouse<627 && yMouse-y>898 && yMouse-y<933){
			return  boardModel.getTerritory("Blackwater Bay");
		}else if(xMouse>566 && xMouse<656 && yMouse-y>998 && yMouse-y<1018){
			return  boardModel.getTerritory("Kingswood");
		}else if(xMouse>676 && xMouse<755 && yMouse-y>997 && yMouse-y<1040){
			return  boardModel.getTerritory("Shipbreaker Bay");
		}else if(xMouse>328 && xMouse<447 && yMouse-y>1066 && yMouse-y<1090){
			return  boardModel.getTerritory("The Reach");
		}else if(xMouse>539 && xMouse<639 && yMouse-y>1139 && yMouse-y<1157){
			return  boardModel.getTerritory("Storm's End");
		}else if(xMouse>101 && xMouse<223 && yMouse-y>1045 && yMouse-y<1068){
			return  boardModel.getTerritory("Highgarden");
		}else if(xMouse>469 && xMouse<584 && yMouse-y>954 && yMouse-y<980){
			return  boardModel.getTerritory("King's Landing");
		}else if(xMouse>142 && xMouse<224 && yMouse-y>1183 && yMouse-y<1204){
			return  boardModel.getTerritory("Oldtown");
		}else if(xMouse>276 && xMouse<404 && yMouse-y>1126 && yMouse-y<1148){
			return  boardModel.getTerritory("Dornish Marches");
		}else if(xMouse>493 && xMouse<621 && yMouse-y>1212 && yMouse-y<1237){
			return  boardModel.getTerritory("Sea of Dorne");
		}else if(xMouse>435 && xMouse<521 && yMouse-y>1126 && yMouse-y<1163){
			return  boardModel.getTerritory("The Boneway");
		}else if(xMouse>561 && xMouse<661 && yMouse-y>1271 && yMouse-y<1299){
			return  boardModel.getTerritory("Sunspear");
		}else if(xMouse>54 && xMouse<126 && yMouse-y>1297 && yMouse-y<1339){
			return  boardModel.getTerritory("Redwyne Straights");
		}else if(xMouse>54 && xMouse<108 && yMouse-y>1354 && yMouse-y<1395){
			return  boardModel.getTerritory("The Arbor");
		}else if(xMouse>199 && xMouse<294 && yMouse-y>1247 && yMouse-y<1267){
			return  boardModel.getTerritory("Three Towers");
		}else if(xMouse>280 && xMouse<378 && yMouse-y>1189 && yMouse-y<1207){
			return  boardModel.getTerritory("Prince's Pass");
		}else if(xMouse>276 && xMouse<354 && yMouse-y>1351 && yMouse-y<1372){
			return  boardModel.getTerritory("Starfall");
		}else if(xMouse>354 && xMouse<431 && yMouse-y>1276 && yMouse-y<1296){
			return  boardModel.getTerritory("Yronwood");
		}else if(xMouse>471 && xMouse<571 && yMouse-y>1324 && yMouse-y<1345){
			return  boardModel.getTerritory("Salt Shore");
		}else if(xMouse>137 && xMouse<366 && yMouse-y>1433 && yMouse-y<1457){
			return  boardModel.getTerritory("West Summer Sea");
		}else if(xMouse>484 && xMouse<696 && yMouse-y>1433 && yMouse-y<1457){
			return  boardModel.getTerritory("East Summer Sea");
		}else if(xMouse>199 && xMouse<240 && yMouse-y>145 && yMouse-y<195){// now the ports
			return  boardModel.getTerritory("Winterfell's Port");
		}else if(xMouse>469 && xMouse<512 && yMouse-y>445 && yMouse-y<495){
			return  boardModel.getTerritory("White Harbor's Port");
		}else if(xMouse>166 && xMouse<213 && yMouse-y>607 && yMouse-y<652){
			return  boardModel.getTerritory("Pyke's Port");
		}else if(xMouse>724 && xMouse<770 && yMouse-y>903 && yMouse-y<945){
			return  boardModel.getTerritory("Dragonstone's Port");
		}else if(xMouse>134 && xMouse<174 && yMouse-y>807 && yMouse-y<849){
			return  boardModel.getTerritory("Lannisport's Port");
		}else if(xMouse>611 && xMouse<650 && yMouse-y>1095 && yMouse-y<1127){
			return  boardModel.getTerritory("Storm's End's Port");
		}else if(xMouse>85 && xMouse<125 && yMouse-y>1168 && yMouse-y<1205){
			return  boardModel.getTerritory("Oldtown's Port");
		}else if(xMouse>704 && xMouse<740 && yMouse-y>1298 && yMouse-y<1336){
			return  boardModel.getTerritory("Sunspear's Port");
		}else{return null;}
	}
	
	/**
	 * Display the troops on the given territory
	 * but first you must to be sure that there is some troops on it
	 * @param territory territory with troop on it
	 * @param g
	 */
	private void showTroops(Territory territory , Graphics g){
		// recupere les coordonée dans territoryCoord
		int[] coordinate=territoryCoord.get(territory.getName());
		Image[] troopsImage=images.getSmallTroopsImage(territory.getFamily());
		if(territory instanceof Water){
			Image strength=images.getSmallNumber(territory.getTroup().getEffectif());
			g.drawImage(troopsImage[0],coordinate[0]+x,coordinate[1]+y, null);
			g.drawImage(strength,coordinate[0]+x+15,coordinate[1]+y+13, null);
		}else{
			int[] troops= territory.getTroup().getTroops();
			int i=0;
			if(troops[1]>0){
				g.drawImage(troopsImage[1],coordinate[0]+x,coordinate[1]+y, null);
				g.drawImage(images.getSmallNumber(troops[1]),coordinate[0]+x+10,coordinate[1]+y+15, null);
				i++;
			}
			if(troops[2]>0){
				g.drawImage(troopsImage[2],coordinate[i*2]+x,coordinate[i*2+1]+y, null);
				g.drawImage(images.getSmallNumber(troops[2]),coordinate[i*2]+x+10,coordinate[i*2+1]+y+15, null);
				i++;
			}
			if(troops[3]>0){
				g.drawImage(troopsImage[3],coordinate[i*2]+x,coordinate[i*2+1]+y, null);
				g.drawImage(images.getSmallNumber(troops[3]),coordinate[i*2]+x+10,coordinate[i*2+1]+y+15, null);
			}
			
		}
	}
	
	
	/**display on the boards the orders that the player can see (is own during the programation's phase, all during the execution)*/
	public void showOrders(Graphics g){
		for (Territory territory : boardModel.board.values()){
			if (territory.getOrder()!=null ){
				if(territory.getFamily().getPlayer()==player || gameModel.getPhase()==ModelState.PHASE_EXECUTION){
					// on choisit la bonne image
					Image orderImage= images.getSmallOrderImage(territory.getOrder());
					// 	recupere les coordonée dans territoryCoord
					int[] coordinate=territoryCoord.get(territory.getName());
					g.drawImage(orderImage,coordinate[0]+x-30,coordinate[1]+y, null);
				}
			}
		}
	}
	/**
	 * This method display the neutral force on the board 
	 * @param territory on which we're going to display the neutral force
	 * @param g
	 */
	private void displayNeutralForce(Territory territory,Graphics g){
		g.drawImage(images.getNeutralForceImage(territory.getNeutralForce()),territoryCoord.get(territory.getName())[6],territoryCoord.get(territory.getName())[7]+y,null);
	}
	
	/**
	 * display the garrison on the correct emplacement   
	 * @param territory
	 * @param g
	 */
	private void displayGarrison(Territory territory,Graphics g){
		g.drawImage(images.getGarrisonImage(territory.getName()),territoryCoord.get(territory.getName())[6],territoryCoord.get(territory.getName())[7]+y,null);
	}
	
	
	//appelé dans le constructeur uniquement pour la creation des coordonnées
	private void coordinate(){
		territoryCoord = new HashMap<String,int[]>();
		// for Water there is 2 coordinate and for Land 6 (2*potential different kind of troop) 
		int[] winterfell ={330,271,360,270,390,270,303,204};
		int[] lannisport={190,800,220,800,250,800,190,837};
		int[] pyke={127,681,157,681,187,681,69,645};
		int[] sunspear={618,1297,648,1297,678,1297,661,1260};
		int[] dragonstone={715,829,745,828,775,828,690,828};
		int[] highgarden={214,1077,244,1077,274,1077,204,1121};
		
		int[] karhold ={623,136,653,136,683,136,683,136};
		int[] whiteHarbor = {460,340,471,376,508,406,508,406};
		int[] widowsWatch ={554,369,584,369,614,369,584,369};
		int[] stonyShore ={222,343,252,343,282,343,282,343};
		int[] castelBlack={460,45,490,45,520,45,490,45};
		int[] moatCailin={354,536,384,536,414,536,414,536};
		int[] greyWater={268,515,298,515,328,515,328,515};
		int[] flintFiger={182,521,212,521,242,521,212,521};
		int[] seaguard={300,638,330,638,360,638,330,638};
		int[] twins={398,629,228,629,258,629,228,629};
		int[] fingers={539,592,569,592,599,592,569,592};
		int[] mountainsMoon={462,687,492,687,522,687,492,687};
		int[] eyrie={578,713,608,713,638,713,600,757};
		int[] riverrun={351,726,381,726,411,726,381,726};
		int[] harrenhale={431,829,461,829,491,829,461,829};
		int[] stoneySept={313,860,343,860,373,860,343,860};
		int[] crackClaw={524,826,554,826,584,826,554,826};
		int[] searoad={194,956,224,956,254,956,224,956};
		int[] blackwater={355,970,385,970,415,970,385,970};
		int[] kingsWood={547,1025,507,1055,537,1055,507,1055};
		int[] reach={384,1042,414,1042,444,1042,414,1042};
		int[] stormeEnd={557,1104,587,1104,617,1104,607,1154};
		int[] kingsLanding={509,983,539,983,569,983,479,920};
		int[] oldtown={173,1206,203,1206,233,1206,143,1206};
		int[] dornishMarches={299,1150,329,1150,359,1150,329,1150};
		int[] boneway={415,1169,445,1169,475,1169,445,1169};
		int[] arbor={69,1400,99,1400,129,1400,99,1400};
		int[] threeTowers={236,1272,236,1272,266,1272,236,1272};
		int[] princePass={320,1212,320,1212,350,1212,320,1212};
		int[] starfall={320,1328,320,1328,350,1328,320,1328};
		int[] yronwood={457,1279,457,1279,487,1279,457,1279};
		int[] saltShore={448,1341,448,1341,478,1341,448,1341};
		//sea
		int[] shiveringSea ={674,320};
		int[] ironManBay={256,679};
		int[] redwyne={62,1248};
		int[] sunsetSea={32,524};
		int[] goldenSound={78,838};
		int[] shipbreaker={728,1050};
		int[] seaOfDorne={529,1238};
		int[] narrowSea ={692,485};
		int[] westSummerSea={46,1066};
		int[] eastSummerSea={706,1388};
		int[] blackwaterBay={605,874};
		int[] bayOfIce={64,176};
		//ports
		int[] winterfellPort={221,168};
		int[] whitHarborPort={480,463};
		int[] pykePort={180,614};
		int[] dragonstonePort={746,918};
		int[] stormEndPort={629,1105};
		int[] lannisportPort={154,822};
		int[] oldtownPort={104,1179};
		int[] sunspearport={715,1309};
		
		
		territoryCoord.put("Bay Of Ice", bayOfIce);
		territoryCoord.put("The Boneway", boneway);
		territoryCoord.put("Sunspear", sunspear);
		territoryCoord.put("Redwyne Straights", redwyne);
		territoryCoord.put("The Arbor", arbor);
		territoryCoord.put("Starfall", starfall);
		territoryCoord.put("Three Towers", threeTowers);
		territoryCoord.put("Prince's Pass", princePass);
		territoryCoord.put("Yronwood", yronwood);
		territoryCoord.put("West Summer Sea", westSummerSea);
		territoryCoord.put("East Summer Sea", eastSummerSea);
		territoryCoord.put("Salt Shore", saltShore);
		territoryCoord.put("Sea of Dorne", seaOfDorne);
		territoryCoord.put("Dornish Marches", dornishMarches);
		territoryCoord.put("Oldtown", oldtown);
		territoryCoord.put("Highgarden", highgarden);
		territoryCoord.put("Storm's End", stormeEnd);
		territoryCoord.put("The Reach", reach);
		territoryCoord.put("Shipbreaker Bay", shipbreaker);
		territoryCoord.put("Kingswood", kingsWood);
		territoryCoord.put("King's Landing", kingsLanding);
		territoryCoord.put("Blackwater Bay", blackwaterBay);
		territoryCoord.put("Blackwater", blackwater);
		territoryCoord.put("Searoad Marches", searoad);
		territoryCoord.put("Crackclaw Point", crackClaw);
		territoryCoord.put("Stoney Sept", stoneySept);
		territoryCoord.put("Harrenhal", harrenhale);
		territoryCoord.put("Dragonstone", dragonstone);
		territoryCoord.put("The Golden Sound", goldenSound);
		territoryCoord.put("Lannisport", lannisport);
		territoryCoord.put("Riverrun", riverrun);
		territoryCoord.put("The Eyrie", eyrie);
		territoryCoord.put("The Moutains of the Moon", mountainsMoon);
		territoryCoord.put("The Fingers", fingers);
		territoryCoord.put("The Twins", twins);
		territoryCoord.put("Pyke", pyke);
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
		territoryCoord.put("Winterfell's Port", winterfellPort);
		territoryCoord.put("White Harbor's Port", whitHarborPort);
		territoryCoord.put("Pyke's Port", pykePort);
		territoryCoord.put("Dragonstone's Port", dragonstonePort);
		territoryCoord.put("Lannisport's Port", lannisportPort);
		territoryCoord.put("Storm's End's Port", stormEndPort);
		territoryCoord.put("Oldtown's Port", oldtownPort);
		territoryCoord.put("Sunspear's Port", sunspearport);
		
	
	}
	
}
