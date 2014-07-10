package wyq.appengine.component;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import wyq.appengine.Component;
import wyq.appengine.ComponentField;
import wyq.appengine.FactoryParameter;
import wyq.appengine.component.DaoInitFactory.DaoInitFactoryParam;
import wyq.appengine.component.dao.DaoEngine;

/**
 * This initialize the DAO part of the component in the Repository.
 * 
 * @author dewafer
 * 
 */
public class DaoInitFactory extends
		AbstractFactory<Object, DaoInitFactoryParam> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1740371298282094781L;

	@Override
	protected Object build(DaoInitFactoryParam fparam) {
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
					if (Component.class.isAssignableFrom(param)
							|| !param.isInterface()) {
						ignored = true;
						break;
					}
				}
				if (!ignored) {
					Object[] paramComps = new Object[params.length];
					for (int i = 0; i < params.length; i++) {
						paramComps[i] = DaoEngine.get().getDao(params[i]);
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
	protected Class<? extends DaoInitFactoryParam> factoryParamType() {
		return DaoInitFactoryParam.class;
	}

	class DaoInitFactoryParam implements FactoryParameter {
		private Object compObject;

		public DaoInitFactoryParam(Object compObject) {
			this.compObject = compObject;
		}

	}

}
