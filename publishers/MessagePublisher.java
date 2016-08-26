package publishers;

import java.util.HashMap;
import java.util.HashSet;

import messages.CancelMessage;
import messages.FillMessage;
import messages.MarketMessage;
import client.User;
import exceptions.AlreadySubscribedException;
import exceptions.InvalidMessageOperation;
import exceptions.InvalidPriceOperation;
import exceptions.InvalidStringOperation;
import exceptions.InvalidUserOperation;
import exceptions.NoSuchProductException;
import exceptions.NotSubscribedException;

public final class MessagePublisher implements Publisher {
	
	private static HashMap<String, HashSet<User>> subscriptions = new HashMap<String, HashSet<User>>();
	
	private volatile static MessagePublisher ourInstance;
	
	public static MessagePublisher getInstance() {
		if (ourInstance == null) {
			synchronized (MessagePublisher.class) {
				if (ourInstance == null)
					ourInstance = new MessagePublisher();
			}
		}
		return ourInstance;
	}

	private MessagePublisher() {
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
	
	
	public synchronized void publishCancel(CancelMessage cm)
			throws InvalidMessageOperation {
		if (cm == null) {
			throw new InvalidMessageOperation("Invalid Message.");
		}
		if (subscriptions.get(cm.getProduct()) == null){
			return;
		}
		
		for (User usr : subscriptions.get(cm.getProduct())) {
			if (usr.getUserName() == cm.getUser()) {
				usr.acceptMessage(cm);
			}
		}
	}

	
	public synchronized void publishFill(FillMessage fm)
			throws InvalidMessageOperation, NoSuchProductException, InvalidPriceOperation {
		if (fm == null) {
			throw new InvalidMessageOperation("Invalid Message.");
		}
		if (subscriptions.get(fm.getProduct()) == null){
			return;
		}
		for (User usr : subscriptions.get(fm.getProduct())) {
			if (usr.getUserName() == fm.getUser()) {
				usr.acceptMessage(fm);
			}
		}
	}
	
	
	public synchronized void publishMarketMessage(MarketMessage mm) throws InvalidMessageOperation, InvalidStringOperation{
		if (mm == null) {
			throw new InvalidMessageOperation("Invalid Message.");
		}
		for (HashSet<User> userSet : subscriptions.values()){
			for (User usr : userSet){
				usr.acceptMarketMessage(mm.toString());
				}
		}
			
		
		
		
	}
	
	
	
	
	
	
	
	}
