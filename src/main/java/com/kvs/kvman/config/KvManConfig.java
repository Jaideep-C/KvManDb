package com.kvs.kvman.config;

/** Configuration constants for KvMan implementation. */
public final class KvManConfig {
  public static final int DEFAULT_CACHE_SIZE = 1000;
  public static final double MIN_FREE_MEMORY_RATIO = 0.02;

  private KvManConfig() {
    // Prevent instantiation
  }
}
