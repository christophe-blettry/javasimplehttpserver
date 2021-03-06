package net.microfaas.net.simplehttp.util;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Find classes in the classpath (reads JARs and classpath folders).
 *
 * @author P&aring;l Brattberg, brattberg@gmail.com
 * @see http://gist.github.com/pal
 */
@SuppressWarnings("unchecked")
public class ClasspathInspector {

	static boolean debug = false;
	static Set<String> validPackagePrefixes;

	public static List<Class<?>> getAllKnownClasses() {
		List<Class<?>> classFiles = new ArrayList<>();
		List<File> classLocations = getClassLocationsForCurrentClasspath();
		for (File file : classLocations) {
			classFiles.addAll(getClassesFromPath(file));
		}
		return classFiles;
	}

	public static List<Class<?>> getMatchingClasses(Class<?> interfaceOrSuperclass) {
		List<Class<?>> matchingClasses = new ArrayList<>();
		List<Class<?>> classes = getAllKnownClasses();
		log("checking %s classes", classes.size());
		classes.stream().filter(clazz -> (interfaceOrSuperclass.isAssignableFrom(clazz))).map(clazz -> {
			matchingClasses.add(clazz);
			return clazz;
		}).forEachOrdered(clazz -> {
			log("class %s is assignable from %s", interfaceOrSuperclass, clazz);
		});
		return matchingClasses;
	}

	public static List<Class<?>> getMatchingClasses(Set<String> validPackagePref) {
		validPackagePrefixes = validPackagePref;
		return getAllKnownClasses();
	}

	private static Collection<? extends Class<?>> getClassesFromPath(File path) {
		if (path.isDirectory()) {
			return getClassesFromDirectory(path);
		} else {
			return getClassesFromJarFile(path);
		}
	}

	private static String fromFileToClassName(final String fileName) {
		return fileName.substring(0, fileName.length() - 6).replaceAll("/|\\\\", "\\.");
	}

	private static boolean isClassNameMatches(String className) {
		if (validPackagePrefixes == null) {
			return true;
		}
		return validPackagePrefixes.stream().filter(name -> className.startsWith(name)).count() > 0;
	}

	private static List<Class<?>> getClassesFromJarFile(File path) {
		List<Class<?>> classes = new ArrayList<>();
		log("getClassesFromJarFile: Getting classes for %s", path);

		try {
			if (path.canRead()) {
				try (JarFile jar = new JarFile(path)) {
					classes.addAll(walkOnJar(jar));
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("Failed to read classes from jar file: " + path, e);
		}
		return classes;
	}

	private static List<Class<?>> walkOnJar(JarFile jar) {
		List<Class<?>> classes = new ArrayList<>();
		Enumeration<JarEntry> en = jar.entries();
		while (en.hasMoreElements()) {
			JarEntry entry = en.nextElement();
			if (entry.getName().endsWith("class")) {
				String className = fromFileToClassName(entry.getName());
				log("\twalkOnJar: found %s", className);
				if (isClassNameMatches(className)) {
					try {
						classes.add(Class.forName(className));
					} catch (ClassNotFoundException ex) {
						Logger.getLogger(ClasspathInspector.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			}
		}
		return classes;
	}

	private static List<Class<?>> getClassesFromDirectory(File path) {
		List<Class<?>> classes = new ArrayList<>();
		log("getClassesFromDirectory: Getting classes for %s", path);

		// get jar files from top-level directory
		List<File> jarFiles = listFiles(path, ".jar", false);
		for (File file : jarFiles) {
			classes.addAll(getClassesFromJarFile(file));
		}

		// get all class-files
		List<File> classFiles = listFiles(path, ".class", true);

		int substringBeginIndex = path.getAbsolutePath().length() + 1;
		for (File classfile : classFiles) {
			String className = classfile.getAbsolutePath().substring(substringBeginIndex);
			className = fromFileToClassName(className);
			if (isClassNameMatches(className)) {
				log("Found class %s in path %s: ", className, path);
				try {
					classes.add(Class.forName(className));
				} catch (Exception e) {
					log("Couldn't create class %s. %s: ", className, e);
				}
			}
		}
		return classes;
	}

	private static List<File> listFiles(File directory, String filterExtension, boolean recurse) {
		List<File> files = new ArrayList<>();
		File[] entries = directory.listFiles();

		// Go over entries
		for (File entry : entries) {
			// If there is no filter or the filter accepts the
			// file / directory, add it to the list
			if (filterExtension == null || entry.getName().endsWith(filterExtension)) {
				files.add(entry);
			}

			// If the file is a directory and the recurse flag
			// is set, recurse into the directory
			if (recurse && entry.isDirectory()) {
				files.addAll(listFiles(entry, filterExtension, recurse));
			}
		}
		// Return collection of files
		return files;
	}

	public static List<File> getClassLocationsForCurrentClasspath() {
		List<File> urls = new ArrayList<>();
		String javaClassPath = System.getProperty("java.class.path");
		if (javaClassPath != null) {
			for (String path : javaClassPath.split(File.pathSeparator)) {
				urls.add(new File(path));
			}
		}
		return urls;
	}

	public static URL normalize(URL url) throws MalformedURLException {
		String spec = url.getFile();

		// get url base - remove everything after ".jar!/??" , if exists
		final int i = spec.indexOf("!/");
		if (i != -1) {
			spec = spec.substring(0, spec.indexOf("!/"));
		}

		// uppercase windows drive
		url = new URL(url, spec);
		final String file = url.getFile();
		final int i1 = file.indexOf(':');
		if (i1 != -1) {
			String drive = file.substring(i1 - 1, 2).toUpperCase();
			url = new URL(url, file.substring(0, i1 - 1) + drive + file.substring(i1));
		}

		return url;
	}

	public static void main(String[] args) {
		// find all classes in classpath
		List<Class<?>> allClasses = ClasspathInspector.getAllKnownClasses();
		log(String.format("There are %d classes available in the classpath", allClasses.size()));

		// find all classes that implement/subclass an interface/superclass
		List<Class<?>> serializableClasses = ClasspathInspector.getMatchingClasses(Serializable.class);
		for (Class<?> clazz : serializableClasses) {
			log(String.format("%s is Serializable", clazz.getName()));
		}
	}

	private static void log(String pattern, final Object... args) {
		if (debug) {
			log(String.format(pattern, args));
		}
	}

	private static void log(String message) {
		System.out.println(message);
	}
}
