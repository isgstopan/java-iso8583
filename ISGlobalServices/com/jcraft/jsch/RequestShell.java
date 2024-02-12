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
/*    */ class RequestShell
/*    */   extends Request
/*    */ {
/*    */   public void request(Session session, Channel channel) throws Exception {
/* 34 */     super.request(session, channel);
/*    */     
/* 36 */     Buffer buf = new Buffer();
/* 37 */     Packet packet = new Packet(buf);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 44 */     packet.reset();
/* 45 */     buf.putByte((byte)98);
/* 46 */     buf.putInt(channel.getRecipient());
/* 47 */     buf.putString(Util.str2byte("shell"));
/* 48 */     buf.putByte((byte)(waitForReply() ? 1 : 0));
/* 49 */     write(packet);
/*    */   }
/*    */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\RequestShell.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */