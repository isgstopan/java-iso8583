package com.jcraft.jsch;

public interface SignatureRSA extends Signature {
  void setPubKey(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) throws Exception;
  
  void setPrvKey(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) throws Exception;
}


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\SignatureRSA.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */