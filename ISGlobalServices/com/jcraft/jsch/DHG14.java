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
/*     */ public class DHG14
/*     */   extends KeyExchange
/*     */ {
/*  34 */   static final byte[] g = new byte[] { 2 };
/*  35 */   static final byte[] p = new byte[] { 0, -1, -1, -1, -1, -1, -1, -1, -1, -55, 15, -38, -94, 33, 104, -62, 52, -60, -58, 98, -117, Byte.MIN_VALUE, -36, 28, -47, 41, 2, 78, 8, -118, 103, -52, 116, 2, 11, -66, -90, 59, 19, -101, 34, 81, 74, 8, 121, -114, 52, 4, -35, -17, -107, 25, -77, -51, 58, 67, 27, 48, 43, 10, 109, -14, 95, 20, 55, 79, -31, 53, 109, 109, 81, -62, 69, -28, -123, -75, 118, 98, 94, 126, -58, -12, 76, 66, -23, -90, 55, -19, 107, 11, -1, 92, -74, -12, 6, -73, -19, -18, 56, 107, -5, 90, -119, -97, -91, -82, -97, 36, 17, 124, 75, 31, -26, 73, 40, 102, 81, -20, -28, 91, 61, -62, 0, 124, -72, -95, 99, -65, 5, -104, -38, 72, 54, 28, 85, -45, -102, 105, 22, 63, -88, -3, 36, -49, 95, -125, 101, 93, 35, -36, -93, -83, -106, 28, 98, -13, 86, 32, -123, 82, -69, -98, -43, 41, 7, 112, -106, -106, 109, 103, 12, 53, 78, 74, -68, -104, 4, -15, 116, 108, 8, -54, 24, 33, 124, 50, -112, 94, 70, 46, 54, -50, 59, -29, -98, 119, 44, 24, 14, -122, 3, -101, 39, -125, -94, -20, 7, -94, -113, -75, -59, 93, -16, 111, 76, 82, -55, -34, 43, -53, -10, -107, 88, 23, 24, 57, -107, 73, 124, -22, -107, 106, -27, 21, -46, 38, 24, -104, -6, 5, 16, 21, 114, -114, 90, -118, -84, -86, 104, -1, -1, -1, -1, -1, -1, -1, -1 };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int SSH_MSG_KEXDH_INIT = 30;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int SSH_MSG_KEXDH_REPLY = 31;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int state;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   DH dh;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   byte[] V_S;
/*     */ 
/*     */ 
/*     */   
/*     */   byte[] V_C;
/*     */ 
/*     */ 
/*     */   
/*     */   byte[] I_S;
/*     */ 
/*     */ 
/*     */   
/*     */   byte[] I_C;
/*     */ 
/*     */ 
/*     */   
/*     */   byte[] e;
/*     */ 
/*     */ 
/*     */   
/*     */   private Buffer buf;
/*     */ 
/*     */ 
/*     */   
/*     */   private Packet packet;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void init(Session session, byte[] V_S, byte[] V_C, byte[] I_S, byte[] I_C) throws Exception {
/*  90 */     this.session = session;
/*  91 */     this.V_S = V_S;
/*  92 */     this.V_C = V_C;
/*  93 */     this.I_S = I_S;
/*  94 */     this.I_C = I_C;
/*     */     
/*     */     try {
/*  97 */       Class c = Class.forName(session.getConfig("sha-1"));
/*  98 */       this.sha = (HASH)c.newInstance();
/*  99 */       this.sha.init();
/*     */     }
/* 101 */     catch (Exception e) {
/* 102 */       System.err.println(e);
/*     */     } 
/*     */     
/* 105 */     this.buf = new Buffer();
/* 106 */     this.packet = new Packet(this.buf);
/*     */     
/*     */     try {
/* 109 */       Class c = Class.forName(session.getConfig("dh"));
/* 110 */       this.dh = (DH)c.newInstance();
/* 111 */       this.dh.init();
/*     */     }
/* 113 */     catch (Exception e) {
/*     */       
/* 115 */       throw e;
/*     */     } 
/*     */     
/* 118 */     this.dh.setP(p);
/* 119 */     this.dh.setG(g);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 125 */     this.e = this.dh.getE();
/* 126 */     this.packet.reset();
/* 127 */     this.buf.putByte((byte)30);
/* 128 */     this.buf.putMPInt(this.e);
/*     */     
/* 130 */     if (V_S == null) {
/*     */       return;
/*     */     }
/*     */     
/* 134 */     session.write(this.packet);
/*     */     
/* 136 */     if (JSch.getLogger().isEnabled(1)) {
/* 137 */       JSch.getLogger().log(1, "SSH_MSG_KEXDH_INIT sent");
/*     */       
/* 139 */       JSch.getLogger().log(1, "expecting SSH_MSG_KEXDH_REPLY");
/*     */     } 
/*     */ 
/*     */     
/* 143 */     this.state = 31; } public boolean next(Buffer _buf) throws Exception { int i; int j;
/*     */     byte[] f;
/*     */     byte[] sig_of_H;
/*     */     byte[] foo;
/*     */     String alg;
/*     */     boolean result;
/* 149 */     switch (this.state) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       case 31:
/* 156 */         j = _buf.getInt();
/* 157 */         j = _buf.getByte();
/* 158 */         j = _buf.getByte();
/* 159 */         if (j != 31) {
/* 160 */           System.err.println("type: must be 31 " + j);
/* 161 */           return false;
/*     */         } 
/*     */         
/* 164 */         this.K_S = _buf.getString();
/*     */         
/* 166 */         f = _buf.getMPInt();
/* 167 */         sig_of_H = _buf.getString();
/*     */         
/* 169 */         this.dh.setF(f);
/*     */         
/* 171 */         this.dh.checkRange();
/*     */         
/* 173 */         this.K = normalize(this.dh.getK());
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
/* 187 */         this.buf.reset();
/* 188 */         this.buf.putString(this.V_C); this.buf.putString(this.V_S);
/* 189 */         this.buf.putString(this.I_C); this.buf.putString(this.I_S);
/* 190 */         this.buf.putString(this.K_S);
/* 191 */         this.buf.putMPInt(this.e); this.buf.putMPInt(f);
/* 192 */         this.buf.putMPInt(this.K);
/* 193 */         foo = new byte[this.buf.getLength()];
/* 194 */         this.buf.getByte(foo);
/* 195 */         this.sha.update(foo, 0, foo.length);
/* 196 */         this.H = this.sha.digest();
/*     */ 
/*     */         
/* 199 */         i = 0;
/* 200 */         j = 0;
/* 201 */         j = this.K_S[i++] << 24 & 0xFF000000 | this.K_S[i++] << 16 & 0xFF0000 | this.K_S[i++] << 8 & 0xFF00 | this.K_S[i++] & 0xFF;
/*     */         
/* 203 */         alg = Util.byte2str(this.K_S, i, j);
/* 204 */         i += j;
/*     */         
/* 206 */         result = verify(alg, this.K_S, i, sig_of_H);
/*     */         
/* 208 */         this.state = 0;
/* 209 */         return result;
/*     */     } 
/* 211 */     return false; }
/*     */   
/*     */   public int getState() {
/* 214 */     return this.state;
/*     */   }
/*     */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\DHG14.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */