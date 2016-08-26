package exceptions;

public class NotSubscribedException extends Exception{
	
	private static final long serialVersionUID = 1L;

	public NotSubscribedException(String msg) {
		super(msg);
	}

}
