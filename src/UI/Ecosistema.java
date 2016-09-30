package UI;

import creatures.Creature;
import processing.core.PApplet;
import terrain.T_Place;
import terrain.Territory;
import util.EcoUtilities;

public class Ecosistema extends PApplet{
	
	private final int NUM_CREATURES = 50;
	private final boolean toroidal = true;
	Creature[] creatures;
	Territory t;
//	Carnivore car;
	
    public void settings(){
        size(800,500);
//        fullScreen();
        
    }

    public void setup(){
    	frameRate(30);
        background(0);
    	creatures = new Creature[NUM_CREATURES];
    	
    	//Inicialize creatures one by one.
    	for(int i = 0; i < NUM_CREATURES; i++){
    		creatures[i] = EcoUtilities.generateCreature(this, toroidal, 0.3f, 0.3f);
    	}
//        car = new Carnivore(this, true);
//        car.setSize(20);
    }

    public void draw(){
    	
    	background(0);
    	mouseAreaDraw();
    	
    	checkTerritory();
    	solveEncounters();
    	moveCreatures();

    }
    
	private void moveCreatures() {
	       for(Creature c: creatures){
	    	   if(c.isAlive()){
	    		   //System.out.println(c.getEnergy());
	    		   c.move();
	    		   c.draw();
	    	   }
	       }
	}

	private void solveEncounters() {
	    int limit = 20;
	    for( int i=0 ; i<t.getCol() ; i++ ){
	    //recorre una por una las

	        for( int j=0 ; j<t.getRow() ; j++ ){
	        //celdas del territorio

	            T_Place thisPlace = t.getPlaces()[i][j];
	            //toma cada lugar del territorio

	            for( int k=0 ; k < thisPlace.getTotal()-1 && k<limit ; k++ ){
	            // toma uno por uno los objetos de este lugar

	                int id1 = thisPlace.getId()[k];
	                // recupera el id

	                for( int l=k+1 ;l < thisPlace.getTotal() && l<limit ; l++ ){
	                // toma otro objeto del lugar

	                    int id2 = thisPlace.getId()[l];
	                    // recupera el id

	                    creatures[id1].solveEncounter( creatures[id2] );
	                    // enfrenta al organismo
	                    // 1 con el 2

	                    creatures[id2].solveEncounter( creatures[id1] );
	                    // enfrenta al organismo
	                    // 2 con el 1

	                }

	            }

	        }

	    }		
	}

	private void checkTerritory() {
	    this.t = new Territory( width , height , 3 , 3);
	    for(int i=0 ; i< NUM_CREATURES;i++){ //se recorre cada animal y :

	        t.locate( this.creatures[i].getX() , this.creatures[i].getY() , i );

	    }		
	}

	public boolean isToroidal() {
		return toroidal;
	}
	
	public void mouseClicked(){
		
		for(Creature c: creatures){
			rectMode(CENTER);
			if (mouseX >= c.getX() - c.getSize() && mouseX <= c.getX() + c.getSize()  && mouseY >= c.getY() - c.getSize() && mouseY <= c.getY() + c.getSize() ){
				background(100);
				System.out.println("______________");				
				System.out.println("NAME:" + c.getName());
				System.out.println("GENDER:" + c.getGender());
				System.out.println(c.getClass());
				System.out.println("______________");
			}
		}
		
	}
	
	public void mouseAreaDraw(){
		stroke(204, 102, 0.8f);
		noFill();
		ellipse(mouseX, mouseY, 15, 15);
	}
	

}
