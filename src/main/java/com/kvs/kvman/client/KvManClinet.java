package com.kvs.kvman.client;

import com.kvs.kvman.dtos.ResponseCode;
import com.kvs.kvman.dtos.request.GetRequestDto;
import com.kvs.kvman.dtos.request.PutRequestDto;
import com.kvs.kvman.dtos.response.GenericResponseDto;
import com.kvs.kvman.dtos.response.GetResponseDto;
import com.kvs.kvman.exception.KvStoreException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.time.Duration;

public class KvManClinet {
  private final Socket socket;

  public KvManClinet(String host, int port, Duration timeout) throws IOException {
    this.socket = new Socket(host, port);
    this.socket.setSoTimeout((int) timeout.toMillis());
  }

  public GenericResponseDto put(byte[] key, byte[] value) throws IOException {
    socket.getOutputStream().write(new PutRequestDto(key, value).toBytes());
    socket.getOutputStream().flush();
    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    var code = reader.readLine();
    if (code.equals(ResponseCode.OK.toString())) {
      return new GenericResponseDto(ResponseCode.OK);
    }
    var errCode = ResponseCode.valueOf(code);
    throw new KvStoreException(errCode);
  }

  public GetResponseDto get(byte[] key) throws IOException {
    socket.getOutputStream().write(new GetRequestDto(key).toBytes());
    socket.getOutputStream().flush();
    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    var code = ResponseCode.fromCode(Integer.parseInt(reader.readLine()));

    if (code.equals(ResponseCode.OK)) {
      return new GetResponseDto(key, reader.readLine().getBytes());
    }
    throw new KvStoreException(code);
  }
}
