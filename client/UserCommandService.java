package client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import price.Price;
import publishers.CurrentMarketPublisher;
import publishers.LastSalePublisher;
import publishers.MessagePublisher;
import publishers.TickerPublisher;
import tradables.BookSide;
import tradables.Order;
import tradables.Quote;
import tradables.TradableDTO;
import book.ProductService;
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
import exceptions.NotSubscribedException;
import exceptions.OrderNotFoundException;
import exceptions.UserNotConnectedException;

public final class UserCommandService {
	
	private HashMap<String, Long> connectedUserIds = new HashMap<String, Long>();
	private HashMap<String, User> connectedUsers = new HashMap<String, User>();
	private HashMap<String, Long> connectedTime = new HashMap<String, Long>();
	
	
	private volatile static UserCommandService ourInstance;

	public static UserCommandService getInstance() {
		if (ourInstance == null) {
			synchronized (UserCommandService.class) {
				if (ourInstance == null)
					ourInstance = new UserCommandService();
			}
		}
		return ourInstance;
	}

	private UserCommandService() {}
	
	
	private void verifyUser(String userName, long connId) throws InvalidUserOperation, UserNotConnectedException, InvalidConnectionIdException{
		if (userName == null || userName.isEmpty()) {
			throw new InvalidUserOperation("Bad username value passed.");}
		if (!connectedUserIds.containsKey(userName)){
			throw new UserNotConnectedException("Not connected. Username: " + userName);
		}
		if (!connectedUserIds.get(userName).equals(connId)){
			throw new InvalidConnectionIdException("Invalid Connection");
		}		
	}
	
	
	public synchronized long connect(User user) throws InvalidUserOperation, AlreadyConnectedException{
		if (user == null) {
			throw new InvalidUserOperation("Bad username value passed.");}
		if (connectedUserIds.containsKey(user.getUserName())){
			throw new AlreadyConnectedException("Already connected.");}
		long retTime = System.nanoTime();
		connectedUserIds.put(user.getUserName(), retTime);
		connectedUsers.put(user.getUserName(), user);
		connectedTime.put(user.getUserName(), System.currentTimeMillis());
		return retTime;		
	}
	
	
	public synchronized void disConnect(String userName, long connId) throws InvalidUserOperation, UserNotConnectedException, InvalidConnectionIdException{
		if (userName == null || userName.isEmpty()) {
			throw new InvalidUserOperation("Bad username value passed.");}
		verifyUser(userName, connId);
		connectedUserIds.remove(userName);
		connectedUsers.remove(userName);
		connectedTime.remove(userName);		
	}
	
	
	public String[][] getBookDepth(String userName, long connId, String product) throws InvalidUserOperation, NoSuchProductException, InvalidStringOperation, UserNotConnectedException, InvalidConnectionIdException{
		if (userName == null || userName.isEmpty()) {
			throw new InvalidUserOperation("Bad username value passed.");}
		if (product == null || product.isEmpty()) {
			throw new NoSuchProductException("Bad product value passed.");}
		verifyUser(userName, connId);
		return ProductService.getInstance().getBookDepth(product);		
	}
	
	
	public String getMarketState(String userName, long connId) throws InvalidUserOperation, UserNotConnectedException, InvalidConnectionIdException{
		if (userName == null || userName.isEmpty()) {
			throw new InvalidUserOperation("Bad username value passed.");}
		verifyUser(userName, connId);
		return ProductService.getInstance().getMarketState().toString();
	}
	
	
	public synchronized ArrayList<TradableDTO> getOrdersWithRemainingQty(String userName, long connId, String product) throws InvalidUserOperation, NoSuchProductException, UserNotConnectedException, InvalidConnectionIdException, InvalidStringOperation{
		if (userName == null || userName.isEmpty()) {
			throw new InvalidUserOperation("Bad username value passed.");}
		if (product == null || product.isEmpty()) {
			throw new NoSuchProductException("Bad product value passed.");}
		verifyUser(userName, connId);
		return ProductService.getInstance().getOrdersWithRemainingQty(userName, product);		
	}
	
	
	public ArrayList<String> getProducts(String userName, long connId) throws InvalidUserOperation, UserNotConnectedException, InvalidConnectionIdException{
		if (userName == null || userName.isEmpty()) {
			throw new InvalidUserOperation("Bad username value passed.");}
		verifyUser(userName, connId);
		ArrayList<String> results = ProductService.getInstance().getProductList();
		Collections.sort(results);
		return results;	
	}
	
	
	public String submitOrder(String userName, long connId, String product, Price price, int volume, BookSide side) throws Exception{
		if (userName == null || userName.isEmpty()) {
			throw new InvalidUserOperation("Bad username value passed.");}
		if (product == null || product.isEmpty()) {
			throw new NoSuchProductException("Bad product value passed.");}
		verifyUser(userName, connId);
		Order newOrder = new Order(userName, product, price, volume, side);
		return ProductService.getInstance().submitOrder(newOrder);		
	}
	
	
	public void submitOrderCancel(String userName, long connId, String product, BookSide side, String orderId) throws InvalidUserOperation, NoSuchProductException, UserNotConnectedException, InvalidConnectionIdException, InvalidMarketStateTransition, InvalidStringOperation, InvalidDtoOperation, OrderNotFoundException, InvalidPriceOperation, InvalidIntOperation, InvalidMessageOperation, InvalidTradableOperation, IOException{
		if (userName == null || userName.isEmpty()) {
			throw new InvalidUserOperation("Bad username value passed.");}
		if (product == null || product.isEmpty()) {
			throw new NoSuchProductException("Bad product value passed.");}
		if (orderId == null || orderId.isEmpty()) {
			throw new InvalidTradableOperation("Bad orderID value passed.");}		
		verifyUser(userName, connId);
		ProductService.getInstance().submitOrderCancel(product, side, orderId);		
	}
	
	
	public void submitQuote(String userName, long connId, String product, Price bPrice, int bVolume, Price sPrice, int sVolume) throws Exception{
		if (userName == null || userName.isEmpty()) {
			throw new InvalidUserOperation("Bad username value passed.");}
		if (product == null || product.isEmpty()) {
			throw new NoSuchProductException("Bad product value passed.");}
		verifyUser(userName, connId);
		Quote newQuote = new Quote(userName, product, bPrice, bVolume, sPrice, sVolume);
		ProductService.getInstance().submitQuote(newQuote);		
	}
	
	
	public void submitQuoteCancel(String userName, long connId, String product) throws InvalidUserOperation, NoSuchProductException, UserNotConnectedException, InvalidConnectionIdException, InvalidMarketStateTransition, InvalidStringOperation, InvalidPriceOperation, InvalidIntOperation, InvalidMessageOperation, InvalidDtoOperation{
		if (userName == null || userName.isEmpty()) {
			throw new InvalidUserOperation("Bad username value passed.");}
		if (product == null || product.isEmpty()) {
			throw new NoSuchProductException("Bad product value passed.");}
		verifyUser(userName, connId);
		ProductService.getInstance().submitQuoteCancel(userName, product);		
	}
	
	
	public void subscribeCurrentMarket(String userName, long connId, String product) throws InvalidUserOperation, UserNotConnectedException, InvalidConnectionIdException, NoSuchProductException, AlreadySubscribedException{
		if (userName == null || userName.isEmpty()) {
			throw new InvalidUserOperation("Bad username value passed.");}
		if (product == null || product.isEmpty()) {
			throw new NoSuchProductException("Bad product value passed.");}
		verifyUser(userName, connId);
		CurrentMarketPublisher.getInstance().subscribe(connectedUsers.get(userName), product);
	}
	
	
	public void subscribeLastSale(String userName, long connId, String product) throws InvalidUserOperation, UserNotConnectedException, InvalidConnectionIdException, NoSuchProductException, AlreadySubscribedException{
		if (userName == null || userName.isEmpty()) {
			throw new InvalidUserOperation("Bad username value passed.");}
		if (product == null || product.isEmpty()) {
			throw new NoSuchProductException("Bad product value passed.");}
		verifyUser(userName, connId);
		LastSalePublisher.getInstance().subscribe(connectedUsers.get(userName), product);		
	}
	
	
	public void subscribeMessages(String userName, long connId, String product) throws InvalidUserOperation, UserNotConnectedException, InvalidConnectionIdException, NoSuchProductException, AlreadySubscribedException{
		if (userName == null || userName.isEmpty()) {
			throw new InvalidUserOperation("Bad username value passed.");}
		if (product == null || product.isEmpty()) {
			throw new NoSuchProductException("Bad product value passed.");}
		verifyUser(userName, connId);
		MessagePublisher.getInstance().subscribe(connectedUsers.get(userName), product);		
	}
	
	
	public void subscribeTicker(String userName, long connId, String product) throws InvalidUserOperation, UserNotConnectedException, InvalidConnectionIdException, NoSuchProductException, AlreadySubscribedException{
		if (userName == null || userName.isEmpty()) {
			throw new InvalidUserOperation("Bad username value passed.");}
		if (product == null || product.isEmpty()) {
			throw new NoSuchProductException("Bad product value passed.");}
		verifyUser(userName, connId);
		TickerPublisher.getInstance().subscribe(connectedUsers.get(userName), product);		
	}
	
	
	public void unSubscribeCurrentMarket(String userName, long connId, String product) throws InvalidUserOperation, UserNotConnectedException, InvalidConnectionIdException, NoSuchProductException, NotSubscribedException{
		if (userName == null || userName.isEmpty()) {
			throw new InvalidUserOperation("Bad username value passed.");}
		if (product == null || product.isEmpty()) {
			throw new NoSuchProductException("Bad product value passed.");}
		verifyUser(userName, connId);
		CurrentMarketPublisher.getInstance().unSubscribe(connectedUsers.get(userName), product);
	}
	
	
	public void unSubscribeLastSale(String userName, long connId, String product) throws InvalidUserOperation, UserNotConnectedException, InvalidConnectionIdException, NoSuchProductException, NotSubscribedException{
		if (userName == null || userName.isEmpty()) {
			throw new InvalidUserOperation("Bad username value passed.");}
		if (product == null || product.isEmpty()) {
			throw new NoSuchProductException("Bad product value passed.");}
		verifyUser(userName, connId);
		LastSalePublisher.getInstance().unSubscribe(connectedUsers.get(userName), product);		
	}
	
	
	public void unSubscribeTicker(String userName, long connId, String product) throws InvalidUserOperation, UserNotConnectedException, InvalidConnectionIdException, NoSuchProductException, NotSubscribedException{
		if (userName == null || userName.isEmpty()) {
			throw new InvalidUserOperation("Bad username value passed.");}
		if (product == null || product.isEmpty()) {
			throw new NoSuchProductException("Bad product value passed.");}
		verifyUser(userName, connId);
		TickerPublisher.getInstance().unSubscribe(connectedUsers.get(userName), product);		
	}
	
	
	public void unSubscribeMessages(String userName, long connId, String product) throws InvalidUserOperation, UserNotConnectedException, InvalidConnectionIdException, NoSuchProductException, NotSubscribedException{
		if (userName == null || userName.isEmpty()) {
			throw new InvalidUserOperation("Bad username value passed.");}
		if (product == null || product.isEmpty()) {
			throw new NoSuchProductException("Bad product value passed.");}
		verifyUser(userName, connId);
		MessagePublisher.getInstance().unSubscribe(connectedUsers.get(userName), product);		
	}
	
	
}