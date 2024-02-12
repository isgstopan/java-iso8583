/*    */ package com.igs.core.har.utils;
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class StringHelper
/*    */ {
/*    */   public static String PadRight(String s, int n) {
/*  8 */     return String.format("%1$-" + n + "s", new Object[] { s });
/*    */   }
/*    */   
/*    */   public static String PadLeft(String s, int n) {
/* 12 */     return String.format("%1$" + n + "s", new Object[] { s });
/*    */   }
/*    */   
/*    */   public static String PadLeft(String s, int n, char padChar) {
/* 16 */     StringBuilder res = new StringBuilder(s);
/* 17 */     for (int i = s.length(); i < n; i++) {
/* 18 */       res.append(padChar);
/*    */     }
/* 20 */     return res.toString();
/*    */   }
/*    */   
/*    */   public static String PadRight(String s, int n, char padChar) {
/* 24 */     StringBuilder res = new StringBuilder();
/* 25 */     for (int i = 0; i < n - s.length(); i++) {
/* 26 */       res.append(padChar);
/*    */     }
/* 28 */     res.append(s);
/* 29 */     return res.toString();
/*    */   }
/*    */   
/*    */   public static String Limit(String s, int limit) {
/* 33 */     if (s == null) {
/* 34 */       return "";
/*    */     }
/*    */     
/* 37 */     return s.substring(0, Math.min(s.length(), limit));
/*    */   }
/*    */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\igs\core\ha\\utils\StringHelper.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */