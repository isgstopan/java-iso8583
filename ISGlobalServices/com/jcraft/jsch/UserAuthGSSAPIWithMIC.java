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
/*     */ public class UserAuthGSSAPIWithMIC
/*     */   extends UserAuth
/*     */ {
/*     */   private static final int SSH_MSG_USERAUTH_GSSAPI_RESPONSE = 60;
/*     */   private static final int SSH_MSG_USERAUTH_GSSAPI_TOKEN = 61;
/*     */   private static final int SSH_MSG_USERAUTH_GSSAPI_EXCHANGE_COMPLETE = 63;
/*     */   private static final int SSH_MSG_USERAUTH_GSSAPI_ERROR = 64;
/*     */   private static final int SSH_MSG_USERAUTH_GSSAPI_ERRTOK = 65;
/*     */   private static final int SSH_MSG_USERAUTH_GSSAPI_MIC = 66;
/*  40 */   private static final byte[][] supported_oid = new byte[][] { { 6, 9, 42, -122, 72, -122, -9, 18, 1, 2, 2 } };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  47 */   private static final String[] supported_method = new String[] { "gssapi-with-mic.krb5" };
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean start(Session session) throws Exception {
/*  52 */     super.start(session);
/*     */     
/*  54 */     byte[] _username = Util.str2byte(this.username);
/*     */     
/*  56 */     this.packet.reset();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  64 */     this.buf.putByte((byte)50);
/*  65 */     this.buf.putString(_username);
/*  66 */     this.buf.putString(Util.str2byte("ssh-connection"));
/*  67 */     this.buf.putString(Util.str2byte("gssapi-with-mic"));
/*  68 */     this.buf.putInt(supported_oid.length);
/*  69 */     for (int i = 0; i < supported_oid.length; i++) {
/*  70 */       this.buf.putString(supported_oid[i]);
/*     */     }
/*  72 */     session.write(this.packet);
/*     */     
/*  74 */     String method = null;
/*     */     
/*     */     while (true) {
/*  77 */       this.buf = session.read(this.buf);
/*  78 */       int command = this.buf.getCommand() & 0xFF;
/*     */       
/*  80 */       if (command == 51) {
/*  81 */         return false;
/*     */       }
/*     */       
/*  84 */       if (command == 60) {
/*  85 */         this.buf.getInt(); this.buf.getByte(); this.buf.getByte();
/*  86 */         byte[] message = this.buf.getString();
/*     */         
/*  88 */         for (int k = 0; k < supported_oid.length; k++) {
/*  89 */           if (Util.array_equals(message, supported_oid[k])) {
/*  90 */             method = supported_method[k];
/*     */             
/*     */             break;
/*     */           } 
/*     */         } 
/*  95 */         if (method == null) {
/*  96 */           return false;
/*     */         }
/*     */         
/*     */         break;
/*     */       } 
/*     */       
/* 102 */       if (command == 53) {
/* 103 */         this.buf.getInt(); this.buf.getByte(); this.buf.getByte();
/* 104 */         byte[] _message = this.buf.getString();
/* 105 */         byte[] lang = this.buf.getString();
/* 106 */         String message = Util.byte2str(_message);
/* 107 */         if (this.userinfo != null) {
/* 108 */           this.userinfo.showMessage(message);
/*     */         }
/*     */         continue;
/*     */       } 
/* 112 */       return false;
/*     */     } 
/*     */     
/* 115 */     GSSContext context = null;
/*     */     try {
/* 117 */       Class c = Class.forName(session.getConfig(method));
/* 118 */       context = (GSSContext)c.newInstance();
/*     */     }
/* 120 */     catch (Exception e) {
/* 121 */       return false;
/*     */     } 
/*     */     
/*     */     try {
/* 125 */       context.create(this.username, session.host);
/*     */     }
/* 127 */     catch (JSchException e) {
/* 128 */       return false;
/*     */     } 
/*     */     
/* 131 */     byte[] token = new byte[0];
/*     */     
/* 133 */     while (!context.isEstablished()) {
/*     */       try {
/* 135 */         token = context.init(token, 0, token.length);
/*     */       }
/* 137 */       catch (JSchException e) {
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 142 */         return false;
/*     */       } 
/*     */       
/* 145 */       if (token != null) {
/* 146 */         this.packet.reset();
/* 147 */         this.buf.putByte((byte)61);
/* 148 */         this.buf.putString(token);
/* 149 */         session.write(this.packet);
/*     */       } 
/*     */       
/* 152 */       if (!context.isEstablished()) {
/* 153 */         this.buf = session.read(this.buf);
/* 154 */         int k = this.buf.getCommand() & 0xFF;
/* 155 */         if (k == 64) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 161 */           this.buf = session.read(this.buf);
/* 162 */           k = this.buf.getCommand() & 0xFF;
/*     */         
/*     */         }
/* 165 */         else if (k == 65) {
/*     */ 
/*     */           
/* 168 */           this.buf = session.read(this.buf);
/* 169 */           k = this.buf.getCommand() & 0xFF;
/*     */         } 
/*     */ 
/*     */         
/* 173 */         if (k == 51) {
/* 174 */           return false;
/*     */         }
/*     */         
/* 177 */         this.buf.getInt(); this.buf.getByte(); this.buf.getByte();
/* 178 */         token = this.buf.getString();
/*     */       } 
/*     */     } 
/*     */     
/* 182 */     Buffer mbuf = new Buffer();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 188 */     mbuf.putString(session.getSessionId());
/* 189 */     mbuf.putByte((byte)50);
/* 190 */     mbuf.putString(_username);
/* 191 */     mbuf.putString(Util.str2byte("ssh-connection"));
/* 192 */     mbuf.putString(Util.str2byte("gssapi-with-mic"));
/*     */     
/* 194 */     byte[] mic = context.getMIC(mbuf.buffer, 0, mbuf.getLength());
/*     */     
/* 196 */     if (mic == null) {
/* 197 */       return false;
/*     */     }
/*     */     
/* 200 */     this.packet.reset();
/* 201 */     this.buf.putByte((byte)66);
/* 202 */     this.buf.putString(mic);
/* 203 */     session.write(this.packet);
/*     */     
/* 205 */     context.dispose();
/*     */     
/* 207 */     this.buf = session.read(this.buf);
/* 208 */     int j = this.buf.getCommand() & 0xFF;
/*     */     
/* 210 */     if (j == 52) {
/* 211 */       return true;
/*     */     }
/* 213 */     if (j == 51) {
/* 214 */       this.buf.getInt(); this.buf.getByte(); this.buf.getByte();
/* 215 */       byte[] foo = this.buf.getString();
/* 216 */       int partial_success = this.buf.getByte();
/*     */ 
/*     */       
/* 219 */       if (partial_success != 0) {
/* 220 */         throw new JSchPartialAuthException(Util.byte2str(foo));
/*     */       }
/*     */     } 
/* 223 */     return false;
/*     */   }
/*     */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\UserAuthGSSAPIWithMIC.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */