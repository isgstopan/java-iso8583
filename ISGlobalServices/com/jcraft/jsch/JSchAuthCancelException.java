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
/*    */ class JSchAuthCancelException
/*    */   extends JSchException
/*    */ {
/*    */   String method;
/*    */   
/*    */   JSchAuthCancelException() {}
/*    */   
/*    */   JSchAuthCancelException(String s) {
/* 39 */     super(s);
/* 40 */     this.method = s;
/*    */   }
/*    */   public String getMethod() {
/* 43 */     return this.method;
/*    */   }
/*    */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\JSchAuthCancelException.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */