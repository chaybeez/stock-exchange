package exceptions;

public class InvalidDtoOperation extends Exception {

	private static final long serialVersionUID = 1L;

	public InvalidDtoOperation(String msg) {
		super(msg);
	}
}
