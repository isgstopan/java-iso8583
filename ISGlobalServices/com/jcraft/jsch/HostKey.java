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
/*     */ public class HostKey
/*     */ {
/*  34 */   private static final byte[][] names = new byte[][] { Util.str2byte("ssh-dss"), Util.str2byte("ssh-rsa"), Util.str2byte("ecdsa-sha2-nistp256"), Util.str2byte("ecdsa-sha2-nistp384"), Util.str2byte("ecdsa-sha2-nistp521") };
/*     */   
/*     */   protected static final int GUESS = 0;
/*     */   
/*     */   public static final int SSHDSS = 1;
/*     */   
/*     */   public static final int SSHRSA = 2;
/*     */   
/*     */   public static final int ECDSA256 = 3;
/*     */   
/*     */   public static final int ECDSA384 = 4;
/*     */   
/*     */   public static final int ECDSA521 = 5;
/*     */   
/*     */   static final int UNKNOWN = 6;
/*     */   
/*     */   protected String marker;
/*     */   protected String host;
/*     */   protected int type;
/*     */   protected byte[] key;
/*     */   protected String comment;
/*     */   
/*     */   public HostKey(String host, byte[] key) throws JSchException {
/*  57 */     this(host, 0, key);
/*     */   }
/*     */   
/*     */   public HostKey(String host, int type, byte[] key) throws JSchException {
/*  61 */     this(host, type, key, null);
/*     */   }
/*     */   public HostKey(String host, int type, byte[] key, String comment) throws JSchException {
/*  64 */     this("", host, type, key, comment);
/*     */   }
/*     */   public HostKey(String marker, String host, int type, byte[] key, String comment) throws JSchException {
/*  67 */     this.marker = marker;
/*  68 */     this.host = host;
/*  69 */     if (type == 0) {
/*  70 */       if (key[8] == 100) { this.type = 1; }
/*  71 */       else if (key[8] == 114) { this.type = 2; }
/*  72 */       else if (key[8] == 97 && key[20] == 50) { this.type = 3; }
/*  73 */       else if (key[8] == 97 && key[20] == 51) { this.type = 4; }
/*  74 */       else if (key[8] == 97 && key[20] == 53) { this.type = 5; }
/*  75 */       else { throw new JSchException("invalid key type"); }
/*     */     
/*     */     } else {
/*  78 */       this.type = type;
/*     */     } 
/*  80 */     this.key = key;
/*  81 */     this.comment = comment;
/*     */   }
/*     */   public String getHost() {
/*  84 */     return this.host;
/*     */   } public String getType() {
/*  86 */     if (this.type == 1 || this.type == 2 || this.type == 3 || this.type == 4 || this.type == 5)
/*     */     {
/*     */ 
/*     */ 
/*     */       
/*  91 */       return Util.byte2str(names[this.type - 1]);
/*     */     }
/*  93 */     return "UNKNOWN";
/*     */   }
/*     */   protected static int name2type(String name) {
/*  96 */     for (int i = 0; i < names.length; i++) {
/*  97 */       if (Util.byte2str(names[i]).equals(name)) {
/*  98 */         return i + 1;
/*     */       }
/*     */     } 
/* 101 */     return 6;
/*     */   }
/*     */   public String getKey() {
/* 104 */     return Util.byte2str(Util.toBase64(this.key, 0, this.key.length));
/*     */   }
/*     */   public String getFingerPrint(JSch jsch) {
/* 107 */     HASH hash = null;
/*     */     try {
/* 109 */       Class c = Class.forName(JSch.getConfig("md5"));
/* 110 */       hash = (HASH)c.newInstance();
/*     */     } catch (Exception e) {
/* 112 */       System.err.println("getFingerPrint: " + e);
/* 113 */     }  return Util.getFingerPrint(hash, this.key);
/*     */   }
/* 115 */   public String getComment() { return this.comment; } public String getMarker() {
/* 116 */     return this.marker;
/*     */   }
/*     */   boolean isMatched(String _host) {
/* 119 */     return isIncluded(_host);
/*     */   }
/*     */   
/*     */   private boolean isIncluded(String _host) {
/* 123 */     int i = 0;
/* 124 */     String hosts = this.host;
/* 125 */     int hostslen = hosts.length();
/* 126 */     int hostlen = _host.length();
/*     */     
/* 128 */     while (i < hostslen) {
/* 129 */       int j = hosts.indexOf(',', i);
/* 130 */       if (j == -1) {
/* 131 */         if (hostlen != hostslen - i) return false; 
/* 132 */         return hosts.regionMatches(true, i, _host, 0, hostlen);
/*     */       } 
/* 134 */       if (hostlen == j - i && 
/* 135 */         hosts.regionMatches(true, i, _host, 0, hostlen)) return true;
/*     */       
/* 137 */       i = j + 1;
/*     */     } 
/* 139 */     return false;
/*     */   }
/*     */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\HostKey.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */