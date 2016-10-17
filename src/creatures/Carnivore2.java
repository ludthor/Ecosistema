package creatures;

import java.awt.Color;

import actions.CarnivoreActionManager;
import actions.DefaultActionChooser;
import actions.DefaultActionManager;
import enums.Species;
import interfaces.ActionChooserI;
import interfaces.ActionManagerI;
import processing.core.PApplet;

public class Carnivore2 extends Creature2  {
	
	private PApplet processing;
	private ActionManagerI actionManager;
	private ActionChooserI actionChooser;
	
	

	public Carnivore2(PApplet p, boolean toroidal) {
		super(p, toroidal);
		
		this.processing = p;
		this.color = new Color(processing.color(processing.random(100,255),0,0));
		this.dirVariation = 15.0f;
		this.setSize(20);
		this.actionChooser= new DefaultActionChooser();
		this.actionManager = new CarnivoreActionManager();
		this.species= Species.CARNIVORE;
		actionManager.init(processing, this);
		actionChooser.init(processing);
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

	public void setActionChooser(ActionChooserI actionChooser) {
		this.actionChooser = actionChooser;
	}
	
	
	
	

}
