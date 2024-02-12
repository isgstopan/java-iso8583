/*    */ package com.jcraft.jsch;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.io.OutputStream;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ChannelSubsystem
/*    */   extends ChannelSession
/*    */ {
/*    */   boolean xforwading = false;
/*    */   boolean pty = false;
/*    */   boolean want_reply = true;
/* 36 */   String subsystem = "";
/* 37 */   public void setXForwarding(boolean foo) { this.xforwading = foo; }
/* 38 */   public void setPty(boolean foo) { this.pty = foo; }
/* 39 */   public void setWantReply(boolean foo) { this.want_reply = foo; } public void setSubsystem(String foo) {
/* 40 */     this.subsystem = foo;
/*    */   } public void start() throws JSchException {
/* 42 */     Session _session = getSession();
/*    */     
/*    */     try {
/* 45 */       if (this.xforwading) {
/* 46 */         Request request1 = new RequestX11();
/* 47 */         request1.request(_session, this);
/*    */       } 
/* 49 */       if (this.pty) {
/* 50 */         Request request1 = new RequestPtyReq();
/* 51 */         request1.request(_session, this);
/*    */       } 
/* 53 */       Request request = new RequestSubsystem();
/* 54 */       ((RequestSubsystem)request).request(_session, this, this.subsystem, this.want_reply);
/*    */     }
/* 56 */     catch (Exception e) {
/* 57 */       if (e instanceof JSchException) throw (JSchException)e; 
/* 58 */       if (e instanceof Throwable)
/* 59 */         throw new JSchException("ChannelSubsystem", e); 
/* 60 */       throw new JSchException("ChannelSubsystem");
/*    */     } 
/* 62 */     if (this.io.in != null) {
/* 63 */       this.thread = new Thread(this);
/* 64 */       this.thread.setName("Subsystem for " + _session.host);
/* 65 */       if (_session.daemon_thread) {
/* 66 */         this.thread.setDaemon(_session.daemon_thread);
/*    */       }
/* 68 */       this.thread.start();
/*    */     } 
/*    */   }
/*    */   
/*    */   void init() throws JSchException {
/* 73 */     this.io.setInputStream((getSession()).in);
/* 74 */     this.io.setOutputStream((getSession()).out);
/*    */   }
/*    */   
/*    */   public void setErrStream(OutputStream out) {
/* 78 */     setExtOutputStream(out);
/*    */   }
/*    */   public InputStream getErrStream() throws IOException {
/* 81 */     return getExtInputStream();
/*    */   }
/*    */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\ChannelSubsystem.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */