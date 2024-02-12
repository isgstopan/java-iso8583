/*    */ package com.igs.core.har.uimprobe;
/*    */ 
/*    */ import com.igs.core.har.utils.config.Configuration;
/*    */ import com.igs.core.har.utils.log.Logger;
/*    */ import java.io.IOException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class DataManager
/*    */ {
/* 13 */   private Logger probeLogger = null;
/* 14 */   private Configuration config = null;
/*    */   
/*    */   public DataManager(Logger probeLogger, String configPath) throws IOException {
/* 17 */     this.probeLogger = probeLogger;
/* 18 */     Initialize(configPath);
/*    */   }
/*    */   
/*    */   private void Initialize(String configPath) throws IOException {
/* 22 */     this.probeLogger.Print("Loading Configuration [" + configPath + "]", 2);
/* 23 */     this.config = Configuration.Load(configPath);
/* 24 */     this.probeLogger.Print("Configuration Loaded!", 2);
/*    */   }
/*    */   
/*    */   public AppInformation GetAppInformation(String appName) {
/* 28 */     AppInformation appInfo = new AppInformation();
/*    */     
/* 30 */     appInfo.SetDirectory(this.config.GetString(appName, "DIR", "E:\\Probe\\Bin"));
/* 31 */     appInfo.SetId(this.config.GetInteger(appName, "ID", -1));
/* 32 */     appInfo.SetName(this.config.GetString(appName, "NAME", "noname"));
/* 33 */     appInfo.SetClassPath(this.config.GetString(appName, "CLASSPATH", "noclasspath"));
/*    */     
/* 35 */     return appInfo;
/*    */   }
/*    */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\igs\core\ha\\uimprobe\DataManager.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */