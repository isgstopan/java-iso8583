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
/*     */ public class DHG1
/*     */   extends KeyExchange
/*     */ {
/*  34 */   static final byte[] g = new byte[] { 2 };
/*  35 */   static final byte[] p = new byte[] { 0, -1, -1, -1, -1, -1, -1, -1, -1, -55, 15, -38, -94, 33, 104, -62, 52, -60, -58, 98, -117, Byte.MIN_VALUE, -36, 28, -47, 41, 2, 78, 8, -118, 103, -52, 116, 2, 11, -66, -90, 59, 19, -101, 34, 81, 74, 8, 121, -114, 52, 4, -35, -17, -107, 25, -77, -51, 58, 67, 27, 48, 43, 10, 109, -14, 95, 20, 55, 79, -31, 53, 109, 109, 81, -62, 69, -28, -123, -75, 118, 98, 94, 126, -58, -12, 76, 66, -23, -90, 55, -19, 107, 11, -1, 92, -74, -12, 6, -73, -19, -18, 56, 107, -5, 90, -119, -97, -91, -82, -97, 36, 17, 124, 75, 31, -26, 73, 40, 102, 81, -20, -26, 83, -127, -1, -1, -1, -1, -1, -1, -1, -1 };
/*     */ 
/*     */ 
/*     */   
/*     */   private static final int SSH_MSG_KEXDH_INIT = 30;
/*     */ 
/*     */   
/*     */   private static final int SSH_MSG_KEXDH_REPLY = 31;
/*     */ 
/*     */   
/*     */   private int state;
/*     */ 
/*     */   
/*     */   DH dh;
/*     */ 
/*     */   
/*     */   byte[] V_S;
/*     */ 
/*     */   
/*     */   byte[] V_C;
/*     */ 
/*     */   
/*     */   byte[] I_S;
/*     */ 
/*     */   
/*     */   byte[] I_C;
/*     */ 
/*     */   
/*     */   byte[] e;
/*     */ 
/*     */   
/*     */   private Buffer buf;
/*     */ 
/*     */   
/*     */   private Packet packet;
/*     */ 
/*     */ 
/*     */   
/*     */   public void init(Session session, byte[] V_S, byte[] V_C, byte[] I_S, byte[] I_C) throws Exception {
/*  74 */     this.session = session;
/*  75 */     this.V_S = V_S;
/*  76 */     this.V_C = V_C;
/*  77 */     this.I_S = I_S;
/*  78 */     this.I_C = I_C;
/*     */     
/*     */     try {
/*  81 */       Class c = Class.forName(session.getConfig("sha-1"));
/*  82 */       this.sha = (HASH)c.newInstance();
/*  83 */       this.sha.init();
/*     */     }
/*  85 */     catch (Exception e) {
/*  86 */       System.err.println(e);
/*     */     } 
/*     */     
/*  89 */     this.buf = new Buffer();
/*  90 */     this.packet = new Packet(this.buf);
/*     */     
/*     */     try {
/*  93 */       Class c = Class.forName(session.getConfig("dh"));
/*  94 */       this.dh = (DH)c.newInstance();
/*  95 */       this.dh.init();
/*     */     }
/*  97 */     catch (Exception e) {
/*     */       
/*  99 */       throw e;
/*     */     } 
/*     */     
/* 102 */     this.dh.setP(p);
/* 103 */     this.dh.setG(g);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 110 */     this.e = this.dh.getE();
/*     */     
/* 112 */     this.packet.reset();
/* 113 */     this.buf.putByte((byte)30);
/* 114 */     this.buf.putMPInt(this.e);
/* 115 */     session.write(this.packet);
/*     */     
/* 117 */     if (JSch.getLogger().isEnabled(1)) {
/* 118 */       JSch.getLogger().log(1, "SSH_MSG_KEXDH_INIT sent");
/*     */       
/* 120 */       JSch.getLogger().log(1, "expecting SSH_MSG_KEXDH_REPLY");
/*     */     } 
/*     */ 
/*     */     
/* 124 */     this.state = 31; } public boolean next(Buffer _buf) throws Exception { int i; int j;
/*     */     byte[] f;
/*     */     byte[] sig_of_H;
/*     */     byte[] foo;
/*     */     String alg;
/*     */     boolean result;
/* 130 */     switch (this.state) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       case 31:
/* 137 */         j = _buf.getInt();
/* 138 */         j = _buf.getByte();
/* 139 */         j = _buf.getByte();
/* 140 */         if (j != 31) {
/* 141 */           System.err.println("type: must be 31 " + j);
/* 142 */           return false;
/*     */         } 
/*     */         
/* 145 */         this.K_S = _buf.getString();
/*     */         
/* 147 */         f = _buf.getMPInt();
/* 148 */         sig_of_H = _buf.getString();
/*     */         
/* 150 */         this.dh.setF(f);
/*     */         
/* 152 */         this.dh.checkRange();
/*     */         
/* 154 */         this.K = normalize(this.dh.getK());
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
/* 168 */         this.buf.reset();
/* 169 */         this.buf.putString(this.V_C); this.buf.putString(this.V_S);
/* 170 */         this.buf.putString(this.I_C); this.buf.putString(this.I_S);
/* 171 */         this.buf.putString(this.K_S);
/* 172 */         this.buf.putMPInt(this.e); this.buf.putMPInt(f);
/* 173 */         this.buf.putMPInt(this.K);
/* 174 */         foo = new byte[this.buf.getLength()];
/* 175 */         this.buf.getByte(foo);
/* 176 */         this.sha.update(foo, 0, foo.length);
/* 177 */         this.H = this.sha.digest();
/*     */ 
/*     */         
/* 180 */         i = 0;
/* 181 */         j = 0;
/* 182 */         j = this.K_S[i++] << 24 & 0xFF000000 | this.K_S[i++] << 16 & 0xFF0000 | this.K_S[i++] << 8 & 0xFF00 | this.K_S[i++] & 0xFF;
/*     */         
/* 184 */         alg = Util.byte2str(this.K_S, i, j);
/* 185 */         i += j;
/*     */         
/* 187 */         result = verify(alg, this.K_S, i, sig_of_H);
/*     */         
/* 189 */         this.state = 0;
/* 190 */         return result;
/*     */     } 
/* 192 */     return false; }
/*     */   
/*     */   public int getState() {
/* 195 */     return this.state;
/*     */   }
/*     */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\DHG1.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */