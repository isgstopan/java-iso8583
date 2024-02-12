package com.jcraft.jsch;

public interface SftpProgressMonitor {
  public static final int PUT = 0;
  
  public static final int GET = 1;
  
  public static final long UNKNOWN_SIZE = -1L;
  
  void init(int paramInt, String paramString1, String paramString2, long paramLong);
  
  boolean count(long paramLong);
  
  void end();
}


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\SftpProgressMonitor.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */