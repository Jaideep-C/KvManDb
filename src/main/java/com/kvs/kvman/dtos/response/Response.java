package com.kvs.kvman.dtos.response;

import com.kvs.kvman.dtos.ResponseCode;
import java.io.Serializable;

public interface Response extends Serializable {
  public ResponseCode code();

  public byte[] toBytes();
}
