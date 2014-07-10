package wyq.appengine.component;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import wyq.appengine.Component;
import wyq.appengine.ExceptionHandler;
import wyq.appengine.Factory;

/**
 * This is the Repository. Get components from this Repository.
 * 
 * @version 1
 * @author dewafer
 * 
 */
public class Repository implements Component {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1903248669068799516L;

	private static Repository SINGLETON = null;
	protected static String DEFAULT_CONF = "/conf.properties";

	private String repositorySaveFile;
	private String usingFactory;
	private String usingInitFactory;
	private ComponentPool compPool = new ComponentPool();
	private Factory<Component> factory;
	private Factory<Component> initFactory;
	private ExceptionHandler exceptionHandler;
	private FactoryLoader factoryLoader;

	/**
	 * Same as {@code get(name, null)}.
	 * 
	 * @see wyq.appengine.component.Repository#get(String, Class)
	 * @param name
	 * @return
	 */
	public static Component get(String name) {
		return get(name, null);
	}

	/**
	 * Same as {@code get(null, class}.
	 * 
	 * @see wyq.appengine.component.Repository#get(String, Class)
	 * @param cls
	 * @return
	 */
	public static <T extends Component> T get(Class<T> cls) {
		return get(null, cls);
	}

	/**
	 * Get a component. If the component doesn't exist, load it first.
	 * 
	 * @param name
	 * @param cls
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Component> T get(String name, Class<T> cls) {
		Component c = getInstance().findComponent(name, cls);
		if (c == null) {
			c = getInstance().loadComponent(name, cls);
		}
		return (T) c;
	}

	/**
	 * Put a component into the Repository. Can NOT put null into it.
	 * 
	 * @param name
	 * @param cls
	 * @param c
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Component> T put(String name, Class<T> cls,
			Component c) {
		return (T) getInstance().register(c, name, cls);
	}

	/**
	 * Same as {@code find(name, null)}
	 * 
	 * @see wyq.appengine.component.Repository#find(String, Class)
	 * @param name
	 * @return
	 */
	public static Component find(String name) {
		return find(name, null);
	}

	/**
	 * Same as {@code find(null, class)}
	 * 
	 * @see wyq.appengine.component.Repository#find(String, Class)
	 * @param cls
	 * @return
	 */
	public static <T extends Component> T find(Class<T> cls) {
		return find(null, cls);
	}

	/**
	 * Get a component. Won't load it if does not exist.
	 * 
	 * @see wyq.appengine.component.Repository#findComponent(String, Class)
	 * @param name
	 * @param cls
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Component> T find(String name, Class<T> cls) {
		return (T) getInstance().findComponent(name, cls);
	}

	/**
	 * See
	 * {@link wyq.appengine.component.Repository#hasComponent(String, Class)}
	 * 
	 * @param name
	 * @param cls
	 * @return
	 */
	public static <T extends Component> boolean contains(String name,
			Class<T> cls) {
		return getInstance().hasComponent(name, cls);
	}

	/**
	 * See {@link wyq.appengine.component.Repository#hasComponent(Component)}
	 * 
	 * @param c
	 * @return
	 */
	public static boolean contains(Component c) {
		return getInstance().hasComponent(c);
	}

	/**
	 * see {@link wyq.appengine.component.Repository#unregister(String, Class)}
	 * 
	 * @param name
	 * @param cls
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Component> T release(String name, Class<T> cls) {
		return (T) getInstance().unregister(name, cls);
	}

	/**
	 * @deprecated
	 */
	public static void save() {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream(getInstance().repositorySaveFile));
			oos.writeObject(getInstance());
			oos.flush();
			oos.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @deprecated
	 */
	public static void load() {
		try {
			ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
					getInstance().repositorySaveFile));
			setInstance((Repository) ois.readObject());
			ois.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected static Repository getInstance() {
		if (SINGLETON == null) {
			SINGLETON = new Repository();
		}
		return SINGLETON;
	}

	protected static Repository setInstance(Repository res) {
		Repository oldInstance = SINGLETON;
		SINGLETON = res;
		return oldInstance;
	}

	public static String setDefaultConf(String cnf) {
		String oldOne = DEFAULT_CONF;
		DEFAULT_CONF = cnf;
		return oldOne;
	}

	protected Repository() {
		Property p = new Property(DEFAULT_CONF);
		if (p.isEmpty()) {
			throw new RuntimeException("Default configuration file["
					+ DEFAULT_CONF + "] doesn't exist or is empty.");
		}
		repositorySaveFile = p.getProperty("Repository.repositorySaveFile");
		usingFactory = p.getProperty("Repository.usingFactory");
		usingInitFactory = p.getProperty("Repository.usingInitFactory");

		String exceptionHandlerName = p
				.getProperty("Repository.ExceptionHandler");
		try {
			exceptionHandler = (ExceptionHandler) Class.forName(
					exceptionHandlerName).newInstance();
			factoryLoader = new FactoryLoader();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		register(factoryLoader, "FactoryLoader", FactoryLoader.class);
		register(exceptionHandler, "ExceptionHandler", ExceptionHandler.class);
		register(p, "Property", Property.class);
		register(this, "Repository", Repository.class);

		// Can't load factory at instance initialization
		// loadFactory();
	}

	@SuppressWarnings("unchecked")
	protected void loadFactory() {
		try {
			factory = (Factory<Component>) factoryLoader.load(usingFactory);
			register(factory, "Factory", factory.getClass());

			initFactory = (Factory<Component>) factoryLoader
					.load(usingInitFactory);
			register(initFactory, "initFactory", initFactory.getClass());

		} catch (Exception e) {
			exceptionHandler.handle(e);
		}
	}

	protected Component loadComponent(String name,
			Class<? extends Component> cls) {

		// lazy factory load
		if (factory == null) {
			loadFactory();
			// if the user is finding factory
			Component theFactory = findComponent(name, cls);
			if (theFactory != null) {
				return theFactory;
			}
		}

		// Try loading the factory using facoryLoader rather than factory.
		Component component = null;
		if (isFactory(cls)) {
			component = factoryLoader.load(cls.getName());
		}
		// If the factory couldn't load from the factoryLoader then
		// load it from the factory.
		if (component == null) {
			component = factory.manufacture(name, cls);
		}

		if (component != null) {
			register(component, name, cls);
			component = initFactory.manufacture(component);
		}

		return component;
	}

	public static boolean isFactory(Class<?> cls) {
		return cls != null && Factory.class.isAssignableFrom(cls);
	}

	/**
	 * See
	 * {@link wyq.appengine.component.ComponentPool#register(Component, String, Class)}
	 * 
	 * @param name
	 * @param cls
	 * @return
	 */
	protected Component register(Component c, String name,
			Class<? extends Component> cls) {
		return compPool.register(c, name, cls);
	}

	/**
	 * See {@link wyq.appengine.component.ComponentPool#retrieve(String, Class)}
	 * 
	 * @param name
	 * @param cls
	 * @return
	 */
	protected Component findComponent(String name,
			Class<? extends Component> cls) {
		return compPool.retrieve(name, cls);
	}

	/**
	 * See {@link wyq.appengine.component.ComponentPool#contains(String, Class)}
	 * 
	 * @param name
	 * @param cls
	 * @return
	 */
	protected boolean hasComponent(String name, Class<? extends Component> cls) {
		return compPool.contains(name, cls);
	}

	/**
	 * See {@link wyq.appengine.component.ComponentPool#contains(Component)}
	 * 
	 * @param c
	 * @return
	 */
	protected boolean hasComponent(Component c) {
		return compPool.contains(c);
	}

	/**
	 * See
	 * {@link wyq.appengine.component.ComponentPool#unregister(String, Class)}
	 * 
	 * @param name
	 * @param cls
	 * @return
	 */
	protected Component unregister(String name, Class<? extends Component> cls) {
		return compPool.unregister(name, cls);
	}

}
