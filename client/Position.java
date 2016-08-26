package client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import exceptions.InvalidPriceOperation;
import exceptions.NoSuchProductException;
import price.Price;
import price.PriceFactory;
import tradables.BookSide;

public class Position {
	
	private HashMap<String, Integer> holdings;
	private Price accountCosts;
	private HashMap<String, Price> lastSales;
	
	public Position(){
		holdings = new HashMap<String, Integer>();
		accountCosts = PriceFactory.makeLimitPrice(0);
		lastSales = new HashMap<String, Price>();
	}
	
	
	public void updatePosition(String product, Price price, BookSide side, int volume) throws NoSuchProductException, InvalidPriceOperation{
		if (product == null || product.isEmpty()) {
			throw new NoSuchProductException("Bad product value passed.");}
		int adjustedVolume;
		if (side == BookSide.BUY){adjustedVolume = volume;}
		else {adjustedVolume = -volume;}
		if (!holdings.containsKey(product)){holdings.put(product, adjustedVolume);}
		else {
			int res = holdings.get(product);
			res = res + adjustedVolume;
			if (res == 0){holdings.remove(product);}
			else {holdings.put(product, res);}
		}
		Price totalPrice = price.multiply(volume);
		if (side == BookSide.BUY){accountCosts = accountCosts.subtract(totalPrice);}
		else {accountCosts = accountCosts.add(totalPrice);}
	}

	
	public void updateLastSale(String product, Price price) throws NoSuchProductException{
		if (product == null || product.isEmpty()) {
			throw new NoSuchProductException("Bad product value passed.");}
		if (lastSales == null){lastSales = new HashMap<String, Price>();}
		lastSales.put(product, price);
	}
	
	
	public int getStockPositionVolume(String product) throws NoSuchProductException{
		if (product == null || product.isEmpty()) {
			throw new NoSuchProductException("Bad product value passed.");}
		if (holdings.get(product) == null){return 0;}
		else {return holdings.get(product);}			
	}
	
	
	public ArrayList<String> getHoldings(){
		ArrayList<String> h = new ArrayList<>(holdings.keySet());
		Collections.sort(h);
		return h;
	}
	
	
	public Price getStockPositionValue(String product) throws NoSuchProductException, InvalidPriceOperation{
		if (product == null || product.isEmpty()) {
			throw new NoSuchProductException("Bad product value passed.");
		}
		Price lastPrice = PriceFactory.makeLimitPrice(0);
		if (!holdings.containsKey(product)) {
			return lastPrice;
		} else {
			if (lastSales.get(product) != null) {
				lastPrice = lastSales.get(product);} 
		}
		return lastPrice.multiply(holdings.get(product));		
	}
	
	
	public Price getAccountCosts(){
		return accountCosts;		
	}
	
	
	public Price getAllStockValue() throws NoSuchProductException, InvalidPriceOperation{
		Price result = PriceFactory.makeLimitPrice(0);
		for (String p : holdings.keySet()) {
			Price resPrice = getStockPositionValue(p);
			result = result.add(resPrice);			
		}
		return result;		
	}
	
	
	public Price getNetAccountValue() throws InvalidPriceOperation, NoSuchProductException{
		Price result = PriceFactory.makeLimitPrice(0);
		result = result.add(getAccountCosts());
		result = result.add(getAllStockValue());
		return result;		
	}
	
}
