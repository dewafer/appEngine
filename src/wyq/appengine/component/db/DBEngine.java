package wyq.appengine.component.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import wyq.appengine.Component;
import wyq.appengine.ComponentField;
import wyq.appengine.ExceptionHandler;
import wyq.appengine.component.Repository;

/**
 * This class provides the fundamental access to the DB using the JDBC Driver
 * way. The access process is implemented through the DBEngineHandler and the
 * result is wrapped by the DBResult class.
 * 
 * @author dewafer
 * 
 */
public class DBEngine implements Component {

	/**
     * 
     */
	private static final long serialVersionUID = 7080149505747366739L;

	protected Connection conn;

	protected DBEngineHandler handler;

	protected ConnectionProvider provider;

	private ExceptionHandler exceptionHandler;

	public static DBEngine get() {
		return Repository.get("DBEngine", DBEngine.class);
	}

	public void connect() {
		try {
			if (conn == null || conn.isClosed()) {
				Class.forName(provider.getSqlConnProviderClass());
				conn = DriverManager.getConnection(provider.getConnStr(),
						provider.getUser(), provider.getPassword());
			}
		} catch (ClassNotFoundException e) {
			exceptionHandler.handle(e);
		} catch (SQLException e) {
			exceptionHandler.handle(e);
		}
	}

	public void executeSQL(String sql) {
		if (conn == null)
			return;
		try {
			Statement stmt = null;
			if (sql.contains("?") && handler != null) {
				PreparedStatement pstmt = conn.prepareStatement(sql);
				handlerCall(pstmt);
				stmt = pstmt;
			} else {
				stmt = conn.createStatement();
			}
			if (stmt instanceof PreparedStatement) {
				PreparedStatement pstmt = (PreparedStatement) stmt;
				pstmt.execute();
			} else {
				stmt.execute(sql);
			}
			if (handler != null) {
				ResultSet resultSet = stmt.getResultSet();
				int updateCount = stmt.getUpdateCount();
				DBResult result = new DBResult(updateCount, resultSet);
				handlerCall(result);
			}
			if (!conn.getAutoCommit()) {
				conn.commit();
			}
		} catch (SQLException e) {
			try {
				if (!conn.getAutoCommit()) {
					conn.rollback();
				}
			} catch (SQLException e1) {
				exceptionHandler.handle(e1);
			}
			exceptionHandler.handle(e);
		}
	}

	private void handlerCall(Object o) {
		if (handler == null) {
			return;
		}
		if (o instanceof PreparedStatement) {
			PreparedStatement p = (PreparedStatement) o;
			handler.prepareParameter(p);
		} else if (o instanceof DBResult) {
			DBResult r = (DBResult) o;
			handler.processResult(r);
		}
	}

	public void close() {
		try {
			if (conn != null && !conn.isClosed()) {
				conn.close();
			}
		} catch (SQLException e) {
			exceptionHandler.handle(e);
		}
	}

	public DBEngineHandler getHandler() {
		return handler;
	}

	public void setHandler(DBEngineHandler handler) {
		this.handler = handler;
	}

	public ConnectionProvider getProvider() {
		return provider;
	}

	@ComponentField(name = { "DBEngineConnectionProvider" })
	public void setProvider(ConnectionProvider provider) {
		this.provider = provider;
	}

	public class DBResult {
		private int rowsCount;
		private ResultSet resultSet;

		protected DBResult(int rowsCount, ResultSet resultSet) {
			this.rowsCount = rowsCount;
			this.resultSet = resultSet;
		}

		public int getRowsCount() {
			return rowsCount;
		}

		public ResultSet getResultSet() {
			return resultSet;
		}

		public boolean hasResultSet() {
			return this.resultSet != null;
		}

	}

	public ExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}

	public void setExceptionHandler(ExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}
}
