package publishers;

import price.Price;

public class MarketDataDTO {

	public String product;
	public Price buyPrice;
	public int buyVolume;
	public Price sellPrice;
	public int sellVolume;

	public MarketDataDTO(String uProduct, Price uBuyPrice, int uBuyVolume,
			Price uSellPrice, int uSellVolume) {
		product = uProduct;
		buyPrice = uBuyPrice;
		buyVolume = uBuyVolume;
		sellPrice = uSellPrice;
		sellVolume = uSellVolume;
	}

	public String toString() {
		String out = ("Product: ");
		out = out.concat(product);
		out = out.concat(", Buy Price: ");
		out = out.concat(buyPrice.toString());
		out = out.concat(", Buy Volume: ");
		out = out.concat(Integer.toString(buyVolume));
		out = out.concat(", Sell Price: ");
		out = out.concat(sellPrice.toString());
		out = out.concat(", Sell Volume: ");
		out = out.concat(Integer.toString(sellVolume));
		return out;
	}
}
