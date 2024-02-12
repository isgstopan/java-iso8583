/*      */ package com.jcraft.jsch;
/*      */ 
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.InterruptedIOException;
/*      */ import java.io.OutputStream;
/*      */ import java.net.Socket;
/*      */ import java.util.Arrays;
/*      */ import java.util.Enumeration;
/*      */ import java.util.Hashtable;
/*      */ import java.util.Properties;
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
/*      */ public class Session
/*      */   implements Runnable
/*      */ {
/*      */   static final int SSH_MSG_DISCONNECT = 1;
/*      */   static final int SSH_MSG_IGNORE = 2;
/*      */   static final int SSH_MSG_UNIMPLEMENTED = 3;
/*      */   static final int SSH_MSG_DEBUG = 4;
/*      */   static final int SSH_MSG_SERVICE_REQUEST = 5;
/*      */   static final int SSH_MSG_SERVICE_ACCEPT = 6;
/*      */   static final int SSH_MSG_KEXINIT = 20;
/*      */   static final int SSH_MSG_NEWKEYS = 21;
/*      */   static final int SSH_MSG_KEXDH_INIT = 30;
/*      */   static final int SSH_MSG_KEXDH_REPLY = 31;
/*      */   static final int SSH_MSG_KEX_DH_GEX_GROUP = 31;
/*      */   static final int SSH_MSG_KEX_DH_GEX_INIT = 32;
/*      */   static final int SSH_MSG_KEX_DH_GEX_REPLY = 33;
/*      */   static final int SSH_MSG_KEX_DH_GEX_REQUEST = 34;
/*      */   static final int SSH_MSG_GLOBAL_REQUEST = 80;
/*      */   static final int SSH_MSG_REQUEST_SUCCESS = 81;
/*      */   static final int SSH_MSG_REQUEST_FAILURE = 82;
/*      */   static final int SSH_MSG_CHANNEL_OPEN = 90;
/*      */   static final int SSH_MSG_CHANNEL_OPEN_CONFIRMATION = 91;
/*      */   static final int SSH_MSG_CHANNEL_OPEN_FAILURE = 92;
/*      */   static final int SSH_MSG_CHANNEL_WINDOW_ADJUST = 93;
/*      */   static final int SSH_MSG_CHANNEL_DATA = 94;
/*      */   static final int SSH_MSG_CHANNEL_EXTENDED_DATA = 95;
/*      */   static final int SSH_MSG_CHANNEL_EOF = 96;
/*      */   static final int SSH_MSG_CHANNEL_CLOSE = 97;
/*      */   static final int SSH_MSG_CHANNEL_REQUEST = 98;
/*      */   static final int SSH_MSG_CHANNEL_SUCCESS = 99;
/*      */   static final int SSH_MSG_CHANNEL_FAILURE = 100;
/*      */   private static final int PACKET_MAX_SIZE = 262144;
/*      */   private byte[] V_S;
/*   71 */   private byte[] V_C = Util.str2byte("SSH-2.0-JSCH-0.1.54");
/*      */   
/*      */   private byte[] I_C;
/*      */   
/*      */   private byte[] I_S;
/*      */   
/*      */   private byte[] K_S;
/*      */   
/*      */   private byte[] session_id;
/*      */   private byte[] IVc2s;
/*      */   private byte[] IVs2c;
/*      */   private byte[] Ec2s;
/*      */   private byte[] Es2c;
/*      */   private byte[] MACc2s;
/*      */   private byte[] MACs2c;
/*   86 */   private int seqi = 0;
/*   87 */   private int seqo = 0;
/*      */   
/*   89 */   String[] guess = null;
/*      */   
/*      */   private Cipher s2ccipher;
/*      */   
/*      */   private Cipher c2scipher;
/*      */   
/*      */   private MAC s2cmac;
/*      */   private MAC c2smac;
/*      */   private byte[] s2cmac_result1;
/*      */   private byte[] s2cmac_result2;
/*      */   private Compression deflater;
/*      */   private Compression inflater;
/*      */   private IO io;
/*      */   private Socket socket;
/*  103 */   private int timeout = 0;
/*      */   
/*      */   private volatile boolean isConnected = false;
/*      */   
/*      */   private boolean isAuthed = false;
/*      */   
/*  109 */   private Thread connectThread = null;
/*  110 */   private Object lock = new Object();
/*      */   
/*      */   boolean x11_forwarding = false;
/*      */   
/*      */   boolean agent_forwarding = false;
/*  115 */   InputStream in = null;
/*  116 */   OutputStream out = null;
/*      */   
/*      */   static Random random;
/*      */   
/*      */   Buffer buf;
/*      */   
/*      */   Packet packet;
/*  123 */   SocketFactory socket_factory = null;
/*      */ 
/*      */   
/*      */   static final int buffer_margin = 128;
/*      */ 
/*      */   
/*  129 */   private Hashtable config = null;
/*      */   
/*  131 */   private Proxy proxy = null;
/*      */   
/*      */   private UserInfo userinfo;
/*  134 */   private String hostKeyAlias = null;
/*  135 */   private int serverAliveInterval = 0;
/*  136 */   private int serverAliveCountMax = 1;
/*      */   
/*  138 */   private IdentityRepository identityRepository = null;
/*  139 */   private HostKeyRepository hostkeyRepository = null;
/*      */   
/*      */   protected boolean daemon_thread = false;
/*      */   
/*  143 */   private long kex_start_time = 0L;
/*      */   
/*  145 */   int max_auth_tries = 6;
/*  146 */   int auth_failures = 0;
/*      */   
/*  148 */   String host = "127.0.0.1";
/*  149 */   String org_host = "127.0.0.1";
/*  150 */   int port = 22;
/*      */   
/*  152 */   String username = null;
/*      */   JSch jsch; private volatile boolean in_kex; private volatile boolean in_prompt; int[] uncompress_len; int[] compress_len; private int s2ccipher_size; private int c2scipher_size; Runnable thread; private GlobalRequestReply grr; public void connect(int connectTimeout) throws JSchException { // Byte code:
/*      */     //   0: aload_0
/*      */     //   1: getfield isConnected : Z
/*      */     //   4: ifeq -> 17
/*      */     //   7: new com/jcraft/jsch/JSchException
/*      */     //   10: dup
/*      */     //   11: ldc 'session is already connected'
/*      */     //   13: invokespecial <init> : (Ljava/lang/String;)V
/*      */     //   16: athrow
/*      */     //   17: aload_0
/*      */     //   18: new com/jcraft/jsch/IO
/*      */     //   21: dup
/*      */     //   22: invokespecial <init> : ()V
/*      */     //   25: putfield io : Lcom/jcraft/jsch/IO;
/*      */     //   28: getstatic com/jcraft/jsch/Session.random : Lcom/jcraft/jsch/Random;
/*      */     //   31: ifnonnull -> 74
/*      */     //   34: aload_0
/*      */     //   35: ldc 'random'
/*      */     //   37: invokevirtual getConfig : (Ljava/lang/String;)Ljava/lang/String;
/*      */     //   40: invokestatic forName : (Ljava/lang/String;)Ljava/lang/Class;
/*      */     //   43: astore_2
/*      */     //   44: aload_2
/*      */     //   45: invokevirtual newInstance : ()Ljava/lang/Object;
/*      */     //   48: checkcast com/jcraft/jsch/Random
/*      */     //   51: checkcast com/jcraft/jsch/Random
/*      */     //   54: putstatic com/jcraft/jsch/Session.random : Lcom/jcraft/jsch/Random;
/*      */     //   57: goto -> 74
/*      */     //   60: astore_2
/*      */     //   61: new com/jcraft/jsch/JSchException
/*      */     //   64: dup
/*      */     //   65: aload_2
/*      */     //   66: invokevirtual toString : ()Ljava/lang/String;
/*      */     //   69: aload_2
/*      */     //   70: invokespecial <init> : (Ljava/lang/String;Ljava/lang/Throwable;)V
/*      */     //   73: athrow
/*      */     //   74: getstatic com/jcraft/jsch/Session.random : Lcom/jcraft/jsch/Random;
/*      */     //   77: invokestatic setRandom : (Lcom/jcraft/jsch/Random;)V
/*      */     //   80: invokestatic getLogger : ()Lcom/jcraft/jsch/Logger;
/*      */     //   83: iconst_1
/*      */     //   84: invokeinterface isEnabled : (I)Z
/*      */     //   89: ifeq -> 135
/*      */     //   92: invokestatic getLogger : ()Lcom/jcraft/jsch/Logger;
/*      */     //   95: iconst_1
/*      */     //   96: new java/lang/StringBuffer
/*      */     //   99: dup
/*      */     //   100: invokespecial <init> : ()V
/*      */     //   103: ldc 'Connecting to '
/*      */     //   105: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   108: aload_0
/*      */     //   109: getfield host : Ljava/lang/String;
/*      */     //   112: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   115: ldc ' port '
/*      */     //   117: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   120: aload_0
/*      */     //   121: getfield port : I
/*      */     //   124: invokevirtual append : (I)Ljava/lang/StringBuffer;
/*      */     //   127: invokevirtual toString : ()Ljava/lang/String;
/*      */     //   130: invokeinterface log : (ILjava/lang/String;)V
/*      */     //   135: aload_0
/*      */     //   136: getfield proxy : Lcom/jcraft/jsch/Proxy;
/*      */     //   139: ifnonnull -> 266
/*      */     //   142: aload_0
/*      */     //   143: getfield socket_factory : Lcom/jcraft/jsch/SocketFactory;
/*      */     //   146: ifnonnull -> 186
/*      */     //   149: aload_0
/*      */     //   150: aload_0
/*      */     //   151: getfield host : Ljava/lang/String;
/*      */     //   154: aload_0
/*      */     //   155: getfield port : I
/*      */     //   158: iload_1
/*      */     //   159: invokestatic createSocket : (Ljava/lang/String;II)Ljava/net/Socket;
/*      */     //   162: putfield socket : Ljava/net/Socket;
/*      */     //   165: aload_0
/*      */     //   166: getfield socket : Ljava/net/Socket;
/*      */     //   169: invokevirtual getInputStream : ()Ljava/io/InputStream;
/*      */     //   172: astore #4
/*      */     //   174: aload_0
/*      */     //   175: getfield socket : Ljava/net/Socket;
/*      */     //   178: invokevirtual getOutputStream : ()Ljava/io/OutputStream;
/*      */     //   181: astore #5
/*      */     //   183: goto -> 237
/*      */     //   186: aload_0
/*      */     //   187: aload_0
/*      */     //   188: getfield socket_factory : Lcom/jcraft/jsch/SocketFactory;
/*      */     //   191: aload_0
/*      */     //   192: getfield host : Ljava/lang/String;
/*      */     //   195: aload_0
/*      */     //   196: getfield port : I
/*      */     //   199: invokeinterface createSocket : (Ljava/lang/String;I)Ljava/net/Socket;
/*      */     //   204: putfield socket : Ljava/net/Socket;
/*      */     //   207: aload_0
/*      */     //   208: getfield socket_factory : Lcom/jcraft/jsch/SocketFactory;
/*      */     //   211: aload_0
/*      */     //   212: getfield socket : Ljava/net/Socket;
/*      */     //   215: invokeinterface getInputStream : (Ljava/net/Socket;)Ljava/io/InputStream;
/*      */     //   220: astore #4
/*      */     //   222: aload_0
/*      */     //   223: getfield socket_factory : Lcom/jcraft/jsch/SocketFactory;
/*      */     //   226: aload_0
/*      */     //   227: getfield socket : Ljava/net/Socket;
/*      */     //   230: invokeinterface getOutputStream : (Ljava/net/Socket;)Ljava/io/OutputStream;
/*      */     //   235: astore #5
/*      */     //   237: aload_0
/*      */     //   238: getfield socket : Ljava/net/Socket;
/*      */     //   241: iconst_1
/*      */     //   242: invokevirtual setTcpNoDelay : (Z)V
/*      */     //   245: aload_0
/*      */     //   246: getfield io : Lcom/jcraft/jsch/IO;
/*      */     //   249: aload #4
/*      */     //   251: invokevirtual setInputStream : (Ljava/io/InputStream;)V
/*      */     //   254: aload_0
/*      */     //   255: getfield io : Lcom/jcraft/jsch/IO;
/*      */     //   258: aload #5
/*      */     //   260: invokevirtual setOutputStream : (Ljava/io/OutputStream;)V
/*      */     //   263: goto -> 355
/*      */     //   266: aload_0
/*      */     //   267: getfield proxy : Lcom/jcraft/jsch/Proxy;
/*      */     //   270: dup
/*      */     //   271: astore #4
/*      */     //   273: monitorenter
/*      */     //   274: aload_0
/*      */     //   275: getfield proxy : Lcom/jcraft/jsch/Proxy;
/*      */     //   278: aload_0
/*      */     //   279: getfield socket_factory : Lcom/jcraft/jsch/SocketFactory;
/*      */     //   282: aload_0
/*      */     //   283: getfield host : Ljava/lang/String;
/*      */     //   286: aload_0
/*      */     //   287: getfield port : I
/*      */     //   290: iload_1
/*      */     //   291: invokeinterface connect : (Lcom/jcraft/jsch/SocketFactory;Ljava/lang/String;II)V
/*      */     //   296: aload_0
/*      */     //   297: getfield io : Lcom/jcraft/jsch/IO;
/*      */     //   300: aload_0
/*      */     //   301: getfield proxy : Lcom/jcraft/jsch/Proxy;
/*      */     //   304: invokeinterface getInputStream : ()Ljava/io/InputStream;
/*      */     //   309: invokevirtual setInputStream : (Ljava/io/InputStream;)V
/*      */     //   312: aload_0
/*      */     //   313: getfield io : Lcom/jcraft/jsch/IO;
/*      */     //   316: aload_0
/*      */     //   317: getfield proxy : Lcom/jcraft/jsch/Proxy;
/*      */     //   320: invokeinterface getOutputStream : ()Ljava/io/OutputStream;
/*      */     //   325: invokevirtual setOutputStream : (Ljava/io/OutputStream;)V
/*      */     //   328: aload_0
/*      */     //   329: aload_0
/*      */     //   330: getfield proxy : Lcom/jcraft/jsch/Proxy;
/*      */     //   333: invokeinterface getSocket : ()Ljava/net/Socket;
/*      */     //   338: putfield socket : Ljava/net/Socket;
/*      */     //   341: aload #4
/*      */     //   343: monitorexit
/*      */     //   344: goto -> 355
/*      */     //   347: astore #6
/*      */     //   349: aload #4
/*      */     //   351: monitorexit
/*      */     //   352: aload #6
/*      */     //   354: athrow
/*      */     //   355: iload_1
/*      */     //   356: ifle -> 374
/*      */     //   359: aload_0
/*      */     //   360: getfield socket : Ljava/net/Socket;
/*      */     //   363: ifnull -> 374
/*      */     //   366: aload_0
/*      */     //   367: getfield socket : Ljava/net/Socket;
/*      */     //   370: iload_1
/*      */     //   371: invokevirtual setSoTimeout : (I)V
/*      */     //   374: aload_0
/*      */     //   375: iconst_1
/*      */     //   376: putfield isConnected : Z
/*      */     //   379: invokestatic getLogger : ()Lcom/jcraft/jsch/Logger;
/*      */     //   382: iconst_1
/*      */     //   383: invokeinterface isEnabled : (I)Z
/*      */     //   388: ifeq -> 402
/*      */     //   391: invokestatic getLogger : ()Lcom/jcraft/jsch/Logger;
/*      */     //   394: iconst_1
/*      */     //   395: ldc 'Connection established'
/*      */     //   397: invokeinterface log : (ILjava/lang/String;)V
/*      */     //   402: aload_0
/*      */     //   403: getfield jsch : Lcom/jcraft/jsch/JSch;
/*      */     //   406: aload_0
/*      */     //   407: invokevirtual addSession : (Lcom/jcraft/jsch/Session;)V
/*      */     //   410: aload_0
/*      */     //   411: getfield V_C : [B
/*      */     //   414: arraylength
/*      */     //   415: iconst_1
/*      */     //   416: iadd
/*      */     //   417: newarray byte
/*      */     //   419: astore #4
/*      */     //   421: aload_0
/*      */     //   422: getfield V_C : [B
/*      */     //   425: iconst_0
/*      */     //   426: aload #4
/*      */     //   428: iconst_0
/*      */     //   429: aload_0
/*      */     //   430: getfield V_C : [B
/*      */     //   433: arraylength
/*      */     //   434: invokestatic arraycopy : (Ljava/lang/Object;ILjava/lang/Object;II)V
/*      */     //   437: aload #4
/*      */     //   439: aload #4
/*      */     //   441: arraylength
/*      */     //   442: iconst_1
/*      */     //   443: isub
/*      */     //   444: bipush #10
/*      */     //   446: bastore
/*      */     //   447: aload_0
/*      */     //   448: getfield io : Lcom/jcraft/jsch/IO;
/*      */     //   451: aload #4
/*      */     //   453: iconst_0
/*      */     //   454: aload #4
/*      */     //   456: arraylength
/*      */     //   457: invokevirtual put : ([BII)V
/*      */     //   460: iconst_0
/*      */     //   461: istore_2
/*      */     //   462: iconst_0
/*      */     //   463: istore_3
/*      */     //   464: iload_2
/*      */     //   465: aload_0
/*      */     //   466: getfield buf : Lcom/jcraft/jsch/Buffer;
/*      */     //   469: getfield buffer : [B
/*      */     //   472: arraylength
/*      */     //   473: if_icmpge -> 514
/*      */     //   476: aload_0
/*      */     //   477: getfield io : Lcom/jcraft/jsch/IO;
/*      */     //   480: invokevirtual getByte : ()I
/*      */     //   483: istore_3
/*      */     //   484: iload_3
/*      */     //   485: ifge -> 491
/*      */     //   488: goto -> 514
/*      */     //   491: aload_0
/*      */     //   492: getfield buf : Lcom/jcraft/jsch/Buffer;
/*      */     //   495: getfield buffer : [B
/*      */     //   498: iload_2
/*      */     //   499: iload_3
/*      */     //   500: i2b
/*      */     //   501: bastore
/*      */     //   502: iinc #2, 1
/*      */     //   505: iload_3
/*      */     //   506: bipush #10
/*      */     //   508: if_icmpne -> 464
/*      */     //   511: goto -> 514
/*      */     //   514: iload_3
/*      */     //   515: ifge -> 528
/*      */     //   518: new com/jcraft/jsch/JSchException
/*      */     //   521: dup
/*      */     //   522: ldc 'connection is closed by foreign host'
/*      */     //   524: invokespecial <init> : (Ljava/lang/String;)V
/*      */     //   527: athrow
/*      */     //   528: aload_0
/*      */     //   529: getfield buf : Lcom/jcraft/jsch/Buffer;
/*      */     //   532: getfield buffer : [B
/*      */     //   535: iload_2
/*      */     //   536: iconst_1
/*      */     //   537: isub
/*      */     //   538: baload
/*      */     //   539: bipush #10
/*      */     //   541: if_icmpne -> 570
/*      */     //   544: iinc #2, -1
/*      */     //   547: iload_2
/*      */     //   548: ifle -> 570
/*      */     //   551: aload_0
/*      */     //   552: getfield buf : Lcom/jcraft/jsch/Buffer;
/*      */     //   555: getfield buffer : [B
/*      */     //   558: iload_2
/*      */     //   559: iconst_1
/*      */     //   560: isub
/*      */     //   561: baload
/*      */     //   562: bipush #13
/*      */     //   564: if_icmpne -> 570
/*      */     //   567: iinc #2, -1
/*      */     //   570: iload_2
/*      */     //   571: iconst_3
/*      */     //   572: if_icmple -> 460
/*      */     //   575: iload_2
/*      */     //   576: aload_0
/*      */     //   577: getfield buf : Lcom/jcraft/jsch/Buffer;
/*      */     //   580: getfield buffer : [B
/*      */     //   583: arraylength
/*      */     //   584: if_icmpeq -> 646
/*      */     //   587: aload_0
/*      */     //   588: getfield buf : Lcom/jcraft/jsch/Buffer;
/*      */     //   591: getfield buffer : [B
/*      */     //   594: iconst_0
/*      */     //   595: baload
/*      */     //   596: bipush #83
/*      */     //   598: if_icmpne -> 460
/*      */     //   601: aload_0
/*      */     //   602: getfield buf : Lcom/jcraft/jsch/Buffer;
/*      */     //   605: getfield buffer : [B
/*      */     //   608: iconst_1
/*      */     //   609: baload
/*      */     //   610: bipush #83
/*      */     //   612: if_icmpne -> 460
/*      */     //   615: aload_0
/*      */     //   616: getfield buf : Lcom/jcraft/jsch/Buffer;
/*      */     //   619: getfield buffer : [B
/*      */     //   622: iconst_2
/*      */     //   623: baload
/*      */     //   624: bipush #72
/*      */     //   626: if_icmpne -> 460
/*      */     //   629: aload_0
/*      */     //   630: getfield buf : Lcom/jcraft/jsch/Buffer;
/*      */     //   633: getfield buffer : [B
/*      */     //   636: iconst_3
/*      */     //   637: baload
/*      */     //   638: bipush #45
/*      */     //   640: if_icmpeq -> 646
/*      */     //   643: goto -> 460
/*      */     //   646: iload_2
/*      */     //   647: aload_0
/*      */     //   648: getfield buf : Lcom/jcraft/jsch/Buffer;
/*      */     //   651: getfield buffer : [B
/*      */     //   654: arraylength
/*      */     //   655: if_icmpeq -> 693
/*      */     //   658: iload_2
/*      */     //   659: bipush #7
/*      */     //   661: if_icmplt -> 693
/*      */     //   664: aload_0
/*      */     //   665: getfield buf : Lcom/jcraft/jsch/Buffer;
/*      */     //   668: getfield buffer : [B
/*      */     //   671: iconst_4
/*      */     //   672: baload
/*      */     //   673: bipush #49
/*      */     //   675: if_icmpne -> 703
/*      */     //   678: aload_0
/*      */     //   679: getfield buf : Lcom/jcraft/jsch/Buffer;
/*      */     //   682: getfield buffer : [B
/*      */     //   685: bipush #6
/*      */     //   687: baload
/*      */     //   688: bipush #57
/*      */     //   690: if_icmpeq -> 703
/*      */     //   693: new com/jcraft/jsch/JSchException
/*      */     //   696: dup
/*      */     //   697: ldc 'invalid server's version string'
/*      */     //   699: invokespecial <init> : (Ljava/lang/String;)V
/*      */     //   702: athrow
/*      */     //   703: aload_0
/*      */     //   704: iload_2
/*      */     //   705: newarray byte
/*      */     //   707: putfield V_S : [B
/*      */     //   710: aload_0
/*      */     //   711: getfield buf : Lcom/jcraft/jsch/Buffer;
/*      */     //   714: getfield buffer : [B
/*      */     //   717: iconst_0
/*      */     //   718: aload_0
/*      */     //   719: getfield V_S : [B
/*      */     //   722: iconst_0
/*      */     //   723: iload_2
/*      */     //   724: invokestatic arraycopy : (Ljava/lang/Object;ILjava/lang/Object;II)V
/*      */     //   727: invokestatic getLogger : ()Lcom/jcraft/jsch/Logger;
/*      */     //   730: iconst_1
/*      */     //   731: invokeinterface isEnabled : (I)Z
/*      */     //   736: ifeq -> 807
/*      */     //   739: invokestatic getLogger : ()Lcom/jcraft/jsch/Logger;
/*      */     //   742: iconst_1
/*      */     //   743: new java/lang/StringBuffer
/*      */     //   746: dup
/*      */     //   747: invokespecial <init> : ()V
/*      */     //   750: ldc 'Remote version string: '
/*      */     //   752: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   755: aload_0
/*      */     //   756: getfield V_S : [B
/*      */     //   759: invokestatic byte2str : ([B)Ljava/lang/String;
/*      */     //   762: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   765: invokevirtual toString : ()Ljava/lang/String;
/*      */     //   768: invokeinterface log : (ILjava/lang/String;)V
/*      */     //   773: invokestatic getLogger : ()Lcom/jcraft/jsch/Logger;
/*      */     //   776: iconst_1
/*      */     //   777: new java/lang/StringBuffer
/*      */     //   780: dup
/*      */     //   781: invokespecial <init> : ()V
/*      */     //   784: ldc 'Local version string: '
/*      */     //   786: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   789: aload_0
/*      */     //   790: getfield V_C : [B
/*      */     //   793: invokestatic byte2str : ([B)Ljava/lang/String;
/*      */     //   796: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   799: invokevirtual toString : ()Ljava/lang/String;
/*      */     //   802: invokeinterface log : (ILjava/lang/String;)V
/*      */     //   807: aload_0
/*      */     //   808: invokespecial send_kexinit : ()V
/*      */     //   811: aload_0
/*      */     //   812: aload_0
/*      */     //   813: aload_0
/*      */     //   814: getfield buf : Lcom/jcraft/jsch/Buffer;
/*      */     //   817: invokevirtual read : (Lcom/jcraft/jsch/Buffer;)Lcom/jcraft/jsch/Buffer;
/*      */     //   820: putfield buf : Lcom/jcraft/jsch/Buffer;
/*      */     //   823: aload_0
/*      */     //   824: getfield buf : Lcom/jcraft/jsch/Buffer;
/*      */     //   827: invokevirtual getCommand : ()B
/*      */     //   830: bipush #20
/*      */     //   832: if_icmpeq -> 873
/*      */     //   835: aload_0
/*      */     //   836: iconst_0
/*      */     //   837: putfield in_kex : Z
/*      */     //   840: new com/jcraft/jsch/JSchException
/*      */     //   843: dup
/*      */     //   844: new java/lang/StringBuffer
/*      */     //   847: dup
/*      */     //   848: invokespecial <init> : ()V
/*      */     //   851: ldc 'invalid protocol: '
/*      */     //   853: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   856: aload_0
/*      */     //   857: getfield buf : Lcom/jcraft/jsch/Buffer;
/*      */     //   860: invokevirtual getCommand : ()B
/*      */     //   863: invokevirtual append : (I)Ljava/lang/StringBuffer;
/*      */     //   866: invokevirtual toString : ()Ljava/lang/String;
/*      */     //   869: invokespecial <init> : (Ljava/lang/String;)V
/*      */     //   872: athrow
/*      */     //   873: invokestatic getLogger : ()Lcom/jcraft/jsch/Logger;
/*      */     //   876: iconst_1
/*      */     //   877: invokeinterface isEnabled : (I)Z
/*      */     //   882: ifeq -> 896
/*      */     //   885: invokestatic getLogger : ()Lcom/jcraft/jsch/Logger;
/*      */     //   888: iconst_1
/*      */     //   889: ldc 'SSH_MSG_KEXINIT received'
/*      */     //   891: invokeinterface log : (ILjava/lang/String;)V
/*      */     //   896: aload_0
/*      */     //   897: aload_0
/*      */     //   898: getfield buf : Lcom/jcraft/jsch/Buffer;
/*      */     //   901: invokespecial receive_kexinit : (Lcom/jcraft/jsch/Buffer;)Lcom/jcraft/jsch/KeyExchange;
/*      */     //   904: astore #4
/*      */     //   906: aload_0
/*      */     //   907: aload_0
/*      */     //   908: aload_0
/*      */     //   909: getfield buf : Lcom/jcraft/jsch/Buffer;
/*      */     //   912: invokevirtual read : (Lcom/jcraft/jsch/Buffer;)Lcom/jcraft/jsch/Buffer;
/*      */     //   915: putfield buf : Lcom/jcraft/jsch/Buffer;
/*      */     //   918: aload #4
/*      */     //   920: invokevirtual getState : ()I
/*      */     //   923: aload_0
/*      */     //   924: getfield buf : Lcom/jcraft/jsch/Buffer;
/*      */     //   927: invokevirtual getCommand : ()B
/*      */     //   930: if_icmpne -> 992
/*      */     //   933: aload_0
/*      */     //   934: invokestatic currentTimeMillis : ()J
/*      */     //   937: putfield kex_start_time : J
/*      */     //   940: aload #4
/*      */     //   942: aload_0
/*      */     //   943: getfield buf : Lcom/jcraft/jsch/Buffer;
/*      */     //   946: invokevirtual next : (Lcom/jcraft/jsch/Buffer;)Z
/*      */     //   949: istore #5
/*      */     //   951: iload #5
/*      */     //   953: ifne -> 989
/*      */     //   956: aload_0
/*      */     //   957: iconst_0
/*      */     //   958: putfield in_kex : Z
/*      */     //   961: new com/jcraft/jsch/JSchException
/*      */     //   964: dup
/*      */     //   965: new java/lang/StringBuffer
/*      */     //   968: dup
/*      */     //   969: invokespecial <init> : ()V
/*      */     //   972: ldc 'verify: '
/*      */     //   974: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   977: iload #5
/*      */     //   979: invokevirtual append : (Z)Ljava/lang/StringBuffer;
/*      */     //   982: invokevirtual toString : ()Ljava/lang/String;
/*      */     //   985: invokespecial <init> : (Ljava/lang/String;)V
/*      */     //   988: athrow
/*      */     //   989: goto -> 1030
/*      */     //   992: aload_0
/*      */     //   993: iconst_0
/*      */     //   994: putfield in_kex : Z
/*      */     //   997: new com/jcraft/jsch/JSchException
/*      */     //   1000: dup
/*      */     //   1001: new java/lang/StringBuffer
/*      */     //   1004: dup
/*      */     //   1005: invokespecial <init> : ()V
/*      */     //   1008: ldc 'invalid protocol(kex): '
/*      */     //   1010: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   1013: aload_0
/*      */     //   1014: getfield buf : Lcom/jcraft/jsch/Buffer;
/*      */     //   1017: invokevirtual getCommand : ()B
/*      */     //   1020: invokevirtual append : (I)Ljava/lang/StringBuffer;
/*      */     //   1023: invokevirtual toString : ()Ljava/lang/String;
/*      */     //   1026: invokespecial <init> : (Ljava/lang/String;)V
/*      */     //   1029: athrow
/*      */     //   1030: aload #4
/*      */     //   1032: invokevirtual getState : ()I
/*      */     //   1035: ifne -> 906
/*      */     //   1038: goto -> 1041
/*      */     //   1041: invokestatic currentTimeMillis : ()J
/*      */     //   1044: lstore #5
/*      */     //   1046: aload_0
/*      */     //   1047: iconst_1
/*      */     //   1048: putfield in_prompt : Z
/*      */     //   1051: aload_0
/*      */     //   1052: aload_0
/*      */     //   1053: getfield host : Ljava/lang/String;
/*      */     //   1056: aload_0
/*      */     //   1057: getfield port : I
/*      */     //   1060: aload #4
/*      */     //   1062: invokespecial checkHost : (Ljava/lang/String;ILcom/jcraft/jsch/KeyExchange;)V
/*      */     //   1065: aload_0
/*      */     //   1066: iconst_0
/*      */     //   1067: putfield in_prompt : Z
/*      */     //   1070: aload_0
/*      */     //   1071: dup
/*      */     //   1072: getfield kex_start_time : J
/*      */     //   1075: invokestatic currentTimeMillis : ()J
/*      */     //   1078: lload #5
/*      */     //   1080: lsub
/*      */     //   1081: ladd
/*      */     //   1082: putfield kex_start_time : J
/*      */     //   1085: goto -> 1103
/*      */     //   1088: astore #5
/*      */     //   1090: aload_0
/*      */     //   1091: iconst_0
/*      */     //   1092: putfield in_kex : Z
/*      */     //   1095: aload_0
/*      */     //   1096: iconst_0
/*      */     //   1097: putfield in_prompt : Z
/*      */     //   1100: aload #5
/*      */     //   1102: athrow
/*      */     //   1103: aload_0
/*      */     //   1104: invokespecial send_newkeys : ()V
/*      */     //   1107: aload_0
/*      */     //   1108: aload_0
/*      */     //   1109: aload_0
/*      */     //   1110: getfield buf : Lcom/jcraft/jsch/Buffer;
/*      */     //   1113: invokevirtual read : (Lcom/jcraft/jsch/Buffer;)Lcom/jcraft/jsch/Buffer;
/*      */     //   1116: putfield buf : Lcom/jcraft/jsch/Buffer;
/*      */     //   1119: aload_0
/*      */     //   1120: getfield buf : Lcom/jcraft/jsch/Buffer;
/*      */     //   1123: invokevirtual getCommand : ()B
/*      */     //   1126: bipush #21
/*      */     //   1128: if_icmpne -> 1167
/*      */     //   1131: invokestatic getLogger : ()Lcom/jcraft/jsch/Logger;
/*      */     //   1134: iconst_1
/*      */     //   1135: invokeinterface isEnabled : (I)Z
/*      */     //   1140: ifeq -> 1154
/*      */     //   1143: invokestatic getLogger : ()Lcom/jcraft/jsch/Logger;
/*      */     //   1146: iconst_1
/*      */     //   1147: ldc 'SSH_MSG_NEWKEYS received'
/*      */     //   1149: invokeinterface log : (ILjava/lang/String;)V
/*      */     //   1154: aload_0
/*      */     //   1155: aload_0
/*      */     //   1156: getfield buf : Lcom/jcraft/jsch/Buffer;
/*      */     //   1159: aload #4
/*      */     //   1161: invokespecial receive_newkeys : (Lcom/jcraft/jsch/Buffer;Lcom/jcraft/jsch/KeyExchange;)V
/*      */     //   1164: goto -> 1205
/*      */     //   1167: aload_0
/*      */     //   1168: iconst_0
/*      */     //   1169: putfield in_kex : Z
/*      */     //   1172: new com/jcraft/jsch/JSchException
/*      */     //   1175: dup
/*      */     //   1176: new java/lang/StringBuffer
/*      */     //   1179: dup
/*      */     //   1180: invokespecial <init> : ()V
/*      */     //   1183: ldc 'invalid protocol(newkyes): '
/*      */     //   1185: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   1188: aload_0
/*      */     //   1189: getfield buf : Lcom/jcraft/jsch/Buffer;
/*      */     //   1192: invokevirtual getCommand : ()B
/*      */     //   1195: invokevirtual append : (I)Ljava/lang/StringBuffer;
/*      */     //   1198: invokevirtual toString : ()Ljava/lang/String;
/*      */     //   1201: invokespecial <init> : (Ljava/lang/String;)V
/*      */     //   1204: athrow
/*      */     //   1205: aload_0
/*      */     //   1206: ldc 'MaxAuthTries'
/*      */     //   1208: invokevirtual getConfig : (Ljava/lang/String;)Ljava/lang/String;
/*      */     //   1211: astore #5
/*      */     //   1213: aload #5
/*      */     //   1215: ifnull -> 1227
/*      */     //   1218: aload_0
/*      */     //   1219: aload #5
/*      */     //   1221: invokestatic parseInt : (Ljava/lang/String;)I
/*      */     //   1224: putfield max_auth_tries : I
/*      */     //   1227: goto -> 1266
/*      */     //   1230: astore #5
/*      */     //   1232: new com/jcraft/jsch/JSchException
/*      */     //   1235: dup
/*      */     //   1236: new java/lang/StringBuffer
/*      */     //   1239: dup
/*      */     //   1240: invokespecial <init> : ()V
/*      */     //   1243: ldc 'MaxAuthTries: '
/*      */     //   1245: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   1248: aload_0
/*      */     //   1249: ldc 'MaxAuthTries'
/*      */     //   1251: invokevirtual getConfig : (Ljava/lang/String;)Ljava/lang/String;
/*      */     //   1254: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   1257: invokevirtual toString : ()Ljava/lang/String;
/*      */     //   1260: aload #5
/*      */     //   1262: invokespecial <init> : (Ljava/lang/String;Ljava/lang/Throwable;)V
/*      */     //   1265: athrow
/*      */     //   1266: iconst_0
/*      */     //   1267: istore #5
/*      */     //   1269: iconst_0
/*      */     //   1270: istore #6
/*      */     //   1272: aconst_null
/*      */     //   1273: astore #7
/*      */     //   1275: aload_0
/*      */     //   1276: ldc 'userauth.none'
/*      */     //   1278: invokevirtual getConfig : (Ljava/lang/String;)Ljava/lang/String;
/*      */     //   1281: invokestatic forName : (Ljava/lang/String;)Ljava/lang/Class;
/*      */     //   1284: astore #8
/*      */     //   1286: aload #8
/*      */     //   1288: invokevirtual newInstance : ()Ljava/lang/Object;
/*      */     //   1291: checkcast com/jcraft/jsch/UserAuth
/*      */     //   1294: checkcast com/jcraft/jsch/UserAuth
/*      */     //   1297: astore #7
/*      */     //   1299: goto -> 1319
/*      */     //   1302: astore #8
/*      */     //   1304: new com/jcraft/jsch/JSchException
/*      */     //   1307: dup
/*      */     //   1308: aload #8
/*      */     //   1310: invokevirtual toString : ()Ljava/lang/String;
/*      */     //   1313: aload #8
/*      */     //   1315: invokespecial <init> : (Ljava/lang/String;Ljava/lang/Throwable;)V
/*      */     //   1318: athrow
/*      */     //   1319: aload #7
/*      */     //   1321: aload_0
/*      */     //   1322: invokevirtual start : (Lcom/jcraft/jsch/Session;)Z
/*      */     //   1325: istore #5
/*      */     //   1327: aload_0
/*      */     //   1328: ldc 'PreferredAuthentications'
/*      */     //   1330: invokevirtual getConfig : (Ljava/lang/String;)Ljava/lang/String;
/*      */     //   1333: astore #8
/*      */     //   1335: aload #8
/*      */     //   1337: ldc ','
/*      */     //   1339: invokestatic split : (Ljava/lang/String;Ljava/lang/String;)[Ljava/lang/String;
/*      */     //   1342: astore #9
/*      */     //   1344: aconst_null
/*      */     //   1345: astore #10
/*      */     //   1347: iload #5
/*      */     //   1349: ifne -> 1381
/*      */     //   1352: aload #7
/*      */     //   1354: checkcast com/jcraft/jsch/UserAuthNone
/*      */     //   1357: invokevirtual getMethods : ()Ljava/lang/String;
/*      */     //   1360: astore #10
/*      */     //   1362: aload #10
/*      */     //   1364: ifnull -> 1377
/*      */     //   1367: aload #10
/*      */     //   1369: invokevirtual toLowerCase : ()Ljava/lang/String;
/*      */     //   1372: astore #10
/*      */     //   1374: goto -> 1381
/*      */     //   1377: aload #8
/*      */     //   1379: astore #10
/*      */     //   1381: aload #10
/*      */     //   1383: ldc ','
/*      */     //   1385: invokestatic split : (Ljava/lang/String;Ljava/lang/String;)[Ljava/lang/String;
/*      */     //   1388: astore #11
/*      */     //   1390: iconst_0
/*      */     //   1391: istore #12
/*      */     //   1393: iload #5
/*      */     //   1395: ifne -> 1908
/*      */     //   1398: aload #9
/*      */     //   1400: ifnull -> 1908
/*      */     //   1403: iload #12
/*      */     //   1405: aload #9
/*      */     //   1407: arraylength
/*      */     //   1408: if_icmpge -> 1908
/*      */     //   1411: aload #9
/*      */     //   1413: iload #12
/*      */     //   1415: iinc #12, 1
/*      */     //   1418: aaload
/*      */     //   1419: astore #13
/*      */     //   1421: iconst_0
/*      */     //   1422: istore #14
/*      */     //   1424: iconst_0
/*      */     //   1425: istore #15
/*      */     //   1427: iload #15
/*      */     //   1429: aload #11
/*      */     //   1431: arraylength
/*      */     //   1432: if_icmpge -> 1460
/*      */     //   1435: aload #11
/*      */     //   1437: iload #15
/*      */     //   1439: aaload
/*      */     //   1440: aload #13
/*      */     //   1442: invokevirtual equals : (Ljava/lang/Object;)Z
/*      */     //   1445: ifeq -> 1454
/*      */     //   1448: iconst_1
/*      */     //   1449: istore #14
/*      */     //   1451: goto -> 1460
/*      */     //   1454: iinc #15, 1
/*      */     //   1457: goto -> 1427
/*      */     //   1460: iload #14
/*      */     //   1462: ifne -> 1468
/*      */     //   1465: goto -> 1393
/*      */     //   1468: invokestatic getLogger : ()Lcom/jcraft/jsch/Logger;
/*      */     //   1471: iconst_1
/*      */     //   1472: invokeinterface isEnabled : (I)Z
/*      */     //   1477: ifeq -> 1601
/*      */     //   1480: ldc 'Authentications that can continue: '
/*      */     //   1482: astore #15
/*      */     //   1484: iload #12
/*      */     //   1486: iconst_1
/*      */     //   1487: isub
/*      */     //   1488: istore #16
/*      */     //   1490: iload #16
/*      */     //   1492: aload #9
/*      */     //   1494: arraylength
/*      */     //   1495: if_icmpge -> 1561
/*      */     //   1498: new java/lang/StringBuffer
/*      */     //   1501: dup
/*      */     //   1502: invokespecial <init> : ()V
/*      */     //   1505: aload #15
/*      */     //   1507: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   1510: aload #9
/*      */     //   1512: iload #16
/*      */     //   1514: aaload
/*      */     //   1515: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   1518: invokevirtual toString : ()Ljava/lang/String;
/*      */     //   1521: astore #15
/*      */     //   1523: iload #16
/*      */     //   1525: iconst_1
/*      */     //   1526: iadd
/*      */     //   1527: aload #9
/*      */     //   1529: arraylength
/*      */     //   1530: if_icmpge -> 1555
/*      */     //   1533: new java/lang/StringBuffer
/*      */     //   1536: dup
/*      */     //   1537: invokespecial <init> : ()V
/*      */     //   1540: aload #15
/*      */     //   1542: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   1545: ldc ','
/*      */     //   1547: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   1550: invokevirtual toString : ()Ljava/lang/String;
/*      */     //   1553: astore #15
/*      */     //   1555: iinc #16, 1
/*      */     //   1558: goto -> 1490
/*      */     //   1561: invokestatic getLogger : ()Lcom/jcraft/jsch/Logger;
/*      */     //   1564: iconst_1
/*      */     //   1565: aload #15
/*      */     //   1567: invokeinterface log : (ILjava/lang/String;)V
/*      */     //   1572: invokestatic getLogger : ()Lcom/jcraft/jsch/Logger;
/*      */     //   1575: iconst_1
/*      */     //   1576: new java/lang/StringBuffer
/*      */     //   1579: dup
/*      */     //   1580: invokespecial <init> : ()V
/*      */     //   1583: ldc 'Next authentication method: '
/*      */     //   1585: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   1588: aload #13
/*      */     //   1590: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   1593: invokevirtual toString : ()Ljava/lang/String;
/*      */     //   1596: invokeinterface log : (ILjava/lang/String;)V
/*      */     //   1601: aconst_null
/*      */     //   1602: astore #7
/*      */     //   1604: aconst_null
/*      */     //   1605: astore #15
/*      */     //   1607: aload_0
/*      */     //   1608: new java/lang/StringBuffer
/*      */     //   1611: dup
/*      */     //   1612: invokespecial <init> : ()V
/*      */     //   1615: ldc 'userauth.'
/*      */     //   1617: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   1620: aload #13
/*      */     //   1622: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   1625: invokevirtual toString : ()Ljava/lang/String;
/*      */     //   1628: invokevirtual getConfig : (Ljava/lang/String;)Ljava/lang/String;
/*      */     //   1631: ifnull -> 1676
/*      */     //   1634: aload_0
/*      */     //   1635: new java/lang/StringBuffer
/*      */     //   1638: dup
/*      */     //   1639: invokespecial <init> : ()V
/*      */     //   1642: ldc 'userauth.'
/*      */     //   1644: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   1647: aload #13
/*      */     //   1649: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   1652: invokevirtual toString : ()Ljava/lang/String;
/*      */     //   1655: invokevirtual getConfig : (Ljava/lang/String;)Ljava/lang/String;
/*      */     //   1658: invokestatic forName : (Ljava/lang/String;)Ljava/lang/Class;
/*      */     //   1661: astore #15
/*      */     //   1663: aload #15
/*      */     //   1665: invokevirtual newInstance : ()Ljava/lang/Object;
/*      */     //   1668: checkcast com/jcraft/jsch/UserAuth
/*      */     //   1671: checkcast com/jcraft/jsch/UserAuth
/*      */     //   1674: astore #7
/*      */     //   1676: goto -> 1727
/*      */     //   1679: astore #15
/*      */     //   1681: invokestatic getLogger : ()Lcom/jcraft/jsch/Logger;
/*      */     //   1684: iconst_2
/*      */     //   1685: invokeinterface isEnabled : (I)Z
/*      */     //   1690: ifeq -> 1727
/*      */     //   1693: invokestatic getLogger : ()Lcom/jcraft/jsch/Logger;
/*      */     //   1696: iconst_2
/*      */     //   1697: new java/lang/StringBuffer
/*      */     //   1700: dup
/*      */     //   1701: invokespecial <init> : ()V
/*      */     //   1704: ldc 'failed to load '
/*      */     //   1706: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   1709: aload #13
/*      */     //   1711: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   1714: ldc ' method'
/*      */     //   1716: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   1719: invokevirtual toString : ()Ljava/lang/String;
/*      */     //   1722: invokeinterface log : (ILjava/lang/String;)V
/*      */     //   1727: aload #7
/*      */     //   1729: ifnull -> 1905
/*      */     //   1732: iconst_0
/*      */     //   1733: istore #6
/*      */     //   1735: aload #7
/*      */     //   1737: aload_0
/*      */     //   1738: invokevirtual start : (Lcom/jcraft/jsch/Session;)Z
/*      */     //   1741: istore #5
/*      */     //   1743: iload #5
/*      */     //   1745: ifeq -> 1794
/*      */     //   1748: invokestatic getLogger : ()Lcom/jcraft/jsch/Logger;
/*      */     //   1751: iconst_1
/*      */     //   1752: invokeinterface isEnabled : (I)Z
/*      */     //   1757: ifeq -> 1794
/*      */     //   1760: invokestatic getLogger : ()Lcom/jcraft/jsch/Logger;
/*      */     //   1763: iconst_1
/*      */     //   1764: new java/lang/StringBuffer
/*      */     //   1767: dup
/*      */     //   1768: invokespecial <init> : ()V
/*      */     //   1771: ldc 'Authentication succeeded ('
/*      */     //   1773: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   1776: aload #13
/*      */     //   1778: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   1781: ldc ').'
/*      */     //   1783: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   1786: invokevirtual toString : ()Ljava/lang/String;
/*      */     //   1789: invokeinterface log : (ILjava/lang/String;)V
/*      */     //   1794: goto -> 1905
/*      */     //   1797: astore #15
/*      */     //   1799: iconst_1
/*      */     //   1800: istore #6
/*      */     //   1802: goto -> 1905
/*      */     //   1805: astore #15
/*      */     //   1807: aload #10
/*      */     //   1809: astore #16
/*      */     //   1811: aload #15
/*      */     //   1813: invokevirtual getMethods : ()Ljava/lang/String;
/*      */     //   1816: astore #10
/*      */     //   1818: aload #10
/*      */     //   1820: ldc ','
/*      */     //   1822: invokestatic split : (Ljava/lang/String;Ljava/lang/String;)[Ljava/lang/String;
/*      */     //   1825: astore #11
/*      */     //   1827: aload #16
/*      */     //   1829: aload #10
/*      */     //   1831: invokevirtual equals : (Ljava/lang/Object;)Z
/*      */     //   1834: ifne -> 1840
/*      */     //   1837: iconst_0
/*      */     //   1838: istore #12
/*      */     //   1840: iconst_0
/*      */     //   1841: istore #6
/*      */     //   1843: goto -> 1393
/*      */     //   1846: astore #15
/*      */     //   1848: aload #15
/*      */     //   1850: athrow
/*      */     //   1851: astore #15
/*      */     //   1853: aload #15
/*      */     //   1855: athrow
/*      */     //   1856: astore #15
/*      */     //   1858: invokestatic getLogger : ()Lcom/jcraft/jsch/Logger;
/*      */     //   1861: iconst_2
/*      */     //   1862: invokeinterface isEnabled : (I)Z
/*      */     //   1867: ifeq -> 1902
/*      */     //   1870: invokestatic getLogger : ()Lcom/jcraft/jsch/Logger;
/*      */     //   1873: iconst_2
/*      */     //   1874: new java/lang/StringBuffer
/*      */     //   1877: dup
/*      */     //   1878: invokespecial <init> : ()V
/*      */     //   1881: ldc 'an exception during authentication\\n'
/*      */     //   1883: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   1886: aload #15
/*      */     //   1888: invokevirtual toString : ()Ljava/lang/String;
/*      */     //   1891: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   1894: invokevirtual toString : ()Ljava/lang/String;
/*      */     //   1897: invokeinterface log : (ILjava/lang/String;)V
/*      */     //   1902: goto -> 1908
/*      */     //   1905: goto -> 1393
/*      */     //   1908: iload #5
/*      */     //   1910: ifne -> 1992
/*      */     //   1913: aload_0
/*      */     //   1914: getfield auth_failures : I
/*      */     //   1917: aload_0
/*      */     //   1918: getfield max_auth_tries : I
/*      */     //   1921: if_icmplt -> 1967
/*      */     //   1924: invokestatic getLogger : ()Lcom/jcraft/jsch/Logger;
/*      */     //   1927: iconst_1
/*      */     //   1928: invokeinterface isEnabled : (I)Z
/*      */     //   1933: ifeq -> 1967
/*      */     //   1936: invokestatic getLogger : ()Lcom/jcraft/jsch/Logger;
/*      */     //   1939: iconst_1
/*      */     //   1940: new java/lang/StringBuffer
/*      */     //   1943: dup
/*      */     //   1944: invokespecial <init> : ()V
/*      */     //   1947: ldc 'Login trials exceeds '
/*      */     //   1949: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   1952: aload_0
/*      */     //   1953: getfield max_auth_tries : I
/*      */     //   1956: invokevirtual append : (I)Ljava/lang/StringBuffer;
/*      */     //   1959: invokevirtual toString : ()Ljava/lang/String;
/*      */     //   1962: invokeinterface log : (ILjava/lang/String;)V
/*      */     //   1967: iload #6
/*      */     //   1969: ifeq -> 1982
/*      */     //   1972: new com/jcraft/jsch/JSchException
/*      */     //   1975: dup
/*      */     //   1976: ldc 'Auth cancel'
/*      */     //   1978: invokespecial <init> : (Ljava/lang/String;)V
/*      */     //   1981: athrow
/*      */     //   1982: new com/jcraft/jsch/JSchException
/*      */     //   1985: dup
/*      */     //   1986: ldc 'Auth fail'
/*      */     //   1988: invokespecial <init> : (Ljava/lang/String;)V
/*      */     //   1991: athrow
/*      */     //   1992: aload_0
/*      */     //   1993: getfield socket : Ljava/net/Socket;
/*      */     //   1996: ifnull -> 2021
/*      */     //   1999: iload_1
/*      */     //   2000: ifgt -> 2010
/*      */     //   2003: aload_0
/*      */     //   2004: getfield timeout : I
/*      */     //   2007: ifle -> 2021
/*      */     //   2010: aload_0
/*      */     //   2011: getfield socket : Ljava/net/Socket;
/*      */     //   2014: aload_0
/*      */     //   2015: getfield timeout : I
/*      */     //   2018: invokevirtual setSoTimeout : (I)V
/*      */     //   2021: aload_0
/*      */     //   2022: iconst_1
/*      */     //   2023: putfield isAuthed : Z
/*      */     //   2026: aload_0
/*      */     //   2027: getfield lock : Ljava/lang/Object;
/*      */     //   2030: dup
/*      */     //   2031: astore #13
/*      */     //   2033: monitorenter
/*      */     //   2034: aload_0
/*      */     //   2035: getfield isConnected : Z
/*      */     //   2038: ifeq -> 2116
/*      */     //   2041: aload_0
/*      */     //   2042: new java/lang/Thread
/*      */     //   2045: dup
/*      */     //   2046: aload_0
/*      */     //   2047: invokespecial <init> : (Ljava/lang/Runnable;)V
/*      */     //   2050: putfield connectThread : Ljava/lang/Thread;
/*      */     //   2053: aload_0
/*      */     //   2054: getfield connectThread : Ljava/lang/Thread;
/*      */     //   2057: new java/lang/StringBuffer
/*      */     //   2060: dup
/*      */     //   2061: invokespecial <init> : ()V
/*      */     //   2064: ldc 'Connect thread '
/*      */     //   2066: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   2069: aload_0
/*      */     //   2070: getfield host : Ljava/lang/String;
/*      */     //   2073: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   2076: ldc ' session'
/*      */     //   2078: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   2081: invokevirtual toString : ()Ljava/lang/String;
/*      */     //   2084: invokevirtual setName : (Ljava/lang/String;)V
/*      */     //   2087: aload_0
/*      */     //   2088: getfield daemon_thread : Z
/*      */     //   2091: ifeq -> 2105
/*      */     //   2094: aload_0
/*      */     //   2095: getfield connectThread : Ljava/lang/Thread;
/*      */     //   2098: aload_0
/*      */     //   2099: getfield daemon_thread : Z
/*      */     //   2102: invokevirtual setDaemon : (Z)V
/*      */     //   2105: aload_0
/*      */     //   2106: getfield connectThread : Ljava/lang/Thread;
/*      */     //   2109: invokevirtual start : ()V
/*      */     //   2112: aload_0
/*      */     //   2113: invokespecial requestPortForwarding : ()V
/*      */     //   2116: aload #13
/*      */     //   2118: monitorexit
/*      */     //   2119: goto -> 2130
/*      */     //   2122: astore #17
/*      */     //   2124: aload #13
/*      */     //   2126: monitorexit
/*      */     //   2127: aload #17
/*      */     //   2129: athrow
/*      */     //   2130: aload_0
/*      */     //   2131: getfield password : [B
/*      */     //   2134: invokestatic bzero : ([B)V
/*      */     //   2137: aload_0
/*      */     //   2138: aconst_null
/*      */     //   2139: putfield password : [B
/*      */     //   2142: goto -> 2322
/*      */     //   2145: astore_2
/*      */     //   2146: aload_0
/*      */     //   2147: iconst_0
/*      */     //   2148: putfield in_kex : Z
/*      */     //   2151: aload_0
/*      */     //   2152: getfield isConnected : Z
/*      */     //   2155: ifeq -> 2237
/*      */     //   2158: aload_2
/*      */     //   2159: invokevirtual toString : ()Ljava/lang/String;
/*      */     //   2162: astore_3
/*      */     //   2163: aload_0
/*      */     //   2164: getfield packet : Lcom/jcraft/jsch/Packet;
/*      */     //   2167: invokevirtual reset : ()V
/*      */     //   2170: aload_0
/*      */     //   2171: getfield buf : Lcom/jcraft/jsch/Buffer;
/*      */     //   2174: bipush #13
/*      */     //   2176: aload_3
/*      */     //   2177: invokevirtual length : ()I
/*      */     //   2180: iadd
/*      */     //   2181: iconst_2
/*      */     //   2182: iadd
/*      */     //   2183: sipush #128
/*      */     //   2186: iadd
/*      */     //   2187: invokevirtual checkFreeSize : (I)V
/*      */     //   2190: aload_0
/*      */     //   2191: getfield buf : Lcom/jcraft/jsch/Buffer;
/*      */     //   2194: iconst_1
/*      */     //   2195: invokevirtual putByte : (B)V
/*      */     //   2198: aload_0
/*      */     //   2199: getfield buf : Lcom/jcraft/jsch/Buffer;
/*      */     //   2202: iconst_3
/*      */     //   2203: invokevirtual putInt : (I)V
/*      */     //   2206: aload_0
/*      */     //   2207: getfield buf : Lcom/jcraft/jsch/Buffer;
/*      */     //   2210: aload_3
/*      */     //   2211: invokestatic str2byte : (Ljava/lang/String;)[B
/*      */     //   2214: invokevirtual putString : ([B)V
/*      */     //   2217: aload_0
/*      */     //   2218: getfield buf : Lcom/jcraft/jsch/Buffer;
/*      */     //   2221: ldc 'en'
/*      */     //   2223: invokestatic str2byte : (Ljava/lang/String;)[B
/*      */     //   2226: invokevirtual putString : ([B)V
/*      */     //   2229: aload_0
/*      */     //   2230: aload_0
/*      */     //   2231: getfield packet : Lcom/jcraft/jsch/Packet;
/*      */     //   2234: invokevirtual write : (Lcom/jcraft/jsch/Packet;)V
/*      */     //   2237: goto -> 2241
/*      */     //   2240: astore_3
/*      */     //   2241: aload_0
/*      */     //   2242: invokevirtual disconnect : ()V
/*      */     //   2245: goto -> 2249
/*      */     //   2248: astore_3
/*      */     //   2249: aload_0
/*      */     //   2250: iconst_0
/*      */     //   2251: putfield isConnected : Z
/*      */     //   2254: aload_2
/*      */     //   2255: instanceof java/lang/RuntimeException
/*      */     //   2258: ifeq -> 2266
/*      */     //   2261: aload_2
/*      */     //   2262: checkcast java/lang/RuntimeException
/*      */     //   2265: athrow
/*      */     //   2266: aload_2
/*      */     //   2267: instanceof com/jcraft/jsch/JSchException
/*      */     //   2270: ifeq -> 2278
/*      */     //   2273: aload_2
/*      */     //   2274: checkcast com/jcraft/jsch/JSchException
/*      */     //   2277: athrow
/*      */     //   2278: new com/jcraft/jsch/JSchException
/*      */     //   2281: dup
/*      */     //   2282: new java/lang/StringBuffer
/*      */     //   2285: dup
/*      */     //   2286: invokespecial <init> : ()V
/*      */     //   2289: ldc 'Session.connect: '
/*      */     //   2291: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuffer;
/*      */     //   2294: aload_2
/*      */     //   2295: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuffer;
/*      */     //   2298: invokevirtual toString : ()Ljava/lang/String;
/*      */     //   2301: invokespecial <init> : (Ljava/lang/String;)V
/*      */     //   2304: athrow
/*      */     //   2305: astore #18
/*      */     //   2307: aload_0
/*      */     //   2308: getfield password : [B
/*      */     //   2311: invokestatic bzero : ([B)V
/*      */     //   2314: aload_0
/*      */     //   2315: aconst_null
/*      */     //   2316: putfield password : [B
/*      */     //   2319: aload #18
/*      */     //   2321: athrow
/*      */     //   2322: return
/*      */     // Line number table:
/*      */     //   Java source line number -> byte code offset
/*      */     //   #187	-> 0
/*      */     //   #188	-> 7
/*      */     //   #191	-> 17
/*      */     //   #192	-> 28
/*      */     //   #194	-> 34
/*      */     //   #195	-> 44
/*      */     //   #199	-> 57
/*      */     //   #197	-> 60
/*      */     //   #198	-> 61
/*      */     //   #201	-> 74
/*      */     //   #203	-> 80
/*      */     //   #204	-> 92
/*      */     //   #211	-> 135
/*      */     //   #214	-> 142
/*      */     //   #215	-> 149
/*      */     //   #216	-> 165
/*      */     //   #217	-> 174
/*      */     //   #220	-> 186
/*      */     //   #221	-> 207
/*      */     //   #222	-> 222
/*      */     //   #225	-> 237
/*      */     //   #226	-> 245
/*      */     //   #227	-> 254
/*      */     //   #228	-> 263
/*      */     //   #230	-> 266
/*      */     //   #231	-> 274
/*      */     //   #232	-> 296
/*      */     //   #233	-> 312
/*      */     //   #234	-> 328
/*      */     //   #235	-> 341
/*      */     //   #238	-> 355
/*      */     //   #239	-> 366
/*      */     //   #242	-> 374
/*      */     //   #244	-> 379
/*      */     //   #245	-> 391
/*      */     //   #249	-> 402
/*      */     //   #253	-> 410
/*      */     //   #254	-> 421
/*      */     //   #255	-> 437
/*      */     //   #256	-> 447
/*      */     //   #260	-> 460
/*      */     //   #261	-> 462
/*      */     //   #262	-> 464
/*      */     //   #263	-> 476
/*      */     //   #264	-> 484
/*      */     //   #265	-> 491
/*      */     //   #266	-> 505
/*      */     //   #268	-> 514
/*      */     //   #269	-> 518
/*      */     //   #272	-> 528
/*      */     //   #273	-> 544
/*      */     //   #274	-> 547
/*      */     //   #275	-> 567
/*      */     //   #279	-> 570
/*      */     //   #285	-> 643
/*      */     //   #288	-> 646
/*      */     //   #292	-> 693
/*      */     //   #297	-> 703
/*      */     //   #300	-> 727
/*      */     //   #301	-> 739
/*      */     //   #303	-> 773
/*      */     //   #307	-> 807
/*      */     //   #309	-> 811
/*      */     //   #310	-> 823
/*      */     //   #311	-> 835
/*      */     //   #312	-> 840
/*      */     //   #315	-> 873
/*      */     //   #316	-> 885
/*      */     //   #320	-> 896
/*      */     //   #323	-> 906
/*      */     //   #324	-> 918
/*      */     //   #325	-> 933
/*      */     //   #326	-> 940
/*      */     //   #327	-> 951
/*      */     //   #329	-> 956
/*      */     //   #330	-> 961
/*      */     //   #332	-> 989
/*      */     //   #334	-> 992
/*      */     //   #335	-> 997
/*      */     //   #337	-> 1030
/*      */     //   #338	-> 1038
/*      */     //   #343	-> 1041
/*      */     //   #344	-> 1046
/*      */     //   #345	-> 1051
/*      */     //   #346	-> 1065
/*      */     //   #347	-> 1070
/*      */     //   #353	-> 1085
/*      */     //   #349	-> 1088
/*      */     //   #350	-> 1090
/*      */     //   #351	-> 1095
/*      */     //   #352	-> 1100
/*      */     //   #355	-> 1103
/*      */     //   #358	-> 1107
/*      */     //   #360	-> 1119
/*      */     //   #362	-> 1131
/*      */     //   #363	-> 1143
/*      */     //   #367	-> 1154
/*      */     //   #370	-> 1167
/*      */     //   #371	-> 1172
/*      */     //   #375	-> 1205
/*      */     //   #376	-> 1213
/*      */     //   #377	-> 1218
/*      */     //   #382	-> 1227
/*      */     //   #380	-> 1230
/*      */     //   #381	-> 1232
/*      */     //   #384	-> 1266
/*      */     //   #385	-> 1269
/*      */     //   #387	-> 1272
/*      */     //   #389	-> 1275
/*      */     //   #390	-> 1286
/*      */     //   #394	-> 1299
/*      */     //   #392	-> 1302
/*      */     //   #393	-> 1304
/*      */     //   #396	-> 1319
/*      */     //   #398	-> 1327
/*      */     //   #400	-> 1335
/*      */     //   #402	-> 1344
/*      */     //   #403	-> 1347
/*      */     //   #404	-> 1352
/*      */     //   #405	-> 1362
/*      */     //   #406	-> 1367
/*      */     //   #411	-> 1377
/*      */     //   #415	-> 1381
/*      */     //   #417	-> 1390
/*      */     //   #423	-> 1393
/*      */     //   #425	-> 1411
/*      */     //   #426	-> 1421
/*      */     //   #427	-> 1424
/*      */     //   #428	-> 1435
/*      */     //   #429	-> 1448
/*      */     //   #430	-> 1451
/*      */     //   #427	-> 1454
/*      */     //   #433	-> 1460
/*      */     //   #434	-> 1465
/*      */     //   #439	-> 1468
/*      */     //   #440	-> 1480
/*      */     //   #441	-> 1484
/*      */     //   #442	-> 1498
/*      */     //   #443	-> 1523
/*      */     //   #444	-> 1533
/*      */     //   #441	-> 1555
/*      */     //   #446	-> 1561
/*      */     //   #448	-> 1572
/*      */     //   #452	-> 1601
/*      */     //   #454	-> 1604
/*      */     //   #455	-> 1607
/*      */     //   #456	-> 1634
/*      */     //   #457	-> 1663
/*      */     //   #465	-> 1676
/*      */     //   #460	-> 1679
/*      */     //   #461	-> 1681
/*      */     //   #462	-> 1693
/*      */     //   #467	-> 1727
/*      */     //   #468	-> 1732
/*      */     //   #470	-> 1735
/*      */     //   #471	-> 1743
/*      */     //   #473	-> 1760
/*      */     //   #504	-> 1794
/*      */     //   #477	-> 1797
/*      */     //   #478	-> 1799
/*      */     //   #504	-> 1802
/*      */     //   #480	-> 1805
/*      */     //   #481	-> 1807
/*      */     //   #482	-> 1811
/*      */     //   #483	-> 1818
/*      */     //   #484	-> 1827
/*      */     //   #485	-> 1837
/*      */     //   #488	-> 1840
/*      */     //   #489	-> 1843
/*      */     //   #491	-> 1846
/*      */     //   #492	-> 1848
/*      */     //   #494	-> 1851
/*      */     //   #495	-> 1853
/*      */     //   #497	-> 1856
/*      */     //   #499	-> 1858
/*      */     //   #500	-> 1870
/*      */     //   #503	-> 1902
/*      */     //   #506	-> 1905
/*      */     //   #510	-> 1908
/*      */     //   #511	-> 1913
/*      */     //   #512	-> 1924
/*      */     //   #513	-> 1936
/*      */     //   #517	-> 1967
/*      */     //   #518	-> 1972
/*      */     //   #519	-> 1982
/*      */     //   #522	-> 1992
/*      */     //   #523	-> 2010
/*      */     //   #526	-> 2021
/*      */     //   #528	-> 2026
/*      */     //   #529	-> 2034
/*      */     //   #530	-> 2041
/*      */     //   #531	-> 2053
/*      */     //   #532	-> 2087
/*      */     //   #533	-> 2094
/*      */     //   #535	-> 2105
/*      */     //   #537	-> 2112
/*      */     //   #543	-> 2116
/*      */     //   #568	-> 2130
/*      */     //   #569	-> 2137
/*      */     //   #570	-> 2142
/*      */     //   #545	-> 2145
/*      */     //   #546	-> 2146
/*      */     //   #548	-> 2151
/*      */     //   #549	-> 2158
/*      */     //   #550	-> 2163
/*      */     //   #551	-> 2170
/*      */     //   #552	-> 2190
/*      */     //   #553	-> 2198
/*      */     //   #554	-> 2206
/*      */     //   #555	-> 2217
/*      */     //   #556	-> 2229
/*      */     //   #559	-> 2237
/*      */     //   #560	-> 2241
/*      */     //   #561	-> 2249
/*      */     //   #563	-> 2254
/*      */     //   #564	-> 2266
/*      */     //   #565	-> 2278
/*      */     //   #568	-> 2305
/*      */     //   #569	-> 2314
/*      */     //   #571	-> 2322
/*      */     // Local variable table:
/*      */     //   start	length	slot	name	descriptor
/*      */     //   44	13	2	c	Ljava/lang/Class;
/*      */     //   61	13	2	e	Ljava/lang/Exception;
/*      */     //   174	12	4	in	Ljava/io/InputStream;
/*      */     //   183	3	5	out	Ljava/io/OutputStream;
/*      */     //   222	41	4	in	Ljava/io/InputStream;
/*      */     //   237	26	5	out	Ljava/io/OutputStream;
/*      */     //   421	39	4	foo	[B
/*      */     //   951	38	5	result	Z
/*      */     //   1046	39	5	tmp	J
/*      */     //   1090	13	5	ee	Lcom/jcraft/jsch/JSchException;
/*      */     //   1213	14	5	s	Ljava/lang/String;
/*      */     //   1232	34	5	e	Ljava/lang/NumberFormatException;
/*      */     //   1286	13	8	c	Ljava/lang/Class;
/*      */     //   1304	15	8	e	Ljava/lang/Exception;
/*      */     //   1427	33	15	k	I
/*      */     //   1490	71	16	k	I
/*      */     //   1484	117	15	str	Ljava/lang/String;
/*      */     //   1607	69	15	c	Ljava/lang/Class;
/*      */     //   1681	46	15	e	Ljava/lang/Exception;
/*      */     //   1799	3	15	ee	Lcom/jcraft/jsch/JSchAuthCancelException;
/*      */     //   1811	35	16	tmp	Ljava/lang/String;
/*      */     //   1807	39	15	ee	Lcom/jcraft/jsch/JSchPartialAuthException;
/*      */     //   1848	3	15	ee	Ljava/lang/RuntimeException;
/*      */     //   1853	3	15	ee	Lcom/jcraft/jsch/JSchException;
/*      */     //   1858	47	15	ee	Ljava/lang/Exception;
/*      */     //   1421	484	13	method	Ljava/lang/String;
/*      */     //   1424	481	14	acceptable	Z
/*      */     //   462	1668	2	i	I
/*      */     //   464	1666	3	j	I
/*      */     //   906	1224	4	kex	Lcom/jcraft/jsch/KeyExchange;
/*      */     //   1269	861	5	auth	Z
/*      */     //   1272	858	6	auth_cancel	Z
/*      */     //   1275	855	7	ua	Lcom/jcraft/jsch/UserAuth;
/*      */     //   1335	795	8	cmethods	Ljava/lang/String;
/*      */     //   1344	786	9	cmethoda	[Ljava/lang/String;
/*      */     //   1347	783	10	smethods	Ljava/lang/String;
/*      */     //   1390	740	11	smethoda	[Ljava/lang/String;
/*      */     //   1393	737	12	methodi	I
/*      */     //   2163	74	3	message	Ljava/lang/String;
/*      */     //   2241	0	3	ee	Ljava/lang/Exception;
/*      */     //   2249	0	3	ee	Ljava/lang/Exception;
/*      */     //   2146	159	2	e	Ljava/lang/Exception;
/*      */     //   0	2323	0	this	Lcom/jcraft/jsch/Session;
/*      */     //   0	2323	1	connectTimeout	I
/*      */     // Exception table:
/*      */     //   from	to	target	type
/*      */     //   34	57	60	java/lang/Exception
/*      */     //   135	2130	2145	java/lang/Exception
/*      */     //   135	2130	2305	finally
/*      */     //   274	344	347	finally
/*      */     //   347	352	347	finally
/*      */     //   1041	1085	1088	com/jcraft/jsch/JSchException
/*      */     //   1205	1227	1230	java/lang/NumberFormatException
/*      */     //   1275	1299	1302	java/lang/Exception
/*      */     //   1604	1676	1679	java/lang/Exception
/*      */     //   1735	1794	1797	com/jcraft/jsch/JSchAuthCancelException
/*      */     //   1735	1794	1805	com/jcraft/jsch/JSchPartialAuthException
/*      */     //   1735	1794	1846	java/lang/RuntimeException
/*      */     //   1735	1794	1851	com/jcraft/jsch/JSchException
/*      */     //   1735	1794	1856	java/lang/Exception
/*      */     //   2034	2119	2122	finally
/*      */     //   2122	2127	2122	finally
/*      */     //   2145	2307	2305	finally
/*      */     //   2151	2237	2240	java/lang/Exception
/*  153 */     //   2241	2245	2248	java/lang/Exception } byte[] password = null;
/*      */   public void rekey() throws Exception { send_kexinit(); }
/*      */   private void send_kexinit() throws Exception { if (this.in_kex) return;  String cipherc2s = getConfig("cipher.c2s"); String ciphers2c = getConfig("cipher.s2c"); String[] not_available_ciphers = checkCiphers(getConfig("CheckCiphers")); if (not_available_ciphers != null && not_available_ciphers.length > 0) { cipherc2s = Util.diffString(cipherc2s, not_available_ciphers); ciphers2c = Util.diffString(ciphers2c, not_available_ciphers); if (cipherc2s == null || ciphers2c == null) throw new JSchException("There are not any available ciphers.");  }  String kex = getConfig("kex"); String[] not_available_kexes = checkKexes(getConfig("CheckKexes")); if (not_available_kexes != null && not_available_kexes.length > 0) { kex = Util.diffString(kex, not_available_kexes); if (kex == null) throw new JSchException("There are not any available kexes.");  }  String server_host_key = getConfig("server_host_key"); String[] not_available_shks = checkSignatures(getConfig("CheckSignatures")); if (not_available_shks != null && not_available_shks.length > 0) { server_host_key = Util.diffString(server_host_key, not_available_shks); if (server_host_key == null) throw new JSchException("There are not any available sig algorithm.");  }  this.in_kex = true; this.kex_start_time = System.currentTimeMillis(); Buffer buf = new Buffer(); Packet packet = new Packet(buf); packet.reset(); buf.putByte((byte)20); synchronized (random) { random.fill(buf.buffer, buf.index, 16); buf.skip(16); }  buf.putString(Util.str2byte(kex)); buf.putString(Util.str2byte(server_host_key)); buf.putString(Util.str2byte(cipherc2s)); buf.putString(Util.str2byte(ciphers2c)); buf.putString(Util.str2byte(getConfig("mac.c2s"))); buf.putString(Util.str2byte(getConfig("mac.s2c"))); buf.putString(Util.str2byte(getConfig("compression.c2s"))); buf.putString(Util.str2byte(getConfig("compression.s2c"))); buf.putString(Util.str2byte(getConfig("lang.c2s"))); buf.putString(Util.str2byte(getConfig("lang.s2c"))); buf.putByte((byte)0); buf.putInt(0); buf.setOffSet(5); this.I_C = new byte[buf.getLength()]; buf.getByte(this.I_C); write(packet); if (JSch.getLogger().isEnabled(1)) JSch.getLogger().log(1, "SSH_MSG_KEXINIT sent");  }
/*      */   private void send_newkeys() throws Exception { this.packet.reset(); this.buf.putByte((byte)21); write(this.packet); if (JSch.getLogger().isEnabled(1)) JSch.getLogger().log(1, "SSH_MSG_NEWKEYS sent");  }
/*      */   private void checkHost(String chost, int port, KeyExchange kex) throws JSchException { String shkc = getConfig("StrictHostKeyChecking"); if (this.hostKeyAlias != null) chost = this.hostKeyAlias;  byte[] K_S = kex.getHostKey(); String key_type = kex.getKeyType(); String key_fprint = kex.getFingerPrint(); if (this.hostKeyAlias == null && port != 22) chost = "[" + chost + "]:" + port;  HostKeyRepository hkr = getHostKeyRepository(); String hkh = getConfig("HashKnownHosts"); if (hkh.equals("yes") && hkr instanceof KnownHosts) { this.hostkey = ((KnownHosts)hkr).createHashedHostKey(chost, K_S); } else { this.hostkey = new HostKey(chost, K_S); }  int i = 0; synchronized (hkr) { i = hkr.check(chost, K_S); }  boolean insert = false; if ((shkc.equals("ask") || shkc.equals("yes")) && i == 2) { String file = null; synchronized (hkr) { file = hkr.getKnownHostsRepositoryID(); }  if (file == null) file = "known_hosts";  boolean b = false; if (this.userinfo != null) { String message = "WARNING: REMOTE HOST IDENTIFICATION HAS CHANGED!\nIT IS POSSIBLE THAT SOMEONE IS DOING SOMETHING NASTY!\nSomeone could be eavesdropping on you right now (man-in-the-middle attack)!\nIt is also possible that the " + key_type + " host key has just been changed.\n" + "The fingerprint for the " + key_type + " key sent by the remote host " + chost + " is\n" + key_fprint + ".\n" + "Please contact your system administrator.\n" + "Add correct host key in " + file + " to get rid of this message."; if (shkc.equals("ask")) { b = this.userinfo.promptYesNo(message + "\nDo you want to delete the old key and insert the new key?"); } else { this.userinfo.showMessage(message); }  }  if (!b) throw new JSchException("HostKey has been changed: " + chost);  synchronized (hkr) { hkr.remove(chost, kex.getKeyAlgorithName(), null); insert = true; }  }  if ((shkc.equals("ask") || shkc.equals("yes")) && i != 0 && !insert) { if (shkc.equals("yes")) throw new JSchException("reject HostKey: " + this.host);  if (this.userinfo != null) { boolean foo = this.userinfo.promptYesNo("The authenticity of host '" + this.host + "' can't be established.\n" + key_type + " key fingerprint is " + key_fprint + ".\n" + "Are you sure you want to continue connecting?"); if (!foo) throw new JSchException("reject HostKey: " + this.host);  insert = true; } else { if (i == 1) throw new JSchException("UnknownHostKey: " + this.host + ". " + key_type + " key fingerprint is " + key_fprint);  throw new JSchException("HostKey has been changed: " + this.host); }  }  if (shkc.equals("no") && 1 == i)
/*      */       insert = true;  if (i == 0) { HostKey[] keys = hkr.getHostKey(chost, kex.getKeyAlgorithName()); String _key = Util.byte2str(Util.toBase64(K_S, 0, K_S.length)); for (int j = 0; j < keys.length; j++) { if (keys[i].getKey().equals(_key) && keys[j].getMarker().equals("@revoked")) { if (this.userinfo != null)
/*      */             this.userinfo.showMessage("The " + key_type + " host key for " + this.host + " is marked as revoked.\n" + "This could mean that a stolen key is being used to " + "impersonate this host.");  if (JSch.getLogger().isEnabled(1))
/*      */             JSch.getLogger().log(1, "Host '" + this.host + "' has provided revoked key.");  throw new JSchException("revoked HostKey: " + this.host); }  }  }  if (i == 0 && JSch.getLogger().isEnabled(1))
/*      */       JSch.getLogger().log(1, "Host '" + this.host + "' is known and matches the " + key_type + " host key");  if (insert && JSch.getLogger().isEnabled(2))
/*      */       JSch.getLogger().log(2, "Permanently added '" + this.host + "' (" + key_type + ") to the list of known hosts.");  if (insert)
/*      */       synchronized (hkr) { hkr.add(this.hostkey, this.userinfo); }   }
/*      */   public Channel openChannel(String type) throws JSchException { if (!this.isConnected)
/*      */       throw new JSchException("session is down");  try { Channel channel = Channel.getChannel(type); addChannel(channel); channel.init(); if (channel instanceof ChannelSession)
/*      */         applyConfigChannel((ChannelSession)channel);  return channel; } catch (Exception e) { return null; }  }
/*      */   public void encode(Packet packet) throws Exception { if (this.deflater != null) { this.compress_len[0] = packet.buffer.index; packet.buffer.buffer = this.deflater.compress(packet.buffer.buffer, 5, this.compress_len); packet.buffer.index = this.compress_len[0]; }  if (this.c2scipher != null) { packet.padding(this.c2scipher_size); int pad = packet.buffer.buffer[4]; synchronized (random) { random.fill(packet.buffer.buffer, packet.buffer.index - pad, pad); }  } else { packet.padding(8); }  if (this.c2smac != null) { this.c2smac.update(this.seqo); this.c2smac.update(packet.buffer.buffer, 0, packet.buffer.index); this.c2smac.doFinal(packet.buffer.buffer, packet.buffer.index); }  if (this.c2scipher != null) { byte[] buf = packet.buffer.buffer; this.c2scipher.update(buf, 0, packet.buffer.index, buf, 0); }  if (this.c2smac != null)
/*      */       packet.buffer.skip(this.c2smac.getBlockSize());  }
/*      */   public Buffer read(Buffer buf) throws Exception { int j = 0; while (true) { buf.reset(); this.io.getByte(buf.buffer, buf.index, this.s2ccipher_size); buf.index += this.s2ccipher_size; if (this.s2ccipher != null)
/*      */         this.s2ccipher.update(buf.buffer, 0, this.s2ccipher_size, buf.buffer, 0);  j = buf.buffer[0] << 24 & 0xFF000000 | buf.buffer[1] << 16 & 0xFF0000 | buf.buffer[2] << 8 & 0xFF00 | buf.buffer[3] & 0xFF; if (j < 5 || j > 262144)
/*      */         start_discard(buf, this.s2ccipher, this.s2cmac, j, 262144);  int need = j + 4 - this.s2ccipher_size; if (buf.index + need > buf.buffer.length) { byte[] foo = new byte[buf.index + need]; System.arraycopy(buf.buffer, 0, foo, 0, buf.index); buf.buffer = foo; }  if (need % this.s2ccipher_size != 0) { String message = "Bad packet length " + need; if (JSch.getLogger().isEnabled(4))
/*      */           JSch.getLogger().log(4, message);  start_discard(buf, this.s2ccipher, this.s2cmac, j, 262144 - this.s2ccipher_size); }  if (need > 0) { this.io.getByte(buf.buffer, buf.index, need); buf.index += need; if (this.s2ccipher != null)
/*      */           this.s2ccipher.update(buf.buffer, this.s2ccipher_size, need, buf.buffer, this.s2ccipher_size);  }  if (this.s2cmac != null) { this.s2cmac.update(this.seqi); this.s2cmac.update(buf.buffer, 0, buf.index); this.s2cmac.doFinal(this.s2cmac_result1, 0); this.io.getByte(this.s2cmac_result2, 0, this.s2cmac_result2.length); if (!Arrays.equals(this.s2cmac_result1, this.s2cmac_result2)) { if (need > 262144)
/*      */             throw new IOException("MAC Error");  start_discard(buf, this.s2ccipher, this.s2cmac, j, 262144 - need); continue; }  }  this.seqi++; if (this.inflater != null) { int pad = buf.buffer[4]; this.uncompress_len[0] = buf.index - 5 - pad; byte[] foo = this.inflater.uncompress(buf.buffer, 5, this.uncompress_len); if (foo != null) { buf.buffer = foo; buf.index = 5 + this.uncompress_len[0]; } else { System.err.println("fail in inflater"); break; }  }  int type = buf.getCommand() & 0xFF; if (type == 1) { buf.rewind(); buf.getInt(); buf.getShort(); int reason_code = buf.getInt(); byte[] description = buf.getString(); byte[] language_tag = buf.getString(); throw new JSchException("SSH_MSG_DISCONNECT: " + reason_code + " " + Util.byte2str(description) + " " + Util.byte2str(language_tag)); }  if (type == 2)
/*      */         continue;  if (type == 3) { buf.rewind(); buf.getInt(); buf.getShort(); int reason_id = buf.getInt(); if (JSch.getLogger().isEnabled(1))
/*      */           JSch.getLogger().log(1, "Received SSH_MSG_UNIMPLEMENTED for " + reason_id);  continue; }  if (type == 4) { buf.rewind(); buf.getInt(); buf.getShort(); continue; }  if (type == 93) { buf.rewind(); buf.getInt(); buf.getShort(); Channel c = Channel.getChannel(buf.getInt(), this); if (c == null)
/*      */           continue;  c.addRemoteWindowSize(buf.getUInt()); continue; }  if (type == 52) { this.isAuthed = true; if (this.inflater == null && this.deflater == null) { String method = this.guess[6]; initDeflater(method); method = this.guess[7]; initInflater(method); }  }  break; }  buf.rewind(); return buf; }
/*      */   private void start_discard(Buffer buf, Cipher cipher, MAC mac, int packet_length, int discard) throws JSchException, IOException { MAC discard_mac = null; if (!cipher.isCBC())
/*      */       throw new JSchException("Packet corrupt");  if (packet_length != 262144 && mac != null)
/*      */       discard_mac = mac;  discard -= buf.index; while (discard > 0) { buf.reset(); int len = (discard > buf.buffer.length) ? buf.buffer.length : discard; this.io.getByte(buf.buffer, 0, len); if (discard_mac != null)
/*      */         discard_mac.update(buf.buffer, 0, len);  discard -= len; }  if (discard_mac != null)
/*      */       discard_mac.doFinal(buf.buffer, 0);  throw new JSchException("Packet corrupt"); }
/*  183 */   byte[] getSessionId() { return this.session_id; } public void connect() throws JSchException { connect(this.timeout); } private void receive_newkeys(Buffer buf, KeyExchange kex) throws Exception { updateKeys(kex); this.in_kex = false; }
/*      */   private void updateKeys(KeyExchange kex) throws Exception { byte[] K = kex.getK(); byte[] H = kex.getH(); HASH hash = kex.getHash(); if (this.session_id == null) {
/*      */       this.session_id = new byte[H.length]; System.arraycopy(H, 0, this.session_id, 0, H.length);
/*      */     }  this.buf.reset(); this.buf.putMPInt(K); this.buf.putByte(H); this.buf.putByte((byte)65); this.buf.putByte(this.session_id); hash.update(this.buf.buffer, 0, this.buf.index); this.IVc2s = hash.digest(); int j = this.buf.index - this.session_id.length - 1; this.buf.buffer[j] = (byte)(this.buf.buffer[j] + 1); hash.update(this.buf.buffer, 0, this.buf.index); this.IVs2c = hash.digest(); this.buf.buffer[j] = (byte)(this.buf.buffer[j] + 1); hash.update(this.buf.buffer, 0, this.buf.index); this.Ec2s = hash.digest(); this.buf.buffer[j] = (byte)(this.buf.buffer[j] + 1); hash.update(this.buf.buffer, 0, this.buf.index); this.Es2c = hash.digest(); this.buf.buffer[j] = (byte)(this.buf.buffer[j] + 1); hash.update(this.buf.buffer, 0, this.buf.index); this.MACc2s = hash.digest(); this.buf.buffer[j] = (byte)(this.buf.buffer[j] + 1); hash.update(this.buf.buffer, 0, this.buf.index); this.MACs2c = hash.digest(); try {
/*      */       String method = this.guess[3]; Class c = Class.forName(getConfig(method)); this.s2ccipher = (Cipher)c.newInstance(); while (this.s2ccipher.getBlockSize() > this.Es2c.length) {
/*      */         this.buf.reset(); this.buf.putMPInt(K); this.buf.putByte(H); this.buf.putByte(this.Es2c); hash.update(this.buf.buffer, 0, this.buf.index); byte[] foo = hash.digest(); byte[] bar = new byte[this.Es2c.length + foo.length]; System.arraycopy(this.Es2c, 0, bar, 0, this.Es2c.length); System.arraycopy(foo, 0, bar, this.Es2c.length, foo.length); this.Es2c = bar;
/*      */       }  this.s2ccipher.init(1, this.Es2c, this.IVs2c); this.s2ccipher_size = this.s2ccipher.getIVSize(); method = this.guess[5]; c = Class.forName(getConfig(method)); this.s2cmac = (MAC)c.newInstance(); this.MACs2c = expandKey(this.buf, K, H, this.MACs2c, hash, this.s2cmac.getBlockSize()); this.s2cmac.init(this.MACs2c); this.s2cmac_result1 = new byte[this.s2cmac.getBlockSize()]; this.s2cmac_result2 = new byte[this.s2cmac.getBlockSize()]; method = this.guess[2]; c = Class.forName(getConfig(method)); this.c2scipher = (Cipher)c.newInstance(); while (this.c2scipher.getBlockSize() > this.Ec2s.length) {
/*      */         this.buf.reset(); this.buf.putMPInt(K); this.buf.putByte(H); this.buf.putByte(this.Ec2s); hash.update(this.buf.buffer, 0, this.buf.index); byte[] foo = hash.digest(); byte[] bar = new byte[this.Ec2s.length + foo.length]; System.arraycopy(this.Ec2s, 0, bar, 0, this.Ec2s.length); System.arraycopy(foo, 0, bar, this.Ec2s.length, foo.length); this.Ec2s = bar;
/*      */       }  this.c2scipher.init(0, this.Ec2s, this.IVc2s); this.c2scipher_size = this.c2scipher.getIVSize(); method = this.guess[4]; c = Class.forName(getConfig(method)); this.c2smac = (MAC)c.newInstance(); this.MACc2s = expandKey(this.buf, K, H, this.MACc2s, hash, this.c2smac.getBlockSize()); this.c2smac.init(this.MACc2s); method = this.guess[6]; initDeflater(method); method = this.guess[7]; initInflater(method);
/*      */     } catch (Exception e) {
/*      */       if (e instanceof JSchException)
/*      */         throw e;  throw new JSchException(e.toString(), e);
/*      */     }  }
/*      */   private byte[] expandKey(Buffer buf, byte[] K, byte[] H, byte[] key, HASH hash, int required_length) throws Exception { byte[] result = key; int size = hash.getBlockSize(); while (result.length < required_length) {
/*      */       buf.reset(); buf.putMPInt(K); buf.putByte(H); buf.putByte(result); hash.update(buf.buffer, 0, buf.index); byte[] tmp = new byte[result.length + size]; System.arraycopy(result, 0, tmp, 0, result.length); System.arraycopy(hash.digest(), 0, tmp, result.length, size); Util.bzero(result); result = tmp;
/*      */     }  return result; }
/*      */   void write(Packet packet, Channel c, int length) throws Exception { long t = getTimeout(); while (true) {
/*      */       while (this.in_kex) {
/*      */         if (t > 0L && System.currentTimeMillis() - this.kex_start_time > t)
/*      */           throw new JSchException("timeout in waiting for rekeying process.");  try {
/*      */           Thread.sleep(10L);
/*      */         } catch (InterruptedException e) {}
/*      */       }  synchronized (c) {
/*      */         if (c.rwsize < length)
/*      */           try {
/*      */             c.notifyme++; c.wait(100L);
/*      */           } catch (InterruptedException e) {
/*      */           
/*      */           } finally {
/*      */             c.notifyme--;
/*      */           }   if (this.in_kex)
/*      */           continue;  if (c.rwsize >= length) {
/*      */           c.rwsize -= length; break;
/*      */         } 
/*      */       }  if (c.close || !c.isConnected())
/*      */         throw new IOException("channel is broken");  boolean sendit = false; int s = 0; byte command = 0; int recipient = -1;
/*      */       synchronized (c) {
/*      */         if (c.rwsize > 0L) {
/*      */           long len = c.rwsize;
/*      */           if (len > length)
/*      */             len = length; 
/*      */           if (len != length)
/*      */             s = packet.shift((int)len, (this.c2scipher != null) ? this.c2scipher_size : 8, (this.c2smac != null) ? this.c2smac.getBlockSize() : 0); 
/*      */           command = packet.buffer.getCommand();
/*      */           recipient = c.getRecipient();
/*      */           length = (int)(length - len);
/*      */           c.rwsize -= len;
/*      */           sendit = true;
/*      */         } 
/*      */       } 
/*      */       if (sendit) {
/*      */         _write(packet);
/*      */         if (length == 0)
/*      */           return; 
/*      */         packet.unshift(command, recipient, s, length);
/*      */       } 
/*      */       synchronized (c) {
/*      */         if (this.in_kex)
/*      */           continue; 
/*      */         if (c.rwsize >= length) {
/*      */           c.rwsize -= length;
/*      */           break;
/*      */         } 
/*      */       } 
/*      */     } 
/*      */     _write(packet); }
/*      */   public void write(Packet packet) throws Exception { long t = getTimeout();
/*      */     while (this.in_kex) {
/*      */       if (t > 0L && System.currentTimeMillis() - this.kex_start_time > t && !this.in_prompt)
/*      */         throw new JSchException("timeout in waiting for rekeying process."); 
/*      */       byte command = packet.buffer.getCommand();
/*      */       if (command == 20 || command == 21 || command == 30 || command == 31 || command == 31 || command == 32 || command == 33 || command == 34 || command == 1)
/*      */         break; 
/*      */       try {
/*      */         Thread.sleep(10L);
/*      */       } catch (InterruptedException e) {}
/*      */     } 
/*      */     _write(packet); }
/*      */   private void _write(Packet packet) throws Exception { synchronized (this.lock) {
/*      */       encode(packet);
/*      */       if (this.io != null) {
/*      */         this.io.put(packet);
/*      */         this.seqo++;
/*      */       } 
/*      */     }  }
/*      */   public void run() { this.thread = this;
/*      */     Buffer buf = new Buffer();
/*      */     Packet packet = new Packet(buf);
/*      */     int i = 0;
/*      */     int[] start = new int[1];
/*      */     int[] length = new int[1];
/*      */     KeyExchange kex = null;
/*      */     int stimeout = 0;
/*      */     try {
/*      */       while (this.isConnected && this.thread != null) {
/*      */         byte[] foo;
/*      */         Channel channel;
/*      */         int len, r;
/*      */         long rws;
/*      */         int rps;
/*      */         boolean reply;
/*      */         String ctyp;
/*      */         Thread tmp, t;
/*      */         try {
/*      */           buf = read(buf);
/*      */           stimeout = 0;
/*      */         } catch (InterruptedIOException ee) {
/*      */           if (!this.in_kex && stimeout < this.serverAliveCountMax) {
/*      */             sendKeepAliveMsg();
/*      */             stimeout++;
/*      */             continue;
/*      */           } 
/*      */           if (this.in_kex && stimeout < this.serverAliveCountMax) {
/*      */             stimeout++;
/*      */             continue;
/*      */           } 
/*      */           throw ee;
/*      */         } 
/*      */         int msgType = buf.getCommand() & 0xFF;
/*      */         if (kex != null && kex.getState() == msgType) {
/*      */           this.kex_start_time = System.currentTimeMillis();
/*      */           boolean result = kex.next(buf);
/*      */           if (!result)
/*      */             throw new JSchException("verify: " + result); 
/*      */           continue;
/*      */         } 
/*      */         switch (msgType) {
/*      */           case 20:
/*      */             kex = receive_kexinit(buf);
/*      */             continue;
/*      */           case 21:
/*      */             send_newkeys();
/*      */             receive_newkeys(buf, kex);
/*      */             kex = null;
/*      */             continue;
/*      */           case 94:
/*      */             buf.getInt();
/*      */             buf.getByte();
/*      */             buf.getByte();
/*      */             i = buf.getInt();
/*      */             channel = Channel.getChannel(i, this);
/*      */             foo = buf.getString(start, length);
/*      */             if (channel == null)
/*      */               continue; 
/*      */             if (length[0] == 0)
/*      */               continue; 
/*      */             try {
/*      */               channel.write(foo, start[0], length[0]);
/*      */             } catch (Exception e) {
/*      */               try {
/*      */                 channel.disconnect();
/*      */               } catch (Exception ee) {}
/*      */               continue;
/*      */             } 
/*      */             len = length[0];
/*      */             channel.setLocalWindowSize(channel.lwsize - len);
/*      */             if (channel.lwsize < channel.lwsize_max / 2) {
/*      */               packet.reset();
/*      */               buf.putByte((byte)93);
/*      */               buf.putInt(channel.getRecipient());
/*      */               buf.putInt(channel.lwsize_max - channel.lwsize);
/*      */               synchronized (channel) {
/*      */                 if (!channel.close)
/*      */                   write(packet); 
/*      */               } 
/*      */               channel.setLocalWindowSize(channel.lwsize_max);
/*      */             } 
/*      */             continue;
/*      */           case 95:
/*      */             buf.getInt();
/*      */             buf.getShort();
/*      */             i = buf.getInt();
/*      */             channel = Channel.getChannel(i, this);
/*      */             buf.getInt();
/*      */             foo = buf.getString(start, length);
/*      */             if (channel == null)
/*      */               continue; 
/*      */             if (length[0] == 0)
/*      */               continue; 
/*      */             channel.write_ext(foo, start[0], length[0]);
/*      */             len = length[0];
/*      */             channel.setLocalWindowSize(channel.lwsize - len);
/*      */             if (channel.lwsize < channel.lwsize_max / 2) {
/*      */               packet.reset();
/*      */               buf.putByte((byte)93);
/*      */               buf.putInt(channel.getRecipient());
/*      */               buf.putInt(channel.lwsize_max - channel.lwsize);
/*      */               synchronized (channel) {
/*      */                 if (!channel.close)
/*      */                   write(packet); 
/*      */               } 
/*      */               channel.setLocalWindowSize(channel.lwsize_max);
/*      */             } 
/*      */             continue;
/*      */           case 93:
/*      */             buf.getInt();
/*      */             buf.getShort();
/*      */             i = buf.getInt();
/*      */             channel = Channel.getChannel(i, this);
/*      */             if (channel == null)
/*      */               continue; 
/*      */             channel.addRemoteWindowSize(buf.getUInt());
/*      */             continue;
/*      */           case 96:
/*      */             buf.getInt();
/*      */             buf.getShort();
/*      */             i = buf.getInt();
/*      */             channel = Channel.getChannel(i, this);
/*      */             if (channel != null)
/*      */               channel.eof_remote(); 
/*      */             continue;
/*      */           case 97:
/*      */             buf.getInt();
/*      */             buf.getShort();
/*      */             i = buf.getInt();
/*      */             channel = Channel.getChannel(i, this);
/*      */             if (channel != null)
/*      */               channel.disconnect(); 
/*      */             continue;
/*      */           case 91:
/*      */             buf.getInt();
/*      */             buf.getShort();
/*      */             i = buf.getInt();
/*      */             channel = Channel.getChannel(i, this);
/*      */             r = buf.getInt();
/*      */             rws = buf.getUInt();
/*      */             rps = buf.getInt();
/*      */             if (channel != null) {
/*      */               channel.setRemoteWindowSize(rws);
/*      */               channel.setRemotePacketSize(rps);
/*      */               channel.open_confirmation = true;
/*      */               channel.setRecipient(r);
/*      */             } 
/*      */             continue;
/*      */           case 92:
/*      */             buf.getInt();
/*      */             buf.getShort();
/*      */             i = buf.getInt();
/*      */             channel = Channel.getChannel(i, this);
/*      */             if (channel != null) {
/*      */               int reason_code = buf.getInt();
/*      */               channel.setExitStatus(reason_code);
/*      */               channel.close = true;
/*      */               channel.eof_remote = true;
/*      */               channel.setRecipient(0);
/*      */             } 
/*      */             continue;
/*      */           case 98:
/*      */             buf.getInt();
/*      */             buf.getShort();
/*      */             i = buf.getInt();
/*      */             foo = buf.getString();
/*      */             reply = (buf.getByte() != 0);
/*      */             channel = Channel.getChannel(i, this);
/*      */             if (channel != null) {
/*      */               byte reply_type = 100;
/*      */               if (Util.byte2str(foo).equals("exit-status")) {
/*      */                 i = buf.getInt();
/*      */                 channel.setExitStatus(i);
/*      */                 reply_type = 99;
/*      */               } 
/*      */               if (reply) {
/*      */                 packet.reset();
/*      */                 buf.putByte(reply_type);
/*      */                 buf.putInt(channel.getRecipient());
/*      */                 write(packet);
/*      */               } 
/*      */             } 
/*      */             continue;
/*      */           case 90:
/*      */             buf.getInt();
/*      */             buf.getShort();
/*      */             foo = buf.getString();
/*      */             ctyp = Util.byte2str(foo);
/*      */             if (!"forwarded-tcpip".equals(ctyp) && (!"x11".equals(ctyp) || !this.x11_forwarding) && (!"auth-agent@openssh.com".equals(ctyp) || !this.agent_forwarding)) {
/*      */               packet.reset();
/*      */               buf.putByte((byte)92);
/*      */               buf.putInt(buf.getInt());
/*      */               buf.putInt(1);
/*      */               buf.putString(Util.empty);
/*      */               buf.putString(Util.empty);
/*      */               write(packet);
/*      */               continue;
/*      */             } 
/*      */             channel = Channel.getChannel(ctyp);
/*      */             addChannel(channel);
/*      */             channel.getData(buf);
/*      */             channel.init();
/*      */             tmp = new Thread(channel);
/*      */             tmp.setName("Channel " + ctyp + " " + this.host);
/*      */             if (this.daemon_thread)
/*      */               tmp.setDaemon(this.daemon_thread); 
/*      */             tmp.start();
/*      */             continue;
/*      */           case 99:
/*      */             buf.getInt();
/*      */             buf.getShort();
/*      */             i = buf.getInt();
/*      */             channel = Channel.getChannel(i, this);
/*      */             if (channel == null)
/*      */               continue; 
/*      */             channel.reply = 1;
/*      */             continue;
/*      */           case 100:
/*      */             buf.getInt();
/*      */             buf.getShort();
/*      */             i = buf.getInt();
/*      */             channel = Channel.getChannel(i, this);
/*      */             if (channel == null)
/*      */               continue; 
/*      */             channel.reply = 0;
/*      */             continue;
/*      */           case 80:
/*      */             buf.getInt();
/*      */             buf.getShort();
/*      */             foo = buf.getString();
/*      */             reply = (buf.getByte() != 0);
/*      */             if (reply) {
/*      */               packet.reset();
/*      */               buf.putByte((byte)82);
/*      */               write(packet);
/*      */             } 
/*      */             continue;
/*      */           case 81:
/*      */           case 82:
/*      */             t = this.grr.getThread();
/*      */             if (t != null) {
/*      */               this.grr.setReply((msgType == 81) ? 1 : 0);
/*      */               if (msgType == 81 && this.grr.getPort() == 0) {
/*      */                 buf.getInt();
/*      */                 buf.getShort();
/*      */                 this.grr.setPort(buf.getInt());
/*      */               } 
/*      */               t.interrupt();
/*      */             } 
/*      */             continue;
/*      */         } 
/*      */         throw new IOException("Unknown SSH message type " + msgType);
/*      */       } 
/*      */     } catch (Exception e) {
/*      */       this.in_kex = false;
/*      */       if (JSch.getLogger().isEnabled(1))
/*      */         JSch.getLogger().log(1, "Caught an exception, leaving main loop due to " + e.getMessage()); 
/*      */     } 
/*      */     try {
/*      */       disconnect();
/*      */     } catch (NullPointerException e) {
/*      */     
/*      */     } catch (Exception e) {}
/*      */     this.isConnected = false; }
/*      */   public void disconnect() { if (!this.isConnected)
/*      */       return; 
/*      */     if (JSch.getLogger().isEnabled(1))
/*      */       JSch.getLogger().log(1, "Disconnecting from " + this.host + " port " + this.port); 
/*      */     Channel.disconnect(this);
/*      */     this.isConnected = false;
/*      */     PortWatcher.delPort(this);
/*      */     ChannelForwardedTCPIP.delPort(this);
/*      */     ChannelX11.removeFakedCookie(this);
/*      */     synchronized (this.lock) {
/*      */       if (this.connectThread != null) {
/*      */         Thread.yield();
/*      */         this.connectThread.interrupt();
/*      */         this.connectThread = null;
/*      */       } 
/*      */     } 
/*      */     this.thread = null;
/*      */     try {
/*      */       if (this.io != null) {
/*      */         if (this.io.in != null)
/*      */           this.io.in.close(); 
/*      */         if (this.io.out != null)
/*      */           this.io.out.close(); 
/*      */         if (this.io.out_ext != null)
/*      */           this.io.out_ext.close(); 
/*      */       } 
/*      */       if (this.proxy == null) {
/*      */         if (this.socket != null)
/*      */           this.socket.close(); 
/*      */       } else {
/*      */         synchronized (this.proxy) {
/*      */           this.proxy.close();
/*      */         } 
/*      */         this.proxy = null;
/*      */       } 
/*      */     } catch (Exception e) {}
/*      */     this.io = null;
/*      */     this.socket = null;
/*      */     this.jsch.removeSession(this); }
/*      */   public int setPortForwardingL(int lport, String host, int rport) throws JSchException { return setPortForwardingL("127.0.0.1", lport, host, rport); }
/*      */   public int setPortForwardingL(String bind_address, int lport, String host, int rport) throws JSchException { return setPortForwardingL(bind_address, lport, host, rport, null); }
/*  574 */   private KeyExchange receive_kexinit(Buffer buf) throws Exception { int j = buf.getInt();
/*  575 */     if (j != buf.getLength()) {
/*  576 */       buf.getByte();
/*  577 */       this.I_S = new byte[buf.index - 5];
/*      */     } else {
/*      */       
/*  580 */       this.I_S = new byte[j - 1 - buf.getByte()];
/*      */     } 
/*  582 */     System.arraycopy(buf.buffer, buf.s, this.I_S, 0, this.I_S.length);
/*      */     
/*  584 */     if (!this.in_kex) {
/*  585 */       send_kexinit();
/*      */     }
/*      */     
/*  588 */     this.guess = KeyExchange.guess(this.I_S, this.I_C);
/*  589 */     if (this.guess == null) {
/*  590 */       throw new JSchException("Algorithm negotiation fail");
/*      */     }
/*      */     
/*  593 */     if (!this.isAuthed && (this.guess[2].equals("none") || this.guess[3].equals("none")))
/*      */     {
/*      */       
/*  596 */       throw new JSchException("NONE Cipher should not be chosen before authentification is successed.");
/*      */     }
/*      */     
/*  599 */     KeyExchange kex = null;
/*      */     try {
/*  601 */       Class c = Class.forName(getConfig(this.guess[0]));
/*  602 */       kex = (KeyExchange)c.newInstance();
/*      */     }
/*  604 */     catch (Exception e) {
/*  605 */       throw new JSchException(e.toString(), e);
/*      */     } 
/*      */     
/*  608 */     kex.init(this, this.V_S, this.V_C, this.I_S, this.I_C);
/*  609 */     return kex; } public int setPortForwardingL(String bind_address, int lport, String host, int rport, ServerSocketFactory ssf) throws JSchException { return setPortForwardingL(bind_address, lport, host, rport, ssf, 0); } public int setPortForwardingL(String bind_address, int lport, String host, int rport, ServerSocketFactory ssf, int connectTimeout) throws JSchException { PortWatcher pw = PortWatcher.addPort(this, bind_address, lport, host, rport, ssf); pw.setConnectTimeout(connectTimeout); Thread tmp = new Thread(pw); tmp.setName("PortWatcher Thread for " + host); if (this.daemon_thread) tmp.setDaemon(this.daemon_thread);  tmp.start(); return pw.lport; } public void delPortForwardingL(int lport) throws JSchException { delPortForwardingL("127.0.0.1", lport); } public void delPortForwardingL(String bind_address, int lport) throws JSchException { PortWatcher.delPort(this, bind_address, lport); } public String[] getPortForwardingL() throws JSchException { return PortWatcher.getPortForwarding(this); } public void setPortForwardingR(int rport, String host, int lport) throws JSchException { setPortForwardingR(null, rport, host, lport, (SocketFactory)null); } public void setPortForwardingR(String bind_address, int rport, String host, int lport) throws JSchException { setPortForwardingR(bind_address, rport, host, lport, (SocketFactory)null); } public void setPortForwardingR(int rport, String host, int lport, SocketFactory sf) throws JSchException { setPortForwardingR(null, rport, host, lport, sf); } public void setPortForwardingR(String bind_address, int rport, String host, int lport, SocketFactory sf) throws JSchException { int allocated = _setPortForwardingR(bind_address, rport); ChannelForwardedTCPIP.addPort(this, bind_address, rport, allocated, host, lport, sf); } public void setPortForwardingR(int rport, String daemon) throws JSchException { setPortForwardingR((String)null, rport, daemon, (Object[])null); } public void setPortForwardingR(int rport, String daemon, Object[] arg) throws JSchException { setPortForwardingR((String)null, rport, daemon, arg); } public void setPortForwardingR(String bind_address, int rport, String daemon, Object[] arg) throws JSchException { int allocated = _setPortForwardingR(bind_address, rport); ChannelForwardedTCPIP.addPort(this, bind_address, rport, allocated, daemon, arg); } public String[] getPortForwardingR() throws JSchException { return ChannelForwardedTCPIP.getPortForwarding(this); } private class Forwarding {
/*      */     private Forwarding() {} String bind_address = null; int port = -1; String host = null; int hostport = -1; private final Session this$0; } private Forwarding parseForwarding(String conf) throws JSchException { String[] tmp = conf.split(" "); if (tmp.length > 1) { Vector foo = new Vector(); for (int i = 0; i < tmp.length; i++) { if (tmp[i].length() != 0) foo.addElement(tmp[i].trim());  }  StringBuffer sb = new StringBuffer(); for (int j = 0; j < foo.size(); j++) { sb.append(foo.elementAt(j)); if (j + 1 < foo.size()) sb.append(":");  }  conf = sb.toString(); }  String org = conf; Forwarding f = new Forwarding(); try { if (conf.lastIndexOf(":") == -1) throw new JSchException("parseForwarding: " + org);  f.hostport = Integer.parseInt(conf.substring(conf.lastIndexOf(":") + 1)); conf = conf.substring(0, conf.lastIndexOf(":")); if (conf.lastIndexOf(":") == -1) throw new JSchException("parseForwarding: " + org);  f.host = conf.substring(conf.lastIndexOf(":") + 1); conf = conf.substring(0, conf.lastIndexOf(":")); if (conf.lastIndexOf(":") != -1) { f.port = Integer.parseInt(conf.substring(conf.lastIndexOf(":") + 1)); conf = conf.substring(0, conf.lastIndexOf(":")); if (conf.length() == 0 || conf.equals("*")) conf = "0.0.0.0";  if (conf.equals("localhost")) conf = "127.0.0.1";  f.bind_address = conf; } else { f.port = Integer.parseInt(conf); f.bind_address = "127.0.0.1"; }  } catch (NumberFormatException e) { throw new JSchException("parseForwarding: " + e.toString()); }  return f; } public int setPortForwardingL(String conf) throws JSchException { Forwarding f = parseForwarding(conf); return setPortForwardingL(f.bind_address, f.port, f.host, f.hostport); } public int setPortForwardingR(String conf) throws JSchException { Forwarding f = parseForwarding(conf); int allocated = _setPortForwardingR(f.bind_address, f.port); ChannelForwardedTCPIP.addPort(this, f.bind_address, f.port, allocated, f.host, f.hostport, null); return allocated; }
/*      */   public Channel getStreamForwarder(String host, int port) throws JSchException { ChannelDirectTCPIP channel = new ChannelDirectTCPIP(); channel.init(); addChannel(channel); channel.setHost(host); channel.setPort(port); return channel; }
/*  612 */   Session(JSch jsch, String username, String host, int port) throws JSchException { this.in_kex = false;
/*  613 */     this.in_prompt = false;
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
/*  917 */     this.uncompress_len = new int[1];
/*  918 */     this.compress_len = new int[1];
/*      */     
/*  920 */     this.s2ccipher_size = 8;
/*  921 */     this.c2scipher_size = 8;
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
/* 2152 */     this.grr = new GlobalRequestReply();
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
/* 2396 */     this.hostkey = null; this.jsch = jsch; this.buf = new Buffer(); this.packet = new Packet(this.buf); this.username = username; this.org_host = this.host = host; this.port = port; applyConfig(); if (this.username == null) try { this.username = (String)System.getProperties().get("user.name"); } catch (SecurityException e) {}  if (this.username == null) throw new JSchException("username is not given.");  } private class GlobalRequestReply {
/* 2397 */     private Thread thread = null; private int reply = -1; private int port = 0; private final Session this$0; void setThread(Thread thread) { this.thread = thread; this.reply = -1; } Thread getThread() { return this.thread; } void setReply(int reply) { this.reply = reply; } int getReply() { return this.reply; } int getPort() { return this.port; } void setPort(int port) { this.port = port; } private GlobalRequestReply() {} } private int _setPortForwardingR(String bind_address, int rport) throws JSchException { synchronized (this.grr) { Buffer buf = new Buffer(100); Packet packet = new Packet(buf); String address_to_bind = ChannelForwardedTCPIP.normalize(bind_address); this.grr.setThread(Thread.currentThread()); this.grr.setPort(rport); try { packet.reset(); buf.putByte((byte)80); buf.putString(Util.str2byte("tcpip-forward")); buf.putByte((byte)1); buf.putString(Util.str2byte(address_to_bind)); buf.putInt(rport); write(packet); } catch (Exception e) { this.grr.setThread(null); if (e instanceof Throwable) throw new JSchException(e.toString(), e);  throw new JSchException(e.toString()); }  int count = 0; int reply = this.grr.getReply(); while (count < 10 && reply == -1) { try { Thread.sleep(1000L); } catch (Exception e) {} count++; reply = this.grr.getReply(); }  this.grr.setThread(null); if (reply != 1) throw new JSchException("remote port forwarding failed for listen port " + rport);  rport = this.grr.getPort(); }  return rport; } public void delPortForwardingR(int rport) throws JSchException { delPortForwardingR(null, rport); } public void delPortForwardingR(String bind_address, int rport) throws JSchException { ChannelForwardedTCPIP.delPort(this, bind_address, rport); } private void initDeflater(String method) throws JSchException { if (method.equals("none")) { this.deflater = null; return; }  String foo = getConfig(method); if (foo != null && (method.equals("zlib") || (this.isAuthed && method.equals("zlib@openssh.com")))) try { Class c = Class.forName(foo); this.deflater = (Compression)c.newInstance(); int level = 6; try { level = Integer.parseInt(getConfig("compression_level")); } catch (Exception ee) {} this.deflater.init(1, level); } catch (NoClassDefFoundError ee) { throw new JSchException(ee.toString(), ee); } catch (Exception ee) { throw new JSchException(ee.toString(), ee); }   } private void initInflater(String method) throws JSchException { if (method.equals("none")) { this.inflater = null; return; }  String foo = getConfig(method); if (foo != null && (method.equals("zlib") || (this.isAuthed && method.equals("zlib@openssh.com")))) try { Class c = Class.forName(foo); this.inflater = (Compression)c.newInstance(); this.inflater.init(0, 0); } catch (Exception ee) { throw new JSchException(ee.toString(), ee); }   } void addChannel(Channel channel) { channel.setSession(this); } public void setProxy(Proxy proxy) { this.proxy = proxy; } public void setHost(String host) { this.host = host; } public void setPort(int port) { this.port = port; } void setUserName(String username) { this.username = username; } public void setUserInfo(UserInfo userinfo) { this.userinfo = userinfo; } public UserInfo getUserInfo() { return this.userinfo; } public void setInputStream(InputStream in) { this.in = in; } public void setOutputStream(OutputStream out) { this.out = out; } public void setX11Host(String host) { ChannelX11.setHost(host); } public void setX11Port(int port) { ChannelX11.setPort(port); } public void setX11Cookie(String cookie) { ChannelX11.setCookie(cookie); } public void setPassword(String password) { if (password != null) this.password = Util.str2byte(password);  } public void setPassword(byte[] password) { if (password != null) { this.password = new byte[password.length]; System.arraycopy(password, 0, this.password, 0, password.length); }  } public void setConfig(Properties newconf) { setConfig(newconf); } public void setConfig(Hashtable newconf) { synchronized (this.lock) { if (this.config == null) this.config = new Hashtable();  for (Enumeration e = newconf.keys(); e.hasMoreElements(); ) { String key = e.nextElement(); this.config.put(key, (String)newconf.get(key)); }  }  } public void setConfig(String key, String value) { synchronized (this.lock) { if (this.config == null) this.config = new Hashtable();  this.config.put(key, value); }  } public String getConfig(String key) { Object foo = null; if (this.config != null) { foo = this.config.get(key); if (foo instanceof String) return (String)foo;  }  foo = JSch.getConfig(key); if (foo instanceof String) return (String)foo;  return null; } public void setSocketFactory(SocketFactory sfactory) { this.socket_factory = sfactory; } public boolean isConnected() { return this.isConnected; } public int getTimeout() { return this.timeout; } public void setTimeout(int timeout) throws JSchException { if (this.socket == null) { if (timeout < 0) throw new JSchException("invalid timeout value");  this.timeout = timeout; return; }  try { this.socket.setSoTimeout(timeout); this.timeout = timeout; } catch (Exception e) { if (e instanceof Throwable) throw new JSchException(e.toString(), e);  throw new JSchException(e.toString()); }  } public String getServerVersion() { return Util.byte2str(this.V_S); } public String getClientVersion() { return Util.byte2str(this.V_C); } public void setClientVersion(String cv) { this.V_C = Util.str2byte(cv); } public void sendIgnore() throws Exception { Buffer buf = new Buffer(); Packet packet = new Packet(buf); packet.reset(); buf.putByte((byte)2); write(packet); } private static final byte[] keepalivemsg = Util.str2byte("keepalive@jcraft.com"); public void sendKeepAliveMsg() throws Exception { Buffer buf = new Buffer(); Packet packet = new Packet(buf); packet.reset(); buf.putByte((byte)80); buf.putString(keepalivemsg); buf.putByte((byte)1); write(packet); } private static final byte[] nomoresessions = Util.str2byte("no-more-sessions@openssh.com"); private HostKey hostkey; public void noMoreSessionChannels() throws Exception { Buffer buf = new Buffer(); Packet packet = new Packet(buf); packet.reset(); buf.putByte((byte)80); buf.putString(nomoresessions); buf.putByte((byte)0); write(packet); } public HostKey getHostKey() { return this.hostkey; }
/* 2398 */   public String getHost() { return this.host; }
/* 2399 */   public String getUserName() { return this.username; } public int getPort() {
/* 2400 */     return this.port;
/*      */   } public void setHostKeyAlias(String hostKeyAlias) {
/* 2402 */     this.hostKeyAlias = hostKeyAlias;
/*      */   }
/*      */   public String getHostKeyAlias() {
/* 2405 */     return this.hostKeyAlias;
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
/*      */   public void setServerAliveInterval(int interval) throws JSchException {
/* 2417 */     setTimeout(interval);
/* 2418 */     this.serverAliveInterval = interval;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getServerAliveInterval() {
/* 2427 */     return this.serverAliveInterval;
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
/*      */   public void setServerAliveCountMax(int count) {
/* 2440 */     this.serverAliveCountMax = count;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public int getServerAliveCountMax() {
/* 2449 */     return this.serverAliveCountMax;
/*      */   }
/*      */   
/*      */   public void setDaemonThread(boolean enable) {
/* 2453 */     this.daemon_thread = enable;
/*      */   }
/*      */   
/*      */   private String[] checkCiphers(String ciphers) {
/* 2457 */     if (ciphers == null || ciphers.length() == 0) {
/* 2458 */       return null;
/*      */     }
/* 2460 */     if (JSch.getLogger().isEnabled(1)) {
/* 2461 */       JSch.getLogger().log(1, "CheckCiphers: " + ciphers);
/*      */     }
/*      */ 
/*      */     
/* 2465 */     String cipherc2s = getConfig("cipher.c2s");
/* 2466 */     String ciphers2c = getConfig("cipher.s2c");
/*      */     
/* 2468 */     Vector result = new Vector();
/* 2469 */     String[] _ciphers = Util.split(ciphers, ",");
/* 2470 */     for (int i = 0; i < _ciphers.length; i++) {
/* 2471 */       String cipher = _ciphers[i];
/* 2472 */       if (ciphers2c.indexOf(cipher) != -1 || cipherc2s.indexOf(cipher) != -1)
/*      */       {
/* 2474 */         if (!checkCipher(getConfig(cipher)))
/* 2475 */           result.addElement(cipher); 
/*      */       }
/*      */     } 
/* 2478 */     if (result.size() == 0)
/* 2479 */       return null; 
/* 2480 */     String[] foo = new String[result.size()];
/* 2481 */     System.arraycopy(result.toArray(), 0, foo, 0, result.size());
/*      */     
/* 2483 */     if (JSch.getLogger().isEnabled(1)) {
/* 2484 */       for (int j = 0; j < foo.length; j++) {
/* 2485 */         JSch.getLogger().log(1, foo[j] + " is not available.");
/*      */       }
/*      */     }
/*      */ 
/*      */     
/* 2490 */     return foo;
/*      */   }
/*      */   
/*      */   static boolean checkCipher(String cipher) {
/*      */     try {
/* 2495 */       Class c = Class.forName(cipher);
/* 2496 */       Cipher _c = (Cipher)c.newInstance();
/* 2497 */       _c.init(0, new byte[_c.getBlockSize()], new byte[_c.getIVSize()]);
/*      */ 
/*      */       
/* 2500 */       return true;
/*      */     }
/* 2502 */     catch (Exception e) {
/* 2503 */       return false;
/*      */     } 
/*      */   }
/*      */   
/*      */   private String[] checkKexes(String kexes) {
/* 2508 */     if (kexes == null || kexes.length() == 0) {
/* 2509 */       return null;
/*      */     }
/* 2511 */     if (JSch.getLogger().isEnabled(1)) {
/* 2512 */       JSch.getLogger().log(1, "CheckKexes: " + kexes);
/*      */     }
/*      */ 
/*      */     
/* 2516 */     Vector result = new Vector();
/* 2517 */     String[] _kexes = Util.split(kexes, ",");
/* 2518 */     for (int i = 0; i < _kexes.length; i++) {
/* 2519 */       if (!checkKex(this, getConfig(_kexes[i]))) {
/* 2520 */         result.addElement(_kexes[i]);
/*      */       }
/*      */     } 
/* 2523 */     if (result.size() == 0)
/* 2524 */       return null; 
/* 2525 */     String[] foo = new String[result.size()];
/* 2526 */     System.arraycopy(result.toArray(), 0, foo, 0, result.size());
/*      */     
/* 2528 */     if (JSch.getLogger().isEnabled(1)) {
/* 2529 */       for (int j = 0; j < foo.length; j++) {
/* 2530 */         JSch.getLogger().log(1, foo[j] + " is not available.");
/*      */       }
/*      */     }
/*      */ 
/*      */     
/* 2535 */     return foo;
/*      */   }
/*      */   
/*      */   static boolean checkKex(Session s, String kex) {
/*      */     try {
/* 2540 */       Class c = Class.forName(kex);
/* 2541 */       KeyExchange _c = (KeyExchange)c.newInstance();
/* 2542 */       _c.init(s, null, null, null, null);
/* 2543 */       return true;
/*      */     } catch (Exception e) {
/* 2545 */       return false;
/*      */     } 
/*      */   }
/*      */   private String[] checkSignatures(String sigs) {
/* 2549 */     if (sigs == null || sigs.length() == 0) {
/* 2550 */       return null;
/*      */     }
/* 2552 */     if (JSch.getLogger().isEnabled(1)) {
/* 2553 */       JSch.getLogger().log(1, "CheckSignatures: " + sigs);
/*      */     }
/*      */ 
/*      */     
/* 2557 */     Vector result = new Vector();
/* 2558 */     String[] _sigs = Util.split(sigs, ",");
/* 2559 */     for (int i = 0; i < _sigs.length; i++) {
/*      */       try {
/* 2561 */         Class c = Class.forName(JSch.getConfig(_sigs[i]));
/* 2562 */         Signature sig = (Signature)c.newInstance();
/* 2563 */         sig.init();
/*      */       }
/* 2565 */       catch (Exception e) {
/* 2566 */         result.addElement(_sigs[i]);
/*      */       } 
/*      */     } 
/* 2569 */     if (result.size() == 0)
/* 2570 */       return null; 
/* 2571 */     String[] foo = new String[result.size()];
/* 2572 */     System.arraycopy(result.toArray(), 0, foo, 0, result.size());
/* 2573 */     if (JSch.getLogger().isEnabled(1)) {
/* 2574 */       for (int j = 0; j < foo.length; j++) {
/* 2575 */         JSch.getLogger().log(1, foo[j] + " is not available.");
/*      */       }
/*      */     }
/*      */     
/* 2579 */     return foo;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setIdentityRepository(IdentityRepository identityRepository) {
/* 2590 */     this.identityRepository = identityRepository;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   IdentityRepository getIdentityRepository() {
/* 2601 */     if (this.identityRepository == null)
/* 2602 */       return this.jsch.getIdentityRepository(); 
/* 2603 */     return this.identityRepository;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public void setHostKeyRepository(HostKeyRepository hostkeyRepository) {
/* 2613 */     this.hostkeyRepository = hostkeyRepository;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public HostKeyRepository getHostKeyRepository() {
/* 2624 */     if (this.hostkeyRepository == null)
/* 2625 */       return this.jsch.getHostKeyRepository(); 
/* 2626 */     return this.hostkeyRepository;
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
/*      */   
/*      */   private void applyConfig() throws JSchException {
/* 2670 */     ConfigRepository configRepository = this.jsch.getConfigRepository();
/* 2671 */     if (configRepository == null) {
/*      */       return;
/*      */     }
/*      */     
/* 2675 */     ConfigRepository.Config config = configRepository.getConfig(this.org_host);
/*      */ 
/*      */     
/* 2678 */     String value = null;
/*      */     
/* 2680 */     value = config.getUser();
/* 2681 */     if (value != null) {
/* 2682 */       this.username = value;
/*      */     }
/* 2684 */     value = config.getHostname();
/* 2685 */     if (value != null) {
/* 2686 */       this.host = value;
/*      */     }
/* 2688 */     int port = config.getPort();
/* 2689 */     if (port != -1) {
/* 2690 */       this.port = port;
/*      */     }
/* 2692 */     checkConfig(config, "kex");
/* 2693 */     checkConfig(config, "server_host_key");
/*      */     
/* 2695 */     checkConfig(config, "cipher.c2s");
/* 2696 */     checkConfig(config, "cipher.s2c");
/* 2697 */     checkConfig(config, "mac.c2s");
/* 2698 */     checkConfig(config, "mac.s2c");
/* 2699 */     checkConfig(config, "compression.c2s");
/* 2700 */     checkConfig(config, "compression.s2c");
/* 2701 */     checkConfig(config, "compression_level");
/*      */     
/* 2703 */     checkConfig(config, "StrictHostKeyChecking");
/* 2704 */     checkConfig(config, "HashKnownHosts");
/* 2705 */     checkConfig(config, "PreferredAuthentications");
/* 2706 */     checkConfig(config, "MaxAuthTries");
/* 2707 */     checkConfig(config, "ClearAllForwardings");
/*      */     
/* 2709 */     value = config.getValue("HostKeyAlias");
/* 2710 */     if (value != null) {
/* 2711 */       setHostKeyAlias(value);
/*      */     }
/* 2713 */     value = config.getValue("UserKnownHostsFile");
/* 2714 */     if (value != null) {
/* 2715 */       KnownHosts kh = new KnownHosts(this.jsch);
/* 2716 */       kh.setKnownHosts(value);
/* 2717 */       setHostKeyRepository(kh);
/*      */     } 
/*      */     
/* 2720 */     String[] values = config.getValues("IdentityFile");
/* 2721 */     if (values != null) {
/* 2722 */       String[] global = configRepository.getConfig("").getValues("IdentityFile");
/*      */       
/* 2724 */       if (global != null) {
/* 2725 */         for (int i = 0; i < global.length; i++) {
/* 2726 */           this.jsch.addIdentity(global[i]);
/*      */         }
/*      */       } else {
/*      */         
/* 2730 */         global = new String[0];
/*      */       } 
/* 2732 */       if (values.length - global.length > 0) {
/* 2733 */         IdentityRepository.Wrapper ir = new IdentityRepository.Wrapper(this.jsch.getIdentityRepository(), true);
/*      */         
/* 2735 */         for (int i = 0; i < values.length; i++) {
/* 2736 */           String ifile = values[i];
/* 2737 */           for (int j = 0; j < global.length; ) {
/* 2738 */             if (!ifile.equals(global[j])) {
/*      */               j++; continue;
/* 2740 */             }  ifile = null;
/*      */           } 
/*      */           
/* 2743 */           if (ifile != null) {
/*      */             
/* 2745 */             Identity identity = IdentityFile.newInstance(ifile, null, this.jsch);
/*      */             
/* 2747 */             ir.add(identity);
/*      */           } 
/* 2749 */         }  setIdentityRepository(ir);
/*      */       } 
/*      */     } 
/*      */     
/* 2753 */     value = config.getValue("ServerAliveInterval");
/* 2754 */     if (value != null) {
/*      */       try {
/* 2756 */         setServerAliveInterval(Integer.parseInt(value));
/*      */       }
/* 2758 */       catch (NumberFormatException e) {}
/*      */     }
/*      */ 
/*      */     
/* 2762 */     value = config.getValue("ConnectTimeout");
/* 2763 */     if (value != null) {
/*      */       try {
/* 2765 */         setTimeout(Integer.parseInt(value));
/*      */       }
/* 2767 */       catch (NumberFormatException e) {}
/*      */     }
/*      */ 
/*      */     
/* 2771 */     value = config.getValue("MaxAuthTries");
/* 2772 */     if (value != null) {
/* 2773 */       setConfig("MaxAuthTries", value);
/*      */     }
/*      */     
/* 2776 */     value = config.getValue("ClearAllForwardings");
/* 2777 */     if (value != null) {
/* 2778 */       setConfig("ClearAllForwardings", value);
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private void applyConfigChannel(ChannelSession channel) throws JSchException {
/* 2784 */     ConfigRepository configRepository = this.jsch.getConfigRepository();
/* 2785 */     if (configRepository == null) {
/*      */       return;
/*      */     }
/*      */     
/* 2789 */     ConfigRepository.Config config = configRepository.getConfig(this.org_host);
/*      */ 
/*      */     
/* 2792 */     String value = null;
/*      */     
/* 2794 */     value = config.getValue("ForwardAgent");
/* 2795 */     if (value != null) {
/* 2796 */       channel.setAgentForwarding(value.equals("yes"));
/*      */     }
/*      */     
/* 2799 */     value = config.getValue("RequestTTY");
/* 2800 */     if (value != null) {
/* 2801 */       channel.setPty(value.equals("yes"));
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private void requestPortForwarding() throws JSchException {
/* 2807 */     if (getConfig("ClearAllForwardings").equals("yes")) {
/*      */       return;
/*      */     }
/* 2810 */     ConfigRepository configRepository = this.jsch.getConfigRepository();
/* 2811 */     if (configRepository == null) {
/*      */       return;
/*      */     }
/*      */     
/* 2815 */     ConfigRepository.Config config = configRepository.getConfig(this.org_host);
/*      */ 
/*      */     
/* 2818 */     String[] values = config.getValues("LocalForward");
/* 2819 */     if (values != null) {
/* 2820 */       for (int i = 0; i < values.length; i++) {
/* 2821 */         setPortForwardingL(values[i]);
/*      */       }
/*      */     }
/*      */     
/* 2825 */     values = config.getValues("RemoteForward");
/* 2826 */     if (values != null) {
/* 2827 */       for (int i = 0; i < values.length; i++) {
/* 2828 */         setPortForwardingR(values[i]);
/*      */       }
/*      */     }
/*      */   }
/*      */   
/*      */   private void checkConfig(ConfigRepository.Config config, String key) {
/* 2834 */     String value = config.getValue(key);
/* 2835 */     if (value != null)
/* 2836 */       setConfig(key, value); 
/*      */   }
/*      */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\Session.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */