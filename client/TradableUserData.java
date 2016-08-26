package client;

import exceptions.InvalidStringOperation;
import tradables.BookSide;

public class TradableUserData {

	private String userName;
	private String product;
	private BookSide bookSide;
	private String orderId;
	
	public TradableUserData(String u, String p, BookSide b, String o) throws InvalidStringOperation{
		
		setUserName(u);
		setProduct(p);
		setBookSide(b);
		setOrderId(o);
	}

	private void setUserName(String uName) throws InvalidStringOperation {
		if (uName == null || uName.isEmpty()){throw new InvalidStringOperation("Bad Username String.");}
		userName = uName;
	}

	private void setProduct(String uProduct) throws InvalidStringOperation {
		if (uProduct == null || uProduct.isEmpty()){throw new InvalidStringOperation("Bad Product String.");}
		product = uProduct;		
	}
	
	private void setBookSide(BookSide uSide) {
		bookSide = uSide;		
	}
	
	private void setOrderId(String uId) throws InvalidStringOperation {
		if (uId == null || uId.isEmpty()){throw new InvalidStringOperation("Bad Order ID String.");}
		orderId = uId;		
	}
	
	public String getUser(){
		return userName;
	}
	
	public String getProduct(){
		return product;		
	}
		
	public BookSide getSide(){
		return bookSide;
	}
	
	public String getId(){
		return orderId;
	}

	public String toString(){
		String out = "";
		out = out.concat("User ");
		out = out.concat(getUser());
		out = out.concat(", ");
		out = out.concat(getSide().toString());
		out = out.concat(" ");
		out = out.concat(getProduct());
		out = out.concat(" (");
		out = out.concat(getId());
		out = out.concat(")");
		return out;
	}
	
}
