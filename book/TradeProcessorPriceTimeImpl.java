package book;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import price.Price;
import exceptions.InvalidIntOperation;
import exceptions.InvalidMessageOperation;
import exceptions.InvalidPriceOperation;
import exceptions.InvalidStringOperation;
import exceptions.InvalidTradableOperation;
import messages.FillMessage;
import tradables.Tradable;

public class TradeProcessorPriceTimeImpl implements TradeProcessor {

	private HashMap<String, FillMessage> fillMessages = new HashMap<String, FillMessage>();
	private ProductBookSide productBookSide;
	
	public TradeProcessorPriceTimeImpl(ProductBookSide side){
		productBookSide = side;
	}
	
	
	
	private String makeFillKey(FillMessage fm) throws InvalidMessageOperation{
		if (fm == null){
			throw new InvalidMessageOperation("Null Message Passed.");}
		String out = "";
		out = out.concat(fm.getUser());
		out = out.concat(fm.getId());
		out = out.concat(fm.getPrice().toString());
		return out;		
	}
	
	
	private boolean isNewFill(FillMessage fm) throws InvalidMessageOperation{
		if (fm == null){
			throw new InvalidMessageOperation("Null Message Passed.");}
		String fillKey = makeFillKey(fm);
		if (fillMessages.get(fillKey) == null){return true;}
		FillMessage oldFill = fillMessages.get(fillKey);
		if(oldFill.getSide() != fm.getSide()){return true;}
		if(oldFill.getId() != fm.getId()){return true;}
		return false;				
	}
	
	
	private void addFillMessage(FillMessage fm) throws InvalidMessageOperation, InvalidIntOperation, InvalidStringOperation, InvalidPriceOperation{
		if (fm == null){
			throw new InvalidMessageOperation("Null Message Passed.");}
		if (isNewFill(fm)){
			String fKey = makeFillKey(fm);
			fillMessages.put(fKey, fm);
		}
		String fKey = makeFillKey(fm);
		FillMessage oldFill = fillMessages.get(fKey);
		oldFill.setVolume(oldFill.getVolume() + fm.getVolume());
		oldFill.setUser(fm.getUser());
		oldFill.setProduct(fm.getProduct());
		oldFill.setPrice(fm.getPrice());
		oldFill.setDetails(fm.getDetails());
		oldFill.setSide(fm.getSide());
		oldFill.setId(fm.getId());		
	}
	
	
	@Override
	public HashMap<String, FillMessage> doTrade(Tradable trd) throws InvalidTradableOperation, InvalidStringOperation, InvalidPriceOperation, InvalidIntOperation, InvalidMessageOperation, IOException {
		if (trd == null) {throw new InvalidTradableOperation("Null Tradable Passed.");}
		fillMessages = new HashMap<String, FillMessage>();
		ArrayList<Tradable> tradedOut = new ArrayList<Tradable>();
		ArrayList<Tradable> entriesAtPrice = productBookSide.getEntriesAtTopOfBook();
		for (Tradable t : entriesAtPrice) {
			if (trd.getRemainingVolume() == 0) {break;}
			if (trd.getRemainingVolume() > 0) {
				if (trd.getRemainingVolume() >= t.getRemainingVolume()) {
					tradedOut.add(t);
					Price tPrice;
					if (t.getPrice().isMarket()) {tPrice = trd.getPrice();} 
					else {tPrice = t.getPrice();}
					FillMessage tMessage = new FillMessage(t.getUser(), t.getProduct(), tPrice, t.getRemainingVolume(), "leaving " + 0 , t.getSide(), t.getId());
					addFillMessage(tMessage);
					FillMessage trdMessage = new FillMessage (trd.getUser(), t.getProduct(), tPrice, t.getRemainingVolume(), "leaving " + (trd.getRemainingVolume() - t.getRemainingVolume()), trd.getSide(), trd.getId());
					addFillMessage(trdMessage);
					trd.setRemainingVolume(trd.getRemainingVolume() - t.getRemainingVolume());
					t.setRemainingVolume(0);
					productBookSide.getBookInstance().addOldEntry(t);
				} else {
					int remainder = t.getRemainingVolume() - trd.getRemainingVolume();
					Price tPrice;
					if (t.getPrice().isMarket()) {
						tPrice = trd.getPrice();
					} else {
						tPrice = t.getPrice();}
					FillMessage tMessage = new FillMessage(t.getUser(), t.getProduct(), tPrice, trd.getRemainingVolume(), "leaving " + remainder, t.getSide(), t.getId());
					addFillMessage(tMessage);
					FillMessage trdMessage = new FillMessage(trd.getUser(), t.getProduct(), tPrice, trd.getRemainingVolume(), "leaving " + 0, trd.getSide(), trd.getId());
					addFillMessage(trdMessage);
					trd.setRemainingVolume(0);
					t.setRemainingVolume(remainder);
					productBookSide.getBookInstance().addOldEntry(trd);
					break;
				}
			}
		}
		for (Tradable t : tradedOut) {
			entriesAtPrice.remove(t);
			if (entriesAtPrice.isEmpty()) {
				productBookSide.clearIfEmpty(productBookSide.topOfBookPrice());}
		}
		return fillMessages;
	}
	
	
	

}
