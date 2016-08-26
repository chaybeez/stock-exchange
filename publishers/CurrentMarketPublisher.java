package publishers;

import java.util.HashMap;
import java.util.HashSet;

import price.Price;
import price.PriceFactory;
import client.User;
import exceptions.InvalidDtoOperation;
import exceptions.InvalidStringOperation;
import exceptions.InvalidUserOperation;
import exceptions.AlreadySubscribedException;
import exceptions.NoSuchProductException;
import exceptions.NotSubscribedException;

public final class CurrentMarketPublisher implements Publisher {

	private static HashMap<String, HashSet<User>> subscriptions = new HashMap<String, HashSet<User>>();

	private volatile static CurrentMarketPublisher ourInstance;

	public static CurrentMarketPublisher getInstance() {
		if (ourInstance == null) {
			synchronized (CurrentMarketPublisher.class) {
				if (ourInstance == null)
					ourInstance = new CurrentMarketPublisher();
			}
		}
		return ourInstance;
	}

	private CurrentMarketPublisher() {
	}

	
	public synchronized void subscribe(User uUser, String uProduct)	throws InvalidUserOperation, AlreadySubscribedException {
		if (uUser == null || uProduct == null) {
			throw new InvalidUserOperation("Null value passed.");
		}

		if (subscriptions.get(uProduct) == null) {
			HashSet<User> set = new HashSet<User>();			
			set.add(uUser);
			subscriptions.put(uProduct, set);
			return;
		}
				
		if (subscriptions.get(uProduct).contains(uUser)) {
			throw new AlreadySubscribedException("Already Subscribed.");
		}
		subscriptions.get(uProduct).add(uUser);
	}

	
	public synchronized void unSubscribe(User uUser, String uProduct) throws InvalidUserOperation, NotSubscribedException {
		if (uUser == null || uProduct == null) {
			throw new InvalidUserOperation("Null value passed.");
		}
		if (!subscriptions.get(uProduct).contains(uUser)) {
			throw new NotSubscribedException("Not Subscribed.");
		}
		subscriptions.get(uProduct).remove(uUser);
	}

	
	public synchronized void publishCurrentMarket(MarketDataDTO md)
			throws InvalidDtoOperation, InvalidStringOperation, InvalidUserOperation, NoSuchProductException {

		if (md == null) {
			throw new InvalidDtoOperation("Null DTO passed.");
		}
		if (subscriptions.get(md.product) == null){
			return;
		}
			
		
		Price pcmBuyPrice;
		Price pcmSellPrice;
		
		if (md.buyPrice == null) {
			pcmBuyPrice = PriceFactory.makeLimitPrice("$0.00");			
		}
			else pcmBuyPrice = md.buyPrice;
		if (md.sellPrice == null){
				pcmSellPrice = PriceFactory.makeLimitPrice("$0.00");
		}
		else pcmSellPrice = md.sellPrice;

		
		for (User usr : subscriptions.get(md.product)) {
			usr.acceptCurrentMarket(md.product, pcmBuyPrice, md.buyVolume,
					pcmSellPrice, md.sellVolume);
		}

	}
}
