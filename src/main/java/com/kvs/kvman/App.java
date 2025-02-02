package com.kvs.kvman;

import com.kvs.kvman.core.KvManStore;
import com.kvs.kvman.core.implementation.KvManStoreImplementation;
import com.kvs.kvman.dtos.ResponseCode;
import com.kvs.kvman.dtos.request.GetRequestDto;
import com.kvs.kvman.dtos.request.PutRequestDto;
import com.kvs.kvman.dtos.response.GenericResponseDto;
import com.kvs.kvman.dtos.response.GetResponseDto;
import com.kvs.kvman.server.SocketHandler;
import com.kvs.kvman.server.TcpServer;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Hello world! */
public class App {
  private static final Logger logger = LoggerFactory.getLogger(App.class);
  private static final KvManStore kvStore = new KvManStoreImplementation();
  private static final int THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors();
  private static final ExecutorService[] threadPools = new ExecutorService[THREAD_POOL_SIZE];

  public static void main(String[] args) throws IOException {
    for (int i = 0; i < THREAD_POOL_SIZE; i++) {
      threadPools[i] = Executors.newSingleThreadExecutor();
    }
    logger.info("Starting KvMan");
    logger.info("Max memory: {}", Runtime.getRuntime().maxMemory() / 1024.0 / 1024.0 / 1024.0);
    logger.info("Total memory: {}", Runtime.getRuntime().totalMemory() / 1024.0 / 1024.0 / 1024.0);
    logger.info("Free memory: {}", Runtime.getRuntime().freeMemory() / 1024.0 / 1024.0 / 1024.0);
    logger.info(
        "maxMemoryToBeUsed: {}",
        (Runtime.getRuntime().maxMemory() * 0.50) / 1024.0 / 1024.0 / 1024.0);
    var port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
    TcpServer server = new TcpServer(port);
    server.start(
        socket -> {
          var socketHandler =
              new SocketHandler(
                  (req) -> {
                    if (req instanceof PutRequestDto) {
                      var putReq = (PutRequestDto) req;
                      logger.info("Processing PUT request for key: {}", new String(putReq.key()));
                      kvStore.put(putReq.key(), putReq.value());
                      logger.debug(
                          "Successfully stored value for key: {}", new String(putReq.key()));
                      return new GenericResponseDto(ResponseCode.OK);
                    }
                    if (req instanceof GetRequestDto) {
                      var getReq = (GetRequestDto) req;
                      logger.info("Processing GET request for key: {}", new String(getReq.key()));
                      var value = kvStore.get(getReq.key());
                      logger.debug(
                          "Retrieved value for key: {}, found: {}",
                          new String(getReq.key()),
                          value != null ? "yes" : "no");
                      return value != null
                          ? new GetResponseDto(getReq.key(), value)
                          : new GenericResponseDto(ResponseCode.NOT_FOUND);
                    }
                    logger.error(
                        "Received invalid request type: {}", req.getClass().getSimpleName());
                    throw new IllegalArgumentException("Invalid request: " + req);
                  });
          socketHandler.handle(socket);
          return null;
        });
  }
}
