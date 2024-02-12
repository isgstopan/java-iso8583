/*    */ package com.jcraft.jsch.jce;
/*    */ 
/*    */ import com.jcraft.jsch.KeyPairGenRSA;
/*    */ import java.security.KeyPair;
/*    */ import java.security.KeyPairGenerator;
/*    */ import java.security.PrivateKey;
/*    */ import java.security.PublicKey;
/*    */ import java.security.SecureRandom;
/*    */ import java.security.interfaces.RSAPrivateCrtKey;
/*    */ import java.security.interfaces.RSAPrivateKey;
/*    */ import java.security.interfaces.RSAPublicKey;
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
/*    */ public class KeyPairGenRSA
/*    */   implements KeyPairGenRSA
/*    */ {
/*    */   byte[] d;
/*    */   byte[] e;
/*    */   byte[] n;
/*    */   byte[] c;
/*    */   byte[] ep;
/*    */   byte[] eq;
/*    */   byte[] p;
/*    */   byte[] q;
/*    */   
/*    */   public void init(int key_size) throws Exception {
/* 47 */     KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
/* 48 */     keyGen.initialize(key_size, new SecureRandom());
/* 49 */     KeyPair pair = keyGen.generateKeyPair();
/*    */     
/* 51 */     PublicKey pubKey = pair.getPublic();
/* 52 */     PrivateKey prvKey = pair.getPrivate();
/*    */     
/* 54 */     this.d = ((RSAPrivateKey)prvKey).getPrivateExponent().toByteArray();
/* 55 */     this.e = ((RSAPublicKey)pubKey).getPublicExponent().toByteArray();
/* 56 */     this.n = ((RSAPrivateKey)prvKey).getModulus().toByteArray();
/*    */     
/* 58 */     this.c = ((RSAPrivateCrtKey)prvKey).getCrtCoefficient().toByteArray();
/* 59 */     this.ep = ((RSAPrivateCrtKey)prvKey).getPrimeExponentP().toByteArray();
/* 60 */     this.eq = ((RSAPrivateCrtKey)prvKey).getPrimeExponentQ().toByteArray();
/* 61 */     this.p = ((RSAPrivateCrtKey)prvKey).getPrimeP().toByteArray();
/* 62 */     this.q = ((RSAPrivateCrtKey)prvKey).getPrimeQ().toByteArray();
/*    */   }
/* 64 */   public byte[] getD() { return this.d; }
/* 65 */   public byte[] getE() { return this.e; }
/* 66 */   public byte[] getN() { return this.n; }
/* 67 */   public byte[] getC() { return this.c; }
/* 68 */   public byte[] getEP() { return this.ep; }
/* 69 */   public byte[] getEQ() { return this.eq; }
/* 70 */   public byte[] getP() { return this.p; } public byte[] getQ() {
/* 71 */     return this.q;
/*    */   }
/*    */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\jce\KeyPairGenRSA.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */