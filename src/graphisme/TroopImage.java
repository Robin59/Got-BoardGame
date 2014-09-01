package graphisme;

import java.awt.Image;

import org.jogre.client.awt.GameImages;


//INUTILE POUR L'INSTANT
public class  TroopImage{

	/**
	 * la premiere coordoné est pour la famille, la seconde pour le type (naval ou terrestre)
	 */
	private Image[][] TroopsImages;
	
	public TroopImage (){
		TroopsImages= new Image[6][2];
		for (int i =1; i<3;i++){
			for(int y=0;i<6;i++){
				TroopsImages[i-1][y]= GameImages.getImage(i*6+y);
			}
		}
	}
}
