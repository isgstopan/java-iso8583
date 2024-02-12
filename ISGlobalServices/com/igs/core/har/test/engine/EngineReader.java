/*     */ package com.igs.core.har.test.engine;
/*     */ 
/*     */ import com.igs.core.har.utils.RunnerTask;
/*     */ import com.igs.core.har.utils.log.Logger;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.RandomAccessFile;
/*     */ import java.util.Arrays;
/*     */ import java.util.Comparator;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ 
/*     */ public class EngineReader extends RunnerTask {
/*  13 */   private long fileLength = 0L;
/*  14 */   private long lastPos = 0L;
/*  15 */   private long fileIndex = 0L;
/*  16 */   private String currentFilename = "";
/*  17 */   private ConcurrentHashMap<String, Boolean> filesMap = new ConcurrentHashMap<>();
/*     */   
/*     */   private int GetAvailableFiles(ConcurrentHashMap<String, Boolean> files) {
/*  20 */     int notRead = 0;
/*  21 */     for (String f : files.keySet()) {
/*  22 */       if (!((Boolean)files.get(f)).booleanValue()) {
/*  23 */         notRead++;
/*     */       }
/*     */     } 
/*  26 */     return notRead;
/*     */   }
/*     */   
/*     */   private String[] Sort(ConcurrentHashMap<String, Boolean> files) {
/*  30 */     String[] fs = new String[files.keySet().size()];
/*  31 */     files.keySet().toArray((Object[])fs);
/*     */     
/*  33 */     if (fs.length > 0) {
/*  34 */       Arrays.sort(fs, new Comparator<String>() {
/*     */             public int compare(String o1, String o2) {
/*  36 */               if (o1.compareTo(o2) > 0)
/*  37 */                 return 1; 
/*  38 */               if (o1.compareTo(o2) < 1) {
/*  39 */                 return -1;
/*     */               }
/*  41 */               return 0;
/*     */             }
/*     */           });
/*     */     }
/*     */ 
/*     */     
/*  47 */     return fs;
/*     */   }
/*     */   
/*     */   public EngineReader() {
/*  51 */     super("Reader", 10, null);
/*  52 */     Logger genLogger = Logger.Register("All", "F:\\Temp\\Logs", false);
/*  53 */     genLogger.SetIsHousekeepingEnabled(true);
/*  54 */     genLogger.GetHouseKeeper().SetAutoCreateArchivedDirectory(false);
/*  55 */     genLogger.GetHouseKeeper().SetNeedToMove(false);
/*  56 */     genLogger.SetPrintLevel(3);
/*  57 */     SetLogger(genLogger);
/*     */   }
/*     */ 
/*     */   
/*     */   public void DoJob(Object[] args) {
/*     */     try {
/*  63 */       File dir = new File("F:\\Temp\\Logs");
/*  64 */       File[] files = dir.listFiles();
/*     */       
/*  66 */       if (files != null && files.length > 0) {
/*  67 */         Arrays.sort(files, new Comparator<File>() {
/*     */               public int compare(File o1, File o2) {
/*  69 */                 return Long.compare(o1.lastModified(), o2.lastModified());
/*     */               }
/*     */             });
/*     */         
/*  73 */         for (File f : files) {
/*  74 */           if (!this.filesMap.containsKey(f.getPath())) {
/*  75 */             System.out.println(f.getPath());
/*  76 */             this.filesMap.put(f.getPath(), Boolean.valueOf(false));
/*     */           } 
/*     */         } 
/*     */         
/*  80 */         if (this.currentFilename.equalsIgnoreCase("")) {
/*  81 */           this.currentFilename = Sort(this.filesMap)[(int)this.fileIndex++];
/*  82 */           this.fileLength = 0L;
/*  83 */           this.lastPos = 0L;
/*     */         } 
/*     */         
/*  86 */         if (!this.currentFilename.equalsIgnoreCase("") && (this.lastPos >= this.fileLength || this.lastPos >= this.fileLength - 1L) && !Sort(this.filesMap)[this.filesMap.size() - 1].equalsIgnoreCase(this.currentFilename) && GetAvailableFiles(this.filesMap) > 0) {
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*  91 */           this.filesMap.put(this.currentFilename, Boolean.valueOf(true));
/*  92 */           this.currentFilename = Sort(this.filesMap)[(int)this.fileIndex++];
/*  93 */           this.fileLength = 0L;
/*  94 */           this.lastPos = 0L;
/*     */         } 
/*     */       } 
/*     */       
/*  98 */       if (!this.currentFilename.equalsIgnoreCase("")) {
/*     */         
/* 100 */         RandomAccessFile raf = new RandomAccessFile(new File(this.currentFilename), "r");
/* 101 */         StringBuilder sb = new StringBuilder();
/*     */         
/* 103 */         while (this.lastPos < (this.fileLength = raf.length())) {
/* 104 */           raf.seek(this.lastPos);
/* 105 */           int currByte = raf.readByte();
/*     */           
/* 107 */           this.lastPos++;
/*     */           
/* 109 */           if (currByte == 13 || currByte == 10) {
/* 110 */             String currLine = sb.toString();
/* 111 */             GetLogger().Print(currLine, 2);
/* 112 */             sb.delete(0, sb.length()); continue;
/*     */           } 
/* 114 */           sb.append((char)currByte);
/*     */         } 
/*     */         
/* 117 */         raf.close();
/*     */       } 
/* 119 */     } catch (IOException e) {
/* 120 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\igs\core\har\test\engine\EngineReader.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */