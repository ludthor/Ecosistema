package terrain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.sql.rowset.spi.TransactionalWriter;

import enums.Errors;

public class PlaceProperties {
	
	private HashMap<String, Object> properties;
	private List<String> propertyNames;
	
	
	public PlaceProperties() {
		properties= new HashMap<>();
		propertyNames= new ArrayList<>();
	}
	
	public void setProperty(String key,Object o){
		properties.put(key, o);
		propertyNames.add(key);
	}
	
	public Integer getIntProperty(String key){
		Object o= properties.get(key);	
		if (o instanceof Integer) 
			return (Integer) o;
		throw new Error(Errors.ERROR_PARSE_INT.toString());
	}
	public String getStringProperty(String key){
		Object o= properties.get(key);	
		if (o instanceof String) 
			return (String)o;
		throw new Error(Errors.ERROR_PARSE_STRING.toString());
	}
	public Boolean getBooleanProperty(String key){
		Object o= properties.get(key);	
		if (o instanceof Boolean) 
			return (Boolean)o;
		throw new Error(Errors.ERROR_PARSE_BOOLEAN.toString());
	}
	public Double getDoubleProperty(String key){
		Object o= properties.get(key);	
		if (o instanceof Double) 
			return (Double)o;
		throw new Error(Errors.ERROR_PARSE_DOUBLE.toString());
	}
	public Float getFloatProperty(String key){
		Object o= properties.get(key);
		if (o instanceof Float) 
			return (Float)o;
		throw new Error(Errors.ERROR_PARSE_FLOAT.toString());
	}
	public Object getObjectProperty(String key){
		return properties.get(key);	
		
	}

	public List<String> getPropertyNames() {
		return propertyNames;
	}

	
	
	

}
