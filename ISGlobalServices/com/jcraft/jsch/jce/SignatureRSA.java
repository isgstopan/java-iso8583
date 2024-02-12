/*    */ package com.jcraft.jsch.jce;
/*    */ 
/*    */ import com.jcraft.jsch.SignatureRSA;
/*    */ import java.math.BigInteger;
/*    */ import java.security.KeyFactory;
/*    */ import java.security.PrivateKey;
/*    */ import java.security.PublicKey;
/*    */ import java.security.Signature;
/*    */ import java.security.spec.RSAPrivateKeySpec;
/*    */ import java.security.spec.RSAPublicKeySpec;
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
/*    */ public class SignatureRSA
/*    */   implements SignatureRSA
/*    */ {
/*    */   Signature signature;
/*    */   KeyFactory keyFactory;
/*    */   
/*    */   public void init() throws Exception {
/* 42 */     this.signature = Signature.getInstance("SHA1withRSA");
/* 43 */     this.keyFactory = KeyFactory.getInstance("RSA");
/*    */   }
/*    */   public void setPubKey(byte[] e, byte[] n) throws Exception {
/* 46 */     RSAPublicKeySpec rsaPubKeySpec = new RSAPublicKeySpec(new BigInteger(n), new BigInteger(e));
/*    */ 
/*    */     
/* 49 */     PublicKey pubKey = this.keyFactory.generatePublic(rsaPubKeySpec);
/* 50 */     this.signature.initVerify(pubKey);
/*    */   }
/*    */   public void setPrvKey(byte[] d, byte[] n) throws Exception {
/* 53 */     RSAPrivateKeySpec rsaPrivKeySpec = new RSAPrivateKeySpec(new BigInteger(n), new BigInteger(d));
/*    */ 
/*    */     
/* 56 */     PrivateKey prvKey = this.keyFactory.generatePrivate(rsaPrivKeySpec);
/* 57 */     this.signature.initSign(prvKey);
/*    */   }
/*    */   public byte[] sign() throws Exception {
/* 60 */     byte[] sig = this.signature.sign();
/* 61 */     return sig;
/*    */   }
/*    */   public void update(byte[] foo) throws Exception {
/* 64 */     this.signature.update(foo);
/*    */   }
/*    */   public boolean verify(byte[] sig) throws Exception {
/* 67 */     int i = 0;
/* 68 */     int j = 0;
/*    */ 
/*    */     
/* 71 */     if (sig[0] == 0 && sig[1] == 0 && sig[2] == 0) {
/* 72 */       j = sig[i++] << 24 & 0xFF000000 | sig[i++] << 16 & 0xFF0000 | sig[i++] << 8 & 0xFF00 | sig[i++] & 0xFF;
/*    */       
/* 74 */       i += j;
/* 75 */       j = sig[i++] << 24 & 0xFF000000 | sig[i++] << 16 & 0xFF0000 | sig[i++] << 8 & 0xFF00 | sig[i++] & 0xFF;
/*    */       
/* 77 */       byte[] tmp = new byte[j];
/* 78 */       System.arraycopy(sig, i, tmp, 0, j); sig = tmp;
/*    */     } 
/*    */     
/* 81 */     return this.signature.verify(sig);
/*    */   }
/*    */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\jce\SignatureRSA.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */