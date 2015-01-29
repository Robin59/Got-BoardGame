package graphisme;

import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

import org.jogre.client.awt.GameImages;
import org.jogre.gameOfThrones.common.Family;
import org.jogre.gameOfThrones.common.orders.Order;
import org.jogre.gameOfThrones.common.orders.OrderType;
import org.jogre.gameOfThrones.common.territory.Territory;
import org.jogre.gameOfThrones.common.territory.Water;


/**
 * This class the images of the game, its only purpose is to select and send the right images to the other classes.
 * It's a singleton.
 * @author robin
 *
 */
public class  ImageSelector{

	/** the only instance of this class*/
	public final static ImageSelector IMAGESELECTOR=new ImageSelector();
	
	/* small images of the troops, for the board
	 *the first coordinate is family.getPlayer, the second is for the kind of troop (0 for boat, 1 footman, 2 knigth, 3 seige tower)*/
	private Image[][] troopsImages;
	/*bigger images of the troops for the playerChoice*/
	private Image[][] troopsSelectImage;
	private Image[] bigOrdersImages;
	private Image[] smallOrdersImages;
	private Image[] powerImages;//influence token
	private Image[] smallInfluenceTonken;
	private Image[] numberImages;
	private Image[] smallNumberImages;
	/*Image of the garrisons for the board*/
	private Map<String,Image> garrisonImages;
	private Map<String,Image> playerCards;
	private Map<String,Image> westerosCards;
	private Map<String,Image> wildingsCards;
	
	// Images for the neutral forces
	private Image[] neutralForceImages;
	
	
	private ImageSelector (){
		//images des troupes
		troopsImages= new Image[6][4];
		for (int i =0; i<4;i++){
			for(int y=0;y<6;y++){
				troopsImages[y][i]= GameImages.getImage(i+y*4+164);
			}
		}
		//image des troupes mais plus grandes 
		troopsSelectImage =new Image[6][4];
		for (int i =0; i<4;i++){
			for(int y=0;y<6;y++){
				troopsSelectImage[y][i]= GameImages.getImage(i+y*4+42);
			}
		}
		//Images des ordres, grand de 19 à 29 et petit de 8 à 18
		bigOrdersImages = new Image[11];
		for (int i=0;i<11;i++){
			bigOrdersImages[i]=GameImages.getImage(19+i);
		}
		smallOrdersImages = new Image[11];
		for (int i=0;i<11;i++){
			smallOrdersImages[i]=GameImages.getImage(8+i);
		}
		powerImages = new Image[6];
		for (int i=0;i<6;i++){
			powerImages[i]=GameImages.getImage(128+i);
		}
		smallInfluenceTonken = new Image[6];
		for (int i=0;i<6;i++){
			smallInfluenceTonken[i]=GameImages.getImage(203+i);
		}
		numberImages = new Image[6];
		for (int i=0;i<5;i++){
			numberImages[i]=GameImages.getImage(139+i);
		}
		
		smallNumberImages = new Image[4];
		for (int i=0;i<4;i++){
			smallNumberImages[i]=GameImages.getImage(160+i);
		}
		// les cartes de famille
		playerCards = new HashMap<String, Image>();
		
		playerCards.put("Mellissandre", GameImages.getImage(67));
		playerCards.put("Salladhor", GameImages.getImage(68));
		playerCards.put("Davos", GameImages.getImage(69));
		playerCards.put("Brienne", GameImages.getImage(70));
		playerCards.put("Cersei", GameImages.getImage(73));
		playerCards.put("Kevan", GameImages.getImage(74));
		playerCards.put("Tyrion", GameImages.getImage(75));
		playerCards.put("The Hound", GameImages.getImage(76));
		playerCards.put("Jaime", GameImages.getImage(77));
		playerCards.put("Gregor", GameImages.getImage(78));
		playerCards.put("Tywin", GameImages.getImage(79));
		//stark
		playerCards.put("Catelyn", GameImages.getImage(80));
		playerCards.put("BlackFish", GameImages.getImage(81));
		playerCards.put("Rodrick", GameImages.getImage(82));
		playerCards.put("Roose", GameImages.getImage(83));
		playerCards.put("GreatJon", GameImages.getImage(84));
		playerCards.put("Robb", GameImages.getImage(85));
		playerCards.put("Eddard", GameImages.getImage(86));
		//greyjoy
		playerCards.put("Aeron", GameImages.getImage(87));
		playerCards.put("Asha", GameImages.getImage(88));
		playerCards.put("Dagmar", GameImages.getImage(89));
		playerCards.put("Balon", GameImages.getImage(90));
		playerCards.put("Theon", GameImages.getImage(91));
		playerCards.put("Victarion", GameImages.getImage(92));
		playerCards.put("Euron", GameImages.getImage(93));
		//Tyrells
		playerCards.put("Queen", GameImages.getImage(94));
		playerCards.put("Margaery", GameImages.getImage(95));
		playerCards.put("Alester", GameImages.getImage(96));
		playerCards.put("Garlan", GameImages.getImage(97));
		playerCards.put("Randyll", GameImages.getImage(98));
		playerCards.put("Loras", GameImages.getImage(99));
		playerCards.put("Mace", GameImages.getImage(100));
		
		
		//westeros Cards
		westerosCards= new HashMap<String, Image>();
		westerosCards.put("Winter", GameImages.getImage(111));
		westerosCards.put("Summer", GameImages.getImage(112));
		westerosCards.put("Mustering", GameImages.getImage(113));
		westerosCards.put("Supply", GameImages.getImage(114));
		westerosCards.put("ThroneOfBlades", GameImages.getImage(115));
		westerosCards.put("GameOfThrones", GameImages.getImage(116));
		westerosCards.put("ClashOfKings", GameImages.getImage(117));
		westerosCards.put("DarkWingsDarkWords", GameImages.getImage(118));
		westerosCards.put("WebOfLies", GameImages.getImage(119));
		westerosCards.put("Wildings", GameImages.getImage(120));
		westerosCards.put("FeastForCrows", GameImages.getImage(121));
		westerosCards.put("RainsOfAutumn", GameImages.getImage(122));
		westerosCards.put("SeaOfStorms", GameImages.getImage(123));
		westerosCards.put("StromOfSwords", GameImages.getImage(124));
		westerosCards.put("PutToTheSword", GameImages.getImage(125));
		
		//Wildings Cards
		wildingsCards= new HashMap<String, Image>();
		wildingsCards.put("SilenceAtTheWall", GameImages.getImage(188));
		wildingsCards.put("SkinchangerScout", GameImages.getImage(189));
		
		//the garrisons 
		garrisonImages= new HashMap<String,Image>();
		garrisonImages.put("Dragonstone", GameImages.getImage(30));
		garrisonImages.put("Lannisport", GameImages.getImage(31));
		garrisonImages.put("Winterfell", GameImages.getImage(32));
		garrisonImages.put("Pyke", GameImages.getImage(32));
		garrisonImages.put("Highgarden", GameImages.getImage(34));
		garrisonImages.put("Sunspear", GameImages.getImage(35));
		
		//the neutral force 
		neutralForceImages=new Image[5];
		neutralForceImages[0]=GameImages.getImage(197);
		neutralForceImages[1]=GameImages.getImage(198);
		neutralForceImages[2]=GameImages.getImage(199);
		neutralForceImages[3]=GameImages.getImage(200);
		neutralForceImages[4]=GameImages.getImage(201);
	}
	
