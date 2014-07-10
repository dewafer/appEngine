package wyq.appengine.component.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import wyq.appengine.Component;
import wyq.appengine.component.datamodel.Table;
import wyq.appengine.component.datamodel.TableDataSource;
import wyq.appengine.component.db.DBEngine.DBResult;

/**
 * This class provides a simple yet elegant way to access the DB. Access the DB
 * using select/insert/update/delete methods without any SQLs. This class is
 * implemented based on the DBEngine. The quote characters in the parameters
 * which may harm the DB is already escaped.
 * 
 * @author dewafer
 * 
 */
public class DBDriver implements Component {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3362145883070773767L;

	protected DBEngine engine = DBEngine.get();

	private static final String SQL_PREFIX_INSERT = "INSERT INTO ";
	private static final String SQL_PREFIX_SELECT = "SELECT ";
	private static final String SQL_PREFIX_UPDATE = "UPDATE ";
	private static final String SQL_PREFIX_DELETE = "DELETE FROM ";

	public int insert(String tblName, List<Map<String, Object>> lines) {

		engine.connect();

		// prepare SQL prefix
		StringBuilder sqlPrefix = new StringBuilder(SQL_PREFIX_INSERT);
		sqlPrefix.append(escapeQuote(tblName));

		int resultCount = 0;

		// go with each line
		for (Map<String, Object> entry : lines) {
			// prepare sql
			StringBuilder sql = new StringBuilder(sqlPrefix.toString());
			sql.append(" ( ");
			// key list
			List<String> keyList = new ArrayList<String>();
			Iterator<String> iterator = entry.keySet().iterator();
			while (iterator.hasNext()) {
				String key = escapeQuote(iterator.next());
				keyList.add(key);
				sql.append(key);
				if (iterator.hasNext()) {
					sql.append(" , ");
				}
			}
			sql.append(" ) VALUES (");
			for (int i = 0; i < keyList.size(); i++) {
				sql.append(" ? ");
				if (i != keyList.size() - 1) {
					sql.append(" , ");
				}
			}
			sql.append(" ) ");
			// set handler
			DBDriverHandler handler = new DBDriverHandler();
			handler.entry = entry;
			handler.keyList = keyList;
			engine.setHandler(handler);
			// go sql
			engine.executeSQL(sql.toString());
			// count
			resultCount += handler.result.getRowsCount();
		}

		engine.close();
		return resultCount;
	}

	public int insert(String tblName, Map<String, Object> values) {
		List<Map<String, Object>> v = new ArrayList<Map<String, Object>>(1);
		v.add(values);
		return insert(tblName, v);
	}

	public Table select(String tblName, List<String> colList,
			Map<String, Object> where, List<String> orderBy) {

		engine.connect();

		// prepare SQL
		StringBuilder sql = new StringBuilder(SQL_PREFIX_SELECT);
		if (colList != null) {
			Iterator<String> itr = colList.iterator();
			while (itr.hasNext()) {
				sql.append(escapeQuote(itr.next()));
				if (itr.hasNext()) {
					sql.append(" , ");
				}
			}
		} else {
			sql.append(" * ");
		}

		sql.append(" FROM ");
		sql.append(escapeQuote(tblName));

		List<String> keyList = new ArrayList<String>();
		if (where != null && where.size() > 0) {
			sql.append(" WHERE ");
			Iterator<String> iterator = where.keySet().iterator();
			while (iterator.hasNext()) {
				String key = escapeQuote(iterator.next());
				keyList.add(key);
				sql.append(key);
				sql.append(" = ?");
				if (iterator.hasNext()) {
					sql.append(" AND ");
				}
			}
		}

		if (orderBy != null && orderBy.size() > 0) {
			sql.append(" ORDER BY ");
			Iterator<String> itr = orderBy.iterator();
			while (itr.hasNext()) {
				sql.append(escapeQuote(itr.next()));
				if (itr.hasNext()) {
					sql.append(" , ");
				}
			}
		}

		// set handler
		DBDriverHandler handler = new DBDriverHandler();
		handler.entry = where;
		handler.keyList = keyList;
		engine.setHandler(handler);
		// go sql
		engine.executeSQL(sql.toString());
		// process result
		ResultSetTableSourceAdapter adapter = new ResultSetTableSourceAdapter();
		adapter.resultSet = handler.result.getResultSet();
		try {
			adapter.metaData = handler.result.getResultSet().getMetaData();
		} catch (SQLException e) {
			engine.getExceptionHandler().handle(e);
		}

		DBDriverResultTable tbl = new DBDriverResultTable();
		tbl.load(adapter);

		engine.close();

		return tbl;
	}

	public int update(String tblName, List<Map<String, Object>> setList,
			List<Map<String, Object>> whereList) {

		engine.connect();

		// prepare SQL prefix
		StringBuilder sqlPrefix = new StringBuilder(SQL_PREFIX_UPDATE);
		sqlPrefix.append(escapeQuote(tblName));
		sqlPrefix.append(" SET ");

		int resultCount = 0;

		for (int i = 0; i < setList.size(); i++) {
			if (i < whereList.size()) {
				StringBuilder sql = new StringBuilder(sqlPrefix.toString());
				DBDriverHandler handler = new DBDriverHandler();

				List<String> keyList = new ArrayList<String>();
				Map<String, Object> mergedEntry = new HashMap<String, Object>();
				Map<String, Object> setEntry = setList.get(i);
				Iterator<String> itr = setEntry.keySet().iterator();
				while (itr.hasNext()) {
					String key = escapeQuote(itr.next());
					keyList.add(key);
					sql.append(key);
					sql.append(" = ?");
					if (itr.hasNext()) {
						sql.append(" , ");
					}
				}
				mergedEntry.putAll(setEntry);

				Map<String, Object> whereEntry = whereList.get(i);
				if (whereEntry.size() > 0) {
					sql.append(" WHERE ");
					itr = whereEntry.keySet().iterator();
					while (itr.hasNext()) {
						String key = escapeQuote(itr.next());
						keyList.add(key);
						sql.append(key);
						sql.append(" = ?");
						if (itr.hasNext()) {
							sql.append(" AND ");
						}
					}
					mergedEntry.putAll(whereEntry);
				}
				handler.keyList = keyList;
				handler.entry = mergedEntry;

				engine.setHandler(handler);
				engine.executeSQL(sql.toString());

				resultCount += handler.result.getRowsCount();
			}
		}

		engine.close();
		return resultCount;
	}

	public int update(String tblName, Map<String, Object> set,
			Map<String, Object> where) {
		List<Map<String, Object>> s = new ArrayList<Map<String, Object>>(1);
		s.add(set);
		List<Map<String, Object>> w = new ArrayList<Map<String, Object>>(1);
		if (where == null) {
			where = new HashMap<String, Object>();
		}
		w.add(where);
		return update(tblName, s, w);
	}

	public int delete(String tblName, List<Map<String, Object>> whereList) {
		engine.connect();

		// prepare SQL prefix
		StringBuilder sqlPrefix = new StringBuilder(SQL_PREFIX_DELETE);
		sqlPrefix.append(escapeQuote(tblName));

		int resultCount = 0;

		// go with each line
		for (Map<String, Object> entry : whereList) {
			// prepare sql
			StringBuilder sql = new StringBuilder(sqlPrefix.toString());
			// key list
			List<String> keyList = new ArrayList<String>();
			if (entry.size() > 0) {
				sql.append(" WHERE ");
				Iterator<String> iterator = entry.keySet().iterator();
				while (iterator.hasNext()) {
					String key = escapeQuote(iterator.next());
					keyList.add(key);
					sql.append(key);
					sql.append(" = ?");
					if (iterator.hasNext()) {
						sql.append(" AND ");
					}
				}
			}
			// set handler
			DBDriverHandler handler = new DBDriverHandler();
			handler.entry = entry;
			handler.keyList = keyList;
			engine.setHandler(handler);
			// go sql
			engine.executeSQL(sql.toString());
			// count
			resultCount += handler.result.getRowsCount();
		}

		engine.close();
		return resultCount;
	}

	public int delete(String tblName, Map<String, Object> where) {
		List<Map<String, Object>> w = new ArrayList<Map<String, Object>>(1);
		if (where == null) {
			where = new HashMap<String, Object>();
		}
		w.add(where);
		return delete(tblName, w);
	}

	private String escapeQuote(String key) {
		if (key.contains("'")) {
			return key.replace('\'', ' ');
		} else {
			return key;
		}
	}

	public DBEngine getEngine() {
		return engine;
	}

	public void setEngine(DBEngine engine) {
		this.engine = engine;
	}

	class DBDriverHandler implements DBEngineHandler {

		/**
		 * 
		 */
		private static final long serialVersionUID = -4246612962971563779L;

		List<String> keyList;
		Map<String, Object> entry;
		DBResult result;

		@Override
		public void processResult(DBResult result) {
			this.result = result;
		}

		@Override
		public void prepareParameter(PreparedStatement stmt) {

			if (keyList != null && entry != null) {
				// set values
				for (int i = 0; i < keyList.size(); i++) {
					String key = keyList.get(i);
					Object value = entry.get(key);
					int jdbcType = Types.getJDBCType(value);
					try {
						stmt.setObject(i + 1, value, jdbcType);
					} catch (SQLException e) {
						engine.getExceptionHandler().handle(e);
					}
				}
			}

		}
	}

	class ResultSetTableSourceAdapter implements TableDataSource {

		ResultSet resultSet;
		ResultSetMetaData metaData;

		int currentColDef = 0;
		int currentCol = 0;

		@Override
		public boolean nextColumn() throws Exception {
			if (currentColDef < metaData.getColumnCount()) {
				currentColDef++;
				return true;
			} else {
				return false;
			}
		}

		@Override
		public String getColumnName() throws Exception {
			return metaData.getColumnLabel(currentColDef);
		}

		@Override
		public boolean nextRow() throws Exception {
			currentCol = 0;
			return resultSet.next();
		}

		@Override
		public Class<?> getColumnType() throws Exception {
			return Types.getJavaType(metaData.getColumnType(currentColDef));
		}

		@Override
		public boolean nextRowValue() throws Exception {
			if (currentCol < metaData.getColumnCount()) {
				currentCol++;
				return true;
			} else {
				return false;
			}
		}

		@Override
		public Object getRowValue() throws Exception {
			return resultSet.getObject(currentCol,
					Types.getJavaType(metaData.getColumnType(currentCol)));
		}

	}

	class DBDriverResultTable extends Table {
		/**
		 * 
		 */
		private static final long serialVersionUID = -6862038438939264146L;

		public void load(TableDataSource source) {
			try {
				this.loadData(source);
			} catch (Exception e) {
				engine.getExceptionHandler().handle(e);
			}
		}
	}
}
