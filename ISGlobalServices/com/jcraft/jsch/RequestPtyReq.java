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
/*    */ class RequestPtyReq
/*    */   extends Request
/*    */ {
/* 33 */   private String ttype = "vt100";
/* 34 */   private int tcol = 80;
/* 35 */   private int trow = 24;
/* 36 */   private int twp = 640;
/* 37 */   private int thp = 480;
/*    */   
/* 39 */   private byte[] terminal_mode = Util.empty;
/*    */ 
/*    */   
/*    */   void setCode(String cookie) {}
/*    */   
/*    */   void setTType(String ttype) {
/* 45 */     this.ttype = ttype;
/*    */   }
/*    */   
/*    */   void setTerminalMode(byte[] terminal_mode) {
/* 49 */     this.terminal_mode = terminal_mode;
/*    */   }
/*    */   
/*    */   void setTSize(int tcol, int trow, int twp, int thp) {
/* 53 */     this.tcol = tcol;
/* 54 */     this.trow = trow;
/* 55 */     this.twp = twp;
/* 56 */     this.thp = thp;
/*    */   }
/*    */   
/*    */   public void request(Session session, Channel channel) throws Exception {
/* 60 */     super.request(session, channel);
/*    */     
/* 62 */     Buffer buf = new Buffer();
/* 63 */     Packet packet = new Packet(buf);
/*    */     
/* 65 */     packet.reset();
/* 66 */     buf.putByte((byte)98);
/* 67 */     buf.putInt(channel.getRecipient());
/* 68 */     buf.putString(Util.str2byte("pty-req"));
/* 69 */     buf.putByte((byte)(waitForReply() ? 1 : 0));
/* 70 */     buf.putString(Util.str2byte(this.ttype));
/* 71 */     buf.putInt(this.tcol);
/* 72 */     buf.putInt(this.trow);
/* 73 */     buf.putInt(this.twp);
/* 74 */     buf.putInt(this.thp);
/* 75 */     buf.putString(this.terminal_mode);
/* 76 */     write(packet);
/*    */   }
/*    */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\RequestPtyReq.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */