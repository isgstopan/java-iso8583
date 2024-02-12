/*     */ package com.jcraft.jsch;
/*     */ 
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.InetAddress;
/*     */ import java.net.ServerSocket;
/*     */ import java.net.Socket;
/*     */ import java.net.UnknownHostException;
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
/*     */ class PortWatcher
/*     */   implements Runnable
/*     */ {
/*  36 */   private static Vector pool = new Vector();
/*  37 */   private static InetAddress anyLocalAddress = null;
/*     */   
/*     */   Session session;
/*     */   int lport;
/*     */   int rport;
/*     */   
/*     */   static {
/*     */     try {
/*  45 */       anyLocalAddress = InetAddress.getByName("0.0.0.0");
/*  46 */     } catch (UnknownHostException e) {}
/*     */   }
/*     */ 
/*     */   
/*     */   String host;
/*     */   
/*     */   InetAddress boundaddress;
/*     */   
/*     */   Runnable thread;
/*     */   
/*     */   ServerSocket ss;
/*  57 */   int connectTimeout = 0;
/*     */   
/*     */   static String[] getPortForwarding(Session session) {
/*  60 */     Vector foo = new Vector();
/*  61 */     synchronized (pool) {
/*  62 */       for (int j = 0; j < pool.size(); j++) {
/*  63 */         PortWatcher p = pool.elementAt(j);
/*  64 */         if (p.session == session) {
/*  65 */           foo.addElement(p.lport + ":" + p.host + ":" + p.rport);
/*     */         }
/*     */       } 
/*     */     } 
/*  69 */     String[] bar = new String[foo.size()];
/*  70 */     for (int i = 0; i < foo.size(); i++) {
/*  71 */       bar[i] = foo.elementAt(i);
/*     */     }
/*  73 */     return bar;
/*     */   }
/*     */   static PortWatcher getPort(Session session, String address, int lport) throws JSchException {
/*     */     InetAddress addr;
/*     */     try {
/*  78 */       addr = InetAddress.getByName(address);
/*     */     }
/*  80 */     catch (UnknownHostException uhe) {
/*  81 */       throw new JSchException("PortForwardingL: invalid address " + address + " specified.", uhe);
/*     */     } 
/*  83 */     synchronized (pool) {
/*  84 */       for (int i = 0; i < pool.size(); i++) {
/*  85 */         PortWatcher p = pool.elementAt(i);
/*  86 */         if (p.session == session && p.lport == lport && ((
/*  87 */           anyLocalAddress != null && p.boundaddress.equals(anyLocalAddress)) || p.boundaddress.equals(addr)))
/*     */         {
/*     */           
/*  90 */           return p;
/*     */         }
/*     */       } 
/*  93 */       return null;
/*     */     } 
/*     */   }
/*     */   private static String normalize(String address) {
/*  97 */     if (address != null)
/*  98 */       if (address.length() == 0 || address.equals("*")) {
/*  99 */         address = "0.0.0.0";
/* 100 */       } else if (address.equals("localhost")) {
/* 101 */         address = "127.0.0.1";
/*     */       }  
/* 103 */     return address;
/*     */   }
/*     */   static PortWatcher addPort(Session session, String address, int lport, String host, int rport, ServerSocketFactory ssf) throws JSchException {
/* 106 */     address = normalize(address);
/* 107 */     if (getPort(session, address, lport) != null) {
/* 108 */       throw new JSchException("PortForwardingL: local port " + address + ":" + lport + " is already registered.");
/*     */     }
/* 110 */     PortWatcher pw = new PortWatcher(session, address, lport, host, rport, ssf);
/* 111 */     pool.addElement(pw);
/* 112 */     return pw;
/*     */   }
/*     */   static void delPort(Session session, String address, int lport) throws JSchException {
/* 115 */     address = normalize(address);
/* 116 */     PortWatcher pw = getPort(session, address, lport);
/* 117 */     if (pw == null) {
/* 118 */       throw new JSchException("PortForwardingL: local port " + address + ":" + lport + " is not registered.");
/*     */     }
/* 120 */     pw.delete();
/* 121 */     pool.removeElement(pw);
/*     */   }
/*     */   static void delPort(Session session) {
/* 124 */     synchronized (pool) {
/* 125 */       PortWatcher[] foo = new PortWatcher[pool.size()];
/* 126 */       int count = 0; int i;
/* 127 */       for (i = 0; i < pool.size(); i++) {
/* 128 */         PortWatcher p = pool.elementAt(i);
/* 129 */         if (p.session == session) {
/* 130 */           p.delete();
/* 131 */           foo[count++] = p;
/*     */         } 
/*     */       } 
/* 134 */       for (i = 0; i < count; i++) {
/* 135 */         PortWatcher p = foo[i];
/* 136 */         pool.removeElement(p);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   PortWatcher(Session session, String address, int lport, String host, int rport, ServerSocketFactory factory) throws JSchException {
/* 144 */     this.session = session;
/* 145 */     this.lport = lport;
/* 146 */     this.host = host;
/* 147 */     this.rport = rport;
/*     */     try {
/* 149 */       this.boundaddress = InetAddress.getByName(address);
/* 150 */       this.ss = (factory == null) ? new ServerSocket(lport, 0, this.boundaddress) : factory.createServerSocket(lport, 0, this.boundaddress);
/*     */ 
/*     */     
/*     */     }
/* 154 */     catch (Exception e) {
/*     */       
/* 156 */       String message = "PortForwardingL: local port " + address + ":" + lport + " cannot be bound.";
/* 157 */       if (e instanceof Throwable)
/* 158 */         throw new JSchException(message, e); 
/* 159 */       throw new JSchException(message);
/*     */     } 
/* 161 */     if (lport == 0) {
/* 162 */       int assigned = this.ss.getLocalPort();
/* 163 */       if (assigned != -1)
/* 164 */         this.lport = assigned; 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void run() {
/* 169 */     this.thread = this;
/*     */     try {
/* 171 */       while (this.thread != null) {
/* 172 */         Socket socket = this.ss.accept();
/* 173 */         socket.setTcpNoDelay(true);
/* 174 */         InputStream in = socket.getInputStream();
/* 175 */         OutputStream out = socket.getOutputStream();
/* 176 */         ChannelDirectTCPIP channel = new ChannelDirectTCPIP();
/* 177 */         channel.init();
/* 178 */         channel.setInputStream(in);
/* 179 */         channel.setOutputStream(out);
/* 180 */         this.session.addChannel(channel);
/* 181 */         channel.setHost(this.host);
/* 182 */         channel.setPort(this.rport);
/* 183 */         channel.setOrgIPAddress(socket.getInetAddress().getHostAddress());
/* 184 */         channel.setOrgPort(socket.getPort());
/* 185 */         channel.connect(this.connectTimeout);
/* 186 */         if (channel.exitstatus != -1);
/*     */       }
/*     */     
/*     */     }
/* 190 */     catch (Exception e) {}
/*     */ 
/*     */     
/* 193 */     delete();
/*     */   }
/*     */   
/*     */   void delete() {
/* 197 */     this.thread = null;
/*     */     try {
/* 199 */       if (this.ss != null) this.ss.close(); 
/* 200 */       this.ss = null;
/*     */     }
/* 202 */     catch (Exception e) {}
/*     */   }
/*     */ 
/*     */   
/*     */   void setConnectTimeout(int connectTimeout) {
/* 207 */     this.connectTimeout = connectTimeout;
/*     */   }
/*     */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\PortWatcher.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */