package actions;

import creatures.Creature2;
import enums.Behavior;
import interfaces.ActionChooserI;
import processing.core.PApplet;

public class DefaultActionChooser implements ActionChooserI{

	private PApplet processing;	

	@Override
	public void init(PApplet p) {
		this.processing = p;
	}

	@Override
	public Behavior selectAction(Creature2 c) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Behavior selectAction(Creature2 c, Creature2 other) {
		if(c.getHealth()>other.getHealth())
			return Behavior.CHASE;
		else
			return Behavior.RUN_AWAY;
	}

}
