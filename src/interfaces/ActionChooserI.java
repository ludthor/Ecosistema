package interfaces;

import creatures.Creature2;
import enums.Behavior;
import processing.core.PApplet;

public interface ActionChooserI {
	
	public void init(PApplet p);
	
	public void loadBehavior();
	
	public Behavior selectAction(Creature2 c);
	
	public Behavior selectAction(Creature2 c,Creature2 other);

}
