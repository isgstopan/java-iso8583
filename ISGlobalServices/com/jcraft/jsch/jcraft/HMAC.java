/*     */ package com.jcraft.jsch.jcraft;
/*     */ 
/*     */ import java.security.MessageDigest;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class HMAC
/*     */ {
/*     */   private static final int B = 64;
/*  47 */   private byte[] k_ipad = null;
/*  48 */   private byte[] k_opad = null;
/*     */   
/*  50 */   private MessageDigest md = null;
/*     */   
/*  52 */   private int bsize = 0;
/*     */   
/*     */   protected void setH(MessageDigest md) {
/*  55 */     this.md = md;
/*  56 */     this.bsize = md.getDigestLength();
/*     */   }
/*     */   public int getBlockSize() {
/*  59 */     return this.bsize;
/*     */   } public void init(byte[] key) throws Exception {
/*  61 */     this.md.reset();
/*  62 */     if (key.length > this.bsize) {
/*  63 */       byte[] tmp = new byte[this.bsize];
/*  64 */       System.arraycopy(key, 0, tmp, 0, this.bsize);
/*  65 */       key = tmp;
/*     */     } 
/*     */ 
/*     */     
/*  69 */     if (key.length > 64) {
/*  70 */       this.md.update(key, 0, key.length);
/*  71 */       key = this.md.digest();
/*     */     } 
/*     */     
/*  74 */     this.k_ipad = new byte[64];
/*  75 */     System.arraycopy(key, 0, this.k_ipad, 0, key.length);
/*  76 */     this.k_opad = new byte[64];
/*  77 */     System.arraycopy(key, 0, this.k_opad, 0, key.length);
/*     */ 
/*     */     
/*  80 */     for (int i = 0; i < 64; i++) {
/*  81 */       this.k_ipad[i] = (byte)(this.k_ipad[i] ^ 0x36);
/*  82 */       this.k_opad[i] = (byte)(this.k_opad[i] ^ 0x5C);
/*     */     } 
/*     */     
/*  85 */     this.md.update(this.k_ipad, 0, 64);
/*     */   }
/*     */   
/*  88 */   private final byte[] tmp = new byte[4];
/*     */   public void update(int i) {
/*  90 */     this.tmp[0] = (byte)(i >>> 24);
/*  91 */     this.tmp[1] = (byte)(i >>> 16);
/*  92 */     this.tmp[2] = (byte)(i >>> 8);
/*  93 */     this.tmp[3] = (byte)i;
/*  94 */     update(this.tmp, 0, 4);
/*     */   }
/*     */   
/*     */   public void update(byte[] foo, int s, int l) {
/*  98 */     this.md.update(foo, s, l);
/*     */   }
/*     */   
/*     */   public void doFinal(byte[] buf, int offset) {
/* 102 */     byte[] result = this.md.digest();
/* 103 */     this.md.update(this.k_opad, 0, 64);
/* 104 */     this.md.update(result, 0, this.bsize); 
/* 105 */     try { this.md.digest(buf, offset, this.bsize); } catch (Exception e) {}
/* 106 */     this.md.update(this.k_ipad, 0, 64);
/*     */   }
/*     */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\jcraft\HMAC.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */