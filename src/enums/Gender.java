package enums;

public enum Gender {
	
	MALE(1), FEMALE(2),UNDETERMINED(3);

	private int value;
	
	private Gender(int _value){
		this.value = _value;
	}
	
	public int getGenderValue(){
		return this.value;
	}

}
