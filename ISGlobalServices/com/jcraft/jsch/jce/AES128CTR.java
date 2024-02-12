/*    */ package com.jcraft.jsch.jce;
/*    */ 
/*    */ import com.jcraft.jsch.Cipher;
/*    */ import javax.crypto.Cipher;
/*    */ import javax.crypto.spec.IvParameterSpec;
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
/*    */ public class AES128CTR
/*    */   implements Cipher
/*    */ {
/*    */   private static final int ivsize = 16;
/*    */   private static final int bsize = 16;
/*    */   private Cipher cipher;
/*    */   
/*    */   public int getIVSize() {
/* 39 */     return 16; } public int getBlockSize() {
/* 40 */     return 16;
/*    */   } public void init(int mode, byte[] key, byte[] iv) throws Exception {
/* 42 */     String pad = "NoPadding";
/*    */     
/* 44 */     if (iv.length > 16) {
/* 45 */       byte[] tmp = new byte[16];
/* 46 */       System.arraycopy(iv, 0, tmp, 0, tmp.length);
/* 47 */       iv = tmp;
/*    */     } 
/* 49 */     if (key.length > 16) {
/* 50 */       byte[] tmp = new byte[16];
/* 51 */       System.arraycopy(key, 0, tmp, 0, tmp.length);
/* 52 */       key = tmp;
/*    */     } 
/*    */     
/*    */     try {
/* 56 */       SecretKeySpec keyspec = new SecretKeySpec(key, "AES");
/* 57 */       this.cipher = Cipher.getInstance("AES/CTR/" + pad);
/* 58 */       synchronized (Cipher.class) {
/* 59 */         this.cipher.init((mode == 0) ? 1 : 2, keyspec, new IvParameterSpec(iv));
/*    */       
/*    */       }
/*    */ 
/*    */     
/*    */     }
/* 65 */     catch (Exception e) {
/* 66 */       this.cipher = null;
/* 67 */       throw e;
/*    */     } 
/*    */   }
/*    */   public void update(byte[] foo, int s1, int len, byte[] bar, int s2) throws Exception {
/* 71 */     this.cipher.update(foo, s1, len, bar, s2);
/*    */   }
/*    */   public boolean isCBC() {
/* 74 */     return false;
/*    */   }
/*    */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\jce\AES128CTR.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */