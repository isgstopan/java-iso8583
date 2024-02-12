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
/*     */ 
/*     */ class LocalIdentityRepository
/*     */   implements IdentityRepository
/*     */ {
/*     */   private static final String name = "Local Identity Repository";
/*  37 */   private Vector identities = new Vector();
/*     */   private JSch jsch;
/*     */   
/*     */   LocalIdentityRepository(JSch jsch) {
/*  41 */     this.jsch = jsch;
/*     */   }
/*     */   
/*     */   public String getName() {
/*  45 */     return "Local Identity Repository";
/*     */   }
/*     */   
/*     */   public int getStatus() {
/*  49 */     return 2;
/*     */   }
/*     */   
/*     */   public synchronized Vector getIdentities() {
/*  53 */     removeDupulicates();
/*  54 */     Vector v = new Vector();
/*  55 */     for (int i = 0; i < this.identities.size(); i++) {
/*  56 */       v.addElement(this.identities.elementAt(i));
/*     */     }
/*  58 */     return v;
/*     */   }
/*     */   
/*     */   public synchronized void add(Identity identity) {
/*  62 */     if (!this.identities.contains(identity)) {
/*  63 */       byte[] blob1 = identity.getPublicKeyBlob();
/*  64 */       if (blob1 == null) {
/*  65 */         this.identities.addElement(identity);
/*     */         return;
/*     */       } 
/*  68 */       for (int i = 0; i < this.identities.size(); i++) {
/*  69 */         byte[] blob2 = ((Identity)this.identities.elementAt(i)).getPublicKeyBlob();
/*  70 */         if (blob2 != null && Util.array_equals(blob1, blob2)) {
/*  71 */           if (!identity.isEncrypted() && ((Identity)this.identities.elementAt(i)).isEncrypted()) {
/*     */             
/*  73 */             remove(blob2);
/*     */           } else {
/*     */             return;
/*     */           } 
/*     */         }
/*     */       } 
/*     */       
/*  80 */       this.identities.addElement(identity);
/*     */     } 
/*     */   }
/*     */   
/*     */   public synchronized boolean add(byte[] identity) {
/*     */     try {
/*  86 */       Identity _identity = IdentityFile.newInstance("from remote:", identity, null, this.jsch);
/*     */       
/*  88 */       add(_identity);
/*  89 */       return true;
/*     */     }
/*  91 */     catch (JSchException e) {
/*  92 */       return false;
/*     */     } 
/*     */   }
/*     */   
/*     */   synchronized void remove(Identity identity) {
/*  97 */     if (this.identities.contains(identity)) {
/*  98 */       this.identities.removeElement(identity);
/*  99 */       identity.clear();
/*     */     } else {
/*     */       
/* 102 */       remove(identity.getPublicKeyBlob());
/*     */     } 
/*     */   }
/*     */   
/*     */   public synchronized boolean remove(byte[] blob) {
/* 107 */     if (blob == null) return false; 
/* 108 */     for (int i = 0; i < this.identities.size(); ) {
/* 109 */       Identity _identity = this.identities.elementAt(i);
/* 110 */       byte[] _blob = _identity.getPublicKeyBlob();
/* 111 */       if (_blob == null || !Util.array_equals(blob, _blob)) {
/*     */         i++; continue;
/* 113 */       }  this.identities.removeElement(_identity);
/* 114 */       _identity.clear();
/* 115 */       return true;
/*     */     } 
/* 117 */     return false;
/*     */   }
/*     */   
/*     */   public synchronized void removeAll() {
/* 121 */     for (int i = 0; i < this.identities.size(); i++) {
/* 122 */       Identity identity = this.identities.elementAt(i);
/* 123 */       identity.clear();
/*     */     } 
/* 125 */     this.identities.removeAllElements();
/*     */   }
/*     */   
/*     */   private void removeDupulicates() {
/* 129 */     Vector v = new Vector();
/* 130 */     int len = this.identities.size();
/* 131 */     if (len == 0)
/* 132 */       return;  int i; for (i = 0; i < len; i++) {
/* 133 */       Identity foo = this.identities.elementAt(i);
/* 134 */       byte[] foo_blob = foo.getPublicKeyBlob();
/* 135 */       if (foo_blob != null)
/* 136 */         for (int j = i + 1; j < len; j++) {
/* 137 */           Identity bar = this.identities.elementAt(j);
/* 138 */           byte[] bar_blob = bar.getPublicKeyBlob();
/* 139 */           if (bar_blob != null && 
/* 140 */             Util.array_equals(foo_blob, bar_blob) && foo.isEncrypted() == bar.isEncrypted()) {
/*     */             
/* 142 */             v.addElement(foo_blob);
/*     */             break;
/*     */           } 
/*     */         }  
/*     */     } 
/* 147 */     for (i = 0; i < v.size(); i++)
/* 148 */       remove(v.elementAt(i)); 
/*     */   }
/*     */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\LocalIdentityRepository.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */