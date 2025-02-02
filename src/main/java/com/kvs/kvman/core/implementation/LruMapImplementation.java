package com.kvs.kvman.core.implementation;

import com.kvs.kvman.core.LruMap;
import com.kvs.kvman.core.implementation.KvManStoreImplementation.ByteArrayWrapper;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LruMapImplementation implements LruMap<ByteArrayWrapper, byte[]> {
  private static final Logger logger = LoggerFactory.getLogger(LruMapImplementation.class);
  private final Function<LruMap<ByteArrayWrapper, byte[]>, Boolean> evictFunction;
  private final Map<ByteArrayWrapper, Node<ByteArrayWrapper, byte[]>> map;
  private Node<ByteArrayWrapper, byte[]> head;
  private Node<ByteArrayWrapper, byte[]> tail;
  private long totalBytes = 0;
  private static final float OVERHEAD = 148_437;

  public LruMapImplementation(
      final Function<LruMap<ByteArrayWrapper, byte[]>, Boolean> evictFunction) {
    this.evictFunction = evictFunction;
    this.map = new TreeMap<>();
    this.head = new Node<>(null, null);
    this.tail = head;
  }

  @Override
  public int size() {
    return map.size();
  }

  public long totalBytes() {
    return totalBytes;
  }

  @Override
  public void put(final ByteArrayWrapper key, final byte[] value) {
    totalBytes += value.length + key.length() + OVERHEAD;
    while (evictFunction.apply(this)) {
      evict();
    }

    var node = map.get(key);
    if (node != null) {
      node.value(value);
      moveToHead(node);
      return;
    }
    node = new Node<>(key, value);
    map.put(key, node);
    moveToHead(node);
    if (map.size() % 10 == 0) {
      logger.info("LruMapImplementation: Map size: {}", map.size());
      logger.info(
          "LruMapImplementation: Memory used GB: {}",
          Runtime.getRuntime().totalMemory() / 1024.0 / 1024.0 / 1024.0);
      logger.info(
          "LruMapImplementation: Memory free GB: {}",
          Runtime.getRuntime().freeMemory() / 1024.0 / 1024.0 / 1024.0);
      logger.info(
          "LruMapImplementation: Memory max GB: {}",
          Runtime.getRuntime().maxMemory() / 1024.0 / 1024.0 / 1024.0);
    }
    logger.info("LruMapImplementation: Total bytes: {}", totalBytes);
    logger.info("LruMapImplementation: Map size: {}", map.size());
  }

  @Override
  public byte[] get(final ByteArrayWrapper key) {
    var node = map.get(key);
    moveToHead(node);
    return node == null ? null : node.value();
  }

  private void moveToHead(final Node<ByteArrayWrapper, byte[]> node) {
    if (node == null) {
      return;
    }

    // if node is a new node, then it is the new head
    if (node.prev == null && node.next == null) {
      head.next = node;
      node.prev = head;
      head = node;
      return;
    }

    // if node is the head, then we don't need to do anything
    if (node.next == null) {
      return;
    }
    // node is not a new node, so we remove it from the list and add it to the head
    var current = node;
    var prev = current.prev;
    var next = current.next;
    current.prev = null;
    current.next = null;
    if (prev != null) {
      prev.next = next;
    }
    if (next != null) {
      next.prev = prev;
    }
    head.next = current;
    current.prev = head;
    head = current;
  }

  @Override
  public void evict() {
    totalBytes -= tail.next.key().length() + tail.next.value().length + OVERHEAD;
    if (tail.next.next == null) {
      return;
    }
    var cur = tail.next;
    var next = tail.next.next;
    tail.next = next;
    next.prev = tail;
    cur.next = null;
    cur.prev = null;
    map.remove(cur.key());
    System.gc();
  }
}
