/*     */ package com.jcraft.jsch;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SftpStatVFS
/*     */ {
/*     */   private long bsize;
/*     */   private long frsize;
/*     */   private long blocks;
/*     */   private long bfree;
/*     */   private long bavail;
/*     */   private long files;
/*     */   private long ffree;
/*     */   private long favail;
/*     */   private long fsid;
/*     */   private long flag;
/*     */   private long namemax;
/*  54 */   int flags = 0;
/*     */   long size;
/*     */   int uid;
/*     */   int gid;
/*     */   int permissions;
/*     */   int atime;
/*     */   int mtime;
/*  61 */   String[] extended = null;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static SftpStatVFS getStatVFS(Buffer buf) {
/*  67 */     SftpStatVFS statvfs = new SftpStatVFS();
/*     */     
/*  69 */     statvfs.bsize = buf.getLong();
/*  70 */     statvfs.frsize = buf.getLong();
/*  71 */     statvfs.blocks = buf.getLong();
/*  72 */     statvfs.bfree = buf.getLong();
/*  73 */     statvfs.bavail = buf.getLong();
/*  74 */     statvfs.files = buf.getLong();
/*  75 */     statvfs.ffree = buf.getLong();
/*  76 */     statvfs.favail = buf.getLong();
/*  77 */     statvfs.fsid = buf.getLong();
/*  78 */     int flag = (int)buf.getLong();
/*  79 */     statvfs.namemax = buf.getLong();
/*     */     
/*  81 */     statvfs.flag = ((flag & 0x1) != 0) ? 1L : 0L;
/*     */     
/*  83 */     statvfs.flag |= ((flag & 0x2) != 0) ? 2L : 0L;
/*     */ 
/*     */     
/*  86 */     return statvfs;
/*     */   }
/*     */   
/*  89 */   public long getBlockSize() { return this.bsize; }
/*  90 */   public long getFragmentSize() { return this.frsize; }
/*  91 */   public long getBlocks() { return this.blocks; }
/*  92 */   public long getFreeBlocks() { return this.bfree; }
/*  93 */   public long getAvailBlocks() { return this.bavail; }
/*  94 */   public long getINodes() { return this.files; }
/*  95 */   public long getFreeINodes() { return this.ffree; }
/*  96 */   public long getAvailINodes() { return this.favail; }
/*  97 */   public long getFileSystemID() { return this.fsid; }
/*  98 */   public long getMountFlag() { return this.flag; } public long getMaximumFilenameLength() {
/*  99 */     return this.namemax;
/*     */   }
/*     */   public long getSize() {
/* 102 */     return getFragmentSize() * getBlocks() / 1024L;
/*     */   }
/*     */   
/*     */   public long getUsed() {
/* 106 */     return getFragmentSize() * (getBlocks() - getFreeBlocks()) / 1024L;
/*     */   }
/*     */   
/*     */   public long getAvailForNonRoot() {
/* 110 */     return getFragmentSize() * getAvailBlocks() / 1024L;
/*     */   }
/*     */   
/*     */   public long getAvail() {
/* 114 */     return getFragmentSize() * getFreeBlocks() / 1024L;
/*     */   }
/*     */   
/*     */   public int getCapacity() {
/* 118 */     return (int)(100L * (getBlocks() - getFreeBlocks()) / getBlocks());
/*     */   }
/*     */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\SftpStatVFS.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */