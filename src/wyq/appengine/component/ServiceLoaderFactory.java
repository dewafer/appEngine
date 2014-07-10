package wyq.appengine.component;

import java.util.Iterator;
import java.util.ServiceLoader;

import wyq.appengine.Component;
import wyq.appengine.FactoryParameter;

public class ServiceLoaderFactory extends
		AbstractFactory<Object, ServiceLoaderFactory.Param> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -606539249229182814L;

	public static interface ServiceLoaderFilter extends Component {
		public abstract boolean filter(Object service);
	}

	public static class DefaultFilter implements ServiceLoaderFilter {

		/**
		 * 
		 */
		private static final long serialVersionUID = -1823319972306961411L;

		@Override
		public boolean filter(Object service) {
			// always return true;
			return true;
		}

	}

	private ServiceLoaderFilter filter;

	public class Param implements FactoryParameter {
		private Class<?> iface;

		public Param(Class<?> iface) {
			this.iface = iface;
		}

		public Param(Class<?> iface, ServiceLoaderFilter filter) {
			this.iface = iface;
			ServiceLoaderFactory.this.filter = filter;
		}

	}

	@Override
	protected Object build(Param param) {
		if (param.iface == null) {
			return null;
		}

		ServiceLoader<?> loader = ServiceLoader.load(param.iface);
		Iterator<?> iterator = loader.iterator();
		Object service = null;

		while (iterator.hasNext()) {
			Object next = iterator.next();
			if (filter != null) {
				if (filter.filter(next)) {
					service = next;
					break;
				}
			} else {
				service = next;
				break;
			}
		}

		return service;

	}

	@Override
	protected Class<? extends Param> factoryParamType() {
		return (Class<? extends Param>) Param.class;
	}

	/**
	 * @return the filter
	 */
	public ServiceLoaderFilter getFilter() {
		return filter;
	}

	/**
	 * @param filter
	 *            the filter to set
	 */
	public void setFilter(ServiceLoaderFilter filter) {
		this.filter = filter;
	}

}
