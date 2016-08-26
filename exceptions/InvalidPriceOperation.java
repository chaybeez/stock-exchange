package exceptions;

public class InvalidPriceOperation extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidPriceOperation(String msg) {
		super(msg);
	}

}
