package GlobalConstants;

public enum MarketState {
	
	CLOSED("CLOSED"),
	OPEN("OPEN"),
	PREOPEN("PREOPEN");
	
	private String string;

	private MarketState(String name){string = name;}
	
	public String toString() {
	       return string;
	   }

}
