package skeleton.delegate.i;

import skeleton.model.rdbms.User;
import skeleton.representation.UserRep;

public interface IUserDelegate {
	
	public UserRep get(Long userId);
	
	public UserRep get(User user);
}
