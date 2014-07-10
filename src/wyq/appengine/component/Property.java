package wyq.appengine.component;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import wyq.appengine.Component;

/**
 * This class reads the property of the configuration.
 * 
 * @author dewafer
 * 
 */
public class Property extends Properties implements Component {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2323409154240652900L;

	public static Property get() {
		return Repository.get("Property", Property.class);
	}

	public static Property get(String name) {
		return Repository.get(name, Property.class);
	}

	public static Property get(Class<?> c) {
		return Repository.get(c.getName(), Property.class);
	}

	public Property(Properties p) {
		super(p);
	}

	public Property(String p) {
		super();
		loadPropertyFile(p);
	}

	public Property() {
		super();
		loadPropertyFile(this.getClass());
	}

	public Property(Class<?> c) {
		loadPropertyFile(c);
	}

	protected void loadPropertyFile(String name) {
		loadPropertyFile(null, name);
	}

	protected void loadPropertyFile(Class<?> c) {
		loadPropertyFile(c, null);
	}

	protected void loadPropertyFile(Class<?> c, String name) {
		Class<?> clazz;
		if (c == null) {
			clazz = this.getClass();
		} else {
			clazz = c;
		}
		InputStream resourceAsStream = null;
		if (name == null) {
			name = clazz.getSimpleName() + ".properties";
			resourceAsStream = clazz.getResourceAsStream(name);
		} else {
			if (c == null) {
				try {
					resourceAsStream = new FileInputStream(name);
				} catch (FileNotFoundException e) {
					resourceAsStream = clazz.getResourceAsStream(name);
				}
			} else {
				resourceAsStream = clazz.getResourceAsStream(name);
			}
		}
		if (resourceAsStream == null) {
			resourceAsStream = clazz.getResourceAsStream("/" + name);
		}
		if (resourceAsStream != null) {
			try {
				loadAction(resourceAsStream);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public void load(String file) {
		loadPropertyFile(file);
	}

	public void load(Class<?> c) {
		loadPropertyFile(c);
	}

	protected void loadAction(InputStream in) throws IOException {
		load(in);
	}

	public boolean isKeyDefined(String keyRegExp) {
		for (Object key : this.keySet()) {
			String strKey = key.toString();
			if (Pattern.matches(keyRegExp, strKey)) {
				return true;
			}
		}
		return false;
	}

	public String[] getProperties(String keyRegExp) {
		List<String> p = new ArrayList<String>();
		for (Object key : this.keySet()) {
			String strKey = key.toString();
			if (Pattern.matches(keyRegExp, strKey)) {
				p.add(getProperty(strKey));
			}
		}
		String[] values = new String[p.size()];
		return p.toArray(values);
	}
}
