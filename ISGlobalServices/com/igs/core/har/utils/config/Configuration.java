/*     */ package com.igs.core.har.utils.config;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.math.BigInteger;
/*     */ import java.util.HashMap;
/*     */ 
/*     */ public class Configuration {
/*  11 */   private HashMap<String, ConfigurationItem> configurationItems = new HashMap<>();
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
/*     */   private String fileName;
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
/*     */   public static Configuration Load(String configFilename) throws IOException {
/*  45 */     Configuration config = null;
/*     */     
/*  47 */     File file = new File(configFilename);
/*  48 */     if (file.exists() && !file.isDirectory()) {
/*  49 */       config = new Configuration();
/*  50 */       config.SetFileName(configFilename);
/*     */       try {
/*  52 */         String currentLine = "";
/*  53 */         InputStream fis = new FileInputStream(file.getPath());
/*  54 */         InputStreamReader isr = new InputStreamReader(fis, Charset.forName("UTF-8"));
/*  55 */         BufferedReader br = new BufferedReader(isr);
/*     */         
/*  57 */         ConfigurationItem item = null;
/*  58 */         while ((currentLine = br.readLine()) != null) {
/*  59 */           if (currentLine.startsWith(";") || currentLine.trim().equalsIgnoreCase("")) {
/*     */             continue;
/*     */           }
/*     */           
/*  63 */           if (currentLine.startsWith("[")) {
/*  64 */             String section = currentLine.substring(1, currentLine.length() - 1).toUpperCase();
/*  65 */             item = new ConfigurationItem(section);
/*  66 */             config.GetConfigurationItems().put(section, item); continue;
/*  67 */           }  if (item != null) {
/*  68 */             String[] kv = currentLine.split(Pattern.quote("="));
/*  69 */             item.GetList().put(kv[0].trim(), kv[1].trim());
/*     */           } 
/*     */         } 
/*  72 */       } catch (IOException e) {
/*  73 */         e.printStackTrace();
/*     */       } 
/*     */     } 
/*  76 */     return config;
/*     */   }
/*     */   
/*     */   public static void PrintConfiguration(Configuration config) {
/*  80 */     String[] sections = new String[config.GetConfigurationItems().keySet().size()];
/*  81 */     config.GetConfigurationItems().keySet().toArray((Object[])sections);
/*  82 */     for (String section : sections) {
/*  83 */       ConfigurationItem item = config.GetConfigurationItems().get(section);
/*  84 */       System.out.println("[" + item.GetSection() + "]");
/*     */       
/*  86 */       String[] keys = new String[item.GetList().keySet().size()];
/*  87 */       item.GetList().keySet().toArray((Object[])keys);
/*  88 */       for (String key : keys) {
/*  89 */         System.out.println(key + "=" + item.GetList().get(key));
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public String GetFileName() {
/*  95 */     return this.fileName;
/*     */   }
/*     */   
/*     */   public HashMap<String, ConfigurationItem> GetConfigurationItems() {
/*  99 */     return this.configurationItems;
/*     */   }
/*     */   
/*     */   private void SetFileName(String fileName) {
/* 103 */     this.fileName = fileName;
/*     */   }
/*     */   
/*     */   public void Put(String section, String key, Object value) {
/* 107 */     ConfigurationItem item = GetConfigurationItems().get(section);
/* 108 */     if (item == null) {
/* 109 */       item = new ConfigurationItem(section);
/* 110 */       item.Put(key, value);
/*     */       
/* 112 */       GetConfigurationItems().put(section, item);
/*     */     } else {
/* 114 */       item.Put(key, value);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void Save() {
/* 119 */     File file = new File(GetFileName());
/*     */     try {
/* 121 */       OutputStream stream = new FileOutputStream(GetFileName());
/* 122 */       OutputStreamWriter writer = new OutputStreamWriter(stream);
/*     */       
/* 124 */       String[] ciKeys = new String[GetConfigurationItems().keySet().size()];
/* 125 */       GetConfigurationItems().keySet().toArray((Object[])ciKeys);
/* 126 */       for (String ciKey : ciKeys) {
/* 127 */         ConfigurationItem item = GetConfigurationItems().get(ciKey);
/*     */         
/* 129 */         writer.write("[" + ciKey + "]\r\n");
/*     */         
/* 131 */         String[] itemKeys = new String[item.GetList().keySet().size()];
/* 132 */         item.GetList().keySet().toArray((Object[])itemKeys);
/* 133 */         for (String iKey : itemKeys) {
/* 134 */           writer.write(iKey + "=" + item.GetList().get(iKey) + "\r\n");
/*     */         }
/*     */       } 
/* 137 */       writer.flush();
/* 138 */       writer.close();
/* 139 */     } catch (IOException e) {
/* 140 */       e.printStackTrace();
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean GetBoolean(String section, String key, boolean defvalue) {
/* 145 */     section = section.toUpperCase();
/* 146 */     key = key.toUpperCase();
/* 147 */     ConfigurationItem item = GetConfigurationItems().get(section);
/* 148 */     if (item != null) {
/* 149 */       Object value = item.GetList().get(key);
/* 150 */       if (value != null) {
/* 151 */         return Boolean.parseBoolean(value.toString());
/*     */       }
/* 153 */       return defvalue;
/*     */     } 
/* 155 */     return defvalue;
/*     */   }
/*     */   
/*     */   public int GetInteger(String section, String key, int defvalue) {
/* 159 */     section = section.toUpperCase();
/* 160 */     key = key.toUpperCase();
/* 161 */     ConfigurationItem item = this.configurationItems.get(section);
/* 162 */     if (item != null) {
/* 163 */       Object value = item.GetList().get(key);
/* 164 */       if (value != null) {
/* 165 */         return Integer.parseInt(value.toString());
/*     */       }
/* 167 */       return defvalue;
/*     */     } 
/* 169 */     return defvalue;
/*     */   }
/*     */   
/*     */   public float GetFloat(String section, String key, float defvalue) {
/* 173 */     section = section.toUpperCase();
/* 174 */     key = key.toUpperCase();
/* 175 */     ConfigurationItem item = this.configurationItems.get(section);
/* 176 */     if (item != null) {
/* 177 */       Object value = item.GetList().get(key);
/* 178 */       if (value != null) {
/* 179 */         return Float.parseFloat(value.toString());
/*     */       }
/* 181 */       return defvalue;
/*     */     } 
/* 183 */     return defvalue;
/*     */   }
/*     */   
/*     */   public double GetDouble(String section, String key, double defvalue) {
/* 187 */     section = section.toUpperCase();
/* 188 */     key = key.toUpperCase();
/* 189 */     ConfigurationItem item = this.configurationItems.get(section);
/* 190 */     if (item != null) {
/* 191 */       Object value = item.GetList().get(key);
/* 192 */       if (value != null) {
/* 193 */         return Double.parseDouble(value.toString());
/*     */       }
/* 195 */       return defvalue;
/*     */     } 
/* 197 */     return defvalue;
/*     */   }
/*     */   
/*     */   public String GetString(String section, String key, String defvalue) {
/* 201 */     section = section.toUpperCase();
/* 202 */     key = key.toUpperCase();
/* 203 */     ConfigurationItem item = this.configurationItems.get(section);
/* 204 */     if (item != null) {
/* 205 */       Object value = item.GetList().get(key);
/* 206 */       if (value != null) {
/* 207 */         return value.toString();
/*     */       }
/* 209 */       return defvalue;
/*     */     } 
/* 211 */     return defvalue;
/*     */   }
/*     */   
/*     */   public BigInteger GetBigInteger(String section, String key, BigInteger defvalue) {
/* 215 */     section = section.toUpperCase();
/* 216 */     key = key.toUpperCase();
/* 217 */     ConfigurationItem item = this.configurationItems.get(section);
/* 218 */     if (item != null) {
/* 219 */       Object value = item.GetList().get(key);
/* 220 */       if (value != null) {
/* 221 */         return new BigInteger(value.toString());
/*     */       }
/* 223 */       return defvalue;
/*     */     } 
/* 225 */     return defvalue;
/*     */   }
/*     */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\igs\core\ha\\utils\config\Configuration.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */