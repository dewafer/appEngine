package wyq.appengine.tool.compiler;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager.Location;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;

public class JavaStringCompiler extends AbstractCompiler {

	class JavaSourceFromString extends SimpleJavaFileObject {
		final String code;

		JavaSourceFromString(String name, String code) {
			super(URI.create("string:///" + name.replace('.', '/')
					+ Kind.SOURCE.extension), Kind.SOURCE);
			this.code = code;
		}

		@Override
		public CharSequence getCharContent(boolean ignoreEncodingErrors) {
			return code;
		}
	}

	public JavaStringCompiler(String javaClassName, String javaContent,
			String outputPath) {
		JavaFileObject file = new JavaSourceFromString(javaClassName,
				javaContent);
		setCompilationUnits(Arrays.asList(file));

		if (outputPath != null && outputPath.length() > 0) {
			JavaCompiler compiler = getCompiler();
			DiagnosticListener<? super JavaFileObject> diagnostics = getDiagnosticListener();

			StandardJavaFileManager fileManager = compiler
					.getStandardFileManager(diagnostics, null, null);

			Location location = StandardLocation.CLASS_OUTPUT;
			try {
				fileManager.setLocation(location,
						Arrays.asList(new File[] { new File(outputPath) }));
			} catch (IOException e) {
				try {
					fileManager.close();
				} catch (IOException e1) {
					throw new RuntimeException(e1);
				}
				throw new RuntimeException(e);
			}
			setFileManager(fileManager);
		}
	}

	public JavaStringCompiler(String javaClassName, String javaContent) {
		this(javaClassName, javaContent, null);
	}

}
