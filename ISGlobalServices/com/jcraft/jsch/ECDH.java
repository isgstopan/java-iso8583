package com.jcraft.jsch;

public interface ECDH {
  void init(int paramInt) throws Exception;
  
  byte[] getSecret(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) throws Exception;
  
  byte[] getQ() throws Exception;
  
  boolean validate(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) throws Exception;
}


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\ECDH.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */