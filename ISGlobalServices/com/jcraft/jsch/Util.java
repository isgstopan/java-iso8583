/*     */ package com.jcraft.jsch;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.net.Socket;
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
/*     */ class Util
/*     */ {
/*  38 */   private static final byte[] b64 = str2byte("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=");
/*     */   private static byte val(byte foo) {
/*  40 */     if (foo == 61) return 0; 
/*  41 */     for (int j = 0; j < b64.length; j++) {
/*  42 */       if (foo == b64[j]) return (byte)j; 
/*     */     } 
/*  44 */     return 0;
/*     */   }
/*     */   static byte[] fromBase64(byte[] buf, int start, int length) throws JSchException {
/*     */     try {
/*  48 */       byte[] foo = new byte[length];
/*  49 */       int j = 0;
/*  50 */       for (int i = start; i < start + length; i += 4) {
/*  51 */         foo[j] = (byte)(val(buf[i]) << 2 | (val(buf[i + 1]) & 0x30) >>> 4);
/*  52 */         if (buf[i + 2] == 61) { j++; break; }
/*  53 */          foo[j + 1] = (byte)((val(buf[i + 1]) & 0xF) << 4 | (val(buf[i + 2]) & 0x3C) >>> 2);
/*  54 */         if (buf[i + 3] == 61) { j += 2; break; }
/*  55 */          foo[j + 2] = (byte)((val(buf[i + 2]) & 0x3) << 6 | val(buf[i + 3]) & 0x3F);
/*  56 */         j += 3;
/*     */       } 
/*  58 */       byte[] bar = new byte[j];
/*  59 */       System.arraycopy(foo, 0, bar, 0, j);
/*  60 */       return bar;
/*     */     }
/*  62 */     catch (ArrayIndexOutOfBoundsException e) {
/*  63 */       throw new JSchException("fromBase64: invalid base64 data", e);
/*     */     } 
/*     */   }
/*     */   
/*     */   static byte[] toBase64(byte[] buf, int start, int length) {
/*  68 */     byte[] tmp = new byte[length * 2];
/*     */ 
/*     */     
/*  71 */     int foo = length / 3 * 3 + start;
/*  72 */     int i = 0; int j;
/*  73 */     for (j = start; j < foo; j += 3) {
/*  74 */       int k = buf[j] >>> 2 & 0x3F;
/*  75 */       tmp[i++] = b64[k];
/*  76 */       k = (buf[j] & 0x3) << 4 | buf[j + 1] >>> 4 & 0xF;
/*  77 */       tmp[i++] = b64[k];
/*  78 */       k = (buf[j + 1] & 0xF) << 2 | buf[j + 2] >>> 6 & 0x3;
/*  79 */       tmp[i++] = b64[k];
/*  80 */       k = buf[j + 2] & 0x3F;
/*  81 */       tmp[i++] = b64[k];
/*     */     } 
/*     */     
/*  84 */     foo = start + length - foo;
/*  85 */     if (foo == 1) {
/*  86 */       int k = buf[j] >>> 2 & 0x3F;
/*  87 */       tmp[i++] = b64[k];
/*  88 */       k = (buf[j] & 0x3) << 4 & 0x3F;
/*  89 */       tmp[i++] = b64[k];
/*  90 */       tmp[i++] = 61;
/*  91 */       tmp[i++] = 61;
/*     */     }
/*  93 */     else if (foo == 2) {
/*  94 */       int k = buf[j] >>> 2 & 0x3F;
/*  95 */       tmp[i++] = b64[k];
/*  96 */       k = (buf[j] & 0x3) << 4 | buf[j + 1] >>> 4 & 0xF;
/*  97 */       tmp[i++] = b64[k];
/*  98 */       k = (buf[j + 1] & 0xF) << 2 & 0x3F;
/*  99 */       tmp[i++] = b64[k];
/* 100 */       tmp[i++] = 61;
/*     */     } 
/* 102 */     byte[] bar = new byte[i];
/* 103 */     System.arraycopy(tmp, 0, bar, 0, i);
/* 104 */     return bar;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static String[] split(String foo, String split) {
/* 110 */     if (foo == null)
/* 111 */       return null; 
/* 112 */     byte[] buf = str2byte(foo);
/* 113 */     Vector bar = new Vector();
/* 114 */     int start = 0;
/*     */     
/*     */     while (true) {
/* 117 */       int index = foo.indexOf(split, start);
/* 118 */       if (index >= 0) {
/* 119 */         bar.addElement(byte2str(buf, start, index - start));
/* 120 */         start = index + 1; continue;
/*     */       }  break;
/*     */     } 
/* 123 */     bar.addElement(byte2str(buf, start, buf.length - start));
/*     */ 
/*     */     
/* 126 */     String[] result = new String[bar.size()];
/* 127 */     for (int i = 0; i < result.length; i++) {
/* 128 */       result[i] = bar.elementAt(i);
/*     */     }
/* 130 */     return result;
/*     */   }
/*     */   static boolean glob(byte[] pattern, byte[] name) {
/* 133 */     return glob0(pattern, 0, name, 0);
/*     */   }
/*     */   
/*     */   private static boolean glob0(byte[] pattern, int pattern_index, byte[] name, int name_index) {
/* 137 */     if (name.length > 0 && name[0] == 46) {
/* 138 */       if (pattern.length > 0 && pattern[0] == 46) {
/* 139 */         if (pattern.length == 2 && pattern[1] == 42) return true; 
/* 140 */         return glob(pattern, pattern_index + 1, name, name_index + 1);
/*     */       } 
/* 142 */       return false;
/*     */     } 
/* 144 */     return glob(pattern, pattern_index, name, name_index);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean glob(byte[] pattern, int pattern_index, byte[] name, int name_index) {
/* 150 */     int patternlen = pattern.length;
/* 151 */     if (patternlen == 0) {
/* 152 */       return false;
/*     */     }
/* 154 */     int namelen = name.length;
/* 155 */     int i = pattern_index;
/* 156 */     int j = name_index;
/*     */     
/* 158 */     while (i < patternlen && j < namelen) {
/* 159 */       if (pattern[i] == 92) {
/* 160 */         if (i + 1 == patternlen)
/* 161 */           return false; 
/* 162 */         i++;
/* 163 */         if (pattern[i] != name[j])
/* 164 */           return false; 
/* 165 */         i += skipUTF8Char(pattern[i]);
/* 166 */         j += skipUTF8Char(name[j]);
/*     */         
/*     */         continue;
/*     */       } 
/* 170 */       if (pattern[i] == 42) {
/* 171 */         while (i < patternlen && 
/* 172 */           pattern[i] == 42) {
/* 173 */           i++;
/*     */         }
/*     */ 
/*     */ 
/*     */         
/* 178 */         if (patternlen == i) {
/* 179 */           return true;
/*     */         }
/* 181 */         byte foo = pattern[i];
/* 182 */         if (foo == 63) {
/* 183 */           while (j < namelen) {
/* 184 */             if (glob(pattern, i, name, j)) {
/* 185 */               return true;
/*     */             }
/* 187 */             j += skipUTF8Char(name[j]);
/*     */           } 
/* 189 */           return false;
/*     */         } 
/* 191 */         if (foo == 92) {
/* 192 */           if (i + 1 == patternlen)
/* 193 */             return false; 
/* 194 */           i++;
/* 195 */           foo = pattern[i];
/* 196 */           while (j < namelen) {
/* 197 */             if (foo == name[j] && 
/* 198 */               glob(pattern, i + skipUTF8Char(foo), name, j + skipUTF8Char(name[j])))
/*     */             {
/* 200 */               return true;
/*     */             }
/*     */             
/* 203 */             j += skipUTF8Char(name[j]);
/*     */           } 
/* 205 */           return false;
/*     */         } 
/*     */         
/* 208 */         while (j < namelen) {
/* 209 */           if (foo == name[j] && 
/* 210 */             glob(pattern, i, name, j)) {
/* 211 */             return true;
/*     */           }
/*     */           
/* 214 */           j += skipUTF8Char(name[j]);
/*     */         } 
/* 216 */         return false;
/*     */       } 
/*     */       
/* 219 */       if (pattern[i] == 63) {
/* 220 */         i++;
/* 221 */         j += skipUTF8Char(name[j]);
/*     */         
/*     */         continue;
/*     */       } 
/* 225 */       if (pattern[i] != name[j]) {
/* 226 */         return false;
/*     */       }
/* 228 */       i += skipUTF8Char(pattern[i]);
/* 229 */       j += skipUTF8Char(name[j]);
/*     */       
/* 231 */       if (j >= namelen) {
/* 232 */         if (i >= patternlen) {
/* 233 */           return true;
/*     */         }
/* 235 */         if (pattern[i] == 42) {
/*     */           break;
/*     */         }
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 242 */     if (i == patternlen && j == namelen) {
/* 243 */       return true;
/*     */     }
/* 245 */     if (j >= namelen && pattern[i] == 42) {
/*     */       
/* 247 */       boolean ok = true;
/* 248 */       while (i < patternlen) {
/* 249 */         if (pattern[i++] != 42) {
/* 250 */           ok = false;
/*     */           break;
/*     */         } 
/*     */       } 
/* 254 */       return ok;
/*     */     } 
/*     */     
/* 257 */     return false;
/*     */   }
/*     */   
/*     */   static String quote(String path) {
/* 261 */     byte[] _path = str2byte(path);
/* 262 */     int count = 0;
/* 263 */     for (int i = 0; i < _path.length; i++) {
/* 264 */       byte b = _path[i];
/* 265 */       if (b == 92 || b == 63 || b == 42)
/* 266 */         count++; 
/*     */     } 
/* 268 */     if (count == 0)
/* 269 */       return path; 
/* 270 */     byte[] _path2 = new byte[_path.length + count];
/* 271 */     for (int k = 0, j = 0; k < _path.length; k++) {
/* 272 */       byte b = _path[k];
/* 273 */       if (b == 92 || b == 63 || b == 42) {
/* 274 */         _path2[j++] = 92;
/*     */       }
/* 276 */       _path2[j++] = b;
/*     */     } 
/* 278 */     return byte2str(_path2);
/*     */   }
/*     */   
/*     */   static String unquote(String path) {
/* 282 */     byte[] foo = str2byte(path);
/* 283 */     byte[] bar = unquote(foo);
/* 284 */     if (foo.length == bar.length)
/* 285 */       return path; 
/* 286 */     return byte2str(bar);
/*     */   }
/*     */   static byte[] unquote(byte[] path) {
/* 289 */     int pathlen = path.length;
/* 290 */     int i = 0;
/* 291 */     while (i < pathlen) {
/* 292 */       if (path[i] == 92) {
/* 293 */         if (i + 1 == pathlen)
/*     */           break; 
/* 295 */         System.arraycopy(path, i + 1, path, i, path.length - i + 1);
/* 296 */         pathlen--;
/* 297 */         i++;
/*     */         continue;
/*     */       } 
/* 300 */       i++;
/*     */     } 
/* 302 */     if (pathlen == path.length)
/* 303 */       return path; 
/* 304 */     byte[] foo = new byte[pathlen];
/* 305 */     System.arraycopy(path, 0, foo, 0, pathlen);
/* 306 */     return foo;
/*     */   }
/*     */   
/* 309 */   private static String[] chars = new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
/*     */ 
/*     */   
/*     */   static String getFingerPrint(HASH hash, byte[] data) {
/*     */     try {
/* 314 */       hash.init();
/* 315 */       hash.update(data, 0, data.length);
/* 316 */       byte[] foo = hash.digest();
/* 317 */       StringBuffer sb = new StringBuffer();
/*     */       
/* 319 */       for (int i = 0; i < foo.length; i++) {
/* 320 */         int bar = foo[i] & 0xFF;
/* 321 */         sb.append(chars[bar >>> 4 & 0xF]);
/* 322 */         sb.append(chars[bar & 0xF]);
/* 323 */         if (i + 1 < foo.length)
/* 324 */           sb.append(":"); 
/*     */       } 
/* 326 */       return sb.toString();
/*     */     }
/* 328 */     catch (Exception e) {
/* 329 */       return "???";
/*     */     } 
/*     */   }
/*     */   static boolean array_equals(byte[] foo, byte[] bar) {
/* 333 */     int i = foo.length;
/* 334 */     if (i != bar.length) return false; 
/* 335 */     for (int j = 0; j < i; ) { if (foo[j] != bar[j]) return false;  j++; }
/*     */     
/* 337 */     return true;
/*     */   }
/*     */   static Socket createSocket(String host, int port, int timeout) throws JSchException {
/* 340 */     Socket socket = null;
/* 341 */     if (timeout == 0) {
/*     */       try {
/* 343 */         socket = new Socket(host, port);
/* 344 */         return socket;
/*     */       }
/* 346 */       catch (Exception e) {
/* 347 */         String str = e.toString();
/* 348 */         if (e instanceof Throwable)
/* 349 */           throw new JSchException(str, e); 
/* 350 */         throw new JSchException(str);
/*     */       } 
/*     */     }
/* 353 */     final String _host = host;
/* 354 */     final int _port = port;
/* 355 */     final Socket[] sockp = new Socket[1];
/* 356 */     final Exception[] ee = new Exception[1];
/* 357 */     String message = "";
/* 358 */     Thread tmp = new Thread(new Runnable() { private final Socket[] val$sockp; private final String val$_host;
/*     */           public void run() {
/* 360 */             sockp[0] = null;
/*     */             try {
/* 362 */               sockp[0] = new Socket(_host, _port);
/*     */             }
/* 364 */             catch (Exception e) {
/* 365 */               ee[0] = e;
/* 366 */               if (sockp[0] != null && sockp[0].isConnected()) {
/*     */                 try {
/* 368 */                   sockp[0].close();
/*     */                 }
/* 370 */                 catch (Exception eee) {}
/*     */               }
/* 372 */               sockp[0] = null;
/*     */             } 
/*     */           } private final int val$_port; private final Exception[] val$ee; }
/*     */       );
/* 376 */     tmp.setName("Opening Socket " + host);
/* 377 */     tmp.start();
/*     */     try {
/* 379 */       tmp.join(timeout);
/* 380 */       message = "timeout: ";
/*     */     }
/* 382 */     catch (InterruptedException eee) {}
/*     */     
/* 384 */     if (sockp[0] != null && sockp[0].isConnected()) {
/* 385 */       socket = sockp[0];
/*     */     } else {
/*     */       
/* 388 */       message = message + "socket is not established";
/* 389 */       if (ee[0] != null) {
/* 390 */         message = ee[0].toString();
/*     */       }
/* 392 */       tmp.interrupt();
/* 393 */       tmp = null;
/* 394 */       throw new JSchException(message, ee[0]);
/*     */     } 
/* 396 */     return socket;
/*     */   }
/*     */   
/*     */   static byte[] str2byte(String str, String encoding) {
/* 400 */     if (str == null)
/* 401 */       return null;  try {
/* 402 */       return str.getBytes(encoding);
/* 403 */     } catch (UnsupportedEncodingException e) {
/* 404 */       return str.getBytes();
/*     */     } 
/*     */   }
/*     */   
/*     */   static byte[] str2byte(String str) {
/* 409 */     return str2byte(str, "UTF-8");
/*     */   }
/*     */   
/*     */   static String byte2str(byte[] str, String encoding) {
/* 413 */     return byte2str(str, 0, str.length, encoding);
/*     */   }
/*     */   static String byte2str(byte[] str, int s, int l, String encoding) {
/*     */     try {
/* 417 */       return new String(str, s, l, encoding);
/* 418 */     } catch (UnsupportedEncodingException e) {
/* 419 */       return new String(str, s, l);
/*     */     } 
/*     */   }
/*     */   
/*     */   static String byte2str(byte[] str) {
/* 424 */     return byte2str(str, 0, str.length, "UTF-8");
/*     */   }
/*     */   
/*     */   static String byte2str(byte[] str, int s, int l) {
/* 428 */     return byte2str(str, s, l, "UTF-8");
/*     */   }
/*     */   
/*     */   static String toHex(byte[] str) {
/* 432 */     StringBuffer sb = new StringBuffer();
/* 433 */     for (int i = 0; i < str.length; i++) {
/* 434 */       String foo = Integer.toHexString(str[i] & 0xFF);
/* 435 */       sb.append("0x" + ((foo.length() == 1) ? "0" : "") + foo);
/* 436 */       if (i + 1 < str.length)
/* 437 */         sb.append(":"); 
/*     */     } 
/* 439 */     return sb.toString();
/*     */   }
/*     */   
/* 442 */   static final byte[] empty = str2byte("");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static void bzero(byte[] foo) {
/* 465 */     if (foo == null)
/*     */       return; 
/* 467 */     for (int i = 0; i < foo.length; i++)
/* 468 */       foo[i] = 0; 
/*     */   }
/*     */   
/*     */   static String diffString(String str, String[] not_available) {
/* 472 */     String[] stra = split(str, ",");
/* 473 */     String result = null;
/*     */     int i;
/* 475 */     label18: for (i = 0; i < stra.length; i++) {
/* 476 */       for (int j = 0; j < not_available.length; j++) {
/* 477 */         if (stra[i].equals(not_available[j])) {
/*     */           continue label18;
/*     */         }
/*     */       } 
/* 481 */       if (result == null) { result = stra[i]; }
/* 482 */       else { result = result + "," + stra[i]; }
/*     */     
/* 484 */     }  return result;
/*     */   }
/*     */   
/*     */   static String checkTilde(String str) {
/*     */     try {
/* 489 */       if (str.startsWith("~")) {
/* 490 */         str = str.replace("~", System.getProperty("user.home"));
/*     */       }
/*     */     }
/* 493 */     catch (SecurityException e) {}
/*     */     
/* 495 */     return str;
/*     */   }
/*     */   
/*     */   private static int skipUTF8Char(byte b) {
/* 499 */     if ((byte)(b & 0x80) == 0) return 1; 
/* 500 */     if ((byte)(b & 0xE0) == -64) return 2; 
/* 501 */     if ((byte)(b & 0xF0) == -32) return 3; 
/* 502 */     return 1;
/*     */   }
/*     */   
/*     */   static byte[] fromFile(String _file) throws IOException {
/* 506 */     _file = checkTilde(_file);
/* 507 */     File file = new File(_file);
/* 508 */     FileInputStream fis = new FileInputStream(_file);
/*     */     try {
/* 510 */       byte[] result = new byte[(int)file.length()];
/* 511 */       int len = 0;
/*     */       while (true) {
/* 513 */         int i = fis.read(result, len, result.length - len);
/* 514 */         if (i <= 0)
/*     */           break; 
/* 516 */         len += i;
/*     */       } 
/* 518 */       fis.close();
/* 519 */       return result;
/*     */     } finally {
/*     */       
/* 522 */       if (fis != null)
/* 523 */         fis.close(); 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\Util.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */