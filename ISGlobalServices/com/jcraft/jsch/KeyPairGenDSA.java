package com.jcraft.jsch;

public interface KeyPairGenDSA {
  void init(int paramInt) throws Exception;
  
  byte[] getX();
  
  byte[] getY();
  
  byte[] getP();
  
  byte[] getQ();
  
  byte[] getG();
}


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\KeyPairGenDSA.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */