package com.kvs.kvman.core;

public interface KvManStore {
  void put(byte[] key, byte[] value);

  byte[] get(byte[] key);
}
