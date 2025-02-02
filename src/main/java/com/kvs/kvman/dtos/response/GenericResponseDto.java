package com.kvs.kvman.dtos.response;

import com.kvs.kvman.dtos.ResponseCode;

public class GenericResponseDto implements Response {

  private static final long serialVersionUID = 1L;
  private final ResponseCode code;

  public GenericResponseDto(final ResponseCode code) {
    this.code = code;
  }

  @Override
  public ResponseCode code() {
    return code;
  }

  @Override
  public byte[] toBytes() {
    return String.format("%s\n", code.toString()).getBytes();
  }
}
