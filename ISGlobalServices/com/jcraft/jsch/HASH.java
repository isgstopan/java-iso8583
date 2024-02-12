package com.jcraft.jsch;

public interface HASH {
  void init() throws Exception;
  
  int getBlockSize();
  
  void update(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws Exception;
  
  byte[] digest() throws Exception;
}


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\HASH.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */