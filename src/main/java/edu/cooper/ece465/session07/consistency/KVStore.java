package edu.cooper.ece465.session07.consistency;

/**
 * Session 07: Consistency
 * Interface for a simple Key-Value Store.
 */
public interface KVStore {
    void put(String key, String value);

    String get(String key);
}
