package com.jcraft.jsch;

public interface Signature {
  void init() throws Exception;
  
  void update(byte[] paramArrayOfbyte) throws Exception;
  
  boolean verify(byte[] paramArrayOfbyte) throws Exception;
  
  byte[] sign() throws Exception;
}


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\Signature.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */