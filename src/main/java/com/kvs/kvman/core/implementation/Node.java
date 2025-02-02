package com.kvs.kvman.core.implementation;

public class Node<K, V> {
  private final K key;
  private V value;
  Node<K, V> next;
  Node<K, V> prev;

  public Node(final K key, final V value) {
    this.key = key;
    this.value = value;
  }

  public K key() {
    return key;
  }

  public V value() {
    return value;
  }

  public void value(final V value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "Node{" + "key=" + key + ", value=" + value + '}';
  }
}
