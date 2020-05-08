package com.nokchax.test.reflection.loader;

import org.reflections.ReflectionsException;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class CustomClassLoader {
    public static Class<?> loadClass(String packagePath) {
        for (final URL url : forResource(resourceName(packagePath), Thread.currentThread().getContextClassLoader())) {
            try {
            } catch (ReflectionsException e) {
                e.printStackTrace();
            }
        }


        ClassLoader contextClassLoader = Thread.currentThread()
                .getContextClassLoader();

        try {
            return contextClassLoader.loadClass(packagePath);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String resourceName(String name) {
        if(name == null || name.isEmpty()) {
            return null;
        }

        String resourceName = name.replace(".", "/")
                .replace("\\", "/");

        return resourceName.startsWith("/") ? resourceName.substring(1) : resourceName;
    }

    public static Collection<URL> forResource(String resourceName, ClassLoader classLoader) {
        final List<URL> result = new ArrayList<>();

        try {
            final Enumeration<URL> urls = ClassLoader.getSystemClassLoader().getResources(resourceName);
            while (urls.hasMoreElements()) {
                final URL url = urls.nextElement();
                System.out.println(url.toExternalForm());
                int index = url.toExternalForm().lastIndexOf(resourceName);
                if (index != -1) {
                    // Add old url as contextUrl to support exotic url handlers
                    result.add(new URL(url, url.toExternalForm().substring(0, index)));
                } else {
                    result.add(url);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return distinctUrls(result);
    }

    private static Collection<URL> distinctUrls(Collection<URL> urls) {
        Map<String, URL> distinct = new LinkedHashMap<String, URL>(urls.size());
        for (URL url : urls) {
            distinct.put(url.toExternalForm(), url);
        }
        return distinct.values();
    }

}
