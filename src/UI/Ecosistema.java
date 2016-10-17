package UI;

import creatures.Creature;
import creatures.Creature2;
import processing.core.PApplet;
import terrain.T_Place;
import terrain.Territory;
import util.EcoUtilities;

public class Ecosistema extends PApplet{
	
	private final int NUM_CREATURES = 50;
	private final boolean toroidal = true;
	//Creature[] creatures;
	Creature2[] creatures2;
	Territory t;

	
    public void settings(){
        size(800,500);
//        fullScreen();
        
    }

    public void setup(){
    	frameRate(30);
        background(0);
    	//creatures = new Creature[NUM_CREATURES];
    	creatures2 = new Creature2[NUM_CREATURES];
    	
    	//Inicialize creatures one by one.
    	for(int i = 0; i < NUM_CREATURES; i++){
    		creatures2[i] = EcoUtilities.generateCreature2(this, toroidal, 0.3f, 0.3f);
    		//creatures2[i] = new Creature2(this, toroidal);
    	}

    	this.t = new Territory( width , height , 3 , 3);
    }

    public void draw(){
    	
    	background(0);
    	mouseAreaDraw();
    	checkTerritory();
    	solveEncounters();
    	moveCreatures();

    }
    
	private void moveCreatures() {
	       for(Creature2 c: creatures2){
	    	   if(c.isAlive()){
	    		   //System.out.println(c.getEnergy());
	    		   c.getActionManager().move();
	    		   c.getActionManager().draw();
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
	                if(creatures2[id1].isAlive())
		                for( int l=k+1 ;l < thisPlace.getTotal() && l<limit ; l++ ){
		                // toma otro objeto del lugar
		                    int id2 = thisPlace.getId()[l];
		                    // recupera el id
		                    if(creatures2[id2].isAlive()){
			                    creatures2[id1].getActionManager().solveEncounter( creatures2[id2] );
			                    // enfrenta al organismo
			                    // 1 con el 2
			                    creatures2[id2].getActionManager().solveEncounter( creatures2[id1] );
			                    // enfrenta al organismo
			                    // 2 con el 1
		                    }
		                }
	            }

	        }

	    }		
	}

	private void checkTerritory() {
	    
	    for(int i=0 ; i< NUM_CREATURES;i++){ //se recorre cada animal y :

	        t.locate( this.creatures2[i].getX() , this.creatures2[i].getY() , i );

	    }		
	}

	public boolean isToroidal() {
		return toroidal;
	}
	
	public void mouseClicked(){
		
		for(Creature2 c: creatures2){
			rectMode(CENTER);
			if (mouseX >= c.getX() - c.getSize() && mouseX <= c.getX() + c.getSize()  && mouseY >= c.getY() - c.getSize() && mouseY <= c.getY() + c.getSize() ){
				background(150);
				System.out.println("______________");				
				//System.out.println("NAME:" + c.getName());
				Object[] args = {c};
				Class[] aArgs={Creature2.class};
				c.getActionManager().doAction("printNameCreature",aArgs, args);
				System.out.println("GENDER:" + c.getGender());
				//System.out.println(c.getClass());
				System.out.println("______________");
			}
		}
		this.t.printProperties(mouseX, mouseY);
		
	}
	
	public void mouseAreaDraw(){
		stroke(204, 102, 0.8f);
		noFill();
		ellipse(mouseX, mouseY, 15, 15);
	}
	

}
