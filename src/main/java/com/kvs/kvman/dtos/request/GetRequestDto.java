package com.kvs.kvman.dtos.request;

public class GetRequestDto implements Request {
  private static final long serialVersionUID = 1L;
  private final byte[] key;

  public GetRequestDto(final byte[] key) {
    this.key = key;
  }

  public byte[] key() {
    return key;
  }

  @Override
  public byte[] toBytes() {
    return String.format("GET\n%s", key).getBytes();
  }
}
