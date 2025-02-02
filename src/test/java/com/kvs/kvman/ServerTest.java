package com.kvs.kvman;

import com.kvs.kvman.client.KvManClinet;
import com.kvs.kvman.dtos.response.GetResponseDto;
import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerTest {
  private static Logger logger = LoggerFactory.getLogger(ServerTest.class);
  private static Thread serverThread;

  public static void main(String[] args) throws InterruptedException {
    serverThread =
        new Thread(
            () -> {
              try {
                App.main(args);
              } catch (IOException e) {
                logger.error("Error starting server", e);
              }
            });
    serverThread.start();
    Thread.sleep(1000);
    put_key_test();
    serverThread.interrupt();
  }

  private static void put_key_test() {
    try {
      KvManClinet client = new KvManClinet("localhost", 8080, Duration.ofMillis(10090));
      client.put("test".getBytes(), "test".getBytes());
      GetResponseDto response = client.get("test".getBytes());
      assert (Arrays.equals("test".getBytes(), response.value()));
    } catch (IOException e) {
      logger.error("Error processing put and get", e);
    }
  }
}
