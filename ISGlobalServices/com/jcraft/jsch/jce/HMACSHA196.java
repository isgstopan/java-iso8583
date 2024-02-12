/*    */ package com.jcraft.jsch.jce;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class HMACSHA196
/*    */   extends HMACSHA1
/*    */ {
/*    */   public int getBlockSize() {
/* 39 */     return 12;
/*    */   }
/*    */   
/* 42 */   private final byte[] _buf20 = new byte[20];
/*    */   public void doFinal(byte[] buf, int offset) {
/* 44 */     super.doFinal(this._buf20, 0);
/* 45 */     System.arraycopy(this._buf20, 0, buf, offset, 12);
/*    */   }
/*    */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\jce\HMACSHA196.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */