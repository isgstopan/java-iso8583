/*     */ package com.jcraft.jsch;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PipedInputStream;
/*     */ import java.io.PipedOutputStream;
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
/*     */ 
/*     */ 
/*     */ public abstract class Channel
/*     */   implements Runnable
/*     */ {
/*     */   static final int SSH_MSG_CHANNEL_OPEN_CONFIRMATION = 91;
/*     */   static final int SSH_MSG_CHANNEL_OPEN_FAILURE = 92;
/*     */   static final int SSH_MSG_CHANNEL_WINDOW_ADJUST = 93;
/*     */   static final int SSH_OPEN_ADMINISTRATIVELY_PROHIBITED = 1;
/*     */   static final int SSH_OPEN_CONNECT_FAILED = 2;
/*     */   static final int SSH_OPEN_UNKNOWN_CHANNEL_TYPE = 3;
/*     */   static final int SSH_OPEN_RESOURCE_SHORTAGE = 4;
/*  50 */   static int index = 0;
/*  51 */   private static Vector pool = new Vector(); int id;
/*     */   static Channel getChannel(String type) {
/*  53 */     if (type.equals("session")) {
/*  54 */       return new ChannelSession();
/*     */     }
/*  56 */     if (type.equals("shell")) {
/*  57 */       return new ChannelShell();
/*     */     }
/*  59 */     if (type.equals("exec")) {
/*  60 */       return new ChannelExec();
/*     */     }
/*  62 */     if (type.equals("x11")) {
/*  63 */       return new ChannelX11();
/*     */     }
/*  65 */     if (type.equals("auth-agent@openssh.com")) {
/*  66 */       return new ChannelAgentForwarding();
/*     */     }
/*  68 */     if (type.equals("direct-tcpip")) {
/*  69 */       return new ChannelDirectTCPIP();
/*     */     }
/*  71 */     if (type.equals("forwarded-tcpip")) {
/*  72 */       return new ChannelForwardedTCPIP();
/*     */     }
/*  74 */     if (type.equals("sftp")) {
/*  75 */       return new ChannelSftp();
/*     */     }
/*  77 */     if (type.equals("subsystem")) {
/*  78 */       return new ChannelSubsystem();
/*     */     }
/*  80 */     return null;
/*     */   }
/*     */   static Channel getChannel(int id, Session session) {
/*  83 */     synchronized (pool) {
/*  84 */       for (int i = 0; i < pool.size(); i++) {
/*  85 */         Channel c = pool.elementAt(i);
/*  86 */         if (c.id == id && c.session == session) return c; 
/*     */       } 
/*     */     } 
/*  89 */     return null;
/*     */   }
/*     */   static void del(Channel c) {
/*  92 */     synchronized (pool) {
/*  93 */       pool.removeElement(c);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*  98 */   volatile int recipient = -1;
/*  99 */   protected byte[] type = Util.str2byte("foo");
/* 100 */   volatile int lwsize_max = 1048576;
/* 101 */   volatile int lwsize = this.lwsize_max;
/* 102 */   volatile int lmpsize = 16384;
/*     */   
/* 104 */   volatile long rwsize = 0L;
/* 105 */   volatile int rmpsize = 0;
/*     */   
/* 107 */   IO io = null;
/* 108 */   Thread thread = null;
/*     */   
/*     */   volatile boolean eof_local = false;
/*     */   
/*     */   volatile boolean eof_remote = false;
/*     */   
/*     */   volatile boolean close = false;
/*     */   volatile boolean connected = false;
/*     */   volatile boolean open_confirmation = false;
/* 117 */   volatile int exitstatus = -1;
/*     */   
/* 119 */   volatile int reply = 0;
/* 120 */   volatile int connectTimeout = 0;
/*     */   
/*     */   private Session session;
/*     */   
/* 124 */   int notifyme = 0;
/*     */   
/*     */   Channel() {
/* 127 */     synchronized (pool) {
/* 128 */       this.id = index++;
/* 129 */       pool.addElement(this);
/*     */     } 
/*     */   }
/*     */   synchronized void setRecipient(int foo) {
/* 133 */     this.recipient = foo;
/* 134 */     if (this.notifyme > 0)
/* 135 */       notifyAll(); 
/*     */   }
/*     */   int getRecipient() {
/* 138 */     return this.recipient;
/*     */   }
/*     */ 
/*     */   
/*     */   void init() throws JSchException {}
/*     */   
/*     */   public void connect() throws JSchException {
/* 145 */     connect(0);
/*     */   }
/*     */   
/*     */   public void connect(int connectTimeout) throws JSchException {
/* 149 */     this.connectTimeout = connectTimeout;
/*     */     try {
/* 151 */       sendChannelOpen();
/* 152 */       start();
/*     */     }
/* 154 */     catch (Exception e) {
/* 155 */       this.connected = false;
/* 156 */       disconnect();
/* 157 */       if (e instanceof JSchException)
/* 158 */         throw (JSchException)e; 
/* 159 */       throw new JSchException(e.toString(), e);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setXForwarding(boolean foo) {}
/*     */   
/*     */   public void start() throws JSchException {}
/*     */   
/*     */   public boolean isEOF() {
/* 168 */     return this.eof_remote;
/*     */   }
/*     */   void getData(Buffer buf) {
/* 171 */     setRecipient(buf.getInt());
/* 172 */     setRemoteWindowSize(buf.getUInt());
/* 173 */     setRemotePacketSize(buf.getInt());
/*     */   }
/*     */   
/*     */   public void setInputStream(InputStream in) {
/* 177 */     this.io.setInputStream(in, false);
/*     */   }
/*     */   public void setInputStream(InputStream in, boolean dontclose) {
/* 180 */     this.io.setInputStream(in, dontclose);
/*     */   }
/*     */   public void setOutputStream(OutputStream out) {
/* 183 */     this.io.setOutputStream(out, false);
/*     */   }
/*     */   public void setOutputStream(OutputStream out, boolean dontclose) {
/* 186 */     this.io.setOutputStream(out, dontclose);
/*     */   }
/*     */   public void setExtOutputStream(OutputStream out) {
/* 189 */     this.io.setExtOutputStream(out, false);
/*     */   }
/*     */   public void setExtOutputStream(OutputStream out, boolean dontclose) {
/* 192 */     this.io.setExtOutputStream(out, dontclose);
/*     */   }
/*     */   public InputStream getInputStream() throws IOException {
/* 195 */     int max_input_buffer_size = 32768;
/*     */     try {
/* 197 */       max_input_buffer_size = Integer.parseInt(getSession().getConfig("max_input_buffer_size"));
/*     */     
/*     */     }
/* 200 */     catch (Exception e) {}
/* 201 */     PipedInputStream in = new MyPipedInputStream(32768, max_input_buffer_size);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 206 */     boolean resizable = (32768 < max_input_buffer_size);
/* 207 */     this.io.setOutputStream(new PassiveOutputStream(in, resizable), false);
/* 208 */     return in;
/*     */   }
/*     */   public InputStream getExtInputStream() throws IOException {
/* 211 */     int max_input_buffer_size = 32768;
/*     */     try {
/* 213 */       max_input_buffer_size = Integer.parseInt(getSession().getConfig("max_input_buffer_size"));
/*     */     
/*     */     }
/* 216 */     catch (Exception e) {}
/* 217 */     PipedInputStream in = new MyPipedInputStream(32768, max_input_buffer_size);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 222 */     boolean resizable = (32768 < max_input_buffer_size);
/* 223 */     this.io.setExtOutputStream(new PassiveOutputStream(in, resizable), false);
/* 224 */     return in;
/*     */   }
/*     */   
/*     */   public OutputStream getOutputStream() throws IOException {
/* 228 */     final Channel channel = this;
/* 229 */     OutputStream out = new OutputStream() {
/* 230 */         private int dataLen = 0;
/* 231 */         private Buffer buffer = null;
/* 232 */         private Packet packet = null; private boolean closed = false;
/*     */         
/*     */         private synchronized void init() throws IOException {
/* 235 */           this.buffer = new Buffer(Channel.this.rmpsize);
/* 236 */           this.packet = new Packet(this.buffer);
/*     */           
/* 238 */           byte[] _buf = this.buffer.buffer;
/* 239 */           if (_buf.length - 14 - 128 <= 0) {
/* 240 */             this.buffer = null;
/* 241 */             this.packet = null;
/* 242 */             throw new IOException("failed to initialize the channel.");
/*     */           } 
/*     */         }
/*     */         private final Channel val$channel;
/* 246 */         byte[] b = new byte[1]; private final Channel this$0;
/*     */         public void write(int w) throws IOException {
/* 248 */           this.b[0] = (byte)w;
/* 249 */           write(this.b, 0, 1);
/*     */         }
/*     */         public void write(byte[] buf, int s, int l) throws IOException {
/* 252 */           if (this.packet == null) {
/* 253 */             init();
/*     */           }
/*     */           
/* 256 */           if (this.closed) {
/* 257 */             throw new IOException("Already closed");
/*     */           }
/*     */           
/* 260 */           byte[] _buf = this.buffer.buffer;
/* 261 */           int _bufl = _buf.length;
/* 262 */           while (l > 0) {
/* 263 */             int _l = l;
/* 264 */             if (l > _bufl - 14 + this.dataLen - 128) {
/* 265 */               _l = _bufl - 14 + this.dataLen - 128;
/*     */             }
/*     */             
/* 268 */             if (_l <= 0) {
/* 269 */               flush();
/*     */               
/*     */               continue;
/*     */             } 
/* 273 */             System.arraycopy(buf, s, _buf, 14 + this.dataLen, _l);
/* 274 */             this.dataLen += _l;
/* 275 */             s += _l;
/* 276 */             l -= _l;
/*     */           } 
/*     */         }
/*     */         
/*     */         public void flush() throws IOException {
/* 281 */           if (this.closed) {
/* 282 */             throw new IOException("Already closed");
/*     */           }
/* 284 */           if (this.dataLen == 0)
/*     */             return; 
/* 286 */           this.packet.reset();
/* 287 */           this.buffer.putByte((byte)94);
/* 288 */           this.buffer.putInt(Channel.this.recipient);
/* 289 */           this.buffer.putInt(this.dataLen);
/* 290 */           this.buffer.skip(this.dataLen);
/*     */           try {
/* 292 */             int foo = this.dataLen;
/* 293 */             this.dataLen = 0;
/* 294 */             synchronized (channel) {
/* 295 */               if (!channel.close) {
/* 296 */                 Channel.this.getSession().write(this.packet, channel, foo);
/*     */               }
/*     */             } 
/* 299 */           } catch (Exception e) {
/* 300 */             close();
/* 301 */             throw new IOException(e.toString());
/*     */           } 
/*     */         }
/*     */         
/*     */         public void close() throws IOException {
/* 306 */           if (this.packet == null) {
/*     */             try {
/* 308 */               init();
/*     */             }
/* 310 */             catch (IOException e) {
/*     */               return;
/*     */             } 
/*     */           }
/*     */           
/* 315 */           if (this.closed) {
/*     */             return;
/*     */           }
/* 318 */           if (this.dataLen > 0) {
/* 319 */             flush();
/*     */           }
/* 321 */           channel.eof();
/* 322 */           this.closed = true;
/*     */         }
/*     */       };
/* 325 */     return out;
/*     */   }
/*     */   
/*     */   class MyPipedInputStream extends PipedInputStream {
/* 329 */     private int BUFFER_SIZE = 1024;
/* 330 */     private int max_buffer_size = this.BUFFER_SIZE; private final Channel this$0;
/*     */     MyPipedInputStream() throws IOException {}
/*     */     
/*     */     MyPipedInputStream(int size) throws IOException {
/* 334 */       this.buffer = new byte[size];
/* 335 */       this.BUFFER_SIZE = size;
/* 336 */       this.max_buffer_size = size;
/*     */     }
/*     */     MyPipedInputStream(int size, int max_buffer_size) throws IOException {
/* 339 */       this(size);
/* 340 */       this.max_buffer_size = max_buffer_size;
/*     */     } MyPipedInputStream(PipedOutputStream out) throws IOException {
/* 342 */       super(out);
/*     */     } MyPipedInputStream(PipedOutputStream out, int size) throws IOException {
/* 344 */       super(out);
/* 345 */       this.buffer = new byte[size];
/* 346 */       this.BUFFER_SIZE = size;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public synchronized void updateReadSide() throws IOException {
/* 356 */       if (available() != 0) {
/*     */         return;
/*     */       }
/* 359 */       this.in = 0;
/* 360 */       this.out = 0;
/* 361 */       this.buffer[this.in++] = 0;
/* 362 */       read();
/*     */     }
/*     */     
/*     */     private int freeSpace() {
/* 366 */       int size = 0;
/* 367 */       if (this.out < this.in) {
/* 368 */         size = this.buffer.length - this.in;
/*     */       }
/* 370 */       else if (this.in < this.out) {
/* 371 */         if (this.in == -1) { size = this.buffer.length; }
/* 372 */         else { size = this.out - this.in; }
/*     */       
/* 374 */       }  return size;
/*     */     }
/*     */     synchronized void checkSpace(int len) throws IOException {
/* 377 */       int size = freeSpace();
/* 378 */       if (size < len) {
/* 379 */         int datasize = this.buffer.length - size;
/* 380 */         int foo = this.buffer.length;
/* 381 */         while (foo - datasize < len) {
/* 382 */           foo *= 2;
/*     */         }
/*     */         
/* 385 */         if (foo > this.max_buffer_size) {
/* 386 */           foo = this.max_buffer_size;
/*     */         }
/* 388 */         if (foo - datasize < len)
/*     */           return; 
/* 390 */         byte[] tmp = new byte[foo];
/* 391 */         if (this.out < this.in) {
/* 392 */           System.arraycopy(this.buffer, 0, tmp, 0, this.buffer.length);
/*     */         }
/* 394 */         else if (this.in < this.out) {
/* 395 */           if (this.in != -1)
/*     */           {
/*     */             
/* 398 */             System.arraycopy(this.buffer, 0, tmp, 0, this.in);
/* 399 */             System.arraycopy(this.buffer, this.out, tmp, tmp.length - this.buffer.length - this.out, this.buffer.length - this.out);
/*     */ 
/*     */             
/* 402 */             this.out = tmp.length - this.buffer.length - this.out;
/*     */           }
/*     */         
/* 405 */         } else if (this.in == this.out) {
/* 406 */           System.arraycopy(this.buffer, 0, tmp, 0, this.buffer.length);
/* 407 */           this.in = this.buffer.length;
/*     */         } 
/* 409 */         this.buffer = tmp;
/*     */       }
/* 411 */       else if (this.buffer.length == size && size > this.BUFFER_SIZE) {
/* 412 */         int i = size / 2;
/* 413 */         if (i < this.BUFFER_SIZE) i = this.BUFFER_SIZE; 
/* 414 */         byte[] tmp = new byte[i];
/* 415 */         this.buffer = tmp;
/*     */       } 
/*     */     } }
/*     */   
/* 419 */   void setLocalWindowSizeMax(int foo) { this.lwsize_max = foo; }
/* 420 */   void setLocalWindowSize(int foo) { this.lwsize = foo; }
/* 421 */   void setLocalPacketSize(int foo) { this.lmpsize = foo; } synchronized void setRemoteWindowSize(long foo) {
/* 422 */     this.rwsize = foo;
/*     */   } synchronized void addRemoteWindowSize(long foo) {
/* 424 */     this.rwsize += foo;
/* 425 */     if (this.notifyme > 0)
/* 426 */       notifyAll(); 
/*     */   } void setRemotePacketSize(int foo) {
/* 428 */     this.rmpsize = foo;
/*     */   }
/*     */   
/*     */   public void run() {}
/*     */   
/*     */   void write(byte[] foo) throws IOException {
/* 434 */     write(foo, 0, foo.length);
/*     */   }
/*     */   void write(byte[] foo, int s, int l) throws IOException {
/*     */     try {
/* 438 */       this.io.put(foo, s, l);
/* 439 */     } catch (NullPointerException e) {}
/*     */   }
/*     */   void write_ext(byte[] foo, int s, int l) throws IOException {
/*     */     try {
/* 443 */       this.io.put_ext(foo, s, l);
/* 444 */     } catch (NullPointerException e) {}
/*     */   }
/*     */   
/*     */   void eof_remote() {
/* 448 */     this.eof_remote = true;
/*     */     try {
/* 450 */       this.io.out_close();
/*     */     }
/* 452 */     catch (NullPointerException e) {}
/*     */   }
/*     */   
/*     */   void eof() {
/* 456 */     if (this.eof_local)
/* 457 */       return;  this.eof_local = true;
/*     */     
/* 459 */     int i = getRecipient();
/* 460 */     if (i == -1)
/*     */       return; 
/*     */     try {
/* 463 */       Buffer buf = new Buffer(100);
/* 464 */       Packet packet = new Packet(buf);
/* 465 */       packet.reset();
/* 466 */       buf.putByte((byte)96);
/* 467 */       buf.putInt(i);
/* 468 */       synchronized (this) {
/* 469 */         if (!this.close) {
/* 470 */           getSession().write(packet);
/*     */         }
/*     */       } 
/* 473 */     } catch (Exception e) {}
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void close() {
/* 519 */     if (this.close)
/* 520 */       return;  this.close = true;
/* 521 */     this.eof_local = this.eof_remote = true;
/*     */     
/* 523 */     int i = getRecipient();
/* 524 */     if (i == -1)
/*     */       return; 
/*     */     try {
/* 527 */       Buffer buf = new Buffer(100);
/* 528 */       Packet packet = new Packet(buf);
/* 529 */       packet.reset();
/* 530 */       buf.putByte((byte)97);
/* 531 */       buf.putInt(i);
/* 532 */       synchronized (this) {
/* 533 */         getSession().write(packet);
/*     */       }
/*     */     
/* 536 */     } catch (Exception e) {}
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isClosed() {
/* 541 */     return this.close;
/*     */   }
/*     */   static void disconnect(Session session) {
/* 544 */     Channel[] channels = null;
/* 545 */     int count = 0;
/* 546 */     synchronized (pool) {
/* 547 */       channels = new Channel[pool.size()];
/* 548 */       for (int j = 0; j < pool.size(); j++) {
/*     */         try {
/* 550 */           Channel c = pool.elementAt(j);
/* 551 */           if (c.session == session) {
/* 552 */             channels[count++] = c;
/*     */           }
/*     */         }
/* 555 */         catch (Exception e) {}
/*     */       } 
/*     */     } 
/*     */     
/* 559 */     for (int i = 0; i < count; i++) {
/* 560 */       channels[i].disconnect();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void disconnect() {
/*     */     try {
/* 570 */       synchronized (this) {
/* 571 */         if (!this.connected) {
/*     */           return;
/*     */         }
/* 574 */         this.connected = false;
/*     */       } 
/*     */       
/* 577 */       close();
/*     */       
/* 579 */       this.eof_remote = this.eof_local = true;
/*     */       
/* 581 */       this.thread = null;
/*     */       
/*     */       try {
/* 584 */         if (this.io != null) {
/* 585 */           this.io.close();
/*     */         }
/*     */       }
/* 588 */       catch (Exception e) {}
/*     */     
/*     */     }
/*     */     finally {
/*     */ 
/*     */       
/* 594 */       del(this);
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean isConnected() {
/* 599 */     Session _session = this.session;
/* 600 */     if (_session != null) {
/* 601 */       return (_session.isConnected() && this.connected);
/*     */     }
/* 603 */     return false;
/*     */   }
/*     */   
/*     */   public void sendSignal(String signal) throws Exception {
/* 607 */     RequestSignal request = new RequestSignal();
/* 608 */     request.setSignal(signal);
/* 609 */     request.request(getSession(), this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   class PassiveInputStream
/*     */     extends MyPipedInputStream
/*     */   {
/*     */     PipedOutputStream out;
/*     */ 
/*     */ 
/*     */     
/*     */     private final Channel this$0;
/*     */ 
/*     */ 
/*     */     
/*     */     PassiveInputStream(PipedOutputStream out, int size) throws IOException {
/* 627 */       super(out, size);
/* 628 */       this.out = out;
/*     */     }
/*     */     PassiveInputStream(PipedOutputStream out) throws IOException {
/* 631 */       super(out);
/* 632 */       this.out = out;
/*     */     }
/*     */     public void close() throws IOException {
/* 635 */       if (this.out != null) {
/* 636 */         this.out.close();
/*     */       }
/* 638 */       this.out = null;
/*     */     } }
/*     */   
/*     */   class PassiveOutputStream extends PipedOutputStream {
/* 642 */     private Channel.MyPipedInputStream _sink = null; private final Channel this$0;
/*     */     
/*     */     PassiveOutputStream(PipedInputStream in, boolean resizable_buffer) throws IOException {
/* 645 */       super(in);
/* 646 */       if (resizable_buffer && in instanceof Channel.MyPipedInputStream)
/* 647 */         this._sink = (Channel.MyPipedInputStream)in; 
/*     */     }
/*     */     
/*     */     public void write(int b) throws IOException {
/* 651 */       if (this._sink != null) {
/* 652 */         this._sink.checkSpace(1);
/*     */       }
/* 654 */       super.write(b);
/*     */     }
/*     */     public void write(byte[] b, int off, int len) throws IOException {
/* 657 */       if (this._sink != null) {
/* 658 */         this._sink.checkSpace(len);
/*     */       }
/* 660 */       super.write(b, off, len);
/*     */     }
/*     */   }
/*     */   
/* 664 */   void setExitStatus(int status) { this.exitstatus = status; } public int getExitStatus() {
/* 665 */     return this.exitstatus;
/*     */   }
/*     */   void setSession(Session session) {
/* 668 */     this.session = session;
/*     */   }
/*     */   
/*     */   public Session getSession() throws JSchException {
/* 672 */     Session _session = this.session;
/* 673 */     if (_session == null) {
/* 674 */       throw new JSchException("session is not available");
/*     */     }
/* 676 */     return _session;
/*     */   } public int getId() {
/* 678 */     return this.id;
/*     */   }
/*     */   protected void sendOpenConfirmation() throws Exception {
/* 681 */     Buffer buf = new Buffer(100);
/* 682 */     Packet packet = new Packet(buf);
/* 683 */     packet.reset();
/* 684 */     buf.putByte((byte)91);
/* 685 */     buf.putInt(getRecipient());
/* 686 */     buf.putInt(this.id);
/* 687 */     buf.putInt(this.lwsize);
/* 688 */     buf.putInt(this.lmpsize);
/* 689 */     getSession().write(packet);
/*     */   }
/*     */   
/*     */   protected void sendOpenFailure(int reasoncode) {
/*     */     try {
/* 694 */       Buffer buf = new Buffer(100);
/* 695 */       Packet packet = new Packet(buf);
/* 696 */       packet.reset();
/* 697 */       buf.putByte((byte)92);
/* 698 */       buf.putInt(getRecipient());
/* 699 */       buf.putInt(reasoncode);
/* 700 */       buf.putString(Util.str2byte("open failed"));
/* 701 */       buf.putString(Util.empty);
/* 702 */       getSession().write(packet);
/*     */     }
/* 704 */     catch (Exception e) {}
/*     */   }
/*     */ 
/*     */   
/*     */   protected Packet genChannelOpenPacket() {
/* 709 */     Buffer buf = new Buffer(100);
/* 710 */     Packet packet = new Packet(buf);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 716 */     packet.reset();
/* 717 */     buf.putByte((byte)90);
/* 718 */     buf.putString(this.type);
/* 719 */     buf.putInt(this.id);
/* 720 */     buf.putInt(this.lwsize);
/* 721 */     buf.putInt(this.lmpsize);
/* 722 */     return packet;
/*     */   }
/*     */   
/*     */   protected void sendChannelOpen() throws Exception {
/* 726 */     Session _session = getSession();
/* 727 */     if (!_session.isConnected()) {
/* 728 */       throw new JSchException("session is down");
/*     */     }
/*     */     
/* 731 */     Packet packet = genChannelOpenPacket();
/* 732 */     _session.write(packet);
/*     */     
/* 734 */     int retry = 2000;
/* 735 */     long start = System.currentTimeMillis();
/* 736 */     long timeout = this.connectTimeout;
/* 737 */     if (timeout != 0L) retry = 1; 
/* 738 */     synchronized (this) {
/*     */ 
/*     */       
/* 741 */       while (getRecipient() == -1 && _session.isConnected() && retry > 0) {
/* 742 */         if (timeout > 0L && 
/* 743 */           System.currentTimeMillis() - start > timeout) {
/* 744 */           retry = 0;
/*     */           
/*     */           continue;
/*     */         } 
/*     */         try {
/* 749 */           long t = (timeout == 0L) ? 10L : timeout;
/* 750 */           this.notifyme = 1;
/* 751 */           wait(t);
/*     */         }
/* 753 */         catch (InterruptedException e) {
/*     */         
/*     */         } finally {
/* 756 */           this.notifyme = 0;
/*     */         } 
/* 758 */         retry--;
/*     */       } 
/*     */     } 
/* 761 */     if (!_session.isConnected()) {
/* 762 */       throw new JSchException("session is down");
/*     */     }
/* 764 */     if (getRecipient() == -1) {
/* 765 */       throw new JSchException("channel is not opened.");
/*     */     }
/* 767 */     if (!this.open_confirmation) {
/* 768 */       throw new JSchException("channel is not opened.");
/*     */     }
/* 770 */     this.connected = true;
/*     */   }
/*     */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\Channel.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */