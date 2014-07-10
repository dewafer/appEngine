package wyq.appengine;

import wyq.appengine.component.Repository;

/**
 * Whatever class extended from this one has the same initialize procedure as
 * those components in the Repository.
 * 
 * @author wyq
 * 
 */
public abstract class AbstractApp {

	public AbstractApp() {
		Repository.get("initFactory", Factory.class).manufacture(this);
	}
}
