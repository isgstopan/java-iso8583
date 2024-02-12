/*     */ package com.jcraft.jsch;
/*     */ 
/*     */ import java.io.IOException;
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
/*     */ 
/*     */ 
/*     */ class ChannelAgentForwarding
/*     */   extends Channel
/*     */ {
/*     */   private static final int LOCAL_WINDOW_SIZE_MAX = 131072;
/*     */   private static final int LOCAL_MAXIMUM_PACKET_SIZE = 16384;
/*  40 */   private final byte SSH_AGENTC_REQUEST_RSA_IDENTITIES = 1;
/*  41 */   private final byte SSH_AGENT_RSA_IDENTITIES_ANSWER = 2;
/*  42 */   private final byte SSH_AGENTC_RSA_CHALLENGE = 3;
/*  43 */   private final byte SSH_AGENT_RSA_RESPONSE = 4;
/*  44 */   private final byte SSH_AGENT_FAILURE = 5;
/*  45 */   private final byte SSH_AGENT_SUCCESS = 6;
/*  46 */   private final byte SSH_AGENTC_ADD_RSA_IDENTITY = 7;
/*  47 */   private final byte SSH_AGENTC_REMOVE_RSA_IDENTITY = 8;
/*  48 */   private final byte SSH_AGENTC_REMOVE_ALL_RSA_IDENTITIES = 9;
/*     */   
/*  50 */   private final byte SSH2_AGENTC_REQUEST_IDENTITIES = 11;
/*  51 */   private final byte SSH2_AGENT_IDENTITIES_ANSWER = 12;
/*  52 */   private final byte SSH2_AGENTC_SIGN_REQUEST = 13;
/*  53 */   private final byte SSH2_AGENT_SIGN_RESPONSE = 14;
/*  54 */   private final byte SSH2_AGENTC_ADD_IDENTITY = 17;
/*  55 */   private final byte SSH2_AGENTC_REMOVE_IDENTITY = 18;
/*  56 */   private final byte SSH2_AGENTC_REMOVE_ALL_IDENTITIES = 19;
/*  57 */   private final byte SSH2_AGENT_FAILURE = 30;
/*     */   
/*     */   boolean init = true;
/*     */   
/*  61 */   private Buffer rbuf = null;
/*  62 */   private Buffer wbuf = null;
/*  63 */   private Packet packet = null;
/*  64 */   private Buffer mbuf = null;
/*     */ 
/*     */ 
/*     */   
/*     */   ChannelAgentForwarding() {
/*  69 */     setLocalWindowSizeMax(131072);
/*  70 */     setLocalWindowSize(131072);
/*  71 */     setLocalPacketSize(16384);
/*     */     
/*  73 */     this.type = Util.str2byte("auth-agent@openssh.com");
/*  74 */     this.rbuf = new Buffer();
/*  75 */     this.rbuf.reset();
/*     */ 
/*     */     
/*  78 */     this.mbuf = new Buffer();
/*  79 */     this.connected = true;
/*     */   }
/*     */   
/*     */   public void run() {
/*     */     try {
/*  84 */       sendOpenConfirmation();
/*     */     }
/*  86 */     catch (Exception e) {
/*  87 */       this.close = true;
/*  88 */       disconnect();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   void write(byte[] foo, int s, int l) throws IOException {
/*  94 */     if (this.packet == null) {
/*  95 */       this.wbuf = new Buffer(this.rmpsize);
/*  96 */       this.packet = new Packet(this.wbuf);
/*     */     } 
/*     */     
/*  99 */     this.rbuf.shift();
/* 100 */     if (this.rbuf.buffer.length < this.rbuf.index + l) {
/* 101 */       byte[] newbuf = new byte[this.rbuf.s + l];
/* 102 */       System.arraycopy(this.rbuf.buffer, 0, newbuf, 0, this.rbuf.buffer.length);
/* 103 */       this.rbuf.buffer = newbuf;
/*     */     } 
/*     */     
/* 106 */     this.rbuf.putByte(foo, s, l);
/*     */     
/* 108 */     int mlen = this.rbuf.getInt();
/* 109 */     if (mlen > this.rbuf.getLength()) {
/* 110 */       this.rbuf.s -= 4;
/*     */       
/*     */       return;
/*     */     } 
/* 114 */     int typ = this.rbuf.getByte();
/*     */     
/* 116 */     Session _session = null;
/*     */     try {
/* 118 */       _session = getSession();
/*     */     }
/* 120 */     catch (JSchException e) {
/* 121 */       throw new IOException(e.toString());
/*     */     } 
/*     */     
/* 124 */     IdentityRepository irepo = _session.getIdentityRepository();
/* 125 */     UserInfo userinfo = _session.getUserInfo();
/*     */     
/* 127 */     this.mbuf.reset();
/*     */     
/* 129 */     if (typ == 11) {
/* 130 */       this.mbuf.putByte((byte)12);
/* 131 */       Vector identities = irepo.getIdentities();
/* 132 */       synchronized (identities) {
/* 133 */         int count = 0; int i;
/* 134 */         for (i = 0; i < identities.size(); i++) {
/* 135 */           Identity identity = identities.elementAt(i);
/* 136 */           if (identity.getPublicKeyBlob() != null)
/* 137 */             count++; 
/*     */         } 
/* 139 */         this.mbuf.putInt(count);
/* 140 */         for (i = 0; i < identities.size(); i++) {
/* 141 */           Identity identity = identities.elementAt(i);
/* 142 */           byte[] pubkeyblob = identity.getPublicKeyBlob();
/* 143 */           if (pubkeyblob != null) {
/*     */             
/* 145 */             this.mbuf.putString(pubkeyblob);
/* 146 */             this.mbuf.putString(Util.empty);
/*     */           } 
/*     */         } 
/*     */       } 
/* 150 */     } else if (typ == 1) {
/* 151 */       this.mbuf.putByte((byte)2);
/* 152 */       this.mbuf.putInt(0);
/*     */     }
/* 154 */     else if (typ == 13) {
/* 155 */       byte[] blob = this.rbuf.getString();
/* 156 */       byte[] data = this.rbuf.getString();
/* 157 */       int flags = this.rbuf.getInt();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 163 */       Vector identities = irepo.getIdentities();
/* 164 */       Identity identity = null;
/* 165 */       synchronized (identities) {
/* 166 */         for (int i = 0; i < identities.size(); i++) {
/* 167 */           Identity _identity = identities.elementAt(i);
/* 168 */           if (_identity.getPublicKeyBlob() == null)
/*     */             continue; 
/* 170 */           if (!Util.array_equals(blob, _identity.getPublicKeyBlob())) {
/*     */             continue;
/*     */           }
/* 173 */           if (_identity.isEncrypted()) {
/* 174 */             if (userinfo == null)
/*     */               continue; 
/* 176 */             while (_identity.isEncrypted() && 
/* 177 */               userinfo.promptPassphrase("Passphrase for " + _identity.getName())) {
/*     */ 
/*     */ 
/*     */               
/* 181 */               String _passphrase = userinfo.getPassphrase();
/* 182 */               if (_passphrase == null) {
/*     */                 break;
/*     */               }
/*     */               
/* 186 */               byte[] passphrase = Util.str2byte(_passphrase);
/*     */               try {
/* 188 */                 if (_identity.setPassphrase(passphrase)) {
/*     */                   break;
/*     */                 }
/*     */               }
/* 192 */               catch (JSchException e) {
/*     */                 break;
/*     */               } 
/*     */             } 
/*     */           } 
/*     */           
/* 198 */           if (!_identity.isEncrypted()) {
/* 199 */             identity = _identity;
/*     */             break;
/*     */           } 
/*     */           continue;
/*     */         } 
/*     */       } 
/* 205 */       byte[] signature = null;
/*     */       
/* 207 */       if (identity != null) {
/* 208 */         signature = identity.getSignature(data);
/*     */       }
/*     */       
/* 211 */       if (signature == null) {
/* 212 */         this.mbuf.putByte((byte)30);
/*     */       } else {
/*     */         
/* 215 */         this.mbuf.putByte((byte)14);
/* 216 */         this.mbuf.putString(signature);
/*     */       }
/*     */     
/* 219 */     } else if (typ == 18) {
/* 220 */       byte[] blob = this.rbuf.getString();
/* 221 */       irepo.remove(blob);
/* 222 */       this.mbuf.putByte((byte)6);
/*     */     }
/* 224 */     else if (typ == 9) {
/* 225 */       this.mbuf.putByte((byte)6);
/*     */     }
/* 227 */     else if (typ == 19) {
/* 228 */       irepo.removeAll();
/* 229 */       this.mbuf.putByte((byte)6);
/*     */     }
/* 231 */     else if (typ == 17) {
/* 232 */       int fooo = this.rbuf.getLength();
/* 233 */       byte[] tmp = new byte[fooo];
/* 234 */       this.rbuf.getByte(tmp);
/* 235 */       boolean result = irepo.add(tmp);
/* 236 */       this.mbuf.putByte(result ? 6 : 5);
/*     */     } else {
/*     */       
/* 239 */       this.rbuf.skip(this.rbuf.getLength() - 1);
/* 240 */       this.mbuf.putByte((byte)5);
/*     */     } 
/*     */     
/* 243 */     byte[] response = new byte[this.mbuf.getLength()];
/* 244 */     this.mbuf.getByte(response);
/* 245 */     send(response);
/*     */   }
/*     */   
/*     */   private void send(byte[] message) {
/* 249 */     this.packet.reset();
/* 250 */     this.wbuf.putByte((byte)94);
/* 251 */     this.wbuf.putInt(this.recipient);
/* 252 */     this.wbuf.putInt(4 + message.length);
/* 253 */     this.wbuf.putString(message);
/*     */     
/*     */     try {
/* 256 */       getSession().write(this.packet, this, 4 + message.length);
/*     */     }
/* 258 */     catch (Exception e) {}
/*     */   }
/*     */ 
/*     */   
/*     */   void eof_remote() {
/* 263 */     super.eof_remote();
/* 264 */     eof();
/*     */   }
/*     */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\ChannelAgentForwarding.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */