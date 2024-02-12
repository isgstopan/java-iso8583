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
/*    */ public class HMACMD596
/*    */   extends HMACMD5
/*    */ {
/*    */   private static final String name = "hmac-md5-96";
/*    */   private static final int BSIZE = 12;
/*    */   
/*    */   public int getBlockSize() {
/* 39 */     return 12;
/*    */   }
/* 41 */   private final byte[] _buf16 = new byte[16];
/*    */   public void doFinal(byte[] buf, int offset) {
/* 43 */     super.doFinal(this._buf16, 0);
/* 44 */     System.arraycopy(this._buf16, 0, buf, offset, 12);
/*    */   }
/*    */   
/*    */   public String getName() {
/* 48 */     return "hmac-md5-96";
/*    */   }
/*    */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\jcraft\HMACMD596.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */