/*     */ package com.jcraft.jsch;
/*     */ 
/*     */ import java.util.Date;
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
/*     */ public class SftpATTRS
/*     */ {
/*     */   static final int S_ISUID = 2048;
/*     */   static final int S_ISGID = 1024;
/*     */   static final int S_ISVTX = 512;
/*     */   static final int S_IRUSR = 256;
/*     */   static final int S_IWUSR = 128;
/*     */   static final int S_IXUSR = 64;
/*     */   static final int S_IREAD = 256;
/*     */   static final int S_IWRITE = 128;
/*     */   static final int S_IEXEC = 64;
/*     */   static final int S_IRGRP = 32;
/*     */   static final int S_IWGRP = 16;
/*     */   static final int S_IXGRP = 8;
/*     */   static final int S_IROTH = 4;
/*     */   static final int S_IWOTH = 2;
/*     */   static final int S_IXOTH = 1;
/*     */   private static final int pmask = 4095;
/*     */   public static final int SSH_FILEXFER_ATTR_SIZE = 1;
/*     */   public static final int SSH_FILEXFER_ATTR_UIDGID = 2;
/*     */   public static final int SSH_FILEXFER_ATTR_PERMISSIONS = 4;
/*     */   public static final int SSH_FILEXFER_ATTR_ACMODTIME = 8;
/*     */   public static final int SSH_FILEXFER_ATTR_EXTENDED = -2147483648;
/*     */   static final int S_IFMT = 61440;
/*     */   static final int S_IFIFO = 4096;
/*     */   static final int S_IFCHR = 8192;
/*     */   static final int S_IFDIR = 16384;
/*     */   static final int S_IFBLK = 24576;
/*     */   static final int S_IFREG = 32768;
/*     */   static final int S_IFLNK = 40960;
/*     */   static final int S_IFSOCK = 49152;
/*     */   
/*     */   public String getPermissionsString() {
/*  73 */     StringBuffer buf = new StringBuffer(10);
/*     */     
/*  75 */     if (isDir()) { buf.append('d'); }
/*  76 */     else if (isLink()) { buf.append('l'); }
/*  77 */     else { buf.append('-'); }
/*     */     
/*  79 */     if ((this.permissions & 0x100) != 0) { buf.append('r'); }
/*  80 */     else { buf.append('-'); }
/*     */     
/*  82 */     if ((this.permissions & 0x80) != 0) { buf.append('w'); }
/*  83 */     else { buf.append('-'); }
/*     */     
/*  85 */     if ((this.permissions & 0x800) != 0) { buf.append('s'); }
/*  86 */     else if ((this.permissions & 0x40) != 0) { buf.append('x'); }
/*  87 */     else { buf.append('-'); }
/*     */     
/*  89 */     if ((this.permissions & 0x20) != 0) { buf.append('r'); }
/*  90 */     else { buf.append('-'); }
/*     */     
/*  92 */     if ((this.permissions & 0x10) != 0) { buf.append('w'); }
/*  93 */     else { buf.append('-'); }
/*     */     
/*  95 */     if ((this.permissions & 0x400) != 0) { buf.append('s'); }
/*  96 */     else if ((this.permissions & 0x8) != 0) { buf.append('x'); }
/*  97 */     else { buf.append('-'); }
/*     */     
/*  99 */     if ((this.permissions & 0x4) != 0) { buf.append('r'); }
/* 100 */     else { buf.append('-'); }
/*     */     
/* 102 */     if ((this.permissions & 0x2) != 0) { buf.append('w'); }
/* 103 */     else { buf.append('-'); }
/*     */     
/* 105 */     if ((this.permissions & 0x1) != 0) { buf.append('x'); }
/* 106 */     else { buf.append('-'); }
/* 107 */      return buf.toString();
/*     */   }
/*     */   
/*     */   public String getAtimeString() {
/* 111 */     Date date = new Date(this.atime * 1000L);
/* 112 */     return date.toString();
/*     */   }
/*     */   
/*     */   public String getMtimeString() {
/* 116 */     Date date = new Date(this.mtime * 1000L);
/* 117 */     return date.toString();
/*     */   }
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
/* 135 */   int flags = 0;
/*     */   long size;
/*     */   int uid;
/*     */   int gid;
/*     */   int permissions;
/*     */   int atime;
/*     */   int mtime;
/* 142 */   String[] extended = null;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static SftpATTRS getATTR(Buffer buf) {
/* 148 */     SftpATTRS attr = new SftpATTRS();
/* 149 */     attr.flags = buf.getInt();
/* 150 */     if ((attr.flags & 0x1) != 0) attr.size = buf.getLong(); 
/* 151 */     if ((attr.flags & 0x2) != 0) {
/* 152 */       attr.uid = buf.getInt(); attr.gid = buf.getInt();
/*     */     } 
/* 154 */     if ((attr.flags & 0x4) != 0) {
/* 155 */       attr.permissions = buf.getInt();
/*     */     }
/* 157 */     if ((attr.flags & 0x8) != 0) {
/* 158 */       attr.atime = buf.getInt();
/*     */     }
/* 160 */     if ((attr.flags & 0x8) != 0) {
/* 161 */       attr.mtime = buf.getInt();
/*     */     }
/* 163 */     if ((attr.flags & Integer.MIN_VALUE) != 0) {
/* 164 */       int count = buf.getInt();
/* 165 */       if (count > 0) {
/* 166 */         attr.extended = new String[count * 2];
/* 167 */         for (int i = 0; i < count; i++) {
/* 168 */           attr.extended[i * 2] = Util.byte2str(buf.getString());
/* 169 */           attr.extended[i * 2 + 1] = Util.byte2str(buf.getString());
/*     */         } 
/*     */       } 
/*     */     } 
/* 173 */     return attr;
/*     */   }
/*     */   
/*     */   int length() {
/* 177 */     int len = 4;
/*     */     
/* 179 */     if ((this.flags & 0x1) != 0) len += 8; 
/* 180 */     if ((this.flags & 0x2) != 0) len += 8; 
/* 181 */     if ((this.flags & 0x4) != 0) len += 4; 
/* 182 */     if ((this.flags & 0x8) != 0) len += 8; 
/* 183 */     if ((this.flags & Integer.MIN_VALUE) != 0) {
/* 184 */       len += 4;
/* 185 */       int count = this.extended.length / 2;
/* 186 */       if (count > 0) {
/* 187 */         for (int i = 0; i < count; i++) {
/* 188 */           len += 4; len += this.extended[i * 2].length();
/* 189 */           len += 4; len += this.extended[i * 2 + 1].length();
/*     */         } 
/*     */       }
/*     */     } 
/* 193 */     return len;
/*     */   }
/*     */   
/*     */   void dump(Buffer buf) {
/* 197 */     buf.putInt(this.flags);
/* 198 */     if ((this.flags & 0x1) != 0) buf.putLong(this.size); 
/* 199 */     if ((this.flags & 0x2) != 0) {
/* 200 */       buf.putInt(this.uid); buf.putInt(this.gid);
/*     */     } 
/* 202 */     if ((this.flags & 0x4) != 0) {
/* 203 */       buf.putInt(this.permissions);
/*     */     }
/* 205 */     if ((this.flags & 0x8) != 0) buf.putInt(this.atime); 
/* 206 */     if ((this.flags & 0x8) != 0) buf.putInt(this.mtime); 
/* 207 */     if ((this.flags & Integer.MIN_VALUE) != 0) {
/* 208 */       int count = this.extended.length / 2;
/* 209 */       if (count > 0)
/* 210 */         for (int i = 0; i < count; i++) {
/* 211 */           buf.putString(Util.str2byte(this.extended[i * 2]));
/* 212 */           buf.putString(Util.str2byte(this.extended[i * 2 + 1]));
/*     */         }  
/*     */     } 
/*     */   }
/*     */   
/*     */   void setFLAGS(int flags) {
/* 218 */     this.flags = flags;
/*     */   }
/*     */   public void setSIZE(long size) {
/* 221 */     this.flags |= 0x1;
/* 222 */     this.size = size;
/*     */   }
/*     */   public void setUIDGID(int uid, int gid) {
/* 225 */     this.flags |= 0x2;
/* 226 */     this.uid = uid;
/* 227 */     this.gid = gid;
/*     */   }
/*     */   public void setACMODTIME(int atime, int mtime) {
/* 230 */     this.flags |= 0x8;
/* 231 */     this.atime = atime;
/* 232 */     this.mtime = mtime;
/*     */   }
/*     */   public void setPERMISSIONS(int permissions) {
/* 235 */     this.flags |= 0x4;
/* 236 */     permissions = this.permissions & 0xFFFFF000 | permissions & 0xFFF;
/* 237 */     this.permissions = permissions;
/*     */   }
/*     */   
/*     */   private boolean isType(int mask) {
/* 241 */     return ((this.flags & 0x4) != 0 && (this.permissions & 0xF000) == mask);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isReg() {
/* 246 */     return isType(32768);
/*     */   }
/*     */   
/*     */   public boolean isDir() {
/* 250 */     return isType(16384);
/*     */   }
/*     */   
/*     */   public boolean isChr() {
/* 254 */     return isType(8192);
/*     */   }
/*     */   
/*     */   public boolean isBlk() {
/* 258 */     return isType(24576);
/*     */   }
/*     */   
/*     */   public boolean isFifo() {
/* 262 */     return isType(4096);
/*     */   }
/*     */   
/*     */   public boolean isLink() {
/* 266 */     return isType(40960);
/*     */   }
/*     */   
/*     */   public boolean isSock() {
/* 270 */     return isType(49152);
/*     */   }
/*     */   
/* 273 */   public int getFlags() { return this.flags; }
/* 274 */   public long getSize() { return this.size; }
/* 275 */   public int getUId() { return this.uid; }
/* 276 */   public int getGId() { return this.gid; }
/* 277 */   public int getPermissions() { return this.permissions; }
/* 278 */   public int getATime() { return this.atime; }
/* 279 */   public int getMTime() { return this.mtime; } public String[] getExtended() {
/* 280 */     return this.extended;
/*     */   }
/*     */   public String toString() {
/* 283 */     return getPermissionsString() + " " + getUId() + " " + getGId() + " " + getSize() + " " + getMtimeString();
/*     */   }
/*     */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\SftpATTRS.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */