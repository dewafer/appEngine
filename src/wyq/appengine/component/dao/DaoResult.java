package wyq.appengine.component.dao;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import wyq.appengine.component.datamodel.Table;
import wyq.appengine.component.datamodel.TableDataSource;
import wyq.appengine.component.db.DBEngine.DBResult;

/**
 * This Object wrapped the DBResult of DBEngine.
 * 
 * @author dewafer
 * @version 1
 */
public class DaoResult extends Table {

	/**
	 * 
	 */
	private static final long serialVersionUID = 797784647329137009L;
	protected int updateCount;
	protected boolean hasResult;

	protected DaoResult(DBResult result) throws Exception {
		loadData(result);
	}

	protected void loadData(DBResult result) throws Exception {
		updateCount = result.getRowsCount();
		hasResult = result.hasResultSet();

		if (hasResult) {
			final ResultSet resultSet = result.getResultSet();
			final ResultSetMetaData metaData = resultSet.getMetaData();

			loadData(new TableDataSource() {

				int col = 0;
				int rowCol = 0;

				@Override
				public boolean nextRowValue() throws SQLException {
					return ++rowCol <= metaData.getColumnCount();
				}

				@Override
				public boolean nextRow() throws SQLException {
					rowCol = 0;
					return resultSet.next();
				}

				@Override
				public boolean nextColumn() throws SQLException {
					return ++col <= metaData.getColumnCount();
				}

				@Override
				public Object getRowValue() throws SQLException {
					return resultSet.getObject(rowCol);
				}

				@Override
				public Class<?> getColumnType() {
					return null;
				}

				@Override
				public String getColumnName() throws SQLException {
					return metaData.getColumnLabel(col);
				}

			});

		}
	}

	@Override
	public int size() {
		if (hasResult) {
			return super.size();
		} else {
			return updateCount;
		}
	}

}
