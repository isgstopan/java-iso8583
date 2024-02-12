/*     */ package com.igs.core.har.utils.log;
/*     */ 
/*     */ import com.igs.core.har.utils.FileHelper;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.math.BigInteger;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Arrays;
/*     */ import java.util.Comparator;
/*     */ import java.util.Date;
/*     */ 
/*     */ public class HouseKeeper {
/*     */   private Logger loggerInstance;
/*     */   private String path;
/*     */   private String dateTimeFormat;
/*  16 */   private BigInteger sizeLimit = new BigInteger("104857600");
/*  17 */   private int fileCountToKeep = 10;
/*     */   private String prefix;
/*     */   private String suffix;
/*     */   private boolean isKeeping;
/*     */   private boolean isAutoCreateArchivedDirectory;
/*     */   private boolean isNeedToMove;
/*     */   
/*     */   public HouseKeeper(Logger instance) {
/*  25 */     this.loggerInstance = instance;
/*  26 */     this.dateTimeFormat = "yyyyMMdd_HHmmss";
/*     */     
/*  28 */     this.prefix = null;
/*  29 */     this.suffix = null;
/*     */     
/*  31 */     this.path = instance.GetPath() + instance.GetFilename() + "_Archived";
/*     */   }
/*     */   
/*     */   public void SetPrefix(String pref) {
/*  35 */     this.prefix = pref;
/*     */   }
/*     */   
/*     */   public void SetSuffix(String suff) {
/*  39 */     this.suffix = suff;
/*     */   }
/*     */   
/*     */   public void SetFileCountToKeep(int fileCountToKeep) {
/*  43 */     this.fileCountToKeep = fileCountToKeep;
/*     */   }
/*     */   
/*     */   public void SetSizeLimit(long sizeLimit) {
/*  47 */     this.sizeLimit = new BigInteger(String.valueOf(sizeLimit));
/*     */   }
/*     */   
/*     */   public void SetAutoCreateArchivedDirectory(boolean auto) {
/*  51 */     this.isAutoCreateArchivedDirectory = auto;
/*     */   }
/*     */   
/*     */   public boolean GetAutoCreateArchivedDirectory() {
/*  55 */     return this.isAutoCreateArchivedDirectory;
/*     */   }
/*     */   
/*     */   public boolean SetDateTimeFormat(String newFormat) {
/*     */     try {
/*  60 */       String dateFormat = (new SimpleDateFormat(newFormat)).format(new Date());
/*  61 */       this.dateTimeFormat = newFormat;
/*  62 */       return true;
/*  63 */     } catch (Exception ex) {
/*  64 */       this.loggerInstance.Print("Error when set datetimeformat. old '" + this.dateTimeFormat + "' " + "to '" + newFormat + "'.\n" + "Detail: " + ex.getMessage(), 1);
/*     */       
/*  66 */       ex.printStackTrace();
/*     */       
/*  68 */       return false;
/*     */     } 
/*     */   }
/*     */   public String GetPrefix() {
/*  72 */     return this.prefix;
/*     */   }
/*     */   
/*     */   public String GetSuffix() {
/*  76 */     return this.suffix;
/*     */   }
/*     */   
/*     */   public String GetDateTimeFormat() {
/*  80 */     return this.dateTimeFormat;
/*     */   }
/*     */   
/*     */   public int GetFileCountToKeep() {
/*  84 */     return this.fileCountToKeep;
/*     */   }
/*     */   
/*     */   public BigInteger GetSizeLimit() {
/*  88 */     return this.sizeLimit;
/*     */   }
/*     */ 
/*     */   
/*     */   public synchronized void Keep() throws IOException {
/*  93 */     File file = new File(this.loggerInstance.GetFullpath());
/*  94 */     if (file.length() > GetSizeLimit().longValue()) {
/*  95 */       this.isKeeping = true;
/*  96 */       if (this.isNeedToMove) {
/*  97 */         String dateFormat = (new SimpleDateFormat(this.dateTimeFormat)).format(new Date());
/*  98 */         String filename = "";
/*     */         
/* 100 */         File dir = new File(this.path);
/* 101 */         if (!dir.exists() && this.isAutoCreateArchivedDirectory) {
/* 102 */           boolean succeeded = dir.mkdir();
/* 103 */           if (succeeded) {
/* 104 */             this.loggerInstance.Print("Archive folder has been created successfully!", 2);
/*     */           } else {
/* 106 */             this.loggerInstance.Print("Archive folder failed to create!", 2);
/*     */           } 
/*     */         } 
/* 109 */         filename = filename + this.path + (Logger.isWindows() ? "\\" : "/");
/* 110 */         if (GetPrefix() != null) {
/* 111 */           filename = filename + (!GetPrefix().equals("") ? (GetPrefix() + "_") : "");
/*     */         }
/* 113 */         filename = filename + dateFormat + "_" + file.getName().substring(0, file.getName().lastIndexOf("."));
/* 114 */         if (GetSuffix() != null) {
/* 115 */           filename = filename + (!GetSuffix().equals("") ? ("_" + GetSuffix()) : "");
/*     */         }
/* 117 */         filename = filename + file.getName().substring(file.getName().lastIndexOf("."));
/* 118 */         if (file.renameTo(new File(filename))) {
/* 119 */           this.loggerInstance.Print(file.getName() + " is moved successful!", 2);
/*     */         } else {
/* 121 */           this.loggerInstance.Print(file.getName() + " is failed to move!", 2);
/*     */         } 
/*     */       } 
/* 124 */       boolean delResult = file.delete();
/* 125 */       if (this.isNeedToMove) {
/* 126 */         if (delResult) {
/* 127 */           this.loggerInstance.Print(file.getName() + " has been deleted successfully!", 2);
/*     */         } else {
/* 129 */           this.loggerInstance.Print(file.getName() + " cannot be deleted!", 2);
/*     */         } 
/* 131 */         File directory = new File(this.path);
/* 132 */         File[] files = directory.listFiles();
/* 133 */         if (files != null && files.length > GetFileCountToKeep()) {
/* 134 */           Arrays.sort(files, (Comparator<? super File>)new FileHelper.LastModifiedComparator());
/* 135 */           for (int i = 0; i < files.length - GetFileCountToKeep(); i++) {
/* 136 */             boolean delhResult = files[files.length - i + 1].delete();
/* 137 */             if (delhResult) {
/* 138 */               this.loggerInstance.Print(files[files.length - i + 1].getName() + " has been deleted successfully!", 2);
/*     */             } else {
/* 140 */               this.loggerInstance.Print(files[files.length - i + 1].getName() + " cannot be deleted!", 2);
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } 
/* 145 */       this.isKeeping = false;
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean GetIsKeeping() {
/* 150 */     return this.isKeeping;
/*     */   }
/*     */   
/*     */   public void SetNeedToMove(boolean move) {
/* 154 */     this.isNeedToMove = move;
/*     */   }
/*     */   
/*     */   public boolean GetNeedToMove() {
/* 158 */     return this.isNeedToMove;
/*     */   }
/*     */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\igs\core\ha\\utils\log\HouseKeeper.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */