/*    */ package com.jcraft.jsch.jce;
/*    */ 
/*    */ import com.jcraft.jsch.JSchException;
/*    */ import com.jcraft.jsch.KeyPairGenECDSA;
/*    */ import java.security.KeyPair;
/*    */ import java.security.KeyPairGenerator;
/*    */ import java.security.interfaces.ECPrivateKey;
/*    */ import java.security.interfaces.ECPublicKey;
/*    */ import java.security.spec.ECGenParameterSpec;
/*    */ import java.security.spec.ECParameterSpec;
/*    */ import java.security.spec.ECPoint;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class KeyPairGenECDSA
/*    */   implements KeyPairGenECDSA
/*    */ {
/*    */   byte[] d;
/*    */   byte[] r;
/*    */   byte[] s;
/*    */   ECPublicKey pubKey;
/*    */   ECPrivateKey prvKey;
/*    */   ECParameterSpec params;
/*    */   
/*    */   public void init(int key_size) throws Exception {
/* 45 */     String name = null;
/* 46 */     if (key_size == 256) { name = "secp256r1"; }
/* 47 */     else if (key_size == 384) { name = "secp384r1"; }
/* 48 */     else if (key_size == 521) { name = "secp521r1"; }
/* 49 */     else { throw new JSchException("unsupported key size: " + key_size); }
/*    */     
/* 51 */     for (int i = 0; i < 1000; i++) {
/* 52 */       KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC");
/* 53 */       ECGenParameterSpec ecsp = new ECGenParameterSpec(name);
/* 54 */       kpg.initialize(ecsp);
/* 55 */       KeyPair kp = kpg.genKeyPair();
/* 56 */       this.prvKey = (ECPrivateKey)kp.getPrivate();
/* 57 */       this.pubKey = (ECPublicKey)kp.getPublic();
/* 58 */       this.params = this.pubKey.getParams();
/* 59 */       this.d = this.prvKey.getS().toByteArray();
/* 60 */       ECPoint w = this.pubKey.getW();
/* 61 */       this.r = w.getAffineX().toByteArray();
/* 62 */       this.s = w.getAffineY().toByteArray();
/*    */       
/* 64 */       if (this.r.length == this.s.length && ((
/* 65 */         key_size == 256 && this.r.length == 32) || (
/* 66 */         key_size == 384 && this.r.length == 48) || (
/* 67 */         key_size == 521 && this.r.length == 66)))
/*    */         break; 
/* 69 */     }  if (this.d.length < this.r.length)
/* 70 */       this.d = insert0(this.d); 
/*    */   }
/*    */   
/* 73 */   public byte[] getD() { return this.d; }
/* 74 */   public byte[] getR() { return this.r; }
/* 75 */   public byte[] getS() { return this.s; }
/* 76 */   ECPublicKey getPublicKey() { return this.pubKey; } ECPrivateKey getPrivateKey() {
/* 77 */     return this.prvKey;
/*    */   }
/*    */   
/*    */   private byte[] insert0(byte[] buf) {
/* 81 */     byte[] tmp = new byte[buf.length + 1];
/* 82 */     System.arraycopy(buf, 0, tmp, 1, buf.length);
/* 83 */     bzero(buf);
/* 84 */     return tmp;
/*    */   }
/*    */   private byte[] chop0(byte[] buf) {
/* 87 */     if (buf[0] != 0 || (buf[1] & 0x80) == 0) return buf; 
/* 88 */     byte[] tmp = new byte[buf.length - 1];
/* 89 */     System.arraycopy(buf, 1, tmp, 0, tmp.length);
/* 90 */     bzero(buf);
/* 91 */     return tmp;
/*    */   }
/*    */   private void bzero(byte[] buf) {
/* 94 */     for (int i = 0; i < buf.length; ) { buf[i] = 0; i++; }
/*    */   
/*    */   }
/*    */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\jce\KeyPairGenECDSA.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */