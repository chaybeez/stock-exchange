package book;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import messages.CancelMessage;
import messages.FillMessage;
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
import tradables.BookSide;
import tradables.Order;
import tradables.Tradable;
import tradables.TradableDTO;
import publishers.MessagePublisher;

public class ProductBookSide {

	private BookSide bookSide;
	private HashMap<Price, ArrayList<Tradable>> bookEntries = new HashMap< Price, ArrayList<Tradable>>();
	private TradeProcessor tradeProcessorInstance;
	private ProductBook bookInstance;
	
	
	public ProductBookSide(ProductBook uProdBook, BookSide uSide) throws InvalidStringOperation{
		bookSide = uSide;
		tradeProcessorInstance = TradeProcessorFactory.createTradeProcessor("TradeProcessorPriceTimeImpl", this);
		bookInstance = uProdBook;
	}

	
	public synchronized ArrayList<TradableDTO> getOrdersWithRemainingQty(String userName) throws InvalidUserOperation {
		if (userName == null || userName.isEmpty()) {
			throw new InvalidUserOperation("Bad username value passed.");}
		ArrayList<TradableDTO> remQuanList = new ArrayList<TradableDTO>();
		for (ArrayList<Tradable> uList : bookEntries.values()) {
			for (Tradable uTrader : uList) {
				if (uTrader.getRemainingVolume() > 0 && uTrader.getUser().equals(userName) && uTrader instanceof Order) {
					TradableDTO uOrder = new TradableDTO(uTrader.getProduct(),
							uTrader.getPrice(), uTrader.getOriginalVolume(),
							uTrader.getRemainingVolume(),
							uTrader.getCancelledVolume(), uTrader.getUser(),
							uTrader.getSide(), uTrader.isQuote(),
							uTrader.getId());
					remQuanList.add(uOrder);}
			}
		}
		return remQuanList;
	}
	
	
	synchronized ArrayList<Tradable> getEntriesAtTopOfBook() {
		if (bookEntries.isEmpty()) {return null;}
		ArrayList<Price> sorted = new ArrayList<Price>(bookEntries.keySet());
		Collections.sort(sorted);
		if (bookSide == BookSide.BUY) {
			Collections.reverse(sorted);}
		return bookEntries.get(sorted.get(0));
	}
	
	
    public synchronized String[] getBookDepth() {
        if (bookEntries.isEmpty()) {
            return new String[]{"<Empty>"};}
        ArrayList<String> output = new ArrayList<>();
        ArrayList<Price> sorted = new ArrayList<Price>(bookEntries.keySet());
        Collections.sort(sorted);
        if (bookSide == BookSide.BUY) {
            Collections.reverse(sorted);}
        for (Price bPrice : sorted) {
            ArrayList<Tradable> uList = bookEntries.get(bPrice);
            int total = 0;
            for (Tradable uTrade : uList) {
                total = total + uTrade.getRemainingVolume();
                String out = "";
                out = out.concat(bPrice.toString());
                out = out.concat(" x ");
                out = out.concat(Integer.toString(total));
                output.add(out);
            }
        }
        return output.toArray(new String[output.size()]);
    }
	
	
	synchronized ArrayList<Tradable> getEntriesAtPrice(Price price) throws InvalidPriceOperation{
		if (price == null) {
			throw new InvalidPriceOperation("Bad price value passed.");}
		if (bookEntries.get(price) == null) {return null;};
		return bookEntries.get(price);		
	}
	
	
	public synchronized boolean hasMarketPrice(){
		if (bookEntries.get(PriceFactory.makeMarketPrice()) != null){return true;}
		return false;
	}
	
	
	public synchronized boolean hasOnlyMarketPrice(){
		if (bookEntries.get(PriceFactory.makeMarketPrice()) != null && bookEntries.size() == 1){
			return true;}
		return false;
	}
	
	
	public synchronized Price topOfBookPrice(){
		if (bookEntries.isEmpty()) {return null;}
		ArrayList<Price> sorted = new ArrayList<Price>(bookEntries.keySet());
		Collections.sort(sorted);
		if (bookSide == BookSide.BUY) {
			Collections.reverse(sorted);}
		return sorted.get(0);
	}
	
	
	public synchronized int topOfBookVolume(){
		int total = 0;
		if (bookEntries.isEmpty()) {return 0;}
		ArrayList<Price> sorted = new ArrayList<Price>(bookEntries.keySet());
		Collections.sort(sorted);
		if (bookSide == BookSide.BUY) {
			Collections.reverse(sorted);}
		ArrayList<Tradable> uList = bookEntries.get(sorted.get(0));
		for (Tradable uTrade: uList){
			total = total + uTrade.getRemainingVolume();
		}
		return total;
	}
	
	
	public synchronized boolean isEmpty(){
		return bookEntries.isEmpty();		
	}
	
	
	public synchronized void cancelAll() throws InvalidStringOperation, InvalidUserOperation, InvalidPriceOperation, InvalidIntOperation, InvalidMessageOperation, OrderNotFoundException, InvalidTradableOperation, IOException{
		if (bookEntries.isEmpty()) {return;}
		for (ArrayList<Tradable> uList : bookEntries.values()) {
			ArrayList<Tradable> tmp = new ArrayList<>(uList); 
				for (Tradable uTrader : tmp) {
				if (uTrader != null){
				if (uTrader instanceof Order){
					submitOrderCancel(uTrader.getId());}
				else{
					submitQuoteCancel(uTrader.getUser());}
			}
			}	
		}
	}
	
	
	public synchronized TradableDTO removeQuote(String user) throws InvalidStringOperation, InvalidUserOperation, InvalidPriceOperation, InvalidIntOperation, InvalidMessageOperation{
		if (user == null || user.isEmpty()) {
			throw new InvalidStringOperation("Bad string value passed.");}
		if (bookEntries.isEmpty()) {
			return null;}
		for (ArrayList<Tradable> uList : bookEntries.values()) {
			ArrayList<Tradable> tmp = new ArrayList<>(uList);
			for (Tradable uTrader : tmp) {
				if (uTrader.getUser().equals(user)) {
					TradableDTO uOrder = new TradableDTO(uTrader.getProduct(),
							uTrader.getPrice(), uTrader.getOriginalVolume(),
							uTrader.getRemainingVolume(),
							uTrader.getCancelledVolume(), uTrader.getUser(),
							uTrader.getSide(), uTrader.isQuote(),
							uTrader.getId());
					bookEntries.get(uTrader.getPrice()).remove(uTrader); 
                    if (bookEntries.get(uTrader.getPrice()).isEmpty()) { 
                        bookEntries.remove(uTrader.getPrice());}
					return uOrder;
				}
			}
		}
		return null;
	}
	
		
	public synchronized void submitOrderCancel(String orderId) throws InvalidStringOperation, OrderNotFoundException, InvalidPriceOperation, InvalidIntOperation, InvalidMessageOperation, InvalidTradableOperation, IOException {
		if (orderId == null || orderId.isEmpty()) {
			throw new InvalidStringOperation("Bad string value passed.");}
		if (bookEntries.isEmpty()) {
			return;}
		
		for (ArrayList<Tradable> uList : bookEntries.values()) {
			ArrayList<Tradable> tmp = new ArrayList<>(uList);
			for (Tradable uTrader : tmp) {
				if (uTrader.getId().equals(orderId)) {
					bookEntries.get(uTrader.getPrice()).remove(uTrader);
					CancelMessage message = new CancelMessage(
							uTrader.getUser(), uTrader.getProduct(),
							uTrader.getPrice(), uTrader.getRemainingVolume(),
							"Order Cancelled", uTrader.getSide(), uTrader.getId());
					MessagePublisher.getInstance().publishCancel(message);
					addOldEntry(uTrader);
					if (bookEntries.get(uTrader.getPrice()) == null) {
						bookEntries.remove(uTrader.getPrice());}
					return;
					}
				}
			}
		bookInstance.checkTooLateToCancel(orderId);
	}
		

