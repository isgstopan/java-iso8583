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
/*    */ public class HMACMD5
/*    */   extends HMAC
/*    */   implements MAC
/*    */ {
/*    */   private static final String name = "hmac-md5";
/*    */   
/*    */   public HMACMD5() {
/* 40 */     MessageDigest md = null; try {
/* 41 */       md = MessageDigest.getInstance("MD5");
/* 42 */     } catch (Exception e) {
/* 43 */       System.err.println(e);
/*    */     } 
/* 45 */     setH(md);
/*    */   }
/*    */   
/*    */   public String getName() {
/* 49 */     return "hmac-md5";
/*    */   }
/*    */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\jcraft\HMACMD5.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */