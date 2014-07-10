package wyq.appengine.component.bean;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import wyq.appengine.Convertor;
import wyq.appengine.FactoryParameter;
import wyq.appengine.component.AbstractFactory;
import wyq.appengine.component.bean.BeanFactory.BeanFactoryParameter;

/**
 * Set the bean's values from BeanDataSource by using setter methods
 * automatically. This factory will set the values by using the bean's setters
 * first, and then by using the fields of the bean only when then BeanField
 * annotation is presented.
 * 
 * @author dewafer
 * @version 1
 */
public class BeanFactory extends AbstractFactory<Object, BeanFactoryParameter> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1073671067251961068L;

	private BeanDataSource dataSource;

	private Convertor convertor;

	@Override
	protected Object build(BeanFactoryParameter param) {
		Class<?> beanClass = param.beanClass;
		if (param.dataSource != null) {
			dataSource = param.dataSource;
		}

		// check bean class
		if (!beanClass.isAnnotationPresent(Bean.class)) {
			throw new RuntimeException(new IllegalArgumentException());
		}

		// create bean object
		Object bean = null;
		try {
			bean = beanClass.newInstance();
		} catch (Exception e) {
			exceptionHandler.handle(e);
		}

		// set values
		if (bean != null) {
			// set Setters first
			for (Method m : beanClass.getMethods()) {
				String methodName = m.getName();
				Class<?>[] paramClassTypes = m.getParameterTypes();
				int paramLength = paramClassTypes.length;

				if (methodName.startsWith("set") && paramLength > 0) {
					BeanField beanField = m.getAnnotation(BeanField.class);
					List<String> beanFieldNames = new ArrayList<String>(
							paramLength);
					if (beanField == null) {
						beanFieldNames.add(methodName.substring(3));
					} else {
						Collections.addAll(beanFieldNames, beanField.value());
					}

					// prepare arguments
					Object[] values = new Object[paramLength];
					for (int i = 0; i < paramLength; i++) {
						String key = beanFieldNames.get(i);
						Object value = null;
						if (key != null && key.length() > 0) {
							value = dataSource.getValue(key);
							if (convertor != null) {
								value = convertor.convert(value,
										paramClassTypes[i]);
							}
						}
						values[i] = value;
					}

					try {
						m.invoke(bean, values);
					} catch (IllegalAccessException e) {
						exceptionHandler.handle(e);
					} catch (IllegalArgumentException e) {
						exceptionHandler.handle(e);
					} catch (InvocationTargetException e) {
						exceptionHandler.handle(e);
					}
				}
			}

			// then set fields after
			for (Field f : beanClass.getDeclaredFields()) {
				if (f.isAnnotationPresent(BeanField.class)) {
					BeanField fieldMeta = f.getAnnotation(BeanField.class);
					String fieldName = fieldMeta.value()[0];
					if (fieldName != null && fieldName.length() != 0) {
						try {
							Object value = dataSource.getValue(fieldName);
							if (convertor != null) {
								value = convertor.convert(value, f.getType());
							}
							f.setAccessible(true);
							f.set(bean, value);
						} catch (Exception e) {
							exceptionHandler.handle(e);
						}
					}
				}
			}
		}
		return bean;
	}

	@Override
	protected Class<BeanFactoryParameter> factoryParamType() {
		return BeanFactoryParameter.class;
	}

	class BeanFactoryParameter implements FactoryParameter {

		private Class<?> beanClass;

		private BeanDataSource dataSource;

		protected BeanFactoryParameter(Class<?> beanClass,
				BeanDataSource dataSource) {
			this.beanClass = beanClass;
			this.dataSource = dataSource;
		}
	}

	public BeanDataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(BeanDataSource dataSource) {
		this.dataSource = dataSource;
	}

	public Convertor getConvertor() {
		return convertor;
	}

	public void setConvertor(Convertor convertor) {
		this.convertor = convertor;
	}

}
