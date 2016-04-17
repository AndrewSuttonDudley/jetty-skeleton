package skeleton.model.rdbms;

import javax.persistence.Entity;

@Entity
public class User extends EntityBase {
	
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
