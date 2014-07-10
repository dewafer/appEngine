package wyq.appengine.component;

import wyq.appengine.ExceptionHandler;

/**
 * This class handles the exception in the repository.
 * 
 * @author dewafer
 * 
 */
public class RepositoryExceptionHandler implements ExceptionHandler {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1909844012074747208L;

	@Override
	public void handle(Exception e) {
		throw new RuntimeException(e);
	}

}
