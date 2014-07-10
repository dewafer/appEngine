package wyq.appengine;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The fields of the components with this annotation will be automatically set
 * within the CompInitFactory.
 * 
 * @author wyq
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PropertyField {

	boolean ignore() default false;

	String name() default "";

	boolean useStandalonePropFile() default false;

	String standalonePropFileName() default "";
}
