package com.kvs.kvman.dtos.response;

import com.kvs.kvman.dtos.ResponseCode;

public class GetResponseDto implements Response {
  private static final long serialVersionUID = 1L;
  private final byte[] key;
  private final byte[] value;

  public GetResponseDto(final byte[] key, final byte[] value) {
    this.key = key;
    this.value = value;
  }

  @Override
  public ResponseCode code() {
    return ResponseCode.OK;
  }

  @Override
  public byte[] toBytes() {
    StringBuilder sb = new StringBuilder();
    sb.append(code().toString())
        .append("\n")
        .append(new String(key))
        .append("\n")
        .append(new String(value))
        .append("\n");
    return sb.toString().getBytes();
  }

  public byte[] key() {
    return key;
  }

  public byte[] value() {
    return value;
  }
}
