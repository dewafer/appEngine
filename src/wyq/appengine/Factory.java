package wyq.appengine;

/**
 * All the factories should have this interface, but do not implement this
 * interface directly, extend AbstractFactory instead.
 * 
 * @author wyq
 * 
 * @param <T>
 */
public interface Factory<T> extends Component {

	public abstract T factory(FactoryParameter parameterObject);

	public abstract FactoryParameter prepare(Object... values);

	public abstract T manufacture(Object... values);

	public abstract String getName();

}