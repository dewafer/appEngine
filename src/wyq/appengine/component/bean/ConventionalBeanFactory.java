package wyq.appengine.component.bean;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

import wyq.appengine.Convertor;
import wyq.appengine.FactoryParameter;
import wyq.appengine.component.AbstractFactory;
import wyq.appengine.component.bean.ConventionalBeanFactory.ConventionalBeanFactoryParameter;

/**
 * * Set the bean's values from BeanDataSource by the conventional Introspector
 * of the java. This bean factory ignores the bean annotations.
 * 
 * @author dewafer
 * 
 */
public class ConventionalBeanFactory extends
		AbstractFactory<Object, ConventionalBeanFactoryParameter> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5210952485622449896L;

	private BeanDataSource dataSource;

	private Convertor convertor;

	@Override
	protected Object build(ConventionalBeanFactoryParameter param) {
		Class<?> beanClass = param.beanClass;
		if (param.dataSource != null) {
			dataSource = param.dataSource;
		}

		Object bean = null;
		try {
			// Using the stander Introspector of the java SE
			// Do not search the Object.class;
			BeanInfo beanInfo = Introspector.getBeanInfo(beanClass,
					Object.class);
			PropertyDescriptor[] propertyDescriptors = beanInfo
					.getPropertyDescriptors();

			// create bean object
			bean = beanClass.newInstance();

			// set properties
			for (PropertyDescriptor p : propertyDescriptors) {
				Method writeMethod = p.getWriteMethod();
				// set only if the property is writable
				if (writeMethod != null) {
					String propertyName = p.getName();
					Object value = dataSource.getValue(propertyName);
					Class<?> propertyType = p.getPropertyType();
					if (convertor != null) {
						value = convertor.convert(value, propertyType);
					}
					writeMethod.invoke(bean, value);
				}
			}

		} catch (Exception e) {
			exceptionHandler.handle(e);
		}

		return bean;
	}

	@Override
	protected Class<ConventionalBeanFactoryParameter> factoryParamType() {
		return ConventionalBeanFactoryParameter.class;
	}

	class ConventionalBeanFactoryParameter implements FactoryParameter {

		private Class<?> beanClass;

		private BeanDataSource dataSource;

		protected ConventionalBeanFactoryParameter(Class<?> beanClass,
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
