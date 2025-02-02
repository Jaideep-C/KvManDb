package com.kvs.kvman.core.implementation;

import com.kvs.kvman.core.KvManStore;
import com.kvs.kvman.core.LruMap;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KvManStoreImplementation implements KvManStore {
  private static final Logger logger = LoggerFactory.getLogger(KvManStoreImplementation.class);

  private final LruMap<ByteArrayWrapper, byte[]> kvStore;
  private final long maxMemory = Runtime.getRuntime().maxMemory();
  private final long maxMemoryToBeUsed = (long) (maxMemory * 0.85);

  public KvManStoreImplementation() {
    logger.info("Max memory: {}", maxMemory);
    logger.info("Max memory to be used: {}", maxMemoryToBeUsed);

    this.kvStore =
        new LruMapImplementation(
            (map) -> {
              logger.info("Total diff: {}", map.totalBytes() - maxMemoryToBeUsed);
              if (map.totalBytes() > maxMemoryToBeUsed) {
                logger.info("Called remove tail");
                logger.info("Total bytes: {}", map.totalBytes());
                logger.info("Max memory to be used: {}", maxMemoryToBeUsed);
                return true;
              }
              return false;
            });
  }

  @Override
  public void put(final byte[] key, final byte[] value) {
    if (key == null || value == null) {
      throw new IllegalArgumentException("Key and value cannot be null");
    }

    try {
      kvStore.put(new ByteArrayWrapper(key), value.clone());
      logger.debug("Set value for key: {}", new ByteArrayWrapper(key));
    } catch (OutOfMemoryError e) {
      logger.error("Failed to set value due to memory constraints", e);
      kvStore.evict();
      put(key, value);
    }
  }

  @Override
  public byte[] get(final byte[] key) {
    if (key == null) {
      throw new IllegalArgumentException("Key cannot be null");
    }

    byte[] value = kvStore.get(new ByteArrayWrapper(key));
    if (value != null) {
      logger.debug("Retrieved value for key: {}", new ByteArrayWrapper(key));
      return value.clone();
    }
    return null;
  }

  // Wrapper class to properly handle byte array equality and hashing
  public static class ByteArrayWrapper implements Comparable<ByteArrayWrapper> {
    private final byte[] data;

    public ByteArrayWrapper(byte[] data) {
      this.data = data;
    }

    public int length() {
      return data.length;
    }

    @Override
    public boolean equals(Object other) {
      if (!(other instanceof ByteArrayWrapper)) {
        return false;
      }
      return Arrays.equals(data, ((ByteArrayWrapper) other).data);
    }

    @Override
    public int hashCode() {
      return Arrays.hashCode(data);
    }

    @Override
    public String toString() {
      return Arrays.toString(data);
    }

    @Override
    public int compareTo(ByteArrayWrapper other) {
      return Arrays.compare(this.data, other.data);
    }
  }
}
