/*    */ package com.jcraft.jsch.jce;
/*    */ 
/*    */ import com.jcraft.jsch.PBKDF;
/*    */ import java.security.NoSuchAlgorithmException;
/*    */ import java.security.spec.InvalidKeySpecException;
/*    */ import javax.crypto.SecretKeyFactory;
/*    */ import javax.crypto.spec.PBEKeySpec;
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
/*    */ 
/*    */ public class PBKDF
/*    */   implements PBKDF
/*    */ {
/*    */   public byte[] getKey(byte[] _pass, byte[] salt, int iterations, int size) {
/* 41 */     char[] pass = new char[_pass.length];
/* 42 */     for (int i = 0; i < _pass.length; i++) {
/* 43 */       pass[i] = (char)(_pass[i] & 0xFF);
/*    */     }
/*    */     try {
/* 46 */       PBEKeySpec spec = new PBEKeySpec(pass, salt, iterations, size * 8);
/*    */       
/* 48 */       SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
/*    */       
/* 50 */       byte[] key = skf.generateSecret(spec).getEncoded();
/* 51 */       return key;
/*    */     }
/* 53 */     catch (InvalidKeySpecException e) {
/*    */     
/* 55 */     } catch (NoSuchAlgorithmException e) {}
/*    */     
/* 57 */     return null;
/*    */   }
/*    */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\jce\PBKDF.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */