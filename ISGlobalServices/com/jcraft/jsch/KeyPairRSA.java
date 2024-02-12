/*     */ package com.jcraft.jsch;
/*     */ 
/*     */ import java.math.BigInteger;
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
/*     */ 
/*     */ 
/*     */ public class KeyPairRSA
/*     */   extends KeyPair
/*     */ {
/*     */   private byte[] n_array;
/*     */   private byte[] pub_array;
/*     */   private byte[] prv_array;
/*     */   private byte[] p_array;
/*     */   private byte[] q_array;
/*     */   private byte[] ep_array;
/*     */   private byte[] eq_array;
/*     */   private byte[] c_array;
/*  45 */   private int key_size = 1024;
/*     */   
/*     */   public KeyPairRSA(JSch jsch) {
/*  48 */     this(jsch, (byte[])null, (byte[])null, (byte[])null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public KeyPairRSA(JSch jsch, byte[] n_array, byte[] pub_array, byte[] prv_array) {
/*  55 */     super(jsch);
/*  56 */     this.n_array = n_array;
/*  57 */     this.pub_array = pub_array;
/*  58 */     this.prv_array = prv_array;
/*  59 */     if (n_array != null) {
/*  60 */       this.key_size = (new BigInteger(n_array)).bitLength();
/*     */     }
/*     */   }
/*     */   
/*     */   void generate(int key_size) throws JSchException {
/*  65 */     this.key_size = key_size;
/*     */     try {
/*  67 */       Class c = Class.forName(JSch.getConfig("keypairgen.rsa"));
/*  68 */       KeyPairGenRSA keypairgen = (KeyPairGenRSA)c.newInstance();
/*  69 */       keypairgen.init(key_size);
/*  70 */       this.pub_array = keypairgen.getE();
/*  71 */       this.prv_array = keypairgen.getD();
/*  72 */       this.n_array = keypairgen.getN();
/*     */       
/*  74 */       this.p_array = keypairgen.getP();
/*  75 */       this.q_array = keypairgen.getQ();
/*  76 */       this.ep_array = keypairgen.getEP();
/*  77 */       this.eq_array = keypairgen.getEQ();
/*  78 */       this.c_array = keypairgen.getC();
/*     */       
/*  80 */       keypairgen = null;
/*     */     }
/*  82 */     catch (Exception e) {
/*     */       
/*  84 */       if (e instanceof Throwable)
/*  85 */         throw new JSchException(e.toString(), e); 
/*  86 */       throw new JSchException(e.toString());
/*     */     } 
/*     */   }
/*     */   
/*  90 */   private static final byte[] begin = Util.str2byte("-----BEGIN RSA PRIVATE KEY-----");
/*  91 */   private static final byte[] end = Util.str2byte("-----END RSA PRIVATE KEY-----");
/*     */   
/*  93 */   byte[] getBegin() { return begin; } byte[] getEnd() {
/*  94 */     return end;
/*     */   }
/*     */   byte[] getPrivateKey() {
/*  97 */     int content = 1 + countLength(1) + 1 + 1 + countLength(this.n_array.length) + this.n_array.length + 1 + countLength(this.pub_array.length) + this.pub_array.length + 1 + countLength(this.prv_array.length) + this.prv_array.length + 1 + countLength(this.p_array.length) + this.p_array.length + 1 + countLength(this.q_array.length) + this.q_array.length + 1 + countLength(this.ep_array.length) + this.ep_array.length + 1 + countLength(this.eq_array.length) + this.eq_array.length + 1 + countLength(this.c_array.length) + this.c_array.length;
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
/* 108 */     int total = 1 + countLength(content) + content;
/*     */ 
/*     */     
/* 111 */     byte[] plain = new byte[total];
/* 112 */     int index = 0;
/* 113 */     index = writeSEQUENCE(plain, index, content);
/* 114 */     index = writeINTEGER(plain, index, new byte[1]);
/* 115 */     index = writeINTEGER(plain, index, this.n_array);
/* 116 */     index = writeINTEGER(plain, index, this.pub_array);
/* 117 */     index = writeINTEGER(plain, index, this.prv_array);
/* 118 */     index = writeINTEGER(plain, index, this.p_array);
/* 119 */     index = writeINTEGER(plain, index, this.q_array);
/* 120 */     index = writeINTEGER(plain, index, this.ep_array);
/* 121 */     index = writeINTEGER(plain, index, this.eq_array);
/* 122 */     index = writeINTEGER(plain, index, this.c_array);
/* 123 */     return plain;
/*     */   }
/*     */ 
/*     */   
/*     */   boolean parse(byte[] plain) {
/*     */     try {
/* 129 */       int index = 0;
/* 130 */       int length = 0;
/*     */       
/* 132 */       if (this.vendor == 2) {
/* 133 */         Buffer buf = new Buffer(plain);
/* 134 */         buf.skip(plain.length);
/*     */         
/*     */         try {
/* 137 */           byte[][] tmp = buf.getBytes(4, "");
/* 138 */           this.prv_array = tmp[0];
/* 139 */           this.p_array = tmp[1];
/* 140 */           this.q_array = tmp[2];
/* 141 */           this.c_array = tmp[3];
/*     */         }
/* 143 */         catch (JSchException e) {
/* 144 */           return false;
/*     */         } 
/*     */         
/* 147 */         getEPArray();
/* 148 */         getEQArray();
/*     */         
/* 150 */         return true;
/*     */       } 
/*     */       
/* 153 */       if (this.vendor == 1) {
/* 154 */         if (plain[index] != 48) {
/* 155 */           Buffer buf = new Buffer(plain);
/* 156 */           this.pub_array = buf.getMPIntBits();
/* 157 */           this.prv_array = buf.getMPIntBits();
/* 158 */           this.n_array = buf.getMPIntBits();
/* 159 */           byte[] u_array = buf.getMPIntBits();
/* 160 */           this.p_array = buf.getMPIntBits();
/* 161 */           this.q_array = buf.getMPIntBits();
/* 162 */           if (this.n_array != null) {
/* 163 */             this.key_size = (new BigInteger(this.n_array)).bitLength();
/*     */           }
/*     */           
/* 166 */           getEPArray();
/* 167 */           getEQArray();
/* 168 */           getCArray();
/*     */           
/* 170 */           return true;
/*     */         } 
/* 172 */         return false;
/*     */       } 
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
/* 191 */       index++;
/* 192 */       length = plain[index++] & 0xFF;
/* 193 */       if ((length & 0x80) != 0) {
/* 194 */         int foo = length & 0x7F; length = 0;
/* 195 */         for (; foo-- > 0; length = (length << 8) + (plain[index++] & 0xFF));
/*     */       } 
/*     */       
/* 198 */       if (plain[index] != 2) return false; 
/* 199 */       index++;
/* 200 */       length = plain[index++] & 0xFF;
/* 201 */       if ((length & 0x80) != 0) {
/* 202 */         int foo = length & 0x7F; length = 0;
/* 203 */         for (; foo-- > 0; length = (length << 8) + (plain[index++] & 0xFF));
/*     */       } 
/* 205 */       index += length;
/*     */       
/* 207 */       index++;
/* 208 */       length = plain[index++] & 0xFF;
/* 209 */       if ((length & 0x80) != 0) {
/* 210 */         int foo = length & 0x7F; length = 0;
/* 211 */         for (; foo-- > 0; length = (length << 8) + (plain[index++] & 0xFF));
/*     */       } 
/* 213 */       this.n_array = new byte[length];
/* 214 */       System.arraycopy(plain, index, this.n_array, 0, length);
/* 215 */       index += length;
/*     */       
/* 217 */       index++;
/* 218 */       length = plain[index++] & 0xFF;
/* 219 */       if ((length & 0x80) != 0) {
/* 220 */         int foo = length & 0x7F; length = 0;
/* 221 */         for (; foo-- > 0; length = (length << 8) + (plain[index++] & 0xFF));
/*     */       } 
/* 223 */       this.pub_array = new byte[length];
/* 224 */       System.arraycopy(plain, index, this.pub_array, 0, length);
/* 225 */       index += length;
/*     */       
/* 227 */       index++;
/* 228 */       length = plain[index++] & 0xFF;
/* 229 */       if ((length & 0x80) != 0) {
/* 230 */         int foo = length & 0x7F; length = 0;
/* 231 */         for (; foo-- > 0; length = (length << 8) + (plain[index++] & 0xFF));
/*     */       } 
/* 233 */       this.prv_array = new byte[length];
/* 234 */       System.arraycopy(plain, index, this.prv_array, 0, length);
/* 235 */       index += length;
/*     */       
/* 237 */       index++;
/* 238 */       length = plain[index++] & 0xFF;
/* 239 */       if ((length & 0x80) != 0) {
/* 240 */         int foo = length & 0x7F; length = 0;
/* 241 */         for (; foo-- > 0; length = (length << 8) + (plain[index++] & 0xFF));
/*     */       } 
/* 243 */       this.p_array = new byte[length];
/* 244 */       System.arraycopy(plain, index, this.p_array, 0, length);
/* 245 */       index += length;
/*     */       
/* 247 */       index++;
/* 248 */       length = plain[index++] & 0xFF;
/* 249 */       if ((length & 0x80) != 0) {
/* 250 */         int foo = length & 0x7F; length = 0;
/* 251 */         for (; foo-- > 0; length = (length << 8) + (plain[index++] & 0xFF));
/*     */       } 
/* 253 */       this.q_array = new byte[length];
/* 254 */       System.arraycopy(plain, index, this.q_array, 0, length);
/* 255 */       index += length;
/*     */       
/* 257 */       index++;
/* 258 */       length = plain[index++] & 0xFF;
/* 259 */       if ((length & 0x80) != 0) {
/* 260 */         int foo = length & 0x7F; length = 0;
/* 261 */         for (; foo-- > 0; length = (length << 8) + (plain[index++] & 0xFF));
/*     */       } 
/* 263 */       this.ep_array = new byte[length];
/* 264 */       System.arraycopy(plain, index, this.ep_array, 0, length);
/* 265 */       index += length;
/*     */       
/* 267 */       index++;
/* 268 */       length = plain[index++] & 0xFF;
/* 269 */       if ((length & 0x80) != 0) {
/* 270 */         int foo = length & 0x7F; length = 0;
/* 271 */         for (; foo-- > 0; length = (length << 8) + (plain[index++] & 0xFF));
/*     */       } 
/* 273 */       this.eq_array = new byte[length];
/* 274 */       System.arraycopy(plain, index, this.eq_array, 0, length);
/* 275 */       index += length;
/*     */       
/* 277 */       index++;
/* 278 */       length = plain[index++] & 0xFF;
/* 279 */       if ((length & 0x80) != 0) {
/* 280 */         int foo = length & 0x7F; length = 0;
/* 281 */         for (; foo-- > 0; length = (length << 8) + (plain[index++] & 0xFF));
/*     */       } 
/* 283 */       this.c_array = new byte[length];
/* 284 */       System.arraycopy(plain, index, this.c_array, 0, length);
/* 285 */       index += length;
/*     */       
/* 287 */       if (this.n_array != null) {
/* 288 */         this.key_size = (new BigInteger(this.n_array)).bitLength();
/*     */       
/*     */       }
/*     */     }
/* 292 */     catch (Exception e) {
/*     */       
/* 294 */       return false;
/*     */     } 
/* 296 */     return true;
/*     */   }
/*     */   
/*     */   public byte[] getPublicKeyBlob() {
/* 300 */     byte[] foo = super.getPublicKeyBlob();
/* 301 */     if (foo != null) return foo;
/*     */     
/* 303 */     if (this.pub_array == null) return null; 
/* 304 */     byte[][] tmp = new byte[3][];
/* 305 */     tmp[0] = sshrsa;
/* 306 */     tmp[1] = this.pub_array;
/* 307 */     tmp[2] = this.n_array;
/* 308 */     return (Buffer.fromBytes(tmp)).buffer;
/*     */   }
/*     */   
/* 311 */   private static final byte[] sshrsa = Util.str2byte("ssh-rsa");
/* 312 */   byte[] getKeyTypeName() { return sshrsa; } public int getKeyType() {
/* 313 */     return 2;
/*     */   }
/*     */   public int getKeySize() {
/* 316 */     return this.key_size;
/*     */   }
/*     */   
/*     */   public byte[] getSignature(byte[] data) {
/*     */     try {
/* 321 */       Class c = Class.forName(JSch.getConfig("signature.rsa"));
/* 322 */       SignatureRSA rsa = (SignatureRSA)c.newInstance();
/* 323 */       rsa.init();
/* 324 */       rsa.setPrvKey(this.prv_array, this.n_array);
/*     */       
/* 326 */       rsa.update(data);
/* 327 */       byte[] sig = rsa.sign();
/* 328 */       byte[][] tmp = new byte[2][];
/* 329 */       tmp[0] = sshrsa;
/* 330 */       tmp[1] = sig;
/* 331 */       return (Buffer.fromBytes(tmp)).buffer;
/*     */     }
/* 333 */     catch (Exception e) {
/*     */       
/* 335 */       return null;
/*     */     } 
/*     */   }
/*     */   public Signature getVerifier() {
/*     */     try {
/* 340 */       Class c = Class.forName(JSch.getConfig("signature.rsa"));
/* 341 */       SignatureRSA rsa = (SignatureRSA)c.newInstance();
/* 342 */       rsa.init();
/*     */       
/* 344 */       if (this.pub_array == null && this.n_array == null && getPublicKeyBlob() != null) {
/* 345 */         Buffer buf = new Buffer(getPublicKeyBlob());
/* 346 */         buf.getString();
/* 347 */         this.pub_array = buf.getString();
/* 348 */         this.n_array = buf.getString();
/*     */       } 
/*     */       
/* 351 */       rsa.setPubKey(this.pub_array, this.n_array);
/* 352 */       return rsa;
/*     */     }
/* 354 */     catch (Exception e) {
/*     */       
/* 356 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   static KeyPair fromSSHAgent(JSch jsch, Buffer buf) throws JSchException {
/* 361 */     byte[][] tmp = buf.getBytes(8, "invalid key format");
/*     */     
/* 363 */     byte[] n_array = tmp[1];
/* 364 */     byte[] pub_array = tmp[2];
/* 365 */     byte[] prv_array = tmp[3];
/* 366 */     KeyPairRSA kpair = new KeyPairRSA(jsch, n_array, pub_array, prv_array);
/* 367 */     kpair.c_array = tmp[4];
/* 368 */     kpair.p_array = tmp[5];
/* 369 */     kpair.q_array = tmp[6];
/* 370 */     kpair.publicKeyComment = new String(tmp[7]);
/* 371 */     kpair.vendor = 0;
/* 372 */     return kpair;
/*     */   }
/*     */   
/*     */   public byte[] forSSHAgent() throws JSchException {
/* 376 */     if (isEncrypted()) {
/* 377 */       throw new JSchException("key is encrypted.");
/*     */     }
/* 379 */     Buffer buf = new Buffer();
/* 380 */     buf.putString(sshrsa);
/* 381 */     buf.putString(this.n_array);
/* 382 */     buf.putString(this.pub_array);
/* 383 */     buf.putString(this.prv_array);
/* 384 */     buf.putString(getCArray());
/* 385 */     buf.putString(this.p_array);
/* 386 */     buf.putString(this.q_array);
/* 387 */     buf.putString(Util.str2byte(this.publicKeyComment));
/* 388 */     byte[] result = new byte[buf.getLength()];
/* 389 */     buf.getByte(result, 0, result.length);
/* 390 */     return result;
/*     */   }
/*     */   
/*     */   private byte[] getEPArray() {
/* 394 */     if (this.ep_array == null) {
/* 395 */       this.ep_array = (new BigInteger(this.prv_array)).mod((new BigInteger(this.p_array)).subtract(BigInteger.ONE)).toByteArray();
/*     */     }
/* 397 */     return this.ep_array;
/*     */   }
/*     */   
/*     */   private byte[] getEQArray() {
/* 401 */     if (this.eq_array == null) {
/* 402 */       this.eq_array = (new BigInteger(this.prv_array)).mod((new BigInteger(this.q_array)).subtract(BigInteger.ONE)).toByteArray();
/*     */     }
/* 404 */     return this.eq_array;
/*     */   }
/*     */   
/*     */   private byte[] getCArray() {
/* 408 */     if (this.c_array == null) {
/* 409 */       this.c_array = (new BigInteger(this.q_array)).modInverse(new BigInteger(this.p_array)).toByteArray();
/*     */     }
/* 411 */     return this.c_array;
/*     */   }
/*     */   
/*     */   public void dispose() {
/* 415 */     super.dispose();
/* 416 */     Util.bzero(this.prv_array);
/*     */   }
/*     */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\KeyPairRSA.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */