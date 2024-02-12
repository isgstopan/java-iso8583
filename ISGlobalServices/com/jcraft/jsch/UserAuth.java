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
/*    */ 
/*    */ 
/*    */ public abstract class UserAuth
/*    */ {
/*    */   protected static final int SSH_MSG_USERAUTH_REQUEST = 50;
/*    */   protected static final int SSH_MSG_USERAUTH_FAILURE = 51;
/*    */   protected static final int SSH_MSG_USERAUTH_SUCCESS = 52;
/*    */   protected static final int SSH_MSG_USERAUTH_BANNER = 53;
/*    */   protected static final int SSH_MSG_USERAUTH_INFO_REQUEST = 60;
/*    */   protected static final int SSH_MSG_USERAUTH_INFO_RESPONSE = 61;
/*    */   protected static final int SSH_MSG_USERAUTH_PK_OK = 60;
/*    */   protected UserInfo userinfo;
/*    */   protected Packet packet;
/*    */   protected Buffer buf;
/*    */   protected String username;
/*    */   
/*    */   public boolean start(Session session) throws Exception {
/* 47 */     this.userinfo = session.getUserInfo();
/* 48 */     this.packet = session.packet;
/* 49 */     this.buf = this.packet.getBuffer();
/* 50 */     this.username = session.getUserName();
/* 51 */     return true;
/*    */   }
/*    */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\UserAuth.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */