package messages;

import GlobalConstants.MarketState;
import exceptions.InvalidStringOperation;

public class MarketMessage {

	private MarketState state;

	public MarketMessage(MarketState uState) throws InvalidStringOperation {
		state = uState;
	}	
	
	public MarketMessage(String uMessage) throws InvalidStringOperation {
		setState(uMessage);
	}

	private void setState(String sString) throws InvalidStringOperation {
		if (sString == null) {
			throw new InvalidStringOperation("Null String.");
		}
		String str = sString;
		str.replaceAll("\\s+", "");
		str.toUpperCase();
		if (str.equals("CLOSED")) {
			state = MarketState.CLOSED;
		}
		if (str.equals("PREOPEN")) {
			state = MarketState.PREOPEN;
		}
		if (str.equals("OPEN")) {
			state = MarketState.OPEN;
		} else
			throw new InvalidStringOperation("Invalid State.");
	}

	public String getState() {
		return state.toString();
	}

	public String toString() {
		String out = "";
		out = out.concat(getState());
		return out;
	}

}
