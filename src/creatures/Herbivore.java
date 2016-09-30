package creatures;

import java.awt.Color;

import processing.core.PApplet;

public class Herbivore extends Creature {
	
	private PApplet processing;

	public Herbivore(PApplet p, boolean toroidal) {
		super(p, toroidal);
		this.processing = p;
		this.color = new Color(processing.color(0,0,processing.random(100,255)));
		this.dirVariation = 30.0f;
		this.setSize(20);
	}
	
	public void draw(){
		processing.stroke(this.color.getRGB());
		processing.fill(this.color.getRGB());
		
		float xFront = this.getX() + (this.size * processing.cos(this.direction));
		float yFront = this.getY() + (this.size * processing.sin(this.direction));
		float xLeft = this.getX() + ((this.size * (float) Math.random()) * processing.cos(this.direction + processing.radians(90)));
		float yLeft = this.getY() + ((this.size * (float) Math.random()) * processing.sin(this.direction + processing.radians(90)));
		float xBack = this.getX() + ((this.size * (float) Math.random()) * processing.cos(this.direction + processing.radians(180)));
		float yBack = this.getY() + ((this.size * (float) Math.random()) * processing.sin(this.direction + processing.radians(180)));
		float xRight = this.getX() + ((this.size * (float) Math.random()) * processing.cos(this.direction + processing.radians(270)));
		float yRight = this.getY() + ((this.size * (float) Math.random()) * processing.sin(this.direction + processing.radians(270)));
		
		
		processing.quad(xFront,yFront, xLeft, yLeft, xBack, yBack, xRight, yRight);
	}
	
	

}
