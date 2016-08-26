package book;

import exceptions.InvalidStringOperation;

public class TradeProcessorFactory {

	public TradeProcessorFactory() {
	}

	public static TradeProcessor createTradeProcessor(String type, ProductBookSide side)
			throws InvalidStringOperation {
		if (type == null || type.isEmpty()) {
			throw new InvalidStringOperation("Bad String Passed.");
		}
		if (type.equals("TradeProcessorPriceTimeImpl")) {
			return new TradeProcessorPriceTimeImpl(side);
		}
		return null;
	}

}
