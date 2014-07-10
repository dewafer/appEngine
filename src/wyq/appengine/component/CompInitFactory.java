package wyq.appengine.component;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import wyq.appengine.Component;
import wyq.appengine.ComponentField;
import wyq.appengine.FactoryParameter;
import wyq.appengine.component.CompInitFactory.CompInitFactoryParam;

/**
 * This factory initialize the components in the Repository. This factory do the
 * DI jobs. Inject objects from Repository to the Component being initializing
 * using methods start with 'set'. Use {@link wyq.appengine.ComponentField}
 * annotation to change the DI behavior.
 * 
 * @version 1
 * @author dewafer
 * 
 */
public class CompInitFactory extends
		AbstractFactory<Object, CompInitFactoryParam> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1740371298282094781L;

	@Override
	protected Object build(CompInitFactoryParam fparam) {
		Object component = fparam.compObject;

		if (component == null) {
			return null;
		}
		// ignore Proxy
		if (Proxy.isProxyClass(component.getClass())) {
			return component;
		}

		Class<?> compClass = component.getClass();
		for (Method m : compClass.getMethods()) {
			if (m.getName().startsWith("set")) {
				ComponentField meta = m.getAnnotation(ComponentField.class);
				if (meta != null && meta.ignore()) {
					continue;
				}
				Class<?>[] params = m.getParameterTypes();
				boolean ignored = (params.length == 0);
				for (Class<?> param : params) {
					if (!Component.class.isAssignableFrom(param)) {
						ignored = true;
						break;
					}
				}
				if (!ignored) {
					String[] compNames = new String[0];
					if (meta != null) {
						compNames = meta.name();
					}
					if (compNames.length > 0
							&& compNames.length != params.length) {
						throw new RuntimeException(
								"Error Component Name Length!");
					}
					if (compNames.length == 0) {
						compNames = new String[params.length];
						for (int i = 0; i < params.length; i++) {
							compNames[i] = params[i].getSimpleName();
						}
					}
					Object[] paramComps = new Object[params.length];
					for (int i = 0; i < params.length; i++) {
						String n = compNames[i];
						@SuppressWarnings("unchecked")
						Class<? extends Component> c = (Class<? extends Component>) params[i];
						paramComps[i] = Repository.get(n, c);
					}
					try {
						m.invoke(component, paramComps);
					} catch (Exception e) {
						exceptionHandler.handle(e);
					}
				}
			}
		}
		return component;
	}

	@Override
	protected Class<? extends CompInitFactoryParam> factoryParamType() {
		return CompInitFactoryParam.class;
	}

	class CompInitFactoryParam implements FactoryParameter {
		private Object compObject;

		public CompInitFactoryParam(Object compObject) {
			this.compObject = compObject;
		}

	}

}