	public synchronized void submitQuoteCancel(String userName) throws InvalidUserOperation, InvalidStringOperation, InvalidPriceOperation, InvalidIntOperation, InvalidMessageOperation{
		if (userName == null || userName.isEmpty()) {
			throw new InvalidUserOperation("Bad username value passed.");}
		TradableDTO uOrder = removeQuote(userName);
		if (uOrder != null){
			String out = "Quote ";
			out = out.concat(uOrder.bookSide.toString());
			out = out.concat("-Side Cancelled");
			CancelMessage message = new CancelMessage(
					uOrder.userName, uOrder.product,
					uOrder.price, uOrder.remainingOrderVolume,
					out, uOrder.bookSide, uOrder.id);
			MessagePublisher.getInstance().publishCancel(message);
		}
	}

	
	public void addOldEntry(Tradable t) throws InvalidTradableOperation, IOException {
		if (t == null){
			throw new InvalidTradableOperation ("Null Tradable Passed.");}
		bookInstance.addOldEntry(t);		
	}
	
	
	public synchronized void addToBook(Tradable trd) throws InvalidTradableOperation{
		if (trd == null){
			throw new InvalidTradableOperation ("Null Tradable Passed.");}
		if (!bookEntries.containsKey(trd.getPrice())){
			ArrayList<Tradable> newList = new ArrayList<Tradable>();
			newList.add(trd);
			bookEntries.put(trd.getPrice(), newList);
		}else{
			ArrayList<Tradable> oldList = bookEntries.get(trd.getPrice());
			oldList.add(trd);
			bookEntries.put(trd.getPrice(), oldList);
		}
	}
	
	
	public HashMap<String, FillMessage> tryTrade(Tradable trd) throws InvalidTradableOperation, InvalidMessageOperation, InvalidStringOperation, InvalidPriceOperation, InvalidIntOperation, IOException, NoSuchProductException{
		if (trd == null){
			throw new InvalidTradableOperation ("Null Tradable Passed.");}
		HashMap<String, FillMessage> allFills = new HashMap<String, FillMessage>();
		if (bookSide == BookSide.BUY){
			allFills = trySellAgainstBuySideTrade(trd);}
		else if (bookSide == BookSide.SELL){
			allFills = tryBuyAgainstSellSideTrade(trd);}
		
		for (FillMessage fill : allFills.values()){
			MessagePublisher.getInstance().publishFill(fill);}
		return allFills;		
	}
	
	
	public synchronized HashMap<String, FillMessage> trySellAgainstBuySideTrade(Tradable trd) throws InvalidTradableOperation, InvalidStringOperation, InvalidPriceOperation, InvalidIntOperation, InvalidMessageOperation, IOException{
		if (trd == null){
			throw new InvalidTradableOperation ("Null Tradable Passed.");}
		HashMap<String, FillMessage> allFills = new HashMap<String, FillMessage>();
		HashMap<String, FillMessage> fillMsgs = new HashMap<String, FillMessage>();
		while ((trd.getRemainingVolume() > 0 && !isEmpty() && trd.getPrice().lessOrEqual(topOfBookPrice())
				|| (trd.getRemainingVolume() > 0 && !isEmpty() && trd.getPrice().isMarket()))){
					HashMap<String, FillMessage> someMsgs = tradeProcessorInstance.doTrade(trd);
					fillMsgs = mergeFills(fillMsgs, someMsgs);
					break;
					}
		
		allFills.putAll(fillMsgs);
		return allFills;
	}
	
	
	private HashMap<String, FillMessage> mergeFills(HashMap<String, FillMessage> existing, HashMap<String, FillMessage> newOnes) throws InvalidIntOperation, InvalidStringOperation{
		if (existing.isEmpty()){ return new HashMap<String, FillMessage>(newOnes);}
		HashMap<String, FillMessage> results = new HashMap<>(existing);
		for (String key : newOnes.keySet()) {
			if (!existing.containsKey(key)) {
				results.put(key, newOnes.get(key));
			} else {
				FillMessage fm = results.get(key);
				fm.setVolume(newOnes.get(key).getVolume());
				fm.setDetails(newOnes.get(key).getDetails());				
			}
		}
		return results;
	}
	
	
	public synchronized HashMap<String, FillMessage> tryBuyAgainstSellSideTrade(Tradable trd) throws InvalidTradableOperation, InvalidStringOperation, InvalidPriceOperation, InvalidIntOperation, InvalidMessageOperation, IOException {
		if (trd == null) {
			throw new InvalidTradableOperation("Null Tradable Passed.");}
		HashMap<String, FillMessage> allFills = new HashMap<String, FillMessage>();
		HashMap<String, FillMessage> fillMsgs = new HashMap<String, FillMessage>();
		while ((trd.getRemainingVolume() > 0 && !isEmpty() && trd.getPrice().greaterOrEqual(topOfBookPrice()) 
				|| (trd.getRemainingVolume() > 0 && !isEmpty() && trd.getPrice().isMarket()))){
				HashMap<String, FillMessage> someMsgs = tradeProcessorInstance.doTrade(trd);
				fillMsgs = mergeFills(fillMsgs, someMsgs);
				}
		allFills.putAll(fillMsgs);
		return allFills;
	}
	
	
	public synchronized void clearIfEmpty(Price p) throws InvalidPriceOperation{
		if (p == null) {
			throw new InvalidPriceOperation("Bad price value passed.");}
		if (bookEntries.get(p).isEmpty() || bookEntries.get(p) == null) {
			bookEntries.remove(p);
		}		
	}
	
	public synchronized void removeTradable(Tradable t) throws InvalidTradableOperation, InvalidPriceOperation{
		if (t == null) {
			throw new InvalidTradableOperation("Null Tradable Passed.");}
		ArrayList<Tradable> entries = bookEntries.get(t.getPrice());
		if (entries == null){return;}
		Boolean results = entries.remove(t);
		if(results == false){return;}
		if(entries.isEmpty()){clearIfEmpty(t.getPrice());}
	}
	
		
	public ProductBook getBookInstance(){
		return bookInstance;
	}
	
	
	
}