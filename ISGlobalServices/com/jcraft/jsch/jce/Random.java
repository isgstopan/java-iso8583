/*    */ package com.jcraft.jsch.jce;
/*    */ 
/*    */ import com.jcraft.jsch.Random;
/*    */ import java.security.SecureRandom;
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
/*    */ public class Random
/*    */   implements Random
/*    */ {
/* 35 */   private byte[] tmp = new byte[16];
/* 36 */   private SecureRandom random = null;
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
/*    */   public Random() {
/* 48 */     this.random = new SecureRandom();
/*    */   }
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
/*    */   public void fill(byte[] foo, int start, int len) {
/* 77 */     if (len > this.tmp.length) this.tmp = new byte[len]; 
/* 78 */     this.random.nextBytes(this.tmp);
/* 79 */     System.arraycopy(this.tmp, 0, foo, start, len);
/*    */   }
/*    */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\jce\Random.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */