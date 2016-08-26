package exceptions;

public class DataValidationException extends Exception {

	private static final long serialVersionUID = 1L;

	public DataValidationException(String msg) {
		super(msg);
	}
}
