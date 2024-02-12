/*     */ package com.jcraft.jsch;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.FileReader;
/*     */ import java.io.IOException;
/*     */ import java.io.Reader;
/*     */ import java.io.StringReader;
/*     */ import java.util.Hashtable;
/*     */ import java.util.Vector;
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
/*     */ public class OpenSSHConfig
/*     */   implements ConfigRepository
/*     */ {
/*     */   private final Hashtable config;
/*     */   private final Vector hosts;
/*     */   
/*     */   public static OpenSSHConfig parse(String conf) throws IOException {
/*  80 */     Reader r = new StringReader(conf);
/*     */     try {
/*  82 */       return new OpenSSHConfig(r);
/*     */     } finally {
/*     */       
/*  85 */       r.close();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static OpenSSHConfig parseFile(String file) throws IOException {
/*  96 */     Reader r = new FileReader(Util.checkTilde(file));
/*     */     try {
/*  98 */       return new OpenSSHConfig(r);
/*     */     } finally {
/*     */       
/* 101 */       r.close();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   OpenSSHConfig(Reader r) throws IOException {
/* 109 */     this.config = new Hashtable();
/* 110 */     this.hosts = new Vector();
/*     */     _parse(r);
/*     */   } private void _parse(Reader r) throws IOException {
/* 113 */     BufferedReader br = new BufferedReader(r);
/*     */     
/* 115 */     String host = "";
/* 116 */     Vector kv = new Vector();
/* 117 */     String l = null;
/*     */     
/* 119 */     while ((l = br.readLine()) != null) {
/* 120 */       l = l.trim();
/* 121 */       if (l.length() == 0 || l.startsWith("#")) {
/*     */         continue;
/*     */       }
/* 124 */       String[] key_value = l.split("[= \t]", 2);
/* 125 */       for (int i = 0; i < key_value.length; i++) {
/* 126 */         key_value[i] = key_value[i].trim();
/*     */       }
/* 128 */       if (key_value.length <= 1) {
/*     */         continue;
/*     */       }
/* 131 */       if (key_value[0].equals("Host")) {
/* 132 */         this.config.put(host, kv);
/* 133 */         this.hosts.addElement(host);
/* 134 */         host = key_value[1];
/* 135 */         kv = new Vector();
/*     */         continue;
/*     */       } 
/* 138 */       kv.addElement(key_value);
/*     */     } 
/*     */     
/* 141 */     this.config.put(host, kv);
/* 142 */     this.hosts.addElement(host);
/*     */   }
/*     */   
/*     */   public ConfigRepository.Config getConfig(String host) {
/* 146 */     return new MyConfig(host);
/*     */   }
/*     */   
/* 149 */   private static final Hashtable keymap = new Hashtable();
/*     */   static {
/* 151 */     keymap.put("kex", "KexAlgorithms");
/* 152 */     keymap.put("server_host_key", "HostKeyAlgorithms");
/* 153 */     keymap.put("cipher.c2s", "Ciphers");
/* 154 */     keymap.put("cipher.s2c", "Ciphers");
/* 155 */     keymap.put("mac.c2s", "Macs");
/* 156 */     keymap.put("mac.s2c", "Macs");
/* 157 */     keymap.put("compression.s2c", "Compression");
/* 158 */     keymap.put("compression.c2s", "Compression");
/* 159 */     keymap.put("compression_level", "CompressionLevel");
/* 160 */     keymap.put("MaxAuthTries", "NumberOfPasswordPrompts");
/*     */   }
/*     */   
/*     */   class MyConfig
/*     */     implements ConfigRepository.Config {
/*     */     private String host;
/* 166 */     private Vector _configs = new Vector(); private final OpenSSHConfig this$0;
/*     */     
/*     */     MyConfig(String host) {
/* 169 */       this.host = host;
/*     */       
/* 171 */       this._configs.addElement(OpenSSHConfig.this.config.get(""));
/*     */       
/* 173 */       byte[] _host = Util.str2byte(host);
/* 174 */       if (OpenSSHConfig.this.hosts.size() > 1) {
/* 175 */         for (int i = 1; i < OpenSSHConfig.this.hosts.size(); i++) {
/* 176 */           String[] patterns = ((String)OpenSSHConfig.this.hosts.elementAt(i)).split("[ \t]");
/* 177 */           for (int j = 0; j < patterns.length; j++) {
/* 178 */             boolean negate = false;
/* 179 */             String foo = patterns[j].trim();
/* 180 */             if (foo.startsWith("!")) {
/* 181 */               negate = true;
/* 182 */               foo = foo.substring(1).trim();
/*     */             } 
/* 184 */             if (Util.glob(Util.str2byte(foo), _host)) {
/* 185 */               if (!negate) {
/* 186 */                 this._configs.addElement(OpenSSHConfig.this.config.get(OpenSSHConfig.this.hosts.elementAt(i)));
/*     */               }
/*     */             }
/* 189 */             else if (negate) {
/* 190 */               this._configs.addElement(OpenSSHConfig.this.config.get(OpenSSHConfig.this.hosts.elementAt(i)));
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       }
/*     */     }
/*     */     
/*     */     private String find(String key) {
/* 198 */       if (OpenSSHConfig.keymap.get(key) != null) {
/* 199 */         key = (String)OpenSSHConfig.keymap.get(key);
/*     */       }
/* 201 */       key = key.toUpperCase();
/* 202 */       String value = null;
/* 203 */       for (int i = 0; i < this._configs.size(); i++) {
/* 204 */         Vector v = this._configs.elementAt(i);
/* 205 */         for (int j = 0; j < v.size(); j++) {
/* 206 */           String[] kv = v.elementAt(j);
/* 207 */           if (kv[0].toUpperCase().equals(key)) {
/* 208 */             value = kv[1];
/*     */             break;
/*     */           } 
/*     */         } 
/* 212 */         if (value != null)
/*     */           break; 
/*     */       } 
/* 215 */       return value;
/*     */     }
/*     */     
/*     */     private String[] multiFind(String key) {
/* 219 */       key = key.toUpperCase();
/* 220 */       Vector value = new Vector();
/* 221 */       for (int i = 0; i < this._configs.size(); i++) {
/* 222 */         Vector v = this._configs.elementAt(i);
/* 223 */         for (int j = 0; j < v.size(); j++) {
/* 224 */           String[] kv = v.elementAt(j);
/* 225 */           if (kv[0].toUpperCase().equals(key)) {
/* 226 */             String foo = kv[1];
/* 227 */             if (foo != null) {
/* 228 */               value.remove(foo);
/* 229 */               value.addElement(foo);
/*     */             } 
/*     */           } 
/*     */         } 
/*     */       } 
/* 234 */       String[] result = new String[value.size()];
/* 235 */       value.toArray(result);
/* 236 */       return result;
/*     */     }
/*     */     
/* 239 */     public String getHostname() { return find("Hostname"); } public String getUser() {
/* 240 */       return find("User");
/*     */     } public int getPort() {
/* 242 */       String foo = find("Port");
/* 243 */       int port = -1;
/*     */       try {
/* 245 */         port = Integer.parseInt(foo);
/*     */       }
/* 247 */       catch (NumberFormatException e) {}
/*     */ 
/*     */       
/* 250 */       return port;
/*     */     }
/*     */     public String getValue(String key) {
/* 253 */       if (key.equals("compression.s2c") || key.equals("compression.c2s")) {
/*     */         
/* 255 */         String foo = find(key);
/* 256 */         if (foo == null || foo.equals("no"))
/* 257 */           return "none,zlib@openssh.com,zlib"; 
/* 258 */         return "zlib@openssh.com,zlib,none";
/*     */       } 
/* 260 */       return find(key);
/*     */     } public String[] getValues(String key) {
/* 262 */       return multiFind(key);
/*     */     }
/*     */   }
/*     */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\OpenSSHConfig.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */