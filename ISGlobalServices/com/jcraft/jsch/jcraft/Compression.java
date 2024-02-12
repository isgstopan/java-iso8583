/*     */ package com.jcraft.jsch.jcraft;
/*     */ 
/*     */ import com.jcraft.jsch.Compression;
/*     */ import com.jcraft.jzlib.ZStream;
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
/*     */ public class Compression
/*     */   implements Compression
/*     */ {
/*     */   private static final int BUF_SIZE = 4096;
/*  36 */   private final int buffer_margin = 52;
/*     */   private int type;
/*     */   private ZStream stream;
/*  39 */   private byte[] tmpbuf = new byte[4096]; private byte[] inflated_buf;
/*     */   
/*     */   public Compression() {
/*  42 */     this.stream = new ZStream();
/*     */   }
/*     */   
/*     */   public void init(int type, int level) {
/*  46 */     if (type == 1) {
/*  47 */       this.stream.deflateInit(level);
/*  48 */       this.type = 1;
/*     */     }
/*  50 */     else if (type == 0) {
/*  51 */       this.stream.inflateInit();
/*  52 */       this.inflated_buf = new byte[4096];
/*  53 */       this.type = 0;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] compress(byte[] buf, int start, int[] len) {
/*  60 */     this.stream.next_in = buf;
/*  61 */     this.stream.next_in_index = start;
/*  62 */     this.stream.avail_in = len[0] - start;
/*     */     
/*  64 */     int outputlen = start;
/*  65 */     byte[] outputbuf = buf;
/*  66 */     int tmp = 0;
/*     */     
/*     */     do {
/*  69 */       this.stream.next_out = this.tmpbuf;
/*  70 */       this.stream.next_out_index = 0;
/*  71 */       this.stream.avail_out = 4096;
/*  72 */       int status = this.stream.deflate(1);
/*  73 */       switch (status) {
/*     */         case 0:
/*  75 */           tmp = 4096 - this.stream.avail_out;
/*  76 */           if (outputbuf.length < outputlen + tmp + 52) {
/*  77 */             byte[] foo = new byte[(outputlen + tmp + 52) * 2];
/*  78 */             System.arraycopy(outputbuf, 0, foo, 0, outputbuf.length);
/*  79 */             outputbuf = foo;
/*     */           } 
/*  81 */           System.arraycopy(this.tmpbuf, 0, outputbuf, outputlen, tmp);
/*  82 */           outputlen += tmp;
/*     */           break;
/*     */         default:
/*  85 */           System.err.println("compress: deflate returnd " + status);
/*     */           break;
/*     */       } 
/*  88 */     } while (this.stream.avail_out == 0);
/*     */     
/*  90 */     len[0] = outputlen;
/*  91 */     return outputbuf;
/*     */   }
/*     */   
/*     */   public byte[] uncompress(byte[] buffer, int start, int[] length) {
/*  95 */     int status, inflated_end = 0;
/*     */     
/*  97 */     this.stream.next_in = buffer;
/*  98 */     this.stream.next_in_index = start;
/*  99 */     this.stream.avail_in = length[0];
/*     */     
/*     */     while (true) {
/* 102 */       this.stream.next_out = this.tmpbuf;
/* 103 */       this.stream.next_out_index = 0;
/* 104 */       this.stream.avail_out = 4096;
/* 105 */       status = this.stream.inflate(1);
/* 106 */       switch (status) {
/*     */         case 0:
/* 108 */           if (this.inflated_buf.length < inflated_end + 4096 - this.stream.avail_out) {
/* 109 */             int len = this.inflated_buf.length * 2;
/* 110 */             if (len < inflated_end + 4096 - this.stream.avail_out)
/* 111 */               len = inflated_end + 4096 - this.stream.avail_out; 
/* 112 */             byte[] foo = new byte[len];
/* 113 */             System.arraycopy(this.inflated_buf, 0, foo, 0, inflated_end);
/* 114 */             this.inflated_buf = foo;
/*     */           } 
/* 116 */           System.arraycopy(this.tmpbuf, 0, this.inflated_buf, inflated_end, 4096 - this.stream.avail_out);
/*     */ 
/*     */           
/* 119 */           inflated_end += 4096 - this.stream.avail_out;
/* 120 */           length[0] = inflated_end;
/*     */           continue;
/*     */         case -5:
/* 123 */           if (inflated_end > buffer.length - start) {
/* 124 */             byte[] foo = new byte[inflated_end + start];
/* 125 */             System.arraycopy(buffer, 0, foo, 0, start);
/* 126 */             System.arraycopy(this.inflated_buf, 0, foo, start, inflated_end);
/* 127 */             buffer = foo;
/*     */           } else {
/*     */             
/* 130 */             System.arraycopy(this.inflated_buf, 0, buffer, start, inflated_end);
/*     */           } 
/* 132 */           length[0] = inflated_end;
/* 133 */           return buffer;
/*     */       }  break;
/* 135 */     }  System.err.println("uncompress: inflate returnd " + status);
/* 136 */     return null;
/*     */   }
/*     */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\jcraft\Compression.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */