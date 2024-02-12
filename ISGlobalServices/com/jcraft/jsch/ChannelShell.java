/*    */ package com.jcraft.jsch;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ChannelShell
/*    */   extends ChannelSession
/*    */ {
/*    */   public void start() throws JSchException {
/* 42 */     Session _session = getSession();
/*    */     try {
/* 44 */       sendRequests();
/*    */       
/* 46 */       Request request = new RequestShell();
/* 47 */       request.request(_session, this);
/*    */     }
/* 49 */     catch (Exception e) {
/* 50 */       if (e instanceof JSchException) throw (JSchException)e; 
/* 51 */       if (e instanceof Throwable)
/* 52 */         throw new JSchException("ChannelShell", e); 
/* 53 */       throw new JSchException("ChannelShell");
/*    */     } 
/*    */     
/* 56 */     if (this.io.in != null) {
/* 57 */       this.thread = new Thread(this);
/* 58 */       this.thread.setName("Shell for " + _session.host);
/* 59 */       if (_session.daemon_thread) {
/* 60 */         this.thread.setDaemon(_session.daemon_thread);
/*    */       }
/* 62 */       this.thread.start();
/*    */     } 
/*    */   }
/*    */   
/*    */   void init() throws JSchException {
/* 67 */     this.io.setInputStream((getSession()).in);
/* 68 */     this.io.setOutputStream((getSession()).out);
/*    */   }
/*    */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\ChannelShell.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */