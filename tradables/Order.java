package tradables;

import java.io.IOException;

import exceptions.InvalidIntOperation;
import exceptions.InvalidPriceOperation;
import exceptions.InvalidStringOperation;
import price.Price;

public class Order implements Tradable {

	private String userName;
	private String product;
	String orderId;
	private BookSide bookSide;
	private Price price;
	private String originalOrderVolume;
	private String remainingOrderVolume;
	private String cancelledOrderVolume;

	public Order(String userName, String productSymbol, Price orderPrice, int originalVolume, BookSide side) throws Exception {
		cancelledOrderVolume = "0";
		setUserName(userName);
		setProduct(productSymbol);
		setBookSide(side);
		setPrice(orderPrice);
		setOriginalOrderVolume(originalVolume);
		setRemainingVolume(originalVolume);
		setOrderId();
	}

	public Order(Order order) throws Exception {
		setUserName(order.getUser());
		setProduct(order.getProduct());
		setBookSide(order.getSide());
		setPrice(order.getPrice());
		setOriginalOrderVolume(order.getOriginalVolume());
		setRemainingVolume(order.getRemainingVolume());
		setCancelledVolume(order.getCancelledVolume());
		setOrderId();
	}
	
	private void setUserName(String uName) throws InvalidStringOperation{
		if (uName == null){throw new InvalidStringOperation("Null String.");}
		userName = uName;		
	}
	
	private void setProduct(String uProduct) throws InvalidStringOperation{
		if (uProduct == null){throw new InvalidStringOperation("Null String.");}
		product = uProduct;
	}
	
	private void setBookSide(BookSide uBook) throws InvalidStringOperation{
		if (uBook == null){throw new InvalidStringOperation("Null String.");}
		bookSide = uBook;
	}
	
	private void setPrice(Price uPrice) throws InvalidPriceOperation{
		if (uPrice == null){throw new InvalidPriceOperation("Null Price.");}
		price = uPrice;
	}
	
	private void setOriginalOrderVolume(int uOriginalVolume) throws InvalidIntOperation{
		if (uOriginalVolume <= 0){
			throw new InvalidIntOperation("Invalid Order Volume: " + uOriginalVolume);
		}
		originalOrderVolume = new Integer(uOriginalVolume).toString();
	}
	
	public void setRemainingVolume(int newRemainingVolume) throws IOException{
		if (newRemainingVolume < 0){
			throw new IOException("Value cannot be negative.");
		}	
		if ((getCancelledVolume() + newRemainingVolume) > getOriginalVolume()){
			throw new IOException("Requested new Remaining Volume (" + newRemainingVolume + ") plus the Cancelled Volume ("
					+ getCancelledVolume() + ") exceeds the tradable's Original Volume (" + getOriginalVolume() + ")");
		}
		remainingOrderVolume = new Integer(newRemainingVolume).toString();
	}
	
	public void setCancelledVolume(int newCancelledVolume) throws IOException{
		if (newCancelledVolume < 0){
			throw new IOException("Value cannot be negative.");
		}	
		if ((getRemainingVolume() + newCancelledVolume) > getOriginalVolume()){
			throw new IOException("Requested new Cancelled Volume (" + newCancelledVolume + ") plus the Remaining Volume (" + 
					getRemainingVolume() + ") exceeds the tradable's Original Volume (" +  getOriginalVolume() + ")");
		}
		cancelledOrderVolume = new Integer(newCancelledVolume).toString();
	}
	
	private void setOrderId(){
		String s = userName;
		s = s.concat(product);
		s = s.concat(price.toString());
		s = s.concat(String.valueOf(System.nanoTime()));
		orderId = s;
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
	public boolean isQuote(){
		return false;
		
	}
	public String getId(){
		return orderId;
	}
	
	public Price getPrice(){
		return price;
	}
	
	public int getOriginalVolume(){
		return Integer.valueOf(originalOrderVolume);
	}
	
	public int getRemainingVolume(){
		return Integer.valueOf(remainingOrderVolume);
	}
	
	public int getCancelledVolume(){
		return Integer.valueOf(cancelledOrderVolume);
	}
	
	public String toString(){
		StringBuilder newString = new StringBuilder();
		newString.append(getUser());
		newString.append(" order: ");
		newString.append(getSide());
		newString.append(" ");
		newString.append(getRemainingVolume());
		newString.append(" ");
		newString.append(getProduct());
		newString.append(" at ");
		newString.append(getPrice().toString());
		newString.append(" (Original Vol: ");
		newString.append(getOriginalVolume());
		newString.append(", CXL'd Vol: ");
		newString.append(getCancelledVolume());
		newString.append("), ID: ");
		newString.append(getId());
		return newString.toString();		
	}
	
}