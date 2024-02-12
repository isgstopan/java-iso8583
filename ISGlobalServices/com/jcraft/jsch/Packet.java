/*     */ package com.jcraft.jsch;
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
/*     */ public class Packet
/*     */ {
/*  34 */   private static Random random = null; static void setRandom(Random foo) {
/*  35 */     random = foo;
/*     */   }
/*     */   Buffer buffer;
/*  38 */   byte[] ba4 = new byte[4];
/*     */   public Packet(Buffer buffer) {
/*  40 */     this.buffer = buffer;
/*     */   }
/*     */   public void reset() {
/*  43 */     this.buffer.index = 5;
/*     */   }
/*     */   void padding(int bsize) {
/*  46 */     int len = this.buffer.index;
/*  47 */     int pad = -len & bsize - 1;
/*  48 */     if (pad < bsize) {
/*  49 */       pad += bsize;
/*     */     }
/*  51 */     len = len + pad - 4;
/*  52 */     this.ba4[0] = (byte)(len >>> 24);
/*  53 */     this.ba4[1] = (byte)(len >>> 16);
/*  54 */     this.ba4[2] = (byte)(len >>> 8);
/*  55 */     this.ba4[3] = (byte)len;
/*  56 */     System.arraycopy(this.ba4, 0, this.buffer.buffer, 0, 4);
/*  57 */     this.buffer.buffer[4] = (byte)pad;
/*  58 */     synchronized (random) {
/*  59 */       random.fill(this.buffer.buffer, this.buffer.index, pad);
/*     */     } 
/*  61 */     this.buffer.skip(pad);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   int shift(int len, int bsize, int mac) {
/*  72 */     int s = len + 5 + 9;
/*  73 */     int pad = -s & bsize - 1;
/*  74 */     if (pad < bsize) pad += bsize; 
/*  75 */     s += pad;
/*  76 */     s += mac;
/*  77 */     s += 32;
/*     */ 
/*     */     
/*  80 */     if (this.buffer.buffer.length < s + this.buffer.index - 5 - 9 - len) {
/*  81 */       byte[] foo = new byte[s + this.buffer.index - 5 - 9 - len];
/*  82 */       System.arraycopy(this.buffer.buffer, 0, foo, 0, this.buffer.buffer.length);
/*  83 */       this.buffer.buffer = foo;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  93 */     System.arraycopy(this.buffer.buffer, len + 5 + 9, this.buffer.buffer, s, this.buffer.index - 5 - 9 - len);
/*     */ 
/*     */ 
/*     */     
/*  97 */     this.buffer.index = 10;
/*  98 */     this.buffer.putInt(len);
/*  99 */     this.buffer.index = len + 5 + 9;
/* 100 */     return s;
/*     */   }
/*     */   void unshift(byte command, int recipient, int s, int len) {
/* 103 */     System.arraycopy(this.buffer.buffer, s, this.buffer.buffer, 14, len);
/*     */ 
/*     */     
/* 106 */     this.buffer.buffer[5] = command;
/* 107 */     this.buffer.index = 6;
/* 108 */     this.buffer.putInt(recipient);
/* 109 */     this.buffer.putInt(len);
/* 110 */     this.buffer.index = len + 5 + 9;
/*     */   }
/*     */   Buffer getBuffer() {
/* 113 */     return this.buffer;
/*     */   }
/*     */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\Packet.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */