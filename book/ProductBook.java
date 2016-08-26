package book;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import GlobalConstants.MarketState;
import messages.CancelMessage;
import messages.FillMessage;
import exceptions.DataValidationException;
import exceptions.InvalidDtoOperation;
import exceptions.InvalidIntOperation;
import exceptions.InvalidMessageOperation;
import exceptions.InvalidPriceOperation;
import exceptions.InvalidStringOperation;
import exceptions.InvalidTradableOperation;
import exceptions.InvalidUserOperation;
import exceptions.NoSuchProductException;
import exceptions.OrderNotFoundException;
import price.Price;
import price.PriceFactory;
import publishers.CurrentMarketPublisher;
import publishers.LastSalePublisher;
import publishers.MarketDataDTO;
import publishers.MessagePublisher;
import tradables.BookSide;
import tradables.Order;
import tradables.Quote;
import tradables.Tradable;
import tradables.TradableDTO;

public class ProductBook {

	private String product;
	private ProductBookSide buy;
	private ProductBookSide sell;
	private String lastCurrentMarket;
	private HashSet<String> userQuotes = new HashSet<>();
	private HashMap<Price, ArrayList<Tradable>> oldEntries = new HashMap<Price, ArrayList<Tradable>>();
	
	
	public ProductBook(String uProduct) throws InvalidStringOperation{
		setProduct(uProduct);
		buy = new ProductBookSide(this, BookSide.BUY);
		sell = new ProductBookSide(this, BookSide.SELL);
	}
	
	private void setProduct(String p) throws InvalidStringOperation{
		if (p == null || p.isEmpty()){throw new InvalidStringOperation("setProduct - Bad/Null String.");}
		product = p;
	}
	
	
	public synchronized ArrayList<TradableDTO> getOrdersWithRemainingQty(String userName) throws InvalidUserOperation, InvalidStringOperation{
		if (userName == null || userName.isEmpty()){throw new InvalidStringOperation("getOrdersWithRemainingQty - Bad/Null String.");}
		ArrayList<TradableDTO> tempList = new ArrayList<TradableDTO>();
		ArrayList<TradableDTO> buyList = buy.getOrdersWithRemainingQty(userName);
		for (TradableDTO buys : buyList){tempList.add(buys);}
		ArrayList<TradableDTO> sellList = sell.getOrdersWithRemainingQty(userName);
		for (TradableDTO sells : sellList){tempList.add(sells);}
		return tempList;
	}
	
	
	public synchronized void checkTooLateToCancel(String orderId) throws InvalidStringOperation, OrderNotFoundException, InvalidPriceOperation, InvalidIntOperation, InvalidMessageOperation {
		if (orderId == null || orderId.isEmpty()) {
			throw new InvalidStringOperation("checkTooLateToCancel - Bad/Null String - InvalidStringOperation");}
		for (ArrayList<Tradable> uList : oldEntries.values()) {
			for (Tradable uTrader : uList) {
				if (uTrader.getId().equals(orderId)) {
					CancelMessage message = new CancelMessage(
							uTrader.getUser(), uTrader.getProduct(),
							uTrader.getPrice(), uTrader.getRemainingVolume(),
							"Too Late to Cancel", uTrader.getSide(),
							uTrader.getId());
					MessagePublisher.getInstance().publishCancel(message);
					return;
				} 
			}
		}
		throw new OrderNotFoundException("ProductBook.checkTooLateToCancel - Requested order could not be found.");
	}
	
	
	public synchronized String[ ][ ] getBookDepth(){
		String[][] bd = new String[2][];
		bd[0] = buy.getBookDepth();
		bd[1] = sell.getBookDepth();
		return bd;		
	}
	
	
	public synchronized MarketDataDTO getMarketData(){
		Price bestBuySidePrice = buy.topOfBookPrice();
		Price bestSellSidePrice = sell.topOfBookPrice();
		int bestBuySideVolume = buy.topOfBookVolume();
		int bestSellSideVolume = sell.topOfBookVolume();
		if(bestBuySidePrice == null){bestBuySidePrice = PriceFactory.makeLimitPrice(0);}
		if(bestSellSidePrice == null){bestSellSidePrice = PriceFactory.makeLimitPrice(0);}
		MarketDataDTO mDto = new MarketDataDTO(product, bestBuySidePrice, bestBuySideVolume, bestSellSidePrice, bestSellSideVolume);
		return mDto;
	}
	
	
	public synchronized void addOldEntry(Tradable t) throws InvalidTradableOperation, IOException{
		if (t == null){throw new InvalidTradableOperation ("Null Tradable Passed.");}
		Price price = t.getPrice();
		if (!oldEntries.containsKey(price)){
			ArrayList<Tradable> newList = new ArrayList<Tradable>();
			oldEntries.put(price, newList);}
		t.setRemainingVolume(0);
		t.setCancelledVolume(t.getRemainingVolume());
		ArrayList<Tradable> tempList = oldEntries.get(price);
		tempList.add(t);
		oldEntries.put(price, tempList);
	}	
	
	
	public synchronized void openMarket() throws InvalidPriceOperation, InvalidTradableOperation, InvalidMessageOperation, InvalidDtoOperation, InvalidStringOperation, InvalidUserOperation, DataValidationException, InvalidIntOperation, IOException, NoSuchProductException{
		Price buyPrice = buy.topOfBookPrice();
		Price sellPrice = sell.topOfBookPrice();
		if (buyPrice == null || sellPrice == null){return;}
		while (buyPrice.greaterOrEqual(sellPrice) || buyPrice.isMarket() || sellPrice.isMarket()){
			ArrayList<Tradable> topOfBuySide = buy.getEntriesAtPrice(buyPrice);
			HashMap<String, FillMessage> allFills = new HashMap<String, FillMessage>();
			ArrayList<Tradable> toRemove = new ArrayList<Tradable>();
			for (Tradable t : topOfBuySide){
				allFills = sell.tryTrade(t);
				if (t.getRemainingVolume() == 0){toRemove.add(t);}				
			}
			for (Tradable t : toRemove){
				buy.removeTradable(t);
			}
			updateCurrentMarket();
			Price lastSalePrice = determineLastSalePrice(allFills);
			int lastSaleVolume = determineLastSaleQuantity(allFills);
			LastSalePublisher lspInstance = LastSalePublisher.getInstance();
			lspInstance.publishLastSale(product, lastSalePrice, lastSaleVolume);
			buyPrice = buy.topOfBookPrice();
			sellPrice = sell.topOfBookPrice();
			if (buyPrice == null || sellPrice == null){break;}			
		}
	}
	
	
	public synchronized void closeMarket() throws InvalidDtoOperation, InvalidStringOperation, InvalidUserOperation, InvalidPriceOperation, InvalidIntOperation, InvalidMessageOperation, OrderNotFoundException, InvalidTradableOperation, IOException, NoSuchProductException{
		buy.cancelAll();
		sell.cancelAll();
		updateCurrentMarket();
	}
	
	
	public synchronized void cancelOrder(BookSide side, String orderId) throws InvalidStringOperation, InvalidDtoOperation, InvalidUserOperation, OrderNotFoundException, InvalidPriceOperation, InvalidIntOperation, InvalidMessageOperation, InvalidTradableOperation, IOException, NoSuchProductException{
		if (orderId == null || orderId.isEmpty()) {
			throw new InvalidStringOperation("cancelOrder - Bad/Null String.");}
		if (side == BookSide.BUY){buy.submitOrderCancel(orderId);}
		else {sell.submitOrderCancel(orderId);}
		updateCurrentMarket();		
	}
	
	
	public synchronized void cancelQuote(String userName) throws InvalidStringOperation, InvalidUserOperation, InvalidPriceOperation, InvalidIntOperation, InvalidMessageOperation, InvalidDtoOperation, NoSuchProductException{
		if (userName == null || userName.isEmpty()) {
			throw new InvalidStringOperation("cancelQuote - Bad/Null String.");}
		buy.submitQuoteCancel(userName);
		sell.submitQuoteCancel(userName);
		updateCurrentMarket();		
	}
	
	
	public synchronized void addToBook(Quote q) throws Exception{
		if (q == null) {throw new InvalidTradableOperation("Bad/Null Quote.");}
		if (q.getQuoteSide("SELL").getPrice().lessOrEqual(q.getQuoteSide("BUY").getPrice())){
			throw new DataValidationException("Price of the BUY or SELL side is less than or equal to zero");}
		if (q.getQuoteSide("BUY").getPrice().lessOrEqual(PriceFactory.makeLimitPrice(0)) || q.getQuoteSide("SELL").getPrice().lessOrEqual(PriceFactory.makeLimitPrice(0))){
			throw new DataValidationException("Illegal Price");}
		if (q.getQuoteSide("BUY").getOriginalVolume() <= 0 || q.getQuoteSide("SELL").getOriginalVolume() <= 0){
			throw new DataValidationException("Illegal Volume");}
		if (userQuotes.contains(q.getUserName())) {
			buy.removeQuote(q.getUserName());
			sell.removeQuote(q.getUserName());
			updateCurrentMarket();
		}
			addToBook(BookSide.BUY, q.getQuoteSide("BUY"));
			addToBook(BookSide.SELL, q.getQuoteSide("SELL"));
			userQuotes.add(q.getUserName());
			updateCurrentMarket();	
		}
	
	
	
