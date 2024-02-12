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
/*    */ public interface ConfigRepository
/*    */ {
/* 44 */   public static final Config defaultConfig = new Config() {
/* 45 */       public String getHostname() { return null; }
/* 46 */       public String getUser() { return null; }
/* 47 */       public int getPort() { return -1; }
/* 48 */       public String getValue(String key) { return null; } public String[] getValues(String key) {
/* 49 */         return null;
/*    */       }
/*    */     };
/* 52 */   public static final ConfigRepository nullConfig = new ConfigRepository() { public ConfigRepository.Config getConfig(String host) {
/* 53 */         return defaultConfig;
/*    */       } }
/*    */   ;
/*    */   
/*    */   public static interface Config {
/*    */     String getHostname();
/*    */     
/*    */     String getUser();
/*    */     
/*    */     int getPort();
/*    */     
/*    */     String getValue(String param1String);
/*    */     
/*    */     String[] getValues(String param1String);
/*    */   }
/*    */   
/*    */   Config getConfig(String paramString);
/*    */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\ConfigRepository.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */