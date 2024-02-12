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
/*    */ class JSchPartialAuthException
/*    */   extends JSchException
/*    */ {
/*    */   String methods;
/*    */   
/*    */   public JSchPartialAuthException() {}
/*    */   
/*    */   public JSchPartialAuthException(String s) {
/* 39 */     super(s);
/* 40 */     this.methods = s;
/*    */   }
/*    */   public String getMethods() {
/* 43 */     return this.methods;
/*    */   }
/*    */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\JSchPartialAuthException.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */