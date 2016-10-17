package creatures;

import java.awt.Color;

import actions.CarnivoreActionManager;
import actions.DefaultActionChooser;
import actions.DefaultActionManager;
import actions.HerbivoreActionManager;
import actions.PlantActionManager;
import enums.Species;
import interfaces.ActionChooserI;
import interfaces.ActionManagerI;
import processing.core.PApplet;

public class Plant2 extends Creature2  {
	
	private PApplet processing;
	private ActionManagerI actionManager;
	private ActionChooserI actionChooser;
	private Species species= Species.PLANT;
	

	public Plant2(PApplet p, boolean toroidal) {
		super(p, toroidal);
		
		this.size = 5;
		this.processing = p;
		this.color = new Color(processing.color(0,processing.random(100,255),0));
		this.actionChooser= new DefaultActionChooser();
		this.actionManager = new PlantActionManager();
		actionManager.init(processing, this);
		actionChooser.init(processing); 
		this.species= Species.PLANT;
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
