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
/*    */ public class RequestSftp
/*    */   extends Request
/*    */ {
/*    */   RequestSftp() {
/* 34 */     setReply(true);
/*    */   }
/*    */   public void request(Session session, Channel channel) throws Exception {
/* 37 */     super.request(session, channel);
/*    */     
/* 39 */     Buffer buf = new Buffer();
/* 40 */     Packet packet = new Packet(buf);
/* 41 */     packet.reset();
/* 42 */     buf.putByte((byte)98);
/* 43 */     buf.putInt(channel.getRecipient());
/* 44 */     buf.putString(Util.str2byte("subsystem"));
/* 45 */     buf.putByte((byte)(waitForReply() ? 1 : 0));
/* 46 */     buf.putString(Util.str2byte("sftp"));
/* 47 */     write(packet);
/*    */   }
/*    */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\RequestSftp.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */