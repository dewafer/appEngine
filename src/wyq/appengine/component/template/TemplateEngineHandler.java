package wyq.appengine.component.template;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import wyq.appengine.Component;

public interface TemplateEngineHandler extends InvocationHandler, Component {

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable;
}
