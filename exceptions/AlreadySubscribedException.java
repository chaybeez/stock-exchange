package exceptions;

public class AlreadySubscribedException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public AlreadySubscribedException(String msg) {
		super(msg);
	}

}
