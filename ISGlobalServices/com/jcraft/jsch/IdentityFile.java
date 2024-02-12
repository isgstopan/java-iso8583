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
/*     */ class IdentityFile
/*     */   implements Identity
/*     */ {
/*     */   private JSch jsch;
/*     */   private KeyPair kpair;
/*     */   private String identity;
/*     */   
/*     */   static IdentityFile newInstance(String prvfile, String pubfile, JSch jsch) throws JSchException {
/*  40 */     KeyPair kpair = KeyPair.load(jsch, prvfile, pubfile);
/*  41 */     return new IdentityFile(jsch, prvfile, kpair);
/*     */   }
/*     */ 
/*     */   
/*     */   static IdentityFile newInstance(String name, byte[] prvkey, byte[] pubkey, JSch jsch) throws JSchException {
/*  46 */     KeyPair kpair = KeyPair.load(jsch, prvkey, pubkey);
/*  47 */     return new IdentityFile(jsch, name, kpair);
/*     */   }
/*     */   
/*     */   private IdentityFile(JSch jsch, String name, KeyPair kpair) throws JSchException {
/*  51 */     this.jsch = jsch;
/*  52 */     this.identity = name;
/*  53 */     this.kpair = kpair;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean setPassphrase(byte[] passphrase) throws JSchException {
/*  63 */     return this.kpair.decrypt(passphrase);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] getPublicKeyBlob() {
/*  71 */     return this.kpair.getPublicKeyBlob();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] getSignature(byte[] data) {
/*  80 */     return this.kpair.getSignature(data);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean decrypt() {
/*  88 */     throw new RuntimeException("not implemented");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getAlgName() {
/*  96 */     return new String(this.kpair.getKeyTypeName());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getName() {
/* 104 */     return this.identity;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEncrypted() {
/* 112 */     return this.kpair.isEncrypted();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clear() {
/* 119 */     this.kpair.dispose();
/* 120 */     this.kpair = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public KeyPair getKeyPair() {
/* 128 */     return this.kpair;
/*     */   }
/*     */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\IdentityFile.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */