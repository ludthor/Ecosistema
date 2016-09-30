package creatures;

import java.awt.Color;

import enums.Gender;
import processing.core.PApplet;
import util.EcoUtilities;

public class Creature{
	
	protected String name;
	protected int health;
	protected int energy;
	protected float speed;
	protected Gender gender;
	protected Color color;
	
	//position
	protected float x;
	protected float y;
	protected float dx;
	protected float dy;
	protected float direction;
	protected float dirVariation;
	protected int size;
	protected boolean toroidal;
	
	protected PApplet processing;
		
	//Constructors
	public Creature(PApplet p, boolean toroidal){
		this.processing = p;
		this.name = EcoUtilities.nameGenerator();
		this.health = 100;
		this.energy = 100000;
		this.speed = processing.random(5);
		this.gender = EcoUtilities.binaryGender(0.5f);
		
		//Coloring the creature
		float r = processing.random(255);
		float g = processing.random(255);
		float b = processing.random(255);
		this.color = new Color(processing.color(r,g,b));
		
		this.size = 5;
		this.direction = processing.random(processing.TWO_PI);
		this.dirVariation = 30.0f;
		this.toroidal = toroidal;
		initPos();
	}
	
	private void initPos(){
		this.x = processing.random(processing.width);
		this.y = processing.random(processing.height);
	    this.dx = processing.random(-10,10);
	    this.dy = processing.random(-10,10);
		
	}
	
	//Methods
	public void move(){
		
		changeAngle(this.dirVariation);
		
		this.dx = speed * processing.cos(this.direction);  
		this.dy = speed * processing.sin(this.direction);
		this.x += this.dx; 	
		this.y += this.dy; 
		
		//If the space is defined as toroidal we must restart the coordinates at the borders.
		if(true){
			
			x = x >= processing.width ? x - processing.width : x;
			x = x <= 0 ? x + processing.width : x;
			y = y >= processing.height ? y - processing.height : y;
			y = y <= 0 ? y + processing.displayHeight : y;
		}
		
		this.energy--;
	}
	
    void changeAngle( float angle ){ 

        float rad = processing.radians( angle ); 
        this.direction += processing.random( -rad , rad ); 

    }
	
	
	public void draw(){
		processing.rectMode(processing.CENTER);
		processing.stroke(this.color.getRGB());
		processing.fill(this.color.getRGB());
	    processing.rect(this.x,this.y,size,size);
	}
	
	public void solveEncounter(Creature other){
		if(processing.dist(this.x, this.y, other.getX(), other.getY()) < this.size*4){
			this.direction = processing.atan2(this.y - other.y, this.x - other.x);
		}
	}
	
	public boolean isAlive(){
		if (this.getHealth() > 0 && getEnergy() > 0)
			return true;
		return false;
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

}
