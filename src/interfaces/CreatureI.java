package interfaces;

import creatures.Creature;

public interface CreatureI {
	
	public void move();
	
	public void draw();
	
	public void rest();
	
	public void solveEncounter(Creature other);
	
	public void eat(Creature other);
	
	public void fight(Creature other);
	

}
