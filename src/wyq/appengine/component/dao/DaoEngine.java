package wyq.appengine.component.dao;

import java.lang.reflect.InvocationHandler;

import wyq.appengine.component.AbstractProxyEngine;
import wyq.appengine.component.Repository;

/**
 * This Engine provides an easy DAO object which can access the DB through the
 * JDBC Drivers. Simply use the DaoEngine.get() to get the DaoEngine with the
 * default configuration of the Repository.
 * 
 * @author dewafer
 * @version 1
 */
public class DaoEngine extends AbstractProxyEngine {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8705962428277588108L;

	protected DaoEngineHandler handler;

	public <T> T getDao(Class<? extends T> daoInterface) {
		return getProxyInstance(daoInterface);
	}

	public static DaoEngine get() {
		return Repository.get("DaoEngine", DaoEngine.class);
	}

	@Override
	public InvocationHandler getHandler() {
		return handler;
	}

	public void setHandler(DaoEngineHandler handler) {
		this.handler = handler;
	}
}
