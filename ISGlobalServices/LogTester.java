/*    */ import com.igs.core.har.test.engine.EngineReader;
/*    */ import com.igs.core.har.test.engine.EngineWriter;
/*    */ import com.igs.core.har.utils.StringHelper;
/*    */ import com.igs.core.har.utils.config.Configuration;
/*    */ import java.io.IOException;
/*    */ import java.sql.Timestamp;
/*    */ import java.text.ParseException;
/*    */ import java.util.Calendar;
/*    */ 
/*    */ 
/*    */ public class LogTester
/*    */ {
/*    */   public static void main(String[] args) {
/*    */     try {
/* 15 */       mainConfiguration(args);
/*    */ 
/*    */     
/*    */     }
/* 19 */     catch (Exception e) {
/* 20 */       e.printStackTrace();
/*    */     } 
/*    */   }
/*    */   
/*    */   public static void mainConfiguration(String[] args) throws IOException {
/* 25 */     Configuration config = Configuration.Load("C:\\Users\\Harfiyan Shia\\Documents\\crp");
/* 26 */     config.GetFileName();
/* 27 */     Configuration.PrintConfiguration(config);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static void mainDateTimeRoundup(String[] args) throws ParseException {
/* 35 */     Calendar c = Calendar.getInstance();
/*    */     
/* 37 */     System.out.println(c.getTime());
/*    */     
/* 39 */     int minute = c.get(12);
/* 40 */     int second = c.get(13);
/* 41 */     int ms = c.get(14);
/*    */     
/* 43 */     if (minute % 5 != 0) {
/* 44 */       c.add(12, -(minute % 5));
/*    */     }
/*    */     
/* 47 */     if (second != 0) {
/* 48 */       c.add(13, -second);
/*    */     }
/*    */     
/* 51 */     if (ms != 0) {
/* 52 */       c.add(14, -ms);
/*    */     }
/*    */     
/* 55 */     System.out.println(c.getTime());
/*    */     
/* 57 */     Timestamp timestamp = new Timestamp(c.getTime().getTime());
/*    */     
/* 59 */     System.out.println(timestamp.toString());
/*    */   }
/*    */   
/*    */   public static void mainStringUtil(String[] args) {
/* 63 */     System.out.println("\"" + StringHelper.PadRight("TESTING", 30) + "\"");
/*    */   }
/*    */   
/*    */   public static void mainLogging(String[] args) {
/* 67 */     EngineWriter w = new EngineWriter();
/* 68 */     w.SetDelay(0);
/* 69 */     w.Start();
/*    */     
/* 71 */     EngineWriter w2 = new EngineWriter();
/* 72 */     w2.SetDelay(100);
/* 73 */     w2.Start();
/*    */     
/* 75 */     EngineWriter w3 = new EngineWriter();
/* 76 */     w3.SetDelay(500);
/* 77 */     w3.Start();
/*    */     
/* 79 */     EngineReader r = new EngineReader();
/* 80 */     r.SetDelay(0);
/* 81 */     r.Start();
/*    */   }
/*    */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\LogTester.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */