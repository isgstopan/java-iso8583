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
/*     */ public class DHGEX
/*     */   extends KeyExchange
/*     */ {
/*     */   private static final int SSH_MSG_KEX_DH_GEX_GROUP = 31;
/*     */   private static final int SSH_MSG_KEX_DH_GEX_INIT = 32;
/*     */   private static final int SSH_MSG_KEX_DH_GEX_REPLY = 33;
/*     */   private static final int SSH_MSG_KEX_DH_GEX_REQUEST = 34;
/*  39 */   static int min = 1024;
/*  40 */   static int preferred = 1024;
/*  41 */   int max = 1024;
/*     */   
/*     */   private int state;
/*     */   
/*     */   DH dh;
/*     */   
/*     */   byte[] V_S;
/*     */   
/*     */   byte[] V_C;
/*     */   
/*     */   byte[] I_S;
/*     */   
/*     */   byte[] I_C;
/*     */   private Buffer buf;
/*     */   private Packet packet;
/*     */   private byte[] p;
/*     */   private byte[] g;
/*     */   private byte[] e;
/*  59 */   protected String hash = "sha-1";
/*     */ 
/*     */   
/*     */   public void init(Session session, byte[] V_S, byte[] V_C, byte[] I_S, byte[] I_C) throws Exception {
/*  63 */     this.session = session;
/*  64 */     this.V_S = V_S;
/*  65 */     this.V_C = V_C;
/*  66 */     this.I_S = I_S;
/*  67 */     this.I_C = I_C;
/*     */     
/*     */     try {
/*  70 */       Class c = Class.forName(session.getConfig(this.hash));
/*  71 */       this.sha = (HASH)c.newInstance();
/*  72 */       this.sha.init();
/*     */     }
/*  74 */     catch (Exception e) {
/*  75 */       System.err.println(e);
/*     */     } 
/*     */     
/*  78 */     this.buf = new Buffer();
/*  79 */     this.packet = new Packet(this.buf);
/*     */     
/*     */     try {
/*  82 */       Class c = Class.forName(session.getConfig("dh"));
/*     */ 
/*     */       
/*  85 */       preferred = this.max = check2048(c, this.max);
/*  86 */       this.dh = (DH)c.newInstance();
/*  87 */       this.dh.init();
/*     */     }
/*  89 */     catch (Exception e) {
/*  90 */       throw e;
/*     */     } 
/*     */     
/*  93 */     this.packet.reset();
/*  94 */     this.buf.putByte((byte)34);
/*  95 */     this.buf.putInt(min);
/*  96 */     this.buf.putInt(preferred);
/*  97 */     this.buf.putInt(this.max);
/*  98 */     session.write(this.packet);
/*     */     
/* 100 */     if (JSch.getLogger().isEnabled(1)) {
/* 101 */       JSch.getLogger().log(1, "SSH_MSG_KEX_DH_GEX_REQUEST(" + min + "<" + preferred + "<" + this.max + ") sent");
/*     */       
/* 103 */       JSch.getLogger().log(1, "expecting SSH_MSG_KEX_DH_GEX_GROUP");
/*     */     } 
/*     */ 
/*     */     
/* 107 */     this.state = 31; } public boolean next(Buffer _buf) throws Exception { int i; int j; byte[] f;
/*     */     byte[] sig_of_H;
/*     */     byte[] foo;
/*     */     String alg;
/*     */     boolean result;
/* 112 */     switch (this.state) {
/*     */ 
/*     */ 
/*     */       
/*     */       case 31:
/* 117 */         _buf.getInt();
/* 118 */         _buf.getByte();
/* 119 */         j = _buf.getByte();
/* 120 */         if (j != 31) {
/* 121 */           System.err.println("type: must be SSH_MSG_KEX_DH_GEX_GROUP " + j);
/* 122 */           return false;
/*     */         } 
/*     */         
/* 125 */         this.p = _buf.getMPInt();
/* 126 */         this.g = _buf.getMPInt();
/*     */         
/* 128 */         this.dh.setP(this.p);
/* 129 */         this.dh.setG(this.g);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 135 */         this.e = this.dh.getE();
/*     */         
/* 137 */         this.packet.reset();
/* 138 */         this.buf.putByte((byte)32);
/* 139 */         this.buf.putMPInt(this.e);
/* 140 */         this.session.write(this.packet);
/*     */         
/* 142 */         if (JSch.getLogger().isEnabled(1)) {
/* 143 */           JSch.getLogger().log(1, "SSH_MSG_KEX_DH_GEX_INIT sent");
/*     */           
/* 145 */           JSch.getLogger().log(1, "expecting SSH_MSG_KEX_DH_GEX_REPLY");
/*     */         } 
/*     */ 
/*     */         
/* 149 */         this.state = 33;
/* 150 */         return true;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       case 33:
/* 159 */         j = _buf.getInt();
/* 160 */         j = _buf.getByte();
/* 161 */         j = _buf.getByte();
/* 162 */         if (j != 33) {
/* 163 */           System.err.println("type: must be SSH_MSG_KEX_DH_GEX_REPLY " + j);
/* 164 */           return false;
/*     */         } 
/*     */         
/* 167 */         this.K_S = _buf.getString();
/*     */         
/* 169 */         f = _buf.getMPInt();
/* 170 */         sig_of_H = _buf.getString();
/*     */         
/* 172 */         this.dh.setF(f);
/*     */         
/* 174 */         this.dh.checkRange();
/*     */         
/* 176 */         this.K = normalize(this.dh.getK());
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
/* 196 */         this.buf.reset();
/* 197 */         this.buf.putString(this.V_C); this.buf.putString(this.V_S);
/* 198 */         this.buf.putString(this.I_C); this.buf.putString(this.I_S);
/* 199 */         this.buf.putString(this.K_S);
/* 200 */         this.buf.putInt(min); this.buf.putInt(preferred); this.buf.putInt(this.max);
/* 201 */         this.buf.putMPInt(this.p); this.buf.putMPInt(this.g); this.buf.putMPInt(this.e); this.buf.putMPInt(f);
/* 202 */         this.buf.putMPInt(this.K);
/*     */         
/* 204 */         foo = new byte[this.buf.getLength()];
/* 205 */         this.buf.getByte(foo);
/* 206 */         this.sha.update(foo, 0, foo.length);
/*     */         
/* 208 */         this.H = this.sha.digest();
/*     */ 
/*     */ 
/*     */         
/* 212 */         i = 0;
/* 213 */         j = 0;
/* 214 */         j = this.K_S[i++] << 24 & 0xFF000000 | this.K_S[i++] << 16 & 0xFF0000 | this.K_S[i++] << 8 & 0xFF00 | this.K_S[i++] & 0xFF;
/*     */         
/* 216 */         alg = Util.byte2str(this.K_S, i, j);
/* 217 */         i += j;
/*     */         
/* 219 */         result = verify(alg, this.K_S, i, sig_of_H);
/*     */         
/* 221 */         this.state = 0;
/* 222 */         return result;
/*     */     } 
/* 224 */     return false; }
/*     */   
/*     */   public int getState() {
/* 227 */     return this.state;
/*     */   }
/*     */   protected int check2048(Class c, int _max) throws Exception {
/* 230 */     DH dh = c.newInstance();
/* 231 */     dh.init();
/* 232 */     byte[] foo = new byte[257];
/* 233 */     foo[1] = -35;
/* 234 */     foo[256] = 115;
/* 235 */     dh.setP(foo);
/* 236 */     byte[] bar = { 2 };
/* 237 */     dh.setG(bar);
/*     */     try {
/* 239 */       dh.getE();
/* 240 */       _max = 2048;
/*     */     }
/* 242 */     catch (Exception e) {}
/* 243 */     return _max;
/*     */   }
/*     */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\DHGEX.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */