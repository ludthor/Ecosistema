package terrain;

import java.util.List;

import interfaces.PlacePropertiesManagerI;

public class RandomNumericProperties implements PlacePropertiesManagerI {
	
	private List<String> propertyNames;
	
	
	public RandomNumericProperties(List<String> propertyNames) {
		super();
		this.propertyNames = propertyNames;
	
	}





	@Override
	public PlaceProperties startProperties() {
		PlaceProperties properties = new PlaceProperties();
		for(String s:propertyNames){
			properties.setProperty(s, Math.random());
		}
		return properties;

	}

}
