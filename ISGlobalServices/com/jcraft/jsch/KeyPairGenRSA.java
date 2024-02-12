package com.jcraft.jsch;

public interface KeyPairGenRSA {
  void init(int paramInt) throws Exception;
  
  byte[] getD();
  
  byte[] getE();
  
  byte[] getN();
  
  byte[] getC();
  
  byte[] getEP();
  
  byte[] getEQ();
  
  byte[] getP();
  
  byte[] getQ();
}


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\KeyPairGenRSA.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */