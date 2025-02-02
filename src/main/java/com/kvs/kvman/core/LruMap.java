package com.kvs.kvman.core;

public interface LruMap<K, V> {
  void put(final K key, final V value);

  V get(final K key);

  void evict();

  int size();

  long totalBytes();
}
