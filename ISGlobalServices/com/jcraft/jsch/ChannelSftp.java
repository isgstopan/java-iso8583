/*      */ package com.jcraft.jsch;
/*      */ 
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.io.PipedInputStream;
/*      */ import java.io.PipedOutputStream;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Vector;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class ChannelSftp
/*      */   extends ChannelSession
/*      */ {
/*      */   private static final int LOCAL_MAXIMUM_PACKET_SIZE = 32768;
/*      */   private static final int LOCAL_WINDOW_SIZE_MAX = 2097152;
/*      */   private static final byte SSH_FXP_INIT = 1;
/*      */   private static final byte SSH_FXP_VERSION = 2;
/*      */   private static final byte SSH_FXP_OPEN = 3;
/*      */   private static final byte SSH_FXP_CLOSE = 4;
/*      */   private static final byte SSH_FXP_READ = 5;
/*      */   private static final byte SSH_FXP_WRITE = 6;
/*      */   private static final byte SSH_FXP_LSTAT = 7;
/*      */   private static final byte SSH_FXP_FSTAT = 8;
/*      */   private static final byte SSH_FXP_SETSTAT = 9;
/*      */   private static final byte SSH_FXP_FSETSTAT = 10;
/*      */   private static final byte SSH_FXP_OPENDIR = 11;
/*      */   private static final byte SSH_FXP_READDIR = 12;
/*      */   private static final byte SSH_FXP_REMOVE = 13;
/*      */   private static final byte SSH_FXP_MKDIR = 14;
/*      */   private static final byte SSH_FXP_RMDIR = 15;
/*      */   private static final byte SSH_FXP_REALPATH = 16;
/*      */   private static final byte SSH_FXP_STAT = 17;
/*      */   private static final byte SSH_FXP_RENAME = 18;
/*      */   private static final byte SSH_FXP_READLINK = 19;
/*      */   private static final byte SSH_FXP_SYMLINK = 20;
/*      */   private static final byte SSH_FXP_STATUS = 101;
/*      */   private static final byte SSH_FXP_HANDLE = 102;
/*      */   private static final byte SSH_FXP_DATA = 103;
/*      */   private static final byte SSH_FXP_NAME = 104;
/*      */   private static final byte SSH_FXP_ATTRS = 105;
/*      */   private static final byte SSH_FXP_EXTENDED = -56;
/*      */   private static final byte SSH_FXP_EXTENDED_REPLY = -55;
/*      */   private static final int SSH_FXF_READ = 1;
/*      */   private static final int SSH_FXF_WRITE = 2;
/*      */   private static final int SSH_FXF_APPEND = 4;
/*      */   private static final int SSH_FXF_CREAT = 8;
/*      */   private static final int SSH_FXF_TRUNC = 16;
/*      */   private static final int SSH_FXF_EXCL = 32;
/*      */   private static final int SSH_FILEXFER_ATTR_SIZE = 1;
/*      */   private static final int SSH_FILEXFER_ATTR_UIDGID = 2;
/*      */   private static final int SSH_FILEXFER_ATTR_PERMISSIONS = 4;
/*      */   private static final int SSH_FILEXFER_ATTR_ACMODTIME = 8;
/*      */   private static final int SSH_FILEXFER_ATTR_EXTENDED = -2147483648;
/*      */   public static final int SSH_FX_OK = 0;
/*      */   public static final int SSH_FX_EOF = 1;
/*      */   public static final int SSH_FX_NO_SUCH_FILE = 2;
/*      */   public static final int SSH_FX_PERMISSION_DENIED = 3;
/*      */   public static final int SSH_FX_FAILURE = 4;
/*      */   public static final int SSH_FX_BAD_MESSAGE = 5;
/*      */   public static final int SSH_FX_NO_CONNECTION = 6;
/*      */   public static final int SSH_FX_CONNECTION_LOST = 7;
/*      */   public static final int SSH_FX_OP_UNSUPPORTED = 8;
/*      */   private static final int MAX_MSG_LENGTH = 262144;
/*      */   public static final int OVERWRITE = 0;
/*      */   public static final int RESUME = 1;
/*      */   public static final int APPEND = 2;
/*      */   private boolean interactive = false;
/*  135 */   private int seq = 1;
/*  136 */   private int[] ackid = new int[1];
/*      */   
/*      */   private Buffer buf;
/*      */   
/*      */   private Packet packet;
/*      */   
/*      */   private Buffer obuf;
/*      */   
/*      */   private Packet opacket;
/*  145 */   private int client_version = 3;
/*  146 */   private int server_version = 3;
/*  147 */   private String version = String.valueOf(this.client_version);
/*      */   
/*  149 */   private Hashtable extensions = null;
/*  150 */   private InputStream io_in = null;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean extension_posix_rename = false;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean extension_statvfs = false;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private boolean extension_hardlink = false;
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  173 */   private static final String file_separator = File.separator;
/*  174 */   private static final char file_separatorc = File.separatorChar;
/*  175 */   private static boolean fs_is_bs = ((byte)File.separatorChar == 92);
/*      */   
/*      */   private String cwd;
/*      */   
/*      */   private String home;
/*      */   private String lcwd;
/*      */   private static final String UTF8 = "UTF-8";
/*  182 */   private String fEncoding = "UTF-8";
/*      */   
/*      */   private boolean fEncoding_is_utf8 = true;
/*  185 */   private RequestQueue rq = new RequestQueue(16);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setBulkRequests(int bulk_requests) throws JSchException {
/*  195 */     if (bulk_requests > 0) {
/*  196 */       this.rq = new RequestQueue(bulk_requests);
/*      */     } else {
/*  198 */       throw new JSchException("setBulkRequests: " + bulk_requests + " must be greater than 0.");
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getBulkRequests() {
/*  209 */     return this.rq.size();
/*      */   }
/*      */ 
/*      */   
/*      */   public ChannelSftp() {
/*  214 */     setLocalWindowSizeMax(2097152);
/*  215 */     setLocalWindowSize(2097152);
/*  216 */     setLocalPacketSize(32768);
/*      */   }
/*      */ 
/*      */   
/*      */   void init() {}
/*      */ 
/*      */   
/*      */   public void start() throws JSchException {
/*      */     try {
/*  225 */       PipedOutputStream pos = new PipedOutputStream();
/*  226 */       this.io.setOutputStream(pos);
/*  227 */       PipedInputStream pis = new Channel.MyPipedInputStream(this, pos, this.rmpsize);
/*  228 */       this.io.setInputStream(pis);
/*      */       
/*  230 */       this.io_in = this.io.in;
/*      */       
/*  232 */       if (this.io_in == null) {
/*  233 */         throw new JSchException("channel is down");
/*      */       }
/*      */       
/*  236 */       Request request = new RequestSftp();
/*  237 */       request.request(getSession(), this);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  246 */       this.buf = new Buffer(this.lmpsize);
/*  247 */       this.packet = new Packet(this.buf);
/*      */       
/*  249 */       this.obuf = new Buffer(this.rmpsize);
/*  250 */       this.opacket = new Packet(this.obuf);
/*      */       
/*  252 */       int i = 0;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  258 */       sendINIT();
/*      */ 
/*      */       
/*  261 */       Header header = new Header();
/*  262 */       header = header(this.buf, header);
/*  263 */       int length = header.length;
/*  264 */       if (length > 262144) {
/*  265 */         throw new SftpException(4, "Received message is too long: " + length);
/*      */       }
/*      */       
/*  268 */       int type = header.type;
/*  269 */       this.server_version = header.rid;
/*      */       
/*  271 */       this.extensions = new Hashtable();
/*  272 */       if (length > 0) {
/*      */         
/*  274 */         fill(this.buf, length);
/*  275 */         byte[] extension_name = null;
/*  276 */         byte[] extension_data = null;
/*  277 */         while (length > 0) {
/*  278 */           extension_name = this.buf.getString();
/*  279 */           length -= 4 + extension_name.length;
/*  280 */           extension_data = this.buf.getString();
/*  281 */           length -= 4 + extension_data.length;
/*  282 */           this.extensions.put(Util.byte2str(extension_name), Util.byte2str(extension_data));
/*      */         } 
/*      */       } 
/*      */ 
/*      */       
/*  287 */       if (this.extensions.get("posix-rename@openssh.com") != null && this.extensions.get("posix-rename@openssh.com").equals("1"))
/*      */       {
/*  289 */         this.extension_posix_rename = true;
/*      */       }
/*      */       
/*  292 */       if (this.extensions.get("statvfs@openssh.com") != null && this.extensions.get("statvfs@openssh.com").equals("2"))
/*      */       {
/*  294 */         this.extension_statvfs = true;
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  304 */       if (this.extensions.get("hardlink@openssh.com") != null && this.extensions.get("hardlink@openssh.com").equals("1"))
/*      */       {
/*  306 */         this.extension_hardlink = true;
/*      */       }
/*      */       
/*  309 */       this.lcwd = (new File(".")).getCanonicalPath();
/*      */     }
/*  311 */     catch (Exception e) {
/*      */       
/*  313 */       if (e instanceof JSchException) throw (JSchException)e; 
/*  314 */       if (e instanceof Throwable)
/*  315 */         throw new JSchException(e.toString(), e); 
/*  316 */       throw new JSchException(e.toString());
/*      */     } 
/*      */   }
/*      */   
/*  320 */   public void quit() { disconnect(); } public void exit() {
/*  321 */     disconnect();
/*      */   } public void lcd(String path) throws SftpException {
/*  323 */     path = localAbsolutePath(path);
/*  324 */     if ((new File(path)).isDirectory()) {
/*      */       try {
/*  326 */         path = (new File(path)).getCanonicalPath();
/*      */       }
/*  328 */       catch (Exception e) {}
/*  329 */       this.lcwd = path;
/*      */       return;
/*      */     } 
/*  332 */     throw new SftpException(2, "No such directory");
/*      */   }
/*      */   
/*      */   public void cd(String path) throws SftpException {
/*      */     try {
/*  337 */       ((Channel.MyPipedInputStream)this.io_in).updateReadSide();
/*      */       
/*  339 */       path = remoteAbsolutePath(path);
/*  340 */       path = isUnique(path);
/*      */       
/*  342 */       byte[] str = _realpath(path);
/*  343 */       SftpATTRS attr = _stat(str);
/*      */       
/*  345 */       if ((attr.getFlags() & 0x4) == 0) {
/*  346 */         throw new SftpException(4, "Can't change directory: " + path);
/*      */       }
/*      */       
/*  349 */       if (!attr.isDir()) {
/*  350 */         throw new SftpException(4, "Can't change directory: " + path);
/*      */       }
/*      */ 
/*      */       
/*  354 */       setCwd(Util.byte2str(str, this.fEncoding));
/*      */     }
/*  356 */     catch (Exception e) {
/*  357 */       if (e instanceof SftpException) throw (SftpException)e; 
/*  358 */       if (e instanceof Throwable)
/*  359 */         throw new SftpException(4, "", e); 
/*  360 */       throw new SftpException(4, "");
/*      */     } 
/*      */   }
/*      */   
/*      */   public void put(String src, String dst) throws SftpException {
/*  365 */     put(src, dst, (SftpProgressMonitor)null, 0);
/*      */   }
/*      */   public void put(String src, String dst, int mode) throws SftpException {
/*  368 */     put(src, dst, (SftpProgressMonitor)null, mode);
/*      */   }
/*      */   
/*      */   public void put(String src, String dst, SftpProgressMonitor monitor) throws SftpException {
/*  372 */     put(src, dst, monitor, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void put(String src, String dst, SftpProgressMonitor monitor, int mode) throws SftpException {
/*      */     try {
/*  389 */       ((Channel.MyPipedInputStream)this.io_in).updateReadSide();
/*      */       
/*  391 */       src = localAbsolutePath(src);
/*  392 */       dst = remoteAbsolutePath(dst);
/*      */       
/*  394 */       Vector v = glob_remote(dst);
/*  395 */       int vsize = v.size();
/*  396 */       if (vsize != 1) {
/*  397 */         if (vsize == 0) {
/*  398 */           if (isPattern(dst)) {
/*  399 */             throw new SftpException(4, dst);
/*      */           }
/*  401 */           dst = Util.unquote(dst);
/*      */         } 
/*  403 */         throw new SftpException(4, v.toString());
/*      */       } 
/*      */       
/*  406 */       dst = v.elementAt(0);
/*      */ 
/*      */       
/*  409 */       boolean isRemoteDir = isRemoteDir(dst);
/*      */       
/*  411 */       v = glob_local(src);
/*  412 */       vsize = v.size();
/*      */       
/*  414 */       StringBuffer dstsb = null;
/*  415 */       if (isRemoteDir) {
/*  416 */         if (!dst.endsWith("/")) {
/*  417 */           dst = dst + "/";
/*      */         }
/*  419 */         dstsb = new StringBuffer(dst);
/*      */       }
/*  421 */       else if (vsize > 1) {
/*  422 */         throw new SftpException(4, "Copying multiple files, but the destination is missing or a file.");
/*      */       } 
/*      */ 
/*      */       
/*  426 */       for (int j = 0; j < vsize; j++) {
/*  427 */         String _src = v.elementAt(j);
/*  428 */         String _dst = null;
/*  429 */         if (isRemoteDir) {
/*  430 */           int i = _src.lastIndexOf(file_separatorc);
/*  431 */           if (fs_is_bs) {
/*  432 */             int ii = _src.lastIndexOf('/');
/*  433 */             if (ii != -1 && ii > i)
/*  434 */               i = ii; 
/*      */           } 
/*  436 */           if (i == -1) { dstsb.append(_src); }
/*  437 */           else { dstsb.append(_src.substring(i + 1)); }
/*  438 */            _dst = dstsb.toString();
/*  439 */           dstsb.delete(dst.length(), _dst.length());
/*      */         } else {
/*      */           
/*  442 */           _dst = dst;
/*      */         } 
/*      */ 
/*      */         
/*  446 */         long size_of_dst = 0L;
/*  447 */         if (mode == 1) {
/*      */           try {
/*  449 */             SftpATTRS attr = _stat(_dst);
/*  450 */             size_of_dst = attr.getSize();
/*      */           }
/*  452 */           catch (Exception eee) {}
/*      */ 
/*      */           
/*  455 */           long size_of_src = (new File(_src)).length();
/*  456 */           if (size_of_src < size_of_dst) {
/*  457 */             throw new SftpException(4, "failed to resume for " + _dst);
/*      */           }
/*      */           
/*  460 */           if (size_of_src == size_of_dst) {
/*      */             return;
/*      */           }
/*      */         } 
/*      */         
/*  465 */         if (monitor != null) {
/*  466 */           monitor.init(0, _src, _dst, (new File(_src)).length());
/*      */           
/*  468 */           if (mode == 1) {
/*  469 */             monitor.count(size_of_dst);
/*      */           }
/*      */         } 
/*  472 */         FileInputStream fis = null;
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     }
/*  484 */     catch (Exception e) {
/*  485 */       if (e instanceof SftpException) throw (SftpException)e; 
/*  486 */       if (e instanceof Throwable)
/*  487 */         throw new SftpException(4, e.toString(), e); 
/*  488 */       throw new SftpException(4, e.toString());
/*      */     } 
/*      */   }
/*      */   public void put(InputStream src, String dst) throws SftpException {
/*  492 */     put(src, dst, (SftpProgressMonitor)null, 0);
/*      */   }
/*      */   public void put(InputStream src, String dst, int mode) throws SftpException {
/*  495 */     put(src, dst, (SftpProgressMonitor)null, mode);
/*      */   }
/*      */   
/*      */   public void put(InputStream src, String dst, SftpProgressMonitor monitor) throws SftpException {
/*  499 */     put(src, dst, monitor, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void put(InputStream src, String dst, SftpProgressMonitor monitor, int mode) throws SftpException {
/*      */     try {
/*  515 */       ((Channel.MyPipedInputStream)this.io_in).updateReadSide();
/*      */       
/*  517 */       dst = remoteAbsolutePath(dst);
/*      */       
/*  519 */       Vector v = glob_remote(dst);
/*  520 */       int vsize = v.size();
/*  521 */       if (vsize != 1) {
/*  522 */         if (vsize == 0) {
/*  523 */           if (isPattern(dst)) {
/*  524 */             throw new SftpException(4, dst);
/*      */           }
/*  526 */           dst = Util.unquote(dst);
/*      */         } 
/*  528 */         throw new SftpException(4, v.toString());
/*      */       } 
/*      */       
/*  531 */       dst = v.elementAt(0);
/*      */ 
/*      */       
/*  534 */       if (monitor != null) {
/*  535 */         monitor.init(0, "-", dst, -1L);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*  540 */       _put(src, dst, monitor, mode);
/*      */     }
/*  542 */     catch (Exception e) {
/*  543 */       if (e instanceof SftpException) {
/*  544 */         if (((SftpException)e).id == 4 && isRemoteDir(dst))
/*      */         {
/*  546 */           throw new SftpException(4, dst + " is a directory");
/*      */         }
/*  548 */         throw (SftpException)e;
/*      */       } 
/*  550 */       if (e instanceof Throwable)
/*  551 */         throw new SftpException(4, e.toString(), e); 
/*  552 */       throw new SftpException(4, e.toString());
/*      */     } 
/*      */   }
/*      */   
/*      */   public void _put(InputStream src, String dst, SftpProgressMonitor monitor, int mode) throws SftpException {
/*      */     try {
/*      */       int count;
/*  559 */       ((Channel.MyPipedInputStream)this.io_in).updateReadSide();
/*      */       
/*  561 */       byte[] dstb = Util.str2byte(dst, this.fEncoding);
/*  562 */       long skip = 0L;
/*  563 */       if (mode == 1 || mode == 2) {
/*      */         try {
/*  565 */           SftpATTRS attr = _stat(dstb);
/*  566 */           skip = attr.getSize();
/*      */         }
/*  568 */         catch (Exception eee) {}
/*      */       }
/*      */ 
/*      */       
/*  572 */       if (mode == 1 && skip > 0L) {
/*  573 */         long skipped = src.skip(skip);
/*  574 */         if (skipped < skip) {
/*  575 */           throw new SftpException(4, "failed to resume for " + dst);
/*      */         }
/*      */       } 
/*      */       
/*  579 */       if (mode == 0) { sendOPENW(dstb); }
/*  580 */       else { sendOPENA(dstb); }
/*      */       
/*  582 */       Header header = new Header();
/*  583 */       header = header(this.buf, header);
/*  584 */       int length = header.length;
/*  585 */       int type = header.type;
/*      */       
/*  587 */       fill(this.buf, length);
/*      */       
/*  589 */       if (type != 101 && type != 102) {
/*  590 */         throw new SftpException(4, "invalid type=" + type);
/*      */       }
/*  592 */       if (type == 101) {
/*  593 */         int i = this.buf.getInt();
/*  594 */         throwStatusError(this.buf, i);
/*      */       } 
/*  596 */       byte[] handle = this.buf.getString();
/*  597 */       byte[] data = null;
/*      */       
/*  599 */       boolean dontcopy = true;
/*      */       
/*  601 */       if (!dontcopy) {
/*  602 */         data = new byte[this.obuf.buffer.length - 39 + handle.length + 128];
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  608 */       long offset = 0L;
/*  609 */       if (mode == 1 || mode == 2) {
/*  610 */         offset += skip;
/*      */       }
/*      */       
/*  613 */       int startid = this.seq;
/*  614 */       int ackcount = 0;
/*  615 */       int _s = 0;
/*  616 */       int _datalen = 0;
/*      */       
/*  618 */       if (!dontcopy) {
/*  619 */         _datalen = data.length;
/*      */       } else {
/*      */         
/*  622 */         data = this.obuf.buffer;
/*  623 */         _s = 39 + handle.length;
/*  624 */         _datalen = this.obuf.buffer.length - _s - 128;
/*      */       } 
/*      */       
/*  627 */       int bulk_requests = this.rq.size();
/*      */       
/*      */       do {
/*  630 */         int nread = 0;
/*  631 */         count = 0;
/*  632 */         int s = _s;
/*  633 */         int datalen = _datalen;
/*      */         
/*      */         do {
/*  636 */           nread = src.read(data, s, datalen);
/*  637 */           if (nread <= 0)
/*  638 */             continue;  s += nread;
/*  639 */           datalen -= nread;
/*  640 */           count += nread;
/*      */         
/*      */         }
/*  643 */         while (datalen > 0 && nread > 0);
/*  644 */         if (count <= 0)
/*      */           break; 
/*  646 */         int foo = count;
/*  647 */         while (foo > 0) {
/*  648 */           if (this.seq - 1 == startid || this.seq - startid - ackcount >= bulk_requests)
/*      */           {
/*  650 */             while (this.seq - startid - ackcount >= bulk_requests && 
/*  651 */               checkStatus(this.ackid, header)) {
/*  652 */               int _ackid = this.ackid[0];
/*  653 */               if (startid > _ackid || _ackid > this.seq - 1) {
/*  654 */                 if (_ackid == this.seq) {
/*  655 */                   System.err.println("ack error: startid=" + startid + " seq=" + this.seq + " _ackid=" + _ackid);
/*      */                 } else {
/*      */                   
/*  658 */                   throw new SftpException(4, "ack error: startid=" + startid + " seq=" + this.seq + " _ackid=" + _ackid);
/*      */                 } 
/*      */               }
/*  661 */               ackcount++;
/*      */             } 
/*      */           }
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  668 */           if (dontcopy) {
/*  669 */             foo -= sendWRITE(handle, offset, data, 0, foo);
/*  670 */             if (data != this.obuf.buffer) {
/*  671 */               data = this.obuf.buffer;
/*  672 */               _datalen = this.obuf.buffer.length - _s - 128;
/*      */             } 
/*      */             continue;
/*      */           } 
/*  676 */           foo -= sendWRITE(handle, offset, data, _s, foo);
/*      */         } 
/*      */         
/*  679 */         offset += count;
/*  680 */       } while (monitor == null || monitor.count(count));
/*      */ 
/*      */ 
/*      */       
/*  684 */       int _ackcount = this.seq - startid;
/*  685 */       while (_ackcount > ackcount && 
/*  686 */         checkStatus((int[])null, header))
/*      */       {
/*      */         
/*  689 */         ackcount++;
/*      */       }
/*  691 */       if (monitor != null) monitor.end(); 
/*  692 */       _sendCLOSE(handle, header);
/*      */     }
/*  694 */     catch (Exception e) {
/*  695 */       if (e instanceof SftpException) throw (SftpException)e; 
/*  696 */       if (e instanceof Throwable)
/*  697 */         throw new SftpException(4, e.toString(), e); 
/*  698 */       throw new SftpException(4, e.toString());
/*      */     } 
/*      */   }
/*      */   
/*      */   public OutputStream put(String dst) throws SftpException {
/*  703 */     return put(dst, (SftpProgressMonitor)null, 0);
/*      */   }
/*      */   public OutputStream put(String dst, int mode) throws SftpException {
/*  706 */     return put(dst, (SftpProgressMonitor)null, mode);
/*      */   }
/*      */   public OutputStream put(String dst, SftpProgressMonitor monitor, int mode) throws SftpException {
/*  709 */     return put(dst, monitor, mode, 0L);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public OutputStream put(String dst, final SftpProgressMonitor monitor, int mode, long offset) throws SftpException {
/*      */     try {
/*  725 */       ((Channel.MyPipedInputStream)this.io_in).updateReadSide();
/*      */       
/*  727 */       dst = remoteAbsolutePath(dst);
/*  728 */       dst = isUnique(dst);
/*      */       
/*  730 */       if (isRemoteDir(dst)) {
/*  731 */         throw new SftpException(4, dst + " is a directory");
/*      */       }
/*      */       
/*  734 */       byte[] dstb = Util.str2byte(dst, this.fEncoding);
/*      */       
/*  736 */       long skip = 0L;
/*  737 */       if (mode == 1 || mode == 2) {
/*      */         try {
/*  739 */           SftpATTRS attr = _stat(dstb);
/*  740 */           skip = attr.getSize();
/*      */         }
/*  742 */         catch (Exception eee) {}
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*  747 */       if (monitor != null) {
/*  748 */         monitor.init(0, "-", dst, -1L);
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*  753 */       if (mode == 0) { sendOPENW(dstb); }
/*  754 */       else { sendOPENA(dstb); }
/*      */       
/*  756 */       Header header = new Header();
/*  757 */       header = header(this.buf, header);
/*  758 */       int length = header.length;
/*  759 */       int type = header.type;
/*      */       
/*  761 */       fill(this.buf, length);
/*      */       
/*  763 */       if (type != 101 && type != 102) {
/*  764 */         throw new SftpException(4, "");
/*      */       }
/*  766 */       if (type == 101) {
/*  767 */         int i = this.buf.getInt();
/*  768 */         throwStatusError(this.buf, i);
/*      */       } 
/*  770 */       final byte[] handle = this.buf.getString();
/*      */       
/*  772 */       if (mode == 1 || mode == 2) {
/*  773 */         offset += skip;
/*      */       }
/*      */       
/*  776 */       final long[] _offset = new long[1];
/*  777 */       _offset[0] = offset;
/*  778 */       OutputStream out = new OutputStream() {
/*      */           private boolean init = true;
/*      */           private boolean isClosed = false;
/*  781 */           private int[] ackid = new int[1];
/*  782 */           private int startid = 0;
/*  783 */           private int _ackid = 0;
/*  784 */           private int ackcount = 0;
/*  785 */           private int writecount = 0;
/*  786 */           private ChannelSftp.Header header = new ChannelSftp.Header();
/*      */           
/*      */           public void write(byte[] d) throws IOException {
/*  789 */             write(d, 0, d.length);
/*      */           }
/*      */           
/*      */           public void write(byte[] d, int s, int len) throws IOException {
/*  793 */             if (this.init) {
/*  794 */               this.startid = ChannelSftp.this.seq;
/*  795 */               this._ackid = ChannelSftp.this.seq;
/*  796 */               this.init = false;
/*      */             } 
/*      */             
/*  799 */             if (this.isClosed) {
/*  800 */               throw new IOException("stream already closed");
/*      */             }
/*      */ 
/*      */             
/*  804 */             try { int _len = len;
/*  805 */               while (_len > 0) {
/*  806 */                 int sent = ChannelSftp.this.sendWRITE(handle, _offset[0], d, s, _len);
/*  807 */                 this.writecount++;
/*  808 */                 _offset[0] = _offset[0] + sent;
/*  809 */                 s += sent;
/*  810 */                 _len -= sent;
/*  811 */                 if (ChannelSftp.this.seq - 1 == this.startid || ChannelSftp.this.io_in.available() >= 1024)
/*      */                 {
/*  813 */                   while (ChannelSftp.this.io_in.available() > 0 && 
/*  814 */                     ChannelSftp.this.checkStatus(this.ackid, this.header)) {
/*  815 */                     this._ackid = this.ackid[0];
/*  816 */                     if (this.startid > this._ackid || this._ackid > ChannelSftp.this.seq - 1) {
/*  817 */                       throw new SftpException(4, "");
/*      */                     }
/*  819 */                     this.ackcount++;
/*      */                   } 
/*      */                 }
/*      */               } 
/*      */ 
/*      */ 
/*      */ 
/*      */               
/*  827 */               if (monitor != null && !monitor.count(len)) {
/*  828 */                 close();
/*  829 */                 throw new IOException("canceled");
/*      */               }  }
/*      */             catch (IOException e)
/*  832 */             { throw e; }
/*  833 */             catch (Exception e) { throw new IOException(e.toString()); }
/*      */           
/*      */           }
/*  836 */           byte[] _data = new byte[1]; private final byte[] val$handle; private final long[] val$_offset; private final SftpProgressMonitor val$monitor; private final ChannelSftp this$0;
/*      */           public void write(int foo) throws IOException {
/*  838 */             this._data[0] = (byte)foo;
/*  839 */             write(this._data, 0, 1);
/*      */           }
/*      */ 
/*      */           
/*      */           public void flush() throws IOException {
/*  844 */             if (this.isClosed) {
/*  845 */               throw new IOException("stream already closed");
/*      */             }
/*      */             
/*  848 */             if (!this.init) {
/*      */               try {
/*  850 */                 while (this.writecount > this.ackcount && 
/*  851 */                   ChannelSftp.this.checkStatus((int[])null, this.header))
/*      */                 {
/*      */                   
/*  854 */                   this.ackcount++;
/*      */                 }
/*      */               }
/*  857 */               catch (SftpException e) {
/*  858 */                 throw new IOException(e.toString());
/*      */               } 
/*      */             }
/*      */           }
/*      */           
/*      */           public void close() throws IOException {
/*  864 */             if (this.isClosed) {
/*      */               return;
/*      */             }
/*  867 */             flush();
/*  868 */             if (monitor != null) monitor.end();  
/*  869 */             try { ChannelSftp.this._sendCLOSE(handle, this.header); }
/*  870 */             catch (IOException e) { throw e; }
/*  871 */             catch (Exception e)
/*  872 */             { throw new IOException(e.toString()); }
/*      */             
/*  874 */             this.isClosed = true;
/*      */           }
/*      */         };
/*  877 */       return out;
/*      */     }
/*  879 */     catch (Exception e) {
/*  880 */       if (e instanceof SftpException) throw (SftpException)e; 
/*  881 */       if (e instanceof Throwable)
/*  882 */         throw new SftpException(4, "", e); 
/*  883 */       throw new SftpException(4, "");
/*      */     } 
/*      */   }
/*      */   
/*      */   public void get(String src, String dst) throws SftpException {
/*  888 */     get(src, dst, (SftpProgressMonitor)null, 0);
/*      */   }
/*      */   
/*      */   public void get(String src, String dst, SftpProgressMonitor monitor) throws SftpException {
/*  892 */     get(src, dst, monitor, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public void get(String src, String dst, SftpProgressMonitor monitor, int mode) throws SftpException {
/*  898 */     boolean _dstExist = false;
/*  899 */     String _dst = null;
/*      */     try {
/*  901 */       ((Channel.MyPipedInputStream)this.io_in).updateReadSide();
/*      */       
/*  903 */       src = remoteAbsolutePath(src);
/*  904 */       dst = localAbsolutePath(dst);
/*      */       
/*  906 */       Vector v = glob_remote(src);
/*  907 */       int vsize = v.size();
/*  908 */       if (vsize == 0) {
/*  909 */         throw new SftpException(2, "No such file");
/*      */       }
/*      */       
/*  912 */       File dstFile = new File(dst);
/*  913 */       boolean isDstDir = dstFile.isDirectory();
/*  914 */       StringBuffer dstsb = null;
/*  915 */       if (isDstDir) {
/*  916 */         if (!dst.endsWith(file_separator)) {
/*  917 */           dst = dst + file_separator;
/*      */         }
/*  919 */         dstsb = new StringBuffer(dst);
/*      */       }
/*  921 */       else if (vsize > 1) {
/*  922 */         throw new SftpException(4, "Copying multiple files, but destination is missing or a file.");
/*      */       } 
/*      */ 
/*      */       
/*  926 */       for (int j = 0; j < vsize; j++) {
/*  927 */         String _src = v.elementAt(j);
/*  928 */         SftpATTRS attr = _stat(_src);
/*  929 */         if (attr.isDir()) {
/*  930 */           throw new SftpException(4, "not supported to get directory " + _src);
/*      */         }
/*      */ 
/*      */         
/*  934 */         _dst = null;
/*  935 */         if (isDstDir) {
/*  936 */           int i = _src.lastIndexOf('/');
/*  937 */           if (i == -1) { dstsb.append(_src); }
/*  938 */           else { dstsb.append(_src.substring(i + 1)); }
/*  939 */            _dst = dstsb.toString();
/*  940 */           if (_dst.indexOf("..") != -1) {
/*  941 */             String dstc = (new File(dst)).getCanonicalPath();
/*  942 */             String _dstc = (new File(_dst)).getCanonicalPath();
/*  943 */             if (_dstc.length() <= dstc.length() || !_dstc.substring(0, dstc.length() + 1).equals(dstc + file_separator))
/*      */             {
/*  945 */               throw new SftpException(4, "writing to an unexpected file " + _src);
/*      */             }
/*      */           } 
/*      */           
/*  949 */           dstsb.delete(dst.length(), _dst.length());
/*      */         } else {
/*      */           
/*  952 */           _dst = dst;
/*      */         } 
/*      */         
/*  955 */         File _dstFile = new File(_dst);
/*  956 */         if (mode == 1) {
/*  957 */           long size_of_src = attr.getSize();
/*  958 */           long size_of_dst = _dstFile.length();
/*  959 */           if (size_of_dst > size_of_src) {
/*  960 */             throw new SftpException(4, "failed to resume for " + _dst);
/*      */           }
/*      */           
/*  963 */           if (size_of_dst == size_of_src) {
/*      */             return;
/*      */           }
/*      */         } 
/*      */         
/*  968 */         if (monitor != null) {
/*  969 */           monitor.init(1, _src, _dst, attr.getSize());
/*  970 */           if (mode == 1) {
/*  971 */             monitor.count(_dstFile.length());
/*      */           }
/*      */         } 
/*      */         
/*  975 */         FileOutputStream fos = null;
/*  976 */         _dstExist = _dstFile.exists();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*      */       }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     }
/*  994 */     catch (Exception e) {
/*  995 */       if (!_dstExist && _dst != null) {
/*  996 */         File _dstFile = new File(_dst);
/*  997 */         if (_dstFile.exists() && _dstFile.length() == 0L) {
/*  998 */           _dstFile.delete();
/*      */         }
/*      */       } 
/* 1001 */       if (e instanceof SftpException) throw (SftpException)e; 
/* 1002 */       if (e instanceof Throwable)
/* 1003 */         throw new SftpException(4, "", e); 
/* 1004 */       throw new SftpException(4, "");
/*      */     } 
/*      */   }
/*      */   public void get(String src, OutputStream dst) throws SftpException {
/* 1008 */     get(src, dst, (SftpProgressMonitor)null, 0, 0L);
/*      */   }
/*      */   
/*      */   public void get(String src, OutputStream dst, SftpProgressMonitor monitor) throws SftpException {
/* 1012 */     get(src, dst, monitor, 0, 0L);
/*      */   }
/*      */ 
/*      */   
/*      */   public void get(String src, OutputStream dst, SftpProgressMonitor monitor, int mode, long skip) throws SftpException {
/*      */     try {
/* 1018 */       ((Channel.MyPipedInputStream)this.io_in).updateReadSide();
/*      */       
/* 1020 */       src = remoteAbsolutePath(src);
/* 1021 */       src = isUnique(src);
/*      */       
/* 1023 */       if (monitor != null) {
/* 1024 */         SftpATTRS attr = _stat(src);
/* 1025 */         monitor.init(1, src, "??", attr.getSize());
/* 1026 */         if (mode == 1) {
/* 1027 */           monitor.count(skip);
/*      */         }
/*      */       } 
/* 1030 */       _get(src, dst, monitor, mode, skip);
/*      */     }
/* 1032 */     catch (Exception e) {
/* 1033 */       if (e instanceof SftpException) throw (SftpException)e; 
/* 1034 */       if (e instanceof Throwable)
/* 1035 */         throw new SftpException(4, "", e); 
/* 1036 */       throw new SftpException(4, "");
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void _get(String src, OutputStream dst, SftpProgressMonitor monitor, int mode, long skip) throws SftpException {
/* 1044 */     byte[] srcb = Util.str2byte(src, this.fEncoding);
/*      */     try {
/* 1046 */       sendOPENR(srcb);
/*      */       
/* 1048 */       Header header = new Header();
/* 1049 */       header = header(this.buf, header);
/* 1050 */       int length = header.length;
/* 1051 */       int type = header.type;
/*      */       
/* 1053 */       fill(this.buf, length);
/*      */       
/* 1055 */       if (type != 101 && type != 102) {
/* 1056 */         throw new SftpException(4, "");
/*      */       }
/*      */       
/* 1059 */       if (type == 101) {
/* 1060 */         int i = this.buf.getInt();
/* 1061 */         throwStatusError(this.buf, i);
/*      */       } 
/*      */       
/* 1064 */       byte[] handle = this.buf.getString();
/*      */       
/* 1066 */       long offset = 0L;
/* 1067 */       if (mode == 1) {
/* 1068 */         offset += skip;
/*      */       }
/*      */       
/* 1071 */       int request_max = 1;
/* 1072 */       this.rq.init();
/* 1073 */       long request_offset = offset;
/*      */       
/* 1075 */       int request_len = this.buf.buffer.length - 13;
/* 1076 */       if (this.server_version == 0) request_len = 1024;
/*      */ 
/*      */ 
/*      */       
/*      */       label71: while (true) {
/* 1081 */         while (this.rq.count() < request_max) {
/* 1082 */           sendREAD(handle, request_offset, request_len, this.rq);
/* 1083 */           request_offset += request_len;
/*      */         } 
/*      */         
/* 1086 */         header = header(this.buf, header);
/* 1087 */         length = header.length;
/* 1088 */         type = header.type;
/*      */         
/* 1090 */         RequestQueue.Request rr = null;
/*      */         try {
/* 1092 */           rr = this.rq.get(header.rid);
/*      */         }
/* 1094 */         catch (OutOfOrderException e) {
/* 1095 */           request_offset = e.offset;
/* 1096 */           skip(header.length);
/* 1097 */           this.rq.cancel(header, this.buf);
/*      */           
/*      */           continue;
/*      */         } 
/* 1101 */         if (type == 101) {
/* 1102 */           fill(this.buf, length);
/* 1103 */           int i = this.buf.getInt();
/* 1104 */           if (i == 1) {
/*      */             break;
/*      */           }
/* 1107 */           throwStatusError(this.buf, i);
/*      */         } 
/*      */         
/* 1110 */         if (type != 103) {
/*      */           break;
/*      */         }
/*      */         
/* 1114 */         this.buf.rewind();
/* 1115 */         fill(this.buf.buffer, 0, 4); length -= 4;
/* 1116 */         int length_of_data = this.buf.getInt();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1128 */         int optional_data = length - length_of_data;
/*      */         
/* 1130 */         int foo = length_of_data;
/* 1131 */         while (foo > 0) {
/* 1132 */           int bar = foo;
/* 1133 */           if (bar > this.buf.buffer.length) {
/* 1134 */             bar = this.buf.buffer.length;
/*      */           }
/* 1136 */           int data_len = this.io_in.read(this.buf.buffer, 0, bar);
/* 1137 */           if (data_len < 0) {
/*      */             break label71;
/*      */           }
/*      */           
/* 1141 */           dst.write(this.buf.buffer, 0, data_len);
/*      */           
/* 1143 */           offset += data_len;
/* 1144 */           foo -= data_len;
/*      */           
/* 1146 */           if (monitor != null && 
/* 1147 */             !monitor.count(data_len)) {
/* 1148 */             skip(foo);
/* 1149 */             if (optional_data > 0) {
/* 1150 */               skip(optional_data);
/*      */               
/*      */               break label71;
/*      */             } 
/*      */             
/*      */             break label71;
/*      */           } 
/*      */         } 
/*      */         
/* 1159 */         if (optional_data > 0) {
/* 1160 */           skip(optional_data);
/*      */         }
/*      */         
/* 1163 */         if (length_of_data < rr.length) {
/* 1164 */           this.rq.cancel(header, this.buf);
/* 1165 */           sendREAD(handle, rr.offset + length_of_data, (int)(rr.length - length_of_data), this.rq);
/* 1166 */           request_offset = rr.offset + rr.length;
/*      */         } 
/*      */         
/* 1169 */         if (request_max < this.rq.size()) {
/* 1170 */           request_max++;
/*      */         }
/*      */       } 
/* 1173 */       dst.flush();
/*      */       
/* 1175 */       if (monitor != null) monitor.end();
/*      */       
/* 1177 */       this.rq.cancel(header, this.buf);
/*      */       
/* 1179 */       _sendCLOSE(handle, header);
/*      */     }
/* 1181 */     catch (Exception e) {
/* 1182 */       if (e instanceof SftpException) throw (SftpException)e; 
/* 1183 */       if (e instanceof Throwable)
/* 1184 */         throw new SftpException(4, "", e); 
/* 1185 */       throw new SftpException(4, "");
/*      */     } 
/*      */   }
/*      */   
/*      */   class OutOfOrderException extends Exception
/*      */   {
/*      */     long offset;
/*      */     private final ChannelSftp.RequestQueue this$1;
/*      */     
/* 1194 */     OutOfOrderException(long offset) { this.offset = offset; } } private class RequestQueue { class OutOfOrderException extends Exception { long offset; OutOfOrderException(long offset) { this.offset = offset; }
/*      */       
/*      */       private final ChannelSftp.RequestQueue this$1; }
/*      */ 
/*      */     
/*      */     class Request { int id;
/*      */       long offset;
/*      */       long length;
/*      */       private final ChannelSftp.RequestQueue this$1; }
/* 1203 */     Request[] rrq = null; int head; int count; private final ChannelSftp this$0;
/*      */     
/*      */     RequestQueue(int size) {
/* 1206 */       this.rrq = new Request[size];
/* 1207 */       for (int i = 0; i < this.rrq.length; i++) {
/* 1208 */         this.rrq[i] = new Request();
/*      */       }
/* 1210 */       init();
/*      */     }
/*      */     
/*      */     void init() {
/* 1214 */       this.head = this.count = 0;
/*      */     }
/*      */     
/*      */     void add(int id, long offset, int length) {
/* 1218 */       if (this.count == 0) this.head = 0; 
/* 1219 */       int tail = this.head + this.count;
/* 1220 */       if (tail >= this.rrq.length) tail -= this.rrq.length; 
/* 1221 */       (this.rrq[tail]).id = id;
/* 1222 */       (this.rrq[tail]).offset = offset;
/* 1223 */       (this.rrq[tail]).length = length;
/* 1224 */       this.count++;
/*      */     }
/*      */     
/*      */     Request get(int id) throws OutOfOrderException, SftpException {
/* 1228 */       this.count--;
/* 1229 */       int i = this.head;
/* 1230 */       this.head++;
/* 1231 */       if (this.head == this.rrq.length) this.head = 0; 
/* 1232 */       if ((this.rrq[i]).id != id) {
/* 1233 */         long offset = getOffset();
/* 1234 */         boolean find = false;
/* 1235 */         for (int j = 0; j < this.rrq.length; j++) {
/* 1236 */           if ((this.rrq[j]).id == id) {
/* 1237 */             find = true;
/* 1238 */             (this.rrq[j]).id = 0;
/*      */             break;
/*      */           } 
/*      */         } 
/* 1242 */         if (find)
/* 1243 */           throw new OutOfOrderException(offset); 
/* 1244 */         throw new SftpException(4, "RequestQueue: unknown request id " + id);
/*      */       } 
/*      */       
/* 1247 */       (this.rrq[i]).id = 0;
/* 1248 */       return this.rrq[i];
/*      */     }
/*      */     
/*      */     int count() {
/* 1252 */       return this.count;
/*      */     }
/*      */     
/*      */     int size() {
/* 1256 */       return this.rrq.length;
/*      */     }
/*      */     
/*      */     void cancel(ChannelSftp.Header header, Buffer buf) throws IOException {
/* 1260 */       int _count = this.count;
/* 1261 */       for (int i = 0; i < _count; i++) {
/* 1262 */         header = ChannelSftp.this.header(buf, header);
/* 1263 */         int length = header.length;
/* 1264 */         for (int j = 0; j < this.rrq.length; j++) {
/* 1265 */           if ((this.rrq[j]).id == header.rid) {
/* 1266 */             (this.rrq[j]).id = 0;
/*      */             break;
/*      */           } 
/*      */         } 
/* 1270 */         ChannelSftp.this.skip(length);
/*      */       } 
/* 1272 */       init();
/*      */     }
/*      */     
/*      */     long getOffset() {
/* 1276 */       long result = Long.MAX_VALUE;
/*      */       
/* 1278 */       for (int i = 0; i < this.rrq.length; i++) {
/* 1279 */         if ((this.rrq[i]).id != 0)
/*      */         {
/* 1281 */           if (result > (this.rrq[i]).offset)
/* 1282 */             result = (this.rrq[i]).offset; 
/*      */         }
/*      */       } 
/* 1285 */       return result;
/*      */     } }
/*      */   class Request { int id; long offset; long length; private final ChannelSftp.RequestQueue this$1; }
/*      */   
/*      */   public InputStream get(String src) throws SftpException {
/* 1290 */     return get(src, (SftpProgressMonitor)null, 0L);
/*      */   }
/*      */   public InputStream get(String src, SftpProgressMonitor monitor) throws SftpException {
/* 1293 */     return get(src, monitor, 0L);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public InputStream get(String src, int mode) throws SftpException {
/* 1300 */     return get(src, (SftpProgressMonitor)null, 0L);
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public InputStream get(String src, SftpProgressMonitor monitor, int mode) throws SftpException {
/* 1306 */     return get(src, monitor, 0L);
/*      */   }
/*      */   
/*      */   public InputStream get(String src, final SftpProgressMonitor monitor, final long skip) throws SftpException {
/*      */     try {
/* 1311 */       ((Channel.MyPipedInputStream)this.io_in).updateReadSide();
/*      */       
/* 1313 */       src = remoteAbsolutePath(src);
/* 1314 */       src = isUnique(src);
/*      */       
/* 1316 */       byte[] srcb = Util.str2byte(src, this.fEncoding);
/*      */       
/* 1318 */       SftpATTRS attr = _stat(srcb);
/* 1319 */       if (monitor != null) {
/* 1320 */         monitor.init(1, src, "??", attr.getSize());
/*      */       }
/*      */       
/* 1323 */       sendOPENR(srcb);
/*      */       
/* 1325 */       Header header = new Header();
/* 1326 */       header = header(this.buf, header);
/* 1327 */       int length = header.length;
/* 1328 */       int type = header.type;
/*      */       
/* 1330 */       fill(this.buf, length);
/*      */       
/* 1332 */       if (type != 101 && type != 102) {
/* 1333 */         throw new SftpException(4, "");
/*      */       }
/* 1335 */       if (type == 101) {
/* 1336 */         int i = this.buf.getInt();
/* 1337 */         throwStatusError(this.buf, i);
/*      */       } 
/*      */       
/* 1340 */       final byte[] handle = this.buf.getString();
/*      */       
/* 1342 */       this.rq.init();
/*      */       
/* 1344 */       InputStream in = new InputStream() {
/* 1345 */           long offset = skip;
/*      */           boolean closed = false;
/* 1347 */           int rest_length = 0;
/* 1348 */           byte[] _data = new byte[1];
/* 1349 */           byte[] rest_byte = new byte[1024];
/* 1350 */           ChannelSftp.Header header = new ChannelSftp.Header();
/* 1351 */           int request_max = 1;
/* 1352 */           long request_offset = this.offset; private final long val$skip; private final SftpProgressMonitor val$monitor; private final byte[] val$handle; private final ChannelSftp this$0;
/*      */           
/*      */           public int read() throws IOException {
/* 1355 */             if (this.closed) return -1; 
/* 1356 */             int i = read(this._data, 0, 1);
/* 1357 */             if (i == -1) return -1;
/*      */             
/* 1359 */             return this._data[0] & 0xFF;
/*      */           }
/*      */           
/*      */           public int read(byte[] d) throws IOException {
/* 1363 */             if (this.closed) return -1; 
/* 1364 */             return read(d, 0, d.length);
/*      */           }
/*      */           public int read(byte[] d, int s, int len) throws IOException {
/* 1367 */             if (this.closed) return -1; 
/* 1368 */             if (d == null) throw new NullPointerException(); 
/* 1369 */             if (s < 0 || len < 0 || s + len > d.length) {
/* 1370 */               throw new IndexOutOfBoundsException();
/*      */             }
/* 1372 */             if (len == 0) return 0;
/*      */             
/* 1374 */             if (this.rest_length > 0) {
/* 1375 */               int i = this.rest_length;
/* 1376 */               if (i > len) i = len; 
/* 1377 */               System.arraycopy(this.rest_byte, 0, d, s, i);
/* 1378 */               if (i != this.rest_length) {
/* 1379 */                 System.arraycopy(this.rest_byte, i, this.rest_byte, 0, this.rest_length - i);
/*      */               }
/*      */ 
/*      */               
/* 1383 */               if (monitor != null && 
/* 1384 */                 !monitor.count(i)) {
/* 1385 */                 close();
/* 1386 */                 return -1;
/*      */               } 
/*      */ 
/*      */               
/* 1390 */               this.rest_length -= i;
/* 1391 */               return i;
/*      */             } 
/*      */             
/* 1394 */             if (ChannelSftp.this.buf.buffer.length - 13 < len) {
/* 1395 */               len = ChannelSftp.this.buf.buffer.length - 13;
/*      */             }
/* 1397 */             if (ChannelSftp.this.server_version == 0 && len > 1024) {
/* 1398 */               len = 1024;
/*      */             }
/*      */             
/* 1401 */             if (ChannelSftp.this.rq.count() != 0);
/*      */ 
/*      */ 
/*      */             
/* 1405 */             int request_len = ChannelSftp.this.buf.buffer.length - 13;
/* 1406 */             if (ChannelSftp.this.server_version == 0) request_len = 1024;
/*      */             
/* 1408 */             while (ChannelSftp.this.rq.count() < this.request_max) {
/*      */               try {
/* 1410 */                 ChannelSftp.this.sendREAD(handle, this.request_offset, request_len, ChannelSftp.this.rq);
/*      */               } catch (Exception e) {
/* 1412 */                 throw new IOException("error");
/* 1413 */               }  this.request_offset += request_len;
/*      */             } 
/*      */ 
/*      */             
/* 1417 */             this.header = ChannelSftp.this.header(ChannelSftp.this.buf, this.header);
/* 1418 */             this.rest_length = this.header.length;
/* 1419 */             int type = this.header.type;
/* 1420 */             int id = this.header.rid;
/*      */             
/* 1422 */             ChannelSftp.RequestQueue.Request rr = null;
/*      */             try {
/* 1424 */               rr = ChannelSftp.this.rq.get(this.header.rid);
/*      */             }
/* 1426 */             catch (OutOfOrderException e) {
/* 1427 */               this.request_offset = e.offset;
/* 1428 */               skip(this.header.length);
/* 1429 */               ChannelSftp.this.rq.cancel(this.header, ChannelSftp.this.buf);
/* 1430 */               return 0;
/*      */             }
/* 1432 */             catch (SftpException e) {
/* 1433 */               throw new IOException("error: " + e.toString());
/*      */             } 
/*      */             
/* 1436 */             if (type != 101 && type != 103) {
/* 1437 */               throw new IOException("error");
/*      */             }
/* 1439 */             if (type == 101) {
/* 1440 */               ChannelSftp.this.fill(ChannelSftp.this.buf, this.rest_length);
/* 1441 */               int i = ChannelSftp.this.buf.getInt();
/* 1442 */               this.rest_length = 0;
/* 1443 */               if (i == 1) {
/* 1444 */                 close();
/* 1445 */                 return -1;
/*      */               } 
/*      */               
/* 1448 */               throw new IOException("error");
/*      */             } 
/*      */             
/* 1451 */             ChannelSftp.this.buf.rewind();
/* 1452 */             ChannelSftp.this.fill(ChannelSftp.this.buf.buffer, 0, 4);
/* 1453 */             int length_of_data = ChannelSftp.this.buf.getInt(); this.rest_length -= 4;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 1465 */             int optional_data = this.rest_length - length_of_data;
/*      */             
/* 1467 */             this.offset += length_of_data;
/* 1468 */             int foo = length_of_data;
/* 1469 */             if (foo > 0) {
/* 1470 */               int bar = foo;
/* 1471 */               if (bar > len) {
/* 1472 */                 bar = len;
/*      */               }
/* 1474 */               int i = ChannelSftp.this.io_in.read(d, s, bar);
/* 1475 */               if (i < 0) {
/* 1476 */                 return -1;
/*      */               }
/* 1478 */               foo -= i;
/* 1479 */               this.rest_length = foo;
/*      */               
/* 1481 */               if (foo > 0) {
/* 1482 */                 if (this.rest_byte.length < foo) {
/* 1483 */                   this.rest_byte = new byte[foo];
/*      */                 }
/* 1485 */                 int _s = 0;
/* 1486 */                 int _len = foo;
/*      */                 
/* 1488 */                 while (_len > 0) {
/* 1489 */                   int j = ChannelSftp.this.io_in.read(this.rest_byte, _s, _len);
/* 1490 */                   if (j <= 0)
/* 1491 */                     break;  _s += j;
/* 1492 */                   _len -= j;
/*      */                 } 
/*      */               } 
/*      */               
/* 1496 */               if (optional_data > 0) {
/* 1497 */                 ChannelSftp.this.io_in.skip(optional_data);
/*      */               }
/*      */               
/* 1500 */               if (length_of_data < rr.length) {
/* 1501 */                 ChannelSftp.this.rq.cancel(this.header, ChannelSftp.this.buf);
/*      */                 try {
/* 1503 */                   ChannelSftp.this.sendREAD(handle, rr.offset + length_of_data, (int)(rr.length - length_of_data), ChannelSftp.this.rq);
/*      */                 }
/*      */                 catch (Exception e) {
/*      */                   
/* 1507 */                   throw new IOException("error");
/* 1508 */                 }  this.request_offset = rr.offset + rr.length;
/*      */               } 
/*      */               
/* 1511 */               if (this.request_max < ChannelSftp.this.rq.size()) {
/* 1512 */                 this.request_max++;
/*      */               }
/*      */               
/* 1515 */               if (monitor != null && 
/* 1516 */                 !monitor.count(i)) {
/* 1517 */                 close();
/* 1518 */                 return -1;
/*      */               } 
/*      */ 
/*      */               
/* 1522 */               return i;
/*      */             } 
/* 1524 */             return 0;
/*      */           }
/*      */           
/* 1527 */           public void close() throws IOException { if (this.closed)
/* 1528 */               return;  this.closed = true;
/* 1529 */             if (monitor != null) monitor.end(); 
/* 1530 */             ChannelSftp.this.rq.cancel(this.header, ChannelSftp.this.buf); 
/* 1531 */             try { ChannelSftp.this._sendCLOSE(handle, this.header); }
/* 1532 */             catch (Exception e) { throw new IOException("error"); }
/*      */              }
/*      */         };
/* 1535 */       return in;
/*      */     }
/* 1537 */     catch (Exception e) {
/* 1538 */       if (e instanceof SftpException) throw (SftpException)e; 
/* 1539 */       if (e instanceof Throwable)
/* 1540 */         throw new SftpException(4, "", e); 
/* 1541 */       throw new SftpException(4, "");
/*      */     } 
/*      */   }
/*      */   
/*      */   public Vector ls(String path) throws SftpException {
/* 1546 */     final Vector v = new Vector();
/* 1547 */     LsEntrySelector selector = new LsEntrySelector() { private final Vector val$v; private final ChannelSftp this$0;
/*      */         public int select(ChannelSftp.LsEntry entry) {
/* 1549 */           v.addElement(entry);
/* 1550 */           return 0;
/*      */         } }
/*      */       ;
/* 1553 */     ls(path, selector);
/* 1554 */     return v;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void ls(String path, LsEntrySelector selector) throws SftpException {
/*      */     try {
/* 1570 */       ((Channel.MyPipedInputStream)this.io_in).updateReadSide();
/*      */       
/* 1572 */       path = remoteAbsolutePath(path);
/* 1573 */       byte[] pattern = null;
/* 1574 */       Vector v = new Vector();
/*      */       
/* 1576 */       int foo = path.lastIndexOf('/');
/* 1577 */       String dir = path.substring(0, (foo == 0) ? 1 : foo);
/* 1578 */       String _pattern = path.substring(foo + 1);
/* 1579 */       dir = Util.unquote(dir);
/*      */ 
/*      */ 
/*      */       
/* 1583 */       byte[][] _pattern_utf8 = new byte[1][];
/* 1584 */       boolean pattern_has_wildcard = isPattern(_pattern, _pattern_utf8);
/*      */       
/* 1586 */       if (pattern_has_wildcard) {
/* 1587 */         pattern = _pattern_utf8[0];
/*      */       } else {
/*      */         
/* 1590 */         String upath = Util.unquote(path);
/*      */         
/* 1592 */         SftpATTRS attr = _stat(upath);
/* 1593 */         if (attr.isDir()) {
/* 1594 */           pattern = null;
/* 1595 */           dir = upath;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*      */         }
/* 1607 */         else if (this.fEncoding_is_utf8) {
/* 1608 */           pattern = _pattern_utf8[0];
/* 1609 */           pattern = Util.unquote(pattern);
/*      */         } else {
/*      */           
/* 1612 */           _pattern = Util.unquote(_pattern);
/* 1613 */           pattern = Util.str2byte(_pattern, this.fEncoding);
/*      */         } 
/*      */       } 
/*      */ 
/*      */ 
/*      */       
/* 1619 */       sendOPENDIR(Util.str2byte(dir, this.fEncoding));
/*      */       
/* 1621 */       Header header = new Header();
/* 1622 */       header = header(this.buf, header);
/* 1623 */       int length = header.length;
/* 1624 */       int type = header.type;
/*      */       
/* 1626 */       fill(this.buf, length);
/*      */       
/* 1628 */       if (type != 101 && type != 102) {
/* 1629 */         throw new SftpException(4, "");
/*      */       }
/* 1631 */       if (type == 101) {
/* 1632 */         int i = this.buf.getInt();
/* 1633 */         throwStatusError(this.buf, i);
/*      */       } 
/*      */       
/* 1636 */       int cancel = 0;
/* 1637 */       byte[] handle = this.buf.getString();
/*      */       
/* 1639 */       while (cancel == 0) {
/*      */         
/* 1641 */         sendREADDIR(handle);
/*      */         
/* 1643 */         header = header(this.buf, header);
/* 1644 */         length = header.length;
/* 1645 */         type = header.type;
/* 1646 */         if (type != 101 && type != 104) {
/* 1647 */           throw new SftpException(4, "");
/*      */         }
/* 1649 */         if (type == 101) {
/* 1650 */           fill(this.buf, length);
/* 1651 */           int i = this.buf.getInt();
/* 1652 */           if (i == 1)
/*      */             break; 
/* 1654 */           throwStatusError(this.buf, i);
/*      */         } 
/*      */         
/* 1657 */         this.buf.rewind();
/* 1658 */         fill(this.buf.buffer, 0, 4); length -= 4;
/* 1659 */         int count = this.buf.getInt();
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1664 */         this.buf.reset();
/* 1665 */         while (count > 0) {
/* 1666 */           if (length > 0) {
/* 1667 */             this.buf.shift();
/* 1668 */             int j = (this.buf.buffer.length > this.buf.index + length) ? length : (this.buf.buffer.length - this.buf.index);
/*      */ 
/*      */             
/* 1671 */             int i = fill(this.buf.buffer, this.buf.index, j);
/* 1672 */             this.buf.index += i;
/* 1673 */             length -= i;
/*      */           } 
/* 1675 */           byte[] filename = this.buf.getString();
/* 1676 */           byte[] longname = null;
/* 1677 */           if (this.server_version <= 3) {
/* 1678 */             longname = this.buf.getString();
/*      */           }
/* 1680 */           SftpATTRS attrs = SftpATTRS.getATTR(this.buf);
/*      */           
/* 1682 */           if (cancel == 1) {
/* 1683 */             count--;
/*      */             
/*      */             continue;
/*      */           } 
/* 1687 */           boolean find = false;
/* 1688 */           String f = null;
/* 1689 */           if (pattern == null) {
/* 1690 */             find = true;
/*      */           }
/* 1692 */           else if (!pattern_has_wildcard) {
/* 1693 */             find = Util.array_equals(pattern, filename);
/*      */           } else {
/*      */             
/* 1696 */             byte[] _filename = filename;
/* 1697 */             if (!this.fEncoding_is_utf8) {
/* 1698 */               f = Util.byte2str(_filename, this.fEncoding);
/* 1699 */               _filename = Util.str2byte(f, "UTF-8");
/*      */             } 
/* 1701 */             find = Util.glob(pattern, _filename);
/*      */           } 
/*      */           
/* 1704 */           if (find) {
/* 1705 */             if (f == null) {
/* 1706 */               f = Util.byte2str(filename, this.fEncoding);
/*      */             }
/* 1708 */             String l = null;
/* 1709 */             if (longname == null) {
/*      */ 
/*      */               
/* 1712 */               l = attrs.toString() + " " + f;
/*      */             } else {
/*      */               
/* 1715 */               l = Util.byte2str(longname, this.fEncoding);
/*      */             } 
/*      */             
/* 1718 */             cancel = selector.select(new LsEntry(f, l, attrs));
/*      */           } 
/*      */           
/* 1721 */           count--;
/*      */         } 
/*      */       } 
/* 1724 */       _sendCLOSE(handle, header);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     }
/* 1744 */     catch (Exception e) {
/* 1745 */       if (e instanceof SftpException) throw (SftpException)e; 
/* 1746 */       if (e instanceof Throwable)
/* 1747 */         throw new SftpException(4, "", e); 
/* 1748 */       throw new SftpException(4, "");
/*      */     } 
/*      */   }
/*      */   
/*      */   public String readlink(String path) throws SftpException {
/*      */     try {
/* 1754 */       if (this.server_version < 3) {
/* 1755 */         throw new SftpException(8, "The remote sshd is too old to support symlink operation.");
/*      */       }
/*      */ 
/*      */       
/* 1759 */       ((Channel.MyPipedInputStream)this.io_in).updateReadSide();
/*      */       
/* 1761 */       path = remoteAbsolutePath(path);
/*      */       
/* 1763 */       path = isUnique(path);
/*      */       
/* 1765 */       sendREADLINK(Util.str2byte(path, this.fEncoding));
/*      */       
/* 1767 */       Header header = new Header();
/* 1768 */       header = header(this.buf, header);
/* 1769 */       int length = header.length;
/* 1770 */       int type = header.type;
/*      */       
/* 1772 */       fill(this.buf, length);
/*      */       
/* 1774 */       if (type != 101 && type != 104) {
/* 1775 */         throw new SftpException(4, "");
/*      */       }
/* 1777 */       if (type == 104) {
/* 1778 */         int count = this.buf.getInt();
/* 1779 */         byte[] filename = null;
/* 1780 */         for (int j = 0; j < count; j++) {
/* 1781 */           filename = this.buf.getString();
/* 1782 */           if (this.server_version <= 3) {
/* 1783 */             byte[] longname = this.buf.getString();
/*      */           }
/* 1785 */           SftpATTRS.getATTR(this.buf);
/*      */         } 
/* 1787 */         return Util.byte2str(filename, this.fEncoding);
/*      */       } 
/*      */       
/* 1790 */       int i = this.buf.getInt();
/* 1791 */       throwStatusError(this.buf, i);
/*      */     }
/* 1793 */     catch (Exception e) {
/* 1794 */       if (e instanceof SftpException) throw (SftpException)e; 
/* 1795 */       if (e instanceof Throwable)
/* 1796 */         throw new SftpException(4, "", e); 
/* 1797 */       throw new SftpException(4, "");
/*      */     } 
/* 1799 */     return null;
/*      */   }
/*      */   
/*      */   public void symlink(String oldpath, String newpath) throws SftpException {
/* 1803 */     if (this.server_version < 3) {
/* 1804 */       throw new SftpException(8, "The remote sshd is too old to support symlink operation.");
/*      */     }
/*      */ 
/*      */     
/*      */     try {
/* 1809 */       ((Channel.MyPipedInputStream)this.io_in).updateReadSide();
/*      */       
/* 1811 */       String _oldpath = remoteAbsolutePath(oldpath);
/* 1812 */       newpath = remoteAbsolutePath(newpath);
/*      */       
/* 1814 */       _oldpath = isUnique(_oldpath);
/* 1815 */       if (oldpath.charAt(0) != '/') {
/* 1816 */         String cwd = getCwd();
/* 1817 */         oldpath = _oldpath.substring(cwd.length() + (cwd.endsWith("/") ? 0 : 1));
/*      */       } else {
/*      */         
/* 1820 */         oldpath = _oldpath;
/*      */       } 
/*      */       
/* 1823 */       if (isPattern(newpath)) {
/* 1824 */         throw new SftpException(4, newpath);
/*      */       }
/* 1826 */       newpath = Util.unquote(newpath);
/*      */       
/* 1828 */       sendSYMLINK(Util.str2byte(oldpath, this.fEncoding), Util.str2byte(newpath, this.fEncoding));
/*      */ 
/*      */       
/* 1831 */       Header header = new Header();
/* 1832 */       header = header(this.buf, header);
/* 1833 */       int length = header.length;
/* 1834 */       int type = header.type;
/*      */       
/* 1836 */       fill(this.buf, length);
/*      */       
/* 1838 */       if (type != 101) {
/* 1839 */         throw new SftpException(4, "");
/*      */       }
/*      */       
/* 1842 */       int i = this.buf.getInt();
/* 1843 */       if (i == 0)
/* 1844 */         return;  throwStatusError(this.buf, i);
/*      */     }
/* 1846 */     catch (Exception e) {
/* 1847 */       if (e instanceof SftpException) throw (SftpException)e; 
/* 1848 */       if (e instanceof Throwable)
/* 1849 */         throw new SftpException(4, "", e); 
/* 1850 */       throw new SftpException(4, "");
/*      */     } 
/*      */   }
/*      */   
/*      */   public void hardlink(String oldpath, String newpath) throws SftpException {
/* 1855 */     if (!this.extension_hardlink) {
/* 1856 */       throw new SftpException(8, "hardlink@openssh.com is not supported");
/*      */     }
/*      */ 
/*      */     
/*      */     try {
/* 1861 */       ((Channel.MyPipedInputStream)this.io_in).updateReadSide();
/*      */       
/* 1863 */       String _oldpath = remoteAbsolutePath(oldpath);
/* 1864 */       newpath = remoteAbsolutePath(newpath);
/*      */       
/* 1866 */       _oldpath = isUnique(_oldpath);
/* 1867 */       if (oldpath.charAt(0) != '/') {
/* 1868 */         String cwd = getCwd();
/* 1869 */         oldpath = _oldpath.substring(cwd.length() + (cwd.endsWith("/") ? 0 : 1));
/*      */       } else {
/*      */         
/* 1872 */         oldpath = _oldpath;
/*      */       } 
/*      */       
/* 1875 */       if (isPattern(newpath)) {
/* 1876 */         throw new SftpException(4, newpath);
/*      */       }
/* 1878 */       newpath = Util.unquote(newpath);
/*      */       
/* 1880 */       sendHARDLINK(Util.str2byte(oldpath, this.fEncoding), Util.str2byte(newpath, this.fEncoding));
/*      */ 
/*      */       
/* 1883 */       Header header = new Header();
/* 1884 */       header = header(this.buf, header);
/* 1885 */       int length = header.length;
/* 1886 */       int type = header.type;
/*      */       
/* 1888 */       fill(this.buf, length);
/*      */       
/* 1890 */       if (type != 101) {
/* 1891 */         throw new SftpException(4, "");
/*      */       }
/*      */       
/* 1894 */       int i = this.buf.getInt();
/* 1895 */       if (i == 0)
/* 1896 */         return;  throwStatusError(this.buf, i);
/*      */     }
/* 1898 */     catch (Exception e) {
/* 1899 */       if (e instanceof SftpException) throw (SftpException)e; 
/* 1900 */       if (e instanceof Throwable)
/* 1901 */         throw new SftpException(4, "", e); 
/* 1902 */       throw new SftpException(4, "");
/*      */     } 
/*      */   }
/*      */   
/*      */   public void rename(String oldpath, String newpath) throws SftpException {
/* 1907 */     if (this.server_version < 2) {
/* 1908 */       throw new SftpException(8, "The remote sshd is too old to support rename operation.");
/*      */     }
/*      */ 
/*      */     
/*      */     try {
/* 1913 */       ((Channel.MyPipedInputStream)this.io_in).updateReadSide();
/*      */       
/* 1915 */       oldpath = remoteAbsolutePath(oldpath);
/* 1916 */       newpath = remoteAbsolutePath(newpath);
/*      */       
/* 1918 */       oldpath = isUnique(oldpath);
/*      */       
/* 1920 */       Vector v = glob_remote(newpath);
/* 1921 */       int vsize = v.size();
/* 1922 */       if (vsize >= 2) {
/* 1923 */         throw new SftpException(4, v.toString());
/*      */       }
/* 1925 */       if (vsize == 1) {
/* 1926 */         newpath = v.elementAt(0);
/*      */       } else {
/*      */         
/* 1929 */         if (isPattern(newpath))
/* 1930 */           throw new SftpException(4, newpath); 
/* 1931 */         newpath = Util.unquote(newpath);
/*      */       } 
/*      */       
/* 1934 */       sendRENAME(Util.str2byte(oldpath, this.fEncoding), Util.str2byte(newpath, this.fEncoding));
/*      */ 
/*      */       
/* 1937 */       Header header = new Header();
/* 1938 */       header = header(this.buf, header);
/* 1939 */       int length = header.length;
/* 1940 */       int type = header.type;
/*      */       
/* 1942 */       fill(this.buf, length);
/*      */       
/* 1944 */       if (type != 101) {
/* 1945 */         throw new SftpException(4, "");
/*      */       }
/*      */       
/* 1948 */       int i = this.buf.getInt();
/* 1949 */       if (i == 0)
/* 1950 */         return;  throwStatusError(this.buf, i);
/*      */     }
/* 1952 */     catch (Exception e) {
/* 1953 */       if (e instanceof SftpException) throw (SftpException)e; 
/* 1954 */       if (e instanceof Throwable)
/* 1955 */         throw new SftpException(4, "", e); 
/* 1956 */       throw new SftpException(4, "");
/*      */     } 
/*      */   }
/*      */   public void rm(String path) throws SftpException {
/*      */     try {
/* 1961 */       ((Channel.MyPipedInputStream)this.io_in).updateReadSide();
/*      */       
/* 1963 */       path = remoteAbsolutePath(path);
/*      */       
/* 1965 */       Vector v = glob_remote(path);
/* 1966 */       int vsize = v.size();
/*      */       
/* 1968 */       Header header = new Header();
/*      */       
/* 1970 */       for (int j = 0; j < vsize; j++) {
/* 1971 */         path = v.elementAt(j);
/* 1972 */         sendREMOVE(Util.str2byte(path, this.fEncoding));
/*      */         
/* 1974 */         header = header(this.buf, header);
/* 1975 */         int length = header.length;
/* 1976 */         int type = header.type;
/*      */         
/* 1978 */         fill(this.buf, length);
/*      */         
/* 1980 */         if (type != 101) {
/* 1981 */           throw new SftpException(4, "");
/*      */         }
/* 1983 */         int i = this.buf.getInt();
/* 1984 */         if (i != 0) {
/* 1985 */           throwStatusError(this.buf, i);
/*      */         }
/*      */       }
/*      */     
/* 1989 */     } catch (Exception e) {
/* 1990 */       if (e instanceof SftpException) throw (SftpException)e; 
/* 1991 */       if (e instanceof Throwable)
/* 1992 */         throw new SftpException(4, "", e); 
/* 1993 */       throw new SftpException(4, "");
/*      */     } 
/*      */   }
/*      */   
/*      */   private boolean isRemoteDir(String path) {
/*      */     try {
/* 1999 */       sendSTAT(Util.str2byte(path, this.fEncoding));
/*      */       
/* 2001 */       Header header = new Header();
/* 2002 */       header = header(this.buf, header);
/* 2003 */       int length = header.length;
/* 2004 */       int type = header.type;
/*      */       
/* 2006 */       fill(this.buf, length);
/*      */       
/* 2008 */       if (type != 105) {
/* 2009 */         return false;
/*      */       }
/* 2011 */       SftpATTRS attr = SftpATTRS.getATTR(this.buf);
/* 2012 */       return attr.isDir();
/*      */     }
/* 2014 */     catch (Exception e) {
/* 2015 */       return false;
/*      */     } 
/*      */   }
/*      */   public void chgrp(int gid, String path) throws SftpException {
/*      */     try {
/* 2020 */       ((Channel.MyPipedInputStream)this.io_in).updateReadSide();
/*      */       
/* 2022 */       path = remoteAbsolutePath(path);
/*      */       
/* 2024 */       Vector v = glob_remote(path);
/* 2025 */       int vsize = v.size();
/* 2026 */       for (int j = 0; j < vsize; j++) {
/* 2027 */         path = v.elementAt(j);
/*      */         
/* 2029 */         SftpATTRS attr = _stat(path);
/*      */         
/* 2031 */         attr.setFLAGS(0);
/* 2032 */         attr.setUIDGID(attr.uid, gid);
/* 2033 */         _setStat(path, attr);
/*      */       }
/*      */     
/* 2036 */     } catch (Exception e) {
/* 2037 */       if (e instanceof SftpException) throw (SftpException)e; 
/* 2038 */       if (e instanceof Throwable)
/* 2039 */         throw new SftpException(4, "", e); 
/* 2040 */       throw new SftpException(4, "");
/*      */     } 
/*      */   }
/*      */   
/*      */   public void chown(int uid, String path) throws SftpException {
/*      */     try {
/* 2046 */       ((Channel.MyPipedInputStream)this.io_in).updateReadSide();
/*      */       
/* 2048 */       path = remoteAbsolutePath(path);
/*      */       
/* 2050 */       Vector v = glob_remote(path);
/* 2051 */       int vsize = v.size();
/* 2052 */       for (int j = 0; j < vsize; j++) {
/* 2053 */         path = v.elementAt(j);
/*      */         
/* 2055 */         SftpATTRS attr = _stat(path);
/*      */         
/* 2057 */         attr.setFLAGS(0);
/* 2058 */         attr.setUIDGID(uid, attr.gid);
/* 2059 */         _setStat(path, attr);
/*      */       }
/*      */     
/* 2062 */     } catch (Exception e) {
/* 2063 */       if (e instanceof SftpException) throw (SftpException)e; 
/* 2064 */       if (e instanceof Throwable)
/* 2065 */         throw new SftpException(4, "", e); 
/* 2066 */       throw new SftpException(4, "");
/*      */     } 
/*      */   }
/*      */   
/*      */   public void chmod(int permissions, String path) throws SftpException {
/*      */     try {
/* 2072 */       ((Channel.MyPipedInputStream)this.io_in).updateReadSide();
/*      */       
/* 2074 */       path = remoteAbsolutePath(path);
/*      */       
/* 2076 */       Vector v = glob_remote(path);
/* 2077 */       int vsize = v.size();
/* 2078 */       for (int j = 0; j < vsize; j++) {
/* 2079 */         path = v.elementAt(j);
/*      */         
/* 2081 */         SftpATTRS attr = _stat(path);
/*      */         
/* 2083 */         attr.setFLAGS(0);
/* 2084 */         attr.setPERMISSIONS(permissions);
/* 2085 */         _setStat(path, attr);
/*      */       }
/*      */     
/* 2088 */     } catch (Exception e) {
/* 2089 */       if (e instanceof SftpException) throw (SftpException)e; 
/* 2090 */       if (e instanceof Throwable)
/* 2091 */         throw new SftpException(4, "", e); 
/* 2092 */       throw new SftpException(4, "");
/*      */     } 
/*      */   }
/*      */   
/*      */   public void setMtime(String path, int mtime) throws SftpException {
/*      */     try {
/* 2098 */       ((Channel.MyPipedInputStream)this.io_in).updateReadSide();
/*      */       
/* 2100 */       path = remoteAbsolutePath(path);
/*      */       
/* 2102 */       Vector v = glob_remote(path);
/* 2103 */       int vsize = v.size();
/* 2104 */       for (int j = 0; j < vsize; j++) {
/* 2105 */         path = v.elementAt(j);
/*      */         
/* 2107 */         SftpATTRS attr = _stat(path);
/*      */         
/* 2109 */         attr.setFLAGS(0);
/* 2110 */         attr.setACMODTIME(attr.getATime(), mtime);
/* 2111 */         _setStat(path, attr);
/*      */       }
/*      */     
/* 2114 */     } catch (Exception e) {
/* 2115 */       if (e instanceof SftpException) throw (SftpException)e; 
/* 2116 */       if (e instanceof Throwable)
/* 2117 */         throw new SftpException(4, "", e); 
/* 2118 */       throw new SftpException(4, "");
/*      */     } 
/*      */   }
/*      */   
/*      */   public void rmdir(String path) throws SftpException {
/*      */     try {
/* 2124 */       ((Channel.MyPipedInputStream)this.io_in).updateReadSide();
/*      */       
/* 2126 */       path = remoteAbsolutePath(path);
/*      */       
/* 2128 */       Vector v = glob_remote(path);
/* 2129 */       int vsize = v.size();
/*      */       
/* 2131 */       Header header = new Header();
/*      */       
/* 2133 */       for (int j = 0; j < vsize; j++) {
/* 2134 */         path = v.elementAt(j);
/* 2135 */         sendRMDIR(Util.str2byte(path, this.fEncoding));
/*      */         
/* 2137 */         header = header(this.buf, header);
/* 2138 */         int length = header.length;
/* 2139 */         int type = header.type;
/*      */         
/* 2141 */         fill(this.buf, length);
/*      */         
/* 2143 */         if (type != 101) {
/* 2144 */           throw new SftpException(4, "");
/*      */         }
/*      */         
/* 2147 */         int i = this.buf.getInt();
/* 2148 */         if (i != 0) {
/* 2149 */           throwStatusError(this.buf, i);
/*      */         }
/*      */       }
/*      */     
/* 2153 */     } catch (Exception e) {
/* 2154 */       if (e instanceof SftpException) throw (SftpException)e; 
/* 2155 */       if (e instanceof Throwable)
/* 2156 */         throw new SftpException(4, "", e); 
/* 2157 */       throw new SftpException(4, "");
/*      */     } 
/*      */   }
/*      */   
/*      */   public void mkdir(String path) throws SftpException {
/*      */     try {
/* 2163 */       ((Channel.MyPipedInputStream)this.io_in).updateReadSide();
/*      */       
/* 2165 */       path = remoteAbsolutePath(path);
/*      */       
/* 2167 */       sendMKDIR(Util.str2byte(path, this.fEncoding), (SftpATTRS)null);
/*      */       
/* 2169 */       Header header = new Header();
/* 2170 */       header = header(this.buf, header);
/* 2171 */       int length = header.length;
/* 2172 */       int type = header.type;
/*      */       
/* 2174 */       fill(this.buf, length);
/*      */       
/* 2176 */       if (type != 101) {
/* 2177 */         throw new SftpException(4, "");
/*      */       }
/*      */       
/* 2180 */       int i = this.buf.getInt();
/* 2181 */       if (i == 0)
/* 2182 */         return;  throwStatusError(this.buf, i);
/*      */     }
/* 2184 */     catch (Exception e) {
/* 2185 */       if (e instanceof SftpException) throw (SftpException)e; 
/* 2186 */       if (e instanceof Throwable)
/* 2187 */         throw new SftpException(4, "", e); 
/* 2188 */       throw new SftpException(4, "");
/*      */     } 
/*      */   }
/*      */   
/*      */   public SftpATTRS stat(String path) throws SftpException {
/*      */     try {
/* 2194 */       ((Channel.MyPipedInputStream)this.io_in).updateReadSide();
/*      */       
/* 2196 */       path = remoteAbsolutePath(path);
/* 2197 */       path = isUnique(path);
/*      */       
/* 2199 */       return _stat(path);
/*      */     }
/* 2201 */     catch (Exception e) {
/* 2202 */       if (e instanceof SftpException) throw (SftpException)e; 
/* 2203 */       if (e instanceof Throwable)
/* 2204 */         throw new SftpException(4, "", e); 
/* 2205 */       throw new SftpException(4, "");
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   private SftpATTRS _stat(byte[] path) throws SftpException {
/*      */     try {
/* 2213 */       sendSTAT(path);
/*      */       
/* 2215 */       Header header = new Header();
/* 2216 */       header = header(this.buf, header);
/* 2217 */       int length = header.length;
/* 2218 */       int type = header.type;
/*      */       
/* 2220 */       fill(this.buf, length);
/*      */       
/* 2222 */       if (type != 105) {
/* 2223 */         if (type == 101) {
/* 2224 */           int i = this.buf.getInt();
/* 2225 */           throwStatusError(this.buf, i);
/*      */         } 
/* 2227 */         throw new SftpException(4, "");
/*      */       } 
/* 2229 */       SftpATTRS attr = SftpATTRS.getATTR(this.buf);
/* 2230 */       return attr;
/*      */     }
/* 2232 */     catch (Exception e) {
/* 2233 */       if (e instanceof SftpException) throw (SftpException)e; 
/* 2234 */       if (e instanceof Throwable)
/* 2235 */         throw new SftpException(4, "", e); 
/* 2236 */       throw new SftpException(4, "");
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private SftpATTRS _stat(String path) throws SftpException {
/* 2242 */     return _stat(Util.str2byte(path, this.fEncoding));
/*      */   }
/*      */   
/*      */   public SftpStatVFS statVFS(String path) throws SftpException {
/*      */     try {
/* 2247 */       ((Channel.MyPipedInputStream)this.io_in).updateReadSide();
/*      */       
/* 2249 */       path = remoteAbsolutePath(path);
/* 2250 */       path = isUnique(path);
/*      */       
/* 2252 */       return _statVFS(path);
/*      */     }
/* 2254 */     catch (Exception e) {
/* 2255 */       if (e instanceof SftpException) throw (SftpException)e; 
/* 2256 */       if (e instanceof Throwable)
/* 2257 */         throw new SftpException(4, "", e); 
/* 2258 */       throw new SftpException(4, "");
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private SftpStatVFS _statVFS(byte[] path) throws SftpException {
/* 2264 */     if (!this.extension_statvfs) {
/* 2265 */       throw new SftpException(8, "statvfs@openssh.com is not supported");
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*      */     try {
/* 2271 */       sendSTATVFS(path);
/*      */       
/* 2273 */       Header header = new Header();
/* 2274 */       header = header(this.buf, header);
/* 2275 */       int length = header.length;
/* 2276 */       int type = header.type;
/*      */       
/* 2278 */       fill(this.buf, length);
/*      */       
/* 2280 */       if (type != 201) {
/* 2281 */         if (type == 101) {
/* 2282 */           int i = this.buf.getInt();
/* 2283 */           throwStatusError(this.buf, i);
/*      */         } 
/* 2285 */         throw new SftpException(4, "");
/*      */       } 
/*      */       
/* 2288 */       SftpStatVFS stat = SftpStatVFS.getStatVFS(this.buf);
/* 2289 */       return stat;
/*      */     
/*      */     }
/* 2292 */     catch (Exception e) {
/* 2293 */       if (e instanceof SftpException) throw (SftpException)e; 
/* 2294 */       if (e instanceof Throwable)
/* 2295 */         throw new SftpException(4, "", e); 
/* 2296 */       throw new SftpException(4, "");
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   private SftpStatVFS _statVFS(String path) throws SftpException {
/* 2302 */     return _statVFS(Util.str2byte(path, this.fEncoding));
/*      */   }
/*      */   
/*      */   public SftpATTRS lstat(String path) throws SftpException {
/*      */     try {
/* 2307 */       ((Channel.MyPipedInputStream)this.io_in).updateReadSide();
/*      */       
/* 2309 */       path = remoteAbsolutePath(path);
/* 2310 */       path = isUnique(path);
/*      */       
/* 2312 */       return _lstat(path);
/*      */     }
/* 2314 */     catch (Exception e) {
/* 2315 */       if (e instanceof SftpException) throw (SftpException)e; 
/* 2316 */       if (e instanceof Throwable)
/* 2317 */         throw new SftpException(4, "", e); 
/* 2318 */       throw new SftpException(4, "");
/*      */     } 
/*      */   }
/*      */   
/*      */   private SftpATTRS _lstat(String path) throws SftpException {
/*      */     try {
/* 2324 */       sendLSTAT(Util.str2byte(path, this.fEncoding));
/*      */       
/* 2326 */       Header header = new Header();
/* 2327 */       header = header(this.buf, header);
/* 2328 */       int length = header.length;
/* 2329 */       int type = header.type;
/*      */       
/* 2331 */       fill(this.buf, length);
/*      */       
/* 2333 */       if (type != 105) {
/* 2334 */         if (type == 101) {
/* 2335 */           int i = this.buf.getInt();
/* 2336 */           throwStatusError(this.buf, i);
/*      */         } 
/* 2338 */         throw new SftpException(4, "");
/*      */       } 
/* 2340 */       SftpATTRS attr = SftpATTRS.getATTR(this.buf);
/* 2341 */       return attr;
/*      */     }
/* 2343 */     catch (Exception e) {
/* 2344 */       if (e instanceof SftpException) throw (SftpException)e; 
/* 2345 */       if (e instanceof Throwable)
/* 2346 */         throw new SftpException(4, "", e); 
/* 2347 */       throw new SftpException(4, "");
/*      */     } 
/*      */   }
/*      */   
/*      */   private byte[] _realpath(String path) throws SftpException, IOException, Exception {
/* 2352 */     sendREALPATH(Util.str2byte(path, this.fEncoding));
/*      */     
/* 2354 */     Header header = new Header();
/* 2355 */     header = header(this.buf, header);
/* 2356 */     int length = header.length;
/* 2357 */     int type = header.type;
/*      */     
/* 2359 */     fill(this.buf, length);
/*      */     
/* 2361 */     if (type != 101 && type != 104) {
/* 2362 */       throw new SftpException(4, "");
/*      */     }
/*      */     
/* 2365 */     if (type == 101) {
/* 2366 */       int j = this.buf.getInt();
/* 2367 */       throwStatusError(this.buf, j);
/*      */     } 
/* 2369 */     int i = this.buf.getInt();
/*      */     
/* 2371 */     byte[] str = null;
/* 2372 */     while (i-- > 0) {
/* 2373 */       str = this.buf.getString();
/* 2374 */       if (this.server_version <= 3) {
/* 2375 */         byte[] lname = this.buf.getString();
/*      */       }
/* 2377 */       SftpATTRS attr = SftpATTRS.getATTR(this.buf);
/*      */     } 
/* 2379 */     return str;
/*      */   }
/*      */   
/*      */   public void setStat(String path, SftpATTRS attr) throws SftpException {
/*      */     try {
/* 2384 */       ((Channel.MyPipedInputStream)this.io_in).updateReadSide();
/*      */       
/* 2386 */       path = remoteAbsolutePath(path);
/*      */       
/* 2388 */       Vector v = glob_remote(path);
/* 2389 */       int vsize = v.size();
/* 2390 */       for (int j = 0; j < vsize; j++) {
/* 2391 */         path = v.elementAt(j);
/* 2392 */         _setStat(path, attr);
/*      */       }
/*      */     
/* 2395 */     } catch (Exception e) {
/* 2396 */       if (e instanceof SftpException) throw (SftpException)e; 
/* 2397 */       if (e instanceof Throwable)
/* 2398 */         throw new SftpException(4, "", e); 
/* 2399 */       throw new SftpException(4, "");
/*      */     } 
/*      */   }
/*      */   private void _setStat(String path, SftpATTRS attr) throws SftpException {
/*      */     try {
/* 2404 */       sendSETSTAT(Util.str2byte(path, this.fEncoding), attr);
/*      */       
/* 2406 */       Header header = new Header();
/* 2407 */       header = header(this.buf, header);
/* 2408 */       int length = header.length;
/* 2409 */       int type = header.type;
/*      */       
/* 2411 */       fill(this.buf, length);
/*      */       
/* 2413 */       if (type != 101) {
/* 2414 */         throw new SftpException(4, "");
/*      */       }
/* 2416 */       int i = this.buf.getInt();
/* 2417 */       if (i != 0) {
/* 2418 */         throwStatusError(this.buf, i);
/*      */       }
/*      */     }
/* 2421 */     catch (Exception e) {
/* 2422 */       if (e instanceof SftpException) throw (SftpException)e; 
/* 2423 */       if (e instanceof Throwable)
/* 2424 */         throw new SftpException(4, "", e); 
/* 2425 */       throw new SftpException(4, "");
/*      */     } 
/*      */   }
/*      */   
/* 2429 */   public String pwd() throws SftpException { return getCwd(); }
/* 2430 */   public String lpwd() { return this.lcwd; } public String version() {
/* 2431 */     return this.version;
/*      */   } public String getHome() throws SftpException {
/* 2433 */     if (this.home == null) {
/*      */       try {
/* 2435 */         ((Channel.MyPipedInputStream)this.io_in).updateReadSide();
/*      */         
/* 2437 */         byte[] _home = _realpath("");
/* 2438 */         this.home = Util.byte2str(_home, this.fEncoding);
/*      */       }
/* 2440 */       catch (Exception e) {
/* 2441 */         if (e instanceof SftpException) throw (SftpException)e; 
/* 2442 */         if (e instanceof Throwable)
/* 2443 */           throw new SftpException(4, "", e); 
/* 2444 */         throw new SftpException(4, "");
/*      */       } 
/*      */     }
/* 2447 */     return this.home;
/*      */   }
/*      */   
/*      */   private String getCwd() throws SftpException {
/* 2451 */     if (this.cwd == null)
/* 2452 */       this.cwd = getHome(); 
/* 2453 */     return this.cwd;
/*      */   }
/*      */   
/*      */   private void setCwd(String cwd) {
/* 2457 */     this.cwd = cwd;
/*      */   }
/*      */   
/*      */   private void read(byte[] buf, int s, int l) throws IOException, SftpException {
/* 2461 */     int i = 0;
/* 2462 */     while (l > 0) {
/* 2463 */       i = this.io_in.read(buf, s, l);
/* 2464 */       if (i <= 0) {
/* 2465 */         throw new SftpException(4, "");
/*      */       }
/* 2467 */       s += i;
/* 2468 */       l -= i;
/*      */     } 
/*      */   }
/*      */   
/*      */   private boolean checkStatus(int[] ackid, Header header) throws IOException, SftpException {
/* 2473 */     header = header(this.buf, header);
/* 2474 */     int length = header.length;
/* 2475 */     int type = header.type;
/* 2476 */     if (ackid != null) {
/* 2477 */       ackid[0] = header.rid;
/*      */     }
/* 2479 */     fill(this.buf, length);
/*      */     
/* 2481 */     if (type != 101) {
/* 2482 */       throw new SftpException(4, "");
/*      */     }
/* 2484 */     int i = this.buf.getInt();
/* 2485 */     if (i != 0) {
/* 2486 */       throwStatusError(this.buf, i);
/*      */     }
/* 2488 */     return true;
/*      */   }
/*      */   private boolean _sendCLOSE(byte[] handle, Header header) throws Exception {
/* 2491 */     sendCLOSE(handle);
/* 2492 */     return checkStatus((int[])null, header);
/*      */   }
/*      */   
/*      */   private void sendINIT() throws Exception {
/* 2496 */     this.packet.reset();
/* 2497 */     putHEAD((byte)1, 5);
/* 2498 */     this.buf.putInt(3);
/* 2499 */     getSession().write(this.packet, this, 9);
/*      */   }
/*      */   
/*      */   private void sendREALPATH(byte[] path) throws Exception {
/* 2503 */     sendPacketPath((byte)16, path);
/*      */   }
/*      */   private void sendSTAT(byte[] path) throws Exception {
/* 2506 */     sendPacketPath((byte)17, path);
/*      */   }
/*      */   private void sendSTATVFS(byte[] path) throws Exception {
/* 2509 */     sendPacketPath((byte)0, path, "statvfs@openssh.com");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void sendLSTAT(byte[] path) throws Exception {
/* 2517 */     sendPacketPath((byte)7, path);
/*      */   }
/*      */   private void sendFSTAT(byte[] handle) throws Exception {
/* 2520 */     sendPacketPath((byte)8, handle);
/*      */   }
/*      */   private void sendSETSTAT(byte[] path, SftpATTRS attr) throws Exception {
/* 2523 */     this.packet.reset();
/* 2524 */     putHEAD((byte)9, 9 + path.length + attr.length());
/* 2525 */     this.buf.putInt(this.seq++);
/* 2526 */     this.buf.putString(path);
/* 2527 */     attr.dump(this.buf);
/* 2528 */     getSession().write(this.packet, this, 9 + path.length + attr.length() + 4);
/*      */   }
/*      */   private void sendREMOVE(byte[] path) throws Exception {
/* 2531 */     sendPacketPath((byte)13, path);
/*      */   }
/*      */   private void sendMKDIR(byte[] path, SftpATTRS attr) throws Exception {
/* 2534 */     this.packet.reset();
/* 2535 */     putHEAD((byte)14, 9 + path.length + ((attr != null) ? attr.length() : 4));
/* 2536 */     this.buf.putInt(this.seq++);
/* 2537 */     this.buf.putString(path);
/* 2538 */     if (attr != null) { attr.dump(this.buf); }
/* 2539 */     else { this.buf.putInt(0); }
/* 2540 */      getSession().write(this.packet, this, 9 + path.length + ((attr != null) ? attr.length() : 4) + 4);
/*      */   }
/*      */   private void sendRMDIR(byte[] path) throws Exception {
/* 2543 */     sendPacketPath((byte)15, path);
/*      */   }
/*      */   private void sendSYMLINK(byte[] p1, byte[] p2) throws Exception {
/* 2546 */     sendPacketPath((byte)20, p1, p2);
/*      */   }
/*      */   private void sendHARDLINK(byte[] p1, byte[] p2) throws Exception {
/* 2549 */     sendPacketPath((byte)0, p1, p2, "hardlink@openssh.com");
/*      */   }
/*      */   private void sendREADLINK(byte[] path) throws Exception {
/* 2552 */     sendPacketPath((byte)19, path);
/*      */   }
/*      */   private void sendOPENDIR(byte[] path) throws Exception {
/* 2555 */     sendPacketPath((byte)11, path);
/*      */   }
/*      */   private void sendREADDIR(byte[] path) throws Exception {
/* 2558 */     sendPacketPath((byte)12, path);
/*      */   }
/*      */   private void sendRENAME(byte[] p1, byte[] p2) throws Exception {
/* 2561 */     sendPacketPath((byte)18, p1, p2, this.extension_posix_rename ? "posix-rename@openssh.com" : null);
/*      */   }
/*      */   
/*      */   private void sendCLOSE(byte[] path) throws Exception {
/* 2565 */     sendPacketPath((byte)4, path);
/*      */   }
/*      */   private void sendOPENR(byte[] path) throws Exception {
/* 2568 */     sendOPEN(path, 1);
/*      */   }
/*      */   private void sendOPENW(byte[] path) throws Exception {
/* 2571 */     sendOPEN(path, 26);
/*      */   }
/*      */   private void sendOPENA(byte[] path) throws Exception {
/* 2574 */     sendOPEN(path, 10);
/*      */   }
/*      */   private void sendOPEN(byte[] path, int mode) throws Exception {
/* 2577 */     this.packet.reset();
/* 2578 */     putHEAD((byte)3, 17 + path.length);
/* 2579 */     this.buf.putInt(this.seq++);
/* 2580 */     this.buf.putString(path);
/* 2581 */     this.buf.putInt(mode);
/* 2582 */     this.buf.putInt(0);
/* 2583 */     getSession().write(this.packet, this, 17 + path.length + 4);
/*      */   }
/*      */   private void sendPacketPath(byte fxp, byte[] path) throws Exception {
/* 2586 */     sendPacketPath(fxp, path, (String)null);
/*      */   }
/*      */   private void sendPacketPath(byte fxp, byte[] path, String extension) throws Exception {
/* 2589 */     this.packet.reset();
/* 2590 */     int len = 9 + path.length;
/* 2591 */     if (extension == null) {
/* 2592 */       putHEAD(fxp, len);
/* 2593 */       this.buf.putInt(this.seq++);
/*      */     } else {
/*      */       
/* 2596 */       len += 4 + extension.length();
/* 2597 */       putHEAD((byte)-56, len);
/* 2598 */       this.buf.putInt(this.seq++);
/* 2599 */       this.buf.putString(Util.str2byte(extension));
/*      */     } 
/* 2601 */     this.buf.putString(path);
/* 2602 */     getSession().write(this.packet, this, len + 4);
/*      */   }
/*      */   
/*      */   private void sendPacketPath(byte fxp, byte[] p1, byte[] p2) throws Exception {
/* 2606 */     sendPacketPath(fxp, p1, p2, (String)null);
/*      */   }
/*      */   private void sendPacketPath(byte fxp, byte[] p1, byte[] p2, String extension) throws Exception {
/* 2609 */     this.packet.reset();
/* 2610 */     int len = 13 + p1.length + p2.length;
/* 2611 */     if (extension == null) {
/* 2612 */       putHEAD(fxp, len);
/* 2613 */       this.buf.putInt(this.seq++);
/*      */     } else {
/*      */       
/* 2616 */       len += 4 + extension.length();
/* 2617 */       putHEAD((byte)-56, len);
/* 2618 */       this.buf.putInt(this.seq++);
/* 2619 */       this.buf.putString(Util.str2byte(extension));
/*      */     } 
/* 2621 */     this.buf.putString(p1);
/* 2622 */     this.buf.putString(p2);
/* 2623 */     getSession().write(this.packet, this, len + 4);
/*      */   }
/*      */ 
/*      */   
/*      */   private int sendWRITE(byte[] handle, long offset, byte[] data, int start, int length) throws Exception {
/* 2628 */     int _length = length;
/* 2629 */     this.opacket.reset();
/* 2630 */     if (this.obuf.buffer.length < this.obuf.index + 13 + 21 + handle.length + length + 128) {
/* 2631 */       _length = this.obuf.buffer.length - this.obuf.index + 13 + 21 + handle.length + 128;
/*      */     }
/*      */ 
/*      */     
/* 2635 */     putHEAD(this.obuf, (byte)6, 21 + handle.length + _length);
/* 2636 */     this.obuf.putInt(this.seq++);
/* 2637 */     this.obuf.putString(handle);
/* 2638 */     this.obuf.putLong(offset);
/* 2639 */     if (this.obuf.buffer != data) {
/* 2640 */       this.obuf.putString(data, start, _length);
/*      */     } else {
/*      */       
/* 2643 */       this.obuf.putInt(_length);
/* 2644 */       this.obuf.skip(_length);
/*      */     } 
/* 2646 */     getSession().write(this.opacket, this, 21 + handle.length + _length + 4);
/* 2647 */     return _length;
/*      */   }
/*      */   private void sendREAD(byte[] handle, long offset, int length) throws Exception {
/* 2650 */     sendREAD(handle, offset, length, (RequestQueue)null);
/*      */   }
/*      */   
/*      */   private void sendREAD(byte[] handle, long offset, int length, RequestQueue rrq) throws Exception {
/* 2654 */     this.packet.reset();
/* 2655 */     putHEAD((byte)5, 21 + handle.length);
/* 2656 */     this.buf.putInt(this.seq++);
/* 2657 */     this.buf.putString(handle);
/* 2658 */     this.buf.putLong(offset);
/* 2659 */     this.buf.putInt(length);
/* 2660 */     getSession().write(this.packet, this, 21 + handle.length + 4);
/* 2661 */     if (rrq != null) {
/* 2662 */       rrq.add(this.seq - 1, offset, length);
/*      */     }
/*      */   }
/*      */   
/*      */   private void putHEAD(Buffer buf, byte type, int length) throws Exception {
/* 2667 */     buf.putByte((byte)94);
/* 2668 */     buf.putInt(this.recipient);
/* 2669 */     buf.putInt(length + 4);
/* 2670 */     buf.putInt(length);
/* 2671 */     buf.putByte(type);
/*      */   }
/*      */   
/*      */   private void putHEAD(byte type, int length) throws Exception {
/* 2675 */     putHEAD(this.buf, type, length);
/*      */   }
/*      */   
/*      */   private Vector glob_remote(String _path) throws Exception {
/* 2679 */     Vector v = new Vector();
/* 2680 */     int i = 0;
/*      */     
/* 2682 */     int foo = _path.lastIndexOf('/');
/* 2683 */     if (foo < 0) {
/* 2684 */       v.addElement(Util.unquote(_path));
/* 2685 */       return v;
/*      */     } 
/*      */     
/* 2688 */     String dir = _path.substring(0, (foo == 0) ? 1 : foo);
/* 2689 */     String _pattern = _path.substring(foo + 1);
/*      */     
/* 2691 */     dir = Util.unquote(dir);
/*      */     
/* 2693 */     byte[] pattern = null;
/* 2694 */     byte[][] _pattern_utf8 = new byte[1][];
/* 2695 */     boolean pattern_has_wildcard = isPattern(_pattern, _pattern_utf8);
/*      */     
/* 2697 */     if (!pattern_has_wildcard) {
/* 2698 */       if (!dir.equals("/"))
/* 2699 */         dir = dir + "/"; 
/* 2700 */       v.addElement(dir + Util.unquote(_pattern));
/* 2701 */       return v;
/*      */     } 
/*      */     
/* 2704 */     pattern = _pattern_utf8[0];
/*      */     
/* 2706 */     sendOPENDIR(Util.str2byte(dir, this.fEncoding));
/*      */     
/* 2708 */     Header header = new Header();
/* 2709 */     header = header(this.buf, header);
/* 2710 */     int length = header.length;
/* 2711 */     int type = header.type;
/*      */     
/* 2713 */     fill(this.buf, length);
/*      */     
/* 2715 */     if (type != 101 && type != 102) {
/* 2716 */       throw new SftpException(4, "");
/*      */     }
/* 2718 */     if (type == 101) {
/* 2719 */       i = this.buf.getInt();
/* 2720 */       throwStatusError(this.buf, i);
/*      */     } 
/*      */     
/* 2723 */     byte[] handle = this.buf.getString();
/* 2724 */     String pdir = null;
/*      */     
/*      */     while (true) {
/* 2727 */       sendREADDIR(handle);
/* 2728 */       header = header(this.buf, header);
/* 2729 */       length = header.length;
/* 2730 */       type = header.type;
/*      */       
/* 2732 */       if (type != 101 && type != 104) {
/* 2733 */         throw new SftpException(4, "");
/*      */       }
/* 2735 */       if (type == 101) {
/* 2736 */         fill(this.buf, length);
/*      */         
/*      */         break;
/*      */       } 
/* 2740 */       this.buf.rewind();
/* 2741 */       fill(this.buf.buffer, 0, 4); length -= 4;
/* 2742 */       int count = this.buf.getInt();
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 2747 */       this.buf.reset();
/* 2748 */       while (count > 0) {
/* 2749 */         if (length > 0) {
/* 2750 */           this.buf.shift();
/* 2751 */           int j = (this.buf.buffer.length > this.buf.index + length) ? length : (this.buf.buffer.length - this.buf.index);
/* 2752 */           i = this.io_in.read(this.buf.buffer, this.buf.index, j);
/* 2753 */           if (i <= 0)
/* 2754 */             break;  this.buf.index += i;
/* 2755 */           length -= i;
/*      */         } 
/*      */         
/* 2758 */         byte[] filename = this.buf.getString();
/*      */         
/* 2760 */         if (this.server_version <= 3) {
/* 2761 */           byte[] arrayOfByte = this.buf.getString();
/*      */         }
/* 2763 */         SftpATTRS attrs = SftpATTRS.getATTR(this.buf);
/*      */         
/* 2765 */         byte[] _filename = filename;
/* 2766 */         String f = null;
/* 2767 */         boolean found = false;
/*      */         
/* 2769 */         if (!this.fEncoding_is_utf8) {
/* 2770 */           f = Util.byte2str(filename, this.fEncoding);
/* 2771 */           _filename = Util.str2byte(f, "UTF-8");
/*      */         } 
/* 2773 */         found = Util.glob(pattern, _filename);
/*      */         
/* 2775 */         if (found) {
/* 2776 */           if (f == null) {
/* 2777 */             f = Util.byte2str(filename, this.fEncoding);
/*      */           }
/* 2779 */           if (pdir == null) {
/* 2780 */             pdir = dir;
/* 2781 */             if (!pdir.endsWith("/")) {
/* 2782 */               pdir = pdir + "/";
/*      */             }
/*      */           } 
/* 2785 */           v.addElement(pdir + f);
/*      */         } 
/* 2787 */         count--;
/*      */       } 
/*      */     } 
/* 2790 */     if (_sendCLOSE(handle, header))
/* 2791 */       return v; 
/* 2792 */     return null;
/*      */   }
/*      */   
/*      */   private boolean isPattern(byte[] path) {
/* 2796 */     int length = path.length;
/* 2797 */     int i = 0;
/* 2798 */     while (i < length) {
/* 2799 */       if (path[i] == 42 || path[i] == 63)
/* 2800 */         return true; 
/* 2801 */       if (path[i] == 92 && i + 1 < length)
/* 2802 */         i++; 
/* 2803 */       i++;
/*      */     } 
/* 2805 */     return false;
/*      */   }
/*      */   
/*      */   private Vector glob_local(String _path) throws Exception {
/*      */     byte[] dir;
/* 2810 */     Vector v = new Vector();
/* 2811 */     byte[] path = Util.str2byte(_path, "UTF-8");
/* 2812 */     int i = path.length - 1;
/* 2813 */     while (i >= 0) {
/* 2814 */       if (path[i] != 42 && path[i] != 63) {
/* 2815 */         i--;
/*      */         continue;
/*      */       } 
/* 2818 */       if (!fs_is_bs && i > 0 && path[i - 1] == 92) {
/*      */         
/* 2820 */         i--;
/* 2821 */         if (i > 0 && path[i - 1] == 92) {
/* 2822 */           i--;
/* 2823 */           i--;
/*      */         } 
/*      */       } 
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/* 2830 */     if (i < 0) { v.addElement(fs_is_bs ? _path : Util.unquote(_path)); return v; }
/*      */     
/* 2832 */     while (i >= 0 && 
/* 2833 */       path[i] != file_separatorc && (!fs_is_bs || path[i] != 47))
/*      */     {
/*      */ 
/*      */       
/* 2837 */       i--;
/*      */     }
/*      */     
/* 2840 */     if (i < 0) { v.addElement(fs_is_bs ? _path : Util.unquote(_path)); return v; }
/*      */ 
/*      */     
/* 2843 */     if (i == 0) { dir = new byte[] { (byte)file_separatorc }; }
/*      */     else
/* 2845 */     { dir = new byte[i];
/* 2846 */       System.arraycopy(path, 0, dir, 0, i); }
/*      */ 
/*      */     
/* 2849 */     byte[] pattern = new byte[path.length - i - 1];
/* 2850 */     System.arraycopy(path, i + 1, pattern, 0, pattern.length);
/*      */ 
/*      */     
/*      */     try {
/* 2854 */       String[] children = (new File(Util.byte2str(dir, "UTF-8"))).list();
/* 2855 */       String pdir = Util.byte2str(dir) + file_separator;
/* 2856 */       for (int j = 0; j < children.length; j++)
/*      */       {
/* 2858 */         if (Util.glob(pattern, Util.str2byte(children[j], "UTF-8"))) {
/* 2859 */           v.addElement(pdir + children[j]);
/*      */         }
/*      */       }
/*      */     
/* 2863 */     } catch (Exception e) {}
/*      */     
/* 2865 */     return v;
/*      */   }
/*      */   
/*      */   private void throwStatusError(Buffer buf, int i) throws SftpException {
/* 2869 */     if (this.server_version >= 3 && buf.getLength() >= 4) {
/*      */       
/* 2871 */       byte[] str = buf.getString();
/*      */       
/* 2873 */       throw new SftpException(i, Util.byte2str(str, "UTF-8"));
/*      */     } 
/*      */     
/* 2876 */     throw new SftpException(i, "Failure");
/*      */   }
/*      */ 
/*      */   
/*      */   private static boolean isLocalAbsolutePath(String path) {
/* 2881 */     return (new File(path)).isAbsolute();
/*      */   }
/*      */   
/*      */   public void disconnect() {
/* 2885 */     super.disconnect();
/*      */   }
/*      */   
/*      */   private boolean isPattern(String path, byte[][] utf8) {
/* 2889 */     byte[] _path = Util.str2byte(path, "UTF-8");
/* 2890 */     if (utf8 != null)
/* 2891 */       utf8[0] = _path; 
/* 2892 */     return isPattern(_path);
/*      */   }
/*      */   
/*      */   private boolean isPattern(String path) {
/* 2896 */     return isPattern(path, (byte[][])null);
/*      */   }
/*      */   
/*      */   private void fill(Buffer buf, int len) throws IOException {
/* 2900 */     buf.reset();
/* 2901 */     fill(buf.buffer, 0, len);
/* 2902 */     buf.skip(len);
/*      */   }
/*      */   
/*      */   private int fill(byte[] buf, int s, int len) throws IOException {
/* 2906 */     int i = 0;
/* 2907 */     int foo = s;
/* 2908 */     while (len > 0) {
/* 2909 */       i = this.io_in.read(buf, s, len);
/* 2910 */       if (i <= 0) {
/* 2911 */         throw new IOException("inputstream is closed");
/*      */       }
/*      */       
/* 2914 */       s += i;
/* 2915 */       len -= i;
/*      */     } 
/* 2917 */     return s - foo;
/*      */   }
/*      */   private void skip(long foo) throws IOException {
/* 2920 */     while (foo > 0L) {
/* 2921 */       long bar = this.io_in.skip(foo);
/* 2922 */       if (bar <= 0L)
/*      */         break; 
/* 2924 */       foo -= bar;
/*      */     } 
/*      */   }
/*      */   class Header {
/*      */     int length;
/*      */     int type;
/*      */     int rid;
/*      */     private final ChannelSftp this$0; }
/*      */   
/*      */   private Header header(Buffer buf, Header header) throws IOException {
/* 2934 */     buf.rewind();
/* 2935 */     int i = fill(buf.buffer, 0, 9);
/* 2936 */     header.length = buf.getInt() - 5;
/* 2937 */     header.type = buf.getByte() & 0xFF;
/* 2938 */     header.rid = buf.getInt();
/* 2939 */     return header;
/*      */   }
/*      */   
/*      */   private String remoteAbsolutePath(String path) throws SftpException {
/* 2943 */     if (path.charAt(0) == '/') return path; 
/* 2944 */     String cwd = getCwd();
/*      */     
/* 2946 */     if (cwd.endsWith("/")) return cwd + path; 
/* 2947 */     return cwd + "/" + path;
/*      */   }
/*      */   
/*      */   private String localAbsolutePath(String path) {
/* 2951 */     if (isLocalAbsolutePath(path)) return path; 
/* 2952 */     if (this.lcwd.endsWith(file_separator)) return this.lcwd + path; 
/* 2953 */     return this.lcwd + file_separator + path;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private String isUnique(String path) throws SftpException, Exception {
/* 2963 */     Vector v = glob_remote(path);
/* 2964 */     if (v.size() != 1) {
/* 2965 */       throw new SftpException(4, path + " is not unique: " + v.toString());
/*      */     }
/* 2967 */     return v.elementAt(0);
/*      */   }
/*      */   
/*      */   public int getServerVersion() throws SftpException {
/* 2971 */     if (!isConnected()) {
/* 2972 */       throw new SftpException(4, "The channel is not connected.");
/*      */     }
/* 2974 */     return this.server_version;
/*      */   }
/*      */   
/*      */   public void setFilenameEncoding(String encoding) throws SftpException {
/* 2978 */     int sversion = getServerVersion();
/* 2979 */     if (3 <= sversion && sversion <= 5 && !encoding.equals("UTF-8"))
/*      */     {
/* 2981 */       throw new SftpException(4, "The encoding can not be changed for this sftp server.");
/*      */     }
/*      */     
/* 2984 */     if (encoding.equals("UTF-8")) {
/* 2985 */       encoding = "UTF-8";
/*      */     }
/* 2987 */     this.fEncoding = encoding;
/* 2988 */     this.fEncoding_is_utf8 = this.fEncoding.equals("UTF-8");
/*      */   }
/*      */   
/*      */   public String getExtension(String key) {
/* 2992 */     if (this.extensions == null)
/* 2993 */       return null; 
/* 2994 */     return (String)this.extensions.get(key);
/*      */   }
/*      */   
/*      */   public String realpath(String path) throws SftpException {
/*      */     try {
/* 2999 */       byte[] _path = _realpath(remoteAbsolutePath(path));
/* 3000 */       return Util.byte2str(_path, this.fEncoding);
/*      */     }
/* 3002 */     catch (Exception e) {
/* 3003 */       if (e instanceof SftpException) throw (SftpException)e; 
/* 3004 */       if (e instanceof Throwable)
/* 3005 */         throw new SftpException(4, "", e); 
/* 3006 */       throw new SftpException(4, "");
/*      */     } 
/*      */   }
/*      */   public class LsEntry implements Comparable { private String filename;
/*      */     private String longname;
/*      */     private SftpATTRS attrs;
/*      */     private final ChannelSftp this$0;
/*      */     
/*      */     LsEntry(String filename, String longname, SftpATTRS attrs) {
/* 3015 */       setFilename(filename);
/* 3016 */       setLongname(longname);
/* 3017 */       setAttrs(attrs);
/*      */     }
/* 3019 */     public String getFilename() { return this.filename; }
/* 3020 */     void setFilename(String filename) { this.filename = filename; }
/* 3021 */     public String getLongname() { return this.longname; }
/* 3022 */     void setLongname(String longname) { this.longname = longname; }
/* 3023 */     public SftpATTRS getAttrs() { return this.attrs; }
/* 3024 */     void setAttrs(SftpATTRS attrs) { this.attrs = attrs; } public String toString() {
/* 3025 */       return this.longname;
/*      */     } public int compareTo(Object o) throws ClassCastException {
/* 3027 */       if (o instanceof LsEntry) {
/* 3028 */         return this.filename.compareTo(((LsEntry)o).getFilename());
/*      */       }
/* 3030 */       throw new ClassCastException("a decendent of LsEntry must be given.");
/*      */     } }
/*      */ 
/*      */   
/*      */   public static interface LsEntrySelector {
/*      */     public static final int CONTINUE = 0;
/*      */     public static final int BREAK = 1;
/*      */     
/*      */     int select(ChannelSftp.LsEntry param1LsEntry);
/*      */   }
/*      */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\ChannelSftp.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */