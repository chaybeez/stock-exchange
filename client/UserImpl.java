package client;

import messages.CancelMessage;
import messages.FillMessage;
import price.Price;
import tradables.BookSide;
import tradables.TradableDTO;
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
import gui.UserDisplayManager;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;


public class UserImpl implements User{
	
	private String userName;
	private long connectionId;
	private ArrayList<String> listOfStocks;
	private ArrayList<TradableUserData> tradableUserDataList;
	private Position positionInstance;
	private UserDisplayManager userDisplayManagerInstance;
	
	
	public UserImpl(String uName) throws InvalidStringOperation{
		setUserName(uName);
		positionInstance = new Position();
		tradableUserDataList = new ArrayList<TradableUserData>();
		
	}

	
	
	private void setUserName(String u) throws InvalidStringOperation {
		if (u == null || u.isEmpty()){throw new InvalidStringOperation("Bad Username String.");}
		userName = u;		
	}
	
	
	@Override
	public String getUserName(){
		return userName;
	}
	
	
	@Override
	public void acceptLastSale(String product, Price price, int volume)	throws NoSuchProductException {
		if (product == null || product.isEmpty()) {
			throw new NoSuchProductException("Bad product value passed.");}
		try {
			userDisplayManagerInstance.updateLastSale(product, price, volume);
			positionInstance.updateLastSale(product, price);
		} catch (Exception ex) {
			System.out
					.println("acceptLastSale - Update caused an unexpected exception: " + ex.getMessage());
		}
	}
	
	
	@Override
	public void acceptMessage(FillMessage fm) throws InvalidMessageOperation, NoSuchProductException, InvalidPriceOperation{
		if (fm == null) {
			throw new InvalidMessageOperation("Bad product value passed.");}
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		String summary = ("{" + ts + "} Fill Message: " + fm.getSide().toString() + " " + fm.getVolume() + " "
				+ fm.getProduct() + " at " + fm.getPrice() + " leaving 0 [Tradable Id: " + fm.getId() + "]");
		try {
		userDisplayManagerInstance.updateMarketActivity(summary);
		positionInstance.updatePosition(fm.getProduct(), fm.getPrice(), fm.getSide(), fm.getVolume());
		} catch (Exception ex) {
			System.out
					.println("acceptMessage - Update caused an unexpected exception: " + ex.getMessage());
		}		
	}
	
	
	@Override
	public void acceptMessage(CancelMessage cm) throws InvalidMessageOperation{
		if (cm == null) {
			throw new InvalidMessageOperation("Bad product value passed.");}
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		String summary = ("{" + ts + "} Cancel Message: " + cm.getSide().toString() + " " + cm.getVolume() + " "
				+ cm.getProduct() + " at " + cm.getPrice() + cm.getSide().toString() + " Order Cancelled [Tradable Id: " + cm.getId() + "]");
		try {
			userDisplayManagerInstance.updateMarketActivity(summary);
		} catch (Exception ex) {
			System.out
					.println("acceptMessage - Update caused an unexpected exception: " + ex.getMessage());
		}
	}
	
	
	@Override
	public void acceptMarketMessage(String message) throws InvalidStringOperation{
		if (message == null || message.isEmpty()) {
			throw new InvalidStringOperation("Bad message passed.");}
		try {
			userDisplayManagerInstance.updateMarketState(message);
		} catch (Exception ex) {
			System.out
					.println("acceptMarketMessage - Update caused an unexpected exception: " + ex.getMessage());
		}		
	}
	
	
	@Override
	public void acceptTicker(String product, Price price, char direction) throws NoSuchProductException{
		if (product == null || product.isEmpty()) {
			throw new NoSuchProductException("Bad product value passed.");}
		try {
			userDisplayManagerInstance.updateTicker(product, price, direction);
		} catch (Exception ex) {
			System.out
					.println("acceptTicker - Update caused an unexpected exception: " + ex.getMessage());
		}
	}
	
	
	@Override
	public void acceptCurrentMarket(String product, Price bPrice, int bVolume, Price sPrice, int sVolume) throws NoSuchProductException{
		if (product == null || product.isEmpty()) {
			throw new NoSuchProductException("Bad product value passed.");}
		try {
			userDisplayManagerInstance.updateMarketData(product, bPrice, bVolume, sPrice, sVolume);
		} catch (Exception ex) {
			System.out
					.println("acceptCurrentMarket - Update caused an unexpected exception: " + ex.getMessage());
		}		
	}
	
	
	@Override
	public void connect() throws InvalidUserOperation, AlreadyConnectedException, UserNotConnectedException, InvalidConnectionIdException{
		connectionId = UserCommandService.getInstance().connect(this);
		listOfStocks = UserCommandService.getInstance().getProducts(userName, connectionId);
	}

	
	@Override
	public void disConnect() throws InvalidUserOperation, UserNotConnectedException, InvalidConnectionIdException {
		UserCommandService.getInstance().disConnect(userName, connectionId);	
	}


	@Override
	public void showMarketDisplay() throws UserNotConnectedException, Exception {
		if (listOfStocks == null){
			throw new UserNotConnectedException("User not connected");}
		if (userDisplayManagerInstance == null){
			userDisplayManagerInstance = new UserDisplayManager(this);}
		userDisplayManagerInstance.showMarketDisplay();
	}


	@Override
	public String submitOrder(String product, Price price, int volume, BookSide side) throws Exception {
		if (product == null || product.isEmpty()) {
			throw new NoSuchProductException("Bad product value passed.");}
		String sId = UserCommandService.getInstance().submitOrder(userName, connectionId, product, price, volume, side);
		TradableUserData tDto = new TradableUserData(userName, product, side, sId);
		if (tradableUserDataList == null){
			tradableUserDataList = new ArrayList<TradableUserData>();
		}
		tradableUserDataList.add(tDto);
		return sId;
	}


	@Override
	public void submitOrderCancel(String product, BookSide side, String orderId) throws NoSuchProductException, InvalidUserOperation, UserNotConnectedException, InvalidConnectionIdException, InvalidMarketStateTransition, InvalidStringOperation, InvalidDtoOperation, OrderNotFoundException, InvalidPriceOperation, InvalidIntOperation, InvalidMessageOperation, InvalidTradableOperation, IOException {
		if (product == null || product.isEmpty()) {
			throw new NoSuchProductException("Bad product value passed.");}
		UserCommandService.getInstance().submitOrderCancel(userName, connectionId, product, side, orderId);
	}


	@Override
	public void submitQuote(String product, Price buyPrice, int buyVolume, Price sellPrice, int sellVolume) throws Exception {
		if (product == null || product.isEmpty()) {
			throw new NoSuchProductException("Bad product value passed.");}
		UserCommandService.getInstance().submitQuote(userName, connectionId, product, buyPrice, buyVolume, sellPrice, sellVolume);
	}


	@Override
	public void submitQuoteCancel(String product) throws InvalidUserOperation, NoSuchProductException, UserNotConnectedException, InvalidConnectionIdException, InvalidMarketStateTransition, InvalidStringOperation, InvalidPriceOperation, InvalidIntOperation, InvalidMessageOperation, InvalidDtoOperation {
		if (product == null || product.isEmpty()) {
			throw new NoSuchProductException("Bad product value passed.");}
		UserCommandService.getInstance().submitQuoteCancel(userName, connectionId, product);
	}


	@Override
	public void subscribeCurrentMarket(String product) throws InvalidUserOperation, UserNotConnectedException, InvalidConnectionIdException, NoSuchProductException, AlreadySubscribedException {
		if (product == null || product.isEmpty()) {
			throw new NoSuchProductException("Bad product value passed.");}
		UserCommandService.getInstance().subscribeCurrentMarket(userName, connectionId, product);		
	}


	@Override
	public void subscribeLastSale(String product) throws InvalidUserOperation, UserNotConnectedException, InvalidConnectionIdException, NoSuchProductException, AlreadySubscribedException {
		if (product == null || product.isEmpty()) {
			throw new NoSuchProductException("Bad product value passed.");}
		UserCommandService.getInstance().subscribeLastSale(userName, connectionId, product);
	}


	@Override
	public void subscribeMessages(String product) throws InvalidUserOperation, UserNotConnectedException, InvalidConnectionIdException, NoSuchProductException, AlreadySubscribedException {
		if (product == null || product.isEmpty()) {
			throw new NoSuchProductException("Bad product value passed.");}
		UserCommandService.getInstance().subscribeMessages(userName, connectionId, product);
	}


	@Override
	public void subscribeTicker(String product) throws InvalidUserOperation, UserNotConnectedException, InvalidConnectionIdException, NoSuchProductException, AlreadySubscribedException {
		if (product == null || product.isEmpty()) {
			throw new NoSuchProductException("Bad product value passed.");}
		UserCommandService.getInstance().subscribeTicker(userName, connectionId, product);
	}


	@Override
	public Price getAllStockValue() throws NoSuchProductException, InvalidPriceOperation {
		return positionInstance.getAllStockValue();
	}


	@Override
	public Price getAccountCosts() {
		return positionInstance.getAccountCosts();
	}


	@Override
	public Price getNetAccountValue() throws InvalidPriceOperation, NoSuchProductException {
		return positionInstance.getNetAccountValue();
	}


	@Override
	public String[][] getBookDepth(String product) throws InvalidUserOperation, NoSuchProductException, InvalidStringOperation, UserNotConnectedException, InvalidConnectionIdException {
		if (product == null || product.isEmpty()) {
			throw new NoSuchProductException("Bad product value passed.");}
		return UserCommandService.getInstance().getBookDepth(userName, connectionId, product);
	}


	@Override
	public String getMarketState() throws InvalidUserOperation, UserNotConnectedException, InvalidConnectionIdException {
		return UserCommandService.getInstance().getMarketState(userName, connectionId);
	}


	@Override
	public ArrayList<TradableUserData> getOrderIds() {
		if (tradableUserDataList == null){
			tradableUserDataList = new ArrayList<TradableUserData>();
		}
		return tradableUserDataList;
	}


	@Override
	public ArrayList<String> getProductList() {
		return listOfStocks;
	}


	@Override
	public Price getStockPositionValue(String product) throws NoSuchProductException, InvalidPriceOperation {
		return positionInstance.getStockPositionValue(product);
	}


	@Override
	public int getStockPositionVolume(String product) throws NoSuchProductException {
		return positionInstance.getStockPositionVolume(product);
	}


	@Override
	public ArrayList<String> getHoldings() {
		return positionInstance.getHoldings();
	}


	@Override
	public ArrayList<TradableDTO> getOrdersWithRemainingQty(String product) throws InvalidUserOperation, NoSuchProductException, UserNotConnectedException, InvalidConnectionIdException, InvalidStringOperation {
		return UserCommandService.getInstance().getOrdersWithRemainingQty(userName, connectionId, product);
	}
	
	
}
