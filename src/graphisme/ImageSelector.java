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



public class  ImageSelector{

	/** l'unique instance de cette classe*/
	public final static ImageSelector IMAGESELECTOR=new ImageSelector();
	
	/** la premiere coordoné est pour la famille, la seconde pour le type (naval ou terrestre)*/
	private Image[][] troopsImages;
	private Image[][] troopsSelectImage;
	private Image[] bigOrdersImages;
	private Image[] smallOrdersImages;
	private Map<String,Image> playerCards;
	private Map<String,Image> westerosCards;
	
	private ImageSelector (){
		//images des troupes
		troopsImages= new Image[6][2];
		for (int i =0; i<2;i++){
			for(int y=0;y<6;y++){
				troopsImages[y][i]= GameImages.getImage(i+y*2+30);
			}
		}
		//image des troupes mais plus grandes et avec les cavaliers et les tours 
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
		
		//westeros Cards
		westerosCards= new HashMap<String, Image>();
		westerosCards.put("Winter", GameImages.getImage(111));
		westerosCards.put("Summer", GameImages.getImage(112));
		westerosCards.put("Mustering", GameImages.getImage(113));
		westerosCards.put("Supply", GameImages.getImage(114));
		westerosCards.put("ThroneOfBlades", GameImages.getImage(115));
		westerosCards.put("GameOfThrones", GameImages.getImage(116));
		westerosCards.put("ClashOfKings", GameImages.getImage(117));
		westerosCards.put("Wildings", GameImages.getImage(120));
		westerosCards.put("FeastForCrows", GameImages.getImage(121));
		westerosCards.put("RainsOfAutumn", GameImages.getImage(122));
		westerosCards.put("SeaOfStorms", GameImages.getImage(123));
		westerosCards.put("StromOfSwords", GameImages.getImage(124));
	}
	
	public Image[] getTroopImages(Family family){
		return troopsSelectImage[family.getPlayer()];
	}
	
	
	public Image getTroopImage(Family family, Territory territory){
		int fam=territory.getFamily().getPlayer();
		int typ=0;
		if (territory instanceof Water){
			typ++;
		}
		return troopsImages[fam][typ];
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
	
}