	/**
	 * 
	 * @param family
	 * @return
	 */
	public Image[] getTroopImages(Family family){
		return troopsSelectImage[family.getPlayer()];
	}
	/**
	 * 
	 * @param family
	 * @return
	 */
	public Image[] getSmallTroopsImage(Family family){
		return troopsImages[family.getPlayer()];
	}
	
	/**
	 * 
	 * @param order
	 * @return
	 */
	public Image getOrderImage(Order order){
		return bigOrdersImages[orderImage(order)];
	}
	
	public Image getSmallOrderImage(Order order){
		return smallOrdersImages[orderImage(order)];
	}
	public Image getPowerImage(Family family){
		return powerImages[family.getPlayer()];
	}
	
	// use by the 2 methods above
	private int orderImage(Order order){
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
		return res;
	}

	public Image getCardImage(String name) {
		return playerCards.get(name);
	}

	public Image getWestCardImage(String westerosCard) {
		return westerosCards.get(westerosCard);
	}
	
	/**
	 * give an image of the given number
	 * @param i the number you want to have the image
	 * @return
	 */
	public Image getNumber(int i){
		return numberImages[i];
	}
	/**
	 * give a small image of the given number
	 * @param i int between 1 to 4
	 * @return
	 */
	public Image getSmallNumber(int i){
		return smallNumberImages[i-1];
	}
	
	/**
	 * 
	 * @param territory
	 * @return
	 */
	public Image getGarrisonImage(String territory){
		return garrisonImages.get(territory);
	}
	
	public Image getWildingCardImage(String wildingsCard) {
		return wildingsCards.get(wildingsCard);
	}
	
	/**
	 * This method return an image that show the neutral force's strength of the territory 
	 * @param strength the strength of the territory
	 * @return 
	 */
	 public Image getNeutralForceImage(int strength){
		 if(strength>6){
			 return neutralForceImages[4];
		 }else{
			 return neutralForceImages[strength-3];
		 }
	 }

	public Image getSmallInfluenceToken(Family family) {
		return smallInfluenceTonken[family.getPlayer()];
	}
	
}
