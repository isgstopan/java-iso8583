/*    */ package igs.corp;
/*    */ 
/*    */ import java.util.regex.Matcher;
/*    */ import java.util.regex.Pattern;
/*    */ 
/*    */ public final class Tools {
/*    */   public static byte[] GetBytesWithLength(String msg) {
/*  8 */     StringBuilder sb = new StringBuilder();
/*  9 */     sb.append(GetLengthChars(msg.length(), 2));
/* 10 */     sb.append(msg);
/* 11 */     return sb.toString().getBytes();
/*    */   }
/*    */   
/*    */   public static String GetLengthChars(int numberBase10, int numOfBytes) {
/* 15 */     StringBuilder base16 = new StringBuilder(String.format("%X", new Object[] { Integer.valueOf(numberBase10) }));
/*    */     
/* 17 */     int start = 0, end = numOfBytes;
/* 18 */     int length = numOfBytes * 2;
/*    */     
/* 20 */     for (int i = 0; i < length - base16.length(); i++) {
/* 21 */       base16.insert(0, "0");
/*    */     }
/*    */     
/* 24 */     StringBuilder res = new StringBuilder();
/* 25 */     for (int j = 0; j < base16.length() / numOfBytes; j++) {
/* 26 */       char r = (char)Integer.parseInt(base16.substring(start, end), 16);
/* 27 */       res.append(r);
/* 28 */       start += 2;
/* 29 */       end += 2;
/*    */     } 
/*    */     
/* 32 */     return res.toString();
/*    */   }
/*    */   
/*    */   public static String[] GetTransactionUniqueness(String msg, String key, int numDigit) {
/* 36 */     String[] results = null;
/*    */     
/* 38 */     if (msg == null) return null; 
/* 39 */     if (msg.equalsIgnoreCase("")) return null;
/*    */     
/* 41 */     Pattern pattern = Pattern.compile("(\\d{12})(\\d{2})(" + key + "\\d{" + numDigit + "})");
/* 42 */     Matcher matcher = pattern.matcher(msg);
/*    */     
/* 44 */     if (matcher.find()) {
/* 45 */       results = new String[3];
/* 46 */       results[0] = matcher.group(1);
/* 47 */       results[1] = matcher.group(2);
/* 48 */       results[2] = matcher.group(3);
/*    */     } 
/*    */     
/* 51 */     return results;
/*    */   }
/*    */   
/*    */   public static String GetTransactionCode(String msg) {
/* 55 */     if (msg == null) return null; 
/* 56 */     if (msg.equalsIgnoreCase("")) return null;
/*    */     
/* 58 */     Pattern pattern = Pattern.compile("F[0-9a-zA-Z]{31}\\d{2}\\d{16}([A-Z0-9]{6})");
/* 59 */     Matcher matcher = pattern.matcher(msg);
/*    */     
/* 61 */     if (matcher.find()) {
/* 62 */       return matcher.group(1);
/*    */     }
/*    */     
/* 65 */     return null;
/*    */   }
/*    */ }


/* Location:              F:\JAVA\Corp.jar!\igs\corp\Tools.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */