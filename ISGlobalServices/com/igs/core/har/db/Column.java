/*    */ package com.igs.core.har.db;
/*    */ 
/*    */ public class Column
/*    */ {
/*    */   private int columnIndex;
/*    */   private String columnName;
/*    */   private Class columnType;
/*    */   private Object columnValue;
/*    */   
/*    */   public Column(String tableColumn, Object tableColumnValue, Class tableColumnType) {
/* 11 */     this.columnName = tableColumn;
/* 12 */     this.columnType = tableColumnType;
/* 13 */     this.columnValue = tableColumnValue;
/*    */   }
/*    */   
/*    */   public Column(String tableColumn, Object tableColumnValue, Class tableColumnType, int index) {
/* 17 */     this.columnIndex = index;
/* 18 */     this.columnName = tableColumn;
/* 19 */     this.columnType = tableColumnType;
/* 20 */     this.columnValue = tableColumnValue;
/*    */   }
/*    */   
/*    */   public int GetIndex() {
/* 24 */     return this.columnIndex;
/*    */   }
/*    */   
/*    */   public void SetIndex(int columnIndex) {
/* 28 */     this.columnIndex = columnIndex;
/*    */   }
/*    */   
/*    */   public String GetName() {
/* 32 */     return this.columnName;
/*    */   }
/*    */   
/*    */   public void SetName(String columnName) {
/* 36 */     this.columnName = columnName;
/*    */   }
/*    */   
/*    */   public Class GetType() {
/* 40 */     return this.columnType;
/*    */   }
/*    */   
/*    */   public void SetType(Class columnType) {
/* 44 */     this.columnType = columnType;
/*    */   }
/*    */   
/*    */   public Object GetValue() {
/* 48 */     return this.columnValue;
/*    */   }
/*    */   
/*    */   public void SetValue(Object columnValue) {
/* 52 */     this.columnValue = columnValue;
/*    */   }
/*    */   
/*    */   public Column Clone() {
/* 56 */     return new Column(this.columnName, this.columnValue, this.columnType, this.columnIndex);
/*    */   }
/*    */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\igs\core\har\db\Column.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */