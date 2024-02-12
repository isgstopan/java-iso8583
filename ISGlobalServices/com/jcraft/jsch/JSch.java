/*     */ package com.jcraft.jsch;
/*     */ 
/*     */ import java.io.InputStream;
/*     */ import java.util.Enumeration;
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
/*     */ public class JSch
/*     */ {
/*     */   public static final String VERSION = "0.1.54";
/*  41 */   static Hashtable config = new Hashtable();
/*     */   static {
/*  43 */     config.put("kex", "ecdh-sha2-nistp256,ecdh-sha2-nistp384,ecdh-sha2-nistp521,diffie-hellman-group14-sha1,diffie-hellman-group-exchange-sha256,diffie-hellman-group-exchange-sha1,diffie-hellman-group1-sha1");
/*  44 */     config.put("server_host_key", "ssh-rsa,ssh-dss,ecdsa-sha2-nistp256,ecdsa-sha2-nistp384,ecdsa-sha2-nistp521");
/*  45 */     config.put("cipher.s2c", "aes128-ctr,aes128-cbc,3des-ctr,3des-cbc,blowfish-cbc,aes192-ctr,aes192-cbc,aes256-ctr,aes256-cbc");
/*     */     
/*  47 */     config.put("cipher.c2s", "aes128-ctr,aes128-cbc,3des-ctr,3des-cbc,blowfish-cbc,aes192-ctr,aes192-cbc,aes256-ctr,aes256-cbc");
/*     */ 
/*     */     
/*  50 */     config.put("mac.s2c", "hmac-md5,hmac-sha1,hmac-sha2-256,hmac-sha1-96,hmac-md5-96");
/*  51 */     config.put("mac.c2s", "hmac-md5,hmac-sha1,hmac-sha2-256,hmac-sha1-96,hmac-md5-96");
/*  52 */     config.put("compression.s2c", "none");
/*  53 */     config.put("compression.c2s", "none");
/*     */     
/*  55 */     config.put("lang.s2c", "");
/*  56 */     config.put("lang.c2s", "");
/*     */     
/*  58 */     config.put("compression_level", "6");
/*     */     
/*  60 */     config.put("diffie-hellman-group-exchange-sha1", "com.jcraft.jsch.DHGEX");
/*     */     
/*  62 */     config.put("diffie-hellman-group1-sha1", "com.jcraft.jsch.DHG1");
/*     */     
/*  64 */     config.put("diffie-hellman-group14-sha1", "com.jcraft.jsch.DHG14");
/*     */     
/*  66 */     config.put("diffie-hellman-group-exchange-sha256", "com.jcraft.jsch.DHGEX256");
/*     */ 
/*     */     
/*  69 */     config.put("ecdsa-sha2-nistp256", "com.jcraft.jsch.jce.SignatureECDSA");
/*  70 */     config.put("ecdsa-sha2-nistp384", "com.jcraft.jsch.jce.SignatureECDSA");
/*  71 */     config.put("ecdsa-sha2-nistp521", "com.jcraft.jsch.jce.SignatureECDSA");
/*     */     
/*  73 */     config.put("ecdh-sha2-nistp256", "com.jcraft.jsch.DHEC256");
/*  74 */     config.put("ecdh-sha2-nistp384", "com.jcraft.jsch.DHEC384");
/*  75 */     config.put("ecdh-sha2-nistp521", "com.jcraft.jsch.DHEC521");
/*     */     
/*  77 */     config.put("ecdh-sha2-nistp", "com.jcraft.jsch.jce.ECDHN");
/*     */     
/*  79 */     config.put("dh", "com.jcraft.jsch.jce.DH");
/*  80 */     config.put("3des-cbc", "com.jcraft.jsch.jce.TripleDESCBC");
/*  81 */     config.put("blowfish-cbc", "com.jcraft.jsch.jce.BlowfishCBC");
/*  82 */     config.put("hmac-sha1", "com.jcraft.jsch.jce.HMACSHA1");
/*  83 */     config.put("hmac-sha1-96", "com.jcraft.jsch.jce.HMACSHA196");
/*  84 */     config.put("hmac-sha2-256", "com.jcraft.jsch.jce.HMACSHA256");
/*     */ 
/*     */ 
/*     */     
/*  88 */     config.put("hmac-md5", "com.jcraft.jsch.jce.HMACMD5");
/*  89 */     config.put("hmac-md5-96", "com.jcraft.jsch.jce.HMACMD596");
/*  90 */     config.put("sha-1", "com.jcraft.jsch.jce.SHA1");
/*  91 */     config.put("sha-256", "com.jcraft.jsch.jce.SHA256");
/*  92 */     config.put("sha-384", "com.jcraft.jsch.jce.SHA384");
/*  93 */     config.put("sha-512", "com.jcraft.jsch.jce.SHA512");
/*  94 */     config.put("md5", "com.jcraft.jsch.jce.MD5");
/*  95 */     config.put("signature.dss", "com.jcraft.jsch.jce.SignatureDSA");
/*  96 */     config.put("signature.rsa", "com.jcraft.jsch.jce.SignatureRSA");
/*  97 */     config.put("signature.ecdsa", "com.jcraft.jsch.jce.SignatureECDSA");
/*  98 */     config.put("keypairgen.dsa", "com.jcraft.jsch.jce.KeyPairGenDSA");
/*  99 */     config.put("keypairgen.rsa", "com.jcraft.jsch.jce.KeyPairGenRSA");
/* 100 */     config.put("keypairgen.ecdsa", "com.jcraft.jsch.jce.KeyPairGenECDSA");
/* 101 */     config.put("random", "com.jcraft.jsch.jce.Random");
/*     */     
/* 103 */     config.put("none", "com.jcraft.jsch.CipherNone");
/*     */     
/* 105 */     config.put("aes128-cbc", "com.jcraft.jsch.jce.AES128CBC");
/* 106 */     config.put("aes192-cbc", "com.jcraft.jsch.jce.AES192CBC");
/* 107 */     config.put("aes256-cbc", "com.jcraft.jsch.jce.AES256CBC");
/*     */     
/* 109 */     config.put("aes128-ctr", "com.jcraft.jsch.jce.AES128CTR");
/* 110 */     config.put("aes192-ctr", "com.jcraft.jsch.jce.AES192CTR");
/* 111 */     config.put("aes256-ctr", "com.jcraft.jsch.jce.AES256CTR");
/* 112 */     config.put("3des-ctr", "com.jcraft.jsch.jce.TripleDESCTR");
/* 113 */     config.put("arcfour", "com.jcraft.jsch.jce.ARCFOUR");
/* 114 */     config.put("arcfour128", "com.jcraft.jsch.jce.ARCFOUR128");
/* 115 */     config.put("arcfour256", "com.jcraft.jsch.jce.ARCFOUR256");
/*     */     
/* 117 */     config.put("userauth.none", "com.jcraft.jsch.UserAuthNone");
/* 118 */     config.put("userauth.password", "com.jcraft.jsch.UserAuthPassword");
/* 119 */     config.put("userauth.keyboard-interactive", "com.jcraft.jsch.UserAuthKeyboardInteractive");
/* 120 */     config.put("userauth.publickey", "com.jcraft.jsch.UserAuthPublicKey");
/* 121 */     config.put("userauth.gssapi-with-mic", "com.jcraft.jsch.UserAuthGSSAPIWithMIC");
/* 122 */     config.put("gssapi-with-mic.krb5", "com.jcraft.jsch.jgss.GSSContextKrb5");
/*     */     
/* 124 */     config.put("zlib", "com.jcraft.jsch.jcraft.Compression");
/* 125 */     config.put("zlib@openssh.com", "com.jcraft.jsch.jcraft.Compression");
/*     */     
/* 127 */     config.put("pbkdf", "com.jcraft.jsch.jce.PBKDF");
/*     */     
/* 129 */     config.put("StrictHostKeyChecking", "ask");
/* 130 */     config.put("HashKnownHosts", "no");
/*     */     
/* 132 */     config.put("PreferredAuthentications", "gssapi-with-mic,publickey,keyboard-interactive,password");
/*     */     
/* 134 */     config.put("CheckCiphers", "aes256-ctr,aes192-ctr,aes128-ctr,aes256-cbc,aes192-cbc,aes128-cbc,3des-ctr,arcfour,arcfour128,arcfour256");
/* 135 */     config.put("CheckKexes", "diffie-hellman-group14-sha1,ecdh-sha2-nistp256,ecdh-sha2-nistp384,ecdh-sha2-nistp521");
/* 136 */     config.put("CheckSignatures", "ecdsa-sha2-nistp256,ecdsa-sha2-nistp384,ecdsa-sha2-nistp521");
/*     */     
/* 138 */     config.put("MaxAuthTries", "6");
/* 139 */     config.put("ClearAllForwardings", "no");
/*     */   }
/*     */   
/* 142 */   private Vector sessionPool = new Vector();
/*     */   
/* 144 */   private IdentityRepository defaultIdentityRepository = new LocalIdentityRepository(this);
/*     */ 
/*     */   
/* 147 */   private IdentityRepository identityRepository = this.defaultIdentityRepository;
/*     */   
/* 149 */   private ConfigRepository configRepository = null;
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
/*     */   public synchronized void setIdentityRepository(IdentityRepository identityRepository) {
/* 161 */     if (identityRepository == null) {
/* 162 */       this.identityRepository = this.defaultIdentityRepository;
/*     */     } else {
/*     */       
/* 165 */       this.identityRepository = identityRepository;
/*     */     } 
/*     */   }
/*     */   
/*     */   public synchronized IdentityRepository getIdentityRepository() {
/* 170 */     return this.identityRepository;
/*     */   }
/*     */   
/*     */   public ConfigRepository getConfigRepository() {
/* 174 */     return this.configRepository;
/*     */   }
/*     */   
/*     */   public void setConfigRepository(ConfigRepository configRepository) {
/* 178 */     this.configRepository = configRepository;
/*     */   }
/*     */   
/* 181 */   private HostKeyRepository known_hosts = null;
/*     */   
/* 183 */   private static final Logger DEVNULL = new Logger() { public void log(int level, String message) {} public boolean isEnabled(int level) {
/* 184 */         return false;
/*     */       } }
/*     */   ;
/* 187 */   static Logger logger = DEVNULL;
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
/*     */   public Session getSession(String host) throws JSchException {
/* 226 */     return getSession(null, host, 22);
/*     */   }
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
/*     */   public Session getSession(String username, String host) throws JSchException {
/* 249 */     return getSession(username, host, 22);
/*     */   }
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
/*     */   public Session getSession(String username, String host, int port) throws JSchException {
/* 271 */     if (host == null) {
/* 272 */       throw new JSchException("host must not be null.");
/*     */     }
/* 274 */     Session s = new Session(this, username, host, port);
/* 275 */     return s;
/*     */   }
/*     */   
/*     */   protected void addSession(Session session) {
/* 279 */     synchronized (this.sessionPool) {
/* 280 */       this.sessionPool.addElement(session);
/*     */     } 
/*     */   }
/*     */   
/*     */   protected boolean removeSession(Session session) {
/* 285 */     synchronized (this.sessionPool) {
/* 286 */       return this.sessionPool.remove(session);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setHostKeyRepository(HostKeyRepository hkrepo) {
/* 299 */     this.known_hosts = hkrepo;
/*     */   }
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
/*     */   public void setKnownHosts(String filename) throws JSchException {
/* 314 */     if (this.known_hosts == null) this.known_hosts = new KnownHosts(this); 
/* 315 */     if (this.known_hosts instanceof KnownHosts) {
/* 316 */       synchronized (this.known_hosts) {
/* 317 */         ((KnownHosts)this.known_hosts).setKnownHosts(filename);
/*     */       } 
/*     */     }
/*     */   }
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
/*     */   public void setKnownHosts(InputStream stream) throws JSchException {
/* 334 */     if (this.known_hosts == null) this.known_hosts = new KnownHosts(this); 
/* 335 */     if (this.known_hosts instanceof KnownHosts) {
/* 336 */       synchronized (this.known_hosts) {
/* 337 */         ((KnownHosts)this.known_hosts).setKnownHosts(stream);
/*     */       } 
/*     */     }
/*     */   }
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
/*     */   public HostKeyRepository getHostKeyRepository() {
/* 352 */     if (this.known_hosts == null) this.known_hosts = new KnownHosts(this); 
/* 353 */     return this.known_hosts;
/*     */   }
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
/*     */   public void addIdentity(String prvkey) throws JSchException {
/* 367 */     addIdentity(prvkey, (byte[])null);
/*     */   }
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
/*     */   public void addIdentity(String prvkey, String passphrase) throws JSchException {
/* 384 */     byte[] _passphrase = null;
/* 385 */     if (passphrase != null) {
/* 386 */       _passphrase = Util.str2byte(passphrase);
/*     */     }
/* 388 */     addIdentity(prvkey, _passphrase);
/* 389 */     if (_passphrase != null) {
/* 390 */       Util.bzero(_passphrase);
/*     */     }
/*     */   }
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
/*     */   public void addIdentity(String prvkey, byte[] passphrase) throws JSchException {
/* 407 */     Identity identity = IdentityFile.newInstance(prvkey, null, this);
/* 408 */     addIdentity(identity, passphrase);
/*     */   }
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
/*     */   public void addIdentity(String prvkey, String pubkey, byte[] passphrase) throws JSchException {
/* 424 */     Identity identity = IdentityFile.newInstance(prvkey, pubkey, this);
/* 425 */     addIdentity(identity, passphrase);
/*     */   }
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
/*     */   public void addIdentity(String name, byte[] prvkey, byte[] pubkey, byte[] passphrase) throws JSchException {
/* 442 */     Identity identity = IdentityFile.newInstance(name, prvkey, pubkey, this);
/* 443 */     addIdentity(identity, passphrase);
/*     */   }
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
/*     */   public void addIdentity(Identity identity, byte[] passphrase) throws JSchException {
/* 458 */     if (passphrase != null) {
/*     */       try {
/* 460 */         byte[] goo = new byte[passphrase.length];
/* 461 */         System.arraycopy(passphrase, 0, goo, 0, passphrase.length);
/* 462 */         passphrase = goo;
/* 463 */         identity.setPassphrase(passphrase);
/*     */       } finally {
/*     */         
/* 466 */         Util.bzero(passphrase);
/*     */       } 
/*     */     }
/*     */     
/* 470 */     if (this.identityRepository instanceof LocalIdentityRepository) {
/* 471 */       ((LocalIdentityRepository)this.identityRepository).add(identity);
/*     */     }
/* 473 */     else if (identity instanceof IdentityFile && !identity.isEncrypted()) {
/* 474 */       this.identityRepository.add(((IdentityFile)identity).getKeyPair().forSSHAgent());
/*     */     } else {
/*     */       
/* 477 */       synchronized (this) {
/* 478 */         if (!(this.identityRepository instanceof IdentityRepository.Wrapper)) {
/* 479 */           setIdentityRepository(new IdentityRepository.Wrapper(this.identityRepository));
/*     */         }
/*     */       } 
/* 482 */       ((IdentityRepository.Wrapper)this.identityRepository).add(identity);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeIdentity(String name) throws JSchException {
/* 490 */     Vector identities = this.identityRepository.getIdentities();
/* 491 */     for (int i = 0; i < identities.size(); i++) {
/* 492 */       Identity identity = identities.elementAt(i);
/* 493 */       if (identity.getName().equals(name))
/*     */       {
/* 495 */         if (this.identityRepository instanceof LocalIdentityRepository) {
/* 496 */           ((LocalIdentityRepository)this.identityRepository).remove(identity);
/*     */         } else {
/*     */           
/* 499 */           this.identityRepository.remove(identity.getPublicKeyBlob());
/*     */         } 
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeIdentity(Identity identity) throws JSchException {
/* 511 */     this.identityRepository.remove(identity.getPublicKeyBlob());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Vector getIdentityNames() throws JSchException {
/* 522 */     Vector foo = new Vector();
/* 523 */     Vector identities = this.identityRepository.getIdentities();
/* 524 */     for (int i = 0; i < identities.size(); i++) {
/* 525 */       Identity identity = identities.elementAt(i);
/* 526 */       foo.addElement(identity.getName());
/*     */     } 
/* 528 */     return foo;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeAllIdentity() throws JSchException {
/* 537 */     this.identityRepository.removeAll();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getConfig(String key) {
/* 547 */     synchronized (config) {
/* 548 */       return (String)config.get(key);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setConfig(Hashtable newconf) {
/* 558 */     synchronized (config) {
/* 559 */       for (Enumeration e = newconf.keys(); e.hasMoreElements(); ) {
/* 560 */         String key = e.nextElement();
/* 561 */         config.put(key, (String)newconf.get(key));
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setConfig(String key, String value) {
/* 573 */     config.put(key, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setLogger(Logger logger) {
/* 584 */     if (logger == null) logger = DEVNULL; 
/* 585 */     JSch.logger = logger;
/*     */   }
/*     */   
/*     */   static Logger getLogger() {
/* 589 */     return logger;
/*     */   }
/*     */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\JSch.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */