/*    */ package com.igs.core.har.utils;
/*    */ 
/*    */ import com.igs.core.har.utils.log.Logger;
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class RunnerTask
/*    */   implements Runnable
/*    */ {
/*    */   private Thread thread;
/*    */   private String threadName;
/*    */   private Logger loggerInstance;
/*    */   private Object[] objectArgs;
/*    */   private int delay;
/*    */   private boolean isRun = false;
/*    */   
/*    */   public RunnerTask(String threadName) {
/* 18 */     this.delay = 1000;
/* 19 */     this.threadName = threadName;
/*    */   }
/*    */   
/*    */   public RunnerTask(String threadName, int delay) {
/* 23 */     this(threadName);
/* 24 */     this.delay = delay;
/*    */   }
/*    */   
/*    */   public RunnerTask(String threadName, int delay, Logger logger) {
/* 28 */     this(threadName, delay);
/* 29 */     this.loggerInstance = logger;
/*    */   }
/*    */   
/*    */   public abstract void DoJob(Object[] paramArrayOfObject);
/*    */   
/*    */   public void run() {
/* 35 */     System.out.println("Engine is running..");
/*    */     while (true) {
/*    */       try {
/* 38 */         Thread.sleep(this.delay);
/* 39 */         DoJob(this.objectArgs);
/* 40 */       } catch (InterruptedException e) {
/* 41 */         if (this.loggerInstance != null) {
/* 42 */           Logger.PrintException(this.loggerInstance, e, "Error in run method");
/*    */         }
/*    */       } 
/* 45 */       if (!this.isRun) {
/* 46 */         System.out.println("Engine is stopped..");
/*    */         return;
/*    */       } 
/*    */     }  } public void Start() {
/* 50 */     this.isRun = true;
/* 51 */     if (this.thread == null) {
/* 52 */       this.thread = new Thread(this, this.threadName);
/*    */     }
/* 54 */     this.thread.start();
/*    */   }
/*    */   
/*    */   public void Start(Object[] args) {
/* 58 */     this.objectArgs = args;
/* 59 */     Start();
/*    */   }
/*    */   
/*    */   public void Stop() {
/* 63 */     this.isRun = false;
/*    */   }
/*    */   
/*    */   public void SetDelay(int delay) {
/* 67 */     this.delay = delay;
/*    */   }
/*    */   
/*    */   public int GetDelay() {
/* 71 */     return this.delay;
/*    */   }
/*    */   
/*    */   public void SetObjectArgs(Object[] args) {
/* 75 */     this.objectArgs = args;
/*    */   }
/*    */   
/*    */   public Object[] GetObjectArgs() {
/* 79 */     return this.objectArgs;
/*    */   }
/*    */   
/*    */   public void SetLogger(Logger logger) {
/* 83 */     this.loggerInstance = logger;
/*    */   }
/*    */   
/*    */   public Logger GetLogger() {
/* 87 */     return this.loggerInstance;
/*    */   }
/*    */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\igs\core\ha\\utils\RunnerTask.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */