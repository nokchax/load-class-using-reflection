package com.nokchax.test.reflection;

import com.sun.javafx.runtime.SystemProperties;
import org.reflections.Reflections;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    private static final String PACKAGE = "com.nokchax.test.reflection";
    private static final String DOT_ATTACHED_PACKAGE = PACKAGE + ".";
    private static final List<String> ANNOTATION_ATTACHED_CLASSES = attachPackage(Collections.singletonList("LoadAttachedClass"));
    private static final List<String> NORMAL_CLASSES = attachPackage(Arrays.asList("Load", "NormalClass", "LoadClassTest"));
    private static final List<String> ALL_CLASSES = Stream.concat(
            ANNOTATION_ATTACHED_CLASSES.stream(),
            NORMAL_CLASSES.stream()
    )
            .collect(Collectors.toList());

    public static void main(String[] args) throws Exception {
        System.out.println("start");
        List<Class<?>> classes = getClasses("com.nokchax.test.reflection");

        classes.forEach(System.out::println);
        System.out.println("end : " + classes.size());

        System.out.println();
        System.out.println("start using library");
        Reflections reflections = new Reflections(PACKAGE);

        List<String> annotationAttachedClasses = reflections.getTypesAnnotatedWith(Load.class)
                .stream()
                .map(Class::getCanonicalName)
                .collect(Collectors.toList());

        annotationAttachedClasses.forEach(System.out::println);
        System.out.println("end : " + annotationAttachedClasses.size());

        System.out.println();
        System.out.println("start using library");
        Collection<Class<?>> classes1 = PackageUtil.getClasses(PACKAGE);
        classes1.forEach(System.out::println);
        System.out.println("end : " + classes1.size());
    }

    private static List<Class<?>> getClasses(String packageName)
            throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class<?>> classes = new ArrayList<>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes;
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        if (!directory.exists() || directory.listFiles() == null) {
            return Collections.emptyList();
        }

        List<Class<?>> classes = new ArrayList<>();

        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }

    private static List<String> attachPackage(List<String> classNames) {
        return classNames.stream()
                .map(className -> DOT_ATTACHED_PACKAGE + className)
                .collect(Collectors.toList());
    }
}
