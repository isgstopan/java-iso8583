/*    */ package com.jcraft.jsch;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SftpException
/*    */   extends Exception
/*    */ {
/*    */   public int id;
/* 35 */   private Throwable cause = null;
/*    */   public SftpException(int id, String message) {
/* 37 */     super(message);
/* 38 */     this.id = id;
/*    */   }
/*    */   public SftpException(int id, String message, Throwable e) {
/* 41 */     super(message);
/* 42 */     this.id = id;
/* 43 */     this.cause = e;
/*    */   }
/*    */   public String toString() {
/* 46 */     return this.id + ": " + getMessage();
/*    */   }
/*    */   public Throwable getCause() {
/* 49 */     return this.cause;
/*    */   }
/*    */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\SftpException.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */