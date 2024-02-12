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
/*    */ public class RequestSubsystem
/*    */   extends Request
/*    */ {
/* 33 */   private String subsystem = null;
/*    */   public void request(Session session, Channel channel, String subsystem, boolean want_reply) throws Exception {
/* 35 */     setReply(want_reply);
/* 36 */     this.subsystem = subsystem;
/* 37 */     request(session, channel);
/*    */   }
/*    */   public void request(Session session, Channel channel) throws Exception {
/* 40 */     super.request(session, channel);
/*    */     
/* 42 */     Buffer buf = new Buffer();
/* 43 */     Packet packet = new Packet(buf);
/*    */     
/* 45 */     packet.reset();
/* 46 */     buf.putByte((byte)98);
/* 47 */     buf.putInt(channel.getRecipient());
/* 48 */     buf.putString(Util.str2byte("subsystem"));
/* 49 */     buf.putByte((byte)(waitForReply() ? 1 : 0));
/* 50 */     buf.putString(Util.str2byte(this.subsystem));
/* 51 */     write(packet);
/*    */   }
/*    */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\RequestSubsystem.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */