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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ProxySOCKS5
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
/*     */   public ProxySOCKS5(String proxy_host) {
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
/*     */   public ProxySOCKS5(String proxy_host, int proxy_port) {
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
/*     */ 
/*     */ 
/*     */       
/* 115 */       buf[index++] = 5;
/*     */       
/* 117 */       buf[index++] = 2;
/* 118 */       buf[index++] = 0;
/* 119 */       buf[index++] = 2;
/*     */       
/* 121 */       this.out.write(buf, 0, index);
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
/* 134 */       fill(this.in, buf, 2);
/*     */       
/* 136 */       boolean check = false;
/* 137 */       switch (buf[1] & 0xFF) {
/*     */         case 0:
/* 139 */           check = true;
/*     */           break;
/*     */         case 2:
/* 142 */           if (this.user == null || this.passwd == null) {
/*     */             break;
/*     */           }
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
/* 163 */           index = 0;
/* 164 */           buf[index++] = 1;
/* 165 */           buf[index++] = (byte)this.user.length();
/* 166 */           System.arraycopy(Util.str2byte(this.user), 0, buf, index, this.user.length());
/* 167 */           index += this.user.length();
/* 168 */           buf[index++] = (byte)this.passwd.length();
/* 169 */           System.arraycopy(Util.str2byte(this.passwd), 0, buf, index, this.passwd.length());
/* 170 */           index += this.passwd.length();
/*     */           
/* 172 */           this.out.write(buf, 0, index);
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
/* 189 */           fill(this.in, buf, 2);
/* 190 */           if (buf[1] == 0) {
/* 191 */             check = true;
/*     */           }
/*     */           break;
/*     */       } 
/*     */       
/* 196 */       if (!check) { try {
/* 197 */           this.socket.close();
/* 198 */         } catch (Exception eee) {}
/*     */         
/* 200 */         throw new JSchException("fail in SOCKS5 proxy"); }
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
/* 229 */       index = 0;
/* 230 */       buf[index++] = 5;
/* 231 */       buf[index++] = 1;
/* 232 */       buf[index++] = 0;
/*     */       
/* 234 */       byte[] hostb = Util.str2byte(host);
/* 235 */       int len = hostb.length;
/* 236 */       buf[index++] = 3;
/* 237 */       buf[index++] = (byte)len;
/* 238 */       System.arraycopy(hostb, 0, buf, index, len);
/* 239 */       index += len;
/* 240 */       buf[index++] = (byte)(port >>> 8);
/* 241 */       buf[index++] = (byte)(port & 0xFF);
/*     */       
/* 243 */       this.out.write(buf, 0, index);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 281 */       fill(this.in, buf, 4);
/*     */       
/* 283 */       if (buf[1] != 0) { try {
/* 284 */           this.socket.close();
/* 285 */         } catch (Exception eee) {}
/*     */         
/* 287 */         throw new JSchException("ProxySOCKS5: server returns " + buf[1]); }
/*     */ 
/*     */       
/* 290 */       switch (buf[3] & 0xFF) {
/*     */         
/*     */         case 1:
/* 293 */           fill(this.in, buf, 6);
/*     */           break;
/*     */         
/*     */         case 3:
/* 297 */           fill(this.in, buf, 1);
/*     */           
/* 299 */           fill(this.in, buf, (buf[0] & 0xFF) + 2);
/*     */           break;
/*     */         
/*     */         case 4:
/* 303 */           fill(this.in, buf, 18);
/*     */           break;
/*     */       } 
/*     */       
/*     */        }
/* 308 */     catch (RuntimeException e)
/* 309 */     { throw e; }
/*     */     
/* 311 */     catch (Exception e) { try {
/* 312 */         if (this.socket != null) this.socket.close(); 
/* 313 */       } catch (Exception eee) {}
/*     */       
/* 315 */       String message = "ProxySOCKS5: " + e.toString();
/* 316 */       if (e instanceof Throwable)
/* 317 */         throw new JSchException(message, e); 
/* 318 */       throw new JSchException(message); }
/*     */   
/*     */   }
/* 321 */   public InputStream getInputStream() { return this.in; }
/* 322 */   public OutputStream getOutputStream() { return this.out; } public Socket getSocket() {
/* 323 */     return this.socket;
/*     */   } public void close() {
/*     */     try {
/* 326 */       if (this.in != null) this.in.close(); 
/* 327 */       if (this.out != null) this.out.close(); 
/* 328 */       if (this.socket != null) this.socket.close();
/*     */     
/* 330 */     } catch (Exception e) {}
/*     */     
/* 332 */     this.in = null;
/* 333 */     this.out = null;
/* 334 */     this.socket = null;
/*     */   }
/*     */   public static int getDefaultPort() {
/* 337 */     return DEFAULTPORT;
/*     */   }
/*     */   private void fill(InputStream in, byte[] buf, int len) throws JSchException, IOException {
/* 340 */     int s = 0;
/* 341 */     while (s < len) {
/* 342 */       int i = in.read(buf, s, len - s);
/* 343 */       if (i <= 0) {
/* 344 */         throw new JSchException("ProxySOCKS5: stream is closed");
/*     */       }
/* 346 */       s += i;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\ProxySOCKS5.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */