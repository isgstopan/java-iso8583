/*    */ package com.igs.core.har.utils.config;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ 
/*    */ public class ConfigurationItem
/*    */ {
/*    */   private String section;
/*    */   private HashMap<String, Object> list;
/*    */   
/*    */   ConfigurationItem(String section) {
/* 11 */     this.section = section;
/* 12 */     this.list = new HashMap<>();
/*    */   }
/*    */   
/*    */   public Object Get(String key) {
/* 16 */     return this.list.get(key);
/*    */   }
/*    */   
/*    */   public String GetSection() {
/* 20 */     return this.section;
/*    */   }
/*    */   
/*    */   public HashMap<String, Object> GetList() {
/* 24 */     return this.list;
/*    */   }
/*    */   
/*    */   void Put(String key, Object value) {
/* 28 */     this.list.put(key, value);
/*    */   }
/*    */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\igs\core\ha\\utils\config\ConfigurationItem.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */