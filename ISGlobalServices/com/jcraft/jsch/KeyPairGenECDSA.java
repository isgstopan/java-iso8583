package com.jcraft.jsch;

public interface KeyPairGenECDSA {
  void init(int paramInt) throws Exception;
  
  byte[] getD();
  
  byte[] getR();
  
  byte[] getS();
}


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\KeyPairGenECDSA.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */