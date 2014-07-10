package wyq.appengine.component.db;

import wyq.appengine.Component;

/**
 * This interface provide the least information a Connection needs.
 * 
 * @author dewafer
 * 
 */
public interface ConnectionProvider extends Component {
	public abstract String getSqlConnProviderClass();

	public abstract String getConnStr();

	public abstract String getUser();

	public abstract String getPassword();
}
