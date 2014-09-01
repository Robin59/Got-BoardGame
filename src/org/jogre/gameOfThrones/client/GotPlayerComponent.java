package org.jogre.gameOfThrones.client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import org.jogre.client.awt.GameImages;
import org.jogre.client.awt.JogreComponent;
import org.jogre.gameOfThrones.common.GameOfThronesModel;


// cette classe affiche les composantes graphique du joueur seulement accesible à lui
// (ordres a placer)
public class GotPlayerComponent extends JogreComponent { // CHANGER EN PLAYERCOMPONENT ?

	// la famille que controle le joueur
	private int familly;
	
	// les images 
	private Image[] images;
	
	// les régles du jeu 
	private GameOfThronesModel model;
	
	public GotPlayerComponent (GameOfThronesModel model, int fam){
		this.model= model;
		this.familly=fam;
		
		// Set component dimension        
		Dimension dim = new Dimension (70, 70); //peut-etre à changer
		setPreferredSize(dim);
		setMinimumSize(dim);
		
		// a changer
		images = new Image[2];
		images[0]=GameImages.getImage(4);
		images[1]=GameImages.getImage(3);
	}
	
	/**
	 * Draw the player area
	 */
	public void paintComponent (Graphics g) {
		super.paintComponent (g);
		g.drawImage(images[0],0,0, null);
		
	}
}
