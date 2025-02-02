package com.kvs.kvman.dtos.request;

public class PutRequestDto implements Request {
  private static final long serialVersionUID = 1L;
  private final byte[] key;
  private final byte[] value;

  public PutRequestDto(final byte[] key, final byte[] value) {
    this.key = key;
    this.value = value;
  }

  public byte[] key() {
    return key;
  }

  public byte[] value() {
    return value;
  }

  @Override
  public byte[] toBytes() {
    return String.format("PUT\n%s\n%s\n", new String(key), new String(value)).getBytes();
  }
}
