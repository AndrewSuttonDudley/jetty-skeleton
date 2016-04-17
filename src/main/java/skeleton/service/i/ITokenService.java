package skeleton.service.i;

import skeleton.model.rdbms.Token;
import skeleton.model.rdbms.User;

public interface ITokenService extends IService<Token> {
	
	public Token getResponseToken(String requestAccessToken);
	
	public Boolean isTokenExpired(Token token);
	
	public Token findMostRecentByUser(User user);
}
