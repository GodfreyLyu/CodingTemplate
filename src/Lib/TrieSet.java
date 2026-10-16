package Lib;
import java.util.*;

/**
 * Stores unique strings in a trie backed by TrieMap. Supports membership
 * checks, prefix searches, and patterns where {@code .} matches any single
 * character. Use non-null strings with character values from 0 through 255.
 *
 * <p>Example inside the {@code Lib} package:
 * <pre>{@code
 * TrieSet words = new TrieSet();
 * words.add("app");
 * words.add("apple");
 * words.add("app"); // Adding the same word again does not create a duplicate.
 * System.out.println(words.size());                       // 2
 * System.out.println(words.contains("app"));              // true
 * System.out.println(words.keysWithPrefix("app"));        // [app, apple]
 * System.out.println(words.longestPrefixOf("applesauce")); // apple
 * System.out.println(words.hasKeyWithPattern("a.."));     // true
 * words.remove("app");
 * System.out.println(words.contains("app"));              // false
 * }</pre>
 */
class TrieSet {
    // Use a TrieMap whose keys are the set elements and whose values are placeholders.
    // Any value type would work; use Object, as in the Java standard library.
    private final TrieMap<Object> map = new TrieMap<>();

    // **** Insertion ****

    // Add key to the set.
    public void add(String key) {
        map.put(key, new Object());
    }

    // **** Removal ****

    // Remove key from the set.
    public void remove(String key) {
        map.remove(key);
    }

    // **** Queries ****

    // Check whether the set contains key.
    public boolean contains(String key) {
        return map.containsKey(key);
    }

    // Find the shortest prefix of query that is in the set.
    public String shortestPrefixOf(String query) {
        return map.shortestPrefixOf(query);
    }

    // Find the longest prefix of query that is in the set.
    public String longestPrefixOf(String query) {
        return map.longestPrefixOf(query);
    }

    // Find all elements that start with prefix.
    public List<String> keysWithPrefix(String prefix) {
        return map.keysWithPrefix(prefix);
    }

    // Check whether any element starts with prefix.
    public boolean hasKeyWithPrefix(String prefix) {
        return map.hasKeyWithPrefix(prefix);
    }

    // Return all elements matching pattern; '.' matches any single character.
    public List<String> keysWithPattern(String pattern) {
        return map.keysWithPattern(pattern);
    }

    // Check whether any element matches pattern; '.' matches any single character.
    public boolean hasKeyWithPattern(String pattern) {
        return map.hasKeyWithPattern(pattern);
    }

    // Return the number of elements in the set.
    public int size() {
        return map.size();
    }
}
