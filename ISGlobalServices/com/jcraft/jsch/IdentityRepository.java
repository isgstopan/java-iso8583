/*     */ package com.jcraft.jsch;
/*     */ 
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
/*     */ public interface IdentityRepository
/*     */ {
/*     */   public static final int UNAVAILABLE = 0;
/*     */   public static final int NOTRUNNING = 1;
/*     */   public static final int RUNNING = 2;
/*     */   
/*     */   String getName();
/*     */   
/*     */   int getStatus();
/*     */   
/*     */   Vector getIdentities();
/*     */   
/*     */   boolean add(byte[] paramArrayOfbyte);
/*     */   
/*     */   boolean remove(byte[] paramArrayOfbyte);
/*     */   
/*     */   void removeAll();
/*     */   
/*     */   public static class Wrapper
/*     */     implements IdentityRepository
/*     */   {
/*     */     private IdentityRepository ir;
/*  54 */     private Vector cache = new Vector(); private boolean keep_in_cache = false;
/*     */     
/*     */     Wrapper(IdentityRepository ir) {
/*  57 */       this(ir, false);
/*     */     }
/*     */     Wrapper(IdentityRepository ir, boolean keep_in_cache) {
/*  60 */       this.ir = ir;
/*  61 */       this.keep_in_cache = keep_in_cache;
/*     */     }
/*     */     public String getName() {
/*  64 */       return this.ir.getName();
/*     */     }
/*     */     public int getStatus() {
/*  67 */       return this.ir.getStatus();
/*     */     }
/*     */     public boolean add(byte[] identity) {
/*  70 */       return this.ir.add(identity);
/*     */     }
/*     */     public boolean remove(byte[] blob) {
/*  73 */       return this.ir.remove(blob);
/*     */     }
/*     */     public void removeAll() {
/*  76 */       this.cache.removeAllElements();
/*  77 */       this.ir.removeAll();
/*     */     }
/*     */     public Vector getIdentities() {
/*  80 */       Vector result = new Vector();
/*  81 */       for (int i = 0; i < this.cache.size(); i++) {
/*  82 */         Identity identity = this.cache.elementAt(i);
/*  83 */         result.add(identity);
/*     */       } 
/*  85 */       Vector tmp = this.ir.getIdentities();
/*  86 */       for (int j = 0; j < tmp.size(); j++) {
/*  87 */         result.add(tmp.elementAt(j));
/*     */       }
/*  89 */       return result;
/*     */     }
/*     */     void add(Identity identity) {
/*  92 */       if (!this.keep_in_cache && !identity.isEncrypted() && identity instanceof IdentityFile) {
/*     */         
/*     */         try {
/*  95 */           this.ir.add(((IdentityFile)identity).getKeyPair().forSSHAgent());
/*     */         }
/*  97 */         catch (JSchException e) {}
/*     */       
/*     */       }
/*     */       else {
/*     */         
/* 102 */         this.cache.addElement(identity);
/*     */       } 
/*     */     } void check() {
/* 105 */       if (this.cache.size() > 0) {
/* 106 */         Object[] identities = this.cache.toArray();
/* 107 */         for (int i = 0; i < identities.length; i++) {
/* 108 */           Identity identity = (Identity)identities[i];
/* 109 */           this.cache.removeElement(identity);
/* 110 */           add(identity);
/*     */         } 
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\IdentityRepository.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */