package skeleton.service.i;

import skeleton.model.rdbms.Token;
import skeleton.model.rdbms.User;

public interface IUserService extends IService<User> {
	
	public User findOneByTokenValue(String tokenValue);
	
	public User findOneByToken(Token token);
}
