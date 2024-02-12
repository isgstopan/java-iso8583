package com.jcraft.jsch;

public interface Compression {
  public static final int INFLATER = 0;
  
  public static final int DEFLATER = 1;
  
  void init(int paramInt1, int paramInt2);
  
  byte[] compress(byte[] paramArrayOfbyte, int paramInt, int[] paramArrayOfint);
  
  byte[] uncompress(byte[] paramArrayOfbyte, int paramInt, int[] paramArrayOfint);
}


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\Compression.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */