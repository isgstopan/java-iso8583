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
/*    */ class RequestWindowChange
/*    */   extends Request
/*    */ {
/* 33 */   int width_columns = 80;
/* 34 */   int height_rows = 24;
/* 35 */   int width_pixels = 640;
/* 36 */   int height_pixels = 480;
/*    */   void setSize(int col, int row, int wp, int hp) {
/* 38 */     this.width_columns = col;
/* 39 */     this.height_rows = row;
/* 40 */     this.width_pixels = wp;
/* 41 */     this.height_pixels = hp;
/*    */   }
/*    */   public void request(Session session, Channel channel) throws Exception {
/* 44 */     super.request(session, channel);
/*    */     
/* 46 */     Buffer buf = new Buffer();
/* 47 */     Packet packet = new Packet(buf);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 57 */     packet.reset();
/* 58 */     buf.putByte((byte)98);
/* 59 */     buf.putInt(channel.getRecipient());
/* 60 */     buf.putString(Util.str2byte("window-change"));
/* 61 */     buf.putByte((byte)(waitForReply() ? 1 : 0));
/* 62 */     buf.putInt(this.width_columns);
/* 63 */     buf.putInt(this.height_rows);
/* 64 */     buf.putInt(this.width_pixels);
/* 65 */     buf.putInt(this.height_pixels);
/* 66 */     write(packet);
/*    */   }
/*    */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\RequestWindowChange.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */