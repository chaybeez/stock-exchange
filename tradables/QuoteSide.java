package tradables;


import java.io.IOException;

import exceptions.InvalidIntOperation;
import exceptions.InvalidPriceOperation;
import exceptions.InvalidStringOperation;
import price.Price;

public class QuoteSide implements Tradable {
	
	private String userName;
	private String product;
	private String orderId;
	private BookSide bookSide;
	private Price price;
	private String originalOrderVolume;
	private String remainingOrderVolume;
	private String cancelledOrderVolume;

	

	public QuoteSide(String uUserName, String uProductSymbol, Price uSidePrice, int uOriginalVolume, BookSide uSide) throws Exception  {
		cancelledOrderVolume = "0";
		setUserName(uUserName);
		setProduct(uProductSymbol);
		setBookSide(uSide);
		setPrice(uSidePrice);
		setOriginalOrderVolume(uOriginalVolume);
		setRemainingVolume(uOriginalVolume);
		setOrderId();
	}

	public QuoteSide(QuoteSide qs) throws Exception {
		setUserName(qs.getUser());
		setProduct(qs.getProduct());
		setBookSide(qs.getSide());
		setPrice(qs.getPrice());
		setOriginalOrderVolume(qs.getOriginalVolume());
		setRemainingVolume(qs.getRemainingVolume());
		setCancelledVolume(qs.getCancelledVolume());
		setOrderId();
	}
	
	private void setOrderId() {
		String s = getUser();
		s = s.concat(getProduct());
		s = s.concat(String.valueOf(System.nanoTime()));
		orderId = s;
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
	
	
	public String getOrderId(){
		return orderId;		
	}
	
	public boolean isQuote(){
		return true;
	}
	
	public String toString(){
		String out = getPrice().toString();
		out = out.concat(" x ");
		out = out.concat(String.valueOf(getOriginalVolume()));
		out = out.concat(" (Original Vol: ");
		out = out.concat(String.valueOf(getOriginalVolume()));
		out = out.concat(", CXL'd Vol: ");
		out = out.concat(String.valueOf(getCancelledVolume()));
		out = out.concat(") [");
		out = out.concat(orderId);
		out = out.concat("]");
		return out;		
	}

	@Override
	public String getProduct() {
		return product;
	}

	@Override
	public Price getPrice() {
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



	public String getUser(){
		return userName;
	}

	public BookSide getSide(){
		return bookSide;
		
	}

	public String getId(){
		return orderId;
	}
}