	public synchronized void addToBook(Order o) throws InvalidTradableOperation, InvalidDtoOperation, InvalidStringOperation, InvalidUserOperation, InvalidPriceOperation, InvalidIntOperation, InvalidMessageOperation, DataValidationException, IOException, NoSuchProductException{
		if (o == null) {throw new InvalidTradableOperation("Bad Order.");}
		addToBook(o.getSide(), o);
		updateCurrentMarket();
	}
	
	
	public synchronized void updateCurrentMarket() throws InvalidDtoOperation, InvalidStringOperation, InvalidUserOperation, NoSuchProductException {
		Price bp = buy.topOfBookPrice();
		if (bp == null){bp = PriceFactory.makeLimitPrice(0);}
		Price sp = sell.topOfBookPrice();
		if (sp == null){sp = PriceFactory.makeLimitPrice(0);}
		String marketString = bp.toString() + buy.topOfBookVolume()	+ sp.toString() + sell.topOfBookVolume();
		if (!marketString.equals(lastCurrentMarket)) {
			MarketDataDTO tempMdDto = new MarketDataDTO(product,
					buy.topOfBookPrice(), buy.topOfBookVolume(),
					sell.topOfBookPrice(), sell.topOfBookVolume());
			CurrentMarketPublisher.getInstance().publishCurrentMarket(tempMdDto);
			lastCurrentMarket = marketString;
		}
	}

	
	private synchronized Price determineLastSalePrice(HashMap<String, FillMessage> fills) throws DataValidationException{
		if (fills == null) {throw new DataValidationException("Bad HashMap/Data.");}
		ArrayList<FillMessage> msgs = new ArrayList<>(fills.values());
		Collections.sort(msgs);
		return msgs.get(0).getPrice();		
	}
	
	
	private synchronized int determineLastSaleQuantity(HashMap<String, FillMessage> fills) throws DataValidationException{
		if (fills == null) {throw new DataValidationException("Bad HashMap/Data.");}
		ArrayList<FillMessage> msgs = new ArrayList<>(fills.values());
		Collections.sort(msgs);
		return msgs.get(0).getVolume();
	}
	
	
	
	private synchronized void addToBook(BookSide side, Tradable trd) throws InvalidTradableOperation, InvalidStringOperation, InvalidPriceOperation, InvalidIntOperation, InvalidMessageOperation, InvalidDtoOperation, InvalidUserOperation, DataValidationException, IOException, NoSuchProductException{
		if (trd == null) {
			throw new InvalidTradableOperation("Null Tradable Passed.");}
		if (ProductService.getInstance().getMarketState() == MarketState.PREOPEN){
			if (side == BookSide.BUY){
				buy.addToBook(trd);}
			else {sell.addToBook(trd);}
			return;}
		HashMap<String, FillMessage> allFills = null;
		if (side == BookSide.BUY){
			allFills = sell.tryTrade(trd);}
		else {
			allFills = buy.tryTrade(trd);}
		if (!allFills.isEmpty() && allFills != null){
			updateCurrentMarket();
			int tradedAmt = trd.getOriginalVolume() - trd.getRemainingVolume();
			Price lastSalePrice = determineLastSalePrice(allFills);
			LastSalePublisher.getInstance().publishLastSale(product, lastSalePrice, tradedAmt);			
		}
		if (trd.getRemainingVolume() > 0){
			if (trd.getPrice().isMarket()){				
				CancelMessage message = new CancelMessage(trd.getUser(), trd.getProduct(), 
						trd.getPrice(), trd.getRemainingVolume(), "Cancelled", trd.getSide(), trd.getId());
				MessagePublisher.getInstance().publishCancel(message);}
			else{
				if (side == BookSide.BUY){buy.addToBook(trd);}
				else {sell.addToBook(trd);}				
			}
		}
	}
	
	
	
}