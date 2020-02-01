package com.dreamtech.core;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 类扫描器
 */
public class ClassScanner {
    public static synchronized List<Class<?>> scanClasses(String packageName) throws Exception {
        String classPath = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("")).getPath() + "/" + packageName.replace(".", "/");
        return getClassFromDir(packageName, classPath);
    }

    /**
     * 从当前目录中获取类列表
     *
     * @param classPath   当前路径
     * @param packageName 启动类所在的包名
     * @return 类list
     * @throws Exception [Class.forName 异常 | 无法找到java文件异常]
     */
    private static List<Class<?>> getClassFromDir(String packageName, String classPath) throws Exception {
        List<Class<?>> classList = new LinkedList<>();
        File[] javaFiles = new File(classPath).listFiles();
        if (javaFiles == null || javaFiles.length == 0)
            throw new Exception("can not find java file in " + classPath);
        for (File javaFile : javaFiles) {
            if (javaFile.isDirectory()) {
                classList.addAll(getClassFromDir(packageName + "." + javaFile.getName(), javaFile.getAbsolutePath()));
            } else if (javaFile.getName().endsWith(".class")) {
                String javaFileName = javaFile.getName();
                String javaClassName = javaFileName.substring(0, javaFile.getName().length() - 6);
                String classFullName = packageName + "." + javaClassName;
                classList.add(Class.forName(classFullName));
            }
        }
        return classList;
    }

    public static synchronized List<Class<?>> scanClasses() throws IOException, ClassNotFoundException {
        List<Class<?>> classList = new ArrayList<>();
        String path = "org/dreamtech/zty";
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = classLoader.getResources(path);
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            if (resource.getProtocol().contains("jar")) {
                JarURLConnection jarURLConnection = (JarURLConnection) resource.openConnection();
                String jarFilePath = jarURLConnection.getJarFile().getName();
                classList.addAll(getClassesFromJar(jarFilePath, path));
            }
        }
        return classList;
    }

    /**
     * 从jar包中获取类列表
     *
     * @param jarFilePath jar路径
     * @param path        org/dreamtech/zty
     * @return 类列表
     * @throws IOException            IO
     * @throws ClassNotFoundException ClassNotFound
     */
    private static List<Class<?>> getClassesFromJar(String jarFilePath, String path) throws IOException, ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        JarFile jarFile = new JarFile(jarFilePath);
        Enumeration<JarEntry> jarEntries = jarFile.entries();
        while (jarEntries.hasMoreElements()) {
            JarEntry jarEntry = jarEntries.nextElement();
            String entryName = jarEntry.getName();
            if (entryName.startsWith(path) && entryName.endsWith(".class")) {
                String classFullName = entryName.replace("/", ".").substring(0, entryName.length() - 6);
                classes.add(Class.forName(classFullName));
            }
        }
        return classes;
    }
}
