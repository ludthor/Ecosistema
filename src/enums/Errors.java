package enums;

public enum Errors {
	ERROR_PARSE_DOUBLE("ERROR_PARSE_DOUBLE"),
	ERROR_PARSE_INT("ERROR_PARSE_INT"),
	ERROR_PARSE_STRING("ERROR_PARSE_STRING"),
	ERROR_PARSE_FLOAT("ERROR_PARSE_FLOAT"),
	ERROR_PARSE_BOOLEAN("ERROR_PARSE_BOOLEAN"),
	ERROR_PARSE_OBJECT("ERROR_PARSE_OBJECT");

	private String value;
	
	private Errors(String _value){
		this.value = _value;
	}
	
	public String getErrorsValue(){
		return this.value;
	}

}
