package wyq.appengine.tool;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class Runner {

	public static void run(Class<?> runClass) {
		try {
			new Runner().go(runClass);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	protected void go(Class<?> runClass) throws IllegalAccessException,
			InstantiationException, IllegalArgumentException,
			InvocationTargetException {

		if (runClass == null) {
			println("# runClass is null!");
			return;
		}

		println("# run class:[" + runClass + "]");
		long start = System.currentTimeMillis();

		Object o = runClass.newInstance();

		Method[] allMethods = runClass.getMethods();

		for (Method m : allMethods) {
			if (isCallableMethod(m, runClass)) {
				long mstart = System.currentTimeMillis();
				println("# Invoking method:[" + m + "]");
				Object result = null;
				try {
					result = m.invoke(o);
				} catch (Exception e) {
					e.printStackTrace();
					println("# method [" + m
							+ "] has thrown exception caused by: ["
							+ e.getCause() + "]");
				}
				println("# method [" + m + "] invoked. "
						+ (System.currentTimeMillis() - mstart) + " ms. used.");
				if (result != null) {
					println("# result:" + result);
				}
			}
		}

		println("# run class:[" + runClass + "] finished. "
				+ (System.currentTimeMillis() - start) + " ms. used.");
	}

	protected boolean isCallableMethod(Method m, Class<?> runClass) {
		if (!runClass.equals(m.getDeclaringClass()))
			return false;
		if (!Modifier.isPublic(m.getModifiers()))
			return false;
		if (m.getParameterTypes().length != 0)
			return false;
		if (Modifier.isNative(m.getModifiers()))
			return false;
		if (Modifier.isStatic(m.getModifiers()))
			return false;
		if (Modifier.isFinal(m.getModifiers()))
			return false;
		return true;
	}

	protected static void println(Object o) {
		System.out.println(o);
	}
}
