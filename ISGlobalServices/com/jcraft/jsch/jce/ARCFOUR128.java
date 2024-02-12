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
/*    */ public class ARCFOUR128
/*    */   implements Cipher
/*    */ {
/*    */   private static final int ivsize = 8;
/*    */   private static final int bsize = 16;
/*    */   private static final int skip = 1536;
/*    */   private Cipher cipher;
/*    */   
/*    */   public int getIVSize() {
/* 41 */     return 8; } public int getBlockSize() {
/* 42 */     return 16;
/*    */   }
/*    */   public void init(int mode, byte[] key, byte[] iv) throws Exception {
/* 45 */     if (key.length > 16) {
/* 46 */       byte[] tmp = new byte[16];
/* 47 */       System.arraycopy(key, 0, tmp, 0, tmp.length);
/* 48 */       key = tmp;
/*    */     } 
/*    */     try {
/* 51 */       this.cipher = Cipher.getInstance("RC4");
/* 52 */       SecretKeySpec _key = new SecretKeySpec(key, "RC4");
/* 53 */       synchronized (Cipher.class) {
/* 54 */         this.cipher.init((mode == 0) ? 1 : 2, _key);
/*    */       } 
/*    */ 
/*    */ 
/*    */       
/* 59 */       byte[] foo = new byte[1];
/* 60 */       for (int i = 0; i < 1536; i++) {
/* 61 */         this.cipher.update(foo, 0, 1, foo, 0);
/*    */       }
/*    */     }
/* 64 */     catch (Exception e) {
/* 65 */       this.cipher = null;
/* 66 */       throw e;
/*    */     } 
/*    */   }
/*    */   public void update(byte[] foo, int s1, int len, byte[] bar, int s2) throws Exception {
/* 70 */     this.cipher.update(foo, s1, len, bar, s2);
/*    */   } public boolean isCBC() {
/* 72 */     return false;
/*    */   }
/*    */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\jce\ARCFOUR128.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */