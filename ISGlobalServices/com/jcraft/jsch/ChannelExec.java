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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ChannelExec
/*    */   extends ChannelSession
/*    */ {
/* 36 */   byte[] command = new byte[0];
/*    */   
/*    */   public void start() throws JSchException {
/* 39 */     Session _session = getSession();
/*    */     try {
/* 41 */       sendRequests();
/* 42 */       Request request = new RequestExec(this.command);
/* 43 */       request.request(_session, this);
/*    */     }
/* 45 */     catch (Exception e) {
/* 46 */       if (e instanceof JSchException) throw (JSchException)e; 
/* 47 */       if (e instanceof Throwable)
/* 48 */         throw new JSchException("ChannelExec", e); 
/* 49 */       throw new JSchException("ChannelExec");
/*    */     } 
/*    */     
/* 52 */     if (this.io.in != null) {
/* 53 */       this.thread = new Thread(this);
/* 54 */       this.thread.setName("Exec thread " + _session.getHost());
/* 55 */       if (_session.daemon_thread) {
/* 56 */         this.thread.setDaemon(_session.daemon_thread);
/*    */       }
/* 58 */       this.thread.start();
/*    */     } 
/*    */   }
/*    */   
/*    */   public void setCommand(String command) {
/* 63 */     this.command = Util.str2byte(command);
/*    */   }
/*    */   public void setCommand(byte[] command) {
/* 66 */     this.command = command;
/*    */   }
/*    */   
/*    */   void init() throws JSchException {
/* 70 */     this.io.setInputStream((getSession()).in);
/* 71 */     this.io.setOutputStream((getSession()).out);
/*    */   }
/*    */   
/*    */   public void setErrStream(OutputStream out) {
/* 75 */     setExtOutputStream(out);
/*    */   }
/*    */   public void setErrStream(OutputStream out, boolean dontclose) {
/* 78 */     setExtOutputStream(out, dontclose);
/*    */   }
/*    */   public InputStream getErrStream() throws IOException {
/* 81 */     return getExtInputStream();
/*    */   }
/*    */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\ChannelExec.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */