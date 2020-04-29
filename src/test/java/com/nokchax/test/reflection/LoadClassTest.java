package com.nokchax.test.reflection;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
        assertThat(annotationAttachedClasses).containsAll(ANNOTATION_ATTACHED_CLASSES);
    }
}