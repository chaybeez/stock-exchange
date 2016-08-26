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

public final class TickerPublisher implements Publisher {

	private static HashMap<String, HashSet<User>> subscriptions = new HashMap<String, HashSet<User>>();
	private static HashMap<String, Price> values = new HashMap<String, Price>();

	private volatile static TickerPublisher ourInstance;

	public static TickerPublisher getInstance() {
		if (ourInstance == null) {
			synchronized (TickerPublisher.class) {
				if (ourInstance == null)
					ourInstance = new TickerPublisher();
			}
		}
		return ourInstance;
	}

	private TickerPublisher() {
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

	public synchronized void unSubscribe(User uUser, String uProduct)
			throws InvalidUserOperation, NotSubscribedException {
		if (uUser == null || uProduct == null) {
			throw new InvalidUserOperation("Null value passed.");
		}
		if (!subscriptions.get(uProduct).contains(uUser)) {
			throw new NotSubscribedException("Not Subscribed.");
		}
		subscriptions.get(uProduct).remove(uUser);
	}

	public synchronized void publishTicker(String product, Price p)
			throws InvalidStringOperation, NoSuchProductException {
		if (product == null) {
			throw new InvalidStringOperation("Null Product Passed.");
		}
		
		if (subscriptions.get(product) == null){
			return;
		}
			
		char direction = 0;
		if (values.get(product) == null) {
			direction = ' ';
		} else if (values.get(product).equals(p)) {
			direction = '=';
		} else if (values.get(product).lessThan(p)) {
			direction = ((char) 8593);
		} else if (values.get(product).greaterThan(p)) {
			direction = ((char) 8595);
		}
		values.put(product, p);

		if (p == null) {
			Price tPrice;
			tPrice = PriceFactory.makeLimitPrice("$0.00");
			for (User usr : subscriptions.get(product))
				usr.acceptTicker(product, tPrice, direction);
		} else
			for (User usr : subscriptions.get(product))
				usr.acceptTicker(product, p, direction);

	}

}
