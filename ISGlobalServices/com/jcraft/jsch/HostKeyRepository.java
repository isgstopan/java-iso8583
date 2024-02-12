package com.jcraft.jsch;

public interface HostKeyRepository {
  public static final int OK = 0;
  
  public static final int NOT_INCLUDED = 1;
  
  public static final int CHANGED = 2;
  
  int check(String paramString, byte[] paramArrayOfbyte);
  
  void add(HostKey paramHostKey, UserInfo paramUserInfo);
  
  void remove(String paramString1, String paramString2);
  
  void remove(String paramString1, String paramString2, byte[] paramArrayOfbyte);
  
  String getKnownHostsRepositoryID();
  
  HostKey[] getHostKey();
  
  HostKey[] getHostKey(String paramString1, String paramString2);
}


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\HostKeyRepository.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */