/*     */ package com.jcraft.jsch;
/*     */ 
/*     */ import java.math.BigInteger;
/*     */ import java.util.Vector;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class KeyPairPKCS8
/*     */   extends KeyPair
/*     */ {
/*  36 */   private static final byte[] rsaEncryption = new byte[] { 42, -122, 72, -122, -9, 13, 1, 1, 1 };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  41 */   private static final byte[] dsaEncryption = new byte[] { 42, -122, 72, -50, 56, 4, 1 };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  46 */   private static final byte[] pbes2 = new byte[] { 42, -122, 72, -122, -9, 13, 1, 5, 13 };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  51 */   private static final byte[] pbkdf2 = new byte[] { 42, -122, 72, -122, -9, 13, 1, 5, 12 };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  56 */   private static final byte[] aes128cbc = new byte[] { 96, -122, 72, 1, 101, 3, 4, 1, 2 };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  61 */   private static final byte[] aes192cbc = new byte[] { 96, -122, 72, 1, 101, 3, 4, 1, 22 };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  66 */   private static final byte[] aes256cbc = new byte[] { 96, -122, 72, 1, 101, 3, 4, 1, 42 };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  71 */   private static final byte[] pbeWithMD5AndDESCBC = new byte[] { 42, -122, 72, -122, -9, 13, 1, 5, 3 };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  76 */   private KeyPair kpair = null;
/*     */   
/*     */   public KeyPairPKCS8(JSch jsch) {
/*  79 */     super(jsch);
/*     */   }
/*     */ 
/*     */   
/*     */   void generate(int key_size) throws JSchException {}
/*     */   
/*  85 */   private static final byte[] begin = Util.str2byte("-----BEGIN DSA PRIVATE KEY-----");
/*  86 */   private static final byte[] end = Util.str2byte("-----END DSA PRIVATE KEY-----");
/*     */   
/*  88 */   byte[] getBegin() { return begin; } byte[] getEnd() {
/*  89 */     return end;
/*     */   }
/*     */   byte[] getPrivateKey() {
/*  92 */     return null;
/*     */   }
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
/*     */   boolean parse(byte[] plain) {
/*     */     try {
/* 112 */       Vector values = new Vector();
/*     */       
/* 114 */       KeyPair.ASN1[] contents = null;
/* 115 */       KeyPair.ASN1 asn1 = new KeyPair.ASN1(this, plain);
/* 116 */       contents = asn1.getContents();
/*     */       
/* 118 */       KeyPair.ASN1 privateKeyAlgorithm = contents[1];
/* 119 */       KeyPair.ASN1 privateKey = contents[2];
/*     */       
/* 121 */       contents = privateKeyAlgorithm.getContents();
/* 122 */       byte[] privateKeyAlgorithmID = contents[0].getContent();
/* 123 */       contents = contents[1].getContents();
/* 124 */       if (contents.length > 0) {
/* 125 */         for (int i = 0; i < contents.length; i++) {
/* 126 */           values.addElement(contents[i].getContent());
/*     */         }
/*     */       }
/*     */       
/* 130 */       byte[] _data = privateKey.getContent();
/*     */       
/* 132 */       KeyPair _kpair = null;
/* 133 */       if (Util.array_equals(privateKeyAlgorithmID, rsaEncryption)) {
/* 134 */         _kpair = new KeyPairRSA(this.jsch);
/* 135 */         _kpair.copy(this);
/* 136 */         if (_kpair.parse(_data)) {
/* 137 */           this.kpair = _kpair;
/*     */         }
/*     */       }
/* 140 */       else if (Util.array_equals(privateKeyAlgorithmID, dsaEncryption)) {
/* 141 */         asn1 = new KeyPair.ASN1(this, _data);
/* 142 */         if (values.size() == 0) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 151 */           contents = asn1.getContents();
/* 152 */           byte[] bar = contents[1].getContent();
/* 153 */           contents = contents[0].getContents();
/* 154 */           for (int i = 0; i < contents.length; i++) {
/* 155 */             values.addElement(contents[i].getContent());
/*     */           }
/* 157 */           values.addElement(bar);
/*     */         
/*     */         }
/*     */         else {
/*     */ 
/*     */           
/* 163 */           values.addElement(asn1.getContent());
/*     */         } 
/*     */         
/* 166 */         byte[] P_array = values.elementAt(0);
/* 167 */         byte[] Q_array = values.elementAt(1);
/* 168 */         byte[] G_array = values.elementAt(2);
/* 169 */         byte[] prv_array = values.elementAt(3);
/*     */         
/* 171 */         byte[] pub_array = (new BigInteger(G_array)).modPow(new BigInteger(prv_array), new BigInteger(P_array)).toByteArray();
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 176 */         KeyPairDSA _key = new KeyPairDSA(this.jsch, P_array, Q_array, G_array, pub_array, prv_array);
/*     */ 
/*     */         
/* 179 */         plain = _key.getPrivateKey();
/*     */         
/* 181 */         _kpair = new KeyPairDSA(this.jsch);
/* 182 */         _kpair.copy(this);
/* 183 */         if (_kpair.parse(plain)) {
/* 184 */           this.kpair = _kpair;
/*     */         }
/*     */       }
/*     */     
/* 188 */     } catch (ASN1Exception e) {
/* 189 */       return false;
/*     */     }
/* 191 */     catch (Exception e) {
/*     */       
/* 193 */       return false;
/*     */     } 
/* 195 */     return (this.kpair != null);
/*     */   }
/*     */   
/*     */   public byte[] getPublicKeyBlob() {
/* 199 */     return this.kpair.getPublicKeyBlob();
/*     */   }
/*     */   
/* 202 */   byte[] getKeyTypeName() { return this.kpair.getKeyTypeName(); } public int getKeyType() {
/* 203 */     return this.kpair.getKeyType();
/*     */   }
/*     */   public int getKeySize() {
/* 206 */     return this.kpair.getKeySize();
/*     */   }
/*     */   
/*     */   public byte[] getSignature(byte[] data) {
/* 210 */     return this.kpair.getSignature(data);
/*     */   }
/*     */   
/*     */   public Signature getVerifier() {
/* 214 */     return this.kpair.getVerifier();
/*     */   }
/*     */   
/*     */   public byte[] forSSHAgent() throws JSchException {
/* 218 */     return this.kpair.forSSHAgent();
/*     */   }
/*     */   
/*     */   public boolean decrypt(byte[] _passphrase) {
/* 222 */     if (!isEncrypted()) {
/* 223 */       return true;
/*     */     }
/* 225 */     if (_passphrase == null) {
/* 226 */       return !isEncrypted();
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
/*     */     try {
/* 257 */       KeyPair.ASN1[] contents = null;
/* 258 */       KeyPair.ASN1 asn1 = new KeyPair.ASN1(this, this.data);
/*     */       
/* 260 */       contents = asn1.getContents();
/*     */       
/* 262 */       byte[] _data = contents[1].getContent();
/*     */       
/* 264 */       KeyPair.ASN1 pbes = contents[0];
/* 265 */       contents = pbes.getContents();
/* 266 */       byte[] pbesid = contents[0].getContent();
/* 267 */       KeyPair.ASN1 pbesparam = contents[1];
/*     */       
/* 269 */       byte[] salt = null;
/* 270 */       int iterations = 0;
/* 271 */       byte[] iv = null;
/* 272 */       byte[] encryptfuncid = null;
/*     */       
/* 274 */       if (Util.array_equals(pbesid, pbes2)) {
/* 275 */         contents = pbesparam.getContents();
/* 276 */         KeyPair.ASN1 pbkdf = contents[0];
/* 277 */         KeyPair.ASN1 encryptfunc = contents[1];
/* 278 */         contents = pbkdf.getContents();
/* 279 */         byte[] pbkdfid = contents[0].getContent();
/* 280 */         KeyPair.ASN1 pbkdffunc = contents[1];
/* 281 */         contents = pbkdffunc.getContents();
/* 282 */         salt = contents[0].getContent();
/* 283 */         iterations = Integer.parseInt((new BigInteger(contents[1].getContent())).toString());
/*     */ 
/*     */         
/* 286 */         contents = encryptfunc.getContents();
/* 287 */         encryptfuncid = contents[0].getContent();
/* 288 */         iv = contents[1].getContent();
/*     */       } else {
/* 290 */         if (Util.array_equals(pbesid, pbeWithMD5AndDESCBC))
/*     */         {
/* 292 */           return false;
/*     */         }
/*     */         
/* 295 */         return false;
/*     */       } 
/*     */       
/* 298 */       Cipher cipher = getCipher(encryptfuncid);
/* 299 */       if (cipher == null) return false;
/*     */       
/* 301 */       byte[] key = null;
/*     */       try {
/* 303 */         Class c = Class.forName(JSch.getConfig("pbkdf"));
/* 304 */         PBKDF tmp = (PBKDF)c.newInstance();
/* 305 */         key = tmp.getKey(_passphrase, salt, iterations, cipher.getBlockSize());
/*     */       }
/* 307 */       catch (Exception ee) {}
/*     */ 
/*     */       
/* 310 */       if (key == null) {
/* 311 */         return false;
/*     */       }
/*     */       
/* 314 */       cipher.init(1, key, iv);
/* 315 */       Util.bzero(key);
/* 316 */       byte[] plain = new byte[_data.length];
/* 317 */       cipher.update(_data, 0, _data.length, plain, 0);
/* 318 */       if (parse(plain)) {
/* 319 */         this.encrypted = false;
/* 320 */         return true;
/*     */       }
/*     */     
/* 323 */     } catch (ASN1Exception e) {
/*     */ 
/*     */     
/* 326 */     } catch (Exception e) {}
/*     */ 
/*     */ 
/*     */     
/* 330 */     return false;
/*     */   }
/*     */   
/*     */   Cipher getCipher(byte[] id) {
/* 334 */     Cipher cipher = null;
/* 335 */     String name = null;
/*     */     try {
/* 337 */       if (Util.array_equals(id, aes128cbc)) {
/* 338 */         name = "aes128-cbc";
/*     */       }
/* 340 */       else if (Util.array_equals(id, aes192cbc)) {
/* 341 */         name = "aes192-cbc";
/*     */       }
/* 343 */       else if (Util.array_equals(id, aes256cbc)) {
/* 344 */         name = "aes256-cbc";
/*     */       } 
/* 346 */       Class c = Class.forName(JSch.getConfig(name));
/* 347 */       cipher = (Cipher)c.newInstance();
/*     */     }
/* 349 */     catch (Exception e) {
/* 350 */       if (JSch.getLogger().isEnabled(4)) {
/* 351 */         String message = "";
/* 352 */         if (name == null) {
/* 353 */           message = "unknown oid: " + Util.toHex(id);
/*     */         } else {
/*     */           
/* 356 */           message = "function " + name + " is not supported";
/*     */         } 
/* 358 */         JSch.getLogger().log(4, "PKCS8: " + message);
/*     */       } 
/*     */     } 
/* 361 */     return cipher;
/*     */   }
/*     */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\KeyPairPKCS8.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */