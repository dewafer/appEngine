package wyq.appengine.component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import wyq.appengine.Component;
import wyq.appengine.FactoryParameter;
import wyq.appengine.component.ProxyFactory.ProxyFactoryParam;

/**
 * This factory produces the Proxy.
 * 
 * @author dewafer
 * 
 */
public class ProxyFactory extends AbstractFactory<Object, ProxyFactoryParam> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 528513640112684785L;

	@Override
	public FactoryParameter prepare(Object... values) {
		if (values == null || values.length != 2) {
			throw new IllegalArgumentException();
		}
		Class<?>[] interfaces = (Class<?>[]) values[0];
		InvocationHandler invocationHandler = (InvocationHandler) values[1];
		return new ProxyFactoryParam(interfaces, invocationHandler);
	}

	@Override
	protected Object build(ProxyFactoryParam param) {
		Class<?>[] ifaces = param.interfaces;
		InvocationHandler handler = param.invocationHandler;

		String proxyDelegateName = "ProxyDelegate:"
				+ handler.getClass().getName();
		if (!Proxy.isProxyClass(handler.getClass())) {
			proxyDelegateName += "#" + handler.hashCode();
		}
		ProxyDelegate proxyd = Repository.find(proxyDelegateName,
				ProxyDelegate.class);
		if (proxyd == null) {
			proxyd = new ProxyDelegate(ifaces, handler);
			Repository.put(proxyDelegateName, ProxyDelegate.class, proxyd);
		} else if (!proxyd.containsAll(ifaces)) {
			proxyd.renewInterfaces(ifaces);
		}
		return proxyd.getProxy();
	}

	@Override
	protected Class<ProxyFactoryParam> factoryParamType() {
		return ProxyFactoryParam.class;
	}

	class ProxyFactoryParam implements FactoryParameter {

		private Class<?>[] interfaces;
		private InvocationHandler invocationHandler;

		public ProxyFactoryParam(Class<?>[] interfaces,
				InvocationHandler invocationHandler) {
			this.interfaces = interfaces;
			this.invocationHandler = invocationHandler;
		}

	}

	class ProxyDelegate implements Component {
		/**
		 * 
		 */
		private static final long serialVersionUID = -1908967860513894069L;
		private Object proxy;
		private Set<Class<?>> proxyFaces = new HashSet<Class<?>>();

		public ProxyDelegate(Class<?>[] ifaces, InvocationHandler handler) {
			addAll(ifaces);
			proxy = newProxy(handler);
		}

		public Object getProxy() {
			return proxy;
		}

		private boolean addAll(Class<?>... arg0) {
			return Collections.addAll(proxyFaces, arg0);
		}

		public boolean containsAll(Class<?>... arg0) {
			List<Class<?>> list = Arrays.asList(arg0);
			return proxyFaces.containsAll(list);
		}

		private Class<?>[] toArray() {
			Class<?>[] arr = new Class<?>[proxyFaces.size()];
			return proxyFaces.toArray(arr);
		}

		public void renewInterfaces(Class<?>... arg0) {
			InvocationHandler handler = Proxy.getInvocationHandler(proxy);
			addAll(arg0);
			proxy = newProxy(handler);
		}

		private Object newProxy(InvocationHandler handler) {
			return Proxy.newProxyInstance(handler.getClass().getClassLoader(),
					toArray(), handler);
		}

	}

}
