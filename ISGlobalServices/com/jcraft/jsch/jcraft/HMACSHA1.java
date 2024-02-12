/*    */ package com.jcraft.jsch.jcraft;
/*    */ 
/*    */ import com.jcraft.jsch.MAC;
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
/*    */ 
/*    */ public class HMACSHA1
/*    */   extends HMAC
/*    */   implements MAC
/*    */ {
/*    */   private static final String name = "hmac-sha1";
/*    */   
/*    */   public HMACSHA1() {
/* 40 */     MessageDigest md = null; try {
/* 41 */       md = MessageDigest.getInstance("SHA-1");
/* 42 */     } catch (Exception e) {
/* 43 */       System.err.println(e);
/*    */     } 
/* 45 */     setH(md);
/*    */   }
/*    */   
/*    */   public String getName() {
/* 49 */     return "hmac-sha1";
/*    */   }
/*    */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\jcraft\HMACSHA1.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */