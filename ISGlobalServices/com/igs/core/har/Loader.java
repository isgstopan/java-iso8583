/*    */ package com.igs.core.har;
/*    */ 
/*    */ import java.lang.reflect.Constructor;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Loader
/*    */ {
/*  9 */   public static Compiler engine = null;
/*    */ 
/*    */   
/*    */   public static Object Load(String dirPath, String className, Class[] constructor, Object[] arguments) throws Exception {
/* 13 */     engine = new Compiler();
/* 14 */     Class<?> classInstance = engine.LoadClass(dirPath, className);
/*    */     
/* 16 */     Constructor<?> Constructor = classInstance.getConstructor(constructor);
/* 17 */     return Constructor.newInstance(arguments);
/*    */   }
/*    */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\igs\core\har\Loader.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */