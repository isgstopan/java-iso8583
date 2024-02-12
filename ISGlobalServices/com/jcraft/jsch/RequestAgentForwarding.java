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
/*    */ class RequestAgentForwarding
/*    */   extends Request
/*    */ {
/*    */   public void request(Session session, Channel channel) throws Exception {
/* 34 */     super.request(session, channel);
/*    */     
/* 36 */     setReply(false);
/*    */     
/* 38 */     Buffer buf = new Buffer();
/* 39 */     Packet packet = new Packet(buf);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 45 */     packet.reset();
/* 46 */     buf.putByte((byte)98);
/* 47 */     buf.putInt(channel.getRecipient());
/* 48 */     buf.putString(Util.str2byte("auth-agent-req@openssh.com"));
/* 49 */     buf.putByte((byte)(waitForReply() ? 1 : 0));
/* 50 */     write(packet);
/* 51 */     session.agent_forwarding = true;
/*    */   }
/*    */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\RequestAgentForwarding.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */