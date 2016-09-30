package creatures;

import java.awt.Color;

import interfaces.CreatureI;
import processing.core.PApplet;

public class Carnivore extends Creature implements CreatureI {
	
	private PApplet processing;

	public Carnivore(PApplet p, boolean toroidal) {
		super(p, toroidal);
		
		this.processing = p;
		this.color = new Color(processing.color(processing.random(100,255),0,0));
		this.dirVariation = 15.0f;
		this.setSize(20);
	}
	
	public void draw(){
		processing.stroke(this.color.getRGB());
		processing.fill(this.color.getRGB());
		
		float xFront = this.getX() + (this.size * processing.cos(this.direction));
		float yFront = this.getY() + (this.size * processing.sin(this.direction));
		float xLeft = this.getX() + ((this.size * (float) Math.random()) * processing.cos(this.direction + processing.radians(120)));
		float yLeft = this.getY() + ((this.size * (float) Math.random()) * processing.sin(this.direction + processing.radians(120)));
		float xRight = this.getX() + ((this.size * (float) Math.random()) * processing.cos(this.direction - processing.radians(120)));
		float yRight = this.getY() + ((this.size * (float) Math.random()) * processing.sin(this.direction - processing.radians(120)));
		
		
		processing.triangle(xFront, yFront, xLeft, yLeft, xRight, yRight);
//		processing.stroke(255,255,0);
//		processing.line(this.x, this.y, xFront, yFront);
//		processing.stroke(0,255,255);
//		processing.line(this.x, this.y, xLeft, yLeft);
//		processing.stroke(255,0,255);
//		processing.line(this.x, this.y, xRight, yRight);
		
	}

	@Override
	public void rest() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void eat(Creature other) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fight(Creature other) {
		// TODO Auto-generated method stub
		
	}
	
	

}
