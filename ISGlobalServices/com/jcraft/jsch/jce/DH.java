/*    */ package com.jcraft.jsch.jce;
/*    */ 
/*    */ import com.jcraft.jsch.DH;
/*    */ import com.jcraft.jsch.JSchException;
/*    */ import java.math.BigInteger;
/*    */ import java.security.KeyFactory;
/*    */ import java.security.KeyPair;
/*    */ import java.security.KeyPairGenerator;
/*    */ import java.security.PublicKey;
/*    */ import javax.crypto.KeyAgreement;
/*    */ import javax.crypto.interfaces.DHPublicKey;
/*    */ import javax.crypto.spec.DHParameterSpec;
/*    */ import javax.crypto.spec.DHPublicKeySpec;
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
/*    */ public class DH
/*    */   implements DH
/*    */ {
/*    */   BigInteger p;
/*    */   BigInteger g;
/*    */   BigInteger e;
/*    */   byte[] e_array;
/*    */   BigInteger f;
/*    */   BigInteger K;
/*    */   byte[] K_array;
/*    */   private KeyPairGenerator myKpairGen;
/*    */   private KeyAgreement myKeyAgree;
/*    */   
/*    */   public void init() throws Exception {
/* 50 */     this.myKpairGen = KeyPairGenerator.getInstance("DH");
/* 51 */     this.myKeyAgree = KeyAgreement.getInstance("DH");
/*    */   }
/*    */   public byte[] getE() throws Exception {
/* 54 */     if (this.e == null) {
/* 55 */       DHParameterSpec dhSkipParamSpec = new DHParameterSpec(this.p, this.g);
/* 56 */       this.myKpairGen.initialize(dhSkipParamSpec);
/* 57 */       KeyPair myKpair = this.myKpairGen.generateKeyPair();
/* 58 */       this.myKeyAgree.init(myKpair.getPrivate());
/* 59 */       this.e = ((DHPublicKey)myKpair.getPublic()).getY();
/* 60 */       this.e_array = this.e.toByteArray();
/*    */     } 
/* 62 */     return this.e_array;
/*    */   }
/*    */   public byte[] getK() throws Exception {
/* 65 */     if (this.K == null) {
/* 66 */       KeyFactory myKeyFac = KeyFactory.getInstance("DH");
/* 67 */       DHPublicKeySpec keySpec = new DHPublicKeySpec(this.f, this.p, this.g);
/* 68 */       PublicKey yourPubKey = myKeyFac.generatePublic(keySpec);
/* 69 */       this.myKeyAgree.doPhase(yourPubKey, true);
/* 70 */       byte[] mySharedSecret = this.myKeyAgree.generateSecret();
/* 71 */       this.K = new BigInteger(1, mySharedSecret);
/* 72 */       this.K_array = this.K.toByteArray();
/* 73 */       this.K_array = mySharedSecret;
/*    */     } 
/* 75 */     return this.K_array;
/*    */   }
/* 77 */   public void setP(byte[] p) { setP(new BigInteger(1, p)); }
/* 78 */   public void setG(byte[] g) { setG(new BigInteger(1, g)); }
/* 79 */   public void setF(byte[] f) { setF(new BigInteger(1, f)); }
/* 80 */   void setP(BigInteger p) { this.p = p; }
/* 81 */   void setG(BigInteger g) { this.g = g; } void setF(BigInteger f) {
/* 82 */     this.f = f;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void checkRange() throws Exception {}
/*    */ 
/*    */ 
/*    */   
/*    */   private void checkRange(BigInteger tmp) throws Exception {
/* 93 */     BigInteger one = BigInteger.ONE;
/* 94 */     BigInteger p_1 = this.p.subtract(one);
/*    */     
/* 96 */     if (one.compareTo(tmp) >= 0 || tmp.compareTo(p_1) >= 0)
/* 97 */       throw new JSchException("invalid DH value"); 
/*    */   }
/*    */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\jce\DH.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */