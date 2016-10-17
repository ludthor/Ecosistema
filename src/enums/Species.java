package enums;

public enum Species {
	
	PLANT (0), HERBIVORE (1),CARNIVORE(2);

	private int value;
	
	private Species(int _value){
		this.value = _value;
	}
	
	public int getSpeciesValue(){
		return this.value;
	}


}
