package skeleton.delegate;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import skeleton.delegate.i.IUserDelegate;
import skeleton.model.rdbms.User;
import skeleton.representation.UserRep;
import skeleton.service.i.IUserService;

@Service
public class UserDelegate implements IUserDelegate {
	
	@Autowired
	private IUserService userService;
	
	@Override
	public UserRep get(Long userId) {
		
		User user = userService.findOne(userId);
		if (user == null) {
			throw new WebApplicationException(Status.NOT_FOUND);
		}
		return get(user);
	}
	
	@Override
	public UserRep get(User user) {
		
		UserRep userRep = new UserRep();
		userRep.setName(user.getName());
		
		return userRep;
	}
}
