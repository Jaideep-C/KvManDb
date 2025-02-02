package com.kvs.kvman.server;

import com.kvs.kvman.dtos.request.GetRequestDto;
import com.kvs.kvman.dtos.request.PutRequestDto;
import com.kvs.kvman.dtos.request.Request;
import com.kvs.kvman.dtos.response.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Its responsible for handling the socket connection and processing the requests
 * it should be able to read the input stream and map the request to the appropriate handler
 * it should be able to write the response to the output stream
 */

public class SocketHandler {

  private Function<Request, Response> requestHandler;
  private static final Logger logger = LoggerFactory.getLogger(SocketHandler.class);

  public SocketHandler(Function<Request, Response> requestHandler) {
    this.requestHandler = requestHandler;
  }

  public void handle(final Socket socket) {
    try (BufferedReader bufferedReader =
            new BufferedReader(new InputStreamReader(socket.getInputStream()));
        var outputStream = socket.getOutputStream()) {

      String requestType = bufferedReader.readLine();
      if (requestType == null) {
        logger.warn("Received null request type");
        return;
      }

      Request request;
      if (requestType.equals("PUT")) {
        String key = bufferedReader.readLine();
        String value = bufferedReader.readLine();
        if (key == null || value == null) {
          logger.warn("Incomplete PUT request");
          return;
        }
        request = new PutRequestDto(key.getBytes(), value.getBytes());
      } else if (requestType.equals("GET")) {
        String key = bufferedReader.readLine();
        if (key == null) {
          logger.warn("Incomplete GET request");
          return;
        }
        request = new GetRequestDto(key.getBytes());
      } else {
        logger.warn("Unknown request type: {}", requestType);
        return;
      }

      Response response = requestHandler.apply(request);
      byte[] responseBytes = response.toBytes();
      outputStream.write(responseBytes);
      outputStream.flush();

    } catch (IOException e) {
      logger.error("IO error while handling socket connection", e);
    }
  }
}
