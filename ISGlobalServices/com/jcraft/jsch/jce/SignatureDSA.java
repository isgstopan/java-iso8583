/*     */ package com.jcraft.jsch.jce;
/*     */ 
/*     */ import com.jcraft.jsch.SignatureDSA;
/*     */ import java.math.BigInteger;
/*     */ import java.security.KeyFactory;
/*     */ import java.security.PrivateKey;
/*     */ import java.security.PublicKey;
/*     */ import java.security.Signature;
/*     */ import java.security.spec.DSAPrivateKeySpec;
/*     */ import java.security.spec.DSAPublicKeySpec;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SignatureDSA
/*     */   implements SignatureDSA
/*     */ {
/*     */   Signature signature;
/*     */   KeyFactory keyFactory;
/*     */   
/*     */   public void init() throws Exception {
/*  42 */     this.signature = Signature.getInstance("SHA1withDSA");
/*  43 */     this.keyFactory = KeyFactory.getInstance("DSA");
/*     */   }
/*     */   public void setPubKey(byte[] y, byte[] p, byte[] q, byte[] g) throws Exception {
/*  46 */     DSAPublicKeySpec dsaPubKeySpec = new DSAPublicKeySpec(new BigInteger(y), new BigInteger(p), new BigInteger(q), new BigInteger(g));
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  51 */     PublicKey pubKey = this.keyFactory.generatePublic(dsaPubKeySpec);
/*  52 */     this.signature.initVerify(pubKey);
/*     */   }
/*     */   public void setPrvKey(byte[] x, byte[] p, byte[] q, byte[] g) throws Exception {
/*  55 */     DSAPrivateKeySpec dsaPrivKeySpec = new DSAPrivateKeySpec(new BigInteger(x), new BigInteger(p), new BigInteger(q), new BigInteger(g));
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  60 */     PrivateKey prvKey = this.keyFactory.generatePrivate(dsaPrivKeySpec);
/*  61 */     this.signature.initSign(prvKey);
/*     */   }
/*     */   public byte[] sign() throws Exception {
/*  64 */     byte[] sig = this.signature.sign();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  74 */     int len = 0;
/*  75 */     int index = 3;
/*  76 */     len = sig[index++] & 0xFF;
/*     */     
/*  78 */     byte[] r = new byte[len];
/*  79 */     System.arraycopy(sig, index, r, 0, r.length);
/*  80 */     index = index + len + 1;
/*  81 */     len = sig[index++] & 0xFF;
/*     */     
/*  83 */     byte[] s = new byte[len];
/*  84 */     System.arraycopy(sig, index, s, 0, s.length);
/*     */     
/*  86 */     byte[] result = new byte[40];
/*     */ 
/*     */ 
/*     */     
/*  90 */     System.arraycopy(r, (r.length > 20) ? 1 : 0, result, (r.length > 20) ? 0 : (20 - r.length), (r.length > 20) ? 20 : r.length);
/*     */ 
/*     */     
/*  93 */     System.arraycopy(s, (s.length > 20) ? 1 : 0, result, (s.length > 20) ? 20 : (40 - s.length), (s.length > 20) ? 20 : s.length);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 100 */     return result;
/*     */   }
/*     */   public void update(byte[] foo) throws Exception {
/* 103 */     this.signature.update(foo);
/*     */   }
/*     */   public boolean verify(byte[] sig) throws Exception {
/* 106 */     int i = 0;
/* 107 */     int j = 0;
/*     */ 
/*     */     
/* 110 */     if (sig[0] == 0 && sig[1] == 0 && sig[2] == 0) {
/* 111 */       j = sig[i++] << 24 & 0xFF000000 | sig[i++] << 16 & 0xFF0000 | sig[i++] << 8 & 0xFF00 | sig[i++] & 0xFF;
/*     */       
/* 113 */       i += j;
/* 114 */       j = sig[i++] << 24 & 0xFF000000 | sig[i++] << 16 & 0xFF0000 | sig[i++] << 8 & 0xFF00 | sig[i++] & 0xFF;
/*     */       
/* 116 */       byte[] arrayOfByte = new byte[j];
/* 117 */       System.arraycopy(sig, i, arrayOfByte, 0, j); sig = arrayOfByte;
/*     */     } 
/*     */ 
/*     */     
/* 121 */     int frst = ((sig[0] & 0x80) != 0) ? 1 : 0;
/* 122 */     int scnd = ((sig[20] & 0x80) != 0) ? 1 : 0;
/*     */ 
/*     */     
/* 125 */     int length = sig.length + 6 + frst + scnd;
/* 126 */     byte[] tmp = new byte[length];
/* 127 */     tmp[0] = 48; tmp[1] = 44;
/* 128 */     tmp[1] = (byte)(tmp[1] + frst); tmp[1] = (byte)(tmp[1] + scnd);
/* 129 */     tmp[2] = 2; tmp[3] = 20;
/* 130 */     tmp[3] = (byte)(tmp[3] + frst);
/* 131 */     System.arraycopy(sig, 0, tmp, 4 + frst, 20);
/* 132 */     tmp[4 + tmp[3]] = 2; tmp[5 + tmp[3]] = 20;
/* 133 */     tmp[5 + tmp[3]] = (byte)(tmp[5 + tmp[3]] + scnd);
/* 134 */     System.arraycopy(sig, 20, tmp, 6 + tmp[3] + scnd, 20);
/* 135 */     sig = tmp;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 145 */     return this.signature.verify(sig);
/*     */   }
/*     */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\jce\SignatureDSA.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */