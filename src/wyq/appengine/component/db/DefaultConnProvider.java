package wyq.appengine.component.db;

import wyq.appengine.Component;
import wyq.appengine.component.Property;

/**
 * This class provides the information which is need by the Connection using the
 * default configuration in the Repository.
 * 
 * @author dewafer
 * 
 */
public class DefaultConnProvider implements ConnectionProvider, Component {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7183065923265980462L;

	private String username;
	private String password;
	private String sqlConnProviderClass;
	private String connectionString;

	@Override
	public String getUser() {
		return username;
	}

	public DefaultConnProvider() {
		username = getProperty("DBEngine.username");
		password = getProperty("DBEngine.password");
		sqlConnProviderClass = getProperty("DBEngine.SqlConnProviderClass");
		connectionString = getProperty("DBEngine.ConnStr");
	}

	@Override
	public String getSqlConnProviderClass() {
		return sqlConnProviderClass;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getConnStr() {
		return connectionString;
	}

	private String getProperty(String propKey) {
		String value = Property.get().getProperty(propKey);
		if (value == null) {
			value = "";
		}
		return value;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getConnectionString() {
		return connectionString;
	}

	public void setConnectionString(String connectionString) {
		this.connectionString = connectionString;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setSqlConnProviderClass(String sqlConnProviderClass) {
		this.sqlConnProviderClass = sqlConnProviderClass;
	}
}
