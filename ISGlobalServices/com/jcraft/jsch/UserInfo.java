package com.jcraft.jsch;

public interface UserInfo {
  String getPassphrase();
  
  String getPassword();
  
  boolean promptPassword(String paramString);
  
  boolean promptPassphrase(String paramString);
  
  boolean promptYesNo(String paramString);
  
  void showMessage(String paramString);
}


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\UserInfo.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */