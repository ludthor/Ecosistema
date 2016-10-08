package actions;

import interfaces.ActionChooserI;
import processing.core.PApplet;

public class DefaultActionChooser implements ActionChooserI{

	private PApplet processing;

	@Override
	public String selectAction() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void init(PApplet p) {
		this.processing = p;
		
	}

}
