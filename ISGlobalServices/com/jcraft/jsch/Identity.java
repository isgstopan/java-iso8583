package com.jcraft.jsch;

public interface Identity {
  boolean setPassphrase(byte[] paramArrayOfbyte) throws JSchException;
  
  byte[] getPublicKeyBlob();
  
  byte[] getSignature(byte[] paramArrayOfbyte);
  
  boolean decrypt();
  
  String getAlgName();
  
  String getName();
  
  boolean isEncrypted();
  
  void clear();
}


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\Identity.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */