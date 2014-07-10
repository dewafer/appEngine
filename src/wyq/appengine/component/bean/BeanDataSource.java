package wyq.appengine.component.bean;

import wyq.appengine.Component;

/**
 * This interface provieds the data for the bean in BeanFactory.
 * 
 * @author dewafer
 * @version 1
 * 
 */
public interface BeanDataSource extends Component {

	Object getValue(String fieldName);

}
