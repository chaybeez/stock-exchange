package exceptions;

public class InvalidUserOperation extends Exception {
	
	private static final long serialVersionUID = 1L;

	public InvalidUserOperation(String msg) {
		super(msg);
	}

}
