/*     */ package com.igs.core.har.ssh;
/*     */ import com.jcraft.jsch.Channel;
/*     */ import com.jcraft.jsch.ChannelExec;
/*     */ import com.jcraft.jsch.JSchException;
/*     */ import com.jcraft.jsch.Session;
/*     */ import java.io.InputStream;
/*     */ import java.math.BigInteger;
/*     */ import java.util.List;
/*     */ import java.util.Properties;
/*     */ 
/*     */ public class JavaSsh {
/*  12 */   private static JavaSsh ssh = new JavaSsh();
/*  13 */   private JSch internalJsch = new JSch();
/*     */   
/*     */   private static boolean isNumber(String str) {
/*     */     try {
/*  17 */       long l = Long.parseLong(str);
/*  18 */       return true;
/*  19 */     } catch (Exception exception) {
/*     */       
/*  21 */       return false;
/*     */     } 
/*     */   }
/*     */   public static void main(String[] args) throws JSchException, IOException {
/*  25 */     Session session = ssh.CreateSession("10.250.192.171", 22, "root", "Admincod3");
/*     */     
/*  27 */     if (session != null) {
/*     */       
/*  29 */       session.connect();
/*  30 */       if (session.isConnected()) {
/*  31 */         List<String> outs = new ArrayList<>();
/*  32 */         String returnValue = ssh.GetChannelResponse(session, "ls -l /opt/CA/APM/tim/logs | grep 'stdoutlog'");
/*  33 */         String logSize = "";
/*  34 */         if (!returnValue.equalsIgnoreCase("")) {
/*  35 */           String[] values = returnValue.split(Pattern.quote(" "));
/*  36 */           for (String value : values) {
/*  37 */             if (!value.equalsIgnoreCase("")) {
/*  38 */               outs.add(value);
/*     */             }
/*     */           } 
/*  41 */           outs.toArray(values);
/*  42 */           logSize = values[4];
/*     */         } 
/*  44 */         BigInteger sizeLimit = new BigInteger("21474836480");
/*  45 */         BigInteger currentSize = new BigInteger(logSize);
/*     */         
/*  47 */         System.out.println("Current Size: " + currentSize);
/*     */       } 
/*     */ 
/*     */       
/*  51 */       session.disconnect();
/*     */       
/*  53 */       System.out.println("DONE");
/*     */     } 
/*     */   }
/*     */   
/*     */   public Session CreateSession(String host, int port, String username, String password) {
/*     */     try {
/*  59 */       Properties config = new Properties();
/*  60 */       config.put("StrictHostKeyChecking", "no");
/*     */       
/*  62 */       Session session = this.internalJsch.getSession(username, host, port);
/*  63 */       session.setPassword(password);
/*  64 */       session.setConfig(config);
/*     */       
/*  66 */       return session;
/*  67 */     } catch (Exception exception) {
/*     */       
/*  69 */       return null;
/*     */     } 
/*     */   }
/*     */   public String GetChannelResponse(Session session, String command) throws JSchException, IOException {
/*  73 */     Channel channel = session.openChannel("exec");
/*  74 */     channel.setInputStream(null);
/*     */     
/*  76 */     ((ChannelExec)channel).setCommand(command);
/*  77 */     ((ChannelExec)channel).setErrStream(System.err);
/*     */     
/*  79 */     InputStream in = channel.getInputStream();
/*     */     
/*  81 */     channel.connect();
/*  82 */     String returnValue = "";
/*  83 */     if (channel.isConnected()) {
/*  84 */       byte[] tmp = new byte[1024];
/*     */       while (true) {
/*  86 */         if (in.available() > 0) {
/*  87 */           int i = in.read(tmp, 0, 1024);
/*  88 */           if (i >= 0) {
/*  89 */             returnValue = new String(tmp, 0, i);
/*     */             continue;
/*     */           } 
/*     */         } 
/*  93 */         if (channel.isClosed())
/*     */           break; 
/*     */         try {
/*  96 */           Thread.sleep(1000L);
/*  97 */         } catch (Exception exception) {}
/*     */       } 
/*     */     } 
/*     */     
/* 101 */     channel.disconnect();
/*     */     
/* 103 */     return returnValue;
/*     */   }
/*     */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\igs\core\har\ssh\JavaSsh.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */