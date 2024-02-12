/*    */ package igs.corp;
/*    */ 
/*    */ public class MessageBundle extends Message {
/*  4 */   public String transactionCode = "";
/*    */   
/*  6 */   public String timeSend = "";
/*  7 */   public String sendIsoMessage = "";
/*    */   
/*    */   public int sendMsgLength;
/* 10 */   public String timeReceive = "";
/* 11 */   public String recvIsoMessage = "";
/*    */   public int recvMsgLength;
/*    */   
/*    */   public MessageBundle Clone() {
/* 15 */     MessageBundle t = new MessageBundle();
/* 16 */     t.recvIsoMessage = this.recvIsoMessage;
/* 17 */     t.recvMsgLength = this.recvMsgLength;
/* 18 */     t.responseCode = this.responseCode;
/* 19 */     t.responseTime = this.responseTime;
/* 20 */     t.rrnNumber = this.rrnNumber;
/* 21 */     t.sendIsoMessage = this.sendIsoMessage;
/* 22 */     t.sendMsgLength = this.sendMsgLength;
/* 23 */     t.terminalName = this.terminalName;
/* 24 */     t.timeReceive = this.timeReceive;
/* 25 */     t.timeSend = this.timeSend;
/* 26 */     t.transactionCode = this.transactionCode;
/* 27 */     t.messageId = this.messageId;
/* 28 */     return t;
/*    */   }
/*    */ }


/* Location:              F:\JAVA\Corp.jar!\igs\corp\MessageBundle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */