package Lib;

import java.util.LinkedHashMap;

/**
 * Stores integer key-value pairs up to a fixed, positive capacity. Reading or
 * updating an entry marks it as recently used. Adding an entry to a full cache
 * removes the least recently used entry. A missing key returns {@code -1}.
 *
 * <p>Example inside the {@code Lib} package:
 * <pre>{@code
 * LRUCache cache = new LRUCache(2);
 * cache.put(1, 10);
 * cache.put(2, 20);
 * System.out.println(cache.get(1)); // 10: key 1 is now the most recently used.
 * cache.put(3, 30);                // Removes key 2 to make room.
 * System.out.println(cache.get(2)); // -1
 * System.out.println(cache.get(3)); // 30
 * }</pre>
 */
class LRUCache {
    int cap;
    LinkedHashMap<Integer, Integer> cache = new LinkedHashMap<>();
    public LRUCache(int capacity) {
        this.cap = capacity;
    }

    public int get(int key) {
        if (!cache.containsKey(key)) {
            return -1;
        }
        // Mark key as the most recently used entry.
        makeRecently(key);
        return cache.get(key);
    }

    public void put(int key, int val) {
        if (cache.containsKey(key)) {
            // Update the value for key.
            cache.put(key, val);
            // Mark key as the most recently used entry.
            makeRecently(key);
            return;
        }

        if (cache.size() >= this.cap) {
            // The first entry is the least recently used key.
            int oldestKey = cache.keySet().iterator().next();
            cache.remove(oldestKey);
        }
        // Add the new key at the end of the insertion order.
        cache.put(key, val);
    }

    private void makeRecently(int key) {
        int val = cache.get(key);
        // Remove key and reinsert it at the end.
        cache.remove(key);
        cache.put(key, val);
    }
}
