/*     */ package com.jcraft.jsch;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.SocketException;
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
/*     */ public class IO
/*     */ {
/*     */   InputStream in;
/*     */   OutputStream out;
/*     */   OutputStream out_ext;
/*     */   private boolean in_dontclose = false;
/*     */   private boolean out_dontclose = false;
/*     */   private boolean out_ext_dontclose = false;
/*     */   
/*     */   void setOutputStream(OutputStream out) {
/*  43 */     this.out = out;
/*     */   } void setOutputStream(OutputStream out, boolean dontclose) {
/*  45 */     this.out_dontclose = dontclose;
/*  46 */     setOutputStream(out);
/*     */   } void setExtOutputStream(OutputStream out) {
/*  48 */     this.out_ext = out;
/*     */   } void setExtOutputStream(OutputStream out, boolean dontclose) {
/*  50 */     this.out_ext_dontclose = dontclose;
/*  51 */     setExtOutputStream(out);
/*     */   } void setInputStream(InputStream in) {
/*  53 */     this.in = in;
/*     */   } void setInputStream(InputStream in, boolean dontclose) {
/*  55 */     this.in_dontclose = dontclose;
/*  56 */     setInputStream(in);
/*     */   }
/*     */   
/*     */   public void put(Packet p) throws IOException, SocketException {
/*  60 */     this.out.write(p.buffer.buffer, 0, p.buffer.index);
/*  61 */     this.out.flush();
/*     */   }
/*     */   void put(byte[] array, int begin, int length) throws IOException {
/*  64 */     this.out.write(array, begin, length);
/*  65 */     this.out.flush();
/*     */   }
/*     */   void put_ext(byte[] array, int begin, int length) throws IOException {
/*  68 */     this.out_ext.write(array, begin, length);
/*  69 */     this.out_ext.flush();
/*     */   }
/*     */   
/*     */   int getByte() throws IOException {
/*  73 */     return this.in.read();
/*     */   }
/*     */   
/*     */   void getByte(byte[] array) throws IOException {
/*  77 */     getByte(array, 0, array.length);
/*     */   }
/*     */   
/*     */   void getByte(byte[] array, int begin, int length) throws IOException {
/*     */     do {
/*  82 */       int completed = this.in.read(array, begin, length);
/*  83 */       if (completed < 0) {
/*  84 */         throw new IOException("End of IO Stream Read");
/*     */       }
/*  86 */       begin += completed;
/*  87 */       length -= completed;
/*     */     }
/*  89 */     while (length > 0);
/*     */   }
/*     */   
/*     */   void out_close() {
/*     */     try {
/*  94 */       if (this.out != null && !this.out_dontclose) this.out.close(); 
/*  95 */       this.out = null;
/*     */     }
/*  97 */     catch (Exception ee) {}
/*     */   }
/*     */   
/*     */   public void close() {
/*     */     try {
/* 102 */       if (this.in != null && !this.in_dontclose) this.in.close(); 
/* 103 */       this.in = null;
/*     */     }
/* 105 */     catch (Exception ee) {}
/*     */     
/* 107 */     out_close();
/*     */     
/*     */     try {
/* 110 */       if (this.out_ext != null && !this.out_ext_dontclose) this.out_ext.close(); 
/* 111 */       this.out_ext = null;
/*     */     }
/* 113 */     catch (Exception ee) {}
/*     */   }
/*     */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\IO.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */