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
/*    */ class RequestSignal
/*    */   extends Request
/*    */ {
/* 33 */   private String signal = "KILL"; public void setSignal(String foo) {
/* 34 */     this.signal = foo;
/*    */   } public void request(Session session, Channel channel) throws Exception {
/* 36 */     super.request(session, channel);
/*    */     
/* 38 */     Buffer buf = new Buffer();
/* 39 */     Packet packet = new Packet(buf);
/*    */     
/* 41 */     packet.reset();
/* 42 */     buf.putByte((byte)98);
/* 43 */     buf.putInt(channel.getRecipient());
/* 44 */     buf.putString(Util.str2byte("signal"));
/* 45 */     buf.putByte((byte)(waitForReply() ? 1 : 0));
/* 46 */     buf.putString(Util.str2byte(this.signal));
/* 47 */     write(packet);
/*    */   }
/*    */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\RequestSignal.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */