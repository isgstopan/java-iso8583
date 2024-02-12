package com.jcraft.jsch;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public interface Proxy {
  void connect(SocketFactory paramSocketFactory, String paramString, int paramInt1, int paramInt2) throws Exception;
  
  InputStream getInputStream();
  
  OutputStream getOutputStream();
  
  Socket getSocket();
  
  void close();
}


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\Proxy.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */