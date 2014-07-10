/**
 * 
 */
package wyq.appengine.component.javascript;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import wyq.appengine.Component;
import wyq.appengine.ExceptionHandler;
import wyq.appengine.component.Repository;
import wyq.appengine.component.file.TextFile;

/**
 * @author wangyq
 * 
 */
public class JsEngine implements Component {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5780969464909729122L;
	private static final String DEFAULT_SCRIPT_ENGINE_NAME = "javascript";

	protected ExceptionHandler handler;

	public <T> T getJsDelegate(Class<? extends T> jsInterface) {
		initScriptEngine();
		evalScript(jsInterface);
		if (isScriptEngineInvocable()) {
			Invocable i = (Invocable) scriptEngine;
			return i.getInterface(jsInterface);
		}
		return null;
	}

	public static JsEngine get() {
		return Repository.get("JsEngine", JsEngine.class);
	}

	protected ScriptEngine scriptEngine = null;

	protected boolean isScriptEngineInvocable() {
		return scriptEngine instanceof Invocable;
	}

	protected void evalScript(Class<?> iface) {
		String fileName = iface.getSimpleName() + ".js";
		String scriptContent = new TextFile(iface, fileName).readAll();
		try {
			scriptEngine.eval(scriptContent);
		} catch (ScriptException e) {
			handler.handle(e);
		}
	}

	protected void initScriptEngine() {
		if (scriptEngine == null) {
			scriptEngine = new ScriptEngineManager()
					.getEngineByName(DEFAULT_SCRIPT_ENGINE_NAME);
		}
	}

	/**
	 * @return the handler
	 */
	public ExceptionHandler getHandler() {
		return handler;
	}

	/**
	 * @param handler
	 *            the handler to set
	 */
	public void setHandler(ExceptionHandler handler) {
		this.handler = handler;
	}

}
