package wyq.appengine;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The setters of the components with this annotation will be invoked while the
 * CompInitFactory is in the initialize process.
 * 
 * @author wyq
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ComponentField {

	boolean ignore() default false;

	String[] name() default {};

}
