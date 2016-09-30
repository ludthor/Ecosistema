package creatures;

import java.awt.Color;

import processing.core.PApplet;

public class Plant extends Creature {
	
	private PApplet processing;

	public Plant(PApplet p, boolean toroidal) {
		super(p, toroidal);
		this.size = 5;
		this.processing = p;
		this.color = new Color(processing.color(0,processing.random(100,255),0));
	}
	
	public void move(){
		
	}
	
	public void draw(){
		processing.stroke(this.color.getRGB());
		processing.fill(this.color.getRGB());
		processing.ellipse(this.getX(),this.getY(), this.getSize(), this.getSize());
	}
	
	

}
