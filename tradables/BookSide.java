package tradables;

import exceptions.InvalidStringOperation;

public enum BookSide {

	BUY("BUY"),
	SELL("SELL");
	
	private String string;

	private BookSide(String name){string = name;}
	
	public String toString() {
	       return string;
	   }
	//Added 5/16
	public static BookSide toBookSide(String side) throws InvalidStringOperation{
		if (side == null || side.isEmpty()){
			throw new InvalidStringOperation("Bad String Passed.");}
		if (side == "BUY") return BUY;
		if (side == "SELL") return SELL;
		return null;
	}
}
