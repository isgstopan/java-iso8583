/*     */ package com.jcraft.jsch;
/*     */ 
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
/*     */ class UserAuthPublicKey
/*     */   extends UserAuth
/*     */ {
/*     */   public boolean start(Session session) throws Exception {
/*  37 */     super.start(session);
/*     */     
/*  39 */     Vector identities = session.getIdentityRepository().getIdentities();
/*     */     
/*  41 */     byte[] passphrase = null;
/*  42 */     byte[] _username = null;
/*     */ 
/*     */ 
/*     */     
/*  46 */     synchronized (identities) {
/*  47 */       if (identities.size() <= 0) {
/*  48 */         return false;
/*     */       }
/*     */       
/*  51 */       _username = Util.str2byte(this.username);
/*     */       
/*  53 */       for (int i = 0; i < identities.size(); i++) {
/*     */         
/*  55 */         if (session.auth_failures >= session.max_auth_tries) {
/*  56 */           return false;
/*     */         }
/*     */         
/*  59 */         Identity identity = identities.elementAt(i);
/*  60 */         byte[] pubkeyblob = identity.getPublicKeyBlob();
/*     */         
/*  62 */         if (pubkeyblob != null) {
/*     */           int command;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*  71 */           this.packet.reset();
/*  72 */           this.buf.putByte((byte)50);
/*  73 */           this.buf.putString(_username);
/*  74 */           this.buf.putString(Util.str2byte("ssh-connection"));
/*  75 */           this.buf.putString(Util.str2byte("publickey"));
/*  76 */           this.buf.putByte((byte)0);
/*  77 */           this.buf.putString(Util.str2byte(identity.getAlgName()));
/*  78 */           this.buf.putString(pubkeyblob);
/*  79 */           session.write(this.packet);
/*     */ 
/*     */           
/*     */           while (true) {
/*  83 */             this.buf = session.read(this.buf);
/*  84 */             command = this.buf.getCommand() & 0xFF;
/*     */             
/*  86 */             if (command == 60) {
/*     */               break;
/*     */             }
/*  89 */             if (command == 51) {
/*     */               break;
/*     */             }
/*  92 */             if (command == 53) {
/*  93 */               this.buf.getInt(); this.buf.getByte(); this.buf.getByte();
/*  94 */               byte[] _message = this.buf.getString();
/*  95 */               byte[] lang = this.buf.getString();
/*  96 */               String message = Util.byte2str(_message);
/*  97 */               if (this.userinfo != null) {
/*  98 */                 this.userinfo.showMessage(message);
/*     */               }
/*     */ 
/*     */               
/*     */               continue;
/*     */             } 
/*     */ 
/*     */             
/*     */             break;
/*     */           } 
/*     */           
/* 109 */           if (command != 60) {
/*     */             continue;
/*     */           }
/*     */         } 
/*     */ 
/*     */ 
/*     */         
/* 116 */         int count = 5;
/*     */         do {
/* 118 */           if (identity.isEncrypted() && passphrase == null) {
/* 119 */             if (this.userinfo == null) throw new JSchException("USERAUTH fail"); 
/* 120 */             if (identity.isEncrypted() && !this.userinfo.promptPassphrase("Passphrase for " + identity.getName()))
/*     */             {
/* 122 */               throw new JSchAuthCancelException("publickey");
/*     */             }
/*     */ 
/*     */             
/* 126 */             String _passphrase = this.userinfo.getPassphrase();
/* 127 */             if (_passphrase != null) {
/* 128 */               passphrase = Util.str2byte(_passphrase);
/*     */             }
/*     */           } 
/*     */           
/* 132 */           if ((!identity.isEncrypted() || passphrase != null) && 
/* 133 */             identity.setPassphrase(passphrase)) {
/* 134 */             if (passphrase != null && session.getIdentityRepository() instanceof IdentityRepository.Wrapper)
/*     */             {
/* 136 */               ((IdentityRepository.Wrapper)session.getIdentityRepository()).check();
/*     */             }
/*     */             
/*     */             break;
/*     */           } 
/* 141 */           Util.bzero(passphrase);
/* 142 */           passphrase = null;
/* 143 */           --count;
/* 144 */         } while (count != 0);
/*     */ 
/*     */         
/* 147 */         Util.bzero(passphrase);
/* 148 */         passphrase = null;
/*     */ 
/*     */         
/* 151 */         if (!identity.isEncrypted()) {
/* 152 */           if (pubkeyblob == null) pubkeyblob = identity.getPublicKeyBlob();
/*     */ 
/*     */ 
/*     */           
/* 156 */           if (pubkeyblob != null) {
/*     */             int command;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 167 */             this.packet.reset();
/* 168 */             this.buf.putByte((byte)50);
/* 169 */             this.buf.putString(_username);
/* 170 */             this.buf.putString(Util.str2byte("ssh-connection"));
/* 171 */             this.buf.putString(Util.str2byte("publickey"));
/* 172 */             this.buf.putByte((byte)1);
/* 173 */             this.buf.putString(Util.str2byte(identity.getAlgName()));
/* 174 */             this.buf.putString(pubkeyblob);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 180 */             byte[] sid = session.getSessionId();
/* 181 */             int sidlen = sid.length;
/* 182 */             byte[] tmp = new byte[4 + sidlen + this.buf.index - 5];
/* 183 */             tmp[0] = (byte)(sidlen >>> 24);
/* 184 */             tmp[1] = (byte)(sidlen >>> 16);
/* 185 */             tmp[2] = (byte)(sidlen >>> 8);
/* 186 */             tmp[3] = (byte)sidlen;
/* 187 */             System.arraycopy(sid, 0, tmp, 4, sidlen);
/* 188 */             System.arraycopy(this.buf.buffer, 5, tmp, 4 + sidlen, this.buf.index - 5);
/* 189 */             byte[] signature = identity.getSignature(tmp);
/* 190 */             if (signature == null) {
/*     */               break;
/*     */             }
/* 193 */             this.buf.putString(signature);
/* 194 */             session.write(this.packet);
/*     */ 
/*     */             
/*     */             while (true) {
/* 198 */               this.buf = session.read(this.buf);
/* 199 */               command = this.buf.getCommand() & 0xFF;
/*     */               
/* 201 */               if (command == 52) {
/* 202 */                 return true;
/*     */               }
/* 204 */               if (command == 53) {
/* 205 */                 this.buf.getInt(); this.buf.getByte(); this.buf.getByte();
/* 206 */                 byte[] _message = this.buf.getString();
/* 207 */                 byte[] lang = this.buf.getString();
/* 208 */                 String message = Util.byte2str(_message);
/* 209 */                 if (this.userinfo != null)
/* 210 */                   this.userinfo.showMessage(message);  continue;
/*     */               } 
/*     */               break;
/*     */             } 
/* 214 */             if (command == 51) {
/* 215 */               this.buf.getInt(); this.buf.getByte(); this.buf.getByte();
/* 216 */               byte[] foo = this.buf.getString();
/* 217 */               int partial_success = this.buf.getByte();
/*     */ 
/*     */               
/* 220 */               if (partial_success != 0) {
/* 221 */                 throw new JSchPartialAuthException(Util.byte2str(foo));
/*     */               }
/* 223 */               session.auth_failures++;
/*     */             } 
/*     */           } 
/*     */         } 
/*     */         
/*     */         continue;
/*     */       } 
/*     */     } 
/*     */     
/* 232 */     return false;
/*     */   }
/*     */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\UserAuthPublicKey.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */