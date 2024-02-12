/*     */ package com.jcraft.jsch;
/*     */ 
/*     */ import java.util.Enumeration;
/*     */ import java.util.Hashtable;
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
/*     */ class ChannelSession
/*     */   extends Channel
/*     */ {
/*  35 */   private static byte[] _session = Util.str2byte("session");
/*     */   
/*     */   protected boolean agent_forwarding = false;
/*     */   protected boolean xforwading = false;
/*  39 */   protected Hashtable env = null;
/*     */   
/*     */   protected boolean pty = false;
/*     */   
/*  43 */   protected String ttype = "vt100";
/*  44 */   protected int tcol = 80;
/*  45 */   protected int trow = 24;
/*  46 */   protected int twp = 640;
/*  47 */   protected int thp = 480;
/*  48 */   protected byte[] terminal_mode = null;
/*     */ 
/*     */   
/*     */   ChannelSession() {
/*  52 */     this.type = _session;
/*  53 */     this.io = new IO();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAgentForwarding(boolean enable) {
/*  62 */     this.agent_forwarding = enable;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setXForwarding(boolean enable) {
/*  72 */     this.xforwading = enable;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setEnv(Hashtable env) {
/*  81 */     synchronized (this) {
/*  82 */       this.env = env;
/*     */     } 
/*     */   }
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
/*     */   public void setEnv(String name, String value) {
/*  97 */     setEnv(Util.str2byte(name), Util.str2byte(value));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setEnv(byte[] name, byte[] value) {
/* 109 */     synchronized (this) {
/* 110 */       getEnv().put(name, value);
/*     */     } 
/*     */   }
/*     */   
/*     */   private Hashtable getEnv() {
/* 115 */     if (this.env == null)
/* 116 */       this.env = new Hashtable(); 
/* 117 */     return this.env;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPty(boolean enable) {
/* 127 */     this.pty = enable;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setTerminalMode(byte[] terminal_mode) {
/* 136 */     this.terminal_mode = terminal_mode;
/*     */   }
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
/*     */   public void setPtySize(int col, int row, int wp, int hp) {
/* 149 */     setPtyType(this.ttype, col, row, wp, hp);
/* 150 */     if (!this.pty || !isConnected()) {
/*     */       return;
/*     */     }
/*     */     try {
/* 154 */       RequestWindowChange request = new RequestWindowChange();
/* 155 */       request.setSize(col, row, wp, hp);
/* 156 */       request.request(getSession(), this);
/*     */     }
/* 158 */     catch (Exception e) {}
/*     */   }
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
/*     */   public void setPtyType(String ttype) {
/* 171 */     setPtyType(ttype, 80, 24, 640, 480);
/*     */   }
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
/*     */   public void setPtyType(String ttype, int col, int row, int wp, int hp) {
/* 185 */     this.ttype = ttype;
/* 186 */     this.tcol = col;
/* 187 */     this.trow = row;
/* 188 */     this.twp = wp;
/* 189 */     this.thp = hp;
/*     */   }
/*     */   
/*     */   protected void sendRequests() throws Exception {
/* 193 */     Session _session = getSession();
/*     */     
/* 195 */     if (this.agent_forwarding) {
/* 196 */       Request request = new RequestAgentForwarding();
/* 197 */       request.request(_session, this);
/*     */     } 
/*     */     
/* 200 */     if (this.xforwading) {
/* 201 */       Request request = new RequestX11();
/* 202 */       request.request(_session, this);
/*     */     } 
/*     */     
/* 205 */     if (this.pty) {
/* 206 */       Request request = new RequestPtyReq();
/* 207 */       ((RequestPtyReq)request).setTType(this.ttype);
/* 208 */       ((RequestPtyReq)request).setTSize(this.tcol, this.trow, this.twp, this.thp);
/* 209 */       if (this.terminal_mode != null) {
/* 210 */         ((RequestPtyReq)request).setTerminalMode(this.terminal_mode);
/*     */       }
/* 212 */       request.request(_session, this);
/*     */     } 
/*     */     
/* 215 */     if (this.env != null) {
/* 216 */       for (Enumeration _env = this.env.keys(); _env.hasMoreElements(); ) {
/* 217 */         Object name = _env.nextElement();
/* 218 */         Object value = this.env.get(name);
/* 219 */         Request request = new RequestEnv();
/* 220 */         ((RequestEnv)request).setEnv(toByteArray(name), toByteArray(value));
/*     */         
/* 222 */         request.request(_session, this);
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private byte[] toByteArray(Object o) {
/* 228 */     if (o instanceof String) {
/* 229 */       return Util.str2byte((String)o);
/*     */     }
/* 231 */     return (byte[])o;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void run() {
/* 237 */     Buffer buf = new Buffer(this.rmpsize);
/* 238 */     Packet packet = new Packet(buf);
/* 239 */     int i = -1;
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 244 */       while (isConnected() && this.thread != null && this.io != null && this.io.in != null) {
/* 245 */         i = this.io.in.read(buf.buffer, 14, buf.buffer.length - 14 - 128);
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 250 */         if (i == 0)
/* 251 */           continue;  if (i == -1) {
/* 252 */           eof();
/*     */           break;
/*     */         } 
/* 255 */         if (this.close)
/*     */           break; 
/* 257 */         packet.reset();
/* 258 */         buf.putByte((byte)94);
/* 259 */         buf.putInt(this.recipient);
/* 260 */         buf.putInt(i);
/* 261 */         buf.skip(i);
/* 262 */         getSession().write(packet, this, i);
/*     */       }
/*     */     
/* 265 */     } catch (Exception e) {}
/*     */ 
/*     */ 
/*     */     
/* 269 */     Thread _thread = this.thread;
/* 270 */     if (_thread != null)
/* 271 */       synchronized (_thread) { _thread.notifyAll(); }
/*     */        
/* 273 */     this.thread = null;
/*     */   }
/*     */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\ChannelSession.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */