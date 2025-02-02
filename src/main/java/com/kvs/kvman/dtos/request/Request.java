package com.kvs.kvman.dtos.request;

import java.io.Serializable;

public interface Request extends Serializable {
  byte[] toBytes();
}
