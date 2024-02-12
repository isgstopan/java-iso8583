package com.jcraft.jsch;

import java.io.InputStream;
import java.io.OutputStream;

public interface ForwardedTCPIPDaemon extends Runnable {
  void setChannel(ChannelForwardedTCPIP paramChannelForwardedTCPIP, InputStream paramInputStream, OutputStream paramOutputStream);
  
  void setArg(Object[] paramArrayOfObject);
}


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\ForwardedTCPIPDaemon.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */