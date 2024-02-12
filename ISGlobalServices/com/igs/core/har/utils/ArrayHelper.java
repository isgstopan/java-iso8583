/*    */ package com.igs.core.har.utils;
/*    */ 
/*    */ import java.lang.reflect.Array;
/*    */ import java.util.List;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class ArrayHelper
/*    */ {
/*    */   public static String[] StringListToArray(List<String> list) {
/* 13 */     String[] res = new String[list.size()];
/* 14 */     list.toArray(res);
/* 15 */     return res;
/*    */   }
/*    */   
/*    */   public static <T> T[] asArray(Class<T> c, List<T> list) {
/* 19 */     T[] res = (T[])Array.newInstance(c, list.size());
/* 20 */     list.toArray(res);
/* 21 */     return res;
/*    */   }
/*    */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\igs\core\ha\\utils\ArrayHelper.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */