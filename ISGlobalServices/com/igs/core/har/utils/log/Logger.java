/*     */ package com.igs.core.har.utils.log;
/*     */ 
/*     */ import com.igs.core.har.utils.FileHelper;
/*     */ import com.igs.core.har.utils.StringHelper;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.text.SimpleDateFormat;
/*     */ import java.util.Arrays;
/*     */ import java.util.Comparator;
/*     */ import java.util.Date;
/*     */ import java.util.concurrent.BlockingQueue;
/*     */ import java.util.concurrent.LinkedBlockingQueue;
/*     */ 
/*     */ public class Logger {
/*     */   public static final int error = 1;
/*     */   public static final int info = 2;
/*     */   public static final int debug = 3;
/*  20 */   private static BlockingQueue<HLoggerData> queue = new LinkedBlockingQueue<>();
/*  21 */   private static String OS = System.getProperty("os.name").toLowerCase();
/*     */   private String path;
/*     */   private String filename;
/*  24 */   private int printLevel = 3;
/*     */   private boolean isHousekeepingEnabled;
/*     */   private HouseKeeper houseKeeper;
/*     */   
/*     */   private Logger(String path, String filename) {
/*  29 */     this.path = path;
/*  30 */     this.filename = filename;
/*  31 */     this.isHousekeepingEnabled = false;
/*     */     
/*  33 */     this.houseKeeper = new HouseKeeper(this);
/*     */   }
/*     */   
/*     */   public static void PrintException(Logger logger, Exception e, String errHeader) {
/*  37 */     logger.Print(errHeader, 1);
/*  38 */     logger.Print("Error message: " + e.getMessage(), 1);
/*  39 */     logger.Print("Stack Trace Element..", 1);
/*  40 */     StackTraceElement[] els = e.getStackTrace();
/*  41 */     for (StackTraceElement el : els) {
/*  42 */       logger.Print(el.toString(), 1);
/*     */     }
/*     */   }
/*     */   
/*     */   public static void Print(String fileFullpath, String data, String archivePath) {
/*     */     try {
/*  48 */       String logFormat = (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss,SSS")).format(new Date()) + " : " + data;
/*  49 */       File f = new File(fileFullpath);
/*  50 */       boolean createFile = false;
/*  51 */       if (!f.exists()) {
/*  52 */         createFile = f.createNewFile();
/*  53 */       } else if (f.exists() && !f.isDirectory()) {
/*  54 */         createFile = true;
/*     */       } 
/*  56 */       if (createFile) {
/*  57 */         FileWriter fw = new FileWriter(fileFullpath, true);
/*  58 */         fw.write(logFormat + "\n");
/*  59 */         fw.flush();
/*  60 */         fw.close();
/*     */       } 
/*  62 */       Housekeeping(fileFullpath, archivePath);
/*  63 */     } catch (IOException iOException) {}
/*     */   }
/*     */ 
/*     */   
/*     */   public static void Housekeeping(String fileFullpath, String archivePath) {
/*  68 */     File file = new File(fileFullpath);
/*  69 */     if (file.length() > 209715200L) {
/*  70 */       String dateFormat = (new SimpleDateFormat("yyyyMMdd_HHmmss")).format(new Date());
/*  71 */       if (file.renameTo(new File(archivePath + dateFormat + "_" + file.getName()))) {
/*  72 */         Print(fileFullpath, file.getName() + " is moved successful!", archivePath);
/*     */       } else {
/*  74 */         Print(fileFullpath, file.getName() + " is failed to move!", archivePath);
/*     */       } 
/*  76 */       boolean delResult = file.delete();
/*  77 */       if (delResult) {
/*  78 */         Print(fileFullpath, file.getName() + " has been deleted successfully!", archivePath);
/*     */       } else {
/*  80 */         Print(fileFullpath, file.getName() + " cannot be deleted!", archivePath);
/*     */       } 
/*     */     } 
/*  83 */     File directory = new File(archivePath);
/*  84 */     File[] files = directory.listFiles();
/*  85 */     if (files != null && files.length > 10) {
/*  86 */       Arrays.sort(files, (Comparator<? super File>)new FileHelper.LastModifiedComparator());
/*  87 */       boolean delResult = files[files.length - 1].delete();
/*  88 */       if (delResult) {
/*  89 */         Print(fileFullpath, files[files.length - 1].getName() + " has been deleted successfully!", archivePath);
/*     */       } else {
/*  91 */         Print(fileFullpath, files[files.length - 1].getName() + " cannot be deleted!", archivePath);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public static Logger Register(String logFilename, String logPath) {
/*  97 */     Logger logger = new Logger(logPath, logFilename);
/*     */     try {
/*  99 */       File f = new File(logger.GetFullpath());
/* 100 */       boolean isExistOrCreated = false;
/* 101 */       if (!f.exists()) {
/* 102 */         isExistOrCreated = f.createNewFile();
/* 103 */       } else if (f.exists() && !f.isDirectory()) {
/* 104 */         isExistOrCreated = true;
/*     */       } 
/* 106 */       if (isExistOrCreated) {
/* 107 */         logger.Print(Logger.class.toString().replace("class ", "") + " file created/opened!", 2);
/*     */       }
/* 109 */     } catch (IOException e) {
/* 110 */       e.printStackTrace();
/*     */     } 
/* 112 */     return logger;
/*     */   }
/*     */   
/*     */   public static Logger Register(String logFilename, String logPath, boolean initiateMessage) {
/* 116 */     Logger logger = new Logger(logPath, logFilename);
/*     */     try {
/* 118 */       File f = new File(logger.GetFullpath());
/* 119 */       boolean isExistOrCreated = false;
/* 120 */       if (!f.exists()) {
/* 121 */         isExistOrCreated = f.createNewFile();
/* 122 */       } else if (f.exists() && !f.isDirectory()) {
/* 123 */         isExistOrCreated = true;
/*     */       } 
/* 125 */       if (isExistOrCreated && initiateMessage) {
/* 126 */         logger.Print(Logger.class.toString().replace("class ", "") + " file created/opened!", 2);
/*     */       }
/* 128 */     } catch (IOException e) {
/* 129 */       e.printStackTrace();
/*     */     } 
/* 131 */     return logger;
/*     */   }
/*     */   
/*     */   public static boolean isWindows() {
/* 135 */     return OS.contains("win");
/*     */   }
/*     */   
/*     */   public static boolean isMac() {
/* 139 */     return OS.contains("mac");
/*     */   }
/*     */   
/*     */   public static boolean isUnix() {
/* 143 */     return (OS.contains("nix") || OS.contains("nux") || OS.contains("aix"));
/*     */   }
/*     */   
/*     */   public static boolean isSolaris() {
/* 147 */     return OS.contains("sunos");
/*     */   }
/*     */   
/*     */   public void PrintException(Exception e) {
/* 151 */     Print("Error message: " + e.getMessage(), 1);
/* 152 */     Print("Stack Trace Element..", 1);
/* 153 */     StackTraceElement[] els = e.getStackTrace();
/* 154 */     for (StackTraceElement el : els) {
/* 155 */       Print(el.toString(), 1);
/*     */     }
/*     */   }
/*     */   
/*     */   private synchronized void InternalLog(String message) {
/* 160 */     String fullpath = GetFullpath();
/*     */     try {
/* 162 */       File f = new File(fullpath);
/* 163 */       boolean createFile = false;
/* 164 */       if (!f.exists()) {
/* 165 */         createFile = f.createNewFile();
/* 166 */       } else if (f.exists() && !f.isDirectory()) {
/* 167 */         createFile = true;
/*     */       } 
/* 169 */       if (createFile && f.canWrite()) {
/* 170 */         BufferedWriter writer = new BufferedWriter(new FileWriter(fullpath, true));
/* 171 */         writer.write(message + "\n");
/* 172 */         writer.flush();
/* 173 */         writer.close();
/*     */       } 
/* 175 */       if (this.isHousekeepingEnabled) {
/* 176 */         this.houseKeeper.Keep();
/*     */       }
/* 178 */     } catch (IOException e) {
/* 179 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void Print(String message, int messageLvl) {
/* 184 */     if (messageLvl <= GetPrintLevel())
/* 185 */       switch (messageLvl) {
/*     */         case 1:
/* 187 */           InternalLog("[" + (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss,SSS")).format(new Date()) + "][Error] : " + message);
/*     */           break;
/*     */         case 2:
/* 190 */           InternalLog("[" + (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss,SSS")).format(new Date()) + "][Info]  : " + message);
/*     */           break;
/*     */         case 3:
/* 193 */           InternalLog("[" + (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss,SSS")).format(new Date()) + "][Debug] : " + message);
/*     */           break;
/*     */       }  
/*     */   }
/*     */   
/*     */   public void Print(String message, int messageLvl, String section) {
/* 199 */     section = StringHelper.PadRight(section, 20, ' ');
/* 200 */     if (messageLvl <= GetPrintLevel())
/* 201 */       switch (messageLvl) {
/*     */         case 1:
/* 203 */           InternalLog("[" + (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss,SSS")).format(new Date()) + "][Error] [" + section + "] : " + message);
/*     */           break;
/*     */         case 2:
/* 206 */           InternalLog("[" + (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss,SSS")).format(new Date()) + "][Info]  [" + section + "] : " + message);
/*     */           break;
/*     */         case 3:
/* 209 */           InternalLog("[" + (new SimpleDateFormat("yyyy/MM/dd HH:mm:ss,SSS")).format(new Date()) + "][Debug] [" + section + "] : " + message);
/*     */           break;
/*     */       }  
/*     */   }
/*     */   
/*     */   public void Queue(String message, int messageLvl) {
/* 215 */     queue.add(new HLoggerData(message, messageLvl));
/*     */   }
/*     */   
/*     */   public void SetPrintLevel(int printLvl) {
/* 219 */     this.printLevel = printLvl;
/*     */   }
/*     */   
/*     */   public void SetIsHousekeepingEnabled(boolean housekeepingEnabled) {
/* 223 */     this.isHousekeepingEnabled = housekeepingEnabled;
/*     */   }
/*     */   
/*     */   public void SetAutoCreateArchivedDirectory(boolean auto) {
/* 227 */     this.houseKeeper.SetAutoCreateArchivedDirectory(auto);
/*     */   }
/*     */   
/*     */   public int GetPrintLevel() {
/* 231 */     return this.printLevel;
/*     */   }
/*     */   
/*     */   public boolean GetIsHousekeepingEnabled() {
/* 235 */     return this.isHousekeepingEnabled;
/*     */   }
/*     */   
/*     */   public String GetFullpath() {
/* 239 */     String pathSeparator = isWindows() ? "\\" : "/";
/* 240 */     return ((this.path != null && this.path.endsWith(pathSeparator)) ? (this.path + this.filename) : (this.path + pathSeparator + this.filename)) + ".log";
/*     */   }
/*     */   
/*     */   public String GetPath() {
/* 244 */     String pathSeparator = isWindows() ? "\\" : "/";
/* 245 */     return this.path + pathSeparator;
/*     */   }
/*     */   
/*     */   public String GetFilename() {
/* 249 */     return this.filename;
/*     */   }
/*     */   
/*     */   public HouseKeeper GetHouseKeeper() {
/* 253 */     return this.houseKeeper;
/*     */   }
/*     */   
/*     */   public static class HLoggerEngine implements Runnable {
/*     */     private Thread thread;
/*     */     private String threadName;
/*     */     private Logger loggerInstance;
/*     */     private boolean isRun = false;
/*     */     
/*     */     public HLoggerEngine(Logger loggerInstance, String threadName) {
/* 263 */       this.loggerInstance = loggerInstance;
/* 264 */       this.threadName = threadName;
/*     */     }
/*     */     
/*     */     public void run() {
/* 268 */       System.out.println("Engine is running..");
/*     */       while (true) {
/* 270 */         if (!this.loggerInstance.GetHouseKeeper().GetIsKeeping() && !Logger.queue.isEmpty()) {
/*     */           try {
/* 272 */             Logger.HLoggerData hLoggerData = Logger.queue.take();
/* 273 */             this.loggerInstance.Print(hLoggerData.GetMessage(), hLoggerData.GetMessageLevel());
/* 274 */           } catch (InterruptedException e) {
/* 275 */             e.printStackTrace();
/*     */           } 
/*     */         }
/* 278 */         if (!this.isRun) {
/* 279 */           System.out.println("Engine is stopped..");
/*     */           return;
/*     */         } 
/*     */       }  } public void start() {
/* 283 */       this.isRun = true;
/* 284 */       if (this.thread == null) {
/* 285 */         this.thread = new Thread(this, this.threadName);
/*     */       }
/* 287 */       this.thread.start();
/*     */     }
/*     */     
/*     */     public void stop() {
/* 291 */       this.isRun = false;
/*     */     }
/*     */   }
/*     */   
/*     */   public class HLoggerData {
/*     */     private int msgLevel;
/*     */     private String msg;
/*     */     
/*     */     public HLoggerData(String msg, int msgLevel) {
/* 300 */       this.msg = msg;
/* 301 */       this.msgLevel = msgLevel;
/*     */     }
/*     */     
/*     */     public String GetMessage() {
/* 305 */       return this.msg;
/*     */     }
/*     */     
/*     */     public int GetMessageLevel() {
/* 309 */       return this.msgLevel;
/*     */     }
/*     */   }
/*     */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\igs\core\ha\\utils\log\Logger.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */