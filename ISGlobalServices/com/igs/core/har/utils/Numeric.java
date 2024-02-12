/*     */ package com.igs.core.har.utils;
/*     */ 
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class Numeric
/*     */ {
/*     */   public static Result<Integer> TryParseInteger(String s) {
/*  16 */     Result<Integer> result = new Result<>();
/*     */     
/*     */     try {
/*  19 */       result.SetValue(Integer.valueOf(Integer.parseInt(s)));
/*  20 */       result.SetSucceeded(true);
/*  21 */     } catch (Exception e) {
/*  22 */       result.SetValue(Integer.valueOf(0));
/*  23 */       result.SetSucceeded(false);
/*     */     } 
/*     */     
/*  26 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Result<Float> TryParseFloat(String s) {
/*  35 */     Result<Float> result = new Result<>();
/*     */     
/*     */     try {
/*  38 */       result.SetValue(Float.valueOf(Float.parseFloat(s)));
/*  39 */       result.SetSucceeded(true);
/*  40 */     } catch (Exception e) {
/*  41 */       result.SetValue(Float.valueOf(0.0F));
/*  42 */       result.SetSucceeded(false);
/*     */     } 
/*     */     
/*  45 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Result<Double> TryParseDouble(String s) {
/*  54 */     Result<Double> result = new Result<>();
/*     */     
/*     */     try {
/*  57 */       result.SetValue(Double.valueOf(Double.parseDouble(s)));
/*  58 */       result.SetSucceeded(true);
/*  59 */     } catch (Exception e) {
/*  60 */       result.SetValue(Double.valueOf(0.0D));
/*  61 */       result.SetSucceeded(false);
/*     */     } 
/*     */     
/*  64 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Result<BigInteger> TryParseBigInteger(String s) {
/*  73 */     Result<BigInteger> result = new Result<>();
/*     */     
/*     */     try {
/*  76 */       result.SetValue(new BigInteger(s));
/*  77 */       result.SetSucceeded(true);
/*  78 */     } catch (Exception e) {
/*  79 */       result.SetValue(new BigInteger("0"));
/*  80 */       result.SetSucceeded(false);
/*     */     } 
/*     */     
/*  83 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Result<BigDecimal> TryParseBigDecimal(String s) {
/*  92 */     Result<BigDecimal> result = new Result<>();
/*     */     
/*     */     try {
/*  95 */       result.SetValue(new BigDecimal(s));
/*  96 */       result.SetSucceeded(true);
/*  97 */     } catch (Exception e) {
/*  98 */       result.SetValue(new BigDecimal("0"));
/*  99 */       result.SetSucceeded(false);
/*     */     } 
/*     */     
/* 102 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public static final class Result<T>
/*     */   {
/*     */     private boolean succeeded;
/*     */     
/*     */     private T value;
/*     */ 
/*     */     
/*     */     public boolean IsSucceeded() {
/* 114 */       return this.succeeded;
/*     */     }
/*     */     
/*     */     void SetSucceeded(boolean succeeded) {
/* 118 */       this.succeeded = succeeded;
/*     */     }
/*     */     
/*     */     public T GetValue() {
/* 122 */       return this.value;
/*     */     }
/*     */     
/*     */     void SetValue(T value) {
/* 126 */       this.value = value;
/*     */     }
/*     */   }
/*     */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\igs\core\ha\\utils\Numeric.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */