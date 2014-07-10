package wyq.appengine.component;

import wyq.appengine.Component;
import wyq.appengine.Factory;
import wyq.appengine.FactoryParameter;
import wyq.appengine.component.RepositoryInitFactory.CompInitFactoryParam;

/**
 * This is the very first initialization of the repository.
 * 
 * @author dewafer
 * 
 */
public class RepositoryInitFactory extends
		AbstractFactory<Object, CompInitFactoryParam> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1740371298282094781L;

	@SuppressWarnings("unchecked")
	@Override
	protected Object build(CompInitFactoryParam fparam) {
		Object component = fparam.compObject;

		if (component == null) {
			return null;
		}

		String factoryRegKey = "^" + getClass().getName().replace(".", "\\.")
				+ "\\.(\\w+Factory)$";
		for (String initFactoryName : Property.get().getProperties(
				factoryRegKey)) {
			Factory<Component> initFactory = Repository.get(initFactoryName,
					Factory.class);
			component = initFactory.manufacture(component);
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
