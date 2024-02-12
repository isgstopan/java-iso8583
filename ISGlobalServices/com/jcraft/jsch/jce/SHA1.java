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
/*    */ 
/*    */ 
/*    */ public class SHA1
/*    */   implements HASH
/*    */ {
/*    */   MessageDigest md;
/*    */   
/*    */   public int getBlockSize() {
/* 38 */     return 20; } public void init() throws Exception {
/*    */     try {
/* 40 */       this.md = MessageDigest.getInstance("SHA-1");
/* 41 */     } catch (Exception e) {
/* 42 */       System.err.println(e);
/*    */     } 
/*    */   }
/*    */   public void update(byte[] foo, int start, int len) throws Exception {
/* 46 */     this.md.update(foo, start, len);
/*    */   }
/*    */   public byte[] digest() throws Exception {
/* 49 */     return this.md.digest();
/*    */   }
/*    */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\jce\SHA1.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */