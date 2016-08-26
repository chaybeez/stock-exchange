package messages;

import price.Price;
import tradables.BookSide;
import exceptions.InvalidIntOperation;
import exceptions.InvalidPriceOperation;
import exceptions.InvalidStringOperation;

public class FillMessage implements Comparable <FillMessage>  {
	
	private String user;
	private String product;
	private Price price;
	private int volume;
	private String details;
	private BookSide side;
	public String id;

	public FillMessage(String uUser, String uProduct, Price uPrice,
			int uVolume, String uDetails, BookSide uSide, String uId)
			throws InvalidStringOperation, InvalidPriceOperation,
			InvalidIntOperation {
		
		setUser(uUser);
		setProduct(uProduct);
		setPrice(uPrice);
		setVolume(uVolume);
		setDetails(uDetails);
		setSide(uSide);
		setId(uId);
	}

	
	public void setUser(String uUser) throws InvalidStringOperation {
		if (uUser == null || uUser.isEmpty()) {
			throw new InvalidStringOperation("Invalid User Name: " + uUser);
		}
		user = uUser;
	}

	public void setProduct(String uProduct) throws InvalidStringOperation {
		if (uProduct == null || uProduct.isEmpty()) {
			throw new InvalidStringOperation("Invalid Product Name: "
					+ uProduct);
		}
		product = uProduct;
	}

	public void setPrice(Price uPrice) throws InvalidPriceOperation {
		if (uPrice == null) {
			throw new InvalidPriceOperation("Invalid Price: "
					+ getPrice().toString());
		}
		price = uPrice;
	}

	public void setVolume(int uVolume) throws InvalidIntOperation {
		if (uVolume < 0) {
			throw new InvalidIntOperation("Invalid Volume: " + uVolume);
		}
		volume = uVolume;
	}

	public void setDetails(String uDetails) throws InvalidStringOperation {
		if (uDetails == null) {
			throw new InvalidStringOperation("Invalid Details: " + uDetails);
		}
		details = uDetails;
	}

	public void setSide(BookSide uSide) throws InvalidStringOperation {
		if (uSide != BookSide.BUY && uSide != BookSide.SELL) {
			throw new InvalidStringOperation("Invalid Side: " + uSide);
		}
		side = uSide;
	}

	public void setId(String uId) throws InvalidStringOperation {
		if (uId == null) {
			throw new InvalidStringOperation("Invalid ID: " + uId);
		}
		id = uId;
	}

	public String getUser() {
		return user;
	}

	public String getProduct() {
		return product;
	}

	public Price getPrice() {
		return price;
	}

	public int getVolume() {
		return volume;
	}

	public String getDetails() {
		return details;
	}

	public BookSide getSide() {
		return side;
	}

	public String getId() {
		return id;
	}

	public int compareTo(FillMessage fm) {
		return this.price.compareTo(fm.price);
	}
	
	
	public String toString() {
		String out = "User: ";
		out = out.concat(getUser());
		out = out.concat(", Product: ");
		out = out.concat(getProduct());
		out = out.concat(", Price: ");
		out = out.concat(getPrice().toString());
		out = out.concat(", Volume: ");
		out = out.concat(Integer.toString(getVolume()));
		out = out.concat(", Details: ");
		out = out.concat(getDetails());
		out = out.concat(", Side: ");
		out = out.concat(getSide().toString());
		return out;
	}
}
