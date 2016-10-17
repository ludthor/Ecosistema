package actions;

import creatures.Creature2;
import enums.Behavior;
import enums.Species;
import interfaces.ActionChooserI;
import processing.core.PApplet;

public class DefaultActionChooser implements ActionChooserI{

	private PApplet processing;
	Behavior conductaColision[][];
	Behavior conductaCerca[][];
	Behavior conductaLejos[][];
	private int radio=10;

	@Override
	public void init(PApplet p) {
		this.processing = p;
		loadBehavior();
	}

	@Override
	public Behavior selectAction(Creature2 c) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Behavior selectAction(Creature2 c, Creature2 other) {
		float distance = processing.dist( c.getX() , c.getY() , other.getX() , other.getY() );
	    Behavior behavior;
	    //System.out.println(c.getClass().getName()+" "+other.getClass().getName());
	    if( distance < radio*2 ){ //si lo está tocando
	    	behavior = conductaColision[ c.getSpecies().getSpeciesValue() ][ other.getSpecies().getSpeciesValue() ];
	    }
	    else if( distance < radio*6 ){ //si está a un cuerpo de distancia
	    	behavior = conductaCerca[ c.getSpecies().getSpeciesValue() ][ other.getSpecies().getSpeciesValue() ];
	    }
	    else{
	    	behavior = conductaLejos[ c.getSpecies().getSpeciesValue() ][ other.getSpecies().getSpeciesValue() ];
	    }
	    
	    return behavior;

	    /*if( actitudConEste > conducta ){
	      conducta = actitudConEste;
	      elOtro = otro;
	    }*/

		/*if(c.getHealth()>other.getHealth())
			return Behavior.CHASE;
		else
			return Behavior.RUN_AWAY;*/
	}

	@Override
	public void loadBehavior() {
		startBehaviorMatrix();
		
	}
	
	void startBehaviorMatrix(){

	    conductaColision = new Behavior[3][3];
	    conductaCerca = new Behavior[3][3];
	    conductaLejos = new Behavior[3][3];

	    // conducta para cuando dos organismos COLISIONAN
	    conductaColision[ Species.PLANT.getSpeciesValue() ][ Species.PLANT.getSpeciesValue() ] = Behavior.INDIFFERENCE;
	    conductaColision[ Species.PLANT.getSpeciesValue() ][ Species.HERBIVORE.getSpeciesValue() ] = Behavior.INDIFFERENCE;
	    conductaColision[ Species.PLANT.getSpeciesValue() ][ Species.CARNIVORE.getSpeciesValue() ] = Behavior.INDIFFERENCE;

	    conductaColision[ Species.HERBIVORE.getSpeciesValue() ][ Species.PLANT.getSpeciesValue() ] = Behavior.EAT;
	    conductaColision[ Species.HERBIVORE.getSpeciesValue() ][ Species.HERBIVORE.getSpeciesValue() ] = Behavior.DODGE;
	    conductaColision[ Species.HERBIVORE.getSpeciesValue() ][ Species.CARNIVORE.getSpeciesValue() ] = Behavior.RUN_AWAY;

	    conductaColision[ Species.CARNIVORE.getSpeciesValue() ][ Species.PLANT.getSpeciesValue() ] = Behavior.DODGE;
	    conductaColision[ Species.CARNIVORE.getSpeciesValue() ][ Species.HERBIVORE.getSpeciesValue() ] = Behavior.EAT;
	    conductaColision[ Species.CARNIVORE.getSpeciesValue() ][ Species.CARNIVORE.getSpeciesValue() ] = Behavior.DODGE;

	    // conducta para cuando dos organismos ESTAN CERCA
	    conductaCerca[ Species.PLANT.getSpeciesValue() ][ Species.PLANT.getSpeciesValue() ] = Behavior.INDIFFERENCE;
	    conductaCerca[ Species.PLANT.getSpeciesValue() ][ Species.HERBIVORE.getSpeciesValue() ] = Behavior.INDIFFERENCE;
	    conductaCerca[ Species.PLANT.getSpeciesValue() ][ Species.CARNIVORE.getSpeciesValue() ] = Behavior.INDIFFERENCE;

	    conductaCerca[ Species.HERBIVORE.getSpeciesValue() ][ Species.PLANT.getSpeciesValue() ] = Behavior.CHASE;
	    conductaCerca[ Species.HERBIVORE.getSpeciesValue() ][ Species.HERBIVORE.getSpeciesValue() ] = Behavior.INDIFFERENCE;
	    conductaCerca[ Species.HERBIVORE.getSpeciesValue() ][ Species.CARNIVORE.getSpeciesValue() ] = Behavior.WALK_AWAY;

	    conductaCerca[ Species.CARNIVORE.getSpeciesValue() ][ Species.PLANT.getSpeciesValue() ] = Behavior.INDIFFERENCE;
	    conductaCerca[ Species.CARNIVORE.getSpeciesValue() ][ Species.HERBIVORE.getSpeciesValue() ] = Behavior.CHASE;
	    conductaCerca[ Species.CARNIVORE.getSpeciesValue() ][ Species.CARNIVORE.getSpeciesValue() ] = Behavior.INDIFFERENCE;

	    // conducta para cuando dos organismos ESTAN LEJOS
	    conductaLejos[ Species.PLANT.getSpeciesValue() ][ Species.PLANT.getSpeciesValue() ] = Behavior.INDIFFERENCE;
	    conductaLejos[ Species.PLANT.getSpeciesValue() ][ Species.HERBIVORE.getSpeciesValue() ] = Behavior.INDIFFERENCE;
	    conductaLejos[ Species.PLANT.getSpeciesValue() ][ Species.CARNIVORE.getSpeciesValue() ] = Behavior.INDIFFERENCE;

	    conductaLejos[ Species.HERBIVORE.getSpeciesValue() ][ Species.PLANT.getSpeciesValue() ] = Behavior.INDIFFERENCE;
	    conductaLejos[ Species.HERBIVORE.getSpeciesValue() ][ Species.HERBIVORE.getSpeciesValue() ] = Behavior.INDIFFERENCE;
	    conductaLejos[ Species.HERBIVORE.getSpeciesValue() ][ Species.CARNIVORE.getSpeciesValue() ] = Behavior.WALK_AWAY;

	    conductaLejos[ Species.CARNIVORE.getSpeciesValue() ][ Species.PLANT.getSpeciesValue() ] = Behavior.INDIFFERENCE;
	    conductaLejos[ Species.CARNIVORE.getSpeciesValue() ][ Species.HERBIVORE.getSpeciesValue() ] = Behavior.CHASE;
	    conductaLejos[ Species.CARNIVORE.getSpeciesValue() ][ Species.CARNIVORE.getSpeciesValue() ] = Behavior.INDIFFERENCE;

	  }  

}
