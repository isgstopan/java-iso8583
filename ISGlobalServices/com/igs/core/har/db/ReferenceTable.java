/*     */ package com.igs.core.har.db;
/*     */ 
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ public class ReferenceTable
/*     */   extends HashMap<String, String>
/*     */ {
/*     */   private static final long serialVersionUID = 7821583918187136119L;
/*  14 */   private String tableName = "";
/*  15 */   private String query = "";
/*  16 */   private String leftRule = "2";
/*  17 */   private String rightRule = "1";
/*     */   
/*     */   public ReferenceTable(String tableName) {
/*  20 */     this.tableName = tableName;
/*     */   }
/*     */   
/*     */   public ReferenceTable(String tableName, String tdrQuery) {
/*  24 */     this.tableName = tableName;
/*  25 */     this.query = tdrQuery;
/*     */   }
/*     */   
/*     */   public ReferenceTable(String tableName, String tdrQuery, String tdrLeftRule, String tdrRightRule) {
/*  29 */     this.tableName = tableName;
/*  30 */     this.query = tdrQuery;
/*  31 */     this.leftRule = tdrLeftRule;
/*  32 */     this.rightRule = tdrRightRule;
/*     */   }
/*     */   
/*     */   public static ReferenceTable GetTdrReferenceTable(List<ReferenceTable> referenceTables, String tableName) {
/*  36 */     ReferenceTable table = null;
/*  37 */     for (ReferenceTable refTable : referenceTables) {
/*  38 */       if (refTable.tableName.equals(tableName)) {
/*  39 */         table = refTable;
/*     */         break;
/*     */       } 
/*     */     } 
/*  43 */     return table;
/*     */   }
/*     */   
/*     */   public String GetTableName() {
/*  47 */     return this.tableName;
/*     */   }
/*     */   
/*     */   public void SetTableName(String tableName) {
/*  51 */     this.tableName = tableName;
/*     */   }
/*     */   
/*     */   public String GetQuery() {
/*  55 */     return this.query;
/*     */   }
/*     */   
/*     */   public void SetQuery(String query) {
/*  59 */     this.query = query;
/*     */   }
/*     */   
/*     */   public String GetLeftRule() {
/*  63 */     return this.leftRule;
/*     */   }
/*     */   
/*     */   public void SetLeftRule(String leftRule) {
/*  67 */     this.leftRule = leftRule;
/*     */   }
/*     */   
/*     */   public String GetRightRule() {
/*  71 */     return this.rightRule;
/*     */   }
/*     */   
/*     */   public void SetRightRule(String rightRule) {
/*  75 */     this.rightRule = rightRule;
/*     */   }
/*     */   
/*     */   public void Run(Statement statement) throws SQLException {
/*  79 */     clear();
/*  80 */     Run(statement, this.leftRule, this.rightRule);
/*     */   }
/*     */   
/*     */   public void Run(Statement statement, String leftRule, String rightRule) throws SQLException {
/*  84 */     clear();
/*  85 */     this.leftRule = leftRule;
/*  86 */     this.rightRule = rightRule;
/*     */     
/*  88 */     ResultSet tdrResult = statement.executeQuery(this.query);
/*     */     
/*  90 */     String[] leftRules = leftRule.split(Pattern.quote("+"));
/*  91 */     String[] rightRules = rightRule.split(Pattern.quote("+"));
/*     */     
/*  93 */     while (tdrResult.next()) {
/*  94 */       StringBuilder key = new StringBuilder();
/*  95 */       StringBuilder value = new StringBuilder();
/*  96 */       for (String leftRule1 : leftRules) {
/*  97 */         boolean castable = true;
/*  98 */         String resKey = "";
/*     */         try {
/* 100 */           resKey = tdrResult.getString(Integer.parseInt(leftRule1));
/* 101 */           castable = (!resKey.equals(null) && !resKey.equals(""));
/* 102 */         } catch (Exception e) {
/* 103 */           castable = false;
/*     */         } 
/* 105 */         key.append(castable ? resKey.toUpperCase() : "null").append("_");
/*     */       } 
/*     */       
/* 108 */       for (String rightRule1 : rightRules) {
/* 109 */         boolean castable = true;
/* 110 */         String resValue = "";
/*     */         try {
/* 112 */           resValue = tdrResult.getString(Integer.parseInt(rightRule1));
/* 113 */           castable = (!resValue.equals(null) && !resValue.equals(""));
/* 114 */         } catch (Exception e) {
/* 115 */           castable = false;
/*     */         } 
/* 117 */         value.append(castable ? resValue.toUpperCase() : "null").append("_");
/*     */       } 
/*     */       
/* 120 */       key = new StringBuilder(key.substring(0, key.lastIndexOf("_")));
/* 121 */       value = new StringBuilder(value.substring(0, value.lastIndexOf("_")));
/*     */       
/* 123 */       put(key.toString(), value.toString());
/*     */     } 
/*     */   }
/*     */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\igs\core\har\db\ReferenceTable.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */