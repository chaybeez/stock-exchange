package exceptions;

public class InvalidMarketStateTransition extends Exception{
	
	private static final long serialVersionUID = 1L;

	public InvalidMarketStateTransition(String msg) {
		super(msg);
	}

}
