package com.jcraft.jsch;

public interface Logger {
  public static final int DEBUG = 0;
  
  public static final int INFO = 1;
  
  public static final int WARN = 2;
  
  public static final int ERROR = 3;
  
  public static final int FATAL = 4;
  
  boolean isEnabled(int paramInt);
  
  void log(int paramInt, String paramString);
}


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\Logger.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */