package skeleton.model.rdbms;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Token extends EntityBase {
	
	@ManyToOne
	private User user;
	
	private String value;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
