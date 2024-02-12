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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class KeyExchange
/*     */ {
/*     */   static final int PROPOSAL_KEX_ALGS = 0;
/*     */   static final int PROPOSAL_SERVER_HOST_KEY_ALGS = 1;
/*     */   static final int PROPOSAL_ENC_ALGS_CTOS = 2;
/*     */   static final int PROPOSAL_ENC_ALGS_STOC = 3;
/*     */   static final int PROPOSAL_MAC_ALGS_CTOS = 4;
/*     */   static final int PROPOSAL_MAC_ALGS_STOC = 5;
/*     */   static final int PROPOSAL_COMP_ALGS_CTOS = 6;
/*     */   static final int PROPOSAL_COMP_ALGS_STOC = 7;
/*     */   static final int PROPOSAL_LANG_CTOS = 8;
/*     */   static final int PROPOSAL_LANG_STOC = 9;
/*     */   static final int PROPOSAL_MAX = 10;
/*  50 */   static String kex = "diffie-hellman-group1-sha1";
/*  51 */   static String server_host_key = "ssh-rsa,ssh-dss";
/*  52 */   static String enc_c2s = "blowfish-cbc";
/*  53 */   static String enc_s2c = "blowfish-cbc";
/*  54 */   static String mac_c2s = "hmac-md5";
/*     */   
/*  56 */   static String mac_s2c = "hmac-md5";
/*     */ 
/*     */   
/*  59 */   static String lang_c2s = "";
/*  60 */   static String lang_s2c = "";
/*     */   
/*     */   public static final int STATE_END = 0;
/*     */   
/*  64 */   protected Session session = null;
/*  65 */   protected HASH sha = null;
/*  66 */   protected byte[] K = null;
/*  67 */   protected byte[] H = null;
/*  68 */   protected byte[] K_S = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  76 */   protected final int RSA = 0;
/*  77 */   protected final int DSS = 1;
/*  78 */   protected final int ECDSA = 2;
/*  79 */   private int type = 0;
/*  80 */   private String key_alg_name = "";
/*     */   public abstract void init(Session paramSession, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2, byte[] paramArrayOfbyte3, byte[] paramArrayOfbyte4) throws Exception;
/*     */   public String getKeyType() {
/*  83 */     if (this.type == 1) return "DSA"; 
/*  84 */     if (this.type == 0) return "RSA"; 
/*  85 */     return "ECDSA";
/*     */   } public abstract boolean next(Buffer paramBuffer) throws Exception;
/*     */   public abstract int getState();
/*     */   public String getKeyAlgorithName() {
/*  89 */     return this.key_alg_name;
/*     */   }
/*     */   
/*     */   protected static String[] guess(byte[] I_S, byte[] I_C) {
/*  93 */     String[] guess = new String[10];
/*  94 */     Buffer sb = new Buffer(I_S); sb.setOffSet(17);
/*  95 */     Buffer cb = new Buffer(I_C); cb.setOffSet(17);
/*     */     
/*  97 */     if (JSch.getLogger().isEnabled(1)) {
/*  98 */       int j; for (j = 0; j < 10; j++) {
/*  99 */         JSch.getLogger().log(1, "kex: server: " + Util.byte2str(sb.getString()));
/*     */       }
/*     */       
/* 102 */       for (j = 0; j < 10; j++) {
/* 103 */         JSch.getLogger().log(1, "kex: client: " + Util.byte2str(cb.getString()));
/*     */       }
/*     */       
/* 106 */       sb.setOffSet(17);
/* 107 */       cb.setOffSet(17);
/*     */     } 
/*     */     
/* 110 */     for (int i = 0; i < 10; i++) {
/* 111 */       byte[] sp = sb.getString();
/* 112 */       byte[] cp = cb.getString();
/* 113 */       int j = 0;
/* 114 */       int k = 0;
/*     */ 
/*     */       
/* 117 */       label55: while (j < cp.length) {
/* 118 */         for (; j < cp.length && cp[j] != 44; j++);
/* 119 */         if (k == j) return null; 
/* 120 */         String algorithm = Util.byte2str(cp, k, j - k);
/* 121 */         int l = 0;
/* 122 */         int m = 0;
/* 123 */         while (l < sp.length) {
/* 124 */           for (; l < sp.length && sp[l] != 44; l++);
/* 125 */           if (m == l) return null; 
/* 126 */           if (algorithm.equals(Util.byte2str(sp, m, l - m))) {
/* 127 */             guess[i] = algorithm;
/*     */             
/*     */             break label55;
/*     */           } 
/* 131 */           m = ++l;
/*     */         } 
/*     */         
/* 134 */         k = ++j;
/*     */       } 
/* 136 */       if (j == 0) {
/* 137 */         guess[i] = "";
/*     */       }
/* 139 */       else if (guess[i] == null) {
/* 140 */         return null;
/*     */       } 
/*     */     } 
/*     */     
/* 144 */     if (JSch.getLogger().isEnabled(1)) {
/* 145 */       JSch.getLogger().log(1, "kex: server->client " + guess[3] + " " + guess[5] + " " + guess[7]);
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 150 */       JSch.getLogger().log(1, "kex: client->server " + guess[2] + " " + guess[4] + " " + guess[6]);
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 157 */     return guess;
/*     */   }
/*     */   
/*     */   public String getFingerPrint() {
/* 161 */     HASH hash = null;
/*     */     try {
/* 163 */       Class c = Class.forName(this.session.getConfig("md5"));
/* 164 */       hash = (HASH)c.newInstance();
/*     */     } catch (Exception e) {
/* 166 */       System.err.println("getFingerPrint: " + e);
/* 167 */     }  return Util.getFingerPrint(hash, getHostKey());
/*     */   }
/* 169 */   byte[] getK() { return this.K; }
/* 170 */   byte[] getH() { return this.H; }
/* 171 */   HASH getHash() { return this.sha; } byte[] getHostKey() {
/* 172 */     return this.K_S;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected byte[] normalize(byte[] secret) {
/* 180 */     if (secret.length > 1 && secret[0] == 0 && (secret[1] & 0x80) == 0) {
/*     */       
/* 182 */       byte[] tmp = new byte[secret.length - 1];
/* 183 */       System.arraycopy(secret, 1, tmp, 0, tmp.length);
/* 184 */       return normalize(tmp);
/*     */     } 
/*     */     
/* 187 */     return secret;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean verify(String alg, byte[] K_S, int index, byte[] sig_of_H) throws Exception {
/* 195 */     int i = index;
/* 196 */     boolean result = false;
/*     */     
/* 198 */     if (alg.equals("ssh-rsa")) {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 203 */       this.type = 0;
/* 204 */       this.key_alg_name = alg;
/*     */       
/* 206 */       int j = K_S[i++] << 24 & 0xFF000000 | K_S[i++] << 16 & 0xFF0000 | K_S[i++] << 8 & 0xFF00 | K_S[i++] & 0xFF;
/*     */       
/* 208 */       byte[] tmp = new byte[j]; System.arraycopy(K_S, i, tmp, 0, j); i += j;
/* 209 */       byte[] ee = tmp;
/* 210 */       j = K_S[i++] << 24 & 0xFF000000 | K_S[i++] << 16 & 0xFF0000 | K_S[i++] << 8 & 0xFF00 | K_S[i++] & 0xFF;
/*     */       
/* 212 */       tmp = new byte[j]; System.arraycopy(K_S, i, tmp, 0, j); i += j;
/* 213 */       byte[] n = tmp;
/*     */       
/* 215 */       SignatureRSA sig = null;
/*     */       try {
/* 217 */         Class c = Class.forName(this.session.getConfig("signature.rsa"));
/* 218 */         sig = (SignatureRSA)c.newInstance();
/* 219 */         sig.init();
/*     */       }
/* 221 */       catch (Exception e) {
/* 222 */         System.err.println(e);
/*     */       } 
/* 224 */       sig.setPubKey(ee, n);
/* 225 */       sig.update(this.H);
/* 226 */       result = sig.verify(sig_of_H);
/*     */       
/* 228 */       if (JSch.getLogger().isEnabled(1)) {
/* 229 */         JSch.getLogger().log(1, "ssh_rsa_verify: signature " + result);
/*     */       
/*     */       }
/*     */     }
/* 233 */     else if (alg.equals("ssh-dss")) {
/* 234 */       byte[] q = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 240 */       this.type = 1;
/* 241 */       this.key_alg_name = alg;
/*     */       
/* 243 */       int j = K_S[i++] << 24 & 0xFF000000 | K_S[i++] << 16 & 0xFF0000 | K_S[i++] << 8 & 0xFF00 | K_S[i++] & 0xFF;
/*     */       
/* 245 */       byte[] tmp = new byte[j]; System.arraycopy(K_S, i, tmp, 0, j); i += j;
/* 246 */       byte[] p = tmp;
/* 247 */       j = K_S[i++] << 24 & 0xFF000000 | K_S[i++] << 16 & 0xFF0000 | K_S[i++] << 8 & 0xFF00 | K_S[i++] & 0xFF;
/*     */       
/* 249 */       tmp = new byte[j]; System.arraycopy(K_S, i, tmp, 0, j); i += j;
/* 250 */       q = tmp;
/* 251 */       j = K_S[i++] << 24 & 0xFF000000 | K_S[i++] << 16 & 0xFF0000 | K_S[i++] << 8 & 0xFF00 | K_S[i++] & 0xFF;
/*     */       
/* 253 */       tmp = new byte[j]; System.arraycopy(K_S, i, tmp, 0, j); i += j;
/* 254 */       byte[] g = tmp;
/* 255 */       j = K_S[i++] << 24 & 0xFF000000 | K_S[i++] << 16 & 0xFF0000 | K_S[i++] << 8 & 0xFF00 | K_S[i++] & 0xFF;
/*     */       
/* 257 */       tmp = new byte[j]; System.arraycopy(K_S, i, tmp, 0, j); i += j;
/* 258 */       byte[] f = tmp;
/*     */       
/* 260 */       SignatureDSA sig = null;
/*     */       try {
/* 262 */         Class c = Class.forName(this.session.getConfig("signature.dss"));
/* 263 */         sig = (SignatureDSA)c.newInstance();
/* 264 */         sig.init();
/*     */       }
/* 266 */       catch (Exception e) {
/* 267 */         System.err.println(e);
/*     */       } 
/* 269 */       sig.setPubKey(f, p, q, g);
/* 270 */       sig.update(this.H);
/* 271 */       result = sig.verify(sig_of_H);
/*     */       
/* 273 */       if (JSch.getLogger().isEnabled(1)) {
/* 274 */         JSch.getLogger().log(1, "ssh_dss_verify: signature " + result);
/*     */       
/*     */       }
/*     */     }
/* 278 */     else if (alg.equals("ecdsa-sha2-nistp256") || alg.equals("ecdsa-sha2-nistp384") || alg.equals("ecdsa-sha2-nistp521")) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 286 */       this.type = 2;
/* 287 */       this.key_alg_name = alg;
/*     */       
/* 289 */       int j = K_S[i++] << 24 & 0xFF000000 | K_S[i++] << 16 & 0xFF0000 | K_S[i++] << 8 & 0xFF00 | K_S[i++] & 0xFF;
/*     */       
/* 291 */       byte[] tmp = new byte[j]; System.arraycopy(K_S, i, tmp, 0, j); i += j;
/* 292 */       j = K_S[i++] << 24 & 0xFF000000 | K_S[i++] << 16 & 0xFF0000 | K_S[i++] << 8 & 0xFF00 | K_S[i++] & 0xFF;
/*     */       
/* 294 */       i++;
/* 295 */       tmp = new byte[(j - 1) / 2];
/* 296 */       System.arraycopy(K_S, i, tmp, 0, tmp.length); i += (j - 1) / 2;
/* 297 */       byte[] r = tmp;
/* 298 */       tmp = new byte[(j - 1) / 2];
/* 299 */       System.arraycopy(K_S, i, tmp, 0, tmp.length); i += (j - 1) / 2;
/* 300 */       byte[] s = tmp;
/*     */       
/* 302 */       SignatureECDSA sig = null;
/*     */       try {
/* 304 */         Class c = Class.forName(this.session.getConfig("signature.ecdsa"));
/* 305 */         sig = (SignatureECDSA)c.newInstance();
/* 306 */         sig.init();
/*     */       }
/* 308 */       catch (Exception e) {
/* 309 */         System.err.println(e);
/*     */       } 
/*     */       
/* 312 */       sig.setPubKey(r, s);
/*     */       
/* 314 */       sig.update(this.H);
/*     */       
/* 316 */       result = sig.verify(sig_of_H);
/*     */     } else {
/*     */       
/* 319 */       System.err.println("unknown alg");
/*     */     } 
/*     */     
/* 322 */     return result;
/*     */   }
/*     */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\KeyExchange.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */