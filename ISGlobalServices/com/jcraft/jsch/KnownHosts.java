/*     */ package com.jcraft.jsch;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.util.ArrayList;
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
/*     */ public class KnownHosts
/*     */   implements HostKeyRepository
/*     */ {
/*     */   private static final String _known_hosts = "known_hosts";
/*  38 */   private JSch jsch = null;
/*  39 */   private String known_hosts = null;
/*  40 */   private Vector pool = null;
/*     */   
/*  42 */   private MAC hmacsha1 = null;
/*     */ 
/*     */   
/*     */   KnownHosts(JSch jsch) {
/*  46 */     this.jsch = jsch;
/*  47 */     this.hmacsha1 = getHMACSHA1();
/*  48 */     this.pool = new Vector();
/*     */   }
/*     */   
/*     */   void setKnownHosts(String filename) throws JSchException {
/*     */     try {
/*  53 */       this.known_hosts = filename;
/*  54 */       FileInputStream fis = new FileInputStream(Util.checkTilde(filename));
/*  55 */       setKnownHosts(fis);
/*     */     }
/*  57 */     catch (FileNotFoundException e) {}
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
/*     */   void setKnownHosts(InputStream input) throws JSchException {
/*     */     // Byte code:
/*     */     //   0: aload_0
/*     */     //   1: getfield pool : Ljava/util/Vector;
/*     */     //   4: invokevirtual removeAllElements : ()V
/*     */     //   7: new java/lang/StringBuffer
/*     */     //   10: dup
/*     */     //   11: invokespecial <init> : ()V
/*     */     //   14: astore_2
/*     */     //   15: iconst_0
/*     */     //   16: istore #5
/*     */     //   18: aload_1
/*     */     //   19: astore #6
/*     */     //   21: aconst_null
/*     */     //   22: astore #8
/*     */     //   24: sipush #1024
/*     */     //   27: newarray byte
/*     */     //   29: astore #10
/*     */     //   31: iconst_0
/*     */     //   32: istore #11
/*     */     //   34: iconst_0
/*     */     //   35: istore #11
/*     */     //   37: aload #6
/*     */     //   39: invokevirtual read : ()I
/*     */     //   42: istore #4
/*     */     //   44: iload #4
/*     */     //   46: iconst_m1
/*     */     //   47: if_icmpne -> 58
/*     */     //   50: iload #11
/*     */     //   52: ifne -> 136
/*     */     //   55: goto -> 826
/*     */     //   58: iload #4
/*     */     //   60: bipush #13
/*     */     //   62: if_icmpne -> 68
/*     */     //   65: goto -> 37
/*     */     //   68: iload #4
/*     */     //   70: bipush #10
/*     */     //   72: if_icmpne -> 78
/*     */     //   75: goto -> 136
/*     */     //   78: aload #10
/*     */     //   80: arraylength
/*     */     //   81: iload #11
/*     */     //   83: if_icmpgt -> 122
/*     */     //   86: iload #11
/*     */     //   88: sipush #10240
/*     */     //   91: if_icmple -> 97
/*     */     //   94: goto -> 136
/*     */     //   97: aload #10
/*     */     //   99: arraylength
/*     */     //   100: iconst_2
/*     */     //   101: imul
/*     */     //   102: newarray byte
/*     */     //   104: astore #12
/*     */     //   106: aload #10
/*     */     //   108: iconst_0
/*     */     //   109: aload #12
/*     */     //   111: iconst_0
/*     */     //   112: aload #10
/*     */     //   114: arraylength
/*     */     //   115: invokestatic arraycopy : (Ljava/lang/Object;ILjava/lang/Object;II)V
/*     */     //   118: aload #12
/*     */     //   120: astore #10
/*     */     //   122: aload #10
/*     */     //   124: iload #11
/*     */     //   126: iinc #11, 1
/*     */     //   129: iload #4
/*     */     //   131: i2b
/*     */     //   132: bastore
/*     */     //   133: goto -> 37
/*     */     //   136: iconst_0
/*     */     //   137: istore #4
/*     */     //   139: iload #4
/*     */     //   141: iload #11
/*     */     //   143: if_icmpge -> 191
/*     */     //   146: aload #10
/*     */     //   148: iload #4
/*     */     //   150: baload
/*     */     //   151: istore_3
/*     */     //   152: iload_3
/*     */     //   153: bipush #32
/*     */     //   155: if_icmpeq -> 164
/*     */     //   158: iload_3
/*     */     //   159: bipush #9
/*     */     //   161: if_icmpne -> 170
/*     */     //   164: iinc #4, 1
/*     */     //   167: goto -> 139
/*     */     //   170: iload_3
/*     */     //   171: bipush #35
/*     */     //   173: if_icmpne -> 191
/*     */     //   176: aload_0
/*     */     //   177: aload #10
/*     */     //   179: iconst_0
/*     */     //   180: iload #11
/*     */     //   182: invokestatic byte2str : ([BII)Ljava/lang/String;
/*     */     //   185: invokespecial addInvalidLine : (Ljava/lang/String;)V
/*     */     //   188: goto -> 34
/*     */     //   191: iload #4
/*     */     //   193: iload #11
/*     */     //   195: if_icmplt -> 213
/*     */     //   198: aload_0
/*     */     //   199: aload #10
/*     */     //   201: iconst_0
/*     */     //   202: iload #11
/*     */     //   204: invokestatic byte2str : ([BII)Ljava/lang/String;
/*     */     //   207: invokespecial addInvalidLine : (Ljava/lang/String;)V
/*     */     //   210: goto -> 34
/*     */     //   213: aload_2
/*     */     //   214: iconst_0
/*     */     //   215: invokevirtual setLength : (I)V
/*     */     //   218: iload #4
/*     */     //   220: iload #11
/*     */     //   222: if_icmpge -> 259
/*     */     //   225: aload #10
/*     */     //   227: iload #4
/*     */     //   229: iinc #4, 1
/*     */     //   232: baload
/*     */     //   233: istore_3
/*     */     //   234: iload_3
/*     */     //   235: bipush #32
/*     */     //   237: if_icmpeq -> 259
/*     */     //   240: iload_3
/*     */     //   241: bipush #9
/*     */     //   243: if_icmpne -> 249
/*     */     //   246: goto -> 259
/*     */     //   249: aload_2
/*     */     //   250: iload_3
/*     */     //   251: i2c
/*     */     //   252: invokevirtual append : (C)Ljava/lang/StringBuffer;
/*     */     //   255: pop
/*     */     //   256: goto -> 218
/*     */     //   259: aload_2
/*     */     //   260: invokevirtual toString : ()Ljava/lang/String;
/*     */     //   263: astore #7
/*     */     //   265: iload #4
/*     */     //   267: iload #11
/*     */     //   269: if_icmpge -> 280
/*     */     //   272: aload #7
/*     */     //   274: invokevirtual length : ()I
/*     */     //   277: ifne -> 295
/*     */     //   280: aload_0
/*     */     //   281: aload #10
/*     */     //   283: iconst_0
/*     */     //   284: iload #11
/*     */     //   286: invokestatic byte2str : ([BII)Ljava/lang/String;
/*     */     //   289: invokespecial addInvalidLine : (Ljava/lang/String;)V
/*     */     //   292: goto -> 34
/*     */     //   295: iload #4
/*     */     //   297: iload #11
/*     */     //   299: if_icmpge -> 326
/*     */     //   302: aload #10
/*     */     //   304: iload #4
/*     */     //   306: baload
/*     */     //   307: istore_3
/*     */     //   308: iload_3
/*     */     //   309: bipush #32
/*     */     //   311: if_icmpeq -> 320
/*     */     //   314: iload_3
/*     */     //   315: bipush #9
/*     */     //   317: if_icmpne -> 326
/*     */     //   320: iinc #4, 1
/*     */     //   323: goto -> 295
/*     */     //   326: ldc ''
/*     */     //   328: astore #12
/*     */     //   330: aload #7
/*     */     //   332: iconst_0
/*     */     //   333: invokevirtual charAt : (I)C
/*     */     //   336: bipush #64
/*     */     //   338: if_icmpne -> 458
/*     */     //   341: aload #7
/*     */     //   343: astore #12
/*     */     //   345: aload_2
/*     */     //   346: iconst_0
/*     */     //   347: invokevirtual setLength : (I)V
/*     */     //   350: iload #4
/*     */     //   352: iload #11
/*     */     //   354: if_icmpge -> 391
/*     */     //   357: aload #10
/*     */     //   359: iload #4
/*     */     //   361: iinc #4, 1
/*     */     //   364: baload
/*     */     //   365: istore_3
/*     */     //   366: iload_3
/*     */     //   367: bipush #32
/*     */     //   369: if_icmpeq -> 391
/*     */     //   372: iload_3
/*     */     //   373: bipush #9
/*     */     //   375: if_icmpne -> 381
/*     */     //   378: goto -> 391
/*     */     //   381: aload_2
/*     */     //   382: iload_3
/*     */     //   383: i2c
/*     */     //   384: invokevirtual append : (C)Ljava/lang/StringBuffer;
/*     */     //   387: pop
/*     */     //   388: goto -> 350
/*     */     //   391: aload_2
/*     */     //   392: invokevirtual toString : ()Ljava/lang/String;
/*     */     //   395: astore #7
/*     */     //   397: iload #4
/*     */     //   399: iload #11
/*     */     //   401: if_icmpge -> 412
/*     */     //   404: aload #7
/*     */     //   406: invokevirtual length : ()I
/*     */     //   409: ifne -> 427
/*     */     //   412: aload_0
/*     */     //   413: aload #10
/*     */     //   415: iconst_0
/*     */     //   416: iload #11
/*     */     //   418: invokestatic byte2str : ([BII)Ljava/lang/String;
/*     */     //   421: invokespecial addInvalidLine : (Ljava/lang/String;)V
/*     */     //   424: goto -> 34
/*     */     //   427: iload #4
/*     */     //   429: iload #11
/*     */     //   431: if_icmpge -> 458
/*     */     //   434: aload #10
/*     */     //   436: iload #4
/*     */     //   438: baload
/*     */     //   439: istore_3
/*     */     //   440: iload_3
/*     */     //   441: bipush #32
/*     */     //   443: if_icmpeq -> 452
/*     */     //   446: iload_3
/*     */     //   447: bipush #9
/*     */     //   449: if_icmpne -> 458
/*     */     //   452: iinc #4, 1
/*     */     //   455: goto -> 427
/*     */     //   458: aload_2
/*     */     //   459: iconst_0
/*     */     //   460: invokevirtual setLength : (I)V
/*     */     //   463: iconst_m1
/*     */     //   464: istore #9
/*     */     //   466: iload #4
/*     */     //   468: iload #11
/*     */     //   470: if_icmpge -> 507
/*     */     //   473: aload #10
/*     */     //   475: iload #4
/*     */     //   477: iinc #4, 1
/*     */     //   480: baload
/*     */     //   481: istore_3
/*     */     //   482: iload_3
/*     */     //   483: bipush #32
/*     */     //   485: if_icmpeq -> 507
/*     */     //   488: iload_3
/*     */     //   489: bipush #9
/*     */     //   491: if_icmpne -> 497
/*     */     //   494: goto -> 507
/*     */     //   497: aload_2
/*     */     //   498: iload_3
/*     */     //   499: i2c
/*     */     //   500: invokevirtual append : (C)Ljava/lang/StringBuffer;
/*     */     //   503: pop
/*     */     //   504: goto -> 466
/*     */     //   507: aload_2
/*     */     //   508: invokevirtual toString : ()Ljava/lang/String;
/*     */     //   511: astore #13
/*     */     //   513: aload #13
/*     */     //   515: invokestatic name2type : (Ljava/lang/String;)I
/*     */     //   518: bipush #6
/*     */     //   520: if_icmpeq -> 533
/*     */     //   523: aload #13
/*     */     //   525: invokestatic name2type : (Ljava/lang/String;)I
/*     */     //   528: istore #9
/*     */     //   530: goto -> 537
/*     */     //   533: iload #11
/*     */     //   535: istore #4
/*     */     //   537: iload #4
/*     */     //   539: iload #11
/*     */     //   541: if_icmplt -> 559
/*     */     //   544: aload_0
/*     */     //   545: aload #10
/*     */     //   547: iconst_0
/*     */     //   548: iload #11
/*     */     //   550: invokestatic byte2str : ([BII)Ljava/lang/String;
/*     */     //   553: invokespecial addInvalidLine : (Ljava/lang/String;)V
/*     */     //   556: goto -> 34
/*     */     //   559: iload #4
/*     */     //   561: iload #11
/*     */     //   563: if_icmpge -> 590
/*     */     //   566: aload #10
/*     */     //   568: iload #4
/*     */     //   570: baload
/*     */     //   571: istore_3
/*     */     //   572: iload_3
/*     */     //   573: bipush #32
/*     */     //   575: if_icmpeq -> 584
/*     */     //   578: iload_3
/*     */     //   579: bipush #9
/*     */     //   581: if_icmpne -> 590
/*     */     //   584: iinc #4, 1
/*     */     //   587: goto -> 559
/*     */     //   590: aload_2
/*     */     //   591: iconst_0
/*     */     //   592: invokevirtual setLength : (I)V
/*     */     //   595: iload #4
/*     */     //   597: iload #11
/*     */     //   599: if_icmpge -> 654
/*     */     //   602: aload #10
/*     */     //   604: iload #4
/*     */     //   606: iinc #4, 1
/*     */     //   609: baload
/*     */     //   610: istore_3
/*     */     //   611: iload_3
/*     */     //   612: bipush #13
/*     */     //   614: if_icmpne -> 620
/*     */     //   617: goto -> 595
/*     */     //   620: iload_3
/*     */     //   621: bipush #10
/*     */     //   623: if_icmpne -> 629
/*     */     //   626: goto -> 654
/*     */     //   629: iload_3
/*     */     //   630: bipush #32
/*     */     //   632: if_icmpeq -> 654
/*     */     //   635: iload_3
/*     */     //   636: bipush #9
/*     */     //   638: if_icmpne -> 644
/*     */     //   641: goto -> 654
/*     */     //   644: aload_2
/*     */     //   645: iload_3
/*     */     //   646: i2c
/*     */     //   647: invokevirtual append : (C)Ljava/lang/StringBuffer;
/*     */     //   650: pop
/*     */     //   651: goto -> 595
/*     */     //   654: aload_2
/*     */     //   655: invokevirtual toString : ()Ljava/lang/String;
/*     */     //   658: astore #8
/*     */     //   660: aload #8
/*     */     //   662: invokevirtual length : ()I
/*     */     //   665: ifne -> 683
/*     */     //   668: aload_0
/*     */     //   669: aload #10
/*     */     //   671: iconst_0
/*     */     //   672: iload #11
/*     */     //   674: invokestatic byte2str : ([BII)Ljava/lang/String;
/*     */     //   677: invokespecial addInvalidLine : (Ljava/lang/String;)V
/*     */     //   680: goto -> 34
/*     */     //   683: iload #4
/*     */     //   685: iload #11
/*     */     //   687: if_icmpge -> 714
/*     */     //   690: aload #10
/*     */     //   692: iload #4
/*     */     //   694: baload
/*     */     //   695: istore_3
/*     */     //   696: iload_3
/*     */     //   697: bipush #32
/*     */     //   699: if_icmpeq -> 708
/*     */     //   702: iload_3
/*     */     //   703: bipush #9
/*     */     //   705: if_icmpne -> 714
/*     */     //   708: iinc #4, 1
/*     */     //   711: goto -> 683
/*     */     //   714: aconst_null
/*     */     //   715: astore #14
/*     */     //   717: iload #4
/*     */     //   719: iload #11
/*     */     //   721: if_icmpge -> 779
/*     */     //   724: aload_2
/*     */     //   725: iconst_0
/*     */     //   726: invokevirtual setLength : (I)V
/*     */     //   729: iload #4
/*     */     //   731: iload #11
/*     */     //   733: if_icmpge -> 773
/*     */     //   736: aload #10
/*     */     //   738: iload #4
/*     */     //   740: iinc #4, 1
/*     */     //   743: baload
/*     */     //   744: istore_3
/*     */     //   745: iload_3
/*     */     //   746: bipush #13
/*     */     //   748: if_icmpne -> 754
/*     */     //   751: goto -> 729
/*     */     //   754: iload_3
/*     */     //   755: bipush #10
/*     */     //   757: if_icmpne -> 763
/*     */     //   760: goto -> 773
/*     */     //   763: aload_2
/*     */     //   764: iload_3
/*     */     //   765: i2c
/*     */     //   766: invokevirtual append : (C)Ljava/lang/StringBuffer;
/*     */     //   769: pop
/*     */     //   770: goto -> 729
/*     */     //   773: aload_2
/*     */     //   774: invokevirtual toString : ()Ljava/lang/String;
/*     */     //   777: astore #14
/*     */     //   779: aconst_null
/*     */     //   780: astore #15
/*     */     //   782: new com/jcraft/jsch/KnownHosts$HashedHostKey
/*     */     //   785: dup
/*     */     //   786: aload_0
/*     */     //   787: aload #12
/*     */     //   789: aload #7
/*     */     //   791: iload #9
/*     */     //   793: aload #8
/*     */     //   795: invokestatic str2byte : (Ljava/lang/String;)[B
/*     */     //   798: iconst_0
/*     */     //   799: aload #8
/*     */     //   801: invokevirtual length : ()I
/*     */     //   804: invokestatic fromBase64 : ([BII)[B
/*     */     //   807: aload #14
/*     */     //   809: invokespecial <init> : (Lcom/jcraft/jsch/KnownHosts;Ljava/lang/String;Ljava/lang/String;I[BLjava/lang/String;)V
/*     */     //   812: astore #15
/*     */     //   814: aload_0
/*     */     //   815: getfield pool : Ljava/util/Vector;
/*     */     //   818: aload #15
/*     */     //   820: invokevirtual addElement : (Ljava/lang/Object;)V
/*     */     //   823: goto -> 34
/*     */     //   826: iload #5
/*     */     //   828: ifeq -> 841
/*     */     //   831: new com/jcraft/jsch/JSchException
/*     */     //   834: dup
/*     */     //   835: ldc 'KnownHosts: invalid format'
/*     */     //   837: invokespecial <init> : (Ljava/lang/String;)V
/*     */     //   840: athrow
/*     */     //   841: aload_1
/*     */     //   842: invokevirtual close : ()V
/*     */     //   845: goto -> 946
/*     */     //   848: astore #6
/*     */     //   850: new com/jcraft/jsch/JSchException
/*     */     //   853: dup
/*     */     //   854: aload #6
/*     */     //   856: invokevirtual toString : ()Ljava/lang/String;
/*     */     //   859: aload #6
/*     */     //   861: invokespecial <init> : (Ljava/lang/String;Ljava/lang/Throwable;)V
/*     */     //   864: athrow
/*     */     //   865: astore #6
/*     */     //   867: aload #6
/*     */     //   869: instanceof com/jcraft/jsch/JSchException
/*     */     //   872: ifeq -> 881
/*     */     //   875: aload #6
/*     */     //   877: checkcast com/jcraft/jsch/JSchException
/*     */     //   880: athrow
/*     */     //   881: aload #6
/*     */     //   883: instanceof java/lang/Throwable
/*     */     //   886: ifeq -> 904
/*     */     //   889: new com/jcraft/jsch/JSchException
/*     */     //   892: dup
/*     */     //   893: aload #6
/*     */     //   895: invokevirtual toString : ()Ljava/lang/String;
/*     */     //   898: aload #6
/*     */     //   900: invokespecial <init> : (Ljava/lang/String;Ljava/lang/Throwable;)V
/*     */     //   903: athrow
/*     */     //   904: new com/jcraft/jsch/JSchException
/*     */     //   907: dup
/*     */     //   908: aload #6
/*     */     //   910: invokevirtual toString : ()Ljava/lang/String;
/*     */     //   913: invokespecial <init> : (Ljava/lang/String;)V
/*     */     //   916: athrow
/*     */     //   917: astore #16
/*     */     //   919: aload_1
/*     */     //   920: invokevirtual close : ()V
/*     */     //   923: goto -> 943
/*     */     //   926: astore #17
/*     */     //   928: new com/jcraft/jsch/JSchException
/*     */     //   931: dup
/*     */     //   932: aload #17
/*     */     //   934: invokevirtual toString : ()Ljava/lang/String;
/*     */     //   937: aload #17
/*     */     //   939: invokespecial <init> : (Ljava/lang/String;Ljava/lang/Throwable;)V
/*     */     //   942: athrow
/*     */     //   943: aload #16
/*     */     //   945: athrow
/*     */     //   946: return
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #62	-> 0
/*     */     //   #63	-> 7
/*     */     //   #66	-> 15
/*     */     //   #68	-> 18
/*     */     //   #70	-> 21
/*     */     //   #72	-> 24
/*     */     //   #73	-> 31
/*     */     //   #76	-> 34
/*     */     //   #78	-> 37
/*     */     //   #79	-> 44
/*     */     //   #80	-> 50
/*     */     //   #83	-> 58
/*     */     //   #84	-> 68
/*     */     //   #85	-> 78
/*     */     //   #86	-> 86
/*     */     //   #87	-> 97
/*     */     //   #88	-> 106
/*     */     //   #89	-> 118
/*     */     //   #91	-> 122
/*     */     //   #94	-> 136
/*     */     //   #95	-> 139
/*     */     //   #96	-> 146
/*     */     //   #97	-> 152
/*     */     //   #98	-> 170
/*     */     //   #99	-> 176
/*     */     //   #100	-> 188
/*     */     //   #104	-> 191
/*     */     //   #105	-> 198
/*     */     //   #106	-> 210
/*     */     //   #109	-> 213
/*     */     //   #110	-> 218
/*     */     //   #111	-> 225
/*     */     //   #112	-> 234
/*     */     //   #113	-> 249
/*     */     //   #115	-> 259
/*     */     //   #116	-> 265
/*     */     //   #117	-> 280
/*     */     //   #118	-> 292
/*     */     //   #121	-> 295
/*     */     //   #122	-> 302
/*     */     //   #123	-> 308
/*     */     //   #127	-> 326
/*     */     //   #128	-> 330
/*     */     //   #129	-> 341
/*     */     //   #131	-> 345
/*     */     //   #132	-> 350
/*     */     //   #133	-> 357
/*     */     //   #134	-> 366
/*     */     //   #135	-> 381
/*     */     //   #137	-> 391
/*     */     //   #138	-> 397
/*     */     //   #139	-> 412
/*     */     //   #140	-> 424
/*     */     //   #143	-> 427
/*     */     //   #144	-> 434
/*     */     //   #145	-> 440
/*     */     //   #150	-> 458
/*     */     //   #151	-> 463
/*     */     //   #152	-> 466
/*     */     //   #153	-> 473
/*     */     //   #154	-> 482
/*     */     //   #155	-> 497
/*     */     //   #157	-> 507
/*     */     //   #158	-> 513
/*     */     //   #159	-> 523
/*     */     //   #161	-> 533
/*     */     //   #162	-> 537
/*     */     //   #163	-> 544
/*     */     //   #164	-> 556
/*     */     //   #167	-> 559
/*     */     //   #168	-> 566
/*     */     //   #169	-> 572
/*     */     //   #173	-> 590
/*     */     //   #174	-> 595
/*     */     //   #175	-> 602
/*     */     //   #176	-> 611
/*     */     //   #177	-> 620
/*     */     //   #178	-> 629
/*     */     //   #179	-> 644
/*     */     //   #181	-> 654
/*     */     //   #182	-> 660
/*     */     //   #183	-> 668
/*     */     //   #184	-> 680
/*     */     //   #187	-> 683
/*     */     //   #188	-> 690
/*     */     //   #189	-> 696
/*     */     //   #203	-> 714
/*     */     //   #204	-> 717
/*     */     //   #205	-> 724
/*     */     //   #206	-> 729
/*     */     //   #207	-> 736
/*     */     //   #208	-> 745
/*     */     //   #209	-> 754
/*     */     //   #210	-> 763
/*     */     //   #212	-> 773
/*     */     //   #218	-> 779
/*     */     //   #219	-> 782
/*     */     //   #222	-> 814
/*     */     //   #223	-> 823
/*     */     //   #224	-> 826
/*     */     //   #225	-> 831
/*     */     //   #236	-> 841
/*     */     //   #239	-> 845
/*     */     //   #237	-> 848
/*     */     //   #238	-> 850
/*     */     //   #228	-> 865
/*     */     //   #229	-> 867
/*     */     //   #230	-> 875
/*     */     //   #231	-> 881
/*     */     //   #232	-> 889
/*     */     //   #233	-> 904
/*     */     //   #236	-> 917
/*     */     //   #239	-> 923
/*     */     //   #237	-> 926
/*     */     //   #238	-> 928
/*     */     //   #241	-> 946
/*     */     // Local variable table:
/*     */     //   start	length	slot	name	descriptor
/*     */     //   106	16	12	newbuf	[B
/*     */     //   152	39	3	i	B
/*     */     //   234	25	3	i	B
/*     */     //   308	18	3	i	B
/*     */     //   366	25	3	i	B
/*     */     //   440	18	3	i	B
/*     */     //   482	25	3	i	B
/*     */     //   572	18	3	i	B
/*     */     //   611	43	3	i	B
/*     */     //   696	18	3	i	B
/*     */     //   745	28	3	i	B
/*     */     //   330	493	12	marker	Ljava/lang/String;
/*     */     //   513	310	13	tmp	Ljava/lang/String;
/*     */     //   717	106	14	comment	Ljava/lang/String;
/*     */     //   782	41	15	hk	Lcom/jcraft/jsch/HostKey;
/*     */     //   265	561	7	host	Ljava/lang/String;
/*     */     //   466	360	9	type	I
/*     */     //   21	820	6	fis	Ljava/io/InputStream;
/*     */     //   24	817	8	key	Ljava/lang/String;
/*     */     //   31	810	10	buf	[B
/*     */     //   34	807	11	bufl	I
/*     */     //   850	15	6	e	Ljava/io/IOException;
/*     */     //   867	50	6	e	Ljava/lang/Exception;
/*     */     //   928	15	17	e	Ljava/io/IOException;
/*     */     //   0	947	0	this	Lcom/jcraft/jsch/KnownHosts;
/*     */     //   0	947	1	input	Ljava/io/InputStream;
/*     */     //   15	932	2	sb	Ljava/lang/StringBuffer;
/*     */     //   44	903	4	j	I
/*     */     //   18	929	5	error	Z
/*     */     // Exception table:
/*     */     //   from	to	target	type
/*     */     //   18	841	865	java/lang/Exception
/*     */     //   18	841	917	finally
/*     */     //   841	845	848	java/io/IOException
/*     */     //   865	919	917	finally
/*     */     //   919	923	926	java/io/IOException
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
/*     */   private void addInvalidLine(String line) throws JSchException {
/* 243 */     HostKey hk = new HostKey(line, 6, null);
/* 244 */     this.pool.addElement(hk);
/*     */   }
/* 246 */   String getKnownHostsFile() { return this.known_hosts; } public String getKnownHostsRepositoryID() {
/* 247 */     return this.known_hosts;
/*     */   }
/*     */   public int check(String host, byte[] key) {
/* 250 */     int result = 1;
/* 251 */     if (host == null) {
/* 252 */       return result;
/*     */     }
/*     */     
/* 255 */     HostKey hk = null;
/*     */     try {
/* 257 */       hk = new HostKey(host, 0, key);
/*     */     }
/* 259 */     catch (JSchException e) {
/* 260 */       return result;
/*     */     } 
/*     */     
/* 263 */     synchronized (this.pool) {
/* 264 */       for (int i = 0; i < this.pool.size(); i++) {
/* 265 */         HostKey _hk = this.pool.elementAt(i);
/* 266 */         if (_hk.isMatched(host) && _hk.type == hk.type) {
/* 267 */           if (Util.array_equals(_hk.key, key)) {
/* 268 */             return 0;
/*     */           }
/*     */           
/* 271 */           result = 2;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 277 */     if (result == 1 && host.startsWith("[") && host.indexOf("]:") > 1)
/*     */     {
/*     */ 
/*     */       
/* 281 */       return check(host.substring(1, host.indexOf("]:")), key);
/*     */     }
/*     */     
/* 284 */     return result;
/*     */   }
/*     */   
/*     */   public void add(HostKey hostkey, UserInfo userinfo) {
/* 288 */     int type = hostkey.type;
/* 289 */     String host = hostkey.getHost();
/* 290 */     byte[] key = hostkey.key;
/*     */     
/* 292 */     HostKey hk = null;
/* 293 */     synchronized (this.pool) {
/* 294 */       for (int i = 0; i < this.pool.size(); i++) {
/* 295 */         hk = this.pool.elementAt(i);
/* 296 */         if (!hk.isMatched(host) || hk.type == type);
/*     */       } 
/*     */     } 
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
/* 312 */     hk = hostkey;
/*     */     
/* 314 */     this.pool.addElement(hk);
/*     */     
/* 316 */     String bar = getKnownHostsRepositoryID();
/* 317 */     if (bar != null) {
/* 318 */       boolean foo = true;
/* 319 */       File goo = new File(Util.checkTilde(bar));
/* 320 */       if (!goo.exists()) {
/* 321 */         foo = false;
/* 322 */         if (userinfo != null) {
/* 323 */           foo = userinfo.promptYesNo(bar + " does not exist.\n" + "Are you sure you want to create it?");
/*     */ 
/*     */           
/* 326 */           goo = goo.getParentFile();
/* 327 */           if (foo && goo != null && !goo.exists()) {
/* 328 */             foo = userinfo.promptYesNo("The parent directory " + goo + " does not exist.\n" + "Are you sure you want to create it?");
/*     */ 
/*     */             
/* 331 */             if (foo) {
/* 332 */               if (!goo.mkdirs()) {
/* 333 */                 userinfo.showMessage(goo + " has not been created.");
/* 334 */                 foo = false;
/*     */               } else {
/*     */                 
/* 337 */                 userinfo.showMessage(goo + " has been succesfully created.\nPlease check its access permission.");
/*     */               } 
/*     */             }
/*     */           } 
/* 341 */           if (goo == null) foo = false; 
/*     */         } 
/*     */       } 
/* 344 */       if (foo)
/*     */         try {
/* 346 */           sync(bar);
/*     */         } catch (Exception e) {
/* 348 */           System.err.println("sync known_hosts: " + e);
/*     */         }  
/*     */     } 
/*     */   }
/*     */   
/*     */   public HostKey[] getHostKey() {
/* 354 */     return getHostKey(null, (String)null);
/*     */   }
/*     */   public HostKey[] getHostKey(String host, String type) {
/* 357 */     synchronized (this.pool) {
/* 358 */       ArrayList v = new ArrayList();
/* 359 */       for (int i = 0; i < this.pool.size(); i++) {
/* 360 */         HostKey hk = this.pool.elementAt(i);
/* 361 */         if (hk.type != 6 && (
/* 362 */           host == null || (hk.isMatched(host) && (type == null || hk.getType().equals(type)))))
/*     */         {
/*     */           
/* 365 */           v.add(hk);
/*     */         }
/*     */       } 
/* 368 */       HostKey[] foo = new HostKey[v.size()];
/* 369 */       for (int j = 0; j < v.size(); j++) {
/* 370 */         foo[j] = v.get(j);
/*     */       }
/* 372 */       if (host != null && host.startsWith("[") && host.indexOf("]:") > 1) {
/* 373 */         HostKey[] tmp = getHostKey(host.substring(1, host.indexOf("]:")), type);
/*     */         
/* 375 */         if (tmp.length > 0) {
/* 376 */           HostKey[] bar = new HostKey[foo.length + tmp.length];
/* 377 */           System.arraycopy(foo, 0, bar, 0, foo.length);
/* 378 */           System.arraycopy(tmp, 0, bar, foo.length, tmp.length);
/* 379 */           foo = bar;
/*     */         } 
/*     */       } 
/* 382 */       return foo;
/*     */     } 
/*     */   }
/*     */   public void remove(String host, String type) {
/* 386 */     remove(host, type, null);
/*     */   }
/*     */   public void remove(String host, String type, byte[] key) {
/* 389 */     boolean sync = false;
/* 390 */     synchronized (this.pool) {
/* 391 */       for (int i = 0; i < this.pool.size(); i++) {
/* 392 */         HostKey hk = this.pool.elementAt(i);
/* 393 */         if (host == null || (hk.isMatched(host) && (type == null || (hk.getType().equals(type) && (key == null || Util.array_equals(key, hk.key)))))) {
/*     */ 
/*     */ 
/*     */           
/* 397 */           String hosts = hk.getHost();
/* 398 */           if (hosts.equals(host) || (hk instanceof HashedHostKey && ((HashedHostKey)hk).isHashed())) {
/*     */ 
/*     */             
/* 401 */             this.pool.removeElement(hk);
/*     */           } else {
/*     */             
/* 404 */             hk.host = deleteSubString(hosts, host);
/*     */           } 
/* 406 */           sync = true;
/*     */         } 
/*     */       } 
/*     */     } 
/* 410 */     if (sync) {
/* 411 */       try { sync(); } catch (Exception e) {}
/*     */     }
/*     */   }
/*     */   
/*     */   protected void sync() throws IOException {
/* 416 */     if (this.known_hosts != null)
/* 417 */       sync(this.known_hosts); 
/*     */   }
/*     */   protected synchronized void sync(String foo) throws IOException {
/* 420 */     if (foo == null)
/* 421 */       return;  FileOutputStream fos = new FileOutputStream(Util.checkTilde(foo));
/* 422 */     dump(fos);
/* 423 */     fos.close();
/*     */   }
/*     */   
/* 426 */   private static final byte[] space = new byte[] { 32 };
/* 427 */   private static final byte[] cr = Util.str2byte("\n");
/*     */   
/*     */   void dump(OutputStream out) throws IOException {
/*     */     try {
/* 431 */       synchronized (this.pool) {
/* 432 */         for (int i = 0; i < this.pool.size(); i++) {
/* 433 */           HostKey hk = this.pool.elementAt(i);
/*     */           
/* 435 */           String marker = hk.getMarker();
/* 436 */           String host = hk.getHost();
/* 437 */           String type = hk.getType();
/* 438 */           String comment = hk.getComment();
/* 439 */           if (type.equals("UNKNOWN")) {
/* 440 */             out.write(Util.str2byte(host));
/* 441 */             out.write(cr);
/*     */           } else {
/*     */             
/* 444 */             if (marker.length() != 0) {
/* 445 */               out.write(Util.str2byte(marker));
/* 446 */               out.write(space);
/*     */             } 
/* 448 */             out.write(Util.str2byte(host));
/* 449 */             out.write(space);
/* 450 */             out.write(Util.str2byte(type));
/* 451 */             out.write(space);
/* 452 */             out.write(Util.str2byte(hk.getKey()));
/* 453 */             if (comment != null) {
/* 454 */               out.write(space);
/* 455 */               out.write(Util.str2byte(comment));
/*     */             } 
/* 457 */             out.write(cr);
/*     */           } 
/*     */         } 
/*     */       } 
/* 461 */     } catch (Exception e) {
/* 462 */       System.err.println(e);
/*     */     } 
/*     */   }
/*     */   
/*     */   private String deleteSubString(String hosts, String host) {
/* 467 */     int i = 0;
/* 468 */     int hostlen = host.length();
/* 469 */     int hostslen = hosts.length();
/*     */     
/* 471 */     while (i < hostslen) {
/* 472 */       int j = hosts.indexOf(',', i);
/* 473 */       if (j == -1)
/* 474 */         break;  if (!host.equals(hosts.substring(i, j))) {
/* 475 */         i = j + 1;
/*     */         continue;
/*     */       } 
/* 478 */       return hosts.substring(0, i) + hosts.substring(j + 1);
/*     */     } 
/* 480 */     if (hosts.endsWith(host) && hostslen - i == hostlen) {
/* 481 */       return hosts.substring(0, (hostlen == hostslen) ? 0 : (hostslen - hostlen - 1));
/*     */     }
/* 483 */     return hosts;
/*     */   }
/*     */   
/*     */   private MAC getHMACSHA1() {
/* 487 */     if (this.hmacsha1 == null) {
/*     */       try {
/* 489 */         Class c = Class.forName(JSch.getConfig("hmac-sha1"));
/* 490 */         this.hmacsha1 = (MAC)c.newInstance();
/*     */       }
/* 492 */       catch (Exception e) {
/* 493 */         System.err.println("hmacsha1: " + e);
/*     */       } 
/*     */     }
/* 496 */     return this.hmacsha1;
/*     */   }
/*     */   
/*     */   HostKey createHashedHostKey(String host, byte[] key) throws JSchException {
/* 500 */     HashedHostKey hhk = new HashedHostKey(host, key);
/* 501 */     hhk.hash();
/* 502 */     return hhk;
/*     */   }
/*     */   
/*     */   class HashedHostKey extends HostKey {
/*     */     private static final String HASH_MAGIC = "|1|";
/*     */     private static final String HASH_DELIM = "|";
/*     */     private boolean hashed = false;
/* 509 */     byte[] salt = null;
/* 510 */     byte[] hash = null; private final KnownHosts this$0;
/*     */     
/*     */     HashedHostKey(String host, byte[] key) throws JSchException {
/* 513 */       this(host, 0, key);
/*     */     }
/*     */     HashedHostKey(String host, int type, byte[] key) throws JSchException {
/* 516 */       this("", host, type, key, null);
/*     */     }
/*     */     HashedHostKey(String marker, String host, int type, byte[] key, String comment) throws JSchException {
/* 519 */       super(marker, host, type, key, comment);
/* 520 */       if (this.host.startsWith("|1|") && this.host.substring("|1|".length()).indexOf("|") > 0) {
/*     */         
/* 522 */         String data = this.host.substring("|1|".length());
/* 523 */         String _salt = data.substring(0, data.indexOf("|"));
/* 524 */         String _hash = data.substring(data.indexOf("|") + 1);
/* 525 */         this.salt = Util.fromBase64(Util.str2byte(_salt), 0, _salt.length());
/* 526 */         this.hash = Util.fromBase64(Util.str2byte(_hash), 0, _hash.length());
/* 527 */         if (this.salt.length != 20 || this.hash.length != 20) {
/*     */           
/* 529 */           this.salt = null;
/* 530 */           this.hash = null;
/*     */           return;
/*     */         } 
/* 533 */         this.hashed = true;
/*     */       } 
/*     */     }
/*     */     
/*     */     boolean isMatched(String _host) {
/* 538 */       if (!this.hashed) {
/* 539 */         return super.isMatched(_host);
/*     */       }
/* 541 */       MAC macsha1 = KnownHosts.this.getHMACSHA1();
/*     */       try {
/* 543 */         synchronized (macsha1) {
/* 544 */           macsha1.init(this.salt);
/* 545 */           byte[] foo = Util.str2byte(_host);
/* 546 */           macsha1.update(foo, 0, foo.length);
/* 547 */           byte[] bar = new byte[macsha1.getBlockSize()];
/* 548 */           macsha1.doFinal(bar, 0);
/* 549 */           return Util.array_equals(this.hash, bar);
/*     */         }
/*     */       
/* 552 */       } catch (Exception e) {
/* 553 */         System.out.println(e);
/*     */         
/* 555 */         return false;
/*     */       } 
/*     */     }
/*     */     boolean isHashed() {
/* 559 */       return this.hashed;
/*     */     }
/*     */     
/*     */     void hash() {
/* 563 */       if (this.hashed)
/*     */         return; 
/* 565 */       MAC macsha1 = KnownHosts.this.getHMACSHA1();
/* 566 */       if (this.salt == null) {
/* 567 */         Random random = Session.random;
/* 568 */         synchronized (random) {
/* 569 */           this.salt = new byte[macsha1.getBlockSize()];
/* 570 */           random.fill(this.salt, 0, this.salt.length);
/*     */         } 
/*     */       } 
/*     */       try {
/* 574 */         synchronized (macsha1) {
/* 575 */           macsha1.init(this.salt);
/* 576 */           byte[] foo = Util.str2byte(this.host);
/* 577 */           macsha1.update(foo, 0, foo.length);
/* 578 */           this.hash = new byte[macsha1.getBlockSize()];
/* 579 */           macsha1.doFinal(this.hash, 0);
/*     */         }
/*     */       
/* 582 */       } catch (Exception e) {}
/*     */       
/* 584 */       this.host = "|1|" + Util.byte2str(Util.toBase64(this.salt, 0, this.salt.length)) + "|" + Util.byte2str(Util.toBase64(this.hash, 0, this.hash.length));
/*     */       
/* 586 */       this.hashed = true;
/*     */     }
/*     */   }
/*     */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\KnownHosts.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */