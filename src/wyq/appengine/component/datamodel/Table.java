package wyq.appengine.component.datamodel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class Table extends AbstractTable<TableDataSource> implements
		Iterable<Map<String, Object>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7990328666836164483L;

	@Override
	protected void loadData(TableDataSource dataSource) throws Exception {
		while (dataSource.nextColumn()) {
			columnNames.add(dataSource.getColumnName());
			columnTypes.add(dataSource.getColumnType());
		}
		while (dataSource.nextRow()) {
			List<Object> dataRow = new ArrayList<Object>();
			while (dataSource.nextRowValue()) {
				dataRow.add(dataSource.getRowValue());
			}
			resultList.add(dataRow);
		}
	}

	@Override
	public Iterator<Map<String, Object>> iterator() {
		beforeFirst();
		return new Iterator<Map<String, Object>>() {

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

			@Override
			public Map<String, Object> next() {
				Map<String, Object> row = new LinkedHashMap<String, Object>();
				if (Table.this.next()) {
					for (int i = 0; i < Table.this.getColumnCount(); i++) {
						String key = Table.this.getColumnName(i);
						Object value = Table.this.getValue(i);
						row.put(key, value);
					}
				}
				return row;
			}

			@Override
			public boolean hasNext() {
				return !Table.this.isLast();
			}
		};
	}

	@Override
	public String toString() {
		String newLine = System.getProperty("line.separator");
		StringBuilder sb = new StringBuilder(super.toString() + "\\DATA=["
				+ newLine);
		Iterator<Map<String, Object>> iterator = this.iterator();
		while (iterator.hasNext()) {
			Map<String, Object> entry = iterator.next();
			Iterator<String> iterator2 = entry.keySet().iterator();
			while (iterator2.hasNext()) {
				String key = iterator2.next();
				Object objValue = entry.get(key);
				sb.append("[" + key + "=" + objValue + "]");
			}
			sb.append(System.getProperty("line.separator"));
		}
		sb.append("]");
		return sb.toString();
	}
}
