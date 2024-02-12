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
/*     */ public class KeyPairDSA
/*     */   extends KeyPair
/*     */ {
/*     */   private byte[] P_array;
/*     */   private byte[] Q_array;
/*     */   private byte[] G_array;
/*     */   private byte[] pub_array;
/*     */   private byte[] prv_array;
/*  40 */   private int key_size = 1024;
/*     */   
/*     */   public KeyPairDSA(JSch jsch) {
/*  43 */     this(jsch, (byte[])null, (byte[])null, (byte[])null, (byte[])null, (byte[])null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public KeyPairDSA(JSch jsch, byte[] P_array, byte[] Q_array, byte[] G_array, byte[] pub_array, byte[] prv_array) {
/*  52 */     super(jsch);
/*  53 */     this.P_array = P_array;
/*  54 */     this.Q_array = Q_array;
/*  55 */     this.G_array = G_array;
/*  56 */     this.pub_array = pub_array;
/*  57 */     this.prv_array = prv_array;
/*  58 */     if (P_array != null)
/*  59 */       this.key_size = (new BigInteger(P_array)).bitLength(); 
/*     */   }
/*     */   
/*     */   void generate(int key_size) throws JSchException {
/*  63 */     this.key_size = key_size;
/*     */     try {
/*  65 */       Class c = Class.forName(JSch.getConfig("keypairgen.dsa"));
/*  66 */       KeyPairGenDSA keypairgen = (KeyPairGenDSA)c.newInstance();
/*  67 */       keypairgen.init(key_size);
/*  68 */       this.P_array = keypairgen.getP();
/*  69 */       this.Q_array = keypairgen.getQ();
/*  70 */       this.G_array = keypairgen.getG();
/*  71 */       this.pub_array = keypairgen.getY();
/*  72 */       this.prv_array = keypairgen.getX();
/*     */       
/*  74 */       keypairgen = null;
/*     */     }
/*  76 */     catch (Exception e) {
/*     */       
/*  78 */       if (e instanceof Throwable)
/*  79 */         throw new JSchException(e.toString(), e); 
/*  80 */       throw new JSchException(e.toString());
/*     */     } 
/*     */   }
/*     */   
/*  84 */   private static final byte[] begin = Util.str2byte("-----BEGIN DSA PRIVATE KEY-----");
/*  85 */   private static final byte[] end = Util.str2byte("-----END DSA PRIVATE KEY-----");
/*     */   
/*  87 */   byte[] getBegin() { return begin; } byte[] getEnd() {
/*  88 */     return end;
/*     */   }
/*     */   byte[] getPrivateKey() {
/*  91 */     int content = 1 + countLength(1) + 1 + 1 + countLength(this.P_array.length) + this.P_array.length + 1 + countLength(this.Q_array.length) + this.Q_array.length + 1 + countLength(this.G_array.length) + this.G_array.length + 1 + countLength(this.pub_array.length) + this.pub_array.length + 1 + countLength(this.prv_array.length) + this.prv_array.length;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  99 */     int total = 1 + countLength(content) + content;
/*     */ 
/*     */     
/* 102 */     byte[] plain = new byte[total];
/* 103 */     int index = 0;
/* 104 */     index = writeSEQUENCE(plain, index, content);
/* 105 */     index = writeINTEGER(plain, index, new byte[1]);
/* 106 */     index = writeINTEGER(plain, index, this.P_array);
/* 107 */     index = writeINTEGER(plain, index, this.Q_array);
/* 108 */     index = writeINTEGER(plain, index, this.G_array);
/* 109 */     index = writeINTEGER(plain, index, this.pub_array);
/* 110 */     index = writeINTEGER(plain, index, this.prv_array);
/* 111 */     return plain;
/*     */   }
/*     */ 
/*     */   
/*     */   boolean parse(byte[] plain) {
/*     */     try {
/* 117 */       if (this.vendor == 1) {
/* 118 */         if (plain[0] != 48) {
/* 119 */           Buffer buf = new Buffer(plain);
/* 120 */           buf.getInt();
/* 121 */           this.P_array = buf.getMPIntBits();
/* 122 */           this.G_array = buf.getMPIntBits();
/* 123 */           this.Q_array = buf.getMPIntBits();
/* 124 */           this.pub_array = buf.getMPIntBits();
/* 125 */           this.prv_array = buf.getMPIntBits();
/* 126 */           if (this.P_array != null)
/* 127 */             this.key_size = (new BigInteger(this.P_array)).bitLength(); 
/* 128 */           return true;
/*     */         } 
/* 130 */         return false;
/*     */       } 
/* 132 */       if (this.vendor == 2) {
/* 133 */         Buffer buf = new Buffer(plain);
/* 134 */         buf.skip(plain.length);
/*     */         
/*     */         try {
/* 137 */           byte[][] tmp = buf.getBytes(1, "");
/* 138 */           this.prv_array = tmp[0];
/*     */         }
/* 140 */         catch (JSchException e) {
/* 141 */           return false;
/*     */         } 
/*     */         
/* 144 */         return true;
/*     */       } 
/*     */       
/* 147 */       int index = 0;
/* 148 */       int length = 0;
/*     */       
/* 150 */       if (plain[index] != 48) return false; 
/* 151 */       index++;
/* 152 */       length = plain[index++] & 0xFF;
/* 153 */       if ((length & 0x80) != 0) {
/* 154 */         int foo = length & 0x7F; length = 0;
/* 155 */         for (; foo-- > 0; length = (length << 8) + (plain[index++] & 0xFF));
/*     */       } 
/*     */       
/* 158 */       if (plain[index] != 2) return false; 
/* 159 */       index++;
/* 160 */       length = plain[index++] & 0xFF;
/* 161 */       if ((length & 0x80) != 0) {
/* 162 */         int foo = length & 0x7F; length = 0;
/* 163 */         for (; foo-- > 0; length = (length << 8) + (plain[index++] & 0xFF));
/*     */       } 
/* 165 */       index += length;
/*     */       
/* 167 */       index++;
/* 168 */       length = plain[index++] & 0xFF;
/* 169 */       if ((length & 0x80) != 0) {
/* 170 */         int foo = length & 0x7F; length = 0;
/* 171 */         for (; foo-- > 0; length = (length << 8) + (plain[index++] & 0xFF));
/*     */       } 
/* 173 */       this.P_array = new byte[length];
/* 174 */       System.arraycopy(plain, index, this.P_array, 0, length);
/* 175 */       index += length;
/*     */       
/* 177 */       index++;
/* 178 */       length = plain[index++] & 0xFF;
/* 179 */       if ((length & 0x80) != 0) {
/* 180 */         int foo = length & 0x7F; length = 0;
/* 181 */         for (; foo-- > 0; length = (length << 8) + (plain[index++] & 0xFF));
/*     */       } 
/* 183 */       this.Q_array = new byte[length];
/* 184 */       System.arraycopy(plain, index, this.Q_array, 0, length);
/* 185 */       index += length;
/*     */       
/* 187 */       index++;
/* 188 */       length = plain[index++] & 0xFF;
/* 189 */       if ((length & 0x80) != 0) {
/* 190 */         int foo = length & 0x7F; length = 0;
/* 191 */         for (; foo-- > 0; length = (length << 8) + (plain[index++] & 0xFF));
/*     */       } 
/* 193 */       this.G_array = new byte[length];
/* 194 */       System.arraycopy(plain, index, this.G_array, 0, length);
/* 195 */       index += length;
/*     */       
/* 197 */       index++;
/* 198 */       length = plain[index++] & 0xFF;
/* 199 */       if ((length & 0x80) != 0) {
/* 200 */         int foo = length & 0x7F; length = 0;
/* 201 */         for (; foo-- > 0; length = (length << 8) + (plain[index++] & 0xFF));
/*     */       } 
/* 203 */       this.pub_array = new byte[length];
/* 204 */       System.arraycopy(plain, index, this.pub_array, 0, length);
/* 205 */       index += length;
/*     */       
/* 207 */       index++;
/* 208 */       length = plain[index++] & 0xFF;
/* 209 */       if ((length & 0x80) != 0) {
/* 210 */         int foo = length & 0x7F; length = 0;
/* 211 */         for (; foo-- > 0; length = (length << 8) + (plain[index++] & 0xFF));
/*     */       } 
/* 213 */       this.prv_array = new byte[length];
/* 214 */       System.arraycopy(plain, index, this.prv_array, 0, length);
/* 215 */       index += length;
/*     */       
/* 217 */       if (this.P_array != null) {
/* 218 */         this.key_size = (new BigInteger(this.P_array)).bitLength();
/*     */       }
/* 220 */     } catch (Exception e) {
/*     */ 
/*     */       
/* 223 */       return false;
/*     */     } 
/* 225 */     return true;
/*     */   }
/*     */   
/*     */   public byte[] getPublicKeyBlob() {
/* 229 */     byte[] foo = super.getPublicKeyBlob();
/* 230 */     if (foo != null) return foo;
/*     */     
/* 232 */     if (this.P_array == null) return null; 
/* 233 */     byte[][] tmp = new byte[5][];
/* 234 */     tmp[0] = sshdss;
/* 235 */     tmp[1] = this.P_array;
/* 236 */     tmp[2] = this.Q_array;
/* 237 */     tmp[3] = this.G_array;
/* 238 */     tmp[4] = this.pub_array;
/* 239 */     return (Buffer.fromBytes(tmp)).buffer;
/*     */   }
/*     */   
/* 242 */   private static final byte[] sshdss = Util.str2byte("ssh-dss");
/* 243 */   byte[] getKeyTypeName() { return sshdss; } public int getKeyType() {
/* 244 */     return 1;
/*     */   }
/*     */   public int getKeySize() {
/* 247 */     return this.key_size;
/*     */   }
/*     */   
/*     */   public byte[] getSignature(byte[] data) {
/*     */     try {
/* 252 */       Class c = Class.forName(JSch.getConfig("signature.dss"));
/* 253 */       SignatureDSA dsa = (SignatureDSA)c.newInstance();
/* 254 */       dsa.init();
/* 255 */       dsa.setPrvKey(this.prv_array, this.P_array, this.Q_array, this.G_array);
/*     */       
/* 257 */       dsa.update(data);
/* 258 */       byte[] sig = dsa.sign();
/* 259 */       byte[][] tmp = new byte[2][];
/* 260 */       tmp[0] = sshdss;
/* 261 */       tmp[1] = sig;
/* 262 */       return (Buffer.fromBytes(tmp)).buffer;
/*     */     }
/* 264 */     catch (Exception e) {
/*     */ 
/*     */       
/* 267 */       return null;
/*     */     } 
/*     */   }
/*     */   public Signature getVerifier() {
/*     */     try {
/* 272 */       Class c = Class.forName(JSch.getConfig("signature.dss"));
/* 273 */       SignatureDSA dsa = (SignatureDSA)c.newInstance();
/* 274 */       dsa.init();
/*     */       
/* 276 */       if (this.pub_array == null && this.P_array == null && getPublicKeyBlob() != null) {
/* 277 */         Buffer buf = new Buffer(getPublicKeyBlob());
/* 278 */         buf.getString();
/* 279 */         this.P_array = buf.getString();
/* 280 */         this.Q_array = buf.getString();
/* 281 */         this.G_array = buf.getString();
/* 282 */         this.pub_array = buf.getString();
/*     */       } 
/*     */       
/* 285 */       dsa.setPubKey(this.pub_array, this.P_array, this.Q_array, this.G_array);
/* 286 */       return dsa;
/*     */     }
/* 288 */     catch (Exception e) {
/*     */ 
/*     */       
/* 291 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   static KeyPair fromSSHAgent(JSch jsch, Buffer buf) throws JSchException {
/* 296 */     byte[][] tmp = buf.getBytes(7, "invalid key format");
/*     */     
/* 298 */     byte[] P_array = tmp[1];
/* 299 */     byte[] Q_array = tmp[2];
/* 300 */     byte[] G_array = tmp[3];
/* 301 */     byte[] pub_array = tmp[4];
/* 302 */     byte[] prv_array = tmp[5];
/* 303 */     KeyPairDSA kpair = new KeyPairDSA(jsch, P_array, Q_array, G_array, pub_array, prv_array);
/*     */ 
/*     */     
/* 306 */     kpair.publicKeyComment = new String(tmp[6]);
/* 307 */     kpair.vendor = 0;
/* 308 */     return kpair;
/*     */   }
/*     */   
/*     */   public byte[] forSSHAgent() throws JSchException {
/* 312 */     if (isEncrypted()) {
/* 313 */       throw new JSchException("key is encrypted.");
/*     */     }
/* 315 */     Buffer buf = new Buffer();
/* 316 */     buf.putString(sshdss);
/* 317 */     buf.putString(this.P_array);
/* 318 */     buf.putString(this.Q_array);
/* 319 */     buf.putString(this.G_array);
/* 320 */     buf.putString(this.pub_array);
/* 321 */     buf.putString(this.prv_array);
/* 322 */     buf.putString(Util.str2byte(this.publicKeyComment));
/* 323 */     byte[] result = new byte[buf.getLength()];
/* 324 */     buf.getByte(result, 0, result.length);
/* 325 */     return result;
/*     */   }
/*     */   
/*     */   public void dispose() {
/* 329 */     super.dispose();
/* 330 */     Util.bzero(this.prv_array);
/*     */   }
/*     */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\KeyPairDSA.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */