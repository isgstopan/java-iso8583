/*     */ package com.jcraft.jsch;
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
/*     */ public class KeyPairECDSA
/*     */   extends KeyPair
/*     */ {
/*  34 */   private static byte[][] oids = new byte[][] { { 6, 8, 42, -122, 72, -50, 61, 3, 1, 7 }, { 6, 5, 43, -127, 4, 0, 34 }, { 6, 5, 43, -127, 4, 0, 35 } };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  43 */   private static String[] names = new String[] { "nistp256", "nistp384", "nistp521" };
/*     */ 
/*     */ 
/*     */   
/*  47 */   private byte[] name = Util.str2byte(names[0]);
/*     */   
/*     */   private byte[] r_array;
/*     */   private byte[] s_array;
/*     */   private byte[] prv_array;
/*  52 */   private int key_size = 256;
/*     */   
/*     */   public KeyPairECDSA(JSch jsch) {
/*  55 */     this(jsch, (byte[])null, (byte[])null, (byte[])null, (byte[])null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public KeyPairECDSA(JSch jsch, byte[] name, byte[] r_array, byte[] s_array, byte[] prv_array) {
/*  63 */     super(jsch);
/*  64 */     if (name != null)
/*  65 */       this.name = name; 
/*  66 */     this.r_array = r_array;
/*  67 */     this.s_array = s_array;
/*  68 */     this.prv_array = prv_array;
/*  69 */     if (prv_array != null) {
/*  70 */       this.key_size = (prv_array.length >= 64) ? 521 : ((prv_array.length >= 48) ? 384 : 256);
/*     */     }
/*     */   }
/*     */   
/*     */   void generate(int key_size) throws JSchException {
/*  75 */     this.key_size = key_size;
/*     */     try {
/*  77 */       Class c = Class.forName(JSch.getConfig("keypairgen.ecdsa"));
/*  78 */       KeyPairGenECDSA keypairgen = (KeyPairGenECDSA)c.newInstance();
/*  79 */       keypairgen.init(key_size);
/*  80 */       this.prv_array = keypairgen.getD();
/*  81 */       this.r_array = keypairgen.getR();
/*  82 */       this.s_array = keypairgen.getS();
/*  83 */       this.name = Util.str2byte(names[(this.prv_array.length >= 64) ? 2 : ((this.prv_array.length >= 48) ? 1 : 0)]);
/*     */       
/*  85 */       keypairgen = null;
/*     */     }
/*  87 */     catch (Exception e) {
/*  88 */       if (e instanceof Throwable)
/*  89 */         throw new JSchException(e.toString(), e); 
/*  90 */       throw new JSchException(e.toString());
/*     */     } 
/*     */   }
/*     */   
/*  94 */   private static final byte[] begin = Util.str2byte("-----BEGIN EC PRIVATE KEY-----");
/*     */   
/*  96 */   private static final byte[] end = Util.str2byte("-----END EC PRIVATE KEY-----");
/*     */   
/*     */   byte[] getBegin() {
/*  99 */     return begin; } byte[] getEnd() {
/* 100 */     return end;
/*     */   }
/*     */   
/*     */   byte[] getPrivateKey() {
/* 104 */     byte[] tmp = new byte[1]; tmp[0] = 1;
/*     */     
/* 106 */     byte[] oid = oids[(this.r_array.length >= 64) ? 2 : ((this.r_array.length >= 48) ? 1 : 0)];
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 111 */     byte[] point = toPoint(this.r_array, this.s_array);
/*     */     
/* 113 */     int bar = ((point.length + 1 & 0x80) == 0) ? 3 : 4;
/* 114 */     byte[] foo = new byte[point.length + bar];
/* 115 */     System.arraycopy(point, 0, foo, bar, point.length);
/* 116 */     foo[0] = 3;
/* 117 */     if (bar == 3) {
/* 118 */       foo[1] = (byte)(point.length + 1);
/*     */     } else {
/*     */       
/* 121 */       foo[1] = -127;
/* 122 */       foo[2] = (byte)(point.length + 1);
/*     */     } 
/* 124 */     point = foo;
/*     */     
/* 126 */     int content = 1 + countLength(tmp.length) + tmp.length + 1 + countLength(this.prv_array.length) + this.prv_array.length + 1 + countLength(oid.length) + oid.length + 1 + countLength(point.length) + point.length;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 132 */     int total = 1 + countLength(content) + content;
/*     */ 
/*     */     
/* 135 */     byte[] plain = new byte[total];
/* 136 */     int index = 0;
/* 137 */     index = writeSEQUENCE(plain, index, content);
/* 138 */     index = writeINTEGER(plain, index, tmp);
/* 139 */     index = writeOCTETSTRING(plain, index, this.prv_array);
/* 140 */     index = writeDATA(plain, (byte)-96, index, oid);
/* 141 */     index = writeDATA(plain, (byte)-95, index, point);
/*     */     
/* 143 */     return plain;
/*     */   }
/*     */ 
/*     */   
/*     */   boolean parse(byte[] plain) {
/*     */     try {
/* 149 */       if (this.vendor == 1)
/*     */       {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 156 */         return false;
/*     */       }
/* 158 */       if (this.vendor == 2)
/*     */       {
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
/* 173 */         return false;
/*     */       }
/*     */       
/* 176 */       int index = 0;
/* 177 */       int length = 0;
/*     */       
/* 179 */       if (plain[index] != 48) return false; 
/* 180 */       index++;
/* 181 */       length = plain[index++] & 0xFF;
/* 182 */       if ((length & 0x80) != 0) {
/* 183 */         int foo = length & 0x7F; length = 0;
/* 184 */         for (; foo-- > 0; length = (length << 8) + (plain[index++] & 0xFF));
/*     */       } 
/*     */       
/* 187 */       if (plain[index] != 2) return false; 
/* 188 */       index++;
/*     */       
/* 190 */       length = plain[index++] & 0xFF;
/* 191 */       if ((length & 0x80) != 0) {
/* 192 */         int foo = length & 0x7F; length = 0;
/* 193 */         for (; foo-- > 0; length = (length << 8) + (plain[index++] & 0xFF));
/*     */       } 
/*     */       
/* 196 */       index += length;
/* 197 */       index++;
/*     */       
/* 199 */       length = plain[index++] & 0xFF;
/* 200 */       if ((length & 0x80) != 0) {
/* 201 */         int foo = length & 0x7F; length = 0;
/* 202 */         for (; foo-- > 0; length = (length << 8) + (plain[index++] & 0xFF));
/*     */       } 
/*     */       
/* 205 */       this.prv_array = new byte[length];
/* 206 */       System.arraycopy(plain, index, this.prv_array, 0, length);
/*     */       
/* 208 */       index += length;
/*     */       
/* 210 */       index++;
/*     */       
/* 212 */       length = plain[index++] & 0xFF;
/* 213 */       if ((length & 0x80) != 0) {
/* 214 */         int foo = length & 0x7F; length = 0;
/* 215 */         for (; foo-- > 0; length = (length << 8) + (plain[index++] & 0xFF));
/*     */       } 
/*     */       
/* 218 */       byte[] oid_array = new byte[length];
/* 219 */       System.arraycopy(plain, index, oid_array, 0, length);
/* 220 */       index += length;
/*     */       
/* 222 */       for (int i = 0; i < oids.length; i++) {
/* 223 */         if (Util.array_equals(oids[i], oid_array)) {
/* 224 */           this.name = Util.str2byte(names[i]);
/*     */           
/*     */           break;
/*     */         } 
/*     */       } 
/* 229 */       index++;
/*     */       
/* 231 */       length = plain[index++] & 0xFF;
/* 232 */       if ((length & 0x80) != 0) {
/* 233 */         int foo = length & 0x7F; length = 0;
/* 234 */         for (; foo-- > 0; length = (length << 8) + (plain[index++] & 0xFF));
/*     */       } 
/*     */       
/* 237 */       byte[] Q_array = new byte[length];
/* 238 */       System.arraycopy(plain, index, Q_array, 0, length);
/* 239 */       index += length;
/*     */       
/* 241 */       byte[][] tmp = fromPoint(Q_array);
/* 242 */       this.r_array = tmp[0];
/* 243 */       this.s_array = tmp[1];
/*     */       
/* 245 */       if (this.prv_array != null) {
/* 246 */         this.key_size = (this.prv_array.length >= 64) ? 521 : ((this.prv_array.length >= 48) ? 384 : 256);
/*     */       }
/*     */     }
/* 249 */     catch (Exception e) {
/*     */ 
/*     */       
/* 252 */       return false;
/*     */     } 
/* 254 */     return true;
/*     */   }
/*     */   
/*     */   public byte[] getPublicKeyBlob() {
/* 258 */     byte[] foo = super.getPublicKeyBlob();
/*     */     
/* 260 */     if (foo != null) return foo;
/*     */     
/* 262 */     if (this.r_array == null) return null;
/*     */     
/* 264 */     byte[][] tmp = new byte[3][];
/* 265 */     tmp[0] = Util.str2byte("ecdsa-sha2-" + new String(this.name));
/* 266 */     tmp[1] = this.name;
/* 267 */     tmp[2] = new byte[1 + this.r_array.length + this.s_array.length];
/* 268 */     tmp[2][0] = 4;
/* 269 */     System.arraycopy(this.r_array, 0, tmp[2], 1, this.r_array.length);
/* 270 */     System.arraycopy(this.s_array, 0, tmp[2], 1 + this.r_array.length, this.s_array.length);
/*     */     
/* 272 */     return (Buffer.fromBytes(tmp)).buffer;
/*     */   }
/*     */   
/*     */   byte[] getKeyTypeName() {
/* 276 */     return Util.str2byte("ecdsa-sha2-" + new String(this.name));
/*     */   }
/*     */   public int getKeyType() {
/* 279 */     return 3;
/*     */   }
/*     */   public int getKeySize() {
/* 282 */     return this.key_size;
/*     */   }
/*     */   
/*     */   public byte[] getSignature(byte[] data) {
/*     */     try {
/* 287 */       Class c = Class.forName(JSch.getConfig("signature.ecdsa"));
/* 288 */       SignatureECDSA ecdsa = (SignatureECDSA)c.newInstance();
/* 289 */       ecdsa.init();
/* 290 */       ecdsa.setPrvKey(this.prv_array);
/*     */       
/* 292 */       ecdsa.update(data);
/* 293 */       byte[] sig = ecdsa.sign();
/*     */       
/* 295 */       byte[][] tmp = new byte[2][];
/* 296 */       tmp[0] = Util.str2byte("ecdsa-sha2-" + new String(this.name));
/* 297 */       tmp[1] = sig;
/* 298 */       return (Buffer.fromBytes(tmp)).buffer;
/*     */     }
/* 300 */     catch (Exception e) {
/*     */ 
/*     */       
/* 303 */       return null;
/*     */     } 
/*     */   }
/*     */   public Signature getVerifier() {
/*     */     try {
/* 308 */       Class c = Class.forName(JSch.getConfig("signature.ecdsa"));
/* 309 */       SignatureECDSA ecdsa = (SignatureECDSA)c.newInstance();
/* 310 */       ecdsa.init();
/*     */       
/* 312 */       if (this.r_array == null && this.s_array == null && getPublicKeyBlob() != null) {
/* 313 */         Buffer buf = new Buffer(getPublicKeyBlob());
/* 314 */         buf.getString();
/* 315 */         buf.getString();
/* 316 */         byte[][] tmp = fromPoint(buf.getString());
/* 317 */         this.r_array = tmp[0];
/* 318 */         this.s_array = tmp[1];
/*     */       } 
/* 320 */       ecdsa.setPubKey(this.r_array, this.s_array);
/* 321 */       return ecdsa;
/*     */     }
/* 323 */     catch (Exception e) {
/*     */ 
/*     */       
/* 326 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   static KeyPair fromSSHAgent(JSch jsch, Buffer buf) throws JSchException {
/* 331 */     byte[][] tmp = buf.getBytes(5, "invalid key format");
/*     */     
/* 333 */     byte[] name = tmp[1];
/* 334 */     byte[][] foo = fromPoint(tmp[2]);
/* 335 */     byte[] r_array = foo[0];
/* 336 */     byte[] s_array = foo[1];
/*     */     
/* 338 */     byte[] prv_array = tmp[3];
/* 339 */     KeyPairECDSA kpair = new KeyPairECDSA(jsch, name, r_array, s_array, prv_array);
/*     */ 
/*     */ 
/*     */     
/* 343 */     kpair.publicKeyComment = new String(tmp[4]);
/* 344 */     kpair.vendor = 0;
/* 345 */     return kpair;
/*     */   }
/*     */   
/*     */   public byte[] forSSHAgent() throws JSchException {
/* 349 */     if (isEncrypted()) {
/* 350 */       throw new JSchException("key is encrypted.");
/*     */     }
/* 352 */     Buffer buf = new Buffer();
/* 353 */     buf.putString(Util.str2byte("ecdsa-sha2-" + new String(this.name)));
/* 354 */     buf.putString(this.name);
/* 355 */     buf.putString(toPoint(this.r_array, this.s_array));
/* 356 */     buf.putString(this.prv_array);
/* 357 */     buf.putString(Util.str2byte(this.publicKeyComment));
/* 358 */     byte[] result = new byte[buf.getLength()];
/* 359 */     buf.getByte(result, 0, result.length);
/* 360 */     return result;
/*     */   }
/*     */   
/*     */   static byte[] toPoint(byte[] r_array, byte[] s_array) {
/* 364 */     byte[] tmp = new byte[1 + r_array.length + s_array.length];
/* 365 */     tmp[0] = 4;
/* 366 */     System.arraycopy(r_array, 0, tmp, 1, r_array.length);
/* 367 */     System.arraycopy(s_array, 0, tmp, 1 + r_array.length, s_array.length);
/* 368 */     return tmp;
/*     */   }
/*     */   
/*     */   static byte[][] fromPoint(byte[] point) {
/* 372 */     int i = 0;
/* 373 */     for (; point[i] != 4; i++);
/* 374 */     i++;
/* 375 */     byte[][] tmp = new byte[2][];
/* 376 */     byte[] r_array = new byte[(point.length - i) / 2];
/* 377 */     byte[] s_array = new byte[(point.length - i) / 2];
/*     */     
/* 379 */     System.arraycopy(point, i, r_array, 0, r_array.length);
/* 380 */     System.arraycopy(point, i + r_array.length, s_array, 0, s_array.length);
/* 381 */     tmp[0] = r_array;
/* 382 */     tmp[1] = s_array;
/*     */     
/* 384 */     return tmp;
/*     */   }
/*     */   
/*     */   public void dispose() {
/* 388 */     super.dispose();
/* 389 */     Util.bzero(this.prv_array);
/*     */   }
/*     */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\KeyPairECDSA.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */