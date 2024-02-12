/*    */ package com.igs.core.har.utils;
/*    */ import com.igs.core.har.utils.log.Logger;
/*    */ import java.util.ArrayList;
/*    */ import java.util.Arrays;
/*    */ import java.util.Date;
/*    */ import java.util.List;
/*    */ import java.util.Timer;
/*    */ import java.util.TimerTask;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ 
/*    */ public abstract class PeriodicTask {
/* 12 */   private Timer timer = null;
/* 13 */   private long previousInterval = 0L;
/* 14 */   private Date startDate = null;
/* 15 */   private int intervalLimit = 5;
/* 16 */   private Logger genLogger = null;
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private boolean condition = true;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void RunScheduler(Object... args) {
/* 28 */     this.timer = new Timer();
/* 29 */     this.startDate = new Date();
/*    */     
/* 31 */     this.timer.scheduleAtFixedRate(new TimerTask() {
/*    */           public void run() {
/* 33 */             long interval = (new Date()).getTime() - PeriodicTask.this.startDate.getTime();
/* 34 */             long intervalInSecond = TimeUnit.MILLISECONDS.toSeconds(interval);
/*    */             
/* 36 */             List<Object> newArgs = new ArrayList();
/* 37 */             newArgs.addAll(Arrays.asList(args));
/*    */             
/* 39 */             newArgs.add(Long.valueOf(interval));
/* 40 */             newArgs.add(Long.valueOf(intervalInSecond));
/*    */             
/* 42 */             PeriodicTask.this.SetupCondition();
/*    */             
/* 44 */             if ((PeriodicTask.this.condition || (intervalInSecond != 0L && intervalInSecond % PeriodicTask.this.intervalLimit == 0L)) && PeriodicTask.this.previousInterval != intervalInSecond) {
/* 45 */               PeriodicTask.this.previousInterval = intervalInSecond;
/*    */               
/* 47 */               PeriodicTask.this.DoJob(ArrayHelper.asArray(Object.class, newArgs));
/*    */             } 
/*    */             
/* 50 */             if (intervalInSecond > 3600L) {
/* 51 */               PeriodicTask.this.startDate = new Date();
/* 52 */               PeriodicTask.this.genLogger.Print("Interval reset", 2);
/*    */             } 
/*    */           }
/*    */         }1L, 1L);
/*    */   }
/*    */   
/*    */   public int GetIntervalLimit() {
/* 59 */     return this.intervalLimit;
/*    */   }
/*    */   
/*    */   public void SetIntervalLimit(int intervalLimit) {
/* 63 */     this.intervalLimit = intervalLimit;
/*    */   }
/*    */   
/*    */   public Logger GetGenLogger() {
/* 67 */     return this.genLogger;
/*    */   }
/*    */   
/*    */   public void SetGenLogger(Logger genLogger) {
/* 71 */     this.genLogger = genLogger;
/*    */   }
/*    */   
/*    */   public boolean Condition() {
/* 75 */     return this.condition;
/*    */   }
/*    */   
/*    */   public void SetCondition(boolean condition) {
/* 79 */     this.condition = condition;
/*    */   }
/*    */   
/*    */   public abstract void SetupCondition();
/*    */   
/*    */   public abstract void DoJob(Object[] paramArrayOfObject);
/*    */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\igs\core\ha\\utils\PeriodicTask.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */