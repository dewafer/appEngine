package wyq.appengine.tool.compiler;

import java.io.Writer;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.DiagnosticListener;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;

public abstract class AbstractCompiler {

	private Writer outputWriter;
	private JavaFileManager fileManager;
	private DiagnosticListener<? super JavaFileObject> diagnosticListener = new DiagnosticCollector<JavaFileObject>();
	private Iterable<? extends JavaFileObject> compilationUnits;
	private Iterable<String> options;
	private Iterable<String> classes;
	private JavaCompiler compiler;

	protected AbstractCompiler() {
		compiler = ToolProvider.getSystemJavaCompiler();
		if (compiler == null)
			throw new RuntimeException("No compiler found!");
	}

	public boolean compile() throws Throwable {
		try {
			boolean success = false;

			success = compiler.getTask(outputWriter, fileManager,
					diagnosticListener, options, classes, compilationUnits)
					.call();

			printDiagnostic(diagnosticListener);

			return success;
		} catch (Exception e) {
			throw e;
		} finally {
			if (fileManager != null) {
				fileManager.close();
			}
		}
	}

	protected void printDiagnostic(
			DiagnosticListener<? super JavaFileObject> diagListener) {
		if (diagListener != null && diagListener instanceof DiagnosticCollector) {
			@SuppressWarnings("unchecked")
			DiagnosticCollector<JavaFileObject> collector = (DiagnosticCollector<JavaFileObject>) diagListener;
			for (Diagnostic<? extends JavaFileObject> diag : collector
					.getDiagnostics()) {
				System.out.println("Error on line: " + diag.getLineNumber()
						+ "; URI: " + diag.getSource().toString());
			}
		}
	}

	public Writer getOutputWriter() {
		return outputWriter;
	}

	public void setOutputWriter(Writer outputWriter) {
		this.outputWriter = outputWriter;
	}

	public JavaFileManager getFileManager() {
		return fileManager;
	}

	public void setFileManager(JavaFileManager fileManager) {
		this.fileManager = fileManager;
	}

	public DiagnosticListener<? super JavaFileObject> getDiagnosticListener() {
		return diagnosticListener;
	}

	public void setDiagnosticListener(
			DiagnosticListener<? super JavaFileObject> diagnosticListener) {
		this.diagnosticListener = diagnosticListener;
	}

	public Iterable<String> getOptions() {
		return options;
	}

	public void setOptions(Iterable<String> options) {
		this.options = options;
	}

	public Iterable<String> getClasses() {
		return classes;
	}

	public void setClasses(Iterable<String> classes) {
		this.classes = classes;
	}

	public void setCompilationUnits(
			Iterable<? extends JavaFileObject> compilationUnits) {
		this.compilationUnits = compilationUnits;
	}

	public JavaCompiler getCompiler() {
		return compiler;
	}

	public void setCompiler(JavaCompiler compiler) {
		this.compiler = compiler;
	}

	public Iterable<? extends JavaFileObject> getCompilationUnits() {
		return compilationUnits;
	}
}
