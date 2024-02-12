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
/*     */ class UserAuthNone
/*     */   extends UserAuth
/*     */ {
/*     */   private static final int SSH_MSG_SERVICE_ACCEPT = 6;
/*  34 */   private String methods = null;
/*     */   
/*     */   public boolean start(Session session) throws Exception {
/*  37 */     super.start(session);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  43 */     this.packet.reset();
/*  44 */     this.buf.putByte((byte)5);
/*  45 */     this.buf.putString(Util.str2byte("ssh-userauth"));
/*  46 */     session.write(this.packet);
/*     */     
/*  48 */     if (JSch.getLogger().isEnabled(1)) {
/*  49 */       JSch.getLogger().log(1, "SSH_MSG_SERVICE_REQUEST sent");
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  56 */     this.buf = session.read(this.buf);
/*  57 */     int command = this.buf.getCommand();
/*     */     
/*  59 */     boolean result = (command == 6);
/*     */     
/*  61 */     if (JSch.getLogger().isEnabled(1)) {
/*  62 */       JSch.getLogger().log(1, "SSH_MSG_SERVICE_ACCEPT received");
/*     */     }
/*     */     
/*  65 */     if (!result) {
/*  66 */       return false;
/*     */     }
/*  68 */     byte[] _username = null;
/*  69 */     _username = Util.str2byte(this.username);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  76 */     this.packet.reset();
/*  77 */     this.buf.putByte((byte)50);
/*  78 */     this.buf.putString(_username);
/*  79 */     this.buf.putString(Util.str2byte("ssh-connection"));
/*  80 */     this.buf.putString(Util.str2byte("none"));
/*  81 */     session.write(this.packet);
/*     */ 
/*     */     
/*     */     while (true) {
/*  85 */       this.buf = session.read(this.buf);
/*  86 */       command = this.buf.getCommand() & 0xFF;
/*     */       
/*  88 */       if (command == 52) {
/*  89 */         return true;
/*     */       }
/*  91 */       if (command == 53) {
/*  92 */         this.buf.getInt(); this.buf.getByte(); this.buf.getByte();
/*  93 */         byte[] _message = this.buf.getString();
/*  94 */         byte[] lang = this.buf.getString();
/*  95 */         String message = Util.byte2str(_message);
/*  96 */         if (this.userinfo != null)
/*     */           try {
/*  98 */             this.userinfo.showMessage(message);
/*     */           }
/* 100 */           catch (RuntimeException ee) {} 
/*     */         continue;
/*     */       } 
/*     */       break;
/*     */     } 
/* 105 */     if (command == 51) {
/* 106 */       this.buf.getInt(); this.buf.getByte(); this.buf.getByte();
/* 107 */       byte[] foo = this.buf.getString();
/* 108 */       int partial_success = this.buf.getByte();
/* 109 */       this.methods = Util.byte2str(foo);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     }
/*     */     else {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 120 */       throw new JSchException("USERAUTH fail (" + command + ")");
/*     */     } 
/*     */ 
/*     */     
/* 124 */     return false;
/*     */   }
/*     */   String getMethods() {
/* 127 */     return this.methods;
/*     */   }
/*     */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\UserAuthNone.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */