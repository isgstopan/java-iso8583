package com.jcraft.jsch;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public interface SocketFactory {
  Socket createSocket(String paramString, int paramInt) throws IOException, UnknownHostException;
  
  InputStream getInputStream(Socket paramSocket) throws IOException;
  
  OutputStream getOutputStream(Socket paramSocket) throws IOException;
}


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\SocketFactory.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */