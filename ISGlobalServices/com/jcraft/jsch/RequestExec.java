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
/*    */ class RequestExec
/*    */   extends Request
/*    */ {
/* 33 */   private byte[] command = new byte[0];
/*    */   RequestExec(byte[] command) {
/* 35 */     this.command = command;
/*    */   }
/*    */   public void request(Session session, Channel channel) throws Exception {
/* 38 */     super.request(session, channel);
/*    */     
/* 40 */     Buffer buf = new Buffer();
/* 41 */     Packet packet = new Packet(buf);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 49 */     packet.reset();
/* 50 */     buf.putByte((byte)98);
/* 51 */     buf.putInt(channel.getRecipient());
/* 52 */     buf.putString(Util.str2byte("exec"));
/* 53 */     buf.putByte((byte)(waitForReply() ? 1 : 0));
/* 54 */     buf.checkFreeSize(4 + this.command.length);
/* 55 */     buf.putString(this.command);
/* 56 */     write(packet);
/*    */   }
/*    */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\RequestExec.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */