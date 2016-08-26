package book;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import messages.MarketMessage;
import publishers.MarketDataDTO;
import publishers.MessagePublisher;
import tradables.BookSide;
import tradables.Order;
import tradables.Quote;
import tradables.TradableDTO;
import exceptions.AlreadySubscribedException;
import exceptions.DataValidationException;
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
import GlobalConstants.MarketState;

public final class ProductService {
	
	private HashMap<String, ProductBook> allBooks = new HashMap<String, ProductBook>();
	private MarketState marketState;
	private volatile static ProductService ourInstance;

	public static ProductService getInstance() {
		if (ourInstance == null) {
			synchronized (ProductService.class) {
				if (ourInstance == null)
					ourInstance = new ProductService();
			}
		}
		return ourInstance;
	}

	private ProductService() {
		marketState = MarketState.CLOSED;
	}
	
	
	public synchronized ArrayList<TradableDTO> getOrdersWithRemainingQty(String userName, String product) throws InvalidStringOperation, InvalidUserOperation{
		if (userName == null || userName.isEmpty() || product == null || product.isEmpty()) {
			throw new InvalidStringOperation("Bad string value passed.");}
		ProductBook pBook = allBooks.get(product);
		ArrayList<TradableDTO> returnList = pBook.getOrdersWithRemainingQty(userName);
		return returnList;		
	}
	
	
	public synchronized MarketDataDTO getMarketData(String product) throws InvalidStringOperation{
		if (product == null || product.isEmpty()) {
			throw new InvalidStringOperation("Bad string value passed.");}
		ProductBook pBook = allBooks.get(product);
		MarketDataDTO returnDTO = pBook.getMarketData();
		return returnDTO;
	}
	
	
	public synchronized MarketState getMarketState(){
		return marketState;
	}
	
	
	public synchronized String[][] getBookDepth(String product) throws InvalidStringOperation, NoSuchProductException{
		if (product == null || product.isEmpty()) {
			throw new InvalidStringOperation("Bad string value passed.");}
		if (allBooks.get(product) == null) {
			throw new NoSuchProductException("No Such Product.");}
		ProductBook pBook = allBooks.get(product);
		String[][] retList = pBook.getBookDepth();
		return retList;			
	}
	
	
	public synchronized ArrayList<String> getProductList(){
		return new ArrayList<String>(allBooks.keySet());		
	}
	
	
	public synchronized void setMarketState(MarketState ms) throws InvalidMarketStateTransition, InvalidStringOperation, InvalidMessageOperation, InvalidPriceOperation, InvalidTradableOperation, InvalidDtoOperation, InvalidUserOperation, DataValidationException, InvalidIntOperation, IOException, OrderNotFoundException, NoSuchProductException{
		if (ms == null){
			throw new InvalidMarketStateTransition("Null Market State.");}		
		if (getMarketState() == MarketState.OPEN && ms == MarketState.PREOPEN){
			throw new InvalidMarketStateTransition("Cannot change from Open to Pre-Open");}
		if (getMarketState() == MarketState.PREOPEN && ms == MarketState.CLOSED){
			throw new InvalidMarketStateTransition("Cannot change from Pre-Open to Closed");}
		if (getMarketState() == MarketState.CLOSED && ms == MarketState.OPEN){
			throw new InvalidMarketStateTransition("Cannot change from Closed to Open");}
		marketState = ms;
		MarketMessage message = new MarketMessage(marketState);
		MessagePublisher.getInstance().publishMarketMessage(message);
		if (marketState == MarketState.OPEN){
			for (ProductBook pBook : allBooks.values()){
				if (pBook != null){
				pBook.openMarket();}}
		}
		if (marketState == MarketState.CLOSED){
			for (ProductBook pBook : allBooks.values()){
				if (pBook != null){
				pBook.closeMarket();}}
		}		
	}
		
	public synchronized void createProduct(String product) throws InvalidStringOperation, AlreadySubscribedException{
		if (product == null || product.isEmpty()) {
			throw new InvalidStringOperation("Bad string value passed.");}
		if (allBooks.containsKey(product)){
			throw new AlreadySubscribedException("Product Already Exists.");}
		ProductBook pBook = new ProductBook(product);
		allBooks.put(product, pBook);			
	}
	
	
	public synchronized void submitQuote(Quote q) throws Exception{
		if (marketState == MarketState.CLOSED){
			throw new InvalidMarketStateTransition("Market is Closed,");}
		if (allBooks.get(q.getProduct()) == null) {
			throw new NoSuchProductException("No Such Product.");}
		ProductBook pBook =	allBooks.get(q.getProduct());
		pBook.addToBook(q);
	}
		

	public synchronized String submitOrder(Order o) throws InvalidMarketStateTransition, NoSuchProductException, InvalidTradableOperation, InvalidDtoOperation, InvalidStringOperation, InvalidUserOperation, InvalidPriceOperation, InvalidIntOperation, InvalidMessageOperation, DataValidationException, IOException{
		if (marketState == MarketState.CLOSED){
			throw new InvalidMarketStateTransition("Market is Closed,");}
		if (marketState == MarketState.PREOPEN && o.getPrice().isMarket()){
			throw new InvalidMarketStateTransition("You cannot submit MKT orders during PREOPEN.");}
		if (allBooks.get(o.getProduct()) == null) {
			throw new NoSuchProductException("No Such Product.");}
		
		ProductBook pBook = allBooks.get(o.getProduct());
		pBook.addToBook(o);
		return o.getId();
	}
	
	
	public synchronized void submitOrderCancel(String product, BookSide side, String orderId) throws InvalidMarketStateTransition, NoSuchProductException, InvalidStringOperation, InvalidDtoOperation, InvalidUserOperation, OrderNotFoundException, InvalidPriceOperation, InvalidIntOperation, InvalidMessageOperation, InvalidTradableOperation, IOException{
		if (marketState == MarketState.CLOSED){
			throw new InvalidMarketStateTransition("Market is Closed,");}
		if (allBooks.get(product) == null) {
			throw new NoSuchProductException("No Such Product.");}
		ProductBook pBook = allBooks.get(product);
		pBook.cancelOrder(side, orderId);
	}
	
	
	public synchronized void submitQuoteCancel(String userName, String product) throws NoSuchProductException, InvalidMarketStateTransition, InvalidStringOperation, InvalidUserOperation, InvalidPriceOperation, InvalidIntOperation, InvalidMessageOperation, InvalidDtoOperation{
		if (marketState == MarketState.CLOSED){
			throw new InvalidMarketStateTransition("Market is Closed,");}
		if (allBooks.get(product) == null) {
			throw new NoSuchProductException("No Such Product.");}
		ProductBook pBook = allBooks.get(product);
		pBook.cancelQuote(userName);
	}
	

}