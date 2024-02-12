/*    */ package com.jcraft.jsch.jce;
/*    */ 
/*    */ import com.jcraft.jsch.MAC;
/*    */ import javax.crypto.Mac;
/*    */ import javax.crypto.ShortBufferException;
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
/*    */ 
/*    */ abstract class HMAC
/*    */   implements MAC
/*    */ {
/*    */   protected String name;
/*    */   protected int bsize;
/*    */   protected String algorithm;
/*    */   private Mac mac;
/*    */   
/*    */   public int getBlockSize() {
/* 43 */     return this.bsize;
/*    */   }
/*    */   
/*    */   public void init(byte[] key) throws Exception {
/* 47 */     if (key.length > this.bsize) {
/* 48 */       byte[] tmp = new byte[this.bsize];
/* 49 */       System.arraycopy(key, 0, tmp, 0, this.bsize);
/* 50 */       key = tmp;
/*    */     } 
/* 52 */     SecretKeySpec skey = new SecretKeySpec(key, this.algorithm);
/* 53 */     this.mac = Mac.getInstance(this.algorithm);
/* 54 */     this.mac.init(skey);
/*    */   }
/*    */   
/* 57 */   private final byte[] tmp = new byte[4];
/*    */   public void update(int i) {
/* 59 */     this.tmp[0] = (byte)(i >>> 24);
/* 60 */     this.tmp[1] = (byte)(i >>> 16);
/* 61 */     this.tmp[2] = (byte)(i >>> 8);
/* 62 */     this.tmp[3] = (byte)i;
/* 63 */     update(this.tmp, 0, 4);
/*    */   }
/*    */   
/*    */   public void update(byte[] foo, int s, int l) {
/* 67 */     this.mac.update(foo, s, l);
/*    */   }
/*    */   
/*    */   public void doFinal(byte[] buf, int offset) {
/*    */     try {
/* 72 */       this.mac.doFinal(buf, offset);
/*    */     }
/* 74 */     catch (ShortBufferException e) {
/* 75 */       System.err.println(e);
/*    */     } 
/*    */   }
/*    */   
/*    */   public String getName() {
/* 80 */     return this.name;
/*    */   }
/*    */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\jce\HMAC.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */