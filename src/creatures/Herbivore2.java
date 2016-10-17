package creatures;

import java.awt.Color;

import actions.CarnivoreActionManager;
import actions.DefaultActionChooser;
import actions.DefaultActionManager;
import actions.HerbivoreActionManager;
import enums.Species;
import interfaces.ActionChooserI;
import interfaces.ActionManagerI;
import processing.core.PApplet;

public class Herbivore2 extends Creature2  {
	
	private PApplet processing;
	private ActionManagerI actionManager;
	private ActionChooserI actionChooser;
	
	

	public Herbivore2(PApplet p, boolean toroidal) {
		super(p, toroidal);
		
		this.processing = p;
		this.color = new Color(processing.color(0,0,processing.random(100,255)));
		this.dirVariation = 30.0f;
		this.setSize(20);
		this.actionChooser= new DefaultActionChooser();
		this.actionManager = new HerbivoreActionManager();
		actionManager.init(processing, this);
		actionChooser.init(processing);
		this.species= Species.HERBIVORE;
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
