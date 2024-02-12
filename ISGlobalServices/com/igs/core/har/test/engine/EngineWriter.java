/*    */ package com.igs.core.har.test.engine;
/*    */ 
/*    */ import com.igs.core.har.utils.RunnerTask;
/*    */ import com.igs.core.har.utils.log.Logger;
/*    */ import java.text.DateFormat;
/*    */ import java.text.SimpleDateFormat;
/*    */ import java.util.Date;
/*    */ import java.util.Locale;
/*    */ 
/*    */ public class EngineWriter
/*    */   extends RunnerTask {
/* 12 */   private Logger genLogger = null;
/*    */   
/*    */   public EngineWriter() {
/* 15 */     super("Writer", 10, null);
/*    */   }
/*    */ 
/*    */   
/*    */   private void SetupLogger(String logName) {
/* 20 */     this.genLogger = Logger.Register(logName, "F:\\Temp\\Logs", false);
/* 21 */     this.genLogger.SetIsHousekeepingEnabled(true);
/* 22 */     this.genLogger.GetHouseKeeper().SetAutoCreateArchivedDirectory(false);
/* 23 */     this.genLogger.GetHouseKeeper().SetNeedToMove(false);
/* 24 */     this.genLogger.SetPrintLevel(3);
/*    */   }
/*    */ 
/*    */   
/*    */   public void DoJob(Object[] args) {
/* 29 */     DateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH);
/* 30 */     DateFormat format2 = new SimpleDateFormat("yyyyMMdd_HHmmss,S", Locale.ENGLISH);
/* 31 */     String fileDate = format.format(new Date());
/*    */     
/* 33 */     String fileName = "myLog_" + fileDate;
/* 34 */     SetupLogger(fileName);
/* 35 */     this.genLogger.Print("My Current Log: " + format2.format(new Date()), 2);
/*    */   }
/*    */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\igs\core\har\test\engine\EngineWriter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */