/*     */ package com.jcraft.jsch.jce;
/*     */ 
/*     */ import com.jcraft.jsch.ECDH;
/*     */ import java.math.BigInteger;
/*     */ import java.security.KeyFactory;
/*     */ import java.security.PublicKey;
/*     */ import java.security.interfaces.ECPublicKey;
/*     */ import java.security.spec.ECFieldFp;
/*     */ import java.security.spec.ECParameterSpec;
/*     */ import java.security.spec.ECPoint;
/*     */ import java.security.spec.ECPublicKeySpec;
/*     */ import java.security.spec.EllipticCurve;
/*     */ import javax.crypto.KeyAgreement;
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
/*     */ public class ECDHN
/*     */   implements ECDH
/*     */ {
/*     */   byte[] Q_array;
/*     */   ECPublicKey publicKey;
/*     */   private KeyAgreement myKeyAgree;
/*     */   
/*     */   public void init(int size) throws Exception {
/*  44 */     this.myKeyAgree = KeyAgreement.getInstance("ECDH");
/*  45 */     KeyPairGenECDSA kpair = new KeyPairGenECDSA();
/*  46 */     kpair.init(size);
/*  47 */     this.publicKey = kpair.getPublicKey();
/*  48 */     byte[] r = kpair.getR();
/*  49 */     byte[] s = kpair.getS();
/*  50 */     this.Q_array = toPoint(r, s);
/*  51 */     this.myKeyAgree.init(kpair.getPrivateKey());
/*     */   }
/*     */   
/*     */   public byte[] getQ() throws Exception {
/*  55 */     return this.Q_array;
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] getSecret(byte[] r, byte[] s) throws Exception {
/*  60 */     KeyFactory kf = KeyFactory.getInstance("EC");
/*  61 */     ECPoint w = new ECPoint(new BigInteger(1, r), new BigInteger(1, s));
/*  62 */     ECPublicKeySpec spec = new ECPublicKeySpec(w, this.publicKey.getParams());
/*  63 */     PublicKey theirPublicKey = kf.generatePublic(spec);
/*  64 */     this.myKeyAgree.doPhase(theirPublicKey, true);
/*  65 */     return this.myKeyAgree.generateSecret();
/*     */   }
/*     */   
/*  68 */   private static BigInteger two = BigInteger.ONE.add(BigInteger.ONE);
/*  69 */   private static BigInteger three = two.add(BigInteger.ONE);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean validate(byte[] r, byte[] s) throws Exception {
/*  75 */     BigInteger x = new BigInteger(1, r);
/*  76 */     BigInteger y = new BigInteger(1, s);
/*     */ 
/*     */ 
/*     */     
/*  80 */     ECPoint w = new ECPoint(x, y);
/*  81 */     if (w.equals(ECPoint.POINT_INFINITY)) {
/*  82 */       return false;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  91 */     ECParameterSpec params = this.publicKey.getParams();
/*  92 */     EllipticCurve curve = params.getCurve();
/*  93 */     BigInteger p = ((ECFieldFp)curve.getField()).getP();
/*     */ 
/*     */     
/*  96 */     BigInteger p_sub1 = p.subtract(BigInteger.ONE);
/*  97 */     if (x.compareTo(p_sub1) > 0 || y.compareTo(p_sub1) > 0) {
/*  98 */       return false;
/*     */     }
/*     */ 
/*     */     
/* 102 */     BigInteger tmp = x.multiply(curve.getA()).add(curve.getB()).add(x.modPow(three, p)).mod(p);
/*     */ 
/*     */ 
/*     */     
/* 106 */     BigInteger y_2 = y.modPow(two, p);
/* 107 */     if (!y_2.equals(tmp)) {
/* 108 */       return false;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 119 */     return true;
/*     */   }
/*     */   
/*     */   private byte[] toPoint(byte[] r_array, byte[] s_array) {
/* 123 */     byte[] tmp = new byte[1 + r_array.length + s_array.length];
/* 124 */     tmp[0] = 4;
/* 125 */     System.arraycopy(r_array, 0, tmp, 1, r_array.length);
/* 126 */     System.arraycopy(s_array, 0, tmp, 1 + r_array.length, s_array.length);
/* 127 */     return tmp;
/*     */   }
/*     */   private byte[] insert0(byte[] buf) {
/* 130 */     if ((buf[0] & 0x80) == 0) return buf; 
/* 131 */     byte[] tmp = new byte[buf.length + 1];
/* 132 */     System.arraycopy(buf, 0, tmp, 1, buf.length);
/* 133 */     bzero(buf);
/* 134 */     return tmp;
/*     */   }
/*     */   private byte[] chop0(byte[] buf) {
/* 137 */     if (buf[0] != 0) return buf; 
/* 138 */     byte[] tmp = new byte[buf.length - 1];
/* 139 */     System.arraycopy(buf, 1, tmp, 0, tmp.length);
/* 140 */     bzero(buf);
/* 141 */     return tmp;
/*     */   }
/*     */   private void bzero(byte[] buf) {
/* 144 */     for (int i = 0; i < buf.length; ) { buf[i] = 0; i++; }
/*     */   
/*     */   }
/*     */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\jce\ECDHN.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */