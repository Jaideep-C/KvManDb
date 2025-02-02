package com.kvs.kvman.exception;

import com.kvs.kvman.dtos.ResponseCode;

/** Custom exception for KvMan store operations. */
public class KvStoreException extends RuntimeException {
  private final ResponseCode code;

  public KvStoreException(ResponseCode code) {
    super(code.toString());
    this.code = code;
  }

  public ResponseCode code() {
    return code;
  }
}
