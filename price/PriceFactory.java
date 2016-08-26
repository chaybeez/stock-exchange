package price;

import java.util.HashMap;
import exceptions.InvalidStringOperation;

public class PriceFactory {

	private static final Price marketPrice = new Price();
	private static HashMap<Long, Price> prices = new HashMap<Long, Price>();

	public static Price makeLimitPrice(String value) throws InvalidStringOperation {
		if (value == null) {
			throw new InvalidStringOperation("Null value passed.");
		}
		String valueString = value.replaceAll("[$,]", "");
		double valueDouble = (Double.parseDouble(valueString)) * 100.0;
		long result = Math.round(valueDouble);
		return makeLimitPrice(result);
	}

	public static Price makeLimitPrice(long value) {
		if (prices.get(value) == null) {
			Price newPrice = new Price(value);
			prices.put(value, newPrice);
			return newPrice;
		}
		return prices.get(value);
	}

	public static Price makeMarketPrice() {
		return marketPrice;
	}

}
