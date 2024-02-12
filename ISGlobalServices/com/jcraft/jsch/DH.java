package com.jcraft.jsch;

public interface DH {
  void init() throws Exception;
  
  void setP(byte[] paramArrayOfbyte);
  
  void setG(byte[] paramArrayOfbyte);
  
  byte[] getE() throws Exception;
  
  void setF(byte[] paramArrayOfbyte);
  
  byte[] getK() throws Exception;
  
  void checkRange() throws Exception;
}


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\DH.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */