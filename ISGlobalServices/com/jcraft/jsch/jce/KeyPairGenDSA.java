/*    */ package com.jcraft.jsch.jce;
/*    */ 
/*    */ import com.jcraft.jsch.KeyPairGenDSA;
/*    */ import java.security.KeyPair;
/*    */ import java.security.KeyPairGenerator;
/*    */ import java.security.PrivateKey;
/*    */ import java.security.PublicKey;
/*    */ import java.security.SecureRandom;
/*    */ import java.security.interfaces.DSAKey;
/*    */ import java.security.interfaces.DSAParams;
/*    */ import java.security.interfaces.DSAPrivateKey;
/*    */ import java.security.interfaces.DSAPublicKey;
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
/*    */ public class KeyPairGenDSA
/*    */   implements KeyPairGenDSA
/*    */ {
/*    */   byte[] x;
/*    */   byte[] y;
/*    */   byte[] p;
/*    */   byte[] q;
/*    */   byte[] g;
/*    */   
/*    */   public void init(int key_size) throws Exception {
/* 43 */     KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
/* 44 */     keyGen.initialize(key_size, new SecureRandom());
/* 45 */     KeyPair pair = keyGen.generateKeyPair();
/* 46 */     PublicKey pubKey = pair.getPublic();
/* 47 */     PrivateKey prvKey = pair.getPrivate();
/*    */     
/* 49 */     this.x = ((DSAPrivateKey)prvKey).getX().toByteArray();
/* 50 */     this.y = ((DSAPublicKey)pubKey).getY().toByteArray();
/*    */     
/* 52 */     DSAParams params = ((DSAKey)prvKey).getParams();
/* 53 */     this.p = params.getP().toByteArray();
/* 54 */     this.q = params.getQ().toByteArray();
/* 55 */     this.g = params.getG().toByteArray();
/*    */   }
/* 57 */   public byte[] getX() { return this.x; }
/* 58 */   public byte[] getY() { return this.y; }
/* 59 */   public byte[] getP() { return this.p; }
/* 60 */   public byte[] getQ() { return this.q; } public byte[] getG() {
/* 61 */     return this.g;
/*    */   }
/*    */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\jce\KeyPairGenDSA.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */