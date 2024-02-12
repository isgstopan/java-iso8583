/*     */ package com.jcraft.jsch;
/*     */ 
/*     */ import java.io.PipedOutputStream;
/*     */ import java.net.Socket;
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
/*     */ public class ChannelForwardedTCPIP
/*     */   extends Channel
/*     */ {
/*  38 */   private static Vector pool = new Vector();
/*     */   
/*     */   private static final int LOCAL_WINDOW_SIZE_MAX = 131072;
/*     */   
/*     */   private static final int LOCAL_MAXIMUM_PACKET_SIZE = 16384;
/*     */   
/*     */   private static final int TIMEOUT = 10000;
/*     */   
/*  46 */   private Socket socket = null;
/*  47 */   private ForwardedTCPIPDaemon daemon = null;
/*  48 */   private Config config = null;
/*     */ 
/*     */   
/*     */   ChannelForwardedTCPIP() {
/*  52 */     setLocalWindowSizeMax(131072);
/*  53 */     setLocalWindowSize(131072);
/*  54 */     setLocalPacketSize(16384);
/*  55 */     this.io = new IO();
/*  56 */     this.connected = true;
/*     */   }
/*     */   
/*     */   public void run() {
/*     */     try {
/*  61 */       if (this.config instanceof ConfigDaemon) {
/*  62 */         ConfigDaemon _config = (ConfigDaemon)this.config;
/*  63 */         Class c = Class.forName(_config.target);
/*  64 */         this.daemon = (ForwardedTCPIPDaemon)c.newInstance();
/*     */         
/*  66 */         PipedOutputStream out = new PipedOutputStream();
/*  67 */         this.io.setInputStream(new Channel.PassiveInputStream(this, out, 32768), false);
/*     */ 
/*     */ 
/*     */         
/*  71 */         this.daemon.setChannel(this, getInputStream(), out);
/*  72 */         this.daemon.setArg(_config.arg);
/*  73 */         (new Thread(this.daemon)).start();
/*     */       } else {
/*     */         
/*  76 */         ConfigLHost _config = (ConfigLHost)this.config;
/*  77 */         this.socket = (_config.factory == null) ? Util.createSocket(_config.target, _config.lport, 10000) : _config.factory.createSocket(_config.target, _config.lport);
/*     */ 
/*     */         
/*  80 */         this.socket.setTcpNoDelay(true);
/*  81 */         this.io.setInputStream(this.socket.getInputStream());
/*  82 */         this.io.setOutputStream(this.socket.getOutputStream());
/*     */       } 
/*  84 */       sendOpenConfirmation();
/*     */     }
/*  86 */     catch (Exception e) {
/*  87 */       sendOpenFailure(1);
/*  88 */       this.close = true;
/*  89 */       disconnect();
/*     */       
/*     */       return;
/*     */     } 
/*  93 */     this.thread = Thread.currentThread();
/*  94 */     Buffer buf = new Buffer(this.rmpsize);
/*  95 */     Packet packet = new Packet(buf);
/*  96 */     int i = 0;
/*     */     try {
/*  98 */       Session _session = getSession();
/*     */ 
/*     */       
/* 101 */       while (this.thread != null && this.io != null && this.io.in != null) {
/* 102 */         i = this.io.in.read(buf.buffer, 14, buf.buffer.length - 14 - 128);
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 107 */         if (i <= 0) {
/* 108 */           eof();
/*     */           break;
/*     */         } 
/* 111 */         packet.reset();
/* 112 */         buf.putByte((byte)94);
/* 113 */         buf.putInt(this.recipient);
/* 114 */         buf.putInt(i);
/* 115 */         buf.skip(i);
/* 116 */         synchronized (this) {
/* 117 */           if (this.close)
/*     */             break; 
/* 119 */           _session.write(packet, this, i);
/*     */         }
/*     */       
/*     */       } 
/* 123 */     } catch (Exception e) {}
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 128 */     disconnect();
/*     */   }
/*     */   
/*     */   void getData(Buffer buf) {
/* 132 */     setRecipient(buf.getInt());
/* 133 */     setRemoteWindowSize(buf.getUInt());
/* 134 */     setRemotePacketSize(buf.getInt());
/* 135 */     byte[] addr = buf.getString();
/* 136 */     int port = buf.getInt();
/* 137 */     byte[] orgaddr = buf.getString();
/* 138 */     int orgport = buf.getInt();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 147 */     Session _session = null;
/*     */     try {
/* 149 */       _session = getSession();
/*     */     }
/* 151 */     catch (JSchException e) {}
/*     */ 
/*     */ 
/*     */     
/* 155 */     this.config = getPort(_session, Util.byte2str(addr), port);
/* 156 */     if (this.config == null) {
/* 157 */       this.config = getPort(_session, (String)null, port);
/*     */     }
/* 159 */     if (this.config == null && 
/* 160 */       JSch.getLogger().isEnabled(3)) {
/* 161 */       JSch.getLogger().log(3, "ChannelForwardedTCPIP: " + Util.byte2str(addr) + ":" + port + " is not registered.");
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static Config getPort(Session session, String address_to_bind, int rport) {
/* 168 */     synchronized (pool) {
/* 169 */       for (int i = 0; i < pool.size(); i++) {
/* 170 */         Config bar = pool.elementAt(i);
/* 171 */         if (bar.session == session && (
/* 172 */           bar.rport == rport || (
/* 173 */           bar.rport == 0 && bar.allocated_rport == rport)))
/*     */         {
/*     */           
/* 176 */           if (address_to_bind == null || bar.address_to_bind.equals(address_to_bind))
/*     */           {
/* 178 */             return bar; }  } 
/*     */       } 
/* 180 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   static String[] getPortForwarding(Session session) {
/* 185 */     Vector foo = new Vector();
/* 186 */     synchronized (pool) {
/* 187 */       for (int j = 0; j < pool.size(); j++) {
/* 188 */         Config config = pool.elementAt(j);
/* 189 */         if (config instanceof ConfigDaemon) {
/* 190 */           foo.addElement(config.allocated_rport + ":" + config.target + ":");
/*     */         } else {
/* 192 */           foo.addElement(config.allocated_rport + ":" + config.target + ":" + ((ConfigLHost)config).lport);
/*     */         } 
/*     */       } 
/* 195 */     }  String[] bar = new String[foo.size()];
/* 196 */     for (int i = 0; i < foo.size(); i++) {
/* 197 */       bar[i] = foo.elementAt(i);
/*     */     }
/* 199 */     return bar;
/*     */   }
/*     */   
/*     */   static String normalize(String address) {
/* 203 */     if (address == null) return "localhost"; 
/* 204 */     if (address.length() == 0 || address.equals("*")) return ""; 
/* 205 */     return address;
/*     */   }
/*     */ 
/*     */   
/*     */   static void addPort(Session session, String _address_to_bind, int port, int allocated_port, String target, int lport, SocketFactory factory) throws JSchException {
/* 210 */     String address_to_bind = normalize(_address_to_bind);
/* 211 */     synchronized (pool) {
/* 212 */       if (getPort(session, address_to_bind, port) != null) {
/* 213 */         throw new JSchException("PortForwardingR: remote port " + port + " is already registered.");
/*     */       }
/* 215 */       ConfigLHost config = new ConfigLHost();
/* 216 */       config.session = session;
/* 217 */       config.rport = port;
/* 218 */       config.allocated_rport = allocated_port;
/* 219 */       config.target = target;
/* 220 */       config.lport = lport;
/* 221 */       config.address_to_bind = address_to_bind;
/* 222 */       config.factory = factory;
/* 223 */       pool.addElement(config);
/*     */     } 
/*     */   }
/*     */   
/*     */   static void addPort(Session session, String _address_to_bind, int port, int allocated_port, String daemon, Object[] arg) throws JSchException {
/* 228 */     String address_to_bind = normalize(_address_to_bind);
/* 229 */     synchronized (pool) {
/* 230 */       if (getPort(session, address_to_bind, port) != null) {
/* 231 */         throw new JSchException("PortForwardingR: remote port " + port + " is already registered.");
/*     */       }
/* 233 */       ConfigDaemon config = new ConfigDaemon();
/* 234 */       config.session = session;
/* 235 */       config.rport = port;
/* 236 */       config.allocated_rport = port;
/* 237 */       config.target = daemon;
/* 238 */       config.arg = arg;
/* 239 */       config.address_to_bind = address_to_bind;
/* 240 */       pool.addElement(config);
/*     */     } 
/*     */   }
/*     */   static void delPort(ChannelForwardedTCPIP c) {
/* 244 */     Session _session = null;
/*     */     try {
/* 246 */       _session = c.getSession();
/*     */     }
/* 248 */     catch (JSchException e) {}
/*     */ 
/*     */     
/* 251 */     if (_session != null && c.config != null)
/* 252 */       delPort(_session, c.config.rport); 
/*     */   }
/*     */   static void delPort(Session session, int rport) {
/* 255 */     delPort(session, (String)null, rport);
/*     */   }
/*     */   static void delPort(Session session, String address_to_bind, int rport) {
/* 258 */     synchronized (pool) {
/* 259 */       Config foo = getPort(session, normalize(address_to_bind), rport);
/* 260 */       if (foo == null)
/* 261 */         foo = getPort(session, (String)null, rport); 
/* 262 */       if (foo == null)
/* 263 */         return;  pool.removeElement(foo);
/* 264 */       if (address_to_bind == null) {
/* 265 */         address_to_bind = foo.address_to_bind;
/*     */       }
/* 267 */       if (address_to_bind == null) {
/* 268 */         address_to_bind = "0.0.0.0";
/*     */       }
/*     */     } 
/*     */     
/* 272 */     Buffer buf = new Buffer(100);
/* 273 */     Packet packet = new Packet(buf);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 281 */       packet.reset();
/* 282 */       buf.putByte((byte)80);
/* 283 */       buf.putString(Util.str2byte("cancel-tcpip-forward"));
/* 284 */       buf.putByte((byte)0);
/* 285 */       buf.putString(Util.str2byte(address_to_bind));
/* 286 */       buf.putInt(rport);
/* 287 */       session.write(packet);
/*     */     }
/* 289 */     catch (Exception e) {}
/*     */   }
/*     */ 
/*     */   
/*     */   static void delPort(Session session) {
/* 294 */     int[] rport = null;
/* 295 */     int count = 0;
/* 296 */     synchronized (pool) {
/* 297 */       rport = new int[pool.size()];
/* 298 */       for (int j = 0; j < pool.size(); j++) {
/* 299 */         Config config = pool.elementAt(j);
/* 300 */         if (config.session == session) {
/* 301 */           rport[count++] = config.rport;
/*     */         }
/*     */       } 
/*     */     } 
/* 305 */     for (int i = 0; i < count; i++)
/* 306 */       delPort(session, rport[i]); 
/*     */   }
/*     */   
/*     */   public int getRemotePort() {
/* 310 */     return (this.config != null) ? this.config.rport : 0;
/*     */   } private void setSocketFactory(SocketFactory factory) {
/* 312 */     if (this.config != null && this.config instanceof ConfigLHost)
/* 313 */       ((ConfigLHost)this.config).factory = factory; 
/*     */   }
/*     */   
/*     */   static abstract class Config {
/*     */     Session session;
/*     */     int rport;
/*     */     int allocated_rport;
/*     */     String address_to_bind;
/*     */     String target;
/*     */   }
/*     */   
/*     */   static class ConfigDaemon extends Config {
/*     */     Object[] arg;
/*     */   }
/*     */   
/*     */   static class ConfigLHost extends Config {
/*     */     int lport;
/*     */     SocketFactory factory;
/*     */   }
/*     */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\ChannelForwardedTCPIP.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */