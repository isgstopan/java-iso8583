/*     */ package com.jcraft.jsch;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.net.Socket;
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
/*     */ 
/*     */ 
/*     */ class ChannelX11
/*     */   extends Channel
/*     */ {
/*     */   private static final int LOCAL_WINDOW_SIZE_MAX = 131072;
/*     */   private static final int LOCAL_MAXIMUM_PACKET_SIZE = 16384;
/*     */   private static final int TIMEOUT = 10000;
/*  41 */   private static String host = "127.0.0.1";
/*  42 */   private static int port = 6000;
/*     */   
/*     */   private boolean init = true;
/*     */   
/*  46 */   static byte[] cookie = null;
/*  47 */   private static byte[] cookie_hex = null;
/*     */   
/*  49 */   private static Hashtable faked_cookie_pool = new Hashtable();
/*  50 */   private static Hashtable faked_cookie_hex_pool = new Hashtable();
/*     */   
/*  52 */   private static byte[] table = new byte[] { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102 };
/*     */ 
/*     */   
/*  55 */   private Socket socket = null; private byte[] cache;
/*     */   
/*     */   static int revtable(byte foo) {
/*  58 */     for (int i = 0; i < table.length; i++) {
/*  59 */       if (table[i] == foo) return i; 
/*     */     } 
/*  61 */     return 0;
/*     */   }
/*     */   static void setCookie(String foo) {
/*  64 */     cookie_hex = Util.str2byte(foo);
/*  65 */     cookie = new byte[16];
/*  66 */     for (int i = 0; i < 16; i++)
/*  67 */       cookie[i] = (byte)(revtable(cookie_hex[i * 2]) << 4 & 0xF0 | revtable(cookie_hex[i * 2 + 1]) & 0xF); 
/*     */   }
/*     */   
/*     */   static void setHost(String foo) {
/*  71 */     host = foo; } static void setPort(int foo) {
/*  72 */     port = foo;
/*     */   } static byte[] getFakedCookie(Session session) {
/*  74 */     synchronized (faked_cookie_hex_pool) {
/*  75 */       byte[] foo = (byte[])faked_cookie_hex_pool.get(session);
/*  76 */       if (foo == null) {
/*  77 */         Random random = Session.random;
/*  78 */         foo = new byte[16];
/*  79 */         synchronized (random) {
/*  80 */           random.fill(foo, 0, 16);
/*     */         } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*  89 */         faked_cookie_pool.put(session, foo);
/*  90 */         byte[] bar = new byte[32];
/*  91 */         for (int i = 0; i < 16; i++) {
/*  92 */           bar[2 * i] = table[foo[i] >>> 4 & 0xF];
/*  93 */           bar[2 * i + 1] = table[foo[i] & 0xF];
/*     */         } 
/*  95 */         faked_cookie_hex_pool.put(session, bar);
/*  96 */         foo = bar;
/*     */       } 
/*  98 */       return foo;
/*     */     } 
/*     */   }
/*     */   
/*     */   static void removeFakedCookie(Session session) {
/* 103 */     synchronized (faked_cookie_hex_pool) {
/* 104 */       faked_cookie_hex_pool.remove(session);
/* 105 */       faked_cookie_pool.remove(session);
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
/*     */   public void run() {
/*     */     try {
/* 136 */       this.socket = Util.createSocket(host, port, 10000);
/* 137 */       this.socket.setTcpNoDelay(true);
/* 138 */       this.io = new IO();
/* 139 */       this.io.setInputStream(this.socket.getInputStream());
/* 140 */       this.io.setOutputStream(this.socket.getOutputStream());
/* 141 */       sendOpenConfirmation();
/*     */     }
/* 143 */     catch (Exception e) {
/* 144 */       sendOpenFailure(1);
/* 145 */       this.close = true;
/* 146 */       disconnect();
/*     */       
/*     */       return;
/*     */     } 
/* 150 */     this.thread = Thread.currentThread();
/* 151 */     Buffer buf = new Buffer(this.rmpsize);
/* 152 */     Packet packet = new Packet(buf);
/* 153 */     int i = 0;
/*     */ 
/*     */     
/*     */     try {
/* 157 */       while (this.thread != null && this.io != null && this.io.in != null) {
/* 158 */         i = this.io.in.read(buf.buffer, 14, buf.buffer.length - 14 - 128);
/*     */ 
/*     */         
/* 161 */         if (i <= 0) {
/* 162 */           eof();
/*     */           break;
/*     */         } 
/* 165 */         if (this.close)
/* 166 */           break;  packet.reset();
/* 167 */         buf.putByte((byte)94);
/* 168 */         buf.putInt(this.recipient);
/* 169 */         buf.putInt(i);
/* 170 */         buf.skip(i);
/* 171 */         getSession().write(packet, this, i);
/*     */       }
/*     */     
/* 174 */     } catch (Exception e) {}
/*     */ 
/*     */     
/* 177 */     disconnect();
/*     */   }
/*     */   
/* 180 */   ChannelX11() { this.cache = new byte[0]; setLocalWindowSizeMax(131072); setLocalWindowSize(131072); setLocalPacketSize(16384);
/*     */     this.type = Util.str2byte("x11");
/* 182 */     this.connected = true; } private byte[] addCache(byte[] foo, int s, int l) { byte[] bar = new byte[this.cache.length + l];
/* 183 */     System.arraycopy(foo, s, bar, this.cache.length, l);
/* 184 */     if (this.cache.length > 0)
/* 185 */       System.arraycopy(this.cache, 0, bar, 0, this.cache.length); 
/* 186 */     this.cache = bar;
/* 187 */     return this.cache; }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void write(byte[] foo, int s, int l) throws IOException {
/* 193 */     if (this.init) {
/*     */       
/* 195 */       Session _session = null;
/*     */       try {
/* 197 */         _session = getSession();
/*     */       }
/* 199 */       catch (JSchException e) {
/* 200 */         throw new IOException(e.toString());
/*     */       } 
/*     */       
/* 203 */       foo = addCache(foo, s, l);
/* 204 */       s = 0;
/* 205 */       l = foo.length;
/*     */       
/* 207 */       if (l < 9) {
/*     */         return;
/*     */       }
/* 210 */       int plen = (foo[s + 6] & 0xFF) * 256 + (foo[s + 7] & 0xFF);
/* 211 */       int dlen = (foo[s + 8] & 0xFF) * 256 + (foo[s + 9] & 0xFF);
/*     */       
/* 213 */       if ((foo[s] & 0xFF) != 66)
/*     */       {
/* 215 */         if ((foo[s] & 0xFF) == 108) {
/* 216 */           plen = plen >>> 8 & 0xFF | plen << 8 & 0xFF00;
/* 217 */           dlen = dlen >>> 8 & 0xFF | dlen << 8 & 0xFF00;
/*     */         } 
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 223 */       if (l < 12 + plen + (-plen & 0x3) + dlen) {
/*     */         return;
/*     */       }
/* 226 */       byte[] bar = new byte[dlen];
/* 227 */       System.arraycopy(foo, s + 12 + plen + (-plen & 0x3), bar, 0, dlen);
/* 228 */       byte[] faked_cookie = null;
/*     */       
/* 230 */       synchronized (faked_cookie_pool) {
/* 231 */         faked_cookie = (byte[])faked_cookie_pool.get(_session);
/*     */       } 
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
/* 247 */       if (equals(bar, faked_cookie)) {
/* 248 */         if (cookie != null) {
/* 249 */           System.arraycopy(cookie, 0, foo, s + 12 + plen + (-plen & 0x3), dlen);
/*     */         }
/*     */       } else {
/*     */         
/* 253 */         this.thread = null;
/* 254 */         eof();
/* 255 */         this.io.close();
/* 256 */         disconnect();
/*     */       } 
/* 258 */       this.init = false;
/* 259 */       this.io.put(foo, s, l);
/* 260 */       this.cache = null;
/*     */       return;
/*     */     } 
/* 263 */     this.io.put(foo, s, l);
/*     */   }
/*     */   
/*     */   private static boolean equals(byte[] foo, byte[] bar) {
/* 267 */     if (foo.length != bar.length) return false; 
/* 268 */     for (int i = 0; i < foo.length; i++) {
/* 269 */       if (foo[i] != bar[i]) return false; 
/*     */     } 
/* 271 */     return true;
/*     */   }
/*     */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\ChannelX11.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */