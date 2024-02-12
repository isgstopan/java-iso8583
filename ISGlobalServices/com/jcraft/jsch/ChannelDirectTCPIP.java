/*     */ package com.jcraft.jsch;
/*     */ 
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
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
/*     */ public class ChannelDirectTCPIP
/*     */   extends Channel
/*     */ {
/*     */   private static final int LOCAL_WINDOW_SIZE_MAX = 131072;
/*     */   private static final int LOCAL_MAXIMUM_PACKET_SIZE = 16384;
/*  38 */   private static final byte[] _type = Util.str2byte("direct-tcpip");
/*     */   
/*     */   String host;
/*     */   int port;
/*  42 */   String originator_IP_address = "127.0.0.1";
/*  43 */   int originator_port = 0;
/*     */ 
/*     */   
/*     */   ChannelDirectTCPIP() {
/*  47 */     this.type = _type;
/*  48 */     setLocalWindowSizeMax(131072);
/*  49 */     setLocalWindowSize(131072);
/*  50 */     setLocalPacketSize(16384);
/*     */   }
/*     */   
/*     */   void init() {
/*  54 */     this.io = new IO();
/*     */   }
/*     */   
/*     */   public void connect(int connectTimeout) throws JSchException {
/*  58 */     this.connectTimeout = connectTimeout;
/*     */     try {
/*  60 */       Session _session = getSession();
/*  61 */       if (!_session.isConnected()) {
/*  62 */         throw new JSchException("session is down");
/*     */       }
/*     */       
/*  65 */       if (this.io.in != null) {
/*  66 */         this.thread = new Thread(this);
/*  67 */         this.thread.setName("DirectTCPIP thread " + _session.getHost());
/*  68 */         if (_session.daemon_thread) {
/*  69 */           this.thread.setDaemon(_session.daemon_thread);
/*     */         }
/*  71 */         this.thread.start();
/*     */       } else {
/*     */         
/*  74 */         sendChannelOpen();
/*     */       }
/*     */     
/*  77 */     } catch (Exception e) {
/*  78 */       this.io.close();
/*  79 */       this.io = null;
/*  80 */       Channel.del(this);
/*  81 */       if (e instanceof JSchException) {
/*  82 */         throw (JSchException)e;
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void run() {
/*     */     try {
/*  90 */       sendChannelOpen();
/*     */       
/*  92 */       Buffer buf = new Buffer(this.rmpsize);
/*  93 */       Packet packet = new Packet(buf);
/*  94 */       Session _session = getSession();
/*  95 */       int i = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 100 */       while (isConnected() && this.thread != null && this.io != null && this.io.in != null) {
/* 101 */         i = this.io.in.read(buf.buffer, 14, buf.buffer.length - 14 - 128);
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 106 */         if (i <= 0) {
/* 107 */           eof();
/*     */           break;
/*     */         } 
/* 110 */         packet.reset();
/* 111 */         buf.putByte((byte)94);
/* 112 */         buf.putInt(this.recipient);
/* 113 */         buf.putInt(i);
/* 114 */         buf.skip(i);
/* 115 */         synchronized (this) {
/* 116 */           if (this.close)
/*     */             break; 
/* 118 */           _session.write(packet, this, i);
/*     */         }
/*     */       
/*     */       } 
/* 122 */     } catch (Exception e) {
/*     */ 
/*     */       
/* 125 */       if (!this.connected) {
/* 126 */         this.connected = true;
/*     */       }
/* 128 */       disconnect();
/*     */       
/*     */       return;
/*     */     } 
/* 132 */     eof();
/* 133 */     disconnect();
/*     */   }
/*     */   
/*     */   public void setInputStream(InputStream in) {
/* 137 */     this.io.setInputStream(in);
/*     */   }
/*     */   public void setOutputStream(OutputStream out) {
/* 140 */     this.io.setOutputStream(out);
/*     */   }
/*     */   
/* 143 */   public void setHost(String host) { this.host = host; }
/* 144 */   public void setPort(int port) { this.port = port; }
/* 145 */   public void setOrgIPAddress(String foo) { this.originator_IP_address = foo; } public void setOrgPort(int foo) {
/* 146 */     this.originator_port = foo;
/*     */   }
/*     */   protected Packet genChannelOpenPacket() {
/* 149 */     Buffer buf = new Buffer(50 + this.host.length() + this.originator_IP_address.length() + 128);
/*     */ 
/*     */     
/* 152 */     Packet packet = new Packet(buf);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 158 */     packet.reset();
/* 159 */     buf.putByte((byte)90);
/* 160 */     buf.putString(this.type);
/* 161 */     buf.putInt(this.id);
/* 162 */     buf.putInt(this.lwsize);
/* 163 */     buf.putInt(this.lmpsize);
/* 164 */     buf.putString(Util.str2byte(this.host));
/* 165 */     buf.putInt(this.port);
/* 166 */     buf.putString(Util.str2byte(this.originator_IP_address));
/* 167 */     buf.putInt(this.originator_port);
/* 168 */     return packet;
/*     */   }
/*     */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\ChannelDirectTCPIP.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */