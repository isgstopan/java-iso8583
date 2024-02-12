/*     */ package com.jcraft.jsch.jgss;
/*     */ 
/*     */ import com.jcraft.jsch.GSSContext;
/*     */ import com.jcraft.jsch.JSchException;
/*     */ import java.net.InetAddress;
/*     */ import java.net.UnknownHostException;
/*     */ import org.ietf.jgss.GSSContext;
/*     */ import org.ietf.jgss.GSSCredential;
/*     */ import org.ietf.jgss.GSSException;
/*     */ import org.ietf.jgss.GSSManager;
/*     */ import org.ietf.jgss.GSSName;
/*     */ import org.ietf.jgss.MessageProp;
/*     */ import org.ietf.jgss.Oid;
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
/*     */ public class GSSContextKrb5
/*     */   implements GSSContext
/*     */ {
/*     */   private static final String pUseSubjectCredsOnly = "javax.security.auth.useSubjectCredsOnly";
/*  48 */   private static String useSubjectCredsOnly = getSystemProperty("javax.security.auth.useSubjectCredsOnly");
/*     */ 
/*     */   
/*  51 */   private GSSContext context = null;
/*     */   
/*     */   public void create(String user, String host) throws JSchException {
/*     */     try {
/*  55 */       Oid krb5 = new Oid("1.2.840.113554.1.2.2");
/*     */       
/*  57 */       Oid principalName = new Oid("1.2.840.113554.1.2.2.1");
/*     */       
/*  59 */       GSSManager mgr = GSSManager.getInstance();
/*     */       
/*  61 */       GSSCredential crd = null;
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
/*  74 */       String cname = host;
/*     */       try {
/*  76 */         cname = InetAddress.getByName(cname).getCanonicalHostName();
/*     */       }
/*  78 */       catch (UnknownHostException e) {}
/*     */       
/*  80 */       GSSName _host = mgr.createName("host/" + cname, principalName);
/*     */       
/*  82 */       this.context = mgr.createContext(_host, krb5, crd, 0);
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
/* 101 */       this.context.requestMutualAuth(true);
/* 102 */       this.context.requestConf(true);
/* 103 */       this.context.requestInteg(true);
/* 104 */       this.context.requestCredDeleg(true);
/* 105 */       this.context.requestAnonymity(false);
/*     */ 
/*     */       
/*     */       return;
/* 109 */     } catch (GSSException ex) {
/* 110 */       throw new JSchException(ex.toString());
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean isEstablished() {
/* 115 */     return this.context.isEstablished();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] init(byte[] token, int s, int l) throws JSchException {
/*     */     try {
/* 126 */       if (useSubjectCredsOnly == null) {
/* 127 */         setSystemProperty("javax.security.auth.useSubjectCredsOnly", "false");
/*     */       }
/* 129 */       return this.context.initSecContext(token, 0, l);
/*     */     }
/* 131 */     catch (GSSException ex) {
/* 132 */       throw new JSchException(ex.toString());
/*     */     }
/* 134 */     catch (SecurityException ex) {
/* 135 */       throw new JSchException(ex.toString());
/*     */     } finally {
/*     */       
/* 138 */       if (useSubjectCredsOnly == null)
/*     */       {
/* 140 */         setSystemProperty("javax.security.auth.useSubjectCredsOnly", "true");
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public byte[] getMIC(byte[] message, int s, int l) {
/*     */     try {
/* 147 */       MessageProp prop = new MessageProp(0, true);
/* 148 */       return this.context.getMIC(message, s, l, prop);
/*     */     }
/* 150 */     catch (GSSException ex) {
/* 151 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   public void dispose() {
/*     */     try {
/* 157 */       this.context.dispose();
/*     */     }
/* 159 */     catch (GSSException ex) {}
/*     */   }
/*     */   
/*     */   private static String getSystemProperty(String key) {
/*     */     try {
/* 164 */       return System.getProperty(key);
/* 165 */     } catch (Exception e) {
/*     */       
/* 167 */       return null;
/*     */     } 
/*     */   }
/*     */   private static void setSystemProperty(String key, String value) {
/*     */     try {
/* 172 */       System.setProperty(key, value);
/* 173 */     } catch (Exception e) {}
/*     */   }
/*     */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\jgss\GSSContextKrb5.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */