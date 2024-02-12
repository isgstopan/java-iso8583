/*    */ package com.jcraft.jsch.jcraft;
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
/*    */   private static final String name = "hmac-sha1-96";
/*    */   private static final int BSIZE = 12;
/*    */   
/*    */   public int getBlockSize() {
/* 39 */     return 12;
/*    */   }
/* 41 */   private final byte[] _buf16 = new byte[20];
/*    */   public void doFinal(byte[] buf, int offset) {
/* 43 */     super.doFinal(this._buf16, 0);
/* 44 */     System.arraycopy(this._buf16, 0, buf, offset, 12);
/*    */   }
/*    */   
/*    */   public String getName() {
/* 48 */     return "hmac-sha1-96";
/*    */   }
/*    */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\jcraft\HMACSHA196.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */