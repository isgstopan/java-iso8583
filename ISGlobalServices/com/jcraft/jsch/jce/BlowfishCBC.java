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
/*    */ public class BlowfishCBC
/*    */   implements Cipher
/*    */ {
/*    */   private static final int ivsize = 8;
/*    */   private static final int bsize = 16;
/*    */   private Cipher cipher;
/*    */   
/*    */   public int getIVSize() {
/* 39 */     return 8; } public int getBlockSize() {
/* 40 */     return 16;
/*    */   } public void init(int mode, byte[] key, byte[] iv) throws Exception {
/* 42 */     String pad = "NoPadding";
/*    */ 
/*    */     
/* 45 */     if (iv.length > 8) {
/* 46 */       byte[] tmp = new byte[8];
/* 47 */       System.arraycopy(iv, 0, tmp, 0, tmp.length);
/* 48 */       iv = tmp;
/*    */     } 
/* 50 */     if (key.length > 16) {
/* 51 */       byte[] tmp = new byte[16];
/* 52 */       System.arraycopy(key, 0, tmp, 0, tmp.length);
/* 53 */       key = tmp;
/*    */     } 
/*    */     try {
/* 56 */       SecretKeySpec skeySpec = new SecretKeySpec(key, "Blowfish");
/* 57 */       this.cipher = Cipher.getInstance("Blowfish/CBC/" + pad);
/* 58 */       synchronized (Cipher.class) {
/* 59 */         this.cipher.init((mode == 0) ? 1 : 2, skeySpec, new IvParameterSpec(iv));
/*    */       
/*    */       }
/*    */ 
/*    */     
/*    */     }
/* 65 */     catch (Exception e) {
/* 66 */       throw e;
/*    */     } 
/*    */   }
/*    */   public void update(byte[] foo, int s1, int len, byte[] bar, int s2) throws Exception {
/* 70 */     this.cipher.update(foo, s1, len, bar, s2);
/*    */   } public boolean isCBC() {
/* 72 */     return true;
/*    */   }
/*    */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\jce\BlowfishCBC.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */