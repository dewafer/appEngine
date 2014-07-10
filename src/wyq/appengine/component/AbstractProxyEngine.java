package wyq.appengine.component;

import java.lang.reflect.InvocationHandler;

import wyq.appengine.Component;
import wyq.appengine.Factory;

public abstract class AbstractProxyEngine implements Component {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7021455746789026195L;

	public AbstractProxyEngine() {
		super();
	}

	public abstract InvocationHandler getHandler();

	@SuppressWarnings("unchecked")
	protected <T> T getProxyInstance(Class<? extends T> iface) {
		Factory<Object> proxyFactory = Repository.get("ProxyFactory",
				ProxyFactory.class);
		return (T) proxyFactory.manufacture(new Class<?>[] { iface },
				getHandler());

	}

}