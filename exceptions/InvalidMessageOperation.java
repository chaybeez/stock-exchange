package exceptions;

public class InvalidMessageOperation extends Exception {
	
	private static final long serialVersionUID = 1L;

	public InvalidMessageOperation(String msg) {
		super(msg);
	}

}
