package price;
import java.text.DecimalFormat;

import exceptions.InvalidPriceOperation;

public class Price implements Comparable<Price> {
	
	private long price;
	private boolean market;
		
	public Price (long priceIn){
		price = priceIn;
		market = false;		
	}
	
	public Price (){
		market = true;		
	}
	
			
	public Price add(Price p) throws InvalidPriceOperation{
		if (this.isMarket() || p.isMarket()) {throw new InvalidPriceOperation("Cannot add a LIMIT price to a MARKET Price");}
		long result = this.price + p.price;
		Price returnPrice = PriceFactory.makeLimitPrice(result);
		return returnPrice;
	}
	
	public Price subtract(Price p) throws InvalidPriceOperation{
		if (this.isMarket() || p.isMarket()) {throw new InvalidPriceOperation("Cannot subtract a LIMIT price from a MARKET Price");}
		long result = this.price - p.price;
		Price returnPrice = PriceFactory.makeLimitPrice(result);
		return returnPrice;		
	}
	
	public Price multiply(int p) throws InvalidPriceOperation{
		if (this.isMarket()) {throw new InvalidPriceOperation("Cannot multiply a MARKET price");}
		long result = this.price * p;
		Price returnPrice = PriceFactory.makeLimitPrice(result);
		return returnPrice;		
	}

	public int compareTo(Price p){
		if (this.price > p.price) return +1;
		if (this.price < p.price) return -1;
		return 0;		
	}


	
	public boolean greaterOrEqual(Price p){
		if (this.isMarket() && p.isMarket()) return false;
		if (this.isMarket() && !p.isMarket()) return false;
		if (!this.isMarket() && p.isMarket()) return false;
		if (this.price >= p.price) return true;
		return false;
	}
	
	public boolean greaterThan(Price p){
		if (this.isMarket() && p.isMarket()) return false;
		if (this.isMarket() && !p.isMarket()) return false;
		if (!this.isMarket() && p.isMarket()) return false;
		if (this.price > p.price) return true;
		return false;
	}
	
	public boolean lessOrEqual(Price p){
		if (this.isMarket() && p.isMarket()) return false;
		if (this.isMarket() && !p.isMarket()) return false;
		if (!this.isMarket() && p.isMarket()) return false;
		if (this.price <= p.price) return true;
		return false;
	}
	
	public boolean lessThan(Price p){
		if (this.isMarket() && p.isMarket()) return false;
		if (this.isMarket() && !p.isMarket()) return false;
		if (!this.isMarket() && p.isMarket()) return false;
		if (this.price < p.price) return true;
		return false;		
	}
	
	public boolean equals(Price p){
		if (p == null || (getClass() != p.getClass())) {return false;}
		if (this.price != p.price) {return false;}
		return true;
	}
	
	public int hashCode() {
		int hash = 7;
		hash = 97 * hash + Long.valueOf(price).hashCode();
		return hash;
	}
	
		public boolean isMarket(){
		return this.market;
	}
	
	public boolean isNegative(){
		if (isMarket()) return false;
		if (this.price < 0) return true;
		return false;
	}
	
	
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (this.isMarket()) {
			sb.append("MKT");
			return sb.toString();
		} else {
			DecimalFormat format = new DecimalFormat("$#,##0.00;$-#,##0.00");
			String formatted = format.format(this.price / 100.0);
			return formatted;
		}

	}

}
