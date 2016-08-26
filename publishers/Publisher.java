package publishers;

import client.User;
import exceptions.AlreadySubscribedException;
import exceptions.InvalidUserOperation;
import exceptions.NotSubscribedException;

public interface Publisher {
	
	public void subscribe(User uUser, String uProduct) throws InvalidUserOperation, AlreadySubscribedException;
	
	public void unSubscribe(User uUser, String uProduct) throws InvalidUserOperation, NotSubscribedException;
	
}
