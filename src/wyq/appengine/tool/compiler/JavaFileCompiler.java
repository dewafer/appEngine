package wyq.appengine.tool.compiler;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager.Location;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;

public class JavaFileCompiler extends AbstractCompiler {

	public JavaFileCompiler(String fullFileName, String outputPath)
			throws IOException {
		JavaCompiler compiler = getCompiler();
		DiagnosticListener<? super JavaFileObject> diagnostics = getDiagnosticListener();

		StandardJavaFileManager fileManager = compiler.getStandardFileManager(
				diagnostics, null, null);

		Location location = StandardLocation.CLASS_OUTPUT;
		try {
			fileManager.setLocation(location,
					Arrays.asList(new File[] { new File(outputPath) }));
		} catch (IOException e) {
			fileManager.close();
			throw e;
		}
		setFileManager(fileManager);

		// get JavaFileObject object, it will specify the java source file.
		Iterable<? extends JavaFileObject> itr = fileManager
				.getJavaFileObjectsFromFiles(Arrays.asList(new File(
						fullFileName)));
		setCompilationUnits(itr);
	}

}
