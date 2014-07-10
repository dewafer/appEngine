package wyq.appengine.component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import wyq.appengine.Component;
import wyq.appengine.ExceptionHandler;
import wyq.appengine.Factory;

/**
 * The component in the Repository may be a Proxy class, and if true, the
 * invocation of the Proxy class is handled here.
 * 
 * @author dewafer
 * 
 */
public class ComponentProxyHandler implements InvocationHandler, Component {

	/**
	 * *
	 */
	private static final long serialVersionUID = 6916777032196397091L;

	private ExceptionHandler exceptionHandler;

	@Override
	public Object invoke(Object proxyInstance, Method method, Object[] params)
			throws Throwable {
		Class<?> clazz = method.getDeclaringClass();
		String methodName = method.getName();
		String keyName = clazz.getName() + "." + methodName + ".impl";
		// Search for class.name.method.name.impl in default configure file.
		String implClazzName = Property.get().getProperty(keyName);
		if (implClazzName == null) {
			// Search for class.name.impl in default configure file.
			keyName = clazz.getName() + ".impl";
			implClazzName = Property.get().getProperty(keyName);
		}
		if (implClazzName == null) {
			// Search for method.impl in class's configure file.
			implClazzName = new Property(clazz).getProperty(methodName
					+ ".impl");
		}
		if (implClazzName == null) {
			// Search for impl in class's configure file.
			implClazzName = new Property(clazz).getProperty("impl");
		}
		if (implClazzName == null) {
			// throw new RuntimeException("No implements configuration!");
			exceptionHandler.handle(new UnsupportedOperationException(method
					.toString()));
		}
		if (implClazzName.length() == 0) {
			// impl key existed but no definition
			// do nothing just return the argument
			Class<?> returnType = method.getReturnType();
			if (params != null) {
				for (Object o : params) {
					if (returnType.isAssignableFrom(o.getClass())) {
						return returnType.cast(o);
					}
				}
			}
			return null;
		}

		Class<?> implClazz = null;
		Object implObj = null;
		try {
			try {
				implClazz = Class.forName(implClazzName);
			} catch (ClassNotFoundException e) {
				String defaultPakName = Property.get().getProperty(
						"ComponentFactory.defaultPackageName");
				implClazz = Class.forName(defaultPakName + "." + implClazzName);
			}
			implClazzName = implClazz.getName();
			if (Component.class.isAssignableFrom(implClazz)) {
				Class<? extends Component> implCompClz = implClazz
						.asSubclass(Component.class);
				implObj = Repository.get("ComponentFactoryProxy:"
						+ implClazzName, implCompClz);
			} else {
				throw new ClassNotFoundException();
			}
		} catch (ClassNotFoundException e) {
			@SuppressWarnings("unchecked")
			Factory<Component> f = Repository.get("Factory", Factory.class);
			implObj = f.manufacture(implClazzName, implClazz);
		}

		Object result = null;
		if (implObj != null) {
			if (implClazz == null) {
				implClazz = implObj.getClass();
			}

			// find same method in implObj
			Class<?>[] paramTypes = method.getParameterTypes();
			Method realMethod;
			try {
				realMethod = implClazz
						.getDeclaredMethod(methodName, paramTypes);
				realMethod.setAccessible(true);
				result = realMethod.invoke(implObj, params);
			} catch (Exception e) {
				exceptionHandler.handle(e);
			}
		}

		return result;
	}

	public ExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}

	public void setExceptionHandler(ExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}

}
