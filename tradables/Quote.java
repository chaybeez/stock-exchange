package tradables;

import java.io.IOException;

import exceptions.InvalidStringOperation;
import price.Price;

public class Quote {
	
	private String userName;
	private String product;
	private QuoteSide buy;
	private QuoteSide sell;
	
	public Quote(String userName, String productSymbol, Price buyPrice, int buyVolume, Price sellPrice, int	sellVolume) throws Exception {
		setUserName(userName);
		setProduct(productSymbol);
		buy = new QuoteSide(userName, productSymbol, buyPrice, buyVolume, BookSide.BUY);
		sell = new QuoteSide(userName, productSymbol, sellPrice, sellVolume, BookSide.SELL);		
	}
		
	private void setUserName(String uName) throws InvalidStringOperation{
		if (uName == null){throw new InvalidStringOperation("Null String.");}
		userName = uName;		
	}
	
	private void setProduct(String uProduct) throws InvalidStringOperation{
		if (uProduct == null){throw new InvalidStringOperation("Null String.");}
		product = uProduct;
	}
	
	public String getUserName(){
		return userName;
	}
	
	public String getProduct (){
		return product;
	}
	
	public QuoteSide getQuoteSide(String sideIn) throws Exception {
		if (sideIn == null) {throw new IOException("Bookside cannot be null.");}
		if (sideIn.equals("BUY")) {
			return new QuoteSide(buy.getUser(), buy.getProduct(),
					buy.getPrice(), buy.getOriginalVolume(),
					BookSide.BUY);
		} else {
			return new QuoteSide(sell.getUser(), sell.getProduct(),
					sell.getPrice(), sell.getOriginalVolume(), BookSide.SELL);
		}
	}
	
	public String toString(){
		String out = "";
		out = out.concat(getUserName());
		out = out.concat(" quote: ");
		out = out.concat(getProduct());
		out = out.concat(" ");
		out = out.concat(buy.getPrice().toString());
		out = out.concat(" x ");
		out = out.concat(Integer.toString(buy.getRemainingVolume()));
		out = out.concat(" (Original Vol: ");		
		out = out.concat(Integer.toString(buy.getOriginalVolume()));
		out = out.concat(", CXL'd Vol: ");
		out = out.concat(Integer.toString(buy.getCancelledVolume()));
		out = out.concat(") [");
		out = out.concat(buy.getOrderId());
		out = out.concat("] - ");
		out = out.concat(sell.getPrice().toString());
		out = out.concat(" x ");
		out = out.concat(Integer.toString(sell.getRemainingVolume()));
		out = out.concat(" (Original Vol: ");		
		out = out.concat(Integer.toString(sell.getOriginalVolume()));
		out = out.concat(", CXL'd Vol: ");
		out = out.concat(Integer.toString(sell.getCancelledVolume()));
		out = out.concat(") [");
		out = out.concat(sell.getOrderId());
		out = out.concat("]");
		return out;
	}
		
}
