package exceptions;

public class InvalidTradableOperation extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidTradableOperation(String msg) {
		super(msg);
	}

}
