package wyq.appengine.component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * This class reads the XML files instead of properties file.
 * 
 * @author dewafer
 * 
 */
public class XMLProperty extends Property {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3126579033871056194L;

	@Override
	protected void loadAction(InputStream in) throws IOException {
		loadFromXML(in);
	}

	protected XMLProperty() {
		super();
	}

	protected XMLProperty(Properties p) {
		super(p);
	}

	protected XMLProperty(String p) {
		super(p);
	}
}
