package interfaces;

import creatures.Creature2;
import processing.core.PApplet;

public interface ActionManagerI {
	
	
	public void init(PApplet p,Creature2 self);
	
	public void move();
	
	public void draw();
	
	public void doAction(String action,Class[]cArgs,Object[]args);
	
	public void solveEncounter(Creature2 other);

}
