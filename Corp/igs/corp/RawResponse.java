/*    */ package igs.corp;
/*    */ 
/*    */ public class RawResponse {
/*    */   private String timeArrival;
/*    */   private String message;
/*    */   
/*    */   public RawResponse(String timeArrival, String message) {
/*  8 */     this.timeArrival = timeArrival;
/*  9 */     this.message = message;
/*    */   }
/*    */   
/*    */   public String GetTimeArrival() {
/* 13 */     return this.timeArrival;
/*    */   }
/*    */   
/*    */   public void SetTimeArrival(String timeArrival) {
/* 17 */     this.timeArrival = timeArrival;
/*    */   }
/*    */   
/*    */   public String GetMessage() {
/* 21 */     return this.message;
/*    */   }
/*    */   
/*    */   public void SetMessage(String message) {
/* 25 */     this.message = message;
/*    */   }
/*    */ }


/* Location:              F:\JAVA\Corp.jar!\igs\corp\RawResponse.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */