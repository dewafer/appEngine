package wyq.appengine.component;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import wyq.appengine.ExceptionHandler;
import wyq.appengine.Factory;
import wyq.appengine.FactoryParameter;

/**
 * This is the base abstract factory class of all the factories.
 * 
 * @author dewafer
 * 
 * @param <T>
 * @param <R>
 */
public abstract class AbstractFactory<T, R extends FactoryParameter> implements
		Factory<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2836098297625275172L;

	protected ExceptionHandler exceptionHandler;

	@Override
	public T factory(FactoryParameter parameterObject) {
		if (factoryParamType().isInstance(parameterObject)) {
			return build(factoryParamType().cast(parameterObject));
		} else {
			throw new IllegalArgumentException();
		}
	}

	protected abstract T build(R param);

	@Override
	public FactoryParameter prepare(Object... values) {
		Class<?> fparamType = factoryParamType();
		int fpConsTypesLength = values.length;
		Class<?>[] fpConsTypes = new Class<?>[fpConsTypesLength];
		for (int i = 0; i < fpConsTypesLength; i++) {
			Object v = values[i];
			if (v != null) {
				fpConsTypes[i] = v.getClass();
			}
		}

		Constructor<?> constructor;
		FactoryParameter p = null;
		Object[] initValues = values;
		try {
			if (fparamType.isMemberClass()) {
				List<Class<?>> fpConsTypeList = new ArrayList<Class<?>>();
				Collections.addAll(fpConsTypeList, fpConsTypes);
				fpConsTypeList.add(0, this.getClass());
				fpConsTypes = fpConsTypeList.toArray(fpConsTypes);

				List<Object> initValueList = new ArrayList<Object>();
				Collections.addAll(initValueList, values);
				initValueList.add(0, this);
				initValues = initValueList.toArray(initValues);
			}
			constructor = findConstructor(fparamType, fpConsTypes);
			if (constructor == null)
				throw new IllegalArgumentException();
			constructor.setAccessible(true);
			p = (FactoryParameter) constructor.newInstance(initValues);
		} catch (Exception e) {
			exceptionHandler.handle(e);
		}
		return p;
	}

	private Constructor<?> findConstructor(Class<?> fparamType,
			Class<?>[] fpConsTypes) {
		for (Constructor<?> c : fparamType.getDeclaredConstructors()) {
			if (c.getParameterTypes().length != fpConsTypes.length) {
				continue;
			}
			boolean skip = false;
			for (int i = 0; i < c.getParameterTypes().length; i++) {
				Class<?> cpt = c.getParameterTypes()[i];
				Class<?> fpConsType = fpConsTypes[i];
				if (fpConsType == null) {
					continue;
				}
				if (!equals(cpt, fpConsType)) {
					skip = true;
					break;
				}
			}
			if (!skip) {
				return c;
			}
		}
		return null;
	}

	private boolean equals(Class<?> cpt, Class<?> fpConsType) {
		Class<?> aCpt = autoBox(cpt);
		Class<?> afpConsType = autoBox(fpConsType);

		if (aCpt.equals(afpConsType)) {
			return true;
		} else {
			return cpt.isAssignableFrom(afpConsType);
		}
	}

	private Class<?> autoBox(Class<?> c) {
		// 8 elements
		// Boolean.TYPE, Character.TYPE, Byte.TYPE, Short.TYPE,
		// Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE
		if (Boolean.TYPE.equals(c)) {
			return Boolean.class;
		} else if (Character.TYPE.equals(c)) {
			return Character.class;
		} else if (Byte.TYPE.equals(c)) {
			return Byte.class;
		} else if (Short.TYPE.equals(c)) {
			return Short.class;
		} else if (Integer.TYPE.equals(c)) {
			return Integer.class;
		} else if (Long.TYPE.equals(c)) {
			return Long.class;
		} else if (Float.TYPE.equals(c)) {
			return Float.class;
		} else if (Double.TYPE.equals(c)) {
			return Double.class;
		} else {
			return c;
		}
	}

	protected abstract Class<? extends R> factoryParamType();

	@Override
	public T manufacture(Object... values) {
		return factory(prepare(values));
	}

	public ExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}

	public void setExceptionHandler(ExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}

	@Override
	public String getName() {
		return getClass().getName();
	}
}
