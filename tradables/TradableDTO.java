package tradables;

import price.Price;

public class TradableDTO {

	public String product;
	public Price price;
	public int originalOrderVolume;
	public int remainingOrderVolume;
	public int cancelledOrderVolume;
	public String userName;
	public BookSide bookSide;
	public boolean isQuote;
	public String id;

	public TradableDTO(String uProduct, Price uPrice, int uOriginalVolume,
			int uRemainingVolume, int uCancelledVolume, String uUser,
			BookSide uSide, boolean uQuote, String uId) {
		product = uProduct;
		price = uPrice;
		originalOrderVolume = uOriginalVolume;
		remainingOrderVolume = uRemainingVolume;
		cancelledOrderVolume = uCancelledVolume;
		userName = uUser;
		bookSide = uSide;
		isQuote = uQuote;
		id = uId;
	}

	public String toString() {
		String out = ("Product: ");
		out = out.concat(product);
		out = out.concat(", Price: ");
		out = out.concat(price.toString());
		out = out.concat(", OriginalVolume: ");
		out = out.concat(Integer.toString(originalOrderVolume));
		out = out.concat(", RemainingVolume: ");
		out = out.concat(Integer.toString(remainingOrderVolume));
		out = out.concat(", CancelledVolume: ");
		out = out.concat(Integer.toString(cancelledOrderVolume));
		out = out.concat(", User: ");
		out = out.concat(userName);
		out = out.concat(", Side: ");
		out = out.concat(bookSide.toString());
		out = out.concat(", IsQuote: ");
		out = out.concat(Boolean.toString(isQuote));
		out = out.concat(", Id: ");
		out = out.concat(id);
		return out;
	}

}
