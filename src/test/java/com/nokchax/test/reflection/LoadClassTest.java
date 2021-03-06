package com.nokchax.test.reflection;

import com.nokchax.test.reflection.loader.CustomClassLoader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class LoadClassTest {
    private static final String PACKAGE = "com.nokchax.test.reflection";
    private static final String DOT_ATTACHED_PACKAGE = PACKAGE + ".";
    private static final List<String> ANNOTATION_ATTACHED_CLASSES = attachPackage(Collections.singletonList("LoadAttachedClass"));
    private static final List<String> NORMAL_CLASSES = attachPackage(Arrays.asList("Load", "NormalClass", "LoadClassTest"));
    private static final List<String> ALL_CLASSES = Stream.concat(
                    ANNOTATION_ATTACHED_CLASSES.stream(),
                    NORMAL_CLASSES.stream()
            )
            .collect(Collectors.toList());

    private static List<String> attachPackage(List<String> classNames) {
        return classNames.stream()
                .map(className -> DOT_ATTACHED_PACKAGE + className)
                .collect(Collectors.toList());
    }

    @Test
    @DisplayName("리플렉션 라이브러리로 로드 테스트")
    void loadClassUsingReflectionLibrary() {
        Reflections reflections = new Reflections(
                PACKAGE,
                new SubTypesScanner(false)
        );

        List<String> classNames = reflections.getSubTypesOf(Object.class)
                .stream()
                .map(Class::getName)
                .collect(Collectors.toList());

        classNames.forEach(System.out::println);
        assertThat(classNames).containsAll(ALL_CLASSES);
    }

    @Test
    @DisplayName("어노테이션이 붙어있는 클래스만 로드하기")
    void LoadAnnotationAttachedClassUsingReflectionLibrary() {
        Reflections reflections = new Reflections(PACKAGE);

        List<String> annotationAttachedClasses = reflections.getTypesAnnotatedWith(Load.class)
                .stream()
                .map(Class::getCanonicalName)
                .collect(Collectors.toList());

        annotationAttachedClasses.forEach(System.out::println);
        assertThat(annotationAttachedClasses).containsAll(ANNOTATION_ATTACHED_CLASSES)
                .doesNotContainSequence(NORMAL_CLASSES);
    }

    @Test
    @DisplayName("resourceName 테스트")
    void resourceNameTest() {
        String resourceName = CustomClassLoader.resourceName(DOT_ATTACHED_PACKAGE + "Load");

        System.out.println("Before : " + DOT_ATTACHED_PACKAGE + "Load");
        System.out.println("After : " + resourceName);
    }

    @Test
    @DisplayName("for resource 테스트")
    void forResourceTest() throws IOException, ClassNotFoundException {
        List<Class<?>> classes = getClasses("com.nokchax.test.reflection");

        classes.forEach(System.out::println);
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
}