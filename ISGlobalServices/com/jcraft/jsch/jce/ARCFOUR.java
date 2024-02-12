/*    */ package com.jcraft.jsch.jce;
/*    */ 
/*    */ import com.jcraft.jsch.Cipher;
/*    */ import javax.crypto.Cipher;
/*    */ import javax.crypto.spec.SecretKeySpec;
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
/*    */ public class ARCFOUR
/*    */   implements Cipher
/*    */ {
/*    */   private static final int ivsize = 8;
/*    */   private static final int bsize = 16;
/*    */   private Cipher cipher;
/*    */   
/*    */   public int getIVSize() {
/* 40 */     return 8; } public int getBlockSize() {
/* 41 */     return 16;
/*    */   } public void init(int mode, byte[] key, byte[] iv) throws Exception {
/* 43 */     String pad = "NoPadding";
/*    */     
/* 45 */     if (key.length > 16) {
/* 46 */       byte[] tmp = new byte[16];
/* 47 */       System.arraycopy(key, 0, tmp, 0, tmp.length);
/* 48 */       key = tmp;
/*    */     } 
/*    */     
/*    */     try {
/* 52 */       this.cipher = Cipher.getInstance("RC4");
/* 53 */       SecretKeySpec _key = new SecretKeySpec(key, "RC4");
/* 54 */       synchronized (Cipher.class) {
/* 55 */         this.cipher.init((mode == 0) ? 1 : 2, _key);
/*    */       
/*    */       }
/*    */ 
/*    */     
/*    */     }
/* 61 */     catch (Exception e) {
/* 62 */       this.cipher = null;
/* 63 */       throw e;
/*    */     } 
/*    */   }
/*    */   public void update(byte[] foo, int s1, int len, byte[] bar, int s2) throws Exception {
/* 67 */     this.cipher.update(foo, s1, len, bar, s2);
/*    */   } public boolean isCBC() {
/* 69 */     return false;
/*    */   }
/*    */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\jce\ARCFOUR.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */