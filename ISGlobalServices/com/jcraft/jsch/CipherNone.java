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
/*    */ public class CipherNone
/*    */   implements Cipher
/*    */ {
/*    */   private static final int ivsize = 8;
/*    */   private static final int bsize = 16;
/*    */   
/*    */   public int getIVSize() {
/* 35 */     return 8; } public int getBlockSize() {
/* 36 */     return 16;
/*    */   }
/*    */   public void init(int mode, byte[] key, byte[] iv) throws Exception {}
/*    */   public void update(byte[] foo, int s1, int len, byte[] bar, int s2) throws Exception {}
/*    */   public boolean isCBC() {
/* 41 */     return false;
/*    */   }
/*    */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\CipherNone.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */