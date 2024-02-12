/*    */ package com.jcraft.jsch.jce;
/*    */ 
/*    */ import com.jcraft.jsch.Cipher;
/*    */ import javax.crypto.Cipher;
/*    */ import javax.crypto.SecretKey;
/*    */ import javax.crypto.SecretKeyFactory;
/*    */ import javax.crypto.spec.DESedeKeySpec;
/*    */ import javax.crypto.spec.IvParameterSpec;
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
/*    */ public class TripleDESCTR
/*    */   implements Cipher
/*    */ {
/*    */   private static final int ivsize = 8;
/*    */   private static final int bsize = 24;
/*    */   private Cipher cipher;
/*    */   
/*    */   public int getIVSize() {
/* 40 */     return 8; } public int getBlockSize() {
/* 41 */     return 24;
/*    */   } public void init(int mode, byte[] key, byte[] iv) throws Exception {
/* 43 */     String pad = "NoPadding";
/*    */ 
/*    */     
/* 46 */     if (iv.length > 8) {
/* 47 */       byte[] tmp = new byte[8];
/* 48 */       System.arraycopy(iv, 0, tmp, 0, tmp.length);
/* 49 */       iv = tmp;
/*    */     } 
/* 51 */     if (key.length > 24) {
/* 52 */       byte[] tmp = new byte[24];
/* 53 */       System.arraycopy(key, 0, tmp, 0, tmp.length);
/* 54 */       key = tmp;
/*    */     } 
/*    */     
/*    */     try {
/* 58 */       this.cipher = Cipher.getInstance("DESede/CTR/" + pad);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */       
/* 67 */       DESedeKeySpec keyspec = new DESedeKeySpec(key);
/* 68 */       SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("DESede");
/* 69 */       SecretKey _key = keyfactory.generateSecret(keyspec);
/* 70 */       synchronized (Cipher.class) {
/* 71 */         this.cipher.init((mode == 0) ? 1 : 2, _key, new IvParameterSpec(iv));
/*    */       
/*    */       }
/*    */ 
/*    */     
/*    */     }
/* 77 */     catch (Exception e) {
/* 78 */       this.cipher = null;
/* 79 */       throw e;
/*    */     } 
/*    */   }
/*    */   public void update(byte[] foo, int s1, int len, byte[] bar, int s2) throws Exception {
/* 83 */     this.cipher.update(foo, s1, len, bar, s2);
/*    */   } public boolean isCBC() {
/* 85 */     return false;
/*    */   }
/*    */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\jce\TripleDESCTR.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */