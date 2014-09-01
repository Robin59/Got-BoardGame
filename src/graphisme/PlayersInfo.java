package graphisme;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseMotionListener;

import org.jogre.client.awt.GameImages;
import org.jogre.client.awt.JogreComponent;
import org.jogre.gameOfThrones.common.GameOfThronesModel;


/**
 * Show all the general informations relative to the players and the game 
 * such as the turn number, victory course, players influence, etc. 
 * @author robin
 *
 */
public class PlayersInfo extends JogreComponent{
	
	// the model of the game
	private GameOfThronesModel model;
	
	// the images show
	private Image backImage;
	private Image[] tokens;
	
	public PlayersInfo(GameOfThronesModel model){
		this.model=model;
		//load the images
		backImage=GameImages.getImage(2);
	}
	
	/**
	 * Draw the player area
	 */
	public void paintComponent (Graphics g) {
		super.paintComponent (g);
		g.drawImage(backImage,0,0, null);
	}
}
