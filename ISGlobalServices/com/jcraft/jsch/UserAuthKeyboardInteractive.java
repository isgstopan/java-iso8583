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
/*     */ class UserAuthKeyboardInteractive
/*     */   extends UserAuth
/*     */ {
/*     */   public boolean start(Session session) throws Exception {
/*  34 */     super.start(session);
/*     */     
/*  36 */     if (this.userinfo != null && !(this.userinfo instanceof UIKeyboardInteractive)) {
/*  37 */       return false;
/*     */     }
/*     */     
/*  40 */     String dest = this.username + "@" + session.host;
/*  41 */     if (session.port != 22) {
/*  42 */       dest = dest + ":" + session.port;
/*     */     }
/*  44 */     byte[] password = session.password;
/*     */     
/*  46 */     boolean cancel = false;
/*     */     
/*  48 */     byte[] _username = null;
/*  49 */     _username = Util.str2byte(this.username);
/*     */ 
/*     */     
/*     */     while (true) {
/*  53 */       if (session.auth_failures >= session.max_auth_tries) {
/*  54 */         return false;
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  64 */       this.packet.reset();
/*  65 */       this.buf.putByte((byte)50);
/*  66 */       this.buf.putString(_username);
/*  67 */       this.buf.putString(Util.str2byte("ssh-connection"));
/*     */       
/*  69 */       this.buf.putString(Util.str2byte("keyboard-interactive"));
/*  70 */       this.buf.putString(Util.empty);
/*  71 */       this.buf.putString(Util.empty);
/*  72 */       session.write(this.packet);
/*     */       
/*  74 */       boolean firsttime = true;
/*     */       
/*     */       while (true) {
/*  77 */         this.buf = session.read(this.buf);
/*  78 */         int command = this.buf.getCommand() & 0xFF;
/*     */         
/*  80 */         if (command == 52) {
/*  81 */           return true;
/*     */         }
/*  83 */         if (command == 53) {
/*  84 */           this.buf.getInt(); this.buf.getByte(); this.buf.getByte();
/*  85 */           byte[] _message = this.buf.getString();
/*  86 */           byte[] lang = this.buf.getString();
/*  87 */           String message = Util.byte2str(_message);
/*  88 */           if (this.userinfo != null) {
/*  89 */             this.userinfo.showMessage(message);
/*     */           }
/*     */           continue;
/*     */         } 
/*  93 */         if (command == 51) {
/*  94 */           this.buf.getInt(); this.buf.getByte(); this.buf.getByte();
/*  95 */           byte[] foo = this.buf.getString();
/*  96 */           int partial_success = this.buf.getByte();
/*     */ 
/*     */ 
/*     */           
/* 100 */           if (partial_success != 0) {
/* 101 */             throw new JSchPartialAuthException(Util.byte2str(foo));
/*     */           }
/*     */           
/* 104 */           if (firsttime) {
/* 105 */             return false;
/*     */           }
/*     */ 
/*     */           
/* 109 */           session.auth_failures++;
/*     */           break;
/*     */         } 
/* 112 */         if (command == 60) {
/* 113 */           firsttime = false;
/* 114 */           this.buf.getInt(); this.buf.getByte(); this.buf.getByte();
/* 115 */           String name = Util.byte2str(this.buf.getString());
/* 116 */           String instruction = Util.byte2str(this.buf.getString());
/* 117 */           String languate_tag = Util.byte2str(this.buf.getString());
/* 118 */           int num = this.buf.getInt();
/* 119 */           String[] prompt = new String[num];
/* 120 */           boolean[] echo = new boolean[num];
/* 121 */           for (int i = 0; i < num; i++) {
/* 122 */             prompt[i] = Util.byte2str(this.buf.getString());
/* 123 */             echo[i] = (this.buf.getByte() != 0);
/*     */           } 
/*     */           
/* 126 */           byte[][] response = (byte[][])null;
/*     */           
/* 128 */           if (password != null && prompt.length == 1 && !echo[0] && prompt[0].toLowerCase().indexOf("password:") >= 0) {
/*     */ 
/*     */ 
/*     */             
/* 132 */             response = new byte[1][];
/* 133 */             response[0] = password;
/* 134 */             password = null;
/*     */           }
/* 136 */           else if (num > 0 || name.length() > 0 || instruction.length() > 0) {
/*     */ 
/*     */             
/* 139 */             if (this.userinfo != null) {
/* 140 */               UIKeyboardInteractive kbi = (UIKeyboardInteractive)this.userinfo;
/* 141 */               String[] _response = kbi.promptKeyboardInteractive(dest, name, instruction, prompt, echo);
/*     */ 
/*     */ 
/*     */ 
/*     */               
/* 146 */               if (_response != null) {
/* 147 */                 response = new byte[_response.length][];
/* 148 */                 for (int j = 0; j < _response.length; j++) {
/* 149 */                   response[j] = Util.str2byte(_response[j]);
/*     */                 }
/*     */               } 
/*     */             } 
/*     */           } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 160 */           this.packet.reset();
/* 161 */           this.buf.putByte((byte)61);
/* 162 */           if (num > 0 && (response == null || num != response.length)) {
/*     */ 
/*     */ 
/*     */             
/* 166 */             if (response == null) {
/*     */               
/* 168 */               this.buf.putInt(num);
/* 169 */               for (int j = 0; j < num; j++) {
/* 170 */                 this.buf.putString(Util.empty);
/*     */               }
/*     */             } else {
/*     */               
/* 174 */               this.buf.putInt(0);
/*     */             } 
/*     */             
/* 177 */             if (response == null) {
/* 178 */               cancel = true;
/*     */             }
/*     */           } else {
/* 181 */             this.buf.putInt(num);
/* 182 */             for (int j = 0; j < num; j++) {
/* 183 */               this.buf.putString(response[j]);
/*     */             }
/*     */           } 
/* 186 */           session.write(this.packet);
/*     */ 
/*     */ 
/*     */           
/*     */           continue;
/*     */         } 
/*     */ 
/*     */         
/* 194 */         return false;
/*     */       } 
/* 196 */       if (cancel)
/* 197 */         throw new JSchAuthCancelException("keyboard-interactive"); 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\UserAuthKeyboardInteractive.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */