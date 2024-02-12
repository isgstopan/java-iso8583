/*    */ package com.igs.core.har.utils;
/*    */ 
/*    */ import java.io.File;
/*    */ import java.util.Comparator;
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class FileHelper
/*    */ {
/*    */   public static class LastModifiedComparator
/*    */     implements Comparator<File>
/*    */   {
/*    */     public int compare(File o1, File o2) {
/* 14 */       if (o1.lastModified() > o2.lastModified()) {
/* 15 */         return -1;
/*    */       }
/* 17 */       if (o1.lastModified() < o2.lastModified()) {
/* 18 */         return 1;
/*    */       }
/* 20 */       return 0;
/*    */     }
/*    */   }
/*    */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\igs\core\ha\\utils\FileHelper.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */