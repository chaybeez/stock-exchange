package tradables;

import java.io.IOException;

import price.Price;

public interface Tradable {

	String getProduct();

	Price getPrice();

	int getOriginalVolume();

	int getRemainingVolume();

	int getCancelledVolume();

	void setCancelledVolume(int newCancelledVolume) throws IOException;

	void setRemainingVolume(int newRemainingVolume) throws IOException;

	String getUser();

	BookSide getSide();

	boolean isQuote();

	String getId();

}
