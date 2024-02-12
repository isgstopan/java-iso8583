package com.jcraft.jsch;

public interface Cipher {
  public static final int ENCRYPT_MODE = 0;
  
  public static final int DECRYPT_MODE = 1;
  
  int getIVSize();
  
  int getBlockSize();
  
  void init(int paramInt, byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) throws Exception;
  
  void update(byte[] paramArrayOfbyte1, int paramInt1, int paramInt2, byte[] paramArrayOfbyte2, int paramInt3) throws Exception;
  
  boolean isCBC();
}


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\Cipher.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */