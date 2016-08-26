package publishers;

import java.util.HashMap;
import java.util.HashSet;

import price.Price;
import price.PriceFactory;
import client.User;
import exceptions.AlreadySubscribedException;
import exceptions.InvalidStringOperation;
import exceptions.InvalidUserOperation;
import exceptions.NoSuchProductException;
import exceptions.NotSubscribedException;

public final class LastSalePublisher implements Publisher{

	private static HashMap<String, HashSet<User>> subscriptions = new HashMap<String, HashSet<User>>();
	
	private volatile static LastSalePublisher ourInstance;
	
	public static LastSalePublisher getInstance() {
		if (ourInstance == null) {
			synchronized (LastSalePublisher.class) {
				if (ourInstance == null)
					ourInstance = new LastSalePublisher();
			}
		}
		return ourInstance;
	}
	
	private LastSalePublisher() {
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
	
	
	public synchronized void publishLastSale(String product, Price p, int v) throws InvalidStringOperation, NoSuchProductException{
		if (product == null ){
			throw new InvalidStringOperation("Null Product Passed.");
		}
		
		if (subscriptions.get(product) == null){
			return;
		}
		
		if (p == null) {
			Price plsPrice;
			plsPrice = PriceFactory.makeLimitPrice("$0.00");
			for (User usr : subscriptions.get(product)) 
				usr.acceptLastSale(product, plsPrice, v);
				TickerPublisher.getInstance().publishTicker(product, plsPrice);
		}
		else
			for (User usr : subscriptions.get(product)) 
			usr.acceptLastSale(product, p, v);
			TickerPublisher.getInstance().publishTicker(product, p);
		}
		
}



