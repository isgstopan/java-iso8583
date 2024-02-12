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
/*    */ public class JSchException
/*    */   extends Exception
/*    */ {
/* 34 */   private Throwable cause = null;
/*    */   
/*    */   public JSchException() {}
/*    */   
/*    */   public JSchException(String s) {
/* 39 */     super(s);
/*    */   }
/*    */   public JSchException(String s, Throwable e) {
/* 42 */     super(s);
/* 43 */     this.cause = e;
/*    */   }
/*    */   public Throwable getCause() {
/* 46 */     return this.cause;
/*    */   }
/*    */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\JSchException.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */