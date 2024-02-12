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
/*     */ 
/*     */ public abstract class DHECN
/*     */   extends KeyExchange
/*     */ {
/*     */   private static final int SSH_MSG_KEX_ECDH_INIT = 30;
/*     */   private static final int SSH_MSG_KEX_ECDH_REPLY = 31;
/*     */   private int state;
/*     */   byte[] Q_C;
/*     */   byte[] V_S;
/*     */   byte[] V_C;
/*     */   byte[] I_S;
/*     */   byte[] I_C;
/*     */   byte[] e;
/*     */   private Buffer buf;
/*     */   private Packet packet;
/*     */   private ECDH ecdh;
/*     */   protected String sha_name;
/*     */   protected int key_size;
/*     */   
/*     */   public void init(Session session, byte[] V_S, byte[] V_C, byte[] I_S, byte[] I_C) throws Exception {
/*  57 */     this.session = session;
/*  58 */     this.V_S = V_S;
/*  59 */     this.V_C = V_C;
/*  60 */     this.I_S = I_S;
/*  61 */     this.I_C = I_C;
/*     */     
/*     */     try {
/*  64 */       Class c = Class.forName(session.getConfig(this.sha_name));
/*  65 */       this.sha = (HASH)c.newInstance();
/*  66 */       this.sha.init();
/*     */     }
/*  68 */     catch (Exception e) {
/*  69 */       System.err.println(e);
/*     */     } 
/*     */     
/*  72 */     this.buf = new Buffer();
/*  73 */     this.packet = new Packet(this.buf);
/*     */     
/*  75 */     this.packet.reset();
/*  76 */     this.buf.putByte((byte)30);
/*     */     
/*     */     try {
/*  79 */       Class c = Class.forName(session.getConfig("ecdh-sha2-nistp"));
/*  80 */       this.ecdh = (ECDH)c.newInstance();
/*  81 */       this.ecdh.init(this.key_size);
/*     */       
/*  83 */       this.Q_C = this.ecdh.getQ();
/*  84 */       this.buf.putString(this.Q_C);
/*     */     }
/*  86 */     catch (Exception e) {
/*  87 */       if (e instanceof Throwable)
/*  88 */         throw new JSchException(e.toString(), e); 
/*  89 */       throw new JSchException(e.toString());
/*     */     } 
/*     */     
/*  92 */     if (V_S == null) {
/*     */       return;
/*     */     }
/*     */     
/*  96 */     session.write(this.packet);
/*     */     
/*  98 */     if (JSch.getLogger().isEnabled(1)) {
/*  99 */       JSch.getLogger().log(1, "SSH_MSG_KEX_ECDH_INIT sent");
/*     */       
/* 101 */       JSch.getLogger().log(1, "expecting SSH_MSG_KEX_ECDH_REPLY");
/*     */     } 
/*     */ 
/*     */     
/* 105 */     this.state = 31; } public boolean next(Buffer _buf) throws Exception { int i; int j; byte[] Q_S; byte[][] r_s;
/*     */     byte[] sig_of_H;
/*     */     byte[] foo;
/*     */     String alg;
/*     */     boolean result;
/* 110 */     switch (this.state) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       case 31:
/* 117 */         j = _buf.getInt();
/* 118 */         j = _buf.getByte();
/* 119 */         j = _buf.getByte();
/* 120 */         if (j != 31) {
/* 121 */           System.err.println("type: must be 31 " + j);
/* 122 */           return false;
/*     */         } 
/*     */         
/* 125 */         this.K_S = _buf.getString();
/*     */         
/* 127 */         Q_S = _buf.getString();
/*     */         
/* 129 */         r_s = KeyPairECDSA.fromPoint(Q_S);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 137 */         if (!this.ecdh.validate(r_s[0], r_s[1])) {
/* 138 */           return false;
/*     */         }
/*     */         
/* 141 */         this.K = this.ecdh.getSecret(r_s[0], r_s[1]);
/* 142 */         this.K = normalize(this.K);
/*     */         
/* 144 */         sig_of_H = _buf.getString();
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
/* 159 */         this.buf.reset();
/* 160 */         this.buf.putString(this.V_C); this.buf.putString(this.V_S);
/* 161 */         this.buf.putString(this.I_C); this.buf.putString(this.I_S);
/* 162 */         this.buf.putString(this.K_S);
/* 163 */         this.buf.putString(this.Q_C); this.buf.putString(Q_S);
/* 164 */         this.buf.putMPInt(this.K);
/* 165 */         foo = new byte[this.buf.getLength()];
/* 166 */         this.buf.getByte(foo);
/*     */         
/* 168 */         this.sha.update(foo, 0, foo.length);
/* 169 */         this.H = this.sha.digest();
/*     */         
/* 171 */         i = 0;
/* 172 */         j = 0;
/* 173 */         j = this.K_S[i++] << 24 & 0xFF000000 | this.K_S[i++] << 16 & 0xFF0000 | this.K_S[i++] << 8 & 0xFF00 | this.K_S[i++] & 0xFF;
/*     */         
/* 175 */         alg = Util.byte2str(this.K_S, i, j);
/* 176 */         i += j;
/*     */         
/* 178 */         result = verify(alg, this.K_S, i, sig_of_H);
/*     */         
/* 180 */         this.state = 0;
/* 181 */         return result;
/*     */     } 
/* 183 */     return false; }
/*     */   
/*     */   public int getState() {
/* 186 */     return this.state;
/*     */   }
/*     */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\DHECN.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */