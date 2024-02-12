package com.jcraft.jsch;

public interface GSSContext {
  void create(String paramString1, String paramString2) throws JSchException;
  
  boolean isEstablished();
  
  byte[] init(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws JSchException;
  
  byte[] getMIC(byte[] paramArrayOfbyte, int paramInt1, int paramInt2);
  
  void dispose();
}


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\GSSContext.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */