package wyq.appengine.component.template;

import java.lang.reflect.InvocationHandler;

import wyq.appengine.component.AbstractProxyEngine;
import wyq.appengine.component.Repository;

/**
 * This class handles templates through interfaces.
 * 
 * @author dewafer
 * 
 */
public class TemplateEngine extends AbstractProxyEngine {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1812650709192507019L;

	private TemplateEngineHandler handler;

	public <T> T getTemplate(Class<? extends T> templateInterface) {
		return getProxyInstance(templateInterface);
	}

	public static TemplateEngine get() {
		return Repository.get("TemplateEngine", TemplateEngine.class);
	}

	@Override
	public InvocationHandler getHandler() {
		return handler;
	}

	public void setHandler(TemplateEngineHandler handler) {
		this.handler = handler;
	}
}
