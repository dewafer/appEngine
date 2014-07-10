package wyq.appengine.component.db;

import java.sql.PreparedStatement;

import wyq.appengine.Component;
import wyq.appengine.component.db.DBEngine.DBResult;

/**
 * implement this interface to access the DB with DBEngine.
 * 
 * @author dewafer
 * 
 */
public interface DBEngineHandler extends Component {

	public abstract void prepareParameter(PreparedStatement stmt);

	public abstract void processResult(DBResult result);

}
