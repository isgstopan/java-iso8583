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
/*    */ class RequestX11
/*    */   extends Request
/*    */ {
/*    */   public void setCookie(String cookie) {
/* 34 */     ChannelX11.cookie = Util.str2byte(cookie);
/*    */   }
/*    */   public void request(Session session, Channel channel) throws Exception {
/* 37 */     super.request(session, channel);
/*    */     
/* 39 */     Buffer buf = new Buffer();
/* 40 */     Packet packet = new Packet(buf);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 50 */     packet.reset();
/* 51 */     buf.putByte((byte)98);
/* 52 */     buf.putInt(channel.getRecipient());
/* 53 */     buf.putString(Util.str2byte("x11-req"));
/* 54 */     buf.putByte((byte)(waitForReply() ? 1 : 0));
/* 55 */     buf.putByte((byte)0);
/* 56 */     buf.putString(Util.str2byte("MIT-MAGIC-COOKIE-1"));
/* 57 */     buf.putString(ChannelX11.getFakedCookie(session));
/* 58 */     buf.putInt(0);
/* 59 */     write(packet);
/*    */     
/* 61 */     session.x11_forwarding = true;
/*    */   }
/*    */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\RequestX11.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */