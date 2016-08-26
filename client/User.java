package client;

import java.io.IOException;
import java.util.ArrayList;

import exceptions.AlreadyConnectedException;
import exceptions.AlreadySubscribedException;
import exceptions.InvalidConnectionIdException;
import exceptions.InvalidDtoOperation;
import exceptions.InvalidIntOperation;
import exceptions.InvalidMarketStateTransition;
import exceptions.InvalidMessageOperation;
import exceptions.InvalidPriceOperation;
import exceptions.InvalidStringOperation;
import exceptions.InvalidTradableOperation;
import exceptions.InvalidUserOperation;
import exceptions.NoSuchProductException;
import exceptions.OrderNotFoundException;
import exceptions.UserNotConnectedException;
import messages.CancelMessage;
import messages.FillMessage;
import price.Price;
import tradables.BookSide;
import tradables.TradableDTO;

public interface User {

	String getUserName();

	void acceptLastSale(String product, Price p, int v) throws NoSuchProductException;

	void acceptMessage(FillMessage fm) throws InvalidMessageOperation, NoSuchProductException, InvalidPriceOperation;

	void acceptMessage(CancelMessage cm) throws InvalidMessageOperation;

	void acceptMarketMessage(String message) throws InvalidStringOperation;

	void acceptTicker(String product, Price p, char direction) throws NoSuchProductException;

	void acceptCurrentMarket(String product, Price bp, int bv, Price sp, int sv) throws NoSuchProductException;
	
	void connect() throws InvalidUserOperation, AlreadyConnectedException, UserNotConnectedException, InvalidConnectionIdException;
	
	void disConnect() throws InvalidUserOperation, UserNotConnectedException, InvalidConnectionIdException;
	
	void showMarketDisplay() throws UserNotConnectedException, Exception;
	
	String submitOrder(String product, Price price, int volume, BookSide side) throws NoSuchProductException, Exception;
	
	void submitOrderCancel(String product, BookSide side, String orderId) throws NoSuchProductException, InvalidUserOperation, UserNotConnectedException, InvalidConnectionIdException, InvalidMarketStateTransition, InvalidStringOperation, InvalidDtoOperation, OrderNotFoundException, InvalidPriceOperation, InvalidIntOperation, InvalidMessageOperation, InvalidTradableOperation, IOException;
	
	void submitQuote(String product, Price buyPrice, int buyVolume, Price sellPrice, int sellVolume) throws Exception;
	
	void submitQuoteCancel(String product) throws InvalidUserOperation, NoSuchProductException, UserNotConnectedException, InvalidConnectionIdException, InvalidMarketStateTransition, InvalidStringOperation, InvalidPriceOperation, InvalidIntOperation, InvalidMessageOperation, InvalidDtoOperation;
	
	void subscribeCurrentMarket(String product) throws InvalidUserOperation, UserNotConnectedException, InvalidConnectionIdException, NoSuchProductException, AlreadySubscribedException;
	
	void subscribeLastSale(String product) throws InvalidUserOperation, UserNotConnectedException, InvalidConnectionIdException, NoSuchProductException, AlreadySubscribedException;
	
	void subscribeMessages(String product) throws InvalidUserOperation, UserNotConnectedException, InvalidConnectionIdException, NoSuchProductException, AlreadySubscribedException;
	
	void subscribeTicker(String product) throws InvalidUserOperation, UserNotConnectedException, InvalidConnectionIdException, NoSuchProductException, AlreadySubscribedException;
	
	Price getAllStockValue() throws NoSuchProductException, InvalidPriceOperation;
	
	Price getAccountCosts();
	
	Price getNetAccountValue() throws InvalidPriceOperation, NoSuchProductException;
	
	String[][] getBookDepth(String product) throws InvalidUserOperation, NoSuchProductException, InvalidStringOperation, UserNotConnectedException, InvalidConnectionIdException;
	
	String getMarketState() throws InvalidUserOperation, UserNotConnectedException, InvalidConnectionIdException;
	
	ArrayList<TradableUserData> getOrderIds();
	
	ArrayList<String> getProductList();
	
	Price getStockPositionValue(String sym) throws NoSuchProductException, InvalidPriceOperation;
	
	int getStockPositionVolume(String product) throws NoSuchProductException;
	
	ArrayList<String> getHoldings();
	
	ArrayList<TradableDTO> getOrdersWithRemainingQty(String product) throws InvalidUserOperation, NoSuchProductException, UserNotConnectedException, InvalidConnectionIdException, InvalidStringOperation;
	
}
