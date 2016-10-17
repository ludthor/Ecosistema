package creatures;

import java.awt.Color;

import actions.DefaultActionChooser;
import actions.DefaultActionManager;
import enums.Gender;
import enums.Species;
import interfaces.ActionChooserI;
import interfaces.ActionManagerI;
import processing.core.PApplet;
import util.EcoUtilities;

public class Creature2{
	
	protected String name;
	protected int health;
	protected int energy;
	protected float speed;
	protected Gender gender;
	protected Color color;
	protected Species species;
	//position
	protected float x;
	protected float y;
	private float dx;
	protected float dy;
	protected float direction;
	protected float dirVariation;
	protected int size;
	protected boolean toroidal;
	protected boolean isAlive;
	
	protected PApplet processing;
	private ActionManagerI actionManager;
	private ActionChooserI actionChooser;
		
	//Constructors
	public Creature2(PApplet p, boolean toroidal){
		this.processing = p;
		this.name = EcoUtilities.nameGenerator();
		this.health = 100;
		this.energy = 100000;
		this.speed = processing.random(5);
		this.gender = EcoUtilities.binaryGender(0.5f);
		isAlive=true;
		this.species=Species.PLANT;
		
		//Coloring the creature
		float r = processing.random(255);
		float g = processing.random(255);
		float b = processing.random(255);
		this.setColor(new Color(processing.color(r,g,b)));
		
		this.size = 5;
		this.setDirection(processing.random(processing.TWO_PI));
		this.setDirVariation(30.0f);
		this.toroidal = toroidal;
		initPos();
		
		actionManager= new DefaultActionManager();
		actionManager.init(processing, this);
		actionChooser= new DefaultActionChooser();
		actionChooser.init(processing);
		
	}
	
	private void initPos(){
		this.x = processing.random(processing.width);
		this.y = processing.random(processing.height);
	    this.setDx(processing.random(-10,10));
	    this.setDy(processing.random(-10,10));
		
	}
	
	
	public boolean isAlive(){
		/*if (this.getHealth() > 0 && getEnergy() > 0)
			return true;
		return false;*/
		return isAlive;
	}
	
	//Getters & Setters
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getHealth() {
		return health;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public int getEnergy() {
		return energy;
	}

	public void setEnergy(int energy) {
		this.energy = energy;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public float getDirection() {
		return direction;
	}

	public void setDirection(float direction) {
		this.direction = direction;
	}

	public float getDirVariation() {
		return dirVariation;
	}

	public void setDirVariation(float dirVariation) {
		this.dirVariation = dirVariation;
	}

	public float getDx() {
		return dx;
	}

	public void setDx(float dx) {
		this.dx = dx;
	}

	public float getDy() {
		return dy;
	}

	public void setDy(float dy) {
		this.dy = dy;
	}

	public ActionManagerI getActionManager() {
		return actionManager;
	}

	public void setActionManager(ActionManagerI actionManager) {
		this.actionManager = actionManager;
	}

	public ActionChooserI getActionChooser() {
		return actionChooser;
	}

	public void setActionChooser(ActionChooserI actionChooserI) {
		this.actionChooser = actionChooserI;
	}

	public Species getSpecies() {
		return species;
	}

	public void setSpecies(Species species) {
		this.species = species;
	}

	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}
	
	public void die(){
		this.isAlive=false;
	}
	
	
	
	
	

	
}
