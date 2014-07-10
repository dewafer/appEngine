/**
 * 
 */
package wyq.appengine.component.db;

import wyq.appengine.Component;

/**
 * @author wangyq
 * 
 */
public interface JDBCTypesMapper extends Component {

	public int getJDBCType(Class<?> c);

	public Class<?> getJavaType(int sqlType);
}
