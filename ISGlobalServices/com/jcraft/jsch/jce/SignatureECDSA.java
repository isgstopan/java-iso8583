/*     */ package com.jcraft.jsch.jce;
/*     */ 
/*     */ import com.jcraft.jsch.Buffer;
/*     */ import com.jcraft.jsch.SignatureECDSA;
/*     */ import java.math.BigInteger;
/*     */ import java.security.AlgorithmParameters;
/*     */ import java.security.KeyFactory;
/*     */ import java.security.PrivateKey;
/*     */ import java.security.PublicKey;
/*     */ import java.security.Signature;
/*     */ import java.security.spec.ECGenParameterSpec;
/*     */ import java.security.spec.ECParameterSpec;
/*     */ import java.security.spec.ECPoint;
/*     */ import java.security.spec.ECPrivateKeySpec;
/*     */ import java.security.spec.ECPublicKeySpec;
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
/*     */ public class SignatureECDSA
/*     */   implements SignatureECDSA
/*     */ {
/*     */   Signature signature;
/*     */   KeyFactory keyFactory;
/*     */   
/*     */   public void init() throws Exception {
/*  43 */     this.signature = Signature.getInstance("SHA256withECDSA");
/*  44 */     this.keyFactory = KeyFactory.getInstance("EC");
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPubKey(byte[] r, byte[] s) throws Exception {
/*  50 */     r = insert0(r);
/*  51 */     s = insert0(s);
/*     */     
/*  53 */     String name = "secp256r1";
/*  54 */     if (r.length >= 64) { name = "secp521r1"; }
/*  55 */     else if (r.length >= 48) { name = "secp384r1"; }
/*     */     
/*  57 */     AlgorithmParameters param = AlgorithmParameters.getInstance("EC");
/*  58 */     param.init(new ECGenParameterSpec(name));
/*  59 */     ECParameterSpec ecparam = param.<ECParameterSpec>getParameterSpec(ECParameterSpec.class);
/*     */     
/*  61 */     ECPoint w = new ECPoint(new BigInteger(1, r), new BigInteger(1, s));
/*  62 */     PublicKey pubKey = this.keyFactory.generatePublic(new ECPublicKeySpec(w, ecparam));
/*     */     
/*  64 */     this.signature.initVerify(pubKey);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPrvKey(byte[] d) throws Exception {
/*  70 */     d = insert0(d);
/*     */     
/*  72 */     String name = "secp256r1";
/*  73 */     if (d.length >= 64) { name = "secp521r1"; }
/*  74 */     else if (d.length >= 48) { name = "secp384r1"; }
/*     */     
/*  76 */     AlgorithmParameters param = AlgorithmParameters.getInstance("EC");
/*  77 */     param.init(new ECGenParameterSpec(name));
/*  78 */     ECParameterSpec ecparam = param.<ECParameterSpec>getParameterSpec(ECParameterSpec.class);
/*     */     
/*  80 */     BigInteger _d = new BigInteger(1, d);
/*  81 */     PrivateKey prvKey = this.keyFactory.generatePrivate(new ECPrivateKeySpec(_d, ecparam));
/*     */     
/*  83 */     this.signature.initSign(prvKey);
/*     */   }
/*     */   public byte[] sign() throws Exception {
/*  86 */     byte[] sig = this.signature.sign();
/*     */ 
/*     */ 
/*     */     
/*  90 */     if (sig[0] == 48 && (sig[1] + 2 == sig.length || ((sig[1] & 0x80) != 0 && (sig[2] & 0xFF) + 3 == sig.length))) {
/*     */ 
/*     */ 
/*     */       
/*  94 */       int index = 3;
/*  95 */       if ((sig[1] & 0x80) != 0 && (sig[2] & 0xFF) + 3 == sig.length) {
/*  96 */         index = 4;
/*     */       }
/*  98 */       byte[] r = new byte[sig[index]];
/*  99 */       byte[] s = new byte[sig[index + 2 + sig[index]]];
/* 100 */       System.arraycopy(sig, index + 1, r, 0, r.length);
/* 101 */       System.arraycopy(sig, index + 3 + sig[index], s, 0, s.length);
/*     */       
/* 103 */       r = chop0(r);
/* 104 */       s = chop0(s);
/*     */       
/* 106 */       Buffer buf = new Buffer();
/* 107 */       buf.putMPInt(r);
/* 108 */       buf.putMPInt(s);
/*     */       
/* 110 */       sig = new byte[buf.getLength()];
/* 111 */       buf.setOffSet(0);
/* 112 */       buf.getByte(sig);
/*     */     } 
/*     */     
/* 115 */     return sig;
/*     */   }
/*     */   public void update(byte[] foo) throws Exception {
/* 118 */     this.signature.update(foo);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean verify(byte[] sig) throws Exception {
/* 124 */     if (sig[0] != 48 || (sig[1] + 2 != sig.length && ((sig[1] & 0x80) == 0 || (sig[2] & 0xFF) + 3 != sig.length))) {
/*     */ 
/*     */       
/* 127 */       Buffer b = new Buffer(sig);
/*     */       
/* 129 */       b.getString();
/* 130 */       b.getInt();
/*     */       
/* 132 */       byte[] r = b.getMPInt();
/* 133 */       byte[] s = b.getMPInt();
/*     */       
/* 135 */       r = insert0(r);
/* 136 */       s = insert0(s);
/*     */       
/* 138 */       byte[] asn1 = null;
/* 139 */       if (r.length < 64) {
/* 140 */         asn1 = new byte[6 + r.length + s.length];
/* 141 */         asn1[0] = 48;
/* 142 */         asn1[1] = (byte)(4 + r.length + s.length);
/* 143 */         asn1[2] = 2;
/* 144 */         asn1[3] = (byte)r.length;
/* 145 */         System.arraycopy(r, 0, asn1, 4, r.length);
/* 146 */         asn1[r.length + 4] = 2;
/* 147 */         asn1[r.length + 5] = (byte)s.length;
/* 148 */         System.arraycopy(s, 0, asn1, 6 + r.length, s.length);
/*     */       } else {
/*     */         
/* 151 */         asn1 = new byte[6 + r.length + s.length + 1];
/* 152 */         asn1[0] = 48;
/* 153 */         asn1[1] = -127;
/* 154 */         asn1[2] = (byte)(4 + r.length + s.length);
/* 155 */         asn1[3] = 2;
/* 156 */         asn1[4] = (byte)r.length;
/* 157 */         System.arraycopy(r, 0, asn1, 5, r.length);
/* 158 */         asn1[r.length + 5] = 2;
/* 159 */         asn1[r.length + 6] = (byte)s.length;
/* 160 */         System.arraycopy(s, 0, asn1, 7 + r.length, s.length);
/*     */       } 
/* 162 */       sig = asn1;
/*     */     } 
/*     */     
/* 165 */     return this.signature.verify(sig);
/*     */   }
/*     */   
/*     */   private byte[] insert0(byte[] buf) {
/* 169 */     if ((buf[0] & 0x80) == 0) return buf; 
/* 170 */     byte[] tmp = new byte[buf.length + 1];
/* 171 */     System.arraycopy(buf, 0, tmp, 1, buf.length);
/* 172 */     bzero(buf);
/* 173 */     return tmp;
/*     */   }
/*     */   private byte[] chop0(byte[] buf) {
/* 176 */     if (buf[0] != 0) return buf; 
/* 177 */     byte[] tmp = new byte[buf.length - 1];
/* 178 */     System.arraycopy(buf, 1, tmp, 0, tmp.length);
/* 179 */     bzero(buf);
/* 180 */     return tmp;
/*     */   }
/*     */   
/*     */   private void bzero(byte[] buf) {
/* 184 */     for (int i = 0; i < buf.length; ) { buf[i] = 0; i++; }
/*     */   
/*     */   }
/*     */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\jce\SignatureECDSA.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */