/*     */ package com.jcraft.jsch;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.net.Socket;
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
/*     */ public class ProxyHTTP
/*     */   implements Proxy
/*     */ {
/*  36 */   private static int DEFAULTPORT = 80;
/*     */   
/*     */   private String proxy_host;
/*     */   private int proxy_port;
/*     */   private InputStream in;
/*     */   private OutputStream out;
/*     */   private Socket socket;
/*     */   private String user;
/*     */   private String passwd;
/*     */   
/*     */   public ProxyHTTP(String proxy_host) {
/*  47 */     int port = DEFAULTPORT;
/*  48 */     String host = proxy_host;
/*  49 */     if (proxy_host.indexOf(':') != -1) {
/*     */       try {
/*  51 */         host = proxy_host.substring(0, proxy_host.indexOf(':'));
/*  52 */         port = Integer.parseInt(proxy_host.substring(proxy_host.indexOf(':') + 1));
/*     */       }
/*  54 */       catch (Exception e) {}
/*     */     }
/*     */     
/*  57 */     this.proxy_host = host;
/*  58 */     this.proxy_port = port;
/*     */   }
/*     */   public ProxyHTTP(String proxy_host, int proxy_port) {
/*  61 */     this.proxy_host = proxy_host;
/*  62 */     this.proxy_port = proxy_port;
/*     */   }
/*     */   public void setUserPasswd(String user, String passwd) {
/*  65 */     this.user = user;
/*  66 */     this.passwd = passwd;
/*     */   }
/*     */   public void connect(SocketFactory socket_factory, String host, int port, int timeout) throws JSchException {
/*     */     
/*  70 */     try { if (socket_factory == null) {
/*  71 */         this.socket = Util.createSocket(this.proxy_host, this.proxy_port, timeout);
/*  72 */         this.in = this.socket.getInputStream();
/*  73 */         this.out = this.socket.getOutputStream();
/*     */       } else {
/*     */         
/*  76 */         this.socket = socket_factory.createSocket(this.proxy_host, this.proxy_port);
/*  77 */         this.in = socket_factory.getInputStream(this.socket);
/*  78 */         this.out = socket_factory.getOutputStream(this.socket);
/*     */       } 
/*  80 */       if (timeout > 0) {
/*  81 */         this.socket.setSoTimeout(timeout);
/*     */       }
/*  83 */       this.socket.setTcpNoDelay(true);
/*     */       
/*  85 */       this.out.write(Util.str2byte("CONNECT " + host + ":" + port + " HTTP/1.0\r\n"));
/*     */       
/*  87 */       if (this.user != null && this.passwd != null) {
/*  88 */         byte[] arrayOfByte = Util.str2byte(this.user + ":" + this.passwd);
/*  89 */         arrayOfByte = Util.toBase64(arrayOfByte, 0, arrayOfByte.length);
/*  90 */         this.out.write(Util.str2byte("Proxy-Authorization: Basic "));
/*  91 */         this.out.write(arrayOfByte);
/*  92 */         this.out.write(Util.str2byte("\r\n"));
/*     */       } 
/*     */       
/*  95 */       this.out.write(Util.str2byte("\r\n"));
/*  96 */       this.out.flush();
/*     */       
/*  98 */       int foo = 0;
/*     */       
/* 100 */       StringBuffer sb = new StringBuffer();
/* 101 */       while (foo >= 0) {
/* 102 */         foo = this.in.read(); if (foo != 13) { sb.append((char)foo); continue; }
/* 103 */          foo = this.in.read(); if (foo != 10);
/*     */       } 
/*     */       
/* 106 */       if (foo < 0) {
/* 107 */         throw new IOException();
/*     */       }
/*     */       
/* 110 */       String response = sb.toString();
/* 111 */       String reason = "Unknow reason";
/* 112 */       int code = -1;
/*     */       try {
/* 114 */         foo = response.indexOf(' ');
/* 115 */         int bar = response.indexOf(' ', foo + 1);
/* 116 */         code = Integer.parseInt(response.substring(foo + 1, bar));
/* 117 */         reason = response.substring(bar + 1);
/*     */       }
/* 119 */       catch (Exception e) {}
/*     */       
/* 121 */       if (code != 200) {
/* 122 */         throw new IOException("proxy error: " + reason);
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
/* 135 */       int count = 0;
/*     */       do {
/* 137 */         count = 0;
/* 138 */         while (foo >= 0) {
/* 139 */           foo = this.in.read(); if (foo != 13) { count++; continue; }
/* 140 */            foo = this.in.read(); if (foo != 10);
/*     */         } 
/*     */         
/* 143 */         if (foo < 0) {
/* 144 */           throw new IOException();
/*     */         }
/* 146 */       } while (count != 0);
/*     */        }
/*     */     
/* 149 */     catch (RuntimeException e)
/* 150 */     { throw e; }
/*     */     
/* 152 */     catch (Exception e) { try {
/* 153 */         if (this.socket != null) this.socket.close(); 
/* 154 */       } catch (Exception eee) {}
/*     */       
/* 156 */       String message = "ProxyHTTP: " + e.toString();
/* 157 */       if (e instanceof Throwable)
/* 158 */         throw new JSchException(message, e); 
/* 159 */       throw new JSchException(message); }
/*     */   
/*     */   }
/* 162 */   public InputStream getInputStream() { return this.in; }
/* 163 */   public OutputStream getOutputStream() { return this.out; } public Socket getSocket() {
/* 164 */     return this.socket;
/*     */   } public void close() {
/*     */     try {
/* 167 */       if (this.in != null) this.in.close(); 
/* 168 */       if (this.out != null) this.out.close(); 
/* 169 */       if (this.socket != null) this.socket.close();
/*     */     
/* 171 */     } catch (Exception e) {}
/*     */     
/* 173 */     this.in = null;
/* 174 */     this.out = null;
/* 175 */     this.socket = null;
/*     */   }
/*     */   public static int getDefaultPort() {
/* 178 */     return DEFAULTPORT;
/*     */   }
/*     */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\ProxyHTTP.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */