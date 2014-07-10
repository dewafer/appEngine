/**
 * 
 */
package wyq.appengine.component;

import java.util.ServiceLoader;

import wyq.appengine.Component;
import wyq.appengine.Factory;

/**
 * @author wangyq
 * 
 */
public class FactoryLoader implements Component {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1857384445153355673L;
	private static final String FACTORY_CLASS = "wyq.appengine.Factory";
	private Class<Factory<?>> serviceClass = null;

	@SuppressWarnings("unchecked")
	public FactoryLoader() throws ClassNotFoundException {
		serviceClass = (Class<Factory<?>>) Class.forName(FACTORY_CLASS);
	}

	public Factory<?> load(String serviceName) {
		ServiceLoader<Factory<?>> serviceLoader = ServiceLoader
				.load(serviceClass);
		if (serviceName == null || serviceLoader == null) {
			return null;
		}
		for (Factory<?> factory : serviceLoader) {
			if (serviceName.equals(factory.getName())) {
				return factory;
			}
		}
		return null;
	}

}
