/*      */ package com.jcraft.jsch;
/*      */ 
/*      */ import java.io.File;
/*      */ import java.io.FileNotFoundException;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.IOException;
/*      */ import java.io.OutputStream;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Vector;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public abstract class KeyPair
/*      */ {
/*      */   public static final int ERROR = 0;
/*      */   public static final int DSA = 1;
/*      */   public static final int RSA = 2;
/*      */   public static final int ECDSA = 3;
/*      */   public static final int UNKNOWN = 4;
/*      */   static final int VENDOR_OPENSSH = 0;
/*      */   static final int VENDOR_FSECURE = 1;
/*      */   static final int VENDOR_PUTTY = 2;
/*      */   static final int VENDOR_PKCS8 = 3;
/*   49 */   int vendor = 0;
/*      */   
/*   51 */   private static final byte[] cr = Util.str2byte("\n");
/*      */   
/*      */   public static KeyPair genKeyPair(JSch jsch, int type) throws JSchException {
/*   54 */     return genKeyPair(jsch, type, 1024);
/*      */   }
/*      */   public static KeyPair genKeyPair(JSch jsch, int type, int key_size) throws JSchException {
/*   57 */     KeyPair kpair = null;
/*   58 */     if (type == 1) { kpair = new KeyPairDSA(jsch); }
/*   59 */     else if (type == 2) { kpair = new KeyPairRSA(jsch); }
/*   60 */     else if (type == 3) { kpair = new KeyPairECDSA(jsch); }
/*   61 */      if (kpair != null) {
/*   62 */       kpair.generate(key_size);
/*      */     }
/*   64 */     return kpair;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getPublicKeyComment() {
/*   79 */     return this.publicKeyComment;
/*      */   }
/*      */   
/*      */   public void setPublicKeyComment(String publicKeyComment) {
/*   83 */     this.publicKeyComment = publicKeyComment;
/*      */   }
/*      */   
/*   86 */   protected String publicKeyComment = "no comment";
/*      */   
/*   88 */   JSch jsch = null;
/*      */ 
/*      */   
/*      */   private Cipher cipher;
/*      */   
/*      */   private HASH hash;
/*      */   
/*      */   private Random random;
/*      */   
/*      */   private byte[] passphrase;
/*      */   
/*   99 */   static byte[][] header = new byte[][] { Util.str2byte("Proc-Type: 4,ENCRYPTED"), Util.str2byte("DEK-Info: DES-EDE3-CBC,") };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writePrivateKey(OutputStream out) {
/*  110 */     writePrivateKey(out, (byte[])null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writePrivateKey(OutputStream out, byte[] passphrase) {
/*  119 */     if (passphrase == null) {
/*  120 */       passphrase = this.passphrase;
/*      */     }
/*  122 */     byte[] plain = getPrivateKey();
/*  123 */     byte[][] _iv = new byte[1][];
/*  124 */     byte[] encoded = encrypt(plain, _iv, passphrase);
/*  125 */     if (encoded != plain)
/*  126 */       Util.bzero(plain); 
/*  127 */     byte[] iv = _iv[0];
/*  128 */     byte[] prv = Util.toBase64(encoded, 0, encoded.length);
/*      */     
/*      */     try {
/*  131 */       out.write(getBegin()); out.write(cr);
/*  132 */       if (passphrase != null) {
/*  133 */         out.write(header[0]); out.write(cr);
/*  134 */         out.write(header[1]);
/*  135 */         for (int j = 0; j < iv.length; j++) {
/*  136 */           out.write(b2a((byte)(iv[j] >>> 4 & 0xF)));
/*  137 */           out.write(b2a((byte)(iv[j] & 0xF)));
/*      */         } 
/*  139 */         out.write(cr);
/*  140 */         out.write(cr);
/*      */       } 
/*  142 */       int i = 0;
/*  143 */       while (i < prv.length) {
/*  144 */         if (i + 64 < prv.length) {
/*  145 */           out.write(prv, i, 64);
/*  146 */           out.write(cr);
/*  147 */           i += 64;
/*      */           continue;
/*      */         } 
/*  150 */         out.write(prv, i, prv.length - i);
/*  151 */         out.write(cr);
/*      */       } 
/*      */       
/*  154 */       out.write(getEnd()); out.write(cr);
/*      */     
/*      */     }
/*  157 */     catch (Exception e) {}
/*      */   }
/*      */ 
/*      */   
/*  161 */   private static byte[] space = Util.str2byte(" ");
/*      */ 
/*      */   
/*      */   protected boolean encrypted;
/*      */   
/*      */   protected byte[] data;
/*      */   
/*      */   private byte[] iv;
/*      */   
/*      */   private byte[] publickeyblob;
/*      */ 
/*      */   
/*      */   public byte[] getPublicKeyBlob() {
/*  174 */     return this.publickeyblob;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writePublicKey(OutputStream out, String comment) {
/*  183 */     byte[] pubblob = getPublicKeyBlob();
/*  184 */     byte[] pub = Util.toBase64(pubblob, 0, pubblob.length);
/*      */     try {
/*  186 */       out.write(getKeyTypeName()); out.write(space);
/*  187 */       out.write(pub, 0, pub.length); out.write(space);
/*  188 */       out.write(Util.str2byte(comment));
/*  189 */       out.write(cr);
/*      */     }
/*  191 */     catch (Exception e) {}
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writePublicKey(String name, String comment) throws FileNotFoundException, IOException {
/*  202 */     FileOutputStream fos = new FileOutputStream(name);
/*  203 */     writePublicKey(fos, comment);
/*  204 */     fos.close();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeSECSHPublicKey(OutputStream out, String comment) {
/*  214 */     byte[] pubblob = getPublicKeyBlob();
/*  215 */     byte[] pub = Util.toBase64(pubblob, 0, pubblob.length);
/*      */     try {
/*  217 */       out.write(Util.str2byte("---- BEGIN SSH2 PUBLIC KEY ----")); out.write(cr);
/*  218 */       out.write(Util.str2byte("Comment: \"" + comment + "\"")); out.write(cr);
/*  219 */       int index = 0;
/*  220 */       while (index < pub.length) {
/*  221 */         int len = 70;
/*  222 */         if (pub.length - index < len) len = pub.length - index; 
/*  223 */         out.write(pub, index, len); out.write(cr);
/*  224 */         index += len;
/*      */       } 
/*  226 */       out.write(Util.str2byte("---- END SSH2 PUBLIC KEY ----")); out.write(cr);
/*      */     }
/*  228 */     catch (Exception e) {}
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writeSECSHPublicKey(String name, String comment) throws FileNotFoundException, IOException {
/*  240 */     FileOutputStream fos = new FileOutputStream(name);
/*  241 */     writeSECSHPublicKey(fos, comment);
/*  242 */     fos.close();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writePrivateKey(String name) throws FileNotFoundException, IOException {
/*  251 */     writePrivateKey(name, (byte[])null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void writePrivateKey(String name, byte[] passphrase) throws FileNotFoundException, IOException {
/*  261 */     FileOutputStream fos = new FileOutputStream(name);
/*  262 */     writePrivateKey(fos, passphrase);
/*  263 */     fos.close();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public String getFingerPrint() {
/*  271 */     if (this.hash == null) this.hash = genHash(); 
/*  272 */     byte[] kblob = getPublicKeyBlob();
/*  273 */     if (kblob == null) return null; 
/*  274 */     return Util.getFingerPrint(this.hash, kblob);
/*      */   }
/*      */   
/*      */   private byte[] encrypt(byte[] plain, byte[][] _iv, byte[] passphrase) {
/*  278 */     if (passphrase == null) return plain;
/*      */     
/*  280 */     if (this.cipher == null) this.cipher = genCipher(); 
/*  281 */     byte[] iv = _iv[0] = new byte[this.cipher.getIVSize()];
/*      */     
/*  283 */     if (this.random == null) this.random = genRandom(); 
/*  284 */     this.random.fill(iv, 0, iv.length);
/*      */     
/*  286 */     byte[] key = genKey(passphrase, iv);
/*  287 */     byte[] encoded = plain;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  292 */     int bsize = this.cipher.getIVSize();
/*  293 */     byte[] foo = new byte[(encoded.length / bsize + 1) * bsize];
/*  294 */     System.arraycopy(encoded, 0, foo, 0, encoded.length);
/*  295 */     int padding = bsize - encoded.length % bsize;
/*  296 */     for (int i = foo.length - 1; foo.length - padding <= i; i--) {
/*  297 */       foo[i] = (byte)padding;
/*      */     }
/*  299 */     encoded = foo;
/*      */ 
/*      */     
/*      */     try {
/*  303 */       this.cipher.init(0, key, iv);
/*  304 */       this.cipher.update(encoded, 0, encoded.length, encoded, 0);
/*      */     }
/*  306 */     catch (Exception e) {}
/*      */ 
/*      */     
/*  309 */     Util.bzero(key);
/*  310 */     return encoded;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private byte[] decrypt(byte[] data, byte[] passphrase, byte[] iv) {
/*      */     try {
/*  318 */       byte[] key = genKey(passphrase, iv);
/*  319 */       this.cipher.init(1, key, iv);
/*  320 */       Util.bzero(key);
/*  321 */       byte[] plain = new byte[data.length];
/*  322 */       this.cipher.update(data, 0, data.length, plain, 0);
/*  323 */       return plain;
/*      */     }
/*  325 */     catch (Exception e) {
/*      */ 
/*      */       
/*  328 */       return null;
/*      */     } 
/*      */   }
/*      */   int writeSEQUENCE(byte[] buf, int index, int len) {
/*  332 */     buf[index++] = 48;
/*  333 */     index = writeLength(buf, index, len);
/*  334 */     return index;
/*      */   }
/*      */   int writeINTEGER(byte[] buf, int index, byte[] data) {
/*  337 */     buf[index++] = 2;
/*  338 */     index = writeLength(buf, index, data.length);
/*  339 */     System.arraycopy(data, 0, buf, index, data.length);
/*  340 */     index += data.length;
/*  341 */     return index;
/*      */   }
/*      */   
/*      */   int writeOCTETSTRING(byte[] buf, int index, byte[] data) {
/*  345 */     buf[index++] = 4;
/*  346 */     index = writeLength(buf, index, data.length);
/*  347 */     System.arraycopy(data, 0, buf, index, data.length);
/*  348 */     index += data.length;
/*  349 */     return index;
/*      */   }
/*      */   
/*      */   int writeDATA(byte[] buf, byte n, int index, byte[] data) {
/*  353 */     buf[index++] = n;
/*  354 */     index = writeLength(buf, index, data.length);
/*  355 */     System.arraycopy(data, 0, buf, index, data.length);
/*  356 */     index += data.length;
/*  357 */     return index;
/*      */   }
/*      */   
/*      */   int countLength(int len) {
/*  361 */     int i = 1;
/*  362 */     if (len <= 127) return i; 
/*  363 */     while (len > 0) {
/*  364 */       len >>>= 8;
/*  365 */       i++;
/*      */     } 
/*  367 */     return i;
/*      */   }
/*      */   
/*      */   int writeLength(byte[] data, int index, int len) {
/*  371 */     int i = countLength(len) - 1;
/*  372 */     if (i == 0) {
/*  373 */       data[index++] = (byte)len;
/*  374 */       return index;
/*      */     } 
/*  376 */     data[index++] = (byte)(0x80 | i);
/*  377 */     int j = index + i;
/*  378 */     while (i > 0) {
/*  379 */       data[index + i - 1] = (byte)(len & 0xFF);
/*  380 */       len >>>= 8;
/*  381 */       i--;
/*      */     } 
/*  383 */     return j;
/*      */   }
/*      */   
/*      */   private Random genRandom() {
/*  387 */     if (this.random == null)
/*      */       try {
/*  389 */         Class c = Class.forName(JSch.getConfig("random"));
/*  390 */         this.random = (Random)c.newInstance();
/*      */       } catch (Exception e) {
/*  392 */         System.err.println("connect: random " + e);
/*      */       }  
/*  394 */     return this.random;
/*      */   }
/*      */   
/*      */   private HASH genHash() {
/*      */     try {
/*  399 */       Class c = Class.forName(JSch.getConfig("md5"));
/*  400 */       this.hash = (HASH)c.newInstance();
/*  401 */       this.hash.init();
/*      */     }
/*  403 */     catch (Exception e) {}
/*      */     
/*  405 */     return this.hash;
/*      */   }
/*      */   
/*      */   private Cipher genCipher() {
/*      */     try {
/*  410 */       Class c = Class.forName(JSch.getConfig("3des-cbc"));
/*  411 */       this.cipher = (Cipher)c.newInstance();
/*      */     }
/*  413 */     catch (Exception e) {}
/*      */     
/*  415 */     return this.cipher;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   synchronized byte[] genKey(byte[] passphrase, byte[] iv) {
/*  425 */     if (this.cipher == null) this.cipher = genCipher(); 
/*  426 */     if (this.hash == null) this.hash = genHash();
/*      */     
/*  428 */     byte[] key = new byte[this.cipher.getBlockSize()];
/*  429 */     int hsize = this.hash.getBlockSize();
/*  430 */     byte[] hn = new byte[key.length / hsize * hsize + ((key.length % hsize == 0) ? 0 : hsize)];
/*      */     
/*      */     try {
/*  433 */       byte[] tmp = null;
/*  434 */       if (this.vendor == 0) {
/*  435 */         int index; for (index = 0; index + hsize <= hn.length; ) {
/*  436 */           if (tmp != null) this.hash.update(tmp, 0, tmp.length); 
/*  437 */           this.hash.update(passphrase, 0, passphrase.length);
/*  438 */           this.hash.update(iv, 0, (iv.length > 8) ? 8 : iv.length);
/*  439 */           tmp = this.hash.digest();
/*  440 */           System.arraycopy(tmp, 0, hn, index, tmp.length);
/*  441 */           index += tmp.length;
/*      */         } 
/*  443 */         System.arraycopy(hn, 0, key, 0, key.length);
/*      */       }
/*  445 */       else if (this.vendor == 1) {
/*  446 */         int index; for (index = 0; index + hsize <= hn.length; ) {
/*  447 */           if (tmp != null) this.hash.update(tmp, 0, tmp.length); 
/*  448 */           this.hash.update(passphrase, 0, passphrase.length);
/*  449 */           tmp = this.hash.digest();
/*  450 */           System.arraycopy(tmp, 0, hn, index, tmp.length);
/*  451 */           index += tmp.length;
/*      */         } 
/*  453 */         System.arraycopy(hn, 0, key, 0, key.length);
/*      */       }
/*  455 */       else if (this.vendor == 2) {
/*  456 */         Class c = Class.forName(JSch.getConfig("sha-1"));
/*  457 */         HASH sha1 = (HASH)c.newInstance();
/*  458 */         tmp = new byte[4];
/*  459 */         key = new byte[40];
/*  460 */         for (int i = 0; i < 2; i++) {
/*  461 */           sha1.init();
/*  462 */           tmp[3] = (byte)i;
/*  463 */           sha1.update(tmp, 0, tmp.length);
/*  464 */           sha1.update(passphrase, 0, passphrase.length);
/*  465 */           System.arraycopy(sha1.digest(), 0, key, i * 20, 20);
/*      */         }
/*      */       
/*      */       } 
/*  469 */     } catch (Exception e) {
/*  470 */       System.err.println(e);
/*      */     } 
/*  472 */     return key;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setPassphrase(String passphrase) {
/*  479 */     if (passphrase == null || passphrase.length() == 0) {
/*  480 */       setPassphrase((byte[])null);
/*      */     } else {
/*      */       
/*  483 */       setPassphrase(Util.str2byte(passphrase));
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setPassphrase(byte[] passphrase) {
/*  491 */     if (passphrase != null && passphrase.length == 0)
/*  492 */       passphrase = null; 
/*  493 */     this.passphrase = passphrase;
/*      */   }
/*      */   
/*  496 */   public KeyPair(JSch jsch) { this.encrypted = false;
/*  497 */     this.data = null;
/*  498 */     this.iv = null;
/*  499 */     this.publickeyblob = null;
/*      */     this.jsch = jsch; } public boolean isEncrypted() {
/*  501 */     return this.encrypted;
/*      */   } public boolean decrypt(String _passphrase) {
/*  503 */     if (_passphrase == null || _passphrase.length() == 0) {
/*  504 */       return !this.encrypted;
/*      */     }
/*  506 */     return decrypt(Util.str2byte(_passphrase));
/*      */   }
/*      */   
/*      */   public boolean decrypt(byte[] _passphrase) {
/*  510 */     if (!this.encrypted) {
/*  511 */       return true;
/*      */     }
/*  513 */     if (_passphrase == null) {
/*  514 */       return !this.encrypted;
/*      */     }
/*  516 */     byte[] bar = new byte[_passphrase.length];
/*  517 */     System.arraycopy(_passphrase, 0, bar, 0, bar.length);
/*  518 */     _passphrase = bar;
/*  519 */     byte[] foo = decrypt(this.data, _passphrase, this.iv);
/*  520 */     Util.bzero(_passphrase);
/*  521 */     if (parse(foo)) {
/*  522 */       this.encrypted = false;
/*      */     }
/*  524 */     return !this.encrypted;
/*      */   }
/*      */   
/*      */   public static KeyPair load(JSch jsch, String prvkey) throws JSchException {
/*  528 */     String pubkey = prvkey + ".pub";
/*  529 */     if (!(new File(pubkey)).exists()) {
/*  530 */       pubkey = null;
/*      */     }
/*  532 */     return load(jsch, prvkey, pubkey);
/*      */   }
/*      */   
/*      */   public static KeyPair load(JSch jsch, String prvfile, String pubfile) throws JSchException {
/*  536 */     byte[] prvkey = null;
/*  537 */     byte[] pubkey = null;
/*      */     
/*      */     try {
/*  540 */       prvkey = Util.fromFile(prvfile);
/*      */     }
/*  542 */     catch (IOException e) {
/*  543 */       throw new JSchException(e.toString(), e);
/*      */     } 
/*      */     
/*  546 */     String _pubfile = pubfile;
/*  547 */     if (pubfile == null) {
/*  548 */       _pubfile = prvfile + ".pub";
/*      */     }
/*      */     
/*      */     try {
/*  552 */       pubkey = Util.fromFile(_pubfile);
/*      */     }
/*  554 */     catch (IOException e) {
/*  555 */       if (pubfile != null) {
/*  556 */         throw new JSchException(e.toString(), e);
/*      */       }
/*      */     } 
/*      */     
/*      */     try {
/*  561 */       return load(jsch, prvkey, pubkey);
/*      */     } finally {
/*      */       
/*  564 */       Util.bzero(prvkey);
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*      */   public static KeyPair load(JSch jsch, byte[] prvkey, byte[] pubkey) throws JSchException {
/*  570 */     byte[] iv = new byte[8];
/*  571 */     boolean encrypted = true;
/*  572 */     byte[] data = null;
/*      */     
/*  574 */     byte[] publickeyblob = null;
/*      */     
/*  576 */     int type = 0;
/*  577 */     int vendor = 0;
/*  578 */     String publicKeyComment = "";
/*  579 */     Cipher cipher = null;
/*      */ 
/*      */     
/*  582 */     if (pubkey == null && prvkey != null && prvkey.length > 11 && prvkey[0] == 0 && prvkey[1] == 0 && prvkey[2] == 0 && (prvkey[3] == 7 || prvkey[3] == 19)) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  588 */       Buffer buf = new Buffer(prvkey);
/*  589 */       buf.skip(prvkey.length);
/*  590 */       String _type = new String(buf.getString());
/*  591 */       buf.rewind();
/*      */       
/*  593 */       KeyPair keyPair = null;
/*  594 */       if (_type.equals("ssh-rsa")) {
/*  595 */         keyPair = KeyPairRSA.fromSSHAgent(jsch, buf);
/*      */       }
/*  597 */       else if (_type.equals("ssh-dss")) {
/*  598 */         keyPair = KeyPairDSA.fromSSHAgent(jsch, buf);
/*      */       }
/*  600 */       else if (_type.equals("ecdsa-sha2-nistp256") || _type.equals("ecdsa-sha2-nistp384") || _type.equals("ecdsa-sha2-nistp512")) {
/*      */ 
/*      */         
/*  603 */         keyPair = KeyPairECDSA.fromSSHAgent(jsch, buf);
/*      */       } else {
/*      */         
/*  606 */         throw new JSchException("privatekey: invalid key " + new String(prvkey, 4, 7));
/*      */       } 
/*  608 */       return keyPair;
/*      */     } 
/*      */     
/*      */     try {
/*  612 */       byte[] buf = prvkey;
/*      */       
/*  614 */       if (buf != null) {
/*  615 */         KeyPair ppk = loadPPK(jsch, buf);
/*  616 */         if (ppk != null) {
/*  617 */           return ppk;
/*      */         }
/*      */       } 
/*  620 */       int len = (buf != null) ? buf.length : 0;
/*  621 */       int i = 0;
/*      */ 
/*      */       
/*  624 */       while (i < len && (
/*  625 */         buf[i] != 45 || i + 4 >= len || buf[i + 1] != 45 || buf[i + 2] != 45 || buf[i + 3] != 45 || buf[i + 4] != 45))
/*      */       {
/*      */ 
/*      */ 
/*      */         
/*  630 */         i++;
/*      */       }
/*      */       
/*  633 */       while (i < len) {
/*  634 */         if (buf[i] == 66 && i + 3 < len && buf[i + 1] == 69 && buf[i + 2] == 71 && buf[i + 3] == 73) {
/*  635 */           i += 6;
/*  636 */           if (i + 2 >= len)
/*  637 */             throw new JSchException("invalid privatekey: " + prvkey); 
/*  638 */           if (buf[i] == 68 && buf[i + 1] == 83 && buf[i + 2] == 65) { type = 1; }
/*  639 */           else if (buf[i] == 82 && buf[i + 1] == 83 && buf[i + 2] == 65) { type = 2; }
/*  640 */           else if (buf[i] == 69 && buf[i + 1] == 67) { type = 3; }
/*  641 */           else if (buf[i] == 83 && buf[i + 1] == 83 && buf[i + 2] == 72)
/*  642 */           { type = 4;
/*  643 */             vendor = 1; }
/*      */           
/*  645 */           else if (i + 6 < len && buf[i] == 80 && buf[i + 1] == 82 && buf[i + 2] == 73 && buf[i + 3] == 86 && buf[i + 4] == 65 && buf[i + 5] == 84 && buf[i + 6] == 69)
/*      */           
/*      */           { 
/*      */             
/*  649 */             type = 4;
/*  650 */             vendor = 3;
/*  651 */             encrypted = false;
/*  652 */             i += 3; }
/*      */           
/*  654 */           else if (i + 8 < len && buf[i] == 69 && buf[i + 1] == 78 && buf[i + 2] == 67 && buf[i + 3] == 82 && buf[i + 4] == 89 && buf[i + 5] == 80 && buf[i + 6] == 84 && buf[i + 7] == 69 && buf[i + 8] == 68)
/*      */           
/*      */           { 
/*      */ 
/*      */             
/*  659 */             type = 4;
/*  660 */             vendor = 3;
/*  661 */             i += 5; }
/*      */           else
/*      */           
/*  664 */           { throw new JSchException("invalid privatekey: " + prvkey); }
/*      */           
/*  666 */           i += 3;
/*      */           continue;
/*      */         } 
/*  669 */         if (buf[i] == 65 && i + 7 < len && buf[i + 1] == 69 && buf[i + 2] == 83 && buf[i + 3] == 45 && buf[i + 4] == 50 && buf[i + 5] == 53 && buf[i + 6] == 54 && buf[i + 7] == 45) {
/*      */           
/*  671 */           i += 8;
/*  672 */           if (Session.checkCipher(JSch.getConfig("aes256-cbc"))) {
/*  673 */             Class c = Class.forName(JSch.getConfig("aes256-cbc"));
/*  674 */             cipher = (Cipher)c.newInstance();
/*      */             
/*  676 */             iv = new byte[cipher.getIVSize()];
/*      */             continue;
/*      */           } 
/*  679 */           throw new JSchException("privatekey: aes256-cbc is not available " + prvkey);
/*      */         } 
/*      */ 
/*      */         
/*  683 */         if (buf[i] == 65 && i + 7 < len && buf[i + 1] == 69 && buf[i + 2] == 83 && buf[i + 3] == 45 && buf[i + 4] == 49 && buf[i + 5] == 57 && buf[i + 6] == 50 && buf[i + 7] == 45) {
/*      */           
/*  685 */           i += 8;
/*  686 */           if (Session.checkCipher(JSch.getConfig("aes192-cbc"))) {
/*  687 */             Class c = Class.forName(JSch.getConfig("aes192-cbc"));
/*  688 */             cipher = (Cipher)c.newInstance();
/*      */             
/*  690 */             iv = new byte[cipher.getIVSize()];
/*      */             continue;
/*      */           } 
/*  693 */           throw new JSchException("privatekey: aes192-cbc is not available " + prvkey);
/*      */         } 
/*      */ 
/*      */         
/*  697 */         if (buf[i] == 65 && i + 7 < len && buf[i + 1] == 69 && buf[i + 2] == 83 && buf[i + 3] == 45 && buf[i + 4] == 49 && buf[i + 5] == 50 && buf[i + 6] == 56 && buf[i + 7] == 45) {
/*      */           
/*  699 */           i += 8;
/*  700 */           if (Session.checkCipher(JSch.getConfig("aes128-cbc"))) {
/*  701 */             Class c = Class.forName(JSch.getConfig("aes128-cbc"));
/*  702 */             cipher = (Cipher)c.newInstance();
/*      */             
/*  704 */             iv = new byte[cipher.getIVSize()];
/*      */             continue;
/*      */           } 
/*  707 */           throw new JSchException("privatekey: aes128-cbc is not available " + prvkey);
/*      */         } 
/*      */ 
/*      */         
/*  711 */         if (buf[i] == 67 && i + 3 < len && buf[i + 1] == 66 && buf[i + 2] == 67 && buf[i + 3] == 44) {
/*  712 */           i += 4;
/*  713 */           for (int ii = 0; ii < iv.length; ii++) {
/*  714 */             iv[ii] = (byte)((a2b(buf[i++]) << 4 & 0xF0) + (a2b(buf[i++]) & 0xF));
/*      */           }
/*      */           continue;
/*      */         } 
/*  718 */         if (buf[i] == 13 && i + 1 < buf.length && buf[i + 1] == 10) {
/*  719 */           i++;
/*      */           continue;
/*      */         } 
/*  722 */         if (buf[i] == 10 && i + 1 < buf.length) {
/*  723 */           if (buf[i + 1] == 10) { i += 2; break; }
/*  724 */            if (buf[i + 1] == 13 && i + 2 < buf.length && buf[i + 2] == 10) {
/*      */             
/*  726 */             i += 3; break;
/*      */           } 
/*  728 */           boolean inheader = false;
/*  729 */           for (int j = i + 1; j < buf.length && 
/*  730 */             buf[j] != 10; j++) {
/*      */             
/*  732 */             if (buf[j] == 58) { inheader = true; break; }
/*      */           
/*  734 */           }  if (!inheader) {
/*  735 */             i++;
/*  736 */             if (vendor != 3)
/*  737 */               encrypted = false; 
/*      */             break;
/*      */           } 
/*      */         } 
/*  741 */         i++;
/*      */       } 
/*      */       
/*  744 */       if (buf != null) {
/*      */         
/*  746 */         if (type == 0) {
/*  747 */           throw new JSchException("invalid privatekey: " + prvkey);
/*      */         }
/*      */         
/*  750 */         int start = i;
/*  751 */         while (i < len && 
/*  752 */           buf[i] != 45) {
/*  753 */           i++;
/*      */         }
/*      */         
/*  756 */         if (len - i == 0 || i - start == 0) {
/*  757 */           throw new JSchException("invalid privatekey: " + prvkey);
/*      */         }
/*      */ 
/*      */         
/*  761 */         byte[] tmp = new byte[i - start];
/*  762 */         System.arraycopy(buf, start, tmp, 0, tmp.length);
/*  763 */         byte[] _buf = tmp;
/*      */         
/*  765 */         start = 0;
/*  766 */         i = 0;
/*      */         
/*  768 */         int _len = _buf.length;
/*  769 */         while (i < _len) {
/*  770 */           if (_buf[i] == 10) {
/*  771 */             boolean xd = (_buf[i - 1] == 13);
/*      */             
/*  773 */             System.arraycopy(_buf, i + 1, _buf, i - (xd ? 1 : 0), _len - i + 1);
/*  774 */             if (xd) _len--; 
/*  775 */             _len--;
/*      */             continue;
/*      */           } 
/*  778 */           if (_buf[i] == 45)
/*  779 */             break;  i++;
/*      */         } 
/*      */         
/*  782 */         if (i - start > 0) {
/*  783 */           data = Util.fromBase64(_buf, start, i - start);
/*      */         }
/*  785 */         Util.bzero(_buf);
/*      */       } 
/*      */       
/*  788 */       if (data != null && data.length > 4 && data[0] == 63 && data[1] == 111 && data[2] == -7 && data[3] == -21) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/*  795 */         Buffer _buf = new Buffer(data);
/*  796 */         _buf.getInt();
/*  797 */         _buf.getInt();
/*  798 */         byte[] _type = _buf.getString();
/*      */         
/*  800 */         String _cipher = Util.byte2str(_buf.getString());
/*      */         
/*  802 */         if (_cipher.equals("3des-cbc")) {
/*  803 */           _buf.getInt();
/*  804 */           byte[] foo = new byte[data.length - _buf.getOffSet()];
/*  805 */           _buf.getByte(foo);
/*  806 */           data = foo;
/*  807 */           encrypted = true;
/*  808 */           throw new JSchException("unknown privatekey format: " + prvkey);
/*      */         } 
/*  810 */         if (_cipher.equals("none")) {
/*  811 */           _buf.getInt();
/*  812 */           _buf.getInt();
/*      */           
/*  814 */           encrypted = false;
/*      */           
/*  816 */           byte[] foo = new byte[data.length - _buf.getOffSet()];
/*  817 */           _buf.getByte(foo);
/*  818 */           data = foo;
/*      */         } 
/*      */       } 
/*      */       
/*  822 */       if (pubkey != null) {
/*      */         try {
/*  824 */           buf = pubkey;
/*  825 */           len = buf.length;
/*  826 */           if (buf.length > 4 && buf[0] == 45 && buf[1] == 45 && buf[2] == 45 && buf[3] == 45) {
/*      */ 
/*      */             
/*  829 */             boolean valid = true;
/*  830 */             i = 0; 
/*  831 */             do { i++; } while (buf.length > i && buf[i] != 10);
/*  832 */             if (buf.length <= i) valid = false;
/*      */             
/*  834 */             while (valid) {
/*  835 */               if (buf[i] == 10) {
/*  836 */                 boolean inheader = false;
/*  837 */                 for (int j = i + 1; j < buf.length && 
/*  838 */                   buf[j] != 10; j++) {
/*  839 */                   if (buf[j] == 58) { inheader = true; break; }
/*      */                 
/*  841 */                 }  if (!inheader) {
/*  842 */                   i++;
/*      */                   break;
/*      */                 } 
/*      */               } 
/*  846 */               i++;
/*      */             } 
/*  848 */             if (buf.length <= i) valid = false;
/*      */             
/*  850 */             int start = i;
/*  851 */             while (valid && i < len) {
/*  852 */               if (buf[i] == 10) {
/*  853 */                 System.arraycopy(buf, i + 1, buf, i, len - i - 1);
/*  854 */                 len--;
/*      */                 continue;
/*      */               } 
/*  857 */               if (buf[i] == 45)
/*  858 */                 break;  i++;
/*      */             } 
/*  860 */             if (valid) {
/*  861 */               publickeyblob = Util.fromBase64(buf, start, i - start);
/*  862 */               if (prvkey == null || type == 4) {
/*  863 */                 if (publickeyblob[8] == 100) { type = 1; }
/*  864 */                 else if (publickeyblob[8] == 114) { type = 2; }
/*      */ 
/*      */               
/*      */               }
/*      */             } 
/*  869 */           } else if (buf[0] == 115 && buf[1] == 115 && buf[2] == 104 && buf[3] == 45) {
/*  870 */             if (prvkey == null && buf.length > 7)
/*      */             {
/*  872 */               if (buf[4] == 100) { type = 1; }
/*  873 */               else if (buf[4] == 114) { type = 2; }
/*      */                } 
/*  875 */             i = 0;
/*  876 */             for (; i < len && buf[i] != 32; i++); i++;
/*  877 */             if (i < len) {
/*  878 */               int start = i;
/*  879 */               for (; i < len && buf[i] != 32; i++);
/*  880 */               publickeyblob = Util.fromBase64(buf, start, i - start);
/*      */             } 
/*  882 */             if (i++ < len) {
/*  883 */               int start = i;
/*  884 */               for (; i < len && buf[i] != 10; i++);
/*  885 */               if (i > 0 && buf[i - 1] == 13) i--; 
/*  886 */               if (start < i) {
/*  887 */                 publicKeyComment = new String(buf, start, i - start);
/*      */               }
/*      */             }
/*      */           
/*  891 */           } else if (buf[0] == 101 && buf[1] == 99 && buf[2] == 100 && buf[3] == 115) {
/*  892 */             if (prvkey == null && buf.length > 7) {
/*  893 */               type = 3;
/*      */             }
/*  895 */             i = 0;
/*  896 */             for (; i < len && buf[i] != 32; i++); i++;
/*  897 */             if (i < len) {
/*  898 */               int start = i;
/*  899 */               for (; i < len && buf[i] != 32; i++);
/*  900 */               publickeyblob = Util.fromBase64(buf, start, i - start);
/*      */             } 
/*  902 */             if (i++ < len) {
/*  903 */               int start = i;
/*  904 */               for (; i < len && buf[i] != 10; i++);
/*  905 */               if (i > 0 && buf[i - 1] == 13) i--; 
/*  906 */               if (start < i) {
/*  907 */                 publicKeyComment = new String(buf, start, i - start);
/*      */               }
/*      */             }
/*      */           
/*      */           }
/*      */         
/*  913 */         } catch (Exception ee) {}
/*      */       
/*      */       }
/*      */     }
/*  917 */     catch (Exception e) {
/*  918 */       if (e instanceof JSchException) throw (JSchException)e; 
/*  919 */       if (e instanceof Throwable)
/*  920 */         throw new JSchException(e.toString(), e); 
/*  921 */       throw new JSchException(e.toString());
/*      */     } 
/*      */     
/*  924 */     KeyPair kpair = null;
/*  925 */     if (type == 1) { kpair = new KeyPairDSA(jsch); }
/*  926 */     else if (type == 2) { kpair = new KeyPairRSA(jsch); }
/*  927 */     else if (type == 3) { kpair = new KeyPairECDSA(jsch); }
/*  928 */     else if (vendor == 3) { kpair = new KeyPairPKCS8(jsch); }
/*      */     
/*  930 */     if (kpair != null) {
/*  931 */       kpair.encrypted = encrypted;
/*  932 */       kpair.publickeyblob = publickeyblob;
/*  933 */       kpair.vendor = vendor;
/*  934 */       kpair.publicKeyComment = publicKeyComment;
/*  935 */       kpair.cipher = cipher;
/*      */       
/*  937 */       if (encrypted) {
/*  938 */         kpair.encrypted = true;
/*  939 */         kpair.iv = iv;
/*  940 */         kpair.data = data;
/*      */       } else {
/*      */         
/*  943 */         if (kpair.parse(data)) {
/*  944 */           kpair.encrypted = false;
/*  945 */           return kpair;
/*      */         } 
/*      */         
/*  948 */         throw new JSchException("invalid privatekey: " + prvkey);
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  953 */     return kpair;
/*      */   }
/*      */   
/*      */   private static byte a2b(byte c) {
/*  957 */     if (48 <= c && c <= 57) return (byte)(c - 48); 
/*  958 */     return (byte)(c - 97 + 10);
/*      */   }
/*      */   private static byte b2a(byte c) {
/*  961 */     if (0 <= c && c <= 9) return (byte)(c + 48); 
/*  962 */     return (byte)(c - 10 + 65);
/*      */   }
/*      */   
/*      */   public void dispose() {
/*  966 */     Util.bzero(this.passphrase);
/*      */   }
/*      */   
/*      */   public void finalize() {
/*  970 */     dispose();
/*      */   }
/*      */   
/*  973 */   private static final String[] header1 = new String[] { "PuTTY-User-Key-File-2: ", "Encryption: ", "Comment: ", "Public-Lines: " };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  980 */   private static final String[] header2 = new String[] { "Private-Lines: " };
/*      */ 
/*      */ 
/*      */   
/*  984 */   private static final String[] header3 = new String[] { "Private-MAC: " };
/*      */ 
/*      */ 
/*      */   
/*      */   static KeyPair loadPPK(JSch jsch, byte[] buf) throws JSchException {
/*  989 */     byte[] pubkey = null;
/*  990 */     byte[] prvkey = null;
/*  991 */     int lines = 0;
/*      */     
/*  993 */     Buffer buffer = new Buffer(buf);
/*  994 */     Hashtable v = new Hashtable();
/*      */     do {
/*      */     
/*  997 */     } while (parseHeader(buffer, v));
/*      */ 
/*      */ 
/*      */     
/* 1001 */     String typ = (String)v.get("PuTTY-User-Key-File-2");
/* 1002 */     if (typ == null) {
/* 1003 */       return null;
/*      */     }
/*      */     
/* 1006 */     lines = Integer.parseInt((String)v.get("Public-Lines"));
/* 1007 */     pubkey = parseLines(buffer, lines);
/*      */     do {
/*      */     
/* 1010 */     } while (parseHeader(buffer, v));
/*      */ 
/*      */ 
/*      */     
/* 1014 */     lines = Integer.parseInt((String)v.get("Private-Lines"));
/* 1015 */     prvkey = parseLines(buffer, lines);
/*      */     do {
/*      */     
/* 1018 */     } while (parseHeader(buffer, v));
/*      */ 
/*      */ 
/*      */     
/* 1022 */     prvkey = Util.fromBase64(prvkey, 0, prvkey.length);
/* 1023 */     pubkey = Util.fromBase64(pubkey, 0, pubkey.length);
/*      */     
/* 1025 */     KeyPair kpair = null;
/*      */     
/* 1027 */     if (typ.equals("ssh-rsa")) {
/*      */       
/* 1029 */       Buffer _buf = new Buffer(pubkey);
/* 1030 */       _buf.skip(pubkey.length);
/*      */       
/* 1032 */       int len = _buf.getInt();
/* 1033 */       _buf.getByte(new byte[len]);
/* 1034 */       byte[] pub_array = new byte[_buf.getInt()];
/* 1035 */       _buf.getByte(pub_array);
/* 1036 */       byte[] n_array = new byte[_buf.getInt()];
/* 1037 */       _buf.getByte(n_array);
/*      */       
/* 1039 */       kpair = new KeyPairRSA(jsch, n_array, pub_array, null);
/*      */     }
/* 1041 */     else if (typ.equals("ssh-dss")) {
/* 1042 */       Buffer _buf = new Buffer(pubkey);
/* 1043 */       _buf.skip(pubkey.length);
/*      */       
/* 1045 */       int len = _buf.getInt();
/* 1046 */       _buf.getByte(new byte[len]);
/*      */       
/* 1048 */       byte[] p_array = new byte[_buf.getInt()];
/* 1049 */       _buf.getByte(p_array);
/* 1050 */       byte[] q_array = new byte[_buf.getInt()];
/* 1051 */       _buf.getByte(q_array);
/* 1052 */       byte[] g_array = new byte[_buf.getInt()];
/* 1053 */       _buf.getByte(g_array);
/* 1054 */       byte[] y_array = new byte[_buf.getInt()];
/* 1055 */       _buf.getByte(y_array);
/*      */       
/* 1057 */       kpair = new KeyPairDSA(jsch, p_array, q_array, g_array, y_array, null);
/*      */     } else {
/*      */       
/* 1060 */       return null;
/*      */     } 
/*      */     
/* 1063 */     if (kpair == null) {
/* 1064 */       return null;
/*      */     }
/* 1066 */     kpair.encrypted = !v.get("Encryption").equals("none");
/* 1067 */     kpair.vendor = 2;
/* 1068 */     kpair.publicKeyComment = (String)v.get("Comment");
/* 1069 */     if (kpair.encrypted) {
/* 1070 */       if (Session.checkCipher(JSch.getConfig("aes256-cbc"))) {
/*      */         try {
/* 1072 */           Class c = Class.forName(JSch.getConfig("aes256-cbc"));
/* 1073 */           kpair.cipher = (Cipher)c.newInstance();
/* 1074 */           kpair.iv = new byte[kpair.cipher.getIVSize()];
/*      */         }
/* 1076 */         catch (Exception e) {
/* 1077 */           throw new JSchException("The cipher 'aes256-cbc' is required, but it is not available.");
/*      */         } 
/*      */       } else {
/*      */         
/* 1081 */         throw new JSchException("The cipher 'aes256-cbc' is required, but it is not available.");
/*      */       } 
/* 1083 */       kpair.data = prvkey;
/*      */     } else {
/*      */       
/* 1086 */       kpair.data = prvkey;
/* 1087 */       kpair.parse(prvkey);
/*      */     } 
/* 1089 */     return kpair;
/*      */   }
/*      */   
/*      */   private static byte[] parseLines(Buffer buffer, int lines) {
/* 1093 */     byte[] buf = buffer.buffer;
/* 1094 */     int index = buffer.index;
/* 1095 */     byte[] data = null;
/*      */     
/* 1097 */     int i = index;
/* 1098 */     while (lines-- > 0) {
/* 1099 */       while (buf.length > i) {
/* 1100 */         if (buf[i++] == 13) {
/* 1101 */           if (data == null) {
/* 1102 */             data = new byte[i - index - 1];
/* 1103 */             System.arraycopy(buf, index, data, 0, i - index - 1);
/*      */             break;
/*      */           } 
/* 1106 */           byte[] tmp = new byte[data.length + i - index - 1];
/* 1107 */           System.arraycopy(data, 0, tmp, 0, data.length);
/* 1108 */           System.arraycopy(buf, index, tmp, data.length, i - index - 1);
/* 1109 */           for (int j = 0; j < data.length; ) { data[j] = 0; j++; }
/* 1110 */            data = tmp;
/*      */           
/*      */           break;
/*      */         } 
/*      */       } 
/* 1115 */       if (buf[i] == 10)
/* 1116 */         i++; 
/* 1117 */       index = i;
/*      */     } 
/*      */     
/* 1120 */     if (data != null) {
/* 1121 */       buffer.index = index;
/*      */     }
/* 1123 */     return data;
/*      */   }
/*      */   
/*      */   private static boolean parseHeader(Buffer buffer, Hashtable v) {
/* 1127 */     byte[] buf = buffer.buffer;
/* 1128 */     int index = buffer.index;
/* 1129 */     String key = null;
/* 1130 */     String value = null; int i;
/* 1131 */     for (i = index; i < buf.length && 
/* 1132 */       buf[i] != 13; i++) {
/*      */ 
/*      */       
/* 1135 */       if (buf[i] == 58) {
/* 1136 */         key = new String(buf, index, i - index);
/* 1137 */         i++;
/* 1138 */         if (i < buf.length && buf[i] == 32) {
/* 1139 */           i++;
/*      */         }
/* 1141 */         index = i;
/*      */         
/*      */         break;
/*      */       } 
/*      */     } 
/* 1146 */     if (key == null) {
/* 1147 */       return false;
/*      */     }
/* 1149 */     for (i = index; i < buf.length; i++) {
/* 1150 */       if (buf[i] == 13) {
/* 1151 */         value = new String(buf, index, i - index);
/* 1152 */         i++;
/* 1153 */         if (i < buf.length && buf[i] == 10) {
/* 1154 */           i++;
/*      */         }
/* 1156 */         index = i;
/*      */         
/*      */         break;
/*      */       } 
/*      */     } 
/* 1161 */     if (value != null) {
/* 1162 */       v.put(key, value);
/* 1163 */       buffer.index = index;
/*      */     } 
/*      */     
/* 1166 */     return (key != null && value != null);
/*      */   } abstract void generate(int paramInt) throws JSchException; abstract byte[] getBegin(); abstract byte[] getEnd(); abstract int getKeySize(); public abstract byte[] getSignature(byte[] paramArrayOfbyte);
/*      */   public abstract Signature getVerifier();
/*      */   void copy(KeyPair kpair) {
/* 1170 */     this.publickeyblob = kpair.publickeyblob;
/* 1171 */     this.vendor = kpair.vendor;
/* 1172 */     this.publicKeyComment = kpair.publicKeyComment;
/* 1173 */     this.cipher = kpair.cipher;
/*      */   }
/*      */   public abstract byte[] forSSHAgent() throws JSchException;
/*      */   abstract byte[] getPrivateKey();
/*      */   abstract byte[] getKeyTypeName();
/*      */   public abstract int getKeyType();
/*      */   abstract boolean parse(byte[] paramArrayOfbyte);
/*      */   class ASN1Exception extends Exception {
/*      */     private final KeyPair this$0; }
/*      */   class ASN1 { byte[] buf; int start; int length; private final KeyPair this$0;
/*      */     ASN1(byte[] buf) throws KeyPair.ASN1Exception {
/* 1184 */       this(buf, 0, buf.length);
/*      */     }
/*      */     ASN1(byte[] buf, int start, int length) throws KeyPair.ASN1Exception {
/* 1187 */       this.buf = buf;
/* 1188 */       this.start = start;
/* 1189 */       this.length = length;
/* 1190 */       if (start + length > buf.length)
/* 1191 */         throw new KeyPair.ASN1Exception(); 
/*      */     }
/*      */     int getType() {
/* 1194 */       return this.buf[this.start] & 0xFF;
/*      */     }
/*      */     boolean isSEQUENCE() {
/* 1197 */       return (getType() == 48);
/*      */     }
/*      */     boolean isINTEGER() {
/* 1200 */       return (getType() == 2);
/*      */     }
/*      */     boolean isOBJECT() {
/* 1203 */       return (getType() == 6);
/*      */     }
/*      */     boolean isOCTETSTRING() {
/* 1206 */       return (getType() == 4);
/*      */     }
/*      */     private int getLength(int[] indexp) {
/* 1209 */       int index = indexp[0];
/* 1210 */       int length = this.buf[index++] & 0xFF;
/* 1211 */       if ((length & 0x80) != 0) {
/* 1212 */         int foo = length & 0x7F; length = 0;
/* 1213 */         for (; foo-- > 0; length = (length << 8) + (this.buf[index++] & 0xFF));
/*      */       } 
/* 1215 */       indexp[0] = index;
/* 1216 */       return length;
/*      */     }
/*      */     byte[] getContent() {
/* 1219 */       int[] indexp = new int[1];
/* 1220 */       indexp[0] = this.start + 1;
/* 1221 */       int length = getLength(indexp);
/* 1222 */       int index = indexp[0];
/* 1223 */       byte[] tmp = new byte[length];
/* 1224 */       System.arraycopy(this.buf, index, tmp, 0, tmp.length);
/* 1225 */       return tmp;
/*      */     }
/*      */     ASN1[] getContents() throws KeyPair.ASN1Exception {
/* 1228 */       int typ = this.buf[this.start];
/* 1229 */       int[] indexp = new int[1];
/* 1230 */       indexp[0] = this.start + 1;
/* 1231 */       int length = getLength(indexp);
/* 1232 */       if (typ == 5) {
/* 1233 */         return new ASN1[0];
/*      */       }
/* 1235 */       int index = indexp[0];
/* 1236 */       Vector values = new Vector();
/* 1237 */       while (length > 0) {
/* 1238 */         index++; length--;
/* 1239 */         int tmp = index;
/* 1240 */         indexp[0] = index;
/* 1241 */         int l = getLength(indexp);
/* 1242 */         index = indexp[0];
/* 1243 */         length -= index - tmp;
/* 1244 */         values.addElement(new ASN1(this.buf, tmp - 1, 1 + index - tmp + l));
/* 1245 */         index += l;
/* 1246 */         length -= l;
/*      */       } 
/* 1248 */       ASN1[] result = new ASN1[values.size()];
/* 1249 */       for (int i = 0; i < values.size(); i++) {
/* 1250 */         result[i] = values.elementAt(i);
/*      */       }
/* 1252 */       return result;
/*      */     } }
/*      */ 
/*      */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\KeyPair.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */