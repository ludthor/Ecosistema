package enums;

public enum Behavior {
	
	INDIFFERENCE (1), WALK_AWAY (2),CHASE (3),DODGE (4),MATE (5),RUN_AWAY (6),EAT (7);

	private int value;
	
	private Behavior(int _value){
		this.value = _value;
	}
	
	public int getBehaviorValue(){
		return this.value;
	}


}
