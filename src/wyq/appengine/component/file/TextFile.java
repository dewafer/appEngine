package wyq.appengine.component.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;

import wyq.appengine.Component;
import wyq.appengine.ExceptionHandler;

/**
 * This class is extends the java.io.File which embedded the read/write methods
 * through which you can read or write all the content of a text file at once or
 * just line by line.
 * 
 * @author dewafer
 * @version 1
 */
public class TextFile extends File implements Component {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2188324052240230771L;

	public static final String LINE_SEP = System.getProperty("line.separator");

	private ExceptionHandler exceptionHandler = new ExceptionHandler() {

		/**
		 * 
		 */
		private static final long serialVersionUID = 9064625580881097471L;

		@Override
		public void handle(Exception e) {
			throw new RuntimeException(e);
		}
	};

	private TextFileReaderWriter readerWriter = new TextFileReaderWriter();

	public TextFile(File parent, String child) {
		super(parent, child);
	}

	public TextFile(String parent, String child) {
		super(parent, child);
	}

	public TextFile(URI uri) {
		super(uri);
	}

	public TextFile() {
		super(getRealPath(TextFile.class, null));
	}

	public TextFile(Class<?> c) {
		super(getRealPath(c, null));
	}

	public TextFile(String name) {
		super(getRealPath(null, name));
	}

	public TextFile(Class<?> c, String name) {
		super(getRealPath(c, name));
	}

	protected static String getRealPath(Class<?> c, String name) {
		if (c != null) {
			if (name == null) {
				name = c.getSimpleName();
			}

			URL resource = c.getResource(name);
			if (resource != null) {
				return resource.getFile();
			}

			resource = c.getResource("/" + name);
			if (resource != null) {
				return resource.getFile();
			}
		}
		return name;
	}

	public String readAll() {
		StringBuilder sb = new StringBuilder();
		try {
			readerWriter.openRead();
			String line = null;
			while ((line = readerWriter.readLine()) != null) {
				sb.append(line);
				sb.append(LINE_SEP);
			}
		} catch (Exception e) {
			exceptionHandler.handle(e);
		} finally {
			try {
				readerWriter.closeRead();
			} catch (IOException e) {
				exceptionHandler.handle(e);
			}
		}
		return sb.toString();
	}

	public void writeAll(String content, boolean append) {
		try {
			readerWriter.openWrite(append);
			readerWriter.write(content);
			readerWriter.flush();
		} catch (Exception e) {
			exceptionHandler.handle(e);
		} finally {
			try {
				readerWriter.closeWrite();
			} catch (IOException e) {
				exceptionHandler.handle(e);
			}
		}
	}

	public String readLine() {
		String line = null;
		try {
			readerWriter.openRead();
			line = readerWriter.readLine();
		} catch (Exception e) {
			exceptionHandler.handle(e);
		}
		return line;
	}

	public void writeLine(String line) {
		try {
			readerWriter.openWrite(true);
			readerWriter.writeLine(line);
		} catch (Exception e) {
			exceptionHandler.handle(e);
		}
	}

	public void close() throws IOException {
		readerWriter.close();
	}

	public void reset() throws IOException {
		readerWriter.reset();
	}

	public long skip(long arg0) throws IOException {
		return readerWriter.skip(arg0);
	}

	public void flush() throws IOException {
		readerWriter.flush();
	}

	public ExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}

	public void setExceptionHandler(ExceptionHandler exceptionHandler) {
		this.exceptionHandler = exceptionHandler;
	}

	class TextFileReaderWriter {

		private BufferedReader reader;
		private BufferedWriter writer;

		public void openRead() throws FileNotFoundException {
			if (reader == null) {
				if (TextFile.this.exists()) {
					reader = new BufferedReader(new FileReader(TextFile.this));
				} else {
					// try find in res
					String resPath = TextFile.this.getPath().replace(
							File.separator, "/");

					InputStream inputStream = this.getClass()
							.getResourceAsStream(resPath);
					if (inputStream == null)
						inputStream = this.getClass().getResourceAsStream(
								"/" + resPath);

					if (inputStream == null)
						throw new FileNotFoundException(resPath);

					reader = new BufferedReader(new InputStreamReader(
							inputStream));
				}
			}
		}

		public void openWrite(boolean append) throws IOException {
			if (writer == null) {
				if (!TextFile.this.exists()) {
					TextFile.this.getParentFile().mkdirs();
					TextFile.this.createNewFile();
				}
				writer = new BufferedWriter(new FileWriter(TextFile.this,
						append));

			}
		}

		public void close() throws IOException {
			closeRead();
			closeWrite();
		}

		public void closeRead() throws IOException {
			if (reader != null) {
				reader.close();
				reader = null;
			}
		}

		public void closeWrite() throws IOException {
			if (writer != null) {
				writer.close();
				writer = null;
			}
		}

		public String readLine() throws IOException {
			String line = null;
			if (reader != null) {
				line = reader.readLine();
			}
			return line;
		}

		public void writeLine(String line) throws IOException {
			if (writer != null) {
				writer.write(line);
				writer.newLine();
			}
		}

		public void reset() throws IOException {
			if (reader != null) {
				reader.reset();
			}
		}

		public long skip(long arg0) throws IOException {
			long skipped = 0;
			if (reader != null) {
				skipped = reader.skip(arg0);
			}
			return skipped;
		}

		public void flush() throws IOException {
			if (writer != null) {
				writer.flush();
			}
		}

		public void write(String str) throws IOException {
			if (writer != null) {
				writer.write(str);
			}
		}

	}
}
