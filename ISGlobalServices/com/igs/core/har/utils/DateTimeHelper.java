/*    */ package com.igs.core.har.utils;
/*    */ 
/*    */ import java.sql.Timestamp;
/*    */ import java.text.ParseException;
/*    */ import java.text.SimpleDateFormat;
/*    */ import java.util.Date;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class DateTimeHelper
/*    */ {
/*    */   public static String ToSqlTimestamp(String date, String format) throws ParseException {
/* 17 */     SimpleDateFormat dateFormat = new SimpleDateFormat(format);
/* 18 */     Date parsedDate = dateFormat.parse(date);
/* 19 */     Timestamp timestamp = new Timestamp(parsedDate.getTime());
/*    */     
/* 21 */     return timestamp.toString();
/*    */   }
/*    */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\igs\core\ha\\utils\DateTimeHelper.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */