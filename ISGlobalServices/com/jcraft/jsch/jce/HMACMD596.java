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
/*    */ public class HMACMD596
/*    */   extends HMACMD5
/*    */ {
/*    */   public int getBlockSize() {
/* 38 */     return 12;
/*    */   }
/*    */   
/* 41 */   private final byte[] _buf16 = new byte[16];
/*    */   public void doFinal(byte[] buf, int offset) {
/* 43 */     super.doFinal(this._buf16, 0);
/* 44 */     System.arraycopy(this._buf16, 0, buf, offset, 12);
/*    */   }
/*    */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\jce\HMACMD596.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */