package com.jcraft.jsch;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

public interface ServerSocketFactory {
  ServerSocket createServerSocket(int paramInt1, int paramInt2, InetAddress paramInetAddress) throws IOException;
}


/* Location:              F:\JAVA\ISGlobalServices.jar!\com\jcraft\jsch\ServerSocketFactory.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */