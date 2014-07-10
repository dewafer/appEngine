package wyq.appengine.component.datamodel;

import java.util.ArrayList;
import java.util.List;

import wyq.appengine.Component;

/**
 * This abstract class provided a Table-like interface of data, similar to
 * java.sql.ResultSet.
 * 
 * @author dewafer
 * @version 1
 * @param <T>
 */
public abstract class AbstractTable<T> implements Component {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2813426925249005743L;
	protected List<List<Object>> resultList = new ArrayList<List<Object>>();
	protected List<String> columnNames = new ArrayList<String>();
	protected List<Class<?>> columnTypes = new ArrayList<Class<?>>();
	private int current = -1;

	protected abstract void loadData(T dataSource) throws Exception;

	public Object getValue(String columnName) {
		if (isOutRange(current, 0, resultList.size()))
			return null;
		int col = columnNames.indexOf(columnName);
		List<Object> currentRow = resultList.get(current);
		if (isOutRange(col, 0, currentRow.size()))
			return null;
		return currentRow.get(col);
	}

	public Object getValue(int i) {
		if (isOutRange(current, 0, resultList.size()))
			return null;
		List<Object> currentRow = resultList.get(current);
		if (isOutRange(i, 0, currentRow.size()))
			return null;
		return currentRow.get(i);
	}

	public String getColumnName(int i) {
		if (isOutRange(i, 0, columnNames.size()))
			return null;
		return columnNames.get(i);
	}

	public Class<?> getColumnType(int i) {
		if (isOutRange(i, 0, columnTypes.size()))
			return null;
		return columnTypes.get(i);
	}

	public Class<?> getColumnType(String columnName) {
		int col = columnNames.indexOf(columnName);
		if (isOutRange(col, 0, columnTypes.size()))
			return null;
		return columnTypes.get(col);
	}

	public int getColumnCount() {
		return columnNames.size();
	}

	private boolean isOutRange(int num, int low, int max) {
		return num < low || max <= num;
	}

	public boolean next() {
		current++;
		if (current > resultList.size())
			current = resultList.size();
		return !isOutRange(current, 0, resultList.size());
	}

	public int size() {
		return resultList.size();
	}

	public boolean absolute(int row) {
		current = row;
		if (current > resultList.size())
			current = resultList.size();
		if (current < 0)
			current = -1;
		return !isOutRange(current, 0, resultList.size());
	}

	public void afterLast() {
		current = resultList.size();
	}

	public void beforeFirst() {
		current = -1;
	}

	public boolean first() {
		current = 0;
		return !isOutRange(current, 0, resultList.size());
	}

	public boolean isAfterLast() {
		return current == resultList.size();
	}

	public boolean isBeforeFirst() {
		return current == -1;
	}

	public boolean isFirst() {
		return current == 0;
	}

	public boolean isLast() {
		return current == resultList.size() - 1;
	}

	public boolean last() {
		current = resultList.size() - 1;
		return !isOutRange(current, 0, resultList.size());
	}

	public boolean previous() {
		current--;
		if (current < 0)
			current = -1;
		return !isOutRange(current, 0, resultList.size());
	}

	public boolean relative(int rows) {
		current += rows;
		if (current > resultList.size())
			current = resultList.size();
		if (current < 0)
			current = -1;
		return !isOutRange(current, 0, resultList.size());
	}

}
