package com.jcraft.jsch;

public interface SignatureECDSA extends Signature {
  void setPubKey(byte[] paramArrayOfbyte1, byte[] paramArrayOfbyte2) throws Exception;
  
  void setPrvKey(byte[] paramArrayOfbyte) throws Exception;
}


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\SignatureECDSA.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */