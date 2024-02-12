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
/*    */ class RequestEnv
/*    */   extends Request
/*    */ {
/* 33 */   byte[] name = new byte[0];
/* 34 */   byte[] value = new byte[0];
/*    */   void setEnv(byte[] name, byte[] value) {
/* 36 */     this.name = name;
/* 37 */     this.value = value;
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
/* 48 */     buf.putString(Util.str2byte("env"));
/* 49 */     buf.putByte((byte)(waitForReply() ? 1 : 0));
/* 50 */     buf.putString(this.name);
/* 51 */     buf.putString(this.value);
/* 52 */     write(packet);
/*    */   }
/*    */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\RequestEnv.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */