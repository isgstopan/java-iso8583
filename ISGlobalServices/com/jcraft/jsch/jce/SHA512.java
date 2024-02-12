/*    */ package com.jcraft.jsch.jce;
/*    */ 
/*    */ import com.jcraft.jsch.HASH;
/*    */ import java.security.MessageDigest;
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
/*    */ public class SHA512
/*    */   implements HASH
/*    */ {
/*    */   MessageDigest md;
/*    */   
/*    */   public int getBlockSize() {
/* 36 */     return 64; } public void init() throws Exception {
/*    */     try {
/* 38 */       this.md = MessageDigest.getInstance("SHA-512");
/* 39 */     } catch (Exception e) {
/* 40 */       System.err.println(e);
/*    */     } 
/*    */   }
/*    */   public void update(byte[] foo, int start, int len) throws Exception {
/* 44 */     this.md.update(foo, start, len);
/*    */   }
/*    */   public byte[] digest() throws Exception {
/* 47 */     return this.md.digest();
/*    */   }
/*    */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\jce\SHA512.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */