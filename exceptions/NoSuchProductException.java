package exceptions;

public class NoSuchProductException extends Exception {

	private static final long serialVersionUID = 1L;

	public NoSuchProductException(String msg) {
		super(msg);
	}
}
