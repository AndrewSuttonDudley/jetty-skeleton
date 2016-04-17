package skeleton.config;

import org.hibernate.cfg.ImprovedNamingStrategy;

public class NamingStrategy extends ImprovedNamingStrategy {

	@Override
	public String classToTableName(String className) {
		String name = super.classToTableName(className);

		if ("s".equals(name.substring(name.length() - 1))) {
			return name + "es";

		} else if ("y".equals(name.substring(name.length() - 1))) {
			return name.substring(0, name.length() - 1) + "ies";

		} else if ("x".equals(name.substring(name.length() - 1))) {
			return name + "es";

		} else if ("ch".equals(name.substring(name.length() - 2))) {
			return name + "es";

		} else {
			return name + "s";
		}
	}

	@Override
	public String foreignKeyColumnName(String propertyName, String propertyEntityName, String propertyTableName, String referencedColumnName) {
		return super.foreignKeyColumnName(propertyName, propertyEntityName, propertyTableName, referencedColumnName) + "_id";
	}
}
