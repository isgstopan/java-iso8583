/*     */ package com.jcraft.jsch;
/*     */ 
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.InetAddress;
/*     */ import java.net.Socket;
/*     */ import java.net.UnknownHostException;
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
/*     */ public class ProxySOCKS4
/*     */   implements Proxy
/*     */ {
/*  42 */   private static int DEFAULTPORT = 1080;
/*     */   private String proxy_host;
/*     */   private int proxy_port;
/*     */   private InputStream in;
/*     */   private OutputStream out;
/*     */   private Socket socket;
/*     */   private String user;
/*     */   private String passwd;
/*     */   
/*     */   public ProxySOCKS4(String proxy_host) {
/*  52 */     int port = DEFAULTPORT;
/*  53 */     String host = proxy_host;
/*  54 */     if (proxy_host.indexOf(':') != -1) {
/*     */       try {
/*  56 */         host = proxy_host.substring(0, proxy_host.indexOf(':'));
/*  57 */         port = Integer.parseInt(proxy_host.substring(proxy_host.indexOf(':') + 1));
/*     */       }
/*  59 */       catch (Exception e) {}
/*     */     }
/*     */     
/*  62 */     this.proxy_host = host;
/*  63 */     this.proxy_port = port;
/*     */   }
/*     */   public ProxySOCKS4(String proxy_host, int proxy_port) {
/*  66 */     this.proxy_host = proxy_host;
/*  67 */     this.proxy_port = proxy_port;
/*     */   }
/*     */   public void setUserPasswd(String user, String passwd) {
/*  70 */     this.user = user;
/*  71 */     this.passwd = passwd;
/*     */   }
/*     */   public void connect(SocketFactory socket_factory, String host, int port, int timeout) throws JSchException {
/*     */     
/*  75 */     try { if (socket_factory == null) {
/*  76 */         this.socket = Util.createSocket(this.proxy_host, this.proxy_port, timeout);
/*     */         
/*  78 */         this.in = this.socket.getInputStream();
/*  79 */         this.out = this.socket.getOutputStream();
/*     */       } else {
/*     */         
/*  82 */         this.socket = socket_factory.createSocket(this.proxy_host, this.proxy_port);
/*  83 */         this.in = socket_factory.getInputStream(this.socket);
/*  84 */         this.out = socket_factory.getOutputStream(this.socket);
/*     */       } 
/*  86 */       if (timeout > 0) {
/*  87 */         this.socket.setSoTimeout(timeout);
/*     */       }
/*  89 */       this.socket.setTcpNoDelay(true);
/*     */       
/*  91 */       byte[] buf = new byte[1024];
/*  92 */       int index = 0;
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
/* 112 */       index = 0;
/* 113 */       buf[index++] = 4;
/* 114 */       buf[index++] = 1;
/*     */       
/* 116 */       buf[index++] = (byte)(port >>> 8);
/* 117 */       buf[index++] = (byte)(port & 0xFF);
/*     */       
/*     */       try {
/* 120 */         InetAddress addr = InetAddress.getByName(host);
/* 121 */         byte[] byteAddress = addr.getAddress();
/* 122 */         for (int i = 0; i < byteAddress.length; i++) {
/* 123 */           buf[index++] = byteAddress[i];
/*     */         }
/*     */       }
/* 126 */       catch (UnknownHostException uhe) {
/* 127 */         throw new JSchException("ProxySOCKS4: " + uhe.toString(), uhe);
/*     */       } 
/*     */       
/* 130 */       if (this.user != null) {
/* 131 */         System.arraycopy(Util.str2byte(this.user), 0, buf, index, this.user.length());
/* 132 */         index += this.user.length();
/*     */       } 
/* 134 */       buf[index++] = 0;
/* 135 */       this.out.write(buf, 0, index);
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
/* 164 */       int len = 8;
/* 165 */       int s = 0;
/* 166 */       while (s < len) {
/* 167 */         int i = this.in.read(buf, s, len - s);
/* 168 */         if (i <= 0) {
/* 169 */           throw new JSchException("ProxySOCKS4: stream is closed");
/*     */         }
/* 171 */         s += i;
/*     */       } 
/* 173 */       if (buf[0] != 0) {
/* 174 */         throw new JSchException("ProxySOCKS4: server returns VN " + buf[0]);
/*     */       }
/* 176 */       if (buf[1] != 90) { try {
/* 177 */           this.socket.close();
/* 178 */         } catch (Exception eee) {}
/*     */         
/* 180 */         String message = "ProxySOCKS4: server returns CD " + buf[1];
/* 181 */         throw new JSchException(message); }
/*     */       
/*     */        }
/* 184 */     catch (RuntimeException e)
/* 185 */     { throw e; }
/*     */     
/* 187 */     catch (Exception e) { try {
/* 188 */         if (this.socket != null) this.socket.close(); 
/* 189 */       } catch (Exception eee) {}
/*     */       
/* 191 */       throw new JSchException("ProxySOCKS4: " + e.toString()); }
/*     */   
/*     */   }
/* 194 */   public InputStream getInputStream() { return this.in; }
/* 195 */   public OutputStream getOutputStream() { return this.out; } public Socket getSocket() {
/* 196 */     return this.socket;
/*     */   } public void close() {
/*     */     try {
/* 199 */       if (this.in != null) this.in.close(); 
/* 200 */       if (this.out != null) this.out.close(); 
/* 201 */       if (this.socket != null) this.socket.close();
/*     */     
/* 203 */     } catch (Exception e) {}
/*     */     
/* 205 */     this.in = null;
/* 206 */     this.out = null;
/* 207 */     this.socket = null;
/*     */   }
/*     */   public static int getDefaultPort() {
/* 210 */     return DEFAULTPORT;
/*     */   }
/*     */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\ProxySOCKS4.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */