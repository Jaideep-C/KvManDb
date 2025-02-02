package com.kvs.kvman.dtos;

import java.util.List;

public enum ResponseCode {
  OK(200),
  NOT_FOUND(404),
  OUT_OF_MEMORY(500);

  private final int code;

  ResponseCode(final int code) {
    this.code = code;
  }

  public static ResponseCode fromCode(int code) {
    return List.of(values()).stream()
        .filter(rc -> rc.code == code)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Invalid response code: " + code));
  }
}
