package com.nokchax.test.reflection;

import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class PackageUtil {

    public static Collection<Class<?>> getClasses(final String pack) throws Exception {
        System.setProperty("java.home", "C:\\Program Files\\Java\\jdk1.8.0_111\\jre");
        System.out.println("java home : " + System.getProperty("java.home"));

        final StandardJavaFileManager fileManager = ToolProvider.getSystemJavaCompiler().getStandardFileManager(null, Locale.KOREA, Charset.defaultCharset());
        return StreamSupport.stream(fileManager.list(StandardLocation.CLASS_PATH, pack, Collections.singleton(JavaFileObject.Kind.CLASS), false).spliterator(), false)
                .map(javaFileObject -> {
                    try {
                        final String[] split = javaFileObject.getName()
                                .replace(".class", "")
                                .replace(")", "")
                                .split(Pattern.quote(File.separator));

                        final String fullClassName = pack + "." + split[split.length - 1];
                        System.out.println("fullClassName = " + fullClassName);
                        return Class.forName(fullClassName);
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }

                })
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
