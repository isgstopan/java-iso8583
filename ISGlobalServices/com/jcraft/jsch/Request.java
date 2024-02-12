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
/*    */ abstract class Request
/*    */ {
/*    */   private boolean reply = false;
/* 34 */   private Session session = null;
/* 35 */   private Channel channel = null;
/*    */   void request(Session session, Channel channel) throws Exception {
/* 37 */     this.session = session;
/* 38 */     this.channel = channel;
/* 39 */     if (channel.connectTimeout > 0)
/* 40 */       setReply(true); 
/*    */   }
/*    */   
/* 43 */   boolean waitForReply() { return this.reply; } void setReply(boolean reply) {
/* 44 */     this.reply = reply;
/*    */   } void write(Packet packet) throws Exception {
/* 46 */     if (this.reply) {
/* 47 */       this.channel.reply = -1;
/*    */     }
/* 49 */     this.session.write(packet);
/* 50 */     if (this.reply) {
/* 51 */       long start = System.currentTimeMillis();
/* 52 */       long timeout = this.channel.connectTimeout;
/* 53 */       while (this.channel.isConnected() && this.channel.reply == -1) { try {
/* 54 */           Thread.sleep(10L);
/* 55 */         } catch (Exception ee) {}
/*    */         
/* 57 */         if (timeout > 0L && System.currentTimeMillis() - start > timeout) {
/*    */           
/* 59 */           this.channel.reply = 0;
/* 60 */           throw new JSchException("channel request: timeout");
/*    */         }  }
/*    */ 
/*    */       
/* 64 */       if (this.channel.reply == 0)
/* 65 */         throw new JSchException("failed to send channel request"); 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\Request.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */