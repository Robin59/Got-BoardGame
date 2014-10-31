/*
 * JOGRE (Java Online Gaming Real-time Engine) - GameOfThrones
 * Copyright (C) 2004 - 2014  Robin Giraudon (giraudon.robin@gmail.com)
 * http://jogre.sourceforge.org
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package graphisme;


import java.awt.Graphics;
import java.awt.Image;

import org.jogre.gameOfThrones.common.GameOfThronesModel;
import org.jogre.client.awt.GameImages;
import org.jogre.client.awt.JogreComponent;
import org.jogre.gameOfThrones.common.territory.*;
/**
 * Main visual view for a game of gameOfThrones which should show a
 * graphical representation of the GameOfThrones Board.
 *
 * @author  Robin Giraudon
 * @version Beta 0.3
 */
public class BoardComponent extends JogreComponent {

	//private int curMousePoint;
	private Board board;
    // Link to the model
    protected GameOfThronesModel model;

    // Constructor which creates the board
    public BoardComponent (GameOfThronesModel model) {
        super ();
       // curMousePoint =-1; 
        board =new Board(model);
        this.model = model;         // link to model  
    }

    // Update the graphics depending on the model
    public void paintComponent (Graphics g) {
        super.paintComponent (g);
        
        //partie test
        board.afficher(g);
    }
    public void up(){
    	board.up();
    	repaint();
    }
    public void down(){
    	board.down();
    	repaint();
    }
    /**Set the player who view this component*/
    public void setPlayer(int player){
    	board.setPlayer(player);
    }
    /*public Board getBoard(){
    	return board;
    }*/
    
   

    
    // renvoi les informations du territoire 
    public String getInfo(int x,int y){
	   Territory temp =this.getTerritory(x,y);
	   if(temp==null){
		   return "";
	   }else{
		   return temp.toString();
	   }
    }
    
    // utiliser this.getComponentAt(x,y)
	public Territory getTerritory(int x,int y) { 
		return board.getTeritory(x, y);
	}
}
