package wyq.appengine;

public interface Convertor extends Component {

	public <T> T convert(Object orangValue, Class<T> targetValueClass);
}
