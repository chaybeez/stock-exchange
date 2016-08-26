package exceptions;

public class ProductAlreadyExistsException extends Exception{
	
	private static final long serialVersionUID = 1L;

	public ProductAlreadyExistsException(String msg) {
		super(msg);
	}
}
