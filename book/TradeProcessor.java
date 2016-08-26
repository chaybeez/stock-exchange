package book;

import java.io.IOException;
import java.util.HashMap;

import exceptions.InvalidIntOperation;
import exceptions.InvalidMessageOperation;
import exceptions.InvalidPriceOperation;
import exceptions.InvalidStringOperation;
import exceptions.InvalidTradableOperation;
import tradables.Tradable;
import messages.FillMessage;

public interface TradeProcessor {

	public HashMap<String, FillMessage> doTrade(Tradable trd) throws InvalidTradableOperation, InvalidStringOperation, InvalidPriceOperation, InvalidIntOperation, InvalidMessageOperation, IOException;

}
