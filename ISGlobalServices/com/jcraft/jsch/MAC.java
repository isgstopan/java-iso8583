package com.jcraft.jsch;

public interface MAC {
  String getName();
  
  int getBlockSize();
  
  void init(byte[] paramArrayOfbyte) throws Exception;
  
  void update(byte[] paramArrayOfbyte, int paramInt1, int paramInt2);
  
  void update(int paramInt);
  
  void doFinal(byte[] paramArrayOfbyte, int paramInt);
}


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\MAC.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */