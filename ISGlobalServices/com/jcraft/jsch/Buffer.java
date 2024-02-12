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
/*     */ public class Buffer
/*     */ {
/*  33 */   final byte[] tmp = new byte[4]; byte[] buffer;
/*     */   int index;
/*     */   int s;
/*     */   
/*     */   public Buffer(int size) {
/*  38 */     this.buffer = new byte[size];
/*  39 */     this.index = 0;
/*  40 */     this.s = 0;
/*     */   }
/*     */   public Buffer(byte[] buffer) {
/*  43 */     this.buffer = buffer;
/*  44 */     this.index = 0;
/*  45 */     this.s = 0;
/*     */   } public Buffer() {
/*  47 */     this(20480);
/*     */   } public void putByte(byte foo) {
/*  49 */     this.buffer[this.index++] = foo;
/*     */   }
/*     */   public void putByte(byte[] foo) {
/*  52 */     putByte(foo, 0, foo.length);
/*     */   }
/*     */   public void putByte(byte[] foo, int begin, int length) {
/*  55 */     System.arraycopy(foo, begin, this.buffer, this.index, length);
/*  56 */     this.index += length;
/*     */   }
/*     */   public void putString(byte[] foo) {
/*  59 */     putString(foo, 0, foo.length);
/*     */   }
/*     */   public void putString(byte[] foo, int begin, int length) {
/*  62 */     putInt(length);
/*  63 */     putByte(foo, begin, length);
/*     */   }
/*     */   public void putInt(int val) {
/*  66 */     this.tmp[0] = (byte)(val >>> 24);
/*  67 */     this.tmp[1] = (byte)(val >>> 16);
/*  68 */     this.tmp[2] = (byte)(val >>> 8);
/*  69 */     this.tmp[3] = (byte)val;
/*  70 */     System.arraycopy(this.tmp, 0, this.buffer, this.index, 4);
/*  71 */     this.index += 4;
/*     */   }
/*     */   public void putLong(long val) {
/*  74 */     this.tmp[0] = (byte)(int)(val >>> 56L);
/*  75 */     this.tmp[1] = (byte)(int)(val >>> 48L);
/*  76 */     this.tmp[2] = (byte)(int)(val >>> 40L);
/*  77 */     this.tmp[3] = (byte)(int)(val >>> 32L);
/*  78 */     System.arraycopy(this.tmp, 0, this.buffer, this.index, 4);
/*  79 */     this.tmp[0] = (byte)(int)(val >>> 24L);
/*  80 */     this.tmp[1] = (byte)(int)(val >>> 16L);
/*  81 */     this.tmp[2] = (byte)(int)(val >>> 8L);
/*  82 */     this.tmp[3] = (byte)(int)val;
/*  83 */     System.arraycopy(this.tmp, 0, this.buffer, this.index + 4, 4);
/*  84 */     this.index += 8;
/*     */   }
/*     */   void skip(int n) {
/*  87 */     this.index += n;
/*     */   }
/*     */   void putPad(int n) {
/*  90 */     while (n > 0) {
/*  91 */       this.buffer[this.index++] = 0;
/*  92 */       n--;
/*     */     } 
/*     */   }
/*     */   public void putMPInt(byte[] foo) {
/*  96 */     int i = foo.length;
/*  97 */     if ((foo[0] & 0x80) != 0) {
/*  98 */       i++;
/*  99 */       putInt(i);
/* 100 */       putByte((byte)0);
/*     */     } else {
/*     */       
/* 103 */       putInt(i);
/*     */     } 
/* 105 */     putByte(foo);
/*     */   }
/*     */   public int getLength() {
/* 108 */     return this.index - this.s;
/*     */   }
/*     */   public int getOffSet() {
/* 111 */     return this.s;
/*     */   }
/*     */   public void setOffSet(int s) {
/* 114 */     this.s = s;
/*     */   }
/*     */   public long getLong() {
/* 117 */     long foo = getInt() & 0xFFFFFFFFL;
/* 118 */     foo = foo << 32L | getInt() & 0xFFFFFFFFL;
/* 119 */     return foo;
/*     */   }
/*     */   public int getInt() {
/* 122 */     int foo = getShort();
/* 123 */     foo = foo << 16 & 0xFFFF0000 | getShort() & 0xFFFF;
/* 124 */     return foo;
/*     */   }
/*     */   public long getUInt() {
/* 127 */     long foo = 0L;
/* 128 */     long bar = 0L;
/* 129 */     foo = getByte();
/* 130 */     foo = foo << 8L & 0xFF00L | (getByte() & 0xFF);
/* 131 */     bar = getByte();
/* 132 */     bar = bar << 8L & 0xFF00L | (getByte() & 0xFF);
/* 133 */     foo = foo << 16L & 0xFFFFFFFFFFFF0000L | bar & 0xFFFFL;
/* 134 */     return foo;
/*     */   }
/*     */   int getShort() {
/* 137 */     int foo = getByte();
/* 138 */     foo = foo << 8 & 0xFF00 | getByte() & 0xFF;
/* 139 */     return foo;
/*     */   }
/*     */   public int getByte() {
/* 142 */     return this.buffer[this.s++] & 0xFF;
/*     */   }
/*     */   public void getByte(byte[] foo) {
/* 145 */     getByte(foo, 0, foo.length);
/*     */   }
/*     */   void getByte(byte[] foo, int start, int len) {
/* 148 */     System.arraycopy(this.buffer, this.s, foo, start, len);
/* 149 */     this.s += len;
/*     */   }
/*     */   public int getByte(int len) {
/* 152 */     int foo = this.s;
/* 153 */     this.s += len;
/* 154 */     return foo;
/*     */   }
/*     */   public byte[] getMPInt() {
/* 157 */     int i = getInt();
/* 158 */     if (i < 0 || i > 8192)
/*     */     {
/*     */       
/* 161 */       i = 8192;
/*     */     }
/* 163 */     byte[] foo = new byte[i];
/* 164 */     getByte(foo, 0, i);
/* 165 */     return foo;
/*     */   }
/*     */   public byte[] getMPIntBits() {
/* 168 */     int bits = getInt();
/* 169 */     int bytes = (bits + 7) / 8;
/* 170 */     byte[] foo = new byte[bytes];
/* 171 */     getByte(foo, 0, bytes);
/* 172 */     if ((foo[0] & 0x80) != 0) {
/* 173 */       byte[] bar = new byte[foo.length + 1];
/* 174 */       bar[0] = 0;
/* 175 */       System.arraycopy(foo, 0, bar, 1, foo.length);
/* 176 */       foo = bar;
/*     */     } 
/* 178 */     return foo;
/*     */   }
/*     */   public byte[] getString() {
/* 181 */     int i = getInt();
/* 182 */     if (i < 0 || i > 262144)
/*     */     {
/*     */       
/* 185 */       i = 262144;
/*     */     }
/* 187 */     byte[] foo = new byte[i];
/* 188 */     getByte(foo, 0, i);
/* 189 */     return foo;
/*     */   }
/*     */   byte[] getString(int[] start, int[] len) {
/* 192 */     int i = getInt();
/* 193 */     start[0] = getByte(i);
/* 194 */     len[0] = i;
/* 195 */     return this.buffer;
/*     */   }
/*     */   public void reset() {
/* 198 */     this.index = 0;
/* 199 */     this.s = 0;
/*     */   }
/*     */   public void shift() {
/* 202 */     if (this.s == 0)
/* 203 */       return;  System.arraycopy(this.buffer, this.s, this.buffer, 0, this.index - this.s);
/* 204 */     this.index -= this.s;
/* 205 */     this.s = 0;
/*     */   }
/*     */   void rewind() {
/* 208 */     this.s = 0;
/*     */   }
/*     */   
/*     */   byte getCommand() {
/* 212 */     return this.buffer[5];
/*     */   }
/*     */   
/*     */   void checkFreeSize(int n) {
/* 216 */     int size = this.index + n + 128;
/* 217 */     if (this.buffer.length < size) {
/* 218 */       int i = this.buffer.length * 2;
/* 219 */       if (i < size) i = size; 
/* 220 */       byte[] tmp = new byte[i];
/* 221 */       System.arraycopy(this.buffer, 0, tmp, 0, this.index);
/* 222 */       this.buffer = tmp;
/*     */     } 
/*     */   }
/*     */   
/*     */   byte[][] getBytes(int n, String msg) throws JSchException {
/* 227 */     byte[][] tmp = new byte[n][];
/* 228 */     for (int i = 0; i < n; i++) {
/* 229 */       int j = getInt();
/* 230 */       if (getLength() < j) {
/* 231 */         throw new JSchException(msg);
/*     */       }
/* 233 */       tmp[i] = new byte[j];
/* 234 */       getByte(tmp[i]);
/*     */     } 
/* 236 */     return tmp;
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
/*     */   static Buffer fromBytes(byte[][] args) {
/* 254 */     int length = args.length * 4;
/* 255 */     for (int i = 0; i < args.length; i++) {
/* 256 */       length += (args[i]).length;
/*     */     }
/* 258 */     Buffer buf = new Buffer(length);
/* 259 */     for (int j = 0; j < args.length; j++) {
/* 260 */       buf.putString(args[j]);
/*     */     }
/* 262 */     return buf;
/*     */   }
/*     */ }


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\Buffer.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */