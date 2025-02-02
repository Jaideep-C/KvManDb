package com.kvs.kvman.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpServer {
  private static final Logger logger = LoggerFactory.getLogger(TcpServer.class);
  private final int port;
  private final ServerSocket serverSocket;

  public TcpServer(int port) throws IOException {
    this.port = port;
    this.serverSocket = new ServerSocket(port);
    logger.info("TcpServer started on port {}", this.port);
  }

  public void start(Function<Socket, Void> handler) {
    try {
      while (true) {
        Socket socket = serverSocket.accept();
        logger.info("Client connected: {}", socket.getRemoteSocketAddress());
        handler.apply(socket);
      }
    } catch (IOException e) {
      logger.error("IO Exception in TcpServer", e);
    } catch (Exception e) {
      logger.error("Error in TcpServer", e);
    } finally {
      stop();
    }
  }

  public void stop() {
    try {
      serverSocket.close();
      logger.info("TcpServer stopped on port {}", this.port);
    } catch (IOException e) {
      logger.error("IO Exception while closing TcpServer", e);
    }
  }
}
