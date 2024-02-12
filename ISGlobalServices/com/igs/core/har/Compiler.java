/*    */ package com.igs.core.har;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.lang.reflect.InvocationTargetException;
/*    */ import java.net.URL;
/*    */ import java.net.URLClassLoader;
/*    */ import java.util.Arrays;
/*    */ import javax.tools.JavaCompiler;
/*    */ import javax.tools.JavaFileObject;
/*    */ import javax.tools.StandardJavaFileManager;
/*    */ import javax.tools.ToolProvider;
/*    */ 
/*    */ 
/*    */ public class Compiler
/*    */ {
/*    */   public boolean Compile(String filename, String path) {
/* 17 */     File file = new File(filename);
/*    */     
/* 19 */     String dirPath = path.equals("") ? file.getParent() : path;
/*    */     
/* 21 */     File dir = new File(dirPath);
/*    */     
/* 23 */     JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
/* 24 */     StandardJavaFileManager sjfm = compiler.getStandardFileManager(null, null, null);
/* 25 */     Iterable<? extends JavaFileObject> fileObjects = sjfm.getJavaFileObjects(new String[] { filename });
/* 26 */     String[] options = { "-d", dir.getPath() };
/* 27 */     return compiler.getTask(null, null, null, Arrays.asList(options), null, fileObjects).call().booleanValue();
/*    */   }
/*    */ 
/*    */   
/*    */   public Class<?> LoadCompiledCode(String dirPath, String className) throws Exception {
/* 32 */     File dir = new File(dirPath);
/*    */     
/* 34 */     URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { dir.toURI().toURL() });
/* 35 */     Class<?> cls = Class.forName(className, true, classLoader);
/* 36 */     classLoader.close();
/* 37 */     return cls;
/*    */   }
/*    */ 
/*    */   
/*    */   public Class<?> LoadClass(String dirPath, String className) throws Exception {
/* 42 */     File dir = new File(dirPath);
/*    */     
/* 44 */     URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { dir.toURI().toURL() });
/* 45 */     Class<?> cls = classLoader.loadClass(className);
/* 46 */     classLoader.close();
/* 47 */     return cls;
/*    */   }
/*    */ 
/*    */   
/*    */   public Class<?> LoadSourceCode(String filename, String className) throws Exception {
/* 52 */     File file = new File(filename);
/* 53 */     File dir = new File(file.getParent());
/*    */     
/* 55 */     URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { dir.toURI().toURL() });
/* 56 */     Class<?> cls = Class.forName(className, true, classLoader);
/* 57 */     classLoader.close();
/* 58 */     return cls;
/*    */   }
/*    */ 
/*    */   
/*    */   public Object GetInstance(String filename, String className) throws Exception {
/* 63 */     File file = new File(filename);
/* 64 */     File dir = new File(file.getParent());
/*    */     
/* 66 */     URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { dir.toURI().toURL() });
/* 67 */     Class<?> cls = Class.forName(className, true, classLoader);
/* 68 */     return cls.newInstance();
/*    */   }
/*    */ 
/*    */   
/*    */   public Object Execute(Object instance, String methodName, Class[] parametersType, Object parameters) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
/* 73 */     return instance.getClass().getDeclaredMethod(methodName, parametersType).invoke(instance, new Object[] { parameters });
/*    */   }
/*    */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\igs\core\har\Compiler.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */