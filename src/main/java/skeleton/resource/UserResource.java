package skeleton.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import skeleton.delegate.i.IUserDelegate;
import skeleton.representation.UserRep;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Service
public class UserResource {
	
	@Autowired
	private IUserDelegate userDelegate;
	
	@GET
	@Path("/{userId}")
	public UserRep get(@PathParam("userId") long userId) {
		return userDelegate.get(userId);
	}
}
