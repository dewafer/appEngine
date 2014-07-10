package wyq.appengine.component.template;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import wyq.appengine.component.file.TextFile;

public class DefaultHandler implements TemplateEngineHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9092762480430065871L;

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		Class<?> clazz = method.getDeclaringClass();
		String fileName = clazz.getSimpleName() + "_" + method.getName()
				+ ".template";
		String template = new TextFile(clazz, fileName).readAll();

		Pattern p = Pattern.compile("/\\*\\w+\\*/");
		Matcher m = p.matcher(template);

		List<String> keys = new ArrayList<String>();
		while (m.find()) {
			String keyWord = m.group();
			keys.add(keyWord.substring(2, keyWord.length() - 2));
		}

		Map<String, Object> keyWordValues = new LinkedHashMap<String, Object>();
		if (args != null && args.length != 0) {
			Annotation[][] parameterAnnotations = method
					.getParameterAnnotations();
			for (int i = 0; i < parameterAnnotations.length; i++) {
				Annotation[] annotations = parameterAnnotations[i];
				for (Annotation ano : annotations) {
					if (ano instanceof TemplateKeyWord) {
						TemplateKeyWord keyWord = (TemplateKeyWord) ano;
						String key = keyWord.value();
						Object value = args[i];
						keyWordValues.put(key, value);
						break;
					}
				}
			}

			if (keyWordValues.isEmpty()) {
				int i = 0;
				for (String key : keys) {
					if (!keyWordValues.containsKey(key)) {
						if (i < args.length) {
							Object value = args[i];
							i++;
							keyWordValues.put(key, value);
						}
					}
				}
			}
		}

		for (String key : keys) {
			String repKey = "/\\*" + Matcher.quoteReplacement(key) + "\\*/";
			Object oValue = keyWordValues.get(key);
			String value = (oValue != null) ? oValue.toString() : repKey;
			template = template.replaceFirst(repKey, value);
		}

		return template;
	}
}
