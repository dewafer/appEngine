package wyq.appengine.component.db;

import wyq.appengine.component.Repository;

/**
 * This class tells us the difference between the java types and the JDBC types.
 * 
 * @author dewafer
 * 
 */
public class Types {

	public static int getJDBCType(Class<?> c) {
		JDBCTypesMapper mapper = Repository.get("JDBCTypesMapper",
				JDBCTypesMapper.class);
		return mapper.getJDBCType(c);
	}

	public static int getJDBCType(Object o) {
		if (o == null) {
			return getJDBCType(void.class);
		} else {
			return getJDBCType(o.getClass());
		}
	}

	public static Class<?> getJavaType(int sqlType) {
		JDBCTypesMapper mapper = Repository.get("JDBCTypesMapper",
				JDBCTypesMapper.class);
		return mapper.getJavaType(sqlType);
	}

}
